package cn.idev.excel.test.core.validation;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.validation.ExcelRequired;
import cn.idev.excel.util.ExcelValidationUtils;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for ExcelValidationUtils.
 *
 * @author FastExcel Team
 * @since 1.2.1
 */
public class ExcelValidationUtilsTest {

    @Data
    public static class TestData {
        @ExcelProperty("Name")
        @ExcelRequired(message = "Name is required")
        private String name;
        
        @ExcelProperty("Age")
        private Integer age;
        
        @ExcelProperty("Email")
        @ExcelRequired(message = "Email is required")
        private String email;
    }

    @Test
    public void testValidateRequired_ValidData() {
        TestData data = new TestData();
        data.setName("John Doe");
        data.setAge(25);
        data.setEmail("john@example.com");
        
        List<String> errors = ExcelValidationUtils.validateRequired(data);
        Assertions.assertTrue(errors.isEmpty(), "Valid data should pass validation");
    }

    @Test
    public void testValidateRequired_MissingRequiredFields() {
        TestData data = new TestData();
        data.setAge(25); // Only set non-required field
        
        List<String> errors = ExcelValidationUtils.validateRequired(data);
        Assertions.assertEquals(2, errors.size(), "Should have 2 validation errors");
        
        boolean hasNameError = errors.stream().anyMatch(error -> error.contains("Name"));
        boolean hasEmailError = errors.stream().anyMatch(error -> error.contains("Email"));
        
        Assertions.assertTrue(hasNameError, "Should have name validation error");
        Assertions.assertTrue(hasEmailError, "Should have email validation error");
    }

    @Test
    public void testValidateRequiredBatch() {
        List<TestData> dataList = new ArrayList<>();
        
        // Valid data
        TestData validData = new TestData();
        validData.setName("John");
        validData.setEmail("john@example.com");
        dataList.add(validData);
        
        // Invalid data
        TestData invalidData = new TestData();
        invalidData.setAge(30); // Missing required fields
        dataList.add(invalidData);
        
        List<ExcelValidationUtils.ValidationResult> results = 
            ExcelValidationUtils.validateRequiredBatch(dataList);
        
        Assertions.assertEquals(1, results.size(), "Should have 1 validation result");
        
        ExcelValidationUtils.ValidationResult result = results.get(0);
        Assertions.assertEquals(1, result.getRowIndex(), "Error should be for row 1 (0-based)");
        Assertions.assertEquals(2, result.getErrors().size(), "Should have 2 errors");
    }

    @Test
    public void testValidateRequired_NullObject() {
        List<String> errors = ExcelValidationUtils.validateRequired(null);
        Assertions.assertEquals(1, errors.size(), "Should have 1 error for null object");
        // Just check that we got an error, don't check specific message
        Assertions.assertFalse(errors.get(0).isEmpty(), "Error message should not be empty");
    }

    @Test
    public void testValidateRequiredBatch_EmptyList() {
        List<TestData> emptyList = new ArrayList<>();
        List<ExcelValidationUtils.ValidationResult> results = 
            ExcelValidationUtils.validateRequiredBatch(emptyList);
        
        Assertions.assertTrue(results.isEmpty(), "Empty list should return no validation results");
    }
}
