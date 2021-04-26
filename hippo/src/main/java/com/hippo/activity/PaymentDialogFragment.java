package com.hippo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

import androidx.fragment.app.DialogFragment;
import com.google.gson.Gson;
import com.hippo.BuildConfig;
import com.hippo.HippoColorConfig;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.langs.Restring;
import com.hippo.model.MakePayment;
import com.hippo.model.PaymentResponse;
import com.hippo.model.payment.AddedPaymentGateway;
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

import org.json.JSONObject;

import static android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
import static com.hippo.constant.FuguAppConstant.ANDROID_USER;
import static com.hippo.payment.PaymentConstants.PaymentValue.BILLPLZ;
import static com.hippo.payment.PaymentConstants.PaymentValue.PAYFORT;
import static com.hippo.payment.PaymentConstants.PaymentValue.RAZORPAY;

/**
 * Created by gurmail on 2019-11-05.
 *
 * @author gurmail
 */
public class PaymentDialogFragment extends DialogFragment {

    private RelativeLayout rootToolbar;
    private ImageView ivBackBtn;
    private TextView tvToolbarName;
    private RelativeLayout loadingLayout;
    private ProgressWheel progressWheel;
    private TextView textView;

    private MakePayment paymentData;

    private ProgressBar pbWebPageLoader;
    private WebView webView;

    private HippoColorConfig hippoColorConfig;
    private FuguChatActivity chatActivity;
    private PrePaymentActivity paymentActivity;



    public interface OnInputListener {
        void closeFragment();
    }

    public OnInputListener onInputListener;
    private AddedPaymentGateway paymentGateway;

    //PaymentDialogFragment fragment;

    static PaymentDialogFragment newInstance(String url, String paymentData, AddedPaymentGateway paymentGateway) {
        PaymentDialogFragment fragment = new PaymentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("paymentData", paymentData);
        bundle.putBoolean("fullScreen", true);
        bundle.putString("paymentGateway", new Gson().toJson(paymentGateway));
        bundle.putBoolean("direct_open", false);
        if(!TextUtils.isEmpty(url))
            bundle.putBoolean("direct_open", true);

        fragment.setArguments(bundle);
        return fragment;
    }

