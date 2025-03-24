package cn.idev.excel.test.demo.rare;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.test.demo.write.DemoData;
import cn.idev.excel.test.util.TestFileUtil;
import cn.idev.excel.util.FileUtils;
import cn.idev.excel.util.ListUtils;
import cn.idev.excel.write.handler.RowWriteHandler;
import cn.idev.excel.write.handler.WorkbookWriteHandler;
import cn.idev.excel.write.handler.context.RowWriteHandlerContext;
import cn.idev.excel.write.handler.context.WorkbookWriteHandlerContext;
import cn.idev.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Record some uncommon cases
 *
 * @author Jiaju Zhuang
 */

@Slf4j
public class WriteTest {
    
    /**
     * Compress temporary files When exporting an Excel file in xlsx format, a temporary XML file will be generated,
     * which can be quite large. If disk space is limited, you can compress these files. Note that compression consumes
     * performance.
     */
    @Test
    public void compressedTemporaryFile() {
        log.info("Temporary XML files are stored at: {}", FileUtils.getPoiFilesPath());
        File file = TestFileUtil.createNewFile("rare/compressedTemporaryFile" + System.currentTimeMillis() + ".xlsx");
        
        // Specify which class to use for writing here
        try (ExcelWriter excelWriter = EasyExcel.write(file, DemoData.class)
                .registerWriteHandler(new WorkbookWriteHandler() {
                    
                    /**
                     * Intercept the Workbook creation completion event
                     * @param context
                     */
                    @Override
                    public void afterWorkbookCreate(WorkbookWriteHandlerContext context) {
                        // Get the Workbook object
                        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
                        // Temporary files are only generated in SXSSFWorkbook mode
                        if (workbook instanceof SXSSFWorkbook) {
                            SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbook;
                            // Enable temporary file compression. Note that this will consume CPU performance, but the temporary files will be smaller
                            sxssfWorkbook.setCompressTempFiles(true);
                        }
                    }
                }).build()) {
            // Note that the same sheet should only be created once
            WriteSheet writeSheet = EasyExcel.writerSheet("Template").build();
            // 100,000 data entries to ensure sufficient space
            for (int i = 0; i < 10000; i++) {
                // Query data from the database page by page. Here you can query data for each page from the database
                List<DemoData> data = data();
                excelWriter.write(data, writeSheet);
            }
            log.info("Writing completed, preparing to migrate and compress files.");
        }
    }
    
    /**
     * Write data to a specified cell
     */
    @Test
    public void specifiedCellWrite() {
        File file = TestFileUtil.createNewFile("rare/specifiedCellWrite" + System.currentTimeMillis() + ".xlsx");
        
        // It is necessary to distinguish whether it is before or after the last row
        // The reason for the distinction is: Excel can only move forward, and only 100 rows are stored in memory. The afterRowDispose event is called after each row is written, so modifying a row requires intercepting this event
        // If it is after the last row, since there will be no more data afterwards, just intercept the afterWorkbookDispose event and write the data when the Excel file is almost done
        EasyExcel.write(file, DemoData.class)
                // Writing value before the last row
                .registerWriteHandler(new RowWriteHandler() {
                    @Override
                    public void afterRowDispose(RowWriteHandlerContext context) {
                        if (context.getRow().getRowNum() == 2) {
                            Cell cell = context.getRow().getCell(2);
                            if (cell == null) {
                                cell = context.getRow().createCell(2);
                            }
                            cell.setCellValue("Test data for the second row");
                        }
                    }
                })
                // Writing value after the last row
                .registerWriteHandler(new WorkbookWriteHandler() {
                    @Override
                    public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
                        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
                        Sheet sheet = workbook.getSheetAt(0);
                        Row row = sheet.getRow(99);
                        if (row == null) {
                            row = sheet.createRow(99);
                        }
                        Cell cell = row.getCell(2);
                        if (cell == null) {
                            cell = row.createCell(2);
                        }
                        cell.setCellValue("Test data for row 99");
                    }
                }).sheet("Template").doWrite(data());
        
        log.info("Writing to file completed:{}", file);
    }
    
    private List<DemoData> data() {
        List<DemoData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
    
}
