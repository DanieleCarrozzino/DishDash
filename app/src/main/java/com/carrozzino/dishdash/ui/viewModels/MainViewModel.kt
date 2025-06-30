package com.carrozzino.dishdash.ui.viewModels

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseRealtimeDatabaseInterface
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseStorageInterface
import com.carrozzino.dishdash.data.repository.models.RecipeDayModel
import com.carrozzino.dishdash.data.repository.models.RecipeModel
import com.carrozzino.dishdash.ui.utility.getActualDate
import com.carrozzino.dishdash.ui.utility.getWeek
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
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class MainStatus {
    DEFAULT,
    EMPTY,
    INITIALIZED,
    REFRESHING
}

data class MainState (
    val recipes : List<RecipeDayModel>  = listOf<RecipeDayModel>(),
    val actualDate : String             = "",
    val state : MainStatus              = MainStatus.DEFAULT
)

data class AddingState (
    var uploading : Boolean = false,
    var error : Boolean = false,
    var uri : Uri? = null,
    var recipe : Recipe = Recipe()
)

data class GeneratingState (
    var generating : Boolean = false,
    var error : Boolean = false,
)

data class Recipe(
    var url : String = "",
    var title : String = "",
    var ingredients : String = "",
    var image : ImageBitmap? = null,
    var isSide : Boolean = false,
    var needASide : Boolean = false,
    var idImage : Int = 0,
    var seasons : List<Int> = listOf<Int>()
)

sealed class Intent {
    data class OnImageSelected(val uri : Uri, val image : ImageBitmap) : Intent()
    data class OnRecipeSaved(var recipe : Recipe) : Intent()
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

    private val _generatingState = MutableStateFlow(GeneratingState())
    val generatingState : StateFlow<GeneratingState> = _generatingState.asStateFlow()

    init {
        val days = getWeek()

        // Get the recipes from the real time database
        database.getValues("recipes_of_the_week", listOf())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val values = dataSnapshot
                        .getValue(object : GenericTypeIndicator<ArrayList<HashMap<String, Any>>>() {})
                    if(values?.size != 5) return

                    val dates : MutableList<RecipeDayModel> = mutableListOf()
                    values.forEachIndexed { index, recipe ->
                        dates.add(RecipeDayModel(
                            date = if(index < days.size) days[index] else "",
                            recipeModel = RecipeModel(
                                main = recipe["main"].toString(),
                                side = if(recipe.contains("side")) recipe["side"].toString() else "",
                                mainIngredients = recipe["mainIngredients"].toString(),
                                sideIngredients = if(recipe.contains("sideIngredients")) recipe["sideIngredients"].toString() else "",
                                urlImage = recipe["urlImage"].toString(),
                                idImage = if(recipe.contains("idImage") &&
                                    recipe["idImage"].toString().isNotEmpty())
                                    recipe["idImage"].toString().toInt() else 0
                            )
                        ))
                    }

                    _mainState.update {
                        it.copy(
                            recipes     = dates,
                            actualDate  = getActualDate(),
                            state       = if(dates.isEmpty()) MainStatus.EMPTY else MainStatus.INITIALIZED
                        )
                    }
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
                add(recipe = intent.recipe)
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
            recipe = it.recipe.copy(
                image = image
            )
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
            recipe = Recipe(),
        )}
    }

    private fun add(recipe : Recipe) {
        if(recipe.title.isEmpty() || recipe.ingredients.isEmpty()) {
            _addingState.update { it.copy( error = true )}
            return
        }

        _addingState.update { it.copy(
            uploading = true,
            error = false,
            recipe = recipe)}

        firestore.size(if(recipe.isSide) "total_side_recipes" else "total_recipes") {
            firestore.put(if(recipe.isSide) "total_side_recipes" else "total_recipes",
                it.toString(), hashMapOf<String, Any>(
                "title" to recipe.title,
                "ingredients" to recipe.ingredients,
                "seasons" to recipe.seasons,
                "urlImage" to recipe.url,
                "needASide" to recipe.needASide,
                "idImage" to recipe.idImage,
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
        _generatingState.update {
            it.copy(
                generating = true,
                error = false)}

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

            if(sizeMain == 0L && sizeSides == 0L) {
                _generatingState.update {
                    it.copy(
                        generating = false,
                        error = true)}
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
                val side : DocumentSnapshot? = if(main["needASide"]?.toString() == "true") {
                     suspendCoroutine<DocumentSnapshot> { block ->
                        firestore.get("total_side_recipes", randomSides[index].toString())
                            .addOnCompleteListener { result ->
                                block.resume(result.result)
                            }
                    }
                } else null

                val hash = hashMapOf<String, Any>(
                    "main" to (main["title"]?.toString() ?: ""),
                    "mainIngredients" to (main["ingredients"]?.toString() ?: ""),
                    "urlImage" to (main["urlImage"]?.toString() ?: ""),
                    "idImage" to (main["idImage"]?.toString() ?: "")
                )

                side?.let {
                    hash.put("side", (it["title"]?.toString() ?: ""))
                    hash.put("sideIngredients", (it["ingredients"]?.toString() ?: ""))
                }

                database.putValues("recipes_of_the_week", listOf(), index.toInt(), hash)
                    .addOnCompleteListener { result ->
                        _generatingState.update {
                            it.copy(
                                generating = false,
                                error = !result.isSuccessful)}
                    }
            }
        }
    }
}