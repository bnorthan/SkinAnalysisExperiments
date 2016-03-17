package com.truenorth.skinanalysis;

import java.nio.file.Path;
import java.nio.file.Paths;

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
 * Age spot analysis using approach a: calculates an automatic threshold on the
 * first image and applies it to all images
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Evalulab>Skin Analysis>AgeSpotBatch-a")
public class AgeSpotAnalysisBatchCommanda implements Command {

	@Parameter
	private UIService ui;

	@Parameter
	private LogService logger;

	@Parameter
	private IOService io;

	@Parameter
	CommandService command;

	/**
	 * This routine calculates an automatic threshold on the first image and
	 * applies it to all images
	 */
	@Override
	public void run() {

		// get directory which contains images
		DirectoryChooser dialog = new DirectoryChooser("test");
		String baseDirectory = dialog.getDirectory();

		String strRoutine = "spot_autoD0";

		// define the days of the images to be processed

		/*
		 * String[] imageSets = new String[] { "001", "002", "003", "004",
		 * "005", "006", "007", "008", "009", "010", "011", "012", "013", "014",
		 * "015", "016", "017", "018", "019", "020" };
		 */
		String[] imageSets = new String[] { "003", "007", "010", "014", "016", "017", "019" };

		// String[] imageSets = new String[] { "003", "004"};

		String day1 = "D0";
		String[] days = new String[] { "D0", "D28", "D56", "D84" };

		for (String set : imageSets) {

			String outPath = GlobalSettings.getHomeDir() + "spot_routine_a/";
			String strCSVMaster = GlobalSettings.getHomeDir() + "/stats_spot_routine_a.csv";

			Path dir = Paths.get(baseDirectory, set);
			System.out.println(dir.toString());
			double threshold = 0;

			for (String day : days) {
				String name = set + "-" + day + ".tif";
				name = Paths.get(dir.toString(), name).toString();
				System.out.println("    " + name);

				ImagePlus imp = IJ.openImage(name);

				imp.show();

				Module module = null;

				// if on day 1 calculate auto-threshold
				if (day.equals(day1)) {
					try {
						module = command.run(SkinAnalysis.class, true, "imp", imp, "show", false, "method", "Automatic",
								"mthreshold", 0, "erodeCycles", 3, "minSize", 100, "maxSize", 10000000, "outPath",
								outPath, "edgeThresh", 50, "strCSVMaster", strCSVMaster, "strRoutine", strRoutine)
								.get();

						threshold = (Double) module.getOutput("threshold");
						System.out.println("Threshold is: " + threshold);

					} catch (Exception ex) {
						System.out.println("exception: " + ex);
					}
				} else {
					// if not day1 use threshold from day1
					try {
						System.out.println("reused threshold is: " + threshold);
						module = command.run(SkinAnalysis.class, true, "imp", imp, "show", false, "method", "Manual",
								"mthreshold", threshold, "erodeCycles", 3, "minSize", 100, "maxSize", 10000000,
								"outPath", outPath, "edgeThresh", 50, "strCSVMaster", strCSVMaster, "strRoutine",
								strRoutine).get();

					} catch (Exception ex) {
						System.out.println("exception: " + ex);
					}
				}

				imp.changes = false;
				imp.close();

			}

		}

		logger.info("finished");
	}

}
