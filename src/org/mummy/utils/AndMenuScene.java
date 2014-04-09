package org.mummy.utils;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;

public abstract class AndMenuScene extends MenuScene implements IAndMenuScene, IOnMenuItemClickListener {
	
	protected float mTransparency = 0.65f;
	
	public AndMenuScene() {
		super(AndEnviroment.getInstance().getEngine().getCamera());		
		setBackgroundEnabled(false);
		setOnMenuItemClickListener(this);
		
		createMenuScene();
		buildAnimations();
	}
	
	public float getTransparency() {
		return this.mTransparency;
	}
	
	public void setTransparency(final float pTransparency) {
		this.mTransparency = pTransparency;
	}
		
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		return manageMenuItemClicked(pMenuItem.getID());
	}
	
	public void closeMenuScene() {
		AndScene scene = (AndScene) AndEnviroment.getInstance().getScene();
		scene.clearChildScene();
		((AndFadeLayer) scene.getChildByTag(AndScene.FADE_LAYER)).setTransparency(0f);
	}
	
}
