package com.ruisen.rsmanage.customer.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.admin.api.entity.SysFile;
import com.ruisen.rsmanage.customer.Po.WorkPo;
import com.ruisen.rsmanage.customer.entity.WorkEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkMapper extends BaseMapper<WorkEntity> {
	List<WorkPo> qry(@Param("param") Map<String, Object> param);
	/**
	 * 通过用户ID查询用户名称
	 * @param userId 用户ID
	 * @return 用户名称
	 */
	String getUserNameById(Long userId);

	List<SysFile> getFileDetail(@Param("fileName") String fileName);

}

