package com.example.mis.helloandroid;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Next two lines to make TextView scrollable by: www.viralandroid.com/2015/10/how-to-make-scrollable-textview-in-android.html
        TextView tv = findViewById(R.id.textViewResult);
        tv.setMovementMethod(new ScrollingMovementMethod());
    }

    public void buttonConnectOnClick(View view)
    {
        // get the URL from the user
        TextView urlTextView = findViewById(R.id.editText);
        String urlString = urlTextView.getText().toString();

        // 2. getMimeType method is taken from: https://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android/31691791
        String mimeType = getMimeType(urlString);

        if (mimeType == null)
        {
            new DownloadTextTask().execute(urlString);
/*            Toast.makeText(MainActivity.this, "Unable to connect to this URL! Message: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "URL not formatted correctly! Message: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "Input Output Exception occurred. Message: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();*/
        }
        else if (mimeType.contains("image"))
        {
            new DownloadImageTask().execute(urlString);
        }
        else
        {
            // alert the user that the type is not supported
            Toast.makeText(MainActivity.this, "Only text and Images are supported!\nPlease enter a URL which contains images or plain text.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        InputStream stream = null;
        HttpURLConnection connection = null;
        URL url = null;
        Bitmap result = null;

        // 3. The code to show a progress dialog is taken from here: https://stackoverflow.com/questions/11752961/how-to-show-a-progress-spinner-in-android-when-doinbackground-is-being-execut
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute()
        {
            dialog.setMessage("Loading!");
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings)
        {
            try
            {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(6000);
                connection.connect();
                stream = connection.getInputStream();
                result = readImageStream(stream);
            }
            catch (ConnectException e)
            {
                Log.e("ConnectException", e.getLocalizedMessage(), e);
            }
            catch (MalformedURLException e)
            {
                Log.e("MalformedURLException", e.getLocalizedMessage(), e);
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
        protected void onPostExecute(Bitmap result)
        {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // Set the image
            ImageView iv = findViewById(R.id.imageViewResult);
            iv.setImageBitmap(result);
        }
    }

    private class DownloadTextTask extends AsyncTask<String, Void, String>
    {
        InputStream stream = null;
        HttpURLConnection connection = null;
        URL url = null;
        String result = null;

        // 3. The code to show a progress dialog is taken from here: https://stackoverflow.com/questions/11752961/how-to-show-a-progress-spinner-in-android-when-doinbackground-is-being-execut
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute()
        {
            dialog.setMessage("Loading!");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                stream = connection.getInputStream();
                result = readStream(stream, 500);
            }
            catch (ConnectException e)
            {
                Log.e("ConnectException", e.getLocalizedMessage(), e);
            }
            catch (MalformedURLException e)
            {
                Log.e("MalformedURLException", e.getLocalizedMessage(), e);
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
        protected void onPostExecute(String result)
        {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            TextView tv = findViewById(R.id.textViewResult);
            tv.setText(result);
        }
    }

    private Bitmap readImageStream(InputStream stream) throws IOException
    {
        return BitmapFactory.decodeStream(stream);
    }

    private String readStream(InputStream stream, int maxReadSize)
            throws IOException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0)
        {
            if (readSize > maxReadSize)
            {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}
