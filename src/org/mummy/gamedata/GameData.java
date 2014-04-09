package org.mummy.gamedata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXObjectProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.mummy.sprite.BasePersonSpite;
import org.mummy.sprite.Door;
import org.mummy.sprite.ExplodeEffect;
import org.mummy.utils.AndMusic;
import org.mummy.utils.AndResourceLoader;
import org.mummy.utils.AndSound;

import android.os.Handler;
import android.util.Log;

import com.mummy.touchsprite.BaseTouchSpite;

/**
 * ��һЩ�����õĶ�����Ϸ����Դ�ļ�
 * 
 * @author Hanson.Tian
 * @author 2012.09.10
 */
public class GameData {

	public final static int MAINMENU_PLAY = 1;
	public final static int MAINMENU_CONTINUE = 2;
	public final static int MAINMENU_ABOUT = 3;
	public final static int MAINMENU_NEWGAME = 4;

	public static int currentLevle = 1;
	public static int spriteListCount = 0;
	public static Handler mHandler;// ����lose win��Ϣ��ȫ��handler

	private static GameData mInstance;

	public static ArrayList<BasePersonSpite> spriteList = new ArrayList<BasePersonSpite>();

	/* ģ�⻭�߹��ܵľ����б� */
	public static ArrayList<BaseTouchSpite> touchSpriteList = new ArrayList<BaseTouchSpite>();

	public Door door;
	public ExplodeEffect explodeEffect;// ��ըЧ��
	// ===========================================================
	// ���������ò���
	// ===========================================================

	/* ��������Ч���Ĳ��� */
	public TextureRegion touchMaskTextureRegion;

	public TiledTextureRegion playerTextureRegion;
	/* ��������ľ�����ǵĲ��� */
	public TiledTextureRegion whiteMummyTextureRegion, redMummyTextureRegion,
			scorpionTextureRegion, redScorpionTextureRegion;
	/* ���������ź;����Կ�׵Ĳ��� */
	public TiledTextureRegion doorTextureRegion, keyTextureRegion,
			trapTextureRegion;
	/* �������崥��ʱ�Ĳ��� */
	public TiledTextureRegion pointerTextureRegion;
	/* ��ըЧ�� */
	public TiledTextureRegion explodeEffectTextureRegion;

	/* ����Ϸ������� */
	public TiledTextureRegion menuButtonTextureRegion;
	public TiledTextureRegion undoButtonTextureRegion;
	public TiledTextureRegion dragButtonTextureRegion;
	public TiledTextureRegion exitButtonTextureRegion;

	public TiledTextureRegion upButtonTextureRegion;
	public TiledTextureRegion rightButtonTextureRegion;
	public TiledTextureRegion downButtonTextureRegion;
	public TiledTextureRegion leftButtonTextureRegion;

	public TiledTextureRegion settingButtonTextureRegion;
	public TiledTextureRegion resetButtonTextureRegion;
	public TiledTextureRegion returnButtonTextureRegion;
	public TiledTextureRegion flagButtonTextureRegion;

	public TextureRegion leftWallTextureRegion;// ��ǽ640*720
	public TextureRegion rightWallTextureRegion;// ��ǽ640*720
	public TextureRegion shadowLeftTextureRegion;// ��ǽ��Ӱ
	public TextureRegion shadowRightTextureRegion;// ��ǽ��Ӱ
	public TextureRegion markMainTextureRegion;// ���ձ�
	public TextureRegion mainMenuBgTextureRegion;// �˵���������
	public TextureRegion loadingBgTextureRegion;// loading����

	public TextureRegion gameOverBgTextureRegion;// �˵���������

	// ===========================================================
	/* ��ӭ����Ĳ��� */
	// ===========================================================

	public TiledTextureRegion markTextureRegion;
	public TiledTextureRegion adventureTextureRegion;
	public TiledTextureRegion settingTextureRegion;
	public TiledTextureRegion exitTextureRegion;
	public TiledTextureRegion recomendTextureRegion;
	public TiledTextureRegion helpTextureRegion;
	// ===========================================================
	/* ѡ�ؽ��渽�Ӳ��� */
	// ===========================================================
	public TextureRegion slideTextureRegion;// ���ձ�
	public TiledTextureRegion levelExitTextureRegion;
	public TextureRegion mainWordTextureRegion;// �˵���������
	public TMXTiledMap mTMXTiledMap; // ��Ƭ��ͼ�ļ�
	public TiledTextureRegion allNumberTextureRegion;
	public TiledTextureRegion[] num_TextureRegion;
	// ===========================================================
	/* ���ý��渽�Ӳ��� */
	// ===========================================================
	public TiledTextureRegion musicTextureRegion;
	public TiledTextureRegion soundTextureRegion;
	public TiledTextureRegion menuExitTextureRegion;
	public TiledTextureRegion vibrateTextureRegion;
	// ===========================================================
	/* �������渽�Ӳ��� */
	// ===========================================================
	public TextureRegion helpBgTextureRegion;

