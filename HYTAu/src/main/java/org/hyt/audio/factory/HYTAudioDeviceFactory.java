package org.hyt.audio.factory;

import org.hyt.audio.BaseHYTAudioDevice;
import org.hyt.audio.HYTAudioDevice;

public class HYTAudioDeviceFactory {

    public static HYTAudioDevice getHytAudioDevice() throws Exception{
        return new BaseHYTAudioDevice();
    }

}
