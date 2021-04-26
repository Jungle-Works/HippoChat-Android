package com.hippo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hippo.BuildConfig;
import com.hippo.HippoColorConfig;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.eventbus.BusProvider;
import com.hippo.fragment.PaymentCancelDialog;
import com.hippo.helper.BusEvents;
import com.hippo.helper.FayeMessage;
import com.hippo.langs.Restring;
import com.hippo.model.HippoPayment;
import com.hippo.model.MakePayment;
import com.hippo.model.PaymentResponse;
import com.hippo.model.payment.AddedPaymentGateway;
import com.hippo.model.payment.PaymentListResponse;
import com.hippo.model.payment.PaymentUrl;
import com.hippo.model.payment.PrePaymentData;
import com.hippo.payment.RazorPayData;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.utils.HippoLog;
import com.hippo.utils.UniqueIMEIID;
import com.hippo.utils.loadingBox.ProgressWheel;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;

import faye.ConnectionManager;

import static android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
import static com.hippo.payment.PaymentConstants.PaymentValue.RAZORPAY;

/**
 * Created by gurmail on 2020-06-16.
 *
 * @author gurmail
 */
public class PrePaymentActivity extends FuguBaseActivity implements PaymentResultWithDataListener {

    private RelativeLayout rootToolbar;
    private ImageView ivBackBtn;
    private TextView tvToolbarName;
    private RelativeLayout loadingLayout;
    private ProgressWheel progressWheel;
    private TextView textView;

    private ProgressBar pbWebPageLoader;
    private WebView webView;

