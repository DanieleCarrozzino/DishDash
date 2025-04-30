package com.carrozzino.dishdash.data.network.storage.interfaces

import com.google.firebase.database.DatabaseReference

interface FirebaseRealtimeDatabaseInterface {

    fun putValues(module: String, children: List<String>, index : Int, node: HashMap<String, Any>)

    fun getValues(module : String, children : List<String>) : DatabaseReference

}