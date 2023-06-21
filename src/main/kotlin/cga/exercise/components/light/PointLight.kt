package cga.exercise.components.light

import cga.exercise.components.light.IPointLight
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

open class PointLight (lightPos : Vector3f, val lightColor : Vector3f) : Transformable(), IPointLight {

    init {
        preTranslate(lightPos)
    }

    override fun bind(shaderProgram: ShaderProgram) {
        glUniform3f(glGetUniformLocation(shaderProgram.programID, "positionPoint"), getWorldPosition().x, getWorldPosition().y, getWorldPosition().z)
        glUniform3f(glGetUniformLocation(shaderProgram.programID, "lightColorPoint"), lightColor.x, lightColor.y, lightColor.z)
        glUniform1f(glGetUniformLocation(shaderProgram.programID, "constant"), 1.0f)
        glUniform1f(glGetUniformLocation(shaderProgram.programID, "linear"), 0.09f)
        glUniform1f(glGetUniformLocation(shaderProgram.programID, "quadratic"), 0.032f)
    }
}