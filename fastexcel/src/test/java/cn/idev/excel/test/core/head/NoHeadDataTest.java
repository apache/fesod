package cn.idev.excel.test.core.head;

import cn.idev.excel.FastExcel;
import cn.idev.excel.test.util.TestFileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class NoHeadDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("noHead07.xlsx");
        file03 = TestFileUtil.createNewFile("noHead03.xls");
        fileCsv = TestFileUtil.createNewFile("noHeadCsv.csv");
    }

    @Test
    public void t01ReadAndWrite07() {
        readAndWrite(file07);
    }

    @Test
    public void t02ReadAndWrite03() {
        readAndWrite(file03);
    }

    @Test
    public void t03ReadAndWriteCsv() {
        readAndWrite(fileCsv);
    }

    private void readAndWrite(File file) {
        FastExcel.write(file, NoHeadData.class).needHead(Boolean.FALSE).sheet().doWrite(data());
        FastExcel.read(file, NoHeadData.class, new NoHeadDataListener())
                .headRowNumber(0)
                .sheet()
                .doRead();
    }

    private List<NoHeadData> data() {
        List<NoHeadData> list = new ArrayList<NoHeadData>();
        NoHeadData data = new NoHeadData();
        data.setString("字符串0");
        list.add(data);
        return list;
    }
}
