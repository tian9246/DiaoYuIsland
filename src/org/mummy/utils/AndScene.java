package org.mummy.utils;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

public abstract class AndScene extends Scene implements IAndScene {

	public static int BACKGROUND_LAYER = 0;
	public static int GAME_LAYER = 1;
	public static int EXTRA_GAME_LAYER = 2;
	public static int GUI_LAYER = 3;
	public static int FADE_LAYER = 4;
	public Entity bACKGROUND_LAYEREntity;
	public Entity gAME_LAYEREntity;
	public Entity eXTRA_GAME_LAYEREntity;
	public Entity hIGHEST_GAME_LAYEREntity;
	public Entity mASK_LAYEREntity;

	public AndScene() {
		super();
		bACKGROUND_LAYEREntity = new Entity();
		gAME_LAYEREntity = new Entity();
		mASK_LAYEREntity = new Entity();
		eXTRA_GAME_LAYEREntity = new Entity();
		hIGHEST_GAME_LAYEREntity = new Entity();
		attachChild(bACKGROUND_LAYEREntity);
		attachChild(gAME_LAYEREntity);
		attachChild(mASK_LAYEREntity);
		attachChild(eXTRA_GAME_LAYEREntity);
		attachChild(hIGHEST_GAME_LAYEREntity);
		createScene();
	}

	public void replaceLayer(final int pLayerID, final IEntity pLayer) {
		detachChild(getChildByIndex(pLayerID));
		attachChild(pLayer);
	}

	public void setFadeDelay(final float pDelay) {
		((AndFadeLayer) getChildByTag(FADE_LAYER)).setFadeDelay(pDelay);
	}

	public void setFadeDuration(final float pDuration) {
		((AndFadeLayer) getChildByTag(FADE_LAYER)).setFadeDuration(pDuration);
	}


}
