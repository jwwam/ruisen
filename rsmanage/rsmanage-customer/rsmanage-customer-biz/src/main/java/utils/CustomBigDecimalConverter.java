package utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CustomBigDecimalConverter implements Converter<BigDecimal> {

    @Override
    public Class<BigDecimal> supportJavaTypeKey() {
        return BigDecimal.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public BigDecimal convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                     GlobalConfiguration globalConfiguration) throws Exception {
        String stringValue = cellData.getStringValue();
        if (stringValue == null || stringValue.trim().isEmpty()) {
            return null;
        }
        
        // 处理表头和Total行
        if (stringValue.startsWith("Ad Exchange") || "Total".equalsIgnoreCase(stringValue.trim())) {
            return null;
        }
        
        try {
            // 移除所有逗号和空格
            stringValue = stringValue.replace(",", "").trim();
            return new BigDecimal(stringValue).setScale(4, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;  // 对于无法转换的值返回null而不是抛出异常
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(BigDecimal value, ExcelContentProperty contentProperty,
                                             GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>("");
        }
        // 添加千位分隔符
        String formattedValue = String.format("%,.2f", value);
        return new WriteCellData<>(formattedValue);
    }
}
