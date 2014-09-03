package com.example.rotaturistica;

import com.example.controller.ModeTrasmition;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.os.Build;

public class ModeTrasmitionActivity extends ActionBarActivity {
	
	RadioButton radioOn;
	RadioButton radioOff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mode_trasmition);
		
		ModeTrasmition mt = new ModeTrasmition();
		
		radioOn = (RadioButton) findViewById(R.id.radioOn);
		radioOff = (RadioButton) findViewById(R.id.radioOff);
		
		if(mt.getModoTransmissao(this) == 1){
			Log.w("","ModeTrasmiction - aqui 1");
			radioOff.setChecked(false);
			radioOn.setChecked(true);
		}
		else{
			Log.w("","ModeTrasmiction - aqui 2");
			radioOn.setChecked(false);
			radioOff.setChecked(true);
		}
			
	}

	public void changeMode(View view){
		if(view.getId() == R.id.radioOn){
			ModeTrasmition mt = new ModeTrasmition();
			if(mt.alteraModoDeTrasmissaoOn(this) == 1)
				Toast.makeText(getApplicationContext(), "Modo de transamissao ON-LINE ativado!", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), "Erro!", Toast.LENGTH_LONG).show();
		}else{
			ModeTrasmition mt = new ModeTrasmition();
			if(mt.alteraModoDeTrasmissaoOff(this) == 1)
				Toast.makeText(getApplicationContext(), "Modo de transamissao OFF-LINE ativado!", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), "Erro!", Toast.LENGTH_LONG).show();
		}
	}

}
