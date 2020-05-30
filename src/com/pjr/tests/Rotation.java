package com.pjr.tests;


import com.pjr.common.io.UtilConvert;

public class Rotation {

	public static void main(String[] args) {
		String pdf = "c:\\Users\\pedro\\Downloads\\Final_Closing_Package (2).pdf";
		String tif = "c:\\Users\\pedro\\Downloads\\Test180.tif";
		UtilConvert.convertPdfToTiff(pdf, tif, null, true);		
	}

}
