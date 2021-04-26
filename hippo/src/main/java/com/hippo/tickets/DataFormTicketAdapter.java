package com.hippo.tickets;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.activity.FuguChatActivity;
import com.hippo.adapter.QRCallback;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.langs.Restring;
import com.hippo.model.Image;
import com.hippo.model.Message;
import com.hippo.retrofit.APIError;
import com.hippo.retrofit.CommonParams;
import com.hippo.retrofit.ResponseResolver;
import com.hippo.retrofit.RestClient;
import com.hippo.support.Utils.Constants;
import com.hippo.tickets.CreateCustomerResponce.CreateCustomerResponse;
import com.hippo.utils.InstantAutoComplete;
import com.hippo.utils.Utils;
import com.hippo.utils.countrypicker.Country;
import com.hippo.utils.countrypicker.CountryPicker;
import com.hippo.utils.countrypicker.OnCountryPickerListener;
import com.hippo.utils.fileUpload.FileuploadModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import static com.hippo.utils.countrypicker.CountryPicker.SORT_BY_NAME;

public class DataFormTicketAdapter extends RecyclerView.Adapter<DataFormTicketAdapter.QRViewHolder> implements AttachmentSelectedTicketListener {


    private static final String TAG = DataFormTicketAdapter.class.getSimpleName();
    private final Activity activity;
    private Context context;
    private ArrayList<String> arrayList;
    private QRCallback qrCallback;
    private ArrayList<DataFormTicketAdapter.Question> question = new ArrayList<>();
    private Message currentFormMsg;
    private FragmentManager fragmentManager;
    ArrayList<String> searchItem = new ArrayList<>();
    private ArrayAdapter<String> searchAdapter;


