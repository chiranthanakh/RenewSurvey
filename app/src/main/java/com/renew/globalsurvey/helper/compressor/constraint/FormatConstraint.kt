package com.renew.globalsurvey.helper.compressor.constraint

import android.graphics.Bitmap
import com.renew.globalsurvey.helper.compressor.compressFormat
import com.renew.globalsurvey.helper.compressor.loadBitmap
import com.renew.globalsurvey.helper.compressor.overWrite
import java.io.File

/**
 * Created on : January 24, 2020
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
class FormatConstraint(private val format: Bitmap.CompressFormat) : Constraint {

    override fun isSatisfied(imageFile: File): Boolean {
        return format == imageFile.compressFormat()
    }

    override fun satisfy(imageFile: File): File {
        return overWrite(imageFile, loadBitmap(imageFile), format)
    }
}

fun Compression.format(format: Bitmap.CompressFormat) {
    constraint(FormatConstraint(format))
}