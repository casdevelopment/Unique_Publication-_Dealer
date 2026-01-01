package com.publication.dealer.PDF_Upload.model

data class PDFUploadRequest(
    val adminUserID: String,
    val fileName: String,
    val fileBase64: String
)