    public DataFormTicketAdapter(Message currentFormMsg, QRCallback qrCallback, FragmentManager fragmentManager, Activity activity) {
        this.currentFormMsg = currentFormMsg;
        this.qrCallback = qrCallback;
        this.fragmentManager = fragmentManager;
        this.activity = activity;
        question.clear();
        try {
            if (currentFormMsg.getContentValue() != null && currentFormMsg.getContentValue().size() > 0 && currentFormMsg.getContentValue().get(0).getQuestions() != null) {
                for (int i = 0; i < currentFormMsg.getContentValue().get(0).getQuestions().size(); i++) {
                    if (currentFormMsg.getValues() != null && currentFormMsg.getValues().size() > i && isValidEmail(currentFormMsg.getValues().get(i), currentFormMsg.getContentValue().get(0).getParams().get(i))) {
                        question.add(new DataFormTicketAdapter.Question(currentFormMsg.getContentValue().get(0).getQuestions().get(i),
                                currentFormMsg.getValues().get(i),
                                currentFormMsg.getContentValue().get(0).getData_type().get(i), currentFormMsg.getContentValue().get(0).getParams().get(i), null, null));
                    } else {
                        question.add(new DataFormTicketAdapter.Question(currentFormMsg.getContentValue().get(0).getQuestions().get(i),
                                null, currentFormMsg.getContentValue().get(0).getData_type().get(i),
                                currentFormMsg.getContentValue().get(0).getParams().get(i),
                                currentFormMsg.getContentValue().get(0).getTextValue(),
                                currentFormMsg.getContentValue().get(0).getCountryCode()));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name = "";
        String email = "";
        for (int i = 0; i < question.size(); i++) {
            if (question.get(i).params.equalsIgnoreCase(FuguAppConstant.DataType.NAME)) {
                if (question.get(i).getAnswer() != null)
                    name = question.get(i).getAnswer();
            } else if (question.get(i).params.equalsIgnoreCase(FuguAppConstant.DataType.EMAIL)) {
                if (question.get(i).getAnswer() != null)
                    email = question.get(i).getAnswer();

            }
        }
//        if (!name.isEmpty() && !email.isEmpty() && !currentFormMsg.isCheckAndCreateCustomer()) {
//            checkAndCreateCustomer(name, email);
//        }
        if (!name.isEmpty() && !email.isEmpty() /*&& currentFormMsg.isEdited()*/) {
            int checkitem = 3;
            if (currentFormMsg.getContentValue().get(0).getQuestions().size() < 5)
                checkitem--;

            if (currentFormMsg.getValues().size() > checkitem /*&& currentFormMsg.isEdited()*/)
                checkAndCreateCustomer(name, email);
        }

    }

    private boolean isValidEmail(String value, String param) {
        if (currentFormMsg.getContentValue().get(0).getQuestions().size() != currentFormMsg.getValues().size()) {
            if (param.equalsIgnoreCase(FuguAppConstant.DataType.EMAIL)) {
                if (!Utils.isEmailValid(value))
                    return Utils.isEmailValid(value);
            } else if (param.equalsIgnoreCase(FuguAppConstant.DataType.NAME)) {
                return !value.equalsIgnoreCase("Visitor");
            }
        }
        return true;
    }

    private void checkAndCreateCustomer(String name, String email) {
        CommonParams.Builder params = new CommonParams.Builder();
//        params.add("access_token", MyApplication.getInstance().userData.accessToken)
        params.add("lang", "en")
                .add("app_secret_key", HippoConfig.getInstance().getAppKey())
                .add("customer_name", name)
                .add("customer_email", email)
                .build();

        CommonParams commonParams = params.build();
        RestClient.getApiInterface().checkAndCreateCustomer(commonParams.getMap())
                .enqueue(new ResponseResolver<CreateCustomerResponse>(activity, false, false) {

                    @Override
                    public void success(CreateCustomerResponse createCustomerResponse) {
                        currentFormMsg.setCheckAndCreateCustomer(true);
                        if (currentFormMsg.isEdited())
                            currentFormMsg.setEdited(false);
                    }

                    @Override
                    public void failure(APIError error) {

                    }
                });

    }


    @Override
    public DataFormTicketAdapter.QRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.hippo_item_data_ticket_form, parent, false);
        return new DataFormTicketAdapter.QRViewHolder(view, new DataFormTicketAdapter.MyFormEditTextListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final DataFormTicketAdapter.QRViewHolder holder, final int position) {
        if (Objects.equals(question.get(position).type, FuguAppConstant.DataType.PHONE)) {
            String text = Restring.getString(context, R.string.hippo_with_country_code);
            String enterNum = Restring.getString(context, R.string.hippo_enter_phone_number);
            holder.title.setText(question.get(position).getQuestion() + " " + text);
            holder.title.setText(enterNum);

        } else {
            holder.title.setText(question.get(position).getQuestion());
        }

        try {
            if (position == 0) {
                holder.countView.setVisibility(View.VISIBLE);
                holder.countView.setText(question.size() + "/" + currentFormMsg.getContentValue().get(0).getQuestions().size());
            } else {
                holder.countView.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

        holder.tvSkip.setText(Restring.getString(context, R.string.skip));
        holder.doneBT.setText(Restring.getString(context, R.string.vw_confirm));
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        holder.attachmentsRV.setLayoutManager(layoutManager);
        holder.attachmentsRV.setNestedScrollingEnabled(false);
        //holder.btnSkip.setVisibility(View.GONE);

        holder.etSearch.setTag(position);
        holder.etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int pos = (int) view.getTag();
                searchAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_selectable_list_item, searchItem);
                holder.etSearch.setAdapter(searchAdapter);
                searchItem.clear();
                getDropdownData(question.get(pos).params.equalsIgnoreCase(FuguAppConstant.DataType.ISSUETYPE), "", holder);

            }
        });

        holder.etInputData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (holder.updateIV.getVisibility() == View.VISIBLE) {
                    holder.updateIV.setVisibility(View.GONE);
//                    currentFormMsg.setCheckAndCreateCustomer(false);

                    holder.actionView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hippo_ic_arrow));
                    holder.arrowBgView.setVisibility(View.VISIBLE);
                    holder.actionView.setVisibility(View.VISIBLE);


                }

            }
        });


        holder.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() > 2) {
                    if (!holder.searchedText.equals(editable.toString())) {
                        holder.showDropdown = true;
                        holder.searchedText = editable.toString();
//                        if (!editable.toString().trim().equalsIgnoreCase(Restring.getString(context, R.string.fugu_no_data_found)))
                        getDropdownData(question.get(position).params.equalsIgnoreCase(FuguAppConstant.DataType.ISSUETYPE), editable.toString(), holder);
                    } else {
                        holder.showDropdown = false;
                    }
                } else if (editable.toString().trim().length() == 0) {
                    searchItem.clear();
                    holder.showDropdown = true;
                    holder.searchedText = "";
//                    if (!editable.toString().trim().equalsIgnoreCase(Restring.getString(context, R.string.fugu_no_data_found)))
                    getDropdownData(question.get(position).params.equalsIgnoreCase(FuguAppConstant.DataType.ISSUETYPE), "", holder);

                }

            }
        });

        holder.etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();
                if (!text.trim().equalsIgnoreCase(Restring.getString(context, R.string.fugu_no_data_found)) && !text.trim().equalsIgnoreCase(Restring.getString(context, R.string.fugu_no_option_available))) {
                    holder.etInputData.setText(text);
                    holder.etSearch.setEnoughFilter(false);
                    holder.etSearch.setText(text);
                } else {
                    holder.etSearch.setText("");
                    holder.etInputData.setText("");

                }

            }
        });
        holder.etInputData.setTag(position);
        holder.etInputData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int pos = (int) view.getTag();
                holder.myCustomEditTextListener.updatePosition(currentFormMsg, pos);

            }
        });

        if (currentFormMsg.getValues().size() != currentFormMsg.getContentValue().get(0).getQuestions().size()) {
            if (activity instanceof FuguChatActivity) {
                ((FuguChatActivity) activity).etMsg.setFocusable(false);
                ((FuguChatActivity) activity).etMsg.setFocusableInTouchMode(false);
                ((FuguChatActivity) activity).ivAttachment.setClickable(false);
            }
        } else {
            if (activity instanceof FuguChatActivity) {
                ((FuguChatActivity) activity).etMsg.setFocusable(true);
                ((FuguChatActivity) activity).etMsg.setFocusableInTouchMode(true);
                ((FuguChatActivity) activity).ivAttachment.setClickable(true);

            }
        }

        if (question.get(position).isAnswered()) {
            holder.etInputData.setText(question.get(position).getAnswer());
            holder.etInputData.setEnabled(false);
            holder.etInputData.setVisibility(View.VISIBLE);
            holder.etSearch.setVisibility(View.GONE);
            holder.updateIV.setVisibility(View.GONE);
            holder.etInputData.setFocusable(false);
            holder.etInputData.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            holder.etInputData.setClickable(false);
            holder.actionView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hippo_ic_tick));
            holder.arrowBgView.setVisibility(View.GONE);
            holder.doneBT.setVisibility(View.GONE);
            holder.countryView.setVisibility(View.GONE);
            switch (question.get(position).params) {

                case FuguAppConstant.DataType.EMAIL:
                case FuguAppConstant.DataType.NAME:
                    int sizecheck = 4;
                    if (currentFormMsg.getContentValue().get(0).getQuestions().size() < 5)
                        sizecheck = 3;
                    else
                        sizecheck = 4;

                    if (currentFormMsg.getValues().size() < sizecheck) {
                        holder.etInputData.setEnabled(true);
                        holder.etInputData.setVisibility(View.VISIBLE);
                        holder.etSearch.setVisibility(View.GONE);
                        holder.etInputData.setFocusable(true);
                        holder.actionView.setVisibility(View.GONE);
                        holder.updateIV.setVisibility(View.VISIBLE);
                        holder.etInputData.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                        holder.etInputData.setClickable(true);
                        holder.arrowBgView.setVisibility(View.VISIBLE);
                    } else {
                        holder.etInputData.setEnabled(false);
                        holder.etInputData.setVisibility(View.VISIBLE);
                        holder.etSearch.setVisibility(View.GONE);
                        holder.etInputData.setFocusable(false);
                        holder.updateIV.setVisibility(View.GONE);
                        holder.etInputData.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                        holder.etInputData.setClickable(false);
                        holder.arrowBgView.setVisibility(View.GONE);
                        holder.actionView.setVisibility(View.VISIBLE);
                        holder.actionView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hippo_ic_tick));

                    }
                    break;
                case FuguAppConstant.DataType.ATTACHMENT:
                    try {
                        String text = "N/A";
                        JSONArray jsonArr = null;
                        if (!question.get(position).getAnswer().equalsIgnoreCase("N/A")) {
                            jsonArr = new JSONArray(question.get(position).getAnswer());
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject jsonObj = jsonArr.getJSONObject(i);
                                if (text.equalsIgnoreCase("N/A"))
                                    text = jsonObj.getString("fileName");
                                else
                                    text = text + ", " + jsonObj.getString("fileName");
                            }
                        }
                        if (!text.isEmpty())
                            holder.tvError.setVisibility(View.GONE);
                        holder.etInputData.setText(text);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        } else {
            holder.etInputData.setHint(question.get(position).getQuestion());
            holder.myCustomEditTextListener.updatePosition(currentFormMsg, position);
            holder.etInputData.setEnabled(true);
            holder.etInputData.setSingleLine(true);
            holder.doneBT.setVisibility(View.GONE);
            holder.etInputData.setFocusable(true);
            holder.etInputData.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
            holder.etInputData.setClickable(true);
            holder.updateIV.setVisibility(View.GONE);
            holder.actionView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hippo_ic_arrow));
            holder.arrowBgView.setVisibility(View.VISIBLE);
            holder.etInputData.requestFocus();

            if (currentFormMsg.getIsSkipEvent() == 0 && currentFormMsg.isSkipButton() && position == question.size() - 1) {
                holder.btnSkip.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(question.get(position).text))
                holder.etInputData.setText(question.get(position).text);

            switch (question.get(position).params) {
                case FuguAppConstant.DataType.NUMBER:
                    holder.attachmentsRV.setVisibility(View.GONE);
                    holder.etInputData.setVisibility(View.VISIBLE);
                    holder.actionView.setVisibility(View.VISIBLE);
                    holder.etSearch.setVisibility(View.GONE);
                    holder.doneBT.setVisibility(View.GONE);
                    holder.arrowBgView.setVisibility(View.VISIBLE);
                    holder.etInputData.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    holder.etInputData.setSingleLine(true);
                    holder.etInputData.setFocusable(true);
                    holder.etInputData.setFocusableInTouchMode(true);
                    holder.etInputData.setClickable(true);
                    holder.tvSkip.setVisibility(View.GONE);
                    break;
                case FuguAppConstant.DataType.NAME:
                    holder.attachmentsRV.setVisibility(View.GONE);
                    holder.etInputData.setVisibility(View.VISIBLE);
                    holder.actionView.setVisibility(View.VISIBLE);
                    holder.etSearch.setVisibility(View.GONE);
                    holder.doneBT.setVisibility(View.GONE);
                    holder.arrowBgView.setVisibility(View.VISIBLE);
                    holder.etInputData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    holder.etInputData.setSingleLine(true);
                    holder.etInputData.setFocusable(true);
                    holder.etInputData.setFocusableInTouchMode(true);
                    holder.etInputData.setClickable(true);
                    holder.tvSkip.setVisibility(View.GONE);

                    if (CommonData.getUserDetails().getData().getFullName() != null && !CommonData.getUserDetails().getData().getFullName().isEmpty()) {
                        if (!CommonData.getUserDetails().getData().getFullName().equalsIgnoreCase("Visitor")) {
                            holder.etInputData.setText(CommonData.getUserDetails().getData().getFullName());
                            holder.actionView.callOnClick();
                        }
                    }

                    break;
                case FuguAppConstant.DataType.EMAIL:
                    holder.tvSkip.setVisibility(View.GONE);
                    holder.attachmentsRV.setVisibility(View.GONE);
                    holder.etInputData.setVisibility(View.VISIBLE);
                    holder.actionView.setVisibility(View.VISIBLE);
                    holder.etSearch.setVisibility(View.GONE);
                    holder.arrowBgView.setVisibility(View.VISIBLE);
                    holder.doneBT.setVisibility(View.GONE);
                    holder.etInputData.setFocusable(true);
                    holder.etInputData.setFocusableInTouchMode(true);
                    holder.etInputData.setClickable(true);
                    holder.etInputData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                    if (CommonData.getUserDetails().getData().getEmail() != null && !CommonData.getUserDetails().getData().getEmail().isEmpty()) {
                        holder.etInputData.setText(CommonData.getUserDetails().getData().getEmail());
                        holder.actionView.callOnClick();
                    }

                    break;
                case FuguAppConstant.DataType.PHONE:
                    holder.tvSkip.setVisibility(View.GONE);
                    holder.attachmentsRV.setVisibility(View.GONE);
                    holder.etInputData.setVisibility(View.VISIBLE);
                    holder.etSearch.setVisibility(View.GONE);
                    holder.actionView.setVisibility(View.VISIBLE);
                    holder.arrowBgView.setVisibility(View.VISIBLE);
                    holder.doneBT.setVisibility(View.GONE);
                    holder.etInputData.setFocusable(true);
                    holder.etInputData.setFocusableInTouchMode(true);
                    holder.etInputData.setClickable(true);
                    holder.etInputData.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_CLASS_NUMBER);
                    holder.countryView.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(question.get(position).countryCode))
                        holder.countryView.setText(question.get(position).countryCode);
                    else {
                        String code = getDefaultCode();
                        if (!TextUtils.isEmpty(code))
                            holder.countryView.setText(getDefaultCode());
                        else
                            holder.countryView.setText("+91");
                    }
                    break;
                case FuguAppConstant.DataType.ISSUETYPE:
                    holder.tvSkip.setVisibility(View.GONE);
                    holder.attachmentsRV.setVisibility(View.GONE);
                    holder.etInputData.setVisibility(View.GONE);
                    holder.arrowBgView.setVisibility(View.VISIBLE);
                    holder.doneBT.setVisibility(View.GONE);
                    holder.etSearch.setVisibility(View.VISIBLE);
                    holder.etInputData.setFocusable(true);
                    holder.actionView.setVisibility(View.VISIBLE);
                    holder.etInputData.setFocusableInTouchMode(true);
                    holder.etInputData.setClickable(true);
                    holder.etInputData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                    break;
                case FuguAppConstant.DataType.PRIORITY:
                    holder.tvSkip.setVisibility(View.VISIBLE);
                    holder.attachmentsRV.setVisibility(View.GONE);
                    holder.etInputData.setVisibility(View.GONE);
                    holder.arrowBgView.setVisibility(View.VISIBLE);
                    holder.doneBT.setVisibility(View.GONE);
                    holder.actionView.setVisibility(View.VISIBLE);
                    holder.etSearch.setVisibility(View.VISIBLE);
                    holder.etInputData.setFocusable(true);
                    holder.etInputData.setFocusableInTouchMode(true);
                    holder.etInputData.setClickable(true);
                    holder.etInputData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                    break;
                case FuguAppConstant.DataType.ATTACHMENT:
                    holder.tvSkip.setVisibility(View.VISIBLE);
                    holder.attachmentsRV.setVisibility(View.VISIBLE);
                    holder.etInputData.setFocusable(false);
                    holder.etInputData.setVisibility(View.VISIBLE);
                    holder.etSearch.setVisibility(View.GONE);
                    holder.actionView.setVisibility(View.GONE);
                    holder.arrowBgView.setVisibility(View.GONE);

                    if (question.get(position).isAttachmentUploading()) {
                        holder.uploadProgress.setVisibility(View.VISIBLE);
                        holder.doneBT.setVisibility(View.GONE);
                    } else {
                        holder.uploadProgress.setVisibility(View.GONE);
                        holder.doneBT.setVisibility(View.VISIBLE);
                    }

                    holder.etInputData.setFocusableInTouchMode(false);
                    holder.etInputData.setClickable(true);
                    holder.etInputData.setTag(position);
                    AttachmentsAdapter attachmentsAdapter = new AttachmentsAdapter(activity, question.get(position).getAttachments());
                    holder.attachmentsRV.setAdapter(attachmentsAdapter);

                    holder.etInputData.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!Utils.preventMultipleClicks()) {
                                return;
                            }
                            int pos = (int) view.getTag();
                            if (question.get(pos).attachments.size() < 5 && !question.get(pos).isAttachmentUploading())
                                openAttachmentChooser(pos, view);
                        }
                    });

                    if (question.get(position).attachments.size() > 0)
                        holder.tvError.setVisibility(View.GONE);

                    break;

            }
        }


    }

    private void getDropdownData(final boolean issueTye, String text, final DataFormTicketAdapter.QRViewHolder holder) {
        CommonParams.Builder params = new CommonParams.Builder();
        params.add("lang", "en")
                .add("reference", "Issue")
                .add("business_id", CommonData.getUserDetails().getData().getBusinessId())
                .add("text", text);

        if (issueTye) {
            params.add("type", "Issue Type");
        } else {
            params.add("type", "Issue Priority");
        }

        CommonParams commonParams = params.build();
        RestClient.getApiInterface().erpNextSearch(commonParams.getMap())
                .enqueue(new ResponseResolver<TicketPriorityOptionsData>(activity, false, false) {

                    @Override
                    public void success(TicketPriorityOptionsData ticketPriorityOptionsData) {
                        ArrayList<Result> result = ticketPriorityOptionsData.getData().getResults();

//                        if (issueTye) {
                        if (holder.showDropdown) {
                            searchItem.clear();
                            for (int i = 0; i < ticketPriorityOptionsData.getData().getResults().size(); i++) {
                                searchItem.add(ticketPriorityOptionsData.getData().getResults().get(i).getValue());
                            }

                            if (searchItem.size() == 0) {
                                if (issueTye)
                                    searchItem.add(Restring.getString(context, R.string.fugu_no_data_found));
                                else
                                    searchItem.add(Restring.getString(context, R.string.fugu_no_option_available));

                            }

                            searchAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_selectable_list_item, searchItem);
                            holder.etSearch.setAdapter(searchAdapter);
                            holder.etSearch.setEnoughFilter(true);
                            holder.etSearch.showDropDown();
                        }

                    }

                    @Override
                    public void failure(APIError error) {
                        searchItem.clear();
                        if (issueTye)
                            searchItem.add(Restring.getString(context, R.string.fugu_no_data_found));
                        else
                            searchItem.add(Restring.getString(context, R.string.fugu_no_option_available));

                        searchAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_selectable_list_item, searchItem);
                        holder.etSearch.setAdapter(searchAdapter);
                        holder.etSearch.setEnoughFilter(true);
                        holder.etSearch.showDropDown();
                    }
                });

    }

    private void openAttachmentChooser(int pos, View view) {
        if (question.get(pos).params.equalsIgnoreCase(FuguAppConstant.DataType.ATTACHMENT) && activity instanceof FuguChatActivity) {
            ((FuguChatActivity) activity).selectImage(view, true, this, pos);
        }
    }

    @Override
    public int getItemCount() {
        return question == null ? 0 : question.size();
    }

    /**
     * Method to check whether the
     *
     * @param data
     * @param tvError
     * @return
     */
    public boolean isValid(String data, EditText edittext, String dataType, TextView tvError) {
        if (data.isEmpty() && !dataType.equalsIgnoreCase(FuguAppConstant.DataType.ATTACHMENT) /*&& !dataType.equalsIgnoreCase(FuguAppConstant.DataType.PRIORITY)*/) {
            String text = Restring.getString(context, R.string.hippo_field_cant_empty);
            tvError.setText(text);
            tvError.setVisibility(View.VISIBLE);
            return false;
        }


        boolean isValid;
        String error = null;

        switch (dataType) {

            case FuguAppConstant.DataType.NUMBER:
                String numOnly = Restring.getString(context, R.string.hippo_enter_number_only);
                error = (isValid = Utils.isNumeric(data)) ? null : numOnly;
                break;

            case FuguAppConstant.DataType.EMAIL:
                String validEmail = Restring.getString(context, R.string.hippo_enter_valid);
                error = (isValid = Utils.isEmailValid(data)) ? null : validEmail;
                break;

            case FuguAppConstant.DataType.PHONE:
                String validPhn = Restring.getString(context, R.string.hippo_enter_valid_phn_no);
                error = (isValid = Utils.isValidPhoneNumber(data)) ? null : validPhn;
                break;

            default:
                isValid = true;
                break;
        }

        edittext.requestFocus();
        if (!TextUtils.isEmpty(error)) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(error);
        } else {
            tvError.setVisibility(View.GONE);
        }

        return isValid;
    }


    @Override
    public void onAttachmentListener(FileuploadModel fileuploadModel, int attachmentPosition) {
        question.get(attachmentPosition).setAttachmentUploading(false);
        question.get(attachmentPosition).attachments.add(fileuploadModel);
        notifyItemChanged(attachmentPosition);
    }

    @Override
    public void onAttachmentSelected(int attachmentPosition) {
        question.get(attachmentPosition).setAttachmentUploading(true);
        notifyItemChanged(attachmentPosition);
    }


    public class QRViewHolder extends RecyclerView.ViewHolder {
        public DataFormTicketAdapter.MyFormEditTextListener myCustomEditTextListener;
        ImageView actionView, arrowBgView, updateIV;
        private EditText etInputData;
        private TextView tvError, doneBT;
        private TextView title, tvSkip, countView, countryView;
        private TextView btnSkip;
        private RecyclerView attachmentsRV;
        private InstantAutoComplete etSearch;
        private ProgressBar uploadProgress;
        private boolean showDropdown = true;
        private String searchedText = "";


        public QRViewHolder(View itemView, DataFormTicketAdapter.MyFormEditTextListener myCustomEditTextListener) {
            super(itemView);
            etInputData = (EditText) itemView.findViewById(R.id.etInputData);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.etInputData.addTextChangedListener(myCustomEditTextListener);
            title = (TextView) itemView.findViewById(R.id.title_view);
            tvError = (TextView) itemView.findViewById(R.id.tvError);
            tvSkip = (TextView) itemView.findViewById(R.id.tvSkip);
            uploadProgress = (ProgressBar) itemView.findViewById(R.id.uploadProgress);
            updateIV = (ImageView) itemView.findViewById(R.id.updateIV);
            countView = itemView.findViewById(R.id.count_view);
            countryView = itemView.findViewById(R.id.country_picker);
            actionView = (ImageView) itemView.findViewById(R.id.action_view);
            arrowBgView = (ImageView) itemView.findViewById(R.id.arrow_background_view);
            doneBT = (TextView) itemView.findViewById(R.id.doneBT);
            btnSkip = itemView.findViewById(R.id.btnSkip);
            attachmentsRV = itemView.findViewById(R.id.attachmentsRV);
            etSearch = itemView.findViewById(R.id.etSearch);
            etSearch.setThreshold(1);
            tvSkip.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    if (isValid(currentFormMsg.getComment(), etInputData, question.get(getAdapterPosition()).params, tvError)) {
                    if (countryView.getVisibility() == View.VISIBLE) {
                        String message = currentFormMsg.getComment();
                        currentFormMsg.setComment(countryView.getText().toString().trim() + message);
                    }
                    currentFormMsg.setComment("N/A");

                    qrCallback.onFormClickListenerTicket(getAdapterPosition(), currentFormMsg, getAdapterPosition());
                    currentFormMsg.getContentValue().get(0).setTextValue(null);
                    currentFormMsg.getContentValue().get(0).setCountryCode(null);
                }
//                }
            });
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValid(currentFormMsg.getComment(), etInputData, question.get(getAdapterPosition()).params, tvError)) {

                        if (getAdapterPosition() < currentFormMsg.getValues().size())
                            currentFormMsg.setEdited(true);

                        if (countryView.getVisibility() == View.VISIBLE) {
                            String message = currentFormMsg.getComment();
                            currentFormMsg.setComment(countryView.getText().toString().trim() + message);
                        }

                        qrCallback.onFormClickListenerTicket(getAdapterPosition(), currentFormMsg, getAdapterPosition());
                        currentFormMsg.getContentValue().get(0).setTextValue(null);
                        currentFormMsg.getContentValue().get(0).setCountryCode(null);
                        searchItem.clear();
                    }
                }
            });
            doneBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isValid(currentFormMsg.getComment(), etInputData, question.get(getAdapterPosition()).params, tvError)) {
                        if (countryView.getVisibility() == View.VISIBLE) {
                            String message = currentFormMsg.getComment();
                            currentFormMsg.setComment(countryView.getText().toString().trim() + message);
                        }

                        switch (question.get(getAdapterPosition()).params) {
                            case FuguAppConstant.DataType.ATTACHMENT:
                                try {
                                    JSONArray attachmentArray = new JSONArray();
                                    if (question.get(getAdapterPosition()).getAttachments() == null || question.get(getAdapterPosition()).getAttachments().size() == 0) {
                                        tvError.setVisibility(View.VISIBLE);
                                        tvError.setText(Restring.getString(context, R.string.hippo_field_cant_empty));
                                        return;
                                    }
                                    for (int i = 0; i < question.get(getAdapterPosition()).getAttachments().size(); i++) {
                                        JSONObject obj = new JSONObject();

                                        obj.put("fileName", question.get(getAdapterPosition()).getAttachments().get(i).getFileName());
                                        obj.put("url", question.get(getAdapterPosition()).getAttachments().get(i).getUploadedUrl());
                                        attachmentArray.put(obj);
                                    }
                                    currentFormMsg.setComment(attachmentArray.toString());


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }

                        qrCallback.onFormClickListenerTicket(getAdapterPosition(), currentFormMsg, getAdapterPosition());
                        currentFormMsg.getContentValue().get(0).setTextValue(null);
                        currentFormMsg.getContentValue().get(0).setCountryCode(null);
                        searchItem.clear();
                    }
                }
            });


            updateIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionView.callOnClick();
                }
            });

            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qrCallback.skipFormCallback(currentFormMsg);
                }
            });

            countryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openPicker(countryView, etInputData);
                }
            });
        }
    }

    public class Question {
        String question;
        String answer;
        String type;
        String params;
        String text;
        String countryCode;
        boolean isAnswered;

        public boolean isAttachmentUploading() {
            return attachmentUploading;
        }

        public void setAttachmentUploading(boolean attachmentUploading) {
            this.attachmentUploading = attachmentUploading;
        }

        boolean attachmentUploading = false;

        public ArrayList<FileuploadModel> getAttachments() {
            return attachments;
        }

        ArrayList<FileuploadModel> attachments = new ArrayList<>();

        public Question(String question, String answer, String type, String params, String text, String countryCode) {
            this.question = question;
            this.answer = answer;
            this.type = type;
            this.params = params;
            this.text = text;
            this.countryCode = countryCode;
            this.attachmentUploading = false;
            if (!TextUtils.isEmpty(answer))
                isAnswered = true;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void addAttachment(FileuploadModel fileuploadModel) {
            attachments.add(fileuploadModel);
        }

        public void removeAttachment(int position) {
            attachments.remove(position);
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public boolean isAnswered() {
            return isAnswered;
        }

        public void setAnswered(boolean answered) {
            isAnswered = answered;
        }
    }

    public class MyFormEditTextListener implements TextWatcher {
        private Message currentOrderItem;
        private int position;

        public void updatePosition(Message currentOrderItem, int position) {
            this.currentOrderItem = currentOrderItem;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (currentOrderItem != null)
                currentOrderItem.setComment(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
            if (currentOrderItem != null)
                currentFormMsg.getContentValue().get(0).setTextValue(editable.toString());

        }
    }

    private String getDefaultCode() {
        try {
            CountryPicker countryPicker = new CountryPicker.Builder().with(context).build();
            return countryPicker.getCountryFromSIM().getDialCode();
        } catch (Exception e) {
            return "";
        }
    }

    private void openPicker(final TextView textView, final EditText editText) {
        CountryPicker countryPicker =
                new CountryPicker.Builder().with(context)
                        .sortBy(SORT_BY_NAME)
                        .listener(new OnCountryPickerListener() {
                            @Override
                            public void onSelectCountry(Country country) {
                                textView.setText(country.getDialCode());
                                currentFormMsg.getContentValue().get(0).setCountryCode(country.getDialCode());
                                editText.requestFocus();
                            }
                        })
                        .build();

        countryPicker.showDialog(fragmentManager);
    }
}
