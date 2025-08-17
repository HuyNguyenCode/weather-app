package com.ninegroup.weather.api.client;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    //private static String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoREkwZ2hyVlJvaE5zVy1wSXpZeDBpT2lHMzNlWjJxV21sRk4wWGE1dWkwIn0.eyJleHAiOjE3MDIyMjc2NTIsImlhdCI6MTcwMjE0MTI1MiwianRpIjoiZWM4YzM5MTgtNmUwMy00OTBlLTkzMzYtOTU1ZWIzYjk2ZmU0IiwiaXNzIjoiaHR0cHM6Ly91aW90Lml4eGMuZGV2L2F1dGgvcmVhbG1zL21hc3RlciIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI0ZTNhNDQ5Ni0yZjE5LTQ4MTMtYmYwMC0wOTQwN2QxZWU4Y2IiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJvcGVucmVtb3RlIiwic2Vzc2lvbl9zdGF0ZSI6IjBiM2E4MDRjLTgzMmQtNGViOS05YzA0LWVkZjc3Mzk3ZmU0MSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly91aW90Lml4eGMuZGV2Il0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLW1hc3RlciIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJvcGVucmVtb3RlIjp7InJvbGVzIjpbInJlYWQ6bWFwIiwicmVhZDpydWxlcyIsInJlYWQ6aW5zaWdodHMiLCJyZWFkOmFzc2V0cyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMGIzYTgwNGMtODMyZC00ZWI5LTljMDQtZWRmNzczOTdmZTQxIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiRmlyc3QgTmFtZSBMYXN0IG5hbWUiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyIiwiZ2l2ZW5fbmFtZSI6IkZpcnN0IE5hbWUiLCJmYW1pbHlfbmFtZSI6Ikxhc3QgbmFtZSIsImVtYWlsIjoidXNlckBpeHhjLmRldiJ9.ThR4zql6YTPW4bcCDBYIfTK1EOc-ypDlVP3TV6o23CY8QHYpuAZhsZB251y3P7RMjoeqQk-hdNYLY6vA1flp1ntPoHCdrpzLe2lz6q_hyNyQP9D_kQEn0UUFQ7_ODv9uzHMxvY4_M_O4naGOwd5fhRb6lql_Ds8bQq-Rd2_g8yT1tWqlZ5pm_7wln1liRPkCKp1mzLPoBJexKujYLLEjrM423g6X3IzP0MVGufm52abC-KcX0MgEUgx01NYAqlZMl4PCVk2Ao5NikxrvTFf4rDIjywQ9zyRzcXt2vY_v9dZdl7H9nCgWp5BIh9AQ_okevLFLSqJFucVzhVu9hQd_qw";
    private static OkHttpClient getUnsafeOkHttpClient(String accessToken) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            //Log
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);

            //Bear token
            builder.addInterceptor(new Interceptor() {
                @NonNull
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build();
                    return chain.proceed(newRequest);
                }
            });


            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Retrofit getClientNoToken() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://uiot.ixxc.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        return retrofit;
    }

    public static Retrofit getClient(String accessToken) {

        OkHttpClient client = getUnsafeOkHttpClient(accessToken);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://103.126.161.199/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
