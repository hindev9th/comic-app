package com.cm.app.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.io.File

class ApkInstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_PACKAGE_ADDED) {
            val packageName = intent.data?.encodedSchemeSpecificPart
            val apkPath = packageName?.let { context?.packageManager?.getApplicationInfo(it, 0)?.sourceDir }
            if (!apkPath.isNullOrBlank()) {
                val apkFile = File(apkPath)
                apkFile.delete()
            }
        }
    }
}
