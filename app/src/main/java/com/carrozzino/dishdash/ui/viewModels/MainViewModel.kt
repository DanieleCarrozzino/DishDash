package com.carrozzino.dishdash.ui.viewModels

import android.net.Uri
import android.widget.ImageButton
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseRealtimeDatabaseInterface
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseStorageInterface
import com.carrozzino.dishdash.data.repository.models.RecipeDayModel
import com.carrozzino.dishdash.data.repository.models.RecipeModel
import com.carrozzino.dishdash.ui.utility.getActualDate
import com.carrozzino.dishdash.ui.utility.getRemainingDaysWithDates
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class MainState (
    val recipes : List<RecipeDayModel> = listOf<RecipeDayModel>(),
    val actualDate : String = ""
)

data class AddingState (
    var uploading : Boolean = false,
    var error : Boolean = false,
    var uri : Uri? = null,
    var url : String = "",
    var title : String = "",
    var ingredients : String = "",
    var image : ImageBitmap? = null,
    var isSide : Boolean = false,
    var seasons : List<Int> = listOf<Int>()
)

sealed class Intent {
    data class OnImageSelected(val uri : Uri, val image : ImageBitmap) : Intent()
    data class OnRecipeSaved(
        val title : String,
        val ingredients : String,
        val isSide : Boolean,
        val seasons : List<Int>,
        var url : String
    ) : Intent()
    data object OnClearNewRecipe : Intent()
    data object OnGenerateNewWeek : Intent()
}

@HiltViewModel
class MainViewModel @Inject constructor (
    val database : FirebaseRealtimeDatabaseInterface,
    val firestore : FirebaseFirestoreDatabaseInterface,
    val storage : FirebaseStorageInterface
) : ViewModel() {

    private val _mainState = MutableStateFlow(MainState())
    val mainState : StateFlow<MainState> = _mainState.asStateFlow()

    private val _addingState = MutableStateFlow(AddingState())
    val addingState : StateFlow<AddingState> = _addingState.asStateFlow()

    init {
        val days = getRemainingDaysWithDates()

        // Get the recipes from the real time database
        database.getValues("recipes_of_the_week", listOf())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val values = dataSnapshot
                        .getValue(object : GenericTypeIndicator<ArrayList<HashMap<String, Any>>>() {})
                    if(values?.size != 5) return

                    var startingIndex = 0.coerceAtLeast(values.size - days.size)
                    val dates = days.map {
                        val recipe = values[startingIndex]
                        startingIndex++

                        RecipeDayModel(
                            date = it,
                            recipeModel = RecipeModel(
                                main = recipe["main"].toString(),
                                side = recipe["side"].toString(),
                                mainIngredients = recipe["mainIngredients"].toString(),
                                sideIngredients = recipe["sideIngredients"].toString(),
                                urlImage = recipe["urlImage"].toString()
                            )
                        )
                    }

                    _mainState.update {
                        it.copy(
                            recipes = dates,
                            actualDate = getActualDate()
                        )}
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun onReceive(intent : Intent) = viewModelScope.launch(Dispatchers.IO) {
        when(intent) {
            is Intent.OnImageSelected -> {
                uploadImage(intent.uri, intent.image)
            }
            is Intent.OnRecipeSaved -> {
                add(
                    title = intent.title,
                    ingredients = intent.ingredients,
                    isSide = intent.isSide,
                    seasons = intent.seasons,
                    url = intent.url
                )
            }
            is Intent.OnClearNewRecipe -> {
                clearAddingRecipe()
            }
            is Intent.OnGenerateNewWeek -> {
                generate()
            }
            else -> {}
        }
    }


    private fun uploadImage(uri : Uri, image : ImageBitmap) {
        _addingState.update { it.copy(
            uploading = false,
            uri = uri,
            image = image
        )}
//        storage.upload(uri).addOnCompleteListener {
//            val url = it.toString()
//
//            _addingState.update { it.copy(
//                uploading = false,
//                url = url
//            )}
//        }
    }

    private fun clearAddingRecipe() {
        _addingState.update { it.copy(
            uploading = false,
            error = false,
            title = "",
            ingredients = "",
            url = "",
            seasons = listOf(),
            isSide = false
        )}
    }

    private fun add(
        title : String = "",
        ingredients : String = "",
        isSide : Boolean = false,
        seasons : List<Int> = listOf<Int>(),
        url : String = "",
    ) {
        if(title.isEmpty() || ingredients.isEmpty()) {
            _addingState.update { it.copy( error = true )}
            return
        }

        _addingState.update { it.copy(
            uploading = true,
            error = false,
            title = title,
            ingredients = ingredients,
            url = url,
            seasons = seasons,
            isSide = isSide
            )}

        firestore.size(if(isSide) "total_side_recipes" else "total_recipes") {
            firestore.put(if(isSide) "total_side_recipes" else "total_recipes",
                it.toString(), hashMapOf<String, Any>(
                "title" to title,
                "ingredients" to ingredients,
                "seasons" to seasons,
                "urlImage" to url
            )).addOnCompleteListener { response ->
                if(response.isSuccessful) {
                    clearAddingRecipe()
                } else {
                    _addingState.update { it.copy(
                        uploading = false,
                        error = true )}
                }
            }
        }
    }

    fun generate() {
        viewModelScope.launch(Dispatchers.IO) {
            val sizeSides = suspendCoroutine<Long> { block ->
                firestore.size("total_side_recipes") { result ->
                    block.resume(result)
                }
            }

            val sizeMain = suspendCoroutine<Long> { block ->
                firestore.size("total_recipes") { result ->
                    block.resume(result)
                }
            }

            val randomSides = (0..<sizeSides).shuffled().take(minOf(sizeSides, 5).toInt())
            val randomMain = (0..<sizeMain).shuffled().take(minOf(sizeMain, 5).toInt())

            for(index in 0..<minOf(randomSides.size, randomMain.size)) {

                // Get the main
                val main = suspendCoroutine<DocumentSnapshot> { block ->
                    firestore.get("total_recipes", randomMain[index].toString()).addOnCompleteListener{ result ->
                        block.resume(result.result)
                    }
                }

                // Get the side
                val side = suspendCoroutine<DocumentSnapshot> { block ->
                    firestore.get("total_side_recipes", randomSides[index].toString()).addOnCompleteListener{ result ->
                        block.resume(result.result)
                    }
                }

                database.putValues(
                    "recipes_of_the_week",
                    listOf(), index.toInt(),
                    hashMapOf<String, Any>(
                        "main" to (main["title"]?.toString() ?: ""),
                        "mainIngredients" to (main["ingredients"]?.toString() ?: ""),
                        "side" to (side["title"]?.toString() ?: ""),
                        "sideIngredients" to (side["ingredients"]?.toString() ?: ""),
                        "urlImage" to (main["urlImage"]?.toString() ?: "")
                    ))
            }
        }
    }
}