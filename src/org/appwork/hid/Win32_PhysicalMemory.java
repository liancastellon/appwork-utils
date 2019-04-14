package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_PhysicalMemory
        implements WMIProperty {
    /** uint32 */
    // Attributes,
    /** string */
    BankLabel,
    /** uint64 */
    Capacity,
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint32 */
    // ConfiguredClockSpeed,
    /** uint32 */
    // ConfiguredVoltage,
    /** string */
    // CreationClassName,
    /** uint16 */
    // DataWidth,
    /** string */
    // Description,
    /** string */
    DeviceLocator,
    /** uint16 */
    FormFactor,
    /** boolean */
    HotSwappable,
    /** datetime */
    // InstallDate,
    /** uint16 */
    // InterleaveDataDepth,
    /** uint32 */
    // InterleavePosition,
    /** string */
    Manufacturer,
    /** uint32 */
    // MaxVoltage,
    /** uint16 */
    // MemoryType,
    /** uint32 */
    // MinVoltage,
    /** string */
    Model,
    /** string */
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    OtherIdentifyingInfo,
    /** string */
    PartNumber,
    /** uint32 */
    // PositionInRow,
    /** boolean */
    // PoweredOn,
    /** boolean */
    Removable,
    /** boolean */
    Replaceable,
    /** string */
    SerialNumber,
    /** string */
    SKU,
    /** uint32 */
    // SMBIOSMemoryType,
    /** uint32 */
    // Speed,
    /** string */
    // Status,
    /** string */
    Tag,
    /** uint16 */
    // TotalWidth,
    /** uint16 */
    TypeDetail,
    /** string */
    Version;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_PhysicalMemory(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}