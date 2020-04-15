package light;

import java.util.ArrayList;
import java.util.List;
import model.ModelLoader;
import loader.RawData;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import loader.Loader;
import shader.LightShader;
import util.Matrix4f;
import util.Vector3f;

public class PositionalLight
{

    int activeVAO;
    List<Integer> activeVBOs = new ArrayList<>();
    int nrIndices = 0;

    Vector3f position;
    Vector3f color;
    float intensity = 2;
    float radius = 100;

    Vector3f modelScale = new Vector3f(0.1f, 0.1f, 0.1f); //scale for drawing the square light model.

    public PositionalLight(Vector3f position, Vector3f color, float scale)
    {
        this(position, color);
        setModelScale(scale);
    }
    
    public PositionalLight(Vector3f position, Vector3f color)
    {
        this.position = position;
        this.color = color;
        RawData data = Loader.loadRawData("light.obj", "");
        activeVAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(activeVAO);

        activeVBOs.add(ModelLoader.loadVertexVBO(data.vertices));

        activeVBOs.add(ModelLoader.loadIndicesVBO(data.indices));
        nrIndices = data.indices.length;

        GL30.glBindVertexArray(0);
    }

    public void render(LightShader shader)
    {
        GL30.glBindVertexArray(activeVAO);
        GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);

        shader.loadModelToWorldMatrix(getModelToWorldMatrix());
        shader.loadColor(color);

        //draw!
        GL11.glDrawElements(GL11.GL_TRIANGLES, nrIndices, GL11.GL_UNSIGNED_INT, 0);
        deactivate();
    }

    public void deactivate()
    {
        GL30.glBindVertexArray(0);

        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
    }

    public void destroy()
    {
        deactivate();
        for (int vbo : activeVBOs)
        {
            GL20.glDeleteBuffers(vbo);
        }
        GL30.glDeleteVertexArrays(activeVAO);
    }

    public Matrix4f getModelToWorldMatrix()
    {
        Matrix4f scale = Matrix4f.scale(modelScale.x, modelScale.y, modelScale.z);
        Matrix4f translate = Matrix4f.translate(position.x, position.y, position.z);

        return translate.multiply(scale);
    }

    public void setModelScale(float scale)
    {
        modelScale = new Vector3f(scale * 0.1f, scale * 0.1f, scale * 0.1f);
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public Vector3f getColor()
    {
        return color;
    }

    public void setPosition(Vector3f new_position)
    {
        position = new_position;
    }

    public float getR()
    {
        return radius;
    }

    public float getIntensity()
    {
        return intensity;
    }
}
