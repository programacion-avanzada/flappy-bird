package flappy_bird.utils;

import javafx.scene.media.AudioClip;

public class Sound {

	private AudioClip clip;

    public Sound(String name) {
        clip = new AudioClip("file:src/main/resources/" + name);
    }

    public void play() {
        new Thread() {
            public void run() {
                clip.play();
            }
        }.start();
    }
	
}
