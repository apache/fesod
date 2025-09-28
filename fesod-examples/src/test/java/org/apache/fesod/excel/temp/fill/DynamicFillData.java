package org.apache.fesod.excel.temp.fill;

import lombok.Data;
import org.apache.fesod.excel.annotation.fill.DynamicColumn;

import java.util.Map;

@Data
public class DynamicFillData {
    private String name;
    private double number;
    @DynamicColumn()
    private Map<String,String> qtyMap;

    @DynamicColumn()
    private Map<String,DynamicFillDataObj> priceMap;
}
