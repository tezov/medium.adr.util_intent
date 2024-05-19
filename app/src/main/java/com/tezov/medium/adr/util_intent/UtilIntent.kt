package com.tezov.medium.adr.util_intent

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.tezov.medium.adr.util_intent.UtilsIntent.succeedOnResume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.suspendCoroutine

object UtilsIntent {

    private fun CancellableContinuation<Unit>.succeedOnResume(
        activity: ComponentActivity,
        intent: Intent
    ) {
        val lifecycleListener = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                activity.lifecycle.removeObserver(this)
                resumeWith(Result.success(Unit))
            }
        }
        activity.lifecycle.addObserver(lifecycleListener)
        invokeOnCancellation { activity.lifecycle.removeObserver(lifecycleListener) }
        activity.startActivity(intent)
    }

    suspend fun emailTo(
        activity: ComponentActivity,
        target: String,
        subject: String? = null,
        body: String? = null
    ) = suspendCancellableCoroutine { continuation ->
        val mailto = StringBuilder().apply {
            append("mailto:").append(target).append("?")
            subject?.let {
                append("subject=").append(Uri.encode(it)).append("&")
            }
            body?.let {
                append("body=").append(Uri.encode(it)).append("&")
            }
            replace(length - 1, length, "")
        }
        val intent = Intent().apply {
            action = Intent.ACTION_SENDTO
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            data = Uri.parse(mailto.toString())
        }
        continuation.succeedOnResume(activity = activity, intent = intent)
    }

    suspend fun sendTo(activity: ComponentActivity, subject: String? = null, text: String) =
        suspendCancellableCoroutine { continuation ->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                subject?.let {
                    putExtra(Intent.EXTRA_SUBJECT, it)
                }
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            continuation.succeedOnResume(activity = activity, intent = intent)
        }

    suspend fun callTo(activity: ComponentActivity, target: String) =
        suspendCancellableCoroutine { continuation ->
            val intent = Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:$target")
            }
            continuation.succeedOnResume(activity = activity, intent = intent)
        }

    suspend fun openLink(activity: ComponentActivity, uri: Uri) =
        suspendCancellableCoroutine { continuation ->
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = uri
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            continuation.succeedOnResume(activity = activity, intent = intent)
        }
}