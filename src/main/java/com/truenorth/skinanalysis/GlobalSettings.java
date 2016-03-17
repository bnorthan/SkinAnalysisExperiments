package com.truenorth.skinanalysis;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GlobalSettings {

	// private static String
	// homeDir="/home/bnorthan/images/evalulab/Analysis_03_04_2016/";
	private static String homeDir = "C:\\Brian2016\\Images\\Evalulab\\ImagesJan2016\\Analysis_03_05_2016\\";

	public static String getHomeDir() {

		Path homePath = Paths.get(homeDir);

		if (Files.notExists(homePath))
			try {
				Files.createDirectory(homePath);
			} catch (Exception e) {
				return null;
			}
		return homeDir;
	}

}
