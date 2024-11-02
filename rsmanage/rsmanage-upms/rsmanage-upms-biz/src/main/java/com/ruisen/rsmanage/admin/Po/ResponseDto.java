package com.ruisen.rsmanage.admin.Po;


import lombok.Data;

@Data
public class ResponseDto<T> {
	private String retCode;
	private String retMsg;
	private T data;
	private PageDto page;


	public ResponseDto(String retCode, String retMsg) {
	}

	public ResponseDto() {

	}
}
