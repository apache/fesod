---
id: 'large-data'
title: 'Large Data'
---

# Big Data Files

## Reading

### Overview

When reading files larger than 10 MB, Microsoft Excel 03 is unable to handle them and consumes significantly more
memory. Microsoft Excel 2007 introduces the concept
of [Shared Strings](https://learn.microsoft.com/en-us/office/open-xml/spreadsheet/working-with-the-shared-string-table),
which can consume a significant amount of memory.
If all shared strings are loaded into memory, the memory usage can be approximately 3 to 10 times the size of the spreadsheet
file. Therefore, FesodSheet uses a strategy of first storing the file and then deserialising it to read the data,
thereby
saving memory. Of course, after deserialising the file, efficiency will decrease by approximately 30-50% (this is not
fixed and depends on the hit rate, which may exceed 100%).

If the read efficiency is acceptable, use the default setting. The permanent memory usage (for the entire process of
reading a single spreadsheet file) generally does not exceed 50MB (most likely around 30MB), and the remaining temporary
memory is quickly recycled by the garbage collector.

### Default Policy

By default, large file processing is automatically determined. Shared strings under 5M are stored in memory, which
occupies approximately 15-50M of memory. Strings over 5M are stored in files, and file storage also requires additional
memory to store temporary shared strings, with a default of 20M. Apart from the memory used by shared strings, other
memory usage is minimal, so it can be estimated at 10MB. Therefore, the default of approximately 30MB is sufficient to
read an extremely large file.

### Configure Memory

If you want to customise the settings, first determine how much memory you are willing to allocate for reading a very
large spreadsheet file.
For example, if you want the spreadsheet file to occupy a maximum of 100MB of memory during the reading process (this is the
permanent memory usage during reading, not including memory immediately reclaimed by the young generation), then set the
size of the shared string stored in the file to 20MB (less than 20MB is stored in memory, more than 20MB is stored in a
temporary file), and then set the memory size occupied by the temporary shared string when stored in the file to
approximately 90MB.

If the maximum number of file entries is only a few hundred thousand, and the spreadsheet file is only a few dozen megabytes,
and there is no high concurrency, and memory is sufficient,

```java
// Force memory storage, so a 20M spreadsheet file will use 150M of memory (many temporary objects, so 100M will be constantly GC'd).
// This will be much more efficient than the complex strategy above.
// Just to clarify, all I did was add the readCache(new MapCache()) parameter. For the rest, refer to other examples.
FesodSheet.read().

readCache(new MapCache());
```

High concurrency requirements, and often involve extremely large files.

```java
// The first parameter specifies how many MB of shared string data will be stored in the file (default is 5 MB).
// The second parameter specifies how many MB of cache data will be stored in memory when using file storage (default is 20 MB).
// For example, if you want to use 100 MB of memory (this refers to permanent memory usage during parsing, excluding temporary objects) to parse a spreadsheet file, the calculation shows it to be approximately 20 MB + 90 MB, so set the parameters to: 20 and 90.
// Here's a clarification: the only addition is the readCacheSelector(new SimpleReadCacheSelector(5, 20)) parameter; the rest follows the examples provided.
FesodSheet.read().

readCacheSelector(new SimpleReadCacheSelector(5, 20));
```

### About maxCacheActivateSize

When using file storage, FesodSheet splits the shared string into batches of **1000** items and then stores them in file
storage. Spreadsheets typically read shared strings in sequential order, so by default, 1000 entries (approximately 20MB) are
kept in memory. If a match is found, the data is returned directly; if not, the file is read. Therefore, the size should
not be set too small, as this makes it difficult to find a match and results in constant file reads. Conversely, setting
it too large can consume excessive memory.

To determine whether `maxCacheActivateSize` needs adjustment, enabling `debug` logs will output `Already put :4000000`
as the last value, which can be estimated to be 4 million. Then, checking `Cache misses count:4001` yields a value of
4,000. `4 million / 4,000 = 1,000` indicates that `maxCacheActivateSize` is already very reasonable. If the value is
less than 500, there is a significant issue. Values between 500 and 1000 should be acceptable.
