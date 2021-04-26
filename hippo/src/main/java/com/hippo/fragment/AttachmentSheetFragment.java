package com.hippo.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hippo.R;
import com.hippo.activity.FuguChatActivity;
import com.hippo.langs.Restring;

/**
 * Created by gurmail on 2020-01-28.
 *
 * @author gurmail
 */
public class AttachmentSheetFragment extends BottomSheetDialogFragment {

    private LinearLayout llCamera;
    private LinearLayout llGallery;
    private LinearLayout llVideo;
    private LinearLayout llAudio;
    private LinearLayout llFiles;

    private AppCompatTextView cameraTxt, photoTxt, videoTxt, audioTxt, fileTxt;


    public static AttachmentSheetFragment newInstance() {
        AttachmentSheetFragment frag = new AttachmentSheetFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new BottomSheetDialog(requireContext(), getTheme());
        return inflater.inflate(R.layout.attachmnet_bottom_sheet, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llCamera = view.findViewById(R.id.llCamera);
        llGallery = view.findViewById(R.id.llGallery);
        llAudio = view.findViewById(R.id.llAudio);
        llFiles = view.findViewById(R.id.llFiles);
        llVideo = view.findViewById(R.id.llVideo);

        cameraTxt = view.findViewById(R.id.cameraTxt);
        photoTxt = view.findViewById(R.id.photoTxt);
        videoTxt = view.findViewById(R.id.videoTxt);
        audioTxt = view.findViewById(R.id.audioTxt);
        fileTxt = view.findViewById(R.id.fileTxt);

        cameraTxt.setText(Restring.getString(getActivity(), R.string.fugu_camera));
        photoTxt.setText(Restring.getString(getActivity(), R.string.fugu_gallery));
        videoTxt.setText(Restring.getString(getActivity(), R.string.hippo_video));
        audioTxt.setText(Restring.getString(getActivity(), R.string.fugu_audio));
        fileTxt.setText(Restring.getString(getActivity(), R.string.hippo_files));

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FuguChatActivity activity = (FuguChatActivity) getActivity();
                    activity.openScreenFromSheet(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FuguChatActivity activity = (FuguChatActivity) getActivity();
                    activity.openScreenFromSheet(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
        llAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FuguChatActivity activity = (FuguChatActivity) getActivity();
                    activity.openScreenFromSheet(3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
        llFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FuguChatActivity activity = (FuguChatActivity) getActivity();
                    activity.openScreenFromSheet(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
        llVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FuguChatActivity activity = (FuguChatActivity) getActivity();
                    activity.openScreenFromSheet(4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}
