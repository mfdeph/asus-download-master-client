package com.insolence.admclient;

import com.insolence.admclient.donation.DonationHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.text.InputType;

public class PreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnPreferenceClickListener{

	private DonationHelper donationHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);		
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }			
		donationHelper = new DonationHelper(getActivity());
	}

    private void initSummary(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory pCat = (PreferenceCategory) p;
            for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                initSummary(pCat.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }
    
    private void updatePrefSummary(Preference p) {

        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if ((editTextPref.getEditText().getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) == 0)
            	p.setSummary(editTextPref.getText());
        }
    }
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    donationHelper.unbindService(); 
	}
	
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        findPreference("donatePref").setOnPreferenceClickListener(this);
    }
    
    private void restartApp(){
    	Intent i = Preferences.Current.getPackageManager().getLaunchIntentForPackage(Preferences.Current.getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Preferences.Current.startActivity(i);
    }
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if (key.equals("languagePref")){
			restartApp();
			return;	
		}
		
		if (key.equals("displayModePref"))
			DownloadItemListActivity.resetExpandCollapseManager();
			
		Preference pref = findPreference(key);
		updatePrefSummary(pref);		
	}
	

	public boolean onPreferenceClick(Preference preference)
	{		
	    String key = preference.getKey();
	    if (key.equals("donatePref")){
	    	donationHelper.doDonation();
	    	return true;}
	    return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		   if (requestCode == DonationHelper.DonationRequestCode) {           
			   donationHelper.doDonationComplete(resultCode, data);
			   return;
		   }
		   super.onActivityResult(requestCode, resultCode, data);
	}
	
}
