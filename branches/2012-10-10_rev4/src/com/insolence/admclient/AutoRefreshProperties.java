package com.insolence.admclient;

public class AutoRefreshProperties {

	public AutoRefreshProperties(boolean autoRefreshEnabled, int autoRefreshInterval){
		_autoRefreshEnabled = autoRefreshEnabled;
		_autoRefreshInterval = autoRefreshInterval;
	}

	private boolean _autoRefreshEnabled;	
	private int _autoRefreshInterval;
	
	public boolean isAutoRefreshEnabled() {
		return _autoRefreshEnabled;
	}
	public void setAutoRefreshEnabled(boolean _autoRefreshEnabled) {
		this._autoRefreshEnabled = _autoRefreshEnabled;
	}
	public int getAutoRefreshInterval() {
		return _autoRefreshInterval;
	}
	public void setAutoRefreshInterval(int _autoRefreshInterval) {
		this._autoRefreshInterval = _autoRefreshInterval;
	}
	
	@Override
	public boolean equals(Object object){
		if (!(object instanceof AutoRefreshProperties))
			return false;
		AutoRefreshProperties otherObject = (AutoRefreshProperties)object;
		return(otherObject.getAutoRefreshInterval() == getAutoRefreshInterval() && otherObject.isAutoRefreshEnabled() == isAutoRefreshEnabled());
	}
}
