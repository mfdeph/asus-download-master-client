package com.insolence.admclient;

import java.util.List;

import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuPopupHelper;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.insolence.admclient.asynctasks.SendCommandTask;
import com.insolence.admclient.entity.DownloadItem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadItemListAdapter extends ArrayAdapter<DownloadItem>{

	private OnSelectItemListener _onSelectItemListener;
	
	public DownloadItemListAdapter(Context context, List<DownloadItem> downloadItems, OnSelectItemListener listener) {
		super(context, R.layout.download_item, downloadItems); 
		_onSelectItemListener = listener;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.download_item, null);
        }
        
        final DownloadItem downloadItem = getItem(position);
        
        TextView nameHolder = (TextView) v.findViewById(R.id.download_item_name);
        nameHolder.setText(downloadItem.getName());
        
        ProgressBarTextView advancedTextView = (ProgressBarTextView) v.findViewById(R.id.progress_bar_text_view);
        advancedTextView.setValue(
        		Math.round(downloadItem.getPercentage()*100),
        		null
        		);
        
        TextView summaryHolder = (TextView) v.findViewById(R.id.download_item_summary);
        
        String summary = String.format(getStr(R.string.download_item_status), downloadItem.getStatus(), Math.round(downloadItem.getPercentage()*100),  downloadItem.getVolume());
        
        summaryHolder.setText(summary);
        
        TextView upSpeedHolder = (TextView) v.findViewById(R.id.download_item_up_speed);
        upSpeedHolder.setText(downloadItem.getDownSpeed());
        
        TextView downSpeedHolder = (TextView) v.findViewById(R.id.download_item_down_speed);
        downSpeedHolder.setText(downloadItem.getUpSpeed());
        
        TextView ststusHolder = (TextView) v.findViewById(R.id.download_item_status);
        ststusHolder.setText(downloadItem.getStatus());
        
        TextView volumeHolder = (TextView) v.findViewById(R.id.download_item_volume);
        volumeHolder.setText(downloadItem.getVolume());
        
        TextView timeOnHolder = (TextView) v.findViewById(R.id.download_item_time_on);
        timeOnHolder.setText(downloadItem.getTimeOnLine());
        
        final ImageButton menuButtonHolder = (ImageButton) v.findViewById(R.id.download_item_menu_button);
        menuButtonHolder.setOnClickListener(buildContextMenuOpener(downloadItem));
        		
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("showExpandedPref", false)){
        	expandItem(v);
        	return v;
        }
               
        v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				_onSelectItemListener.setDownloadItemSelected(downloadItem);
				
				if (currentExpandedItem != null)
					collapseItem(currentExpandedItem);
				if (v == currentExpandedItem){
					_onSelectItemListener.setDownloadItemSelected(null);
					currentExpandedItem = null;
				}else{
					expandItem(v);
					currentExpandedItem = v;
				}
				
			}
        	
        	
        });
        
        if (_onSelectItemListener.isItemSelected(downloadItem)){
        	expandItem(v);
        	currentExpandedItem = v;
        }
        
        return v;
	}
	
	private OnClickListener buildContextMenuOpener(final DownloadItem downloadItem){
		
		return new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				MenuBuilder builder = buildContextMenu(downloadItem);
				MenuPopupHelper helper = new MenuPopupHelper(getContext(), builder);
				helper.setAnchorView(v);
				helper.setForceShowIcon(true);
				
				helper.show();				
			}
		};	
	}
	
	private MenuBuilder buildContextMenu(DownloadItem downloadItem){
		
		MenuBuilder builder = new MenuBuilder(getContext());
		
		if (downloadItem.getStatus().equalsIgnoreCase("paused")){
			MenuItem resumeMenuItem = builder.add(getStr(R.string.context_menu_item_resume));
			resumeMenuItem.setIcon(R.drawable.ic_menu_resume);
			resumeMenuItem.setOnMenuItemClickListener(new OnClickDownloadItemListener(downloadItem, "start", getStr(R.string.command_info_part_resume)));				
		}else{				
			MenuItem suspendMenuItem = builder.add(getStr(R.string.context_menu_item_pause));
			suspendMenuItem.setIcon(R.drawable.ic_menu_pause);
			suspendMenuItem.setOnMenuItemClickListener(new OnClickDownloadItemListener(downloadItem, "paused", getStr(R.string.command_info_part_pause)));
		}
		
		MenuItem removeMenuItem = builder.add(getStr(R.string.context_menu_item_delete));
		removeMenuItem.setIcon(R.drawable.ic_menu_close_clear_cancel);
		removeMenuItem.setOnMenuItemClickListener(new OnClickDownloadItemListener(downloadItem, "cancel", getStr(R.string.command_info_part_delete), getStr(R.string.confirmation_message_delete)));
				
	    return builder;
	}
	
	private View currentExpandedItem;
	
	private void expandItem(View view){
		view.findViewById(R.id.view_additional_info_1).setVisibility(View.VISIBLE);
		view.findViewById(R.id.view_additional_info_2).setVisibility(View.VISIBLE);
		view.findViewById(R.id.download_item_summary).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.download_item_name)).setMaxLines(4);
	}
	
	private void collapseItem(View view){
		view.findViewById(R.id.view_additional_info_1).setVisibility(View.GONE);
		view.findViewById(R.id.view_additional_info_2).setVisibility(View.GONE);
		view.findViewById(R.id.download_item_summary).setVisibility(View.VISIBLE);
		((TextView)view.findViewById(R.id.download_item_name)).setMaxLines(1);
	}

	public interface OnSelectItemListener{
		boolean isItemSelected(DownloadItem item);
		void setDownloadItemSelected(DownloadItem item);
	}
	
	private String getStr(int resourceId){
		return getContext().getResources().getString(resourceId);
	}
	
	
	private class OnClickDownloadItemListener implements OnMenuItemClickListener{
		
		DownloadItem _item;
		String _command;
		String _postText;
		String _alertText;

		public OnClickDownloadItemListener(DownloadItem item, String command, String postText){
			_item = item;
			_command = command;
			_postText = postText;
			_alertText = null;
		}
		
		public OnClickDownloadItemListener(DownloadItem item, String command, String postText, String alertText){
			_item = item;
			_command = command;
			_postText = postText;
			_alertText = alertText;
		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (_alertText != null){
				
				new AlertDialog.Builder((ListActivity)getContext())
		           .setMessage(_alertText)
		           .setCancelable(false)
		           .setPositiveButton(getStr(R.string.basic_yes), new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   new SendCommandTask(getContext(), _command, _item.getId()).execute();	       			
		        		   Toast.makeText(
		        				   getContext(),
		        				   getStr(R.string.command_info_part_download) + " \"" + _item.getName() + "\" " + _postText + ".", Toast.LENGTH_SHORT).show();
		               }
		           })
		           .setNegativeButton(getStr(R.string.basic_no), null)
		           .show();

				
			}else{
				new SendCommandTask(getContext(), _command, _item.getId()).execute();
				Toast.makeText(
						getContext(),
						getStr(R.string.command_info_part_download) + " \"" + _item.getName() + "\" " + _postText + ".", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	}

}
