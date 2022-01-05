package org.hyt.model;

import org.hyt.graphics.api.model.HYTGLData;

public class BaseHYTGLModel implements HYTGLModel{

    public static float SPEED = 0.002f;

    private final float _direction = 1.0f;

    private final float _multiplier;

    private float _state;

    private float _goal;

    private float[] _position;

    private HYTGLData _data;

    public BaseHYTGLModel(float multiplier){
        _multiplier = multiplier;
    }

    @Override
    public float getState() {
        float initial = _state;
        if (_goal > 0.0f && _state < _goal){
            _state += SPEED * 50.0f;
        }else {
            _goal = -0.1f;
        }
        if (_state > SPEED * 5.0f && _goal <= 0.0f){
            _state -= SPEED * 20.0f;
        }
        return initial;
    }

    @Override
    public void setState(float state) {
        if (state >= _goal){
            _goal = state > 1.0f ? 1.0f : state + 0.001f;
        }
    }

    @Override
    public float[] getPosition() {
        float y = _position[1];
        if (y > 1.1f){
            _position[1] = -_direction * 1.1f;
        }
        _position[1] += SPEED * _direction;
        return _position;
    }

    @Override
    public void setPosition(float[] position) {
        _position = position;
    }

    @Override
    public HYTGLData getData() {
        return _data;
    }

    @Override
    public void setData(HYTGLData data) {
        _data = data;
    }
}
