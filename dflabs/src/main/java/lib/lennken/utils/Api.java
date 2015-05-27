package lib.lennken.utils;


import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by caprinet on 10/7/14.
 */
public class Api<T,R>{

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TEXT_PLAIN
            = MediaType.parse("text/plain; charset=utf-8");
    public static final MediaType FORM
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private OnApiCallDownload callback;
    private OnApiPlainCallDownload callbackPlain;

    public Api(Context context){
        this.mContext = context;
    }


    public void get(final Class<R> type, String url,final OnApiCallDownload<R> callback){
        Request request = new Request.Builder().url(url).build();
        this.callback = callback;
        call(request, type);
    }

    public void getPlain(String url, OnApiPlainCallDownload callbackPlain){
        Request request = new Request.Builder().url(url).build();
        this.callbackPlain = callbackPlain;
    }

    public void postWithRequest(Request request, Class<R> responseType, OnApiCallDownload<R> callback){
        this.callback = callback;
        call(request, responseType);
    }

    public void postPlainWithRequest(Request request, OnApiPlainCallDownload callback){
        this.callbackPlain = callback;
        callPlain(request);
    }

    public void post(Class<T> typeRequest, T objectRequest, String url, Class<R> responseType, OnApiCallDownload<R> callback){
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(objectRequest, typeRequest));
        this.callback = callback;
        Request request = new Request.Builder().url(url).post(body).build();
        call(request, responseType);
    }

    public void postWithParams(Map<String, String> params,String url, Class<R> responseType, OnApiCallDownload<R> callback){
        StringBuilder formbuilder = new StringBuilder();
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            formbuilder.append(e.getKey()).append("=").append(e.getValue()).append("&");
        }
        RequestBody body = RequestBody.create(FORM, formbuilder.toString());
        this.callback = callback;
        Request request = new Request.Builder().url(url).post(body).build();
        call(request, responseType);
    }

    public void postPlain(Class<R> typeResponse, R objectResponse, String url, OnApiPlainCallDownload callback){
        this.callbackPlain = callback;
        RequestBody body = RequestBody.create(TEXT_PLAIN, new Gson().toJson(objectResponse, typeResponse));
        Request request = new Request.Builder().url(url).post(body).build();
        callPlain(request);
    }

    private void call(Request request,final Class<R> typeResponse){
        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(mContext.getMainLooper());
            @Override
            public void onFailure(Request request, final IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(callback != null)
                            callback.onError(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String responseStr = response.body().string();
                if(callback != null) {
                    try {

                        final R obj = new Gson().fromJson(responseStr, typeResponse);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(obj);
                            }
                        });
                    }catch (Exception e){
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(responseStr);
                            }
                        });
                    }

                }
            }
        });
    }



    private void callPlain(Request request){
        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(mContext.getMainLooper());
            @Override
            public void onFailure(Request request, final IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackPlain.onError(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String obj = response.body().string();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackPlain.onSuccess(obj);
                    }
                });
            }
        });
    }


    public interface OnApiCallDownload<R>{

        public void onSuccess(R object);
        public void onError(String error);

    }

    public interface OnApiPlainCallDownload{

        public void onSuccess(String response);
        public void onError(String error);

    }

}
