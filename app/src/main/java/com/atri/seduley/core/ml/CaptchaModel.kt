package com.atri.seduley.core.ml

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.graphics.get
import androidx.core.graphics.scale
import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.core.exception.BaseException
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 验证码识别模型
 */
object CaptchaModel {

    private lateinit var interpreter: Interpreter
    const val NUM_OUTPUTS = 4 // 4 个输出
    const val NUM_CLASSES = 36 // 每个输出 36 个类别

    /** 初始化模型 */
    fun init(context: Context) {
        try {
            val assetFileDescriptor = context.assets.openFd("model/captcha.tflite")
            val inputStream = assetFileDescriptor.createInputStream()
            val model = inputStream.readBytes()
            inputStream.close()

            val byteBuffer = ByteBuffer.allocateDirect(model.size).apply {
                order(ByteOrder.nativeOrder())
                put(model)
                rewind()
            }
            interpreter = Interpreter(byteBuffer)
            AppLogger.d("解释器成功初始化")
        } catch (e: Exception) {
            AppLogger.e("无法初始化Tflite解释器", e)
        }
    }

    /** 将 ByteArray 图片预处理为 ByteBuffer 输入 TFLite */
    fun preprocessImage(imageBytes: ByteArray, width: Int, height: Int): ByteBuffer {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: throw IllegalArgumentException("无法解码图片")
        val scaled = bitmap.scale(width, height)

        val buffer = ByteBuffer.allocateDirect(4 * width * height).apply {
            order(ByteOrder.nativeOrder())
        }

        for (y in 0 until height) {
            for (x in 0 until width) {
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
     * 执行推理，填充提供的多个输出缓冲区。
     * outputsArray 是一个包含4个 FloatArray 的数组，每个 FloatArray 用于接收一个字符的概率。
     * 每个 FloatArray 的大小应为 NUM_CLASSES (36)。
     */
    fun recognize(input: ByteBuffer, outputsArray: Array<FloatArray>) {
        if (!::interpreter.isInitialized) {
            throw IllegalStateException("解释器未初始化")
        }
        if (outputsArray.size != NUM_OUTPUTS) {
            throw IllegalArgumentException("outputsArray 必须具有 $NUM_OUTPUTS 个元素")
        }
        outputsArray.forEach {
            if (it.size != NUM_CLASSES) {
                throw IllegalArgumentException("outputsArray 中的每个 floatArray 都必须具有 $NUM_CLASSES 个类别")
            }
        }
        val outputsMap = mutableMapOf<Int, Any>()
        for (i in 0 until NUM_OUTPUTS) {
            outputsMap[i] = arrayOf(outputsArray[i])
        }

        val inputs = arrayOf<Any>(input)

        try {
            interpreter.runForMultipleInputsOutputs(inputs, outputsMap)
        } catch (e: Exception) {
            throw BaseException("Tflite推断期间的错误: $e")
        }
    }

    /**
     * 根据多个输出概率数组返回预测的验证码字符串。
     * outputsArray 是一个包含4个 FloatArray 的数组，每个 FloatArray 是一个字符的概率分布。
     */
    fun decodeOutput(outputsArray: Array<FloatArray>): String {
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
        AppLogger.w("解码的数组大小不是 $NUM_OUTPUTS, 按原始顺序返回")
        return arr.joinToString("")
    }
}