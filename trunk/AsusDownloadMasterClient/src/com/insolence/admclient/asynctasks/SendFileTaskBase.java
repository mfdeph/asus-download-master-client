package com.insolence.admclient.asynctasks;

import java.io.File;

import com.insolence.admclient.R;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

public abstract class SendFileTaskBase extends SendTaskBase{
	
	protected String fileIsNullExceptionText = "Torrent file not exists";

	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		File file = getFile();
		if (file == null)
			return new AsyncTaskResult(false, fileIsNullExceptionText);
		if (DownloadMasterNetworkDalc.getInstance().sendFile(file)){
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, getStr(R.string.command_info_cannot_connect));
		}
	}
	
	protected abstract File getFile();

}
