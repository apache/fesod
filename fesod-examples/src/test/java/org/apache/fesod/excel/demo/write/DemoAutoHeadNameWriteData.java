package org.apache.fesod.excel.demo.write;

import lombok.Data;
import lombok.ToString;
import org.apache.fesod.excel.annotation.ExcelProperty;

/**
 * TODO
 *
 * @author GGBOUD
 * @date 2025/10/14
 */
@ToString
@Data
public class DemoAutoHeadNameWriteData {
    @ExcelProperty(value = "String",headConverter = DemoAutoHeadNameWriterConverter.class)
    private String string;

    @ExcelProperty(value = "name")
    private String name;

}
