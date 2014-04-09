package com.mummy.scene;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.modifier.IModifier;
import org.mummy.activity.WelcomeActivity;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;
import org.mummy.utils.AndEnviroment;
import org.mummy.utils.AndResourceLoader;
import org.mummy.utils.AndScene;

/**
 * 关卡选择场景
 * 
 * @author Hanson.Tian
 * 
 */
public class LevelChooseScene extends AndScene implements OnClickListener {
	public static final int EXIT = -1;
	public static final int NEXT = -19;
	public static final int PREVIOUS = -22;

	private int currentBigLevel = 0;// 当前的大关卡,默认是1
	int maxUnlockLevel;// 当前最大解锁关卡
	private static LevelChooseScene mInstance = null;
	public Entity leftEntity, rightEntity, maEntity;
	private Sprite slideSprite, maskSprite;
	public ButtonSprite leftButtonSprite, rightButtonSprite,
			numberButtonSprite[];

	public LevelChooseScene() {
		super();
	}

	@Override
	public MenuScene createMenu() {
		return null;
	}

	@Override
	public void createScene() {
		Sprite bgSprite = new Sprite(0, 0,
				AndResourceLoader.getTextureRegionByName("bg_lev.png"),
				AndEnviroment.getInstance().getVertexBufferObjectManager());
		bgSprite.setScaleCenter(0, 0);
		bgSprite.setScale(GameConfig.scaleRationY);
		this.setBackground(new SpriteBackground(bgSprite));
		/* 左侧的墙体 */
		Sprite leftWallSprite = new Sprite(0, 0,
				GameData.getInstance().leftWallTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		Sprite leftShadowSprite = new Sprite(640, 0,
				GameData.getInstance().shadowRightTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());// 阴影实际上弄烦了
		Sprite markSprite = new Sprite((float) (640 - (195 / 2)), 0,
				GameData.getInstance().markMainTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());

		markSprite.setScale(GameConfig.scaleRationX);
		/* left按钮 */
		leftButtonSprite = new ButtonSprite(leftWallSprite.getWidth() - 96
				* GameConfig.scaleRation * 0.8f, GameConfig.screenHeight / 2
				- 107 * GameConfig.scaleRationX / 2,
				GameData.getInstance().leftButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager()) {
			@Override
			protected void applyRotation(final GLState pGLState) {
				final float rotation = this.mRotation;

				if (rotation != 0) {
					final float rotationCenterX = this.mRotationCenterX;
					final float rotationCenterY = this.mRotationCenterY;

					pGLState.translateModelViewGLMatrixf(rotationCenterX,
							rotationCenterY, 0);
					/*
					 * Note we are applying rotation around the y-axis and not
					 * the z-axis anymore!
					 */
					pGLState.rotateModelViewGLMatrixf(rotation, 0, 1, 0);
					pGLState.translateModelViewGLMatrixf(-rotationCenterX,
							-rotationCenterY, 0);
				}
			}
		};
		leftButtonSprite.setTag(PREVIOUS);
		leftButtonSprite.setOnClickListener(this);
		leftButtonSprite.setScaleCenter(0, 0);
		leftButtonSprite.setScale(GameConfig.scaleRation * 0.8f);
		leftButtonSprite.setEnabled(false);

		this.registerTouchArea(leftButtonSprite);
		leftEntity = new Entity((float) (GameConfig.screenWidth * 0.08 - 640),
				0);
		leftEntity.attachChild(leftWallSprite);
		leftEntity.attachChild(leftShadowSprite);
		leftEntity.attachChild(markSprite);
		leftEntity.attachChild(leftButtonSprite);

		// leftEntity.setColor(Color.CYAN);
		leftEntity.setAlpha(0.5f);

		/* 右侧的墙体 */
		Sprite rightWallSprite = new Sprite(6, 0,
				GameData.getInstance().rightWallTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		Sprite rightShadowSprite = new Sprite(0, 0,
				GameData.getInstance().shadowLeftTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());// 6 piex
		/* left按钮 */
		rightButtonSprite = new ButtonSprite(5, GameConfig.screenHeight / 2
				- 107 * GameConfig.scaleRationX / 2,
				GameData.getInstance().rightButtonTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		rightButtonSprite.setTag(NEXT);
		rightButtonSprite.setOnClickListener(this);
		rightButtonSprite.setScaleCenter(0, 0);
		rightButtonSprite.setScale(GameConfig.scaleRation * 0.8f);
		this.registerTouchArea(rightButtonSprite);
		rightEntity = new Entity((float) (GameConfig.screenWidth * 0.92), 0);

		rightEntity.attachChild(rightShadowSprite);
		rightEntity.attachChild(rightWallSprite);
		rightEntity.attachChild(rightButtonSprite);
		/* 滑动选关背景 */
		slideSprite = new Sprite(
				(float) (0.5 * GameConfig.screenWidth - GameData.getInstance().slideTextureRegion.getWidth()
						* 0.5 * GameConfig.scaleRationY),
				(float) (0.15 * GameConfig.screenHeight),
				GameData.getInstance().slideTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		maskSprite = new Sprite(0, 0, GameConfig.screenWidth,
				GameConfig.screenHeight,
				GameData.getInstance().touchMaskTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		maskSprite.setAlpha(0.8f);

		/* 构建关卡选择的数字按钮 */
		numberButtonSprite = new ButtonSprite[15];

		// TODO 为了测试 改制
		maxUnlockLevel = 
		 Integer.parseInt(AndEnviroment.getInstance().getValue(
		 GameConfig.CURRENTLEVEL, "1"));
		for (int i = 0; i < 15; i++) {
			float ppX = 80 + (i % 5) * 155;
			float ppY = 52 + (i / 5) * 168;
			numberButtonSprite[i] = new ButtonSprite(ppX, ppY,
					GameData.getInstance().num_TextureRegion[i], AndEnviroment
							.getInstance().getVertexBufferObjectManager()) {
				@Override
				protected void applyRotation(final GLState pGLState) {
					final float rotation = this.mRotation;
					if (rotation != 0) {
						final float rotationCenterX = this.mRotationCenterX;
						final float rotationCenterY = this.mRotationCenterY;
						pGLState.translateModelViewGLMatrixf(rotationCenterX,
								rotationCenterY, 0);
						/*
						 * Note we are applying rotation around the y-axis and
						 * not the z-axis anymore!
						 */
						pGLState.rotateModelViewGLMatrixf(rotation, 0, 1, 0);
						pGLState.translateModelViewGLMatrixf(-rotationCenterX,
								-rotationCenterY, 0);
					}
				}
			};
			slideSprite.attachChild(numberButtonSprite[i]);
			numberButtonSprite[i].setTag(i);
			numberButtonSprite[i].setOnClickListener(this);
			if (i >= maxUnlockLevel)
				numberButtonSprite[i].setEnabled(false);
			numberButtonSprite[i].setRotation(180);
			this.registerTouchArea(numberButtonSprite[i]);
		}
		slideSprite.setScaleCenter(0, 0);
		slideSprite.setScale(GameConfig.scaleRationY);
		Sprite wordSprite = new Sprite(
				(float) (GameConfig.screenWidth / 2 - 100 * GameConfig.scaleRation),
				slideSprite.getY() / 2 - 23 * GameConfig.scaleRation, GameData
						.getInstance().mainWordTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		wordSprite.setScale(GameConfig.scaleRationX);
		ButtonSprite exitButtonSprite = new ButtonSprite(slideSprite.getX(),
				slideSprite.getY() + slideSprite.getHeightScaled() + 5,
				GameData.getInstance().levelExitTextureRegion, AndEnviroment
						.getInstance().getVertexBufferObjectManager());
		exitButtonSprite.setScaleCenter(0, 0);
		exitButtonSprite.setScale(GameConfig.scaleRationX);
		exitButtonSprite.setTag(EXIT);
		exitButtonSprite.setOnClickListener(this);
		this.registerTouchArea(exitButtonSprite);
		this.attachChild(maskSprite);
		this.attachChild(exitButtonSprite);
		this.attachChild(wordSprite);
		this.attachChild(slideSprite);
		this.attachChild(rightEntity);
		this.attachChild(leftEntity);

	}

	@Override
	public void endScene() {

	}

	@Override
	public void startScene() {

	}

	@Override
	public void onClick(final ButtonSprite pButtonSprite,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (pButtonSprite.getTag() == EXIT) {
			AndEnviroment.getInstance().getEngine()
					.setScene(WelcomeActivity.mScene);
			return;
		}
		if (pButtonSprite.getTag() == NEXT) {
			onNextBigLevel();
			return;
		}
		if (pButtonSprite.getTag() == PREVIOUS) {
			onPreviouBigLevel();
			return;
		}
		pButtonSprite.registerEntityModifier(new SequenceEntityModifier(
				new IEntityModifierListener() {
					@Override
					public void onModifierFinished(
							IModifier<IEntity> pModifier, IEntity pItem) {

					}

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier,
							IEntity pItem) {

					}
				}, new ScaleModifier(0.05f, 1f, 2.5f), new ScaleModifier(0.1f,
						2.5f, 1f, new IEntityModifierListener() {
							@Override
							public void onModifierFinished(
									IModifier<IEntity> pModifier, IEntity pItem) {
							}

							@Override
							public void onModifierStarted(
									IModifier<IEntity> pModifier, IEntity pItem) {
								GameData.currentLevle = currentBigLevel * 15
										+ pButtonSprite.getTag() + 1;
								/* 进入下一关 */
								AndEnviroment.getInstance().getEngine()
										.setScene(new MainGameScene());

							}
						})));

	}

	/**
	 * 当下一个大的关卡载入的时候
	 */
	public void onNextBigLevel() {
		maskSprite.setColor(255, 255, 255);
		maskSprite.setAlpha(maskSprite.getAlpha() * 0.8f);
		currentBigLevel++;
		if (currentBigLevel == (GameConfig.BIGLEVELCOUNT - 1)) {
			rightButtonSprite.setEnabled(false);
		} else {
			leftButtonSprite.setEnabled(true);
		}
		for (final ButtonSprite item : numberButtonSprite) {
			item.registerEntityModifier(new RotationModifier(1.5f, 0, 180));
			item.registerEntityModifier(new SequenceEntityModifier(
					new DelayModifier(0.75f, new IEntityModifierListener() {
						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {

						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {
							if (currentBigLevel * GameConfig.LEVELPERBIG
									+ item.getTag() + 1 > maxUnlockLevel) {
								// 说明需要变成锁
								item.setEnabled(false);
							} else {
								item.setEnabled(true);
							}

						}
					})));

		}
	}

	public void onPreviouBigLevel() {
		currentBigLevel--;
		if (currentBigLevel == 0) {
			leftButtonSprite.setEnabled(false);
		} else {
			rightButtonSprite.setEnabled(true);
		}
		for (final ButtonSprite item : numberButtonSprite) {
			item.registerEntityModifier(new RotationModifier(1.5f, 0, 180));
			item.registerEntityModifier(new SequenceEntityModifier(
					new DelayModifier(0.75f, new IEntityModifierListener() {

						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {

						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {

							if (currentBigLevel * GameConfig.LEVELPERBIG
									+ item.getTag() + 1 > maxUnlockLevel) {
								// 说明需要变成锁
								item.setEnabled(false);

							} else {
								item.setEnabled(true);

							}
						}
					})));
		}
	}

	/**
	 * 获得场景实例
	 * 
	 * @return
	 */
	public static synchronized AndScene getInstance() {
		if (mInstance == null)
			mInstance = new LevelChooseScene();
		return mInstance;
	}
}
