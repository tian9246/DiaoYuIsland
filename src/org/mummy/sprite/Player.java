package org.mummy.sprite;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.mummy.gamedata.GameData;
import org.mummy.gamedata.Motion;
import org.mummy.utils.AndSound;
import org.mummy.utils.AndUtil;
import org.mummy.utils.AndVibration;

import android.graphics.Point;

import com.mummy.scene.MainGameScene;

/**
 * 用来定义玩家的精灵类
 * 
 * @author Hanson.Tian
 * 
 */
public class Player extends BasePersonSpite {
	
	public int moveDirection;// 玩家的移动方向

	public Player(float pX, float pY,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, GameData.getInstance().playerTextureRegion,
				vertexBufferObjectManager);
		step = 1;// 自己只走一步
		spriteType = SPRITETYPE_PLAYER;
		LVL = 11;
		soundEffect = GameData.getInstance().playerSound;

	}

	/**
	 * 用来触发玩家动作
	 * 
	 * @param direction
	 * @return
	 */
	public boolean execute(final int direction) {

		moveDirection = direction;		
		if (AndUtil.isNextDirectionWall(mapX, mapY, moveDirection)) {
			AndVibration.duration(100);
			return true;
		}
		for (ButtonSprite item : MainGameScene.buttons) {
			item.setEnabled(false);
		}
		/* 开始执行过程 */
		/* 首先吧个个精灵的状态记录在动作列表里，为了以后恢复只用 */
		for (BasePersonSpite itemBasePersonSpite : GameData.spriteList) {
			Motion motion = new Motion();
			if (itemBasePersonSpite instanceof Key) {
				motion.liveState = ((Key) itemBasePersonSpite).doorState;
				itemBasePersonSpite.motionList.push(motion);
				continue;
			}
			motion.x = (int) itemBasePersonSpite.getX();
			motion.y = (int) itemBasePersonSpite.getY();
			motion.mapX = itemBasePersonSpite.mapX;
			motion.mapY = itemBasePersonSpite.mapY;
			motion.liveState = itemBasePersonSpite.liveState;
			itemBasePersonSpite.motionList.push(motion);			
		}

		MainGameScene.isExecuteFinished = false;
		clearEntityModifiers();
		registerEntityModifier(new LoopEntityModifier(new DelayModifier(10,
				new IEntityModifierListener() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier,
					IEntity pItem) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onModifierFinished(
					IModifier<IEntity> pModifier, IEntity pItem) {
				animate(new long[] { 200, 200, 200, 200, 200, 200, 200,
						200, 200, 200, 200, 200, 200, 200, 200 }, 12,
						26, false);

			}
		})));		
		super.execute();
		return false;
	}

	@Override
	public Point whereTo() {
		if (AndUtil.isNextDirectionWall(mapX, mapY, moveDirection)) {
			AndVibration.duration(100);
			return new Point(0, 0);
		}
		return AndUtil.getPointByDirection(moveDirection, this);
	}

	@Override
	public void doForMap() {

	}

}
