package com.calculatoristul.keyboard.network;

import android.content.Context;
import android.util.Log;
import com.calculatoristul.keyboard.model.MessagePayload;
import com.google.gson.Gson;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

import okhttp3.*;

public class TelemetryClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final String serverUrl;

    public TelemetryClient(Context context, String serverHost, int port) {
        this.serverUrl = "https://" + serverHost + ":" + port + "/api/v1/telemetry";
        this.gson = new Gson();
        this.client = buildClient(context);
    }

    private OkHttpClient buildClient(Context context) {
        try {

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate ca;
            try (InputStream caInput = context.getAssets().open("ca.crt")) {
                ca = (X509Certificate) cf.generateCertificate(caInput);
            }


            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            TrustManager[] trustManagers = tmf.getTrustManagers();
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("TLS setup failed", e);
        }
    }

    public void sendMessage(MessagePayload payload) {
        String jsonBody = gson.toJson(payload);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override 
            public void onFailure(Call call, java.io.IOException e) {}

            @Override 
            public void onResponse(Call call, Response response) {
                if (response != null) {
                    response.close();
                }
            }
        });
    }
}