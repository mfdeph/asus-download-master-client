package com.insolence.admclient.asynctasks;

import android.os.AsyncTask;

import com.insolence.admclient.listmanagers.IProcessResultConsumer;

public abstract class SendTaskBase extends AsyncTask<Void, Void, AsyncTaskResult>{

	protected IProcessResultConsumer _target;
	
	public SendTaskBase(IProcessResultConsumer target){
		_target = target;
	}
	
	@Override
	protected void onPostExecute(AsyncTaskResult result){
		if (_target == null)
			return;
		if (result.isSucceed()){
			_target.sendRefreshRequestIfNesessary();
		}else{
			_target.showErrorMessage(result.getMessage());
		}
	}
	
}
