package org.mummy.utils;

import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.LayoutGameActivity;

import android.os.Bundle;
import android.view.KeyEvent;

public abstract class AndGameActivity extends LayoutGameActivity {
	
	protected void onCreate(final Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		AndEnviroment.getInstance().initContext(this);
	}
	
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		Scene scene = AndEnviroment.getInstance().getScene();
		if (scene instanceof AndScene) {
			if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
				if (scene.hasChildScene()) {
					((AndFadeLayer) scene.getChildByTag(AndScene.FADE_LAYER)).setTransparency(0f);
					scene.back();
				} else {
					AndMenuScene menu = (AndMenuScene) ((AndScene) scene).createMenu();
					if (menu != null) {
						((AndFadeLayer) scene.getChildByTag(AndScene.FADE_LAYER)).setTransparency(menu.getTransparency());
						scene.setChildScene(menu, false, true, true);
					}
				}
				return true;
			}
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}
	
}
