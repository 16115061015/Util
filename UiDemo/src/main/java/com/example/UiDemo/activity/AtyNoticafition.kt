package com.example.UiDemo.activity

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
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

            val notifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


            val mChannel = NotificationChannel(CHANNEL_ID.toString(), "测试推送", NotificationManager.IMPORTANCE_MAX)
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val pIntent = PendingIntent.getActivity(this, 1, Intent(), PendingIntent.FLAG_UPDATE_CURRENT)
            notifyManager.createNotificationChannel(mChannel)
            val builder = NotificationCompat.Builder(this, CHANNEL_ID.toString())
                    .setChannelId(CHANNEL_ID.toString())
                    .setContentTitle("活动")
                    .setContentIntent(pIntent)
                    .setContentText("您有一项新活动")
                    .setSmallIcon(R.mipmap.ic_launcher)
            builder.setVisibility(Notification.VISIBILITY_PUBLIC)
            builder.setTicker("悬浮")

            // 关联PendingIntent
            builder.setFullScreenIntent(pIntent, true)
            val notification: Notification = builder.build()

            notifyManager.notify(CHANNEL_ID, notification)


            val stackBuilder = TaskStackBuilder.create(this)
            //添加Activity到返回栈
            stackBuilder.addParentStack(AtyNotification::class.java)
            //添加Intent到栈顶
            stackBuilder.addNextIntent(Intent())

        }
    }


}