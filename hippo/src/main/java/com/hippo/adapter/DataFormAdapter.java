package com.hippo.adapter;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hippo.R;
import com.hippo.activity.VideoPlayerActivity;
import com.hippo.constant.FuguAppConstant;
import com.hippo.langs.Restring;
import com.hippo.model.Message;
import com.hippo.utils.Utils;
import com.hippo.utils.countrypicker.Country;
import com.hippo.utils.countrypicker.CountryPicker;
import com.hippo.utils.countrypicker.OnCountryPickerListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.hippo.utils.countrypicker.CountryPicker.SORT_BY_NAME;


public class DataFormAdapter extends RecyclerView.Adapter<DataFormAdapter.QRViewHolder> {


    private static final String TAG = DataFormAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<String> arrayList;
    private QRCallback qrCallback;
    private ArrayList<Question> question = new ArrayList<>();
    private Message currentFormMsg;
    private FragmentManager fragmentManager;

    public DataFormAdapter(Message currentFormMsg, QRCallback qrCallback, FragmentManager fragmentManager) {
        this.currentFormMsg = currentFormMsg;
        this.qrCallback = qrCallback;
        this.fragmentManager = fragmentManager;

        question.clear();
        try {
            if (currentFormMsg.getContentValue() != null && currentFormMsg.getContentValue().size() > 0 && currentFormMsg.getContentValue().get(0).getQuestions() != null) {
                for (int i = 0; i < currentFormMsg.getContentValue().get(0).getQuestions().size(); i++) {
                    if (currentFormMsg.getValues() != null && currentFormMsg.getValues().size() > i) {
                        question.add(new Question(currentFormMsg.getContentValue().get(0).getQuestions().get(i),
                                currentFormMsg.getValues().get(i),
                                currentFormMsg.getContentValue().get(0).getData_type().get(i), null, null));
                    } else {
                        question.add(new Question(currentFormMsg.getContentValue().get(0).getQuestions().get(i),
                                null, currentFormMsg.getContentValue().get(0).getData_type().get(i),
                                currentFormMsg.getContentValue().get(0).getTextValue(),
                                currentFormMsg.getContentValue().get(0).getCountryCode()));
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public QRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.hippo_item_data_form, parent, false);
        return new QRViewHolder(view, new MyFormEditTextListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final QRViewHolder holder, final int position) {
        if (Objects.equals(question.get(position).type, FuguAppConstant.DataType.PHONE)) {
            String text = Restring.getString(context, R.string.hippo_with_country_code);
            String enterNum = Restring.getString(context, R.string.hippo_enter_phone_number);
            holder.title.setText(question.get(position).getQuestion() + " "+text);
            holder.title.setText(enterNum);

        } else {
            holder.title.setText(question.get(position).getQuestion());
        }

        try {
            if(position == 0) {
                holder.countView.setVisibility(View.VISIBLE);
                holder.countView.setText(question.size()+"/"+currentFormMsg.getContentValue().get(0).getQuestions().size());
            } else {
                holder.countView.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

        //holder.btnSkip.setVisibility(View.GONE);

        if (question.get(position).isAnswered()) {
            holder.etInputData.setText(question.get(position).getAnswer());
            holder.etInputData.setEnabled(false);
            holder.etInputData.setFocusable(false);
            holder.etInputData.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            holder.etInputData.setClickable(false);
            holder.actionView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hippo_ic_tick));
            holder.arrowBgView.setVisibility(View.GONE);
            holder.countryView.setVisibility(View.GONE);
        } else {
            holder.etInputData.setHint(question.get(position).getQuestion());
            holder.myCustomEditTextListener.updatePosition(currentFormMsg, position);
            holder.etInputData.setEnabled(true);
            holder.etInputData.setSingleLine(true);

            holder.etInputData.setFocusable(true);
            holder.etInputData.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
            holder.etInputData.setClickable(true);
            holder.actionView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hippo_ic_arrow));
            holder.arrowBgView.setVisibility(View.VISIBLE);

            if(currentFormMsg.getIsSkipEvent() == 0 && currentFormMsg.isSkipButton() && position == question.size()-1) {
                holder.btnSkip.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(question.get(position).text))
                holder.etInputData.setText(question.get(position).text);

            switch (question.get(position).type) {
                case FuguAppConstant.DataType.NUMBER:
                    holder.etInputData.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    holder.etInputData.setSingleLine(true);
                    break;
                case FuguAppConstant.DataType.EMAIL:
                    holder.etInputData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    break;
                case FuguAppConstant.DataType.PHONE:
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
            }
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
        if (data.isEmpty()) {
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

    public class QRViewHolder extends RecyclerView.ViewHolder {
        public MyFormEditTextListener myCustomEditTextListener;
        ImageView actionView, arrowBgView;
        private EditText etInputData;
        private TextView tvError;
        private TextView title, countView, countryView;
        private TextView btnSkip;

        public QRViewHolder(View itemView, MyFormEditTextListener myCustomEditTextListener) {
            super(itemView);
            etInputData = (EditText) itemView.findViewById(R.id.etInputData);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.etInputData.addTextChangedListener(myCustomEditTextListener);
            title = (TextView) itemView.findViewById(R.id.title_view);
            tvError = (TextView) itemView.findViewById(R.id.tvError);
            countView = itemView.findViewById(R.id.count_view);
            countryView = itemView.findViewById(R.id.country_picker);
            actionView = (ImageView) itemView.findViewById(R.id.action_view);
            arrowBgView = (ImageView) itemView.findViewById(R.id.arrow_background_view);
            btnSkip = itemView.findViewById(R.id.btnSkip);

            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValid(currentFormMsg.getComment(), etInputData, question.get(getAdapterPosition()).type, tvError)) {
                        if(countryView.getVisibility() == View.VISIBLE) {
                            String message = currentFormMsg.getComment();
                            currentFormMsg.setComment(countryView.getText().toString().trim() + message);
                        }
                        qrCallback.onFormClickListener(getAdapterPosition(), currentFormMsg);
                        currentFormMsg.getContentValue().get(0).setTextValue(null);
                        currentFormMsg.getContentValue().get(0).setCountryCode(null);
                    }
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
        String text;
        String countryCode;
        boolean isAnswered;

        public Question(String question, String answer, String type, String text, String countryCode) {
            this.question = question;
            this.answer = answer;
            this.type = type;
            this.text = text;
            this.countryCode = countryCode;
            if (!TextUtils.isEmpty(answer))
                isAnswered = true;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
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
