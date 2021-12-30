package org.hyt;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.nio.Buffer;
import java.nio.FloatBuffer;

public class Listener implements GLEventListener {

    protected GL3 _getGl3(GLAutoDrawable drawable){
        return drawable.getGL().getGL3();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl3 = _getGl3(drawable);
        gl3.glEnable(GL3.GL_DEPTH_TEST);
        gl3.glDepthFunc(GL3.GL_LEQUAL);
        gl3.glEnable(GL3.GL_BLEND);
        gl3.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        gl3.glEnable(GL3.GL_MULTISAMPLE);

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl3 = _getGl3(drawable);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl3 = _getGl3(drawable);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl3 = _getGl3(drawable);
    }

}
