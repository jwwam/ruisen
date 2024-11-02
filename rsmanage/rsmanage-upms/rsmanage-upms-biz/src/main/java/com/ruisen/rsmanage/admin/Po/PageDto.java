package com.ruisen.rsmanage.admin.Po;

import lombok.Data;

@Data
public class PageDto {
	private int size;
	private int current;
	private int total;
//	private int pageSizes;

	public PageDto(int pageSize, int curPage,int totalRow) {
		this.size = pageSize;
		this.current = curPage;
		this.total = totalRow;
//		this.pageSizes = totalPage;
	}
}
