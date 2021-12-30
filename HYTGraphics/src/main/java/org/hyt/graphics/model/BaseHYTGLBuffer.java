package org.hyt.graphics.model;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import org.hyt.graphics.api.model.HYTGLBuffer;

import java.nio.IntBuffer;

public class BaseHYTGLBuffer implements HYTGLBuffer {

    private int _buffer;

    public BaseHYTGLBuffer(GL4 gl4){
        IntBuffer id = IntBuffer.allocate(1);
        gl4.glGenBuffers(1, id);
        _buffer = id.get();
    }

    @Override
    public int getBuffer() {
        return _buffer;
    }

    @Override
    public void setBuffer(int buffer) {
        _buffer =  buffer;
    }

    @Override
    public void setData(GL4 gl4, float[] data) {
        int size = data.length * Float.BYTES;
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, _buffer);
        gl4.glBufferData(
                GL4.GL_ARRAY_BUFFER,
                (long) size,
                Buffers.newDirectFloatBuffer(data),
                GL4.GL_DYNAMIC_DRAW
        );
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void delete(GL4 gl4) {
        IntBuffer id = IntBuffer.allocate(1);
        id.put(0, _buffer);
        gl4.glDeleteBuffers(1, id);
    }
}
