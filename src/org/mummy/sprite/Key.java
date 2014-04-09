package org.mummy.sprite;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.mummy.gamedata.GameData;
import org.mummy.gamedata.Motion;

import android.graphics.Point;

public class Key extends BasePersonSpite {

	public int gateNumber;
	public int doorState = LIVE;// trueΪ�ر�״̬

	public Key(float pX, float pY,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, GameData.getInstance().keyTextureRegion,
				vertexBufferObjectManager);
		step = 0;
		LVL = -90;
		spriteType = SPRITETYPE_PROP;
	}

	@Override
	public void execute() {
		callNextAction();
	}

	@Override
	public Point whereTo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doForMap() {
		// TODO Auto-generated meth
	}

	/**
	 * ��ʼ�����嶯��
	 */
	public void init() {
		animate(250, true);
	}

	@Override
	public void undo() {
		try {
			Motion motion = motionList.pop();
			this.doorState = motion.liveState;
			if (motion.liveState == DEAD)
				GameData.getInstance().door.live();// ������
			else if (motion.liveState == LIVE)
				GameData.getInstance().door.close();// �������
			if (motionList.isEmpty())
				motionList.push(motion);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ʵ���ϴ������Ŷ���
	 */
	@Override
	public void dead() {
		this.doorState = -doorState;
		GameData.getInstance().door.dead();// ������
	}

	/**
	 * ���þ���
	 */
	public void reset() {

	}
}
