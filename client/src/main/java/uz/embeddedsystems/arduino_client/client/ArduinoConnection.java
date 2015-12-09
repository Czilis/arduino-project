package uz.embeddedsystems.arduino_client.client;

import android.util.Log;

import java.io.*;
import java.net.Socket;

/**
 * Created by michal on 29.11.15.
 */
public class ArduinoConnection {

    private static ArduinoConnection instance = new ArduinoConnection();

    private Socket connection = null;
    private String bindsList = "";

    private ArduinoConnection() {
    }

    public static ArduinoConnection getInstance() {
        return instance;
    }

    public boolean connect(String ip_address, String port) throws IOException {
        if (connection == null) {
            connection = new Socket(ip_address, Integer.parseInt(port));
            readBindsConfiguration();
            return true;
        }
        return false;
    }

    private void readBindsConfiguration() throws IOException {
        final Reader reader = new InputStreamReader(connection.getInputStream());
        final BufferedReader stringReader = new BufferedReader(reader);
        final String data = stringReader.readLine();

        final char[] binds = data.toCharArray();
        bindsList = "";
        for (Character bind : binds) {
            bindsList = bindsList + bind;
        }
    }

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
                try {
                    sendBindsConfiguration(builder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public void setBindState(int bind, boolean state) {
        if (state)
            setBindState(bind, 'O');
        else
            setBindState(bind, 'Z');
    }

    private boolean isStateValid(char state) {
        char st = Character.toUpperCase(state);
        if (st == 'Z' || st == 'O')
            return true;
        return false;
    }

    private void sendBindsConfiguration(String binds) throws IOException {
        Writer writer = new OutputStreamWriter(connection.getOutputStream());
        BufferedWriter stringWriter = new BufferedWriter(writer);

        stringWriter.write(binds + '\n');
        stringWriter.flush();
    }

    public boolean isConneced() {
        if (connection != null)
            if (connection.isConnected())
                if (!connection.isClosed())
                    return true;
        return false;
    }

    public boolean disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                return true;
            } catch (IOException e) {
                Log.e("Networking error", e.getStackTrace().toString());
            }
        }
        return false;
    }

}
