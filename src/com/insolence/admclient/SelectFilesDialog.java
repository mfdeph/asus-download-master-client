package com.insolence.admclient;

import com.insolence.admclient.entity.DownloadFileInfo;
import com.insolence.admclient.entity.DownloadInfo;

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
	IDialogResultProcessor processor;
	
	public SelectFilesDialog(Context context, DownloadInfo downloadInfo, final IDialogResultProcessor processor) {
		super(context);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_files_dialog);
		
		this.processor = processor;
		
		filesListView = (ListView) findViewById(R.id.files_list);
		
		View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.select_files_list_footer, null, false);
		filesListView.addFooterView(footerView);
		
		final DownloadFilesListAdapter adapter = new DownloadFilesListAdapter(context, R.layout.select_files_list_item, downloadInfo.getFiles().toArray(new DownloadFileInfo[0]));
		filesListView.setAdapter(adapter);
		
		filesListView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SelectFilesDialog.this.cancel();
			}
		});
		
		filesListView.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SelectFilesDialog.this.cancel();
				String arg = adapter.getDownloadItemsArgument();
				if (processor != null && !arg.equals(""))
					processor.process(arg);
			}
		});
		
	}
	
	public interface IDialogResultProcessor{
		void process(String argument);
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
	        
			((TextView) v.findViewById(R.id.file_name)).setText(current.getName() + " (" + current.getSize() + ")");
			
			final CheckBox selectedCheckBox = (CheckBox) v.findViewById(R.id.file_selected);
			
			selectedCheckBox.setOnCheckedChangeListener(null);
			selectedCheckBox.setChecked(current.isSelected());
			selectedCheckBox.setOnCheckedChangeListener(
					new OnCheckedChangeListener() {
				        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				        	current.setSelected(isChecked);
				        	uncheckedItemsCount += isChecked ? -1 : 1;
						}
					});
			
			return v;
		}
		
		public String getDownloadItemsArgument(){
			if (uncheckedItemsCount == 0)
				return "All";
			String result = "";
			for (int i = 0; i < getCount(); i++){
				DownloadFileInfo c = getItem(i);
				if (c.isSelected())
					result = result + c.getId() + ",";
			}
			if (!result.equals("")){
				result = result.substring(0, result.length() - 1);
			}
			return result;
		}
		
		private int uncheckedItemsCount = 0;
	}

}
