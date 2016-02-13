import os
import csv

def exportToCsv(name, roiList):
	myFile = open(name, 'w')
	writer = csv.writer(myFile, delimiter=',')
	[writer.writerow([roi.getXBase(), roi.getYBase()]) for roi in roiList]

	myFile.flush()
	myFile.close()

def exportSummaryStats(name, header, stats):
	if not os.path.exists(name):
		myFile= open(name, 'w')
		writer = csv.writer(myFile, delimiter=',')	
		writer.writerow(header)
	else:
		myFile= open(name, 'a')
		writer = csv.writer(myFile, delimiter=',')
		
	writer.writerow(stats)

	myFile.flush()
	myFile.close()

def exportUVStats(name, stats):
	myFile = open(name, 'w')
	writer = csv.writer(myFile, delimiter=',')

	areas=stats['Areas']
	redPercentages=stats['redPercentage']

	for i in range(len(areas)):
		writer.writerow([areas[i], redPercentages[i]])

	myFile.flush()
	myFile.close()

def exportStatsList(name, statsList):
	myFile = open(name, 'w')
	writer = csv.writer(myFile, delimiter=',')

	for row in statsList:
			writer.writerow(row)

	myFile.flush()
	myFile.close()