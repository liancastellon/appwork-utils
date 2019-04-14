package org.appwork.utils.os;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.appwork.console.table.Column;
import org.appwork.console.table.Renderer;
import org.appwork.console.table.Table;
import org.appwork.exceptions.WTFException;
import org.appwork.hid.WMIProperty;
import org.appwork.hid.Win32_BIOS;
import org.appwork.hid.Win32_BaseBoard;
import org.appwork.hid.Win32_Battery;
import org.appwork.hid.Win32_CDROMDrive;
import org.appwork.hid.Win32_ComputerSystem;
import org.appwork.hid.Win32_ComputerSystemProduct;
import org.appwork.hid.Win32_DiskDrive;
import org.appwork.hid.Win32_NetworkAdapter;
import org.appwork.hid.Win32_PhysicalMemory;
import org.appwork.hid.Win32_PnPEntity;
import org.appwork.hid.Win32_Processor;
import org.appwork.hid.Win32_videocontroller;
import org.appwork.loggingv3.LogV3;
import org.appwork.storage.JSonStorage;
import org.appwork.storage.TypeRef;
import org.appwork.utils.Regex;
import org.appwork.utils.StringUtils;
import org.appwork.utils.processes.ProcessBuilderFactory;

public class WindowsHardwareIDGenerator {
    private static String WMIC_PATH;

