package com.truenorth.segment;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import net.imagej.ops.OpService;

@Plugin(type = Command.class, headless = true)
public class ErodeCommand implements Command {

	@Parameter
	OpService ops;

	@Parameter
	LogService logger;

	@Parameter
	private ImagePlus imp;

	@Parameter
	private int erodeCycles = 0;

	@Override
	public void run() {

		IJ.run(imp, "Options...", "iterations=1 count=1 black pad");

		// loop and perform erode cycles
		for (int i = 0; i < erodeCycles; i++) {
			IJ.run(imp, "Erode", "");
		}

	}
}
