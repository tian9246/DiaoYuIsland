package org.mummy.gamedata;


/**
 * ����һЩ��Ϸ�йصĳ��������õȵ�
 * 
 * @author Hanson.Tian
 * 
 */
public class GameConfig {	
	public final static String CURRENTLEVEL = "CURRENTLEVEL";//��ǰ�ܿ�
	
	public final static int LEVELPERBIG = 15;//ÿ����ؿ����ж���С�ܿ�
	public final static int BIGLEVELCOUNT= 5;//�ж��ٴ�ؿ�
	
	public final static int GAMESTATE_WIN = -11;
	public final static int GAMESTATE_LOSE = 11;
	
	/* �涨����״̬�����ն�����[0000]-[1111]��ʾ������������ */
	public final static int DIRECTION_NORTH = 1;
	public final static int DIRECTION_NE = 3;
	public final static int DIRECTION_EAST = 2;
	public final static int DIRECTION_ES = 6;
	public final static int DIRECTION_SOUTH = 4;
	public final static int DIRECTION_SW = 12;
	public final static int DIRECTION_WEST = 8;
	public final static int DIRECTION_WN = 9;
	public final static int DIRECTION_MIDDLE = 0;

	/* ������Ļ�ֱ��ʵ����� */
	public static int screenWidth;//��Ļ���
	public static int screenHeight;//��Ļ�߶�
	public static float scaleRationX;//X�᷽�����ű���
	public static float scaleRationY;//y�᷽�����ű���
	public static String language  = "";
	
	public static float tileMapWidth = 68f; // ����֮����Ƭ��ͼ�زĵĿ��
	public static int mapSize = 0;
	public static int realTileMapWidth = 75; // ��Ƭ��ͼ�زĵĿ��
	public static float scaleRation = tileMapWidth / 75f;// ��ͼ���ű���
	public static int camerOffsetX = 0;
	public static int camerOffsetY = 0;
}
