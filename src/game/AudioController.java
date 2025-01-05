package game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class AudioController {
    private float volume;
    private Clip[] clips;
    private int currentClip;
    private boolean locked;

    public AudioController(float volume, Clip[] clips) {
        this.volume = volume;
        this.clips = clips;
        this.currentClip = 0;
        this.locked = false;
    }

    public AudioController(float volume, String[] names) {
        this.volume = volume;
        this.clips = Arrays.stream(names).map(AudioController::loadSound).toArray(Clip[]::new);
        this.currentClip = 0;
        this.locked = false;
    }

    private static Clip loadSound(String name) {
        try {
            File file = new File(Paths.get("audio", name + ".wav").toString());
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioInputStream din;
            if (in != null) {
                Clip clip = AudioSystem.getClip();
                clip.open(in);
                return clip;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public Clip[] getClips() {
        return clips;
    }

    public void setClips(Clip[] clips) {
        this.clips = clips;
    }

    public int getCurrentClip() {
        return currentClip;
    }

    public void setCurrentClip(int currentClip) {
        this.currentClip = currentClip;
    }

    public void playCycle(boolean randomNext) {
        ((FloatControl) clips[currentClip].getControl(FloatControl.Type.MASTER_GAIN)).setValue(volume);
        clips[currentClip].setFramePosition(0);
        clips[currentClip].start();
        if (randomNext) {
            currentClip = ThreadLocalRandom.current().nextInt(clips.length);
        } else {
            currentClip++;
            if (currentClip >= clips.length) {
                currentClip = 0;
            }
        }
    }

    public void playForSecs(boolean randomNext, float secs) {
        long length = clips[currentClip].getMicrosecondLength() / 1000;
        long total = (long) (secs * 1000f);
        setLocked(true);
        playCycle(randomNext);
        try {
            Thread.sleep(total > length ? total - length : 0);
            setLocked(false);
        } catch (InterruptedException e) {
            setLocked(false);
            throw new RuntimeException(e);
        }
    }

    public boolean isPlaying() {
        if (locked) {
            return true;
        }
        for (Clip clip : clips) {
            if (clip.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void stop() {
        for (Clip clip : clips) {
            clip.stop();
        }
    }
}
