package com.mitec.business.service;



import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.mitec.business.model.ReportPDF;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
public class DocxService {

    public byte[] addTextAfterString(File inputFile,
                                     String name,
                                     String address,
                                     String serialNumber,
                                     String sign,
                                     String phone,
                                     ReportPDF reportPDF,
                                     String atmId) throws IOException {

        int signCount = 1;
        int nameCount = 1;
        int addressCount = 1;
        int serialNumberCount = 1;
        int phoneCount = 1;
        int atmIdCount = 1;

        try (XWPFDocument document = new XWPFDocument(new FileInputStream(inputFile))) {
            // Modify paragraphs
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                switch (handleParagraph(paragraph, sign, name, address, phone, serialNumber, signCount, nameCount, addressCount, serialNumberCount, phoneCount, reportPDF, atmId, atmIdCount)) {
                    case 1: nameCount++; break;
                    case 2: serialNumberCount++; break;
                    case 3: addressCount++; break;
                    case 4: signCount++; break;
                    case 5: phoneCount++; break;
                    case 6: atmIdCount++; break;
                }
            }

            // Modify tables
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph cellParagraph : cell.getParagraphs()) {
                            switch (handleParagraph(cellParagraph, sign, name, address, phone, serialNumber, signCount, nameCount, addressCount, serialNumberCount, phoneCount, reportPDF, atmId, atmIdCount)) {
                                case 1: nameCount++; break;
                                case 2: serialNumberCount++; break;
                                case 3: addressCount++; break;
                                case 4: signCount++; break;
                                case 5: phoneCount++; break;
                                case 6: atmIdCount++; break;
                            }
                        }
                    }
                }
            }

            // Write modified DOCX to ByteArrayOutputStream
            ByteArrayOutputStream docxOutputStream = new ByteArrayOutputStream();
            document.write(docxOutputStream);

            // Convert DOCX to PDF in memory
            ByteArrayInputStream docxInputStream = new ByteArrayInputStream(docxOutputStream.toByteArray());
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

            try {
                IConverter converter = LocalConverter.builder().build();
                converter.convert(docxInputStream).as(DocumentType.DOCX).to(pdfOutputStream).as(DocumentType.PDF).execute();
                System.out.println("Conversion to PDF successful");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return the PDF as a byte array
            return pdfOutputStream.toByteArray();
        }
    }


    private int handleParagraph(XWPFParagraph paragraph,String sign, String name, String address,String phone, String serialNumber, int signCount,int nameCount,int addressCount,int serialNumberCount,int phoneCount,ReportPDF reportPDF,String atmId,int atmCount) {
        String paragraphText = paragraph.getText();

        // chu ky
        if (!reportPDF.getKeyword4().isEmpty() &&paragraphText.contains(reportPDF.getKeyword4()) ) {

            if(signCount == reportPDF.getIndex4()){
                String lastChar = getLastCharNotSpace(reportPDF.getKeyword4());

                insertText(paragraph, lastChar, sign);

            }

            return 4;
        }
        // ho ten
        if (!reportPDF.getKeyword1().isEmpty() &&paragraphText.contains(reportPDF.getKeyword1()) ) {
            if (nameCount == reportPDF.getIndex1()){
                String lastChar = getLastCharNotSpace(reportPDF.getKeyword1());

                insertText(paragraph, lastChar, name);

            }
            return 1;
        }

        // address
        if (paragraphText.contains(reportPDF.getKeyword3()) ) {
            if (addressCount == reportPDF.getIndex3()){
                String lastChar = getLastCharNotSpace(reportPDF.getKeyword3());

                insertText(paragraph, lastChar, address);
            }
            return 3;

        }
        // serial number
        if (paragraphText.contains(reportPDF.getKeyword2()) ) {
            if (serialNumberCount == reportPDF.getIndex2()){
                String lastChar = getLastCharNotSpace(reportPDF.getKeyword2());

                insertText(paragraph, lastChar, serialNumber);

            }
            return 2;


        }

        // sdt
        if (!reportPDF.getKeyword5().isEmpty() &&paragraphText.contains(reportPDF.getKeyword5()) ) {

            if(phoneCount == reportPDF.getIndex5()){
                String lastChar = getLastCharNotSpace(reportPDF.getKeyword5());
                insertText(paragraph,lastChar , phone);
            }

            return 5;
        }

        // atmid
        if (!reportPDF.getKeyword6().isEmpty() &&paragraphText.contains(reportPDF.getKeyword6()) ) {

            if(atmCount == reportPDF.getIndex6()){
                String lastChar = getLastCharNotSpace(reportPDF.getKeyword6());
                insertText(paragraph,lastChar , atmId);
            }

            return 6;
        }


        return 0;
    }

    public static String getLastCharNotSpace(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("String cannot be null or empty");
        }

        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return String.valueOf(str.charAt(i));
            }
        }

        throw new IllegalArgumentException("String contains only whitespace characters");
    }

    private void insertText(XWPFParagraph paragraph, String searchString, String newText) {
//        for (XWPFRun run : paragraph.getRuns()) {
//            String runText = run.getText(0);
//            if (runText != null && runText.contains(searchString)) {
//                int index = runText.indexOf(searchString) + searchString.length();
//                String beforeText = runText.substring(0, index);
//                String afterText = runText.substring(index);
//
//                run.setText(beforeText, 0);
//
//                addTextWithLineBreaks(paragraph.createRun(), newText);
//
//                if (!afterText.isEmpty()) {
//                    XWPFRun afterRun = paragraph.createRun();
//                    afterRun.setText(afterText);
//                }
//
//                break;
//            }
//        }
        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
            XWPFRun run = paragraph.getRuns().get(i);
            String runText = run.getText(0);
            if (runText != null && runText.contains(searchString)) {
                int index = runText.lastIndexOf(searchString) + searchString.length();
                String beforeText = runText.substring(0, index);
                String afterText = runText.substring(index);

                run.setText(beforeText, 0);

                addTextWithLineBreaks(paragraph.createRun(), newText);

                if (!afterText.isEmpty()) {
                    XWPFRun afterRun = paragraph.createRun();
                    afterRun.setText(afterText);
                }

                break;
            }
        }

    }

    private void addTextWithLineBreaks(XWPFRun run, String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                run.addBreak();
            }
            run.setText(lines[i]);
            run.setFontFamily("Times New Roman");
            run.setItalic(true);
        }
    }

}




