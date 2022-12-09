package shahriar.fahim.musicplayer.ui.theme

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import shahriar.fahim.musicplayer.R

@SuppressLint("CoroutineCreationDuringComposition")
@Preview
@Composable
fun MainPreview() {
    MainPlayerScreen()
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainPlayerScreen() {
    val animate = remember {
        mutableStateOf(true)
    }

    val rotateTo = remember {
        mutableStateOf(0f)
    }
    var isProgressAnimated by remember {
        mutableStateOf(false)
    }

    val progressValue = animateFloatAsState(
        targetValue = if (animate.value) {
            if (isProgressAnimated) 360f else 0f
        } else 360f,
        animationSpec = infiniteRepeatable(
            tween(
                10000,
                delayMillis = 0,
                easing = LinearEasing
            ),
            RepeatMode.Restart
        )
    )

    LaunchedEffect(key1 = true) {
        isProgressAnimated = animate.value
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Gray)
    ) {

        val (disk, slider, title, other) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "hudai",
            modifier = Modifier
                .rotate(if (animate.value) progressValue.value else rotateTo.value)
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(50)
                )
                .clip(shape = CircleShape)
                .height(200.dp)
                .width(200.dp)
                .constrainAs(disk) {
                    top.linkTo(parent.top, 100.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(text = "Hello",
            modifier = Modifier.constrainAs(title) {
                top.linkTo(disk.top)
                bottom.linkTo(disk.bottom)
                start.linkTo(disk.start)
                end.linkTo(disk.end)
            })

        Slider(
            value = rotateTo.value,
            onValueChange = {
                animate.value = false
                rotateTo.value = it
            },
            onValueChangeFinished = {
                animate.value = true
            },
            enabled = true,
            valueRange = 0f..360f,
            colors = SliderDefaults.colors(Color.LightGray, Color.DarkGray),
            modifier = Modifier
                .padding(20.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(50)
                )
                .constrainAs(slider) {
                    top.linkTo(disk.bottom, 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .constrainAs(other) {
                    top.linkTo(slider.bottom, 20.dp)
                    start.linkTo(parent.start, 20.dp)
                    end.linkTo(parent.end, 20.dp)
                    width = Dimension.fillToConstraints
                }
        ) {

            Image(
                painter = painterResource(id = com.google.android.exoplayer2.R.drawable.exo_controls_previous),
                contentDescription = "prev"
            )
            Image(
                painter = painterResource(id = if (animate.value) com.google.android.exoplayer2.R.drawable.exo_controls_pause else com.google.android.exoplayer2.R.drawable.exo_controls_play),
                contentDescription = "play",
                modifier = Modifier
                    .height(60.dp)
                    .width(60.dp)
                    .clickable {
                        animate.value = !animate.value
                    }
            )
            Image(
                painter = painterResource(id = com.google.android.exoplayer2.R.drawable.exo_controls_next),
                contentDescription = "next"
            )
        }
    }
}