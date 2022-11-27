package com.example.seatreservation.Chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ChatThread extends Thread{
    android.os.Message msg;
    Handler handler;
    boolean isRun = true;

    Socket socket;
    PrintWriter pw;
    BufferedReader br;
    private String ip = "3.34.45.193";
    private int port = 8888;
    InetAddress serverAddr;

    String read;

    public ChatThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this){
            this.isRun = false;
        }
    }

//    public void run(){
//        //반복적으로 수행할 작업을 한다.
//        while(isRun){
//            handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
//            try{
//                Thread.sleep(10000); //10초씩 쉰다.
//            }catch (Exception e) {}
//        }
//    }

    public void run() {

        try {
            serverAddr = InetAddress.getByName(ip);
            socket = new Socket(serverAddr, port);
            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(isRun){
                read = br.readLine();

                if(read!=null){
                    Bundle data = new Bundle();
                    msg = new Message();
                    data.putString("msg", read);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
