package com.truenorth;

import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import net.imagej.Dataset;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Batch Test!")
public class TestBatchCommand extends AbstractBatchCommand {
	@Parameter 
	LogService logger;
	
	@Parameter 
	CommandService command;
	
	void processImage(ImagePlus imgPlus, Dataset dataSet) {
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
		IJ.run(temp, "Auto Threshold", "method=Default");
		IJ.run(temp, "Erode", "");
		IJ.run(temp, "Erode", "");
		IJ.run(temp, "Erode", "");
		//ImagePlus c2=lc.getC2();
		//c2.show();
	}
}
