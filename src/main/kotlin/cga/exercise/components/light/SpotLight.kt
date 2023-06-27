package cga.exercise.components.light

import cga.exercise.components.light.PointLight
import cga.exercise.components.light.ISpotLight
import cga.exercise.components.shader.ShaderProgram
import org.joml.*
import org.joml.Math.cos
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

class SpotLight (private val innerAngle : Float,
                 private val outerAngle : Float,
                 lightPos : Vector3f,
                 lightColor : Vector3f) : PointLight(lightPos, lightColor), ISpotLight
{

    init {
        preTranslate(lightPos)
    }

    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {

        val position = Vector4f(getWorldPosition(), 1f).mul(viewMatrix)

        glUniform1f(glGetUniformLocation(shaderProgram.programID, "innerAngle"), cos(Math.toRadians(innerAngle)))
        glUniform1f(glGetUniformLocation(shaderProgram.programID, "outerAngle"), cos(Math.toRadians(outerAngle)))
        shaderProgram.setUniform("directionSpot", getWorldZAxis().mul(Matrix3f(viewMatrix)))
        shaderProgram.setUniform("positionSpot", Vector3f(position.x, position.y, position.z))
        shaderProgram.setUniform("lightColorSpot", lightColor)
        glUniform1f(glGetUniformLocation(shaderProgram.programID, "constant"), 1.0f)
        glUniform1f(glGetUniformLocation(shaderProgram.programID, "linear"), 0.045f)
        glUniform1f(glGetUniformLocation(shaderProgram.programID, "quadratic"), 0.0075f)
    }

}