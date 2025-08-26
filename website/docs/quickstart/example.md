---
id: 'simple-example'
title: 'Simple example'
---

# Simple example

## Read

Below is an example of reading an Excel document:

```java
// Implement the ReadListener interface to set up operations for reading data
public class DemoDataListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        System.out.println("Parsed a data entry" + JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("All data parsed!");
    }
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // Read Excel file
    FastExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
}
```

### Write

Below is a simple example of creating an Excel document:

```java
// Sample data class
public class DemoData {
    @ExcelProperty("String Title")
    private String string;
    @ExcelProperty("Date Title")
    private Date date;
    @ExcelProperty("Number Title")
    private Double doubleData;
    @ExcelIgnore
    private String ignore;
}

// Prepare data to write
private static List<DemoData> data() {
    List<DemoData> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        DemoData data = new DemoData();
        data.setString("String" + i);
        data.setDate(new Date());
        data.setDoubleData(0.56);
        list.add(data);
    }
    return list;
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // Create a "Template" sheet and write data
    FastExcel.write(fileName, DemoData.class).sheet("Template").doWrite(data());
}
```
