package com.carrozzino.dishdash.data.repository

import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
import com.carrozzino.dishdash.ui.viewModels.Recipe
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseFirestoreRepository @Inject constructor(
    val firestore : FirebaseFirestoreDatabaseInterface
) {

    private val mapMains : HashMap<Int, DocumentSnapshot> = hashMapOf()
    private val mapSides : HashMap<Int, DocumentSnapshot> = hashMapOf()

    private var sizeSides = -1L
    private var sizeMains = -1L

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


        map[indexToUpdate.toString()] = getRecipe(randomMainIndex.toLong())
        map
    }

    suspend fun generate(daysSize : Int) : Map<String, Any> = withContext(Dispatchers.IO) {

        val map = mutableMapOf<String, Any>()
        initSizes()

        if(sizeMains == 0L || sizeSides == 0L) map

        val randomSides = (0..<sizeSides).shuffled().take(minOf(sizeSides, daysSize.toLong()).toInt())
        val randomMain = (0..<sizeMains).shuffled().take(minOf(sizeMains, daysSize.toLong()).toInt())

        for(index in 0..<minOf(randomSides.size, randomMain.size)) {
            map[index.toString()] = getRecipe(randomMain[index])
        }
        map
    }

    suspend fun getRecipe(randomIndexMain : Long) : Map<String, Any> {
        if(!mapMains.containsKey(randomIndexMain.toInt())) {
            val document = suspendCoroutine<DocumentSnapshot> { block ->
                firestore.get("total_recipes", randomIndexMain.toString()).addOnCompleteListener{ result ->
                    block.resume(result.result)
                }
            }
            mapMains[randomIndexMain.toInt()] = document
        }

        if(mapMains[randomIndexMain.toInt()]?.get("needASide")?.toString() == "true" && !mapSides.containsKey(randomIndexMain.toInt())) {
            val document = suspendCoroutine<DocumentSnapshot> { block ->
                firestore.get("total_side_recipes", randomIndexMain.toString()).addOnCompleteListener{ result ->
                    block.resume(result.result)
                }
            }
            mapSides[randomIndexMain.toInt()] = document
        }

        val hash = hashMapOf<String, Any>(
            "main"              to (mapMains[randomIndexMain.toInt()]?.get("title")?.toString() ?: ""),
            "mainIngredients"   to (mapMains[randomIndexMain.toInt()]?.get("ingredients")?.toString() ?: ""),
            "urlImage"          to (mapMains[randomIndexMain.toInt()]?.get("urlImage")?.toString() ?: ""),
            "idImage"           to (mapMains[randomIndexMain.toInt()]?.get("idImage") ?: 0),
            "link"              to (mapMains[randomIndexMain.toInt()]?.get("link")?.toString() ?: ""),
            "serverId"          to randomIndexMain.toInt()
        )

        mapSides.get(randomIndexMain.toInt())?.let {
            hash.put("side", (it["title"]?.toString() ?: ""))
            hash.put("sideIngredients", (it["ingredients"]?.toString() ?: ""))
        }

        return hash
    }

    suspend fun add(recipe : Recipe) : Task<Void?>? = withContext(Dispatchers.IO) {

        if(recipe.isSide)
            sizeSides = getOrKeep(sizeSides) { firestore.size("total_side_recipes", it) }
        else
            sizeMains = getOrKeep(sizeMains) { firestore.size("total_recipes", it) }


        firestore.put(if(recipe.isSide) "total_side_recipes" else "total_recipes",
            document    = (if(recipe.isSide) sizeSides else sizeMains).toString(),
            values      = hashMapOf<String, Any>(
                "title" to recipe.title,
                "ingredients" to recipe.ingredients,
                "link" to recipe.link,
                "seasons" to recipe.seasons,
                "urlImage" to recipe.url,
                "needASide" to recipe.needASide,
                "idImage" to recipe.idImage,
            ))
    }
}