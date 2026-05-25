package com.spacegame;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {

    private static SoundManager instance;
    private Clip musicClip;
    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void play(String fileName) {
        try {
            File file = new File("resources/sounds/" + fileName);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();

            clip.open(audio);
            FloatControl volume =
            	    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            	volume.setValue(-25.0f);
            clip.start();

        } catch (Exception e) {
            System.out.println("Sound error: " + fileName);
        }
    }	
    public void playMusic(String fileName) {
        try {
            if (musicClip != null && musicClip.isRunning()) {
                return;
            }

            File file = new File("resources/sounds/" + fileName);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);

            musicClip = AudioSystem.getClip();
            musicClip.open(audio);

            FloatControl volume =
                (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);

            volume.setValue(-22.0f); // kısık arka plan sesi

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            System.out.println("Music error: " + fileName);
        }
    }
}