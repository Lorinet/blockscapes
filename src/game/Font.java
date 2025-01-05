package game;

public abstract class Font {
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract char[][] getData();

    public char[] getChar(char character) {
        return getData()[character];
    }
}
