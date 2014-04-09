package org.mummy.activity;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.ease.EaseBounceIn;
import org.andengine.util.modifier.ease.EaseBounceOut;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.utils.AndEnviroment;
import org.mummy.utils.AndResourceLoader;

import android.content.Intent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.clov4r.android.recommend.RecommendActivity;
import com.clov4r.android.recommend.lib.AdViewCreateLib;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.mummy.scene.LevelChooseScene;
import com.mummy.scene.SettingScene;

/**
 * 
 * @author Hanson.Tian
 * @since 2012.9.11
 */
public class WelcomeActivity extends SimpleBaseGameActivity implements
		OnClickListener {
	private static int CAMERA_WIDTH = 960;
	private static int CAMERA_HEIGHT = 540;

	public final static int SETTING = 0;
	public final static int BEGIN = 1;
	public final static int EXIT = 2;
	public final static int HELP = 5;
	public final static int RECOMMEND = 4;

	private Camera mBoundChaseCamera;// 场景Bound相机
	private boolean isHelpMenuShow;
	private Sprite helpSprite;
	public static Scene mScene;
	public static boolean needToShowAd = true;
	public boolean needToClose;

	@Override
	public void onCreateAD() {
		if (!needToShowAd)
			return;
		// 添加广告
		ViewGroup.LayoutParams params = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		AdViewCreateLib ac = new AdViewCreateLib(this, 2);
		ac.addViewTo(frameLayout, params);
		AdView testView = (AdView) ac.getAdView();

		testView.setAdListener(new AdListener() {

			@Override
			public void onReceiveAd(Ad arg0) {

			}

			@Override
			public void onPresentScreen(Ad arg0) {

			}
 
			@Override
			public void onLeaveApplication(Ad arg0) {			
				needToClose = true;
			}

			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {

			}

			@Override
			public void onDismissScreen(Ad arg0) {

			}
		});

	}

	public EngineOptions onCreateEngineOptions() {
		AndEnviroment.getInstance().initScreneData(this);
		CAMERA_WIDTH = GameConfig.screenWidth;
		CAMERA_HEIGHT = GameConfig.screenHeight;
		mBoundChaseCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		mBoundChaseCamera.setZClippingPlanes(-100, 100);
		EngineOptions eoEngineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
				mBoundChaseCamera);
		eoEngineOptions.getAudioOptions().setNeedsMusic(true);
		eoEngineOptions.getAudioOptions().setNeedsSound(true);
		return eoEngineOptions;
	}

	@Override
	public void onCreateResources() {
		AndEnviroment.getInstance().initContext(this);
	}

	public void LoadMainWelScene() {
		GameData.getInstance().initWelcomeActivityData();
		mScene = new Scene();
		Sprite bgSprite = new Sprite(0, 0,
				AndResourceLoader.getTextureRegionByName("bg_wel.png"),
				getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(GameConfig.scaleRationX, GameConfig.scaleRationY);
		mScene.setBackground(new SpriteBackground(bgSprite));
		/* 徽标的情况 */
		AnimatedSprite markSprite = new AnimatedSprite(
				(CAMERA_WIDTH - 449) / 2, CAMERA_HEIGHT / 3,
				GameData.getInstance().markTextureRegion,
				getVertexBufferObjectManager());
		markSprite.animate(75);
		markSprite.setScale(GameConfig.scaleRationX);
		mScene.attachChild(markSprite);

		/* 设置按钮 */
		ButtonSprite settingButtonSprite = new ButtonSprite(
				(CAMERA_WIDTH) * 0.5f - (439), CAMERA_HEIGHT * 0.84f,
				GameData.getInstance().settingTextureRegion,
				getVertexBufferObjectManager());
		settingButtonSprite.setScale(GameConfig.scaleRationX);
		settingButtonSprite.setTag(SETTING);
		settingButtonSprite.setOnClickListener(this);
		mScene.registerTouchArea(settingButtonSprite);
		mScene.attachChild(settingButtonSprite);

		/* 开始按钮 */
		ButtonSprite beginButtonSprite = new ButtonSprite((CAMERA_WIDTH) * 0.5f
				- (239 / 2), CAMERA_HEIGHT * 0.84f,
				GameData.getInstance().adventureTextureRegion,
				getVertexBufferObjectManager());
		beginButtonSprite.setScale(GameConfig.scaleRationX);
		beginButtonSprite.setTag(BEGIN);
		beginButtonSprite.setOnClickListener(this);
		mScene.registerTouchArea(beginButtonSprite);
		mScene.attachChild(beginButtonSprite);

		/* 退出按钮 */
		ButtonSprite exitButtonSprite = new ButtonSprite((CAMERA_WIDTH) * 0.5f
				+ (239 / 2) + 80, CAMERA_HEIGHT * 0.84f,
				GameData.getInstance().exitTextureRegion,
				getVertexBufferObjectManager());
		exitButtonSprite.setScale(GameConfig.scaleRationX);
		exitButtonSprite.setTag(EXIT);
		exitButtonSprite.setOnClickListener(this);
		mScene.registerTouchArea(exitButtonSprite);
		mScene.attachChild(exitButtonSprite);
		mScene.setTouchAreaBindingOnActionDownEnabled(true);

		/* 帮助按钮 */
		ButtonSprite helpButton = new ButtonSprite(CAMERA_WIDTH - 91
				* GameConfig.scaleRationX, 0,
				GameData.getInstance().helpTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		helpButton.setScaleCenter(0, 0);
		helpButton.setScale(GameConfig.scaleRationX);
		helpButton.setTag(HELP);
		helpButton.setOnClickListener(this);

		mScene.registerTouchArea(helpButton);
		mScene.attachChild(helpButton);

		/* 推进按钮 */
		ButtonSprite recommendButtonSprite = new ButtonSprite(0, 0,
				GameData.getInstance().recomendTextureRegion,
				getVertexBufferObjectManager());
		recommendButtonSprite.setScale(GameConfig.scaleRationX);
		recommendButtonSprite.setTag(RECOMMEND);
		recommendButtonSprite.setOnClickListener(this);
		recommendButtonSprite.setScaleCenter(0, 0);
		mScene.registerTouchArea(recommendButtonSprite);
		mScene.attachChild(recommendButtonSprite);
		mScene.setTouchAreaBindingOnActionDownEnabled(true);
		initHelpSprite();
		GameData.getInstance().bgMusic.play();// 播放背景音乐
		AndEnviroment.getInstance().getEngine().setScene(mScene);
	}

	@Override
	public Scene onCreateScene() {
		Scene loadingScene = new Scene();
		Sprite bgSprite = new Sprite(0, 0,
				AndResourceLoader.getTextureRegionByName("loading.png"),
				getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(GameConfig.scaleRationX, GameConfig.scaleRationY);
		loadingScene.setBackground(new SpriteBackground(bgSprite));
		runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				LoadMainWelScene();
			}
		});
		return loadingScene;
	}

	/**
	 * 启动推荐Activity
	 */
	public void startRecommend() {
		Intent it = new Intent(this, RecommendActivity.class);
		startActivity(it);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		try {
			frameLayout.removeAllViews();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isHelpMenuShow) {
			onHelpMenuClose();
			System.out.println("WelcomeActivity.onClick()" + isHelpMenuShow);
			return;
		}
		switch (pButtonSprite.getTag()) {
		case EXIT:
			android.os.Process.killProcess(android.os.Process.myPid());
			finish();
			break;
		case BEGIN:
			this.getEngine().setScene(new LevelChooseScene());
			break;
		case SETTING:
			this.getEngine().setScene(new SettingScene());
			break;
		case RECOMMEND:
			startRecommend();
			break;
		case HELP:
			onHelpMenuOpen();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onStop() {
		if (GameData.getInstance().bgMusic != null)
			GameData.getInstance().bgMusic.stop();
		super.onStop();
		if (needToClose){			
			android.os.Process.killProcess(android.os.Process.myPid());
		}
			
	}

	@Override
	protected void onRestart() {
		needToClose = false;
		AndEnviroment.getInstance().initContext(this);
		System.err.println("WelcomeActivity.onRestart()");
		super.onRestart();
	}

	/**
	 * 初始化帮助精灵 TODO 可以移植帮助
	 */
	public void initHelpSprite() {
		/** 添加菜单项目 */
		helpSprite = new Sprite(GameConfig.screenWidth, GameConfig.screenWidth,
				GameData.getInstance().helpBgTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				onHelpMenuClose();
				return true;
			}

		};
		helpSprite.setScaleCenter(0, 0);
		helpSprite
				.setScale(GameConfig.scaleRationX < GameConfig.scaleRationY ? GameConfig.scaleRationY
						: GameConfig.scaleRationX);
		mScene.attachChild(helpSprite);
		mScene.registerTouchArea(helpSprite);

	}

	/**
	 * 当帮助按钮按下之后 TODO onHelpMenuOpen
	 */
	public void onHelpMenuOpen() {
		isHelpMenuShow = true;
		helpSprite
				.registerEntityModifier(new MoveModifier(1.0f, CAMERA_WIDTH / 2
						- helpSprite.getWidthScaled() / 2, CAMERA_WIDTH / 2
						- helpSprite.getWidthScaled() / 2, -501, CAMERA_HEIGHT
						/ 2 - helpSprite.getHeightScaled() / 2, EaseBounceOut
						.getInstance()));
	}

	public void onHelpMenuClose() {
		isHelpMenuShow = false;
		helpSprite.registerEntityModifier(new MoveModifier(1.0f, CAMERA_WIDTH
				/ 2 - helpSprite.getWidthScaled() / 2, CAMERA_WIDTH / 2
				- helpSprite.getWidthScaled() / 2, CAMERA_HEIGHT / 2
				- helpSprite.getHeightScaled() / 2, -helpSprite
				.getHeightScaled(), EaseBounceIn.getInstance()));
	}

}
