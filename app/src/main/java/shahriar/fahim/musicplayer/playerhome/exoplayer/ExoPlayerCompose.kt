package tech.shikho.android.revamp.presentation.exoplayer

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING
import kotlinx.coroutines.delay
import tech.shikho.android.R
import tech.shikho.android.util.formatMinSec

@Composable
@Preview
fun ExoPlayerComposePreview() {
    val videoUrl =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    val context = LocalContext.current

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {

            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(videoUrl))
                .build()
            setMediaItem(mediaItem)

            playWhenReady = false
            prepare()
        }
    }

    ExoPlayerCompose(
        exoPlayer = exoPlayer
    )
}

@Composable
fun ExoPlayerCompose(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    isFullScreen: MutableState<Boolean> = mutableStateOf(false),
    showThumbNail: MutableState<Boolean> = mutableStateOf(true),
    thumbNailImage: MutableState<String> = mutableStateOf(""),
    icPlayerControlVisible: MutableState<Boolean> = mutableStateOf(true),
    isPlaying: MutableState<Boolean> = mutableStateOf(false),
    totalDuration: MutableState<Long> = mutableStateOf(0L),
    progress: MutableState<Long> = mutableStateOf(0L),
    bufferProgress: MutableState<Float> = mutableStateOf(0f),
    changeScreenOrientation: () -> Unit = {},
    speedControlClicked: () -> Unit = {},
    resolutionControlClicked: () -> Unit = {},
    closeIconClicked: () -> Unit = {}
) {
    val playerHeight = LocalConfiguration.current.screenWidthDp.toFloat() * (232f / 412f)

    ConstraintLayout(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    ) {
        val (playerView, videoThumb, control) = createRefs()

        AndroidView(
            factory = {
                StyledPlayerView(it).apply {
                    hideController()
                    player = exoPlayer
                    controllerAutoShow = false
                    useController = false
                    setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING)
                }
            },
            modifier = if (!isFullScreen.value)
                Modifier
                    .constrainAs(playerView) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        width = Dimension.fillToConstraints
                    }
                    .height(playerHeight.dp)
                    .background(color = colorResource(id = R.color.black))
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        icPlayerControlVisible.value = !icPlayerControlVisible.value
                    }
            else
                Modifier
                    .constrainAs(playerView) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        icPlayerControlVisible.value = !icPlayerControlVisible.value
                    }
        )

        AnimatedVisibility(
            visible = showThumbNail.value,
            exit = fadeOut(),
            modifier = Modifier
                .background(colorResource(id = R.color.black))
                .constrainAs(videoThumb) {
                    top.linkTo(playerView.top)
                    end.linkTo(playerView.end)
                    start.linkTo(playerView.start)
                    bottom.linkTo(playerView.bottom)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbNailImage.value)
                    .decoderFactory(SvgDecoder.Factory())
                    .error(R.drawable.mentor)
                    .build(),
                placeholder = painterResource(R.drawable.mentor),
                contentDescription = "Video thumb",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }

        AnimatedVisibility(
            visible = icPlayerControlVisible.value,
            exit = fadeOut(),
            enter = fadeIn(),
            modifier = Modifier
                .constrainAs(control) {
                    top.linkTo(playerView.top)
                    end.linkTo(playerView.end)
                    start.linkTo(playerView.start)
                    bottom.linkTo(playerView.bottom)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxHeight()
            ) {
                val (fullScreen, playPause, seek, bufferSeek, forward, backward, durationText, closeIcon, speedControl, videoResolution) = createRefs()

                if (isFullScreen.value)
                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "full screen",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .constrainAs(closeIcon) {
                                end.linkTo(parent.end, 20.dp)
                                top.linkTo(parent.top, 20.dp)
                            }
                            .size(20.dp)
                            .clickable {
                                closeIconClicked()
                            }
                    )

                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_speed_24),
                    contentDescription = "full screen",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .constrainAs(speedControl) {
                            start.linkTo(parent.start, 20.dp)
                            top.linkTo(parent.top, 20.dp)
                        }
                        .size(20.dp)
                        .clickable {
                            speedControlClicked()
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_settings_black_24dp),
                    contentDescription = "full screen",
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.logoPink)),
                    modifier = Modifier
                        .constrainAs(videoResolution) {
                            start.linkTo(parent.start, 20.dp)
                            top.linkTo(speedControl.bottom, 10.dp)
                        }
                        .size(20.dp)
                        .clickable {
                            resolutionControlClicked()
                        }
                )

                Image(
                    painter = if (isFullScreen.value)
                        painterResource(id = R.drawable.ic_player_fullscreen_exit)
                    else
                        painterResource(id = R.drawable.exo_icon_fullscreen_enter),
                    contentDescription = "full screen",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .constrainAs(fullScreen) {
                            end.linkTo(seek.end)
                            bottom.linkTo(seek.top)
                        }
                        .size(if (isFullScreen.value) 30.dp else 20.dp)
                        .clickable {
                            changeScreenOrientation()
                        }
                )

                Text(
                    text = progress.value.formatMinSec() + " / " + totalDuration.value.formatMinSec(),
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.gray_light),
                    modifier = Modifier.constrainAs(durationText) {
                        start.linkTo(parent.start, if (isFullScreen.value) 20.dp else 10.dp)
                        bottom.linkTo(seek.top, (-5).dp)
                    }
                )

                Image(
                    painter = if (isPlaying.value)
                        painterResource(id = R.drawable.ic_pause)
                    else
                        painterResource(id = R.drawable.ic_play_home_work),
                    contentDescription = "play pause",
                    modifier = Modifier
                        .constrainAs(playPause) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                            start.linkTo(parent.start)
                        }
                        .size(30.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            showThumbNail.value = false
                            if (exoPlayer.isPlaying) {
                                isPlaying.value = false
                                exoPlayer.pause()
                            } else {
                                isPlaying.value = true
                                exoPlayer.play()
                            }
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_video_forw_button),
                    contentDescription = "forward button",
                    modifier = Modifier
                        .constrainAs(forward) {
                            start.linkTo(playPause.end)
                            end.linkTo(parent.end)
                            top.linkTo(playPause.top)
                            bottom.linkTo(playPause.bottom)
                        }
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            exoPlayer.seekForward()
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_video_rew_button),
                    contentDescription = "backward button",
                    modifier = Modifier
                        .constrainAs(backward) {
                            start.linkTo(parent.start)
                            end.linkTo(playPause.start)
                            top.linkTo(playPause.top)
                            bottom.linkTo(playPause.bottom)
                        }
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            exoPlayer.seekBack()
                        }
                )

                Slider(
                    value = bufferProgress.value,
                    enabled = false,
                    onValueChange = { /*do nothing*/ },
                    valueRange = 0f..100f,
                    colors =
                    SliderDefaults.colors(
                        disabledThumbColor = colorResource(id = R.color.transparent),
                        disabledActiveTrackColor = colorResource(id = R.color.gray_medium)
                    ),
                    modifier = Modifier.constrainAs(bufferSeek) {
                        start.linkTo(seek.start)
                        end.linkTo(seek.end)
                        bottom.linkTo(seek.bottom)
                        top.linkTo(seek.top)
                        width = Dimension.fillToConstraints
                    }
                )

                Slider(
                    value = progress.value.toFloat(),
                    valueRange = 0f..totalDuration.value.toFloat(),
                    onValueChange = {
                        progress.value = it.toLong()
                        exoPlayer.seekTo(it.toLong())
                        icPlayerControlVisible.value = true
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = colorResource(id = R.color.red),
                        activeTickColor = colorResource(id = R.color.pink),
                        inactiveTickColor = colorResource(id = R.color.gray_medium),
                        activeTrackColor = colorResource(id = R.color.pink)
                    ),
                    modifier = Modifier.constrainAs(seek) {
                        start.linkTo(parent.start, 20.dp)
                        end.linkTo(parent.end, 20.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                )
            }
        }
    }

    DisposableEffect(key1 = Unit, effect = {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                totalDuration.value = player.duration.coerceAtLeast(0L)
                progress.value = player.currentPosition.coerceAtLeast(0L)
                bufferProgress.value = player.bufferedPercentage.toFloat()
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    })

    if (isPlaying.value) {
        LaunchedEffect(key1 = icPlayerControlVisible.value, block = {
            delay(5000)
            icPlayerControlVisible.value = false
        })
    }

    LaunchedEffect(key1 = true, block = {
        while (true) {
            if (isPlaying.value)
                progress.value = progress.value + 1000L
            delay((1000 / exoPlayer.playbackParameters.speed).toLong())
        }
    })
}