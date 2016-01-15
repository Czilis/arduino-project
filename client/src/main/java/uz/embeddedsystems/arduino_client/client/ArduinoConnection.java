package uz.embeddedsystems.arduino_client.client;

import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by michal on 29.11.15.
 */
public class ArduinoConnection {

    private static ArduinoConnection instance = new ArduinoConnection();
    private Callback connectedCallback;
    private Callback disconnectedCallback;
    private Callback stateSetCallback;
    private Callback stateNotSetCallback;

//    private Socket connection = null;
    private String bindsList = "";

    private ArduinoConnection() {
    }

    public static ArduinoConnection getInstance() {
        return instance;
    }

//    public final void connect(final String ipAddress, final String port) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    if (connection == null) {
//                        connection = new Socket(ipAddress, Integer.parseInt(port));
//
//                        bindsList = readBindsConfiguration();
//                        fireCallback(connectedCallback);
//                    }
//
//                } catch (Exception e) {
//                    logException(e);
//                }
//            }
//        }).start();
////    }

    public int getBindsCount() {
        return bindsList.length();
    }

    public char getBindsState(int bind) {
        if ((bind - 1) > 0 && bindsList.length() >= bind) {
            return bindsList.charAt(bind - 1);
        }
        return ' ';
    }

    public String getBindsStates() {
        return bindsList;
    }

    public void setBindState(int bind, char state) {
        if (isStateValid(state))
            if ((bind) > 0 && bindsList.length() >= bind) {
                StringBuilder builder = new StringBuilder(bindsList);
                builder.setCharAt(bind - 1, Character.toUpperCase(state));
                builder.toString();
//                sendBindsConfiguration(builder.toString());
                bindsList = builder.toString();
            }
    }

    public void setBindState(int bind, boolean state) {
        if (state)
            setBindState(bind, 'O');
        else
            setBindState(bind, 'Z');
    }

    public void setConnectedCallback(Callback connectedCallback) {
        this.connectedCallback = connectedCallback;
    }

    public void setDisconnectedCallback(Callback disconnectedCallback) {
        this.disconnectedCallback = disconnectedCallback;
    }

    public void setStateSetCallback(Callback stateSetCallback) {
        this.stateSetCallback = stateSetCallback;
    }

    public void setStateNotSetCallback(Callback stateNotSetCallback) {
        this.stateNotSetCallback = stateNotSetCallback;
    }

//    public boolean isConneced() {
//        if (connection != null)
//            if (connection.isConnected())
//                if (!connection.isClosed())
//                    return true;
//
//        disconnect();
//        return false;
//    }

//    public boolean disconnect() {
//        if (connection != null) {
//            try {
//                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
//                out.write("END" + '\n');
//                out.flush();
//
//                connection.close();
//                connection = null;
//                fireCallback(disconnectedCallback);
//                return true;
//            } catch (IOException e) {
//                logException(e);
//            }
//        }
//        return false;
//    }

    private boolean isStateValid(char state) {
        char st = Character.toUpperCase(state);
        if (st == 'Z' || st == 'O')
            return true;
        return false;
    }

//    private String readBindsConfiguration() throws IOException {
//        final String data = readString();
//
//        final char[] binds = data.toCharArray();
//        String bindsList = "";
//        for (Character bind : binds) {
//            bindsList = bindsList + bind;
//        }
//
//        return bindsList;
//    }

//    private void sendBindsConfiguration(final String binds) {
//        if (isConneced()) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                        writeString(binds + '\n');
//
//                        String confirmation = readBindsConfiguration();
//                        if (confirmation.contains("K")) {
//                            fireCallback(stateSetCallback);
//                        } else {
//                            fireCallback(stateNotSetCallback);
//                        }
//                    } catch (IOException e) {
//                        logException(e);
//                        fireCallback(stateNotSetCallback);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }
//    }

//    private String readString() throws IOException {
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        return in.readLine();
//    }

//    private void writeString(final String data) throws IOException {
//        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
//        out.write(data);
//        out.flush();
//    }

    private void fireCallback(Callback callback) {
        if (callback != null) {
            callback.execute();
        }
    }

    private void logException(Exception e) {
        Log.e("ArduinoConnection", "Exception occurred!", e);
    }

    public interface Callback {
        void execute();
    }
}
