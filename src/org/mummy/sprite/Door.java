package org.mummy.sprite;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;

import android.graphics.Point;

public class Door extends BasePersonSpite {
	public int direction = 0;// �ŵķ���
	public boolean doorState = true;// ���ſ��ط���

	public Door(float pX, float pY,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, GameData.getInstance().doorTextureRegion,
				vertexBufferObjectManager);
		spriteType = SPRITETYPE_PROP;
	}

	@Override
	public Point whereTo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doForMap() {
		// TODO Auto-generated method stub

	}

	/**
	 * ����������ͼ
	 */
	public void dead() {
		if (doorState) {
			close();
		} else {
			live();
		}
	}

	/**
	 * ����
	 */
	public void close() {		
		switch (direction) {
		case GameConfig.DIRECTION_NORTH:
			animate(new long[] { 50, 50 }, new int[] { 0, 0 }, 3);
			break;
		case GameConfig.DIRECTION_EAST:
			animate(new long[] { 50, 50 }, new int[] { 2, 2 }, 3);
			break;
		case GameConfig.DIRECTION_SOUTH:
			animate(new long[] { 50, 50 }, new int[] { 4, 4 }, 3);
			break;
		case GameConfig.DIRECTION_WEST:
			animate(new long[] { 50, 50 }, new int[] { 6, 6 }, false);
			break;
		}
		changeMap(false);
		doorState = false;
	}

	/**
	 * ����
	 */
	public void live() {	
		switch (direction) {
		case GameConfig.DIRECTION_NORTH:
			animate(new long[] { 50, 50 }, new int[] { 1, 1 }, false);
			break;
		case GameConfig.DIRECTION_EAST:
			animate(new long[] { 50, 50 }, new int[] { 3, 3 }, false);
			break;
		case GameConfig.DIRECTION_SOUTH:
			animate(new long[] { 50, 50 }, new int[] { 5, 5 }, false);
			break;
		case GameConfig.DIRECTION_WEST:
			animate(new long[] { 50, 50 }, new int[] { 7, 7 }, false);
			break;
		}
		changeMap(true);
		doorState = true;
	}

	/**
	 * �ı��ͼ���飬�������(F)����(T)
	 * 
	 * @param state
	 */
	public void changeMap(boolean state) {
		int src = GameData.getInstance().map[mapY][mapX];
		if (!state) {
			/* ���Ų��� */
			if (!doorState) {
				return;
			}
			GameData.getInstance().map[mapY][mapX] = src | direction;
			switch (direction) {
			case GameConfig.DIRECTION_NORTH:
				GameData.getInstance().map[mapY - 1][mapX] = GameData
						.getInstance().map[mapY - 1][mapX] | 4;
				break;
			case GameConfig.DIRECTION_EAST:
				GameData.getInstance().map[mapY][mapX + 1] = GameData
						.getInstance().map[mapY][mapX + 1] | 8;
				break;
			case GameConfig.DIRECTION_SOUTH:
				GameData.getInstance().map[mapY + 1][mapX] = GameData
						.getInstance().map[mapY + 1][mapX] | 1;
				break;
			case GameConfig.DIRECTION_WEST:
				GameData.getInstance().map[mapY][mapX - 1] = GameData
						.getInstance().map[mapY][mapX - 1] | 2;
				break;
			}
		} else {
			/* ���Ų��� */
			if (doorState) {
				return;
			}
			GameData.getInstance().map[mapY][mapX] = src - direction;
			switch (direction) {
			case GameConfig.DIRECTION_NORTH:
				GameData.getInstance().map[mapY - 1][mapX] = GameData
						.getInstance().map[mapY - 1][mapX] - 4;
				break;
			case GameConfig.DIRECTION_EAST:
				GameData.getInstance().map[mapY][mapX + 1] = GameData
						.getInstance().map[mapY][mapX + 1] - 8;
				break;
			case GameConfig.DIRECTION_SOUTH:
				GameData.getInstance().map[mapY + 1][mapX] = GameData
						.getInstance().map[mapY + 1][mapX] - 1;
				break;
			case GameConfig.DIRECTION_WEST:
				GameData.getInstance().map[mapY][mapX - 1] = GameData
						.getInstance().map[mapY][mapX - 1] - 2;
				break;
			}
		}
	}

	/**
	 * ���þ���
	 */
	public void reset() {

	}

}
