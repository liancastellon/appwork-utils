package org.appwork.utils.os;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.appwork.console.table.Column;
import org.appwork.console.table.Renderer;
import org.appwork.console.table.Table;
import org.appwork.exceptions.WTFException;
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
    public WindowsHardwareIDGenerator() throws IOException, InterruptedException {
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
        String[] fields = new String[] { "Name", "Chemistry", "DeviceID", "DesignCapacity", "DesignVoltage" };
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
        result.sort(fields);
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (String s : fields) {
                this.add("Battery", result, s, i, i);
            }
        }
    }

    private void appendSystemEnclosure() throws IOException, InterruptedException {
        CSVContent result = this.wmic("path", "Win32_SystemEnclosure", "get", "*");
        String[] fields = new String[] { "ChassisTypes", "Manufacturer", "SerialNumber" };
        result.sort(fields);
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
        String[] fields = new String[] { "AdapterCompatibility", "AdapterDACType", "Caption", "VideoProcessor", "PNPDeviceID", "DeviceID" };
        result.sort(fields);
        // // System.out.println(result);
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            // filter virtual devices like │ Citrix Indirect Display Adapter │
            if (StringUtils.isEmpty(result.get("AdapterRAM", i))) {
                continue;
            }
            if (StringUtils.isEmpty(result.get("AdapterDACType", i))) {
                continue;
            }
            if (StringUtils.isEmpty(result.get("VideoProcessor", i))) {
                continue;
            }
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (!StringUtils.startsWithCaseInsensitive(result.get("PNPDeviceID", i), "PCI\\")) {
                for (String s : fields) {
                    this.add("GraphicsCard", result, s, i, index);
                }
            }
            index++;
            // probably given by the os
            // add(sb, result, "VolumeSerialNumber", i);
        }
    }

    private void appendPnPentity() throws IOException, InterruptedException {
        // https://www.keysight.com/main/editorial.jspx?ckey=2039700&id=2039700&nid=-11143.0.00&lc=ger&cc=DE
        CSVContent result = this.wmic("path", "Win32_PnPEntity", "get", "*");
        String[] fields = new String[] { "Service", "PNPDeviceID", "DeviceID" };
        result.sort(fields);
        // System.out.println(result);
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            String deviceID = result.get("DeviceID", i);
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (deviceID == null || !deviceID.toLowerCase(Locale.ENGLISH).startsWith("pci\\")) {
                continue;
            }
            for (String s : fields) {
                this.add("PCI Entity", result, s, i, index);
            }
            index++;
            // probably given by the os
            // add(sb, result, "VolumeSerialNumber", i);
        }
    }

    private void appendNetwork() throws IOException, InterruptedException {
        CSVContent result = this.wmic("nic", "get", "*");
        String[] fields = new String[] { "ProductName", "ServiceName", "Manufacturer", "PNPDeviceID", "MACAddress" };
        result.sort(fields);
        // System.out.println(result);
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            String deviceID = result.get("PNPDeviceID", i);
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (deviceID == null || !deviceID.toLowerCase(Locale.ENGLISH).startsWith("pci\\")) {
                continue;
            }
            for (String s : fields) {
                this.add("Network", result, s, i, index);
            }
            index++;
            // probably given by the os
            // add(sb, result, "VolumeSerialNumber", i);
        }
    }

    private void appendCSProduct() throws IOException, InterruptedException {
        CSVContent result = this.wmic("csproduct", "get", "*");
        String[] fields = new String[] { "Name", "UUID", "Vendor", "Version" };
        result.sort(fields);
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (String s : fields) {
                this.add("CSProduct", result, s, i, i);
            }
        }
    }

    private void appendBios() throws IOException, InterruptedException {
        CSVContent result = this.wmic("bios", "get", "*");
        String[] fields = new String[] { "Caption", "Manufacturer", "Name", "SerialNumber" };
        result.sort(fields);
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (String s : fields) {
                this.add("Bios", result, s, i, i);
            }
        }
    }

    private void appendCDRom() throws IOException, InterruptedException {
        CSVContent result = this.wmic("cdrom", "get", "*");
        String[] fields = new String[] { "Name", "SerialNumber" };
        result.sort(fields);
        // System.out.println(result);
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            if (StringUtils.startsWithCaseInsensitive(result.get("DeviceID", i), "SCSI\\") || StringUtils.startsWithCaseInsensitive(result.get("DeviceID", i), "IDE\\")) {
                // seems like virtual devices are >0 (virtual clone drive e.g. at 2)
                if (!"0".equals(result.get("SCSIPort", i))) {
                    continue;
                }
                if ("unknown".equalsIgnoreCase(result.get("MediaType", i))) {
                    continue;
                }
                for (String s : fields) {
                    this.add("CDRom", result, s, i, index);
                }
                index++;
            }
        }
    }

    private void appendbaseBoard() throws IOException, InterruptedException {
        CSVContent result = this.wmic("baseboard", "get", "*");
        // System.out.println(result);
        String[] fields = new String[] { "Manufacturer", "Product", "SerialNumber", "Version" };
        result.sort(fields);
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            for (String s : fields) {
                this.add("BaseBoard", result, s, i, index);
            }
            index++;
        }
    }

    private void appendDiskDrive() throws IOException, InterruptedException {
        CSVContent result = this.wmic("diskdrive", "get", "*");
        // System.out.println(result);
        String[] fields = new String[] { "Caption", "FirmwareRevision", "Model", "SerialNumber", "Size" };
        result.sort(fields);
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            if (StringUtils.isEmpty(result.get("SerialNumber", i))) {
                continue;
            }
            if (!StringUtils.containsIgnoreCase(result.get("MediaType", i), "fixed")) {
                continue;
            }
            if (isVirtualHardware(result.get(i))) {
                continue;
            }
            for (String s : fields) {
                this.add("DiskDrive", result, s, i, index);
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
        // ret |= StringUtils.containsIgnoreCase(line, "vm");
        if (ret) {
            LogV3.info("Virtual Device: " + line);
        }
        return ret;
    }

    private void appendCPU() throws IOException, InterruptedException {
        CSVContent result = this.wmic("cpu", "get", "*");
        String[] fields = new String[] { "Caption", "Name", "NumberOfLogicalProcessors", "ProcessorId", "Revision", "Version" };
        result.sort(fields);
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (String s : fields) {
                this.add("CPU", result, s, i, i);
            }
        }
    }

    private void appendComputerSystem() throws IOException, InterruptedException {
        CSVContent result = this.wmic("computersystem", "get", "*");
        String[] fields = new String[] { "Manufacturer", "Model", "SystemType", "OEMLogoBitmap", "OEMStringArray" };
        result.sort(fields);
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (String s : fields) {
                this.add("ComputerSystem", result, s, i, i);
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

    private void add(String category, CSVContent result, String key, int i, int index) {
        String value = String.valueOf(result.get(key, i));
        if (StringUtils.isEmpty(value)) {
            return;
        }
        this.data.add(category, index, key, value);
    }

    protected void appendRAM() throws IOException, InterruptedException {
        CSVContent result = this.wmic("memorychip", "get", "*");
        String[] fields = new String[] { "BankLabel", "Capacity", "DeviceLocator", "SerialNumber", "Tag", "TypDetail" };
        result.sort(fields);
        // System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            for (String s : fields) {
                this.add("RAM", result, s, i, i);
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

    private CSVContent wmic(String... cmd) throws IOException, InterruptedException {
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
