package edu.rit.csci651.BLELocation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private Socket socket = new Socket();
    private int port = 5000;
    private EditText ipTextField;
    private TextView info;
    private DataInputStream is;
    private DataOutputStream os;
    private BluetoothAdapter bta;
    private String bestName = null;
    private int bestDistance = -200;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BluetoothManager btm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bta = btm.getAdapter();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btn = (Button) findViewById(R.id.connectBtn);
        Button getInfo = (Button) findViewById(R.id.getInfo);

        info = (TextView) findViewById(R.id.info);
        if (bta == null) {
            Toast.makeText(this, R.string.bluetoothError, Toast.LENGTH_SHORT).show();
        }
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        ipTextField = (EditText) findViewById(R.id.ip);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String ip = ipTextField.getText().toString();
                try {
                    socket.connect(new InetSocketAddress(ip, port));
                    is = new DataInputStream(socket.getInputStream());
                    os = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                   Toast.makeText(MainActivity.this, (R.string.connectError + ip), Toast.LENGTH_SHORT).show();
                }
                    
            }
        });

            getInfo.setOnClickListener(new View.OnClickListener(){
                public void onClick (View view){
                    bta.startDiscovery();
                    if (bestName != null) {
                        try {
                            os.writeUTF(bestName);
                            String response = is.readUTF();
                            info.setText(response);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        bestDistance = -200;
                        bestName = null;
                    }
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
