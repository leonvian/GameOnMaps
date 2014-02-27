package br.com.lvc.defenser.entitie;

import br.com.lvc.worldwar.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class TowerDrawer implements MapDrawer {
	
	Tower tower;
	public TowerDrawer(Tower tower) {
		this.tower = tower;
	}
	@Override
	public View getView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.default_map_item, null);
		TextView textViewName = (TextView) view.findViewById(R.id.text_view_name);
		textViewName.setText(tower.getName());
		return view;
	}

}
