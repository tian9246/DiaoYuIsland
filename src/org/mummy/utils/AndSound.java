package org.mummy.utils;

import org.andengine.audio.sound.Sound;

import android.media.AudioManager;

public class AndSound {

	private Sound mSound;

	public AndSound() {

	}

	public AndSound(final Sound pSound) {
		this.mSound = pSound;
	}

	public void setSound(final Sound pSound) {
		this.mSound = pSound;
	}

	public void stop() {
		this.mSound.stop();
	}

	public void play() {
		int mode = AndEnviroment.getInstance().getAudioManager()
				.getRingerMode();
		if (this.mSound != null && mode == AudioManager.RINGER_MODE_NORMAL
				&& AndEnviroment.getInstance().getAudio())
			this.mSound.play();
	}

}