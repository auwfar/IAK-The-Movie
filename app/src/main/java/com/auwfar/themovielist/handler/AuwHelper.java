package com.auwfar.themovielist.handler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auwfar.themovielist.handler.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Auwfar on 24-Jan-17.
 */

public class AuwHelper {
    public static ProgressDialog pDialog;
    public static int POST = Request.Method.POST;
    public static int GET = Request.Method.GET;
    public static int PUT = Request.Method.PUT;

    public static void request(final Context context, final VolleyCallback callback, String url, int method, final HashMap param, boolean dialog) {
        String tag_string_req = "req_ajax";

        actDialog(context);
        if (dialog) {
            pDialog.setMessage("Loading...");
            showDialog();
        }

        StringRequest strReq = new StringRequest(method, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("ajax", "Response: " + response.toString());
                hideDialog();
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ajax", "Error: " + error.getMessage());
                if (context != null) {
                    Toast.makeText(context, "Tidak Dapat Terhubung", Toast.LENGTH_LONG).show();
                }
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                Set keys = param.keySet();

                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    String key = (String) i.next();
                    String value = (String) param.get(key);

                    params.put(key, value);
                }

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    public static void actDialog(Context context) {
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
    }

    public static void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public static void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
