package org.mummy.gamedata;

/**
 * 用来存储动作状态的POGO，用来实现撤销功能
 * @author Hanson.Tian
 * @since 2012.9.18
 */
public class Motion {
    public int x;
    public int y;
    public int mapX;//放置地图上的位置
    public int mapY;    
    public int liveState;
}
