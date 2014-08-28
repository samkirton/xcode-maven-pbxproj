package com.memtrip.pbxproj.hack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.memtrip.pbxproj.model.FileInjectionModel;


/**
 * @author memtrip
 */
public class FileInjector {
	private static final String PBX_BUILD_FILE = "/* End PBXBuildFile section */";
	private static final String PBX_COPY_FILES = "name = \"Copy Files\";";
	private static final String PBX_FILE_REFERENCE = "/* End PBXFileReference section */";
	private static final String PBX_GROUP_CHILDREN = "path = %s;";
	private static final String PBX_SOURCES_BUILD_PHASE = "/* End PBXSourcesBuildPhase section */";
	
	public static String update(String projectName, 
			String filePath, 
			String headerPath,
			String classPath,
			ArrayList<FileInjectionModel> fileInjectionModelList) throws IOException {
		StringBuilder sb = new StringBuilder();
		final String newLine = System.getProperty("line.separator");
		
		int position = 0;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
		    if (line.contains(PBX_BUILD_FILE)) {
		    	String value = generatePBXBuildFileLine(fileInjectionModelList);
		    	sb.insert(position, value);
		    	position += value.length();
		    } else if (line.contains(PBX_COPY_FILES)) {
		    	int positionOffset = 6;
		    	String value = generatePBXCopyFiles(fileInjectionModelList);
		    	sb.insert(position-positionOffset, value);
		    	position += value.length();
		    } else if (line.contains(PBX_FILE_REFERENCE)) {
		    	String value = generatePBXBuildFileReference(fileInjectionModelList, headerPath, classPath);
		    	sb.insert(position, value);
		    	position += value.length();
		    } else if (line.contains(String.format(PBX_GROUP_CHILDREN, projectName))) {
		    	int positionOffset = 7;
		    	String value = generatePBXGroupChildren(fileInjectionModelList);
		    	sb.insert(position-positionOffset, generatePBXGroupChildren(fileInjectionModelList));
		    	position += value.length();
		    } else if (line.contains(PBX_SOURCES_BUILD_PHASE)) {
		    	int positionOffset = 55;
		    	String value = generatePBXSourcesBuildPhase(fileInjectionModelList);
		    	sb.insert(position-positionOffset, generatePBXSourcesBuildPhase(fileInjectionModelList));
		    	position += value.length();
		    }
		    
		    sb.append(line).append(newLine);
		    position+=line.length();
		    position+=1; // include new lines (\n)
		}
		
		bufferedReader.close();
		
		return sb.toString();
	}
	
	/**
	 * Build a list of PBXBuildFileLine
	 * @param	extraFileModelList	The extraFileModelList to build the PBXBuildFileLine for
	 * @return	The PBXBuildFileLine file references
	 */
	private static String generatePBXBuildFileLine(ArrayList<FileInjectionModel> extraFileModelList) {
		String value = "\n";
		
		for (FileInjectionModel extraFileModel : extraFileModelList) {
			if (extraFileModel.getFileType() == FileInjectionModel.TYPE_H) {
				value += "\t\t" + extraFileModel.getBuildRef() + " /* " + extraFileModel.getSimpleFileName() + " in Copy Files */ = {isa = PBXBuildFile; fileRef = " + extraFileModel.getFileRef() + " /* " + extraFileModel.getSimpleFileName() + " */;};\n";
			} else {
				value += "\t\t" + extraFileModel.getBuildRef() + " /* " + extraFileModel.getSimpleFileName() + " in Sources */ = {isa = PBXBuildFile; fileRef = " + extraFileModel.getFileRef() + " /* " + extraFileModel.getSimpleFileName() + " */; };\n";
			}
		}
		
		return value;
	}
	
	/**
	 * Build a list of PBXCopyFiles
	 * @param	extraFileModelList	The extraFileModelList to build the PBXCopyFiles for
	 * @return	The PBXCopyFiles file references
	 */
	private static String generatePBXCopyFiles(ArrayList<FileInjectionModel> extraFileModelList) {
		String value = "";
		
		for (FileInjectionModel extraFileModel : extraFileModelList) {
			if (extraFileModel.getFileType() == FileInjectionModel.TYPE_H) {
				value += "\t\t\t\t" + extraFileModel.getBuildRef() + " /* " + extraFileModel.getSimpleFileName() + " in Copy Files */,\n";
			}
		}
		return value;
	}
	
	/**
	 * Build a list of PBXBuildFileReference
	 * @param	extraFileModelList	The extraFileModelList to build the PBXBuildFileReference for
	 * @return	The PBXBuildFileReference file references
	 */
	private static String generatePBXBuildFileReference(ArrayList<FileInjectionModel> extraFileModelList, String headerPath, String classPath) {
		String value = "";
		
		for (FileInjectionModel extraFileModel : extraFileModelList) {
			if (extraFileModel.getFileType() == FileInjectionModel.TYPE_H) {
				value += "\t\t" + extraFileModel.getFileRef() + " /* " + extraFileModel.getSimpleFileName() + " */ = {isa = PBXFileReference; fileEncoding = 4; lastKnownFileType = sourcecode.c.h; path = " + headerPath + extraFileModel.getSimpleFileName() + "; sourceTree = \"<group>\"; };\n";
			} else {
				value += "\t\t" + extraFileModel.getFileRef() + " /* " + extraFileModel.getSimpleFileName() + " */ = {isa = PBXFileReference; fileEncoding = 4; lastKnownFileType = sourcecode.c.objc; path = " + classPath + extraFileModel.getSimpleFileName() + "; sourceTree = \"<group>\"; };\n";
			}
		}
		
		return value;
	}
	
	/**
	 * Build a list of PBXGroupChildren
	 * @param	extraFileModelList	The extraFileModelList to build the PBXGroupChildren for
	 * @return	The PBXGroupChildren file references
	 */
	private static String generatePBXGroupChildren(ArrayList<FileInjectionModel> extraFileModelList) {
		String value = "\n";
		
		for (FileInjectionModel extraFileModel : extraFileModelList) {
			value += "\t\t\t\t" + extraFileModel.getFileRef() + " /* " + extraFileModel.getSimpleFileName() + " */,\n";
		}
		
		return value;
	}
	
	/**
	 * Build a list of PBXSources
	 * @param	extraFileModelList	The extraFileModelList to build the PBXSources for
	 * @return	The PBXSources file references
	 */
	private static String generatePBXSourcesBuildPhase(ArrayList<FileInjectionModel> extraFileModelList) {
		String value = "\n";
		
		for (FileInjectionModel extraFileModel : extraFileModelList) {
			if (extraFileModel.getFileType() == FileInjectionModel.TYPE_M) {
				value += "\t\t\t\t" + extraFileModel.getBuildRef() + " /* " + extraFileModel.getSimpleFileName() + " in Sources */,\n";
			}
		}
		
		return value;
	}
}
