package org.mummy.sprite;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.utils.AndUtil;

import android.graphics.Point;

/**
 * 模拟蝎子类
 * @author Hanson.Tian
 *
 */
public class Scorpion extends BasePersonSpite {
	@Override
	public void execute() {
		clearEntityModifiers();
		registerEntityModifier(new LoopEntityModifier(new DelayModifier(15,
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
								200, 200, 200, 200, 200, 200, 200, 200, 200 },
								16, 31, false);

					}
				})));
		super.execute();
	}

	@Override
	public void moveController(final int stepIndex) {

		if (liveState != LIVE) {
			callNextAction();// 如果被冻住了，这从责任序列中找到下一个元素
			return;
		}
		if (whatOccur(stepIndex)) {
			if (stepIndex == 0) {
				callNextAction();// 如果走完了，这从责任序列中找到下一个元素
				return;
			}
			final Point point = whereTo();
			final int direction = AndUtil.getPointDirection(point);
			final float moveTime = (direction == GameConfig.DIRECTION_MIDDLE) ? 0.01f
					: moveSpeed;
			/* 改成线程监听的方式，处理爆炸效果的不同步 */
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					/* 用来检测爆炸效果是否完成 */
					while (!GameData.getInstance().explodeEffect.isAnimationFinished) {
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (soundEffect != null & moveTime > 0.01f){						
						soundEffect.play();
					}
					registerEntityModifier(new MoveByModifier(moveTime,
							point.x, point.y, new IEntityModifierListener() {
								@Override
								public void onModifierStarted(
										IModifier<IEntity> pModifier,
										IEntity pItem) {
									switch (direction) {
									case GameConfig.DIRECTION_SOUTH:
										animate(new long[] { 100, 100, 100, 100 },
												4, 7, true);
										break;
									case GameConfig.DIRECTION_EAST:
										animate(new long[] { 100, 100, 100, 100 },
												8, 11, true);
										break;
									case GameConfig.DIRECTION_NORTH:
										animate(new long[] { 100, 100, 100, 100 },
												0, 3, true);
										break;
									case GameConfig.DIRECTION_WEST:
										animate(new long[] { 100, 100, 100, 100 },
												12, 15, true);
										break;
									}
								}

								@Override
								public void onModifierFinished(
										IModifier<IEntity> pModifier,
										IEntity pItem) {
									switch (direction) {
									case GameConfig.DIRECTION_SOUTH:
										animate(new long[] { 5 },
												new int[] { 4 });
										break;
									case GameConfig.DIRECTION_EAST:
										animate(new long[] { 5 },
												new int[] { 8 });
										break;
									case GameConfig.DIRECTION_NORTH:
										animate(new long[] { 5 },
												new int[] { 0 });
										break;
									case GameConfig.DIRECTION_WEST:
										animate(new long[] { 5 },
												new int[] { 12 });
										break;
									}
									moveController(stepIndex - 1);// 这一步走完了
																	// 就执行下一步
								}
							}));

				}
			});
			thread.start();
		} else {
			return;// 反正不再向下执行了
		}

	}

	public Scorpion(float pX, float pY,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, GameData.getInstance().scorpionTextureRegion,
				vertexBufferObjectManager);
		step = 1;
		LVL = 1;
		spriteType = SPRITETYPE_MONSTER;
		soundEffect = GameData.getInstance().mummySound;
		animate(new long[] { 5 }, new int[] { 4 });
	}

	@Override
	public Point whereTo() {
		int expBetweenMummy = AndUtil.getDirectionBetween(mapX, mapY);// 玩家相对木乃伊的方向
		/* 分解水平方向的方位 */
		int horizontalDirection = expBetweenMummy & 10;
		/* 分解垂直方向的方位 */
		int verticalDirection = expBetweenMummy & 5;

		if (horizontalDirection != 0
				&& (!AndUtil.isNextDirectionWall(mapX, mapY,
						horizontalDirection))) {
			/* 水平方向 */
			return AndUtil.getPointByDirection(horizontalDirection, this);

		} else if (verticalDirection != 0
				&& (!AndUtil.isNextDirectionWall(mapX, mapY, verticalDirection))) {
			// 在垂直方向接近
			return AndUtil.getPointByDirection(verticalDirection, this);

		} else {// 如果都没法接近了
			return new Point(0, 0);
		}
	}

	@Override
	public void doForMap() {

	}

}
