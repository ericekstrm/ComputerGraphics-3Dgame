package framebuffer;

import java.nio.ByteBuffer;
import main.main;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FrameBuffer
{

    public static final int WIDTH = 512;
    public static final int HEIGHT = 512;

    private int frameBuffer;
    private int texture;
    private int depthMap;

    public FrameBuffer()
    {
        createFrameBuffer();
        createTextureAttachment(WIDTH, HEIGHT);
        unbindFrameBuffer();
    }

    public void bindFrameBuffer()
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);//To make sure the texture isn't bound
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
    }

    private void createFrameBuffer()
    {
        //generate name for frame buffer
        frameBuffer = GL30.glGenFramebuffers();

        //create the framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);

        //indicate that we will always render to color attachment 0
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
    }

    private void createTextureAttachment(int width, int height)
    {
        texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
                          0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                                  texture, 0);
    }

    public int getTexture()
    {
        //get the resulting texture
        return texture;
    }

    public int getDepth()
    {
        return depthMap;
    }

    public void unbindFrameBuffer()
    {
        //call to switch to default frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, main.WIDTH, main.HEIGHT);
    }
}
