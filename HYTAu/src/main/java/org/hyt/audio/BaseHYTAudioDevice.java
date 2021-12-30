package org.hyt.audio;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BaseHYTAudioDevice implements HYTAudioDevice {

    private final AudioDevice _device;

    private final List<Consumer<short[]>> _samplesConsumers;

    public BaseHYTAudioDevice() throws Exception{
        _samplesConsumers = new ArrayList<>();
        FactoryRegistry registry = FactoryRegistry.systemRegistry();
        _device = registry.createAudioDevice();
    }

    @Override

    public boolean add(Consumer<short[]> consumer){
        return _samplesConsumers.add(consumer);
    }

    @Override
    public void open(Decoder decoder) throws JavaLayerException {
        _device.open(decoder);
    }

    @Override
    public boolean isOpen() {
        return _device.isOpen();
    }

    @Override
    public void write(short[] samples, int offs, int len) throws JavaLayerException {
        _device.write(samples, offs, len);
        for (Consumer<short[]> consumer : _samplesConsumers){
            consumer.accept(samples);
        }
    }

    @Override
    public void close() {
        _samplesConsumers.clear();
        _device.close();
    }

    @Override
    public void flush() {
        _device.flush();
    }

    @Override
    public int getPosition() {
        return _device.getPosition();
    }
}
