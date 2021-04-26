package com.hippo.retrofit;


import android.app.DownloadManager;

import com.hippo.aws.AwsData;
import com.hippo.model.*;
import com.hippo.model.groupCall.GroupCallResponse;
import com.hippo.model.labelResponse.GetLabelMessageResponse;
import com.hippo.model.payment.PaymentListResponse;
import com.hippo.model.payment.PrePaymentData;
import com.hippo.model.promotional.PromotionResponse;
import com.hippo.support.model.HippoSendQueryParams;
import com.hippo.support.model.SupportModelResponse;
import com.hippo.support.model.SupportResponse;
import com.hippo.tickets.CreateCustomerResponce.CreateCustomerResponse;
import com.hippo.tickets.TicketPriorityOptionsData;

import okhttp3.MultipartBody;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import static com.hippo.constant.FuguAppConstant.APP_SECRET_KEY;
import static com.hippo.constant.FuguAppConstant.APP_VERSION;
import static com.hippo.constant.FuguAppConstant.DEVICE_TYPE;

/**
 * ApiInterface
 */
public interface ApiInterface {

    @FormUrlEncoded
    @POST("/api/users/putUserDetails")
    Call<FuguPutUserDetailsResponse> putUserDetails(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/reseller/putUserDetails")
    Call<FuguPutUserDetailsResponse> putUserDetailsReseller(@FieldMap(encoded = false) Map<String, Object> map);

    //@FormUrlEncoded
    @POST("/api/conversation/createConversation")
    Call<FuguCreateConversationResponse> createConversation(@Body FuguCreateConversationParams obj);

    @POST("/api/conversation/getMessages")
    Call<FuguGetMessageResponse> getMessages(@Body FuguGetMessageParams obj);

    @POST("/api/conversation/getByLabelId")
    Call<GetLabelMessageResponse> getByLabelId(@Body FuguGetByLabelIdParams obj);

    @FormUrlEncoded
    @POST("/api/conversation/getConversations")
    Call<FuguGetConversationsResponse> getConversations(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("api/erpnext/search")
    Call<TicketPriorityOptionsData> erpNextSearch(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/erpnext/checkAndCreateCustomer")
    Call<CreateCustomerResponse> checkAndCreateCustomer(@FieldMap Map<String, Object> map);


    @FormUrlEncoded
    @POST("/api/users/userlogout")
    Call<CommonResponse> logOut(@FieldMap Map<String, Object> map);

    @Multipart
    @POST("/api/conversation/uploadFile")
    Call<FuguUploadImageResponse> uploadFile(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST("/api/conversation/uploadFile")
    Call<FuguUploadImageResponse> uploadFile(@Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST("/api/server/logException")
    Call<CommonResponse> sendError(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/server/logException")
    Call<CommonResponse> sendAckToServer(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/business/getBusinessSupportPanel")
    Call<SupportResponse> fetchSupportData(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/business/getBusinessSupportPanel")
    Call<SupportResponse> sendSupportQuery(@FieldMap Map<String, Object> map);

    @POST("/api/support/createConversation")
    Call<SupportModelResponse> createTicket(@Body HippoSendQueryParams obj);

    @GET("/requestCountryCodeGeoIP2")
    Call<UserInfoModel> getUserInfo();

    @FormUrlEncoded
    @POST("/api/users/inRideStatus")
    Call<CommonResponse> stopRideStatus(@FieldMap Map<String, Object> stringObjectMap);

    @POST("/api/conversation/fetchP2PUnreadCount")
    Call<UnreadCountResponse> fetchUnreadCountFor(@Body HippoUnreadCountParams conversationParams);

    @FormUrlEncoded
    @POST("/api/conversation/v2/getConversations")
    Call<FuguGetConversationsResponse> getConversation(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/users/customerLogin")
    Call<CommonResponse> customerLogin(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/users/verifyCustomerOTP")
    Call<UserResponse> verifyNUmber(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/apps/updateApp")
    Call<AppUpdateModel> updateApp(@FieldMap Map<String, Object> map);

    @POST("/api/payment/makeSelectedPayment")
    Call<PaymentResponse> createPaymentLink(@Body MakePayment makePayment);

    @FormUrlEncoded
    @POST("/api/agent/v1/getInfo")
    Call<AgentInfoResponse> getAgentInfo(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/broadcast/getAnnouncements")
    Call<PromotionResponse> fetchMobilePush(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/broadcast/clearAnnouncements")
    Call<CommonResponse> clearMobilePush(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/agent/assignAgent")
    Call<CommonResponse> assignAgent(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/payment/getPaymentGateway")
    Call<PaymentListResponse> getPaymentMethods(@FieldMap Map<String, Object> map);

    @POST("/api/conversation/createOperationalChannel")
    Call<PaymentListResponse> getPrePaymentMethod(@Body PrePaymentData paymentData);

    @POST("/api/apps/fetchAppLanguageData")
    Call<MultilangualResponse> getLanguageData(@Body LangRequest map);

    @FormUrlEncoded
    @POST("/api/apps/updateUserLanguage")
    Call<MultilangualResponse> updateUserLanguage(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/conversation/getGroupCallChannelDetails")
    Call<GroupCallResponse> groupCallChannelDetails(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/conversation/deleteOrEditMessage")
    Call<CommonResponse> deleteOrEditMessage(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/broadcast/getAndUpdateAnnouncement")
    Call<PromotionResponse> getAndUpdateAnnouncement(@FieldMap Map<String, Object> map);

//    @FormUrlEncoded
    @POST("/api/conversation/getUploadFile")
    Call<AwsData> getUploadedFile(@Body Map<String, Object> map);


}
