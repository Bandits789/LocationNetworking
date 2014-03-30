package com.android.locnet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.android.helloworld.R;

public class MainActivity extends Activity implements LocationListener {

	private String logLocs = "";
	private String logNets = ""; 
	File fileLoc;
	File fileNet; 
	
	@Override
	public void onLocationChanged(Location loc) {
		// every time location is changed, write this to the log
		Toast.makeText(
				getBaseContext(),
				"Location changed: Lat: " + loc.getLatitude() + " Lng: "
						+ loc.getLongitude(), Toast.LENGTH_SHORT).show();

		logLocs = "Longitude: " + loc.getLongitude() + "\n";
		logLocs += "Latitude: " + loc.getLatitude() + "\n";
		logLocs += "\n\n\n";
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 10, this);
		
		// Make directory for writing to log files
	    File sdCard = Environment.getExternalStorageDirectory();
	    File dir = new File(sdCard.getAbsolutePath() + "/myLogcat");
	    dir.mkdirs(); 
	    fileLoc = new File(dir, "logLocation.txt");
	    fileNet = new File(dir, "logNetwork.txt"); 
	    
	    try {
			// to write logcat in text file
			FileWriter f = new FileWriter(fileLoc, false);

			// Write the string to the file
			f.write("");
			f.flush();
			f.close();
			
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

	/**
	 * Write the logString to a file in sdcard, every time this is clicked it
	 * adds several newlines to tell the output apart
	 */
	public void writeToLog(View view) {
		
		try {
			// to write logcat in text file
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
	    logNets += "\n\n\n";

		String url = "http://web.mit.edu/21w.789/www/papers/griswold2004.pdf";
		int size = 650924;

		// setup
		HttpGet request = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		HttpClient httpClient = new DefaultHttpClient(httpParameters);

		// time the download
		long beforeTime = System.currentTimeMillis();
		try {
			HttpResponse response = httpClient.execute(request);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long afterTime = System.currentTimeMillis();
		long timeDifference = afterTime - beforeTime;
		// latency = how much time this took
		// throughput = size/secs
		logNets += "Latency: " + timeDifference + "\n";
		logNets += "Throughput: " + size / (timeDifference * 1000) + "\n";
		
		try {
            // write log to text file
            FileOutputStream fOut = new FileOutputStream(fileNet);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(logNets);
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

		Toast.makeText(
				getBaseContext(),
				"Latency: " + timeDifference + "\nThroughput: " + size
						/ (timeDifference * 1000), Toast.LENGTH_SHORT).show();
	}
}
