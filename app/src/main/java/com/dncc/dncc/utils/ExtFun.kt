package com.dncc.dncc.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Uri.getRealPathFromURI(context: Context): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? =
        this.let { context.contentResolver.query(it, projection, null, null, null) }
    val columnIndex: Int? = cursor
        ?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor?.moveToFirst()
    return columnIndex?.let { cursor.getString(it) }
}

fun Exception?.checkFirebaseError(): String {
    return try {
        throw this!!
    } catch (e: FirebaseAuthWeakPasswordException) {
        "Password terlalu sederhana"
    } catch (e: FirebaseAuthInvalidUserException) {
        "Akun tidak ditemukan"
    } catch (e: FirebaseAuthMultiFactorException) {
        "Salah memasukan passsword"
    } catch (e: NullPointerException) {
        "Gagal, mohon coba lagi"
    } catch (e: Exception) {
        "Gagal, mohon coba lagi"
    }
}