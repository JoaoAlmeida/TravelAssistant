package util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ReadPage {
    
    public static synchronized String getContent(String adress) throws Exception {
        URL url = new URL(adress);
        URLConnection urlConnection = url.openConnection();
        InputStream link = urlConnection.getInputStream();
        int c;
        String result = "";
        while ((c = link.read()) != -1) {
            result += (char) c;
        }

        if (urlConnection instanceof HttpURLConnection) {
            ((HttpURLConnection) urlConnection).disconnect();
        } else {
            link.close();
        }
        return result;
    }
}
