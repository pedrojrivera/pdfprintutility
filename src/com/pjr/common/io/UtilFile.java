
package com.pjr.common.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.pjr.common.Util;


/**
 * UtilFile()
 * Utility file class to merge, split, convert and process PDF files.
 */
public class UtilFile {

	private static UtilFile INSTANCE = new UtilFile();
	
	/**
	 * ::Constructor
	 */
	private UtilFile() {}
	
	/**
	 * Get instance of connection 
	 */
	public static UtilFile getInstance() {
		return INSTANCE;
	}

	/**
	 * Merge a list of single page documents to target file
	 */
    public boolean merge(List<String> source, String target) {
    	boolean success = false; 
    	
		try {
	    	PDFMergerUtility PDFmerger = new PDFMergerUtility();    	    	
	    	PDFmerger.setDestinationFileName(target);
			for (String s : source) {
				PDFmerger.addSource(s);
			}
			PDFmerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		return success;
    }
   	
    /**
	 * Split a PDF document into individual pages
	 */
    public boolean split(String pdfDoc, String password) {
    	boolean success = false;
    	PDDocument document = null;

		try {				
			document = PDDocument.load( new File(pdfDoc), password );
            Splitter splitter = new Splitter(); 
            List<PDDocument> pages = splitter.split(document);

            for (int i = 0; i < pages.size(); i++) {
            	String outFile = pdfDoc.substring(0, pdfDoc.indexOf(".pdf")) + "." + Util.fill(String.valueOf(i+1), 3, '0', true);
            	PDDocument doc = pages.get(i);
                doc.save( outFile ); 
                doc.close();
            }
            
            success = true;
            
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
	 * Extract a page range from document and create a new document 
	 * @param pdfDoc
	 * @param password
	 * @param target
	 * @param fromPage
	 * @param toPage
	 */
    public boolean extractPageRange(String pdfDoc, String password, String target, int fromPage, int toPage) {

    	boolean success = false;
    	PDDocument document = null;

		try {
			document = PDDocument.load( new File(pdfDoc), password );

			if (fromPage == 1 && document.getNumberOfPages() == toPage) {
				if (copy(pdfDoc, target)) {
					success = true;
				}
			} else {
	            Splitter splitter = new Splitter();
	            splitter.setStartPage(fromPage);
	            splitter.setEndPage(toPage);
	            splitter.setSplitAtPage(toPage); 
	            List<PDDocument> splittedList = splitter.split(document);
	            for (PDDocument doc : splittedList) {
	                doc.save( target ); 
	                doc.close();                    
	            }
	            success = true;				
			}
            
		} catch (IOException e) {
			e.printStackTrace();
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
     * Delete all files in a directory
     */
    public void deleteFiles(String path) {
	    File dir = new File(path);
	
	    if (dir.isDirectory()) {
    		File[] list = dir.listFiles();
    		for (File f : list) {
    			f.delete();
    		}
	    }

    }

    /**
     * Delete a list of files
     */
    public void deleteFiles(List<String> list) {
		for (String s : list) {
			deleteFile(s); 
		}
    }

    /**
     * Delete a file 
     */
    public void deleteFile(String file) {
		File f = new File(file);
		if (f.exists() && !f.isDirectory()) {
			f.delete();
		}
    }

    /**
     * Delete a directory
     */
    public boolean deleteDir(String dir) {
    	File d = new File(dir);
    	
    	if (!d.exists()) {
    		return true;
    	}
    	
    	if (d.exists() && d.isDirectory()) {
    		File[] files = d.listFiles();
    		for (File f : files) {
    			f.delete();
    		}
    	}
   	
    	return d.delete();
    }
    
    /**
     * Get proper separator character 
     */
    public String getSeperator() {
    		return File.separator;
    }

    /**
     * Get file extension
     */
    public String getFileExtension(String file) {
    	String ext = "";
    	try {
    		ext = file.substring(file.lastIndexOf("."));
    	} catch (Exception e) {
    		ext = "";
		}
    	return ext.toUpperCase();
    }
    
    /**
     * Determine if file exists
     */
    public boolean fileExists(String file) {
    		return (new File(file)).exists();
    }
    
    /**
     * Determine if a directory is empty
     */
    public boolean isDirectoryEmpty(String dir) {
    	File f = new File(dir);
    	
    	if (f.exists() && f.isDirectory()) {
    		if (f.list().length > 0) {
    			return false;
    		}
		}
		
    	return true;
    } 
    
    /**
     * Create directory
     */
    public boolean createDir(String dir) {
    	boolean success = false;
    	Path path = Paths.get(dir);
    	try {
			Files.createDirectory(path);
			success = true; 
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return success; 
    }
 
    /**
     * Create directory structure
     */
    public boolean createDirs(String dir) {
    	boolean success = false;
    	Path path = Paths.get(dir);
    	try {
			Files.createDirectories(path);
			success = true; 
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return success; 
    }

    /**
     * Create a new empty file
     */
    public boolean createNewFile(String file) {
    	try {
	    	if (fileExists(file)) {
	    		deleteFile(file);
	    	}
	    	File f = new File(file);
			return f.createNewFile();
		} catch (IOException e) {
			return false;
		}
    }
    
    /**
     * Move a file
     */
    public boolean move(String source, String target) {
    	boolean success = false;
    	
    	deleteFile(target);
    	
    	Path sourcePath      = Paths.get(source);
    	Path destinationPath = Paths.get(target);

    	try {
    		Files.move(sourcePath, destinationPath);
    		success = true;
    	} catch(FileAlreadyExistsException e) {
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    
    	return success;
    }
    
    /**
     * Copy a file
     */
    public boolean copy(String source, String target) {
    	boolean success = false;
    	
    	deleteFile(target);
    	
    	Path sourcePath      = Paths.get(source);
    	Path destinationPath = Paths.get(target);

    	try {
    		Files.copy(sourcePath, destinationPath);
    		success = true;
    	} catch(FileAlreadyExistsException e) {
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    
    	return success;
    }
    
    /**
     * Rotate document 180 degrees
     * @param pdffile
     */
    public void rotate180(PDDocument pdffile) {
		for (Object page: pdffile.getDocumentCatalog().getPages()) {
			if (page instanceof PDPage) {
				if (((PDPage) page).getRotation() == 0) {
					((PDPage) page).setRotation(180);
				} else if (((PDPage) page).getRotation() == 90) {
					((PDPage) page).setRotation(270);
				} else if (((PDPage) page).getRotation() == 270) {
					((PDPage) page).setRotation(90);
				}				
			}
		}				    	
    }
    
}
