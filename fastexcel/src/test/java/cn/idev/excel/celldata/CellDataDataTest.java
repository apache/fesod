package cn.idev.excel.celldata;

import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.metadata.data.FormulaData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.util.DateUtils;
import cn.idev.excel.util.TestFileUtil;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class CellDataDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;
    private static File file07_formula;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("cellData07.xlsx");
        file03 = TestFileUtil.createNewFile("cellData03.xls");
        fileCsv = TestFileUtil.readFile("cellDataCsv.csv");
        file07_formula = TestFileUtil.readFile("celldata" + File.separator + "celldata_formula.xlsx");
    }

    @Test
    public void t01ReadAndWrite07() throws Exception {
        readAndWrite(file07);
    }

    @Test
    public void t02ReadAndWrite03() throws Exception {
        readAndWrite(file03);
    }

    @Test
    public void t03ReadAndWriteCsv() throws Exception {
        readAndWrite(fileCsv);
    }

    @Test
    public void t04ReadFormula07() throws Exception {
        FastExcel.read(file07_formula, CellDataReadData.class, new AnalysisEventListener<CellDataReadData>() {
                    @Override
                    public void invoke(CellDataReadData data, AnalysisContext context) {
                        Assertions.assertNotNull(
                                data.getFormulaValue().getFormulaData().getFormulaValue());
                        log.info(
                                "row formula: {}",
                                data.getFormulaValue().getFormulaData().getFormulaValue());
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .doReadAll();
    }

    private void readAndWrite(File file) throws Exception {
        FastExcel.write(file, CellDataWriteData.class).sheet().doWrite(data());
        FastExcel.read(file, CellDataReadData.class, new CellDataDataListener())
                .sheet()
                .doRead();
    }

    private List<CellDataWriteData> data() throws Exception {
        List<CellDataWriteData> list = new ArrayList<>();
        CellDataWriteData cellDataData = new CellDataWriteData();
        cellDataData.setDate(new WriteCellData<>(DateUtils.parseDate("2020-01-01 01:01:01")));
        WriteCellData<Integer> integer1 = new WriteCellData<>();
        integer1.setType(CellDataTypeEnum.NUMBER);
        integer1.setNumberValue(BigDecimal.valueOf(2L));
        cellDataData.setInteger1(integer1);
        cellDataData.setInteger2(2);
        WriteCellData<?> formulaValue = new WriteCellData<>();
        FormulaData formulaData = new FormulaData();
        formulaValue.setFormulaData(formulaData);
        formulaData.setFormulaValue("B2+C2");
        cellDataData.setFormulaValue(formulaValue);
        list.add(cellDataData);
        return list;
    }
}
