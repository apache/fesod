package cn.idev.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import cn.idev.excel.read.builder.ExcelReaderBuilder;
import cn.idev.excel.read.builder.ExcelReaderSheetBuilder;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.read.metadata.ReadWorkbook;
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;
import cn.idev.excel.write.builder.ExcelWriterTableBuilder;
import cn.idev.excel.write.metadata.WriteWorkbook;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author : wh
 * @date : 2025/8/11
 * @description:
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FastExcelFactory Unit Tests")
class FastExcelFactoryTest {

    @TempDir
    Path tempDir;

    @Mock
    private OutputStream mockOutputStream;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private ReadListener mockReadListener;

    private File tempFile;
    private String tempFilePath;

    private static class DemoData {}

    private WriteWorkbook writeWorkbook(ExcelWriterBuilder builder) {
        try {
            Method parameterMethod = ExcelWriterBuilder.class.getDeclaredMethod("parameter");
            parameterMethod.setAccessible(true);
            return (WriteWorkbook) parameterMethod.invoke(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ReadWorkbook writeWorkbook(ExcelReaderBuilder builder) {
        try {
            Method parameterMethod = ExcelReaderBuilder.class.getDeclaredMethod("parameter");
            parameterMethod.setAccessible(true);
            return (ReadWorkbook) parameterMethod.invoke(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        tempFile = tempDir.resolve("test.xlsx").toFile();
        tempFilePath = tempFile.getAbsolutePath();
    }

    @Test
    void testWrite_noArgs_shouldReturnBuilder() {
        ExcelWriterBuilder builder = FastExcelFactory.write();
        assertNotNull(builder);
    }

    @Test
    void testWrite_withFile_shouldConfigureFile() {
        ExcelWriterBuilder builder = FastExcelFactory.write(tempFile);
        assertNotNull(builder);

        assertEquals(tempFile, writeWorkbook(builder).getFile());
    }

    @Test
    void testWrite_withFileAndHead_shouldConfigureAll() {
        ExcelWriterBuilder builder = FastExcelFactory.write(tempFile, DemoData.class);
        assertNotNull(builder);
        assertEquals(tempFile, writeWorkbook(builder).getFile());
        assertEquals(DemoData.class, writeWorkbook(builder).getClazz());
    }

    @Test
    void testWrite_withPathName_shouldConfigureFile() {
        ExcelWriterBuilder builder = FastExcelFactory.write(tempFilePath);
        assertNotNull(builder);
        assertEquals(tempFilePath, writeWorkbook(builder).getFile().getAbsolutePath());
    }

    @Test
    void testWrite_withPathNameAndHead_shouldConfigureAll() {
        ExcelWriterBuilder builder = FastExcelFactory.write(tempFilePath, DemoData.class);
        assertNotNull(builder);
        WriteWorkbook workbook = writeWorkbook(builder);
        assertEquals(tempFilePath, workbook.getFile().getAbsolutePath());
        assertEquals(DemoData.class, workbook.getClazz());
    }

    @Test
    void testWrite_withOutputStream_shouldConfigureStream() {
        ExcelWriterBuilder builder = FastExcelFactory.write(mockOutputStream);
        assertNotNull(builder);
        assertSame(mockOutputStream, writeWorkbook(builder).getOutputStream());
    }

    @Test
    void testWrite_withOutputStreamAndHead_shouldConfigureAll() {
        ExcelWriterBuilder builder = FastExcelFactory.write(mockOutputStream, DemoData.class);
        assertNotNull(builder);
        assertSame(mockOutputStream, writeWorkbook(builder).getOutputStream());
        assertEquals(DemoData.class, writeWorkbook(builder).getClazz());
    }

    @Test
    void testWriterSheet_noArgs_shouldReturnBuilder() {
        ExcelWriterSheetBuilder builder = FastExcelFactory.writerSheet();
        assertNotNull(builder);
    }

    @Test
    void testWriterSheet_withSheetNo_shouldReturnBuilder() {
        ExcelWriterSheetBuilder builder = FastExcelFactory.writerSheet(1);
        assertNotNull(builder);
    }

    @Test
    void testWriterSheet_withSheetName_shouldReturnBuilder() {
        ExcelWriterSheetBuilder builder = FastExcelFactory.writerSheet("TestSheet");
        assertNotNull(builder);
    }

    @Test
    void testWriterSheet_withSheetNoAndName_shouldReturnBuilder() {
        ExcelWriterSheetBuilder builder = FastExcelFactory.writerSheet(1, "TestSheet");
        assertNotNull(builder);
    }

    @Test
    void testWriterTable_noArgs_shouldReturnBuilder() {
        ExcelWriterTableBuilder builder = FastExcelFactory.writerTable();
        assertNotNull(builder);
    }

    @Test
    void testWriterTable_withTableNo_shouldReturnBuilder() {
        ExcelWriterTableBuilder builder = FastExcelFactory.writerTable(1);
        assertNotNull(builder);
    }

    // --- Read Methods Tests ---

    @Test
    void testRead_noArgs_shouldReturnBuilder() {
        ExcelReaderBuilder builder = FastExcelFactory.read();
        assertNotNull(builder);
    }

    @Test
    void testRead_withFile_shouldConfigureFile() throws Exception {
        ExcelReaderBuilder builder = FastExcelFactory.read(tempFile);
        assertNotNull(builder);
        ReadWorkbook workbook = writeWorkbook(builder);
        assertEquals(tempFile, workbook.getFile());
    }

    @Test
    void testRead_withFileAndListener_shouldConfigureAll() {
        ExcelReaderBuilder builder = FastExcelFactory.read(tempFile, mockReadListener);
        assertNotNull(builder);
        assertEquals(tempFile, writeWorkbook(builder).getFile());
        assertTrue(writeWorkbook(builder).getCustomReadListenerList().contains(mockReadListener));
    }

    @Test
    void testRead_withFileHeadAndListener_shouldConfigureAll() {
        ExcelReaderBuilder builder = FastExcelFactory.read(tempFile, DemoData.class, mockReadListener);
        assertNotNull(builder);
        assertEquals(tempFile, writeWorkbook(builder).getFile());
        assertEquals(DemoData.class, writeWorkbook(builder).getClazz());
        assertTrue(writeWorkbook(builder).getCustomReadListenerList().contains(mockReadListener));
    }

    @Test
    void testRead_withInputStreamHeadAndListener_shouldConfigureAll() {
        ExcelReaderBuilder builder = FastExcelFactory.read(mockInputStream, DemoData.class, mockReadListener);
        assertNotNull(builder);
        assertSame(mockInputStream, writeWorkbook(builder).getInputStream());
        assertEquals(DemoData.class, writeWorkbook(builder).getClazz());
        assertTrue(writeWorkbook(builder).getCustomReadListenerList().contains(mockReadListener));
    }

    // --- ReadSheet Methods Tests ---

    @Test
    void testReadSheet_noArgs_shouldReturnBuilder() {
        ExcelReaderSheetBuilder builder = FastExcelFactory.readSheet();
        assertNotNull(builder);
    }

    @Test
    void testReadSheet_withSheetNo_shouldReturnBuilder() {
        ExcelReaderSheetBuilder builder = FastExcelFactory.readSheet(0);
        assertNotNull(builder);
    }

    @Test
    void testReadSheet_withSheetName_shouldReturnBuilder() {
        ExcelReaderSheetBuilder builder = FastExcelFactory.readSheet("DataSheet");
        assertNotNull(builder);
    }

    @Test
    void testReadSheet_withAllParams_shouldReturnBuilder() {
        ExcelReaderSheetBuilder builder = FastExcelFactory.readSheet(0, "DataSheet", 100);
        assertNotNull(builder);
    }
}
