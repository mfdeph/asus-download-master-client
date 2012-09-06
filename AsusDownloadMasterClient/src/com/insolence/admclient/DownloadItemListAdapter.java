package com.insolence.admclient;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadItemListAdapter extends ArrayAdapter<DownloadItem>{

	private Context _context;
	
	public DownloadItemListAdapter(Context context) {
		super(context, R.layout.download_item, DownloadItemListManager.getInstance().getDownloadItems(false));
		_context = context; 
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
        advancedTextView.setValue(Math.round(downloadItem.getPercentage()*100));
        
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
        startButtonHolder.setOnClickListener(
        		new OnClickDownloadItemListener(downloadItem, "start", "is managed to start."));
        
        ImageButton pauseButtonHolder = (ImageButton) v.findViewById(R.id.download_item_pause_button);
        pauseButtonHolder.setOnClickListener(
        		new OnClickDownloadItemListener(downloadItem, "paused", "is managed to pause."));
        
        ImageButton deleteButtonHolder = (ImageButton) v.findViewById(R.id.download_item_delete_button);
        deleteButtonHolder.setOnClickListener(
        		new OnClickDownloadItemListener(downloadItem, "cancel", "is managed to delete.", "You are going to delete this torrent. Are you sure?"));
        
        return v;
	}

	
	private class OnClickDownloadItemListener implements OnClickListener{
		
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
		
		public void onClick(View arg0) {
			 
			if (_alertText != null){
				
				new AlertDialog.Builder((ListActivity)_context)
		           .setMessage(_alertText)
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   new SendCommandAsyncTask((ListActivity)_context, _command, _item.getId()).execute();	       			
		        		   Toast.makeText(
		        				   _context,
		        				   "Torrent \"" + _item.getName() + "\" " + _postText, Toast.LENGTH_SHORT).show();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();

				
			}else{
				new SendCommandAsyncTask((ListActivity)_context, _command, _item.getId()).execute();
				Toast.makeText(
					   _context,
					   "Torrent \"" + _item.getName() + "\" " + _postText, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
