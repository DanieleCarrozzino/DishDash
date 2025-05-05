package com.carrozzino.dishdash.ui.screen

import android.Manifest
import android.R.attr.radius
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.Layout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.carrozzino.dishdash.BuildConfig
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.theme.Red50
import com.carrozzino.dishdash.ui.theme.White90
import com.carrozzino.dishdash.ui.viewModels.AddingState
import com.carrozzino.dishdash.ui.viewModels.Intent
import com.carrozzino.dishdash.ui.viewModels.Intent.OnImageSelected
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import com.carrozzino.dishdash.ui.viewModels.Recipe
import java.io.File
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.times
import kotlin.unaryMinus

@Preview
@Composable
fun AddingScreenPresentationPreview() {
    AddingScreenPresentation()
}

@Composable
fun LoadingVegetables(
    listOfImages : List<Int> = listOf<Int>(
        R.drawable.tomato, R.drawable.carrot, R.drawable.broccolo, R.drawable.mushroom
    )
) {
    val offsetX = 120
    val offsetY = 60

    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 42000, easing = LinearEasing),
        )
    )

    val angle2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 48000, easing = LinearEasing),
        )
    )

    val scale1 by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 58000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 54000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val rad = Math.toRadians(angle2.toDouble())
    val offset1X = cos(-rad).toFloat()
    val offset1Y = sin(-rad).toFloat()

    val rad2 = Math.toRadians(90 + angle2.toDouble())
    val offset2X = cos(rad2).toFloat()
    val offset2Y = sin(rad2).toFloat()

    val rad3 = Math.toRadians(180 + angle.toDouble())
    val offset3X = cos(-rad3).toFloat()
    val offset3Y = sin(-rad3).toFloat()

    val rad4 = Math.toRadians(270 + angle.toDouble())
    val offset4X = cos(rad4).toFloat()
    val offset4Y = sin(rad4).toFloat()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = listOfImages[0]),
            contentDescription = null,
            modifier = Modifier
                .zIndex(scale1)
                .align(Alignment.Center)
                .size(200.dp)
                .offset(x = ((offset1X * offsetX)).dp, y = ((offset1Y * offsetY)).dp)
                .graphicsLayer (
                    scaleX = 0.88f + (0.12f * -scale1),
                    scaleY = 0.88f + (0.12f * -scale1),
                    rotationZ = 360 * -scale1
                )
        )

        Image(
            painter = painterResource(id = listOfImages[1]),
            contentDescription = null,
            modifier = Modifier
                .zIndex(scale2)
                .align(Alignment.Center)
                .size(190.dp)
                .offset(x = (offset2X * offsetX).dp, y = (offset2Y * offsetY).dp)
                .rotate(40f)
                .graphicsLayer (
                    scaleX = 0.9f + (0.1f * scale2),
                    scaleY = 0.9f + (0.1f * scale2),
                    rotationZ = 360 * scale2
                )
        )

        Image(
            painter = painterResource(id = listOfImages[2]),
            contentDescription = null,
            modifier = Modifier
                .zIndex(scale3)
                .align(Alignment.Center)
                .size(185.dp)
                .offset(x = (offset3X * offsetX).dp, y = (offset3Y * offsetY).dp)
                .graphicsLayer (
                    scaleX = 0.85f + (0.15f * scale3),
                    scaleY = 0.85f + (0.15f * scale3),
                    rotationZ = 360 * scale3
                )
        )

        Image(
            painter = painterResource(id = listOfImages[3]),
            contentDescription = null,
            modifier = Modifier
                .zIndex(scale2)
                .align(Alignment.Center)
                .size(170.dp)
                .rotate(-40f)
                .offset(x = (offset4X * offsetX).dp, y = (offset4Y * offsetY).dp)
                .graphicsLayer (
                    scaleX = 0.92f + (0.08f * -scale2),
                    scaleY = 0.92f + (0.08f * -scale2),
                    rotationZ = 360 * -scale2
                )
        )
    }
}

