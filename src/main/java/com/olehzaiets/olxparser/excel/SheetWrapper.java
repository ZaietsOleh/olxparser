package com.olehzaiets.olxparser.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SheetWrapper {

    private final Workbook workbook;

    private final Sheet sheet;

    public SheetWrapper(String name) {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(name);
    }

    public byte[] asByteArray() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);
        workbook.close();
        return stream.toByteArray();
    }

    public void setTitles(String... names) {
        insertRow(0, names);
    }

    public void insertRow(int index, String... values) {
        Row valuesRow = getOrCreateRow(index);

        for (int i = 0; i < values.length; ++i) {
            valuesRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                    .setCellValue(values[i]);
        }
    }

    private Row getOrCreateRow(int index) {
        Row row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }

        return row;
    }
}
