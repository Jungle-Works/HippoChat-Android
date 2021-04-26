package com.hippo.payment;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.json.JSONObject;
import java.util.*;

/**
 * Created by gurmail on 21/10/20.
 *
 * @author gurmail
 */

public class RazorpayCallbackService extends IntentService {

    public RazorpayCallbackService(){
        this("RazorpayCallbackService");
    }


    public RazorpayCallbackService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

//        try {
//            HashMap<String, String> map = new HashMap<>();
//            map.put(Constants.KEY_ACCESS_TOKEN, intent.getStringExtra(Constants.KEY_ACCESS_TOKEN));
//            map.put(Constants.KEY_RAZORPAY_PAYMENT_ID, intent.getStringExtra(Constants.KEY_RAZORPAY_PAYMENT_ID));
//            map.put(Constants.KEY_RAZORPAY_SIGNATURE, intent.getStringExtra(Constants.KEY_RAZORPAY_SIGNATURE));
//            map.put(Constants.KEY_REFERENCE_ID, String.valueOf(intent.getIntExtra(Constants.KEY_REFERENCE_ID, 0)));
//            map.put(Constants.KEY_AUTH_ORDER_ID, String.valueOf(intent.getIntExtra(Constants.KEY_AUTH_ORDER_ID, 0)));
//            String rzpErr = intent.getStringExtra(Constants.KEY_RAZORPAY_ERR);
//            if(rzpErr != null && !rzpErr.isEmpty()) {
//                map.put(Constants.KEY_RAZORPAY_ERR, rzpErr);
//            }
//
//            Response response = null;
//            HomeUtil.putDefaultParams(map);
//
//            if(intent.getIntExtra(Constants.SP_RZP_NEGATIVE_BALANCE_SETTLE, 0) == 0) {
//                response = RestClient.getApiServices().razorpayCallback(map);
//            } else {
//                response = RestClient.getApiServices().settleNegativeBalanceRzpCallback(map);
//            }
//
//            if(response != null) {
//                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//                JSONObject jObject1 = new JSONObject(responseStr);
//                backToActivity(jObject1);
//            } else {
//                backToActivity(null);
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            backToActivity(null);
//        }
    }

    private void backToActivity(JSONObject jObject1){
//        Intent intent = new Intent(Constants.INTENT_ACTION_RAZOR_PAY_CALLBACK);
//        if(jObject1 != null) {
//            intent.putExtra(Constants.KEY_RESPONSE, jObject1.toString());
//        }
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
