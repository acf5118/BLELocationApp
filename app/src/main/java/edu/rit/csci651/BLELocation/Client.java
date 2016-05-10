package edu.rit.csci651.BLELocation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;

/**
 * Created by bryan on 5/1/16.
 * @author Bryan Passino, Nicholas Jenis, Adam Fowles
 */
public class Client extends AsyncTask<Void, Void, Void>
{
    //private state
    private String ip;
    private int port;
    private DataInputStream is;
    private DataOutputStream os;
    private MainActivity ma;

    /**
     * Constructor for the client
     * takes in the main activity to use
     * main activity state
     * @param ma - the main activity
     */
    public Client(MainActivity ma)
    {
        this.ip = ma.getIp();
        this.port = ma.getPort();
        this.ma = ma;
    }

    /**
     * Background task, connect up the socket
     * @param arg0 - unused
     * @return - nothing
     */
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
            ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ma.showToast(ma.getString(R.string.unknownHost));
                }
            });
        }
        catch (IOException e) {
            ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ma.showToast(ma.getString(R.string.IOError));
                }
            });

        }
        return null;
    }

    // Access methods
    protected DataInputStream getIS(){
        return is;
    }
    protected DataOutputStream getOS(){
        return os;
    }
}
