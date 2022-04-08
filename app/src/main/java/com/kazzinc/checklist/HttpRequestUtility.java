package com.kazzinc.checklist;

import android.content.ContentValues;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpRequestUtility {
    public String RequestToServer(String queryURL, String requestMethod, ContentValues params) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        URL url = new URL(queryURL + (params != null ? getQuery(params) : ""));

        //Log.d("Alexey", "P12: url " + url);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(requestMethod);
        urlConnection.setReadTimeout(100000);
        urlConnection.setConnectTimeout(150000);
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        resultJson = buffer.toString();
        urlConnection.disconnect();

        return resultJson;
    }

    public String RequestToServer(String queryURL, Object bodyData) throws IOException {
        URL url = new URL(queryURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        StringBuffer resultJson = new StringBuffer();
        Gson gson = new Gson();

        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConnection.setRequestMethod("POST");
        urlConnection.setReadTimeout(100000);
        urlConnection.setConnectTimeout(150000);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
        String json = gson.toJson(bodyData);
        writer.write(json);
        writer.close();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                resultJson.append(line);
            }
            br.close();
        } catch (Exception ex) {
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            String line;
            while ((line = br.readLine()) != null) {
                resultJson.append(line);
            }
            br.close();
            ex.printStackTrace();
        }
        urlConnection.disconnect();

        return resultJson.toString();
    }

    //
    //
    //private methods

    private String getQuery(ContentValues params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> pair : params.valueSet())
        {
            if (first) {
                result.append("?");
                first = false;
            }
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }
}

