package com.tezov.medium.adr.util_intent

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.tezov.medium.adr.util_intent.ui.theme.Util_intentTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Util_intentTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val coroutine = rememberCoroutineScope()
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Button(onClick = {
                            coroutine.launch {
                                Log.d("UtilsIntent", "emailTo")

                                UtilsIntent.emailTo(
                                    activity = this@MainActivity,
                                    target = "tezov.medium@yopmail.com",
                                    subject = "test util intent",
                                    body = "email sent from application test medium util intent"
                                )

                                Log.d("UtilsIntent", "user is back to application")
                            }
                        }) {
                            Text(
                                text = "emailTo",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

// TODO: do not work anymore, the bottomsheet application choice do not make the activity to go onStop
//                        Button(onClick = {
//                            coroutine.launch {
//                                Log.d("UtilsIntent", "sendTo")
//                                UtilsIntent.sendTo(
//                                    activity = this@MainActivity,
//                                    subject = "test util intent",
//                                    text = "text sent from application test medium util intent"
//                                )
//                                Log.d("UtilsIntent", "user is back to application")
//                            }
//                        }) {
//                            Text(
//                                text = "sendTo",
//                                style = MaterialTheme.typography.titleLarge
//                            )
//                        }

                        Button(onClick = {
                            coroutine.launch {
                                Log.d("UtilsIntent", "callTo")

                                UtilsIntent.callTo(
                                    activity = this@MainActivity,
                                    target = "+813-5456-5511"
                                )

                                Log.d("UtilsIntent", "user is back to application")
                            }
                        }) {
                            Text(
                                text = "callTo",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Button(onClick = {
                            coroutine.launch {
                                Log.d("UtilsIntent", "openLink")

                                UtilsIntent.openLink(
                                    activity = this@MainActivity,
                                    uri = Uri.parse("https://asoftmurmur.com/")
                                )

                                Log.d("UtilsIntent", "user is back to application")
                            }
                        }) {
                            Text(
                                text = "openLink",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Util_intentTheme {
        Greeting("Android")
    }
}