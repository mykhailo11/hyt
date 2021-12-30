package org.hyt.graphics.model;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLUniformData;
import org.hyt.graphics.api.model.HYTGLAttribute;
import org.hyt.graphics.api.model.HYTGLBuffer;
import org.hyt.graphics.api.model.HYTGLData;
import org.hyt.graphics.api.model.HYTGLProgram;

import java.nio.IntBuffer;

public class BaseHYTGLData implements HYTGLData {

    private int _data;

    private HYTGLBuffer _buffer;

    private HYTGLAttribute[] _attributes;

    private GLUniformData[] _staticAttributes;

    public BaseHYTGLData(GL4 gl4){
        IntBuffer id = IntBuffer.allocate(1);
        gl4.glGenVertexArrays(1, id);
        _data = id.get();
    }

    @Override
    public int getData() {
        return _data;
    }

    @Override
    public void setData(int data) {
        _data = data;
    }

    @Override
    public HYTGLBuffer getBuffer() {
        return _buffer;
    }

    @Override
    public HYTGLBuffer setBuffer(GL4 gl4, HYTGLProgram program, HYTGLBuffer buffer) {
        HYTGLBuffer initial = _buffer;
        _buffer = buffer;
        gl4.glBindVertexArray(_data);
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, _buffer.getBuffer());
        if (_attributes != null){
            int offset = 0;
            for (HYTGLAttribute attribute : _attributes){
                int location = gl4.glGetAttribLocation(program.getProgram(), attribute.getName());
                int chunk = attribute.getChunk();
                gl4.glEnableVertexAttribArray(location);
                gl4.glVertexAttribPointer(
                        location,
                        chunk,
                        GL4.GL_FLOAT,
                        false,
                        0,
                        offset
                );
                offset += chunk * attribute.getChunks() * Float.BYTES;
            }
        }
        return initial;
    }

    @Override
    public HYTGLAttribute[] getAttributes() {
        return _attributes;
    }

    @Override
    public void setAttributes(HYTGLAttribute[] attributes) {
        _attributes = attributes;
    }

    @Override
    public GLUniformData[] getStaticAttributes() {
        return _staticAttributes;
    }

    @Override
    public void setStaticAttributes(GLUniformData[] staticAttributes) {
        _staticAttributes = staticAttributes;
    }

    @Override
    public HYTGLBuffer delete(GL4 gl4) {
        IntBuffer id = IntBuffer.allocate(1);
        id.put(0, _data);
        gl4.glDeleteVertexArrays(1, id);
        return _buffer;
    }

}
