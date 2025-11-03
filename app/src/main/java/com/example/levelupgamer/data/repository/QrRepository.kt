package com.example.levelupgamer.data.repository

import com.example.levelupgamer.data.model.QrResult

class QrRepository {

    fun processQrContent(content: String): QrResult {
        // Aquí podrías guardar o procesar el QR en BD / API, etc.
        return QrResult(content)
    }
}
