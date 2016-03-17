from ij import IJ
from ij import Prefs
from ij.gui import Roi
from ij.process import ImageProcessor
from ij.plugin.frame import RoiManager;
from ij.measure import Measurements
from ij.plugin.filter import Analyzer
from ij.gui import Overlay
from ij.plugin import ImageCalculator
from ij.plugin import Duplicator
from ij.plugin import ChannelSplitter
from ij.plugin import SubstackMaker

from net.imglib2 import FinalInterval
from jarray import array
import sys
from net.imglib2.meta import ImgPlus
from net.imglib2.img.display.imagej import ImageJFunctions
from java.awt import Color
from ij.plugin.filter import BackgroundSubtracter

import os
import copy

homedir=IJ.getDirectory('imagej')
jythondir=os.path.join(homedir,'plugins/Scripts/Evalulab/')
jythondir=os.path.abspath(jythondir)
sys.path.append(jythondir)

import SpotDetectionFunction
reload(SpotDetectionFunction)
from SpotDetectionFunction import SpotDetectionGray
from SpotDetectionFunction import SpotDetection2
from SpotDetectionFunction import SpotDetection3

import CountParticles
reload(CountParticles)
from CountParticles import countParticles

import ExportDataFunction
reload(ExportDataFunction)
from ExportDataFunction import exportToCsv

import MessageStrings
reload(MessageStrings)
from MessageStrings import Messages 

import DetectionParams
reload(DetectionParams)
from DetectionParams import DetectionParams

import Utility
reload(Utility)

def drawRoi(processor, roi, color):
	processor.setColor(color)
	processor.draw(roi)

def runPoreDetection(inputImp, data, ops, display):

	name=inputImp.getTitle()	
	inputDataset=Utility.getDatasetByName(data, name)
	
	detectionParameters=DetectionParams()

	roi=inputImp.getRoi()
	if (roi is None):
		message=name+": "+Messages.NoRoi
		IJ.write(message)
		return

	roi=inputImp.getRoi().clone();

	header, statslist=poreDetectionTrueColor(inputImp, inputDataset, roi, ops, data, display, detectionParameters)

	directory, overlayname, roiname=Utility.createImageNames(inputImp)
	statsname=directory+'truecolor_stats.csv'
	
	IJ.save(inputImp, overlayname);
	IJ.saveAs(inputImp, "Selection", roiname);

	header.insert(0,Messages.FileName)
	statslist.insert(0,name)

	print header
	print statslist

	ExportDataFunction.exportSummaryStats(statsname, header, statslist)

