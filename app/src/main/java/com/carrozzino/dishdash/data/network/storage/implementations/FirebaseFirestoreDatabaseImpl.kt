package com.carrozzino.dishdash.data.network.storage.implementations

import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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

    override fun getItems(collection: String, limit: Long, offset: Int, where : String) : Task<QuerySnapshot?> {
        val request = database.collection(collection)

        if(where.isNotEmpty()) {
            request
                .orderBy("title", Query.Direction.ASCENDING)
                .whereGreaterThanOrEqualTo("title", where)
                .whereLessThan("title", where + "\uf8ff")
        }
        else if (offset > 0) {
            request
                .orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
                .startAfter(offset.toString())
                .limit(limit)
        }
        return request.get()
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
                    result(0)
                }
            }
    }
}