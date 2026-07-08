package com.example.vip_mobile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NasabahSupabase(
    val id: String? = null, // UUID dari auth.users
    val nama: String,
    val telepon: String,
    val alamat: String = "-",
    val status: String = "AKTIF",
    val created_at: String? = null
)

@Serializable
data class RekeningSupabase(
    val no_rekening: String,
    val id_user: String, // UUID dari auth.users
    val saldo: Double = 0.0,
    val status: String = "AKTIF",
    val created_at: String? = null
)

@Serializable
data class TransaksiSupabase(
    val id_transaksi: Long? = null,
    val no_rekening: String,
    val jenis: String, // 'SETORAN', 'PENARIKAN', 'TRANSFER'
    val nominal: Double,
    val keterangan: String = "-",
    val tgl_transaksi: String? = null
)
