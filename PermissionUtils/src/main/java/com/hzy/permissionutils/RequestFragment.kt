package com.hzy.permissionutils

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

/**
 * User: hzy
 * Date: 2020/8/3
 * Time: 7:00 PM
 * Description: 隐藏Fragment 用于请求权限
 */

class RequestFragment(private val requestBack: ((grantedPermission: HashSet<String>, deniedPermission: HashSet<String>, ignorePermission: HashSet<String>) -> Unit)? = null) : Fragment() {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQ_PERMISSION) return
        val grantedPermission = mutableSetOf<String>()//同意
        val deniedPermission = mutableSetOf<String>() //拒绝
        val ignorePermission = mutableSetOf<String>() //拒绝且不再询问
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                deniedPermission.add(permission)
                if (!this.shouldShowRequestPermissionRationale(permission)) ignorePermission.add(permission)
                else deniedPermission.add(permission)
            } else
                grantedPermission.add(permission)
        }
        requestBack?.invoke(grantedPermission.toHashSet(), deniedPermission.toHashSet(), ignorePermission.toHashSet())

    }


    companion object {
        const val REQ_PERMISSION = 2000
        const val TAG = "RequestFragment"
    }
}