package com.atri.seduley.data.ml

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.graphics.get
import androidx.core.graphics.scale
import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.util.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject


class TFLiteCaptchaRecognizer @Inject constructor(
    @ApplicationContext private val context: Context
) : CaptchaRecognizer {

    private val interpreter: Interpreter by lazy {
        loadInterpreter()
    }

    companion object {
        private const val NUM_OUTPUTS = 4
        private const val NUM_CLASSES = 36
        private const val WIDTH = 80
        private const val HEIGHT = 40
        private const val MODEL_PATH = "model/captcha.tflite"
    }

    /** 加载模型（lazy 初始化） */
    private fun loadInterpreter(): Interpreter {
        return try {
            val afd = context.assets.openFd(MODEL_PATH)
            val modelBytes = afd.createInputStream().readBytes()

            val buffer = ByteBuffer.allocateDirect(modelBytes.size).apply {
                order(ByteOrder.nativeOrder())
                put(modelBytes)
                rewind()
            }

            AppLogger.d("TFLite 模型加载成功")
            Interpreter(buffer)

        } catch (e: Exception) {
            AppLogger.e("初始化 TFLite 解释器失败", e)
            throw BaseException("无法初始化 TFLite 模型: $e")
        }
    }

    override suspend fun recognize(imageBytes: ByteArray): String {
        val inputBuffer = preprocessImage(imageBytes)
        val probabilityOutputs = Array(NUM_OUTPUTS) {
            FloatArray(NUM_CLASSES)
        }
        runInference(inputBuffer, probabilityOutputs)
        return decodeOutput(probabilityOutputs)
    }

    private fun runInference(input: ByteBuffer, outputsArray: Array<FloatArray>) {
        val outputsMap = mutableMapOf<Int, Any>()
        for (i in 0 until NUM_OUTPUTS) {
            outputsMap[i] = arrayOf(outputsArray[i])
        }

        try {
            interpreter.runForMultipleInputsOutputs(arrayOf(input), outputsMap)
        } catch (e: Exception) {
            throw BaseException("TFLite 推理错误: $e")
        }
    }

    /** 将 ByteArray 图片预处理为 ByteBuffer 输入 TFLite */
    private fun preprocessImage(imageBytes: ByteArray): ByteBuffer {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: throw IllegalArgumentException("无法解码图片")
        val scaled = bitmap.scale(WIDTH, HEIGHT)

        val buffer = ByteBuffer.allocateDirect(4 * WIDTH * HEIGHT).apply {
            order(ByteOrder.nativeOrder())
        }

        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                val pixel = scaled[x, y]
                val gray = (0.299 * Color.red(pixel) +
                        0.587 * Color.green(pixel) +
                        0.114 * Color.blue(pixel)) / 255.0
                buffer.putFloat(((gray - 0.5) / 0.5).toFloat())
            }
        }
        buffer.rewind()
        return buffer
    }

    /**
     * 根据多个输出概率数组返回预测的验证码字符串。
     * outputsArray 是一个包含4个 FloatArray 的数组，每个 FloatArray 是一个字符的概率分布。
     */
    private fun decodeOutput(outputsArray: Array<FloatArray>): String {
        val chars = "0123456789abcdefghijklmnopqrstuvwxyz"

        val arr = outputsArray.map { probabilityArray ->
            // probabilityArray 是一个 FloatArray，代表一个字符的36个概率
            val index = probabilityArray.indices.maxByOrNull { probabilityArray[it] } ?: 0
            chars[index].toString()
        }

        // 手动调整顺序
        if (arr.size == NUM_OUTPUTS) {
            return arr[1] + arr[2] + arr[0] + arr[3]
        }

        // 如果不是4位，按原始顺序返回
        AppLogger.w("解码的数组大小不是 ${NUM_OUTPUTS}, 按原始顺序返回")
        return arr.joinToString("")
    }
}