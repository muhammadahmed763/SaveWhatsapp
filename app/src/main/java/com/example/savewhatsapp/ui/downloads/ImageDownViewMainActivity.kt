package com.example.savewhatsapp.ui.downloads

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.savewhatsapp.utils.MyConstants
import com.example.savewhatsapp.utils.ConstantVariables
import com.savewhatsapp.R
import com.savewhatsapp.databinding.ActivityImageDownViewMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class ImageDownViewMainActivity : AppCompatActivity() {
    private val TAG = "DownloadFragment"
    private lateinit var binding: ActivityImageDownViewMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDownViewMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonCard.downloadBtn.setImageResource(R.drawable.delete_icon)
        val uri = intent.getStringExtra("uri").toString()
        val check=intent.getStringExtra(ConstantVariables.PASS_DATA)
        Glide.with(this).load(uri).into(binding.imageview)
        binding.back.setOnClickListener {
            finish()
        }

        binding.buttonCard.whatsappBtn.setOnClickListener{
            val mainUri = Uri.parse(uri)

            shareonwhatsapp(mainUri)
        }
        binding.buttonCard.shareBtn.setOnClickListener {



            try {
                val mainUri = Uri.parse(uri)
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "image/*"
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    startActivity(
                        Intent.createChooser(
                            sharingIntent,
                            "Share Video using"
                        )
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (ex: Exception) {
                Toast.makeText(
                    this,
                    ex.message.toString(),
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

    fun shareonwhatsapp(uri: Uri){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
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


}