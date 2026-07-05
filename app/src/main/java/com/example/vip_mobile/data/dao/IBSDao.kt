package com.example.vip_mobile.data.dao

import androidx.room.*
import com.example.vip_mobile.data.entity.*

@Dao
interface IBSDao {
    @Query("SELECT * FROM nasabah WHERE id_nasabah = :id")
    suspend fun getNasabahById(id: Int): Nasabah?

    @Query("SELECT * FROM rekening WHERE no_rekening = :noRek")
    suspend fun getRekening(noRek: String): Rekening?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaksi(transaksi: Transaksi)

    @Update
    suspend fun updateSaldo(rekening: Rekening)
}