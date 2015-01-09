package com.insolence.admclient.entity;

import com.insolence.admclient.asynctasks.GetItemListResult;

public interface IGetItemListResultPostProcessor {
	
	void postProcessResult(GetItemListResult result);
	
}
