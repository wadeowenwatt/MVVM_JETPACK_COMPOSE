package wade.owen.watts.base_jetpack.ui.pages.calendar.renderer

import android.util.Log
import kotlin.math.cos
import kotlin.math.sin

object SphereGridGenerator {

    /**
     * Generates a wireframe sphere grid (UV Sphere topology).
     *
     * @param radius The radius of the sphere.
     * @param latSegments Number of latitude rings (horizontal).
     * @param longSegments Number of longitude lines (vertical segments).
     * @return Pair of (Vertex Data FloatArray, Index Data IntArray)
     */
    fun generateSphere(
        radius: Float,
        latSegments: Int,
        longSegments: Int
    ): Pair<FloatArray, IntArray> {
        val numVertices = (latSegments + 1) * (longSegments + 1)
        // Each vertex has 7 floats: x, y, z, r, g, b, a
        val vertexData = FloatArray(numVertices * 7)

        val indices = ArrayList<Int>()

        // 1. Generate Vertices
        var vertexIndex = 0
        for (y in 0..latSegments) {
            val v = y.toFloat() / latSegments.toFloat()
            // Latitude angle runs from 0 to PI (North Pole to South Pole)
            val theta = (v * Math.PI).toFloat()

            for (x in 0..longSegments) {
                val u = x.toFloat() / longSegments.toFloat()
                // Longitude angle runs from 0 to 2*PI
                val phi = (u * 2 * Math.PI).toFloat()

                // Spherical to Cartesian conversion
                // x = r * sin(theta) * cos(phi)
                // y = r * cos(theta) (We use Y as up/down axis)
                // z = r * sin(theta) * sin(phi)
                val px = radius * sin(theta) * cos(phi)
                val py = radius * cos(theta)
                val pz = radius * sin(theta) * sin(phi)

                // Position (0, 1, 2)
                vertexData[vertexIndex * 7 + 0] = px
                vertexData[vertexIndex * 7 + 1] = py
                vertexData[vertexIndex * 7 + 2] = pz

                // Color (3, 4, 5, 6) - Default Neon Blue/Cyber color
                // We'll calculate a gradient based on Y position (height)
                var intensity = 0.5f + 0.5f * sin(v * Math.PI.toFloat()) // brighter at equator

                // Neon Cyan/Blue base
                vertexData[vertexIndex * 7 + 3] = 0.0f          // R
                vertexData[vertexIndex * 7 + 4] = 0.8f + (0.2f * intensity) // G
                vertexData[vertexIndex * 7 + 5] = 1.0f          // B
                vertexData[vertexIndex * 7 + 6] = 1.0f          // A

                vertexIndex++
            }
        }

        // 2. Generate Indices (Wireframe)
        // We will generate lines.
        val stride = longSegments + 1

        for (y in 0 until latSegments) {
            for (x in 0 until longSegments) {
                val current = y * stride + x
                val nextHorizontal = current + 1
                val nextVertical = (y + 1) * stride + x

                // Horizontal Line (Latitude Ring)
                // Connect current to nextHorizontal
                // Since we duplicated vertices at u=0 and u=1 for texture coords usually, 
                // but here for wireframe, let's keep it simple.
                // If we want a seamless loop, the last vertex in the row should connect to the first?
                // Our loop above goes 0..longSegments, so index `longSegments` overlaps index `0` spatially.

                if (x < longSegments) {
                    indices.add(current)
                    indices.add(nextHorizontal)
                }

                // Vertical Line (Longitude)
                indices.add(current)
                indices.add(nextVertical)
            }
        }

        Log.i("Indices: ", indices.toString())

        return Pair(vertexData, indices.toIntArray())
    }

    fun generateCustomSphere(
        radius: Float
    ): Pair<FloatArray, IntArray> {

        val VERTEX_STRIDE = 7
        val vertexData = FloatArray(20 * VERTEX_STRIDE)
        val indices = ArrayList<Int>()

        var v = 0

        fun putVertex(
            x: Float, y: Float, z: Float,
            r: Float, g: Float, b: Float, a: Float
        ) {
            vertexData[v * 7 + 0] = x
            vertexData[v * 7 + 1] = y
            vertexData[v * 7 + 2] = z
            vertexData[v * 7 + 3] = r
            vertexData[v * 7 + 4] = g
            vertexData[v * 7 + 5] = b
            vertexData[v * 7 + 6] = a
            v++
        }

        // ---------- 1. Generate vertices ----------

        // Poles
        putVertex(0f, radius, 0f, 1f, 1f, 1f, 1f)   // North -- Index 0

        val northIndex = 0
        val southIndex = 19

        val ringCount = 6
        val step = (2.0 * Math.PI / ringCount).toFloat()

        val yUpper = radius * 0.5f
        val yEquator = 0f
        val yLower = -radius * 0.5f

        fun ringRadius(y: Float) =
            kotlin.math.sqrt(radius * radius - y * y)

        // Upper ring (no offset)
        val rUpper = ringRadius(yUpper)
        for (i in 0 until ringCount) {
            val angle = i * step
            putVertex(
                (rUpper * cos(angle)).toFloat(),
                yUpper,
                (rUpper * sin(angle)).toFloat(),
                0.2f, 1f, 0.3f, 0.8f
            )
        }

        // Equator ring (offset by half step)
        val rEquator = radius
        val offset = step / 2f
        for (i in 0 until ringCount) {
            val angle = i * step + offset
            putVertex(
                (rEquator * cos(angle)).toFloat(),
                yEquator,
                (rEquator * sin(angle)).toFloat(),
                0.4f, 1f, 1f, 1f
            )
        }

        // Lower ring (same alignment as upper)
        val rLower = ringRadius(yLower)
        for (i in 0 until ringCount) {
            val angle = i * step
            putVertex(
                (rLower * cos(angle)).toFloat(),
                yLower,
                (rLower * sin(angle)).toFloat(),
                0.2f, 0.8f, 1f, 1f
            )
        }

        // South Pole -- Index 19
        putVertex(0f, -radius, 0f, 1f, 0f, 0.2f, 1f)

        // ---------- 2. Generate indices (triangles) ----------

        val upperStart = 1
        val equatorStart = 7
        val lowerStart = 13

        // North cap
        for (i in 0 until ringCount) {
            val a = upperStart + i
            val b = upperStart + (i + 1) % ringCount
            indices += northIndex
            indices += a
            indices += b
        }

        // Upper ↔ Equator
        for (i in 0 until ringCount) {
            val u0 = upperStart + i
            val u1 = upperStart + (i + 1) % ringCount
            val e0 = equatorStart + i
            val e1 = equatorStart + (i + 1) % ringCount

            indices += u0; indices += e0; indices += u1
            indices += u1; indices += e0; indices += e1
        }

        // Equator ↔ Lower
        for (i in 0 until ringCount) {
            val e0 = equatorStart + i
            val e1 = equatorStart + (i + 1) % ringCount
            val l0 = lowerStart + i
            val l1 = lowerStart + (i + 1) % ringCount

            indices += e0; indices += l0; indices += e1
            indices += e1; indices += l0; indices += l1
        }

        // South cap
        for (i in 0 until ringCount) {
            val a = lowerStart + i
            val b = lowerStart + (i + 1) % ringCount
            indices += a
            indices += southIndex
            indices += b
        }

        Log.i("Indices: ", indices.toString())

        return Pair(vertexData, indices.toIntArray())
    }
}
