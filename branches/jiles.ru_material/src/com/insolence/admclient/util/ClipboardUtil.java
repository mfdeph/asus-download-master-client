package com.insolence.admclient.util;

import android.content.Context;
import android.text.ClipboardManager;

public class ClipboardUtil {

	public static boolean TryGetTextFromClipboard(Context context, Holder<String> text){
		try{
	    	ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
	    	if (clipboard != null){
	    		CharSequence clipboardText = clipboard.getText();
	    		if (clipboardText != null && clipboardText.length() > 0){
	    			String textResult = clipboardText.toString();
	    			if (!textResult.startsWith("intent")){
	    				text.value = textResult;
	    				return true;
	    			}
	    		}	
	    	}
		}catch(Exception e){
			
		}
    	return false;
	}
	
}
