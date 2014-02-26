package br.com.lvc.defenser.entitie;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.lvc.worldwar.R;

public class EnemyDrawer implements MapDrawer {

	private Enemy enemy;
	 
	public EnemyDrawer(Enemy enemy) {
		super();
		this.enemy = enemy;
	}

 
	public View getView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.enemy_item, null);
		ImageView imageViewEnemy = (ImageView) view.findViewById(R.id.image_view_enemy_view);
		imageViewEnemy.setImageResource(enemy.getImageInRes());
		
		TextView textViewEnemy =  (TextView) view.findViewById(R.id.text_view_enemy_name);
		textViewEnemy.setText(enemy.getName());
		
		
		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_enemy_life);
		progressBar.setMax(enemy.getLifeMax());
		progressBar.setProgress(enemy.getLife());
		
		TextView textViewEnemyLife =  (TextView) view.findViewById(R.id.text_view_enemy_life);
		textViewEnemyLife.setText(String.valueOf(enemy.getLife()));
		
		return view;
	}
}
