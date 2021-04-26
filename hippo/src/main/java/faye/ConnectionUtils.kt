package faye

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import com.hippo.activity.HippoActivityLifecycleCallback
import com.hippo.utils.HippoLog


/**
 * Created by gurmail on 2020-04-25.
 * @author gurmail
 */
object ConnectionUtils {

    fun isAppRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskList = activityManager.getRunningTasks(10)
        if(taskList != null && taskList.size>0) {
            for(task in taskList) {
                HippoLog.w("Name", "name = " + task.topActivity!!.className)
                if(HippoActivityLifecycleCallback.hippoClasses.contains(task.topActivity!!.className)) {
                   return true
                }
                /*if(task.topActivity.className.contains("com.hippo")) {
                    HippoLog.w("Name", "name = " + task.topActivity.className)
                    return true
                }*/
            }
        }
        return false
    }

    fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}