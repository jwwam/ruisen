package utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageConverter implements Converter<BigDecimal> {

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
                                      GlobalConfiguration globalConfiguration) {
        try {
            String stringValue = cellData.getStringValue();
            if (stringValue == null || stringValue.trim().isEmpty()) {
                return null;
            }
            
            // 处理表头和Total行
            if (stringValue.startsWith("Ad Exchange") || "Total".equalsIgnoreCase(stringValue.trim())) {
                return null;
            }
            
            // 移除百分号并转换为小数
            stringValue = stringValue.replace("%", "").trim();
            try {
                BigDecimal value = new BigDecimal(stringValue);
                // 如果值大于1，认为是百分比格式，需要除以100
                if (value.compareTo(BigDecimal.ONE) > 0) {
                    return value.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                }
                return value;
            } catch (NumberFormatException e) {
                return null;  // 对于无法转换的值返回null而不是抛出异常
            }
        } catch (Exception e) {
            return null;  // 对于所有异常情况返回null
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(BigDecimal value, ExcelContentProperty contentProperty,
                                             GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(value.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%");
    }
}
