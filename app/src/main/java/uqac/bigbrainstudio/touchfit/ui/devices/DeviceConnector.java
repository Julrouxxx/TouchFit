package uqac.bigbrainstudio.touchfit.ui.devices;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TouchFit
 * Created by Julrouxxx.
 */
public class DeviceConnector extends AsyncTask<String, Void, Void> {
    private Socket socket;
    public DeviceConnector (Socket socket){
        this.socket = socket;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param strings The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Void doInBackground(String... strings) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            for(String string : strings){
                writer.write(string);
            }
        writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
