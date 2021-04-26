package com.hippo.payment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RazorPayData(
    @SerializedName("order_id")
    @Expose
    public var orderId: String? = null,
    @SerializedName("reference_id")
    @Expose
    public var referenceId: String? = null,
    @SerializedName("phone_no")
    @Expose
    public var phoneNo: String? = null,
    @SerializedName("user_email")
    @Expose
    public var userEmail: String? = null,
    @SerializedName("description")
    @Expose
    public var description: String? = null,
    @SerializedName("auth_order_id")
    @Expose
    public var authOrderId: String? = null,
    public var amount: Double = 0.0,
    @SerializedName("currency")
    @Expose
    public var currency: String? = null,
    @SerializedName("name")
    @Expose
    public var name: String? = null
): Serializable