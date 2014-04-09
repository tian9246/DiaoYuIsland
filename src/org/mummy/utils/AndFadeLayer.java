package org.mummy.utils;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.modifier.IModifier;
import org.mummy.gamedata.GameConfig;

import android.util.Log;

public class AndFadeLayer extends Entity {

	private Rectangle mScreenBlack;
	private float mDelay = 0.2f;
	private float mDuration = 0.5f;

	public AndFadeLayer() {
		this.mScreenBlack = new Rectangle(0, 0, GameConfig.screenWidth,  GameConfig.screenHeight, AndEnviroment.getInstance()
				.getVertexBufferObjectManager());
		this.mScreenBlack.setColor(0f, 0f, 0f);
		mScreenBlack.setAlpha(0.0f);// …Ë÷√≥…Õ∏√˜
		attachChild(this.mScreenBlack);
	}

	public void setFadeDelay(final float pDelay) {
		this.mDelay = pDelay;
	}

	public void setFadeDuration(final float pDuration) {
		this.mDuration = pDuration;
	}

	public void setTransparency(final float pAlpha) {
		Log.e("getTransparency", "getTransparency" + pAlpha);
		this.mScreenBlack.setAlpha(pAlpha);
	}

	public void fadeOut() {
		registerUpdateHandler(new TimerHandler(AndFadeLayer.this.mDelay + 1f,
				false, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						AndFadeLayer.this.mScreenBlack
								.registerEntityModifier(new AlphaModifier(
										AndFadeLayer.this.mDuration, 1f, 0f,
										new IEntityModifierListener() {
											@Override
											public void onModifierFinished(
													IModifier<IEntity> pModifier,
													IEntity pItem) {
												AndFadeLayer.this
														.setTransparency(0f);
												((AndScene) AndEnviroment
														.getInstance()
														.getScene())
														.startScene();
											}

											@Override
											public void onModifierStarted(
													IModifier<IEntity> pModifier,
													IEntity pItem) {

											}
										}));
					}
				}));
	}

	public void fadeIn() {
		registerUpdateHandler(new TimerHandler(AndFadeLayer.this.mDelay, false,
				new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						AndFadeLayer.this.mScreenBlack
								.registerEntityModifier(new AlphaModifier(
										AndFadeLayer.this.mDuration, 0f, 1f,
										new IEntityModifierListener() {
											@Override
											public void onModifierFinished(
													IModifier<IEntity> pModifier,
													IEntity pItem) {
												AndFadeLayer.this
														.setTransparency(1f);
												((AndScene) AndEnviroment
														.getInstance()
														.getScene()).endScene();
											}

											@Override
											public void onModifierStarted(
													IModifier<IEntity> pModifier,
													IEntity pItem) {

											}
										}));
					}
				}));
	}

}