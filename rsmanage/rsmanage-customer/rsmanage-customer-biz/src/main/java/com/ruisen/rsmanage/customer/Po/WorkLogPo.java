package com.ruisen.rsmanage.customer.Po;

import com.ruisen.rsmanage.customer.entity.WorkEntity;
import com.ruisen.rsmanage.customer.entity.WorkLogEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "工单日志Po")
public class WorkLogPo extends WorkLogEntity {
    
    @Schema(description = "工单信息")
    private WorkEntity workInfo;
    
    @Schema(description = "操作人姓名")
    private String performedByName;
    
    @Schema(description = "操作人账号")
    private String performedByUsername;
    
    @Schema(description = "操作人头像")
    private String performedByAvatar;
}
