package org.hyt.audio;

import javazoom.jl.player.AudioDevice;

import java.util.function.Consumer;

public interface HYTAudioDevice extends AudioDevice {

    boolean add(Consumer<short[]> consumer);

}
