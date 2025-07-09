package com.carrozzino.dishdash.data.network.storage.interfaces

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

interface FirebaseFirestoreDatabaseInterface {

    fun put(collection : String, document : String, values : HashMap<String, Any>) : Task<Void?>

    fun get(collection : String, document : String) : Task<DocumentSnapshot>

    fun getItems(collection : String, limit : Long = 20L, offset : Int = 0, where : String = "") : Task<QuerySnapshot?>

    fun size(collection : String, result : (Long) -> Unit)

}