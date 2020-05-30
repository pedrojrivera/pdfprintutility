package com.pjr.document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import com.pjr.common.Util;
import com.pjr.common.io.UtilFile;
import com.pjr.common.io.UtilFileName;
import com.pjr.enums.PageSize;

public class Document { 
	private String file; 
	private String password;
	private int pageCount; 
	private boolean isEncrypted = false;
	private boolean isOpened = false;

	public Document(String file) throws IOException {
		this(file, null);
	}

	public Document(String file, String password) throws IOException {
		this.file = file;
		this.password = password;

		PDDocument document = null;		

		try {
			document = PDDocument.load( new File(file), password);
			this.pageCount = document.getNumberOfPages();
			this.isOpened = true;
		} catch (InvalidPasswordException e) {
			this.isEncrypted = true;
		} catch (IOException e) {			
			throw new IOException(e);			
		} finally {
			try {
				if (document != null) {
					this.pageCount = document.getNumberOfPages();
					document.close();	
				}
			} catch (IOException e) {}		
		}	
		
	}
	
	public int getPageCount() {
		return this.pageCount;
	}
	
	public boolean isEncrypted() {
		return this.isEncrypted;
	}

	public boolean isOpened() {
		return this.isOpened;
	}

	public String getFileName() {
		return this.file;
	}

	public void printDocument(AtomicBoolean isCancelled, String printer, int startPage, int endPage, PageSize pageSize, boolean rotate180) throws IOException {
		DocumentPrint print = new DocumentPrint(isCancelled);
		PDDocument document = null;
		String workingPath; 

		if (startPage == 0 || startPage > endPage) {
			startPage = 1;
		}

		if (endPage == 0) {
			endPage = pageCount;	
		}	

		try {

			document = PDDocument.load( new File(file), password );				

			workingPath = split(file, password, startPage, endPage);

			if (workingPath == null) {
				return;
			}

			List<DocumentPrintProperties> list; 

			// Determine what to print
			if (pageSize.equals(PageSize.LEGAL_AND_LETTER)) {
				list = groupAndMerge(workingPath, password);
			} else {
				list = groupAndMergeLetterOrLegalOnly(workingPath, password, pageSize);
			}

			for (DocumentPrintProperties dc: list) {
				if (isCancelled.get()) {
					break;
				}
				//System.out.println("Printing document " + dc.getDocument() + " .. " + dc.getPageSize() + " .. " + dc.getHeight());
				print.print(dc, printer, rotate180);
			}

			// Remove work directory
			File file = new File(workingPath + ".pdf"); 
			UtilFile.getInstance().deleteDir(file.getParent());

		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			try {
				if (document != null) {
					document.close();	
				}
			} catch (IOException e) {}		
		}		
	}

	/**
	 * Split document 
	 * @param source
	 * @throws IOException
	 */
	private String split(String source, String password, int startPage, int endPage) throws IOException {		
		
		File file = new File(source);
		
		if (!file.exists()) {
			System.out.println("Document " + source + " could not be found.");
			return "";
		}
		
		UtilFileName fn = new UtilFileName(file.getPath(), File.separator, ".pdf");

		// Create work folder
		Path tempDir = Files.createTempDirectory("pjr");
		String tmp = tempDir.toFile().getPath();
		
		if (!UtilFile.getInstance().fileExists(tmp)) {
			System.out.println("Directory does not exist " + tmp);
			return null;
		}

		// Set root file name
		tmp = tmp + File.separator + fn.getFilename();

		// Copy document to working folder and split
		if (UtilFile.getInstance().extractPageRange(source, password, tmp + ".pdf", startPage, endPage)) {
			if (!UtilFile.getInstance().split(tmp + ".pdf", password)) {
				return null;
			}
		} else {
			return null;
		}

		return tmp;
	}


