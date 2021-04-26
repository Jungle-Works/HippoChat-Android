package com.hippo.helper

import androidx.core.content.ContextCompat.startActivity
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.content.Intent
import android.util.Log


/**
 * Created by gurmail on 2020-07-24.
 * @author gurmail
 */
object PermissionHandler {

    fun addAutoStartupswitch(context: Context) {

        try {
            val intent = Intent()
            val manufacturer = android.os.Build.MANUFACTURER.toLowerCase()
            val model = Build.MODEL
            Log.d("DeviceModel", model.toString())

            when (manufacturer) {
                "xiaomi" -> intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
                "oppo" -> intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
                "vivo" -> intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
                "Letv" -> intent.component = ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
                "Honor" -> intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
                "oneplus" -> intent.component = ComponentName(
                    "com.oneplus.security",
                    "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"
                )
            }

            val list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.size > 0) {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("exc", e.toString())
        }

    }
}