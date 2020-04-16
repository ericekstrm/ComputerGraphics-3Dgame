package light;

import camera.Camera;
import java.util.ArrayList;
import java.util.List;
import shader.LightShader;
import util.Matrix4f;
import util.Vector3f;

public class LightHandler {
    
    LightShader lightShader;
    
    List<PositionalLight> pointLights = new ArrayList<>();
    List<DirectionalLight> dirLights = new ArrayList<>();

    public LightHandler(Matrix4f projectionMatrix)
    {
        lightShader = new LightShader(projectionMatrix);
    }
    
    public void addPosLight(Vector3f pos, Vector3f color)
    {
        pointLights.add(new PositionalLight(pos, color));
    }
    
    public void addPosLight(Vector3f pos, Vector3f color, float size)
    {
        PositionalLight l = new PositionalLight(pos, color, size);
        pointLights.add(l);
    }
    
    public void addDirLight(Vector3f dir, Vector3f color)
    {
        dirLights.add(new DirectionalLight(dir, color));
    }
    
    
    public void render(Camera camera)
    {
        lightShader.start();
        lightShader.loadWorldToViewMatrix(camera);
        
        for (PositionalLight light : pointLights)
        {
            light.render(lightShader);
        }
        
        lightShader.stop();
    }
    
    public void moveLight(int index, Matrix4f transform)
    {
        pointLights.get(index).setPosition(transform.multiply(pointLights.get(index).getPosition()));
    }
    
    public void rotateDirLight(int index, Matrix4f rotationMatrix)
    {
        dirLights.get(index).setDirection(rotationMatrix.multiply(dirLights.get(index).getDirection()));
    }
    
    public List<PositionalLight> getPointLights()
    {
        return pointLights;
    }
    
    public List<DirectionalLight> getDirLights()
    {
        return dirLights;
    }
}
