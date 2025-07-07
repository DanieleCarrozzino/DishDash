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

    suspend fun generate(daysSize : Int) : Map<String, Any> = withContext(Dispatchers.IO) {

        val map = mutableMapOf<String, Any>()

        sizeSides = getOrKeep(sizeSides) { firestore.size("total_side_recipes", it) }
        sizeMains = getOrKeep(sizeMains) { firestore.size("total_recipes", it) }

        if(sizeMains == 0L || sizeSides == 0L) map

        // Get
        val randomSides = (0..<sizeSides).shuffled().take(minOf(sizeSides, daysSize.toLong()).toInt())
        val randomMain = (0..<sizeMains).shuffled().take(minOf(sizeMains, daysSize.toLong()).toInt())

        for(index in 0..<minOf(randomSides.size, randomMain.size)) {

            if(!mapMains.containsKey(index)) {
                val document = suspendCoroutine<DocumentSnapshot> { block ->
                    firestore.get("total_recipes", randomMain[index].toString()).addOnCompleteListener{ result ->
                        block.resume(result.result)
                    }
                }
                mapMains[index] = document
            }

            if(mapMains[index]?.get("needASide")?.toString() == "true" && !mapSides.containsKey(index)) {
                val document = suspendCoroutine<DocumentSnapshot> { block ->
                    firestore.get("total_side_recipes", randomMain[index].toString()).addOnCompleteListener{ result ->
                        block.resume(result.result)
                    }
                }
                mapSides[index] = document
            }

            val hash = hashMapOf<String, Any>(
                "main" to (mapMains[index]?.get("title")?.toString() ?: ""),
                "mainIngredients" to (mapMains[index]?.get("ingredients")?.toString() ?: ""),
                "urlImage" to (mapMains[index]?.get("urlImage")?.toString() ?: ""),
                "idImage" to (mapMains[index]?.get("idImage") ?: 0),
                "link" to (mapMains[index]?.get("link")?.toString() ?: "")
            )

            mapSides.get(index)?.let {
                hash.put("side", (it["title"]?.toString() ?: ""))
                hash.put("sideIngredients", (it["ingredients"]?.toString() ?: ""))
            }

            map[index.toString()] = hash
        }
        map
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