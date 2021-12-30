package org.hyt.graphics.api.model;
import com.jogamp.opengl.GL4;

public interface HYTGLBuffer {

    int getBuffer();

    void setBuffer(int buffer);

    void setData(GL4 gl4, float[] data);

    void delete(GL4 gl4);

}
