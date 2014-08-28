package com.memtrip.pbxproj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.memtrip.pbxproj.hack.FileInjector;
import com.memtrip.pbxproj.model.FileInjectionModel;
import com.memtrip.pbxproj.utils.FileUtils;

/**
 * @goal generate
 * @requiresProject false
 */
public class EntryPoint extends AbstractMojo {
	
	/**
	 * The xcode project name
	 * @parameter
	 */
	private String projectNameParam;
	
	/**
	 * The dir of the extra header files that should be added to the xcode project, the path should 
	 * be a local to the project source folder.
	 * @parameter
	 */
	private String extraHeaderFilesDirParam;
	
	/**
	 * The dir of the extra class files that should be added to the xcode project, the path should 
	 * be a local to the project source folder.
	 * @parameter
	 */
	private String extraClassFilesDirParam;
	
	/**
	 * The location of the xcodebuild executable
	 * @parameter
	 */
	private String xcodebuildExecParam;
	
	/**
	 * The location of the project file
	 * @parameter
	 */
	private String projectDirParam;
	
	/**
	 * The default location of xcodebuild on mac
	 */
	private static final String DEFAULT_XCODEBUILD_EXEC = "xcodebuild";
	
	/**
	 * projectName	
	 */
	public void setProjectName(String newVal) {
		projectNameParam = newVal;
	}
	
	/**
	 * extraHeaderFilesDir
	 */
	public void setExtraHeaderFilesDir(String newVal) {
		extraHeaderFilesDirParam = newVal;
	}
	
	/**
	 * extraClassFilesDir
	 */
	public void setExtraClassFilesDir(String newVal) {
		extraClassFilesDirParam = newVal;
	}
	
	/**
	 * pbxprojDir
	 */
	public void setPbxprojDir(String newVal) {
		projectDirParam = newVal;
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (projectNameParam == null)
			throw new IllegalStateException("<projectName> parameter could not be found");
		
		if (xcodebuildExecParam == null)
			xcodebuildExecParam = DEFAULT_XCODEBUILD_EXEC;
		
		if (projectDirParam != null && extraHeaderFilesDirParam != null && extraClassFilesDirParam != null) {
			String pbxProjFileLocation = projectDirParam + "/" + projectNameParam + ".xcodeproj/project.pbxproj";
			File pbxProjFile = new File(pbxProjFileLocation);
			ArrayList<FileInjectionModel> extraFileModelList = buildExtraFileModelList(projectDirParam,projectNameParam);
			
			if (!pbxProjFile.exists())
				throw new IllegalStateException("pbxProjFile could not be found");
			
			// modify the pbxproj file with the extra M / H sources
			String pbxprojSource = null;
			try {
				pbxprojSource = FileInjector.update(projectNameParam, pbxProjFileLocation, extraHeaderFilesDirParam, extraClassFilesDirParam, extraFileModelList);
			} catch (IOException e) { 
				throw new IllegalStateException(e.getMessage());
			}
			
			FileUtils.persistFileOuput(
				pbxprojSource, 
				pbxProjFileLocation
			);
		}
	}
	
	/**
	 * @return	A list of ExteaFileModel objects
	 */
	public ArrayList<FileInjectionModel> buildExtraFileModelList(String projectDir, String projectName) {
		ArrayList<String> extraMSources = new ArrayList<String>();
		ArrayList<String> extraHSources = new ArrayList<String>();
		
		String projectSourceDir = projectDir + "/" + projectName + "/";
		
		FileUtils.getFilePaths(new File(projectSourceDir + extraClassFilesDirParam), ".m", extraMSources);
		FileUtils.getFilePaths(new File(projectSourceDir + extraHeaderFilesDirParam), ".h", extraHSources);
		ArrayList<FileInjectionModel> fileInjectionModelList = FileUtils.generateHackProjectFileList(extraMSources, FileInjectionModel.TYPE_M);
		fileInjectionModelList.addAll(FileUtils.generateHackProjectFileList(extraHSources, FileInjectionModel.TYPE_H));
		return fileInjectionModelList;
	}
}	