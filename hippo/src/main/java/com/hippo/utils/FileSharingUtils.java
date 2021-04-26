//package com.hippo.utils;
//
//import android.animation.Animator;
//import android.app.ActionBar;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.SystemClock;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.*;
//import android.widget.LinearLayout;
//import com.hippo.R;
//import com.hippo.activity.HippoPaymentActivity;
//import com.hippo.adapter.FuguAttachmentAdapter;
//import com.hippo.constant.FuguAppConstant;
//import com.hippo.utils.filepicker.Constant;
//import com.hippo.utils.filepicker.activity.AudioPickActivity;
//import com.hippo.utils.filepicker.activity.ImagePickActivity;
//import com.hippo.utils.filepicker.activity.NormalFilePickActivity;
//import com.hippo.utils.filepicker.activity.VideoPickActivity;
//
//import static com.hippo.utils.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
//import static com.hippo.utils.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;
//import static com.hippo.utils.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;
//import static com.hippo.utils.filepicker.activity.ImagePickActivity.IS_NEED_IMAGE_PAGER;
//
///**
// * Created by gurmail on 09/01/19.
// *
// * @author gurmail
// */
//public class FileSharingUtils implements FuguAppConstant {
//
//    private static final String TAG = FileSharingUtils.class.getSimpleName();
//    private Long mLastClickTime = 0L;
//    private boolean isKeyBoardVisible;
//    private Context context;
//    private View ivAttachment;
//    private FuguImageUtils fuguImageUtils;
//
//    private Dialog dialog;
//    private View dialogView;
//    private boolean isPayment;
//
//    public FileSharingUtils(Context context, View ivAttachment, boolean isKeyBoardVisible,
//                            FuguImageUtils fuguImageUtils, boolean isPayment) {
//        this.context = context;
//        this.ivAttachment = ivAttachment;
//        this.isKeyBoardVisible = isKeyBoardVisible;
//        this.fuguImageUtils = fuguImageUtils;
//        this.isPayment = isPayment;
//    }
//
//    public void selectFiles(final FileSharingListner listner) {
//        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//            return;
//        }
//
//        if (isKeyBoardVisible) {
//            dialogView = View.inflate(context, R.layout.hippo_bottom_sheet_with_keyboard, null);
//        } else {
//            dialogView = View.inflate(context, R.layout.hippo_bottom_sheet, null);
//        }
//
//        mLastClickTime = SystemClock.elapsedRealtime();
//        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(dialogView);
//        Window window = dialog.getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
//        window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
//        WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.gravity = Gravity.BOTTOM;
//        window.setAttributes(wlp);
//        RecyclerView rvAttachment = dialog.findViewById(R.id.rvAttachment);
//        View viewBotton = dialog.findViewById(R.id.viewBottom);
//        rvAttachment.setLayoutManager(new GridLayoutManager(context, 3));
//        FuguAttachmentAdapter adapter = new FuguAttachmentAdapter(context, false, isPayment);
//        adapter.setOnAttachListener(new FuguAttachmentAdapter.OnAttachListener() {
//            @Override
//            public void onAttach(int action) {
//                switch (action) {
//                    case OPEN_CAMERA_ADD_IMAGE:
//                        if(listner != null)
//                            listner.onCameraOpened();
//                        dialog.dismiss();
//                        break;
//                    case OPEN_GALLERY_ADD_IMAGE:
//                        Intent intent1 = new Intent(context, ImagePickActivity.class);
//                        intent1.putExtra(IS_NEED_CAMERA, false);
//                        intent1.putExtra(IS_NEED_IMAGE_PAGER, true);
//                        intent1.putExtra(Constant.MAX_NUMBER, 1);
//                        intent1.putExtra(IS_NEED_FOLDER_LIST, false);
//                        ((Activity) context).startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
//                        dialog.dismiss();
//                        break;
//                    case SELECT_FILE:
//                        Intent intent4 = new Intent(context, NormalFilePickActivity.class);
//                        intent4.putExtra(Constant.MAX_NUMBER, 1);
//                        intent4.putExtra(IS_NEED_FOLDER_LIST, true);
////                        intent4.putExtra(NormalFilePickActivity.SUFFIX,
////                                new String[] {"txt", "doc", "dOcX"});
//                        intent4.putExtra(NormalFilePickActivity.SUFFIX,
//                                new String[] {"txt", "xlsx", "xls", "doc", "docX", "ppt", ".pptx", "pdf",
//                                        "ODT", "apk", "zip", "CSV", "SQL", "PSD"});
//
//                        ((Activity) context).startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
//                        dialog.dismiss();
//                        break;
//                    case SELECT_AUDIO:
//                        Intent intent3 = new Intent(context, AudioPickActivity.class);
//                        intent3.putExtra(IS_NEED_RECORDER, false);
//                        intent3.putExtra(Constant.MAX_NUMBER, 1);
//                        intent3.putExtra(IS_NEED_FOLDER_LIST, true);
//                        ((Activity) context).startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
//                        dialog.dismiss();
//                        break;
//                    case SELECT_VIDEO:
//                        Intent intent2 = new Intent(context, VideoPickActivity.class);
//                        intent2.putExtra(IS_NEED_CAMERA, false);
//                        intent2.putExtra(Constant.MAX_NUMBER, 1);
//                        intent2.putExtra(IS_NEED_FOLDER_LIST, true);
//                        ((Activity) context).startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
//                        dialog.dismiss();
//                        break;
//                    case SELECT_PAYMENT:
//                        Intent paymentIntent = new Intent(context, HippoPaymentActivity.class);
//                        ((Activity) context).startActivityForResult(paymentIntent, Constant.REQUEST_CODE_PICK_PAYMENT);
//                        dialog.dismiss();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        rvAttachment.setAdapter(adapter);
//
//
//        viewBotton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                revealShow(dialogView, false, null);
//                dialog.dismiss();
//            }
//        });
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                revealShow(dialogView, true, null);
//            }
//        });
//
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
//                if(keyCode == KeyEvent.KEYCODE_BACK) {
//                    revealShow(dialogView, false, dialog);
//                    dialog.dismiss();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();
//    }
//
//
//    private void revealShow(View dialogView, boolean b, final Dialog dialog) {
//        final LinearLayout view = dialogView.findViewById(R.id.dialog);
//        int w = view.getWidth();
//        float h = view.getHeight();
//
//        float endRadius = (float) Math.hypot((double) w, (double) h);
//        int cx = ((int)(ivAttachment.getX() + ivAttachment.getWidth() / 2));
//        int cy = ((int) ivAttachment.getY()) + ivAttachment.getHeight() + 56;
//
//        if (b) {
//            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx + 40, cy + 400, 0f, endRadius);
//            view.setVisibility(View.VISIBLE);
//            revealAnimator.setDuration(450);
//            revealAnimator.start();
//        } else {
//            Animator anim = null;
//            if (isKeyBoardVisible) {
//                anim = ViewAnimationUtils.createCircularReveal(view, cx + 40, cy - 100, endRadius, 0f);
//            } else {
//                anim = ViewAnimationUtils.createCircularReveal(view, cx + 40, cy + 400, endRadius, 0f);
//            }
//            anim.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    view.setVisibility(View.INVISIBLE);
//                    //dialog.dismiss();
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            });
//            anim.setDuration(450);
//            anim.start();
//        }
//    }
//
//    public interface FileSharingListner {
//        void onCameraOpened();
//    }
//
//}
