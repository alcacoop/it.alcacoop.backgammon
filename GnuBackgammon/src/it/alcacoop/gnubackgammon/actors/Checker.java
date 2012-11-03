package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;



public class Checker extends Image {

	private TextureRegion region;
	
	public Checker(int type) {
		super();
		if (type==0) //WHITE
			region = new TextureRegion(GnuBackgammon.texture, 0, 570, 40, 40);
		else 
			region = new TextureRegion(GnuBackgammon.texture, 40, 570, 42, 40);
		setRegion(region);
		setScaling(Scaling.none);
		setAlign(Align.LEFT+Align.BOTTOM);
	}

}
