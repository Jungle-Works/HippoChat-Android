package com.hippo.utils

import android.os.AsyncTask
import com.hippo.HippoConfig
import com.hippo.database.CommonData
import com.hippo.model.FuguConversation
import com.hippo.model.UnreadCountModel
import java.util.*

/**
 * Created by gurmail on 12/03/21.
 * @author gurmail
 */
class UnreadCount: AsyncTask<ArrayList<FuguConversation>, Void, Int>() {
    override fun doInBackground(vararg params: ArrayList<FuguConversation>?): Int? {
        return saveData(params[0]!!)
    }

    override fun onPostExecute(result: Int) {
        if (HippoConfig.getInstance().callbackListener != null) {
            println("total unread count = $result")
            HippoConfig.getInstance().callbackListener.count(result)
        }
    }

    private val unreadCountModels = ArrayList<UnreadCountModel>()
    private fun saveData(conversation: ArrayList<FuguConversation>): Int {
        try {
            unreadCountModels.clear()
            CommonData.setUnreadCount(unreadCountModels)
            CommonData.setTotalUnreadCount(0)
            var count = 0
            for(data in conversation) {
                if(data.unreadCount > 0) {
                    val countModel = UnreadCountModel(data.channelId, data.labelId, data.unreadCount)
                    unreadCountModels.add(countModel)
                    count += data.unreadCount
                }
            }
            CommonData.setUnreadCount(unreadCountModels)
            CommonData.setTotalUnreadCount(count)
            CommonData.hasUnreadCount(true)
            return count
        } catch (e: Exception) {
            val count = CommonData.getTotalUnreadCount()
            return count
        }
    }

}