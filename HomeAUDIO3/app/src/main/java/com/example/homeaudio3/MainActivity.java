    /*
    package com.example.homeaudio3;

    import androidx.appcompat.app.AppCompatActivity;

    import android.os.Bundle;

    public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }
    }*/

    package com.example.homeaudio3;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.bluetooth.BluetoothSocket;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.os.Handler;
    import android.os.SystemClock;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.CompoundButton;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.io.UnsupportedEncodingException;
    import java.util.Set;
    import java.util.UUID;
    import android.widget.Switch;
    import androidx.appcompat.app.AppCompatActivity;

    public class MainActivity extends AppCompatActivity {

        private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
        // #defines for identifying shared types between calling functions
        private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
        private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
        private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
        // GUI Components
        private TextView mBluetoothStatus;
        private TextView mReadBuffer;
        private Button mConnectBtn, bt_vol_up, bt_vol_down, amp_vol_up, amp_vol_down, bass_up, bass_down,play_pause;
        private BluetoothAdapter mBTAdapter;
        private Handler mHandler; // Our main handler that will receive callback notifications
        private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
        private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mBluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus); // Keep
            mReadBuffer = (TextView) findViewById(R.id.readBuffer); // Keep
            Switch sw_power_button = (Switch) findViewById(R.id.amp_power_switch);
            Switch bt_sw_power_button = (Switch) findViewById(R.id.bt_power_switch);
            Switch anc_control = (Switch) findViewById(R.id.anc_control_switch);
            mConnectBtn = (Button) findViewById(R.id.connect);
            bt_vol_up = (Button) findViewById(R.id.volume_up);
            bt_vol_down = (Button) findViewById(R.id.volume_down);
            amp_vol_up = (Button) findViewById(R.id.amp_volume_up);
            amp_vol_down = (Button) findViewById(R.id.amp_volume_down);
            bass_up = (Button) findViewById(R.id.bass_up);
            bass_down = (Button) findViewById(R.id.bass_down);
            play_pause = (Button) findViewById(R.id.play_pause);
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
            try_connecting();
            mHandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == MESSAGE_READ) {
                        String readMessage = null;
                        try {
                            readMessage = new String((byte[]) msg.obj, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mReadBuffer.setText(readMessage);
                    }

                    if (msg.what == CONNECTING_STATUS) {
                        if (msg.arg1 == 1)
                            mBluetoothStatus.setText("Connected to Device: " + (String) (msg.obj));
                        else
                            mBluetoothStatus.setText("Connection Failed");
                    }
                }
            };
                mConnectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try_connecting();
                    }
                });

            // Increasing bt volume
            // Sending value: 6
            bt_vol_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                            mConnectedThread.write("6");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Decreasing bt volume
            // Sending value: 7
            bt_vol_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        mConnectedThread.write("7");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Increasing amp bass
            // Sending value: 8
            bass_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        mConnectedThread.write("8");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Decreasing bass
            // Sending value: 9
            bass_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        mConnectedThread.write("9");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Increasing amp volume
            // Sending value: u
            amp_vol_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        mConnectedThread.write("u");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Decreasing amp volume
            // Sending value: d
            amp_vol_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        mConnectedThread.write("d");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Play or pause
            // Sending value: p
            play_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        mConnectedThread.write("p");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Subwoofer power control
            // On : 1
            // Off : 0
            sw_power_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        if (isChecked) {
                            mConnectedThread.write("1");
                            Toast.makeText(getApplicationContext(),
                                    "AMP Power On", Toast.LENGTH_SHORT).show();


                        } else {
                            mConnectedThread.write("0");
                            Toast.makeText(getApplicationContext(),
                                    "AMP Power Off", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Bluetooth interface power control
            // On : 2
            // Off : 3
            bt_sw_power_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        if (isChecked) {
                            mConnectedThread.write("2");
                            Toast.makeText(getApplicationContext(),
                                    "BT Power On", Toast.LENGTH_SHORT).show();


                        } else {
                            mConnectedThread.write("3");
                            Toast.makeText(getApplicationContext(),
                                    "BT Power Off", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //ANC control
            // On : 4
            // Off : 5
            anc_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mConnectedThread != null) //First check to make sure thread created
                    {
                        if (isChecked) {
                            mConnectedThread.write("4");
                            Toast.makeText(getApplicationContext(),
                                    "ANC On", Toast.LENGTH_SHORT).show();


                        } else {
                            mConnectedThread.write("5");
                            Toast.makeText(getApplicationContext(),
                                    "ANC Off", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        private void try_connecting(){
            if (!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }
            mBluetoothStatus.setText("Connecting...");
            final String address = "24:62:AB:FE:27:22";
            final String name = "Bluetooth_Bhavanam";
            new Thread() {
                public void run() {
                    boolean fail = false;
                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
            // Check which request we're responding to
            super.onActivityResult(requestCode, resultCode, Data);
            if (requestCode == REQUEST_ENABLE_BT) {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    // The user picked a contact.
                    // The Intent's data Uri identifies which contact was selected.
                    mBluetoothStatus.setText("Enabled");
                } else
                    mBluetoothStatus.setText("Disabled");
            }
        }

        private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
            return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
            //creates secure outgoing connection with BT device using UUID
        }
        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;
            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;
                // Get the input and output streams, using temp objects because
                // member streams are final
                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                }
                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }
            public void run() {
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()
                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = mmInStream.available();
                        if (bytes != 0) {
                            SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                            bytes = mmInStream.available(); // how many bytes are ready to be read?
                            bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                            mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                    .sendToTarget(); // Send the obtained bytes to the UI activity
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
            /* Call this from the main activity to send data to the remote device */
            public void write(String input) {
                byte[] bytes = input.getBytes();           //converts entered String into bytes
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) {
                }
            }
            /* Call this from the main activity to shutdown the connection */
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }