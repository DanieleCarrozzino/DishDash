package com.carrozzino.dishdash.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.room.Room
import com.carrozzino.dishdash.data.internal.Preferences
import com.carrozzino.dishdash.data.network.authentication.FirebaseAuthenticationImpl
import com.carrozzino.dishdash.data.network.authentication.FirebaseAuthenticationInterface
import com.carrozzino.dishdash.data.network.storage.implementations.FirebaseFirestoreDatabaseImpl
import com.carrozzino.dishdash.data.network.storage.implementations.FirebaseRealtimeDatabaseImpl
import com.carrozzino.dishdash.data.network.storage.implementations.FirebaseStorageImpl
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseFirestoreDatabaseInterface
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseRealtimeDatabaseInterface
import com.carrozzino.dishdash.data.network.storage.interfaces.FirebaseStorageInterface
import com.carrozzino.dishdash.data.repository.database.RecipeModelDatabase
import com.carrozzino.dishdash.data.repository.database.RecipeModelRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DIModule {

    @Provides
    @Singleton
    fun provideInternalShared(
        @ApplicationContext context: Context
    ): Preferences {
        return Preferences(context.getSharedPreferences(Preferences.TAG, Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthenticator(
        @ApplicationContext context: Context,
        credentialManager: CredentialManager
    ): FirebaseAuthenticationInterface {
        return FirebaseAuthenticationImpl(
            context = context,
            firebaseAuthentication = FirebaseAuth.getInstance(),
            credentialManager = credentialManager
        )
    }

    @Singleton
    @Provides
    fun providesCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }

    @Singleton
    @Provides
    fun providesFirebaseRealtimeDatabase(): FirebaseRealtimeDatabaseInterface {
        return FirebaseRealtimeDatabaseImpl()
    }

    @Singleton
    @Provides
    fun providesFirebaseFirestoreDatabase(): FirebaseFirestoreDatabaseInterface {
        return FirebaseFirestoreDatabaseImpl()
    }

    @Singleton
    @Provides
    fun providesFirebaseStorage(): FirebaseStorageInterface {
        return FirebaseStorageImpl()
    }

    @Provides
    @Singleton
    fun provideRecipeModelDatabase(
        @ApplicationContext context : Context
    ) : RecipeModelDatabase {
        return Room.databaseBuilder(context, RecipeModelDatabase::class.java, "recipes_database")
            .fallbackToDestructiveMigration(true)
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeModelRepository(
        recipeModelDatabase: RecipeModelDatabase
    ) : RecipeModelRepository {
        return RecipeModelRepository(recipeModelDatabase)
    }

}