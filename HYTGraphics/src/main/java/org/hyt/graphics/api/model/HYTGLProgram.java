package org.hyt.graphics.api.model;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLUniformData;

import java.util.Map;

public interface HYTGLProgram {

    int getProgram();

    void setProgram(int program);

    String getOutColor();

    GLUniformData[] getGlobalAttributes();

    void setGlobalAttributes(GLUniformData[] globalAttributes);

    void delete(GL4 gl4);

    void use(GL4 gl4, Map<HYTGLData, HYTCallback> resources);

}
