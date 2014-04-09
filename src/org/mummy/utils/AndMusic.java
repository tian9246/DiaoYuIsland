package org.mummy.utils;

import org.andengine.audio.music.Music;

import android.media.AudioManager;

public class AndMusic {

	private Music mMusic;

	public AndMusic() {

	}

	public AndMusic(final Music pMusic) {
		this.mMusic = pMusic;
	}

	public void setMusic(final Music pMusic) {
		this.mMusic = pMusic;
	}

	public void stop() {
		try {
			if (mMusic.isPlaying())
				this.mMusic.pause();
		} catch (Exception e) {
			// TODO: handle exception
		}
	
	}

	public void play() {
		int mode = AndEnviroment.getInstance().getAudioManager()
				.getRingerMode();

		if (this.mMusic != null && mode == AudioManager.RINGER_MODE_NORMAL
				&& AndEnviroment.getInstance().getMusic()) {			
			try {
				this.mMusic.getMediaPlayer().prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.mMusic.play();
		}

	}

}