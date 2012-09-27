package com.insolence.admclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ConfirmDialogFragment extends SherlockDialogFragment {
	//Extra fields
	public static final String ID = "id";
	public static final String DIALOG_MSG = "dialogMsg";
	public static final String TOAST_MSG = "toastMsg";
	public static final String COMMAND = "command";
	public static final String ITEM_ID = "itemId";
	public static final String URI_STRING = "uriString";
	public static final String MAGNET_LINK = "magnetLink";
	public static final String FILE_NAME = "fileName";
	public static final String SCHEME = "scheme";
	
	public static final int LOAD_TORRENT_ID = 0;
	public static final int DELETE_FINISHED_ID = 1;
	public static final int LIST_ITEM_BUTTON_ID = 2;
	
	
	public static ConfirmDialogFragment newInstance(int id, Bundle args) {
		args.putInt(ID, id);
		ConfirmDialogFragment frag = new ConfirmDialogFragment();
		frag.setCancelable(false);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setStyle(SherlockDialogFragment.STYLE_NO_TITLE, R.style.MyDialogTheme);
		
		final SherlockFragmentActivity activity = getSherlockActivity();
		
		final Bundle args = getArguments();
		
		final int id = args.getInt("id");
		final String command = args.getString(COMMAND);
		final String itemId = args.getString(ITEM_ID);
		final String toastMsg = args.getString(TOAST_MSG);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(args.getString(DIALOG_MSG));
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				switch(id) {
				case LOAD_TORRENT_ID:
					new SendFileAsyncTask(activity).execute(args);	
					break;
				case DELETE_FINISHED_ID:
					new SendCommandAsyncTask(activity).execute(null, command);	
					break;
				case LIST_ITEM_BUTTON_ID:
					new SendCommandAsyncTask(activity).execute(itemId, command);	       			
					break;
				}
				
				Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT);
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
}