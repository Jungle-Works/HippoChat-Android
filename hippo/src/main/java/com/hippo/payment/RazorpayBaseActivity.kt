package com.hippo.payment

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.hippo.R
import com.hippo.activity.FuguBaseActivity
import com.hippo.constant.FuguAppConstant
import com.hippo.utils.fileUpload.Prefs
//import com.razorpay.Checkout
//import com.razorpay.PaymentData
//import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

/**
 * Created by gurmail on 21/10/20.
 * @author gurmail
 */
open class RazorpayBaseActivity : FuguBaseActivity(){//, PaymentResultWithDataListener {
    // razor pay callbacks
//    override fun onPaymentSuccess(s: String, paymentData: PaymentData) {
////        val paymentId = paymentData.paymentId
////        val signature = paymentData.signature
////        razorpayCallbackIntentService(paymentId, signature, "")
//    }
//
//    override fun onPaymentError(i: Int, s: String, paymentData: PaymentData) {
//        //razorpayCallbackIntentService("-1", "-1", s)
//    }

   /* fun startRazorPayPayment(options: RazorPayData, isUPI: Boolean) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put(FuguAppConstant.KEY_ORDER_ID, options.orderId)
            jsonObject.put(FuguAppConstant.KEY_PHONE_NO, options.phoneNo)
            jsonObject.put(FuguAppConstant.KEY_USER_EMAIL, options.userEmail)
            jsonObject.put(FuguAppConstant.KEY_DESCRIPTION, options.description)
            jsonObject.put(FuguAppConstant.KEY_AUTH_ORDER_ID, options.authOrderId)
            jsonObject.put(FuguAppConstant.KEY_AMOUNT, options.amount)
            jsonObject.put(FuguAppConstant.KEY_CURRENCY, options.currency)
            jsonObject.put(FuguAppConstant.KEY_NAME, options.name)
            startRazorPayPayment(jsonObject, isUPI)
        } catch (e: Exception) {
            e.printStackTrace()
            val gson = Gson()
            var jObj = JSONObject()
            try {
                jObj = JSONObject(gson.toJson(options, RazorPayData::class.java))
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
            startRazorPayPayment(jObj, isUPI)
        }
    }

    fun startRazorPayPayment(options: JSONObject, isUPI: Boolean) {
        val checkout = Checkout()
        checkout.setImage(R.drawable.hippo_apple)
        try {
            options.remove(FuguAppConstant.KEY_AUTH_ORDER_ID)
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL, options.remove(FuguAppConstant.KEY_USER_EMAIL)!!.toString())
            options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT, options.remove(FuguAppConstant.KEY_PHONE_NO)!!.toString())
            options.put(FuguAppConstant.KEY_RAZORPAY_THEME_COLOR, "#FD7945")
            if (isUPI) {
                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "upi")
                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "")
            } else {
                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "")
                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "")
            }
            Log.i("RazorpayBaseActivity", "startRazorPayPayment options=$options")
            checkout.setFullScreenDisable(true)
            checkout.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error in starting Razorpay Checkout")
        }
    }

    // razor pay callback intent service
    private fun razorpayCallbackIntentService(paymentId: String, signature: String, error: String) {
//        try {
//            val intent = Intent(this, RazorpayCallbackService::class.java)
//            intent.putExtra(Constants.KEY_ACCESS_TOKEN, JSONParser.getAccessTokenPair(this).first)
//            intent.putExtra(Constants.KEY_RAZORPAY_PAYMENT_ID, paymentId)
//            intent.putExtra(Constants.KEY_RAZORPAY_SIGNATURE, signature)
//            intent.putExtra(Constants.KEY_REFERENCE_ID, refId)
//            intent.putExtra(Constants.KEY_AUTH_ORDER_ID, authOrderId)
//            intent.putExtra(Constants.SP_RZP_NEGATIVE_BALANCE_SETTLE, isFromNegativeBalanceSettle)
//            if(error.isNotEmpty()) {
//                intent.putExtra(Constants.KEY_RAZORPAY_ERR, error)
//            }
//            startService(intent)
//            DialogPopup.showLoadingDialog(this, "")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    fun setPurchaseSubscriptionResponse(refId: Int, authOrderId: Int, isFromNegativeBalanceSettle: Int) {
        Prefs.with(this).save(FuguAppConstant.SP_RZP_ORDER_ID, refId)
        Prefs.with(this).save(FuguAppConstant.SP_RZP_AUTH_ORDER_ID, authOrderId)
        Prefs.with(this).save(FuguAppConstant.SP_RZP_NEGATIVE_BALANCE_SETTLE, isFromNegativeBalanceSettle)
    }

    private val refId: Int
        private get() = Prefs.with(this).getInt(FuguAppConstant.SP_RZP_ORDER_ID, -1)

    private val authOrderId: Int
        private get() = Prefs.with(this).getInt(FuguAppConstant.SP_RZP_AUTH_ORDER_ID, -1)

    private val isFromNegativeBalanceSettle: Int
        private get() = Prefs.with(this).getInt(FuguAppConstant.SP_RZP_NEGATIVE_BALANCE_SETTLE, 0)

    var handler: Handler? = null
        get() {
            if (field == null) {
                field = Handler()
            }
            return field
        }
        private set*/
}