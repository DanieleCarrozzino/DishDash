package com.carrozzino.dishdash.ui.viewModels

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carrozzino.dishdash.data.internal.Preferences
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseRealtimeDatabaseInterface
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseStorageInterface
import com.carrozzino.dishdash.data.repository.RecipeModelRepository
import com.carrozzino.dishdash.data.database.models.RecipeDayModel
import com.carrozzino.dishdash.data.database.models.RecipeModel
import com.carrozzino.dishdash.ui.utility.ViewModelUtility
import com.carrozzino.dishdash.ui.utility.ViewModelUtility.Companion.RECIPE_MODULE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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
    val recipes         : List<RecipeDayModel>  = listOf<RecipeDayModel>(),
    val actualDate      : String                = "",
    val state           : MainStatus            = MainStatus.DEFAULT,
    val personalCode    : String                = ""
)

data class AddingState (
    val uploading   : Boolean = false,
    val error       : Boolean = false,
    val uri         : Uri? = null,
    val recipe      : Recipe = Recipe()
)

data class GeneratingState (
    val generating : Boolean = false,
    val error : Boolean = false,
)

data class Recipe(
    val url             : String = "",
    val title           : String = "",
    val ingredients     : String = "",
    val link            : String = "",
    val image           : ImageBitmap? = null,
    val isSide          : Boolean = false,
    val needASide       : Boolean = false,
    val idImage         : Int = 0,
    val seasons         : List<Int> = listOf<Int>()
)

sealed class UserIntent {
    data class OnImageSelected(val uri : Uri, val image : ImageBitmap) : UserIntent()
    data class OnRecipeSaved(var recipe : Recipe) : UserIntent()
    data object OnClearNewRecipe : UserIntent()
    data object OnGenerateNewWeek : UserIntent()
    data class OnOpenLinkRecipe(val link : String) : UserIntent()
    data class OnUpdatingNewCode(val code : String) : UserIntent()
}

@HiltViewModel
class MainViewModel @Inject constructor (
    val database        : FirebaseRealtimeDatabaseInterface,
    val firestore       : FirebaseFirestoreDatabaseInterface,
    val storage         : FirebaseStorageInterface,
    val localDatabase   : RecipeModelRepository,
    val preferences     : Preferences
) : ViewModel() {

    private val _mainState = MutableStateFlow(MainState())
    val mainState : StateFlow<MainState> = _mainState.asStateFlow()

    private val _addingState = MutableStateFlow(AddingState())
    val addingState : StateFlow<AddingState> = _addingState.asStateFlow()

    private val _generatingState = MutableStateFlow(GeneratingState())
    val generatingState : StateFlow<GeneratingState> = _generatingState.asStateFlow()

    /**
     * Call this method to open
     * an external link from the
     * activity
     * */
    var openLink : (String) -> Unit = {}

    /**
     * internal reference to this code,
     * could be different if connected form an
     * external user
     * */
    private var code = preferences.getString("code")

    /**
     * Database reference to the real time database of firebase
     * */
    private var databaseReference : DatabaseReference? = null

    /**
     * days in a week
     * */
    private val days : List<String> = ViewModelUtility.getWeek()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            localDatabase.all().collect { recipes ->
                val dates : MutableList<RecipeDayModel> = mutableListOf()
                recipes.forEach { recipe ->
                    dates.add( RecipeDayModel(
                        date = if(dates.size < days.size) days[dates.size] else "",
                        recipeModel = recipe ))
                }
                _mainState.update {
                    it.copy(
                        recipes     = dates,
                        actualDate  = ViewModelUtility.getActualDate(),
                        state       = if(dates.isEmpty()) MainStatus.EMPTY else MainStatus.INITIALIZED,
                    )
                }
            }
        }
    }

    fun observeWeek() {
        // update internal references
        code = preferences.getString("code")
        _mainState.update { it.copy( personalCode = code) }

        // Get the recipes from the real time database
        databaseReference = database.getValues(RECIPE_MODULE, listOf(code))
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.children.count() < 5) {
                    _mainState.update { it.copy(state = MainStatus.EMPTY) }
                    return
                }

                var count = 0
                for (child in dataSnapshot.children) {
                    var recipe = child.getValue(RecipeModel::class.java)
                    if(recipe?.link?.isEmpty() == true) {
                        recipe = recipe.copy(
                            link = "https://www.google.com/search?q=" + Uri.encode(recipe.main))}
                    localDatabase.insert(recipe?.copy(id = count) ?: RecipeModel())
                    count ++
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun onReceive(userIntent : UserIntent) = viewModelScope.launch(Dispatchers.IO) {
        when(userIntent) {
            is UserIntent.OnImageSelected -> {
                uploadImage(userIntent.uri, userIntent.image)
            }
            is UserIntent.OnRecipeSaved -> {
                add(recipe = userIntent.recipe)
            }
            is UserIntent.OnClearNewRecipe -> {
                clearAddingRecipe()
            }
            is UserIntent.OnGenerateNewWeek -> {
                generate()
            }
            is UserIntent.OnOpenLinkRecipe -> {
                openLink(userIntent.link)
            }
            is UserIntent.OnUpdatingNewCode -> {
                updatingWithANewCode(userIntent.code)
            }
        }
    }

    private fun updatingWithANewCode(code : String) {
        // Update new internal code
        preferences.putString(code, "code")

        // Delete all old references
        localDatabase.deleteAll()

        // Starting to observe the new home!
        observeWeek()
    }

    private fun uploadImage(uri : Uri, image : ImageBitmap) {
        _addingState.update { it.copy(
            uploading = false,
            uri = uri,
            recipe = it.recipe.copy(
                image = image
            )
        )}
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
                "link" to recipe.link,
                "seasons" to recipe.seasons,
                "urlImage" to recipe.url,
                "needASide" to recipe.needASide,
                "idImage" to recipe.idImage,
            )).addOnCompleteListener { response ->
                if(response.isSuccessful) {
                    clearAddingRecipe()
                } else {
                    _addingState.update { state -> state.copy(
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

            val randomSides = (0..<sizeSides).shuffled().take(minOf(sizeSides, days.size.toLong()).toInt())
            val randomMain = (0..<sizeMain).shuffled().take(minOf(sizeMain, days.size.toLong()).toInt())

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
                    "idImage" to (main["idImage"] ?: 0),
                    "link" to (main["link"]?.toString() ?: "")
                )

                side?.let {
                    hash.put("side", (it["title"]?.toString() ?: ""))
                    hash.put("sideIngredients", (it["ingredients"]?.toString() ?: ""))
                }

                database.putValues(RECIPE_MODULE, listOf(code), index, hash)
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