package com.pjr.tests;

import java.io.IOException;

import com.pjr.common.io.UtilConvert;
import com.pjr.document.Document;
import com.pjr.document.DocumentPrint;
import com.pjr.enums.PageSize;

public class DualTrayPrint {

	@SuppressWarnings("unused")
	public static void main(String[] args) {  
//		String pdf = "c:\\tmp\\DANFORD_FINAL_DOCS.pdf";
//		String pdf = "c:\\Users\\pedro\\Downloads\\PHILMAN_PACKAGE.pdf";
		String pdf = "c:\\Users\\pedro\\Downloads\\20-1188R - Lender Package.pdf";
		String tif = "c:\\Users\\pedro\\Downloads\\ESCROW.tif";
		String password = null; //"";
		String printerName = "";
		int startPage = 1;
		int endPage = 0; 
		boolean testFirst = false; 

		// Set logging level
		java.util.logging.Logger
	    .getLogger("org.apache").setLevel(java.util.logging.Level.SEVERE);
		
		if (testFirst) {
			System.out.println("Creating tif...");
			UtilConvert.convertPdfToTiff(pdf, tif, password);
			System.out.println("Done!!");
			return;
		}

		printerName = "Brother HL-L5200DW series";		
//		printerName = "HP LaserJet 400 M401n (6B8C0F)";
		
		
		listAvailablePrinters();
		
		Document doc;
		
		try {
			doc = new Document(pdf);
//			doc.printDocument(printerName, startPage, endPage, PageSize.LEGAL_AND_LETTER, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

		
	/**
	 * List available printers on the workstation
	 */
	public static void listAvailablePrinters() {
		DocumentPrint p = new DocumentPrint(null);
		System.out.println("===========================================================");
		System.out.println("== AVAILABLE PRINTERS =====================================");
		System.out.println(p.getPrinterList());
		System.out.println("===========================================================");
	}
	
}



