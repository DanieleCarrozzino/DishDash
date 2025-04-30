package com.carrozzino.dishdash.data.network.storage.implementations

import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseFirestoreDatabaseImpl : FirebaseFirestoreDatabaseInterface {

    val database = Firebase.firestore

    override fun put(collection: String, document: String, values: HashMap<String, Any>): Task<Void?> {
        return database.collection(collection).document(document).set(values)
    }

    override fun get(collection: String, document: String) : Task<DocumentSnapshot> {
        return database.collection(collection).document(document).get()
    }

    override fun size(collection: String, result : (Long) -> Unit) {
        database.collection(collection)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result(task.result.count)
                } else {
                    println(task.exception)
                    result(-1)
                }
            }
    }
}