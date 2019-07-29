package com.whf.messagerelayer.view

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.whf.messagerelayer.R

public class App : Application() {
    /**
     * 当前Acitity个数
     */
    private var activityAount = 0
    private var isForeground = false
    private var dialog: AlertDialog? = null

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            }

            override fun onActivityStarted(p0: Activity) {
                if (activityAount == 0) {
                    //app回到前台
                    isForeground = true
                    Log.e(this::class.java.toString(), "进入前台")
                    requestPermissionIfNeed(p0)
                }
                activityAount++
            }

            override fun onActivityPaused(p0: Activity) {

            }

            override fun onActivityResumed(p0: Activity) {

            }

            override fun onActivityDestroyed(p0: Activity) {

            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

            }

            override fun onActivityStopped(p0: Activity) {
                activityAount--
                if (activityAount == 0) {
                    Log.e(this::class.java.toString(), "进入后台")
                    isForeground = false
                }
            }
        })
    }

    private fun requestPermissionIfNeed(activity: Activity) {
        val permission = XXPermissions.isHasPermission(this@App, Permission.READ_CONTACTS, Permission.READ_SMS)
        if (permission) {
            dialog?.dismiss()
            return
        }
        XXPermissions.with(activity)
                .permission(Permission.READ_CONTACTS, Permission.READ_SMS) //不指定权限则自动获取清单中的危险权限
                .request(object : OnPermission {
                    override fun hasPermission(granted: List<String>, isAll: Boolean) {
                        if (!isAll) {
                            showPermissionDialog(activity)
                        } else {
                            dialog?.dismiss()
                        }
                    }

                    override fun noPermission(denied: List<String>, quick: Boolean) {
                        showPermissionDialog(activity)
                    }
                })
    }

    private fun showPermissionDialog(activity: Activity) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_alert, null, false)

        dialogBuilder.setView(view)
        dialogBuilder.setPositiveButton("确定") {dialog, _ ->
            XXPermissions.gotoPermissionSettings(activity)
        }

        dialogBuilder.setNegativeButton("取消") { _, _ -> activity.finish() }
        dialog = dialogBuilder.show()
    }
}