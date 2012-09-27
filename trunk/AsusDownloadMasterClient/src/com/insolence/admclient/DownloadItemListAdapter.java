package com.insolence.admclient;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadItemListAdapter extends ArrayAdapter<DownloadItem> {

	private SherlockFragmentActivity _context;
	
	public DownloadItemListAdapter(SherlockFragmentActivity context) {
		super(context, R.layout.download_item);
		_context = context;
	}
	
	public void setData(ArrayList<DownloadItem> items) {
		clear();
		if (items != null) {
			items.trimToSize();
			for (DownloadItem item:items) {
				add(item);
			}
        }
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.download_item, null);
        }
        
        DownloadItem downloadItem = getItem(position);
        
        TextView nameHolder = (TextView) v.findViewById(R.id.download_item_name);
        nameHolder.setText(downloadItem.getName());
        
        ProgressBarTextView advancedTextView = (ProgressBarTextView) v.findViewById(R.id.progress_bar_text_view);
        advancedTextView.setValue(Math.round(downloadItem.getPercentage() * 100));
        
        TextView upSpeedHolder = (TextView) v.findViewById(R.id.download_item_up_speed);
        upSpeedHolder.setText(downloadItem.getUpSpeed());
        
        TextView downSpeedHolder = (TextView) v.findViewById(R.id.download_item_down_speed);
        downSpeedHolder.setText(downloadItem.getDownSpeed());
        
        TextView ststusHolder = (TextView) v.findViewById(R.id.download_item_status);
        ststusHolder.setText(downloadItem.getStatus());
        
        TextView volumeHolder = (TextView) v.findViewById(R.id.download_item_volume);
        volumeHolder.setText(downloadItem.getVolume());
        
        TextView timeOnHolder = (TextView) v.findViewById(R.id.download_item_time_on);
        timeOnHolder.setText(downloadItem.getTimeOnLine());
        
        ImageButton startButtonHolder = (ImageButton) v.findViewById(R.id.download_item_start_button);
        startButtonHolder.setOnClickListener(new OnClickDownloadItemListener(downloadItem, "start", "is queued for start.", null));
        
        ImageButton pauseButtonHolder = (ImageButton) v.findViewById(R.id.download_item_pause_button);
        pauseButtonHolder.setOnClickListener(new OnClickDownloadItemListener(downloadItem, "paused", "is queued for pause.", null));
        
        ImageButton deleteButtonHolder = (ImageButton) v.findViewById(R.id.download_item_delete_button);
        deleteButtonHolder.setOnClickListener(new OnClickDownloadItemListener(downloadItem, "cancel", "is queued for deletion.", "You are going to delete this torrent. Are you sure?"));
        
        return v;
	}
	
	private class OnClickDownloadItemListener implements OnClickListener {	
		DownloadItem _item;
		String _command;
		String _postText;
		String _alertText;
		
		public OnClickDownloadItemListener(DownloadItem item, String command, String postText, String alertText) {
			_item = item;
			_command = command;
			_postText = postText;
			_alertText = alertText;
		}
		
		public void onClick(View arg0) {
			if (_alertText != null) {
				Bundle args = new Bundle();
        		args.putString(ConfirmDialogFragment.COMMAND, _command);
        		args.putString(ConfirmDialogFragment.ITEM_ID, _item.getId());
        		args.putString(ConfirmDialogFragment.DIALOG_MSG, _alertText);
        		args.putString(ConfirmDialogFragment.TOAST_MSG, "Torrent \"" + _item.getName() + "\" " + _postText);
        		
        		((DownloadItemListActivity) _context).showConfirmDialog(ConfirmDialogFragment.LIST_ITEM_BUTTON_ID, args);
			}
			else {
				new SendCommandAsyncTask(_context).execute(_item.getId(), _command);
				Toast.makeText(_context, "Torrent \"" + _item.getName() + "\" " + _postText, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
