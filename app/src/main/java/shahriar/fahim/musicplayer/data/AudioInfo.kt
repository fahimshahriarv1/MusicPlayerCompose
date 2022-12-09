package shahriar.fahim.musicplayer.data

import android.net.Uri

data class AudioInfo(
    val link : Uri,
    val artist : String,
    val thumbNail : Int,
    val duration : Int,
    val title : String
)