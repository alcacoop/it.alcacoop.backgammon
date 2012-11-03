package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;



public class Board extends Image {

	private TextureRegion region;
	
	public Board(){
		super();
		region = new TextureRegion(GnuBackgammon.texture, 0, 0, 760, 570);
		setRegion(region);
		setScaling(Scaling.none);
		setAlign(Align.LEFT+Align.BOTTOM);
	}

}
