package com.carrozzino.dishdash.data.repository

import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
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

    /**
     * Get or keep
     *
     * if the current value is been already initialized
     * I decided to skip it also if someone could have added some
     * recipes in the meantime
     * */
    private suspend fun getOrKeep(current: Long, loader: (callback: (Long) -> Unit) -> Unit): Long {
        return if (current > 0) current else suspendCoroutine { cont ->
            loader { result -> cont.resume(result) }
        }
    }

    /**
     * Init sizes
     *
     * init the size of the list of the recipes, mains and sides
     * */
    private suspend fun initSizes() {
        sizeSides = getOrKeep(sizeSides) { firestore.size("total_side_recipes", it) }
        sizeMains = getOrKeep(sizeMains) { firestore.size("total_recipes", it) }
    }

    /**
     * Generate just one
     *
     * @param idsToAvoid ids that are already inside the main list
     * @param indexToUpdate index of the actual recipe to update 0..6 as the days in a week
     * Generate just one recipe in a specific position defined by indexToUpdate
     * */
    suspend fun generateJustOne(idsToAvoid : List<Int>, indexToUpdate : Int) : Map<String, Any> = withContext(Dispatchers.IO) {

        val map = mutableMapOf<String, Any>()
        initSizes()

        if(sizeMains == 0L || sizeSides == 0L) return@withContext map

        var randomMainIndex = (0..<sizeMains).shuffled().take(1)[0].toInt()
        while(idsToAvoid.contains(randomMainIndex))
            randomMainIndex = (0..<sizeMains).shuffled().take(1)[0].toInt()
        val randomSideIndex = (0..<sizeSides).shuffled().take(1)[0].toInt()

        map[indexToUpdate.toString()] = getRecipe(randomMainIndex.toLong(), randomSideIndex.toLong())
        return@withContext map
    }

    /**
     * Generate
     *
     * @param daysSize num of days to generate
     * Generate the entire week
     * */
    suspend fun generate(daysSize : Int) : Map<String, Any> = withContext(Dispatchers.IO) {

        val map = mutableMapOf<String, Any>()
        initSizes()

        if(sizeMains == 0L || sizeSides == 0L) return@withContext map

        val randomSides = (0..<sizeSides).shuffled().take(minOf(sizeSides, daysSize.toLong()).toInt())
        val randomMain = (0..<sizeMains).shuffled().take(minOf(sizeMains, daysSize.toLong()).toInt())

        for(index in 0..<minOf(randomSides.size, randomMain.size)) {
            map[index.toString()] = getRecipe(randomMain[index], randomSides[minOf(index, (randomSides.size - 1))])
        }
        return@withContext map
    }

    /**
     * Get Recipe
     *
     * @param indexMain index of the main plate
     * @param indexSide index of the side plate if requested by the main
     * get single recipe from indexes
     * */
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

    /**
     * Get recipes
     *
     * @param limit limit number rof recipes
     * @param offset starting offset
     * @param where starting string to filter the list of recipes
     * get full list of recipes with limit, offset and where
     * */
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
                val recipeRaw  = it.toObject(Recipe::class.java)
                recipeRaw?.let { internal ->
                    val recipe = internal.copy(
                        //isVegetarian = true,
                        serverId = it.id.toInt())

//                    firestore.put("total_recipes",
//                        document    = it.id,
//                        values      = recipe.toHashMap()
//                    )

                    mapMains[it.id.toInt()] = recipe
                    list.add(recipe)
                }


            }

            _state.update { current -> current.copy(listRecipes = current.listRecipes + list) }
        }
    }

    /**
     * Add
     *
     * @param recipe recipe to add
     * add a single recipe
     * */
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