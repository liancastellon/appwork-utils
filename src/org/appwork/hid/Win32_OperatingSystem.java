package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

/**
 * @throws @throws
 *             IOException
 *
 */
public enum Win32_OperatingSystem
        implements WMIProperty {
    // BootDevice,
    // Name,
    // OSArchitecture,
    // SerialNumber,
    // SystemDevice
    BootDevice,
    BuildNumber(
            MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    BuildType(
            MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    // CodeSet
    // string CountryCode
    // string CreationClassName
    // string CSCreationClassName
    // CSDVersion,
    // CSName,
    // sint16 CurrentTimeZone
    // boolean DataExecutionPrevention_Available
    // boolean DataExecutionPrevention_32BitApplications
    // boolean DataExecutionPrevention_Drivers
    // uint8 DataExecutionPrevention_SupportPolicy
    // boolean Debug
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    // boolean Distributed
    // uint32 EncryptionLevel
    // uint8 ForegroundApplicationBoost = 2
    // uint64 FreePhysicalMemory
    // uint64 FreeSpaceInPagingFiles
    // uint64 FreeVirtualMemory
    InstallDate(
            MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    // uint32 LargeSystemCache
    // datetime LastBootUpTime
    // datetime LocalDateTime
    // string Locale
    Manufacturer,
    // uint32 MaxNumberOfProcesses
    // uint64 MaxProcessMemorySize
    // string MUILanguages[]
    Name,
    // uint32 NumberOfLicensedUsers,
    // uint32 NumberOfProcesses
    // uint32 NumberOfUsers
    OperatingSystemSKU,
    // Organization,
    OSArchitecture,
    // uint32 OSLanguage
    OSProductSuite,
    OSType,
    OtherTypeDescription,
    // Boolean PAEEnabled
    // string PlusProductID
    // string PlusVersionNumber
    // boolean PortableOperatingSystem
    // boolean Primary
    ProductType,
    // string RegisteredUser,
    SerialNumber,
    ServicePackMajorVersion(
            MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    ServicePackMinorVersion(
            MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    // SizeStoredInPagingFiles
    // string Status
    // uint32 SuiteMask
    SystemDevice,
    SystemDirectory,
    SystemDrive,
    // uint64 TotalSwapSpaceSize
    // uint64 TotalVirtualMemorySize
    // uint64 TotalVisibleMemorySize
    Version(
            MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    WindowsDirectory,
    // uint8 QuantumLength
    // uint8 QuantumType
    ;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_OperatingSystem(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}