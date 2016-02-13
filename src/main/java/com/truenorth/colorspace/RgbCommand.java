package com.truenorth.colorspace;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.plugin.ChannelSplitter;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Evalulab>ColorSpace>RGB")
public class RgbCommand<T extends RealType<T>> implements Command {

	@Parameter
	private ImagePlus imgPlus;
	
	@Parameter(type=ItemIO.OUTPUT)
	private ImagePlus R;
	
	@Parameter(type=ItemIO.OUTPUT)
	private ImagePlus G;
	
	@Parameter(type=ItemIO.OUTPUT)
	private ImagePlus B;
	
	@Override
	public void run() {	
		ImagePlus[] channels = ChannelSplitter.split(imgPlus);
		
		R=channels[0];
		G=channels[1];
		B=channels[2];

	}

}
