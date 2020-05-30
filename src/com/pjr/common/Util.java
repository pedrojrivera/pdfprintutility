
package com.pjr.common;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * Util()
 * General purpose utility class.
 */
public class Util {

	/**
	 * Create a fixed length string padded with any character.
	 */
	public static String fill(String str, int toLen, char chToFill, boolean prepend ){

		if( str.length() >= toLen ) return str ;

		StringBuffer buf = prepend ? new StringBuffer() : new StringBuffer( str.trim() ) ;

		int i = str.trim().length();

		if ( i < 0 ) i = 1;

		while (i < toLen) {
			buf.append( chToFill ) ;
			++i;
		}

		if( prepend ) buf.append( str.trim() ) ;
		
		return buf.toString() ;
	
	}

	/**
	 * Get date difference in days
	 */
	public static int diffInDays(Date d1, Date d2) {
	      int MILLIS_IN_DAY = 86400000;
	 
	      Calendar c1 = Calendar.getInstance();
	      c1.setTime(d1);
	      c1.set(Calendar.MILLISECOND, 0);
	      c1.set(Calendar.SECOND, 0);
	      c1.set(Calendar.MINUTE, 0);
	      c1.set(Calendar.HOUR_OF_DAY, 0);
	 
	      Calendar c2 = Calendar.getInstance();
	      c2.setTime(d2);
	      c2.set(Calendar.MILLISECOND, 0);
	      c2.set(Calendar.SECOND, 0);
	      c2.set(Calendar.MINUTE, 0);
	      c2.set(Calendar.HOUR_OF_DAY, 0);
	 
	      return (int) ((c1.getTimeInMillis() - c2.getTimeInMillis()) / MILLIS_IN_DAY);
	}
	
	/**
	 * Get current time stamp
	 */
	public static Timestamp getTimeStamp() {
		Calendar cal = Calendar.getInstance();
		Timestamp ts = new Timestamp( cal.getTime().getTime() );
		return ts;
	}
	
	/**
	 * Get a date object from a db2 *iso timestamp string 
	 */
	public static Date getDateFromString(String value) {
		DateFormat f1 = new SimpleDateFormat("yyy-MM-dd-HH.mm.ss");
		try {
			return f1.parse(value);
		} catch (ParseException e) {}
		return new Date();
	}

	/**
	 * Center a window to the screen
	 * @param shell
	 */
	public static void centerShell(Shell shell) {
	
		Rectangle bounds;
		
		/** 
		 * Get the size of the parent shell or 
		 * if main shell, get monitor size. 
		 */
		if (shell.getParent() == null) {
			Display display = Display.getDefault();
			Monitor primary = display.getPrimaryMonitor();
			bounds = primary.getBounds();
		} else {
			bounds = shell.getParent().getBounds();
		}
		
		/** get the size of the window */
		Rectangle rect = shell.getBounds();
		
		/** calculate the center */
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		
		/** set the new location */
		shell.setLocation(x, y);
		
	}

}
