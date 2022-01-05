package org.hyt;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import javazoom.jl.player.Player;
import org.hyt.audio.BaseHYTAudioDevice;
import org.hyt.audio.HYTAudioDevice;
import org.hyt.audio.factory.HYTAudioDeviceFactory;
import org.hyt.graphics.api.model.HYTGLAttribute;
import org.hyt.graphics.api.model.HYTGLBuffer;
import org.hyt.graphics.api.model.HYTGLData;
import org.hyt.graphics.api.model.HYTGLProgram;
import org.hyt.graphics.factory.HYTGLFactory;
import org.hyt.model.HYTGLModel;
import org.hyt.model.factory.HYTModelFactory;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class Main {

   /* private static class HYTListener implements GLEventListener {

        HYTGLBuffer _buffer;

        HYTGLData _data;

        HYTGLProgram _program;

        float[] _points;


        public HYTListener() {
            _points = new float[]{
                    -1.0f, -1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f
            };
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            GL4 gl4 = drawable
                    .getGL()
                    .getGL4();

            gl4.glEnable(GL4.GL_DEPTH_TEST);
            gl4.glDepthFunc(GL4.GL_LEQUAL);
            gl4.glEnable(GL4.GL_BLEND);
            gl4.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
            gl4.glEnable(GL4.GL_MULTISAMPLE);
            gl4.glEnable(GL4.GL_POLYGON_SMOOTH);
            gl4.glEnable(GL4.GL_LINE_SMOOTH);
            gl4.glHint(GL4.GL_POLYGON_SMOOTH_HINT, GL4.GL_NICEST);
            gl4.glHint(GL4.GL_LINE_SMOOTH_HINT, GL4.GL_NICEST);

            _buffer = HYTGLFactory.getBuffer(gl4);
            _buffer.setData(gl4, _points);

            _program = HYTGLFactory.getProgram(
                    gl4,
                    "color",
                    "C:\\Users\\mykhailo\\HYT\\HYTAu\\src\\main\\resources\\vsh.glsl",
                    "C:\\Users\\mykhailo\\HYT\\HYTAu\\src\\main\\resources\\fsh.glsl"
            );

            _data = HYTGLFactory.getData(gl4);
            _data.setAttributes(
                    new HYTGLAttribute[]{
                            HYTGLFactory.getAttribute("inPosition", 4, 3)
                    }
            );
            _data.setBuffer(
                    gl4,
                    _program,
                    _buffer
            );
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            GL4 gl4 = drawable
                    .getGL()
                    .getGL4();
            HYTGLBuffer buffer = _data.delete(
                    gl4
            );
            buffer.delete(gl4);
            _program.delete(gl4);
        }

        @Override
        public void display(GLAutoDrawable drawable) {
            GL4 gl4 = drawable
                    .getGL()
                    .getGL4();
            gl4.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
            gl4.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
            _program.use(gl4, Map.of(
                    _data, () -> gl4.glDrawArrays(
                            GL4.GL_TRIANGLES,
                            0,
                            3
                    )
            ));
            int error = gl4.glGetError();
            if (error != GL4.GL_NO_ERROR) {
                System.err.println("ERROR on render : " + error);
            }
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        }

    }*/

    private static float _average(short[] nums) {
        float average = 0;
        for (short num : nums) {
            average = (average + num) / 2.0f;
        }
        return average;
    }

    public static void main(String[] args) {

        HYTGLModel[] models = new HYTGLModel[45];

        int index = 0;

        for (int column = -2; column < 3; column++) {
            float multiplier = 1.0f + (float)Math.random() * 2.0f;
            for (int row = -4; row < 5 && index < 45; row++, index++) {
                HYTGLModel model = HYTModelFactory.getHytGlModel(multiplier);
                model.setPosition(new float[]{(column) / 3.0f, row / 4.0f, 0.0f, 0.0f});
                models[index] = model;
            }
        }
        HYTListener listener = new HYTListener(
                models,
                "C:\\Users\\mykhailo\\HYT\\HYTAu\\src\\main\\resources\\vsh.glsl",
                "C:\\Users\\mykhailo\\HYT\\HYTAu\\src\\main\\resources\\fsh.glsl",
                400,
                400
        );
        GLProfile glProfile = GLProfile.get(GLProfile.GL4);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        glCapabilities.setBackgroundOpaque(true);
        JFrame frame = new JFrame();
        GLCanvas canvas = new GLCanvas(glCapabilities);
        FPSAnimator animator = new FPSAnimator(canvas, 30, true);
        canvas.addGLEventListener(listener);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(canvas);
        frame.setSize(400, 400);
        frame.setVisible(true);
        animator.start();
        try {
            InputStream audioInput = new FileInputStream("C:\\Users\\mykhailo\\HYT\\kaskade-46.mp3");
            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioInput)) {
                AudioFormat audioFormat = audioStream.getFormat();
                HYTAudioDevice device = HYTAudioDeviceFactory.getHytAudioDevice();
                device.add(
                        (samples) -> {
                            float result = Math.abs(_average(samples) - 10000) / 30000.0f + 0.1f;
                            int count = (int)(result / 0.05f);
                            for (int c = 0; c < count; c++){
                                int chosen = (int)(Math.random() * 45);
                                models[chosen].setState(result);
                            }
                        }
                );
                Player player = new Player(audioStream, device);
                player.play();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
