package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_CDROMDrive
        implements WMIProperty {
    /** uint16 */
    // Availability,
    /** uint16 [] */
    // Capabilities,
    /** string [] */
    // CapabilityDescriptions,
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    // CompressionMethod,
    /** uint32 */
    // ConfigManagerErrorCode,
    /** boolean */
    // ConfigManagerUserConfig,
    /** string */
    // CreationClassName,
    /** uint64 */
    // DefaultBlockSize,
    /** string */
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    DeviceID,
    /** string */
    // Drive,
    /** boolean */
    // DriveIntegrity,
    /** boolean */
    // ErrorCleared,
    /** string */
    // ErrorDescription,
    /** string */
    // ErrorMethodology,
    /** uint16 */
    // FileSystemFlags,
    /** uint32 */
    // FileSystemFlagsEx,
    /** string */
    // Id,
    /** datetime */
    // InstallDate,
    /** uint32 */
    // LastErrorCode,
    /** string */
    Manufacturer,
    /** uint64 */
    // MaxBlockSize,
    /** uint32 */
    // MaximumComponentLength,
    /** uint64 */
    // MaxMediaSize,
    /** boolean */
    // MediaLoaded,
    /** string */
    MediaType,
    /** string */
    // MfrAssignedRevisionLevel,
    /** uint64 */
    // MinBlockSize,
    /** string */
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** boolean */
    // NeedsCleaning,
    /** uint32 */
    // NumberOfMediaSupported,
    /** string */
    PNPDeviceID,
    /** uint16 [] */
    // PowerManagementCapabilities,
    /** boolean */
    // PowerManagementSupported,
    /** string */
    // RevisionLevel,
    /** uint32 */
    // SCSIBus,
    /** uint16 */
    // SCSILogicalUnit,
    /** uint16 */
    SCSIPort,
    /** uint16 */
    // SCSITargetId,
    /** string */
    SerialNumber,
    /** uint64 */
    // Size,
    /** string */
    // Status,
    /** uint16 */
    // StatusInfo,
    /** string */
    // SystemCreationClassName,
    /** string */
    // SystemName,
    /** real64 */
    // TransferRate,
    /** string */
    // VolumeName,
    /** string */
    // VolumeSerialNumber,
    ;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_CDROMDrive(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}