package com.example.savewhatsapp.ui.downloads

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.savewhatsapp.utils.MyConstants
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.savewhatsapp.R
import com.savewhatsapp.databinding.ActivityVideoDownBinding
import com.savewhatsapp.databinding.ActivityVideoPlayerBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class VideoDownActivity : AppCompatActivity() {
    private lateinit var player: SimpleExoPlayer

    private lateinit var binding: ActivityVideoDownBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoDownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCard.downloadBtn.setImageResource(R.drawable.delete_icon)


        val uri = intent.getStringExtra("uri").toString()

        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        binding.back.setOnClickListener {
            finish()
        }
        binding.buttonCard.whatsappBtn.setOnClickListener{
            val mainUri = Uri.parse(uri)

            shareVideoToWhatsApp(mainUri)
        }
        binding.buttonCard.shareBtn.setOnClickListener {


            val mainUri = Uri.parse(uri)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "video/mp4"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(Intent.createChooser(sharingIntent, "Share Video using"))
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    e.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.buttonCard.downloadBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.delete_video))
            builder.setMessage(getString(R.string.are_you_sure_want_to_delete))
            val b =File(uri).delete()
            builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                // do something when "Yes" button is clicked
                try {
                    if (b){
                        MyConstants.deleteItem=uri
                        Toast.makeText(
                            this,
                            getString(R.string.file_deleted),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                // do something when "No" button is clicked
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
    }
    private fun shareVideoToWhatsApp(videoUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_STREAM, videoUri)
        intent.setPackage("com.whatsapp")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyFile(sourceFile: File, destFile: File) {
        if (!sourceFile.exists()) {
            return
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        source = FileInputStream(sourceFile).getChannel()
        destination = FileOutputStream(destFile).getChannel()
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        if (source != null) {
            source.close()
        }
        if (destination != null) {
            destination.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}