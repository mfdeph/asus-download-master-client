package com.insolence.admclient.asynctasks;

public class AsyncTaskResult{

	public AsyncTaskResult(){
	}
	
	public AsyncTaskResult(boolean isSucceed, String message){
		this.isSucceed = isSucceed;
		this.message = message;
	}
	
	public AsyncTaskResult(boolean isSucceed, String message, Runnable additionalAction){
		this(isSucceed, message);
		this.additionalAction = additionalAction;
	}
	
	
	public boolean isSucceed() {
		return isSucceed;
	}
	
	public Runnable getAdditionalAction() {
		return additionalAction;
	}
	
	public void setSucceed(boolean isSucceed) {
		this.isSucceed = isSucceed;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	private boolean isSucceed;	
	private String message;
	private Runnable additionalAction;
	
}