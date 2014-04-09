package com.mummy.scene;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.ease.EaseExponentialInOut;
import org.mummy.activity.WelcomeActivity;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.utils.AndEnviroment;
import org.mummy.utils.AndResourceLoader;
import org.mummy.utils.AndScene;

/**
 * ��Ϸ���ó���
 * 
 * @author Hanson.Tian
 * 
 */
public class SettingScene extends AndScene implements OnClickListener {

	public final static int RETURNSCENE = 14113;
	public final static int MUSICSWITCH = 13114;
	public final static int SOUNDSWITCH = 75;
	public final static int VIBRATESWITCH = 88;

	private static SettingScene mInstance = null;
	public ButtonSprite musicSwitchButtonSprite, soundSwitchButtonSprite,
			vibrateSwitchButtonSprite;
	public Entity leftEntity, rightEntity, settingEntity;

	public SettingScene() {
		super();
	}

	@Override
	public MenuScene createMenu() {
		return null;
	}

	@Override
	public void createScene() {
		Sprite bgSprite = new Sprite(0, 0,
				AndResourceLoader.getTextureRegionByName("bg_lev.png"),
				AndEnviroment.getInstance().getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(GameConfig.scaleRationY);
		this.setBackground(new SpriteBackground(bgSprite));
		/* ����ǽ�� */
		Sprite leftWallSprite = new Sprite(0, 0,
				GameData.getInstance().leftWallTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		Sprite leftShadowSprite = new Sprite(640, 0,
				GameData.getInstance().shadowRightTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());// ��Ӱʵ����Ū����
		Sprite markSprite = new Sprite((float) (640 - (195 / 2)), 0,
				GameData.getInstance().markMainTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());

		markSprite.setScale(GameConfig.scaleRationX);
		/* left��ť */

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
						.getInstance().getVertexBufferObjectManager());

		rightEntity = new Entity((float) (GameConfig.screenWidth * 0.92), 0);
		rightEntity.attachChild(rightShadowSprite);
		rightEntity.attachChild(rightWallSprite);
		this.attachChild(rightEntity);
		this.attachChild(leftEntity);

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
		eXTRA_GAME_LAYEREntity.attachChild(settingEntity);
		settingEntity.registerEntityModifier(new MoveModifier(1.6f,
				GameConfig.screenWidth / 2 - (210 * GameConfig.scaleRationX),
				GameConfig.screenWidth / 2 - (210 * GameConfig.scaleRationX),
				-500, GameConfig.screenHeight / 6, EaseExponentialInOut
						.getInstance()));
		intiMusicButton();// ��ʼ�����ְ�ť
		intiSoundButton();// ��ʼ����Ч��ť
		intiVibrateButton();
	}

	@Override
	public void endScene() {

	}

	@Override
	public void startScene() {

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

	@Override
	public void onClick(final ButtonSprite pButtonSprite,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		switch (pButtonSprite.getTag()) {
		case RETURNSCENE:
			AndEnviroment.getInstance().getEngine()
					.setScene(WelcomeActivity.mScene);
			break;
		case SOUNDSWITCH:
			onSoundClick();
			break;
		case MUSICSWITCH:
			onMusicClick();
			break;
		case VIBRATESWITCH:
			onVibrateClick();
		default:
			break;
		}

	}

	/**
	 * ���𶯰�ť����֮��
	 */
	public void onVibrateClick() {
		boolean state = AndEnviroment.getInstance().getVibro();
		if (state) {
			// �ر���Ч
			AndEnviroment.getInstance().toggleVibro();
			vibrateSwitchButtonSprite.setCurrentTileIndex(1);
		} else {
			AndEnviroment.getInstance().toggleVibro();
			vibrateSwitchButtonSprite.setCurrentTileIndex(0);
		}
	}

//	/**
//	 * ��ó���ʵ��
//	 * 
//	 * @return
//	 */
//	@Deprecated
//	public static synchronized AndScene getInstance() {
//		if (mInstance == null)
//			mInstance = new SettingScene();
//		return mInstance;
//	}
}
