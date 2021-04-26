package com.hippo.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hippo.R;
import com.hippo.activity.FuguChatActivity;
import com.hippo.constant.FuguAppConstant;
import com.hippo.database.CommonData;
import com.hippo.langs.Restring;
import com.hippo.model.Message;
import com.hippo.utils.DateUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.Date;


/**
 * Created by gurmail on 2019-08-06.
 *
 * @author gurmail
 */
public class BottomSheetMsgFragment extends BottomSheetDialogFragment implements FuguAppConstant, View.OnClickListener {
    private LinearLayout copy, delete, edit;
    private FuguChatActivity fuguChatActivity;
    private int position;
    private String Muid;
    private int messsagetype;
    private boolean IsSelf;
    private int MessageStatus;
    private String SentAtUtc;
    private TextView tvDelete;
    private TextView ivEditTxt;
    private TextView copyTxt;
    private Boolean isReplied;
    private Boolean isAlreadyDelete;


    public BottomSheetMsgFragment() {

    }

    public static BottomSheetMsgFragment newInstance(int pos, Message message, boolean isSelf, boolean isReplied, boolean isAlreadyDelete) {
        BottomSheetMsgFragment frag = new BottomSheetMsgFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setPostion(pos);
        frag.setMuid(message.getMuid());
        frag.setIsSelf(isSelf);
        frag.setMessageType(message.getOriginalMessageType());
        frag.setMessageStatus(message.getMessageStatus());
        frag.setSentAtUtc(message.getSentAtUtc());
        frag.setReplied(isReplied);
        frag.setAlreadyDelete(isAlreadyDelete);
        return frag;
    }

    private void setAlreadyDelete(boolean isAlreadyDelete) {
        this.isAlreadyDelete = isAlreadyDelete;
    }

    private void setReplied(boolean isReplied) {
        this.isReplied = isReplied;
    }

    private void setSentAtUtc(String sentAtUtc) {
        SentAtUtc = sentAtUtc;
    }

    private void setMessageStatus(int messsageStatus) {
        MessageStatus = messsageStatus;
    }

    private void setIsSelf(boolean isSelf) {
        IsSelf = isSelf;
    }

    private void setMessageType(int messageType) {
        messsagetype = messageType;
    }

    private void setMuid(String muid) {
        Muid = muid;
    }

    private void setPostion(int pos) {
        position = pos;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hippo_message_bottom_sheet, container, false);
        fuguChatActivity = (FuguChatActivity) getActivity();
        copy = view.findViewById(R.id.copy);
        delete = view.findViewById(R.id.delete);
        edit = view.findViewById(R.id.edit);

        copyTxt = view.findViewById(R.id.copyTxt);
        tvDelete = view.findViewById(R.id.tvDelete);
        ivEditTxt = view.findViewById(R.id.ivEditTxt);

        copyTxt.setText(Restring.getString(getActivity(), R.string.hippo_copy_text));
        tvDelete.setText(Restring.getString(getActivity(), R.string.hippo_delete_text));
        ivEditTxt.setText(Restring.getString(getActivity(), R.string.hippo_edit_text));

        tvDelete = view.findViewById(R.id.tvDelete);

        String localDate = DateUtils.getFormattedDate(new Date());
        int newTime = DateUtils.getTimeInMinutes(DateUtils.getInstance().convertToUTC(localDate));
        int oldTime = DateUtils.getTimeInMinutes(SentAtUtc);


        int editableDuration = CommonData.getUserDetails().getData().getEditDeleteDuration();

        if(!isAlreadyDelete && IsSelf && messsagetype == 1 && editableDuration > -1 && (editableDuration == 0 || (Math.abs(newTime - oldTime)) <= editableDuration)) {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

        copy.setOnClickListener(this);
        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        int id = v.getId();
        if (id == R.id.edit) {
            fuguChatActivity.editText(position);
        } else if (id == R.id.copy) {
            fuguChatActivity.copyText(position, isReplied);
            Toast.makeText(getActivity(), Restring.getString(getActivity(), R.string.hippo_copy_to_clipboard), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.delete) {
            if (fuguChatActivity.isNetworkAvailable() || !(MessageStatus == MESSAGE_SENT || MessageStatus == MESSAGE_DELIVERED || MessageStatus == MESSAGE_READ)) {
                    new AlertDialog.Builder(fuguChatActivity)
                            .setMessage(Restring.getString(fuguChatActivity, R.string.hippo_delete_this_message))
                            .setPositiveButton(Restring.getString(fuguChatActivity, R.string.hippo_delete_for_everyone), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    fuguChatActivity.deleteMessage(position, Muid, MessageStatus);
                                }
                            }).setNegativeButton(Restring.getString(fuguChatActivity, R.string.hippo_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                } else {
                    new AlertDialog.Builder(fuguChatActivity)
                            .setMessage(Restring.getString(fuguChatActivity, R.string.fugu_not_connected_to_internet))
                            .setPositiveButton(Restring.getString(fuguChatActivity, R.string.fugu_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (fuguChatActivity != null) {
            fuguChatActivity.setOnLongClickValue();
        }
        super.onCancel(dialog);
    }
}
