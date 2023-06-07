package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram


class Renderable (val meshes : MutableList<Mesh>) : Transformable(), IRenderable {
    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", this.getWorldModelMatrix(), false)
        for (each in meshes) {
            each.render(shaderProgram)
        }
    }
}