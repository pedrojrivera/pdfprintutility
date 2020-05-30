
package com.pjr.document;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import com.pjr.common.io.Printer;
import com.pjr.common.io.UtilFile;
import com.pjr.enums.PageSize;

/**
 * Converts the PDF content into printable format
 */
public class DocumentPrint extends Printer {
	private PrinterJob pJob = null;
	private AtomicBoolean isCancelled;

	/**
	 * ::Constructor()
	 */
	public DocumentPrint(AtomicBoolean isCancelled) {
		if (isCancelled != null) {
			this.isCancelled = isCancelled;
			listenForCancel();
		}
	}

	/**
	 * Print a PDF document
	 */
	public boolean print(DocumentPrintProperties docProp, String printer, boolean rotate180) {
		boolean success = false;

		PDDocument document = null;

		try {

			document = PDDocument.load( new File(docProp.getDocument()), MemoryUsageSetting.setupTempFileOnly() );

			if (rotate180) {
				UtilFile.getInstance().rotate180(document);
			}

			pJob = PrinterJob.getPrinterJob();
			
			if (pJob == null) {
				return success;
			}
			
			PrintService ps = getPrinterService(printer);
			
			if (ps != null) {
				pJob.setPrintService(ps);
				pJob.setJobName(docProp.getJobName());
				pJob.setCopies(docProp.getCopies());
				pJob.setPageable(new PDFPageable(document));

				// Define custom paper
				Paper paper = new Paper();
				paper.setSize(docProp.getWidth(), docProp.getHeight()); // 1/72 inch
				paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight()); // no margins

				// Custom page format
				PageFormat pageFormat = new PageFormat();
				pageFormat.setPaper(paper);

		        // Override the page format
		        Book book = new Book();
		        book.append(new PDFPrintable(document, Scaling.SCALE_TO_FIT, false), pageFormat, document.getNumberOfPages());		        
		        pJob.setPageable(book);

				PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

				// Set paper size attribute
				if (docProp.getPageSize().equals(PageSize.LETTER)) {
					attributes.add(MediaSizeName.NA_LETTER);
				} else {
					attributes.add(MediaSizeName.NA_LEGAL);
				}

				if (!isCancelled.get()) {
					pJob.print(attributes);	
				}

				success = true;				
			}

		} catch (PrinterAbortException e) {
		} catch (PrinterException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (document != null) {
					document.close();	
				}
			} catch (IOException e) {}		
		}
		return success;
	}
	
	/**
	 * Listen for a cancel request
	 */
	private void listenForCancel() {
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {

				synchronized (isCancelled) {
					try {
						isCancelled.wait();
					} catch (InterruptedException e) {
					} finally {
						if (pJob != null) {
							if (isCancelled.get()) {
								pJob.cancel();	
							}							
						}
					}
				}

			}
		});

		t1.start();

	}

}