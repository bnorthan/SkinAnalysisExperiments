from ij import IJ
from ij.measure import Calibration

import os
import sys
import csv

class DetectionParams(object):
	def __init__(self):
		self.pixelWidth=1.0
		self.pixelHeight=1.0
		
		self.porphyrinMinSize=10
		self.porphyrinMaxSize=200
		self.porphyrinMinCircularity=0.5
		self.porphyrinMaxCircularity=1.0
		self.porphyrinRedPercentage=0.3
		
		self.sebumMinSize=1
		self.sebumMaxSize=750
		self.sebumMinCircularity=0.4
		self.sebumMaxCircularity=1.0
		
		self.closedPoresMinSize=10
		self.closedPoresMaxSize=300
		self.closedPoresMinCircularity=0.3
		self.closedPoresMaxCircularity=1.0
		
		self.openPoresMinSize=300
		self.openPoresMaxSize=3500
		self.openPoresMinCircularity=0.3
		self.openPoresMaxCircularity=1.0

		self.readCsv()
		
	def setCalibration(self, imp):
		calibration=Calibration()
		calibration.pixelWidth=self.pixelWidth
		calibration.pixelHeight=self.pixelHeight
		imp.setCalibration(calibration)

	def readCsv(self):
		settingsdir = os.path.join(IJ.getDirectory('imagej'), 'plugins/Scripts/Evalulab/');

		print 'IJm dir:'+IJ.getDirectory('imagej')
		print settingsdir
		settingsname=settingsdir+'settings.txt'
		print settingsname
		print settingsname

		csvfile=open(settingsname, 'rb')
		csvreader=csv.reader(csvfile, delimiter=',')

		for row in csvreader:
			if (len(row)>0):
				if (row[0]=='pixelWidth'):
					self.pixelWidth=float(row[1])
				elif (row[0]=='pixelHeight'):
					self.pixelHeight=float(row[1])
				elif (row[0]=='porphyrinMaxSize'):
					self.porphyrinMaxSize=float(row[1])
				elif (row[0]=='porphyrinMinCircularity'):
					self.porphyrinMinCircularity=float(row[1])
				elif (row[0]=='porphyrinMaxCircularity'):
					self.porphyrinMaxCircularity=float(row[1])
				elif (row[0]=='porphyrinRedPercentage'):
					self.porphyrinRedPercentage=float(row[1])
				elif (row[0]=='sebumMinSize'):
					self.sebumMinSize=float(row[1])
					print self.sebumMinSize
				elif (row[0]=='sebumMaxSize'):
					self.sebumMaxSize=float(row[1])
				elif (row[0]=='sebumMinCircularity'):
					self.sebumMinCircularity=float(row[1])
				elif (row[0]=='sebumMaxCircularity'):
					self.sebumMaxCircularity=float(row[1])
				elif (row[0]=='closedPoresMinSize'):
					self.closedPoresMinSize=float(row[1])
				elif (row[0]=='closedPoresMaxSize'):
					self.closedPoresMaxSize=float(row[1])
				elif (row[0]=='closedPoresMinCircularity'):
					self.closedPoresMinCircularity=float(row[1])
				elif (row[0]=='closedPoresMaxCircularity'):
					self.closedPoresMaxCircularity=float(row[1])
				elif (row[0]=='openPoresMinSize'):
					self.openPoresMinSize=float(row[1])
				elif (row[0]=='openPoresMaxSize'):
					self.openPoresMaxSize=float(row[1])
				elif (row[0]=='openPoresMinCircularity'):
					self.openPoresMinCircularity=float(row[1])
				elif (row[0]=='openPoresMaxCircularity'):
					self.openPoresMaxCircularity=float(row[1])
			