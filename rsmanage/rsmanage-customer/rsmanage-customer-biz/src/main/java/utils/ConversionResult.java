package utils;

public class ConversionResult<T> {
    private T value;
    private String error;
    private String originalData;
    private boolean success;

    private ConversionResult(T value, String error, boolean success, String originalData) {
        this.value = value;
        this.error = error;
        this.success = success;
        this.originalData = originalData;
    }

    public static <T> ConversionResult<T> success(T value, String originalData) {
        return new ConversionResult<>(value, null, true, originalData);
    }

    public static <T> ConversionResult<T> error(String error, String originalData) {
        return new ConversionResult<>(null, error, false, originalData);
    }

    public T getValue() {
        return value;
    }

    public String getError() {
        return error;
    }

    public String getOriginalData() {
        return originalData;
    }

    public boolean isSuccess() {
        return success;
    }
}
