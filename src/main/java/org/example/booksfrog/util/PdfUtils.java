package org.example.booksfrog.util;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.ByteArrayInputStream;

public class PdfUtils {

    // Private constructor to prevent instantiation
    private PdfUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get the total number of pages in a PDF from a byte array.
     * 
     * @param pdfBytes The PDF file content as a byte array.
     * @return The total number of pages, or 0 if an error occurs.
     */
    public static int getPageCount(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            return 0; // No content, return 0 pages
        }
        
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            return document.getNumberOfPages();
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Return 0 if there's an error
        }
    }
}
