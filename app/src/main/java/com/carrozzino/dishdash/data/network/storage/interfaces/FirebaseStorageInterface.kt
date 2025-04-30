package com.carrozzino.dishdash.data.network.storage.interfaces

import android.net.Uri
import com.google.firebase.storage.UploadTask

interface FirebaseStorageInterface {

    fun upload(imageUri: Uri) : UploadTask

}