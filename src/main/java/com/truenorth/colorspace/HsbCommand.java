package com.truenorth.colorspace;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Evalulab>ColorSpace>HSB")
public class HsbCommand<T extends RealType<T>> extends AbstractColorSpaceCommand<T> {

	@Override
	public void IJ1ColorTransform(ImagePlus imgPlus) {

		IJ.run(imgPlus, "HSB Stack", "");

	}

}
