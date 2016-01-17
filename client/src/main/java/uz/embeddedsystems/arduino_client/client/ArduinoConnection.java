package uz.embeddedsystems.arduino_client.client;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

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
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;


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
    final OkHttpClient client = new OkHttpClient();
    Retrofit retrofit;
    ArduinoConnection.ConnectionInterface service;

    private ArduinoConnection() {
        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.setReadTimeout(40, TimeUnit.SECONDS);
    }

    public static ArduinoConnection getInstance() {
        return instance;
    }

    public void fetchServerConfiguration(final String ipAddress, final String port) {
        retrofit = new Retrofit.Builder().baseUrl(PROTOCOL + ipAddress + ":" + port + "/").client(client).build();
        service = retrofit.create(ArduinoConnection.ConnectionInterface.class);
        final Call<ResponseBody> call = service.fetchConfiguration("true");
        call.enqueue(new retrofit.Callback<ResponseBody>() {
            @Override
            public void onResponse(final Response<ResponseBody> response, final Retrofit retrofit) {
                try {
                    serverResponse = new String(response.body().bytes());
                    fireCallback(connectedCallback, serverResponse);

                } catch (IOException e) {
                    fireCallback(exceptionCallback, "IOException occurred !");
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                fireCallback(exceptionCallback, t.getMessage());
            }
        });
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
                    final Retrofit report = new Retrofit.Builder().baseUrl(website).build();

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

    public interface ConnectionInterface {
        @GET("servlet")
        Call<ResponseBody> setBlinds(@Query("blinds") String conf);
        @GET("servlet")
        Call<ResponseBody> fetchConfiguration(@Query("fetchconf") String fetch);
    }
}
