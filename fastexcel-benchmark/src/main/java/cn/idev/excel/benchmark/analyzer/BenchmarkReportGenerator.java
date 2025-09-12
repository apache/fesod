package cn.idev.excel.benchmark.analyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates structured reports from benchmark analysis results
 */
public class BenchmarkReportGenerator {

    /**
     * Generate JSON report
     */
    public void generateJsonReport(ComparisonAnalysis analysis, Path outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath.toFile()))) {
            writeJsonReport(analysis, writer);
        }
    }

    /**
     * Write JSON report manually (without Jackson dependency)
     */
    private void writeJsonReport(ComparisonAnalysis analysis, PrintWriter writer) {
        writer.println("{");
        writer.println("  \"metadata\": {");
        writer.printf("    \"generatedAt\": \"%s\",%n", LocalDateTime.now().toString());
        writer.println("    \"reportType\": \"BenchmarkComparison\",");
        writer.println("    \"version\": \"1.0\"");
        writer.println("  },");
        writer.println("  \"comparisons\": [");

        List<ComparisonAnalysis.ComparisonResult> comparisons = analysis.getComparisons();
        for (int i = 0; i < comparisons.size(); i++) {
            ComparisonAnalysis.ComparisonResult comparison = comparisons.get(i);
            writer.println("    {");
            writer.printf("      \"library1\": \"%s\",%n", escapeJson(comparison.library1));
            writer.printf("      \"library2\": \"%s\",%n", escapeJson(comparison.library2));
            writer.printf("      \"operation\": \"%s\",%n", escapeJson(comparison.operation));
            writer.printf("      \"throughputRatio\": %.3f,%n", comparison.throughputRatio);
            writer.printf("      \"throughputSignificant\": %s,%n", comparison.throughputSignificant);
            writer.printf("      \"memoryRatio\": %.3f,%n", comparison.memoryRatio);
            writer.printf("      \"memorySignificant\": %s,%n", comparison.memorySignificant);
            writer.printf("      \"timeRatio\": %.3f,%n", comparison.timeRatio);
            writer.printf("      \"timeSignificant\": %s,%n", comparison.timeSignificant);
            writer.printf("      \"score1\": %.2f,%n", comparison.score1);
            writer.printf("      \"score2\": %.2f,%n", comparison.score2);
            writer.printf("      \"winner\": \"%s\",%n", escapeJson(comparison.winner));
            writer.printf("      \"recommendation\": \"%s\"%n", escapeJson(comparison.recommendation));
            writer.print("    }");
            if (i < comparisons.size() - 1) {
                writer.println(",");
            } else {
                writer.println();
            }
        }

        writer.println("  ]");
        writer.println("}");
    }

    /**
     * Escape JSON strings
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Generate CSV report
     */
    public void generateCsvReport(ComparisonAnalysis analysis, Path outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath.toFile()))) {
            // Write CSV header
            writer.println("Library1,Library2,Operation,ThroughputRatio,ThroughputSignificant,"
                    + "MemoryRatio,MemorySignificant,TimeRatio,TimeSignificant,"
                    + "Score1,Score2,Winner,Recommendation");

            // Write comparison data
            for (ComparisonAnalysis.ComparisonResult comparison : analysis.getComparisons()) {
                writer.printf(
                        "%s,%s,%s,%.3f,%s,%.3f,%s,%.3f,%s,%.2f,%.2f,%s,\"%s\"%n",
                        comparison.library1,
                        comparison.library2,
                        comparison.operation,
                        comparison.throughputRatio,
                        comparison.throughputSignificant,
                        comparison.memoryRatio,
                        comparison.memorySignificant,
                        comparison.timeRatio,
                        comparison.timeSignificant,
                        comparison.score1,
                        comparison.score2,
                        comparison.winner,
                        comparison.recommendation.replace("\"", "\"\"") // Escape quotes in CSV
                        );
            }
        }
    }

    /**
     * Generate HTML report
     */
    public void generateHtmlReport(ComparisonAnalysis analysis, BenchmarkResultCollector collector, Path outputPath)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath.toFile()))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("    <title>Benchmark Results Report</title>");
            writer.println("    <style>");
            writer.println(getHtmlStyles());
            writer.println("    </style>");
            writer.println("    <script>");
            writer.println(getTableSortingScript());
            writer.println("    </script>");
            writer.println("</head>");
            writer.println("<body>");

            // Header
            writer.println("    <div class=\"header\">");
            writer.println("        <h1>FastExcel vs Apache POI Benchmark Results</h1>");
            writer.printf(
                    "        <p>Generated on: %s</p>%n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("    </div>");

            // Executive Summary
            writer.println("    <div class=\"section\">");
            writer.println("        <h2>Executive Summary</h2>");
            generateExecutiveSummary(analysis, collector, writer);
            writer.println("    </div>");

            // Detailed Statistics
            writer.println("    <div class=\"section\">");
            writer.println("        <h2>Detailed Statistics</h2>");
            generateStatisticsTable(collector, writer);
            writer.println("    </div>");

            // Comparison Results
            writer.println("    <div class=\"section\">");
            writer.println("        <h2>Performance Comparisons</h2>");
            generateComparisonTable(analysis, writer);
            writer.println("    </div>");

            writer.println("</body>");
            writer.println("</html>");
        }
    }

    /**
     * Generate executive summary for HTML report
     */
    private void generateExecutiveSummary(
            ComparisonAnalysis analysis, BenchmarkResultCollector collector, PrintWriter writer) {
        List<ComparisonAnalysis.ComparisonResult> comparisons = analysis.getComparisons();

        if (comparisons.isEmpty()) {
            writer.println("        <p>No comparison data available.</p>");
            return;
        }

        // Find overall winner across all operations
        Map<String, Integer> wins = new HashMap<>();
        for (ComparisonAnalysis.ComparisonResult comparison : comparisons) {
            if (!"Tie".equals(comparison.winner)) {
                wins.put(comparison.winner, wins.getOrDefault(comparison.winner, 0) + 1);
            }
        }

        String overallWinner = wins.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No clear winner");

        writer.printf("        <p><strong>Overall Winner:</strong> %s</p>%n", overallWinner);
        writer.printf("        <p><strong>Total Comparisons:</strong> %d</p>%n", comparisons.size());
    }

    /**
     * Generate statistics table for HTML report
     */
    private void generateStatisticsTable(BenchmarkResultCollector collector, PrintWriter writer) {
        Map<String, BenchmarkStatistics> stats = collector.getAggregatedStatistics();

        writer.println("        <table id=\"statsTable\" class=\"stats-table sortable\">");
        writer.println("            <thead>");
        writer.println("                <tr>");
        writer.println("                    <th onclick=\"sortTable(0)\">Library ↕</th>");
        writer.println("                    <th onclick=\"sortTable(1)\">Operation ↕</th>");
        writer.println("                    <th onclick=\"sortTable(2)\">Dataset Size ↕</th>");
        writer.println("                    <th onclick=\"sortTable(3)\">File Format ↕</th>");
        writer.println("                    <th onclick=\"sortTable(4)\">Avg Throughput (rows/sec) ↕</th>");
        writer.println("                    <th onclick=\"sortTable(5)\">Avg Execution Time (ms) ↕</th>");
        writer.println("                    <th onclick=\"sortTable(6)\">Peak Memory (MB) ↕</th>");
        writer.println("                    <th onclick=\"sortTable(7)\">Performance Score ↕</th>");
        writer.println("                    <th onclick=\"sortTable(8)\">Stable ↕</th>");
        writer.println("                </tr>");
        writer.println("            </thead>");
        writer.println("            <tbody>");

        for (BenchmarkStatistics stat : stats.values()) {
            writer.println("                <tr>");
            writer.printf("                    <td>%s</td>%n", stat.library);
            writer.printf("                    <td>%s</td>%n", stat.operation);
            writer.printf("                    <td>%s</td>%n", stat.datasetSize != null ? stat.datasetSize : "N/A");
            writer.printf("                    <td>%s</td>%n", stat.fileFormat != null ? stat.fileFormat : "N/A");
            writer.printf("                    <td>%.2f</td>%n", stat.throughputStats.mean);
            writer.printf("                    <td>%.2f</td>%n", stat.executionTimeStats.mean);
            writer.printf("                    <td>%.2f</td>%n", stat.peakMemoryStats.mean);
            writer.printf("                    <td>%.1f</td>%n", stat.getPerformanceScore());
            writer.printf(
                    "                    <td class=\"%s\">%s</td>%n",
                    stat.isStable() ? "stable" : "unstable", stat.isStable() ? "Yes" : "No");
            writer.println("                </tr>");
        }

        writer.println("            </tbody>");
        writer.println("        </table>");
    }

    /**
     * Generate comparison table for HTML report
     */
    private void generateComparisonTable(ComparisonAnalysis analysis, PrintWriter writer) {
        writer.println("        <table class=\"comparison-table\">");
        writer.println("            <thead>");
        writer.println("                <tr>");
        writer.println("                    <th>Comparison</th>");
        writer.println("                    <th>Operation</th>");
        writer.println("                    <th>Throughput Ratio</th>");
        writer.println("                    <th>Memory Ratio</th>");
        writer.println("                    <th>Winner</th>");
        writer.println("                    <th>Recommendation</th>");
        writer.println("                </tr>");
        writer.println("            </thead>");
        writer.println("            <tbody>");

        for (ComparisonAnalysis.ComparisonResult comparison : analysis.getComparisons()) {
            writer.println("                <tr>");
            writer.printf("                    <td>%s vs %s</td>%n", comparison.library1, comparison.library2);
            writer.printf("                    <td>%s</td>%n", comparison.operation);
            writer.printf(
                    "                    <td class=\"%s\">%.2fx %s</td>%n",
                    comparison.throughputSignificant ? "significant" : "not-significant",
                    comparison.throughputRatio,
                    comparison.throughputSignificant ? "(sig)" : "");
            writer.printf(
                    "                    <td class=\"%s\">%.2fx %s</td>%n",
                    comparison.memorySignificant ? "significant" : "not-significant",
                    comparison.memoryRatio,
                    comparison.memorySignificant ? "(sig)" : "");
            writer.printf("                    <td class=\"winner\">%s</td>%n", comparison.winner);
            writer.printf("                    <td class=\"recommendation\">%s</td>%n", comparison.recommendation);
            writer.println("                </tr>");
        }

        writer.println("            </tbody>");
        writer.println("        </table>");
    }

    /**
     * Get HTML styles for the report
     */
    private String getHtmlStyles() {
        return "body {\n" + "    font-family: Arial, sans-serif;\n"
                + "    margin: 20px;\n"
                + "    line-height: 1.6;\n"
                + "}\n"
                + ".header {\n"
                + "    background-color: #f4f4f4;\n"
                + "    padding: 20px;\n"
                + "    border-radius: 5px;\n"
                + "    margin-bottom: 20px;\n"
                + "}\n"
                + ".section {\n"
                + "    margin-bottom: 30px;\n"
                + "}\n"
                + "table {\n"
                + "    border-collapse: collapse;\n"
                + "    width: 100%;\n"
                + "    margin-bottom: 20px;\n"
                + "}\n"
                + "th, td {\n"
                + "    border: 1px solid #ddd;\n"
                + "    padding: 8px 12px;\n"
                + "    text-align: left;\n"
                + "}\n"
                + "th {\n"
                + "    background-color: #f2f2f2;\n"
                + "    font-weight: bold;\n"
                + "    cursor: pointer;\n"
                + "    user-select: none;\n"
                + "}\n"
                + "th:hover {\n"
                + "    background-color: #e8e8e8;\n"
                + "}\n"
                + ".sortable th {\n"
                + "    position: relative;\n"
                + "}\n"
                + ".stable {\n"
                + "    color: green;\n"
                + "    font-weight: bold;\n"
                + "}\n"
                + ".unstable {\n"
                + "    color: orange;\n"
                + "    font-weight: bold;\n"
                + "}\n"
                + ".significant {\n"
                + "    background-color: #e8f5e8;\n"
                + "    font-weight: bold;\n"
                + "}\n"
                + ".not-significant {\n"
                + "    background-color: #f5f5f5;\n"
                + "    color: #666;\n"
                + "}\n"
                + ".winner {\n"
                + "    font-weight: bold;\n"
                + "    color: #2c5aa0;\n"
                + "}\n"
                + ".recommendation {\n"
                + "    font-style: italic;\n"
                + "    max-width: 300px;\n"
                + "}\n"
                + ".recommendations {\n"
                + "    background-color: #f9f9f9;\n"
                + "    padding: 15px;\n"
                + "    border-radius: 5px;\n"
                + "    border-left: 4px solid #2c5aa0;\n"
                + "}\n"
                + "h1, h2, h3 {\n"
                + "    color: #2c5aa0;\n"
                + "}";
    }

    /**
     * Get table sorting JavaScript for the report
     */
    private String getTableSortingScript() {
        return "function sortTable(columnIndex) {\n" + "    const table = document.getElementById('statsTable');\n"
                + "    const tbody = table.getElementsByTagName('tbody')[0];\n"
                + "    const rows = Array.from(tbody.getElementsByTagName('tr'));\n"
                + "    \n"
                + "    // Determine sort direction\n"
                + "    const isAscending = table.getAttribute('data-sort-direction') !== 'asc';\n"
                + "    table.setAttribute('data-sort-direction', isAscending ? 'asc' : 'desc');\n"
                + "    \n"
                + "    // Sort rows\n"
                + "    rows.sort((a, b) => {\n"
                + "        const aValue = a.getElementsByTagName('td')[columnIndex].textContent.trim();\n"
                + "        const bValue = b.getElementsByTagName('td')[columnIndex].textContent.trim();\n"
                + "        \n"
                + "        // Try to parse as number for numeric columns\n"
                + "        if (columnIndex >= 2 && columnIndex <= 5) {\n"
                + "            const aNum = parseFloat(aValue);\n"
                + "            const bNum = parseFloat(bValue);\n"
                + "            if (!isNaN(aNum) && !isNaN(bNum)) {\n"
                + "                return isAscending ? aNum - bNum : bNum - aNum;\n"
                + "            }\n"
                + "        }\n"
                + "        \n"
                + "        // String comparison for text columns\n"
                + "        if (isAscending) {\n"
                + "            return aValue.localeCompare(bValue);\n"
                + "        } else {\n"
                + "            return bValue.localeCompare(aValue);\n"
                + "        }\n"
                + "    });\n"
                + "    \n"
                + "    // Clear tbody and re-append sorted rows\n"
                + "    tbody.innerHTML = '';\n"
                + "    rows.forEach(row => tbody.appendChild(row));\n"
                + "    \n"
                + "    // Update header indicators\n"
                + "    const headers = table.getElementsByTagName('th');\n"
                + "    for (let i = 0; i < headers.length; i++) {\n"
                + "        const header = headers[i];\n"
                + "        const text = header.textContent.replace(' ↑', '').replace(' ↓', '').replace(' ↕', '');\n"
                + "        if (i === columnIndex) {\n"
                + "            header.textContent = text + (isAscending ? ' ↑' : ' ↓');\n"
                + "        } else {\n"
                + "            header.textContent = text + ' ↕';\n"
                + "        }\n"
                + "    }\n"
                + "}";
    }
}