	public int map[][];

	/* ��ҳ�ʼ��λ�� */
	public static int initExplorerX = 2, initExplorerY = 5;
	public static int exitPostionX, exitPostionY;

	/* ľ�����ǵĳ�ʼ��λ�� */
	public static ArrayList<SpriteProperty> spritePropertyList;

	/* ���·�������,��Ч */
	public AndMusic bgMusic;

	public AndSound playerSound, mummySound;
	public AndSound deadSound, explodeSound;

	public static synchronized GameData getInstance() {
		if (mInstance == null)
			mInstance = new GameData();
		return mInstance;
	}

	public void intiSetingData() {
		musicTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_setting_music.png", 879, 92, 1, 3);
		soundTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_setting_sound.png", 879, 92, 1, 3);
		vibrateTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_setting_vibrate.png", 879, 92, 1, 3);
		helpBgTextureRegion = AndResourceLoader
				.getTextureRegionByName("help_bg.png");
	}

	public void intiSound() {
		bgMusic = AndResourceLoader.getMusic("bg");
		playerSound = AndResourceLoader.getSound("play_footsetp");
		mummySound = AndResourceLoader.getSound("mummy_footsetp");
		deadSound = AndResourceLoader.getSound("dead");
		explodeSound = AndResourceLoader.getSound("explode");

	}

