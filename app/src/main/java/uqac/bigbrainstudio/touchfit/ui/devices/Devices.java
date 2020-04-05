package uqac.bigbrainstudio.touchfit.ui.devices;

import java.net.InetAddress;
import java.util.UUID;

public class Devices {

    private int id;
    private UUID uuid;
    private String name;
    private InetAddress ip;
    private String hostname;
    private boolean connected;
    public Devices(int id, String name){
        this.id = id;
        this.uuid = UUID.randomUUID();
        this.name = name;
        if(name.equals("Real"))
            uuid = UUID.fromString("feeda0ac-755a-11ea-bc55-0242ac130003");
        if(name.equals("touchfit"))
            uuid = UUID.fromString("111057e2-3415-4999-851e-c5e0269b15d9");
        if(name.equals("julien")){
            uuid = UUID.fromString("6e8daa6c-75ea-11ea-bc55-0242ac130003");
        }
    }

    public String getName() {
        return name;
    }

    public String getHostname() {
        return hostname;
    }

    public UUID getUuid() {
        return uuid;
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

