package com.connect4.thad.connect4_aicv;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class HttpClient {
    private String url;
    private HttpURLConnection con;
    private OutputStream os;

    private String delimiter = "--";
    private String boundary =  "SwA"+Long.toString(System.currentTimeMillis())+"SwA";

    public HttpClient(String url) {
        this.url = url;
    }

    public void connectForMultipart() throws Exception {
        con = (HttpURLConnection) ( new URL(url)).openConnection();
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        con.connect();
        os = con.getOutputStream();
    }

    public void addFilePart(String paramName, String fileName, byte[] data) throws Exception {
        os.write( (delimiter + boundary + "\r\n").getBytes());
        os.write( ("Content-Disposition: form-data; name=\"" + paramName +  "\"; filename=\"" + fileName + "\"\r\n"  ).getBytes());
        os.write( ("Content-Type: application/octet-stream\r\n"  ).getBytes());
        os.write( ("Content-Transfer-Encoding: binary\r\n"  ).getBytes());
        os.write("\r\n".getBytes());

        os.write(data);

        os.write("\r\n".getBytes());
    }

    public void finishMultipart() throws Exception {
        os.write( (delimiter + boundary + delimiter + "\r\n").getBytes());
    }

    public int getResponse() throws Exception {
        int res = con.getResponseCode();
        res -= 200;

        return res;

        /*InputStream is = con.getInputStream();
        Scanner scn = new Scanner(is);

        con.disconnect();

        int res = scn.nextInt();

        return res;*/

        /*byte[] b1 = new byte[1024];
        StringBuffer buffer = new StringBuffer();

        while ( is.read(b1) != -1)
            buffer.append(new String(b1));

        con.disconnect();

        return buffer.toString();*/
    }

}