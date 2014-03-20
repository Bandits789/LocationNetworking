package com.android.locnet;

import java.io.File;
import java.io.FileOutputStream;
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

	private String logString = "";

	@Override
	public void onLocationChanged(Location loc) {
		Toast.makeText(
				getBaseContext(),
				"Location changed: Lat: " + loc.getLatitude() + " Lng: "
						+ loc.getLongitude(), Toast.LENGTH_SHORT).show();

		logString = "Longitude: " + loc.getLongitude() + "\n";
		logString += "Latitude: " + loc.getLatitude() + "\n";
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Write the logString to a file in sdcard
	 */
	public void writeToLog(View view) {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/myLogcat");
		dir.mkdirs();
		File file = new File(dir, "logcat.txt");

		try {
			// to write logcat in text file
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);

			// Write the string to the file
			osw.write(logString);
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Download the file and record latency and throughput
	 */
	public void download(View view) {
		String url = "http://web.mit.edu/21w.789/www/papers/griswold2004.pdf";
		int size = 650924;

		HttpGet request = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		HttpClient httpClient = new DefaultHttpClient(httpParameters);

		long beforeTime = System.currentTimeMillis();
		try {
			HttpResponse response = httpClient.execute(request);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long afterTime = System.currentTimeMillis();
		long timeDifference = afterTime - beforeTime;
		logString += "Latency: " + timeDifference + "\n";
		logString += "Throughput: " + size / (timeDifference * 1000) + "\n";

		Toast.makeText(
				getBaseContext(),
				"Latency: " + timeDifference + "\nThroughput: " + size
						/ (timeDifference * 1000), Toast.LENGTH_SHORT).show();
	}
}
