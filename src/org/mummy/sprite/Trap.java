package org.mummy.sprite;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.mummy.gamedata.GameData;

import android.graphics.Point;

/**
 * ģ��������
 * @author Hanson.Tian
 *
 */
public class Trap extends BasePersonSpite {

	public Trap(float pX, float pY, 
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, GameData.getInstance().trapTextureRegion, vertexBufferObjectManager);
		step = 0;
		LVL = 99;//��ߵȼ�����˭��Ū��
		spriteType = SPRITETYPE_MONSTER;
	}


	@Override
	public void execute() {
		callNextAction();
	}
	
	@Override
	public Point whereTo() {
	
		return null;
	}

	@Override
	public void doForMap() {
	

	}
	
	/**
	 * ��ʼ�����嶯��
	 */
	public void init(){
		animate(150, true);
	}
	
	@Override
	public void undo(){
		
	}
	/**
	 * ���þ���
	 */
	public void reset() {		

	}


}
