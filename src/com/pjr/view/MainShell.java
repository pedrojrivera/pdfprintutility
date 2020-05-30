package com.pjr.view;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.pjr.common.Util;
import com.pjr.common.os.Os;
import com.pjr.document.Document;
import com.pjr.document.DocumentPrint;
import com.pjr.enums.PageSize;
import com.pjr.runnable.PrintJob;

import org.eclipse.swt.widgets.ProgressBar;

public class MainShell extends Shell {
	private Document document;

	private static String appTitle = "Print PDF Utility";
	private Label lblSelectedDocument;
	private Label lblPrintDoc;
	private Label lblShowPrintNone;
	private Text textStartPage;
	private Text textEndPage;
	private Combo comboPrinters;
	private Combo comboPageSize;
	private Button btnOpenDocument; 
	private Button btnPrint;
	private Button btnRotate180;	
	private Button btnCancelPrint;
	private ProgressBar progressBar;		

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {

		/** set logging to a minimum */
		java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.SEVERE);

		/** Set Mac osx Menu Bar name */
		if (Os.isMac()) {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", appTitle);
		}
		
		try {
			Display display = Display.getDefault();
			MainShell shell = new MainShell(display);
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SWTResourceManager.dispose();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public MainShell(Display display) {
		super(display, SWT.CLOSE | SWT.MIN | SWT.RESIZE | SWT.TITLE);

		/** Create shell components */
		createContents();

		/** Remove excess space */
		pack();
		
		/** center window */
		Util.centerShell(getShell());
		
		/** open shell window */
		open();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setImage(SWTResourceManager.getImage(MainShell.class, "/resources/printer-icon.ico"));
		setText(appTitle);

		setMinimumSize(538, 355);
		setSize(538, 468);

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginTop = 5;
		gridLayout.marginBottom = 5;
		setLayout(gridLayout);
		
		btnOpenDocument = new Button(this, SWT.NONE);
		btnOpenDocument.setImage(SWTResourceManager.getImage(MainShell.class, "/resources/folder.png"));
		btnOpenDocument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleControls(openDocument());
			}
		});
		GridData gd_btnOpenDocument = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnOpenDocument.widthHint = 150;
		gd_btnOpenDocument.heightHint = 35;
		btnOpenDocument.setLayoutData(gd_btnOpenDocument);
		btnOpenDocument.setText("Open Document");
		
		lblSelectedDocument = new Label(this, SWT.NONE);
		GridData gd_lblSelectedDocument = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectedDocument.widthHint = 350;
		lblSelectedDocument.setLayoutData(gd_lblSelectedDocument);
		lblSelectedDocument.setText("No Document Selected");

		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Label lblSelectedPrinter = new Label(this, SWT.NONE);
		lblSelectedPrinter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSelectedPrinter.setText("Selected Printer:");

		comboPrinters = new Combo(this, SWT.READ_ONLY);
		comboPrinters.setItems(getPrinters());
		comboPrinters.select(0);
		comboPrinters.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboPrinters.setEnabled(false);
		
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label lblStartingPage = new Label(this, SWT.NONE);
		lblStartingPage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStartingPage.setText("Starting Page:");

		textStartPage = new Text(this, SWT.BORDER);
		textStartPage.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				numbersOnly(e);
			}
		});
		textStartPage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textStartPage.setEnabled(false);
		
		Label lblEndingPage = new Label(this, SWT.NONE);
		lblEndingPage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEndingPage.setText("Ending Page:");
		
		textEndPage = new Text(this, SWT.BORDER);
		textEndPage.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				numbersOnly(e);
			}
		});
		textEndPage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textEndPage.setEnabled(false);
		
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Label  lblPageSize = new Label(this, SWT.NONE);
		lblPageSize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPageSize.setText("Page Size to Print:");

		comboPageSize = new Combo(this, SWT.READ_ONLY);
		comboPageSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboPageSize.setItems(getPageSizes());
		comboPageSize.select(0);
		comboPageSize.setEnabled(false);

		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		btnRotate180 = new Button(this, SWT.CHECK);
		btnRotate180.setEnabled(false);
		btnRotate180.setText("Rotate by 180 degrees");
		
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		btnPrint = new Button(this, SWT.NONE);
		btnPrint.setImage(SWTResourceManager.getImage(MainShell.class, "/resources/printer.png"));
		btnPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				printDocument();
			}
		});
		GridData gd_btnPrint = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnPrint.widthHint = 145;
		gd_btnPrint.heightHint = 35;
		btnPrint.setLayoutData(gd_btnPrint);
		btnPrint.setText("Print Document");
		btnPrint.setEnabled(false);
		
	}

	private void showPrintProgress() {
		btnOpenDocument.setEnabled(false);
		lblPrintDoc = new Label(this, SWT.NONE);
		lblPrintDoc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblPrintDoc.setText("Printing document, please wait. . .");
		progressBar = new ProgressBar(this, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblShowPrintNone = new Label(this, SWT.NONE);
		btnCancelPrint = new Button(this, SWT.NONE);
		btnCancelPrint.setImage(SWTResourceManager.getImage(MainShell.class, "/resources/close.png"));
		GridData gd_btnCancelPrint = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancelPrint.heightHint = 35;
		gd_btnCancelPrint.widthHint = 145;
		btnCancelPrint.setLayoutData(gd_btnCancelPrint);
		btnCancelPrint.setText("Cancel Printing");		
		pack();
	}

	private void removePrintProgress() {
		btnOpenDocument.setEnabled(true);
		lblPrintDoc.dispose();
		lblShowPrintNone.dispose();
		progressBar.dispose();
		btnCancelPrint.dispose();
		lblPrintDoc = null;
		lblShowPrintNone = null;
		progressBar = null;
		btnCancelPrint = null;
		pack();
	}	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private String showPasswordDialog() { 
		PasswordDialog pd = new PasswordDialog(getShell());
		pd.open();
		return pd.getPassword();
	}

	/**
	 * Open file dialog and select a document
	 * @return
	 */
	private String showFileDialog() {
		FileDialog fd = new FileDialog(this, SWT.OPEN);
		fd.setText("Open Document");
		String[] filterExt = { "*.pdf" };
		fd.setFilterExtensions(filterExt);
		return fd.open();
	}
	
	/**
	 * Open a document
	 * @return
	 */
	private boolean openDocument() {
	
		boolean success = false; 
		String selected = showFileDialog();
		
		lblSelectedDocument.setText("No Document Selected");
		textStartPage.setText("");
		textEndPage.setText("");

		if (selected == null) {
			return success;
		}

		File file = new File(selected); 

		if (!file.exists()) {
			return success; 
		}

		try {
			String password = null;
			document = new Document(file.getPath());
			if (document.isEncrypted()) {		
				document = null;
				password = showPasswordDialog();
				if (password != null) {
					document = new Document(file.getPath(), password);
				}
			}
		} catch (IOException e) {
			document = null;
		}

		if (document != null && document.isOpened()) {
			lblSelectedDocument.setText(file.getName());
			textStartPage.setText("1");
			textEndPage.setText(String.valueOf(document.getPageCount()));			
			success = true;
		} else {
			messageBadPassword();
		}
		
		return success;
		
	}

	/**
	 * Print document
	 */
	private void printDocument() {

		if (messageYesNo() != SWT.YES) {
			return;
		}

		if (!isDataValid()) {
			messageInvalidData();
			return;
		}

		toggleControls(false);
		showPrintProgress();

		/** Setup thread safe variables to communicate between threads */
		AtomicBoolean isDonePrinting = new AtomicBoolean(false);
		AtomicBoolean isCancelled = new AtomicBoolean(false);

		/** Setup print cancel button */
		btnCancelPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnCancelPrint.setText("Cancelling job...");
				synchronized (isDonePrinting) {
					isCancelled.set(true);
					isDonePrinting.set(true);					
					isDonePrinting.notifyAll();
				}				
			}
		});

		/** Create thread to wait for print job to complete */
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (isDonePrinting) {
						isDonePrinting.wait();	
					}					
				} catch (InterruptedException e) {
				} finally {
					if (isDonePrinting.get()) {
						getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								removePrintProgress();	
								toggleControls(true);
								if (!isCancelled.get()) {
									messagePrintingCompleted();	
								} else {
									messagePrintingCancelled();
								}
							}
						});
					}					
				}
			}
		});

		/** Create thread to process print job */
		Thread t2 = new Thread(new PrintJob(
					isDonePrinting, 
					document, 
					comboPrinters.getText(), 
					Integer.valueOf(textStartPage.getText().trim()),
					Integer.valueOf(textEndPage.getText().trim()), 
					getSelectedPageSize(), 
					btnRotate180.getSelection()));

		/** Start threads */
		t1.start();
		t2.start();

	}
	
	/**
	 * Toggle controls
	 * @param enable
	 */
	private void toggleControls(boolean enable) {
		if (enable) {
			comboPrinters.setEnabled(true);
			btnPrint.setEnabled(true);		
			textStartPage.setEnabled(true);					
			textEndPage.setEnabled(true);
			comboPageSize.setEnabled(true);
			btnRotate180.setEnabled(true);
		} else {			
			comboPrinters.setEnabled(false);
			btnPrint.setEnabled(false);					
			textStartPage.setEnabled(false);
			textEndPage.setEnabled(false);
			comboPageSize.setEnabled(false);
			btnRotate180.setEnabled(false);
		}
	}

	/**
	 * Only permit number values
	 * @param e
	 */
	private void numbersOnly(VerifyEvent e) {
		String string = e.text;
        char[] chars = new char[string.length()];
        string.getChars(0, chars.length, chars, 0);
        for (int i = 0; i < chars.length; i++) {
          if (!('0' <= chars[i] && chars[i] <= '9')) {
            e.doit = false;
            return;
          }
        }
	}
	
	/**
	 * Validate data
	 * @return
	 */
	private boolean isDataValid() {
		boolean success = true; 
		
		if (textStartPage.getText().trim().length() == 0 ||
			textEndPage.getText().trim().length() == 0 || 
			comboPrinters.getText().trim().length() == 0) {
			success = false;
		}
		
		int start = Integer.valueOf(textStartPage.getText().trim());
		int end = Integer.valueOf(textEndPage.getText().trim());
		
		if (start > end || start == 0 || end == 0) {
			success = false;
		}
		
		return success;		
	}
	
	/**
	 * Show a yes / no message box
	 * @return
	 */
	private int messageYesNo() {
		MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
	    messageBox.setMessage("Are you sure you are ready to print the following document?\n\n" + lblSelectedDocument.getText());
	    messageBox.setText("Confirm Print");
	    return messageBox.open();
	}

	/**
	 * Show an error message for invalid data
	 */
	private void messageInvalidData() {
        MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR );
        messageBox.setText("Bad Input Values");
        messageBox.setMessage("The starting and ending page values are incorrect or missing. \nPlease correct your entries and try again.");
        messageBox.open();
	}

	/**
	 * Show an error message for invalid data
	 */
	private void messageBadPassword() {
        MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR );
        messageBox.setText("Password Error");
        messageBox.setMessage("Password entered is incorrect. Please try again or select another document.");
        messageBox.open();
	}

	/**
	 * Show a message printing has completed
	 */
	private void messagePrintingCompleted() {
        MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION );
        messageBox.setText("Printing Completed");
        messageBox.setMessage("The following document printed successfully:\n\n" + lblSelectedDocument.getText());
        messageBox.open();
	}
	

	/**
	 * Show a message printing has completed
	 */
	private void messagePrintingCancelled() {
        MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR );
        messageBox.setText("Printing Cancelled");
        messageBox.setMessage("Print was cancelled by user.");
        messageBox.open();
	}	
	
	/**
	 * Return a list of available printers
	 * @return
	 */
	private String[] getPrinters() {
		return (new DocumentPrint(null)).getAvailablePrinters();
	}
	
	/**
	 * Return a list of available page sizes
	 * @return
	 */
	private String[] getPageSizes() {
		return new String[] { 
			PageSize.LEGAL_AND_LETTER.toString(), 
			PageSize.LETTER.toString(), 
			PageSize.LEGAL.toString()
		};
	}

	/**
	 * Get selected page size
	 * @return
	 */
	private PageSize getSelectedPageSize() {
		return PageSize.valueOf(comboPageSize.getText());
		
	}
}
