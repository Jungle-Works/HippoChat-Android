package com.hippo.utils

import android.os.AsyncTask
import com.hippo.HippoConfig
import com.hippo.database.CommonData
import com.hippo.model.UnreadCountModel
import java.util.*

/**
 * Created by gurmail on 12/03/21.
 * @author gurmail
 */
class UnreadCountPush: AsyncTask<Long, Void, Int>() {

    override fun doInBackground(vararg params: Long?): Int {
        return updateCount(params[0]!!, params[1]!!)
    }

    override fun onPostExecute(result: Int) {
        if (HippoConfig.getInstance().callbackListener != null) {
            println("total unread count through push = $result")
            HippoConfig.getInstance().callbackListener.count(result)
        }
    }

    private fun updateCount(channelId: Long, count: Long): Int {
        try {
            var index = CommonData.getUnreadCountModel().indexOf(UnreadCountModel(channelId))
            if(index == -1) {
                var position = 0
                for(item in CommonData.getUnreadCountModel()) {
                    if(item.labelId.equals(channelId)) {
                        index = position
                        break
                    }
                    position += 1
                }
            }
            var unreadCountModels = CommonData.getUnreadCountModel()
            if(count > 0) {
                if(index > -1) {
                    val channelCount = unreadCountModels[index].count + 1
                    unreadCountModels[index].count = channelCount
                    CommonData.setUnreadCount(ArrayList())
                    CommonData.setUnreadCount(unreadCountModels)
                    val totalCount = CommonData.getTotalUnreadCount() + 1
                    CommonData.setTotalUnreadCount(totalCount)
                    return totalCount
                } else {
                    val channelCount = 1
                    val countModel = UnreadCountModel(channelId, channelId, channelCount)
                    unreadCountModels = CommonData.getUnreadCountModel()
                    unreadCountModels.add(countModel)
                    CommonData.setUnreadCount(unreadCountModels)
                    val totalCount = CommonData.getTotalUnreadCount() + 1
                    CommonData.setTotalUnreadCount(totalCount)
                    return totalCount
                }
            } else {
                return if(index > -1) {
                    val channelCount = unreadCountModels[index].count
                    unreadCountModels.removeAt(index)
                    CommonData.setUnreadCount(ArrayList())
                    CommonData.setUnreadCount(unreadCountModels)
                    val totalCount = CommonData.getTotalUnreadCount() - channelCount
                    CommonData.setTotalUnreadCount(totalCount)
                    totalCount
                } else {
                    val totalCount = CommonData.getTotalUnreadCount()
                    totalCount
                }
            }

        } catch (e: Exception) {
            return 0
        }
    }
}