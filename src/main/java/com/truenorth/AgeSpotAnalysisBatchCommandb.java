package com.truenorth;

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
 * 
 * Test starting point
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Skin Analysis Jan-2015-b")
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
	 * 
	 */
	@Override
	public void run() {
		DirectoryChooser dialog = new DirectoryChooser("test");

		String baseDirectory = dialog.getDirectory();
		// String outptutDirectory =

		String strRoutine = "spot_autoD0_D84";

		String[] imageSets = new String[] { "001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011",
				"012", "013", "014", "015", "016", "017", "018", "019", "020" };

		// String[] imageSets = new String[] { "001", "002"};

		String[] ids_firstpass = new String[] { "D0", "D84" };
		String[] ids = new String[] { "D28", "D56", "D84" };

		for (String set : imageSets) {

			String outPath = "/home/bnorthan/images/evalulab/Analysis_01_22_2016/spot_routine_b";
			String strCSVMaster = "/home/bnorthan/images/evalulab/Analysis_01_22_2016/stats_spot_routine_b.csv";

			Path dir = Paths.get(baseDirectory, set);
			System.out.println(dir.toString());
			double threshold = 0;
			double n = 0;

			for (String id : ids_firstpass) {
				String name = set + "-" + id + ".tif";
				name = Paths.get(dir.toString(), name).toString();
				System.out.println("    " + name);

				ImagePlus imgPlus = IJ.openImage(name);

				imgPlus.show();

				Module module = null;

				try {

					String temp = null;

					if (n == 0)
						temp = strCSVMaster;

					module = command.run(SkinAnalysis.class, true, "imgPlus", imgPlus, "show", false, "ignoreEdge",
							false, "method", "Default", "mthreshold", 0, "erodeCycles", 3, "minSize", 100, "maxSize",
							10000000, "outPath", outPath, "edgeThresh", 70, "strCSVMaster", temp, "strRoutine",
							strRoutine).get();

					threshold = threshold + (Double) module.getOutput("threshold");
					n++;
					System.out.println("Threshold is: " + threshold);

					imgPlus.changes = false;
					imgPlus.close();

				} catch (Exception ex) {
					System.out.println("exception: " + ex);
				}
			}

			threshold = threshold / n;
			System.out.println("Threshold is: " + threshold);

			for (String id : ids) {
				String name = set + "-" + id + ".tif";
				name = Paths.get(dir.toString(), name).toString();
				System.out.println("    " + name);

				ImagePlus imgPlus = IJ.openImage(name);

				imgPlus.show();

				Module module = null;

				try {
					System.out.println("reused threshold is: " + threshold);
					module = command.run(SkinAnalysis.class, true, "imgPlus", imgPlus, "show", false, "ignoreEdge",
							false, "method", "Manual", "mthreshold", threshold, "erodeCycles", 3, "minSize", 100,
							"maxSize", 10000000, "outPath", outPath, "edgeThresh", 50, "strCSVMaster", strCSVMaster,
							"strRoutine", strRoutine).get();

				} catch (Exception ex) {
					System.out.println("exception: " + ex);
				}

				imgPlus.changes = false;
				imgPlus.close();

			}

		}

		logger.info("finished");
	}

}
