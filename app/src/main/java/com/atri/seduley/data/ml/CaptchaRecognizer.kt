package com.atri.seduley.data.ml

interface CaptchaRecognizer {

    /** 输入字节图，输出识别结果 */
    suspend fun recognize(imageBytes: ByteArray): String
}
