package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {

        emit.bind(0)
        shaderProgram.setUniform("texEmit", 0)

        glUniform2f(glGetUniformLocation(shaderProgram.programID, "tcMultiplier"), tcMultiplier.x, tcMultiplier.y)
    }
}