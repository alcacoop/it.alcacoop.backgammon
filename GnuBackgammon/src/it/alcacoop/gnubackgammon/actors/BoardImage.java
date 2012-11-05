package it.alcacoop.gnubackgammon.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;



public class BoardImage extends Image {

	private TextureRegion region;
	
	public BoardImage(){
		super();
		
		TextureAtlas atlas;
		atlas = new TextureAtlas(Gdx.files.internal("data/pack"));
		region = atlas.findRegion("board");
		
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		setRegion(region);
		setScaling(Scaling.none);
		setAlign(Align.LEFT+Align.BOTTOM);
	}

}
