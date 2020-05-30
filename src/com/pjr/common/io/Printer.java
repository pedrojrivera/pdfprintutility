
package com.pjr.common.io;

import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * Print()
 * Abstract print class
 */
public abstract class Printer {

	/**
	 * Get the default printer
	 */
	public String getDefaultPrinter() {	
		return PrintServiceLookup.lookupDefaultPrintService().getName();
	}

	/**
	 * Determine if printer exists
	 */
	public boolean printerExists(String printer) {
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		boolean found = false;
    	for (int i = 0; i < printServices.length; i++) {
    		if (printServices[i].getName().toLowerCase().equals(printer.toLowerCase())) {
    			found = true;
    			break;
    		}
    	}
    	return found;
	}
	
	/**
	 * Get the proper printer name, if not found
	 * use the default printer.
	 */
	public String getPrinterName(String printer) {
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		String printerName = "";
    	
    	for (int i = 0; i < printServices.length; i++) {
    		if (printServices[i].getName().toLowerCase().equals(printer.toLowerCase())) {
    			printerName = printServices[i].getName();
    			break;
    		}
    	}
    	
    	if (printerName.trim().length() == 0) {
    		printerName = getDefaultPrinter();
    	}
    	
    	return printerName;
	}

	/**
	 * Get the proper printer service, if not found
	 * use the default printer.
	 */
	public PrintService getPrinterService(String printer) {
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		PrintService printService = null;
    	
    	if (printer != null) {
        	if (printer.trim().length() == 0) {
        		printer = getDefaultPrinter();
        	}
        	
        	for (int i = 0; i < printServices.length; i++) {
        		if (printServices[i].getName().trim().toLowerCase().equals(printer.trim().toLowerCase())) {
        			printService = printServices[i];
        			break;
        		}
        	}    		
    	}
    	
    	if (printService == null) {
    		printService = PrintServiceLookup.lookupDefaultPrintService();
    	}
    	
    	return printService;
	}
	
    /**
     * Get a list of available printer
     */
	public String getPrinterList() {
    	PrintService[] printServices = PrinterJob.lookupPrintServices();
    	String printers = "";
    	for (int i = 0; i < printServices.length; i++) {
    		printers += "Printer => " + printServices[i].getName() + "\r\n";
    	}
    	return printers;
   	}
	
    /**
     * Get a list of available printer
     */
	public String[] getAvailablePrinters() {
    	PrintService[] printServices = PrinterJob.lookupPrintServices();
    	String list[] = new String[printServices.length];
    	
    	for (int i = 0; i < printServices.length; i++) {
    		list[i] = printServices[i].getName();
    	}
    	
    	return list;
   	}
	
}
