package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisen.rsmanage.admin.api.entity.SysFile;
import com.ruisen.rsmanage.customer.Po.ContractPo;
import com.ruisen.rsmanage.customer.Po.PageDto;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.ContractEntity;
import com.ruisen.rsmanage.customer.entity.ContractSequence;
import com.ruisen.rsmanage.customer.mapper.ContractMapper;
import com.ruisen.rsmanage.customer.mapper.ContractSequenceMapper;
import com.ruisen.rsmanage.customer.mapper.WorkMapper;
import com.ruisen.rsmanage.customer.service.ContractService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 合同管理表
 *
 * @author rsmanage
 * @date 2024-11-13 13:31:03
 */
@Slf4j
@Service
@AllArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, ContractEntity> implements ContractService {
	@Autowired
	private ContractSequenceMapper sequenceMapper;
	@Autowired
	private  ContractMapper contractMapper;
	@Autowired
	private WorkMapper workMapper;
	@Override
	public ResponseDto qry(Map<String, Object> param) {
		log.info("查询合同列表开始，查询条件：【{}】",param);
		// 需要分页查询
		ResponseDto rspMsg = new ResponseDto ();
		int curPage = (int) param.get("curPage");
		int pageSize = (int) param.get("pageSize");
		String customerId = (String) param.get("customerId");
		String ContractName = (String) param.get("ContractName");
		String ContractNumber = (String) param.get("ContractNumber");
		String IsActive = (String) param.get("IsActive");
		String SignedDate = (String) param.get("SignedDate");
		List<ContractPo> result;
		if (!StringUtils.isEmpty(curPage) && !StringUtils.isEmpty(pageSize)){
			try {
				Page<ContractPo> page = PageHelper.startPage(curPage, pageSize);
				result = contractMapper.qry(customerId,ContractName,ContractNumber,IsActive,SignedDate);
				for (ContractPo contractPo : result){
					if(contractPo.getFileUrl() != null && !contractPo.getFileUrl().isEmpty()) {
						List<SysFile> allAttachments = new ArrayList<>();
						String Attachments = contractPo.getFileUrl() .toString();
						String[] urlArray = Attachments.split(",");
						for (String url : urlArray) {
							// 获取文件名（包含扩展名）
							String fileName = url.substring(url.lastIndexOf("/") + 1);
							List<SysFile> AttachmentList = workMapper.getFileDetail(fileName);
							allAttachments.addAll(AttachmentList);
						}
						contractPo.setAttachmentsList(allAttachments);
					}
				}
				PageInfo<ContractPo> pageInfo = new PageInfo<ContractPo>(result);
				log.info("分页参数:{}", pageInfo);
				int totalRow = (int) pageInfo.getTotal();
				int totalPage = (int) Math.ceil((double) totalRow / pageSize);
				rspMsg.setPage(new PageDto(pageSize, curPage, totalRow));
			} finally {
				PageHelper.clearPage();
			}
		}else {
			result = contractMapper.qry(customerId,ContractName,ContractNumber,IsActive,SignedDate);

		}
		if (result == null || result.size() == 0) {
			return new ResponseDto("ok", "数据为空");
		}

		rspMsg.setRetCode("ok");
		rspMsg.setRetMsg("查询成功");
		rspMsg.setData(result);
		System.out.println(rspMsg);
		log.info("查询合同结束",rspMsg);
		return rspMsg;
	}

	//合同编号生成
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
