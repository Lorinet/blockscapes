package mesh;

public class DirectionalLightArray extends UBO {

    public DirectionalLightArray(DirectionalLight[] lights) {
        super(32, lights.length);

        if (lights.length > 2) {
            throw new RuntimeException("Too many directional lights!");
        }
        setElementCount(lights.length);
        update(lights);
    }

    public void update(DirectionalLight[] lights) {
        if(elementCount != lights.length) {
            throw new RuntimeException("Wrong number of directional lights!");
        }
        bind();
        reset();
        for (DirectionalLight light : lights) {
            putVec4(light.getColor());
            putVec4(light.getDirection());
        }
        bufferData();
    }
}