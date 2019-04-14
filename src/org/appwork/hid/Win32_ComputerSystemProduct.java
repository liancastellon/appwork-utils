package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public  enum Win32_ComputerSystemProduct
        implements WMIProperty {
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    IdentifyingNumber,
    /** string */
    Name(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    SKUNumber,
    /** string */
    Vendor(
            MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string */
    // Version,
    /**
     * string Universally unique identifier (UUID) for this product. A UUID is a 128-bit identifier that is guaranteed to be different
     * from other generated UUIDs. If a UUID is not available, a UUID of all zeros is used.
     *
     * This value comes from the UUID member of the System Information structure in the SMBIOS information.
     *
     */
    UUID;

    public final MayChangeOn[] changeOptions;

    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_ComputerSystemProduct(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}