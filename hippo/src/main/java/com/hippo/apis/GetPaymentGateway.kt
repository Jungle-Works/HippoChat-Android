package com.hippo.apis

import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.TextUtils
import com.hippo.BuildConfig
import com.hippo.callback.OnPaymentListListener
import com.hippo.constant.FuguAppConstant
import com.hippo.constant.FuguAppConstant.APP_SECRET_KEY
import com.hippo.database.CommonData
import com.hippo.model.payment.PaymentListResponse
import com.hippo.retrofit.*

/**
 * Created by gurmail on 2020-05-06.
 * @author gurmail
 */
object GetPaymentGateway {

//    fun getPaymentGatewaysList(paymentListener: OnPaymentListListener?) {
//        val info = getPAckageInfo(activity)
//
//        val params = CommonParams.Builder()
//            .add(APP_SECRET_KEY, CommonData.getUserDetails().data.appSecretKey)
//            .add("get_enabled_gateways", 1)
//            .add("is_sdk_flow", 1)
//            .add(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
//            .add(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
//            .add(FuguAppConstant.APP_VERSION_CODE, BuildConfig.VERSION_CODE)
//            .build()
//
//        RestClient.getApiInterface().getPaymentMethods(params.map).enqueue(object : ResponseResolver<PaymentListResponse>() {
//            override fun success(t: PaymentListResponse?) {
//                CommonData.savePaymentList(t?.data?.addedPaymentGateways!!)
//                paymentListener?.onSuccessListener()
//            }
//            override fun failure(error: APIError?) {
//                paymentListener?.onErrorListener()
//            }
//        })
//    }

    fun getPaymentGatewaysList(activity: Activity, paymentListener: OnPaymentListListener?) {
        getPaymentGatewaysList(activity, CommonData.getUserDetails().data.appSecretKey, paymentListener)
    }

    fun getPaymentGatewaysList(activity: Activity, appSecretKey: String, paymentListener: OnPaymentListListener?) {

        val info = getPAckageInfo(activity)
        val params = CommonParams.Builder()
            .add(APP_SECRET_KEY, appSecretKey)
            .add("get_enabled_gateways", 1)
            .add("is_sdk_flow", 1)
            .add(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
            .add(FuguAppConstant.APP_VERSION, info?.versionName)
            .add(FuguAppConstant.APP_VERSION_CODE, info?.versionCode)
            .build()

        RestClient.getApiInterface().getPaymentMethods(params.map).enqueue(object : ResponseResolver<PaymentListResponse>(activity, true, true) {
            override fun success(t: PaymentListResponse?) {
                CommonData.savePaymentList(t?.data?.addedPaymentGateways!!)
                paymentListener?.onSuccessListener()
            }
            override fun failure(error: APIError?) {
                paymentListener?.onErrorListener()
            }
        })
    }

fun getPAckageInfo(activity: Activity): PackageInfo? {
    val manager = activity.packageManager
    val info = manager.getPackageInfo(activity.packageName, PackageManager.GET_ACTIVITIES)
    return info
}


}