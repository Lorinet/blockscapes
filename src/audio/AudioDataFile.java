package audio;

public class AudioDataFile {
    private String name;
    private Float volume;
    private String[] sounds;

    public AudioDataFile(String name, Float volume, String[] sounds) {
        this.name = name;
        this.volume = volume;
        this.sounds = sounds;
    }

    public String getName() {
        return name;
    }

    public Float getVolume() {
        return volume;
    }

    public String[] getSounds() {
        return sounds;
    }
}