	/**
	 * ��ʼ������
	 */
	public void initMainGameData() {

		whiteMummyTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"white_mummy.png", 1200, 150, 2, 16);
		playerTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"player.png", 225, 675, 9, 3);
		redMummyTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"red_mummy.png", 1200, 150, 2, 16);
		scorpionTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"scorpion.png", 1200, 150, 2, 16);
		redScorpionTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"red_scorpion.png", 1200, 150, 2, 16);
		trapTextureRegion = AndResourceLoader.getTiledTextureRegion("trap.png",
				300, 75, 1, 4);
		keyTextureRegion = AndResourceLoader.getTiledTextureRegion("key.png",
				150, 75, 1, 2);
		explodeEffectTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"explode.png", 1330, 190, 1, 7);
		doorTextureRegion = AndResourceLoader.getTiledTextureRegion("door.png",
				600, 75, 1, 8);
		pointerTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"pointer.png", 375, 75, 1, 5);
		touchMaskTextureRegion = AndResourceLoader
				.getTextureRegionByName("mask.png");
		menuButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_menu.png", 291, 96, 1, 3);
		undoButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_undo.png", 291, 96, 1, 3);
		dragButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_drag.png", 291, 96, 1, 3);
		exitButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_exit.png", 291, 96, 1, 3);
		// �������ҵĿ��ư�ť
		upButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_up.png", 321, 106, 1, 3);
		rightButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_right.png", 321, 106, 1, 3);
		downButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_down.png", 321, 106, 1, 3);
		leftButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_left.png", 321, 106, 1, 3);
		// �����˵�֮��Ŀ��ư�ť
		settingButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_setting.png", 879, 92, 1, 3);
		resetButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_reset.png", 879, 92, 1, 3);
		returnButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_return.png", 879, 92, 1, 3);
		flagButtonTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_main_flag.png", 225, 75, 1, 3);
		menuExitTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_lev_return.png", 879, 92, 1, 3);

		leftWallTextureRegion = AndResourceLoader
				.getTextureRegionByName("bg_wall_left.png");
		rightWallTextureRegion = AndResourceLoader
				.getTextureRegionByName("bg_wall_right.png");
		shadowLeftTextureRegion = AndResourceLoader
				.getTextureRegionByName("sh_wall_left.png");
		shadowRightTextureRegion = AndResourceLoader
				.getTextureRegionByName("sh_wall_right.png");
		markMainTextureRegion = AndResourceLoader
				.getTextureRegionByName("main_mark.png");
		mainMenuBgTextureRegion = AndResourceLoader
				.getTextureRegionByName("bg_main_manu.png");
		gameOverBgTextureRegion = AndResourceLoader
				.getTextureRegionByName("main_gameover.png");

	}

	public void initLoadingScene() {
		loadingBgTextureRegion = AndResourceLoader
				.getTextureRegionByName("loading.png");
	}

	public void initWelcomeActivityData() {
		markTextureRegion = AndResourceLoader.getTiledTextureRegion("mark.png",
				1796, 324, 1, 4);
		adventureTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_wel_begin.png", 717, 78, 1, 3);
		settingTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_wel_setting.png", 717, 78, 1, 3);
		exitTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_wel_exit.png", 717, 78, 1, 3);
		recomendTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_recommend.png", 273, 93, 1, 3);
		helpTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_help.png", 273, 93, 1, 3);	
		levelExitTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_lev_exit.png", 501, 64, 1, 3);
		allNumberTextureRegion = AndResourceLoader.getTiledTextureRegion(
				"btn_lev_all.png", 1365, 270, 3, 15);
		mainWordTextureRegion = AndResourceLoader
				.getTextureRegionByName("bg_lev_word.png");
		num_TextureRegion = new TiledTextureRegion[15];
		for (int i = 0; i < 15; i++) {
			ITexture texture = allNumberTextureRegion.getTextureRegion(14 - i)
					.getTexture();

			num_TextureRegion[i] = new TiledTextureRegion(texture,
					allNumberTextureRegion.getTextureRegion(14 - i),
					allNumberTextureRegion.getTextureRegion(29 + i),
					allNumberTextureRegion.getTextureRegion(30 + i));
		}

		slideTextureRegion = AndResourceLoader
				.getTextureRegionByName("bg_lev_slide.png");
		leftWallTextureRegion = AndResourceLoader
				.getTextureRegionByName("bg_wall_left.png");
		rightWallTextureRegion = AndResourceLoader
				.getTextureRegionByName("bg_wall_right.png");
		shadowLeftTextureRegion = AndResourceLoader
				.getTextureRegionByName("sh_wall_left.png");
		shadowRightTextureRegion = AndResourceLoader
				.getTextureRegionByName("sh_wall_right.png");
		markMainTextureRegion = AndResourceLoader
				.getTextureRegionByName("main_mark.png");
		initMainGameData();
		intiSetingData();
		intiSound();

	}

	/**
	 * ������ӡ��ͼ����
	 */
	public static void printMapArray() {
		for (int[] maps : getInstance().map) {
			for (int i : maps) {
				System.out.print(i + " ");
			}
			System.out.println("");
		}
	}

	/**
	 * �����ڹؿ������ʱ�����������ݣ�������ʼ����������͵���Ϣ��
	 */
	public static void readObjectProperty(TMXTiledMap pTMXTiledMap) {

		spritePropertyList = new ArrayList<SpriteProperty>();
		TMXObjectGroup objectLayer0 = pTMXTiledMap.getTMXObjectGroups().get(0);
		ArrayList<TMXObject> objectList = objectLayer0.getTMXObjects();
		for (TMXObject tmxObject : objectList) {
			/* ��ʼ��player��λ�� */
			try {

				if (tmxObject.getName().toLowerCase().equals("player")) {
					GameData.initExplorerX = tmxObject.getX()
							/ GameConfig.realTileMapWidth;
					GameData.initExplorerY = tmxObject.getY()
							/ GameConfig.realTileMapWidth;
					Log.e("Game.readprotraty", "player");
				} else if (tmxObject.getName().toLowerCase().equals("exit")) {
					GameData.exitPostionX = tmxObject.getX();
					GameData.exitPostionY = tmxObject.getY();

				} else {
					SpriteProperty spriteProperty = new SpriteProperty();
					spriteProperty.type = tmxObject.getName();
					spriteProperty.x = tmxObject.getX()
							/ GameConfig.realTileMapWidth;
					spriteProperty.y = tmxObject.getY()
							/ GameConfig.realTileMapWidth;

					for (TMXObjectProperty tmxObjectProperty : tmxObject
							.getTMXObjectProperties()) {
						if (tmxObjectProperty.getName().equals("value")) {
							spriteProperty.index = Integer
									.parseInt(tmxObjectProperty.getValue());
						}
						if (tmxObjectProperty.getName().equals("direction")) {
							spriteProperty.doordirection = Integer
									.parseInt(tmxObjectProperty.getValue());
						}
					}
					spritePropertyList.add(spriteProperty);
				}
				sortSpriteProperties();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static void sortSpriteProperties() {
		Collections.sort(spritePropertyList, new Comparator<SpriteProperty>() {
			@Override
			public int compare(SpriteProperty lhs, SpriteProperty rhs) {
				if (lhs.index > rhs.index)
					return 1;
				if (lhs.index < rhs.index)
					return -1;
				return 0;
			}
		});
	}

}
