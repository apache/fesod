package cn.idev.excel.test.core.hiddensheets;

import cn.idev.excel.ExcelReader;
import cn.idev.excel.FastExcel;
import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.test.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class HiddenSheetsTest {

    private static File file07;
    private static File file03;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.readFile("hiddensheets" + File.separator + "hiddensheets.xlsx");
        file03 = TestFileUtil.readFile("hiddensheets" + File.separator + "hiddensheets.xls");
    }

    @Test
    public void t01Read07() {
        read(file07, null);
        read(file07, Boolean.FALSE);
        read(file07, Boolean.TRUE);
    }

    @Test
    public void t02Read03() {
        read(file03, null);
        read(file03, Boolean.FALSE);
        read(file03, Boolean.TRUE);
    }

    @Test
    public void t03Read07All() {
        readAll(file07, null);
        readAll(file07, Boolean.FALSE);
        readAll(file07, Boolean.TRUE);
    }

    @Test
    public void t04Read03All() {
        readAll(file03, null);
        readAll(file03, Boolean.FALSE);
        readAll(file03, Boolean.TRUE);
    }

    @Test
    public void t05ReadHiddenList() {
        readHiddenList(file03);
        readHiddenList(file07);
    }
    
    private void readHiddenList(File file) {
        try (ExcelReader excelReader = FastExcel.read(file, HiddenSheetsData.class, new HiddenSheetsListener()).build()) {
            List<ReadSheet> allSheetList = excelReader.excelExecutor().sheetList();
            Assertions.assertEquals(2, allSheetList.stream().filter(ReadSheet::isHidden).count());
            Assertions.assertEquals(1, allSheetList.stream().filter(ReadSheet::isVeryHidden).count());
            Assertions.assertEquals("Sheet5", allSheetList.stream().filter(ReadSheet::isVeryHidden)
                .findFirst().get().getSheetName());
        }
    }

    private void read(File file, Boolean ignoreHidden) {
        try (ExcelReader excelReader = FastExcel.read(file, HiddenSheetsData.class, new HiddenSheetsListener())
            .ignoreHiddenSheet(ignoreHidden).build()) {
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            if (Boolean.TRUE.equals(ignoreHidden)) {
                Assertions.assertEquals(3, sheets.size());
            } else {
                Assertions.assertEquals(6, sheets.size());
            }
        }
    }

    private void readAll(File file, Boolean ignoreHidden) {
        List<HiddenSheetsData> dataList = FastExcel.read(file, HiddenSheetsData.class, new HiddenSheetsListener())
            .ignoreHiddenSheet(ignoreHidden)
            .doReadAllSync();
        if (Boolean.TRUE.equals(ignoreHidden)) {
            Assertions.assertEquals(3, dataList.size());
        } else {
            Assertions.assertEquals(6, dataList.size());
        }
    }

}
