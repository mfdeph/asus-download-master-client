package com.insolence.admclient;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class GetDownloadItemListAsyncTask  extends AsyncTask<Void, Void, AsyncTaskResult>{

	protected ListActivity _listActivity;
	
	public GetDownloadItemListAsyncTask(ListActivity listActivity){
		_listActivity = listActivity;
	}
	

	@Override
	protected AsyncTaskResult doInBackground(Void... arg0) {
		if (!DownloadItemListManager.getInstance().updateDownloadItems(true)){
			return new AsyncTaskResult(false, "Cannot connect to Download Master service");
		}else{
			return new AsyncTaskResult(true, "Succeed");
		}
	}
	
	@Override
	protected void onPostExecute(AsyncTaskResult result){	
		
		if (result.IsSucceed){
		
			DownloadItemListAdapter adapter = new DownloadItemListAdapter(_listActivity);
			
			ListView list = _listActivity.getListView();
			int savedPosition = list.getFirstVisiblePosition();
		    View firstVisibleView = list.getChildAt(0);
		    int savedListTop = (firstVisibleView == null) ? 0 : firstVisibleView.getTop();	
			
			_listActivity.setListAdapter(adapter);
			
			if (savedPosition >= 0) { //initialized to -1
			      list.setSelectionFromTop(savedPosition, savedListTop);
			    }
			
			((DownloadItemListActivity)_listActivity).setDefaultMessageVisibility();
		
		}else{
			((DownloadItemListActivity) _listActivity).announceAutorefreshIssueMessage(result.Message);
		}

		
	}
}
