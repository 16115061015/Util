package com.hzy.permissionutils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:27 PM
 * Description:
 */
class MainAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv.setOnClickListener {
            request()
        }
    }


    private fun request() {
        PermissionUtils.requestPermissions(this, Manifest.permission.CAMERA) { grantedPermission, deniedPermission, ignorePermission ->
            if (grantedPermission.contains(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "通过", Toast.LENGTH_LONG).show()
            }
            if (deniedPermission.contains(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "不通过", Toast.LENGTH_LONG).show()
            }
            if (ignorePermission.contains(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "拒绝且不再询问", Toast.LENGTH_LONG).show()
                PermissionUtils.forwardToSettings(this)

            }
        }

    }

}

