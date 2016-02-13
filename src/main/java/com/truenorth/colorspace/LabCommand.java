package com.truenorth.colorspace;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Evalulab>ColorSpace>>LAB")
public class LabCommand<T extends RealType<T>> extends AbstractColorSpaceCommand<T> {

	@Override
	void IJ1ColorTransform(ImagePlus imgPlus) {
		IJ.run(imgPlus, "Color Transformer", "colour=Lab");
	}

}
