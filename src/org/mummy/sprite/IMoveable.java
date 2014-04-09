package org.mummy.sprite;

import android.graphics.Point;

public interface IMoveable {

	/**
	 * ִ�н�ɫ���������
	 */
	public void execute();

	/**
	 * �ƶ�������������Ҫȥ��
	 */
	public void moveController(final int step);

	/**
	 * �����ڵ�ǰ·���㷢����ʲô���飬�����Ƿ��������ִ��
	 * 
	 * @return �����Ƿ��������ִ��
	 */
	public boolean whatOccur(int stepIndex);

	/**
	 * ����Ӧ��ȥ��
	 */
	public Point whereTo();

	/**
	 * ��������˭��ִ��
	 */
	public void callNextAction();

	/**
	 * ����ͼ���������Ӱ��
	 */
	public void doForMap();

	/**
	 * ���������������Ӱ��
	 */
	public boolean doForOther(BasePersonSpite sprite);

	/**
	 * ����Ū������
	 */
	public void dead();

	/**
	 * ����Ū���
	 */
	public void live();
	/**
	 * ���ᾫ��
	 */
	public void freeze();

	/**
	 * ��������
	 */
	public void undo();

	/**
	 * ������ʼ���������ʹ���ֶ���ת����
	 */
	public void init();

	
}
