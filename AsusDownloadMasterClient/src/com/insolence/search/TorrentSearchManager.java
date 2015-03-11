package com.insolence.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import com.insolence.admclient.DownloadItemListActivity;
import com.insolence.admclient.entity.TorrentSearchResultItem;

public class TorrentSearchManager {
	
	private String searchFilter;
	private Context context;
	
	public TorrentSearchManager(Context context, String searchFilter){
		this.context = context;
		this.searchFilter = searchFilter;
	}
	
	public List<TorrentSearchResultItem> getResult(String query, int page) {

		List<TorrentSearchResultItem> result = new ArrayList<TorrentSearchResultItem>();
		
		try
			{
				String queryUtf = null;
				try {
					queryUtf = URLEncoder.encode(query, "utf-8");		
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					return result;
				}
				String url = "http://bitsnoop.com/search/" + searchFilter + "/" + queryUtf + "/c/d/" + page + "/";
				Response response = Jsoup.connect(url).timeout(3000).execute();
				if (response == null || response.statusCode() != 200) {
					return result;
				}
			    Document doc = response.parse();
			    
			    String h1 = doc.select("h1").text();
			    if (h1.equals("Aww shucks! Nothing was found.")){
			    	return result;
			    }

			    Elements notice = doc.select("li");
			    for (Element e : notice){
			    	
			    	String t = e.child(2).text();
					String size2 = null;
					String size1 = null;
					String size0 =null;
					String href=null;
					String sz = null;
					String seeders = null;
					
					sz = e.select("[id=sz]").text();
					seeders = e.select("[title=seeders]").text();
					href =  e.select("a[href]").attr("href").toString();
							
					if (seeders.equals(""))
						seeders = "0";
					
					String []size = sz.split(" ");
					
					int haha=0;
					for (String size_row : size){
						haha++;
						if(haha==1) size1 = size_row;
						if(haha==2) size2 = size_row;
						
					}
					size0 = size1 + " " + size2;	//ex: 23 GB
					
					result.add(new TorrentSearchResultItem(t, size0, seeders, href));
					
			    }
				return result;			    
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		return result;	
	}
	
	public Links getLink(String string){		
		String []code = string.split("/");
		String ul = code[1];
		try {
			ul = URLEncoder.encode(ul, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			ul = string;
			e1.printStackTrace();
		}
		
		try {
			Response response = Jsoup.connect("http://bitsnoop.com/" + ul).timeout(3000).execute();
			Document doc = response.parse();		    
			Elements hrefs = doc.select("[title=Magnet Link]");
			Elements tors = doc.select("[title=Torrage.com]");
			if (hrefs == null){
			    response = Jsoup.connect(string).timeout(3000).execute();
			    doc = response.parse();
			    hrefs = doc.select("[title=Magnet Link]");
				tors = doc.select("[title=Torrage.com]");
			}
			    	
			Links result = new Links();
			
			result.MagnetLink = hrefs.attr("href");
			result.TorrentLink = tors.attr("href");
			
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public class Links{
		public String MagnetLink;
		public String TorrentLink;
	}
	
	
	
	public void openLinks(TorrentSearchResultItem item, Links links) {
		
		if (links.MagnetLink != null && !links.MagnetLink.equals("")){
			Intent downloadActivity = new Intent(context, DownloadItemListActivity.class);
			downloadActivity.setData(Uri.parse(links.MagnetLink));
			context.startActivity(downloadActivity);
			return;
		}else if(links.TorrentLink != null && !links.TorrentLink.equals("")){
			downloadFile(item, links.TorrentLink);
		}
	}

	String androidDataDir = Environment.getExternalStorageDirectory().toString() + "/DMTorrents";
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void downloadFile(TorrentSearchResultItem item, String torrentLink){
	    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(torrentLink));
	    request.setDescription(item.getTitle());
	    request.setTitle(item.getTitle() + ".torrent");                
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        request.allowScanningByMediaScanner();
	        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
	    }
	    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, androidDataDir);
	    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
	    manager.enqueue(request);
	}
	
}
