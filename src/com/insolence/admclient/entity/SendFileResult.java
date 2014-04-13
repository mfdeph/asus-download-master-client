package com.insolence.admclient.entity;

public class SendFileResult {
	
	private SendFileResultEnum result;
	private String additionalInfo;
	
	public SendFileResultEnum getResult() {
		return result;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public SendFileResult(SendFileResultEnum result, String additionalInfo) {
		this(result);
		this.additionalInfo = additionalInfo;
	}
	
	public SendFileResult(SendFileResultEnum result) {
		super();
		this.result = result;
	}

	public enum SendFileResultEnum {
		
		Succeed,
		NeedSelectFiles,
		Error
		
	}
	
}
