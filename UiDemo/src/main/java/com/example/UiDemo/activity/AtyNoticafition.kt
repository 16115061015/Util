package com.example.UiDemo.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.UiDemo.R
import kotlinx.android.synthetic.main.aty_notification.*

/**
 * User: hzy
 * Date: 2020/9/10
 * Time: 8:58 PM
 * Description:通知显示横幅通知 小米好像不支持这样设置 后面再看看
 */
class AtyNotification : AppCompatActivity() {
    val CHANNEL_ID = 99999

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_notification)
        show.setOnClickListener {

//            val notifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//
//            val mChannel = NotificationChannel(CHANNEL_ID.toString(), "测试推送", NotificationManager.IMPORTANCE_MAX)
//            mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//            val pIntent = PendingIntent.getActivity(this, 1, Intent(), PendingIntent.FLAG_UPDATE_CURRENT)
//            notifyManager.createNotificationChannel(mChannel)
//            val builder = NotificationCompat.Builder(this, CHANNEL_ID.toString())
//                    .setChannelId(CHANNEL_ID.toString())
//                    .setContentTitle("活动")
//                    .setContentIntent(pIntent)
//                    .setContentText("您有一项新活动")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//            builder.setVisibility(Notification.VISIBILITY_PUBLIC)
//            builder.setTicker("悬浮")
//
//            // 关联PendingIntent
//            builder.setFullScreenIntent(pIntent, true)
//            val notification: Notification = builder.build()
//
//            notifyManager.notify(CHANNEL_ID, notification)
//
//
//            val stackBuilder = TaskStackBuilder.create(this)
//            //添加Activity到返回栈
//            stackBuilder.addParentStack(AtyNotification::class.java)
//            //添加Intent到栈顶
//            stackBuilder.addNextIntent(Intent())
            val notifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = this@AtyNotification.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                // 默认 channel
                val channelDefault = NotificationChannel(CHANNEL_ID.toString(), "测试", NotificationManager.IMPORTANCE_DEFAULT)
                channelDefault.description = "以防你不能及时收到圈圈的消息通知。"
                notificationManager.createNotificationChannel(channelDefault)
            }
            var notification = NotificationCompat.Builder(this@AtyNotification, CHANNEL_ID.toString())
                    .setContentTitle("New Messages")
                    .setContentText("You've received 3 new messages.")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setNumber(40)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL).build()


            notifyManager.notify(CHANNEL_ID, notification)

//            ShortcutBadger.applyCount(this,12)
        }
    }

    fun setBadge(context: Context,num: Int) {
        try{
            val launcherClassName = getLauncherClassName(context)
            if (launcherClassName.isNullOrBlank()) {
                return
            }
            val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE").apply {
                putExtra("badge_count", num)
                putExtra("badge_count_package_name", context.packageName)
                putExtra("badge_count_class_name", launcherClassName)
            }
            context.sendBroadcast(intent)
        }catch (e:Exception){
            Log.i("forTest","出错 setBadge $e")
        }
    }
    private fun getLauncherClassName(context: Context): String? {
        val pm: PackageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            val pkgName: String = resolveInfo.activityInfo.applicationInfo.packageName
            if (pkgName.equals(context.packageName, ignoreCase = true)) {
                return resolveInfo.activityInfo.name
            }
        }
        return null
    }

    private fun badgeSamsung(context: Context, badgeCount: Int) {
        val uri = Uri.parse("content://com.sec.badge/apps")
        val columnId = "_id"
        val columnPackage = "package"
        val columnClass = "class"
        val columnBadgeCount = "badgeCount"
        var cursor: Cursor? = null
        try {
            val contentResolver: ContentResolver = context.contentResolver
            cursor = contentResolver.query(uri, arrayOf(columnId), "$columnPackage=?", arrayOf(context.packageName), null)
            if (cursor == null || !cursor.moveToFirst()) {
                val contentValues = ContentValues()
                contentValues.put(columnPackage, context.packageName)
                contentValues.put(columnClass, getLauncherClassName(context))
                contentValues.put(columnBadgeCount, badgeCount)
                contentResolver.insert(uri, contentValues)
            } else {
                val idColumnIndex: Int = cursor.getColumnIndex(columnId)
                val contentValues = ContentValues()
                contentValues.put(columnBadgeCount, badgeCount)
                contentResolver.update(uri, contentValues, "$columnId=?", arrayOf<String>(java.lang.String.valueOf(cursor.getInt(idColumnIndex))))
            }
        } catch (e: java.lang.Exception) {
            setBadge(this,badgeCount)
        } finally {
            cursor?.close()
        }
    }
}