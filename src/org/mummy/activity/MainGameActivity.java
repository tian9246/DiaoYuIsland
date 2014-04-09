package org.mummy.activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.ease.EaseExponentialInOut;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.gamedata.Motion;
import org.mummy.gamedata.SpriteProperty;
import org.mummy.sprite.BasePersonSpite;
import org.mummy.sprite.Door;
import org.mummy.sprite.ExplodeEffect;
import org.mummy.sprite.Player;
import org.mummy.utils.AndEnviroment;
import org.mummy.utils.AndResourceLoader;
import org.mummy.utils.AndUtil;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.mummy.business.GameLogic;
import com.mummy.touchsprite.BaseTouchSpite;

/**
 * 
 * @author Hanson.Tian
 * @since 2012.9.11
 */
@SuppressLint("HandlerLeak")
public class MainGameActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener, OnClickListener {

	public final static int SETTING = 110;
	public final static int UNDO = 111;
	public final static int DRAG = 112;
	public final static int EXIT = 113;

	public final static int RETURN = 1111;
	public final static int GAMESETTING = 1114;
	public final static int RESET = 1115;

	private boolean isMenuShow;
	private int CAMERA_WIDTH = GameConfig.screenWidth;
	private int CAMERA_HEIGHT = GameConfig.screenHeight;

	private int startX, startY;// ������ʼλ������
	public static boolean isExecuteFinished = true;// �Ƿ���Ӧ�û������¼�
	private Camera mBoundChaseCamera;// ����Bound���
	private ButtonSprite flagButton;// ��ʾ����İ�ť
	// ===========================================================
	// ��һ���Ժ�Ҫ��ֲ��GameData��
	// ===========================================================

	public static TMXTiledMap mTMXTiledMap;
	public static TMXLayer wallLayer, backgroundLayerLayer;
	private Player mPlayer;
	public Scene mScene;
	public Sprite touchMaskSprite, markSprite;
	public Entity leftEntity, rightEntity, menuEntity;

