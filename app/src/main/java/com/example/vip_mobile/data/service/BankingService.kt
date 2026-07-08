package com.example.vip_mobile.data.service

import com.example.vip_mobile.data.model.RekeningSupabase
import com.example.vip_mobile.data.model.TransaksiSupabase
import com.example.vip_mobile.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BankingService {

    suspend fun getRekeningByUserId(userId: String): List<RekeningSupabase> = withContext(Dispatchers.IO) {
        return@withContext SupabaseClient.client.postgrest["rekening"]
            .select {
                filter {
                    eq("id_user", userId)
                }
            }.decodeList<RekeningSupabase>()
    }

    suspend fun transferVIP(
        fromNoRek: String,
        toNoRek: String,
        nominal: Double,
        keterangan: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // 1. Ambil data rekening pengirim
            val pengirim = SupabaseClient.client.postgrest["rekening"]
                .select { filter { eq("no_rekening", fromNoRek) } }
                .decodeSingle<RekeningSupabase>()

            if (pengirim.saldo < nominal) return@withContext false

            // 2. Ambil data rekening penerima
            val penerima = SupabaseClient.client.postgrest["rekening"]
                .select { filter { eq("no_rekening", toNoRek) } }
                .decodeSingleOrNull<RekeningSupabase>() ?: return@withContext false

            // 3. Update saldo pengirim
            SupabaseClient.client.postgrest["rekening"].update(
                mapOf("saldo" to pengirim.saldo - nominal)
            ) { filter { eq("no_rekening", fromNoRek) } }

            // 4. Update saldo penerima
            SupabaseClient.client.postgrest["rekening"].update(
                mapOf("saldo" to penerima.saldo + nominal)
            ) { filter { eq("no_rekening", toNoRek) } }

            // 5. Catat Transaksi
            val trx = TransaksiSupabase(
                no_rekening = fromNoRek,
                jenis = "TRANSFER",
                nominal = nominal,
                keterangan = "Transfer ke $toNoRek: $keterangan"
            )
            SupabaseClient.client.postgrest["transaksi"].insert(trx)

            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
