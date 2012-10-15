package com.insolence.admclient.asynctasks;

import java.io.File;

import com.insolence.admclient.listmanagers.IProcessResultConsumer;

public class SendTorrentTask extends SendFileTaskBase{

	protected File _file;
	
	public SendTorrentTask(IProcessResultConsumer target, File file){
		super(target);
		_file = file;
	}
	
	@Override
	protected File getFile(){
		return _file;
	}

}
