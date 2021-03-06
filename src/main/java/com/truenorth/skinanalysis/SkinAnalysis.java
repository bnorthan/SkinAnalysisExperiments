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
@Plugin(type = Command.class, headless = true /*menuPath = "Evalulab>SkinSpotDetection"*/)
public class SkinAnalysis<T extends RealType<T>> implements Command {

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
	
	@Parameter(required = false)
	private File fileCSVMaster=new File("C:\\Brian2016\\Images\\Evalulab\\ImagesJan2016\\stats_spot_routine_b.csv");

	@Parameter(required = false)
	private String strRoutine = null;

	@Parameter(type = ItemIO.OUTPUT)
	private Double threshold;

	/**
	 */
	@Override
	public void run() {

		try {

			// print some diagnostics
			System.out.println("Skin Analysis");
			log.info("The current image is, " + imp.getTitle() + "!");
			log.info("params: show"+show+" edgeThresh:" + edgeThresh + " m:" + method + " er:" + erodeCycles + " mt:" + mthreshold);

			// use the duplicator to make a duplicate
			ImagePlus croppedPlus = new Duplicator().run(imp);

			croppedPlus.show();

			// module object is used to get the outputs from commands
			Module module = null;

			// first run the LAB command
			module = cmd.run(LabCommand.class, false, "imgPlus", croppedPlus, "to8bit", false, "show", false).get();

			// get L channel
			ImagePlus L = (ImagePlus) module.getOutput("c1");
		
			// convert to Img
			Img<FloatType> imgL = ImageJFunctions.convertFloat(L);

			FloatType t;

			// calculate the threshold
			if (!method.equals("Manual")) {
				// if not using a manual threshold calculate auto threshold
				// using ij1 method

				// TODO: support other methods

				Histogram1d<FloatType> hist = ops.image().histogram(imgL, 256);
				t = ops.threshold().ij1(hist);

				threshold = t.getRealDouble();

				System.out.println("Ops thresh is: " + threshold);
			} else {
				t = new FloatType();
				t.setReal(mthreshold);
			}

			// apply the threshold
			IterableInterval<BitType> bitImgThresholded = ops.threshold().apply(imgL, t);

			// TODO: add look for dark objects to threshold ops
			for (BitType b : bitImgThresholded) {
				b.not();
			}

			// it will be more convenient if the output is unsigned byte type
			Img<UnsignedByteType> byteImgThresholded = ops.create().img(bitImgThresholded, new UnsignedByteType());

			// so create a scaler op
			Op scale = ops.op(Ops.Convert.Scale.class, UnsignedByteType.class, BitType.class);

			// and use a map to apply the scaling to each pixel
			ops.map(byteImgThresholded, bitImgThresholded, scale);

			// Img<UnsignedByteType>
			// byteImgThresholded=(Img<UnsignedByteType>)ops.convert().uint8(bitImgThresholded);

			// go back to IJ1
			ImagePlus thresholded = ImageJFunctions.wrapUnsignedByte(byteImgThresholded, "thresholded");
			
			// erode command
			if (erodeCycles > 0) {
				module = cmd.run(ErodeCommand.class, false, "imp", thresholded, "erodeCycles", erodeCycles).get();
			}

			// HACK: TODO look into this bug, seems we need to duplicate to get
			// image to update
			thresholded.updateAndDraw();
			thresholded = new Duplicator().run(thresholded);
			thresholded.show();

			// this command calculate stats inside and outside the mask
			module = cmd.run(MaskStatsCommand.class, true, "mask", ImageJFunctions.wrapByte(thresholded), "intensity",
					ImageJFunctions.wrapFloat(L)).get();

			// create a header for .csv file
			final ArrayList<Object> header = new ArrayList<Object>(Arrays.asList("image name", "method", "percent",
					"r_inside", "r_outside", "g_inside", "g_outside", "b_inside", "b_outside"));

			// create a row of data
			final ArrayList<Object> data = new ArrayList<Object>();

			// add name of image
			data.add(imp.getTitle());

			// add type of routine used
			data.add(strRoutine);

			// percentage of foreground pixels
			data.add(module.getOutput("percent"));

			// run rgb command and get r, g and b channels
			module = cmd.run(RgbCommand.class, false, "imgPlus", croppedPlus).get();
			ImagePlus r = (ImagePlus) module.getOutput("R");
			ImagePlus g = (ImagePlus) module.getOutput("G");
			ImagePlus b = (ImagePlus) module.getOutput("B");

			ImagePlus[] rgb = { r, g, b };

			// for each channel in rgb
			for (ImagePlus c : rgb) {
				// calculate stats inside and outside mask
				module = cmd.run(MaskStatsCommand.class, true, "mask", ImageJFunctions.wrapByte(thresholded),
						"intensity", ImageJFunctions.wrapByte(c)).get();

				data.add(module.getOutput("insideIntensity"));
				data.add(module.getOutput("outsideIntensity"));
			}

			System.out.println();
			System.out.println(header);
			System.out.println(data);
			System.out.println();

			// create an roi manager
			RoiManager roim = new RoiManager(true);

			// run the count particles command
			module = cmd.run(CountParticles.class, false, "imgPlus", thresholded, "minSize", minSize, "maxSize",
					maxSize, "minCircularity", 0.0, "maxCircularity", 1.0, "roim", roim, "table", null).get();

			// print the number of rois found
			log.info("num rois: " + roim.getRoisAsArray().length);

			// get ROIs as array
			Roi[] rois = roim.getRoisAsArray();

			// filter out ROIs close to edge
			module = cmd.run(FilterEdgeParticlesCommand.class, false, "imp", L, "rois", rois, "edgeThresh", edgeThresh)
					.get();

			ArrayList<Roi> filteredRois = (ArrayList<Roi>) module.getOutput("filteredRois");

			// draw ROIs on cropped image
			ROIUtils.drawParticlesOnImage(croppedPlus, filteredRois, Color.CYAN);
			croppedPlus.updateAndDraw();

			// add offsets to ROIs and draw on original image
			//ROIUtils.addOffsets(rois, (long) imp.getRoi().getBounds().getX(), (long) imp.getRoi().getBounds().getY());
			//ROIUtils.drawParticlesOnImage(imp, filteredRois, Color.CYAN);
			//imp.updateAndDraw();

			String name = imp.getShortTitle();

			Path newDir = Paths.get(outPath.getAbsolutePath());

			if (Files.notExists(newDir))
				Files.createDirectory(newDir);

			Path fullName = Paths.get(newDir.toString(), name);

			String croppedName = fullName.toString() + "_cropped" + ".tif";
			IJ.save(croppedPlus, croppedName);
			
			// if csv specified write measurements to the file
			if (fileCSVMaster != null) {
				
				String strCSVMaster=fileCSVMaster.getAbsolutePath();


				if (!Files.exists(Paths.get(strCSVMaster))) {
					cmd.run(WriteCSVCommand.class, true, "fileName", strCSVMaster, "data", header).get();
				}

				cmd.run(WriteCSVCommand.class, true, "fileName", strCSVMaster, "data", data).get();
			}
			
			thresholded.changes=false;
			thresholded.close();

			if (!show) {
				croppedPlus.changes = false;
				croppedPlus.close();
			}

		} catch (Exception ex) {
			System.out.println("exception: " + ex);
		}

	}

}
