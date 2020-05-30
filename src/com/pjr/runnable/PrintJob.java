package com.pjr.runnable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pjr.document.Document;
import com.pjr.enums.PageSize;

public class PrintJob implements Runnable {
	private AtomicBoolean isDonePrinting;
	private Document document;
	private String printer; 
	private int startPage;
	private int endPage; 
	private PageSize pageSize;
	private boolean rotate180;

	public PrintJob(AtomicBoolean isDonePrinting, Document document, String printer, int startPage, int endPage, PageSize pageSize, boolean rotate180) {
		this.isDonePrinting = isDonePrinting;
		this.document = document;
		this.printer = printer;
		this.startPage = startPage;
		this.endPage = endPage;
		this.pageSize = pageSize;
		this.rotate180 = rotate180;
	}
	
	@Override
	public void run() {

		try {				
			document.printDocument(isDonePrinting, printer, startPage, endPage, pageSize, rotate180);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();			
		} finally {
			synchronized (isDonePrinting) {
				isDonePrinting.set(true);
				isDonePrinting.notifyAll();
			}
		}		

	}

}
