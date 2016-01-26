package com.truenorth;

import java.awt.Color;

import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.Duplicator;
import ij.plugin.frame.RoiManager;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.Interval;
import net.imglib2.type.numeric.RealType;

/**
 * Test starting point
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Skin Analysis 3")
public class SkinAnalysis3<T extends RealType<T>> implements Command {

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
			System.out.println("???");
			Module module = command.run(SkinAnalysis.class, true, "imgPlus", imgPlus, "dataset", dataset, "show", false,
					"ignoreEdge", false, "method", "Default", "erodeCycles", 3).get();

			System.out.println(module.getInfo());

		} catch (Exception ex) {

		}

	}

}
