package edu.rit.csci651.BLELocation;

import android.os.AsyncTask;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by bryan on 5/2/16.
 * @author Bryan Passino, Nicholas Jenis, Adam Fowles
 */
public class GetInfoTask extends AsyncTask<Void, Void, Void>
{

    // private state
    private DataInputStream is;
    private DataOutputStream os;
    private MainActivity ma;
    private String response;

    /**
     * Constructor for a task
     * that gets info from the server
     * @param ma - the main activity
     */
    public GetInfoTask(MainActivity ma)
    {
        this.is = ma.getClient().getIS();
        this.os = ma.getClient().getOS();
        this.ma = ma;
    }

    /**
     * The background task for this Async Task
     * Sends and gets info from the server
     * @param params - not used
     * @return - nothing
     */
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

    /**
     * After the doInBackground method finishes
     * this will get called
     * @param result - nothing
     */
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
