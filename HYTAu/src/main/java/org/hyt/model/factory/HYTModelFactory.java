package org.hyt.model.factory;

import org.hyt.model.BaseHYTGLModel;
import org.hyt.model.HYTGLModel;
import org.hyt.model.HYTModel;

public class HYTModelFactory {

    public static HYTGLModel getHytGlModel(float multiplier){
        return new BaseHYTGLModel(multiplier);
    }

}
