package br.com.lvc.defenser.entitie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import br.com.lvc.worldwar.R;
import br.com.lvc.worldwar.entitie.Castle;

public class CastleDrawer implements MapDrawer {

	private Castle castle;
	
	public CastleDrawer(Castle castle) {
		this.castle = castle;
	}

	@Override
	public View getView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.default_map_item, null);
		TextView textViewName = (TextView) view.findViewById(R.id.text_view_name);
		textViewName.setText(castle.getName());
		return view;
	}

}
