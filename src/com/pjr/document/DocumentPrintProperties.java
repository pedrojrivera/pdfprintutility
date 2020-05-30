package com.pjr.document;

import com.pjr.enums.PageSize;

public class DocumentPrintProperties {
	private String document;
	private String jobName; 
	private PageSize pageSize;
	private float width;
	private float height;
	private int copies; 

	public DocumentPrintProperties(String document, String jobName, PageSize pageSize, int copies) {
		this.document = document;
		this.jobName = jobName;
		this.pageSize = pageSize; 
		this.copies = copies;
		
		// Portrait Letter
		if (pageSize.equals(PageSize.LETTER)) {
			this.width = 612;
			this.height = 792;
		// Portrait Legal
		} else {
			this.width = 612;
			this.height = 1008;
		}

	}
	
	public String getDocument() {
		return this.document;
	}
	
	public String getJobName() {
		return this.jobName;
	}
	
	public PageSize getPageSize() {
		return this.pageSize;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public int getCopies() {
		return this.copies;
	}
}
