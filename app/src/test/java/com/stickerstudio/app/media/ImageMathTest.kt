package com.stickerstudio.app.media

import org.junit.Assert.assertEquals
import org.junit.Test

class ImageMathTest {
    @Test fun sampleSize_smallImage_isOne() =
        assertEquals(1, ImageMath.sampleSize(1000, 800, 2048))

    @Test fun sampleSize_bigImage_isPowerOfTwoKeepingTarget() =
        assertEquals(2, ImageMath.sampleSize(4500, 4200, 2048))

    @Test fun sampleSize_invalidDimensions_isOne() =
        assertEquals(1, ImageMath.sampleSize(0, 0, 2048))

    @Test fun fitScale_neverUpscales() =
        assertEquals(1f, ImageMath.fitScale(500, 300, 2048), 0f)

    @Test fun fitScale_downscalesLongestSide() =
        assertEquals(0.5f, ImageMath.fitScale(4096, 2000, 2048), 0.0001f)
}
