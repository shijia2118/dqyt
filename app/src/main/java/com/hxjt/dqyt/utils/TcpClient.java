package com.hxjt.dqyt.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hxjt.dqyt.app.App;
import com.hxjt.dqyt.app.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClient {
    private static TcpClient instance;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Thread messageReceiverThread;
    private boolean isConnected = false; // connection status flag

    private ConnectionListener connectionListener;
    private DataReceivedListener dataReceivedListener;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    /**
     * 消息接收监听器
     */
    public interface DataReceivedListener {
        void onDataReceived(String data);
    }

    /**
     * 连接状态监听器
     */
    public interface ConnectionListener {
        void onConnectionStatusChanged(boolean isConnected);
    }

    /**
     * 设置监听消息的方法
     * @param listener
     */
    public void setDataReceivedListener(DataReceivedListener listener) {
        dataReceivedListener = listener;
    }

    /**
     * 设置监听连接状态的方法
     * @param listener
     */
    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    private void onDataReceived(String data) {
        if (dataReceivedListener != null) {
            dataReceivedListener.onDataReceived(data);
        }
    }

    // Singleton instance creation
    public static synchronized TcpClient getInstance() {
        if (instance == null) {
            instance = new TcpClient();
        }
        return instance;
    }

    // Get connection status
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 接收服务端发送的Tcp消息
     *
     */
    private void messageReceiver() {
        Handler handler = new Handler(Looper.getMainLooper());
        StringBuilder buffer = new StringBuilder();
        char[] charBuffer = new char[1024*30];
        if(reader == null) return;

        try {
            while (!Thread.currentThread().isInterrupted()) {
                int bytesRead = reader.read(charBuffer);
                if (bytesRead == -1) {
                    Log.d("TcpClient", "continue");
                    isConnected = false;
                    notifyConnectionStatusChanged(false);
                    break;
                }
                buffer.setLength(0);
                buffer.append(charBuffer, 0, bytesRead);
                String receivedData = buffer.toString();
                handler.post(() -> {
                    Log.d("TcpClient", "buffer : " + receivedData);
                    onDataReceived(receivedData);
                });
            }
        } catch (IOException e) {
            Log.e("TcpClient", "接收消息: " + e.getMessage());
        }
    }

    /**
     * 连接TCP服务端
     */
    public void connectToServer() {
        executorService.execute(() -> {
            try {
                String ip = SPUtil.getString(Constants.IP_ADDRESS,"");
                String port = SPUtil.getString(Constants.PORT,"");
                if(TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)){
                    ip = Constants.DEFAULT_IP_ADDRESS;
                    port = Constants.DEFAULT_PORT;
                }
                socket = new Socket(ip, Integer.parseInt(port));
                socket.setKeepAlive(true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                new Handler(Looper.getMainLooper()).post(() -> {
                    isConnected = true;
                    notifyConnectionStatusChanged(true);
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    isConnected = false;
                    notifyConnectionStatusChanged(false);
                    ToastUtil.s("TCP连接失败");
                });
            }
        });
    }

    /**
     * 客户端主动向服务端发送消息
     * @param message
     */
    public void sendMessage(String message) {
        executorService.execute(() -> {
            try {
                writer.write(message);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 开启一个线程，用于接收消息
     */
    public void startMessageReceiver() {
        messageReceiverThread = new Thread(this::messageReceiver);
        messageReceiverThread.start();
    }

    /**
     * 关闭消息线程
     */
    public void stopMessageReceiver() {
        if (messageReceiverThread != null) {
            messageReceiverThread.interrupt();
        }
    }

    /**
     * 关闭TCP
     */
    public void close() {
        executorService.execute(() -> {
            try {
                if (socket != null) {
                    socket.close();
                }
                stopMessageReceiver();
                isConnected = false;
                notifyConnectionStatusChanged(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void notifyConnectionStatusChanged(boolean isConnected) {
        if (connectionListener != null) {
            connectionListener.onConnectionStatusChanged(isConnected);
        }
    }

    /**
     * 移除连接状态监听器
     */
    public void removeConnectionListener() {
        this.connectionListener = null;
    }

    public void removeDataReceiveListener() {
        this.dataReceivedListener = null;
    }


}
