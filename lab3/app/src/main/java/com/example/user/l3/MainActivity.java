package com.example.user.l3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    RequestQueue queue;
    String url;
    private Context context;
    private EditText fromField;
    private EditText toField;
    private EditText messageField;
    private ImageButton captchaImageButton;
    private EditText captchaField;
    private Button sendButton;
    private TextView errorText;

    private String captcha_sid;
    private String captcha_token;
    private String form_build_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        fromField = findViewById(R.id.from);
        toField = findViewById(R.id.to);
        messageField = findViewById(R.id.message);
        captchaImageButton = findViewById(R.id.captchaImage);
        captchaField = findViewById(R.id.captchaField);
        sendButton = findViewById(R.id.sendButton);
        errorText = findViewById(R.id.errorText);

        url = "https://www.moldcell.md";

        handleSSLHandshake();

        queue = Volley.newRequestQueue(MainActivity.this);
        manageGETrequest();

        sendButton.setOnClickListener(new sendButtonListener());
        captchaImageButton.setOnClickListener(new captchaImmageButtonListener());
    }

    private void manageGETrequest() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/rom/sendsms",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseHTML(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERRROR " + error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseHTML(String html) {
        Document doc = Jsoup.parse(html);
        Element img = doc.select("img[title=Imagine CAPTCHA]").first();
        Picasso.with(context).load(url + img.attr("src")).fit().into(captchaImageButton);

        Element c_sid = doc.select("input[name=captcha_sid]").first();
        Element c_token = doc.select("input[name=captcha_token]").first();
        Element f_build_id = doc.select("input[name=form_build_id]").last();

        captcha_sid = c_sid.attr("value");
        captcha_token = c_token.attr("value");
        form_build_id = f_build_id.attr("value");
    }

    private class sendButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/rom/mobile/ajax/captcha/validate?text=" + captchaField.getText().toString() + "&csid=" + captcha_sid,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            fieldsValidation(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERRROR " + error.toString());
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    private void fieldsValidation(String cresp) {
        String error = "";
        if (!toField.getText().toString().matches("[1-9][0-9]{7}"))
            error += "\n* The 'To' field should contain 8 digits, without leading 0";
        if (!fromField.getText().toString().matches("[a-z A-Z]{1,9}"))
            error += "\n* The 'From' field should contain only latin letters";
        if (!messageField.getText().toString().matches(".{1,140}"))
            error += "\n* The 'Message' field should contain between 1 and 140 characters";
        if (!cresp.equals("2"))
            error += "\n* Wrong CAPTCHA";
        errorText.setText(error);

        if (error.length() > 0)
            return;
        managePOSTrequest();
    }

    private class captchaImmageButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            captchaImageButton.setImageDrawable(getDrawable(R.mipmap.ic_launcher));
            manageGETrequest();
        }
    }

    private void managePOSTrequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/rom/sendsms",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("POST: " + response);
                        Toast.makeText(context, "Message sent successfully", Toast.LENGTH_LONG).show();
                        captchaField.setText("");
                        manageGETrequest();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("POST ERRROR: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phone", toField.getText().toString());
                params.put("name", fromField.getText().toString());
                params.put("message", messageField.getText().toString());
                params.put("captcha_sid", captcha_sid);
                params.put("captcha_token", captcha_token);
                params.put("captcha_response", captchaField.getText().toString());
                params.put("conditions", "1");
                params.put("op", "");
                params.put("form_build_id", form_build_id);
                params.put("form_id", "websms_main_form");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
