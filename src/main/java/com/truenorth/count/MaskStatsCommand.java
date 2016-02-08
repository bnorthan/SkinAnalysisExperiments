package com.truenorth.count;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class MaskStatsCommand<A extends RealType<A>, B extends RealType<B>> implements Command {

	@Parameter
	Img<A> mask;

	@Parameter
	Img<B> intensity;

	@Parameter(type = ItemIO.OUTPUT)
	double percent;

	@Parameter(type = ItemIO.OUTPUT)
	double insideIntensity;

	@Parameter(type = ItemIO.OUTPUT)
	double outsideIntensity;

	public void run() {
		long numInside = 0;
		long numOutside = 0;
		long total = 0;

		insideIntensity = 0.0;
		outsideIntensity = 0.0;

		Cursor<A> cM = mask.cursor();
		Cursor<B> cI = intensity.cursor();

		while (cM.hasNext()) {
			cM.fwd();
			cI.fwd();

			total++;
			if (cM.get().getRealFloat() == 255.0f) {
				numInside++;
				insideIntensity += cI.get().getRealDouble();
			} else {
				numOutside++;
				outsideIntensity += cI.get().getRealDouble();
			}
		}

		insideIntensity = insideIntensity / numInside;
		outsideIntensity = outsideIntensity / numOutside;
		percent = (double) numInside / (double) total;
	}

}
