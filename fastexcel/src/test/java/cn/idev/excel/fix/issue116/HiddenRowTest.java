package cn.idev.excel.fix.issue116;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.HeadStyle;
import cn.idev.excel.enums.BooleanEnum;
import cn.idev.excel.test.util.TestFileUtil;
import cn.idev.excel.write.handler.impl.HiddenRowWriteHandler;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

/**
 * @see HiddenRowWriteHandler
 */
public class HiddenRowTest {

    @Test
    public void test() {
        String fileName = TestFileUtil.getPath() + "hiddenRowTest" + System.currentTimeMillis() + ".xlsx";
        List<DemoModel> dataList = new ArrayList<>();
        HiddenRowWriteHandler hiddenRowWriteHandler = new HiddenRowWriteHandler();
        for (int i = 2; i <= 50; i++) {
            String category = "我是姓名" + i;
            DemoModel exportModel = new DemoModel(category, i, "test-" + i);
            dataList.add(exportModel);
            if (i % 5 == 0) {
                hiddenRowWriteHandler.addHiddenColumns(i - 2);
            }
        }
        EasyExcel.write(fileName, DemoModel.class)
                .sheet("模板")
                .registerWriteHandler(hiddenRowWriteHandler)
                .doWrite(dataList);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DemoModel {

        @ExcelProperty("名字")
        @HeadStyle(hidden = BooleanEnum.TRUE)
        private String name;

        @ExcelProperty("年龄")
        private Integer age;

        @ExcelProperty("test")
        private String test;
    }
}
