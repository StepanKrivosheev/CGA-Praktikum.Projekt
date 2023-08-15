package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram

interface IPointLights {
    fun render(shaderProgram: ShaderProgram)
}