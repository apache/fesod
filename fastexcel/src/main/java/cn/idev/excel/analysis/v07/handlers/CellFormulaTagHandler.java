package cn.idev.excel.analysis.v07.handlers;

import cn.idev.excel.constant.ExcelXmlConstants;
import cn.idev.excel.context.xlsx.XlsxReadContext;
import cn.idev.excel.metadata.data.FormulaData;
import cn.idev.excel.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import cn.idev.excel.util.StringUtils;
import org.xml.sax.Attributes;

/**
 * Cell formula tag handler
 */
public class CellFormulaTagHandler extends AbstractXlsxTagHandler {

    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        xlsxReadSheetHolder.setTempFormula(new StringBuilder());
        xlsxReadSheetHolder.setTempSharedIndex(null);

        // shared formula
        String t = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_T);
        if (StringUtils.isBlank(t) || !ExcelXmlConstants.ATTRIBUTE_SHARED.equals(t)) {
            return;
        }
        String si = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_SHARED_INDEX);
        if (StringUtils.isBlank(si)) {
            return;
        }
        xlsxReadSheetHolder.setTempSharedIndex(si);
    }

    @Override
    public void endElement(XlsxReadContext xlsxReadContext, String name) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();

        String formulaValue = xlsxReadSheetHolder.getTempFormula().toString();
        String sharedIndex = xlsxReadSheetHolder.getTempSharedIndex();

        if (StringUtils.isNotBlank(sharedIndex)) {
            if (StringUtils.isNotBlank(formulaValue)) {
                xlsxReadSheetHolder.getSharedFormula().putIfAbsent(sharedIndex, formulaValue);
            } else {
                formulaValue = xlsxReadSheetHolder.getSharedFormula().get(sharedIndex);
            }
        }

        FormulaData formulaData = new FormulaData();
        formulaData.setFormulaValue(formulaValue);
        xlsxReadSheetHolder.getTempCellData().setFormulaData(formulaData);
    }

    @Override
    public void characters(XlsxReadContext xlsxReadContext, char[] ch, int start, int length) {
        xlsxReadContext.xlsxReadSheetHolder().getTempFormula().append(ch, start, length);
    }
}
