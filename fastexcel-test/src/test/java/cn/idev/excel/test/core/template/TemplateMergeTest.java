package cn.idev.excel.test.core.template;

import cn.idev.excel.FastExcel;
import cn.idev.excel.test.util.TestFileUtil;
import cn.idev.excel.write.metadata.fill.FillConfig;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author wangmeng
 */
public class TemplateMergeTest {

    private static File file01;

    @BeforeAll
    public static void init() {
        file01 = TestFileUtil.createNewFile("template" + File.separator + "template_out01.xlsx");
    }

    @Test
    public void testMerge() throws IOException {
        write(file01);
    }

    public static void write(File file) {
        FastExcel.write(file)
                .withTemplate(TestFileUtil.readFile("template" + File.separator + "template01.xlsx"))
                .sheet()
                .doFill(
                        data(),
                        FillConfig.builder().forceNewRow(true).autoStyle(true).build());
    }

    private static List<Map<String, String>> data() {
        List<Map<String, String>> list = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("name", "name- " + i);
            map.put("number", String.valueOf(i));
            map.put("age", String.valueOf(i));
            map.put("orderNo", "order-" + i);
            map.put("status", "1");
            list.add(map);
        }
        return list;
    }
}
