package com.example.vip_mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "nasabah")
data class Nasabah(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_nasabah")
    val idNasabah: Int = 0,
    @ColumnInfo(name = "nama") val nama: String,
    @ColumnInfo(name = "alamat") val alamat: String,
    @ColumnInfo(name = "ktp") val ktp: String,
    @ColumnInfo(name = "telepon") val telepon: String,
    @ColumnInfo(name = "status") val status: String = "AKTIF"
)