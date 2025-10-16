package org.apache.fesod.excel.demo.read;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.listener.ReadListener;

/**
 * TODO
 *
 * @author GGBOUD
 * @date 2025/10/14
 */
@Slf4j
public class HeadNameConvertListener implements ReadListener<DemoAutoHeadNameData> {
    @Override
    public void invoke(DemoAutoHeadNameData data, AnalysisContext context) {
        log.info("read data:{}",data);
        log.info("read data:{},{}",context.getTotalCount(),context.getCurrentRowNum());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
