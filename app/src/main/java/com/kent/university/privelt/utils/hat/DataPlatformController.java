package com.kent.university.privelt.utils.hat;

import com.privelt.pda.dataplatform.generic.DataPlatform;

public class DataPlatformController<P extends DataPlatform> {

    protected P platform;

    public DataPlatformController() {
    }

    public DataPlatformController(P platform) {
        this.platform = platform;
    }

    public P getPlatform() {
        return platform;
    }
}
