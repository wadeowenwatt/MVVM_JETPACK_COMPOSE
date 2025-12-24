package wade.owen.watts.base_jetpack.ui.pages.calendar.renderer.touchpad

object TouchPadGenerator {
    fun generateVertexAndIndices():  Pair<FloatArray, IntArray>  {
        val vertices = floatArrayOf(
            1f, 1f, 1f, 1f, 0f, 0f, 1f,
            -1f, 1f, 1f, 0f, 1f, 0f, 1f,
            -1f, -1f, 1f, 0f, 0f, 1f, 1f,
            1f, -1f, 1f, 1f, 1f, 0f, 1f,
        )
        val indices = intArrayOf(
            0, 1, 1, 2, 2, 3, 3, 0,
        )

        return Pair(vertices, indices)
    }

}