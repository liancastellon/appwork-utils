package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_NetworkAdapter
        implements WMIProperty {
    /** string */
    AdapterType,
    /** uint16 */
    AdapterTypeID,
    /** boolean */
    // AutoSense,
    /** uint16 */
    // Availability,
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
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
    /** string */
    // GUID,
    /** uint32 */
    // Index,
    /** datetime */
    // InstallDate,
    /** boolean */
    // Installed,
    /** uint32 */
    // InterfaceIndex,
    /** uint32 */
    // LastErrorCode,
    /** string */
    MACAddress,
    /** string */
    Manufacturer(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint32 */
    // MaxNumberControlled,
    /** uint64 */
    // MaxSpeed,
    /** string */
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    // NetConnectionID,
    /** uint16 */
    // NetConnectionStatus,
    /** boolean */
    // NetEnabled,
    /** string [] */
    // NetworkAddresses,
    /** string */
    // PermanentAddress,
    /** boolean */
    // PhysicalAdapter,
    /** string */
    PNPDeviceID,
    /** uint16 [] */
    // PowerManagementCapabilities,
    /** boolean */
    // PowerManagementSupported,
    /** string */
    ProductName(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    // ServiceName,
    /** uint64 */
    // Speed,
    /** string */
    // Status,
    /** uint16 */
    // StatusInfo,
    /** string */
    // SystemCreationClassName,
    /** string */
    // SystemName,
    /** datetime */
    // TimeOfLastReset,
    ;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_NetworkAdapter(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}