package utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportValidationResult {
    private boolean valid;
    private List<Map<String, Object>> errors;
    private LocalDate parsedDate;

    public ImportValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
    }
    
    // getters and setters
    public boolean isValid() {
        return valid;
    }

    public List<Map<String, Object>> getErrors() {
        return errors;
    }

    public LocalDate getParsedDate() {
        return parsedDate;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setErrors(List<Map<String, Object>> errors) {
        this.errors = errors;
    }

    public void setParsedDate(LocalDate parsedDate) {
        this.parsedDate = parsedDate;
    }
    
}