    /**
     * Collect Hardwareinformation via WMIC on Windows.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public WindowsHardwareIDGenerator() {
    }

    public HardwareInfoStorable build() throws IOException, InterruptedException {
        data = new HardwareInfoStorable();
        data.setTime(System.currentTimeMillis());
        this.appendBattery();
        this.appendRAM();
        this.appendComputerSystem();
        this.appendCPU();
        this.appendDiskDrive();
        this.appendbaseBoard();
        this.appendCDRom();
        this.appendBios();
        this.appendCSProduct();
        this.appendPnPentity();
        this.appendGraphicsCard();
        this.appendNetwork();
        this.appendSystemEnclosure();
        appendWindowsCryptoID();
        appendWindowsOS();
        appendWindowsDeviceID();
        return getData();
    }

    /**
     * @throws InterruptedException
     * @throws IOException
     *
     */
    private void appendWindowsOS() throws IOException, InterruptedException {
        CSVContent result = this.wmic("os", "get", "*");
        String[] fields = new String[] { "BootDevice", "Name", "OSArchitecture", "SerialNumber", "SystemDevice" };
        result.sort(fields);
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (String s : fields) {
                this.add("Windows", result, s, i, i);
            }
        }
    }

    private void appendBattery() throws IOException, InterruptedException {
        CSVContent result = this.wmic("path", "Win32_Battery", "get", "*");
        // http://www.powertheshell.com/reference/wmireference/root/cimv2/win32_battery/
        // $Chemistry_ReturnValue =
        // @{
        // 1='Other'
        // 2='Unknown'
        // 3='Lead Acid'
        // 4='Nickel Cadmium'
        // 5='Nickel Metal Hydride'
        // 6='Lithium-ion'
        // 7='Zinc air'
        // 8='Lithium Polymer'
        // }
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (Win32_Battery s : Win32_Battery.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, i, ((WMIProperty) s).getChangeOptions());
            }
        }
    }

    private void appendSystemEnclosure() throws IOException, InterruptedException {
        CSVContent result = this.wmic("path", "Win32_SystemEnclosure", "get", "*");
        HashMap<Integer, String> mapping = new HashMap<Integer, String>();
        mapping.put(1, "Other");
        mapping.put(2, "Unknown");
        mapping.put(3, "Desktop");
        mapping.put(4, "Low Profile Desktop");
        mapping.put(5, "Pizza Box");
        mapping.put(6, "Mini Tower");
        mapping.put(7, "Tower");
        mapping.put(8, "Portable");
        mapping.put(9, "Laptop");
        mapping.put(10, "Notebook");
        mapping.put(11, "Handheld");
        mapping.put(12, "Docking Station");
        mapping.put(13, "All-in-One");
        mapping.put(14, "Sub-Notebook");
        mapping.put(15, "Space Saving");
        mapping.put(16, "Lunch Box");
        mapping.put(17, "Main System Chassis");
        mapping.put(18, "Expansion Chassis");
        mapping.put(19, "Sub-Chassis");
        mapping.put(20, "Bus Expansion Chassis");
        mapping.put(21, "Peripheral Chassis");
        mapping.put(22, "Storage Chassis");
        mapping.put(23, "Rack Mount Chassis");
        mapping.put(24, "Sealed-Case PC");
        // System.out.println(result);
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            for (String s : fields) {
                if ("ChassisTypes".equals(s)) {
                    ArrayList<String> values = new ArrayList<String>();
                    try {
                        for (int id : JSonStorage.restoreFromString(result.get("ChassisTypes", i).replace("{", "[").replace("}", "]"), TypeRef.INT_ARRAY)) {
                            String v = mapping.get(id);
                            if (v == null) {
                                v = "unknown";
                            }
                            values.add(v);
                        }
                        this.data.add("System Type", index, "ChassisTypes", result.get("ChassisTypes", i) + " - " + StringUtils.join(values, ", "));
                    } catch (Throwable e) {
                        this.data.add("System Type", index, "ChassisTypes", result.get("ChassisTypes", i));
                    }
                } else {
                    this.add("System Type", result, s, i, index);
                }
            }
        }
        // probably given by the os
        // add(sb, result, "VolumeSerialNumber", i);
    }

    private void appendWindowsDeviceID() throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        String result;
        if (CrossSystem.is64BitOperatingSystem()) {
            // with "/reg:64" the command does not work if called from a 3 bit jvm
            result = ProcessBuilderFactory.runCommand("reg", "query", "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\SQMClient", "/reg:64", "/v", "MachineId").getStdOutString();
        } else {
            result = ProcessBuilderFactory.runCommand("reg", "query", "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\SQMClient", "/v", "MachineId").getStdOutString();
        }
        String guid = new Regex(result, "\\s+MachineId\\s+.*\\s+(\\S+)\\s*$").getMatch(0);
        if (StringUtils.isNotEmpty(guid)) {
            this.data.add("Windows", 0, "SQMClient.MachineId", guid.toLowerCase(Locale.ENGLISH));
        }
    }

    /**
     * @throws InterruptedException
     * @throws IOException
     *
     */
    private void appendWindowsCryptoID() throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        String result;
        if (CrossSystem.is64BitOperatingSystem()) {
            // with "/reg:64" the command does not work if called from a 3 bit jvm
            result = ProcessBuilderFactory.runCommand("reg", "query", "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Cryptography", "/reg:64", "/v", "MachineGuid").getStdOutString();
        } else {
            result = ProcessBuilderFactory.runCommand("reg", "query", "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid").getStdOutString();
        }
        String guid = new Regex(result, "\\s+MachineGuid\\s+.*\\s+(\\S+)\\s*$").getMatch(0);
        if (StringUtils.isNotEmpty(guid)) {
            this.data.add("Windows", 0, "Cryptography.MachineGuid", guid.toLowerCase(Locale.ENGLISH));
        }
    }

    private void appendGraphicsCard() throws IOException, InterruptedException {
        CSVContent result = this.wmic("path", "Win32_videocontroller", "get", "*");
        int index = 0;
        HashSet<String> allowedPortTypes = new HashSet<String>();
        allowedPortTypes.add("pci");
        allowedPortTypes.add("scsi");
        allowedPortTypes.add("hdaudio");
        allowedPortTypes.add("acpi");
        allowedPortTypes.add("swd");
        for (int i = 0; i < result.size(); i++) {
            String deviceID = result.get("PNPDeviceID", i);
            String portType = getPortTypeByDeviceID(deviceID);
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (portType == null || !allowedPortTypes.contains(portType)) {
                continue;
            }
            if (getAddressByDeviceID(deviceID) == null) {
                continue;
            }
            if (StringUtils.isEmpty(result.get("AdapterRAM", i))) {
                continue;
            }
            if (StringUtils.isEmpty(result.get("AdapterDACType", i))) {
                continue;
            }
            if (StringUtils.isEmpty(result.get("VideoProcessor", i))) {
                continue;
            }
            String address = getAddressByDeviceID(result.get("PNPDeviceID", i));
            if (address == null) {
                continue;
            }
            for (Win32_videocontroller s : Win32_videocontroller.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, index, ((WMIProperty) s).getChangeOptions());
            }
            index++;
            // probably given by the os
            // add(sb, result, "VolumeSerialNumber", i);
        }
    }

    private void appendPnPentity() throws IOException, InterruptedException {
        // https://www.keysight.com/main/editorial.jspx?ckey=2039700&id=2039700&nid=-11143.0.00&lc=ger&cc=DE
        CSVContent result = this.wmic("path", "Win32_PnPEntity", "get", "*");
        int index = 0;
        HashSet<String> allowedPortTypes = new HashSet<String>();
        allowedPortTypes.add("pci");
        for (int i = 0; i < result.size(); i++) {
            String deviceID = result.get("DeviceID", i);
            String portType = getPortTypeByDeviceID(deviceID);
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (!(deviceID.toLowerCase(Locale.ENGLISH).contains("subsys_") && deviceID.toLowerCase(Locale.ENGLISH).contains("ven_") && deviceID.toLowerCase(Locale.ENGLISH).contains("dev_"))) {
                continue;
            }
            if (portType == null || !allowedPortTypes.contains(portType)) {
                continue;
            }
            if (getAddressByDeviceID(deviceID) == null) {
                continue;
            }
            for (Win32_PnPEntity s : Win32_PnPEntity.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, index, ((WMIProperty) s).getChangeOptions());
            }
            index++;
            // probably given by the os
            // add(sb, result, "VolumeSerialNumber", i);
        }
    }

    protected String getPortTypeByDeviceID(String deviceID) {
        return new Regex(deviceID.toLowerCase(Locale.ENGLISH), "^([^\\\\//]+)").getMatch(0);
    }

    private void appendNetwork() throws IOException, InterruptedException {
        CSVContent result = this.wmic("nic", "get", "*");
        int index = 0;
        HashSet<String> allowedPortTypes = new HashSet<String>();
        allowedPortTypes.add("pci");
        allowedPortTypes.add("scsi");
        // allowedPortTypes.add("hdaudio");
        allowedPortTypes.add("acpi");
        allowedPortTypes.add("swd");
        for (int i = 0; i < result.size(); i++) {
            String deviceID = result.get("PNPDeviceID", i);
            String portType = getPortTypeByDeviceID(deviceID);
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (portType == null || !allowedPortTypes.contains(portType)) {
                continue;
            }
            if (getAddressByDeviceID(deviceID) == null) {
                continue;
            }
            for (Win32_NetworkAdapter s : Win32_NetworkAdapter.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, index, ((WMIProperty) s).getChangeOptions());
            }
            index++;
            // probably given by the os
            // add(sb, result, "VolumeSerialNumber", i);
        }
    }

    /**
     * Check the hardware address. this should return null for virtual devices
     *
     * @param deviceID
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    private String getAddressByDeviceID(String deviceID) throws IOException, InterruptedException {
        if (deviceID == null) {
            return null;
        }
        deviceID = deviceID.replaceAll(Pattern.quote("&amp;"), "&");
        String reg;
        if (CrossSystem.is64BitOperatingSystem()) {
            // with "/reg:64" the command does not work if called from a 3 bit jvm
            reg = ProcessBuilderFactory.runCommand("reg", "query", "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Enum\\" + deviceID, "/reg:64", "/v", "Address").getStdOutString();
        } else {
            reg = ProcessBuilderFactory.runCommand("reg", "query", "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Enum\\" + deviceID, "/v", "Address").getStdOutString();
        }
        return new Regex(reg, "\\s+Address\\s+.*\\s+(\\S+)\\s*$").getMatch(0);
    }

    private void appendCSProduct() throws IOException, InterruptedException {
        CSVContent result = this.wmic("csproduct", "get", "*");
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (Win32_ComputerSystemProduct s : Win32_ComputerSystemProduct.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, i, ((WMIProperty) s).getChangeOptions());
            }
        }
    }

    private void appendBios() throws IOException, InterruptedException {
        CSVContent result = this.wmic("bios", "get", "*");
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (Win32_BIOS s : Win32_BIOS.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, i, ((WMIProperty) s).getChangeOptions());
            }
        }
    }

    private void appendCDRom() throws IOException, InterruptedException {
        CSVContent result = this.wmic("cdrom", "get", "*");
        // System.out.println(result);
        int index = 0;
        HashSet<String> allowedPortTypes = new HashSet<String>();
        allowedPortTypes.add("pci");
        allowedPortTypes.add("scsi");
        // allowedPortTypes.add("hdaudio");
        allowedPortTypes.add("acpi");
        allowedPortTypes.add("swd");
        allowedPortTypes.add("ide");
        for (int i = 0; i < result.size(); i++) {
            String deviceID = result.get("PNPDeviceID", i);
            String portType = getPortTypeByDeviceID(deviceID);
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (portType == null || !allowedPortTypes.contains(portType)) {
                continue;
            }
            if (getAddressByDeviceID(deviceID) == null) {
                continue;
            }
            // seems like virtual devices are >0 (virtual clone drive e.g. at 2)
            if (!"0".equals(result.get("SCSIPort", i))) {
                continue;
            }
            if ("unknown".equalsIgnoreCase(result.get("MediaType", i))) {
                continue;
            }
            for (Win32_CDROMDrive s : Win32_CDROMDrive.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, index, ((WMIProperty) s).getChangeOptions());
            }
            index++;
        }
    }

    private void appendbaseBoard() throws IOException, InterruptedException {
        CSVContent result = this.wmic("baseboard", "get", "*");
        // System.out.println(result);
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            for (Win32_BaseBoard s : Win32_BaseBoard.values()) {
                this.add(Win32_BaseBoard.class.getSimpleName(), result, s.name(), i, index, ((WMIProperty) s).getChangeOptions());
            }
            index++;
        }
    }

    private void appendDiskDrive() throws IOException, InterruptedException {
        CSVContent result = this.wmic("diskdrive", "get", "*");
        // System.out.println(result);
        int index = 0;
        HashSet<String> allowedPortTypes = new HashSet<String>();
        allowedPortTypes.add("pci");
        allowedPortTypes.add("scsi");
        allowedPortTypes.add("hdaudio");
        allowedPortTypes.add("acpi");
        allowedPortTypes.add("swd");
        for (int i = 0; i < result.size(); i++) {
            String deviceID = result.get("PNPDeviceID", i);
            String portType = getPortTypeByDeviceID(deviceID);
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (portType == null || !allowedPortTypes.contains(portType)) {
                continue;
            }
            if (getAddressByDeviceID(deviceID) == null) {
                continue;
            }
            if (StringUtils.isEmpty(result.get("SerialNumber", i))) {
                continue;
            }
            if (!StringUtils.containsIgnoreCase(result.get("MediaType", i), "fixed")) {
                continue;
            }
            for (Win32_DiskDrive s : Win32_DiskDrive.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, index, ((WMIProperty) s).getChangeOptions());
            }
            index++;
        }
    }

    /**
     * @param string
     * @return
     */
    private boolean isVirtualHardware(String line) {
        // TODO Auto-generated method stub
        boolean ret = StringUtils.containsIgnoreCase(line, "virtu");
        ret |= StringUtils.containsIgnoreCase(line, "Citrix");
        ret |= StringUtils.containsIgnoreCase(line, "portable");
        // ret |= StringUtils.containsIgnoreCase(line, "vm");
        if (ret) {
            LogV3.info("Virtual Device: " + line);
        }
        return ret;
    }

    private void appendCPU() throws IOException, InterruptedException {
        CSVContent result = this.wmic("cpu", "get", "*");
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (Win32_Processor s : Win32_Processor.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, i, ((WMIProperty) s).getChangeOptions());
            }
        }
    }

    private void appendComputerSystem() throws IOException, InterruptedException {
        CSVContent result = this.wmic("computersystem", "get", "*");
        for (int i = 0; i < result.size(); i++) {
            for (Win32_ComputerSystem s : Win32_ComputerSystem.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, i, ((WMIProperty) s).getChangeOptions());
            }
        }
    }

    private HardwareInfoStorable data;

    /**
     * @return the data
     */
    public HardwareInfoStorable getData() {
        return data;
    }

    private void add(String category, CSVContent result, String key, int i, int index, MayChangeOn... mayChangeOns) {
        String value = String.valueOf(result.get(key, i));
        if (StringUtils.isEmpty(value)) {
            return;
        }
        this.data.add(category, index, key, value, mayChangeOns);
    }

    protected void appendRAM() throws IOException, InterruptedException {
        CSVContent result = this.wmic("memorychip", "get", "*");
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (Win32_PhysicalMemory s : Win32_PhysicalMemory.values()) {
                this.add(s.getClass().getSimpleName(), result, s.name(), i, i, ((WMIProperty) s).getChangeOptions());
            }
        }
    }

    {
        if (CrossSystem.isWindows()) {
            final String wmic = System.getenv("SYSTEMROOT") + "\\System32\\Wbem\\wmic.exe";
            if (new File(wmic).exists()) {
                WMIC_PATH = wmic;
            } else {
                WMIC_PATH = "wmic";
            }
        }
    }

    public class CSVContent {
        private String[]            captions;
        private ArrayList<String[]> values;

        public CSVContent(String[] captions) {
            this.captions = captions;
            this.values = new ArrayList<String[]>();
        }

        /**
         * @param index
         * @return
         */
        public String get(int index) {
            return StringUtils.join(values.get(index), "\t");
        }

        public void sort(String... strings) {
        }

        public String get(String columnLabel, int index) {
            for (int i = 0; i < this.captions.length; i++) {
                if (columnLabel.equals(this.captions[i])) {
                    return this.values.get(index)[i];
                }
            }
            return null;
        }

        public int size() {
            return this.values.size();
        }

        public void add(String[] values) {
            if (values.length != this.captions.length) {
                throw new WTFException("This should not happen");
            }
            this.values.add(values);
        }

        @Override
        public String toString() {
            Table<String[]> table = new Table<String[]>(false, false, true);
            for (String s : this.captions) {
                table.addColumn(new Column<String[]>(s, false, new Renderer<String[]>() {
                    @Override
                    public Object getString(String[] d, int row, Column<String[]> column) {
                        return d[column.index];
                    }

                    @Override
                    public Object getSubHeader(List<String[]> rows, Column<String[]> column) {
                        return null;
                    }

                    @Override
                    public Object getFooter(List<String[]> rows, Column<String[]> column) {
                        return null;
                    }

                    @Override
                    public int compare(String[] o1, String[] o2, Column<String[]> c) {
                        return 0;
                    }
                }));
            }
            table.setData(this.values);
            return table.toString();
        }
    }

    protected CSVContent wmic(String... cmd) throws IOException, InterruptedException {
        String[] full = new String[cmd.length + 3];
        full[0] = WMIC_PATH;
        System.arraycopy(cmd, 0, full, 1, cmd.length);
        full[full.length - 2] = "/translate:nocomma";
        full[full.length - 1] = "/format:csv";
        String csv = ProcessBuilderFactory.runCommand(full).getStdOutString();
        csv = csv.trim();
        String[] lines = csv.split("[\r\n]+");
        String[] captions = lines[0].split(",", -1);
        CSVContent ret = new CSVContent(captions);
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",", -1);
            ret.add(values);
        }
        return ret;
    }
}
