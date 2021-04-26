//package com.hippo.retrofit;
//
//import com.hippo.agent.model.FuguAgentGetMessageParams;
//import com.hippo.agent.model.FuguAgentGetMessageResponse;
//import com.hippo.agent.model.GetConversationResponse;
//import com.hippo.agent.model.LoginResponse;
//import com.hippo.agent.model.broadcastResponse.BroadcastModel;
//import com.hippo.agent.model.broadcastStatus.BroadcastResponseModel;
//import com.hippo.agent.model.createConversation.CreateConversation;
//import com.hippo.agent.model.unreadResponse.UnreadCountResponse;
//import com.hippo.agent.model.user_details.UserDetailsResponse;
//import com.hippo.constant.FuguAppConstant;
//import com.hippo.model.*;
//
//import java.util.List;
//import java.util.Map;
//
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.http.*;
//
//import static com.hippo.constant.FuguAppConstant.APP_VERSION;
//import static com.hippo.constant.FuguAppConstant.DEVICE_TYPE;
//
///**
// * Created by gurmail on 18/06/18.
// *
// * @author gurmail
// */
//
//public interface AgentApiInterface {
//
//    @FormUrlEncoded
//    @POST("/api/agent/agentLoginViaAuthToken")
//    Call<LoginResponse> verifyAuthToken(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/agent/getAgentLoginInfo")
//    Call<LoginResponse> getAgentToken(@FieldMap Map<String, Object> map);
//
//
//    @FormUrlEncoded
//    @POST("/api/agent/agentLogin")
//    Call<LoginResponse> login(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/agent/agentLogout")
//    Call<LoginResponse> logout(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/conversation/v1/getConversations")
//    Call<GetConversationResponse> getConversation(@FieldMap Map<String, Object> map);
//
//    @POST("/api/conversation/getMessages")
//    Call<FuguAgentGetMessageResponse> getMessages(@Body FuguAgentGetMessageParams obj);
//
//    @FormUrlEncoded
//    @POST("/api/conversation/createConversation")
//    Call<CreateConversation> createConversation(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/chat/createOneToOneChat")
//    Call<CreateConversation> createO2OConversation(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("api/users/getUserDetails")
//    Call<UserDetailsResponse> getUserDetails(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/conversation/markConversation")
//    Call<GetConversationResponse> markConversation(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/conversation/get_customer_unread_count")
//    Call<UnreadCountResponse> getUnreadCount(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/broadcast/getGroupingTag")
//    Call<BroadcastModel> getGroupingTag(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/broadcast/sendBroadcastMessage")
//    Call<BroadcastResponseModel> sendBroadcastMessage(@FieldMap Map<String, Object> map);
//
//    @FormUrlEncoded
//    @POST("/api/broadcast/getBroadcastList")
//    Call<BroadcastResponseModel> getBroadcastList(@FieldMap Map<String, Object> map);
//
//    @Multipart
//    @POST("/api/conversation/uploadFile")
//    Call<FuguUploadImageResponse> uploadFile(@Header(FuguAppConstant.ACCESS_TOKEN) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);
//
//    @GET("/api/broadcast/broadcastStatus")
//    Call<BroadcastResponseModel> broadcastStatus(@QueryMap Map<String, Object> map);
//}
