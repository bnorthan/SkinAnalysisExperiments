package com.truenorth;

import java.util.ArrayList;
import java.util.Hashtable;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.process.ImageStatistics;

@Plugin(type = Command.class, headless = true)
public class MeasureCommand implements Command {

	@Parameter
	private ImagePlus imp;

	@Parameter
	private Roi[] rois;

	@Parameter(type = ItemIO.BOTH, required = false)
	private Hashtable<String, ArrayList<Double>> table;

	/**
	 * Produces an output with the well-known "Hello, World!" message. The
	 */
	@Override
	public void run() {

		ArrayList<Double> areas = new ArrayList<Double>();
		ArrayList<Double> intensities = new ArrayList<Double>();

		ArrayList<Double> xC = new ArrayList<Double>();
		ArrayList<Double> yC = new ArrayList<Double>();

		ArrayList<Double> dEdge = new ArrayList<Double>();

		double w = imp.getWidth();
		double h = imp.getHeight();

		for (Roi roi : rois) {
			imp.setRoi(roi);
			ImageStatistics stats = imp.getStatistics(Measurements.CENTROID);

			areas.add(stats.area);
			intensities.add(stats.mean);
			xC.add(stats.xCentroid);
			yC.add(stats.yCentroid);

			double xEdge = Math.min(stats.xCentroid, w - stats.xCentroid);
			double yEdge = Math.min(stats.yCentroid, h - stats.yCentroid);

			double edge = Math.min(xEdge, yEdge);

			dEdge.add(edge);

		}

		if (table == null) {
			table = new Hashtable<String, ArrayList<Double>>();
		}

		table.put("area2", areas);
		table.put("intensity", intensities);
		table.put("d-edge", dEdge);

	}

}