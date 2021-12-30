package org.hyt.graphics.factory;

import com.jogamp.opengl.GL4;
import org.hyt.graphics.api.model.HYTGLAttribute;
import org.hyt.graphics.api.model.HYTGLBuffer;
import org.hyt.graphics.api.model.HYTGLData;
import org.hyt.graphics.api.model.HYTGLProgram;
import org.hyt.graphics.model.BaseHYTGLAttribute;
import org.hyt.graphics.model.BaseHYTGLBuffer;
import org.hyt.graphics.model.BaseHYTGLData;
import org.hyt.graphics.model.BaseHYTGLProgram;

public class HYTGLFactory {

    public static HYTGLAttribute getAttribute(
            String name,
            int chunk,
            int chunks
    ){
        return new BaseHYTGLAttribute(name, chunk, chunks);
    }

    public static HYTGLBuffer getBuffer(GL4 gl4){
        return new BaseHYTGLBuffer(gl4);
    }

    public static HYTGLData getData(GL4 gl4){
        return new BaseHYTGLData(gl4);
    }

    public static HYTGLProgram getProgram(
            GL4 gl4,
            String outColor,
            String vertexShaderPath,
            String fragmentShaderPath
    ){
        return new BaseHYTGLProgram(gl4, outColor, vertexShaderPath, fragmentShaderPath);
    }

}
