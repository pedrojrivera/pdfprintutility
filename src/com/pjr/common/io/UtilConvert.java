
package com.pjr.common.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import com.github.jaiimageio.plugins.tiff.TIFFTag;

public class UtilConvert {
	private static final char[] INCH_RESOLUTION_UNIT = new char[] {2}; 
	private static final long[][] X_DPI_RESOLUTION = new long[][] {{150, 1}}; 
	private static final long[][] Y_DPI_RESOLUTION = new long[][] {{150, 1}}; 
	private static final char[] BITS_PER_SAMPLE = new char[] {1}; 
	private static final char[] COMPRESSION = new char[] {BaselineTIFFTagSet.COMPRESSION_LZW};  
	private static final int HEIGHT = 1650; 
	private static final float DPI = 150;

	/** 
	* ::Constructor() 
	*/ 
	public UtilConvert() {

	}
	
	/**
	 * Convert a PDF document to a TIF file
	 */
	public static boolean convertPdfToTiff(String pdf, String tif, String password) {
		return convertPdfToTiff(pdf, tif, password, false);
	}

	/**
	 * Convert a PDF document to a TIF file
	 */
	public static boolean convertPdfToTiff(String pdf, String tif, String password, boolean rotate180) {
		try {
			convert(pdf, tif, password, rotate180);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Convert a PDF document to a TIF file
	 */
	private static void convert(String pdf, String tif, String password, boolean rotate180) throws IOException {

		PDDocument pdffile = null;
		
		try {
			if (password != null) {
				pdffile = PDDocument.load(new File(pdf), password);
			} else {
				pdffile = PDDocument.load(new File(pdf));	
			}

			if (rotate180) {
				UtilFile.getInstance().rotate180(pdffile);
			}

			PDFRenderer pdfRenderer;
			
			int numPgs = pdffile.getNumberOfPages();

			BufferedImage  image[] = new BufferedImage[numPgs];

			for (int i = 0; i < numPgs; i++) {
				pdfRenderer = new PDFRenderer(pdffile);
				image[i] = pdfRenderer.renderImageWithDPI(i, DPI, ImageType.RGB);
			}

			save(image, tif);
		} catch (IOException e) {
			throw new IOException(e);
		} finally {					
			if (pdffile != null) {
				pdffile.close();	
			}			
		}

	}

	/**
	 * Save tiff
	 */
	private static void save(BufferedImage[] b, String tif) throws IOException {

		// Get a TIFF writer and set its output.
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("TIFF");

		if (writers == null || !writers.hasNext()) {
			throw new RuntimeException("No writers for available.");
		}

		FileImageOutputStream fios = new FileImageOutputStream(new File(tif));
		
		ImageWriter writer = (ImageWriter) writers.next();
		writer.setOutput(fios);
		writer.prepareWriteSequence(null);

		for (int i = 0; i < b.length; i++) {
			ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromRenderedImage(b[i]);
			IIOMetadata imageMetadata = writer.getDefaultImageMetadata(imageType, null);
			imageMetadata = createImageMetadata(imageMetadata);
			writer.writeToSequence(new IIOImage(b[i], null, imageMetadata), null);
		}

		writer.endWriteSequence();
		writer.dispose();
		writer = null;

		fios.close();
		
	}

	/**
	 * Return the metadata for the new TIF image
	 */
	private static IIOMetadata createImageMetadata(IIOMetadata imageMetadata) throws IIOInvalidTreeException {

		// Get the IFD (Image File Directory) which is the root of all the tags
		// for this image. From here we can get all the tags in the image.
		TIFFDirectory ifd = TIFFDirectory.createFromMetadata(imageMetadata);

		// Create the necessary TIFF tags that we want to add to the image
		// metadata
		BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();

		// Resolution tags...
		TIFFTag tagResUnit = base.getTag(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT);
		TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
		TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);

		// BitsPerSample tag
		TIFFTag tagBitSample = base.getTag(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);

		// Row and Strip tags...
		TIFFTag tagRowStrips = base.getTag(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP);

		// Compression tag
		TIFFTag tagCompression = base.getTag(BaselineTIFFTagSet.TAG_COMPRESSION);

		// Set the tag values
		TIFFField fieldResUnit = new TIFFField(tagResUnit, TIFFTag.TIFF_SHORT, 1, INCH_RESOLUTION_UNIT);
		TIFFField fieldXRes = new TIFFField(tagXRes, TIFFTag.TIFF_RATIONAL, 1, X_DPI_RESOLUTION);
		TIFFField fieldYRes = new TIFFField(tagYRes, TIFFTag.TIFF_RATIONAL, 1, Y_DPI_RESOLUTION);
		TIFFField fieldBitSample = new TIFFField(tagBitSample, TIFFTag.TIFF_SHORT, 1, BITS_PER_SAMPLE);
		TIFFField fieldRowStrips = new TIFFField(tagRowStrips, TIFFTag.TIFF_LONG, 1, new long[] { HEIGHT });
		TIFFField fieldCompression = new TIFFField(tagCompression, TIFFTag.TIFF_SHORT, 1, COMPRESSION);

		// Cleanup the fields
		// ifd.removeTIFFFields();

		// Add the new tag/value sets to the image metadata
		ifd.addTIFFField(fieldResUnit);
		ifd.addTIFFField(fieldXRes);
		ifd.addTIFFField(fieldYRes);
		ifd.addTIFFField(fieldBitSample);
		ifd.addTIFFField(fieldRowStrips);
		ifd.addTIFFField(fieldCompression);

		return ifd.getAsMetadata();

	} 
	
}
