package org.apache.fesod.excel.demo.read;

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
public class DemoAutoHeadNameData {
    @ExcelProperty(value = "String",headConverter = DemoAutoHeadNameConverter.class)
    private String string;
}
