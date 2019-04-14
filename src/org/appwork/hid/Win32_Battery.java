package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_Battery
        implements WMIProperty {
    // Availability,
    // BatteryRechargeTime,
    // BatteryStatus,
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    Chemistry,
    // ConfigManagerErrorCode,
    // ConfigManagerUserConfig,
    // CreationClassName,
    // Description,
    // DesignCapacity,
    // DesignVoltage,
    DeviceID,
    // ErrorCleared,
    // ErrorDescription,
    // EstimatedChargeRemaining,
    // EstimatedRunTime,
    // ExpectedBatteryLife,
    // ExpectedLife,
    // FullChargeCapacity,
    // InstallDate,
    // LastErrorCode,
    // MaxRechargeTime,
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    PNPDeviceID,
    // PowerManagementCapabilities,
    // PowerManagementSupported,
    // SmartBatteryVersion,
    // Status,
    // StatusInfo,
    // SystemCreationClassName,
    // SystemName,
    // TimeOnBattery,
    // TimeToFullCharge;
    ;
    public final MayChangeOn[] changeOptions;

    /**
     * @return the changeOptions
     */
    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_Battery(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}