package org.example.booksfrog.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PdfUtilsTest {

    @Test
    void testGetPageCount_NullInput() {
        int pageCount = PdfUtils.getPageCount(null);
        assertEquals(0, pageCount, "Page count should be 0 for null input");
    }

    @Test
    void testGetPageCount_EmptyInput() {
        int pageCount = PdfUtils.getPageCount(new byte[0]);
        assertEquals(0, pageCount, "Page count should be 0 for empty input");
    }

    @Test
    void testGetPageCount_ValidPdf() throws Exception {
        // Create a simple PDF with 3 pages
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.addPage(new PDPage());
            document.addPage(new PDPage());
            document.save(outputStream);
        }

        byte[] pdfBytes = outputStream.toByteArray();
        int pageCount = PdfUtils.getPageCount(pdfBytes);

        assertEquals(3, pageCount, "Page count should match the number of pages in the PDF");
    }

    @Test
    void testGetPageCount_InvalidPdf() {
        byte[] invalidPdf = "Not a PDF".getBytes();

        int pageCount = PdfUtils.getPageCount(invalidPdf);
        assertEquals(0, pageCount, "Page count should be 0 for invalid PDF content");
    }
}
