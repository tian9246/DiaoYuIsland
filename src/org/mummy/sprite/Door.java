package org.mummy.sprite;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;

import android.graphics.Point;

public class Door extends BasePersonSpite {
	public int direction = 0;// 门的方向
	public boolean doorState = true;// 大门开关方向

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
	 * 用来触发地图
	 */
	public void dead() {
		if (doorState) {
			close();
		} else {
			live();
		}
	}

	/**
	 * 关门
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
	 * 开门
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
	 * 改变地图数组，传入关门(F)或开门(T)
	 * 
	 * @param state
	 */
	public void changeMap(boolean state) {
		int src = GameData.getInstance().map[mapY][mapX];
		if (!state) {
			/* 关门操作 */
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
			/* 开门操作 */
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
	 * 重置精灵
	 */
	public void reset() {

	}

}
