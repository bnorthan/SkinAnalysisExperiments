package com.truenorth;


import ij.gui.Roi;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.Img;

public class Utils {

	static public Interval RoiToInterval(Roi roi, Img<?> img) {

		int n = img.numDimensions();

		long start[] = new long[n];
		long end[] = new long[n];

		start[0] = (long) roi.getBounds().getX();
		start[1] = (long) roi.getBounds().getY();

		end[0] = start[0] + (long) roi.getBounds().getWidth() - 1;
		end[1] = start[1] + (long) roi.getBounds().getHeight() - 1;

		for (int d = 2; d < n; d++) {
			start[d] = 0;
			end[d] = img.dimension(d) - 1;
		}

		return new FinalInterval(start, end);

	}

}
