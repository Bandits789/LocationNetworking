package com.android.locnet;

import java.io.File;
import java.io.FileWriter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.android.helloworld.R;

public class MainActivity extends Activity implements LocationListener {

	private String logLocs = "";
	private String logNets = ""; 
	private LocationManager locationManager;
	File fileLoc;
	File fileNet; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Make directory for writing to log files
	    File sdCard = Environment.getExternalStorageDirectory();
	    File dir = new File(sdCard.getAbsolutePath() + "/myLogcat");
	    dir.mkdirs(); 
	    fileLoc = new File(dir, "logLocation.txt");
	    fileNet = new File(dir, "logNetwork.txt"); 
	    
	    try {
			// to write logcat in text file
			FileWriter f = new FileWriter(fileLoc, false);
			FileWriter w = new FileWriter(fileNet, false);
	
			// Write the string to the file
			f.write("");
			f.flush();
			f.close();
			
			w.write("");
			w.flush();
			w.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location loc) {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		boolean GPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean WIFI = mWifi.isConnected();
		
		String provider = "";
		if (GPS) {
			provider = "GPS";
		} else if (WIFI) {
			provider = "WIFI";
		} else {
			provider = "CELL TOWERS";
		}
		
		// every time location is changed, write this to the log
		Toast.makeText(
				getBaseContext(), provider + "\n" +
				"Location changed: Lat: " + loc.getLatitude() + " Lng: "
						+ loc.getLongitude(), Toast.LENGTH_SHORT).show();

		
		logLocs = provider + "\n";
		logLocs += "Longitude: " + loc.getLongitude() + "\n";
		logLocs += "Latitude: " + loc.getLatitude() + "\n";
		logLocs += "Accuracy:" + loc.getAccuracy();
		logLocs += "\n\n\n";
	}

	/**
	 * Write the logString to a file in sdcard, every time this is clicked it
	 * adds several newlines to tell the output apart
	 */
	public void writeToLog(View view) {
		boolean GPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean WIFI = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				
		if (GPS) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					5000, 0, this);
		} else if (WIFI) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					5000, 0, this);
		} else {
			locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
					5000, 0, this);
		}
		
		try {
			FileWriter f = new FileWriter(fileLoc, true);

			// Write the string to the file
			f.write(logLocs);
			f.flush();
			f.close();
			
			Toast.makeText(
					getBaseContext(),logLocs, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Download the file and record latency (time it took) and throughput
	 * (size/secs)
	 */
	public void download(View view) {
		String url = "http://web.mit.edu/21w.789/www/papers/griswold2004.pdf";
		int size = 650924;

		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		request.setDescription("Some description");
		request.setTitle("Some title");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");

		// get download service and enqueue file
		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		long beforeTime = System.currentTimeMillis();
		manager.enqueue(request);

		long afterTime = System.currentTimeMillis();
		long timeDifference = afterTime - beforeTime;
		// latency = how much time this took
		// throughput = size/secs
		logNets = "Latency: " + timeDifference + "\n";
		logNets += "Throughput: " + size / (timeDifference * 1000);
		logNets += "\n\n\n";
		
		try {
            // write log to text file
			FileWriter f = new FileWriter(fileNet, true);

            // Write the string to the file
            f.write(logNets);
            f.flush();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

		Toast.makeText(
				getBaseContext(),
				"Latency: " + timeDifference + "\nThroughput: " + size
						/ (timeDifference * 1000), Toast.LENGTH_SHORT).show();
	}
	
	public void confirmation(View view) {
		try {
			FileWriter f = new FileWriter(fileLoc, true);

			// Write the string to the file
			f.write("OKAY TEST\n\n");
			f.flush();
			f.close();
			
			Toast.makeText(
					getBaseContext(),"OKAY TEST", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
