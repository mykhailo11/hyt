package org.hyt;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import javazoom.jl.player.Player;
import org.hyt.audio.BaseHYTAudioDevice;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.InputStream;

public class GlTest {

    private static class GlListener implements GLEventListener {

        private final float[] _currentState = new float[9];

        private final float[] _currentAngle = new float[9];

        private final float[] _goalState = new float[9];

        private final float[][] _colors = new float[9][3];

        private static final float _FRAME_DIFFERENCE = 1.0f;


        private final float[] x = new float[500];

        private float dif = 0.0f;

        private final float[] col = new float[]{1.0f, 0.0f, 0.0f};

        private int curColor = 0;

        public void setCurrentState(int index, float state) {
            if (state > 30.0f){
                _goalState[index] = state;
            }
            if (state > 200.0f){
                _goalState[4] = state;
                float corrector = (float) Math.random() * 0.7f;
                _colors[index][0] = corrector;
                _colors[index][1] = corrector;
                _colors[index][2] = corrector;
            }

        }

        @Override
        public void init(GLAutoDrawable drawable) {

            for (int index = 0; index < 9; index++) {
                _currentAngle[index] = 0.0f;
                _currentState[index] = 0.0f;
                _colors[index][0] = 1.0f;
                _colors[index][1] = 1.0f;
                _colors[index][2] = 1.0f;
            }


            float a = -250.0f;

            for (int index = 0; index < 500; index++, a = a + 1.0f) {
                x[index] = a;
            }

            GL2 gl2 = drawable.getGL().getGL2();

            gl2.glEnable(GL2.GL_DEPTH_TEST);
            gl2.glEnable(GL2.GL_BLEND);
            gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gl2.glEnable(GL2.GL_LINE_SMOOTH);
            gl2.glEnable(GL2.GL_POLYGON_SMOOTH);
            gl2.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_FASTEST);
            gl2.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_FASTEST);
            gl2.glEnable(GL2.GL_MULTISAMPLE);
            gl2.glDepthFunc(GL2.GL_LEQUAL);
            gl2.glLineWidth(2.0f);

        }

        @Override
        public void dispose(GLAutoDrawable drawable) {

        }

        private void d(GL2 gl2) {

            gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            gl2.glClearColor(0.05f, 0.05f, 0.1f, 1);

            gl2.glBegin(GL2.GL_POLYGON);

            gl2.glVertex3f(-20.0f, 20.0f, -20.0f);
            gl2.glVertex3f(-20.0f, -20.0f, -20.0f);
            gl2.glVertex3f(20.0f, 20.0f, -50.0f);
            gl2.glVertex3f(20.0f, -20.0f, -50.0f);
            gl2.glEnd();

        }

        @Override
        public void display(GLAutoDrawable drawable) {

            GL2 gl2 = drawable.getGL().getGL2();

            if (gl2.glGetError() == GL2.GL_NO_ERROR) {


                gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

                gl2.glClearColor(col[0] * 0.1f, col[1] * 0.1f, col[2] * 0.1f, 1);

                int prevColor = curColor - 1;

                if (curColor == 3) {
                    curColor = 0;
                }

                if (curColor == 0) {
                    prevColor = 2;
                }

                if (col[curColor] < 1.0f) {
                    col[curColor] += 0.005f;
                } else if (col[prevColor] > 0.0f) {
                    col[prevColor] -= 0.005f;
                } else {
                    curColor++;
                }

                gl2.glColor3f(1.0f, 1.0f, 1.0f);

                for (int index = 0; index < 9; index++) {
                    float currentState = _currentState[index];


                    if (currentState < _goalState[index] && _goalState[index] > 0.0f) {
                        _currentState[index] = currentState + 10.0f;
                    } else if (_goalState[index] > 5.0f) {
                        _goalState[index] = 0.0f;
                    } else if (currentState > 0.1f) {
                        _currentState[index] = currentState - 7.0f * (Math.abs(index - 4) / 15.0f + 0.2f);
                    }

                    if (_currentAngle[index] > 360.0f) {
                        _currentAngle[index] = _currentAngle[index] - 360.0f;
                    }

                    float currentRadians = (float) ((Math.PI * _currentAngle[index]) / 180.0f);
                    float globalX = (float) Math.cos(currentRadians);
                    float globalY = (float) Math.sin(currentRadians);

                    float red = col[0] + _colors[index][0];
                    float green = col[1] + _colors[index][1];
                    float blue = col[2] + _colors[index][2];

                    int mode = index % 2 == 0 ? GL2.GL_TRIANGLE_FAN : GL2.GL_LINE_LOOP;

                    if (currentState > 3.0f){

                        gl2.glColor4f(red, green * 0.95f, blue * 0.95f, 0.3f);

                        float scaling = (1.0f / (Math.abs((float)index / 2.0f - 1.5f) + 1.2f));
                        gl2.glLineWidth(3.0f);
                        gl2.glBegin(GL2.GL_POLYGON);
                        float a = ((float)index - 4.5f) * 60.0f;
                        gl2.glVertex3f(a + globalX, currentState, 0.0f);
                        gl2.glVertex3f(a-60.0f + globalX, 0.0f, 0.0f);
                        gl2.glVertex3f(a + globalX, -currentState, 0.0f);
                        gl2.glVertex3f(a+60.0f + globalX, 0.0f, 0.0f);
                        gl2.glEnd();
                        gl2.glLineWidth(2.0f);


                        gl2.glColor4f(red, green * 0.95f, blue * 0.95f, 0.6f);
                        gl2.glBegin(mode);
                        if (mode == GL2.GL_TRIANGLE_FAN){
                            gl2.glVertex3f(globalX * (index - 4) * currentState * 0.5f, globalY * (index - 4) * currentState * 0.4f, 0.0f);
                        }
                        for (int angle = 0; angle <= 360; angle = angle + 4) {

                            float radians = -(float) (Math.PI * angle) / 180.0f;

                            float x = currentState * 0.8f * scaling * (float) Math.cos(radians);
                            float y = currentState * 0.8f * scaling * (float) Math.sin(radians);

                            gl2.glVertex3f(x + globalX * (index - 4) * currentState * 0.5f, y + globalY * (index - 4) * currentState * 0.4f, 0.0f);
                        }

                        gl2.glEnd();

                    }

                    _currentAngle[index] = _currentAngle[index] + (index - 4.0f) / 4.5f * 2.0f;

                }

            }

        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            GL2 gl2 = drawable.getGL().getGL2();
            gl2.glMatrixMode(GL2.GL_PROJECTION);
            gl2.glLoadIdentity();
            float aspect = (float) height / (float) width;
            gl2.glOrtho(-400.0f, 400.0f, -400.0f * aspect, 400.0f * aspect, 10.0f, -10.0f);
            //gl2.glFrustum(-10.0f, 10.0f, -10.0f, 10.0f, 0.1, 400.0);
            gl2.glMatrixMode(GL2.GL_MODELVIEW);
        }
    }

    private static float _average(short[] nums) {
        float average = 0;
        for (short num : nums) {
            average = (average + num) / 2.0f;
        }
        return average;
    }

    private static void _doProcessing(GlListener listener) throws Exception {
        InputStream audioInput = new FileInputStream("C:/Users/mykhailo/HYT/jungle-keep-moving.mp3");
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioInput)) {
            AudioFormat audioFormat = audioStream.getFormat();
            Integer kbps = (Integer) audioFormat.getProperty("bitrate");

            BaseHYTAudioDevice device = new BaseHYTAudioDevice();
            device.add((samples) -> {
                listener.setCurrentState((int) (Math.random() * 9.0f), (float) (Math.pow(Math.abs(_average(samples)), 1.0f / 2.0f)) * 2.0f);
            });
            Player player = new Player(audioStream, device);
            player.play();
        }
    }

    //video
    public static void main(String[] args) {

        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        final GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(4);

        final GlListener listener = new GlListener();
        final GLJPanel panel = new GLJPanel(capabilities);
        panel.addGLEventListener(listener);
        panel.setSize(300, 400);
        final JFrame frame = new JFrame();
        frame.getContentPane().add(panel);
        frame.setSize(500, 500);
        frame.setVisible(true);

        final FPSAnimator animator = new FPSAnimator(panel, 80, true);
        animator.start();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (animator.isAnimating()) {
                    animator.stop();
                }
                if (panel.isShowing()) {
                    panel.disposeGLEventListener(listener, true);
                    panel.destroy();
                }
                frame.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                windowClosing(e);
            }
        });


        try {
            _doProcessing(listener);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }


}
