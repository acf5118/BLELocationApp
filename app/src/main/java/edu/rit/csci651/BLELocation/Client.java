package edu.rit.csci651.BLELocation;

/**
 * Created by bryan on 5/1/16.
 */
        import java.io.DataInputStream;
        import java.io.DataOutput;
        import java.io.DataOutputStream;
        import java.io.IOException;
        import java.net.InetSocketAddress;
        import java.net.Socket;
        import java.net.UnknownHostException;
        import android.bluetooth.BluetoothAdapter;
        import android.os.AsyncTask;
        import android.widget.TextView;

public class Client extends AsyncTask<Void, Void, Void> {

    private String ip;
    private int port;
    private DataInputStream is;
    private DataOutputStream os;
    private MainActivity ma;


    Client(MainActivity ma) {
        ip = ma.getIp();
        port = ma.getPort();
        this.ma = ma;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 5000);
            System.out.println("Connected");
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            String devices = is.readUTF();
            String[] deviceNames = devices.split(",");
            for(String name: deviceNames){
                ma.addDevice(name);
            }
            ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ma.setConnect();
                }
            });


        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected DataInputStream getIS(){
        return is;
    }

    protected DataOutputStream getOS(){
        return os;
    }
}
