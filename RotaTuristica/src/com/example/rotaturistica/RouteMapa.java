package com.example.rotaturistica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.controller.ModeTrasmition;
import com.example.controller.RotaController;
import com.example.entidade.OrdemPontoRota;
import com.example.entidade.PontoLocalizavel;
import com.example.entidade.RotaTuristica;
import com.example.utils.EnderecoServidor;
import com.example.utils.HttpConnection;
import com.example.utils.PathJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class RouteMapa  extends FragmentActivity{

	private List<OrdemPontoRota> listaDePontoRota;
	List<PontoLocalizavel> listaDePontoLocalizavel;
	private LocationManager locationManager;
	private long id;
	private long idTipo;
	private int icone;
	private  LatLng VOCE;
	private boolean pesquisar = true;
	
	Marker marker;
	private GoogleMap map;
	private ProgressDialog dialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		id = intent.getLongExtra("idItem",0);
		idTipo = intent.getLongExtra("idTipo",0);
		icone = intent.getIntExtra("icone",0);
		String rotaNome = intent.getStringExtra("nome");
		
		getActionBar().setTitle("Rota - " + rotaNome);
		
		dialog = new ProgressDialog(RouteMapa.this);
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

		ModeTrasmition mt = new ModeTrasmition();
		if(mt.getModoTransmissao(this) == 1){
			new RotaSelecionadaTask().execute();
		}else{
			setContentView(R.layout.mapa);
			
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			
			map = mapFrag.getMap();

			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
			
			LatLng estadoDoPara = new LatLng(-4.915833,-52.829591);

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(estadoDoPara,5));
			
			this.listaDePontoLocalizavel = getRota(idTipo);
			
			if(!this.listaDePontoLocalizavel.isEmpty()){
				LatLng zoom = new LatLng(this.listaDePontoLocalizavel.get(0).getLatitude(), this.listaDePontoLocalizavel.get(0).getLongitude());
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(zoom,15));
				map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
				if(isOnline()){
					for(int i=0; i < this.listaDePontoLocalizavel.size()-1; i++){
						LatLng origem =  new LatLng(this.listaDePontoLocalizavel.get(i).getLatitude(),this. listaDePontoLocalizavel.get(i).getLongitude());
						LatLng destino =  new LatLng(this.listaDePontoLocalizavel.get(i+1).getLatitude(), this.listaDePontoLocalizavel.get(i+1).getLongitude());
						String url = getMapsApiDirectionsUrl(origem, destino);
						ReadTask downloadTask = new ReadTask();
						downloadTask.execute(url);
					}
				}
				addMarkers(this.listaDePontoLocalizavel);
				
				if(pesquisar){

					locationManager = (LocationManager) this.getSystemService(RouteMapa.this.LOCATION_SERVICE);

					Listener listener = new Listener();

					long tempoAtualizacao = 0;
					float distancia = 0;

					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);

					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
				}
				
				
			}else{
				Toast.makeText(getApplicationContext(), "É necessário baixar servicos e atrativos.", Toast.LENGTH_LONG).show();
			}
		}
		
		
	}
	
	
	private void constroiRotaPontoMaisProximo(	List<PontoLocalizavel> listaDePontoLocalizavel) {
		VOCE = new LatLng(-1.457816, -48.491655);
		LatLng pontoProximo = getPontoMaisProximo(listaDePontoLocalizavel);
		addMarkers(VOCE);
		String url = getMapsApiDirectionsUrl(VOCE, pontoProximo);
		ReadTask downloadTask = new ReadTask();
		downloadTask.execute(url);
		
	}

	

	private LatLng getPontoMaisProximo(	List<PontoLocalizavel> listaDePontoLocalizavel) {
		int menorDistancia = 0;
		int posicao = 0;
		int aux = 0;
		Location locationA = new Location("A");
		locationA.setLatitude(VOCE.latitude);
		locationA.setLongitude(VOCE.longitude);
		for(int i = 0; i < listaDePontoLocalizavel.size(); i ++){
			Location locationB = new Location("B");
			
			locationB.setLatitude(listaDePontoLocalizavel.get(i).getLatitude());
			locationB.setLongitude(listaDePontoLocalizavel.get(i).getLongitude());
			
			aux = (int) locationA.distanceTo(locationB);
			
			if(menorDistancia < aux){
				menorDistancia = aux;
				posicao = i;
			}
		}
		LatLng ponto = new LatLng(listaDePontoLocalizavel.get(posicao).getLatitude(), listaDePontoLocalizavel.get(posicao).getLongitude());
		
		return ponto;
	}
	
	private void addMarkers(LatLng voce) {
		BitmapDescriptor iconePonto = BitmapDescriptorFactory.fromResource(R.drawable.tourist);

		LatLng ponto =  new LatLng(voce.latitude, voce.longitude);
		map.addMarker(new MarkerOptions().position(ponto).title("Você").icon(iconePonto));
		dialog.dismiss();
	}

	private List<PontoLocalizavel> getRota(long id) {
		RotaController controller = new RotaController();
		return controller.getRota(this,id);
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
			super.onPostExecute(result);
			new ParserTask().execute(result);
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

	private void addMarkers(List<PontoLocalizavel> listaDePontoLocalizavel) {
		BitmapDescriptor iconePonto = BitmapDescriptorFactory.fromResource(icone);
		for(int i=0; i < listaDePontoLocalizavel.size(); i++){
			LatLng ponto =  new LatLng(listaDePontoLocalizavel.get(i).getLatitude(), listaDePontoLocalizavel.get(i).getLongitude());
			map.addMarker(new MarkerOptions().position(ponto).title(listaDePontoLocalizavel.get(i).getNome()).icon(iconePonto).snippet("12"));
		}

	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		Toast.makeText(getApplicationContext(), "Não há conexao com à internet", Toast.LENGTH_LONG).show();
		return false;
	}

	private String getMapsApiDirectionsUrl(LatLng origem, LatLng destino) {
		String waypoints = "waypoints=optimize:true|"
				+ origem.latitude + "," + origem.longitude
				+ "|" + "|" + destino.latitude + ","
				+ destino.longitude;

		String sensor = "sensor=false";
		String mode = "mode=walking";
		String params = waypoints + "&" + sensor + "&" + mode;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params;
		return url;
	}

	private class RotaSelecionadaTask extends AsyncTask<String, Void, String[]>{

		@Override
		protected String[] doInBackground(String... params) {
			String rotaJson = rotaJson(id);
			try {
				JSONObject jsonObject = new JSONObject(rotaJson);
				jsonObject = new JSONObject(jsonObject.getString("rota"));
				JSONArray jsonArray = new JSONArray(jsonObject.getString("listaDePontoRota"));
				jsonObject = jsonArray.getJSONObject(0);
				jsonArray = new JSONArray(jsonObject.getString("ponto_rota"));
				String [] json = new String[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject objectPontoRota = jsonArray.getJSONObject(i);
					objectPontoRota = new JSONObject(objectPontoRota.getString("pontoLocalizavel"));
					String latitude= objectPontoRota.getString("latitude");
					String longitude = objectPontoRota.getString("longitude");
					String nome = objectPontoRota.getString("nome");
					String idPonto = objectPontoRota.getString("id");
					json [i] = latitude + "---" + longitude + "---" + nome + "---" + idPonto;
				}
				return json;
			} catch (Exception e) {
				Log.w(JsonActivity.class.getName(), "Deu erro");
				e.printStackTrace();
			}

			return null;
		}

		private String rotaJson(long id) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			EnderecoServidor es = new EnderecoServidor();
			HttpGet httpGet = new HttpGet(es.getEndereco()+"ponto_rotas/"+id);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					Log.e(JsonActivity.class.toString(), "Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return builder.toString();
		}

		protected void onPostExecute(String[] result) {
			setContentView(R.layout.mapa);
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			map = mapFrag.getMap();

			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

			LatLng estadoDoPara = new LatLng(-4.915833,-52.829591);

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(estadoDoPara,5));

			listaDePontoLocalizavel = getRota(result);

			if(isOnline()){
				for(int i=0; i < listaDePontoLocalizavel.size()-1; i++){
					LatLng origem =  new LatLng(listaDePontoLocalizavel.get(i).getLatitude(), listaDePontoLocalizavel.get(i).getLongitude());
					LatLng destino =  new LatLng(listaDePontoLocalizavel.get(i+1).getLatitude(), listaDePontoLocalizavel.get(i+1).getLongitude());
					String url = getMapsApiDirectionsUrl(origem, destino);
					ReadTask downloadTask = new ReadTask();
					downloadTask.execute(url);
				}
			}

			addMarkers(this,listaDePontoLocalizavel);
			
			
			
			if(pesquisar){

				locationManager = (LocationManager) RouteMapa.this.getSystemService(RouteMapa.this.LOCATION_SERVICE);

				Listener listener = new Listener();

				long tempoAtualizacao = 0;
				float distancia = 0;

				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);

				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
			}
			
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
			{
				@Override
				public void onInfoWindowClick(Marker arg0) {
					Log.w("clicks","You Clicked B1 " + arg0.getTitle());
				}
			}); 

		}
		

		private String getMapsApiDirectionsUrl(LatLng origem, LatLng destino) {
			String waypoints = "waypoints=optimize:true|"
					+ origem.latitude + "," + origem.longitude
					+ "|" + "|" + destino.latitude + ","
					+ destino.longitude;

			String sensor = "sensor=false";
			String mode = "mode=walking";
			String params = waypoints + "&" + sensor + "&" + mode;
			String output = "json";
			String url = "https://maps.googleapis.com/maps/api/directions/"
					+ output + "?" + params;
			return url;
		}

		private void addMarkers(RotaSelecionadaTask rotaSelecionadaTask,List<PontoLocalizavel> listaDePontoLocalizavel) {
			BitmapDescriptor iconePonto = BitmapDescriptorFactory.fromResource(icone);
			for(int i=0; i < listaDePontoLocalizavel.size(); i++){
				String nome = listaDePontoLocalizavel.get(i).getNome();
				long id = listaDePontoLocalizavel.get(i).getId();
				LatLng ponto =  new LatLng(listaDePontoLocalizavel.get(i).getLatitude(), listaDePontoLocalizavel.get(i).getLongitude());
				map.addMarker(new MarkerOptions().position(ponto).title(nome).icon(iconePonto));
			}

		}


		private List<PontoLocalizavel> getRota(String[] result) {

			List<PontoLocalizavel> pontoLocalizavel = new ArrayList<PontoLocalizavel>();

			for(int i = 0; i < result.length; i++){

				PontoLocalizavel ponto = new PontoLocalizavel();

				String[] split = result[i].split("---");
				ponto.setLatitude(Double.parseDouble(split[0]));
				ponto.setLongitude(Double.parseDouble(split[1]));
				ponto.setId(Long.parseLong(split[3]));
				ponto.setNome(split[2]);
				pontoLocalizavel.add(ponto);

			}
			return pontoLocalizavel;
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
				super.onPostExecute(result);
				new ParserTask().execute(result);
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
		
	}
	private class Listener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			VOCE = new LatLng(location.getLatitude(), location.getLongitude());
			addMarkers(VOCE);
			if(isOnline()){
				LatLng pontoProximo = getPontoMaisProximo(listaDePontoLocalizavel);
				String url = getMapsApiDirectionsUrl(VOCE,pontoProximo);
				ReadTask downloadTask = new ReadTask();
				downloadTask.execute(url);
			}else{
				dialog.dismiss();
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


}


}
