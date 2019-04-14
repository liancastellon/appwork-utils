package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_ComputerSystem
        implements WMIProperty {
    /** uint16 */
    // AdminPasswordStatus,
    /** boolean */
    // AutomaticManagedPagefile,
    /** boolean */
    // AutomaticResetBootOption,
    /** boolean */
    // AutomaticResetCapability,
    /** uint16 */
    // BootOptionOnLimit,
    /** uint16 */
    // BootOptionOnWatchDog,
    /** boolean */
    // BootROMSupported,
    /** string */
    // BootupState,
    /** uint16[] */
    // BootStatus,
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /**
     * uint16 Boot up state of the chassis.
     *
     * This value comes from the Boot-up State member of the System Enclosure or Chassis structure in the SMBIOS information.
     */
    ChassisBootupState,
    /**
     * string The chassis or enclosure SKU number as a string.
     *
     * This value comes from the SKU Number member of the System Enclosure or Chassis structure in the SMBIOS information.
     *
     * Windows Server 2012 R2, Windows 8.1, Windows Server 2012, Windows 8, Windows Server 2008 R2, Windows 7, Windows Server 2008 and
     * Windows Vista: This property is not supported before Windows 10 and Windows Server 2016.
     */
    ChassisSKUNumber,
    /** string */
    // CreationClassName,
    /** sint16 */
    // CurrentTimeZone,
    /** boolean */
    // DaylightInEffect,
    /** string */
    // Description,
    /** string */
    // DNSHostName,
    /** string */
    // Domain,
    /** uint16 */
    // DomainRole,
    /** boolean */
    // EnableDaylightSavingsTime,
    /** uint16 */
    // FrontPanelResetStatus,
    /** boolean */
    // HypervisorPresent,
    /** boolean */
    // InfraredSupported,
    /** string[] */
    // InitialLoadInfo,
    /** datetime */
    // InstallDate,
    /** uint16 */
    // KeyboardPasswordStatus,
    /** string */
    // LastLoadInfo,
    /** string */
    Manufacturer,
    /** string */
    Model,
    /** string */
    // Name,
    /** string */
    // NameFormat,
    /** boolean */
    // NetworkServerModeEnabled,
    /** uint32 */
    // NumberOfLogicalProcessors,
    /** uint32 */
    // NumberOfProcessors,
    /**
     * uint8[] List of data for a bitmap that the original equipment manufacturer (OEM) creates.
     */
    // TODO: maybe wmic?
    OEMLogoBitmap,
    /**
     * string [] List of free-form strings that an OEM defines. For example, an OEM defines the part numbers for system reference
     * documents, manufacturer contact information, and so on.
     */
    // TODO: maybe wmic?
    OEMStringArray,
    /** boolean */
    // PartOfDomain,
    /** sint64 */
    // PauseAfterReset,
    /**
     * uint16 Type of the computer in use, such as laptop, desktop, or Tablet.
     *
     *
     * Unspecified (0)
     *
     *
     * Desktop (1)
     *
     *
     * Mobile (2)
     *
     *
     * Workstation (3)
     *
     *
     * Enterprise Server (4)
     *
     *
     * SOHO Server (5)
     *
     * Small Office and Home Office (SOHO) Server
     *
     *
     * Appliance PC (6)
     *
     *
     * Performance Server (7)
     *
     *
     * Maximum (8)
     */
    // PCSystemType,
    /**
     * uint16 Type of the computer in use, such as laptop, desktop, or Tablet.
     *
     * Windows Server 2012, Windows 8, Windows Server 2008 R2, Windows 7, Windows Server 2008 and Windows Vista: This property is not
     * supported before Windows 8.1 and Windows Server 2012 R2.
     *
     *
     * Unspecified (0)
     *
     *
     * Desktop (1)
     *
     *
     * Mobile (2)
     *
     *
     * Workstation (3)
     *
     *
     * Enterprise Server (4)
     *
     *
     * SOHO Server (5)
     *
     *
     * Appliance PC (6)
     *
     *
     * Performance Server (7)
     *
     *
     * Slate (8)
     *
     *
     * Maximum (9)
     */
    // PCSystemTypeEx,
    /** uint16 [] */
    // PowerManagementCapabilities,
    /** boolean */
    // PowerManagementSupported,
    /** uint16 */
    // PowerOnPasswordStatus,
    /** uint16 */
    // PowerState,
    /** uint16 */
    // PowerSupplyState,
    /** string */
    // PrimaryOwnerContact,
    /** string */
    // PrimaryOwnerName,
    /** uint16 */
    // ResetCapability,
    /** sint16 */
    // ResetCount,
    /** sint16 */
    // ResetLimit,
    /** string[] */
    // Roles,
    /** string */
    // Status,
    /** string [] */
    // SupportContactDescription,
    /**
     * string The family to which a particular computer belongs. A family refers to a set of computers that are similar but not
     * identical from a hardware or software point of view.
     *
     * This value comes from the Family member of the System Information structure in the SMBIOS information.
     *
     * Windows Server 2012 R2, Windows 8.1, Windows Server 2012, Windows 8, Windows Server 2008 R2, Windows 7, Windows Server 2008 and
     * Windows Vista: This property is not supported before Windows 10 and Windows Server 2016.
     */
    SystemFamily,
    /**
     * string Identifies a particular computer configuration for sale. It is sometimes also called a product ID or purchase order
     * number.
     *
     * This value comes from the SKU Number member of the System Information structure in the SMBIOS information.
     *
     * Windows Server 2012 R2, Windows 8.1, Windows Server 2012, Windows 8, Windows Server 2008 R2, Windows 7, Windows Server 2008 and
     * Windows Vista: This property is not supported before Windows 10 and Windows Server 2016.
     */
    SystemSKUNumber,
    /** uint16 */
    // SystemStartupDelay,
    /** string [] */
    // SystemStartupOptions,
    /** uint8 */
    // SystemStartupSetting,
    /** string */
    SystemType,
    /** uint16 */
    // ThermalState,
    /** uint64 */
    // TotalPhysicalMemory,
    /** string */
    // UserName,
    /** uint16 */
    // WakeUpType,
    /** string */
    // Workgroup

    ;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_ComputerSystem(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}