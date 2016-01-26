package com.truenorth;

import java.util.ArrayList;
import java.util.Hashtable;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Skin Analysis>CountParticles")
public class CountParticles implements Command {

	@Parameter
	LogService logger;

	@Parameter
	private ImagePlus imgPlus;

	@Parameter
	int minSize;

	@Parameter
	int maxSize;

	@Parameter
	int minCircularity;

	@Parameter
	int maxCircularity;

	@Parameter(required = false)
	RoiManager roim = null;

	@Parameter(type = ItemIO.BOTH, required = false)
	Hashtable<String, ArrayList<Double>> table = null;

	public void run() {

		ResultsTable rt = new ResultsTable();

		if (table == null) {
			table = new Hashtable<String, ArrayList<Double>>();
		}

		// Create the particle analyzer
		ParticleAnalyzer pa = new ParticleAnalyzer(
				ParticleAnalyzer.ADD_TO_MANAGER /*| ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES*/,
				Measurements.AREA | Measurements.MEAN | Measurements.ELLIPSE | Measurements.CIRCULARITY, rt, minSize,
				maxSize, minCircularity, maxCircularity);

		pa.setRoiManager(roim);

		pa.setHideOutputImage(true);

		if (pa.analyze(imgPlus)) {
			System.out.println("All ok");
		} else {
			System.out.println("There was a problem in analyzing");
		}
		
		ArrayList<Double> areas = new ArrayList<Double>();
		ArrayList<Double> circles = new ArrayList<Double>();
		
		// transfer stats from results table to hash table
		for (int row = 0; row < rt.getCounter(); row++) {
			
			areas.add(rt.getValueAsDouble(ResultsTable.AREA, row));
			circles.add(rt.getValueAsDouble(ResultsTable.CIRCULARITY, row));
		
		}
		
		table.put("area", areas);
		table.put("circularity", circles);

	}

}
