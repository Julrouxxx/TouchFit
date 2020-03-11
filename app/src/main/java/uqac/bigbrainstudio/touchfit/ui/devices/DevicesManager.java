package uqac.bigbrainstudio.touchfit.ui.devices;

import java.net.InetAddress;
import java.util.ArrayList;

public class DevicesManager  {
    public final static DevicesManager instance = new DevicesManager();
    public ArrayList<Devices> devices = new ArrayList<>();

    DevicesManager() {
        devices.add(new Devices(0, "127.0.0.1"));
        devices.add(new Devices(1, "192.168.2.63"));
        devices.add(new Devices(2, "192.168.2.47"));
        devices.add(new Devices(3, "192.168.2.120"));
        //TODO: Load from sql
    }
    public Devices getDevicesByIp(InetAddress ip){
        return devices.stream().anyMatch(d -> d.getIp().equals(ip)) ? devices.stream().filter(d -> d.getIp().equals(ip)).findFirst().get() : null;
    }
    public ArrayList<Devices> getDevices() {
        return devices;
    }

}
