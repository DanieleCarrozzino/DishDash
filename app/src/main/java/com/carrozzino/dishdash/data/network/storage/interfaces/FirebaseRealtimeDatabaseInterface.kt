package com.carrozzino.dishdash.data.network.storage.interfaces

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference

interface FirebaseRealtimeDatabaseInterface {

    fun putValue(module: String, children: List<String>, index : Int, node: HashMap<String, Any>) : Task<Void>

    fun putValues(module: String, children: List<String>, nodes: Map<String, Any>) : Task<Void>

    fun getValues(module : String, children : List<String>) : DatabaseReference

}