package org.mummy.utils;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import org.mummy.gamedata.GameConfig;
import org.mummy.gamedata.GameData;

import android.util.Log;

public abstract class AndResourceLoader {

	/**
	 * 指定字体名称获得自定大小的字体
	 * 
	 * @param fontName
	 * @param size
	 * @return
	 */
	public static Font getFont(final String fontName, final float size) {
		FontFactory.setAssetBasePath("font/");
		final ITexture fontTexture = new BitmapTextureAtlas(AndEnviroment
				.getInstance().getTextureManager(), 256, 256,
				TextureOptions.BILINEAR);
		Font font = FontFactory.createFromAsset(AndEnviroment.getInstance()
				.getFontManager(), fontTexture, AndEnviroment.getInstance()
				.getAssetManager(), fontName, size, true,
				android.graphics.Color.WHITE);
		font.load();
		return font;
	}

	public static TiledTextureRegion getTiledTextureRegion(String pName,
			int pBitWidth, int pBitHeight, int pRow, int pColumn) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		BitmapTextureAtlas bTextureAtlas = new BitmapTextureAtlas(AndEnviroment
				.getInstance().getTextureManager(), pBitWidth, pBitHeight,
				TextureOptions.BILINEAR);
		TiledTextureRegion ttrRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(bTextureAtlas, AndEnviroment
						.getInstance().getContext(), pName, 0, 0, pColumn, pRow);
		bTextureAtlas.load();
		return ttrRegion;

	}

	/**
	 * 通过制定材质名称获得材质区域
	 * 
	 * @param texutreNamea
	 * @return
	 */
	public static TextureRegion getTextureRegionByName(final String texutreName) {
		ITexture texture = null;
		try {
			texture = new BitmapTexture(AndEnviroment.getInstance()
					.getTextureManager(), new IInputStreamOpener() {
				public InputStream open() throws IOException {
					return AndEnviroment.getInstance().getAssetManager()
							.open("gfx/" + texutreName);
				}
			}, TextureOptions.BILINEAR);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (texture != null) {
			AndEnviroment.getInstance().getEngine().getTextureManager()
					.loadTexture(texture);
			return TextureRegionFactory.extractFromTexture(texture);
		} else {
			Log.e("AndRecourceLoader", "getTextureByName 失败");
		}
		return null;
	}

	/**
	 * 从指定名称的地图中读入初始化数组
	 * 
	 * @param mapName
	 * @return
	 * @throws TMXLoadException 
	 */
	public static TMXTiledMap initMap(String mapName) throws TMXLoadException {
		GameData.getInstance().map = new int[1][1];		
			final TMXLoader tmxLoader = new TMXLoader(AndEnviroment
					.getInstance().getAssetManager(), AndEnviroment
					.getInstance().getTextureManager(),
					TextureOptions.BILINEAR, AndEnviroment
							.getInstance().getVertexBufferObjectManager(),
					new ITMXTilePropertiesListener() {
						public void onTMXTileWithPropertiesCreated(
								final TMXTiledMap pTMXTiledMap,
								final TMXLayer pTMXLayer,
								final TMXTile pTMXTile,
								final TMXProperties<TMXTileProperty> pTMXTileProperties) {
							try {
								if (GameData.getInstance().map[0].length != pTMXLayer
										.getTileColumns()) {
									GameData.getInstance().map = new int[pTMXLayer
											.getTileColumns()][pTMXLayer
											.getTileRows()];
									GameConfig.mapSize = pTMXLayer
											.getTileRows();
									GameConfig.tileMapWidth = GameConfig.screenHeight
											/ GameConfig.mapSize;
									GameConfig.scaleRation = GameConfig.tileMapWidth
											/ GameConfig.realTileMapWidth;
								}
								String value = pTMXTileProperties.get(0)
										.getValue();
								/* 只有有properties的时候才进行统计数据 */
								GameData.getInstance().map[pTMXTile
										.getTileRow()][pTMXTile.getTileColumn()] = Integer
										.valueOf(value);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});

			return tmxLoader.loadFromAsset("tmx/" + mapName);

		
	}

	public static AndSound getSound(final String pName) {
		AndSound sound = new AndSound();
		try {
			Sound s = SoundFactory.createSoundFromAsset(AndEnviroment
					.getInstance().getEngine().getSoundManager(), AndEnviroment
					.getInstance().getContext(), "mfx/" + pName + ".mp3");
			sound.setSound(s);
		} catch (final IOException e) {

		}
		return sound;
	}
	
	
	public static AndMusic getMusic(final String pName){
		AndMusic mMusic = new AndMusic();
		MusicFactory.setAssetBasePath("mfx/");
		try {			
			Music pMusic = MusicFactory.createMusicFromAsset(AndEnviroment.getInstance().getEngine().getMusicManager(), AndEnviroment.getInstance().getContext(), pName+".mp3");
			pMusic.setLooping(true);
			mMusic.setMusic(pMusic);
		} catch (final IOException e) {
			Debug.e(e);
		}
		return mMusic;
	}
	
	

}
