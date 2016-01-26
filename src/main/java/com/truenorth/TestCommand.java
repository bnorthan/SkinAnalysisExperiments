package com.truenorth;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImageJ;


@Plugin(type = Command.class, headless = true, menuPath = "Help>Hello, World!")
public class TestCommand implements Command {

	@Parameter(label = "What is your name?")
	private String name = "J. Doe";

	@Parameter(type = ItemIO.OUTPUT)
	private String greeting;

	/**
	 * Produces an output with the well-known "Hello, World!" message. The
	 */
	@Override
	public void run() {
		greeting = "Hello, " + name + "!";
	}

	/**
	 * @param args unused
	 */
	public static void main(final String... args) {
		// Launch ImageJ as usual.
		final ImageJ ij = net.imagej.Main.launch(args);

		// Launch our "Hello World" command right away.
		ij.command().run(TestCommand.class, true,"name", "Biff");
	}

}
