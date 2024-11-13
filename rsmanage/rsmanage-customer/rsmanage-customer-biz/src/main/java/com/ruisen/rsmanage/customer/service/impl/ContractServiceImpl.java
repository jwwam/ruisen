package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruisen.rsmanage.customer.entity.ContractEntity;
import com.ruisen.rsmanage.customer.entity.ContractSequence;
import com.ruisen.rsmanage.customer.mapper.ContractMapper;
import com.ruisen.rsmanage.customer.mapper.ContractSequenceMapper;
import com.ruisen.rsmanage.customer.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 合同管理表
 *
 * @author rsmanage
 * @date 2024-11-13 13:31:03
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, ContractEntity> implements ContractService {
	@Autowired
	private ContractSequenceMapper sequenceMapper;

	@Transactional(rollbackFor = Exception.class)
	public String getNextSequence(String signedDate) {
		try {
			// 1. 将字符串日期转换为Date类型
			LocalDate date = LocalDate.parse(signedDate.substring(0, 10));

			// 2. 获取或创建序列记录
			ContractSequence sequence = sequenceMapper.selectByDate(date);
			if (sequence == null) {
				sequence = new ContractSequence();
				sequence.setSequenceDate(date);
				sequence.setCurrentValue(1);
				sequenceMapper.insert(sequence);
			} else {
				// 更新序列值
				sequenceMapper.incrementSequence(date);
				sequence.setCurrentValue(sequence.getCurrentValue() + 1);
			}

			// 3. 生成合同编号：RS + 年月日 + 3位序号
			String dateStr = date.format(DateTimeFormatter.BASIC_ISO_DATE);
			String sequenceStr = String.format("%03d", sequence.getCurrentValue());

			return "RS" + dateStr + sequenceStr;

		} catch (Exception e) {
			log.error("生成合同编号失败", e);
			throw new RuntimeException("生成合同编号失败");
		}
	}
}
