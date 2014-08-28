package com.memtrip.xcodebuild.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.memtrip.xcodebuild.pbxproj.ExtraFileModel;

/**
 * @author memtrip
 */
public class FileUtils {
	public static final String PBXPROJ_EXT = ".pbxproj";
	private static final String EMPTY = "";
	private static final String DOT = ".";
	
	public static ArrayList<String> findSourcesFromDir(String dir) {
		return null;
	}
	
	/**
	 * Build a list of paths associated with the extension and parent path provided
	 * @param	parentPath	The path that contains the file
	 * @param	extension	The file extensions
	 * @param	OUT: A reference of a list of paths
	 */
	public static void getFilePaths(File parent, String extention, ArrayList<String> paths) {
		if (parent.isDirectory()) {
			File[] children = parent.listFiles();
		
			for (File child : children) {
				getFilePaths(child,extention,paths);
			}
		} else {
			if (getFileExtension(parent).equals(extention)) {
				paths.add(parent.getAbsolutePath());
			}
		}
	}
	
	/**
	 * Build a list of ExtraFileModel based on the provided projectFileList
	 * @param	projectFileList	The file list to build the ExtraFileModel from
	 * @return	A list of ExtraFileModel built based on the provided projectFileList
	 */
	public static ArrayList<ExtraFileModel> generateHackProjectFileList(ArrayList<String> projectFileList, int type) {
		ArrayList<ExtraFileModel> extraFileModelList = new ArrayList<ExtraFileModel>();
		for (String filePath : projectFileList)
			extraFileModelList.add(new ExtraFileModel(filePath, type));
		return extraFileModelList;
	}
	
	/**
	 * Get the file extension for the provided file
	 * @param	file	The file to get the ext for
	 * @return	The extension of the provided file
	 */
	private static String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(DOT);
		if (lastIndexOf == -1) {
		    return EMPTY;
		}
		
		return name.substring(lastIndexOf);
	}
	
	/**
	 * Persist the file output string to a file
	 * @param	fileOutput	The file output string that is being saved to a file
	 * @param	filePath	The path where the final file will reside
	 */
	public static void persistFileOuput(String fileOutput, String filePath) {		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
			out.write(fileOutput);
			out.close();
		} catch (IOException e) {
			System.out.println("> FAILED to write file to" + filePath);		
			System.exit(0);
		}
		
		System.out.println("> file has been written to: " + filePath);
	}
}
