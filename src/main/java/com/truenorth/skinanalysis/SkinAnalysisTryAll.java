package com.truenorth.skinanalysis;

import java.awt.Color;

import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import com.truenorth.colorspace.LabCommand;

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
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Evalulab>Skin Analysis>Try All")
public class SkinAnalysisTryAll<T extends RealType<T>> implements Command {

	@Parameter
	private LogService logger;

	@Parameter
	private OpService ops;

	@Parameter
	private CommandService command;

	@Parameter
	private ImagePlus imgPlus;

	@Parameter
	private UIService ui;

	@Parameter
	private DatasetService data;

	@Parameter
	private Dataset dataset;

	/**
	 */
	@Override
	public void run() {

		try {
			ImagePlus cropped= new Duplicator().run(imgPlus);
			cropped.show();
			imgPlus.close();
			
			logger.info("running command!");
		
			Module module=null;

			try {
				module = command.run(LabCommand.class, false, "imgPlus", cropped, "to8bit", true, "show", false).get();
			} catch (Exception e) {

			}
			
			ImagePlus c1=(ImagePlus)module.getOutput("c1");
			cropped.changes=false;
			//cropped.close();
			c1.show();
			
			//IJ.run(c1, "Auto Local Threshold", "method=[Try all] radius=100 parameter_1=0 parameter_2=0");
			ImagePlus temp= new Duplicator().run(c1);
			temp.show();
			IJ.run(temp, "Auto Threshold", "method=[Try all]");
	

		} catch (Exception ex) {

		}

	}

}
