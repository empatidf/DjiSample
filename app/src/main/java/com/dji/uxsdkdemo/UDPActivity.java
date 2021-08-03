package com.dji.uxsdkdemo;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dji.uxsdkdemo.model.LocationModel;

import dji.common.flightcontroller.FlightControllerState;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class UDPActivity extends AppCompatActivity {

    private String targetIp = "192.168.44.166";
    private int port = 65500;
    private int timeInterval = 500;

    private Handler customHandler;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;

    FlightControllerState flightControllerState;
    FlightController mFlightController;

    EditText editTextIP;
    EditText editTextPort;
    public TextView textViewStatus;
    public TextView textViewLatitude;
    public TextView textViewLongitude;
    public TextView textViewAltitude;
    Button buttonConnect;
    Button resetConnect;

    double droneLocationLat;
    double droneLocationLng;
    double droneLocationAlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }

        initUI();
        initActivity();
        registerOnClicks();
        initDrone();
    }

    private void initUI() {
        editTextIP = findViewById(R.id.edit_text_udp_ip);
        editTextPort = findViewById(R.id.edit_text_udp_port);
        textViewStatus = findViewById(R.id.text_view_udp_status);
        textViewLatitude = findViewById(R.id.text_view_latitude);
        textViewLongitude = findViewById(R.id.text_view_longitude);
        textViewAltitude = findViewById(R.id.text_view_altitude);
        buttonConnect = findViewById(R.id.button_udp_connect);
        resetConnect = findViewById(R.id.button_udp_reset);
    }

    private void initActivity() {
        flightControllerState = new FlightControllerState();
        udpClientHandler = new UdpClientHandler(this);
        customHandler = new android.os.Handler();
    }

    private void registerOnClicks() {
        buttonConnect.setOnClickListener(v -> customHandler.postDelayed(updateTimerThread, timeInterval));

        resetConnect.setOnClickListener(v -> {
            textViewStatus.setText("");
            customHandler.removeCallbacks(updateTimerThread);
        });
    }

    private void initDrone() {
        BaseProduct product = DemoApplication.getProductInstance();
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                mFlightController = ((Aircraft) product).getFlightController();
            }
        }

        if (mFlightController != null) {
            mFlightController.setStateCallback(djiFlightControllerCurrentState -> {
                droneLocationLat = djiFlightControllerCurrentState.getAircraftLocation().getLatitude();
                droneLocationLng = djiFlightControllerCurrentState.getAircraftLocation().getLongitude();
                droneLocationAlt = djiFlightControllerCurrentState.getAircraftLocation().getAltitude();
                updateTextViews();
            });
        }
    }

    private void updateTextViews() {
        runOnUiThread(() -> {
            textViewLatitude.setText(String.valueOf(droneLocationLat));
            textViewLongitude.setText(String.valueOf(droneLocationLng));
            textViewAltitude.setText(String.valueOf(droneLocationAlt));
        });
    }

    private void updateDroneLocation() {


        if (editTextIP.getText().toString() != null && editTextPort.getText().toString() != null) {
            if (!editTextPort.getText().toString().isEmpty() && !editTextIP.getText().toString().isEmpty()) {
                udpClientThread = new UdpClientThread(
                        editTextIP.getText().toString(),
                        Integer.parseInt(editTextPort.getText().toString()),
                        udpClientHandler,
                        new LocationModel(
                                droneLocationLat,
                                droneLocationLng,
                                droneLocationAlt
                        )
                );
                udpClientThread.start();
            }
        }
    }

    private final Runnable updateTimerThread = this::sendDataToUDP;

    private void sendDataToUDP() {
        if (editTextIP.getText().toString() != null && editTextPort.getText().toString() != null) {
            if (!editTextPort.getText().toString().isEmpty() && !editTextIP.getText().toString().isEmpty()) {
                udpClientThread = new UdpClientThread(
                        editTextIP.getText().toString(),
                        Integer.parseInt(editTextPort.getText().toString()),
                        udpClientHandler,
                        new LocationModel(
                                droneLocationLat,
                                droneLocationLng,
                                droneLocationAlt
                        )
                );
                udpClientThread.start();
                customHandler.postDelayed(updateTimerThread, timeInterval);
            }
        }
    }

    private void clientEnd(){
        udpClientThread = null;
    }

    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private UDPActivity parent;

        public UdpClientHandler(UDPActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_STATE:
                case UPDATE_MSG:
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

}