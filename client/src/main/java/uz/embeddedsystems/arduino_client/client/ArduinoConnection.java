package uz.embeddedsystems.arduino_client.client;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by michal on 29.11.15.
 */
public class ArduinoConnection {
    private static final String PROTOCOL = "http://";
    private static ArduinoConnection instance = new ArduinoConnection();
    private Callback connectedCallback;
    private Callback disconnectedCallback;
    private Callback stateSetCallback;
    private Callback stateNotSetCallback;
    private Callback exceptionCallback;
    private String serverResponse;
    private String bindsList = "";

    private ArduinoConnection() {
    }

    public static ArduinoConnection getInstance() {
        return instance;
    }

    public final void connect(final String ipAddress, final String port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String address = PROTOCOL + ipAddress + ":" + port + "/wtf";
                    fetchServer(address);
                } catch (Exception e) {
                    logException(e);
                }
            }
        }).start();
    }

    private void fetchServer(final String website) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URI uri = new URI(website);
                    final HttpClient httpClient = new DefaultHttpClient();
                    final HttpGet getRequest = new HttpGet();
                    getRequest.setURI(uri);
                    final HttpResponse httpResponse = httpClient.execute(getRequest);
                    final InputStream content = httpResponse.getEntity().getContent();
                    final BufferedReader in = new BufferedReader(new InputStreamReader(content));
                    serverResponse = in.readLine();
                    Log.e("Odp od serwera: ", serverResponse);
                    fireCallback(connectedCallback);
                    fireCallback(exceptionCallback, serverResponse);
                    content.close();
                } catch (ClientProtocolException e) {
                    // HTTP error
                    fireCallback(exceptionCallback, "HTTP error occurred !");
                   logException(e);
                } catch (IOException e) {
                    // IO error
                    fireCallback(exceptionCallback, "IO exception occurred !");
                    logException(e);
                } catch (URISyntaxException e) {
                    fireCallback(exceptionCallback, "URI error occurred !");
                    logException(e);
                }
            }
        }).start();
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
    public void setExceptionCallback(Callback exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
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
    private void fireCallback(final Callback callback) {
        fireCallback(callback, null);
    }
    private void fireCallback(Callback callback, final String message) {
        if (callback != null) {
            if (null == message || message.isEmpty()) {
                callback.execute();
            } else {
                callback.execute(message);
            }
        }
    }

    private void logException(Exception e) {
        Log.e("ArduinoConnection", e.getMessage(), e);
    }

    public interface Callback {
        void execute();
        void execute(final String message);
    }
}
