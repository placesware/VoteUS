package com.placesware.voteus.models.api.interactors;

import android.util.Log;

import com.demo.ergobot.civicusdemo.models.VoterInfoResponse;
import com.demo.ergobot.civicusdemo.models.api.requests.CivicInfoRequest;
import com.demo.ergobot.civicusdemo.models.api.requests.RequestType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.demo.ergobot.civicusdemo.models.VoterInfoResponse;
//import com.demo.ergobot.civicusdemo.models.api.requests.CivicInfoRequest;
//import com.demo.ergobot.civicusdemo.models.api.requests.RequestType;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import javax.security.cert.CertificateException;
//import javax.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcvandehey on 3/22/16.
 */
public class CivicInfoInteractor extends BaseInteractor<VoterInfoResponse, CivicInfoInteractor.CivicInfoCallback> {
    private static final String TAG = CivicInfoInteractor.class.getSimpleName();

    @Override
    protected VoterInfoResponse doInBackground(RequestType... params) {
        VoterInfoResponse response = null;

        if (params.length > 0) {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new GsonBuilder().create();

            CivicInfoRequest civicInfoRequest = (CivicInfoRequest) params[0];
            Request request = new Request.Builder().url(civicInfoRequest.buildQueryString()).build();


/*            OkHttpClient.Builder clientBuilder = client.newBuilder().readTimeout(10, TimeUnit.SECONDS);


            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] cArrr = new X509Certificate[0];
                    return cArrr;
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain,
                                               final String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(final X509Certificate[] chain,
                                               final String authType) throws CertificateException {
                }
            }};

            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            clientBuilder.sslSocketFactory(sslContext.getSocketFactory());

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.d(TAG, "Trust Host :" + hostname);
                    return true;
                }
            };
            clientBuilder.hostnameVerifier( hostnameVerifier);


            Request request = new Request.Builder().url(civicInfoRequest.buildQueryString()).build();
*/
  //          Request request = clientBuilder.build();

            try {
                Response okHttpResponse = client.newCall(request).execute();
//                Response okHttpResponse = (clientBuilder.build().newCall(request)).execute();//client.newCall(request).execute();

                response = gson.fromJson(okHttpResponse.body().string(), VoterInfoResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Unexpected error in VoterInfoResponse Request");
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(VoterInfoResponse voterInfoResponseResponse) {
        super.onPostExecute(voterInfoResponseResponse);

        CivicInfoCallback callback = getCallback();

        if (callback != null) {
            callback.civicInfoResponse(voterInfoResponseResponse);
        }
    }

    public interface CivicInfoCallback {
        void civicInfoResponse(VoterInfoResponse response);
    }
}
