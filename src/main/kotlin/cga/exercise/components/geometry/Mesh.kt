package cga.exercise.components.geometry

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created 29.03.2023.
 */
class Mesh(vertexdata: FloatArray, indexdata: IntArray, attributes: Array<VertexAttribute>) {
    //private data
    private var vaoId = 0
    private var vboId = 0
    private var iboId = 0
    private var indexcount = indexdata.size

    init {
        // todo Aufgabe 1.2.2 (shovel geometry data to GPU and tell OpenGL how to interpret it)

        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER,vboId)
        GL15.glBufferData(GL_ARRAY_BUFFER,vertexdata, GL_STATIC_DRAW)

        var i = 0
        for (x in attributes){
            GL20.glVertexAttribPointer(i,attributes[i].n,attributes[i].type,false,attributes[i].stride,
                    attributes[i].offset.toLong())
            glEnableVertexAttribArray(i)
            i++
        }

        iboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,iboId)
        GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER,indexdata, GL_STATIC_DRAW)
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER,0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0)
    }

    /**
     * Renders the mesh
     */
    fun render() {
        glBindVertexArray(vaoId)
        GL11.glDrawElements(GL_TRIANGLES,indexcount, GL_UNSIGNED_INT,0)
        glBindVertexArray(0)

    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (iboId != 0) glDeleteBuffers(iboId)
        if (vboId != 0) glDeleteBuffers(vboId)
        if (vaoId != 0) glDeleteVertexArrays(vaoId)
    }
}