package cn.idev.excel.test.temp.bug;

import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;

/**
 */
public class ExcelCreat {

    public static void main(String[] args) throws FileNotFoundException {
        List<DataType> data = getData();
        ExcelWriter excelWriter = null;
        excelWriter = FastExcel.write(new FileOutputStream("all.xlsx")).build();
        WriteSheet writeSheet =
                FastExcel.writerSheet(1, "test").head(HeadType.class).build();
        excelWriter.write(data, writeSheet);
        excelWriter.finish();
    }

    private static List<DataType> getData() {
        DataType vo = new DataType();
        vo.setId(738);
        vo.setFirstRemark("1222");
        vo.setSecRemark("22222");
        return Collections.singletonList(vo);
    }
}
