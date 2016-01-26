package com.truenorth;

import java.io.File;
import java.util.ArrayList;

import org.scijava.command.Command;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

import ij.IJ;
import ij.ImagePlus;
import ij.io.DirectoryChooser;
import net.imagej.Dataset;

/**
 * 
 * Test starting point
 */
public abstract class AbstractBatchCommand implements Command {

	@Parameter
	private LogService logger;

	@Parameter
	private IOService io;

	/**
	 * 
	 */
	@Override
	public void run() {
		DirectoryChooser dialog = new DirectoryChooser("test");

		ArrayList<File> listOfFiles = FileIOUtils.getFileListFromDirectory(dialog.getDirectory(), "tif");

		for (File file : listOfFiles) {
			logger.info("file name" + file.getName());

			String fullName = dialog.getDirectory() + "/" + file.getName();
			ImagePlus imgPlus = IJ.openImage(fullName);

			Dataset data;

			try {
				data = (Dataset) io.open(fullName);
			} catch (Exception e) {
				return;
			}

			imgPlus.show();

			processImage(imgPlus, data);

			imgPlus.changes = false;
			imgPlus.close();

		}

		logger.info("finished");
	}

	abstract void processImage(ImagePlus imgPlus, Dataset dataset);

}
