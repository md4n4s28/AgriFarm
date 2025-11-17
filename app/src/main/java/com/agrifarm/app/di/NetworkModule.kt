package com.agrifarm.app.di

import com.agrifarm.app.data.api.GeminiApiService
import com.agrifarm.app.data.api.SensorApi
import com.agrifarm.app.data.database.SupabaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGeminiApiService(): GeminiApiService {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSensorApi(): SensorApi {
        return Retrofit.Builder()
            .baseUrl("https://eob86s60nw99fn5.m.pipedream.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SensorApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://your-project.supabase.co",
            supabaseKey = "your-anon-key"
        ) {
            install(Postgrest)
            install(Realtime)
        }
    }
    
    @Provides
    @Singleton
    fun provideSupabaseDatabase(supabaseClient: SupabaseClient): SupabaseDatabase {
        return SupabaseDatabase(supabaseClient)
    }
}
