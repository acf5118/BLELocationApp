package edu.rit.csci651.BLELocation;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by bryan on 5/2/16.
 */
public class GetInfoTask extends AsyncTask<Void, Void, Void> {

    private DataInputStream is;
    private DataOutputStream os;
    private BluetoothAdapter bta;
    private MainActivity ma;
    private String response;

    GetInfoTask(MainActivity ma){
        is = ma.getClient().getIS();
        os = ma.getClient().getOS();
        bta = ma.getBta();
        this.ma = ma;
    }
    @Override
    protected Void doInBackground(Void... params) {
        if (ma.getBestName() != null) {
            try {
                System.out.println("sending to server " + ma.getBestName());
                os.writeUTF(ma.getBestName());
                response = is.readUTF();
                System.out.println("response from server: " + response);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("In else");
            response = ma.getString(R.string.noDevices);
        }

        ma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ma.setInfo(response);
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        System.out.println("Inpost");
        ma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ma.endOfDiscovery();
            }
        });
        super.onPostExecute(result);
    }

}