@Composable
fun AddingScreenPresentation(
    modifier : Modifier = Modifier,
    viewModel : MainViewModel = hiltViewModel<MainViewModel>(),
    navController : NavController = rememberNavController(),
    navigate : () -> Unit = {}
) {

    Box(modifier = modifier.fillMaxSize()) {

        Box(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    modifier = Modifier,
                    text = "Add a New Recipe",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    text = "Recipes added here will be included in the pool " +
                            "used to generate your weekly menus.",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        LoadingVegetables()
        
        Button(
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(10f),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp
            ),
            onClick = {
                navController.navigate(Screen.Adding.route)
            }
        ) {
            Text(
                text = "Create a new recipe!",
                style = MaterialTheme.typography.titleSmall
            )
        }

        Button(
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 18.dp)
                .align(Alignment.BottomEnd)
                .zIndex(10f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            onClick = { navigate() }
        ) {
            Text(
                text = "Go back\nto the Calendar",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "navigate back button",
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun AddingScreen(
    modifier : Modifier = Modifier,
    navController : NavController = rememberNavController(),
    viewModel : MainViewModel = hiltViewModel<MainViewModel>()
) {
    val addingState = viewModel.addingState.collectAsState().value

    AnimatedContent (
        targetState = addingState.uploading
    ) { uploading ->
        if (uploading) {
            LoadingVegetables()
        } else {
            AddingCore(
                modifier = modifier,
                navController = navController,
                state = addingState) { intent ->
                viewModel.onReceive(intent = intent)
            }
        }
    }
}

fun createInternalImageFile(context: Context): File {
    return File.createTempFile("temp_image_file", ".jpg", context.cacheDir)
}

@Composable
fun AddingCore(
    modifier : Modifier = Modifier,
    navController : NavController = rememberNavController(),
    state : AddingState = AddingState(),
    click : (intent : Intent) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var titleRecipe by remember { mutableStateOf(state.recipe.title) }
    var ingredients by remember { mutableStateOf(state.recipe.ingredients) }
    var isSide by remember { mutableStateOf(state.recipe.isSide) }
    var needASide by remember { mutableStateOf(state.recipe.needASide) }
    val seasons = remember { mutableStateListOf<Int>().apply { addAll(state.recipe.seasons) } }

    var urlImage by remember { mutableStateOf(state.recipe.url) }

    var uri by remember { mutableStateOf<Uri?>(state.uri) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(state.recipe.image) }

    // Launcher to take a picture and save the bitmap
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                bitmap = BitmapFactory.decodeStream(inputStream).asImageBitmap()
                bitmap?.let { image -> click(OnImageSelected(it, image)) }
            }
        }
    }

    // Launcher to request camera permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (permissionGranted) {
            val photoFile = createInternalImageFile(context)
            uri = FileProvider.getUriForFile(context,
                "${BuildConfig.APPLICATION_ID}.provider", photoFile)

            uri?.let { cameraLauncher.launch(it) }
        }
    }

    // Launcher to open multiple picker
    val pickImageFromAlbumLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { urls ->
        if(urls.isEmpty()) return@rememberLauncherForActivityResult

        val uri = urls.first()
        val inputStream = context.contentResolver.openInputStream(uri)
        bitmap = BitmapFactory.decodeStream(inputStream).asImageBitmap()
        bitmap?.let { image -> click(OnImageSelected(uri, image)) }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                modifier = Modifier.padding(start = 18.dp, top = 8.dp),
                text = "Adding recipe",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                modifier = Modifier.padding(start = 18.dp, bottom = 4.dp),
                text = "adding a new recipe or just a new side for your wonderful combination",
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (uri?.path?.isNotEmpty() == true && bitmap != null) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = bitmap!!,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                } else if (urlImage.isNotEmpty()) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(urlImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = "main image",
                        contentScale = ContentScale.Crop
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.Center),
                    visible = !state.uploading
                ) {

                    Row(modifier = Modifier.animateContentSize()) {
                        NewActionButton(
                            painter = painterResource(R.drawable.add_photo_alternate_24px),
                            background = MaterialTheme.colorScheme.primary
                        ) {
                            val mediaRequest =
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            pickImageFromAlbumLauncher.launch(mediaRequest)
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        NewActionButton(
                            painter = painterResource(R.drawable.photo_camera_24px),
                            background = MaterialTheme.colorScheme.primary
                        ) {
                            val permission = Manifest.permission.CAMERA
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    permission
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(permission)
                                return@NewActionButton
                            }

                            val photoFile = createInternalImageFile(context)
                            uri = FileProvider.getUriForFile(
                                context,
                                "${BuildConfig.APPLICATION_ID}.provider", photoFile
                            )

                            uri?.let { cameraLauncher.launch(it) }
                        }
                    }
                }
            }

            TitleAndTextField(
                title = "Url of the main image",
                text = state.recipe.url
            ) { urlImage = it }
            Spacer(modifier = Modifier.height(5.dp))
            TitleAndTextField(
                title = "Title of the recipe",
                text = state.recipe.title,
                error = state.error) { titleRecipe = it }
            Spacer(modifier = Modifier.height(5.dp))
            TitleAndTextField(
                title = "Ingredients of the recipe",
                lines = 10,
                text = state.recipe.ingredients,
                error = state.error
            ) { ingredients = it }

            Spacer(modifier = Modifier.height(5.dp))

            Row(modifier = Modifier.padding(horizontal = 18.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
                    text = "Is it a side this recipe?",
                    style = MaterialTheme.typography.titleSmall
                )

                Switch(
                    modifier = Modifier.padding(start = 6.dp),
                    checked = isSide,
                    onCheckedChange = {
                        isSide = it
                    }
                )
            }

            Row(modifier = Modifier
                .padding(horizontal = 18.dp)
                .alpha(if(isSide) 0.5f else 1f)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
                    text = "Does it need a side?",
                    style = MaterialTheme.typography.titleSmall
                )

                Switch(
                    modifier = Modifier.padding(start = 6.dp),
                    enabled = !isSide,
                    checked = needASide,
                    onCheckedChange = {
                        needASide = it
                    }
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            Text(
                modifier = Modifier.padding(start = 18.dp, bottom = 4.dp, top = 4.dp),
                text = "Seasons for this recipe",
                style = MaterialTheme.typography.titleSmall
            )

            Row(modifier = Modifier.padding(horizontal = 18.dp)) {
                CheckBoxTitle(
                    modifier = Modifier.weight(1f),
                    title = "Spring",
                    checked = seasons.contains(0)
                ) { if (it) seasons.add(0) else seasons.remove(0) }
                CheckBoxTitle(
                    modifier = Modifier.weight(1f),
                    title = "Summer",
                    checked = seasons.contains(1)
                ) { if (it) seasons.add(1) else seasons.remove(1) }
                CheckBoxTitle(
                    modifier = Modifier.weight(1f),
                    title = "Autumn",
                    checked = seasons.contains(2)
                ) { if (it) seasons.add(2) else seasons.remove(2) }
                CheckBoxTitle(
                    modifier = Modifier.weight(1f),
                    title = "Winter",
                    checked = seasons.contains(3)
                ) { if (it) seasons.add(3) else seasons.remove(3) }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(
                modifier = Modifier.padding(
                    start = 18.dp,
                    end = 9.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ).weight(1f),
                enabled = !state.uploading,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                onClick = {
                    click(Intent.OnRecipeSaved(
                        Recipe(
                            title = titleRecipe,
                            ingredients = ingredients,
                            isSide = isSide,
                            seasons = seasons,
                            url = urlImage,
                            needASide = needASide
                        )
                    ))
                }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add button",
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    tint = White90
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "add", color = White90)
            }

            Button(
                modifier = Modifier.padding(
                    start = 9.dp,
                    end = 18.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
                enabled = !state.uploading,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red50,
                ),
                onClick = {
                    navController.popBackStack()
                    click(Intent.OnClearNewRecipe)
                }) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Clear button",
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    tint = White90
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "delete", color = White90)
            }
        }
    }
}

