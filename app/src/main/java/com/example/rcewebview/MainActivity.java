package com.example.rcewebview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.recaptcha.Recaptcha;
import com.google.android.recaptcha.RecaptchaAction;
import com.google.android.recaptcha.RecaptchaTasksClient;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String myToken = "myDefaultValue";

    /*
     * This might be a good key?: 6LflaTIrAAAAAFa5y8wwY_TX1G2ZcNs7p08jNMTT
     */
    final private String siteKey = "<<INSERT SITE KEY VALUE HERE>>";

    /*
     *    file:///android_asset/index.html
     */
    private String webViewUrl = "https://sdk-login-app-724756488068.us-central1.run.app/login.html";
    @Nullable
    public static RecaptchaTasksClient recaptchaTasksClient = null;



    private void initializeRecaptchaClient() {
        Recaptcha
            .getTasksClient(getApplication(), siteKey)
            .addOnSuccessListener(
            this,
            new OnSuccessListener<RecaptchaTasksClient>() {
                @Override
                public void onSuccess(RecaptchaTasksClient client) {
                    recaptchaTasksClient = client;
                    System.out.println("recaptchaTasksClient has been set");
                }
            })
            .addOnFailureListener(
            this,
            new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle communication errors ...
                    // See "Handle communication errors" section
                    System.out.println("recaptchaTasksClient error: "+e.toString());
                }
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeRecaptchaClient();
        WebView myWebView = (WebView) findViewById(R.id.webView1);
        WebSettings ws = myWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        // Create a Map using HashMap
        Map<String, String> headers = new HashMap<>();
        // Adding key-value pairs to the map
        headers.put("Hostname", "localhost");
        myWebView.loadUrl(webViewUrl,headers);
        myWebView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "app");
        Button button = (Button) findViewById(R.id.btn_reset);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myWebView.loadUrl(webViewUrl,headers);
            }
        });
    }

    /*
     * JavaScript Interface. Web code can access methods in here
     * (as long as they have the @JavascriptInterface annotation)
     */
    public class WebViewJavaScriptInterface{

        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context){
            this.context = context;
        }

        public void setMessage(String msg, boolean append){
            TextView tv2 = (TextView) findViewById(R.id.textView2);
            if(append) {
                tv2.setText(tv2.getText()+"\n"+msg);
            }
            else{
                tv2.setText(msg);
            }
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public String appGetToken(){
            return myToken;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void appMakeToken(){
            assert recaptchaTasksClient != null;
            recaptchaTasksClient
                    .executeTask(RecaptchaAction.LOGIN)
                    .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String token) {
                                try{
                                    WebView myWebView = (WebView) findViewById(R.id.webView1);
                                    myWebView.evaluateJavascript("setToken('"+token+"');",new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) { }
                                    });
                                    setMessage("Token returned",true);
                                }
                                catch(Exception e){
                                    System.out.println("onSuccess: "+e.toString());
                                }
                            }
                        }
                    )
                    .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("onFailureListener: "+e.toString());
                            }
                        }
                    );
            setMessage("Token made",true);
        }
    }
}
