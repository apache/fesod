package cn.idev.excel.test.demo.read;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Read header
 *
 *
 */
@Slf4j
public class CellDataDemoHeadDataListener implements ReadListener<CellDataReadDemoData> {
    /**
     * Store data to database every 5 records. In actual use, it can be 100 records, then clear the list to facilitate memory recycling
     */
    private static final int BATCH_COUNT = 100;

    private List<CellDataReadDemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    @Override
    public void invoke(CellDataReadDemoData data, AnalysisContext context) {
        log.info("Parsed one record: {}", JSON.toJSONString(data));
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("All data parsing completed!");
    }

    /**
     * Save data to database
     */
    private void saveData() {
        log.info("{} records, starting to save to database!", cachedDataList.size());
        log.info("Successfully saved to database!");
    }
}
