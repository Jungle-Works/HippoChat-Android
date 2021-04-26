package com.hippo.apis;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.hippo.*;
import com.hippo.callback.OnPaymentListListener;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.model.BusinessLanguages;
import com.hippo.model.FuguDeviceDetails;
import com.hippo.model.FuguPutUserDetailsResponse;
import com.hippo.model.UserInfoModel;
import com.hippo.retrofit.*;
import com.hippo.utils.HippoLog;
import com.hippo.utils.UniqueIMEIID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import com.hippo.utils.customROM.XiaomiUtilities;
import com.hippo.utils.fileUpload.Prefs;
import com.hippo.utils.filepicker.ToastUtil;
import org.json.JSONException;
import org.json.JSONObject;

import faye.ConnectionManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by ankit on 07/09/17.
 */

public class ApiPutUserDetails implements FuguAppConstant {

    public Activity activity;
    private Callback callback;
    private CaptureUserData userData;
    private boolean fetchAllList = false;

    public ApiPutUserDetails(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
        fetchAllList = false;
    }

    public void sendUserDetails(String resellerToken, int referenceId) {
        sendUserDetails(resellerToken, referenceId, HippoConfig.progressLoader);
    }

    public void sendUserDetails(String resellerToken, int referenceId, boolean showLoading, boolean fetchAllList) {
        this.fetchAllList = fetchAllList;
        sendUserDetails(resellerToken, referenceId, showLoading);
    }

