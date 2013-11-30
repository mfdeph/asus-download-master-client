package com.insolence.admclient.asynctasks;

import java.io.File;

import android.content.Context;

public class SendTorrentTask extends SendFileTaskBase{

	protected File _file;
	
	public SendTorrentTask(Context context, File file){
		super(context);
		_file = file;
	}
	
	@Override
	protected File getFile(){
		return _file;
	}

}
