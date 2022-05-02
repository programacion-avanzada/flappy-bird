package flappy_bird.utils;

import javafx.scene.media.AudioClip;

public final class Sound {

	private AudioClip clip;

    private Sound(String name) {
    	clip = new AudioClip(ClassLoader.getSystemResource(name).toString());
    }

    public void play() {
        new Thread() {
            public void run() {
                clip.play();
            }
        }.start();
    }

	public static Sound getDieAudio() {
		return new Sound("sfx/die.wav");
	}

	public static Sound getHitAudio() {
		return new Sound("sfx/hit.wav");
	}

	public static Sound getWingAudio() {
		return new Sound("sfx/wing.wav");
	}

	public static Sound getPointAudio() {
		return new Sound("sfx/point.wav");
	}
	
}
