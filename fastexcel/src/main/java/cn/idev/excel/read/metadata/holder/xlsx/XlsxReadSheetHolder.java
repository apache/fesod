package cn.idev.excel.read.metadata.holder.xlsx;

import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.read.metadata.holder.ReadSheetHolder;
import cn.idev.excel.read.metadata.holder.ReadWorkbookHolder;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;

/**
 * sheet holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class XlsxReadSheetHolder extends ReadSheetHolder {
    /**
     * Record the label of the current operation to prevent NPE.
     */
    private Deque<String> tagDeque;
    /**
     * Current Column
     */
    private Integer columnIndex;
    /**
     * Data for current label.
     */
    private StringBuilder tempData;
    /**
     * Formula for current label.
     */
    private StringBuilder tempFormula;
    /**
     * Shared index for current label.
     */
    private String tempSharedIndex;
    /**
     * Shared formula for current sheet.
     */
    private Map<String, String> sharedFormula;

    /**
     * excel Relationship
     */
    private PackageRelationshipCollection packageRelationshipCollection;

    public XlsxReadSheetHolder(ReadSheet readSheet, ReadWorkbookHolder readWorkbookHolder) {
        super(readSheet, readWorkbookHolder);
        this.tagDeque = new LinkedList<String>();
        this.sharedFormula = new ConcurrentHashMap<>();
        packageRelationshipCollection = ((XlsxReadWorkbookHolder) readWorkbookHolder)
                .getPackageRelationshipCollectionMap()
                .get(readSheet.getSheetNo());
    }
}
