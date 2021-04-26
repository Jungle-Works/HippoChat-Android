package com.hippo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.hippo.HippoConfig;
import com.hippo.LibApp;
import com.hippo.R;
import com.hippo.adapter.CustomerInitalAdapter;
import com.hippo.apis.ApiPutUserDetails;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.interfaces.CustomerInitalListener;
import com.hippo.model.Field;
import com.hippo.model.FuguConversation;
import com.hippo.utils.HippoLog;
import com.hippo.utils.StringUtil;
import com.hippo.utils.fileUpload.Prefs;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gurmail on 14/02/19.
 *
 * @author gurmail
 */
public class CustomerInitalActivity extends FuguBaseActivity implements FuguAppConstant, CustomerInitalListener {

    private static final String TAG = CustomerInitalActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private CustomerInitalAdapter initalAdapter;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hippo_activity_customer_inital);

        try {
            LibApp.getInstance().screenOpened("Inital Form Screen");
        } catch (Exception e) {

        }

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        String title = "";
        try {
            title = CommonData.getUserDetails().getData().getCustomerInitialFormInfo().getPageTitle();
        } catch (Exception e) {

        }
        title = TextUtils.isEmpty(title) ? CommonData.getChatTitle(this) : title;
        setToolbar(myToolbar, title);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CustomerInitalActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Object> objects = new ArrayList<>();

        objects.addAll(CommonData.getUserDetails().getData().getCustomerInitialFormInfo().getFields());
        objects.add(CommonData.getUserDetails().getData().getCustomerInitialFormInfo().getButton());

        initalAdapter = new CustomerInitalAdapter(objects, getSupportFragmentManager(), this);
        recyclerView.setAdapter(initalAdapter);

    }

    @Override
    public void onButtonClicked(ArrayList<Object> objects) {
        HippoLog.v(TAG, "objects >>>>>> "+new Gson().toJson(objects));

        HashMap<String, Object> objectHashMap = CommonData.getPutUserParams();
        JSONObject obj = new JSONObject();

        for(Object object : objects) {
            if(object instanceof Field) {
                Field field = (Field) object;
                if(field.getType().toLowerCase().equalsIgnoreCase("LABEL")) {
                    //do nothing here.
                } else if(field.getType().toLowerCase().equalsIgnoreCase("contact_number")) {
                    objectHashMap.put(field.getKey(), field.getCountryCode()+field.getTextValue().trim());
                } else {
                    if(field.isBotAttributes()) {
                        try {
                            obj.put(field.getKey(), field.getTextValue().trim());
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    } else {
                        objectHashMap.put(field.getKey(), field.getTextValue().trim());
                        if(field.getKey().equalsIgnoreCase("full_name")) {
                            Prefs.with(this).save("form_full_name", field.getTextValue().trim());
                        }
                    }
                }
            }
        }

        if(obj != null && obj.length()>0)
            objectHashMap.put("bot_attributes", obj);

        String phnNumber = Prefs.with(this).getString("PHONE_NUMBER", "");
        if(!TextUtils.isEmpty(phnNumber)) {
            objectHashMap.put(PHONE_NUMBER, phnNumber);
            Prefs.with(this).save("PHONE_NUMBER", "");
        }

        try {
            LibApp.getInstance().trackEvent("Form Screen", "Button clicked", "form data");
        } catch (Exception e) {

        }

        new ApiPutUserDetails(CustomerInitalActivity.this, new ApiPutUserDetails.Callback() {
            @Override
            public void onSuccess() {

                openConversation();
            }

            @Override
            public void onFailure() {

            }
        }).updateUserData(objectHashMap);

    }

    @Override
    public void onNotifyAdapter(ArrayList<Object> objects) {
        HippoLog.v(TAG, "arrayList ******** "+new Gson().toJson(objects));

        if(initalAdapter != null) {
            initalAdapter = null;
            initalAdapter = new CustomerInitalAdapter(objects, getSupportFragmentManager(), this);
            recyclerView.setAdapter(initalAdapter);
        }
    }

    private void openConversation() {
        if(CommonData.hasDirectScreen()) {
            Intent chatIntent = new Intent(CustomerInitalActivity.this, FuguChatActivity.class);
            FuguConversation conversation = new FuguConversation();
            conversation.setLabelId(CommonData.getConstantLabelId());

            conversation.setLabel("");
            conversation.setOpenChat(true);
            conversation.setUserName(StringUtil.toCamelCase(HippoConfig.getInstance().getUserData().getFullName()));
            conversation.setUserId(HippoConfig.getInstance().getUserData().getUserId());
            conversation.setEnUserId(HippoConfig.getInstance().getUserData().getEnUserId());
            chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
            HippoConfig.getInstance().setAdditionalInfo(false, -1l);
            startActivity(chatIntent);
            finish();
        } else {
            HippoConfig.getInstance().showConversations(CustomerInitalActivity.this, CommonData.getChatTitle(CustomerInitalActivity.this));
            finish();
        }
    }
}
