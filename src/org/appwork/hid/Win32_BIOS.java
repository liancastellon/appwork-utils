package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_BIOS
        implements WMIProperty {
    /** uint16 [] */
    // BiosCharacteristics,
    /** string [] */
    // BIOSVersion,
    /** string */
    BuildNumber(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    // CodeSet,
    /** string */
    // CurrentLanguage,
    /** string */
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** uint8 */
    EmbeddedControllerMajorVersion(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** uint8 */
    EmbeddedControllerMinorVersion(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** string */
    IdentificationCode,
    /** uint16 */
    // InstallableLanguages,
    /** datetime */
    // InstallDate,
    /** string */
    // LanguageEdition,
    /** String [] */
    // ListOfLanguages,
    /** string */
    Manufacturer,
    /** string */
    Name,
    /** string */
    // OtherTargetOS,
    /** boolean */
    // PrimaryBIOS,
    /** datetime */
    ReleaseDate(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** string */
    SerialNumber,
    /** string */
    SMBIOSBIOSVersion(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** uint16 */
    SMBIOSMajorVersion(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** uint16 */
    SMBIOSMinorVersion(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** boolean */
    // SMBIOSPresent,
    /** string */
    // SoftwareElementID,
    /** uint16 */
    // SoftwareElementState,
    /** string */
    // Status,
    /** uint8 */
    SystemBiosMajorVersion(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** uint8 */
    SystemBiosMinorVersion(
            MayChangeOn.BIOS_FW_DRV_UPDATE),
    /** uint16 */
    // TargetOperatingSystem,
    /** string */
    Version(
            MayChangeOn.BIOS_FW_DRV_UPDATE);
    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_BIOS(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}