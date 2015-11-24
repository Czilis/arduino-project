package uz.embeddedsystems.arduino_Project;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyActivity extends Activity {

    Button button;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button) findViewById(R.id.dupa);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Task().doInBackground();
            }
        });

    }



    private class Task extends AsyncTask<Void,Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            dupa();
            return null;
        }
        public void dupa(){
            try {

                int portNum = 4445;

                Socket socket = new Socket("192.168.43.250", portNum);

                // Integer Object to send to Server.
                Integer num = new Integer(50);

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(num);

                String response = (String) in.readObject();

                System.out.println("Server message: " + response);

                Thread.currentThread().sleep(10000);

                out.writeObject("5550505");

                socket.close();
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}