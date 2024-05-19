package com.tezov.medium.adr.util_intent

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

object UtilsIntent {

    @Throws(IllegalStateException::class)
    private fun CancellableContinuation<Unit>.succeedOnResume(
        activity: ComponentActivity,
        intent: Intent
    ) {
        if(activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            throw IllegalStateException("Can be use only in activity resume state")
        }
        val lifecycleListener = object : DefaultLifecycleObserver {
            var hasReachedOnPause = false
            override fun onPause(owner: LifecycleOwner) {
                hasReachedOnPause = true
            }
            override fun onResume(owner: LifecycleOwner) {
                if (hasReachedOnPause) {
                    activity.lifecycle.removeObserver(this)
                    resumeWith(Result.success(Unit))
                }
            }
        }
        activity.lifecycle.addObserver(lifecycleListener)
        invokeOnCancellation { activity.lifecycle.removeObserver(lifecycleListener) }
        activity.startActivity(intent)
    }

    @Throws(IllegalStateException::class)
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

    @Throws(IllegalStateException::class)
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

    @Throws(IllegalStateException::class)
    suspend fun callTo(activity: ComponentActivity, target: String) =
        suspendCancellableCoroutine { continuation ->
            val intent = Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:$target")
            }
            continuation.succeedOnResume(activity = activity, intent = intent)
        }

    @Throws(IllegalStateException::class)
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