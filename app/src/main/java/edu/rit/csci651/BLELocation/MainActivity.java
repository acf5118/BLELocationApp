package edu.rit.csci651.BLELocation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity {

    private EditText ipTextField;
    private TextView infoFromServer;
    private Button connect;
    private BluetoothAdapter bta;
    private Client myClient;
    private GetInfoTask getInfoTask;
    private String bestName = null;
    private String ip;
    private int bestDistance = -200;
    private int port = 5000;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //bring object from content to Main
        connect = (Button) findViewById(R.id.connectBtn);
        Button getInfo = (Button) findViewById(R.id.getInfo);
        infoFromServer = (TextView) findViewById(R.id.info);
        ipTextField = (EditText) findViewById(R.id.ip);
        //blutooth init
        bta = BluetoothAdapter.getDefaultAdapter();
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        //button click actions
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ip = ipTextField.getText().toString();
                myClient = new Client(MainActivity.this);
                myClient.execute();

            }
        });

        getInfo.setOnClickListener(new View.OnClickListener(){
            public void onClick (View view){
                if(bta.isDiscovering()){
                    bta.cancelDiscovery();
                }
                bta.startDiscovery();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStop(){
        System.out.println("in stop");
        unregisterReceiver(receiver);
        bta.cancelDiscovery();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        bta.cancelDiscovery();
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("in receiver");

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                System.out.println("in discovery found " + name + " at " + rssi);
                if (rssi > bestDistance) {
                    bestName = name;
                    bestDistance = rssi;
                    System.out.println("new best " + bestName + " at " + bestDistance);
                    getInfoTask = new GetInfoTask(MainActivity.this);
                    getInfoTask.execute();
                }
                //Toast.makeText(getApplicationContext(), name + "  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected int getBestDistance(){
        return bestDistance;
    }

    protected void setBestDistance(int distance){
        bestDistance = distance;
    }

    protected String getBestName(){
        return bestName;
    }

    protected void setBestName(String name){
        bestName = name;
    }

    protected void setInfo(String response) {
        infoFromServer.setText(response);
    }

    protected Client getClient(){
        return myClient;
    }

    protected BluetoothAdapter getBta(){
        return bta;
    }

    protected String getIp(){
        return ip;
    }

    protected int getPort(){
        return port;
    }

    protected void setConnect(){
        connect.setText("Connected");
    }

}
