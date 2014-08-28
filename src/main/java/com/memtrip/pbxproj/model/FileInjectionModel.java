package com.memtrip.pbxproj.model;

import com.memtrip.pbxproj.utils.StringUtils;

/**
 * @author memtrip
 */
public class FileInjectionModel {
	private String filePath;
	private int fileType;
	private String buildRef;
	private String fileRef;
	
	public static final int TYPE_H = 0x1;
	public static final int TYPE_M = 0x2;
	
	public FileInjectionModel(String filePath, int fileType) {
		this.filePath = filePath;
		this.fileType = fileType;
		this.buildRef = StringUtils.getRandomHexString(24).toUpperCase();
		this.fileRef = StringUtils.getRandomHexString(24).toUpperCase();
	}
	
	public int getFileType() {
		return fileType;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public String getSimpleFileName() {
		String[] fileNameSplit = filePath.split("/");
		return fileNameSplit[fileNameSplit.length-1];
	}
	
	public String getBuildRef() {
		return buildRef;
	}
	
	public String getFileRef() {
		return fileRef;
	}
}
