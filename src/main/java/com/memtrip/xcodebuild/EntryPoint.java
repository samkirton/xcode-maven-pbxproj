package com.memtrip.xcodebuild;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.memtrip.xcodebuild.bash.BashBuilder;
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
	 * An optional scheme that the xcodebuild should target
	 * @parameter
	 */
	private String schemeParam;
	
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
	
	/**
	 * scheme
	 */
	public void setScheme(String newVal) {
		schemeParam = newVal;
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (projectNameParam == null)
			throw new IllegalStateException("<projectName> parameter could not be found");
		
		if (xcodebuildExecParam == null)
			xcodebuildExecParam = DEFAULT_XCODEBUILD_EXEC;
		
		if (projectDirParam != null && extraHeaderFilesDirParam != null && extraClassFilesDirParam != null) {
			//proffer_dto_customer.xcodeproj/project.pbxproj
			String pbxProjFileLocation = projectDirParam + "/" + projectNameParam + ".xcodeproj/project.pbxproj";
			File pbxProjFile = new File(pbxProjFileLocation);
			ArrayList<ExtraFileModel> extraFileModelList = buildExtraFileModelList(projectDirParam,projectNameParam);
			
			if (!pbxProjFile.exists())
				throw new IllegalStateException("pbxProjFile could not be found");
			
			// modify the pbxproj file with the extra M / H sources
			String pbxprojSource = null;
			try {
				pbxprojSource = HackProjectFile.update(projectNameParam, pbxProjFileLocation, extraHeaderFilesDirParam, extraClassFilesDirParam, extraFileModelList);
			} catch (IOException e) { 
				throw new IllegalStateException(e.getMessage());
			}
			
			// TODO: replace the original pbxprojectSource with a new file
			FileUtils.persistFileOuput(
				pbxprojSource, 
				pbxProjFileLocation,
				pbxProjFile.getName()
			);
		}
		
		// create the xcodebuild command
		String[] command = BashBuilder.xcodebuild(
			xcodebuildExecParam, 
			schemeParam
		);
		
		try {
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.directory(new File(projectDirParam));
			builder.redirectErrorStream(true);
			Process process =  builder.start();
			
			Scanner scanner = new Scanner(process.getInputStream());
			StringBuilder text = new StringBuilder();
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine());
				text.append("\n");
			}
			scanner.close();

			int result = process.waitFor();
			
			System.out.println(text.toString());
			if (result != 0)
				throw new MojoExecutionException("xcodebuild FAILED");
		} catch (IOException | InterruptedException e) { 
			throw new MojoExecutionException("xcodebuild FAILED with... \n" + e.getMessage());
		}
	}
	
	/**
	 * @return	A list of ExteaFileModel objects
	 */
	public ArrayList<ExtraFileModel> buildExtraFileModelList(String projectDir, String projectName) {
		ArrayList<String> extraMSources = new ArrayList<String>();
		ArrayList<String> extraHSources = new ArrayList<String>();
		
		String projectSourceDir = projectDir + "/" + projectName + "/";
		
		FileUtils.getFilePaths(new File(projectSourceDir + extraClassFilesDirParam), ".m", extraMSources);
		FileUtils.getFilePaths(new File(projectSourceDir + extraHeaderFilesDirParam), ".h", extraHSources);
		ArrayList<ExtraFileModel> extraFileModelList = FileUtils.generateHackProjectFileList(extraMSources, ExtraFileModel.TYPE_M);
		extraFileModelList.addAll(FileUtils.generateHackProjectFileList(extraHSources, ExtraFileModel.TYPE_H));
		return extraFileModelList;
	}
}	