package com.dji.uxsdkdemo;

import android.os.Message;
import android.util.Base64;

import com.dji.uxsdkdemo.model.LocationModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClientThread extends Thread{

    String dstAddress;
    int dstPort;
    UDPActivity.UdpClientHandler handler;
    LocationModel locationModel;

    DatagramSocket socket;
    InetAddress address;

    public UdpClientThread(String addr, int port, UDPActivity.UdpClientHandler handler, LocationModel locationModel) {
        super();
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
        this.locationModel = locationModel;
    }

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        UDPActivity.UdpClientHandler.UPDATE_STATE, state));
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(dstAddress);

            byte[] sendData;

            String latitudeText = String.valueOf(locationModel.getLatitude());
            String longitudeText = String.valueOf(locationModel.getLongitude());
            String altitudeText = String.valueOf(locationModel.getAltitude());

            JSONObject json = new JSONObject();

//            if (latitudeText.equals("0.0")) {
//            }
//            if (longitudeText.equals("0.0")) {
//            }
//            if (altitudeText.equals("0.0")) {
//            }
            json.put("latitude", latitudeText);
            json.put("longitude", longitudeText);
            json.put("altitude", altitudeText);

            String body = Base64.encodeToString(json.toString().getBytes("UTF-8"), Base64.NO_WRAP);

            sendData = json.toString().getBytes();

            DatagramPacket packet =
                    new DatagramPacket(body.getBytes(), body.length(), address, dstPort);
            socket.send(packet);

            sendState("connected");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();
                handler.sendEmptyMessage(UDPActivity.UdpClientHandler.UPDATE_END);
            }
        }

    }
}
