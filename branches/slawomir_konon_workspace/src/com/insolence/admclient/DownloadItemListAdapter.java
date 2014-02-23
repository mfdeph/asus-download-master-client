package com.insolence.admclient;

import java.util.List;

import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuPopupHelper;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.insolence.admclient.asynctasks.SendCommandTask;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.expandable.IExpandCollapseManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadItemListAdapter extends ArrayAdapter<DownloadItem>{

	private IExpandCollapseManager _expandCollapseManager;
	
	public DownloadItemListAdapter(Context context, List<DownloadItem> downloadItems, IExpandCollapseManager expandCollapseManager) {
		super(context, R.layout.download_item_block, downloadItems); 
		setExpandCollapseManager(expandCollapseManager);
	}
	
	public void setExpandCollapseManager(IExpandCollapseManager expandCollapseManager){
		_expandCollapseManager = expandCollapseManager;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.download_item_block, null);
        }
        
        final DownloadItem downloadItem = getItem(position);
        
        fillView(v, downloadItem);
        
        return v;
	}
	
    private void fillView(View v, DownloadItem downloadItem){
        
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
        
        LinearLayout tipLayout = (LinearLayout) v.findViewById(R.id.download_item_tip);
        tipLayout.setBackgroundResource(getResourceForLeftLine(downloadItem));
        
        final ImageButton menuButtonHolder = (ImageButton) v.findViewById(R.id.download_item_menu_button);
        menuButtonHolder.setOnClickListener(buildContextMenuOpener(downloadItem));
        		
        _expandCollapseManager.setItemState(downloadItem, v);

        
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
				
				new AlertDialog.Builder(getContext())
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
	
	
	private int getResourceForLeftLine(DownloadItem item){
		if (item.getStatus().equalsIgnoreCase("downloading"))
			return R.drawable.download_item_tip_downloading;
		if (item.getStatus().equalsIgnoreCase("seeding"))
			return R.drawable.download_item_tip_seeding;
		if (item.getStatus().contains("rror"))
			return R.drawable.download_item_tip_error;
		return R.drawable.download_item_tip_default;
	}

}
