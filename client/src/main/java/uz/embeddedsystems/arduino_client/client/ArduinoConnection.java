package uz.embeddedsystems.arduino_client.client;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;


public class ArduinoConnection {
    private static final String PROTOCOL = "http://";
    private static final String TAG = "ArduinoConnection";
    private static ArduinoConnection instance = new ArduinoConnection();
    private Callback successfulSetCallback;
    private Callback serverBusyCallback;
    private Callback exceptionCallback;
    private Callback serverFetchCallback;

    private String serverResponse;
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

    public void fetchConfiguration(final String ipAddress, final String port) {
        retrofit = new Retrofit.Builder().baseUrl(PROTOCOL + ipAddress + ":" + port + "/").client(client).build();
        service = retrofit.create(ArduinoConnection.ConnectionInterface.class);
        final Call<ResponseBody> call = service.fetchConfiguration("true");
        call.enqueue(new retrofit.Callback<ResponseBody>() {
            @Override
            public void onResponse(final Response<ResponseBody> response, final Retrofit retrofit) {
                try {
                    serverResponse = new String(response.body().bytes());
                    fireCallback(serverFetchCallback, serverResponse);
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

    public void setConfiguration(final String blinds, final String temp) {
        final Call<ResponseBody> call = service.setConfiguration(blinds, temp);
        call.enqueue(new retrofit.Callback<ResponseBody>() {
            @Override
            public void onResponse(final Response<ResponseBody> response, final Retrofit retrofit) {
                try {
                    serverResponse = new String(response.body().bytes());
                    if (serverResponse.toUpperCase().equals("BUSY")) {
                        Log.e(TAG, "Response: server is busy!");
                        fireCallback(serverBusyCallback, serverResponse);
                    } else if (serverResponse.toUpperCase().equals("OK")) {
                        Log.e(TAG, serverResponse );
                        fireCallback(successfulSetCallback, serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    fireCallback(exceptionCallback, "IOException occurred !");
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                fireCallback(exceptionCallback, t.getMessage());
            }
        });
    }


public void setSuccessfulFetchCallback(final Callback fetchCallback) {
    this.serverFetchCallback = fetchCallback;
}
    public void setServerBusyCallback(final Callback busyCallback){
        this.serverBusyCallback = busyCallback;
    }

    public void setSuccessfulSetCallback(Callback successfulSetCallback) {
        this.successfulSetCallback = successfulSetCallback;
    }

    public void setExceptionCallback(Callback exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
    }

    private void fireCallback(Callback callback, final String message) {
        if (callback != null) {
                callback.execute(message);
        }
    }

    public interface Callback {
        void execute(final String message);
    }

    public interface ConnectionInterface {
        @GET("/")
        Call<ResponseBody> setConfiguration(@Query("blinds") String conf, @Query("temperature") String temp);
        @GET("/")
        Call<ResponseBody> fetchConfiguration(@Query("fetchconf") String fetch);
    }
}
