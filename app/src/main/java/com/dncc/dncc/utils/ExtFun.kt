package com.dncc.dncc.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

fun Uri.getRealPathFromURI(context: Context): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = this.let { context.contentResolver.query(it, projection, null, null, null) }
    val columnIndex: Int? = cursor
        ?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor?.moveToFirst()
    return columnIndex?.let { cursor.getString(it) }
}