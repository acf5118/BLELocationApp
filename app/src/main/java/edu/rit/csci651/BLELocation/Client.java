package edu.rit.csci651.BLELocation;

/**
 * Created by bryan on 5/1/16.
 */
        import java.io.ByteArrayOutputStream;
        import java.io.DataInputStream;
        import java.io.DataOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.net.Socket;
        import java.net.UnknownHostException;

        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothManager;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.AsyncTask;
        import android.widget.TextView;
        import android.widget.Toast;

public class Client extends AsyncTask<Void, Void, Void> {

    String ip;
    int port;
    String response = "";
    TextView info;
    private String bestName = null;
    private int bestDistance = -200;
    BluetoothAdapter bta;
    DataInputStream inputStream;
    DataOutputStream outputStream;

    Client(String ip, int port, TextView info, BluetoothAdapter bta) {
        this.ip = ip;
        this.port = port;
        this.info = info;
        this.bta = bta;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        final BluetoothManager btm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bta = btm.getAdapter();
        if (bta == null) {
            Toast.makeText(MainActivity, R.string.bluetoothError, Toast.LENGTH_SHORT).show();
        }
        Socket socket = null;
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        try {
            socket = new Socket(ip, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());


        }
        catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        info.setText(response);
        super.onPostExecute(result);
    }

    private void getInfo(){
        bta.startDiscovery();
        if (bestName != null) {
            try {
                outputStream.writeUTF(bestName);
                String response = inputStream.readUTF();
                info.setText(response);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            bestDistance = -200;
            bestName = null;
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                if(rssi > bestDistance){
                    bestName = name;
                    bestDistance = rssi;
                }
                //Toast.makeText(getApplicationContext(), name + "  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
