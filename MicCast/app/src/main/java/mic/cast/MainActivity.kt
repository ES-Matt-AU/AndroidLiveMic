package mic.cast

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button

class MainActivity : Activity() {
    private var on = false
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.POST_NOTIFICATIONS), 1)
        val b = Button(this).apply { text = "START"; textSize = 28f; gravity = Gravity.CENTER }
        b.setOnClickListener {
            val i = Intent(this, MicService::class.java)
            if (on) stopService(i) else startForegroundService(i)
            on = !on
            b.text = if (on) "STOP" else "START"
        }
        setContentView(b)
    }
}
