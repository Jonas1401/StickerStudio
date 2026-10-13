package com.stickerstudio.app.media

/** Funções puras (testáveis na JVM) usadas pelo importador de imagens. */
object ImageMath {
    /** Maior potência de 2 tal que a imagem decodificada ainda tenha lado >= [target]. */
    fun sampleSize(width: Int, height: Int, target: Int): Int {
        require(target > 0)
        if (width <= 0 || height <= 0) return 1
        var sample = 1
        var w = width
        var h = height
        while (w / 2 >= target && h / 2 >= target) {
            w /= 2
            h /= 2
            sample *= 2
        }
        return sample
    }

    /** Escala para que o maior lado seja no máximo [max]; nunca amplia. */
    fun fitScale(width: Int, height: Int, max: Int): Float {
        val longest = maxOf(width, height)
        return if (longest <= max || longest == 0) 1f else max.toFloat() / longest
    }
}
