package com.truenorth;

import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import net.imagej.Dataset;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Skin Analysis Batch")
public class SkinAnalysisBatchCommand extends AbstractBatchCommand {
	@Parameter
	LogService logger;

	@Parameter
	CommandService command;

	void processImage(ImagePlus imgPlus, Dataset dataSet) {
		try {
			command.run(SkinAnalysis.class, true, "imgPlus", imgPlus, "dataset", dataSet, "show", false, "ignoreEdge",
					false, "method", "Default", "erodeCycles", 3).get();
		} catch (Exception ex) {
			System.out.println("exception: " + ex);
		}
	}
}
