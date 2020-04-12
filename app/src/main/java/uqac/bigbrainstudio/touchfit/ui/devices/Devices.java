package uqac.bigbrainstudio.touchfit.ui.devices;

import java.net.InetAddress;
import java.util.UUID;

public class Devices {

    private int id;
    private String uuid;
    private String name;
    private InetAddress ip;
    private String hostname;
    private boolean connected;

    public Devices(){

    }

    public Devices(int id, String name){
        this.id = id;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public String getHostname() {
        return hostname;
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public InetAddress getIp() {
        return ip;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getId() {
        return id;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


    public void turnOn(){
        new DevicesUDPSend(this).start();
    }

    public void turnOff(){

    }


}