    public void sendUserDetails(String resellerToken, int referenceId, boolean showLoading) {
        HippoLog.v("inside sendUserDetails", "inside sendUserDetails");
        Gson gson = new GsonBuilder().create();
        JsonObject deviceDetailsJson = null;
        try {
            deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(getAppVersion()).getDeviceDetails()).getAsJsonObject();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        HippoConfigAttributes attributes = CommonData.getAttributes();

        HashMap<String, Object> commonParamsMAp = new HashMap<>();
        if (attributes.isResellerApi()) {
            if(TextUtils.isEmpty(resellerToken))
                resellerToken = attributes.getResellerToken();
            if(TextUtils.isEmpty(resellerToken)) {
                if(HippoConfig.getInstance().getOnApiCallback() != null){
                    HippoConfig.getInstance().getOnApiCallback().onFailure("Reseller Token can't be empty");
                }
                try {
                    if(BuildConfig.DEBUG) {
                        ToastUtil.getInstance(activity).showToast("Reseller Token can't be empty");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            commonParamsMAp.put(RESELLER_TOKEN, resellerToken);
            commonParamsMAp.put(REFERENCE_ID, String.valueOf(referenceId));
            try {
                CommonData.saveResellerData(resellerToken, referenceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if(TextUtils.isEmpty(HippoConfig.getInstance().getAppKey()) && !TextUtils.isEmpty(attributes.getAppKey()))
                HippoConfig.getInstance().appKey = attributes.getAppKey();
            if(TextUtils.isEmpty(HippoConfig.getInstance().getAppKey())) {
                if(HippoConfig.getInstance().getOnApiCallback() != null){
                    HippoConfig.getInstance().getOnApiCallback().onFailure("App secret key can't be empty");
                }
                try {
                    if(BuildConfig.DEBUG) {
                        ToastUtil.getInstance(activity).showToast("App secret key can't be empty");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            commonParamsMAp.put(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
        }
        commonParamsMAp.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(activity));
        commonParamsMAp.put(APP_TYPE, HippoConfig.getInstance().getAppType());
        commonParamsMAp.put(DEVICE_TYPE, ANDROID_USER);
        commonParamsMAp.put(APP_VERSION, HippoConfig.getInstance().getVersionName());
        commonParamsMAp.put(APP_VERSION_CODE, HippoConfig.getInstance().getVersionCode());
        commonParamsMAp.put(DEVICE_DETAILS, deviceDetailsJson);

        userData = HippoConfig.getInstance().getUserData(false);

        try {
            if(attributes.getCustomAttributes() != null && attributes.getCustomAttributes().size() > 0)
                commonParamsMAp.put(CUSTOM_ATTRIBUTES, new JSONObject(attributes.getCustomAttributes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        CommonData.saveRequiredLang("");
        if (userData != null) {
            if (!TextUtils.isEmpty(userData.getUserUniqueKey()))
                commonParamsMAp.put(USER_UNIQUE_KEY, userData.getUserUniqueKey());

            if (!TextUtils.isEmpty(userData.getFullName()))
                commonParamsMAp.put(FULL_NAME, userData.getFullName());

            if (!TextUtils.isEmpty(userData.getEmail()))
                commonParamsMAp.put(EMAIL, userData.getEmail());

            if (!TextUtils.isEmpty(userData.getPhoneNumber())) {
                final String contact = userData.getPhoneNumber();//.replaceAll("[^\\d.]", "");
                /*if (!Utils.isValidPhoneNumber(contact)) {
                    ToastUtil.getInstance(activity).showToast("Invalid phone number");
                    return;
                }*/
                //commonParamsMAp.put(PHONE_NUMBER, contact);
                if(!HippoConfig.getInstance().isSetSkipNumber()) {
//                        || (CommonData.getUserDetails() != null && CommonData.getUserDetails().getData() != null
//                && CommonData.getUpdatedDetails().getData().getCustomerInitialFormInfo() == null)) {
                    commonParamsMAp.put(PHONE_NUMBER, contact);
                } else {
                    Prefs.with(activity).save("PHONE_NUMBER", contact);
                }
            }

            if(!TextUtils.isEmpty(CommonData.getImagePath()))
                commonParamsMAp.put(HIPPO_USER_IMAGE_PATH, CommonData.getImagePath());

            JSONObject attJson = new JSONObject();
            JSONObject addressJson = new JSONObject();
            JSONObject userInfo = new JSONObject();
            if(!TextUtils.isEmpty(CommonData.getUserCountryCode())) {
                try {
                    userInfo.put("country_code", CommonData.getUserCountryCode());
                    userInfo.put("continent_code", CommonData.getUserContCode());

                    attJson.put(COUNTRY_INFO, userInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                if (!TextUtils.isEmpty(userData.getAddressLine1())) {
                    addressJson.put(ADDRESS_LINE1, userData.getAddressLine1());
                }
                if (!TextUtils.isEmpty(userData.getAddressLine2())) {
                    addressJson.put(ADDRESS_LINE2, userData.getAddressLine2());
                }
                if (!TextUtils.isEmpty(userData.getCity())) {
                    addressJson.put(CITY, userData.getCity());
                }
                if (!TextUtils.isEmpty(userData.getRegion())) {
                    addressJson.put(REGION, userData.getRegion());
                }
                if (!TextUtils.isEmpty(userData.getCountry())) {
                    addressJson.put(COUNTRY, userData.getCountry());
                }
                if (!TextUtils.isEmpty(userData.getZipCode())) {
                    addressJson.put(ZIP_CODE, userData.getZipCode());
                }
                if (userData.getLatitude() != 0 && userData.getLongitude() != 0) {
                    attJson.put(LAT_LONG, String.valueOf(userData.getLatitude() + "," + userData.getLongitude()));
                }
                attJson.put(IP_ADDRESS, CommonData.getLocalIpAddress());
                attJson.put(ADDRESS, addressJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            commonParamsMAp.put(ATTRIBUTES, attJson);


            if(!userData.getTags().isEmpty()) {
                ArrayList<GroupingTag> groupingTags = new ArrayList<>();
                for(GroupingTag tag : userData.getTags()) {
                    GroupingTag groupingTag = new GroupingTag();
                    if(!TextUtils.isEmpty(tag.getTagName()))
                        groupingTag.setTagName(tag.getTagName());
                    if(tag.getTeamId() != null)
                        groupingTag.setTeamId(tag.getTeamId());

                    if(!TextUtils.isEmpty(tag.getTagName()) || tag.getTeamId() != null) {
                        groupingTags.add(groupingTag);
                    }
                }
                commonParamsMAp.put(GROUPING_TAGS, new Gson().toJson(groupingTags));
            } else {
                commonParamsMAp.put(GROUPING_TAGS, "[]");
            }

            if(userData.isFetchBusinessLang()) {
                commonParamsMAp.put("fetch_business_lang", 1);
            }

            if(!TextUtils.isEmpty(userData.getLang()))
                CommonData.saveRequiredLang(userData.getLang());
        }

        String deviceToken = null;
        try {
            deviceToken = CommonData.getDeviceToken();
        } catch (Exception e) {
            if(HippoConfig.DEBUG)
                e.printStackTrace();
        }
        if (!TextUtils.isEmpty(deviceToken)) {
            commonParamsMAp.put(DEVICE_TOKEN, deviceToken);
        }

        if(!fetchAllList) {
            commonParamsMAp.put("neglect_conversations", true);
        }

        if(CommonData.getAttributes() !=null && CommonData.getAttributes().getAdditionalInfo() != null &&
                CommonData.getAttributes().getAdditionalInfo().isAnnouncementCount()) {
            commonParamsMAp.put("fetch_announcements_unread_count", 1);
        }

        HippoLog.e("Fugu Config sendUserDetails maps", "==" + commonParamsMAp.toString());
        CommonData.savePutUserParams(commonParamsMAp);
        if (!TextUtils.isEmpty(resellerToken)) {
            apiPutUserDetailReseller(commonParamsMAp);
        } else {
            apiPutUserDetail(commonParamsMAp, showLoading);
        }
    }

    public void updateUserData(HashMap<String, Object> commonParamsMap) {
        if(commonParamsMap.containsKey(RESELLER_TOKEN)) {
            apiPutUserDetailReseller(commonParamsMap, HippoConfig.progressLoader, true);
        } else {
            apiPutUserDetail(commonParamsMap, HippoConfig.progressLoader, true);
        }
    }

    private void apiPutUserDetail(HashMap<String, Object> commonParams, boolean showLoading) {
        apiPutUserDetail(commonParams, showLoading, false);
    }
    private void apiPutUserDetail(HashMap<String, Object> commonParams, boolean showLoading, boolean showError) {
        try {
            if(HippoConfig.getInstance().getOnApiCallback() != null) {
                HippoConfig.getInstance().getOnApiCallback().onProcessing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String requiredLang = CommonData.getRequiredLanguage();
        if(TextUtils.isEmpty(requiredLang))
            requiredLang = CommonData.getCurrentLanguage();
        if(TextUtils.isEmpty(requiredLang))
            requiredLang = "en";
        CommonParams params = new CommonParams.Builder()
                .putMap(commonParams)
                .build(requiredLang);
        RestClient.getApiInterface().putUserDetails(params.getMap())
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(activity, showLoading, showError) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        try {
                            if(fuguPutUserDetailsResponse.getData().getFullName().equalsIgnoreCase("Visitor")) {
                                if(!TextUtils.isEmpty(Prefs.with(activity).getString("form_full_name", ""))) {
                                    fuguPutUserDetailsResponse.getData().setFullName(Prefs.with(activity).getString("form_full_name", ""));
                                    Prefs.with(activity).save("form_full_name", "");
                                }
                            }
                        } catch (Exception e) {

                        }

                        CommonData.setUserDetails(fuguPutUserDetailsResponse);

                        try {
                            if(fuguPutUserDetailsResponse.getData().getFuguConversations() != null &&
                                    fuguPutUserDetailsResponse.getData().getFuguConversations().size() > 0)
                                CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        } catch (Exception e) {

                        }

                        try {
                            if(TextUtils.isEmpty(CommonData.getRequiredLanguage()) && fuguPutUserDetailsResponse.getData().getBusinessLanguages() != null) {
                                for(BusinessLanguages languages : fuguPutUserDetailsResponse.getData().getBusinessLanguages()) {
                                    if(languages.isDefaultLnag()) {
                                        HippoConfig.getInstance().updateLanguage(languages.getLangCode());
                                        break;
                                    }
                                }
                            } else if(!TextUtils.isEmpty(CommonData.getRequiredLanguage()) && fuguPutUserDetailsResponse.getData().getBusinessLanguages() != null) {
                                String lang = "";
                                for(BusinessLanguages languages : fuguPutUserDetailsResponse.getData().getBusinessLanguages()) {
                                    if(CommonData.getRequiredLanguage().equalsIgnoreCase(languages.getLangCode())) {
                                        lang = languages.getLangCode();
                                        break;
                                    } else if(languages.isDefaultLnag()) {
                                        lang = languages.getLangCode();
                                    }
                                }
                                HippoConfig.getInstance().updateLanguage(lang);
                            }
                        } catch (Exception e) {

                        }

                        /*if(TextUtils.isEmpty(CommonData.getCurrentLanguage()) && fuguPutUserDetailsResponse.getData().getBusinessLanguages() != null) {
                            for(BusinessLanguages languages : fuguPutUserDetailsResponse.getData().getBusinessLanguages()) {
                                if(languages.isDefaultLnag()) {
                                    HippoConfig.getInstance().updateLanguage(languages.getLangCode());
                                    CommonData.saveCurrentLang(languages.getLangCode());
                                    break;
                                }
                            }

                        }*/

                        HippoConfig.getInstance().getUserData().setUserId(fuguPutUserDetailsResponse.getData().getUserId());
                        HippoConfig.getInstance().getUserData().setEnUserId(fuguPutUserDetailsResponse.getData().getEn_user_id());
                        CommonData.saveUserData(HippoConfig.getInstance().getUserData());
                        HippoLog.e("en_user_id",fuguPutUserDetailsResponse.getData().getEn_user_id());

                        if(activity != null) {
                            Prefs.with(activity).save("en_user_id", fuguPutUserDetailsResponse.getData().getEn_user_id());
                            Prefs.with(activity).save("user_id", fuguPutUserDetailsResponse.getData().getUserId());
                            Prefs.with(activity).save("full_name", fuguPutUserDetailsResponse.getData().getFullName());
                            Prefs.with(activity).save("email", fuguPutUserDetailsResponse.getData().getEmail());
                        }

                        try {
                            if(HippoConfig.getInstance().getOnApiCallback() != null) {
                                HippoConfig.getInstance().getOnApiCallback().onSucess();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        callback.onSuccess();
                        ConnectionManager.INSTANCE.initFayeConnection();
                        ConnectionManager.INSTANCE.subScribeChannel("/"+fuguPutUserDetailsResponse.getData().getUserChannel());
                        //ConnectionManager.INSTANCE.subScribeChannel("/"+fuguPutUserDetailsResponse.getData().getAppSecretKey()+"/markConversation");



                        try {
                            if(fuguPutUserDetailsResponse.getData().isAskPaymentAllowed())
                                fetchAllGateways();
                        } catch (Exception e) {

                        }

                        try {
                            if(fuguPutUserDetailsResponse.getData().getUnreadChannels() != null) {
                                if (HippoConfig.getInstance().getCallbackListener() != null) {
                                    HippoConfig.getInstance().getCallbackListener().unreadAnnouncementsCount(fuguPutUserDetailsResponse.getData().getUnreadChannels().size());
                                }

                                CommonData.setAnnouncementCount(fuguPutUserDetailsResponse.getData().getUnreadChannels());
                            } else {
                                CommonData.setAnnouncementCount(new HashSet<String>());
                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void failure(APIError error) {
                        try {
                            if(BuildConfig.DEBUG) {
                                ToastUtil.getInstance(activity).showToast(error.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if(HippoConfig.getInstance().getOnApiCallback() != null) {
                                HippoConfig.getInstance().getOnApiCallback().onFailure(error.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        callback.onFailure();
                    }
                });
    }

    private void apiPutUserDetailReseller(HashMap<String, Object> commonParams) {
        apiPutUserDetailReseller(commonParams, false, false);
    }

    private void apiPutUserDetailReseller(HashMap<String, Object> commonParams, boolean showLoading, boolean showError) {
        try {
            if(HippoConfig.getInstance().getOnApiCallback() != null) {
                HippoConfig.getInstance().getOnApiCallback().onProcessing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String requiredLang = CommonData.getRequiredLanguage();
        if(TextUtils.isEmpty(requiredLang))
            requiredLang = CommonData.getCurrentLanguage();
        if(TextUtils.isEmpty(requiredLang))
            requiredLang = "en";
        CommonParams params = new CommonParams.Builder()
                .putMap(commonParams)
                .build(requiredLang);
        RestClient.getApiInterface().putUserDetailsReseller(params.getMap())
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(activity, false, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        try {
                            CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        } catch (Exception e) {

                        }
                        HippoConfig.getInstance().getUserData().setUserId(fuguPutUserDetailsResponse.getData().getUserId());
                        HippoConfig.getInstance().getUserData().setEnUserId(fuguPutUserDetailsResponse.getData().getEn_user_id());
                        CommonData.saveUserData(HippoConfig.getInstance().getUserData());
                        HippoLog.e("en_user_id",fuguPutUserDetailsResponse.getData().getEn_user_id());
                        if (fuguPutUserDetailsResponse.getData().getAppSecretKey() != null &&
                                !TextUtils.isEmpty(fuguPutUserDetailsResponse.getData().getAppSecretKey())) {
                            HippoConfig.getInstance().appKey = fuguPutUserDetailsResponse.getData().getAppSecretKey();
                            CommonData.setAppSecretKey(fuguPutUserDetailsResponse.getData().getAppSecretKey());
                        }

                        if(activity != null) {
                            Prefs.with(activity).save("en_user_id", fuguPutUserDetailsResponse.getData().getEn_user_id());
                            Prefs.with(activity).save("user_id", fuguPutUserDetailsResponse.getData().getUserId());
                            Prefs.with(activity).save("full_name", fuguPutUserDetailsResponse.getData().getFullName());
                            Prefs.with(activity).save("email", fuguPutUserDetailsResponse.getData().getEmail());
                        }

                        try {
                            if(TextUtils.isEmpty(CommonData.getRequiredLanguage()) && fuguPutUserDetailsResponse.getData().getBusinessLanguages() != null) {
                                for(BusinessLanguages languages : fuguPutUserDetailsResponse.getData().getBusinessLanguages()) {
                                    if(languages.isDefaultLnag()) {
                                        HippoConfig.getInstance().updateLanguage(languages.getLangCode());
                                        break;
                                    }
                                }
                            } else if(!TextUtils.isEmpty(CommonData.getRequiredLanguage()) && fuguPutUserDetailsResponse.getData().getBusinessLanguages() != null) {
                                String lang = "";
                                for(BusinessLanguages languages : fuguPutUserDetailsResponse.getData().getBusinessLanguages()) {
                                    if(CommonData.getRequiredLanguage().equalsIgnoreCase(languages.getLangCode())) {
                                        lang = languages.getLangCode();
                                        break;
                                    } else if(languages.isDefaultLnag()) {
                                        lang = languages.getLangCode();
                                    }
                                }
                                HippoConfig.getInstance().updateLanguage(lang);
                            }
                        } catch (Exception e) {

                        }

                        try {
                            if(HippoConfig.getInstance().getOnApiCallback() != null) {
                                HippoConfig.getInstance().getOnApiCallback().onSucess();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        callback.onSuccess();

                        ConnectionManager.INSTANCE.initFayeConnection();
                        ConnectionManager.INSTANCE.subScribeChannel("/"+fuguPutUserDetailsResponse.getData().getUserChannel());

                        try {
                            if(fuguPutUserDetailsResponse.getData().isAskPaymentAllowed())
                                fetchAllGateways();
                        } catch (Exception e) {

                        }

                        try {
                            if(fuguPutUserDetailsResponse.getData().getUnreadChannels() != null) {
                                if (HippoConfig.getInstance().getCallbackListener() != null) {
                                    HippoConfig.getInstance().getCallbackListener().unreadAnnouncementsCount(fuguPutUserDetailsResponse.getData().getUnreadChannels().size());
                                }

                                CommonData.setAnnouncementCount(fuguPutUserDetailsResponse.getData().getUnreadChannels());
                            } else {
                                CommonData.setAnnouncementCount(new HashSet<String>());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(APIError error) {
                        try {
                            if(BuildConfig.DEBUG) {
                                ToastUtil.getInstance(activity).showToast(error.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if(HippoConfig.getInstance().getOnApiCallback() != null) {
                                HippoConfig.getInstance().getOnApiCallback().onFailure(error.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        callback.onFailure();
                    }
                });
    }

    private int getAppVersion() {
        try {
            return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void getUserContryInfo(final HippoConfigAttributes attributes, final UserCallback callback) {
        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://ip.tookanapp.com:8000")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ApiInterface gerritAPI = retrofit.create(ApiInterface.class);

            Call<UserInfoModel> call = gerritAPI.getUserInfo();
            call.enqueue(new ResponseResolver<UserInfoModel>() {
                @Override
                public void success(UserInfoModel userInfoModel) {
                    try {
                        CommonData.setUserContCode(userInfoModel.getData().getContinentCode());
                        CommonData.setUserCountryCode(userInfoModel.getData().getCountryCode());
                    } catch (Exception e) {

                    }
                    if(callback != null) {
                        callback.onSuccess(userInfoModel, attributes);
                    }
                }
                @Override
                public void failure(APIError error) {
                    if(callback != null) {
                        callback.onSuccess(null, attributes);
                    }
                }
            });
        } catch (Exception e) {
            if(callback != null) {
                callback.onSuccess(null, attributes);
            }
        }
    }

    public void stopRideStatus() {
        CommonParams params = new CommonParams.Builder()
                .add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
                .add(EN_USER_ID, HippoConfig.getInstance().getUserData().getEnUserId())
                .build();

        RestClient.getApiInterface().stopRideStatus(params.getMap()).enqueue(new ResponseResolver<CommonResponse>() {
            @Override
            public void success(CommonResponse commonResponse) {
                if(callback != null)
                    callback.onSuccess();
            }

            @Override
            public void failure(APIError error) {
                if(callback != null)
                    callback.onFailure();
            }
        });
    }

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    public interface UserCallback {
        void onSuccess(UserInfoModel userInfoModel, HippoConfigAttributes attributes);
    }

    private void fetchAllGateways() {
        if(HippoConfig.getInstance().isPaymentFeched())
            return;
        GetPaymentGateway.INSTANCE.getPaymentGatewaysList(activity,new OnPaymentListListener() {
            @Override
            public void onSuccessListener() {

            }

            @Override
            public void onErrorListener() {

            }
        });
    }

}
