/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.angelpina.physiocare.Services;

import edu.angelpina.physiocare.Models.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

public class ServiceResponse
{
    private static String token = null;
    private static User actualUser = null;
    public static final String SERVER = "https://angelpg.es/physio/api";
    //public static final String SERVER = "http://localhost:8080";

    public static void setToken(String token) {
        ServiceResponse.token = token;
    }
    public static void removeToken() {
        ServiceResponse.token = null;
    }
    public static boolean isToken() {
        return token != null && !token.isEmpty();
    }

    public static void setActualUser(User user) {
        actualUser = user;
    }
    public static String getUserRol() {
        return actualUser.getRol();
    }

    // Get charset encoding (UTF-8, ISO,...)
    public static String getCharset(String contentType) {
        for (String param : contentType.replace(" ", "").split(";")) {
            if (param.startsWith("charset=")) {
                return param.split("=", 2)[1];
            }
        }

        return null; // Probably binary content
    }

    public static String getResponse(String url, String data, String method) {
        BufferedReader bufInput = null;
        StringJoiner result = new StringJoiner("\n");
        try {
            URL urlConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlConn.openConnection();
            conn.setReadTimeout(20000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(method);

            //conn.setRequestProperty("Host", "localhost");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            conn.setRequestProperty("Accept-Language", "es-ES,es;q=0.8");
            conn.setRequestProperty("Accept-Charset", "UTF-8"); 
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
            
            // If set, send the authentication token
            if(token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }
            
            if (data != null) {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                conn.setDoOutput(true);
                //Send request
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(data.getBytes());
                wr.flush();
                wr.close();
            }

            String charset = getCharset(conn.getHeaderField("Content-Type"));

            if (charset != null) {
                int responseCode = conn.getResponseCode();
                InputStream input;

                if (responseCode >= 400) {
                    System.out.println("Error: " + responseCode);
                    input = conn.getErrorStream();
                } else {
                    input = conn.getInputStream();
                }

                if ("gzip".equals(conn.getContentEncoding())) {
                    input = new GZIPInputStream(input);
                }

                bufInput = new BufferedReader(
                        new InputStreamReader(input));

                String line;
                while((line = bufInput.readLine()) != null) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufInput != null) {
                try {
                    bufInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toString();
    }

    public static CompletableFuture<String>
    getResponseAsync(String url, String data, String method) {
        return CompletableFuture.supplyAsync(() -> {
            String res = getResponse(url, data, method);
            System.out.println("Response: " + res);
            return res;
        });
    }
}
