package com.touwolf.plugin.idea.depschecker.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestClient
{
    private static final Gson GSON = new GsonBuilder().create();

    @Nullable
    public static <T> T get(@NotNull String urlStr, @NotNull Class<T> cls)
    {
        try
        {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode != 200)
            {
                return null;
            }
            String strResponse = readResponse(conn.getInputStream());
            return GSON.fromJson(strResponse, cls);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    @NotNull
    private static String readResponse(InputStream inputStream) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
