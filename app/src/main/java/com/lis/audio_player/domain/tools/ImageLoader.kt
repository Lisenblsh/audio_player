package com.lis.audio_player.domain.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class ImageLoader {
    fun setImage(image: Any?, imageView: ImageView) {
        Glide.with(imageView)
            .load(image)
            .into(imageView)
    }

    fun setImageOnBackground(image: Any?, imageView: ImageView){
        Glide.with(imageView)
            .load(image)
            .apply(RequestOptions.bitmapTransform(jp.wasabeef.glide.transformations.BlurTransformation(5,5)))
            .into(imageView)
    }

    fun setLargeIconToNotification(artworkUri: Uri?, callback: PlayerNotificationManager.BitmapCallback, context: Context) {
        Glide.with(context).asBitmap()
            .load(artworkUri)
            .into(object: CustomTarget<Bitmap>(){
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    callback.onBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }
}