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
 * 
 * Test starting point
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Skin Analysis Jan-2015-a")
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
	 * 
	 */
	@Override
	public void run() {
		DirectoryChooser dialog = new DirectoryChooser("test");

		String baseDirectory = dialog.getDirectory();
		String strRoutine="spot_autoD0";

	/*	String[] imageSets = new String[] { "001", "002", "003", "004",
		 "005", "006", "007", "008", "009", "010", "011",
		 "012", "013", "014", "015", "016", "017", "018", "019", "020" };
*/
		String[] imageSets = new String[] { "003","007","010", "014","016","017", "019" };

		//String[] imageSets = new String[] { "003", "004"};

		String id1 = "D0";
		String[] ids = new String[] { "D0", "D28", "D56", "D84" };

		for (String set : imageSets) {
			
			String outPath = "/home/bnorthan/images/evalulab/Analysis_02_08_2016/spot_routine_a";
			String strCSVMaster = "/home/bnorthan/images/evalulab/Analysis_02_08_2016/stats_spot_routine_a.csv";


			Path dir = Paths.get(baseDirectory, set);
			System.out.println(dir.toString());
			double threshold = 0;

			for (String id : ids) {
				String name = set + "-" + id + ".tif";
				name = Paths.get(dir.toString(), name).toString();
				System.out.println("    " + name);

				ImagePlus imgPlus = IJ.openImage(name);

				imgPlus.show();

				Module module = null;

				if (id.equals(id1)) {
					try {
						module = command.run(SkinAnalysis.class, true, "imgPlus", imgPlus, "show", false, "ignoreEdge",
								false, "method", "Default", "mthreshold", 0, "erodeCycles", 3, "minSize", 100,
								"maxSize", 10000000, "outPath", outPath, "edgeThresh", 50, "strCSVMaster", strCSVMaster,
								"strRoutine", strRoutine).get();

						threshold = (Double) module.getOutput("threshold");
						System.out.println("Threshold is: " + threshold);

					} catch (Exception ex) {
						System.out.println("exception: " + ex);
					}
				} else {
					try {
						System.out.println("reused threshold is: " + threshold);
						module = command.run(SkinAnalysis.class, true, "imgPlus", imgPlus, "show", false, "ignoreEdge",
								false, "method", "Manual", "mthreshold", threshold, "erodeCycles", 3, "minSize", 100,
								"maxSize", 10000000, "outPath", outPath, "edgeThresh", 50, "strCSVMaster", strCSVMaster,
								"strRoutine", strRoutine).get();

					} catch (Exception ex) {
						System.out.println("exception: " + ex);
					}
				}

				imgPlus.changes = false;
				imgPlus.close();

			}

		}

		logger.info("finished");
	}

}