@Composable
fun CheckBoxTitlePreview() {
    CheckBoxTitle(title = "Main title")
}

@Composable
fun CheckBoxTitle(
    modifier : Modifier = Modifier,
    title : String = "",
    checked : Boolean = false,
    onCheckedChanged : (Boolean) -> Unit = {}
) {

    Box(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(3.dp).align(Alignment.Center)) {
            Text(
                modifier = Modifier
                    .padding(start = 3.dp, bottom = 6.dp, top = 3.dp, end = 3.dp),
                text = title,
                style = MaterialTheme.typography.titleSmall)

            Checkbox(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                checked = checked,
                onCheckedChange = { onCheckedChanged(it) }
            )
        }
    }

}

@Composable
fun TitleAndTextField(
    title : String = "",
    lines : Int = 1,
    text : String = "",
    error : Boolean = false,
    onTextChanged : (String) -> Unit = {},
    ) {

    var text by remember { mutableStateOf(text) }

    Text(
        modifier = Modifier.padding(start = 18.dp, bottom = 4.dp, top = 4.dp),
        text = title,
        style = MaterialTheme.typography.titleSmall)

    TextField(
        modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
        value = text,
        isError = error && text.isEmpty(),
        onValueChange = {
            text = it
            onTextChanged(it) },
        singleLine = lines == 1,
        minLines = lines,
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.error,
            errorLabelColor = Red50,
            errorTextColor = Red50
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AddingPreview() {
    AddingCore()
}

@Preview(showBackground = false)
@Composable
fun AddingPreviewWithoutBackground() {
    AddingCore()
}