package com.carrozzino.dishdash.data.network.storage.implementations

import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseRealtimeDatabaseInterface
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseRealtimeDatabaseImpl : FirebaseRealtimeDatabaseInterface {

    val database = FirebaseDatabase
        .getInstance("https://dish-dash-6800c-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun putValues(module: String, children: List<String>, index : Int, node: HashMap<String, Any>) {
        val ref = getReference(module, children).child(index.toString())
        ref.setValue(node)
    }

    override fun getValues(module: String, children: List<String>
    ): DatabaseReference {
        return getReference(module, children)
    }

    private fun getReference(module : String, children : List<String>) : DatabaseReference {
        var ref = database.getReference(module)
        for(child in children){ ref = ref.child(child) }
        return ref
    }
}