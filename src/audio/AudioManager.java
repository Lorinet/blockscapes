package audio;

import com.google.gson.Gson;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class AudioManager {
    private static HashMap<String, AudioController> SOUNDS;

    public static void init() {
        SOUNDS = new HashMap<>();
        File[] dataFiles = new File("audio").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".dat");
            }
        });

        Gson jsp = new Gson();

        assert dataFiles != null;
        for (File file : dataFiles) {
            String json = null;
            try {
                json = Files.readString(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            AudioDataFile audioData = jsp.fromJson(json, AudioDataFile.class);
            SOUNDS.put(audioData.getName(), new AudioController(audioData.getVolume(), audioData.getSounds()));
        }
    }

    public static void unload() {
        for (AudioController controller : SOUNDS.values()) {
            controller.stop();
        }
        SOUNDS.clear();
    }

    public static AudioController getSound(String name) {
        if (SOUNDS.containsKey(name)) {
            return SOUNDS.get(name);
        } else {
            System.err.println("Could not find sound " + name);
            return null;
        }
    }
}
