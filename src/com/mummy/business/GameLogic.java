package com.mummy.business;

import java.util.ArrayList;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.mummy.gamedata.GameData;
import org.mummy.sprite.BasePersonSpite;
import org.mummy.sprite.Player;
import org.mummy.utils.AndEnviroment;

import com.mummy.scene.MainGameScene;
import com.mummy.touchsprite.BaseTouchSpite;

/**
 * 
 * @author Hanson.Tian
 * @since 2012.9.13
 */
public class GameLogic {
	public static boolean isTouchMode = false;

	/**
	 * ���峷����
	 */
	public static void undoMetion() {

		for (BasePersonSpite item : GameData.spriteList) {
			item.undo();
		}
		MainGameScene.isExecuteFinished = true;
	}

	/**
	 * ���ùؿ�
	 */
	public static void resetGame() {
		for (BasePersonSpite item : GameData.spriteList) {
			item.reset();
		}
		for (ButtonSprite item : MainGameScene.buttons) {
			item.setEnabled(true);
		}
		MainGameScene.isExecuteFinished = true;
	}

	public static void initSprites() {
		for (BasePersonSpite itme : GameData.spriteList) {
			itme.init();
		}
		MainGameScene.isExecuteFinished = true;
	}

	/**
	 * ��ʼ������
	 */
	public static void initTouchSprite(Scene pScene) {
		for (BasePersonSpite item : GameData.spriteList) {
			if (item.liveState == BasePersonSpite.LIVE) {
				BaseTouchSpite bts = new BaseTouchSpite(item);
				GameData.touchSpriteList.add(bts);
				pScene.attachChild(bts);
				if (item instanceof Player) {
					bts.init(pScene);
				}
			}
		}
	}
	/**
	 * ����touchMode
	 */
	public static void enableTouchMode(Scene pScene) {
		for (ButtonSprite item : MainGameScene.buttons) {
			if (item.getTag() != MainGameScene.DRAG)
				if (item.getTag() != MainGameScene.EXIT)
					item.setEnabled(false);
			if (item.getTag() == MainGameScene.DRAG)
				item.setCurrentTileIndex(1);
		}

		isTouchMode = true;
		GameData.touchSpriteList = new ArrayList<BaseTouchSpite>();
		initTouchSprite(pScene);
	}

	/**
	 * �ر�ģ�⻭�߹���
	 */
	public static void disableTouchMode(Scene pScene) {
		/* ������ָ���ť״̬ */
		for (ButtonSprite item : MainGameScene.buttons) {
			item.setEnabled(true);
			if (item.getTag() == MainGameScene.DRAG)
				item.setCurrentTileIndex(0);
		}
		isTouchMode = false;
		EngineLock engineLock = AndEnviroment.getInstance().getEngine()
				.getEngineLock();
		engineLock.lock();
		for (BaseTouchSpite item : GameData.touchSpriteList) {
			if (item.fatherBasePersonSpite instanceof Player) {
				pScene.unregisterTouchArea(item);
			}
			pScene.detachChild(item);
			item.dispose();
			item = null;
		}
		for (Line item : BaseTouchSpite.lines) {
			pScene.detachChild(item);
			item.dispose();
			item = null;
		}
		BaseTouchSpite.lines = new ArrayList<Line>();
		pScene.getChildByIndex(pScene.getChildCount() - 1).setAlpha(0);// �����ɰ�͸����
		MainGameScene.isExecuteFinished = true;// ���ó�ִ��δ���
		GameData.touchSpriteList = null;
		engineLock.unlock();
	}
}
