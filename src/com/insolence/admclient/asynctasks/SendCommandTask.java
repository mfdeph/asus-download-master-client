package com.insolence.admclient.asynctasks;

import com.insolence.admclient.listmanagers.IProcessResultConsumer;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

public class SendCommandTask extends SendTaskBase{

	private String _command;
	private String _id = null;
	
	public SendCommandTask(IProcessResultConsumer target, String command){
		super(target);
		_command = command;
	}
	
	public SendCommandTask(IProcessResultConsumer target, String command, String id){
		this(target, command);
		_id = id;
	}

	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		boolean isCommandSent = 
			(_id == null)?
				DownloadMasterNetworkDalc.getInstance().sendGroupCommand(_command)
			:
				DownloadMasterNetworkDalc.getInstance().sendCommand(_command, _id);
		if (isCommandSent){
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, "Cannot connect to Download Master service");
		}
	}

}
