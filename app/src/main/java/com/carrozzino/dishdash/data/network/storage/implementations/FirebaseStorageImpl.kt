package com.carrozzino.dishdash.data.network.storage.implementations

import android.net.Uri
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseStorageInterface
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class FirebaseStorageImpl : FirebaseStorageInterface {
    val storage = FirebaseStorage.getInstance()

    override fun upload(imageUri: Uri) : UploadTask {
        val storageRef : StorageReference = storage.reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
        return imageRef.putFile(imageUri)
    }
}