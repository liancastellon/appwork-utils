package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_videocontroller
        implements WMIProperty {
    /** uint16 [] */
    // AcceleratorCapabilities,
    /** string */
    AdapterCompatibility,
    /** string */
    AdapterDACType,
    /** uint32 */
    AdapterRAM,
    /** uint16 */
    // Availability,
    /** string [] */
    // CapabilityDescriptions,
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint32 */
    // ColorTableEntries,
    /** uint32 */
    // ConfigManagerErrorCode,
    /** boolean */
    // ConfigManagerUserConfig,
    /** string */
    // CreationClassName,
    /** uint32 */
    // CurrentBitsPerPixel,
    /** uint32 */
    // CurrentHorizontalResolution,
    /** uint64 */
    // CurrentNumberOfColors,
    /** uint32 */
    // CurrentNumberOfColumns,
    /** uint32 */
    // CurrentNumberOfRows,
    /** uint32 */
    // CurrentRefreshRate,
    /** uint16 */
    // CurrentScanMode,
    /** uint32 */
    // CurrentVerticalResolution,
    /** string */
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    DeviceID,
    /** uint32 */
    // DeviceSpecificPens,
    /** uint32 */
    // DitherType,
    /** datetime */
    // DriverDate,
    /** string */
    // DriverVersion,
    /** boolean */
    // ErrorCleared,
    /** string */
    // ErrorDescription,
    /** uint32 */
    // ICMIntent,
    /** uint32 */
    // ICMMethod,
    /** string */
    // InfFilename,
    /** string */
    // InfSection,
    /** datetime */
    // InstallDate,
    /** string */
    // InstalledDisplayDrivers,
    /** uint32 */
    // LastErrorCode,
    /** uint32 */
    // MaxMemorySupported,
    /** uint32 */
    // MaxNumberControlled,
    /** uint32 */
    // MaxRefreshRate,
    /** uint32 */
    // MinRefreshRate,
    /** boolean */
    // Monochrome,
    /** string */
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint16 */
    // NumberOfColorPlanes,
    /** uint32 */
    // NumberOfVideoPages,
    /** string */
    PNPDeviceID,
    /** uint16 [] */
    // PowerManagementCapabilities,
    /** boolean */
    // PowerManagementSupported,
    /** uint16 */
    // ProtocolSupported,
    /** uint32 */
    // ReservedSystemPaletteEntries,
    /** uint32 */
    // SpecificationVersion,
    /** string */
    // Status,
    /** uint16 */
    // StatusInfo,
    /** string */
    // SystemCreationClassName,
    /** string */
    // SystemName,
    /** uint32 */
    // SystemPaletteEntries,
    /** datetime */
    // TimeOfLastReset,
    /** uint16 */
    // VideoArchitecture,
    /** uint16 */
    // VideoMemoryType,
    /** uint16 */
    // VideoMode,
    /** string */
    // VideoModeDescription,
    /** string */
    VideoProcessor;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_videocontroller(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}