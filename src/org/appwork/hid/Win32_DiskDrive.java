package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_DiskDrive
        implements WMIProperty {
    // Availability,
    // BytesPerSector,
    // Capabilities,
    // CapabilityDescriptions,
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    // CompressionMethod,
    // ConfigManagerErrorCode,
    // ConfigManagerUserConfig,
    // CreationClassName,
    // DefaultBlockSize,
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    DeviceID,
    // ErrorCleared,
    // ErrorDescription,
    // ErrorMethodology,
    FirmwareRevision(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    // Index,
    // InstallDate,
    // InterfaceType,
    // LastErrorCode,
    Manufacturer(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    // MaxBlockSize,
    // MaxMediaSize,
    MediaLoaded,
    MediaType,
    // MinBlockSize,
    Model,
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    // NeedsCleaning,
    // NumberOfMediaSupported,
    // Partitions,
    PNPDeviceID,
    // PowerManagementCapabilities,
    // PowerManagementSupported,
    SCSIBus,
    // SCSILogicalUnit,
    // SCSIPort,
    // SCSITargetId,
    // SectorsPerTrack,
    SerialNumber,
    Signature(
            MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    Size(
            MayChangeOn.ANYTIME),
    // Status,
    // StatusInfo,
    // SystemCreationClassName,
    // SystemName,
    // TotalCylinders,
    // TotalHeads,
    // TotalSectors,
    // TotalTracks,
    // TracksPerCylinder;
    ;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_DiskDrive(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}