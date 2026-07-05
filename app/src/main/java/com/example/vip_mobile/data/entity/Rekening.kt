package com.example.vip_mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "rekening")
data class Rekening(
    @PrimaryKey
    @ColumnInfo(name = "no_rekening")
    val noRekening: String,
    @ColumnInfo(name = "id_nasabah") val idNasabah: Int,
    @ColumnInfo(name = "saldo") val saldo: Double = 0.0,
    @ColumnInfo(name = "status") val status: String = "AKTIF"
)