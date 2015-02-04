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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class MyActions {
	/** Returns a new or pooled action of the specified type. */
	static public <T extends Action> T action (Class<T> type) {
		Pool<T> pool = Pools.get(type);
		T action = pool.obtain();
		action.setPool(pool);
		return action;
	}


	/** Moves the actor instantly. */
	static public Action moveTo (float x, float y) {
		return moveTo(x, y, 0);
	}

	static public Action moveTo (float x, float y, float duration) {
		MyMoveToAction action = action(MyMoveToAction.class);
    action.setPosition(x, y);
    action.setDuration(duration);
    
		Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    return sequence(action);
	}


	static public Action rotateBy (float rotationAmount) {
		return rotateBy(rotationAmount, 0);
	}

	static public Action rotateBy (float rotationAmount, float duration) {
		MyRotateByAction action = action(MyRotateByAction.class);
    action.setAmount(rotationAmount);
    action.setDuration(duration);
    
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    return sequence(action);
	}


	static public Action alpha (float a) {
		return alpha(a, 0);
	}

	static public Action alpha (float a, float duration) {
		MyAlphaAction action = action(MyAlphaAction.class);
    action.setAlpha(a);
    action.setDuration(duration);
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    return sequence(action);
	}


	static public Action fadeOut (float duration) {
		return alpha(0, duration);
	}
	static public Action fadeIn (float duration) {
		return alpha(1, duration);
	}



	static public MySequenceAction sequence (Action... actions) {
		MySequenceAction action = action(MySequenceAction.class);
		Gdx.graphics.setContinuousRendering(true);
		Gdx.graphics.requestRendering();
    action.addAction(Actions.delay(0.04f));
		for (int i = 0, n = actions.length; i < n; i++)
			action.addAction(actions[i]);
		action.addAction(stopCR());
		return action;
	}


	static public MyParallelAction parallel (Action... actions) {
		MyParallelAction action = action(MyParallelAction.class);
		for (int i = 0, n = actions.length; i < n; i++)
			action.addAction(actions[i]);
		return action;
	}
	
	static public RepeatAction forever (Action repeatedAction) {
		RepeatAction action = action(RepeatAction.class);
		action.setCount(RepeatAction.FOREVER);
		action.setAction(repeatedAction);
		return action;
	}

	static public MyRunnableAction run (Runnable runnable) {
		MyRunnableAction action = action(MyRunnableAction.class);
		action.setRunnable(runnable);
		return action;
	}
	
	static public MyStartCRAction startCR () {
    MyStartCRAction action = action(MyStartCRAction.class);
    return action;
  }
	
	static public MyStopCRAction stopCR () {
    MyStopCRAction action = action(MyStopCRAction.class);
    return action;
  }
}