def poreDetectionTrueColor(inputImp, inputDataset, inputRoi, ops, data, display, detectionParameters):
	
	detectionParameters.setCalibration(inputImp);
	
	# calculate area of roi 
	stats=inputImp.getStatistics()
	inputRoiArea=stats.area
	
	# get the bounding box of the active roi
	inputRec = inputRoi.getBounds()
	x1=long(inputRec.getX())
	y1=long(inputRec.getY())
	x2=x1+long(inputRec.getWidth())-1
	y2=y1+long(inputRec.getHeight())-1
	
	# crop the roi
	interval=FinalInterval( array([x1, y1 ,0], 'l'), array([x2, y2, 2], 'l') )
	cropped=ops.image().crop(inputDataset.getImgPlus(), interval ) 
	
	datacropped=data.create(cropped)
	display.createDisplay("cropped", datacropped)
	croppedPlus=IJ.getImage()
	
	# instantiate the duplicator and the substackmaker classes
	duplicator=Duplicator()
	substackMaker=SubstackMaker()
	
	# duplicate the roi
	duplicate=duplicator.run(croppedPlus)

	# separate into RGB and get the blue channel
	IJ.run(duplicate, "RGB Stack", "")
	bluePlus=substackMaker.makeSubstack(duplicate, "3-3")
	blue=ImgPlus(ImageJFunctions.wrapByte(bluePlus))
	bluePlus.setTitle("Blue")
	
	# duplicate and look for bright spots
	thresholdedLight=SpotDetection2(bluePlus)

	# duplicate and look for dark spots
	thresholdedDark=SpotDetection3(bluePlus, True)

	# convert to mask
	Prefs.blackBackground = True
	#IJ.run(thresholdedDark, "Convert to Mask", "")substackMaker

	# clear the region outside the roi
	clone=inputRoi.clone()
	clone.setLocation(0,0)
	Utility.clearOutsideRoi(thresholdedLight, clone)
	Utility.clearOutsideRoi(thresholdedDark, clone)
	roimClosedPores = RoiManager(True)
	detectionParameters.setCalibration(thresholdedDark)
	countParticles(thresholdedDark, roimClosedPores, detectionParameters.closedPoresMinSize, detectionParameters.closedPoresMaxSize, \
		detectionParameters.closedPoresMinCircularity, detectionParameters.closedPoresMaxCircularity)

	# count number of open pores
	roimOpenPores = RoiManager(True)
	detectionParameters.setCalibration(thresholdedDark)
	countParticles(thresholdedDark, roimOpenPores, detectionParameters.openPoresMinSize, detectionParameters.openPoresMaxSize, \
		detectionParameters.openPoresMinCircularity, detectionParameters.openPoresMaxCircularity)

	# count number of sebum
	roimSebum = RoiManager(True)
	detectionParameters.setCalibration(thresholdedLight)
	countParticles(thresholdedLight, roimSebum, detectionParameters.sebumMinSize, detectionParameters.sebumMaxSize, \
		detectionParameters.sebumMinCircularity, detectionParameters.sebumMaxCircularity)
	
	# create lists for open and closed pores
	closedPoresList=[]
	for roi in roimClosedPores.getRoisAsArray():
		closedPoresList.append(roi.clone())
	openPoresList=[]
	for roi in roimOpenPores.getRoisAsArray():
		openPoresList.append(roi.clone())

	# create lists for sebum
	sebumsList=[]
	for roi in roimSebum.getRoisAsArray():
		sebumsList.append(roi.clone())

	# a list of all pores
	allList=closedPoresList+openPoresList+sebumsList
	
	# calculate the stats for all pores
	detectionParameters.setCalibration(bluePlus)
	statsDict=CountParticles.calculateParticleStats(bluePlus, allList)

	poresTotalArea=0
	for area in statsDict['Areas']:
		poresTotalArea=poresTotalArea+area
		print area
	poresAverageArea=poresTotalArea/len(statsDict['Areas'])
		

	# for each roi add the offset such that the roi is positioned in the correct location for the 
	# original image
	[roi.setLocation(roi.getXBase()+x1, roi.getYBase()+y1) for roi in allList]
	
	# draw the rois on the image
	inputImp.getProcessor().setColor(Color.green)
	IJ.run(inputImp, "Line Width...", "line=3");
	inputImp.getProcessor().draw(inputRoi)
	IJ.run(inputImp, "Line Width...", "line=1");
	[CountParticles.drawParticleOnImage(inputImp, roi, Color.red) for roi in closedPoresList]
	[CountParticles.drawParticleOnImage(inputImp, roi, Color.magenta) for roi in openPoresList]
	[CountParticles.drawParticleOnImage(inputImp, roi, Color.green) for roi in sebumsList]
	
	inputImp.updateAndDraw()
	
	# close images that represent intermediate steps
	croppedPlus.changes=False
	croppedPlus.close()
	bluePlus.changes=False
	bluePlus.close()

	print "Total ROI Area: "+str(inputRoiArea)
	print "Num closed pores: "+str(len(closedPoresList))
	print "Num open pores: "+str(len(openPoresList))
	print "Num sebums: "+str(len(sebumsList))
	
	print "Total particles: "+str(len(allList))+ " total area: "+str(poresTotalArea)
	
	statslist=[inputRoiArea, len(allList), len(closedPoresList), len(openPoresList), len(sebumsList), poresAverageArea, 100*poresTotalArea/inputRoiArea]
	header=[Messages.TotalAreaMask, Messages.TotalDetectedPores, Messages.ClosedPores, Messages.OpenPores, Messages.Sebum, Messages.PoresAverageArea, Messages.PoresFractionalArea]
	
	return header,statslist

	