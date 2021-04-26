package com.hippo.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hippo.HippoColorConfig;
import com.hippo.R;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.interfaces.CustomerInitalListener;
import com.hippo.langs.Restring;
import com.hippo.model.Button;
import com.hippo.model.Field;
import com.hippo.support.Utils.Constants;
import com.hippo.utils.Utils;
import com.hippo.utils.countrypicker.Country;
import com.hippo.utils.countrypicker.CountryPicker;
import com.hippo.utils.countrypicker.OnCountryPickerListener;

import java.util.ArrayList;

import static android.view.View.FOCUS_FORWARD;
import static com.hippo.utils.countrypicker.CountryPicker.SORT_BY_NAME;

/**
 * Created by gurmail on 14/02/19.
 *
 * @author gurmail
 */
public class CustomerInitalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FuguAppConstant {
    private static final String TAG = CustomerInitalAdapter.class.getSimpleName();
    private ArrayList<Object> arrayList = new ArrayList<>();
    private static final int FIELD_TYPE = 1;
    private static final int BUTTON_TYPE = 2;
    private Context mContext;
    private HippoColorConfig hippoColorConfig;
    private FragmentManager fragmentManager;
    private CustomerInitalListener initalListener;
    private boolean hasFocus = false;

    public CustomerInitalAdapter(ArrayList<Object> arrayList, FragmentManager fragmentManager, CustomerInitalListener initalListener) {
        hippoColorConfig = CommonData.getColorConfig();
        this.arrayList = arrayList;
        this.fragmentManager = fragmentManager;
        this.initalListener = initalListener;
        hasFocus = false;
    }

