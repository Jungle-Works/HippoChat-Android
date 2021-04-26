//package com.hippo.payment
//
//import android.app.Activity
//import android.util.Log
//import com.google.gson.Gson
//import com.hippo.R
//import com.hippo.constant.FuguAppConstant
//import com.razorpay.Checkout
//import org.json.JSONObject
//
///**
// * Created by gurmail on 26/10/20.
// * @author gurmail
// */
//object RazorPaymentObj {
//
//    fun startRazorPayPayment(activity: Activity, options: RazorPayData, publicKey: String, isUPI: Boolean) {
//        try {
//            val jsonObject = JSONObject()
//            jsonObject.put(FuguAppConstant.KEY_ORDER_ID, options.orderId)
//            jsonObject.put(FuguAppConstant.KEY_PHONE_NO, options.phoneNo)
//            jsonObject.put(FuguAppConstant.KEY_USER_EMAIL, options.userEmail)
//            jsonObject.put(FuguAppConstant.KEY_DESCRIPTION, options.description)
//            jsonObject.put(FuguAppConstant.KEY_AUTH_ORDER_ID, options.authOrderId)
//            jsonObject.put(FuguAppConstant.KEY_AMOUNT, options.amount)
//            jsonObject.put(FuguAppConstant.KEY_CURRENCY, options.currency)
//            jsonObject.put(FuguAppConstant.KEY_NAME, options.name)
//            startRazorPayPayment(activity, jsonObject, publicKey, isUPI)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            val gson = Gson()
//            var jObj = JSONObject()
//            try {
//                jObj = JSONObject(gson.toJson(options, RazorPayData::class.java))
//            } catch (e1: Exception) {
//                e1.printStackTrace()
//            }
//            startRazorPayPayment(activity, jObj, publicKey, isUPI)
//        }
//    }
//
//    private fun startRazorPayPayment(activity: Activity, options: JSONObject, publicKey: String, isUPI: Boolean) {
//        val checkout = Checkout()
//        checkout.setKeyID(publicKey)
//        //checkout.setKeyID("rzp_live_fsWpMDBnrFvyiW")
//
//        //checkout.setImage(R.drawable.hippo_apple)
//        try {
//            options.remove(FuguAppConstant.KEY_AUTH_ORDER_ID)
//            options.put(
//                FuguAppConstant.KEY_RAZORPAY_PREFILL_EMAIL,
//                options.remove(FuguAppConstant.KEY_USER_EMAIL)!!.toString()
//            )
//            options.put(
//                FuguAppConstant.KEY_RAZORPAY_PREFILL_CONTACT,
//                options.remove(FuguAppConstant.KEY_PHONE_NO)!!.toString()
//            )
//            if (isUPI) {
//                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "upi")
//                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "")
//            } else {
//                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_METHOD, "upi")
//                options.put(FuguAppConstant.KEY_RAZORPAY_PREFILL_VPA, "")
//            }
//            Log.i("RazorpayBaseActivity", "startRazorPayPayment options=$options")
//            checkout.setFullScreenDisable(true)
//            checkout.open(activity, options)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("TAG", "Error in starting Razorpay Checkout")
//        }
//    }
//}