	/**
	 * Group pages by letter or legal in the correct sequence
	 */
	private List<DocumentPrintProperties> groupAndMerge(String workingPath, String password) {
		
		List<DocumentPrintProperties> list = new ArrayList<DocumentPrintProperties>();
		List<String> tmpList = new ArrayList<String>();
		PageSize prvPageSize = PageSize.UNKOWN;
		PageSize pageSize = PageSize.UNKOWN;
		String tmpFile = "";
		float pageHeight = 0;
		int mergeCnt = 1;
		
		PDDocument document = null;
		
		try {
			document = PDDocument.load( new File(workingPath + ".pdf"), password );
			
			String mergeFile = "";
			
			int cnt = document.getNumberOfPages();
			
			for (int i = 0; i < cnt; i++) {
				pageHeight = document.getPage(i).getMediaBox().getHeight();

				pageSize = PageSize.LETTER;
				
				// Letter threshold
				if (pageHeight > 850) {
					pageSize = PageSize.LEGAL;
				}

				if (i == 0) {
					mergeFile = getMergedName(workingPath, pageSize, mergeCnt);
					prvPageSize = pageSize; 
				}

				if (!prvPageSize.equals(pageSize)) {
					UtilFile.getInstance().merge(tmpList, mergeFile);
					list.add(new DocumentPrintProperties(mergeFile, getJobName(prvPageSize, mergeCnt), prvPageSize, 1));
					prvPageSize = pageSize; 
					mergeCnt += 1;
					mergeFile = getMergedName(workingPath, pageSize, mergeCnt);
					tmpList.clear();
				}
				
				tmpFile = workingPath + "." + Util.fill(String.valueOf(i+1), 3, '0', true);
				tmpList.add(tmpFile);

			}
			
			UtilFile.getInstance().merge(tmpList, mergeFile);
			list.add(new DocumentPrintProperties(mergeFile, getJobName(prvPageSize,mergeCnt), prvPageSize, 1));
			tmpList.clear();

		} catch (IOException e) {
		} finally {
			try {
				if (document != null) {
					document.close();	
				}
			} catch (IOException e) {}		
		}	   

		return list;
	}	
	
	/**
	 * Extract letter or legal pages only
	 * @param workingPath
	 * @param password
	 * @param targetPageSize
	 * @return
	 */
	private List<DocumentPrintProperties> groupAndMergeLetterOrLegalOnly(String workingPath, String password, PageSize targetPageSize) {
		List<DocumentPrintProperties> list = new ArrayList<DocumentPrintProperties>();
		List<String> tmpList = new ArrayList<String>();
		PageSize pageSize = PageSize.UNKOWN;
		String tmpFile = "";
		float pageHeight = 0;
		int mergeCnt = 1;
		
		PDDocument document = null;
		
		try {
			document = PDDocument.load( new File(workingPath + ".pdf"), password );

			String mergeFile = getMergedName(workingPath, targetPageSize, mergeCnt);

			int cnt = document.getNumberOfPages();

			for (int i = 0; i < cnt; i++) {
				pageHeight = document.getPage(i).getMediaBox().getHeight();

				pageSize = PageSize.LETTER;
				
				// Letter threshold
				if (pageHeight > 850) {
					pageSize = PageSize.LEGAL;
				}

				if (pageSize.equals(targetPageSize)) {
					tmpFile = workingPath + "." + Util.fill(String.valueOf(i+1), 3, '0', true);
					tmpList.add(tmpFile);
				}

			}

			if (tmpList.size() > 0) {
				UtilFile.getInstance().merge(tmpList, mergeFile);
				list.add(new DocumentPrintProperties(mergeFile, getJobName(targetPageSize, mergeCnt), targetPageSize, 1));
				tmpList.clear();
			}

		} catch (IOException e) {
		} finally {
			try {
				if (document != null) {
					document.close();	
				}
			} catch (IOException e) {}		
		}	   

		return list;
	}
	
	/**
	 * Helper to normalize print job name
	 * @param pageSize
	 * @param count
	 * @return
	 */
	private String getJobName(PageSize pageSize, int count) {
		return "pdf_doc_" + pageSize.toString().toLowerCase() + "_" + count;
	}

	/**
	 * Helper to normalize merge file name
	 * @param path
	 * @param pageSize
	 * @param count
	 * @return
	 */
	private String getMergedName(String path, PageSize pageSize, int count) {
		return path + "_merged_" + pageSize.toString().toLowerCase() + "_" + Util.fill(String.valueOf(count), 3, '0', true) + ".pdf";
	}
}
