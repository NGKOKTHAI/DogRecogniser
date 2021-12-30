package com.example.dogrecogniser;

//to retrieve data from URL using HTTP URL connection and file handling methods


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class DownloadUrl {

    public String readUrl(String myUrl) throws IOException
    {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;


        try {
            //create URL
            URL url = new URL(myUrl);
            //open connection
            urlConnection = (HttpURLConnection) url.openConnection();
            //connected
            urlConnection.connect();

            //read data from URL
            inputStream = urlConnection.getInputStream();
            //create object buffered reader and pass
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            //read each line
            String line = "";
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            //convert to stringbuffer to string
            data = sb.toString();
            br.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //whatever in finally will be executed
        finally {
            inputStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
}
