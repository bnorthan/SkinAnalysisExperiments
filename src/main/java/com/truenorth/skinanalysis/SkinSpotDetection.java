package com.truenorth.skinanalysis;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import com.truenorth.colorspace.LabCommand;
import com.truenorth.colorspace.RgbCommand;
import com.truenorth.count.CountParticles;
import com.truenorth.count.FilterEdgeParticlesCommand;
import com.truenorth.count.MaskStatsCommand;
import com.truenorth.data.WriteCSVCommand;
import com.truenorth.segment.ErodeCommand;
import com.truenorth.utils.ROIUtils;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.Duplicator;
import ij.plugin.frame.RoiManager;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imglib2.IterableInterval;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Test starting point
 */
@Plugin(type = Command.class, headless = true, menuPath = "Evalulab>SkinSpotDetection")
public class SkinSpotDetection<T extends RealType<T>> implements Command {

	@Parameter
	private LogService log;

	@Parameter
	private OpService ops;

	@Parameter
	private CommandService cmd;

	@Parameter
	private UIService ui;

	@Parameter
	private ImagePlus imp;

	@Parameter(required = false)
	private boolean show = false;

	@Parameter(required = false, label = "Method", choices = { "Manual", "Automatic" })
	private String method = "Automatic";

	@Parameter(required = false)
	private Double mthreshold;

	@Parameter(required = false)
	private int erodeCycles = 3;

	@Parameter(required = false)
	private int minSize = 100;

	@Parameter(required = false)
	private int maxSize = 10000000;

	@Parameter(required = false,style="directory")
	private File outPath=new File("C:\\Brian2016\\Images\\Evalulab\\ImagesJan2016\\");
	
	@Parameter(required = false)
	private int edgeThresh = 50;

	//@Parameter(required = false)
	//private String strCSVMaster = null;
	
	//@Parameter(required = false)
	//private File fileCSVMaster=new File("C:\\Brian2016\\Images\\Evalulab\\ImagesJan2016\\stats_spot_routine_b.csv");

	//@Parameter(required = false)
	//private String strRoutine = null;

	//@Parameter(type = ItemIO.OUTPUT)
	//private Double threshold;

	/**
	 */
	@Override
	public void run() {

		try {

			String strCSVMaster = outPath.toString() + "\\stats_spot.csv";
			
			cmd.run(SkinAnalysis.class, true, "imp", imp, "show", show, "method", method,
					"mthreshold", mthreshold, "erodeCycles", erodeCycles, "minSize", minSize, "maxSize", maxSize, "outPath", outPath,
					"edgeThresh", edgeThresh, "fileCSVMaster", strCSVMaster, "strRoutine", "spots").get();

		} catch (Exception ex) {
			System.out.println("exception: " + ex);
		}

	}

}
