package com.insolence.admclient;


import java.util.List;

import com.insolence.admclient.entity.DownloadFileInfo;
import com.insolence.admclient.entity.DownloadInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CheckBox;

public class SelectFilesDialog extends Dialog{

	ListView filesListView;	
	
	public SelectFilesDialog(Context context, DownloadInfo downloadInfo) {
		super(context);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_files_dialog);
		
		filesListView = (ListView) findViewById(R.id.files_list);
		
		DownloadFilesListAdapter adapter = new DownloadFilesListAdapter(context, R.layout.select_files_list_item, downloadInfo.getFiles().toArray(new DownloadFileInfo[0]));
		filesListView.setAdapter(adapter);
		
	}
	
	public class DownloadFilesListAdapter extends ArrayAdapter<DownloadFileInfo>{
		public DownloadFilesListAdapter(Context context, int resource, DownloadFileInfo[] objects) {
			super(context, resource, objects);
		}

		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
			
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.select_files_list_item, null);
	        }
			
	        final DownloadFileInfo current = getItem(position);
	        
			((TextView) v.findViewById(R.id.file_name)).setText(current.getName());
			
			final CheckBox selectedCheckBox = (CheckBox) v.findViewById(R.id.file_selected);
			
			selectedCheckBox.setOnCheckedChangeListener(null);
			selectedCheckBox.setChecked(current.isSelected());
			selectedCheckBox.setOnCheckedChangeListener(
					new OnCheckedChangeListener() {
				        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				        	current.setSelected(isChecked);
						}
					});
			
			return v;
		}
	}

}
