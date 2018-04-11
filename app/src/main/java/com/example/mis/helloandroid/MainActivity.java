package com.example.mis.helloandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void buttonConnectOnClick(View view)
    {
        // get the URL from the user
        TextView urlTextView = findViewById(R.id.editText);
        String urlString = urlTextView.getText().toString();

        new DownloadTask().execute(urlString);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String>
    {
        InputStream stream = null;
        HttpURLConnection connection = null;
        URL url = null;
        String result = null;

        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                stream = connection.getInputStream();
                result = readStream(stream, 5000);
            }
            catch (IOException e)
            {
                Log.e("Error", e.getLocalizedMessage(), e);
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress)
        {

        }

        @Override
        protected void onPostExecute(String result)
        {
            TextView tv = findViewById(R.id.textViewResult);
            tv.setText(result);
        }
    }

    public String readStream(InputStream stream, int maxReadSize)
            throws IOException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }


/*    public void helloWorld(View view) {
        Toast toast = Toast.makeText(MainActivity.this, "hey there", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0 ,0);
        toast.show();
    }*/
}
