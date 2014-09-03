package com.example.rotaturistica;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.entidade.TipoServico;
import com.example.utils.GMapV2Direction;
import com.example.utils.HttpConnection;
import com.example.utils.PathJSONParser;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Join;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Mapa extends FragmentActivity implements LocationListener {

	private  LatLng VOCE;
	private LatLng DESTINO;
	private long idTipo = 0;
	private int icone = 0;
	private String nomeItem = "";


	private LocationManager locationManager;
	Marker voceMarker;
	private ProgressDialog dialog;
	private GoogleMap map;
	private boolean pesquisar = true;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapa);
		
		SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		
		map = mapFrag.getMap();
		
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		Intent intent = getIntent();
		long idItem = intent.getLongExtra("idItem",0);
		idTipo = intent.getLongExtra("idTipo",0);
		double latitude = intent.getDoubleExtra("latitude", 0);
		double longitude = intent.getDoubleExtra("longitude", 0);
		nomeItem = intent.getStringExtra("nome");
		icone = intent.getIntExtra("icone", 0);
		
		DESTINO = new LatLng(latitude, longitude);
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(DESTINO,5));
		
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		getActionBar().setTitle("Localizacao - " + nomeItem);

		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle("Aguarde");
		dialog.setMessage("Pesquisando ponto");
		dialog.setCancelable(false);
		dialog.setMax(800);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				pesquisar = false;
			}
		});
		dialog.show();
		
		
		
		if(pesquisar){

			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

			Listener listener = new Listener();

			long tempoAtualizacao = 0;
			float distancia = 0;

			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
		}

	}
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    Toast.makeText(getApplicationContext(), "Não há conexao com à internet. Impossível construir rota", Toast.LENGTH_LONG).show();
		    return false;
	}

	private String getMapsApiDirectionsUrl() {
		String waypoints = "waypoints=optimize:true|"
				+ VOCE.latitude + "," + VOCE.longitude
				+ "|" + "|" + DESTINO.latitude + ","
				+ DESTINO.longitude;

		String sensor = "sensor=false";
		String mode = "mode=driving";
		String params = waypoints + "&" + sensor + "&" + mode;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params;
		return url;
	}

	private void addMarkers(Listener listener) {
		dialog.dismiss();
		if (map != null) {
			BitmapDescriptor iconVoce = BitmapDescriptorFactory.fromResource(R.drawable.tourist);
			BitmapDescriptor iconDestino = BitmapDescriptorFactory.fromResource(icone);

			if(voceMarker != null)
				voceMarker.remove();

			voceMarker = map.addMarker(new MarkerOptions().position(VOCE)
					.title("Você").icon(iconVoce));
			map.addMarker(new MarkerOptions().position(DESTINO)
					.title(nomeItem).icon(iconDestino));
		}
		
		locationManager.removeUpdates(listener);
	}


	private class ReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			String data = "";
			try {
				HttpConnection http = new HttpConnection();
				data = http.readUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject jObject = null;
			try {
				jObject = new JSONObject(result);
				if(!jObject.getString("status").equalsIgnoreCase("ZERO_RESULTS")){
					super.onPostExecute(result);
					new ParserTask().execute(result);
				}else{
					Toast.makeText(getApplicationContext(), "Impossível construir rota.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				PathJSONParser parser = new PathJSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;

			// traversing through routes
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(2);
				polyLineOptions.color(Color.BLUE);
			}

			map.addPolyline(polyLineOptions);
		}
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	private class Listener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			String latitudeStr = String.valueOf(location.getLatitude());
			String longitudeStr = String.valueOf(location.getLongitude());
			VOCE = new LatLng(location.getLatitude(), location.getLongitude());
			
			if(isOnline()){
				String url = getMapsApiDirectionsUrl();
				ReadTask downloadTask = new ReadTask();
				downloadTask.execute(url);
			}
			
			addMarkers(this);
		}

		

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}


}




