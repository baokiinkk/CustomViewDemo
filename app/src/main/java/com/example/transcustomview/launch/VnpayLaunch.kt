package com.vnpay.vietcredit.utils.launch

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.transcustomview.launch.OpenPermissionResultLauncher
import com.example.transcustomview.launch.OpenResultLauncher
import java.util.concurrent.ConcurrentHashMap

object VnpayLaunch {
    private var resultLauncher: OpenResultLauncher? = null
    private var resultPermissionLauncher: OpenPermissionResultLauncher? = null
    private val openLauncherCache = ConcurrentHashMap<Int, OpenResultLauncher>()
    private val TAG = VnpayLaunch::class.java.simpleName

    fun notifyActive(context: Context) {
        if (context is AppCompatActivity) {
            if (openLauncherCache.containsKey(context.hashCode())) {
                resultLauncher = openLauncherCache[context.hashCode()]
            }
        }
    }

    fun notifyInactive(context: Context) {
        if (context is AppCompatActivity) {
            resultLauncher?.let {
                openLauncherCache[context.hashCode()] = it
            }
        }
    }

    fun register(context: Context) {
        if (context is AppCompatActivity) {
            resultLauncher = OpenResultLauncher()
            resultLauncher?.register(context)
            openLauncherCache[context.hashCode()] = resultLauncher!!
        }
    }

    fun registerFragment(context: Fragment) {
        resultLauncher = OpenResultLauncher()
        resultLauncher?.register(context)
        openLauncherCache[context.hashCode()] = resultLauncher!!
    }

    fun unregister(context: Context) {
        if (context is AppCompatActivity) {
            openLauncherCache.remove(context.hashCode())
        }
    }

    fun launch(context: Context, intent: Intent? = null, callback: ((ActivityResult) -> Unit)) {
        if (context is AppCompatActivity) {
            resultLauncher?.launch(
                intent
            ) { result ->
                callback.invoke(result)
            }
        }
    }

    fun registPermission(context: FragmentActivity){
        resultPermissionLauncher = OpenPermissionResultLauncher()
        resultPermissionLauncher?.register(context)
    }
    fun launchPermission(
        context: FragmentActivity,
        arrayPermission: Array<String>,
        callbackSuccess: () -> Unit,
        callbackFail: (ArrayList<String>) -> Unit
    ) {
        if (context is AppCompatActivity) {

            resultPermissionLauncher?.launch(arrayPermission) { permissionResultMap ->
                var countSuccess = 0
                val arrayPermissionFail = arrayListOf<String>()
                permissionResultMap.forEach {
                    if (ActivityCompat.checkSelfPermission(
                            context, it.key
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        countSuccess++
                    } else {
                        arrayPermissionFail.add(it.key)
                    }
                }
                if (countSuccess == arrayPermission.size) {
                    callbackSuccess.invoke()
                } else {
                    if (arrayPermissionFail.isNotEmpty())
                        callbackFail.invoke(arrayPermissionFail)
                }
            }
        }
    }
}