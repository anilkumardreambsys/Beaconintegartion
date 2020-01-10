package com.beac.beaconintegartion;

import com.ufobeaconsdk.main.UFODevice;

public enum SharedUFODevice {
    INSTANCE;

    UFODevice ufodevice;

    public UFODevice getUfodevice() {
        return ufodevice;
    }

    public void setUfodevice(UFODevice ufodevice) {
        this.ufodevice = ufodevice;
    }
}
