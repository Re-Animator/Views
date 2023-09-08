package com.reanimator.imagefromnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.reanimator.imagefromnetwork.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val VISIBLE = View.VISIBLE
    private val GONE = View.GONE

    private val viewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        binding.imageUrlEditText.editText?.setText(viewModel.imageUrl.value)

        binding.downloadButton.setOnClickListener {
            val link = binding.imageUrlEditText.editText?.text.toString()

            if (link.isEmpty()) {
                onEmptyInputErrorMessage()
            } else {
                viewModel.setNewImageUrl(link)
            }
        }

        viewModel.imageUrl.observe(this) {
            progressBarVisibilityChange(VISIBLE)
            loadImage(it)
        }
    }

    private fun loadImage(imageUrl: String) {
        Picasso.get().load(imageUrl)
            .into(binding.downloadedImage, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    binding.imageUrlEditText.editText?.text?.clear()
                    progressBarVisibilityChange(GONE)
                }

                override fun onError(e: Exception?) {
                    progressBarVisibilityChange(GONE)
                    onDownloadErrorMessage()
                }
            })
    }

    private fun progressBarVisibilityChange(visibility: Int) {
        binding.progressBar.visibility = visibility
    }

    // pop-up window on any Picasso errors
    private fun onDownloadErrorMessage() {
        Toast.makeText(
            this,
            "Unable to download image from ${viewModel.imageUrl.value}",
            Toast.LENGTH_SHORT
        ).show()
    }

    // pop-up window on empty input
    private fun onEmptyInputErrorMessage() {
        Toast.makeText(
            this,
            "You need to enter URL",
            Toast.LENGTH_SHORT
        ).show()
    }
}