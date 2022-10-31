package com.lis.audio_player.domain.tools

import android.widget.ImageView
import coil.load

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
}