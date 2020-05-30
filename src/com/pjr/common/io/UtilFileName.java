
package com.pjr.common.io;

/**
 * UtilFileName(...)
 * This class assumes that the string used to initialize fullPath has a
 * directory path, filename, and extension. The methods won't work if it
 * doesn't.
 */
public class UtilFileName {
	  private String fullPath;
	  private String pathSeparator;
	  private String extensionSeparator;

	/**
	 * ::Constructor
	 */
	public UtilFileName(String str, String sep, String ext) {
		fullPath = str;
		pathSeparator = sep;
		extensionSeparator = ext;
	}

	public String getExtension() {
		int dot = fullPath.lastIndexOf(extensionSeparator);
		return fullPath.substring(dot + 1);
	}
	
	public String getFilename() { // gets filename without extension
		int dot = fullPath.lastIndexOf(extensionSeparator);
		int sep = fullPath.lastIndexOf(pathSeparator);
		return fullPath.substring(sep + 1, dot);
	}
	
	public String getPath() {
		int sep = fullPath.lastIndexOf(pathSeparator);
		return fullPath.substring(0, sep);
	}

}
