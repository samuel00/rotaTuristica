//package com.example.rotaturistica;
//
//import android.app.Activity;
//import android.content.Context;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.widget.TextView;
//
//public class LocationActivity extends Activity{
//	
//	private LocationManager locationManager;
//	private TextView latitude, longitude, provedor;
//
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.mapa);
//		
//		latitude = (TextView) findViewById(R.id.latitude);
//		longitude = (TextView) findViewById(R.id.longitude);
//		provedor = (TextView) findViewById(R.id.provedor);
//
//		
//		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//		
//		Listener listener = new Listener();
//		
//		long tempoAtualizacao = 0;
//		float distancia = 0;
//		
//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);
//		
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
//		
//		}
//	
//	private class Listener implements LocationListener {
//
//		@Override
//		public void onLocationChanged(Location location) {
//			String latitudeStr = String.valueOf(location.getLatitude());
//			String longitudeStr = String.valueOf(location.getLongitude());
//			provedor.setText(location.getProvider());
//			latitude.setText(latitudeStr);
//			longitude.setText(longitudeStr);
//		}
//
//		@Override
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onProviderEnabled(String provider) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onProviderDisabled(String provider) {
//			// TODO Auto-generated method stub
//
//		}
//
//	}
//}