package com.example.vip_mobile.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.gotrue.Auth

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://daxsvienfrfpycrkluol.supabase.co",
        supabaseKey = "sb_publishable_olIQRnqcyWuS796O1QQw5Q_cXpKZ7uY"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
