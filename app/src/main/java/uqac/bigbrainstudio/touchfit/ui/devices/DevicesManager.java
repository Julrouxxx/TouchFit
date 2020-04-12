package uqac.bigbrainstudio.touchfit.ui.devices;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.net.InetAddress;
import java.util.ArrayList;

public class DevicesManager  {
    private final static DevicesManager instance = new DevicesManager();
    public ArrayList<Devices> devices = new ArrayList<>();
    private FirebaseUser mUser;
    private DatabaseReference mData;
    DevicesManager() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        this.mData = FirebaseDatabase.getInstance().getReference();

        /*

        devices.add(new Devices(0, "Bullshit"));
        devices.add(new Devices(1, "Real"));
        devices.add(new Devices(2, "touchfit"));
        devices.add(new Devices(3, "julien"));*/
        //TODO: Load from sql
    }

    public void setup(){
        this.mUser = FirebaseAuth.getInstance().getCurrentUser();

        mData.child("devices").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                devices.clear();
                for(DataSnapshot devicesSnap : dataSnapshot.getChildren()){
                    Devices device = devicesSnap.getValue(Devices.class);
                    devices.add(device);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TouchFit", "Error on reading online error: " + databaseError.getCode());
            }
        });
    }
    public Devices getDevicesByIp(InetAddress ip){
        return devices.stream().anyMatch(d -> d.getIp().equals(ip)) ? devices.stream().filter(d -> d.getIp().equals(ip)).findFirst().get() : null;
    }
    public ArrayList<Devices> getDevices() {
        return devices;
    }

    public void addDevices(Devices device){
        mData.child("devices").child(mUser.getUid()).push().setValue(device);
    }

    public static DevicesManager getInstance() {
        return instance;
    }
}
