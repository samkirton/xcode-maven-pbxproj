package com.memtrip.xcodebuild.utils;

import java.util.Random;

/**
 * @author memtrip
 */
public class StringUtils {
	private static String UNIVERSAL_BUILD_TARGET = "Release-iphoneuniversal";
	
	/**
	 * Build a hex string at the specified length
	 * @param	length	Number of hex characters
	 * @return	A hex string at the specified length
	 */
    public static String getRandomHexString(int length) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < length) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, length);
    }
    
    public static String resolveSymlinkPath(String line) {
		String[] pathSplit = line.split("-resolve-src-symlinks");
		String copy = pathSplit[pathSplit.length-1];
		String[] copySplit = copy.split(" ");
		String path = copySplit[copySplit.length-1];
		String[] artefactDir = path.split(UNIVERSAL_BUILD_TARGET);
		return artefactDir[0] + UNIVERSAL_BUILD_TARGET;
    }
}
