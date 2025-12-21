package wade.owen.watts.base_jetpack.ui.pages.calendar.renderer

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

        return Pair(vertexData, indices.toIntArray())
    }
}
