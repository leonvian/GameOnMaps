package br.com.lvc.worldwar;

import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import br.com.lvc.worldwar.entitie.Castle;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements InfoWindowAdapter {

	
	private Context context;
	private HashMap<Marker, Castle> hashMapMarkerCastle; //new HashMap<Marker, Castle>();

	public CustomInfoWindowAdapter(Context context, HashMap<Marker, Castle> hashMapMarkerCastle) {
		this.context = context;
		this.hashMapMarkerCastle = hashMapMarkerCastle;
	}

	@Override
	public View getInfoContents(Marker marker) {
		View myContentsView = LayoutInflater.from(context).inflate(R.layout.custom_info, null);
		Castle castle = hashMapMarkerCastle.get(marker);
		TextView textViewDescription = (TextView) myContentsView.findViewById(R.id.text_view_description);

		if(castle != null)
			textViewDescription.setText(castle.getName());
		else
			textViewDescription.setText(marker.getTitle());

		return myContentsView;
	}

	@Override
	public View getInfoWindow(Marker marker) {

		return null;//LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
	}

}
