package com.insolence.admclient.donation;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.AndroidException;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.insolence.admclient.R;

public class DonationHelper {

	public String IAP_DONATION_SKU = "donation";
	
	IInAppBillingService  mService;
	
	ServiceConnection mServiceConn = new ServiceConnection() {
		   @Override
		   public void onServiceDisconnected(ComponentName name) {
		       mService = null;
		   }

		   @Override
		   public void onServiceConnected(ComponentName name, 
		      IBinder service) {
		       mService = IInAppBillingService.Stub.asInterface(service);
		   }
		};
	
	Activity context;
	
	public DonationHelper(Activity context){
		this.context = context;
		Intent billingIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		billingIntent.setPackage("com.android.vending");
		context.bindService(billingIntent, mServiceConn, Context.BIND_AUTO_CREATE);
	}
	
    public void unbindService()  {
        if (mService != null) {
        	context.unbindService(mServiceConn);
        }  
    }
	
	public static int DonationRequestCode = 2381;
	
	public void doDonation(){		
		try {
			Bundle buyIntentBundle = mService.getBuyIntent(3, context.getPackageName(),	IAP_DONATION_SKU, "inapp", "");			
			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");			
			context.startIntentSenderForResult(pendingIntent.getIntentSender(), DonationRequestCode, new Intent(), 0, 0, 0);			
		} catch (AndroidException e) {
		}		
	}
	
	public void doDonationComplete(int resultCode, Intent data){
	      int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
	      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");	        
	      if (resultCode == Activity.RESULT_OK && responseCode == 0) {
	         try {
	            JSONObject jo = new JSONObject(purchaseData);
		        final String token = jo.getString("purchaseToken");
		        consumePurchase(token);
		        Toast.makeText(context, getStr(R.string.donation_succeed), Toast.LENGTH_LONG).show();
		        return;
	          }
	          catch (JSONException e) {
	          }
	      }
          Toast.makeText(context, getStr(R.string.donation_failed), Toast.LENGTH_LONG).show();
	}
	
	
	private void consumePurchase(final String token){
        new AsyncTask<Void, Void, Void>(){
			protected Void doInBackground(Void... params) {
				try {
					mService.consumePurchase(3, context.getPackageName(), token);
				} catch (RemoteException e) {
				}
				return null;
			}   	
        }.execute();		
	}

	
	private String getStr(int resourceId){
		return context.getResources().getString(resourceId);
	}
	

}
