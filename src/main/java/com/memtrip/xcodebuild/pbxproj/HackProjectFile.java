package com.memtrip.xcodebuild.pbxproj;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * @author memtrip
 */
public class HackProjectFile {
	private static final String PBX_BUILD_FILE = "/* End PBXBuildFile section */";
	private static final String PBX_COPY_FILES = "name = \"Copy Files\";";
	private static final String PBX_FILE_REFERENCE = "/* End PBXFileReference section */";
	private static final String PBX_GROUP_CHILDREN = "path = %s;";
	private static final String PBX_SOURCES_BUILD_PHASE = "/* End PBXSourcesBuildPhase section */";
	
	public static String update(String projectName, String filePath, ArrayList<ExtraFileModel> extraFileModelList) throws IOException {
		StringBuilder sb = new StringBuilder();
		final String newLine = System.getProperty("line.separator");
		
		List<String> lines = Files.readAllLines(Paths.get(filePath),Charset.defaultCharset());
		int position = 0;
		for (String line : lines) {
		    if (line.contains(PBX_BUILD_FILE)) {
		    	String value = generatePBXBuildFileLine(extraFileModelList);
		    	sb.insert(position, value);
		    	position += value.length();
		    } else if (line.contains(PBX_COPY_FILES)) {
		    	int positionOffset = 6;
		    	String value = generatePBXCopyFiles(extraFileModelList);
		    	sb.insert(position-positionOffset, value);
		    	position += value.length();
		    } else if (line.contains(PBX_FILE_REFERENCE)) {
		    	String value = generatePBXBuildFileReference(extraFileModelList);
		    	sb.insert(position, value);
		    	position += value.length();
		    } else if (line.contains(String.format(PBX_GROUP_CHILDREN, projectName))) {
		    	int positionOffset = 7;
		    	String value = generatePBXGroupChildren(extraFileModelList);
		    	sb.insert(position-positionOffset, generatePBXGroupChildren(extraFileModelList));
		    	position += value.length();
		    } else if (line.contains(PBX_SOURCES_BUILD_PHASE)) {
		    	int positionOffset = 55;
		    	String value = generatePBXSourcesBuildPhase(extraFileModelList);
		    	sb.insert(position-positionOffset, generatePBXSourcesBuildPhase(extraFileModelList));
		    	position += value.length();
		    }
		    
		    sb.append(line).append(newLine);
		    position+=line.length();
		    position+=1; // factor in new lines
		}
	
		return sb.toString();
	}
	
	/**
	 * Build a list of PBXBuildFileLine
	 * @param	extraFileModelList	The extraFileModelList to build the PBXBuildFileLine for
	 * @return	The PBXBuildFileLine file references
	 */
	private static String generatePBXBuildFileLine(ArrayList<ExtraFileModel> extraFileModelList) {
		String value = "\n";
		for (ExtraFileModel extraFileModel : extraFileModelList) {
			if (extraFileModel.getFileType() == ExtraFileModel.TYPE_H) {
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
	private static String generatePBXCopyFiles(ArrayList<ExtraFileModel> extraFileModelList) {
		String value = "";
		for (ExtraFileModel extraFileModel : extraFileModelList) {
			if (extraFileModel.getFileType() == ExtraFileModel.TYPE_H) {
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
	private static String generatePBXBuildFileReference(ArrayList<ExtraFileModel> extraFileModelList) {
		String value = "";
		for (ExtraFileModel extraFileModel : extraFileModelList) {
			if (extraFileModel.getFileType() == ExtraFileModel.TYPE_H) {
				value += "\t\t" + extraFileModel.getFileRef() + " /* " + extraFileModel.getSimpleFileName() + " */ = {isa = PBXFileReference; fileEncoding = 4; lastKnownFileType = sourcecode.c.h; path = generated/ios/h/" + extraFileModel.getSimpleFileName() + "; sourceTree = \"<group>\"; };\n";
			} else {
				value += "\t\t" + extraFileModel.getFileRef() + " /* " + extraFileModel.getSimpleFileName() + " */ = {isa = PBXFileReference; fileEncoding = 4; lastKnownFileType = sourcecode.c.objc; path = generated/ios/m/" + extraFileModel.getSimpleFileName() + "; sourceTree = \"<group>\"; };\n";
			}
		}
		return value;
	}
	
	/**
	 * Build a list of PBXGroupChildren
	 * @param	extraFileModelList	The extraFileModelList to build the PBXGroupChildren for
	 * @return	The PBXGroupChildren file references
	 */
	private static String generatePBXGroupChildren(ArrayList<ExtraFileModel> extraFileModelList) {
		String value = "\n";
		for (ExtraFileModel extraFileModel : extraFileModelList) {
			value += "\t\t\t\t" + extraFileModel.getFileRef() + " /* " + extraFileModel.getSimpleFileName() + " */,\n";
		}
		return value;
	}
	
	/**
	 * Build a list of PBXSources
	 * @param	extraFileModelList	The extraFileModelList to build the PBXSources for
	 * @return	The PBXSources file references
	 */
	private static String generatePBXSourcesBuildPhase(ArrayList<ExtraFileModel> extraFileModelList) {
		String value = "\n";
		for (ExtraFileModel extraFileModel : extraFileModelList) {
			if (extraFileModel.getFileType() == ExtraFileModel.TYPE_M) {
				value += "\t\t\t\t" + extraFileModel.getBuildRef() + " /* " + extraFileModel.getSimpleFileName() + " in Sources */,\n";
			}
		}
		return value;
	}
}
