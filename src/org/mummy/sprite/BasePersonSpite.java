package org.mummy.sprite;

import java.util.ArrayList;
import java.util.Stack;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.gamedata.Motion;
import org.mummy.utils.AndSound;
import org.mummy.utils.AndUtil;

import android.graphics.Point;
import android.os.Handler;
import android.os.Message;

import com.mummy.scene.MainGameScene;

/**
 * 精灵基类
 * 
 * @author Hanson.Tian
 * @since 2013.1.18
 */
public abstract class BasePersonSpite extends AnimatedSprite implements
		IMoveable {

	/* 规定精灵状态 */
	public static final int DEAD = -1;
	public static final int FREEZED = 0;
	public static final int LIVE = 1;

	public static final int SPRITETYPE_PLAYER = 0;
	public static final int SPRITETYPE_MONSTER = 1;
	public static final int SPRITETYPE_PROP = 2;

	/* 用来定义怪物级别 主要是玩家 */
	public int spriteType = 0;

	public static float moveSpeed = 0.35f;

	public int step;// 着货一次走几步
	public int mapX;// 地图坐标x
	public int mapY;// 地图坐标y
	public int LVL;// 怪物生存等级
	public int index;// 记录触发顺寻顺序
	public int liveState = 1;// 当前的生存状态
	public Handler mHandler;// 从activity传过来的信息
	protected Stack<Motion> motionList; // 动作序列
	public AndSound soundEffect;

	public BasePersonSpite(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(GameConfig.camerOffsetX + pX, pY, pTiledTextureRegion,
				vertexBufferObjectManager);
		motionList = new Stack<Motion>();
		// 初始化undo列表
		mapX = (int) (pX / GameConfig.tileMapWidth);
		mapY = (int) (pY / GameConfig.tileMapWidth);
		index = GameData.spriteListCount++;
		setScaleCenter(0, 0);// 设置成以原点为中心的缩放
		setScale(GameConfig.scaleRation);
	}

	@Override
	public void execute() {
		/* 在移动之前就把当前状态加到motionList里面 */
		moveController(step);
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
					if (soundEffect != null & moveTime > 0.01f) {
						System.out.println(step + " play"
								+ getClass().getName());
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
										animate(new long[] { 100, 100, 100 },
												6, 8, true);
										break;
									case GameConfig.DIRECTION_EAST:
										animate(new long[] { 100, 100, 100 },
												3, 5, true);
										break;
									case GameConfig.DIRECTION_NORTH:
										animate(new long[] { 100, 100, 100 },
												0, 2, true);
										break;
									case GameConfig.DIRECTION_WEST:
										animate(new long[] { 100, 100, 100 },
												9, 11, true);
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
												new int[] { 8 });
										break;
									case GameConfig.DIRECTION_EAST:
										animate(new long[] { 5 },
												new int[] { 5 });
										break;
									case GameConfig.DIRECTION_NORTH:
										animate(new long[] { 5 },
												new int[] { 2 });
										break;
									case GameConfig.DIRECTION_WEST:
										animate(new long[] { 5 },
												new int[] { 11 });
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

	@Override
	public void undo() {
		try {
			Motion motion = motionList.pop();
			mapX = motion.mapX;
			mapY = motion.mapY;
			setPosition(motion.x, motion.y);
			if (motion.liveState == DEAD) {
				dead();
			}
			if (motion.liveState == LIVE) {
				live();
			}
			if (motion.liveState == FREEZED) {
				freeze();
			}
			if (motionList.isEmpty())
				motionList.push(motion);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 重置精灵
	 */
	public void reset() {
		try {
			Motion motion = motionList.get(0);
			motionList.clear();
			motionList.push(motion);
			mapX = motion.mapX;
			mapY = motion.mapY;
			setPosition(motion.x, motion.y);
			if (motion.liveState == DEAD) {
				dead();
			}
			if (motion.liveState == LIVE) {
				live();
			}
			if (motion.liveState == FREEZED) {
				freeze();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void dead() {
		System.out.println(getClass().getName() + " dead");
		setAlpha(0);// 设置成透明
		liveState = DEAD;
	}

	@Override
	public void live() {
		System.out.println(getClass().getName() + " live");
		setAlpha(1);// 恢复呈不透明状态
		liveState = LIVE;
	}

	@Override
	public void freeze() {

	}

	@Override
	public void callNextAction() {
		if (1 + index <= (GameData.spriteList.size() - 1)) {
			BasePersonSpite item = GameData.spriteList.get(1 + index);
			if (!(item instanceof Player)) {
				System.out.println("callNextAction" + getClass().getName()
						+ index);
				item.execute();
			}
		} else {
			MainGameScene.isExecuteFinished = true;
			for (ButtonSprite item : MainGameScene.buttons) {
				item.setEnabled(true);
			}
		}

	}

	/**
	 * 检查主角目前的生存状态
	 * 
	 * @return 返回ture 如果主角还活着
	 */
	public static boolean checkPlayerAlive() {
		ArrayList<BasePersonSpite> list = GameData.spriteList;
		Player player = null;
		for (BasePersonSpite basePersonSpite : list) {
			if (basePersonSpite instanceof Player)
				player = (Player) basePersonSpite;
		}
		for (BasePersonSpite item : list) {
			if (!(item instanceof Player)
					& (item.spriteType == SPRITETYPE_MONSTER))
				if (item.liveState == LIVE) {
					if ((item.mapX == player.mapX)
							&& (item.mapY == player.mapY)) {
						/* 玩家死掉了 */
						player.liveState = DEAD;
						Message msg = GameData.mHandler.obtainMessage();
						msg.arg1 = GameConfig.GAMESTATE_LOSE;
						GameData.mHandler.sendMessage(msg);
						MainGameScene.isExecuteFinished = true;
						GameData.getInstance().explodeEffect.explode(
								player.getX(), player.getY());
						GameData.getInstance().explodeEffect.isAnimationFinished = false;
						GameData.getInstance().deadSound.play();
						return false;
					}

				}
		}
		try {
			if (GameData.getInstance().map[player.mapX][player.mapY] == -1) {

			}
		} catch (Exception e) {
			Message msg = GameData.mHandler.obtainMessage();
			msg.arg1 = GameConfig.GAMESTATE_WIN;
			GameData.mHandler.sendMessage(msg);
			return false;
		}
		return true;
	}

	@Override
	public boolean whatOccur(int stepIndex) {
		doForMap();
		if (stepIndex == step)
			return true;
		boolean result = true;

		for (BasePersonSpite item : GameData.spriteList) {
			if (item.liveState != LIVE | item instanceof Player
					| item.index == this.index) {
				continue;
			}
			if (item.mapX == this.mapX && (item.mapY == this.mapY)) {
				result = doForOther(item);
			}
		}
		if (result == false) {
			callNextAction();
			return false;// 如果在过程中被别人弄死了，自己就结束了,但是还向下执行
		}
		result = checkPlayerAlive();
		return result;
	}

	/**
	 * 看谁等级高，弄死等级低的，如果遇到等级一样的，弄死别人
	 */
	@Override
	public boolean doForOther(BasePersonSpite sprite) {
		if (!(sprite instanceof Key)) {
			// 如果不是钥匙才触发动画
			GameData.getInstance().explodeSound.play();
			GameData.getInstance().explodeEffect.explode(getX(), getY());
			GameData.getInstance().explodeEffect.isAnimationFinished = false;
		}
		if (sprite.LVL > this.LVL) {
			this.dead();
			return false;
		}
		if (sprite.LVL < this.LVL) {
			sprite.dead();
		}
		if (sprite.LVL == this.LVL) {
			sprite.dead();
		}
		return true;
	}

	@Override
	public void init() {

	}

}
