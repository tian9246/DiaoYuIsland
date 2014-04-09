package org.mummy.utils;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.input.touch.TouchEvent;

public interface IAndScene {
	
	public void createScene();
	public void startScene();
	public void endScene();
	
	public MenuScene createMenu();
	
	public void replaceLayer(final int pLayerID, final IEntity pLayer);
	
	public void setFadeDelay(final float pDelay);
	public void setFadeDuration(final float pDuration);
	
}
