package com.truenorth;

import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import ij.ImagePlus;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;

/**
 * Test starting point
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Skin Analysis 2")
public class SkinAnalysis2<T extends RealType<T>> implements Command {

	@Parameter
	private LogService logger;

	@Parameter
	private OpService ops;

	@Parameter
	private CommandService command;

	@Parameter
	private UIService ui;

	@Parameter
	private DatasetService data;

	@Parameter
	private ImagePlus imgPlus;

	@Parameter
	private Dataset dataset;

	/**
	 */
	@Override
	public void run() {

		try {
			Module module = command.run(SkinAnalysis.class, true, "imgPlus", imgPlus, "dataset", dataset, "show", false,
					"ignoreEdge", true, "method", "Default", "erodeCycles", 3).get();

		} catch (Exception ex) {

		}

	}

}
