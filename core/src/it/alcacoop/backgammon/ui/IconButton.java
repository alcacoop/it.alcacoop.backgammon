/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package it.alcacoop.backgammon.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class IconButton extends Button {
	private final Label label;
	private TextButtonStyle style;

	
	public IconButton (String text, TextureRegion icon, TextButtonStyle tl) {
		super(tl);
		this.style = tl;
		label = new Label(text, new LabelStyle(tl.font, tl.fontColor));
		label.setAlignment(Align.center);
		Image icon1 = new Image(icon);
		Image icon2 = new Image(icon);

		add(icon1).expandY();
		add(label).expand().fill();
		add(icon2).expandY();
		
		setWidth(getPrefWidth());
		setHeight(getPrefHeight());
	}

	public void setStyle (ButtonStyle style) {
		if (!(style instanceof TextButtonStyle)) throw new IllegalArgumentException("style must be a TextButtonStyle.");
		super.setStyle(style);
		this.style = (TextButtonStyle)style;
        if (label != null) {
            TextButtonStyle textButtonStyle = (TextButtonStyle) style;
            LabelStyle labelStyle = label.getStyle();
            labelStyle.font = textButtonStyle.font;
            labelStyle.fontColor = textButtonStyle.fontColor;
            label.setStyle(labelStyle);
        }
	}

	public TextButtonStyle getStyle () {
		return style;
	}

	public void draw (SpriteBatch batch, float parentAlpha) {
		Color fontColor;
		if (isDisabled() && style.disabledFontColor != null)
			fontColor = style.disabledFontColor;
		else if (isPressed() && style.downFontColor != null)
			fontColor = style.downFontColor;
		else if (isChecked() && style.checkedFontColor != null)
			fontColor = (isOver() && style.checkedOverFontColor != null) ? style.checkedOverFontColor : style.checkedFontColor;
		else if (isOver() && style.overFontColor != null)
			fontColor = style.overFontColor;
		else
			fontColor = style.fontColor;
		if (fontColor != null) label.getStyle().fontColor = fontColor;
		super.draw(batch, parentAlpha);
	}

	public Label getLabel () {
		return label;
	}

	public void setText (String text) {
		label.setText(text);
	}

	public CharSequence getText () {
		return label.getText();
	}

}
