import os

from ij import IJ
from ij import WindowManager

def GetOpenImageList():
	wList=WindowManager.getIDList()
	if (wList==None): return None
	titles=[]
	[titles.append(WindowManager.getImage(id).getTitle()) for id in wList]
	return titles

def printDatasets(data):
	for dataset in data.getDatasets():
		print dataset.getName()

def getDatasetByName(data, name):
	print 'here we are'
	print len(data.getDatasets())
	for dataset in data.getDatasets():
		print dataset.getName()
		print name
		print '-----'
		if dataset.getName()==name:
			return dataset
	return None

def clearOutsideRoi(imp, roi):
	imp.setRoi(roi)
	IJ.setBackgroundColor(0, 0, 0);
	IJ.run(imp, "Clear Outside", "");

def getImageListFromDirectory(directory):
    imagelist=[]
    for root, directories, filenames in os.walk(directory):
        for filename in sorted(filenames):
            if not filename.endswith(".tif"):
                continue
            imagelist.append(filename)
    return imagelist

def createImageNames(imp):
	directory=imp.getOriginalFileInfo().directory
	name=os.path.splitext(imp.getTitle())[0];

	# name of file to save image with overlay	
	overlaydir=os.path.join(directory, 'overlay')
	if not os.path.exists(overlaydir):
		os.makedirs(overlaydir)
	overlayname=os.path.join(overlaydir, name+'_overlay.tif')
	
	# name of file to save roi 
	roidir=os.path.join(directory, 'roi')
	if not os.path.exists(roidir):
		os.makedirs(roidir)
	roiname=os.path.join(roidir, name+'.roi')

	return directory, overlayname, roiname


		