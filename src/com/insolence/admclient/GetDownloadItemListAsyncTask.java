package com.insolence.admclient;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

public class GetDownloadItemListAsyncTask  extends AsyncTask<Void, Void, Void>{

	protected ListActivity _listActivity;
	
	public GetDownloadItemListAsyncTask(ListActivity listActivity){
		_listActivity = listActivity;
	}
	

	@Override
	protected Void doInBackground(Void... arg0) {
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
