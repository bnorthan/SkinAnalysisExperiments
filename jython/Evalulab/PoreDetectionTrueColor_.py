# @Dataset d
# @DatasetService data
# @DisplayService display
# @net.imagej.ops.OpService ops
# @ImageDisplayService ids

from ij import IJ
from ij.plugin import Duplicator

import os
import sys

homedir=IJ.getDirectory('imagej')
jythondir=os.path.join(homedir,'plugins/Scripts/Evalulab/')
jythondir=os.path.abspath(jythondir)
sys.path.append(jythondir)

import PoreDetectionTrueColor
reload(PoreDetectionTrueColor)
from PoreDetectionTrueColor import runPoreDetection

if __name__ == '__main__':
	inputImp = IJ.getImage()
	runPoreDetection(inputImp, data, ops, display)

	
	
