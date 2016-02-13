from ij.measure import ResultsTable
from ij.measure import Measurements
from ij.plugin.frame import RoiManager
from ij.plugin.filter import ParticleAnalyzer

from java.lang import Double

def countParticles(imp, roim, minSize, maxSize, minCircularity, maxCircularity):
	# Create a table to store the results
	table = ResultsTable()
	
	# Create the particle analyzer
	pa = ParticleAnalyzer(ParticleAnalyzer.ADD_TO_MANAGER, Measurements.AREA|Measurements.MEAN|Measurements.ELLIPSE, table, minSize, maxSize, minCircularity, maxCircularity)
	pa.setRoiManager(roim)
	pa.setHideOutputImage(True)

	if pa.analyze(imp):
		print "All ok"
	else:
 		print "There was a problem in analyzing", blobs

 	areas = table.getColumn(0)
	intensities = table.getColumn(1)
	majors=table.getColumn(2)

def filterParticles(imp, roiArray, threshold) :
 	newRoiList=[]
 	for roi in roiArray:
 		imp.setRoi(roi)
		stats = imp.getStatistics(Measurements.MEAN)
		if stats.mean > threshold:
			newRoiList.append(roi)

	return newRoiList

def filterParticlesWithFunction(imp, roiArray, function):
	newRoiList=[]
	for roi in roiArray:
 		imp.setRoi(roi)
		
		if (function(imp, roi)==True):
			newRoiList.append(roi)
	return newRoiList
	
def filterParticlesOutsideRange(imp, roiArray, lowerthreshold, upperthreshold) :
 	newRoiList=[]
 	for roi in roiArray:
 		imp.setRoi(roi)
		stats = imp.getStatistics(Measurements.MEAN | Measurements.MIN_MAX)
		if (stats.min < lowerthreshold)  or  (stats.max > upperthreshold):
			newRoiList.append(roi)
	return newRoiList

def calculateParticleStatsUV(A, B, redMask, roiArray):
	areas=[]
	ALevel=[]
	BLevel=[]
	redPercentage=[]
	diameters=[]

	for roi in roiArray:
		A.setRoi(roi)
		stats = A.getStatistics(Measurements.AREA|Measurements.MEAN|Measurements.ELLIPSE)
		areas.append(stats.area)
		diameters.append(stats.minor)
		ALevel.append(stats.mean)
		B.setRoi(roi)
		stats=B.getStatistics()
		BLevel.append(stats.mean)
		redMask.setRoi(roi)
		stats=redMask.getStatistics()
		redPercentage.append(stats.mean)

	statsdict={}
	statsdict['Areas']=areas
	statsdict['ALevel']=ALevel
	statsdict['BLevel']=BLevel
	statsdict['redPercentage']=redPercentage
	statsdict['Diameters']=diameters

	return statsdict

def calculateParticleStats(intensity, roiArray):
	areas=[]
	intensities=[]
	
	for roi in roiArray:
		intensity.setRoi(roi)
		stats = intensity.getStatistics()
		areas.append(stats.area)
		intensities.append(stats.mean)
		
	statsdict={}
	statsdict['Areas']=areas
	statsdict['Intensity']=intensities
	
	return statsdict


def addParticleToOverlay(roi, overlay, color):
	roi.setStrokeColor(color)
	overlay.add(roi)
	
def drawParticleOnImage(imp, roi, color):
	imp.getProcessor().setColor(color)
	imp.getProcessor().draw(roi)



