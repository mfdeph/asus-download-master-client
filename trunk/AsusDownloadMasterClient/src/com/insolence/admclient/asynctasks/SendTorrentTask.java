package com.insolence.admclient.asynctasks;

import java.io.File;

public class SendTorrentTask extends SendFileTaskBase{

	protected File _file;
	
	public SendTorrentTask(File file){
		super();
		_file = file;
	}
	
	@Override
	protected File getFile(){
		return _file;
	}

}
