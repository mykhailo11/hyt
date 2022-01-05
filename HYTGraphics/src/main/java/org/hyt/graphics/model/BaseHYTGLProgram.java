package org.hyt.graphics.model;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLUniformData;
import org.hyt.graphics.api.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class BaseHYTGLProgram implements HYTGLProgram {

    private int _program;

    private final String _outColor;

    private GLUniformData[] _globalAttributes;

    public BaseHYTGLProgram(
            GL4 gl4,
            String outColor,
            String vertexShaderPath,
            String fragmentShaderPath
    ){
        _outColor = outColor;
        int vertexShader = gl4.glCreateShader(GL4.GL_VERTEX_SHADER);
        int fragmentShader = gl4.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        try {
            gl4.glShaderSource(
                    vertexShader,
                    1,
                    new String[]{Files.readString(Path.of(vertexShaderPath))},
                    null
            );
            gl4.glShaderSource(
                    fragmentShader,
                    1,
                    new String[]{Files.readString(Path.of(fragmentShaderPath))},
                    null
            );
            gl4.glCompileShader(vertexShader);
            gl4.glCompileShader(fragmentShader);

            _program = gl4.glCreateProgram();

            gl4.glAttachShader(_program, vertexShader);
            gl4.glAttachShader(_program, fragmentShader);

            gl4.glBindFragDataLocation(_program, 0, _outColor);

            gl4.glLinkProgram(_program);

            gl4.glDeleteShader(vertexShader);
            gl4.glDeleteShader(fragmentShader);


            System.out.println(printProgramInfoLog(gl4, _program));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String printProgramInfoLog(GL3 gl, int obj) {
        // get the GL info log
        final int logLen = getProgramParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
        if (logLen <= 0)
            return "";

        // Get the log
        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return logMessage;
    }

    /** Gets a program parameter value */
    public int getProgramParameter(GL3 gl, int obj, int paramName) {
        final int params[] = new int[1];
        gl.glGetProgramiv(obj, paramName, params, 0);
        return params[0];
    }

    @Override
    public int getProgram() {
        return _program;
    }

    @Override
    public void setProgram(int program) {
        _program = program;
    }

    @Override
    public String getOutColor() {
        return _outColor;
    }

    @Override
    public GLUniformData[] getGlobalAttributes() {
        return _globalAttributes;
    }

    @Override
    public void setGlobalAttributes(GLUniformData[] globalAttributes) {
        _globalAttributes = globalAttributes;
    }

    @Override
    public void delete(GL4 gl4) {
        gl4.glDeleteProgram(_program);
    }

    @Override
    public void use(GL4 gl4, Map<HYTGLData, HYTCallback> resources) {
        gl4.glUseProgram(_program);
        _setUniforms(gl4, _globalAttributes);
        for (HYTGLData data : resources.keySet()){
            _setUniforms(gl4, data.getStaticAttributes());
            gl4.glBindVertexArray(data.getData());
            try{
                resources.get(data).call();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    private void _setUniforms(GL4 gl4, GLUniformData[] uniforms){
        if (uniforms != null) {
            for (GLUniformData uniform : uniforms) {
                int location = gl4.glGetUniformLocation(_program, uniform.getName());
                uniform.setLocation(location);
                gl4.glUniform(uniform);
            }
        }
    }

}
