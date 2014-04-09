package org.mummy.sprite;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.mummy.gamedata.GameData;

import android.graphics.Point;

/**
 * 模拟陷阱类
 * @author Hanson.Tian
 *
 */
public class Trap extends BasePersonSpite {

	public Trap(float pX, float pY, 
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, GameData.getInstance().trapTextureRegion, vertexBufferObjectManager);
		step = 0;
		LVL = 99;//最高等级，见谁都弄死
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
	 * 初始化陷阱动画
	 */
	public void init(){
		animate(150, true);
	}
	
	@Override
	public void undo(){
		
	}
	/**
	 * 重置精灵
	 */
	public void reset() {		

	}


}
