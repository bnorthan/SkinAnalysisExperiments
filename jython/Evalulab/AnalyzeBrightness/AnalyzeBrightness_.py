# @ImagePlus imp

import os
import sys

from ij import IJ
from ij.measure import Measurements
from ij.plugin.frame import RoiManager

homedir=IJ.getDirectory('imagej')
jythondir=os.path.join(homedir,'plugins/Evalulab/')
jythondir=os.path.abspath(jythondir)
sys.path.append(jythondir)

import ExportDataFunction
reload(ExportDataFunction)
from ExportDataFunction import exportStatsList

roim=RoiManager.getInstance()

if (roim!=None):

	for roi in roim.getRoisAsArray(): print roi
	
	nROIs=roim.getCount()
	
	names=[]
	brightness=[]
	
	statsList=[]
	for n in range(0,nROIs):
		
		print roim.getName(n)
		roi =roim.getRoi(n)
		
		imp.setRoi(roi)
		stats = imp.getStatistics(Measurements.AREA|Measurements.MEAN|Measurements.ELLIPSE)
	
		statsList.append([roim.getName(n), stats.mean])
	
	print statsList 
	
	directory=imp.getOriginalFileInfo().directory
	name=os.path.splitext(imp.getTitle())[0];
	
	fullname=overlayname=os.path.join(directory, name+'.csv')
	
	exportStatsList(fullname, statsList)
	
	print fullname
