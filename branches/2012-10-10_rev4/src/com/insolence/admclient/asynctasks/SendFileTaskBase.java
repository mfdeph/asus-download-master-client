package com.insolence.admclient.asynctasks;

import java.io.File;

import com.insolence.admclient.listmanagers.IProcessResultConsumer;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

public abstract class SendFileTaskBase extends SendTaskBase{
	
	public SendFileTaskBase(IProcessResultConsumer target) {
		super(target);
	}
	
	protected String fileIsNullExceptionText = "Torrent file not exists";

	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		File file = getFile();
		if (file == null)
			return new AsyncTaskResult(false, fileIsNullExceptionText);
		if (DownloadMasterNetworkDalc.getInstance().sendFile(file)){
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, "Cannot connect to Download Master service");
		}
	}
	
	protected abstract File getFile();

}
