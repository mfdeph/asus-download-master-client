package com.insolence.admclient;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class SendCommandAsyncTask  extends AsyncTask<Void, Void, AsyncTaskResult>{

	protected ListActivity _listActivity;
	private String _command;
	private String _id;
	
	public SendCommandAsyncTask(ListActivity listActivity, String command){
		_listActivity = listActivity;
		_command = command;
		_id = null;
	}
	
	public SendCommandAsyncTask(ListActivity listActivity, String command, String id){
		_listActivity = listActivity;
		_command = command;
		_id = id;
	}
	

	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		boolean isCommandSent = 
			(_id == null)?
				DownloadItemListManager.getInstance().SendGroupCommand(_command)
			:
				DownloadItemListManager.getInstance().SendCommand(_command, _id);
		if (isCommandSent){
			DownloadItemListManager.getInstance().getDownloadItems(true);
			return new AsyncTaskResult(true, "Succeed");
		}else{
			return new AsyncTaskResult(false, "Cannot connect to Download Master service");
		}
	}
	
	@Override
	protected void onPostExecute(AsyncTaskResult result){		
		if (result.IsSucceed){
			ListView list = _listActivity.getListView();
			int savedPosition = list.getFirstVisiblePosition();
		    View firstVisibleView = list.getChildAt(0);
		    int savedListTop = (firstVisibleView == null) ? 0 : firstVisibleView.getTop();			
			_listActivity.setListAdapter(new DownloadItemListAdapter(_listActivity));
			if (savedPosition >= 0)
			   list.setSelectionFromTop(savedPosition, savedListTop);
		}else{
			Toast.makeText(_listActivity, result.Message, Toast.LENGTH_LONG).show();
		}
	}
}
