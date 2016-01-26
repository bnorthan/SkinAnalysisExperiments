package com.truenorth;

import java.awt.Color;
import java.util.List;

import ij.ImagePlus;
import ij.gui.Roi;

public class ROIUtils {

	public static void drawParticleOnImage(ImagePlus imp, Roi roi, Color color) {
		imp.getProcessor().setColor(color);
		imp.getProcessor().draw(roi);
	}

	public static void drawParticlesOnImage(ImagePlus imp, List<Roi> rois, Color color) {
		for (Roi roi : rois) {
			drawParticleOnImage(imp, roi, color);
		}
	}

	public static void addOffsets(Roi[] rois, long x, long y) {
		// for each roi add the offset

		for (Roi roi : rois) {
			roi.setLocation(roi.getXBase() + x, roi.getYBase() + y);
		}
	}

}
