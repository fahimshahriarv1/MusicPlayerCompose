package shahriar.fahim.musicplayer.ui.theme

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shahriar.fahim.musicplayer.R

@SuppressLint("CoroutineCreationDuringComposition")
@Preview
@Composable
fun MainPreview() {
//    val progress = remember {
//        mutableStateOf(0F)
//    }
    MainPlayerScreen()
//    CoroutineScope(context = EmptyCoroutineContext).launch {
//        progress.value++
//
//        if (progress.value >= 360f)
//            progress.value = 0f
//    }
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

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
        )

//        Button(onClick = {
//            animate.value = !animate.value
//        }) {
//            Text(text = "mdvlsmkvds")
//        }

        Spacer(modifier = Modifier.height(20.dp))

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
            valueRange = 0f..360f
        )
    }


}