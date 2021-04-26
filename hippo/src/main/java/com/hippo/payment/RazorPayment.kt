package com.hippo.payment

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.hippo.HippoConfig
import com.hippo.R
import com.hippo.activity.FuguBaseActivity
import com.hippo.constant.FuguAppConstant
import com.hippo.database.CommonData
import com.hippo.utils.filepicker.ToastUtil
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

/**
 * Created by gurmail on 21/10/20.
 * @author gurmail
 */
public class RazorPayment: FuguBaseActivity(), PaymentResultWithDataListener {

     // razor pay callbacks
     override fun onPaymentSuccess(s: String, paymentData: PaymentData) {
         val paymentId = paymentData.paymentId
         val signature = paymentData.signature
         razorpayCallbackIntentService(paymentId, signature, "")
         ToastUtil.getInstance(this).showToast("Payment done")
     }

     override fun onPaymentError(i: Int, s: String, paymentData: PaymentData) {
         razorpayCallbackIntentService("-1", "-1", s)
         ToastUtil.getInstance(this).showToast("Payment failed "+s)
         //HippoConfig.getInstance().
         finish()
     }

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         //checkout.setKeyID("<YOUR_KEY_ID>");
         val options: RazorPayData = intent?.getSerializableExtra("razorpayObj") as RazorPayData
         startRazorPayPayment(this, options, false)
     }

     fun startRazorPayPayment(activity: Activity, options: RazorPayData, isUPI: Boolean) {
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
             startRazorPayPayment(activity, jsonObject, isUPI)
         } catch (e: Exception) {
             e.printStackTrace()
             val gson = Gson()
             var jObj = JSONObject()
             try {
                 jObj = JSONObject(gson.toJson(options, RazorPayData::class.java))
             } catch (e1: Exception) {
                 e1.printStackTrace()
             }
             startRazorPayPayment(activity, jObj, isUPI)
         }
     }

     fun startRazorPayPayment(activity: Activity, options: JSONObject, isUPI: Boolean) {
         val checkout = Checkout()
         //checkout.setKeyID("rzp_test_SItorLnGt2wqOv")
         checkout.setKeyID("rzp_live_fsWpMDBnrFvyiW")

         checkout.setImage(R.drawable.hippo_apple)
         try {
             options.remove(FuguAppConstant.KEY_AUTH_ORDER_ID)
             options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL, options.remove(FuguAppConstant.KEY_USER_EMAIL)!!.toString())
             options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT, options.remove(FuguAppConstant.KEY_PHONE_NO)!!.toString())
             options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "upi")
             options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "")
             Log.i("RazorpayBaseActivity", "startRazorPayPayment options=$options")
             checkout.setFullScreenDisable(true)
             checkout.open(activity, options)
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
         CommonData.saveData(FuguAppConstant.SP_RZP_ORDER_ID, refId)
         CommonData.saveData(FuguAppConstant.SP_RZP_AUTH_ORDER_ID, authOrderId)
         CommonData.saveData(FuguAppConstant.SP_RZP_NEGATIVE_BALANCE_SETTLE, isFromNegativeBalanceSettle)

     }

     private val refId: Int
         private get() = CommonData.getInt(FuguAppConstant.SP_RZP_ORDER_ID, -1)

     private val authOrderId: Int
         private get() = CommonData.getInt(FuguAppConstant.SP_RZP_AUTH_ORDER_ID, -1)

     private val isFromNegativeBalanceSettle: Int
         private get() = CommonData.getInt(FuguAppConstant.SP_RZP_NEGATIVE_BALANCE_SETTLE, 0)

     var handler: Handler? = null
         get() {
             if (field == null) {
                 field = Handler()
             }
             return field
         }
         private set


}