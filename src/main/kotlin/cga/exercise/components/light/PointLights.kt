package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram


class PointLights (val pointLights : MutableList<PointLight>): Transformable(), IPointLights {

    override fun render(shaderProgram: ShaderProgram) {

        var i = 1
        for (each in pointLights) {

            shaderProgram.setUniform("positionPoint[$i]",each.getWorldPosition())
            shaderProgram.setUniform("lightColorPoint[$i]",each.lightColor)
            shaderProgram.setUniform("constant", 1.0f)
            shaderProgram.setUniform("linear", 0.09f)
            shaderProgram.setUniform("quadratic", 0.032f)
            i++
        }
    }
}