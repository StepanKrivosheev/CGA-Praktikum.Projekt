package cga.exercise.components.light

import cga.exercise.components.light.IPointLight
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

open class PointLight (val lightPos : Vector3f, var lightColor : Vector3f) : Transformable(), IPointLight {

    init {
        preTranslate(lightPos)
    }

    override fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("positionPoint",getWorldPosition())
        shaderProgram.setUniform("lightColorPoint",lightColor)
        shaderProgram.setUniform("constant", 1.0f)
        shaderProgram.setUniform("linear", 0.09f)
        shaderProgram.setUniform("quadratic", 0.032f)

    }
}