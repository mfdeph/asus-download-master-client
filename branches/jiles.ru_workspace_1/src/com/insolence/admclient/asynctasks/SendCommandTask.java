package com.insolence.admclient.asynctasks;

import android.content.Context;

import com.insolence.admclient.network.DownloadMasterNetworkDalc;

public class SendCommandTask extends SendTaskBase{

	private String _command;
	private String _id = null;
	
	public SendCommandTask(Context context, String command){
		super(context);
		_command = command;
	}
	
	public SendCommandTask(Context context, String command, String id){
		this(context, command);
		_id = id;
	}

	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		DownloadMasterNetworkDalc dalc = new DownloadMasterNetworkDalc(_context);
		boolean isCommandSent = 
			(_id == null)?
				dalc.sendGroupCommand(_command)
			:
				dalc.sendCommand(_command, _id);
		if (isCommandSent){
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, "Cannot connect to Download Master service");
		}
	}

}
