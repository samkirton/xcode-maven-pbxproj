package com.memtrip.xcodebuild.bash;

import java.io.File;
import java.util.ArrayList;

/**
 * @author memtrip
 */
public class BashBuilder {

	/**
	 * @return	Build the xcodebuild 
	 */
	public static final String[] xcodebuild(String xcodebuildExec, String scheme) {
		ArrayList<String> commandList = new ArrayList<String>();
		commandList.add(xcodebuildExec);
		
		if (scheme != null) {
			commandList.add("-scheme");
			commandList.add(scheme);
		}

		String[] commandArray = new String[commandList.size()];
		commandList.toArray(commandArray);
		
		return commandArray;
	}
	
	/**
	 * 
	 * @param buildProductDir
	 * @param projectBuildDir
	 * @return
	 */
	public static final ProcessBuilder copyArtefact(String buildProductDir, String projectBuildDir, String projectDir) {
		ProcessBuilder processBuilder = new ProcessBuilder(
			"cp",
			"-R",
			buildProductDir,
			projectBuildDir + "/ios"
		);
		processBuilder.directory(new File(projectDir));
		processBuilder.redirectErrorStream(true);
		return processBuilder;
	}
}
