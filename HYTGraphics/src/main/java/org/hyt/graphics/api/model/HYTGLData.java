package org.hyt.graphics.api.model;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLUniformData;

public interface HYTGLData {

    int getData();

    void setData(int data);

    HYTGLBuffer getBuffer();

    HYTGLBuffer setBuffer(GL4 gl4, HYTGLProgram program, HYTGLBuffer buffer);

    HYTGLAttribute[] getAttributes();

    void setAttributes(HYTGLAttribute[] attributes);

    GLUniformData[] getStaticAttributes();

    void setStaticAttributes(GLUniformData[] staticAttributes);

    HYTGLBuffer delete(GL4 gl4);

}
