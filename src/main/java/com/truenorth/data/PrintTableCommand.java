
package com.truenorth.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, headless = true)
public class PrintTableCommand implements Command {

	@Parameter
	private LogService logger;

	@Parameter
	private Hashtable<String, ArrayList<Double>> table;

	/**
	 * Produces an output with the well-known "Hello, World!" message. The
	 */
	@Override
	public void run() {

		Enumeration<String> names = table.keys();

		ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();

		boolean end = false;

		ArrayList<String> header = new ArrayList<String>();

		while (names.hasMoreElements()) {
			String str = (String) names.nextElement();
			header.add(str);
		}

		int r = 0;
		while (end == false) {
			names = table.keys();

			ArrayList<Double> row = new ArrayList<Double>();
			while (names.hasMoreElements()) {
				String str = (String) names.nextElement();

				ArrayList<Double> list = table.get(str);
				row.add(list.get(r));

				if (r == list.size() - 1)
					end = true;
			}
			rows.add(row);
			r++;
		}

		System.out.println(header);

		for (ArrayList<Double> row : rows) {
			System.out.println(row);
		}

	}

}
