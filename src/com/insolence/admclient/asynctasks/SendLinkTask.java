package com.insolence.admclient.asynctasks;

import android.content.Context;

import com.insolence.admclient.network.DownloadMasterNetworkDalc;

public class SendLinkTask extends SendTaskBase{

	private String _link;
	
	public SendLinkTask(Context context, String link){
		super(context);
		_link = link;
	}

	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		DownloadMasterNetworkDalc dalc = new DownloadMasterNetworkDalc(_context);
		boolean result = dalc.sendLink(_link);
		if (result){
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, "Cannot connect to Download Master service");
		}
	}

}
