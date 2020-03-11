package uqac.bigbrainstudio.touchfit.ui.devices;

import android.os.AsyncTask;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.Objects;

public class DevicesDataRunnable extends AsyncTask<Devices, Void, Void> {
    private WeakReference<RecyclerView> recyclerView;
    public DevicesDataRunnable(RecyclerView recyclerView){
        this.recyclerView = new WeakReference<>(recyclerView);
    }
    @Override
    protected Void doInBackground(Devices... devices) {
        for(Devices device : devices)
            try {
                InetAddress address = InetAddress.getByName(device.getStringIP());
                device.setHostname(address.getHostName());
                device.setIp(address);
                device.setConnected(address.isReachable(1000));
                recyclerView.get().post(() -> {
                    Objects.requireNonNull(recyclerView.get().getAdapter()).notifyItemChanged(device.getId());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }
}
