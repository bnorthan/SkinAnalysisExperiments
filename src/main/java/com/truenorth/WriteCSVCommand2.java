
package com.truenorth;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, headless = true)
public class WriteCSVCommand2 implements Command {

	@Parameter
	private String fileName;

	@Parameter
	private ArrayList<Object> data;

	@Override
	public void run() {
		FileWriter fileWriter = null;

		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");

		CSVPrinter csvFilePrinter = null;

		try {

			fileWriter = new FileWriter(fileName, true);
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

			csvFilePrinter.printRecord(data);

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
