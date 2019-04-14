package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_Processor
        implements WMIProperty {
    /** uint16 */
    // AddressWidth,
    /** uint16 */
    Architecture,
    /** string */
    // AssetTag,
    /** uint16 */
    // Availability,
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint32 */
    // Characteristics,
    /** uint32 */
    // ConfigManagerErrorCode,
    /** boolean */
    // ConfigManagerUserConfig,
    /** uint16 */
    // CpuStatus,
    /** string */
    // CreationClassName,
    /** uint32 */
    // CurrentClockSpeed,
    /** uint16 */
    // CurrentVoltage,
    /** uint16 */
    // DataWidth,
    /** string */
    // Description,
    /** string */
    DeviceID,
    /** boolean */
    // ErrorCleared,
    /** string */
    // ErrorDescription,
    /** uint32 */
    // ExtClock,
    /** uint16 */
    Family,
    /** datetime */
    // InstallDate,
    /** uint32 */
    // L2CacheSize,
    /** uint32 */
    // L2CacheSpeed,
    /** uint32 */
    // L3CacheSize,
    /** uint32 */
    // L3CacheSpeed,
    /** uint32 */
    // LastErrorCode,
    /** uint16 */
    // Level,
    /** uint16 */
    // LoadPercentage,
    /** string */
    Manufacturer,
    /** uint32 */
    // MaxClockSpeed,
    /** string */
    Name,
    /** uint32 */
    // NumberOfCores,
    /** uint32 */
    // NumberOfEnabledCore,
    /** uint32 */
    NumberOfLogicalProcessors,
    /** string */
    // OtherFamilyDescription,
    /**
     * string This value comes from the Part Number member of the Processor Information structure in the SMBIOS information.
     *
     * Windows Server 2012 R2, Windows 8.1, Windows Server 2012, Windows 8, Windows Server 2008 R2, Windows 7, Windows Server 2008 and
     * Windows Vista: This property is not supported before Windows Server 2016 and Windows 10.
     */
    PartNumber,
    /** string */
    PNPDeviceID,
    /** uint16 [] */
    // PowerManagementCapabilities,
    /** boolean */
    // PowerManagementSupported,
    /**
     * string Processor information that describes the processor features. For an x86 class CPU, the field format depends on the
     * processor support of the CPUID instruction. If the instruction is supported, the property contains 2 (two) DWORD formatted
     * values. The first is an offset of 08h-0Bh, which is the EAX value that a CPUID instruction returns with input EAX set to 1. The
     * second is an offset of 0Ch-0Fh, which is the EDX value that the instruction returns. Only the first two bytes of the property are
     * significant and contain the contents of the DX register at CPU reset—all others are set to 0 (zero), and the contents are in
     * DWORD format.
     *
     * This value comes from the Processor ID member of the Processor Information structure in the SMBIOS information.
     */
    ProcessorId,
    /**
     * uint16 Primary function of the processor.
     *
     * This value comes from the Processor Type member of the Processor Information structure in the SMBIOS information.
     */
    ProcessorType,
    /**
     * uint16 Data type: uint16
     *
     * Access type: Read-only
     *
     * Qualifiers: MappingStrings ("WMI")
     *
     * System revision level that depends on the architecture. The system revision level contains the same values as the Version
     * property, but in a numerical format.
     */
    // Revision,
    /** string */
    // Role,
    /** boolean */
    // SecondLevelAddressTranslationExtensions,
    /**
     * string The serial number of this processor This value is set by the manufacturer and normally not changeable.
     *
     * This value comes from the Serial Number member of the Processor Information structure in the SMBIOS information.
     *
     * Windows Server 2012 R2, Windows 8.1, Windows Server 2012, Windows 8, Windows Server 2008 R2, Windows 7, Windows Server 2008 and
     * Windows Vista: This property is not supported before Windows Server 2016 and Windows 10.
     */
    SerialNumber,
    /** string */
    // SocketDesignation,
    /** string */
    // Status,
    /** uint16 */
    // StatusInfo,
    /** string */
    // Stepping,
    /** string */
    // SystemCreationClassName,
    /** string */
    // SystemName,
    /** uint32 */
    // ThreadCount,
    /**
     * string Data type: string
     *
     * Access type: Read-only
     *
     * Globally unique identifier for the processor. This identifier may only be unique within a processor family.
     *
     * This property is inherited from CIM_Processor.
     */
    UniqueId,
    /** uint16 */
    // UpgradeMethod,
    /** string */
    Version,
    /** boolean */
    // VirtualizationFirmwareEnabled,
    /** boolean */
    // VMMonitorModeExtensions,
    /** uint32 */
    // VoltageCaps;
    ;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_Processor(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}