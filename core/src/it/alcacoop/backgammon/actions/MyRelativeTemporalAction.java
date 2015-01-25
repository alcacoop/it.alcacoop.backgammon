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

package it.alcacoop.backgammon.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

abstract public class MyRelativeTemporalAction extends TemporalAction {
	private float lastPercent;

	protected void begin () {
		lastPercent = 0;
	}
	
	@Override
	protected void end() {
	}

	protected void update (float percent) {
	  Gdx.graphics.requestRendering();
		updateRelative(percent - lastPercent);
		lastPercent = percent;
	}

	abstract protected void updateRelative (float percentDelta);
}
