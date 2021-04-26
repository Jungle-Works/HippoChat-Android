package com.hippo.support.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hippo.ChatByUniqueIdAttributes;
import com.hippo.HippoColorConfig;
import com.hippo.HippoConfig;
import com.hippo.R;
import com.hippo.database.CommonData;
import com.hippo.langs.Restring;
import com.hippo.support.Adapter.HippoSupportAdapter;
import com.hippo.support.Utils.Constants;
import com.hippo.support.Utils.SupportKeys;
import com.hippo.support.callback.HippoSupportView;
import com.hippo.support.callback.OnActionTypeCallback;
import com.hippo.support.callback.OnItemListener;
import com.hippo.support.callback.SupportPresenter;
import com.hippo.support.logicImplView.HippoSupportInteractorImpl;
import com.hippo.support.logicImplView.HippoSupportViewImpl;
import com.hippo.support.model.Category;
import com.hippo.support.model.Item;
import com.hippo.support.model.SupportDataList;
import com.hippo.support.model.callbackModel.OpenChatParams;
import com.hippo.support.model.callbackModel.SendQueryChat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.hippo.constant.FuguAppConstant.FUGU_WEBSITE_URL;
import static com.hippo.database.CommonData.removeLastPath;
import static com.hippo.support.Utils.SupportKeys.SupportKey.DEFAULT_SUPPORT;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_CATEGORY_DATA;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_CATEGORY_ID;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_DB_VERSION;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_POWERED_VIA;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_TITLE;
import static com.hippo.support.Utils.SupportKeys.SupportKey.SUPPORT_TRANSACTION_ID;

/**
 * Created by Gurmail S. Kang on 29/03/18.
 * @author gurmail
 */

public class HippoSupportFragment extends BaseFragment implements HippoSupportView, OnItemListener, View.OnClickListener {

    private static final String TAG = HippoSupportFragment.class.getSimpleName();
    private HippoSupportAdapter supportAdapter;
    private SupportPresenter supportView;
    private OnActionTypeCallback typeCallback;
    private HippoColorConfig hippoColorConfig;
    private String categoryData;

    private int serverDBVersion = -1;
    private String defaultFaqName = null;
    private String transactionId = null;
    private boolean hasPoweredVia;

    private Toolbar toolbar;

    private View rootView;
    private TextView poweredBy, mNoDataTextView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private Gson gson;

    private ArrayList<Item> supportResponses;
    private ArrayList<String> pathList;
    private String title = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        if(getArguments() != null) {
            if(getArguments().containsKey(DEFAULT_SUPPORT))
                supportResponses = gson.fromJson(getArguments().getString(DEFAULT_SUPPORT), Constants.listType);

            if(getArguments().containsKey(SUPPORT_TITLE))
                title = getArguments().getString(SUPPORT_TITLE);

            if(getArguments().containsKey(SUPPORT_DB_VERSION))
                serverDBVersion = getArguments().getInt(SUPPORT_DB_VERSION);

            if(getArguments().containsKey(SUPPORT_TRANSACTION_ID))
                transactionId = getArguments().getString(SUPPORT_TRANSACTION_ID);

            if(getArguments().containsKey(SUPPORT_CATEGORY_ID))
                defaultFaqName = getArguments().getString(SUPPORT_CATEGORY_ID);

            if(getArguments().containsKey(SUPPORT_CATEGORY_DATA))
                categoryData = getArguments().getString(SUPPORT_CATEGORY_DATA);

            if(getArguments().containsKey(SUPPORT_POWERED_VIA))
                hasPoweredVia = getArguments().getBoolean(SUPPORT_POWERED_VIA);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fugu_support_fragment, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        typeCallback = (OnActionTypeCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(typeCallback != null)
            typeCallback = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            removeLastPath();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = rootView.findViewById(R.id.my_toolbar);
        progressBar = rootView.findViewById(R.id.progress_bar);

        supportAdapter = new HippoSupportAdapter(this);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(supportAdapter);
        mNoDataTextView = rootView.findViewById(R.id.tvNoDataFound);
        hippoColorConfig = CommonData.getColorConfig();
        mNoDataTextView.setTextColor(hippoColorConfig.getHippoThemeColorPrimary());
        mNoDataTextView.setText(Restring.getString(getActivity(), R.string.fugu_no_data_found));

        poweredBy = rootView.findViewById(R.id.tvPoweredBy);

        supportView = new HippoSupportViewImpl(getActivity(), this, new HippoSupportInteractorImpl());
        initView(serverDBVersion);
    }

    private void initView(int serverDBVersion) {
        if(supportResponses == null)
            supportView.fetchData(defaultFaqName, serverDBVersion);
        else
            setInnerData(supportResponses);

        if(hasPoweredVia)
            setPoweredByText(poweredBy);
        else
            poweredBy.setVisibility(View.GONE);

        poweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(FUGU_WEBSITE_URL));
                startActivity(i);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(supportView != null)
            supportView.onDestroy();
    }



    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setData(SupportDataList supportResponses) {
        if(supportResponses == null || supportResponses.getList() == null || supportResponses.getList().size() == 0) {
            mNoDataTextView.setVisibility(View.VISIBLE);
            setToolbar(toolbar, Restring.getString(getActivity(), R.string.fugu_support));
        } else {
            if (supportAdapter != null)
                supportAdapter.setAdapterData((ArrayList<Item>) supportResponses.getList());
            setToolbarTitle(supportResponses.getCategoryName());
            Category category = new Category(supportResponses.getCategoryId(), supportResponses.getCategoryName(), "");
            categoryData = gson.toJson(category);
        }
        setPoweredByText(poweredBy);
    }

