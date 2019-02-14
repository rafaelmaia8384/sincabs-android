package br.com.sincabs.appsincabs;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public abstract class SincabsResponse extends AsyncHttpResponseHandler {

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

        try {

            Thread.sleep(500);
        }
        catch (Exception e) { }

        super.onPreProcessResponse(instance, response);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        String error = SincabsServer.getResponseError(responseBody);

        if (error.equals("0")) {

            onResponseNoError(SincabsServer.getResponseMessage(responseBody), SincabsServer.getResponseExtra(responseBody));
        }
        else if (error.equals("1") || error.equals("2")) {

            onResponseError(SincabsServer.getResponseMessage(responseBody));
        }
        else {

            onResponseError("Não foi possível conectar com o servidor.");

            //para debugar!
            //onResponseError(SincabsServer.decryptString(responseBody));
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        onNoResponse("Não foi possível conectar com o servidor.");
    }

    @Override
    public void onFinish() {

        onPostResponse();

        super.onFinish();
    }

    abstract void onResponseNoError(String msg, JSONObject extra);
    abstract void onResponseError(String error);
    abstract void onNoResponse(String error);
    abstract void onPostResponse();
}