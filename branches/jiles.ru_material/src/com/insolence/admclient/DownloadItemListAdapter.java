package com.insolence.admclient;

import java.util.List;
import java.lang.String;
import java.util.HashMap;

import com.insolence.admclient.asynctasks.SendCommandTask;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.expandable.IExpandCollapseManager;
import com.todddavies.components.progressbar.ProgressWheel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
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
	
	private HashMap<String, String> _statusLocalizationMap = new HashMap<String, String>(); 
	
	public DownloadItemListAdapter(Context context, List<DownloadItem> downloadItems, IExpandCollapseManager expandCollapseManager) {
		super(context, R.layout.download_item_block_redisigned, downloadItems); 
		setExpandCollapseManager(expandCollapseManager);	
		initStatusLocalizationMap();
	}
	
	public void setExpandCollapseManager(IExpandCollapseManager expandCollapseManager){
		_expandCollapseManager = expandCollapseManager;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.download_item_block_redisigned, null);
        }
        
        final DownloadItem downloadItem = getItem(position);
        
        fillView(v, downloadItem);
        
        return v;
	}
	
    private void fillView(View v, DownloadItem downloadItem){
        
        TextView nameHolder = (TextView) v.findViewById(R.id.download_item_name);
        nameHolder.setText(downloadItem.getName());
        
        /*ProgressBarTextView advancedTextView = (ProgressBarTextView) v.findViewById(R.id.progress_bar_text_view);
        advancedTextView.setValue(
        		Math.round(downloadItem.getPercentage()*100),
        		null
        		);*/
        
        TextView summaryHolder = (TextView) v.findViewById(R.id.download_item_summary);
        
        String statusToShow = downloadItem.getStatus();
        
        if (_statusLocalizationMap.containsKey(downloadItem.getStatus())){
        	String localizedStatus = _statusLocalizationMap.get(downloadItem.getStatus());
        	if (!localizedStatus.equals(""))
        		statusToShow = localizedStatus;
        }
        
        String summary = String.format(getStr(R.string.download_item_status), statusToShow, Math.round(downloadItem.getPercentage()*100),  downloadItem.getVolume());
        
        summaryHolder.setText(summary);
        
        TextView upSpeedHolder = (TextView) v.findViewById(R.id.download_item_up_speed);
        upSpeedHolder.setText(downloadItem.getDownSpeed());
        
        TextView downSpeedHolder = (TextView) v.findViewById(R.id.download_item_down_speed);
        downSpeedHolder.setText(downloadItem.getUpSpeed());
        
        TextView ststusHolder = (TextView) v.findViewById(R.id.download_item_status);
        ststusHolder.setText(statusToShow);
        
        TextView volumeHolder = (TextView) v.findViewById(R.id.download_item_volume);
        volumeHolder.setText(downloadItem.getVolume());
        
        TextView timeOnHolder = (TextView) v.findViewById(R.id.download_item_time_on);
        timeOnHolder.setText(downloadItem.getTimeOnLine());
        
        View tipLayout = v.findViewById(R.id.download_item_circle);
        tipLayout.setBackgroundResource(getResourceForDownloadItemCircle(downloadItem));
        
        final ImageButton menuButtonHolder = (ImageButton) v.findViewById(R.id.download_item_menu_button);
        menuButtonHolder.setOnClickListener(buildContextMenuOpener(downloadItem));
        
        ProgressWheel progress = (ProgressWheel) v.findViewById(R.id.pw_spinner);
        progress.setProgress(Math.round(downloadItem.getPercentage()*360));
        
        TextView percentageHolder = (TextView) v.findViewById(R.id.download_item_percentage);
        percentageHolder.setText(Math.round(downloadItem.getPercentage()*100) + " %");
        		
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
			resumeMenuItem.setIcon(R.drawable.ic_play_circle_outline_grey600_48dp);
			resumeMenuItem.setOnMenuItemClickListener(new OnClickDownloadItemListener(downloadItem, "start", getStr(R.string.command_info_part_resume)));				
		}else{				
			MenuItem suspendMenuItem = builder.add(getStr(R.string.context_menu_item_pause));
			suspendMenuItem.setIcon(R.drawable.ic_pause_circle_outline_grey600_48dp);
			suspendMenuItem.setOnMenuItemClickListener(new OnClickDownloadItemListener(downloadItem, "paused", getStr(R.string.command_info_part_pause)));
		}
		
		MenuItem removeMenuItem = builder.add(getStr(R.string.context_menu_item_delete));
		removeMenuItem.setIcon(R.drawable.ic_highlight_remove_grey600_48dp);
		removeMenuItem.setOnMenuItemClickListener(new OnClickDownloadItemListener(downloadItem, "cancel", getStr(R.string.command_info_part_delete), getStr(R.string.confirmation_message_delete)));
				
	    return builder;
	}
	
	private String getStr(int resourceId){
		return getContext().getResources().getString(resourceId);
	}
	
	private void initStatusLocalizationMap(){
		String[] statusNative = getStrArray(R.array.TaskStatusNative);
		String[] statusLocalized = getStrArray(R.array.TaskStatusLocalized);		
		for (int i = 0; i < statusNative.length - 1; i++)
			_statusLocalizationMap.put(statusNative[i], statusLocalized[i]);		
	}
	
	private String[] getStrArray(int resourceId){
		return getContext().getResources().getStringArray(resourceId);
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
	
	
	private int getResourceForDownloadItemCircle(DownloadItem item){
		if (item.getStatus().equalsIgnoreCase("downloading"))
			return R.drawable.download_item_circle_downloading;
		if (item.getStatus().equalsIgnoreCase("seeding"))
			return R.drawable.download_item_circle_seeding;
		if (item.getStatus().contains("rror"))
			return R.drawable.download_item_circle_error;
		return R.drawable.download_item_circle_default;
	}

}
