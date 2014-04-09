package org.mummy.utils;

import java.util.Locale;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.preferences.SimplePreferences;
import org.mummy.gamedata.GameConfig;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.view.Display;
import android.view.WindowManager;

public class AndEnviroment {

	private static AndEnviroment mInstance = null;

	private Context mContext = null;

	private AudioManager mAudioManager = null;

	private AndEnviroment() {

	}

	public void showMessage(String message) {

	}

	public static synchronized AndEnviroment getInstance() {
		if (mInstance == null)
			mInstance = new AndEnviroment();
		return mInstance;
	}

	public void setValue(String key, String value) {
		SimplePreferences.getInstance(mContext).edit().putString(key, value)
				.commit();
	}

	public String getValue(String key,String defValue) {
		return SimplePreferences.getInstance(mContext).getString(key, defValue);
	}

	
	@SuppressWarnings("deprecation")
	public void initScreneData(final Context pCtx) {
		WindowManager windowManager = ((Activity) pCtx).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		GameConfig.screenWidth = display.getWidth();
		GameConfig.screenHeight = display.getHeight();
		GameConfig.scaleRationX = GameConfig.screenWidth / 1280f;
		GameConfig.scaleRationY = GameConfig.screenHeight / 720f;
		Locale locale = pCtx.getResources().getConfiguration().locale;
		GameConfig.language = locale.getLanguage();		
	}

	public void initContext(final Context pCtx) {
		this.mContext = pCtx;
		
		this.mAudioManager = (AudioManager) this.mContext
				.getSystemService(Service.AUDIO_SERVICE);

		getEngine().enableVibrator(this.mContext);
	}

	public void setContext(Context pCtx) {
		this.mContext = pCtx;

	}

	public Context getContext() {
		return this.mContext;
	}

	public AudioManager getAudioManager() {
		return this.mAudioManager;
	}

	public Engine getEngine() {
		return ((BaseGameActivity) getContext()).getEngine();
	}

	public Scene getScene() {
		return getEngine().getScene();
	}

	public void nextScene() {
		((AndFadeLayer) getScene().getChildByTag(AndScene.FADE_LAYER)).fadeIn();
	}

	public void setScene(final Scene pScene) {
		getEngine().setScene(pScene);
	}

	public void safeDetachEntity(final IEntity pItem) {
		getEngine().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				pItem.detachSelf();
			}
		});
	}


	public boolean getMusic() {
		return SimplePreferences.getInstance(this.mContext).getBoolean("Music",
				true);
	}

	public void toggleMusic() {
		boolean value = !(getMusic());
		SimplePreferences.getEditorInstance(this.mContext)
				.putBoolean("Music", value).commit();
		
	}
	public boolean getAudio() {
		return SimplePreferences.getInstance(this.mContext).getBoolean("audio",
				true);

	}

	public void toggleAudio() {
		boolean value = !(getAudio());
		SimplePreferences.getEditorInstance(this.mContext)
				.putBoolean("audio", value).commit();
	}

	public boolean getVibro() {
		return SimplePreferences.getInstance(this.mContext).getBoolean("vibro",
				true);
	}

	public void toggleVibro() {
		boolean value = !(getVibro());
		SimplePreferences.getEditorInstance(this.mContext)
		.putBoolean("vibro", value).commit();
	}

	public VertexBufferObjectManager getVertexBufferObjectManager() {
		return ((BaseGameActivity) getContext()).getEngine()
				.getVertexBufferObjectManager();
	}

	public TextureManager getTextureManager() {
		return ((BaseGameActivity) getContext()).getEngine()
				.getTextureManager();
	}

	public FontManager getFontManager() {
		return ((BaseGameActivity) getContext()).getEngine().getFontManager();
	}

	public AssetManager getAssetManager() {
		return mContext.getAssets();
	}

}
