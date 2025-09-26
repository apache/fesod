package cn.idev.excel.fix.issue116;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.HeadStyle;
import cn.idev.excel.enums.BooleanEnum;
import cn.idev.excel.test.util.TestFileUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

/**
 * @see cn.idev.excel.write.handler.impl.HiddenShellWriteHandler
 */
public class HiddenShellTest {

    @Test
    public void test() {
        String fileName = TestFileUtil.getPath() + "hiddenShellTest" + System.currentTimeMillis() + ".xlsx";
        EasyExcel.write(fileName, DemoModel.class).sheet("模板").doWrite(listDemoModel());
    }

    public static List<DemoModel> listDemoModel() {
        List<DemoModel> dataList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String category = "我是姓名" + i;
            DemoModel exportModel = new DemoModel(category, i, "test" + i);
            dataList.add(exportModel);
        }
        return dataList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DemoModel {

        @ExcelProperty("名字")
        private String name;

        @ExcelProperty("年龄")
        @HeadStyle(hidden = BooleanEnum.TRUE)
        private Integer age;

        @ExcelProperty("test")
        private String test;
    }
}
