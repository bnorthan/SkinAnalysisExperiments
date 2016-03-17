package com.truenorth.skinanalysis;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import ij.IJ;
import ij.ImagePlus;
import ij.io.DirectoryChooser;

/**
 * 
 * Test starting point
 */
@Plugin(type = Command.class, headless = true, menuPath = "Evalulab>SkinSpotBatch-b")
public class AgeSpotAnalysisBatchCommandb implements Command {

	@Parameter
	private UIService ui;

	@Parameter
	private LogService logger;

	@Parameter
	private IOService io;

	@Parameter
	CommandService command;

	/**
	 * This routine calculates the threshold as the average between the
	 * threshold at day 0 and day 84, then applies that threshold to all images
	 */
	@Override
	public void run() {
		DirectoryChooser dialog = new DirectoryChooser("test");

		String baseDirectory = dialog.getDirectory();
		
		File[] files=new File(baseDirectory).listFiles();

		ArrayList<String> imageSets=new ArrayList<String>();
		
		for (File file : files) {
			if (file.isDirectory()) {
				String name=file.getName();
				
				try {
					logger.info(name+" "+Integer.valueOf(name));
					imageSets.add(name);
				}
				catch (Exception e) {
					// continue
				}
				
			}
		}

		String strRoutine = "spot_autoD0_D84";

		/*
		 * String[] imageSets = new String[] { "001", "002", "003", "004",
		 * "005", "006", "007", "008", "009", "010", "011", "012", "013", "014",
		 * "015", "016", "017", "018", "019", "020" };
		 */

		//String[] imageSets = new String[] { "010", "011" };

		String[] days_firstpass = new String[] { "D0", "D84" };
		String[] days = new String[] { "D28", "D56", "D84" };

		for (String set : imageSets) {

			Path dir = Paths.get(baseDirectory, set);
			System.out.println(dir.toString());
			double threshold = 0;
			double n = 0;

			String outPath = baseDirectory + "spot_routine_b/";
			String strCSVMaster = baseDirectory + "stats_spot_routine_b.csv";

			for (String day : days_firstpass) {
				String name = set + "-" + day + ".tif";
				name = Paths.get(dir.toString(), name).toString();
				System.out.println("    " + name);

				ImagePlus imp = IJ.openImage(name);

				imp.show();

				Module module = null;

				try {

					String temp = null;

					if (n == 0)
						temp = strCSVMaster;

					module = command.run(SkinAnalysis.class, true, "imp", imp, "show", false, "method", "Automatic",
							"mthreshold", 0, "erodeCycles", 3, "minSize", 100, "maxSize", 10000000, "outPath", outPath,
							"edgeThresh", 70, "fileCSVMaster", temp, "strRoutine", strRoutine).get();

					threshold = threshold + (Double) module.getOutput("threshold");
					n++;
					System.out.println("Threshold is: " + threshold);

					imp.changes = false;
					imp.close();

				} catch (Exception ex) {
					System.out.println("exception: " + ex);
				}
			}

			threshold = threshold / n;
			System.out.println("Threshold is: " + threshold);

			for (String day : days) {
				String name = set + "-" + day + ".tif";
				name = Paths.get(dir.toString(), name).toString();
				System.out.println("    " + name);

				ImagePlus imp = IJ.openImage(name);

				imp.show();

				try {

					System.out.println("reused threshold is: " + threshold);
					command.run(SkinAnalysis.class, true, "imp", imp, "show", false, "method", "Manual", "mthreshold",
							threshold, "erodeCycles", 3, "minSize", 100, "maxSize", 10000000, "outPath", outPath,
							"edgeThresh", 50, "fileCSVMaster", strCSVMaster, "strRoutine", strRoutine).get();

				} catch (Exception ex) {
					System.out.println("exception: " + ex);
				}

				imp.changes = false;
				imp.close();

			}

		}

		logger.info("finished");
	}

}
