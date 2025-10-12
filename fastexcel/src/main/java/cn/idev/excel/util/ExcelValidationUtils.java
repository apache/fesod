package cn.idev.excel.util;

import cn.idev.excel.annotation.validation.ExcelRequired;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.ExcelIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel validation utility class.
 * Provides common methods for Excel data validation.
 *
 * <p>This utility class offers batch validation capabilities for Excel data,
 * particularly useful for validating required fields during data import operations.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * List<ValidationResult> results = ExcelValidationUtils.validateRequiredBatch(dataList);
 * if (!results.isEmpty()) {
 *     // Handle validation errors
 *     for (ValidationResult result : results) {
 *         System.out.println("Row " + (result.getRowIndex() + 1) + ": " +
 *                           String.join(", ", result.getErrors()));
 *     }
 * }
 * }
 * </pre>
 *
 * @author FastExcel Team
 * @since 1.2.1
 */
public class ExcelValidationUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelValidationUtils.class);
    
    /**
     * Validates required fields of an object.
     *
     * @param obj the object to validate
     * @return list of validation errors, empty if validation passes
     */
    public static List<String> validateRequired(Object obj) {
        List<String> errors = new ArrayList<>();
        
        if (obj == null) {
            errors.add("Validation object cannot be null");
            return errors;
        }
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            // 跳过被@ExcelIgnore标记的字段
            if (field.isAnnotationPresent(ExcelIgnore.class)) {
                continue;
            }
            
            // 检查是否有@ExcelProperty注解（确定是Excel列）
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty == null) {
                continue;
            }
            
            // 检查是否有@ExcelRequired注解
            ExcelRequired excelRequired = field.getAnnotation(ExcelRequired.class);
            if (excelRequired == null) {
                continue;
            }
            
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                
                // Check if field value is empty
                if (isEmpty(value)) {
                    String fieldName = getFieldDisplayName(field, excelProperty);
                    String errorMessage = String.format("[%s] %s", fieldName, excelRequired.message());
                    errors.add(errorMessage);
                    logger.debug("Required field validation failed: {}", errorMessage);
                }
                
            } catch (IllegalAccessException e) {
                logger.error("Failed to access field: {}", field.getName(), e);
                errors.add(String.format("Failed to access field %s", field.getName()));
            }
        }
        
        return errors;
    }
    
    /**
     * Batch validates required fields for a list of objects.
     *
     * @param objList the list of objects to validate
     * @return validation results, with row index (0-based) and error messages
     */
    public static List<ValidationResult> validateRequiredBatch(List<?> objList) {
        List<ValidationResult> results = new ArrayList<>();
        
        if (objList == null || objList.isEmpty()) {
            return results;
        }
        
        for (int i = 0; i < objList.size(); i++) {
            Object obj = objList.get(i);
            List<String> errors = validateRequired(obj);
            
            if (!errors.isEmpty()) {
                ValidationResult result = new ValidationResult();
                result.setRowIndex(i);
                result.setErrors(errors);
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * Checks if a value is empty.
     *
     * @param value the value to check
     * @return true if the value is empty, false otherwise
     */
    private static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        
        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }
        
        if (value instanceof Number) {
            return false; // Numbers are not considered empty
        }
        
        return false;
    }
    
    /**
     * Gets the display name of a field.
     *
     * @param field the field
     * @param excelProperty the ExcelProperty annotation
     * @return the display name for the field
     */
    private static String getFieldDisplayName(Field field, ExcelProperty excelProperty) {
        String[] values = excelProperty.value();
        if (values.length > 0 && !values[0].isEmpty()) {
            // Use the last value as display name
            return values[values.length - 1];
        }
        return field.getName();
    }

    /**
     * Validation result class that holds validation errors for a specific row.
     */
    public static class ValidationResult {
        private int rowIndex;
        private List<String> errors;
        
        public int getRowIndex() {
            return rowIndex;
        }
        
        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
        
        @Override
        public String toString() {
            return String.format("第%d行: %s", rowIndex + 1, String.join(", ", errors));
        }
    }
}
