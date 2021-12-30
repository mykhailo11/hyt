package org.hyt;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLUniformData;
import org.hyt.graphics.api.model.*;
import org.hyt.graphics.factory.HYTGLFactory;
import org.hyt.model.HYTGLModel;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class HYTListener implements GLEventListener {

    private int _width;

    private int _height;

    private final HYTGLModel[] _models;

    private HYTGLProgram _program;

    private int _texture;

    private int _fbo;

    private int _rbo;

    public static String OUT_COLOR = "color";

    public static float[] VERTICES = new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    private final String _vertexShaderPath;

    private final String _fragmentShaderPath;


    public HYTListener(
            HYTGLModel[] models,
            String vertexShaderPath,
            String fragmentShaderPath,
            int height,
            int width
    ) {
        _models = models;
        _vertexShaderPath = vertexShaderPath;
        _fragmentShaderPath = fragmentShaderPath;
        _height = height;
        _width = width;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl4 = drawable
                .getGL()
                .getGL4();

        gl4.glEnable(GL4.GL_MULTISAMPLE);
        gl4.glEnable(GL4.GL_DEPTH_TEST);
        gl4.glDepthFunc(GL4.GL_LEQUAL);
        gl4.glEnable(GL4.GL_BLEND);
        gl4.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);

        _program = HYTGLFactory.getProgram(
                gl4,
                OUT_COLOR,
                _vertexShaderPath,
                _fragmentShaderPath
        );
        _program.setGlobalAttributes(
                new GLUniformData[]{
                        new GLUniformData(
                                "window",
                                2,
                                Buffers.newDirectFloatBuffer(new float[]{_width, _height})
                        )
                }
        );

        for (HYTGLModel model : _models) {
            HYTGLBuffer buffer = HYTGLFactory.getBuffer(gl4);
            buffer.setData(gl4, VERTICES);
            HYTGLData data = HYTGLFactory.getData(gl4);
            data.setStaticAttributes(
                    new GLUniformData[]{
                            new GLUniformData("state", model.getState()),
                            new GLUniformData("position", 4, FloatBuffer.wrap(model.getPosition()))
                    }
            );
            data.setAttributes(
                    new HYTGLAttribute[]{
                            HYTGLFactory.getAttribute("current", 1, VERTICES.length)
                    }
            );
            data.setBuffer(gl4, _program, buffer);
            model.setData(data);
        }

        int[] id = new int[1];
        gl4.glGenFramebuffers(1, id, 0);
        _fbo = id[0];
        gl4.glGenRenderbuffers(1, id, 0);
        _rbo = id[0];

        gl4.glBindFramebuffer(GL4.GL_FRAMEBUFFER, _fbo);
        _configureRenderBufferMsaa(gl4);
        gl4.glFramebufferRenderbuffer(
                GL4.GL_FRAMEBUFFER,
                GL4.GL_COLOR_ATTACHMENT0,
                GL4.GL_RENDERBUFFER,
                _rbo
        );
        gl4.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
    }

    private void _configureRenderBufferMsaa(GL4 gl4){
        gl4.glBindRenderbuffer(GL4.GL_RENDERBUFFER, _rbo);
        gl4.glRenderbufferStorageMultisample(
                GL4.GL_RENDERBUFFER,
                16,
                GL4.GL_RGB,
                _width,
                _height
        );
        gl4.glBindRenderbuffer(GL4.GL_RENDERBUFFER, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl4 = drawable
                .getGL()
                .getGL4();
        for (HYTGLModel model : _models) {
            HYTGLData data = model.getData();
            HYTGLBuffer buffer = data.delete(gl4);
            buffer.delete(gl4);
        }
        _program.delete(gl4);
        gl4.glDeleteRenderbuffers(1, new int[]{_rbo}, 0);
        gl4.glDeleteFramebuffers(1, new int[]{_fbo}, 0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl4 = drawable
                .getGL()
                .getGL4();
        gl4.glBindFramebuffer(GL4.GL_FRAMEBUFFER, _fbo);
        gl4.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl4.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        Map<HYTGLData, HYTCallback> resources = new HashMap<>();
        for (HYTGLModel model : _models) {
            HYTGLData data = model.getData();
            GLUniformData[] staticAttributes = data.getStaticAttributes();
            staticAttributes[0].setData(model.getState());
            staticAttributes[1].setData(Buffers.newDirectFloatBuffer(model.getPosition()));
            resources.put(
                    data,
                    () -> gl4.glDrawArrays(
                            GL4.GL_TRIANGLE_STRIP,
                            0,
                            VERTICES.length
                    )
            );
        }
        _program.use(gl4, resources);
        gl4.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
        gl4.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        gl4.glBindFramebuffer(GL4.GL_READ_FRAMEBUFFER, _fbo);
        gl4.glBindFramebuffer(GL4.GL_DRAW_FRAMEBUFFER, 0);
        gl4.glBlitFramebuffer(
                0,
                0,
                _width,
                _height,
                0,
                0,
                _width,
                _height,
                GL4.GL_COLOR_BUFFER_BIT,
                GL4.GL_NEAREST
        );
        gl4.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
        int error = gl4.glGetError();
        if (error != GL4.GL_NO_ERROR) {
            System.err.println("ERROR on render : " + error);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL4 gl4 = drawable
                .getGL()
                .getGL4();
        _width = width;
        _height = height;
        GLUniformData data = _program.getGlobalAttributes()[0];
        if (data != null) {
            data.setData(Buffers.newDirectFloatBuffer(new float[]{_width, _height}));
        }
        _configureRenderBufferMsaa(gl4);
    }

}
