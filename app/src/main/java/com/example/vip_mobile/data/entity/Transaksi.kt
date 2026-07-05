package com.example.vip_mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "transaksi")
data class Transaksi(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id_transaksi")
        val idTransaksi: Int = 0,
        @ColumnInfo(name = "no_rekening") val noRekening: String,
        @ColumnInfo(name = "jenis") val jenis: String, // SETORAN, PENARIKAN, TRANSFER
        @ColumnInfo(name = "nominal") val nominal: Double,
        @ColumnInfo(name = "tgl_transaksi") val tglTransaksi: Long = System.currentTimeMillis()
)