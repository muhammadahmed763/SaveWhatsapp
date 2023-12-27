package com.example.savewhatsapp.ui.businesswhatsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.example.savewhatsapp.modals.StatusModel
import com.example.savewhatsapp.ui.ImageViewerActivity
import com.example.savewhatsapp.utils.MyConstants
import com.example.savewhatsapp.utils.Sp
import com.example.savewhatsapp.utils.Utils
import com.example.statussaverapplication.adapters.recyclers.BusinessImageAdapter

import com.savewhatsapp.R
import com.savewhatsapp.databinding.FragmentBusinessWhatsappImagesBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class BusinessWhatsappImagesFragment : Fragment() {
    private val list: ArrayList<StatusModel> by lazy { ArrayList() }
    private val adapter by lazy { BusinessImageAdapter(list,) }
    private val prefrences by lazy { Sp(requireContext()) }
    private lateinit var binding: FragmentBusinessWhatsappImagesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBusinessWhatsappImagesBinding.inflate(inflater, container, false)
        recyclerView()
        businessWhatsappButtonClick()
        versionControlling()
        refreshLayout()
        return binding.root
    }
    private fun refreshLayout() {
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark),
            ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark),
            ContextCompat.getColor(requireContext(), R.color.redtext),
            ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            list.clear()
            recyclerView()
            versionControlling()
            binding.swipeRefreshLayout.isRefreshing = false

        }
    }

    private fun versionControlling() {
        if (list.isEmpty()) {
            //Toast.makeText(requireContext(), "if", Toast.LENGTH_SHORT).show()
            binding.nofilelayout.visibility = View.VISIBLE
            binding.recyclerview.visibility = View.GONE

        } else {
            // Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show()
            binding.nofilelayout.visibility = View.GONE
            binding.recyclerview.visibility = View.VISIBLE

        }

        //  loadInterstitialAds()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            list.clear()
            try {
                // Current version is equal to or greater than Android Q
                // Your code for handling Android Q and above here
                if (prefrences.getBusinessPath().equals("")) {
                    getWhatsappFolderPermission()
                } else {
                    //  Toast.makeText(requireContext(),"eles",Toast.LENGTH_SHORT).show()

                    val path = prefrences.getBusinessPath()
                    requireContext().contentResolver.takePersistableUriPermission(
                        path.toUri(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    if (path != "") {

                        val doc = DocumentFile.fromTreeUri(requireContext(), path.toUri())
                        if (doc != null) {
                            for (file in doc.listFiles()) {
                                if (!file.name.orEmpty().endsWith(".nomedia")) {
                                    val uri = file.uri
                                    if (uri?.path.orEmpty().endsWith(".png") || uri?.path.orEmpty()
                                            .endsWith(".jpg")
                                    ) {
                                        list.add(
                                            StatusModel(
                                                uri.toString()
                                            )
                                        )
                                    }

                                }

                            }
                            if (list.isEmpty()) {
                                binding.nofilelayout.visibility = View.VISIBLE
                                binding.recyclerview.visibility = View.GONE

                            } else {
                                binding.nofilelayout.visibility = View.GONE
                                binding.recyclerview.visibility = View.VISIBLE
                            }

                        }
                    }
                }

            } catch (e: Exception) {

            }

        } else {
            list.clear()
            try {
                // Current version is below Android Q
                // Your code for handling below Android Q here
                val folder = File(Utils.WBpath)

                val files = folder.listFiles()

                if (files != null) {

                    for (i in files) {
                        if (i.path.endsWith(".png") || i.path.endsWith(".jpg")) {

                            //  Toast.makeText(requireContext(),"running",Toast.LENGTH_SHORT).show()
                            val f = File(i.toString())
                            val path = f.absolutePath
                            list.add(StatusModel(path))
                        }
                    }
                }
                if (list.isEmpty()) {
                    binding.nofilelayout.visibility = View.VISIBLE
                    binding.recyclerview.visibility = View.GONE

                } else {
                    // Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show()

                    binding.nofilelayout.visibility = View.GONE
                    binding.recyclerview.visibility = View.VISIBLE
                }


            } catch (e: Exception) {

            }
            //  Toast.makeText(requireContext(), list.size.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun businessWhatsappButtonClick() {
        binding.gotowhatsapp.setOnClickListener{
            val packageName = "com.whatsapp.w4b"
            val intent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "WhatsApp Business is not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun recyclerView() {
        binding.recyclerview.adapter = adapter
        adapter.recyclerClick(object :BusinessImageAdapter.PassData{
            override fun imageClick(position: Int) {
                val i = Intent(requireContext(), ImageViewerActivity::class.java)
                i.putExtra("uri", list[position].file)
                startActivity(i)
            }

            override fun shareClick(modal: StatusModel) {
                try {
                    val mainUri = Uri.parse(modal.file)
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
                            requireContext(),
                            e.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (ex: Exception) {
                    Toast.makeText(
                        requireContext(),
                        ex.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun saveClick(position: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        val sourceUri: Uri = Uri.parse(MyConstants.list[position].file)
                        val resolver = requireContext().applicationContext.contentResolver
                        val inputStream = resolver.openInputStream(sourceUri)

                        val downloadsDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val folder = File(downloadsDir, "WhatsApp Images")

                        if (!folder.exists()) {
                            folder.mkdir()
                        }

                        val fileName = System.currentTimeMillis().toString() + "wp_image.png"
                        val destinationFile = File(folder, fileName)

                        destinationFile.outputStream().use { outputStream ->
                            inputStream?.copyTo(outputStream)
                        }

                        MediaScannerConnection.scanFile(
                            requireContext().applicationContext,
                            arrayOf(destinationFile.absolutePath),
                            null,
                            null
                        )

                        Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT)
                            .show()

                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.file_is_not_saved) + e.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } else {
                    try {
                        val file = File(MyConstants.list[position].file)

                        val downloadsDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val folder = File(downloadsDir, "WhatsApp Images")

                        if (!folder.exists()) {
                            folder.mkdir()
                        }

                        val fileName = "whatsappImage" + System.currentTimeMillis() + ".jpg"


                        val des = File(folder, fileName)

                        copyFile(file, des)

                        Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT)
                            .show()

                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.file_is_not_saved) + e.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }

            }

        })
    }

    //Create method appInstalledOrNot



    @RequiresApi(Build.VERSION_CODES.Q)
    fun getWhatsappFolderPermission() {
        val sm = requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        val startDir = "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$startDir"
        uri = Uri.parse(scheme)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        startActivityForResult(intent, 2002)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            val treeUri = data?.data
            Log.d("treeuri", treeUri.toString())
            prefrences.setBusinessPath(treeUri.toString())

            if (treeUri != null) {
                requireActivity().contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val doc = DocumentFile.fromTreeUri(requireContext().applicationContext, treeUri)
                if (doc != null) {
                    for (file in doc.listFiles()) {
                        if (!file.name!!.endsWith(".nomedia")
                        ) {

                            if (file.uri.path?.endsWith(".png") == true || file.uri.path?.endsWith(
                                    ".jpg"
                                ) == true
                            ) {
                                list.add(
                                    StatusModel(
                                        file.uri.toString(),
                                    )
                                )
                                // Log.d("Uris", file.uri.toString())
                            }

                        }
                    }
                    if (list.isEmpty()) {
                        binding.nofilelayout.visibility = View.VISIBLE
                        binding.recyclerview.visibility = View.GONE

                    } else {
                        // Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show()

                        binding.nofilelayout.visibility = View.GONE
                        binding.recyclerview.visibility = View.VISIBLE
                    }
                }
            }

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