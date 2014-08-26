package com.memtrip.xcodebuild;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.memtrip.xcodebuild.pbxproj.ExtraFileModel;
import com.memtrip.xcodebuild.pbxproj.HackProjectFile;
import com.memtrip.xcodebuild.utils.FileUtils;

/**
 * @author memtrip
 */
public class EntryPoint extends AbstractMojo {
	
	/**
	 * The xcode project name
	 * @parameter
	 */
	private String projectName;
	
	/**
	 * The location of the .pbxproj file that will be changed to support the extra m / h files
	 * @parameter
	 */
	private String pbxprojDir;
	
	/**
	 * Extra .m sources that should be added to the .pbxproj
	 * @parameter
	 */
	private String extraMSourcesDir;
	
	/**
	 * Extra .h sources that should be added to the .pbxproj
	 * @parameter
	 */
	private String extraHSourcesDir;
	
	public void setProjectName(String newVal) {
		projectName = newVal;
	}
	
	public void setPbxprojDir(String newVal) {
		pbxprojDir = newVal;
	}
	
	public void setExtraMSourcesDir(String newVal) {
		extraMSourcesDir = newVal;
	}
	
	public void setExtraHSourcesDir(String newVal) {
		extraHSourcesDir = newVal;
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (projectName == null)
			throw new IllegalStateException("<projectName></projectName> configuration could not be found");
		
		if (pbxprojDir != null && extraMSourcesDir != null && extraHSourcesDir != null) {
			File pbxProjFile = new File(pbxprojDir);
			ArrayList<ExtraFileModel> extraFileModelList = buildExtraFileModelList();
			
			if (!pbxProjFile.exists())
				throw new IllegalStateException("pbxProjFile could not be found");
			
			// modify the pbxproj file with the extra M / H sources
			String pbxprojSource = null;
			try {
				pbxprojSource = HackProjectFile.update(projectName, pbxprojDir, extraFileModelList);
			} catch (IOException e) { 
				throw new IllegalStateException(e.getMessage());
			}
			
			// TODO: replace the original pbxprojectSource with a new file
			FileUtils.persistFileOuput(
				pbxprojSource, 
				"/tmp/", 
				pbxProjFile.getName()
			);
		}
		
		// TODO: run the xcodebuild
	}
	
	/**
	 * @return	A list of ExteaFileModel objects
	 */
	public ArrayList<ExtraFileModel> buildExtraFileModelList() {
		ArrayList<String> extraMSources = new ArrayList<String>();
		ArrayList<String> extraHSources = new ArrayList<String>();
		
		FileUtils.getFilePaths(new File(extraMSourcesDir), ".m", extraMSources);
		FileUtils.getFilePaths(new File(extraHSourcesDir), ".h", extraHSources);
		ArrayList<ExtraFileModel> extraFileModelList = FileUtils.generateHackProjectFileList(extraMSources, ExtraFileModel.TYPE_M);
		extraFileModelList.addAll(FileUtils.generateHackProjectFileList(extraHSources, ExtraFileModel.TYPE_H));
		return extraFileModelList;
	}
}	
