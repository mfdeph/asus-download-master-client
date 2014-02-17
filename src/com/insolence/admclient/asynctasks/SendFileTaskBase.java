package com.insolence.admclient.asynctasks;

import java.io.File;

import android.content.Context;

import com.insolence.admclient.R;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

@Deprecated
public abstract class SendFileTaskBase extends SendTaskBase{
	
	public SendFileTaskBase(Context context) {
		super(context);
	}

	protected String fileIsNullExceptionText = "Torrent file does not exists";

	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		File file = getFile();
		if (file == null)
			return new AsyncTaskResult(false, fileIsNullExceptionText);
		if (new DownloadMasterNetworkDalc(_context).sendFile(file)){
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, getStr(R.string.command_info_cannot_connect));
		}
	}
	
	protected abstract File getFile();

}