    public void setDataSet(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if(viewType == FIELD_TYPE) {
            return new Fields(LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_fields_list, parent, false), new MyFormEditTextListener());
        } else {
            return new Buttons(LayoutInflater.from(mContext).inflate(R.layout.layout_button_list, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final int itemType = getItemViewType(position);
        switch (itemType) {
            case FIELD_TYPE:
                Fields holder = (Fields) viewHolder;
                Field field = (Field) arrayList.get(position);

                holder.title.setText(field.getTitle());
                if(field.getType().toLowerCase().equalsIgnoreCase("LABEL")) {
                    holder.rlFeild.setVisibility(View.GONE);
                    holder.description.setVisibility(View.VISIBLE);
                    holder.description.setText(field.getDescription());
                    holder.tvError.setVisibility(View.GONE);
                } else {
                    holder.rlFeild.setVisibility(View.VISIBLE);
                    holder.description.setVisibility(View.GONE);

                    if(!TextUtils.isEmpty(field.getTextValue())) {
                        holder.editText.setText(field.getTextValue());
                    } else {
                        holder.editText.setText("");
                    }

                    holder.myCustomEditTextListener.updatePosition(field, position, holder.tvError);
                    holder.editText.setHint(field.getPlaceholder());
                    holder.countryView.setVisibility(View.GONE);
                    setFieldInputType(field, holder.editText, holder.countryView, field.getValidationType(), position);


                    if(!TextUtils.isEmpty(field.getErrorText())) {
                        holder.tvError.setVisibility(View.VISIBLE);
                        holder.tvError.setText(field.getErrorText());
                        if(!hasFocus) {
                            holder.editText.requestFocus();
                            hasFocus = true;
                        }
                    } else {
                        holder.tvError.setVisibility(View.GONE);
                    }
                }


                holder.title.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
                holder.description.setTextColor(hippoColorConfig.getHippoTextColorSecondary());
                holder.editText.setTextColor(hippoColorConfig.getHippoTextColorPrimary());


                break;
            case BUTTON_TYPE:
                Buttons vholder = (Buttons) viewHolder;
                Button button = (Button) arrayList.get(position);
                vholder.compatButton.setText(button.getTitle());
                vholder.compatButton.setTextColor(hippoColorConfig.getHippoActionBarText());
                int radius = (int) Constants.convertDpToPixel(1);
                GradientDrawable drawable = (GradientDrawable) vholder.compatButton.getBackground();
                drawable.setStroke(radius, hippoColorConfig.getHippoActionBarText());
                drawable.setColor(hippoColorConfig.getHippoActionBarBg());

                break;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList.get(position) instanceof Field) {
            return FIELD_TYPE;
        } else {
            return BUTTON_TYPE;
        }
    }

    public class Fields extends RecyclerView.ViewHolder {

        private RelativeLayout rlFeild;
        private MyFormEditTextListener myCustomEditTextListener;
        private TextInputEditText editText;
        private TextView title;
        private TextView description;
        private TextView countryView;
        private TextView tvError;

        public Fields(@NonNull View itemView, MyFormEditTextListener myCustomEditTextListener) {
            super(itemView);
            rlFeild = itemView.findViewById(R.id.rlFeild);
            editText = itemView.findViewById(R.id.field_view);
            title = itemView.findViewById(R.id.title_view);
            description = itemView.findViewById(R.id.title_description);
            countryView = itemView.findViewById(R.id.country_picker);
            tvError = itemView.findViewById(R.id.tvError);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.editText.addTextChangedListener(myCustomEditTextListener);
            countryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openPicker(countryView, editText, getAdapterPosition());
                }
            });

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        View v1 = v.focusSearch(FOCUS_FORWARD);
                        if (v1 != null) {
                            if (!v.requestFocus(FOCUS_FORWARD)) {
                                return true;
                            }
                        }
                        return false;
                    } else return false;
                }
            });
        }
    }

    public class Buttons extends RecyclerView.ViewHolder {
        private AppCompatButton compatButton;
        public Buttons(@NonNull View itemView) {
            super(itemView);
            compatButton = itemView.findViewById(R.id.button_view);
            compatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkValidValues()) {
                        if(initalListener != null)
                            initalListener.onButtonClicked(arrayList);
                    } else {
                        hasFocus = false;
                        if(initalListener != null)
                            initalListener.onNotifyAdapter(arrayList);
                    }
                }
            });
        }
    }

    public boolean checkValidValues() {
        boolean isValid = true;
        boolean isValidCheck = true;
        String error = null;

        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i) instanceof Field) {
                Field field = (Field) arrayList.get(i);
                String data = field.getTextValue();
                if(!TextUtils.isEmpty(data))
                    data = data.trim();
                String dataType = field.getValidationType();
                if(field.getType().toLowerCase().equalsIgnoreCase("label")) {
                    continue;
                }
                if(field.getIsRequired() && TextUtils.isEmpty(data)) {
                    field.setErrorText("Field can't be empty");
                    isValidCheck = false;
                    continue;
                } else {
                    field.setErrorText("");
                }

                switch (dataType.toLowerCase()) {
                    case DataType.NUMBER:
                    case DataType._NUMBER:
                        String text = Restring.getString(mContext, R.string.hippo_enter_number_only);
                        error = (isValid = Utils.isNumeric(data)) ? null : text;
                        if(!isValid) {
                            isValidCheck = false;
                            field.setErrorText(error);
                        }
                    break;
                    case DataType.EMAIL:
                        String email = "email";
                        if(!TextUtils.isEmpty(field.getTitle()))
                            email = field.getTitle().toLowerCase();
                        String validEmail = Restring.getString(mContext, R.string.hippo_enter_valid_email);
                        error = (isValid = Utils.isEmailValid(data)) ? null : validEmail;
                        if(!isValid) {
                            isValidCheck = false;
                            field.setErrorText(error);
                        }
                    break;
                    case DataType.PHONE_NUMBER:
                    case DataType.PHONE:
                        String phone = "phone number";
                        if(!TextUtils.isEmpty(field.getTitle()))
                            phone = field.getTitle().toLowerCase();
                        String validPhn = Restring.getString(mContext, R.string.hippo_enter_valid_phn_no);
                        error = (isValid = Utils.isValidPhoneNumber(data)) ? null : validPhn;
                        if(!isValid) {
                            isValidCheck = false;
                            field.setErrorText(error);
                        }
                    break;
                    default:

                    break;
                }

            }
        }
        return isValidCheck;
    }

    
    private void setFieldInputType(Field field, TextInputEditText etInputData, TextView countryView, String type, int pos) {
        switch (type.toLowerCase()) {
            case DataType.NUMBER:
            case DataType._NUMBER:
                etInputData.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etInputData.setSingleLine(true);
                break;
            case DataType.EMAIL:
                etInputData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case DataType.PHONE_NUMBER:
            case DataType.PHONE:
                etInputData.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_CLASS_NUMBER);
                if(field.getType().equalsIgnoreCase("TEXTFIELD")) {
                    countryView.setVisibility(View.GONE);
                } else {
                    countryView.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(field.getCountryCode())) {
                        countryView.setText(field.getCountryCode());
                    } else {
                        String code = getDefaultCode();
                        code = !TextUtils.isEmpty(code) ? code : "+1";
                        countryView.setText(code);
                        ((Field) arrayList.get(pos)).setCountryCode(code);
                    }
                }
                break;
            default:
                etInputData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
    }

    public class MyFormEditTextListener implements TextWatcher {
        private Field field;
        private int position;
        private TextView errorView;

        public void updatePosition(Field field, int position, TextView errorView) {
            this.field = field;
            this.position = position;
            this.errorView = errorView;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence.length() > 0 && field != null && errorView.getVisibility() == View.VISIBLE) {
                errorView.setVisibility(View.GONE);
                ((Field) arrayList.get(position)).setErrorText("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
            if (field != null) {
                ((Field) arrayList.get(position)).setTextValue(editable.toString());
            }

        }
    }

    private String getDefaultCode() {
        try {
            CountryPicker countryPicker = new CountryPicker.Builder().with(mContext).build();
            return countryPicker.getCountryFromSIM().getDialCode();
        } catch (Exception e) {
            return "";
        }
    }

    private void openPicker(final TextView textView, final EditText editText, final int pos) {
        CountryPicker countryPicker =
                new CountryPicker.Builder().with(mContext)
                        .sortBy(SORT_BY_NAME)
                        .canSearch(true)
                        .listener(new OnCountryPickerListener() {
                            @Override
                            public void onSelectCountry(Country country) {
                                textView.setText(country.getDialCode());
                                ((Field) arrayList.get(pos)).setCountryCode(country.getDialCode());
                                editText.requestFocus();
                            }
                        })
                        .build();

        countryPicker.showDialog(fragmentManager);
    }
}
