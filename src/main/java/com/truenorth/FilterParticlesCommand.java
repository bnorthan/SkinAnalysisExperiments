package com.truenorth;

import java.util.ArrayList;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.process.ImageStatistics;

@Plugin(type = Command.class, headless = true)
public class FilterParticlesCommand implements Command {

	@Parameter
	private ImagePlus imp;

	@Parameter
	private Roi[] rois;
	
	@Parameter(required=false)
	private int edgeThresh=50;

	@Parameter(type = ItemIO.OUTPUT)
	private ArrayList<Roi> filteredRois;

	@Override
	public void run() {

		double w = imp.getWidth();
		double h = imp.getHeight();

		filteredRois = new ArrayList<Roi>();

		for (Roi roi : rois) {
			imp.setRoi(roi);
			ImageStatistics stats = imp.getStatistics(Measurements.CENTROID);

			double xEdge = Math.min(stats.xCentroid, w - stats.xCentroid);
			double yEdge = Math.min(stats.yCentroid, h - stats.yCentroid);
			double edge = Math.min(xEdge, yEdge);

			if (edge > edgeThresh) {
				filteredRois.add(roi);
			}
		}

	}

}