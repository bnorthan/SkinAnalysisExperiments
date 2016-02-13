# @Dataset d
# @DatasetService data
# @DisplayService display
# @net.imagej.ops.OpService ops
# @ImageDisplayService ids

from ij import IJ

import os
import sys
import time

homedir=IJ.getDirectory('imagej')
jythondir=os.path.join(homedir,'plugins/Evalulab/')
jythondir=os.path.abspath(jythondir)
sys.path.append(jythondir)

import PoreDetectionUV
reload(PoreDetectionUV)
from PoreDetectionUV import runPoreDetection

if __name__ == '__main__':
	inputImp = IJ.getImage()

	start_time = time.time()

	runPoreDetection(inputImp, data, ops, display)

	run_time=time.time() - start_time

	#IJ.showMessage("--- %f seconds ---" % run_time )

	
	
