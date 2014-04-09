package org.mummy.utils;

import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.sprite.BasePersonSpite;
import org.mummy.sprite.Player;

import android.graphics.Point;

/**
 * 防止一些游戏相关的计算工具类
 * 
 * @author Hanson.Tian
 * @since 2012.9.12
 */
public class AndUtil {
	/**
	 * 输入两点，计算出夹角方向，如果起始点结束点相同则根据mX mY判断相对位置
	 * 
	 * @param sX  起始位置
	 * @param sY
	 * @param eX
	 * @param eY
	 * @param mX
	 * @param mY
	 * @return
	 */

	public static int getGestureDirectionPlus(int sX, int sY, int eX, int eY,
			float mX, float mY) {
		System.out.println("sX=" + sX + " " + sY + " " + eX + " " + eY + "p="
				+ mX + " " + mY);
		if ((sX == eX & sY == eY)
				|| ((sX -5 < eX | eX < sX + 5) && ((sY - 5 < eY | eY < sY + 5)))) {
			if (sX > mX && sX < mX + GameConfig.tileMapWidth)
				if (sY > mY && sY < mY + GameConfig.tileMapWidth)
					return GameConfig.DIRECTION_MIDDLE;
			sX = (int) ((int) mX + (0.5 * GameConfig.tileMapWidth));
			sY = (int) ((int) mY + (0.5 * GameConfig.tileMapWidth));
		}
		Double angle = Math.atan2(sX - eX, sY - eY) / Math.PI * 180;
		if (angle > -135 && angle < -45)
			return GameConfig.DIRECTION_EAST;
		if (angle > -45 && angle < 45)
			return GameConfig.DIRECTION_NORTH;
		if (angle > 45 && angle < 135)
			return GameConfig.DIRECTION_WEST;
		if (angle > 135 | angle < -135)
			return GameConfig.DIRECTION_SOUTH;
		return 0;
	}
	/**
	 * 
	 * @param sX
	 * @param sY
	 * @param eX
	 * @param eY
	 * @param mX
	 * @param mY
	 * @return
	 */
	public static int getGestureDirection(int sX, int sY, int eX, int eY,
			float mX, float mY) {
		if (sX == eX & sY == eY) {
			sX = (int) ((int) mX + (0.5 * GameConfig.tileMapWidth));
			sY = (int) ((int) mY + (0.5 * GameConfig.tileMapWidth));
		}
		Double angle = Math.atan2(sX - eX, sY - eY) / Math.PI * 180;		
		if (angle > -135 && angle < -45)
			return GameConfig.DIRECTION_EAST;
		if (angle > -45 && angle < 45)
			return GameConfig.DIRECTION_NORTH;
		if (angle > 45 && angle < 135)
			return GameConfig.DIRECTION_WEST;
		if (angle > 135 | angle < -135)
			return GameConfig.DIRECTION_SOUTH;
		return 0;
	}
	/**
	 * 判断下一个方向是不是墙,返回
	 * 
	 * @param direction
	 * @return
	 */
	public static boolean isNextDirectionWall(int x, int y, int direction) {
		int currentPosition = GameData.getInstance().map[y][x];
		int result = currentPosition & direction;
		if (result == 0) {
			// 检测临近墙体
			try {
				switch (direction) {
				case GameConfig.DIRECTION_NORTH:
					result = GameData.getInstance().map[y - 1][x] & 4;
					break;
				case GameConfig.DIRECTION_EAST:
					result = GameData.getInstance().map[y][x + 1] & 8;
					break;
				case GameConfig.DIRECTION_SOUTH:
					result = GameData.getInstance().map[y + 1][x] & 1;
					break;
				case GameConfig.DIRECTION_WEST:
					result = GameData.getInstance().map[y][x - 1] & 2;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (result == 0)
			return false;
		return true;
	}
	
	public static boolean isNextDirectionWallOLD(int x, int y, int direction) {	
		int currentPosition = GameData.getInstance().map[y][x];
		int result = currentPosition & direction;		
		if (result == 0)
			return false;
		
		
		return true;
	}

	/**
	 * * 获得两个坐标之间的相对方向T相对于S的方向
	 * 
	 * @param pT
	 * @param pS
	 * @return 相对方向 s在e的那一边
	 */
	public static int getDirectionBetween(int sX, int sY) {
		Player player = null;
		for (BasePersonSpite item : GameData.spriteList) {
			if (item instanceof Player) {
				player = (Player) item;
				break;
			}
		}

		int eX = player.mapX;
		int eY = player.mapY;
		Double angle = Math.atan2(sY - eY, sX - eX) / Math.PI * 180;

		if (sX == eX & sY == eY)
			return GameConfig.DIRECTION_MIDDLE;
		if (angle > 0d && angle < 90d)
			return GameConfig.DIRECTION_WN;
		if (angle > 90d && angle < 180d)
			return GameConfig.DIRECTION_NE;
		if (angle > -90d && angle < 0d)
			return GameConfig.DIRECTION_SW;
		if (angle > -180d && angle < -90)
			return GameConfig.DIRECTION_ES;
		if (angle == 0d)
			return GameConfig.DIRECTION_WEST;
		if (angle == 90d)
			return GameConfig.DIRECTION_NORTH;
		if (angle == 180d)
			return GameConfig.DIRECTION_EAST;
		if (angle == -90d)
			return GameConfig.DIRECTION_SOUTH;
		return GameConfig.DIRECTION_MIDDLE;
	}

	/**
	 * 用来根据点坐标返回方向
	 * 
	 * @param point
	 * @return
	 */
	public static int getPointDirection(Point point) {
		if (point.y > 0)
			return GameConfig.DIRECTION_SOUTH;
		if (point.y < 0)
			return GameConfig.DIRECTION_NORTH;
		if (point.x < 0)
			return GameConfig.DIRECTION_WEST;
		if (point.x > 0)
			return GameConfig.DIRECTION_EAST;
		return GameConfig.DIRECTION_MIDDLE;

	}

	/**
	 * 
	 * @param moveDirection
	 * @param spite
	 * @return
	 */
	public static Point getPointByDirection(int moveDirection,
			BasePersonSpite spite) {
		Point point = new Point(0, 0);
		switch (moveDirection) {
		case GameConfig.DIRECTION_EAST:
			point.x = (int) GameConfig.tileMapWidth;
			spite.mapX++;
			break;
		case GameConfig.DIRECTION_SOUTH:
			point.y = (int) GameConfig.tileMapWidth;
			spite.mapY++;
			break;
		case GameConfig.DIRECTION_WEST:
			point.x = (int) -GameConfig.tileMapWidth;
			spite.mapX--;
			break;
		case GameConfig.DIRECTION_NORTH:
			point.y = (int) -GameConfig.tileMapWidth;
			spite.mapY--;
			break;
		default:

			break;
		}
		return point;
	}

}
