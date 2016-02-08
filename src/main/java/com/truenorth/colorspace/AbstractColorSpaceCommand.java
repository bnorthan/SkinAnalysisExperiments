package com.truenorth.colorspace;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.SubstackMaker;
import ij.process.ImageConverter;
import net.imglib2.type.numeric.RealType;

abstract public class AbstractColorSpaceCommand<T extends RealType<T>> implements Command {

	@Parameter
	private ImagePlus imgPlus;
	
	@Parameter(required=false)
	private boolean to8bit=false;
	
	@Parameter(required=false)
	private boolean show=false;
	
	@Parameter(type=ItemIO.OUTPUT)
	private ImagePlus c1;
	
	@Parameter(type=ItemIO.OUTPUT)
	private ImagePlus c2;
	
	@Parameter(type=ItemIO.OUTPUT)
	private ImagePlus c3;
	

	@Override
	public void run() {
		IJ1ColorTransform(imgPlus);
		
		ImagePlus cs = IJ.getImage();

		SubstackMaker substackMaker = new SubstackMaker();
		
		ImageConverter.setDoScaling(false);

		c1 = substackMaker.makeSubstack(cs, "1-1");
		if (show) c1.show();
		IJ.resetMinAndMax(c1);
		if (to8bit) IJ.run(c1, "8-bit", "");

		c2 = substackMaker.makeSubstack(cs, "2-2");
		if (show) c2.show();
		IJ.resetMinAndMax(c2);
		if (to8bit) IJ.run(c2, "8-bit", "");

		c3 = substackMaker.makeSubstack(cs, "3-3");
		if (show) c3.show();
		IJ.resetMinAndMax(c3);
		if (to8bit) IJ.run(c3, "8-bit", "");

		cs.changes = false;
		cs.close();
	}
	
	abstract void IJ1ColorTransform(ImagePlus imgPlus);
	
	public ImagePlus getC1() {
		return c1;
	}
	
	public ImagePlus getC2() {
		return c2;
	}
	
	public ImagePlus getC3() {
		return c3;
	}

}
