package org.apache.fesod.excel.write.metadata.fill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DynamicColumnInfo {

    /**
     * dynamic column keys
     * */
    private List<String> keys;

    /**
     * dynamic column group size
     * */
    private Integer groupSize;

}