    private HippoColorConfig hippoColorConfig;
    private FuguChatActivity chatActivity;
    private PrePaymentData paymentData;

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        if(HippoConfig.getInstance().getPrePaymentCallBack() != null) {
            HippoConfig.getInstance().getPrePaymentCallBack().onPaymentSuccess();
        }
        onSuccessActivity();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        if(HippoConfig.getInstance().getPrePaymentCallBack() != null) {
            HippoConfig.getInstance().getPrePaymentCallBack().onPaymentfailed();
        }
        onFailureActivity();
    }

    public interface OnInputListener {
        void closeFragment();
    }

    public OnInputListener onInputListener;
    private AddedPaymentGateway paymentGateway;
    String url = "";
    private String channelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_webview);

        String data = getIntent().getStringExtra("data");
        paymentData = new Gson().fromJson(data, PrePaymentData.class);
        for(AddedPaymentGateway gateway : CommonData.getPaymentList()) {
            if(gateway.getGatewayId() == paymentData.getPayment_gateway_id()) {
                paymentGateway = gateway;
                break;
            }
        }

        HippoLog.e("URL", "URL = "+url);

        String title = Restring.getString(this, R.string.fugu_payment);

        rootToolbar = findViewById(R.id.my_toolbar);
        tvToolbarName = findViewById(R.id.tv_toolbar_name);
        ivBackBtn = findViewById(R.id.ivBackBtn);
        loadingLayout = findViewById(R.id.loadingLayout);
        progressWheel = findViewById(R.id.circle_progress);
        textView = findViewById(R.id.text);

        String text = Restring.getString(PrePaymentActivity.this, R.string.hippo_payment_loader);
        textView.setText(text);

        hippoColorConfig = CommonData.getColorConfig();
        rootToolbar.setBackgroundColor(hippoColorConfig.getHippoActionBarBg());
        tvToolbarName.setTextColor(hippoColorConfig.getHippoActionBarText());
        tvToolbarName.setText(title);

        pbWebPageLoader = findViewById(R.id.pbWebPageLoader);
        webView = findViewById(R.id.webView);


        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        setWebViewProperties(webView);
        webView.setWebViewClient(new MyWebViewClient());

        String keyId = "";
        if(paymentData.getPayment_gateway_id() == 1 && paymentGateway != null && !TextUtils.isEmpty(paymentGateway.getKeyId())) {
            keyId = paymentGateway.getKeyId();
        }
        if(!TextUtils.isEmpty(keyId)) {
            paymentData.setIsSdkFlow(1);
            getChannelData(false);
            handleLayout(0);
        } else {
            if(!TextUtils.isEmpty(url)) {
                handleLayout(1);
                webView.loadUrl(url);
            } else {
                handleLayout(0);
                getChannelData(false);
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!TextUtils.isEmpty(channelId))
            ConnectionManager.INSTANCE.unsubScribeChannel(channelId);
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void handleLayout(int value) {
        if(value == 0) {
            loadingLayout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        } else if(value == 2) {
            progressWheel.setVisibility(View.GONE);
            String text = Restring.getString(PrePaymentActivity.this, R.string.hippo_something_wentwrong);
            textView.setText(text);
        } else {
            loadingLayout.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
    }

    private void setWebViewProperties(final WebView webView) {
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(MIXED_CONTENT_ALWAYS_ALLOW);
        }


        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);

        webView.getSettings().setLightTouchEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                //return true or false after performing the URL request

//                wvWebsite.removeAllViews();
                WebView newView = new WebView(PrePaymentActivity.this);
                newView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                if (paymentGateway.getGatewayId() == RAZORPAY.intValue)
                    webView.addView(newView);

                if (paymentGateway.getGatewayId() == RAZORPAY.intValue)
                    setWebViewProperties(newView); //Dialog update on same screen
                else
                    setWebViewProperties(webView); //Dialog update on different screen

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newView);
                resultMsg.sendToTarget();
                return true;
            }

            @Override
            public void onCloseWindow(WebView window) {
                HippoLog.e("onCloseWindow", window + "");
                webView.removeView(window);
                super.onCloseWindow(window);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                HippoLog.e("URL onJsAlert", url + "");

                return super.onJsAlert(view, url, message, result);
            }
        });

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return onOverrideUrlLoading(view, request.getUrl().toString());
            }
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return onOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //ProgressDialog.show(mActivity);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            try {
                if (pbWebPageLoader != null)
                    pbWebPageLoader.setVisibility(View.INVISIBLE);
            } catch (Exception e) {

            }
            super.onPageFinished(view, url);
        }
    }


    private boolean onOverrideUrlLoading(WebView view, String url) {
        try {
            if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
                if(url.contains("success.html") || url.contains("Success.html")) {
                    if(HippoConfig.getInstance().getPrePaymentCallBack() != null) {
                        HippoConfig.getInstance().getPrePaymentCallBack().onPaymentSuccess();
                    }
                    onSuccessActivity();
                } else if(url.contains("error.html")) {
                    if(HippoConfig.getInstance().getPrePaymentCallBack() != null) {
                        HippoConfig.getInstance().getPrePaymentCallBack().onPaymentfailed();
                    }
                    onFailureActivity();
                }
            }
        } catch (Exception e) {

        }


        return true;
    }

    private void getChannelData(Boolean loader) {
        if(!TextUtils.isEmpty(HippoConfig.getInstance().getCurrentLanguage()))
            paymentData.setLang(HippoConfig.getInstance().getCurrentLanguage());

        RestClient.getApiInterface().getPrePaymentMethod(paymentData).enqueue(new ResponseResolver<PaymentListResponse>(this, loader, true) {
            @Override
            public void success(PaymentListResponse paymentListResponse) {
                channelId = "/"+paymentListResponse.getData().getChannelId();
                subscribeChannels(channelId);
                if(!TextUtils.isEmpty(paymentListResponse.getData().getPaymentUrl().getOrderId())) {
                    PaymentUrl paymentUrl = new PaymentUrl();
                    paymentUrl = paymentListResponse.getData().getPaymentUrl();
                    RazorPayData options = new RazorPayData();
                    //options.setAuthOrderId(Integer.parseInt()response.getData().getAuth_order_id());
                    options.setOrderId(paymentUrl.getOrderId());
                    options.setCurrency(paymentUrl.getCurrency());
                    options.setDescription(paymentUrl.getDescription());
                    options.setPhoneNo(paymentUrl.getPhoneNumber());
                    options.setAmount(paymentUrl.getAmount());
                    options.setUserEmail(paymentUrl.getUserEmail());
                    options.setName(paymentUrl.getName());
                    options.setAuthOrderId(paymentUrl.getAuth_order_id());
                    options.setReferenceId(paymentUrl.getReference_id());
                    startRazorPayPayment(options, paymentUrl.getApiKey());
                } else if(!TextUtils.isEmpty(paymentListResponse.getData().getPaymentUrl().getPaymentUrl())) {
//                    MakePayment makePayment = new MakePayment();
//                    makePayment.setChannel_id(paymentListResponse.getData().getChannelId().intValue());
//                    makePayment.setAppSecretKey(HippoConfig.getInstance().getAppKey());
//                    makePayment.setEn_user_id(HippoConfig.getInstance().getUserData().getEnUserId());
//                    ArrayList<HippoPayment> arrayList = new ArrayList();
//                    makePayment.setItems(arrayList);
//                    String paymentData = new Gson().toJson(makePayment);
//                    PaymentDialogFragment newFragment = PaymentDialogFragment.newInstance(paymentListResponse.getData().getPaymentUrl().getPaymentUrl(), paymentData, paymentGateway);
//                    newFragment.show(getSupportFragmentManager().beginTransaction(), "fragment_dialog");
                    handleLayout(1);
                    webView.loadUrl(paymentListResponse.getData().getPaymentUrl().getPaymentUrl());
                } else {
                    handleLayout(2);
                }
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }


    private void subscribeChannels(String channel) {
        ConnectionManager.INSTANCE.subScribeChannel(channel);
    }

    @Subscribe
    public void onFayeMessage(FayeMessage events) {
        if (events.type.equalsIgnoreCase(BusEvents.CONNECTED_SERVER.toString())) {
            //onConnectedServer();
        } else if (events.type.equalsIgnoreCase(BusEvents.RECEIVED_MESSAGE.toString())) {
            handleResponse(events.message, events.channelId);
        } else if (events.type.equalsIgnoreCase(BusEvents.PONG_RECEIVED.toString())) {
            //onPongReceived();
        } else if (events.type.equalsIgnoreCase(BusEvents.DISCONNECTED_SERVER.toString())) {

        } else if (events.type.equalsIgnoreCase(BusEvents.ERROR_RECEIVED.toString())) {
            //onErrorReceived(events.message, events.channelId);
        } else if (events.type.equalsIgnoreCase(BusEvents.WEBSOCKET_ERROR.toString())) {
            //onWebSocketError();
        } else if (events.type.equalsIgnoreCase(BusEvents.NOT_CONNECTED.toString())) {
            // TODO: 2020-04-27 show error in faye connection.
        }
    }

    private void handleResponse(String message, String msgChannelId) {
        if(!isFinishing() && !TextUtils.isEmpty(msgChannelId) && !TextUtils.isEmpty(channelId) && msgChannelId.equalsIgnoreCase(channelId)) {
            try {
                JSONObject object = new JSONObject(message);
                if(object.optInt("message_type") == 22) {
                    if(object.has("custom_action") && object.optJSONObject("custom_action").optInt("selected_id") > 0) {
                        if(HippoConfig.getInstance().getPrePaymentCallBack() != null) {
                            HippoConfig.getInstance().getPrePaymentCallBack().onPaymentSuccess();
                        }
                        onSuccessActivity();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog() {
        PaymentCancelDialog bottomSheetFragment = PaymentCancelDialog.newInstance();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void cancelPayment() {
        finish();
    }

    boolean btnClicked;
    private synchronized void onSuccessActivity() {
        if(!btnClicked) {
            btnClicked = true;
            Intent intent = new Intent();
            intent.putExtra("status", 1);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private synchronized void onFailureActivity() {
        if(!btnClicked) {
            btnClicked = true;
            Intent intent = new Intent();
            intent.putExtra("status", 2);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void startRazorPayPayment(RazorPayData options, String apiKey) {
        try {
            Checkout checkout = new Checkout();
            checkout.setKeyID(apiKey);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(FuguAppConstant.KEY_ORDER_ID, options.getOrderId());
            jsonObject.put(FuguAppConstant.KEY_PHONE_NO, options.getPhoneNo());
            jsonObject.put(FuguAppConstant.KEY_USER_EMAIL, options.getUserEmail());
            jsonObject.put(FuguAppConstant.KEY_DESCRIPTION, options.getDescription());
            jsonObject.put(FuguAppConstant.KEY_AMOUNT, options.getAmount());
            jsonObject.put(FuguAppConstant.KEY_CURRENCY, options.getCurrency());
            jsonObject.put(FuguAppConstant.KEY_NAME, options.getName());
            if(!TextUtils.isEmpty(options.getUserEmail()))
                jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL, options.getUserEmail());
            if(!TextUtils.isEmpty(options.getPhoneNo()))
                jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT, options.getPhoneNo());
            jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "");
            jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "");

            startRazorPayPayment(this, jsonObject, apiKey);
        } catch (Exception e) {
            e.printStackTrace();
            Gson gson = new Gson();
            JSONObject jObj = new JSONObject();
            try {
                jObj = new JSONObject(gson.toJson(options, RazorPayData.class));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            startRazorPayPayment(this, jObj, apiKey);
        }
    }

    private void startRazorPayPayment(Activity activity, JSONObject options, String apiKeys) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(apiKeys);
        try {
            options.remove(FuguAppConstant.KEY_AUTH_ORDER_ID);
            if(options.has(FuguAppConstant.KEY_USER_EMAIL))
                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL, options.remove(FuguAppConstant.KEY_USER_EMAIL).toString());
            if(options.has(FuguAppConstant.KEY_PHONE_NO))
                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT, options.remove(FuguAppConstant.KEY_PHONE_NO).toString());
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "");
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "");
            Log.i("RazorpayBaseActivity", "startRazorPayPayment options= "+options);
            checkout.setFullScreenDisable(true);
            checkout.open(activity, options);
            //loadingLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error in starting Razorpay Checkout");
        }
    }


}
