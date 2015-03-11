package com.insolence.admclient;

import java.util.List;

import com.insolence.admclient.entity.TorrentSearchResultItem;
import com.insolence.search.TorrentSearchManager;
import com.insolence.search.TorrentSearchManager.Links;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchDialog extends Dialog{

	public SearchDialog(Context context) {
		super(context);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_dialog);
		
		final ListView searchResultList = (ListView) findViewById(R.id.search_result_list);
		
		final Spinner searchFilter = (Spinner)findViewById(R.id.search_filter);
		String[] items = new String[]{"video", "audio", "games", "other", "all"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		searchFilter.setAdapter(adapter);
		
		final SearchView searchView = (SearchView) findViewById(R.id.search_view);
		searchView.onActionViewExpanded();
		
		final TorrentSearchManager searchManager = new TorrentSearchManager(getContext(), searchFilter.getSelectedItem().toString());
		
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
			
			   private	String currentText = "";

	           @Override
	           public boolean onQueryTextSubmit(final String query) {
	        	   
	        	   searchView.clearFocus();
	        	   
	        	   findViewById(R.id.search_progress_bar).setVisibility(View.VISIBLE);
	        	   
	        	   new AsyncTask<Void, Void, List<TorrentSearchResultItem>>(){

						@Override
						protected List<TorrentSearchResultItem> doInBackground(Void... params) {
							return searchManager.getResult(query, 1);							
						}
						
						@Override
						protected void onPostExecute(List<TorrentSearchResultItem> result){
							findViewById(R.id.search_progress_bar).setVisibility(View.GONE);				
							if (result.size() > 0){
								searchResultList.setVisibility(View.VISIBLE);
								searchResultList.setAdapter(new SearchListAdapter(getContext(), result));
								searchResultList.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
										final TorrentSearchResultItem current = (TorrentSearchResultItem) adapter.getItemAtPosition(position);
										
										new AsyncTask<Void, Void, Links>(){
											@Override
											protected Links doInBackground(Void... params) {
												return searchManager.getLink(current.getHref());
											}
											
											@Override
											protected void onPostExecute(Links result){
												searchManager.openLinks(current, result);
											}
										
										}.execute();
									}
								});
							}
						}
	        	   }.execute();
	        	   return true;
	           }

	           @Override
	           public boolean onQueryTextChange(String s) {
	        	   if (currentText.length() > 1 && s.length() == 0){
	        		   searchResultList.setVisibility(View.GONE);
	        	   }
	        	   currentText = s;
	               return false;
	           }
	       });

	    }
		
	public class SearchListAdapter extends ArrayAdapter<TorrentSearchResultItem>{
		public SearchListAdapter(Context context, List<TorrentSearchResultItem> objects) {
			super(context, R.layout.search_list_item, objects);
		}

		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
			
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.search_list_item, null);    
	        }     
	        
	        TorrentSearchResultItem current = getItem(position);
	        
	        ((TextView) v.findViewById(R.id.search_item_title)).setText(current.getTitle());
	        
	        ((TextView) v.findViewById(R.id.search_item_summary)).setText(current.getSize() + ", " + current.getSeeds() + " seeds");
	        
	        return v;
		}
	}
}
