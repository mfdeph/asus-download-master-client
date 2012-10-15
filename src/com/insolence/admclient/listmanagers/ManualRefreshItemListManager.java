package com.insolence.admclient.listmanagers;

public class ManualRefreshItemListManager extends DownloadItemListManagerBase implements IManualRefreshable{

	public ManualRefreshItemListManager(
			IProcessResultConsumer processResultConsumer) {
		super(processResultConsumer);

	}

	@Override
	public void manualRefresh(){
		ExecuteItemListRequest();
	}
	
}
