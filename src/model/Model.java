package model;

import java.util.ArrayList;
import java.util.List;
import loader.Loader;
import loader.Material;
import loader.RawData;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import org.lwjgl.opengl.GL13;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.ModelShader;
import shader.Shader;
import util.Matrix3f;
import util.Vector3f;

public class Model extends Movable
{

    public String objectFileName = "";

    protected List<Integer> activeVAOs = new ArrayList<>();
    protected List<Integer> activeVBOs = new ArrayList<>();
    protected List<Integer> nrOfIndices = new ArrayList<>();
    protected List<Material> matProperties = new ArrayList<>();

    protected List<Integer> textureIDs = new ArrayList<>();
    protected List<Integer> bumpmapIDs = new ArrayList<>();

    //both maxHeight and maxRadius are expressed in the default scale of the model.
    private float maxHeight = 0;
    private float maxRadius = 0;

    public Model(String objectFileName)
    {
        this(Loader.loadObj(objectFileName));
        this.objectFileName = objectFileName;
    }

    public Model(RawData... data)
    {
        for (RawData d : data)
        {
            //add new vao to list
            int vaoID = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoID);
            activeVAOs.add(vaoID);

            activeVBOs.add(ModelLoader.loadVertexVBO(d.vertices));
            activeVBOs.add(ModelLoader.loadTextureVBO(d.textureCoords));
            activeVBOs.add(ModelLoader.loadNormalsVBO(d.normals));
            if (d.tangents != null)
            {
                activeVBOs.add(ModelLoader.loadTangentsVBO(d.tangents));
            }

            //add data that is specific to that vao
            activeVBOs.add(ModelLoader.loadIndicesVBO(d.indices));
            nrOfIndices.add(d.indices.length);

            //materials
            matProperties.add(d.material);

            //texture binding
            textureIDs.add(d.material.Kd_map);
            bumpmapIDs.add(d.material.bump_map);

            GL30.glBindVertexArray(0);

            //find the maximum height of the model.
            if (d.maxHeight > maxHeight)
            {
                maxHeight = d.maxHeight;
            }

            //find the maximum radius of the model.
            if (d.maxRadius > maxRadius)
            {
                maxRadius = d.maxRadius;
            }
        }
    }

    public Model(Model m)
    {
        this.activeVAOs = m.activeVAOs;
        this.activeVBOs = m.activeVBOs;
        this.bumpmapIDs = m.bumpmapIDs;
        this.matProperties = m.matProperties;
        this.maxHeight = m.maxHeight;
        this.nrOfIndices = m.nrOfIndices;
        this.objectFileName = m.objectFileName;
        this.orientation = new Matrix3f(m.orientation);
        this.position = new Vector3f(m.position);
        this.scaleX = m.scaleX;
        this.scaleY = m.scaleY;
        this.scaleZ = m.scaleZ;
        this.textureIDs = m.textureIDs;
    }

    public void render(ModelShader shader)
    {
        for (int i = 0; i < activeVAOs.size(); i++)
        {
            GL30.glBindVertexArray(activeVAOs.get(i));
            GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.TANGENT_ATTRIB);

            shader.loadModelToWorldMatrix(getModelToWorldMatrix());
            shader.loadMaterialLightingProperties(matProperties.get(i));

            //textures
            if (textureIDs.get(i) != 0)
            {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, textureIDs.get(i));
                shader.loadHasTexture(true);
            } else
            {
                shader.loadHasTexture(false);
            }

            if (bumpmapIDs.get(i) != 0)
            {
                glActiveTexture(GL13.GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, bumpmapIDs.get(i));
                shader.loadHasNormalMap(true);
            } else
            {
                shader.loadHasNormalMap(false);
            }

            glActiveTexture(GL13.GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, bumpmapIDs.get(i));

            //draw!
            GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(i), GL11.GL_UNSIGNED_INT, 0);
            deactivate();
        }
    }

    public void deactivate()
    {
        GL30.glBindVertexArray(0);

        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.TEX_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.NORMAL_ATTRIB);
    }

    public void destroy()
    {
        for (int vbo : activeVBOs)
        {
            GL20.glDeleteBuffers(vbo);
        }
        for (int vao : activeVAOs)
        {

            GL30.glDeleteVertexArrays(vao);
        }

        //TODO: remove textures
    }

    public void setMaterialProperties(int index, Material matProp)
    {
        matProperties.set(index, matProp);
    }

    public void update(float deltaTime)
    {
    }

    public List<Integer> getActiveVAOs()
    {
        return activeVAOs;
    }

    public List<Integer> getNrOfIndices()
    {
        return nrOfIndices;
    }

    public float getMaxRadius()
    {
        float maxScale = Math.max(scaleX, Math.max(scaleY, scaleZ));
        return maxRadius * maxScale;
    }

    public List<Integer> getTextureIDs()
    {
        return textureIDs;
    }
}
