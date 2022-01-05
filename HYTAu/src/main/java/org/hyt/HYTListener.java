package org.hyt;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
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

    private int _fbo;

    private int _rbo;

    private int _rboDepth;

    private int _index;

    public static String OUT_COLOR = "color";

    public static int[] VERTICES = new int[]{
            3, 2, 0,
            3, 0, 1,
            2, 4, 0,
            2, 6, 4,
            3, 5, 1,
            3, 7, 5,
            5, 0, 1,
            5, 4, 0,
            7, 4, 5,
            7, 6, 4
    };

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
            HYTGLData data = HYTGLFactory.getData(gl4);
            data.setStaticAttributes(
                    new GLUniformData[]{
                            new GLUniformData("state", model.getState()),
                            new GLUniformData("position", 4, FloatBuffer.wrap(model.getPosition()))
                    }
            );
            model.setData(data);
        }

        int[] id = new int[2];
        gl4.glGenFramebuffers(1, id, 0);
        _fbo = id[0];
        gl4.glGenRenderbuffers(2, id, 0);
        _rbo = id[0];
        _rboDepth = id[1];
        gl4.glGenBuffers(1, id, 0);
        _index = id[0];

        gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, _index);
        gl4.glBufferData(
                GL4.GL_ELEMENT_ARRAY_BUFFER,
                (long)VERTICES.length * Integer.BYTES,
                Buffers.newDirectIntBuffer(VERTICES),
                GL4.GL_DYNAMIC_DRAW
        );

        gl4.glBindFramebuffer(GL4.GL_FRAMEBUFFER, _fbo);
        gl4.glEnable(GL4.GL_MULTISAMPLE);
        gl4.glEnable(GL4.GL_DEPTH_TEST);
        gl4.glDepthFunc(GL4.GL_LEQUAL);
        gl4.glEnable(GL4.GL_BLEND);
        gl4.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        _configureRenderBufferMsaa(gl4);
        _configureRenderBufferDepth(gl4);
        gl4.glFramebufferRenderbuffer(
                GL4.GL_FRAMEBUFFER,
                GL4.GL_COLOR_ATTACHMENT0,
                GL4.GL_RENDERBUFFER,
                _rbo
        );
        gl4.glFramebufferRenderbuffer(
                GL4.GL_FRAMEBUFFER,
                GL4.GL_DEPTH_STENCIL_ATTACHMENT,
                GL4.GL_RENDERBUFFER,
                _rboDepth
        );
        gl4.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
    }

    private void _configureRenderBufferDepth(GL4 gl4){
        gl4.glBindRenderbuffer(GL4.GL_RENDERBUFFER, _rboDepth);
        gl4.glRenderbufferStorageMultisample(
                GL4.GL_RENDERBUFFER,
                8,
                GL4.GL_DEPTH24_STENCIL8,
                _width,
                _height
        );
        gl4.glBindRenderbuffer(GL4.GL_RENDERBUFFER, 0);
    }

    private void _configureRenderBufferMsaa(GL4 gl4) {
        gl4.glBindRenderbuffer(GL4.GL_RENDERBUFFER, _rbo);
        gl4.glRenderbufferStorageMultisample(
                GL4.GL_RENDERBUFFER,
                8,
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
            if (buffer != null){
                buffer.delete(gl4);
            }
        }
        _program.delete(gl4);
        gl4.glDeleteBuffers(1, new int[]{_index}, 0);
        gl4.glDeleteRenderbuffers(2, new int[]{_rbo, _rboDepth}, 0);
        gl4.glDeleteFramebuffers(1, new int[]{_fbo}, 0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl4 = drawable
                .getGL()
                .getGL4();
        gl4.glBindFramebuffer(GL4.GL_FRAMEBUFFER, _fbo);
        gl4.glClearColor(0.01f, 0.01f, 0.01f, 1.0f);
        gl4.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        Map<HYTGLData, HYTCallback> resources = new HashMap<>();
        for (HYTGLModel model : _models) {
            HYTGLData data = model.getData();
            GLUniformData[] staticAttributes = data.getStaticAttributes();
            staticAttributes[0].setData(model.getState());
            staticAttributes[1].setData(Buffers.newDirectFloatBuffer(model.getPosition()));
            resources.put(
                    data,
                    () -> {
                        gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, _index);
                        gl4.glDrawElements(
                                GL4.GL_TRIANGLES,
                                VERTICES.length,
                                GL4.GL_UNSIGNED_INT,
                                0
                        );
                    }
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
                GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT,
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
        gl4.glViewport(x, y, width, height);
        _width = width;
        _height = height;
        GLUniformData data = _program.getGlobalAttributes()[0];
        if (data != null) {
            data.setData(Buffers.newDirectFloatBuffer(new float[]{_width, _height}));
        }
        _configureRenderBufferDepth(gl4);
        _configureRenderBufferMsaa(gl4);
    }

}
