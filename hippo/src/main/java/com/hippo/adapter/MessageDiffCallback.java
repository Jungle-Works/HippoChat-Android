package com.hippo.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.hippo.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 2019-11-18.
 *
 * @author gurmail
 */
public class MessageDiffCallback extends DiffUtil.Callback {

    List<Message> oldList = new ArrayList<>();
    List<Message> newList = new ArrayList<>();

    public MessageDiffCallback(List<Message> oldList, List<Message> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if(newList.get(newItemPosition).isDateView())
            return true;
        return oldList.get(oldItemPosition).getMuid().equalsIgnoreCase(newList.get(newItemPosition).getMuid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
