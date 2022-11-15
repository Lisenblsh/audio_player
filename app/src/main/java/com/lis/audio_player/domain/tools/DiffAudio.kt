package com.lis.audio_player.domain.tools

import com.github.difflib.DiffUtils
import com.github.difflib.patch.AbstractDelta
import com.github.difflib.patch.DeltaType
import com.github.difflib.patch.Patch
import com.lis.audio_player.domain.adapters.MusicPagingAdapter
import com.lis.audio_player.domain.baseModels.AudioModel
import com.lis.audio_player.domain.tools.exoPlayer.AudioDataUpdaterDiff
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DiffAudio(
    private val context: CoroutineContext,
    private val audioList: ArrayList<AudioModel>,
):AudioDataUpdaterDiff {
    private var oldData: List<AudioModel>? = null
    override suspend fun update(incoming: List<AudioModel>) {
        if(oldData.isNullOrEmpty()){
            add(incoming)
            oldData = incoming
        } else {

            val patch: Patch<AudioModel> = withContext(context) {
                DiffUtils.diff(oldData, incoming)
            }

            patch.deltas.forEach { delta ->
                when (delta.type) {
                    DeltaType.CHANGE -> {
                        delta.delete()
                        delta.insert()
                    }
                    DeltaType.DELETE -> {
                        delta.delete()
                    }
                    DeltaType.INSERT -> {
                        delta.insert()
                    }
                    DeltaType.EQUAL -> {}
                    null -> {}
                }

            }

            oldData = incoming
        }
    }

    override suspend fun add(incoming: List<AudioModel>) {
        audioList.clear()
        audioList.addAll(incoming)
    }

    private fun AbstractDelta<AudioModel>.delete() {
    }

    private fun AbstractDelta<AudioModel>.insert() {
        TODO("Not yet implemented")
    }
}
