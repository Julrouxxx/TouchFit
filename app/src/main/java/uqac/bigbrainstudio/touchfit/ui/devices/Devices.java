package uqac.bigbrainstudio.touchfit.ui.devices;

import com.google.firebase.database.Exclude;

import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

public class Devices {

    private int id;
    private int position;
    private String uuid;
    private String name;
    private InetAddress ip;
    private String hostname;
    private boolean connected;
    private String key;
    private Socket socket;
    private Thread thread;

    public Devices() {

    }

    public Devices(int id, String name) {
        this.id = id;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;

    }

    @Exclude
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
    @Exclude
    public boolean isConnected() {
        return connected;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


    public void turnOn(int seconds){
        if(socket != null)
        new DeviceConnector(socket).execute(String.valueOf(seconds));
    }

    public void turnOff(){

    }
    public void startListening(DeviceListener deviceListener){
        this.thread = new Thread(new DeviceButtonThread(socket, deviceListener, this));
        thread.start();
    }
    public void stopListening(){
        thread.interrupt();
    }
    @Exclude
    public String getKey() {
        return key;
    }

    public void setName(String text) {
        this.name = text;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void reset() {
        stopListening();
        if(socket != null)
            new DeviceConnector(socket).execute("del");
    }
    @Exclude
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}

