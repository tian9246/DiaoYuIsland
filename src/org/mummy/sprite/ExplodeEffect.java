package org.mummy.sprite;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.utils.AndVibration;

import android.graphics.Point;

public  class ExplodeEffect  extends BasePersonSpite{

	public static boolean isAnimationFinished = true;
	
	public ExplodeEffect(float pX, float pY,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, GameData.getInstance().explodeEffectTextureRegion, vertexBufferObjectManager);
		setAlpha(0);
		
		
	}

	@Override
	public Point whereTo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doForMap() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void execute() {
		callNextAction();
	}
	
	/**
	 * 爆炸效果，用来演示谁死掉了之类的
	 */
	public void explode(final float f,final float g) {               
				System.out.println(Thread.currentThread().toString()+"extRun");
				AndVibration.duration(700);
				setPosition((float)(f+0.5*(GameConfig.tileMapWidth-getWidth())),
						(float)(g+0.5*(GameConfig.tileMapWidth-getWidth())));
				setAlpha(1);	
				
				animate(150, false, new IAnimationListener() {
					
					@Override
					public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
							int pInitialLoopCount) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
							int pRemainingLoopCount, int pInitialLoopCount) {
						
						
					}
					
					@Override
					public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
							int pOldFrameIndex, int pNewFrameIndex) {
						pAnimatedSprite.setScaleCenter(getWidth()/2, getWidth()/2);
					
						
					}
					
					@Override
					public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
						isAnimationFinished = true;
					   setAlpha(0);//消失
						
					}
				});
		
	}

}