    @Override
    public void openChatSupport(OpenChatParams chatParams) {
        ChatByUniqueIdAttributes attributes = new ChatByUniqueIdAttributes.Builder()
                .setTransactionId(chatParams.getTransactionId())
                .setUserUniqueKey(chatParams.getUserUniqueKey())
                .setChannelName(chatParams.getChannelName())
                .setTags(chatParams.getTagsList())
                .setSupportTicket(true)
                .setCustomAttributes(chatParams.getCustomAttributes()).build();

        HippoConfig.getInstance().openChatByUniqueId(attributes);
    }

    private void setInnerData(ArrayList<Item> supportResponses) {
        if(supportAdapter != null)
            supportAdapter.setAdapterData(supportResponses);
        setToolbarTitle(title);
    }

    private void setToolbarTitle(String toolbarTitle) {
        setToolbar(toolbar, toolbarTitle);

        pathList = CommonData.getPathList();
        pathList.add(toolbarTitle);
        CommonData.setSupportPath(pathList);
    }

    @Override
    public void showError() {
        Toast.makeText(getActivity(), Restring.getString(getActivity(), R.string.hippo_something_wentwrong), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(int actionType, List<Item> items, String title) {
        typeCallback.onActionType((ArrayList<Item>) items, gson.toJson(pathList), title, transactionId, categoryData);
    }

    @Override
    public void onOtherTypeClick(int actionType, Item item) {
        switch (SupportKeys.SupportActionType.get(actionType)) {
            case DESCRIPTION:
                typeCallback.openDetailPage(item, gson.toJson(pathList), transactionId, categoryData);
                break;
            case CHAT_SUPPORT:
                Category category = gson.fromJson(categoryData, Category.class);
                SendQueryChat queryChat = new SendQueryChat(
                        SupportKeys.SupportQueryType.CHAT, category, transactionId,
                        CommonData.getUserUniqueKey(), item.getSupportId(), pathList);

                supportView.openChat(queryChat);
                break;
            case SHOW_CONVERSATION:
                HippoConfig.getInstance().showConversations(getActivity(), Restring.getString(getActivity(), R.string.hippo_chat_support));
                break;
            default:

                break;
        }
    }

    @Override
    public void onDescription(Item item) {
        typeCallback.openDetailPage(item, gson.toJson(pathList), transactionId, categoryData);
    }

    @Override
    public void chatSupport(Item item) {
        Category category = gson.fromJson(categoryData, Category.class);
        SendQueryChat queryChat = new SendQueryChat(SupportKeys.SupportQueryType.CHAT, category,
                transactionId, CommonData.getUserUniqueKey(), item.getSupportId(), pathList);
        if(item.getContent().getSubHeading() != null &&
                item.getContent().getSubHeading().getText() != null)
        queryChat.setSubHeader(item.getContent().getSubHeading().getText());

        supportView.openChat(queryChat);
    }

    @Override
    public void showConversaton(Item item) {
        HippoConfig.getInstance().showConversations(getActivity(), Restring.getString(getActivity(), R.string.hippo_chat_support));
    }

    @Override
    public void onClick(View view) {
        getActivity().onBackPressed();
    }
}
