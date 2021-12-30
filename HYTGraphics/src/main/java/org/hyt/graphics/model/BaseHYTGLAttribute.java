package org.hyt.graphics.model;

import org.hyt.graphics.api.model.HYTGLAttribute;

public class BaseHYTGLAttribute implements HYTGLAttribute {

    private String _name;

    private int _chunk;

    private int _chunks;

    public BaseHYTGLAttribute(String name, int chunk, int chunks){
        _name = name;
        _chunk = chunk;
        _chunks = chunks;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setName(String name) {
        _name = name;
    }

    @Override
    public int getChunk() {
        return _chunk;
    }

    @Override
    public void setChunk(int chunk) {
        _chunk = chunk;
    }

    @Override
    public int getChunks() {
        return _chunks;
    }

    @Override
    public void setChunks(int chunks) {
        _chunks = chunks;
    }
}
