package utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomLocalDateConverter implements Converter<ConversionResult<LocalDate>> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yy", Locale.ENGLISH);
    
    @Override
    public Class<?> supportJavaTypeKey() {
        return ConversionResult.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public ConversionResult<LocalDate> convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                     GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        if (stringValue == null || stringValue.trim().isEmpty()) {
            return ConversionResult.error("日期不能为空", stringValue);
        }
        
        // 忽略表头和汇总行
        if ("Date".equalsIgnoreCase(stringValue.trim()) || "Total".equalsIgnoreCase(stringValue.trim())) {
            return ConversionResult.error("数据可能是表头或汇总行", stringValue);
        }
        
        try {
            return ConversionResult.success(LocalDate.parse(stringValue, FORMATTER), stringValue);
        } catch (Exception e) {
            try {
                return ConversionResult.success(LocalDate.parse(stringValue, DateTimeFormatter.ofPattern("yyyy/MM/dd")), stringValue);
            } catch (Exception ex) {
                return ConversionResult.error(
                    String.format("日期格式错误：'%s'，支持的格式：M/d/yy 或 yyyy/MM/dd", stringValue),
                    stringValue
                );
            }
        }
    }

}
