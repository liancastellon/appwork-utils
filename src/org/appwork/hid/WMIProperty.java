package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public interface WMIProperty {
    public MayChangeOn[] getChangeOptions();

    public String name();
}