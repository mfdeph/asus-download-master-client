package com.insolence.admclient;


public class AsyncTaskResult{
	
	public AsyncTaskResult(boolean isSucceed, String message){
		IsSucceed = isSucceed;
		Message = message;
	}
	
	public boolean IsSucceed;	
	public String Message;
	
}