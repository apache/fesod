package cn.idev.excel.write.handler.impl;

import cn.idev.excel.annotation.write.style.HeadStyle;
import cn.idev.excel.metadata.property.StyleProperty;
import cn.idev.excel.util.BooleanUtils;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import cn.idev.excel.write.property.ExcelWriteHeadProperty;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Hides columns in the sheet.
 *
 * @see HeadStyle#hidden()
 */
public class HiddenShellWriteHandler implements SheetWriteHandler {

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        ExcelWriteHeadProperty excelWriteHeadProperty = writeWorkbookHolder.getExcelWriteHeadProperty();
        if (excelWriteHeadProperty != null) {
            excelWriteHeadProperty.getHeadMap().forEach((key, value) -> {
                if (null != value) {
                    StyleProperty headStyleProperty = value.getHeadStyleProperty();
                    if (null != headStyleProperty) {
                        if (BooleanUtils.isTrue(headStyleProperty.getHidden())) {
                            Sheet sheet = writeSheetHolder.getSheet();
                            sheet.setColumnHidden(key, true);
                        }
                    }
                }
            });
        }
    }
}
