package flappy_bird.utils;

import javafx.scene.media.AudioClip;

public final class AudioResources {

    private static AudioClip create(String name) {
    	return new AudioClip(ClassLoader.getSystemResource(name).toString());
    }

	public static AudioClip getDieAudio() {
		return create("sfx/die.wav");
	}

	public static AudioClip getHitAudio() {
		return create("sfx/hit.wav");
	}

	public static AudioClip getWingAudio() {
		return create("sfx/wing.wav");
	}

	public static AudioClip getPointAudio() {
		return create("sfx/point.wav");
	}
	
}
