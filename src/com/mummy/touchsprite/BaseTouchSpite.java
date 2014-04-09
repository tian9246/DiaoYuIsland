package com.mummy.touchsprite;

import java.util.ArrayList;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.gamedata.Motion;
import org.mummy.sprite.BasePersonSpite;
import org.mummy.sprite.Player;
import org.mummy.utils.AndEnviroment;
import org.mummy.utils.AndUtil;
import org.mummy.utils.AndVibration;

import android.annotation.SuppressLint;

import com.mummy.scene.MainGameScene;

/**
 * �������
 * 
 * @author Hanson.Tian
 * @since 2012.9.27
 */
public class BaseTouchSpite extends AnimatedSprite {

	public int mapX;// ��ͼ����x
	public int mapY;// ��ͼ����y
	public BasePersonSpite fatherBasePersonSpite;// �����鷽��
	public int liveState;// ��ǰ������״̬
	public boolean dragable = false;
	public boolean isDragFinished = true;
	public static ArrayList<Motion> playerMotions;
	public static ArrayList<Line> lines = new ArrayList<Line>();
	public int onTouchScaleRation = 3;
	public Scene mScene;
	public BaseTouchSpite(BasePersonSpite bps) {
		super(
				bps.getX()
						- ((GameConfig.realTileMapWidth - GameConfig.tileMapWidth) / 2),
				bps.getY()
						- ((GameConfig.realTileMapWidth - GameConfig.tileMapWidth) / 2),
				GameData.getInstance().pointerTextureRegion, bps
						.getVertexBufferObjectManager());
		this.fatherBasePersonSpite = bps;
		mapX = bps.mapX;
		mapY = bps.mapY;
		if (bps instanceof Player) {
			dragable = true;
			playerMotions = new ArrayList<Motion>();
			Motion motion = new Motion();
			motion.mapX = mapX;
			motion.mapY = mapY;
			int pX = (int) ((int) ((mapX + 0.5) * GameConfig.tileMapWidth) - 75 * 3 / 2 + MainGameScene.wallLayer
					.getX());
			int pY = (int) ((mapY + 0.5) * GameConfig.tileMapWidth) - 75 * 3 / 2;
			motion.x = pX;
			motion.y = pY;
			playerMotions.add(motion);
		}
		animate(new long[] { 5, 5 }, new int[] { 3, 3 }, 1);
		lines.clear();
	}

	@SuppressLint("ParserError")
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		setScaleCenter(0, 0);
		if (pSceneTouchEvent.isActionDown()) {
			/* ��������¼����� */
			if (isDragFinished) {
				setScale(3);
				animate(new long[] { 10, 10 }, new int[] { 0, 0 }, 1);
				isDragFinished = false;
			}
		}
		if (isDragFinished) {
			return true;
		}
		/* �����ͼλ������ */
		try {
			mapX = MainGameScene.wallLayer.getTMXTileAt(
					pSceneTouchEvent.getX(), pSceneTouchEvent.getY())
					.getTileColumn();//
			mapY = MainGameScene.wallLayer.getTMXTileAt(
					pSceneTouchEvent.getX(), pSceneTouchEvent.getY())
					.getTileRow();
		} catch (Exception e) {
			AndVibration.duration(300);
			isDragFinished = true;
		}

		/* ��õ�ͼ�� ���ӵ����� */
		
		/* �����ͼλ������ */
		int pX = (int) ((int) ((mapX + 0.5) * GameConfig.tileMapWidth) - 75 * 3 / 2 + MainGameScene.wallLayer
				.getX());
		int pY = (int) ((mapY + 0.5) * GameConfig.tileMapWidth) - 75 * 3 / 2;
		setPosition(pX, pY);
		try {
			Motion motion = playerMotions.get(playerMotions.size() - 1);
			if (motion.mapX != mapX || motion.mapY != mapY
					|| (mapX != motion.mapX && mapY != motion.mapY)) {
				/* ˵��λ�øı��� */
				/* �����һ�������ǲ���ǽ */
				if (AndUtil.isNextDirectionWallOLD(motion.mapX, motion.mapY,
						AndUtil.getGestureDirection(motion.mapX, motion.mapY,
								mapX, mapY, 0, 0))) {
					// �����ǽ�Ļ�
					AndVibration.duration(500);
					isDragFinished = true;
					setScale(2);
					setScaleCenter(0, 0);
					setPosition(motion.x + 37.5f, motion.y + 37.5f);
				} else {

					// �������ǽ�Ļ�������һ��С���𶯣�Ȼ����Ӷ����������б���ȥ
					Line line = new Line(motion.x + 112.5f, motion.y + 112.5f,
							pX + 112.5f, pY + 112.5f, 5, AndEnviroment
									.getInstance()
									.getVertexBufferObjectManager());
					line.setColor(Color.RED);
					line.setAlpha(0.5f);
					mScene.attachChild(line);
					lines.add(line);
					AndVibration.duration(50);
					Motion nMotion = new Motion();
					nMotion.x = pX;
					nMotion.y = pY;
					nMotion.mapX = mapX;
					nMotion.mapY = mapY;
					playerMotions.add(nMotion);
					setPosition(pX, pY);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pSceneTouchEvent.isActionUp()) {
			isDragFinished = true;		
			setScale(2);
			setScaleCenter(0, 0);
			setPosition(getX() + (float) (0.5 * GameConfig.realTileMapWidth),
					getY() + (float) (0.5 * GameConfig.realTileMapWidth));
		}
		return true;
	}

	/**
	 * ��ʼ����ҵ���
	 * 
	 * @param pScene
	 */
	public void init(Scene pScene) {
		mScene = pScene;
		setScaleCenter(getWidth() / 2, getHeight() / 2);
		setScale(2);
		animate(new long[] { 50, 50 }, new int[] { 1, 0 }, 3);
		pScene.registerTouchArea(this);// �����player�Ļ���ע�ᴥ��������Ϣ
	}

}
