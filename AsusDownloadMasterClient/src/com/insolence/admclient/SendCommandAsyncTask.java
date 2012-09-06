package com.insolence.admclient;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

public class SendCommandAsyncTask  extends AsyncTask<Void, Void, Void>{

	protected ListActivity _listActivity;
	private String _command;
	private String _id;
	
	
	public SendCommandAsyncTask(ListActivity listActivity, String command, String id){
		_listActivity = listActivity;
		_command = command;
		_id = id;
	}
	

	@Override
	protected Void doInBackground(Void... arg0) {
		DownloadItemListManager.getInstance().SendCommand(_command, _id);
		DownloadItemListManager.getInstance().getDownloadItems(true);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result){		
		DownloadItemListAdapter adapter = new DownloadItemListAdapter(_listActivity);
		
		ListView list = _listActivity.getListView();
		int savedPosition = list.getFirstVisiblePosition();
	    View firstVisibleView = list.getChildAt(0);
	    int savedListTop = (firstVisibleView == null) ? 0 : firstVisibleView.getTop();	
		
		_listActivity.setListAdapter(adapter);
		
		if (savedPosition >= 0) { //initialized to -1
		      list.setSelectionFromTop(savedPosition, savedListTop);
		    }

		
	}
}
