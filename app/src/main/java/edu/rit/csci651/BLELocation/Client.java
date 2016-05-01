package edu.rit.csci651.BLELocation;

/**
 * Created by bryan on 5/1/16.
 */
        import java.io.DataInputStream;
        import java.io.DataOutputStream;
        import java.io.IOException;
        import java.net.Socket;
        import java.net.UnknownHostException;
        import android.bluetooth.BluetoothAdapter;
        import android.os.AsyncTask;
        import android.widget.TextView;

public class Client extends AsyncTask<Void, Void, Void> {

    private String ip;
    private int port;
    private TextView info;
    private BluetoothAdapter bta;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private MainActivity ma;
    private Socket socket;


    Client(String ip, int port, TextView info, BluetoothAdapter bta, MainActivity ma) {
        this.ip = ip;
        this.port = port;
        this.info = info;
        this.bta = bta;
        this.ma = ma;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        try {
            System.out.println("In background: " + ip + " " + port);
            socket = new Socket(ip, port);
            System.out.println("past connect");

            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void getInfo(){
        bta.startDiscovery();
        if (ma.getBestName() != null) {
            try {
                outputStream.writeUTF(ma.getBestName());
                String response = inputStream.readUTF();
                info.setText(response);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            ma.setBestDistance(-200);
            ma.setBestName(null);
        }
    }

}
