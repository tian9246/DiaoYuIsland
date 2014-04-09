package org.mummy.sprite;

import android.graphics.Point;

public interface IMoveable {

	/**
	 * 执行角色动作的入口
	 */
	public void execute();

	/**
	 * 移动控制器，决定要去哪
	 */
	public void moveController(final int step);

	/**
	 * 计算在当前路径点发生了什么事情，返回是否继续向下执行
	 * 
	 * @return 返回是否继续向下执行
	 */
	public boolean whatOccur(int stepIndex);

	/**
	 * 计算应该去哪
	 */
	public Point whereTo();

	/**
	 * 决定下面谁将执行
	 */
	public void callNextAction();

	/**
	 * 给地图物件带来的影响
	 */
	public void doForMap();

	/**
	 * 给其他物件带来的影响
	 */
	public boolean doForOther(BasePersonSpite sprite);

	/**
	 * 用来弄死精灵
	 */
	public void dead();

	/**
	 * 用来弄活精灵
	 */
	public void live();
	/**
	 * 冻结精灵
	 */
	public void freeze();

	/**
	 * 撤销操作
	 */
	public void undo();

	/**
	 * 用来初始化精灵对象，使各种东西转起来
	 */
	public void init();

	
}