//    public void addTextAfterString(String inputFilePath, String outputFilePath, String searchString, String newText) throws IOException {
//        try (XWPFDocument document = new XWPFDocument(new FileInputStream(inputFilePath))) {
//            boolean found = false;
//
//            // Iterate through paragraphs to find the search string
//            for (XWPFParagraph paragraph : document.getParagraphs()) {
//                String paragraphText = paragraph.getText();
//                System.out.println("paragraphText: " + paragraphText);
//
//                if (paragraphText.contains(searchString)) {
//                    // Locate the run that contains the search string
//                    for (XWPFRun run : paragraph.getRuns()) {
//                        String runText = run.getText(0);
//                        if (runText != null && runText.contains(searchString)) {
//                            // Calculate the index for insertion
//                            int index = runText.indexOf(searchString) + searchString.length();
//
//                            // Insert new text while preserving formatting
//                            String beforeText = runText.substring(0, index);
//                            String afterText = runText.substring(index);
//
//                            // Clear the run and re-add text
//                            run.setText(beforeText, 0);
//                            XWPFRun newRun = paragraph.createRun();
//                            newRun.setText(newText);
//                            newRun.setFontFamily("Times New Roman");
//                            newRun.setItalic(true);
//
//                            // Add the remaining text after the new text
//                            if (!afterText.isEmpty()) {
//                                XWPFRun afterRun = paragraph.createRun();
//                                afterRun.setText(afterText);
//                            }
//
//                            found = true;
//                            break; // Exit loop after finding the search string
//                        }
//                    }
//                    if (found) break; // Exit outer loop if found
//                }
//            }
//
//            // Iterate through tables to find the search string
//            for (XWPFTable table : document.getTables()) {
//                for (XWPFTableRow row : table.getRows()) {
//                    for (XWPFTableCell cell : row.getTableCells()) {
//                        for (XWPFParagraph cellParagraph : cell.getParagraphs()) {
//                            String cellText = cellParagraph.getText();
//                            System.out.println("cellText: " + cellText);
//
//                            if (cellText.contains(searchString)) {
//                                // Locate the run that contains the search string
//                                for (XWPFRun run : cellParagraph.getRuns()) {
//                                    String runText = run.getText(0);
//                                    if (runText != null && runText.contains(searchString)) {
//                                        // Calculate the index for insertion
//                                        int index = runText.indexOf(searchString) + searchString.length();
//
//                                        // Insert new text while preserving formatting
//                                        String beforeText = runText.substring(0, index);
//                                        String afterText = runText.substring(index);
//
//                                        // Clear the run and re-add text
//                                        run.setText(beforeText, 0);
//                                        XWPFRun newRun = cellParagraph.createRun();
//                                        newRun.setText(newText);
//                                        newRun.setFontFamily("Times New Roman");
//                                        newRun.setItalic(true);
//
//                                        // Add the remaining text after the new text
//                                        if (!afterText.isEmpty()) {
//                                            XWPFRun afterRun = cellParagraph.createRun();
//                                            afterRun.setText(afterText);
//                                        }
//
//                                        found = true;
//                                        break; // Exit loop after finding the search string
//                                    }
//                                }
//                                if (found) break; // Exit loop if found
//                            }
//                            if (found) break; // Exit loop if found
//                        }
//                        if (found) break; // Exit loop if found
//                    }
//                    if (found) break; // Exit loop if found
//                }
//                if (found) break; // Exit loop if found
//            }
//
//            // Save the updated document
//            if (found) {
//                try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
//                    document.write(fos);
//                }
//                System.out.println("Document updated successfully!");
//            } else {
//                System.out.println("Search string not found.");
//            }
//        }
//    }
