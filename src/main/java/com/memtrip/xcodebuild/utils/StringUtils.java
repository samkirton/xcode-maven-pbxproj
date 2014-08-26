package com.memtrip.xcodebuild.utils;

import java.util.Random;

/**
 * @author memtrip
 */
public class StringUtils {
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
}