	Handler mHhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���
			switch (msg.arg1) {
			case GameConfig.GAMESTATE_LOSE:
				Toast.makeText(MainGameActivity.this, "�군�����˳�����",
						Toast.LENGTH_SHORT).show();
				break;
			case GameConfig.GAMESTATE_WIN:
				onWin();				
				break;
			default:
				break;
			}
		}
	};

	public EngineOptions onCreateEngineOptions() {
		AndEnviroment.getInstance().initScreneData(this);
		CAMERA_WIDTH = GameConfig.screenWidth;
		CAMERA_HEIGHT = GameConfig.screenHeight;

		mBoundChaseCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new FillResolutionPolicy(), mBoundChaseCamera);
	}

	@Override
	public void onCreateResources() {
		AndEnviroment.getInstance().initContext(this);
		GameData.getInstance().initMainGameData();
		}

	/**
	 * ����֮ǰ������
	 */
	public void clearScene() {
		if (GameData.spriteList == null) {
			return;
		}
		if (GameData.spriteList.size() < 2) {
			return;
		}
		// ��Ҫ��ԭ���Ķ���ɾ��
		AndEnviroment.getInstance().getEngine().getEngineLock().lock();
		for (BasePersonSpite item : GameData.spriteList) {
			mScene.detachChild(item);
			item.dispose();
			item = null;
		}
		mScene.detachChild(backgroundLayerLayer);
		backgroundLayerLayer.dispose();
		backgroundLayerLayer = null;

		mScene.detachChild(wallLayer);
		wallLayer.dispose();
		wallLayer = null;

		mScene.detachChild(GameData.getInstance().explodeEffect);
		GameData.getInstance().explodeEffect.dispose();
		GameData.getInstance().explodeEffect = null;

		mScene.detachChild(flagButton);
		flagButton.dispose();
		flagButton = null;
		GameData.spriteList = new ArrayList<BasePersonSpite>();
		AndEnviroment.getInstance().getEngine().getEngineLock().unlock();
		GameData.spriteListCount = 0;
	}

	/**
	 * �������ƶ���ܿ�
	 * 
	 * @param levelName
	 */
	public void loadLevelByName(String levelName) {
		clearScene();
		//mTMXTiledMap = AndResourceLoader.initMap(levelName + ".tmx");
		GameData.readObjectProperty(mTMXTiledMap);// �����������

		/* �˴��������������̵�x��ƫ���� */
		if (GameConfig.camerOffsetX == 0) {
			GameConfig.camerOffsetX = (int) ((int) (markSprite.getWidth()
					* GameConfig.scaleRation + markSprite.getX()) + (GameConfig.screenWidth * 0.08 - 640));
		}
		/* ���찴ť */
		flagButton = new ButtonSprite(GameData.exitPostionX,
				GameData.exitPostionY,
				GameData.getInstance().flagButtonTextureRegion,
				getVertexBufferObjectManager());

		flagButton.setEnabled(false);

		/* ��õ�ͼ�ĵ�һ�� */
		backgroundLayerLayer = mTMXTiledMap.getTMXLayers().get(0);
		wallLayer = mTMXTiledMap.getTMXLayers().get(1);
		wallLayer.setScaleCenter(0, 0);
		wallLayer.setScale(GameConfig.scaleRation);
		wallLayer.setPosition(GameConfig.camerOffsetX, 0);

		backgroundLayerLayer.attachChild(flagButton);
		backgroundLayerLayer.setScaleCenter(0, 0);
		backgroundLayerLayer.setPosition(GameConfig.camerOffsetX, 0);
		backgroundLayerLayer.setScale(GameConfig.scaleRation);

		/* �ƶ���� */

		mPlayer = new Player(GameData.initExplorerX * GameConfig.tileMapWidth,
				GameData.initExplorerY * GameConfig.tileMapWidth,
				getVertexBufferObjectManager());

		touchMaskSprite = new Sprite(0, 0, backgroundLayerLayer.getWidth(),
				backgroundLayerLayer.getHeight(),
				GameData.getInstance().touchMaskTextureRegion,
				getVertexBufferObjectManager());
		touchMaskSprite.setAlpha(0);

		mScene.attachChild(backgroundLayerLayer);
		mScene.attachChild(wallLayer);
		mScene.attachChild(mPlayer);
		mScene.setOnSceneTouchListener(this);
		try {
			GameData.spriteList.add(mPlayer);
			initMummies();
			GameLogic.initSprites();
		} catch (Exception e) {

			e.printStackTrace();
		}
		mScene.attachChild(touchMaskSprite);// ͸�����ɰ�ֻ�ܷ������
	}

	@Override
	public Scene onCreateScene() {

		HUD hud = new HUD();
		mBoundChaseCamera.setHUD(hud);
		mScene = new Scene();
		Sprite bgSprite = new Sprite(0, 0,
				AndResourceLoader.getTextureRegionByName("maingame_bg.png"),
				getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(GameConfig.scaleRationX, GameConfig.scaleRationY);
		mScene.setBackground(new SpriteBackground(bgSprite));
		/* ����ǽ�� */
		Sprite leftWallSprite = new Sprite(0, 0,
				GameData.getInstance().leftWallTextureRegion,
				getVertexBufferObjectManager());
		Sprite leftShadowSprite = new Sprite(640, 0,
				GameData.getInstance().shadowRightTextureRegion,
				getVertexBufferObjectManager());// ��Ӱʵ����Ū����
		markSprite = new Sprite((float) (640 - (195 / 2)), 0,
				GameData.getInstance().markMainTextureRegion,
				getVertexBufferObjectManager());
		markSprite.setScale(GameConfig.scaleRationX);
		leftEntity = new Entity((float) (GameConfig.screenWidth * 0.08 - 640),
				0);
		leftEntity.attachChild(leftWallSprite);
		leftEntity.attachChild(leftShadowSprite);
		leftEntity.attachChild(markSprite);

		/* �Ҳ��ǽ�� */
		Sprite rightWallSprite = new Sprite(6, 0,
				GameData.getInstance().rightWallTextureRegion,
				getVertexBufferObjectManager());
		Sprite rightShadowSprite = new Sprite(0, 0,
				GameData.getInstance().shadowLeftTextureRegion,
				getVertexBufferObjectManager());// 6 piex
		rightEntity = new Entity(
				(float) (GameConfig.screenWidth * 0.08
						+ (markSprite.getWidth() * GameConfig.scaleRationX / 2) + (GameConfig.screenHeight/6*7)),
				0);
		rightEntity.attachChild(rightShadowSprite);
		rightEntity.attachChild(rightWallSprite);
		hud.attachChild(rightEntity);
		hud.attachChild(leftEntity);

		/* up��ť */
		ButtonSprite upButtonSprite = new ButtonSprite(rightEntity.getX()
				+ (GameConfig.screenWidth - rightEntity.getX()) / 2 - 107
				* GameConfig.scaleRationX / 2, GameConfig.screenHeight / 20,
				GameData.getInstance().upButtonTextureRegion,
				getVertexBufferObjectManager());
		upButtonSprite.setScaleCenter(2, 0);
		upButtonSprite.setScale(GameConfig.scaleRationX);
		upButtonSprite.setTag(GameConfig.DIRECTION_NORTH);
		upButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(upButtonSprite);
		hud.attachChild(upButtonSprite);

		/* down��ť */
		ButtonSprite downButtonSprite = new ButtonSprite(rightEntity.getX()
				+ (GameConfig.screenWidth - rightEntity.getX()) / 2 - 107
				* GameConfig.scaleRationX / 2, GameConfig.screenHeight / 20
				* 19 - 106 * GameConfig.scaleRationX,
				GameData.getInstance().downButtonTextureRegion,
				getVertexBufferObjectManager());
		downButtonSprite.setScaleCenter(2, 0);
		downButtonSprite.setScale(GameConfig.scaleRationX);
		downButtonSprite.setTag(GameConfig.DIRECTION_SOUTH);
		downButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(downButtonSprite);
		hud.attachChild(downButtonSprite);

		/* left��ť */
		ButtonSprite leftButtonSprite = new ButtonSprite(
				rightEntity.getX(),
				GameConfig.screenHeight / 2 - 107 * GameConfig.scaleRationX / 2,
				GameData.getInstance().leftButtonTextureRegion,
				getVertexBufferObjectManager());
		leftButtonSprite.setScaleCenter(2, 0);
		leftButtonSprite.setScale(GameConfig.scaleRationX);
		leftButtonSprite.setTag(GameConfig.DIRECTION_WEST);
		leftButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(leftButtonSprite);
		hud.attachChild(leftButtonSprite);

		/* right��ť */
		ButtonSprite rightButtonSprite = new ButtonSprite(
				GameConfig.screenWidth - 107 * GameConfig.scaleRationX,
				GameConfig.screenHeight / 2 - 107 * GameConfig.scaleRationX / 2,
				GameData.getInstance().rightButtonTextureRegion,
				getVertexBufferObjectManager());
		rightButtonSprite.setScaleCenter(2, 0);
		rightButtonSprite.setScale(GameConfig.scaleRationX);
		rightButtonSprite.setTag(GameConfig.DIRECTION_EAST);
		rightButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(rightButtonSprite);
		hud.attachChild(rightButtonSprite);

		/* ���ð�ť */
		ButtonSprite menuButtonSprite = new ButtonSprite(0,
				CAMERA_HEIGHT * 0.3f,
				GameData.getInstance().menuButtonTextureRegion,
				getVertexBufferObjectManager());
		menuButtonSprite.setScaleCenter(2, 0);
		menuButtonSprite.setScale(GameConfig.scaleRationX);
		menuButtonSprite.setTag(SETTING);
		menuButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(menuButtonSprite);
		hud.attachChild(menuButtonSprite);

		/* ������ť */
		ButtonSprite undoButtonSprite = new ButtonSprite(0,
				CAMERA_HEIGHT * 0.48f,
				GameData.getInstance().undoButtonTextureRegion,
				getVertexBufferObjectManager());
		undoButtonSprite.setScaleCenter(2, 0);
		undoButtonSprite.setScale(GameConfig.scaleRationX);
		undoButtonSprite.setTag(UNDO);
		undoButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(undoButtonSprite);
		hud.attachChild(undoButtonSprite);

		/* �϶���ť */
		ButtonSprite dragButtonSprite = new ButtonSprite(0,
				CAMERA_HEIGHT * 0.65f,
				GameData.getInstance().dragButtonTextureRegion,
				getVertexBufferObjectManager());
		dragButtonSprite.setScaleCenter(2, 0);
		dragButtonSprite.setScale(GameConfig.scaleRationX);
		dragButtonSprite.setTag(DRAG);
		dragButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(dragButtonSprite);
		hud.attachChild(dragButtonSprite);

		/* �˳���ť */
		ButtonSprite exitButtonSprite = new ButtonSprite(0,
				CAMERA_HEIGHT * 0.82f,
				GameData.getInstance().exitButtonTextureRegion,
				getVertexBufferObjectManager());
		exitButtonSprite.setScaleCenter(2, 0);
		exitButtonSprite.setScale(GameConfig.scaleRationX);
		exitButtonSprite.setTag(EXIT);
		exitButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(exitButtonSprite);
		hud.attachChild(exitButtonSprite);

		/** ��Ӳ˵���Ŀ */
		Sprite menuSprite = new Sprite(0, 0,
				GameData.getInstance().mainMenuBgTextureRegion,
				getVertexBufferObjectManager());

		/* ���ð�ť */
		ButtonSprite menuResetButtonSprite = new ButtonSprite(66, 88,
				GameData.getInstance().resetButtonTextureRegion,
				getVertexBufferObjectManager());
		menuResetButtonSprite.setTag(RESET);
		menuResetButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(menuResetButtonSprite);

		/* ���ð�ť */
		ButtonSprite menuSettingButtonSprite = new ButtonSprite(66, 198,
				GameData.getInstance().settingButtonTextureRegion,
				getVertexBufferObjectManager());
		menuSettingButtonSprite.setTag(GAMESETTING);
		menuSettingButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(menuSettingButtonSprite);

		/* ���ذ�ť */
		ButtonSprite menuReturnButtonSprite = new ButtonSprite(66, 312,
				GameData.getInstance().returnButtonTextureRegion,
				getVertexBufferObjectManager());
		menuReturnButtonSprite.setTag(RETURN);
		menuReturnButtonSprite.setOnClickListener(this);
		hud.registerTouchArea(menuReturnButtonSprite);

		menuEntity = new Entity(CAMERA_WIDTH, CAMERA_WIDTH);
		menuEntity.attachChild(menuSprite);
		menuEntity.attachChild(menuResetButtonSprite);
		menuEntity.attachChild(menuSettingButtonSprite);
		menuEntity.attachChild(menuReturnButtonSprite);
		menuEntity.setScaleCenter(0, 0);
		menuEntity.setScale(GameConfig.scaleRationX);
		hud.attachChild(menuEntity);

		loadLevelByName("level" + GameData.currentLevle);

		return mScene;
	}

	/**
	 * ��Ҫ����������
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		int x = (int) pSceneTouchEvent.getX();
		int y = (int) pSceneTouchEvent.getY();
		if (pSceneTouchEvent.isActionDown()) {
			startX = x;
			startY = y;
		}
		if (pSceneTouchEvent.isActionUp() && isExecuteFinished) {
			/* ����������̻�û��ɣ�����Ӧ�û������¼� */
			final int direction = AndUtil.getGestureDirectionPlus(startX, startY,
					x, y, mPlayer.getX(), mPlayer.getY());
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mPlayer.execute(direction);

				}
			});
		}
		return false;
	}

	/**
	 * ͨ�����캯������ľ����������
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void initMummies() throws SecurityException, NoSuchMethodException,
			ClassNotFoundException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		int i = 1;
		for (SpriteProperty spriteProperty : GameData.spritePropertyList) {
			System.out.println("spriteProperty.type" + spriteProperty.type);
			Class<?> c = Class.forName("org.mummy.sprite."
					+ spriteProperty.type);
			@SuppressWarnings("rawtypes")
			Class[] pTypes = new Class[] { float.class, float.class,
					VertexBufferObjectManager.class };
			Constructor<?> ctor = c.getConstructor(pTypes);
			Object[] arg = new Object[] {
					spriteProperty.x * GameConfig.tileMapWidth,
					spriteProperty.y * GameConfig.tileMapWidth,
					getVertexBufferObjectManager() };
			BasePersonSpite bps = (BasePersonSpite) ctor.newInstance(arg);
			if (spriteProperty.type.equals("Door")) {// ������ŵ�������ر���
				mScene.attachChild(bps);
				GameData.getInstance().door = (Door) bps;
				GameData.getInstance().door.direction = spriteProperty.doordirection;
				GameData.getInstance().door.close();
				continue;
			}
			bps.index = (i++);
			GameData.spriteList.add(bps);
			mScene.attachChild(bps);
		}
		GameData.mHandler = this.mHhandler;
		GameData.getInstance().explodeEffect = new ExplodeEffect(0, 0,
				getVertexBufferObjectManager());
		mScene.attachChild(GameData.getInstance().explodeEffect);
	}

	/**
	 * ִ��·��(�Լ��ܵĹ���)
	 */
	public void executePath() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while ((BaseTouchSpite.playerMotions.size() != 1)) {
					if (isExecuteFinished) {// �����ûִ����ϣ����һ��
						Motion motion = BaseTouchSpite.playerMotions.get(0);
						Motion motion1 = BaseTouchSpite.playerMotions.get(1);
						final int direction = AndUtil.getGestureDirectionPlus(
								motion.mapX, motion.mapY, motion1.mapX,
								motion1.mapY, 0, 0);
						mPlayer.execute(direction);
						BaseTouchSpite.playerMotions.remove(0);// �Ƴ����һ��Ԫ��
					} else {
						try {
							Thread.sleep(100);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}
		});
		thread.start();
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_MENU
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (GameLogic.isTouchMode) {
				/* ִ�в��� */
				GameLogic.disableTouchMode(mScene);
				mScene.setOnSceneTouchListener(this);
				executePath();
				return true;
			} else {
				isExecuteFinished = false;
				touchMaskSprite.setAlpha(0.9f);
				GameLogic.enableTouchMode(mScene);
				mScene.setOnSceneTouchListener(null);
				return true;
			}

		} else if (pKeyCode == KeyEvent.KEYCODE_BACK
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (!isExecuteFinished) {
				GameLogic.disableTouchMode(mScene);
				mScene.setOnSceneTouchListener(this);
			} else {
				GameLogic.undoMetion();
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	/**
	 * ��Ӯ��֮��
	 */
	public void onWin() {
		leftEntity.registerEntityModifier(new MoveModifier(1.5f, leftEntity
				.getX(), (float) 0.5 * CAMERA_WIDTH - 640, leftEntity.getY(),
				0, EaseExponentialInOut.getInstance()));
		rightEntity.registerEntityModifier(new MoveModifier(1.5f, rightEntity
				.getX(), (float) 0.5 * CAMERA_WIDTH, rightEntity.getY(), 0,
				EaseExponentialInOut.getInstance()));
		Thread thd = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1600);
					flagButton.setEnabled(true);
					loadLevelByName("level" + (++GameData.currentLevle));
					AndEnviroment.getInstance().setValue(GameConfig.CURRENTLEVEL,
							String.valueOf(GameData.currentLevle)); 
					
					leftEntity.registerEntityModifier(new MoveModifier(1.6f,
							(float) 0.5 * CAMERA_WIDTH - 640,
							(float) (GameConfig.screenWidth * 0.08 - 640), 0,
							leftEntity.getY(), EaseExponentialInOut
									.getInstance()));

					rightEntity
							.registerEntityModifier(new MoveModifier(
									1.6f,
									rightEntity.getX(),
									(float) (GameConfig.screenWidth
											* 0.08
											+ (195 * GameConfig.scaleRationX / 2) + ((1 + GameConfig.mapSize) * GameConfig.tileMapWidth)),
									0, 0, EaseExponentialInOut.getInstance()));
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}
		});
		thd.start();

	}

	/**
	 * ���˵�������
	 */
	public void onMenuOpen() {
		// TODO ���ð�ť
		isExecuteFinished = false;
		isMenuShow = true;
		leftEntity.registerEntityModifier(new MoveModifier(1.6f, leftEntity
				.getX(), (float) 0.5 * CAMERA_WIDTH - 640, leftEntity.getY(),
				0, EaseExponentialInOut.getInstance()));
		rightEntity.registerEntityModifier(new MoveModifier(1.6f, rightEntity
				.getX(), (float) 0.5 * CAMERA_WIDTH, rightEntity.getY(), 0,
				EaseExponentialInOut.getInstance()));

		menuEntity.registerEntityModifier(new MoveModifier(1.6f, CAMERA_WIDTH
				/ 2 - (210 * GameConfig.scaleRationX), CAMERA_WIDTH / 2
				- (210 * GameConfig.scaleRationX), -500, CAMERA_HEIGHT / 6,
				EaseExponentialInOut.getInstance()));

	}

	/**
	 * �رղ˵�����
	 */
	public void onMenuClose() {
		isExecuteFinished = true;
		isMenuShow = false;
		leftEntity.registerEntityModifier(new MoveModifier(1.6f, (float) 0.5
				* CAMERA_WIDTH - 640,
				(float) (GameConfig.screenWidth * 0.08 - 640), 0, leftEntity
						.getY(), EaseExponentialInOut.getInstance()));

		rightEntity
				.registerEntityModifier(new MoveModifier(
						1.6f,
						rightEntity.getX(),
						(float) (GameConfig.screenWidth * 0.08
								+ (195 * GameConfig.scaleRationX / 2) + ((1 + GameConfig.mapSize) * GameConfig.tileMapWidth)),
						0, 0, EaseExponentialInOut.getInstance()));

		menuEntity.registerEntityModifier(new MoveModifier(1.6f, menuEntity
				.getX(), menuEntity.getX(), menuEntity.getY(), -CAMERA_HEIGHT,
				EaseExponentialInOut.getInstance()));
	}

	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if (isMenuShow && (pButtonSprite.getTag() < 1000))
			return;// ����˵��򿪣�����Ӧ����
		switch (pButtonSprite.getTag()) {
		case SETTING:
			onMenuOpen();
			break;
		case UNDO:
			if (!isExecuteFinished) {
				GameLogic.disableTouchMode(mScene);
				mScene.setOnSceneTouchListener(this);
			} else {
				GameLogic.undoMetion();
			}
			break;
		case DRAG:
			if (GameLogic.isTouchMode) {
				/* ִ�в��� */
				GameLogic.disableTouchMode(mScene);
				mScene.setOnSceneTouchListener(this);
				executePath();
			} else {
				isExecuteFinished = false;
				touchMaskSprite.setAlpha(0.9f);
				GameLogic.enableTouchMode(mScene);
				mScene.setOnSceneTouchListener(null);
			}
			break;
		case RESET:
			GameLogic.resetGame();
			onMenuClose();
			break;
		case RETURN:
			onMenuClose();
			break;
		case EXIT:
			finish();
			break;
		default:
			if (isExecuteFinished) {
				/* ����������̻�û��ɣ�����Ӧ�û������¼� */
				final int direction = pButtonSprite.getTag();
				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mPlayer.execute(direction);
					}
				});
			}

			break;
		}

	}
}
