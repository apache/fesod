package cn.idev.excel.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel required field annotation.
 * Used to mark fields as required during data validation.
 * 
 * <p>This annotation is used in conjunction with {@link cn.idev.excel.util.ExcelValidationUtils}
 * to validate that required fields are not empty when reading Excel data.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @ExcelProperty("Project Name")
 * @ExcelRequired(message = "Project name cannot be empty")
 * private String projectName;
 * }
 * </pre>
 *
 * @author FastExcel Team
 * @since 1.2.1
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelRequired {
    
    /**
     * Error message to display when validation fails.
     * 
     * @return the error message
     */
    String message() default "This field is required";
}
