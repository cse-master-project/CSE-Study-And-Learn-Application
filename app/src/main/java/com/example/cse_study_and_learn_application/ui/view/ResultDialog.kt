package com.example.cse_study_and_learn_application.ui.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.example.cse_study_and_learn_application.R
import java.io.IOException


class ResultDialog(context: Context, private val resultType: ResultType) : Dialog(context, R.style.TransparentDialog) {
    enum class ResultType {
        SUCCESS, FAILURE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_result)

        val imageContainer: FrameLayout = findViewById(R.id.imageContainer)
        val gifImageView: ImageView = findViewById(R.id.gifImageView)
        val resultTextView: OutlineTextView = findViewById(R.id.resultTextView)

        // Set result text and color based on result type
        when (resultType) {
            ResultType.SUCCESS -> {
                resultTextView.text = "정답!"
                resultTextView.setColors(
                    ContextCompat.getColor(context, R.color.white), // 텍스트 색상
                    ContextCompat.getColor(context, R.color.light_blue_600)  // 테두리 색상
                )
            }
            ResultType.FAILURE -> {
                resultTextView.text = "오답!"
                resultTextView.setColors(
                    ContextCompat.getColor(context, R.color.white), // 텍스트 색상
                    ContextCompat.getColor(context, R.color.incorrect_text_color)  // 테두리 색상
                )
            }
        }

        // Get random GIF filename based on result type
        val gifFileName = getRandomGifFileName(resultType)

        try {
            // Load GIF into gifImageView using Glide
            Glide.with(context)
                .asGif()  // Specify that it's a GIF
                .load("file:///android_asset/images/gnu/$gifFileName")  // Load GIF from assets
                .into(gifImageView)  // Into gifImageView
        } catch (e: IOException) {
            Log.e("ResultDialog", "Error loading GIF", e)
        }

        // Fade in animation
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 300
        imageContainer.startAnimation(fadeIn)
        resultTextView.startAnimation(fadeIn)

        // Fade out animation
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 300
        fadeOut.startOffset = 500 // Start fading out after 0.5 seconds

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                dismiss()
            }
        })

        imageContainer.postDelayed({
            imageContainer.startAnimation(fadeOut)
            resultTextView.startAnimation(fadeOut)
        }, 1000)
    }

    private fun getRandomGifFileName(resultType: ResultType): String {
        val resourceId = when (resultType) {
            ResultType.SUCCESS -> R.array.correct_answer_gifs
            ResultType.FAILURE -> R.array.wrong_answer_gifs
        }
        val gifs = context.resources.getStringArray(resourceId)
        return gifs.random()
    }
}