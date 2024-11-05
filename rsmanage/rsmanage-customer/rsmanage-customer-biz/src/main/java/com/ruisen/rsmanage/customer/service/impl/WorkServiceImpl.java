package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisen.rsmanage.admin.api.entity.SysFile;
import com.ruisen.rsmanage.customer.Po.PageDto;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.Po.WorkPo;
import com.ruisen.rsmanage.customer.entity.WorkEntity;
import com.ruisen.rsmanage.customer.mapper.WorkMapper;
import com.ruisen.rsmanage.customer.service.WorkService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工单表
 *
 * @author rsmanage
 * @date 2024-10-29 17:26:35
 */
@Slf4j
@Service
@AllArgsConstructor
public class WorkServiceImpl extends ServiceImpl<WorkMapper, WorkEntity> implements WorkService {
	private final WorkMapper workMapper;
	@Override
	public ResponseDto qry(Map<String, Object> param) {
		log.info("查询工单页面开始，查询条件：【{}】",param);
		// 需要分页查询
		ResponseDto rspMsg = new ResponseDto ();
		int curPage = (int) param.get("curPage");
		int pageSize = (int) param.get("pageSize");
		String customerId = (String) param.get("customerId");
		String category = (String) param.get("category");
		String status = (String) param.get("status");
		String assignees = (String) param.get("assignees");
		List<WorkPo> result;
		if (!StringUtils.isEmpty(curPage) && !StringUtils.isEmpty(pageSize)){
			try {
				Page<WorkPo> page = PageHelper.startPage(curPage, pageSize);
				result = workMapper.qry(customerId,category,status,assignees);
				for (WorkPo workPo : result) {
					//戒指日期
					if (workPo.getDeadline() != null && !workPo.getDeadline().isEmpty()){
						LocalDate date = LocalDate.parse(workPo.getDeadline() , DateTimeFormatter.ofPattern("yyyyMMdd"));
						String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						workPo.setDeadline(formattedDate);
					}
					//处理人
					if(workPo.getAssignees() != null && !workPo.getAssignees().isEmpty()){
						// 通过处理人id获取姓名
						String assigneesStr = workMapper.getUserNameById(Long.valueOf((workPo.getAssignees())));
						workPo.setAssignees(assigneesStr);
					}
					//抄送人
					if(workPo.getCopy() != null && !workPo.getCopy().isEmpty()){
						String ccStr = workPo.getCopy();
						String[] idArray = ccStr.split(",");
						// 存储用户名称
						List<String> nameList = new ArrayList<>();
						// 循环查询用户名称
						for (String idStr : idArray) {
							Long userId = Long.parseLong(idStr.trim());
							String userName = workMapper.getUserNameById(userId);
							if (userName != null) {
								nameList.add(userName);
							}
						}
						ccStr = String.join(",", nameList);
						workPo.setCopy(ccStr);
					}
					if(workPo.getAttachments() != null && !workPo.getAttachments().isEmpty()) {
						List<SysFile> allAttachments = new ArrayList<>();
						String Attachments = workPo.getAttachments().toString();
						String[] urlArray = Attachments.split(",");
						for (String url : urlArray) {
							// 获取文件名（包含扩展名）
							String fileName = url.substring(url.lastIndexOf("/") + 1);
							List<SysFile> AttachmentList = workMapper.getFileDetail(fileName);
							allAttachments.addAll(AttachmentList);
							System.out.println("文件名: " + fileName);
						}
						workPo.setAttachmentsList(allAttachments);
					}
				}
				PageInfo<WorkPo> pageInfo = new PageInfo<WorkPo>(result);
				log.info("分页参数:{}", pageInfo);
				int totalRow = (int) pageInfo.getTotal();
				int totalPage = (int) Math.ceil((double) totalRow / pageSize);
				rspMsg.setPage(new PageDto(pageSize, curPage, totalRow));
			} finally {
				PageHelper.clearPage();
			}
		}else {
			result = workMapper.qry(customerId,category,status,assignees);

		}
		if (result == null || result.size() == 0) {
			return new ResponseDto("ok", "数据为空");
		}

		rspMsg.setRetCode("ok");
		rspMsg.setRetMsg("查询成功");
		rspMsg.setData(result);
		System.out.println(rspMsg);
		log.info("查询客户与合作伙伴关系结束",rspMsg);
		return rspMsg;
	}
}
