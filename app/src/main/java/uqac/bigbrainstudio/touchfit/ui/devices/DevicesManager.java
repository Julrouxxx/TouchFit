package uqac.bigbrainstudio.touchfit.ui.devices;

import java.net.InetAddress;
import java.util.ArrayList;

public class DevicesManager  {
    public final static DevicesManager instance = new DevicesManager();
    public ArrayList<Devices> devices = new ArrayList<>();

    DevicesManager() {
        devices.add(new Devices(0, "Bullshit"));
        devices.add(new Devices(1, "Real"));
        devices.add(new Devices(2, "touchfit"));
        devices.add(new Devices(3, "julien"));
        //TODO: Load from sql
    }
    public Devices getDevicesByIp(InetAddress ip){
        return devices.stream().anyMatch(d -> d.getIp().equals(ip)) ? devices.stream().filter(d -> d.getIp().equals(ip)).findFirst().get() : null;
    }
    public ArrayList<Devices> getDevices() {
        return devices;
    }

}
