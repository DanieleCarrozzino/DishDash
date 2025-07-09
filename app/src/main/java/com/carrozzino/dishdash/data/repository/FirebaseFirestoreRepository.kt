package com.carrozzino.dishdash.data.repository

import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
import com.carrozzino.dishdash.ui.viewModels.MainState
import com.carrozzino.dishdash.ui.viewModels.Recipe
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class FirestoreRepositoryState(
    val listRecipes : List<Recipe> = listOf()
)

class FirebaseFirestoreRepository @Inject constructor(
    val firestore : FirebaseFirestoreDatabaseInterface
) {

    private val mapMains : HashMap<Int, Recipe> = hashMapOf()
    private val mapSides : HashMap<Int, Recipe> = hashMapOf()

    private var sizeSides = -1L
    private var sizeMains = -1L

    private val _state = MutableStateFlow(FirestoreRepositoryState())
    val state : StateFlow<FirestoreRepositoryState> = _state.asStateFlow()

    private suspend fun getOrKeep(current: Long, loader: (callback: (Long) -> Unit) -> Unit): Long {
        return if (current > 0) current else suspendCoroutine { cont ->
            loader { result -> cont.resume(result) }
        }
    }

    private suspend fun initSizes() {
        sizeSides = getOrKeep(sizeSides) { firestore.size("total_side_recipes", it) }
        sizeMains = getOrKeep(sizeMains) { firestore.size("total_recipes", it) }
    }

    suspend fun generateJustOne(idsToAvoid : List<Int>, indexToUpdate : Int) : Map<String, Any> = withContext(Dispatchers.IO) {

        val map = mutableMapOf<String, Any>()
        initSizes()

        if(sizeMains == 0L || sizeSides == 0L) map

        var randomMainIndex = (0..<sizeMains).shuffled().take(1)[0].toInt()
        while(idsToAvoid.contains(randomMainIndex))
            randomMainIndex = (0..<sizeMains).shuffled().take(1)[0].toInt()
        val randomSideIndex = (0..<sizeSides).shuffled().take(1)[0].toInt()

        map[indexToUpdate.toString()] = getRecipe(randomMainIndex.toLong(), randomSideIndex.toLong())
        map
    }

    suspend fun generate(daysSize : Int) : Map<String, Any> = withContext(Dispatchers.IO) {

        val map = mutableMapOf<String, Any>()
        initSizes()

        if(sizeMains == 0L || sizeSides == 0L) map

        val randomSides = (0..<sizeSides).shuffled().take(minOf(sizeSides, daysSize.toLong()).toInt())
        val randomMain = (0..<sizeMains).shuffled().take(minOf(sizeMains, daysSize.toLong()).toInt())

        for(index in 0..<minOf(randomSides.size, randomMain.size)) {
            map[index.toString()] = getRecipe(randomMain[index], randomSides[minOf(index, (randomSides.size - 1))])
        }
        map
    }

    suspend fun getRecipe(indexMain : Long, indexSide : Long) : Map<String, Any> {
        if(!mapMains.containsKey(indexMain.toInt())) {
            val document = suspendCoroutine<DocumentSnapshot> { block ->
                firestore.get("total_recipes", indexMain.toString()).addOnCompleteListener{ result ->
                    block.resume(result.result)
                }
            }
            mapMains[indexMain.toInt()] = document.toObject<Recipe>(Recipe::class.java) ?: Recipe()
        }

        if(mapMains[indexMain.toInt()]?.needASide == true && !mapSides.containsKey(indexSide.toInt())) {
            val document = suspendCoroutine<DocumentSnapshot> { block ->
                firestore.get("total_side_recipes", indexSide.toString()).addOnCompleteListener{ result ->
                    block.resume(result.result)
                }
            }
            mapSides[indexSide.toInt()] = document.toObject<Recipe>(Recipe::class.java) ?: Recipe()
        }

        val hash = hashMapOf<String, Any>(
            "main"              to (mapMains[indexMain.toInt()]?.title ?: ""),
            "mainIngredients"   to (mapMains[indexMain.toInt()]?.ingredients ?: ""),
            "urlImage"          to (mapMains[indexMain.toInt()]?.urlImage ?: ""),
            "idImage"           to (mapMains[indexMain.toInt()]?.idImage ?: 0),
            "link"              to (mapMains[indexMain.toInt()]?.link ?: ""),
            "isVegetarian"      to (mapMains[indexMain.toInt()]?.isVegetarian ?: false),
            "serverId"          to indexMain.toInt()
        )

        mapSides.get(indexSide.toInt())?.let {
            hash.put("side", (it.title))
            hash.put("sideIngredients", (it.ingredients))
        }

        return hash
    }

    fun getRecipes(limit : Long, offset : Int, where : String = "") {
        if(where.isEmpty() && offset <= 0 && state.value.listRecipes.isNotEmpty()) {
            return
        }

        firestore.getItems(
            collection  = "total_recipes",
            limit       = limit,
            offset      = offset,
            where       = where
        ).addOnCompleteListener { query ->
            if(!query.isSuccessful) return@addOnCompleteListener

            val list = mutableListOf<Recipe>()
            query.result?.documents?.forEach {
                var recipe  = it.toObject(Recipe::class.java)
                recipe      = recipe?.copy(serverId = it.id.toInt())

                mapMains[it.id.toInt()] = recipe ?: Recipe()
                list.add(recipe ?: Recipe())
            }

            _state.update { current -> current.copy(listRecipes = current.listRecipes + list) }
        }
    }

    suspend fun add(recipe : Recipe) : Task<Void?>? = withContext(Dispatchers.IO) {

        if(recipe.isSide)
            sizeSides = getOrKeep(sizeSides) { firestore.size("total_side_recipes", it) }
        else
            sizeMains = getOrKeep(sizeMains) { firestore.size("total_recipes", it) }


        firestore.put(if(recipe.isSide) "total_side_recipes" else "total_recipes",
            document    = (if(recipe.isSide) sizeSides else sizeMains).toString(),
            values      = recipe.toHashMap()
        )
    }
}