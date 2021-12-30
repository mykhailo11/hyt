package org.hyt.model;

import org.hyt.graphics.api.model.HYTGLData;

public class BaseHYTGLModel implements HYTGLModel{

    private float _state;

    private float[] _position;

    private HYTGLData _data;

    public BaseHYTGLModel(){

    }

    @Override
    public float getState() {
        return _state;
    }

    @Override
    public void setState(float state) {
        _state = state;
    }

    @Override
    public float[] getPosition() {
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
