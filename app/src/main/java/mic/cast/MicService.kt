package mic.cast

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.*
import android.os.IBinder

class MicService : Service() {
    @Volatile private var run = false

    override fun onBind(i: Intent?): IBinder? = null

    override fun onStartCommand(i: Intent?, f: Int, id: Int): Int {
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(NotificationChannel("m", "Mic", NotificationManager.IMPORTANCE_LOW))
        val n = Notification.Builder(this, "m").setContentTitle("Broadcasting mic")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now).build()
        startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        if (!run) { run = true; Thread(::loop).start() }
        return START_NOT_STICKY
    }

    private fun loop() {
        val rate = 44100
        val inMin = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val outMin = AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val rec = AudioRecord(MediaRecorder.AudioSource.MIC, rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, inMin)
        val trk = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build())
            .setAudioFormat(AudioFormat.Builder().setSampleRate(rate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
            .setBufferSizeInBytes(outMin)
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY).build()
        getSystemService(AudioManager::class.java).getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }?.let { trk.preferredDevice = it }
        val buf = ShortArray(inMin / 2)
        rec.startRecording(); trk.play()
        while (run) { val r = rec.read(buf, 0, buf.size); if (r > 0) trk.write(buf, 0, r) }
        rec.stop(); rec.release(); trk.stop(); trk.release()
    }

    override fun onDestroy() { run = false; super.onDestroy() }
}
