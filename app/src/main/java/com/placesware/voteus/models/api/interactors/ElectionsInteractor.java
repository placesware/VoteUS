package com.placesware.voteus.models.api.interactors;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.placesware.voteus.models.api.requests.ElectionsRequest;
import com.placesware.voteus.models.api.requests.RequestType;
import com.placesware.voteus.models.api.responses.ElectionsResponse;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by placesware-dev on 9/13/16.
 */
public class ElectionsInteractor extends BaseInteractor<ElectionsResponse, ElectionsInteractor.ElectionsCallback> {
    private static final String TAG = ElectionsInteractor.class.getSimpleName();

    @Override
    protected ElectionsResponse doInBackground(RequestType... params) {
        ElectionsResponse response = null;

        if (params.length > 0) {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new GsonBuilder().create();

            ElectionsRequest electionsRequest = (ElectionsRequest) params[0];
            Request request = new Request.Builder().url(electionsRequest.buildQueryString()).build();

            try {
                Response okHttpResponse = client.newCall(request).execute();

                response = gson.fromJson(okHttpResponse.body().string(), ElectionsResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Unexpected error in ElectionResponse Request");
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(ElectionsResponse electionsResponseResponse) {
        super.onPostExecute(electionsResponseResponse);

        ElectionsCallback callback = getCallback();

        if (callback != null) {
            callback.electionsResponse(electionsResponseResponse);
        }
    }

    public interface ElectionsCallback {
        void electionsResponse(ElectionsResponse response);
    }
}
