package com.mummy.scene;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBounceIn;
import org.andengine.util.modifier.ease.EaseBounceOut;
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
import org.mummy.utils.AndScene;
import org.mummy.utils.AndUtil;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mummy.business.GameLogic;
import com.mummy.touchsprite.BaseTouchSpite;

/**
 * ����Ϸ������ֲ��**��
 * 
 * @author Hanson.Tian
 * 
 */
public class MainGameScene extends AndScene implements IOnSceneTouchListener,
		OnClickListener {

	// ===========================================================
	// ���ó���
	// ===========================================================
	public final static int RETURNSCENE = 14113;
	public final static int MUSICSWITCH = 13114;
	public final static int SOUNDSWITCH = 11755;
	public final static int VIBRATESWITCH = 12307;
	public final static int SETTING = 110;
	public final static int UNDO = 111;
	public final static int DRAG = 112;
	public final static int EXIT = 113;
	public final static int HELP = 115;

	public final static int RETURN = 1113;
	public final static int GAMESETTING = 1114;
	public final static int RESET = 1115;

	/**
	 * ��ó���ʵ��
	 * 
	 * @return
	 */
	public static synchronized AndScene getInstance() {
		if (mInstance == null)
			mInstance = new MainGameScene();
		mInstance.loadLevelByName("level" + GameData.currentLevle);
		return mInstance;
	}

	// ===========================================================
	// �����ؼ���
	// ===========================================================
	private int CAMERA_WIDTH = GameConfig.screenWidth;

	private int CAMERA_HEIGHT = GameConfig.screenHeight;
	private static MainGameScene mInstance = null;
	private int startX, startY;// ������ʼλ������
	public static boolean isExecuteFinished = true;// �Ƿ���Ӧ�û������¼�
	public boolean isGameOver = false;// �Ƿ���Ӧ�û������¼�
	private boolean isMenuShow;
	private boolean isHelpMenuShow;
	private ButtonSprite flagButton;// ��ʾ����İ�ť

	public ButtonSprite musicSwitchButtonSprite, soundSwitchButtonSprite,
			vibrateSwitchButtonSprite;
	public static TMXTiledMap mTMXTiledMap;
	public static TMXLayer wallLayer, backgroundLayerLayer;

	private Player mPlayer;
	public Sprite touchMaskSprite, markSprite, gameOverSprite;
	public Entity leftEntity, rightEntity, menuEntity, settingEntity;
	public static ArrayList<ButtonSprite> buttons;
	Entity hudEntity;
	/** ��Ӱ�����Ŀ */
	Sprite helpSprite;

	public MainGameScene() {
		super();
		isExecuteFinished = true;
		((Activity) AndEnviroment.getInstance().getContext())
				.runOnUiThread(new Runnable() {
					public void run() {
						GameData.mHandler = new Handler() {
							public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���
								switch (msg.arg1) {
								case GameConfig.GAMESTATE_LOSE:
									onGameLost();
									break;
								case GameConfig.GAMESTATE_WIN:
									onGameWin();
									break;
								default:
									break;
								}
							}
						};
					}
				});

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
		try {
			// ��Ҫ��ԭ���Ķ���ɾ��
			AndEnviroment.getInstance().getEngine().getEngineLock().lock();
			for (BasePersonSpite item : GameData.spriteList) {
				gAME_LAYEREntity.detachChild(item);
				item.dispose();
				item = null;
			}
			gAME_LAYEREntity.detachChild(backgroundLayerLayer);
			backgroundLayerLayer.dispose();
			backgroundLayerLayer = null;

			gAME_LAYEREntity.detachChild(wallLayer);
			wallLayer.dispose();
			wallLayer = null;

			gAME_LAYEREntity.detachChild(GameData.getInstance().explodeEffect);
			GameData.getInstance().explodeEffect.dispose();
			GameData.getInstance().explodeEffect = null;
			gAME_LAYEREntity.detachChild(flagButton);
			flagButton.dispose();
			flagButton = null;
			AndEnviroment.getInstance().getEngine().getEngineLock().unlock();
		} catch (Exception e) {

		}
		GameData.spriteListCount = 0;
		GameData.spriteList = new ArrayList<BasePersonSpite>();
	}

	@Override
	public MenuScene createMenu() {
		return null;
	}

	@Override
	public void createScene() {
		onCreateResources();
		onCreateScene();
	}

	@Override
	public void endScene() {

	}

	/**
	 * ִ��·��(�Լ��ܵĹ���)
	 */
	public void executePath() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while ((BaseTouchSpite.playerMotions.size() != 1 && mPlayer.liveState == BasePersonSpite.LIVE)) {
					if (isExecuteFinished) {// �����ûִ����ϣ����һ��
						Motion motion = BaseTouchSpite.playerMotions.get(0);
						Motion motion1 = BaseTouchSpite.playerMotions.get(1);
						final int direction = AndUtil.getGestureDirection(
								motion.mapX, motion.mapY, motion1.mapX,
								motion1.mapY, 0, 0);
						mPlayer.execute(direction);
						BaseTouchSpite.playerMotions.remove(0);// �Ƴ����һ��Ԫ��
					} else {
						try {
							Thread.sleep(600);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}
		});
		thread.start();
	}

	/**
	 * ��ʼ���������� TODO ������ֲ����
	 */
	public void initHelpSprite() {
		/** ��Ӳ˵���Ŀ */
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
		hudEntity.attachChild(helpSprite);
		registerTouchArea(helpSprite);

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
					AndEnviroment.getInstance().getVertexBufferObjectManager() };
			BasePersonSpite bps = (BasePersonSpite) ctor.newInstance(arg);
			if (spriteProperty.type.equals("Door")) {// ������ŵ�������ر���
				gAME_LAYEREntity.attachChild(bps);
				GameData.getInstance().door = (Door) bps;
				GameData.getInstance().door.direction = spriteProperty.doordirection;
				GameData.getInstance().door.close();
				continue;
			}
			bps.index = (i++);
			GameData.spriteList.add(bps);
			gAME_LAYEREntity.attachChild(bps);
		}
		GameData.getInstance().explodeEffect = new ExplodeEffect(0, 0,
				AndEnviroment.getInstance().getVertexBufferObjectManager());
		gAME_LAYEREntity.attachChild(GameData.getInstance().explodeEffect);
	}

	/**
	 * ��ʼ�����ý���
	 */
	public void initSettingMenu() {
		/** ��Ӳ˵���Ŀ */
		Sprite menuSprite = new Sprite(0, 0,
				GameData.getInstance().mainMenuBgTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());

		/* ���ֿ��ذ�ť */
		musicSwitchButtonSprite = new ButtonSprite(66, 47,
				GameData.getInstance().musicTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		musicSwitchButtonSprite.setTag(MUSICSWITCH);
		musicSwitchButtonSprite.setOnClickListener(this);
		this.registerTouchArea(musicSwitchButtonSprite);

		/* ��Ч��ť */
		soundSwitchButtonSprite = new ButtonSprite(66, 161,
				GameData.getInstance().soundTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		soundSwitchButtonSprite.setTag(SOUNDSWITCH);
		soundSwitchButtonSprite.setOnClickListener(this);
		this.registerTouchArea(soundSwitchButtonSprite);

		/* �𶯰�ť */
		vibrateSwitchButtonSprite = new ButtonSprite(66, 275,
				GameData.getInstance().vibrateTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		vibrateSwitchButtonSprite.setTag(VIBRATESWITCH);
		vibrateSwitchButtonSprite.setOnClickListener(this);
		this.registerTouchArea(vibrateSwitchButtonSprite);

		/* ���ذ�ť */
		ButtonSprite menuReturnButtonSprite = new ButtonSprite(66, 390,
				GameData.getInstance().menuExitTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		menuReturnButtonSprite.setTag(RETURNSCENE);
		menuReturnButtonSprite.setOnClickListener(this);
		this.registerTouchArea(menuReturnButtonSprite);
		settingEntity = new Entity(GameConfig.screenWidth,
				GameConfig.screenHeight);
		settingEntity.attachChild(menuSprite);
		settingEntity.attachChild(musicSwitchButtonSprite);
		settingEntity.attachChild(soundSwitchButtonSprite);
		settingEntity.attachChild(vibrateSwitchButtonSprite);
		settingEntity.attachChild(menuReturnButtonSprite);
		settingEntity.setScaleCenter(0, 0);
		settingEntity.setScale(GameConfig.scaleRationX);
		hudEntity.attachChild(settingEntity);
	}

	/**
	 * ������ʼ���������ð�ť״̬
	 */
	public void intiMusicButton() {
		boolean state = AndEnviroment.getInstance().getMusic();
		if (state) {
			musicSwitchButtonSprite.setCurrentTileIndex(0);
		} else {
			musicSwitchButtonSprite.setCurrentTileIndex(1);
		}

	}

	/**
	 * ������ʼ�������ð�ť״̬
	 */
	public void intiVibrateButton() {
		boolean state = AndEnviroment.getInstance().getVibro();
		if (state) {
			vibrateSwitchButtonSprite.setCurrentTileIndex(0);
		} else {
			vibrateSwitchButtonSprite.setCurrentTileIndex(1);
		}

	}

	/**
	 * ������ʼ���������ð�ť״̬
	 */
	public void intiSoundButton() {
		boolean state = AndEnviroment.getInstance().getAudio();
		if (state) {
			soundSwitchButtonSprite.setCurrentTileIndex(0);
		} else {
			soundSwitchButtonSprite.setCurrentTileIndex(1);
		}
	}

	/**
	 * �������ƶ���ܿ�
	 * 
	 * @param levelName
	 */
	public void loadLevelByName(String levelName) {
		clearScene();
		try {
			mTMXTiledMap = AndResourceLoader.initMap(levelName + ".tmx");
		} catch (Exception e) {

			return;
		}
		GameData.readObjectProperty(mTMXTiledMap);// �����������
		/* �˴��������������̵�x��ƫ���� */
		if (GameConfig.camerOffsetX == 0) {

			GameConfig.camerOffsetX = (int) ((int) (markSprite.getWidthScaled() + markSprite
					.getX()) + (GameConfig.screenWidth * 0.08 - 640));

		}
		/* ���찴ť */
		flagButton = new ButtonSprite(GameData.exitPostionX,
				GameData.exitPostionY,
				GameData.getInstance().flagButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
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
		Log.e("GameConfig.camerOffsetX2", GameConfig.camerOffsetX + "=x2");
		/* �ƶ���� */

		mPlayer = new Player(GameData.initExplorerX * GameConfig.tileMapWidth,
				GameData.initExplorerY * GameConfig.tileMapWidth, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		gAME_LAYEREntity.attachChild(backgroundLayerLayer);
		gAME_LAYEREntity.attachChild(wallLayer);
		this.setOnSceneTouchListener(this);
		try {
			GameData.spriteList.add(mPlayer);
			initMummies();
			gAME_LAYEREntity.attachChild(mPlayer);
			GameLogic.initSprites();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * ��ť�����¼��ص�����
	 */
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if ((isMenuShow ) && (pButtonSprite.getTag() < 1000))
			return;// ����˵��򿪣�����Ӧ����
		switch (pButtonSprite.getTag()) {
		case VIBRATESWITCH:
			onVibrateClick();
			break;
		case SOUNDSWITCH:
			onSoundClick();
			break;
		case MUSICSWITCH:
			onMusicClick();
			break;
		case RETURNSCENE:
			onSettingMuenClose();
			break;
		case GAMESETTING:
			onSettingMuenOpen();
			break;
		case SETTING:
			onMenuOpen();
			break;
		case UNDO:
			if (isGameOver) {
				isExecuteFinished = true;
				isGameOver = false;
				gameOverSprite.setVisible(false);
				touchMaskSprite.setVisible(false);// ��͸��
				GameLogic.undoMetion();
				for (ButtonSprite item : MainGameScene.buttons) {
					item.setEnabled(true);
				}
			} else if (isExecuteFinished) {
				GameLogic.undoMetion();
				isExecuteFinished = true;
			}
			break;
		case DRAG:
			if (GameLogic.isTouchMode) {
				/* ִ�в��� */
				GameLogic.disableTouchMode(this);
				touchMaskSprite.setVisible(false);
				this.setOnSceneTouchListener(this);
				executePath();
			} else {
				isExecuteFinished = false;
				touchMaskSprite.setVisible(true);
				GameLogic.enableTouchMode(this);
				this.setOnSceneTouchListener(null);
			}
			break;
		case RESET:
			isGameOver = false;
			GameLogic.resetGame();
			onMenuClose();
			break;
		case RETURN:
			onMenuClose();
			break;
		case EXIT:
			if (isHelpMenuShow) {
				onHelpMenuClose();
				return;
			}
			if (GameLogic.isTouchMode) {
				/* ִ�в��� */
				GameLogic.disableTouchMode(this);
				touchMaskSprite.setVisible(false);
				this.setOnSceneTouchListener(this);
			} else {
				AndEnviroment.getInstance().getEngine()
						.setScene(new LevelChooseScene());
			}
			break;
		case HELP:
			onHelpMenuOpen();			
			break;
		default:
			if (isExecuteFinished) {
				/* ����������̻�û��ɣ�����Ӧ�û������¼� */
				final int direction = pButtonSprite.getTag();
				((Activity) AndEnviroment.getInstance().getContext())
						.runOnUiThread(new Runnable() {
							public void run() {
								mPlayer.execute(direction);
							}
						});
			}
			break;
		}

	}

	public void onCreateResources() {
		CAMERA_WIDTH = GameConfig.screenWidth;
		CAMERA_HEIGHT = GameConfig.screenHeight;
	}

	/**
	 * ��������������ʱ��
	 * 
	 * @return
	 */
	public Scene onCreateScene() {
		buttons = new ArrayList<ButtonSprite>();
		hudEntity = new Entity();
		Sprite bgSprite = new Sprite(0, 0,
				AndResourceLoader.getTextureRegionByName("maingame_bg.png"),
				AndEnviroment.getInstance().getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(GameConfig.scaleRationX, GameConfig.scaleRationY);
		this.setBackground(new SpriteBackground(bgSprite));
		/* ����ǽ�� */
		Sprite leftWallSprite = new Sprite(0, 0,
				GameData.getInstance().leftWallTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		Sprite leftShadowSprite = new Sprite(640, 0,
				GameData.getInstance().shadowRightTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());// ��Ӱʵ����Ū����
		markSprite = new Sprite((float) (640 - (195 / 2)), 0,
				GameData.getInstance().markMainTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		markSprite.setScale(GameConfig.scaleRationX);
		leftEntity = new Entity((float) (GameConfig.screenWidth * 0.08 - 640),
				0);
		leftEntity.attachChild(leftWallSprite);
		leftEntity.attachChild(leftShadowSprite);
		leftEntity.attachChild(markSprite);

		/* �Ҳ��ǽ�� */
		Sprite rightWallSprite = new Sprite(6, 0,
				GameData.getInstance().rightWallTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		Sprite rightShadowSprite = new Sprite(0, 0,
				GameData.getInstance().shadowLeftTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());// 6 piex
		rightEntity = new Entity(
				(float) (GameConfig.screenWidth
						* (-0.0266 + GameConfig.screenWidth * (0.04 / 480f))
						+ (markSprite.getWidth() * GameConfig.scaleRationX / 2) + (GameConfig.screenHeight / 6 * 7)),
				0);
		rightEntity.attachChild(rightShadowSprite);
		rightEntity.attachChild(rightWallSprite);
		hudEntity.attachChild(rightEntity);
		hudEntity.attachChild(leftEntity);

		/* left��ť */
		ButtonSprite leftButtonSprite = new ButtonSprite(
				rightEntity.getX(),
				GameConfig.screenHeight / 2 - 107 * GameConfig.scaleRationX / 2,
				GameData.getInstance().leftButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		leftButtonSprite.setScaleCenter(2, 0);
		leftButtonSprite.setScale(GameConfig.scaleRationX);
		leftButtonSprite.setTag(GameConfig.DIRECTION_WEST);
		leftButtonSprite.setOnClickListener(this);
		buttons.add(leftButtonSprite);
		this.registerTouchArea(leftButtonSprite);
		hudEntity.attachChild(leftButtonSprite);

		/* right��ť */
		ButtonSprite rightButtonSprite = new ButtonSprite(
				GameConfig.screenWidth - 107 * GameConfig.scaleRationX,
				GameConfig.screenHeight / 2 - 107 * GameConfig.scaleRationX / 2,
				GameData.getInstance().rightButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		rightButtonSprite.setScaleCenter(2, 0);
		rightButtonSprite.setScale(GameConfig.scaleRationX);
		rightButtonSprite.setTag(GameConfig.DIRECTION_EAST);
		rightButtonSprite.setOnClickListener(this);
		buttons.add(rightButtonSprite);
		this.registerTouchArea(rightButtonSprite);
		hudEntity.attachChild(rightButtonSprite);

		/* up��ť */
		ButtonSprite upButtonSprite = new ButtonSprite(rightEntity.getX()
				+ (GameConfig.screenWidth - rightEntity.getX()) / 2 - 107
				* GameConfig.scaleRationX / 2, rightButtonSprite.getY()
				- GameConfig.scaleRationX * 106,
				GameData.getInstance().upButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		upButtonSprite.setScaleCenter(2, 0);
		upButtonSprite.setScale(GameConfig.scaleRationX);
		upButtonSprite.setTag(GameConfig.DIRECTION_NORTH);
		upButtonSprite.setOnClickListener(this);
		buttons.add(upButtonSprite);
		this.registerTouchArea(upButtonSprite);
		hudEntity.attachChild(upButtonSprite);

		/* down��ť */
		ButtonSprite downButtonSprite = new ButtonSprite(rightEntity.getX()
				+ (GameConfig.screenWidth - rightEntity.getX()) / 2 - 107
				* GameConfig.scaleRationX / 2, rightButtonSprite.getY()
				+ rightButtonSprite.getHeightScaled(),
				GameData.getInstance().downButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		downButtonSprite.setScaleCenter(2, 0);
		downButtonSprite.setScale(GameConfig.scaleRationX);
		downButtonSprite.setTag(GameConfig.DIRECTION_SOUTH);
		downButtonSprite.setOnClickListener(this);
		buttons.add(downButtonSprite);
		this.registerTouchArea(downButtonSprite);
		hudEntity.attachChild(downButtonSprite);
		/* ���ð�ť */
		ButtonSprite menuButtonSprite = new ButtonSprite(0,
				CAMERA_HEIGHT * 0.3f,
				GameData.getInstance().menuButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		menuButtonSprite.setScaleCenter(2, 0);
		menuButtonSprite.setScale(GameConfig.scaleRationX);
		menuButtonSprite.setTag(SETTING);
		menuButtonSprite.setOnClickListener(this);
		buttons.add(menuButtonSprite);
		this.registerTouchArea(menuButtonSprite);
		hudEntity.attachChild(menuButtonSprite);

		/* ������ť */
		ButtonSprite undoButtonSprite = new ButtonSprite(0,
				CAMERA_HEIGHT * 0.48f,
				GameData.getInstance().undoButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		undoButtonSprite.setScaleCenter(2, 0);
		undoButtonSprite.setScale(GameConfig.scaleRationX);
		undoButtonSprite.setTag(UNDO);
		undoButtonSprite.setOnClickListener(this);
		buttons.add(undoButtonSprite);
		this.registerTouchArea(undoButtonSprite);
		hudEntity.attachChild(undoButtonSprite);

		/* �϶���ť */
		ButtonSprite dragButtonSprite = new ButtonSprite(0,
				CAMERA_HEIGHT * 0.65f,
				GameData.getInstance().dragButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		dragButtonSprite.setScaleCenter(2, 0);
		dragButtonSprite.setScale(GameConfig.scaleRationX);
		dragButtonSprite.setTag(DRAG);
		dragButtonSprite.setOnClickListener(this);
		buttons.add(dragButtonSprite);
		this.registerTouchArea(dragButtonSprite);
		hudEntity.attachChild(dragButtonSprite);

		/* �˳���ť */
		ButtonSprite exitButtonSprite = new ButtonSprite(0,
				CAMERA_HEIGHT * 0.82f,
				GameData.getInstance().exitButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		exitButtonSprite.setScaleCenter(2, 0);
		exitButtonSprite.setScale(GameConfig.scaleRationX);
		exitButtonSprite.setTag(EXIT);
		exitButtonSprite.setOnClickListener(this);
		buttons.add(exitButtonSprite);
		this.registerTouchArea(exitButtonSprite);
		hudEntity.attachChild(exitButtonSprite);
		/* ������ť */
		ButtonSprite helpButton = new ButtonSprite(CAMERA_WIDTH - 91
				* GameConfig.scaleRationX, 0,
				GameData.getInstance().helpTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		helpButton.setScaleCenter(0, 0);
		helpButton.setScale(GameConfig.scaleRationX);
		helpButton.setTag(HELP);
		helpButton.setOnClickListener(this);
		buttons.add(helpButton);
		this.registerTouchArea(helpButton);
		hudEntity.attachChild(helpButton);

		/** ��Ӳ˵���Ŀ */
		Sprite menuSprite = new Sprite(0, 0,
				GameData.getInstance().mainMenuBgTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());

		/* ���ð�ť */
		ButtonSprite menuResetButtonSprite = new ButtonSprite(66, 88,
				GameData.getInstance().resetButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		menuResetButtonSprite.setTag(RESET);
		menuResetButtonSprite.setOnClickListener(this);
		this.registerTouchArea(menuResetButtonSprite);

		/* ���ð�ť */
		ButtonSprite menuSettingButtonSprite = new ButtonSprite(66, 198,
				GameData.getInstance().settingButtonTextureRegion,
				AndEnviroment.getInstance().getVertexBufferObjectManager());
		menuSettingButtonSprite.setTag(GAMESETTING);
		menuSettingButtonSprite.setOnClickListener(this);
		this.registerTouchArea(menuSettingButtonSprite);

		/* ���ذ�ť */
		ButtonSprite menuReturnButtonSprite = new ButtonSprite(66, 312,
				GameData.getInstance().returnButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		menuReturnButtonSprite.setTag(RETURN);
		menuReturnButtonSprite.setOnClickListener(this);
		this.registerTouchArea(menuReturnButtonSprite);
		menuEntity = new Entity(CAMERA_WIDTH, CAMERA_WIDTH);
		menuEntity.attachChild(menuSprite);
		menuEntity.attachChild(menuResetButtonSprite);
		menuEntity.attachChild(menuSettingButtonSprite);
		menuEntity.attachChild(menuReturnButtonSprite);
		menuEntity.setScaleCenter(0, 0);
		menuEntity.setScale(GameConfig.scaleRationX);
		hudEntity.attachChild(menuEntity);
		initSettingMenu();
		initHelpSprite();
		loadLevelByName("level" + GameData.currentLevle);
		touchMaskSprite = new Sprite(0, 0, GameConfig.screenHeight * 2,
				GameConfig.screenHeight,
				GameData.getInstance().touchMaskTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		touchMaskSprite.setVisible(false);
		gameOverSprite = new Sprite(0, 0,
				GameData.getInstance().gameOverBgTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		gameOverSprite.setVisible(false);
		gameOverSprite.setScaleCenter(0, 0);
		gameOverSprite.setScale(GameConfig.scaleRation);
		mASK_LAYEREntity.attachChild(touchMaskSprite);// ͸�����ɰ�ֻ�ܷ������
		hIGHEST_GAME_LAYEREntity.attachChild(gameOverSprite);
		eXTRA_GAME_LAYEREntity.attachChild(hudEntity);
		return this;
	}

	/**
	 * ����Ϸ����֮��
	 */
	public void onGameLost() {
		isGameOver = true;
		gameOverSprite.setPosition(
				GameConfig.screenWidth / 2 - gameOverSprite.getWidthScaled()
						/ 2,
				GameConfig.screenHeight / 2 - gameOverSprite.getHeightScaled()
						/ 2);
		gameOverSprite.setVisible(true);
		touchMaskSprite.setVisible(true);
		for (ButtonSprite item : MainGameScene.buttons) {
			item.setEnabled(true);
		}
		for (ButtonSprite item : MainGameScene.buttons) {
			if (item.getTag() != SETTING)
				if (item.getTag() != UNDO) {
					item.setEnabled(false);
				}
		}

	}

	/**
	 * �رղ˵�����
	 */
	public void onMenuClose() {
		for (ButtonSprite item : MainGameScene.buttons) {
			item.setEnabled(true);
		}
		if (isGameOver) {
			touchMaskSprite.setVisible(true);
			gameOverSprite.setVisible(true);
		}
		isMenuShow = false;
		leftEntity.registerEntityModifier(new MoveModifier(1.6f, (float) 0.5
				* CAMERA_WIDTH - 640,
				(float) (GameConfig.screenWidth * 0.08 - 640), 0, leftEntity
						.getY(), EaseExponentialInOut.getInstance()));
		rightEntity
				.registerEntityModifier(new MoveModifier(
						1.6f,
						rightEntity.getX(),
						(float) (GameConfig.screenWidth
								* (-0.0266 + GameConfig.screenWidth
										* (0.04 / 480f))
								+ (195 * GameConfig.scaleRationX / 2) + (GameConfig.screenHeight / 6 * 7)),
						0, 0, EaseExponentialInOut.getInstance()));
		menuEntity.registerEntityModifier(new MoveModifier(1.6f, menuEntity
				.getX(), menuEntity.getX(), menuEntity.getY(), -CAMERA_HEIGHT,
				EaseExponentialInOut.getInstance()));
	}

	/**
	 * ���˵�������
	 */
	public void onMenuOpen() {
		for (ButtonSprite item : MainGameScene.buttons) {
			item.setEnabled(false);
		}
		touchMaskSprite.setVisible(false);
		gameOverSprite.setVisible(false);
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
	 * �������ֿ���
	 */
	public void onMusicClick() {
		boolean state = AndEnviroment.getInstance().getMusic();
		if (state) {
			// �ر�����
			AndEnviroment.getInstance().toggleMusic();
			musicSwitchButtonSprite.setCurrentTileIndex(1);
			GameData.getInstance().bgMusic.stop();
		} else {
			AndEnviroment.getInstance().toggleMusic();
			musicSwitchButtonSprite.setCurrentTileIndex(0);
			GameData.getInstance().bgMusic.play();
		}
	}

	/**
	 * ��Ҫ����������
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (isHelpMenuShow | isMenuShow | isGameOver)
			return false;
		int x = (int) pSceneTouchEvent.getX();
		int y = (int) pSceneTouchEvent.getY();
		/* ����Ƿ��ڿɿ������� */
		if (x < wallLayer.getX()
				| x > (wallLayer.getX() + GameConfig.screenHeight)) {
			return false;
		}

		if (pSceneTouchEvent.isActionDown()) {
			startX = x;
			startY = y;
		}
		if (pSceneTouchEvent.isActionUp() && isExecuteFinished) {
			/* ����������̻�û��ɣ�����Ӧ�û������¼� */
			final int direction = AndUtil.getGestureDirection(startX, startY,
					x, y, mPlayer.getX(), mPlayer.getY());

			((Activity) AndEnviroment.getInstance().getContext())
					.runOnUiThread(new Runnable() {
						public void run() {
							mPlayer.execute(direction);

						}
					});
		}
		return false;
	}

	/**
	 * �����ð�ť�����Ժ�
	 */
	public void onSettingMuenClose() {
		settingEntity.registerEntityModifier(new MoveModifier(1.6f,
				settingEntity.getX(), settingEntity.getX(), settingEntity
						.getY(), CAMERA_HEIGHT, EaseExponentialInOut
						.getInstance()));
		menuEntity.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.2f), new MoveModifier(1.2f,
						GameConfig.screenWidth / 2
								- (210 * GameConfig.scaleRationX),
						GameConfig.screenWidth / 2
								- (210 * GameConfig.scaleRationX), -500,
						GameConfig.screenHeight / 6, EaseExponentialInOut
								.getInstance())));
	}

	/**
	 * �����ð�ť�����Ժ�
	 */
	public void onSettingMuenOpen() {
		intiMusicButton();
		intiSoundButton();
		intiVibrateButton();
		menuEntity.registerEntityModifier(new MoveModifier(1.6f, menuEntity
				.getX(), menuEntity.getX(), menuEntity.getY(), CAMERA_HEIGHT,
				EaseExponentialInOut.getInstance()));
		settingEntity.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.2f), new MoveModifier(1.2f,
						GameConfig.screenWidth / 2
								- (210 * GameConfig.scaleRationX),
						GameConfig.screenWidth / 2
								- (210 * GameConfig.scaleRationX), -500,
						GameConfig.screenHeight / 6, EaseExponentialInOut
								.getInstance())));
	}

	/**
	 * ��������ť����֮�� TODO onHelpMenuOpen
	 */
	public void onHelpMenuOpen() {
		/* ����״̬��ť */
		for (ButtonSprite item : MainGameScene.buttons) {
			if (item.getTag() != EXIT)
				item.setEnabled(false);
		}
		touchMaskSprite.setVisible(false);
		gameOverSprite.setVisible(false);
		isHelpMenuShow = true;
		/* ���Ŷ��� */
		leftEntity.registerEntityModifier(new MoveModifier(1.6f, leftEntity
				.getX(), (float) 0.5 * CAMERA_WIDTH - 640, leftEntity.getY(),
				0, EaseExponentialInOut.getInstance()));
		rightEntity.registerEntityModifier(new MoveModifier(1.6f, rightEntity
				.getX(), (float) 0.5 * CAMERA_WIDTH, rightEntity.getY(), 0,
				EaseExponentialInOut.getInstance()));
		helpSprite
				.registerEntityModifier(new MoveModifier(1.6f, CAMERA_WIDTH / 2
						- helpSprite.getWidthScaled() / 2, CAMERA_WIDTH / 2
						- helpSprite.getWidthScaled() / 2, -500, CAMERA_HEIGHT
						/ 2 - helpSprite.getHeightScaled() / 2, EaseBounceOut
						.getInstance()));
	}

	public void onHelpMenuClose() {
		if (!isHelpMenuShow) {// �����ΰ��£�����Ӧ
			return;
		}
		for (ButtonSprite item : MainGameScene.buttons) {
			item.setEnabled(true);
		}
		if (isGameOver) {
			touchMaskSprite.setVisible(true);
			gameOverSprite.setVisible(true);
		}
		isHelpMenuShow = false;
		leftEntity.registerEntityModifier(new MoveModifier(1.6f, (float) 0.5
				* CAMERA_WIDTH - 640,
				(float) (GameConfig.screenWidth * 0.08 - 640), 0, leftEntity
						.getY(), EaseExponentialInOut.getInstance()));
		rightEntity
				.registerEntityModifier(new MoveModifier(
						1.6f,
						rightEntity.getX(),
						(float) (GameConfig.screenWidth
								* (-0.0266 + GameConfig.screenWidth
										* (0.04 / 480f))
								+ (195 * GameConfig.scaleRationX / 2) + (GameConfig.screenHeight / 6 * 7)),
						0, 0, EaseExponentialInOut.getInstance()));
		helpSprite.registerEntityModifier(new MoveModifier(1.6f, CAMERA_WIDTH
				/ 2 - helpSprite.getWidthScaled() / 2, CAMERA_WIDTH / 2
				- helpSprite.getWidthScaled() / 2, CAMERA_HEIGHT / 2
				- helpSprite.getHeightScaled() / 2, -helpSprite
				.getHeightScaled(), EaseBounceIn.getInstance()));
	}

	/**
	 * ������Ч����
	 */
	public void onSoundClick() {
		boolean state = AndEnviroment.getInstance().getAudio();
		if (state) {
			// �ر���Ч
			AndEnviroment.getInstance().toggleAudio();
			soundSwitchButtonSprite.setCurrentTileIndex(1);
		} else {
			AndEnviroment.getInstance().toggleAudio();
			soundSwitchButtonSprite.setCurrentTileIndex(0);
		}
	}

	/**
	 * ���𶯰�ť����֮��
	 */
	public void onVibrateClick() {
		boolean state = AndEnviroment.getInstance().getVibro();
		if (state) {
			// �ر���
			AndEnviroment.getInstance().toggleVibro();
			vibrateSwitchButtonSprite.setCurrentTileIndex(1);
		} else {
			AndEnviroment.getInstance().toggleVibro();
			vibrateSwitchButtonSprite.setCurrentTileIndex(0);
		}
	}

	/**
	 * ��Ӯ��֮��
	 */
	public void onGameWin() {
		for (ButtonSprite item : MainGameScene.buttons) {
			item.setEnabled(false);
		}
		flagButton.setEnabled(true);
		leftEntity.registerEntityModifier(new MoveModifier(1.5f, leftEntity
				.getX(), (float) 0.5 * CAMERA_WIDTH - 640, leftEntity.getY(),
				0, EaseExponentialInOut.getInstance()));
		rightEntity.registerEntityModifier(new MoveModifier(1.5f, rightEntity
				.getX(), (float) 0.5 * CAMERA_WIDTH, rightEntity.getY(), 0,
				EaseExponentialInOut.getInstance()));
		Thread thd = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1600);// �ȴ�����ȫ�ر�
					int maxUnlockLevel = Integer.parseInt(AndEnviroment
							.getInstance().getValue(GameConfig.CURRENTLEVEL,
									"1"));
					loadLevelByName("level" + (++GameData.currentLevle));
					if (maxUnlockLevel < GameData.currentLevle) {
						AndEnviroment.getInstance().setValue(
								GameConfig.CURRENTLEVEL,
								String.valueOf(GameData.currentLevle));
					}
					leftEntity.registerEntityModifier(new MoveModifier(1.6f,
							(float) 0.5 * CAMERA_WIDTH - 640,
							(float) (GameConfig.screenWidth * 0.08 - 640), 0,
							0, EaseExponentialInOut
									.getInstance()));
				//TODO onWin
					rightEntity
							.registerEntityModifier(new MoveModifier(
									1.6f,
									rightEntity.getX(),
									(float) (GameConfig.screenWidth
											* (-0.0266 + GameConfig.screenWidth
													* (0.04 / 480f))
											+ (195 * GameConfig.scaleRationX / 2) + (GameConfig.screenHeight / 6 * 7)),
									0, 0, new IEntityModifierListener() {
										@Override
										public void onModifierStarted(
												IModifier<IEntity> pModifier,
												IEntity pItem) {

										}

										@Override
										public void onModifierFinished(
												IModifier<IEntity> pModifier,
												IEntity pItem) {
											for (ButtonSprite item : MainGameScene.buttons) {
												item.setEnabled(true);
											}

										}
									}, EaseExponentialInOut.getInstance()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thd.start();

	}

	@Override
	public void startScene() {

	}

}
