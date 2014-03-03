package br.com.lvc.defenser;

import android.os.Bundle;
import android.view.View;
import br.com.lvc.utility.screen.BaseFragmentActivity;
import br.com.lvc.worldwar.R;

public class MainMenuDefense extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu_defense_maps);
	}

	public void onClickLoadGame(View view) {
		goToNextScreen(DefenseMaps.class);
	}

	public void onClickNewGame(View view) {
		goToNextScreen(DefenseMaps.class);
	}

}