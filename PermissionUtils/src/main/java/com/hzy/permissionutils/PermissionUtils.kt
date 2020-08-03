package com.hzy.permissionutils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * User: hzy
 * Date: 2020/8/3
 * Time: 7:43 PM
 * Description: 权限请求工具类
 */
object PermissionUtils {
    fun requestPermissions(act: Any, vararg permission: String, reqCallBack: ((grantedPermission: HashSet<String>, deniedPermission: HashSet<String>, ignorePermission: HashSet<String>) -> Unit)? = null) {
        findOrAddRequestFragment(act, reqCallBack)?.requestPermissions(permission, RequestFragment.REQ_PERMISSION)
    }

    private fun findOrAddRequestFragment(act: Any, reqCallBack: ((grantedPermission: HashSet<String>, deniedPermission: HashSet<String>, ignorePermission: HashSet<String>) -> Unit)? = null): Fragment? {
        val manager: FragmentManager? = getFragmentManager(act)
        manager?.let {
            var requestFragment = manager.findFragmentByTag(RequestFragment.TAG)
            if (requestFragment == null) {
                requestFragment = RequestFragment(reqCallBack)
                manager.beginTransaction().add(requestFragment, RequestFragment.TAG).commitNowAllowingStateLoss()
            }
            return requestFragment
        }
        return null

    }

    private fun getFragmentManager(act: Any): FragmentManager? {
        return when (act) {
            is FragmentActivity -> act.supportFragmentManager
            is Fragment -> act.childFragmentManager
            is View -> getFragmentManager(act.context)
            else -> null
        }
    }

    fun forwardToSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        findOrAddRequestFragment(context)?.startActivityForResult(intent,RequestFragment.REQ_PERMISSION)
//        getInvisibleFragment().startActivityForResult(intent, InvisibleFragment.FORWARD_TO_SETTINGS)
    }

}