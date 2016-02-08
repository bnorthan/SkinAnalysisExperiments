package com.truenorth.utils;

import java.io.File;
import java.util.ArrayList;

public class FileIOUtils {

	public static ArrayList<File> getFileListFromDirectory(String directory, String extension) {
		ArrayList<File> imageList=new ArrayList<File>();
		
		File folder = new File(directory);
		File[] fileList = folder.listFiles();

		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile()) {
				System.out.println("File " + fileList[i].getName());
				
				if (getExtension(fileList[i].getName()).equals(extension)) {
					imageList.add(fileList[i]);
				}
			} else if (fileList[i].isDirectory()) {
				System.out.println("Directory " + fileList[i].getName());
			}
		}

		return imageList;
	}
	
	public static String getExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    extension = fileName.substring(i+1);
		}
		
		return extension;
	}

}
