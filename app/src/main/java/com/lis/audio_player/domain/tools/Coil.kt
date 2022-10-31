package com.lis.audio_player.domain.tools

import android.widget.ImageView
import coil.load
import jp.wasabeef.transformers.coil.BlurTransformation

class Coil {
    fun setImage(image: Any?, imageView: ImageView) {
        if(image is Int){
            imageView.load(image) {
                placeholder(image)
            }
        } else if (image is String){
            imageView.load(image)
        }
    }

    fun setImageOnBackground(image: Any?, imageView: ImageView){
        imageView.load(image) {
            transformations(
                BlurTransformation(
                imageView.context,
                5,
                5
            )
            )
        }
    }
}