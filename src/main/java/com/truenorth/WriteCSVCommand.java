
package com.truenorth;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, headless = true)
public class WriteCSVCommand implements Command {

	@Parameter
	private String fileName;

	@Parameter
	private Hashtable<String, ArrayList<Double>> table;

	@Override
	public void run() {
		FileWriter fileWriter = null;

		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");

		CSVPrinter csvFilePrinter = null;

		try {
			fileWriter = new FileWriter(fileName);

			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

			Enumeration names = table.keys();

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

			csvFilePrinter.printRecord(header);

			for (ArrayList<Double> row : rows) {
				csvFilePrinter.printRecord(row);
			}

		} catch (Exception e) {

		} finally {
			try {
				csvFilePrinter.close();
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {

			}
		}

	}

}
