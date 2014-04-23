package com.insolence.admclient.entity;

import java.util.ArrayList;
import java.util.List;

public class DownloadInfo {
	
	String torrentName;
	List<DownloadFileInfo> files;
	
	public DownloadInfo(String torrentName) {
		super();
		this.torrentName = torrentName;
		files = new ArrayList<DownloadFileInfo>();
	}

	public String getTorrentName() {
		return torrentName;
	}

	public List<DownloadFileInfo> getFiles() {
		return files;
	}
	
	public static DownloadInfo parse(String response){
		try{
			String scriptParameter = response.split("\"")[1];
			String[] parts = scriptParameter.split(",\\s#");
			String torrentName = parts[1].trim();
			DownloadInfo result = new DownloadInfo(torrentName);
			for (int i = 2; i < parts.length; i++){
				String[] subParts = parts[i].split("#");
				int length = subParts.length;
				String id = subParts[0];
				String fileSize = subParts[length - 2];
				String fileName = subParts[length - 1];
				for(int j = 1; j < length - 2; j++){
					if (!subParts[j].equalsIgnoreCase("") && !subParts[j].equalsIgnoreCase("none")){
						fileName = fileName + "/" + subParts[j].trim();
					}
				}
				
				result.getFiles().add(new DownloadFileInfo(id, fileName, fileSize));
			}
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
