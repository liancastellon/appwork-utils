package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_SystemEnclosure
        implements WMIProperty {
    /** boolean */
    // AudibleAlarm,
    /** string */
    // BreachDescription,
    /** string */
    // CableManagementStrategy,
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint16 [] */
    // TODO: maybe wmic?
    ChassisTypes,
    /** string */
    // CreationClassName,
    /** sint16 */
    // CurrentRequiredOrProduced,
    /** real32 */
    // Depth,
    /** string */
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint16 */
    // HeatGeneration,
    /** real32 */
    // Height,
    /** boolean */
    HotSwappable,
    /** datetime */
    // InstallDate,
    /** boolean */
    LockPresent,
    /** string */
    Manufacturer(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    Model,
    /** string */
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint16 */
    NumberOfPowerCords,
    /** string */
    OtherIdentifyingInfo,
    /** string */
    PartNumber,
    /** boolean */
    // PoweredOn,
    /** boolean */
    Removable,
    /** boolean */
    Replaceable,
    /** uint16 */
    // SecurityBreach,
    /** uint16 */
    // SecurityStatus,
    /** string */
    SerialNumber,
    /** string [] */
    // ServiceDescriptions,
    /** uint16 [] */
    // ServicePhilosophy,
    /** string */
    SKU,
    /** string */
    SMBIOSAssetTag,
    /** string */
    // Status,
    /** string */
    Tag,
    /** string [] */
    // TypeDescriptions,
    /** string */
    Version,
    /** boolean */
    // VisibleAlarm,
    /** real32 */
    // Weight,
    /** real32 */
    // Width,

    ;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_SystemEnclosure(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}