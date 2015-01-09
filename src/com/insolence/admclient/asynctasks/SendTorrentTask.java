package com.insolence.admclient.asynctasks;

import com.insolence.admclient.DownloadItemListActivity;
import com.insolence.admclient.R;
import com.insolence.admclient.SelectFilesDialog;
import com.insolence.admclient.SelectFilesDialog.IDialogResultProcessor;
import com.insolence.admclient.entity.DownloadInfo;
import com.insolence.admclient.entity.SendFileResult;
import com.insolence.admclient.entity.SendFileResult.SendFileResultEnum;
import com.insolence.admclient.network.DownloadMasterNetworkDalc;
import com.insolence.admclient.storage.PreferenceAccessor;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

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
		final DownloadMasterNetworkDalc dalc = new DownloadMasterNetworkDalc(_context);
		final SendFileResult result = dalc.sendFile(_uri, _fileName);
		if (result.getResult() == SendFileResultEnum.Succeed)
			return new AsyncTaskResult(true, "Succeed");
		if (result.getResult() == SendFileResultEnum.Error)
			return new AsyncTaskResult(false, getStr(R.string.command_info_cannot_connect));
		//the approval request comes but there's only one file or there's tick download everything in config
		final DownloadInfo downloadInfo = DownloadInfo.parse(result.getAdditionalInfo());
		if (PreferenceAccessor.getInstance(_context).isDownloadWholeTorrentEnabled() || 
			downloadInfo == null || 
			downloadInfo.getFiles().size() < 2){
				if (dalc.confirmDownload(downloadInfo.getTorrentName(), "All")){
					return new AsyncTaskResult(true, "Succeed");
				}else{
					return new AsyncTaskResult(false, getStr(R.string.command_info_cannot_connect));
				}	
		}			
		return new AsyncTaskResult(true, "Need additional action", new Runnable(){		
			public void run() {		
				final Dialog dialog = new SelectFilesDialog(_context, downloadInfo, new IDialogResultProcessor() {			
					public void process(final String argument) {
						new AsyncTask<Void, Void, Void>(){
							protected Void doInBackground(Void... params) {
								dalc.confirmDownload(downloadInfo.getTorrentName(), argument);
								return null;
							}
							protected void onPostExecute(Void result){
								notifyTaskCompleted();
								if (DownloadItemListActivity.getCurrent() != null)
									DownloadItemListActivity.getCurrent().sendRefreshRequest();
							}
						}.execute();			
					}
				});				
				dialog.show();
			}
			
		});
	}
	
	@Override
	protected void onPostExecute(AsyncTaskResult result){
		if (result.getAdditionalAction() != null)
			result.getAdditionalAction().run();
		else{
			notifyTaskCompleted();
		}
		super.onPostExecute(result);
	}
	
	private void notifyTaskCompleted(){
		   Toast.makeText(_context, String.format(getStr(R.string.command_info_download_torrent), _fileName), Toast.LENGTH_SHORT).show();		
	}

}