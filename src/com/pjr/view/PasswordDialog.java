package com.pjr.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.pjr.common.Util;

public class PasswordDialog extends Dialog {

	protected Object result;
	protected Shell shlPasswordRequired;
	private Text textPassword;
	private Button btnOk; 
	private String password;
	private Composite composite;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PasswordDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		shlPasswordRequired.pack();		

		/** center window */
		Util.centerShell(shlPasswordRequired);

		/** display the dialog */
		shlPasswordRequired.open();

		Display display = getParent().getDisplay();

		while (!shlPasswordRequired.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlPasswordRequired = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shlPasswordRequired.setSize(512, 187);
		shlPasswordRequired.setText("Password Required");
		GridLayout gl_shlPasswordRequired = new GridLayout(2, false);
		shlPasswordRequired.setLayout(gl_shlPasswordRequired);
		
		Label lblImage = new Label(shlPasswordRequired, SWT.NONE);
		lblImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 6));
		lblImage.setImage(SWTResourceManager.getImage(PasswordDialog.class, "/resources/encrypted.png"));
		lblImage.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblImage.setAlignment(SWT.CENTER);
		
		Label lblText1 = new Label(shlPasswordRequired, SWT.NONE);
		lblText1.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		lblText1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblText1.setText("Selected document is encrypted.");
		
		Label lblText2 = new Label(shlPasswordRequired, SWT.NONE);
		lblText2.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		lblText2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblText2.setText("Please provide a valid password to continue.");
		new Label(shlPasswordRequired, SWT.NONE);
		
		textPassword = new Text(shlPasswordRequired, SWT.BORDER | SWT.PASSWORD);
		textPassword.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				onPasswordChanged();
			}
		});
		GridData gd_textPassword = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textPassword.widthHint = 389;
		textPassword.setLayoutData(gd_textPassword);
		new Label(shlPasswordRequired, SWT.NONE);
		
		composite = new Composite(shlPasswordRequired, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelPass();
			}
		});
		btnCancel.setImage(SWTResourceManager.getImage(PasswordDialog.class, "/resources/close.png"));
		btnCancel.setBounds(185, 0, 90, 35);
		btnCancel.setText("Cancel");
		
		btnOk = new Button(composite, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				confirmPass();
			}
		});
		btnOk.setImage(SWTResourceManager.getImage(PasswordDialog.class, "/resources/checkmark.png"));
		btnOk.setText("Ok");
		btnOk.setBounds(89, 0, 90, 35);		
		btnOk.setEnabled(false);
		composite.setTabList(new Control[]{btnOk, btnCancel});

	}
	
	private void cancelPass() {
		password = null;
		shlPasswordRequired.close();
	}
	
	private void confirmPass() {
		password = textPassword.getText();
		shlPasswordRequired.close();
	}

	private void onPasswordChanged() {
		if (textPassword.getText().trim().length() == 0) {
			btnOk.setEnabled(false);
		} else {
			btnOk.setEnabled(true);
		}
	}
	
	public String getPassword() {
		return password;
	}
	
}
