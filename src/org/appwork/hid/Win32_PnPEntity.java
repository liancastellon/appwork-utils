package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_PnPEntity
        implements WMIProperty {
    /** uint16 */
    // Availability,
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    // ClassGuid,
    /** string [] */
    // CompatibleID,
    /** uint32 */
    // ConfigManagerErrorCode,
    /** boolean */
    // ConfigManagerUserConfig,
    /** string */
    // CreationClassName,
    /** string */
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    DeviceID,
    /** boolean */
    // ErrorCleared,
    /** string */
    // ErrorDescription,
    /** string [] */
    // HardwareID,
    /** datetime */
    // InstallDate,
    /** uint32 */
    // LastErrorCode,
    /** string */
    Manufacturer(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    // PNPClass,
    /** string */
    PNPDeviceID,
    /** uint16 [] */
    // PowerManagementCapabilities,
    /** boolean */
    // PowerManagementSupported,
    /** boolean */
    // Present,
    /** string */
    // Service,
    /** string */
    // Status,
    /** uint16 */
    // StatusInfo,
    /** string */
    // SystemCreationClassName,
    /** string */
    // SystemName,
    ;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_PnPEntity(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}