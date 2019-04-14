package org.appwork.hid;

import org.appwork.utils.os.MayChangeOn;

public enum Win32_BaseBoard
        implements WMIProperty {
    /** string */
    Caption(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** string [] */
    // ConfigOptions,
    /** string */
    // CreationClassName,
    /** real32 */
    // Depth,
    /** string */
    Description(
            MayChangeOn.LOCALE, MayChangeOn.BIOS_FW_DRV_UPDATE, MayChangeOn.WINDOWS_UDPATE_OR_REINSTALL),
    /** real32 */
    // Height,
    /**
     * boolean If TRUE, the card is a motherboard, or a baseboard in a chassis.
     */
    HostingBoard,
    /** boolean */
    HotSwappable,
    /** datetime */
    // InstallDate,
    /**
     * string values:Notebook
     */
    Manufacturer,
    /**
     * string values:empty
     */
    Model,
    /**
     * string values:null
     */
    Name,
    /** string */
    OtherIdentifyingInfo,
    /** string */
    PartNumber,
    /** boolean */
    // PoweredOn,
    /**
     * string values: P65_P67SG
     */
    Product,
    /** boolean */
    Removable,
    /** boolean */
    Replaceable,
    /** string */
    // RequirementsDescription,
    /** boolean */
    RequiresDaughterBoard,
    /**
     * string values: Not Applicable
     */
    SerialNumber,
    /** string */
    SKU,
    /** string */
    // SlotLayout,
    /** boolean */
    // SpecialRequirements,
    /** string */
    // Status,
    /**
     * string values: Base Board
     */
    Tag,
    /**
     * string values: null
     */
    Version,
    /** real32 */
    // Weight,
    /** real32 */
    // Width;
    ;public final MayChangeOn[] changeOptions;

    /**
     * @return the changeOptions
     */
    public MayChangeOn[] getChangeOptions() {
        return changeOptions;
    }

    private Win32_BaseBoard(MayChangeOn... changeOptions) {
        this.changeOptions = changeOptions;
    }
}