package org.mummy.gamedata;


/**
 * 放置一些游戏有关的常量和配置等等
 * 
 * @author Hanson.Tian
 * 
 */
public class GameConfig {	
	public final static String CURRENTLEVEL = "CURRENTLEVEL";//当前管卡
	
	public final static int LEVELPERBIG = 15;//每个大关卡，有多少小管卡
	public final static int BIGLEVELCOUNT= 5;//有多少大关卡
	
	public final static int GAMESTATE_WIN = -11;
	public final static int GAMESTATE_LOSE = 11;
	
	/* 规定方向状态，按照二级制[0000]-[1111]表示北东南西方向 */
	public final static int DIRECTION_NORTH = 1;
	public final static int DIRECTION_NE = 3;
	public final static int DIRECTION_EAST = 2;
	public final static int DIRECTION_ES = 6;
	public final static int DIRECTION_SOUTH = 4;
	public final static int DIRECTION_SW = 12;
	public final static int DIRECTION_WEST = 8;
	public final static int DIRECTION_WN = 9;
	public final static int DIRECTION_MIDDLE = 0;

	/* 适配屏幕分辨率的数据 */
	public static int screenWidth;//屏幕宽度
	public static int screenHeight;//屏幕高度
	public static float scaleRationX;//X轴方向缩放比例
	public static float scaleRationY;//y轴方向缩放比例
	public static String language  = "";
	
	public static float tileMapWidth = 68f; // 缩放之后瓦片地图素材的宽度
	public static int mapSize = 0;
	public static int realTileMapWidth = 75; // 瓦片地图素材的宽度
	public static float scaleRation = tileMapWidth / 75f;// 地图缩放倍率
	public static int camerOffsetX = 0;
	public static int camerOffsetY = 0;
}
