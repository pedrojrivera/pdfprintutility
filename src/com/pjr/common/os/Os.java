package com.pjr.common.os;

public class Os {

	public Os() {}
	
	public static String getOsName() {
		return System.getProperty("os.name", "unknown");
	}
	
	public static String getOsArch() {
		return System.getProperty("os.arch", "unknown");
	}	
	
	public static String platform() {
		String osname = System.getProperty("os.name", "generic").toLowerCase();
		if (osname.startsWith("windows")) {
			return "win32";
		} else if (osname.startsWith("linux")) {
			return "linux";
		} else if (osname.startsWith("sunos")) {
			return "solaris";
		} else if (osname.startsWith("mac") || osname.startsWith("darwin")) {
			return "mac";
		} else if (osname.startsWith("aix")) {
			return "aix";
		} else return "generic";
	}	

	public static boolean isWindows() {
		return (getOsName().toLowerCase().indexOf("windows") >= 0);
	}

	public static boolean isLinux() {
		return getOsName().toLowerCase().indexOf("linux") >= 0;
	}	

	public static boolean isMac() {
		final String os = getOsName().toLowerCase();
		return os.startsWith("mac") || os.startsWith("darwin");
	}

	public boolean is64Bit() {
		return getOsArch().equals("x64");
	}
	
}
