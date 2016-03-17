# @DatasetService data
# @DisplayService display
# @net.imagej.ops.OpService ops
# @ImageDisplayService ids

from ij import IJ
from ij.io import DirectoryChooser

import os
import sys

homedir=IJ.getDirectory('imagej')
jythondir=os.path.join(homedir,'plugins/Scripts/Evalulab/')
jythondir=os.path.abspath(jythondir)
sys.path.append(jythondir)

import PoreDetectionUV
reload(PoreDetectionUV)
from PoreDetectionUV import runPoreDetection

import MessageStrings
reload(MessageStrings)
from MessageStrings import Messages

import Utility
reload(Utility)

if __name__ == '__main__':
	dlg=DirectoryChooser(Messages.ChooseImageDirectory)
	print dlg.getDirectory()

	imageList=Utility.getImageListFromDirectory(dlg.getDirectory())

	for imageName in imageList:
		fullImageName=os.path.join(dlg.getDirectory(), imageName)
		image=IJ.openImage(fullImageName)
		image.show()
		runPoreDetection(image, data, ops, display)
		image.close()
		
	