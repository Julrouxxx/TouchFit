package uqac.bigbrainstudio.touchfit.controllers;

import com.google.firebase.database.Exclude;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

public class Device implements Comparable<Device> {

    private int id;
    private int position;
    private String uuid;
    private String name;
    private InetAddress ip;
    private String hostname;
    private boolean connected;
    private boolean on;
    private String key;
    private Socket socket;
    private Thread thread;

    public Device() {

    }

    public Device(int id, String name) {
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

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean b) {
        this.on = b;
    }

    public void turnOn(int seconds){
        if(socket != null && !socket.isClosed()) {
            on = true;
            new DeviceConnector(socket).execute(String.valueOf(seconds));
        }

    }

    public void startListening(DeviceListener deviceListener){
        if(socket !=  null) {
            this.thread = new Thread(new DeviceButtonThread(socket, deviceListener, this));
            thread.start();
        }
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

    public void stopListening(){
        if(thread != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Exclude
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void reset() {
        stopListening();
        if(socket != null && !socket.isClosed())
            new DeviceConnector(socket).execute("del");
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Device o) {
        return getId() < o.getId() ? -1 : 1;
    }
}