    String url = "";
    boolean directOpen = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return inflater.inflate(R.layout.activity_webview, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String title = Restring.getString(getActivity(), R.string.fugu_payment);

        rootToolbar = view.findViewById(R.id.my_toolbar);
        tvToolbarName = view.findViewById(R.id.tv_toolbar_name);
        ivBackBtn = view.findViewById(R.id.ivBackBtn);
        loadingLayout = view.findViewById(R.id.loadingLayout);
        progressWheel = view.findViewById(R.id.circle_progress);
        textView = view.findViewById(R.id.text);
        String text = Restring.getString(getActivity(), R.string.hippo_payment_loader);
        textView.setText(text);

        hippoColorConfig = CommonData.getColorConfig();
        rootToolbar.setBackgroundColor(hippoColorConfig.getHippoActionBarBg());
        tvToolbarName.setTextColor(hippoColorConfig.getHippoActionBarText());
        tvToolbarName.setText(title);

        pbWebPageLoader = view.findViewById(R.id.pbWebPageLoader);
        webView = view.findViewById(R.id.webView);

        if(!directOpen && paymentGateway.getGatewayId() == 1 && !TextUtils.isEmpty(paymentGateway.getKeyId())) {
            paymentData.setIsSdkFlow(1);
        } else {
            setWebViewProperties(webView);
            webView.setWebViewClient(new MyWebViewClient());
        }


        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onInputListener != null)
                    onInputListener.closeFragment();
                dismiss();
            }
        });



        if(!TextUtils.isEmpty(url)) {
            handleLayout(1);
            webView.loadUrl(url);
        } else {
            handleLayout(0);
            createPaymentLink(paymentData);
        }

    }

    private void handleLayout(int value) {
        if(value == 0) {
            loadingLayout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        } else if(value == 2) {
            progressWheel.setVisibility(View.GONE);
            String text = Restring.getString(getActivity(), R.string.hippo_something_went_wrong);
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
                WebView newView = new WebView(getActivity());
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            HippoLog.e("TAG", "onAttach: " + e.getMessage());
        }
        if(getActivity() instanceof FuguChatActivity)
            chatActivity = (FuguChatActivity) getActivity();
        else if(getActivity() instanceof PrePaymentActivity)
            paymentActivity = (PrePaymentActivity) getActivity();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(onInputListener != null)
            onInputListener.closeFragment();
        super.onDismiss(dialog);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.ivBackBtn) {
            //onBackPressed();
            //dismiss();
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getArguments().getString("url");
        directOpen = getArguments().getBoolean("direct_open");
        paymentData = new Gson().fromJson(getArguments().getString("paymentData"), MakePayment.class);
        paymentGateway = new Gson().fromJson(getArguments().getString("paymentGateway"), AddedPaymentGateway.class);
        HippoLog.e("URL", "URL = "+url);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return onOverrideUrlLoading(view, request.getUrl().toString());
            }
            return true;
        }

       /* @Override
        public WebResourceResponse shouldInterceptRequest (final WebView view, String url) {
            HippoLog.e("URL", url + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            return super.shouldInterceptRequest(view, url);

        }*/

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
        HippoLog.e("loaded URL", url + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
        String transactionId = "", jobPaymentDetailId = "";


        if (paymentGateway.getGatewayId() == RAZORPAY.intValue) {
            transactionId = sanitizer.getValue("rzp_payment_id");

        } else if (paymentGateway.getGatewayId() == BILLPLZ.intValue) {
            transactionId = sanitizer.getValue("billplz[id]");
        } else if (paymentGateway.getGatewayId() == PAYFORT.intValue) {
            transactionId = sanitizer.getValue("transactionId");
            jobPaymentDetailId = sanitizer.getValue("job_payment_detail_id");
        } else {
            /* paymentMethod == PAYFAST || FAC || INSTAPAY || PAYPAL || PAYMOB  returns transactionId*/

            if (sanitizer.getValue("transactionId") != null) {
                transactionId = sanitizer.getValue("transactionId");
            } else {
                transactionId = sanitizer.getValue("transaction_id");
            }
        }

        try {
            if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
                if(url.contains("success.html") || url.contains("Success.html")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    }, 2500);
                } else if(url.contains("error.html")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    }, 2500);
                }
            }
        } catch (Exception e) {

        }


        return true;
    }

    private void createPaymentLink(MakePayment makePayment) {
        //if (chatActivity.isNetworkAvailable()) {
        if (true) {

            makePayment.setPayment_gateway_id(paymentGateway.getGatewayId());
            makePayment.setIs_multi_gateway_flow(1);
            makePayment.setDevice_details(CommonData.deviceDetailString(getActivity()));
            makePayment.setApp_version(HippoConfig.getInstance().getCodeVersion());
            makePayment.setDevice_id(UniqueIMEIID.getUniqueIMEIId(getActivity()));
            makePayment.setSource_type(1);
            makePayment.setDevice_type(ANDROID_USER);

            if(!TextUtils.isEmpty(HippoConfig.getInstance().getCurrentLanguage()))
                makePayment.setLang(HippoConfig.getInstance().getCurrentLanguage());

            RestClient.getApiInterface().createPaymentLink(makePayment)
                    .enqueue(new ResponseResolver<PaymentResponse>(getActivity(), false, true) {
                        @Override
                        public void success(PaymentResponse response) {
                            if(!TextUtils.isEmpty(response.getData().getOrderId())) {
                                RazorPayData options = new RazorPayData();
                                //options.setAuthOrderId(Integer.parseInt()response.getData().getAuth_order_id());
                                options.setCurrency(response.getData().getCurrency());
                                options.setDescription(response.getData().getDescription());
                                options.setPhoneNo(response.getData().getPhoneNumber());
                                options.setAmount(response.getData().getAmount());
                                options.setUserEmail(response.getData().getUserEmail());
                                options.setName(response.getData().getName());
                                options.setAuthOrderId(response.getData().getAuth_order_id());
                                options.setReferenceId(response.getData().getReference_id());
                                startRazorPayPayment(options, response.getData().getApiKey());
                            } else
                                if(!TextUtils.isEmpty(response.getData().getPaymentUrl())) {
                                handleLayout(1);
                                webView.loadUrl(response.getData().getPaymentUrl());
                            } else {
                                handleLayout(2);
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            handleLayout(2);
                        }
                    });
        } else {
            String text = Restring.getString(getActivity(), R.string.fugu_unable_to_connect_internet);
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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
            //jsonObject.put(FuguAppConstant.KEY_AUTH_ORDER_ID, options.getAuthOrderId());
            jsonObject.put(FuguAppConstant.KEY_AMOUNT, options.getAmount());
            jsonObject.put(FuguAppConstant.KEY_CURRENCY, options.getCurrency());
            jsonObject.put(FuguAppConstant.KEY_NAME, options.getName());
            if(!TextUtils.isEmpty(options.getUserEmail()))
                jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL, options.getUserEmail());
            if(!TextUtils.isEmpty(options.getPhoneNo()))
                jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT, options.getPhoneNo());
            jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "upi");
            jsonObject.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "");

            startRazorPayPayment(getActivity(), jsonObject, apiKey);
        } catch (Exception e) {
            e.printStackTrace();
            Gson gson = new Gson();
            JSONObject jObj = new JSONObject();
            try {
                jObj = new JSONObject(gson.toJson(options, RazorPayData.class));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            startRazorPayPayment(getActivity(), jObj, apiKey);
        }
    }

    private void startRazorPayPayment(Activity activity, JSONObject options, String apiKeys) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(apiKeys);
        try {
            options.remove(FuguAppConstant.KEY_AUTH_ORDER_ID);
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL, options.remove(FuguAppConstant.KEY_USER_EMAIL).toString());
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT, options.remove(FuguAppConstant.KEY_PHONE_NO).toString());
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "upi");
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "");
            Log.i("RazorpayBaseActivity", "startRazorPayPayment options= "+options);
            checkout.setFullScreenDisable(true);
            checkout.open(activity, options);
            loadingLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error in starting Razorpay Checkout");
        }
    }

}
