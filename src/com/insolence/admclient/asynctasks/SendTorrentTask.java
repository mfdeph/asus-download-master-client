package com.insolence.admclient.asynctasks;

import com.insolence.admclient.R;
import com.insolence.admclient.SelectFilesDialog;
import com.insolence.admclient.entity.DownloadFileInfo;
import com.insolence.admclient.entity.DownloadInfo;
import com.insolence.admclient.entity.SendFileResult;
import com.insolence.admclient.entity.SendFileResult.SendFileResultEnum;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.Window;

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
		DownloadMasterNetworkDalc dalc = new DownloadMasterNetworkDalc(_context);
		final SendFileResult result = dalc.sendFile(_uri, _fileName);
		if (result.getResult() == SendFileResultEnum.Succeed)
			return new AsyncTaskResult(true, "Succeed");
		if (result.getResult() == SendFileResultEnum.Error)
			return new AsyncTaskResult(false, getStr(R.string.command_info_cannot_connect));
		return new AsyncTaskResult(true, "Need additional action", new Runnable(){

			@Override
			public void run() {
				DownloadInfo testDownloadInfo = DownloadInfo.parse(result.getAdditionalInfo());
				
				final Dialog dialog = new SelectFilesDialog(_context, testDownloadInfo);				
				dialog.show();
			}
			
		});
	}
	
	@Override
	protected void onPostExecute(AsyncTaskResult result){
		if (result.getAdditionalAction() != null)
			result.getAdditionalAction().run();
		super.onPostExecute(result);
	}

}