package it.alcacoop.gnubackgammon.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;



public class Checker extends Image {

	private TextureRegion region;
	
	public Checker(int type) {
		super();
		
		TextureAtlas atlas;
		atlas = new TextureAtlas(Gdx.files.internal("data/pack"));
		
		if (type==0) //WHITE
			region = atlas.findRegion("cw");
		else 
			region = atlas.findRegion("cb");
		
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		setRegion(region);
		setScaling(Scaling.none);
		setAlign(Align.LEFT+Align.BOTTOM);
	}
}
