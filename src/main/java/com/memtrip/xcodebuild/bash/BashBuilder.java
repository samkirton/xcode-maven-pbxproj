package com.memtrip.xcodebuild.bash;

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
		
		commandList.add("-configuration");
		commandList.add("Release");
		commandList.add("CONFIGURATION_BUILD_DIR=/tmp/build");

		String[] commandArray = new String[commandList.size()];
		commandList.toArray(commandArray);
		
		return commandArray;
	}
}
