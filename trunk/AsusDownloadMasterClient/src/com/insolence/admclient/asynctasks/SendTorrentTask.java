package com.insolence.admclient.asynctasks;

import com.insolence.admclient.R;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

import android.content.Context;
import android.net.Uri;

public class SendTorrentTask extends SendTaskBase{

	private final Uri _uri;
	private final String _fileName;
	
	public SendTorrentTask(Context context, Uri uri, String fileName){
		super(context);
		_uri = uri;
		_fileName = fileName;
	}
	
	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		if (new DownloadMasterNetworkDalc(_context).sendFile(_uri, _fileName)){
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, getStr(R.string.command_info_cannot_connect));
		}
	}

}