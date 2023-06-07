package cga.exercise.components.geometry

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

open class Transformable(private var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {

    /**
     * Returns copy of object model matrix
     * @return modelMatrix
     */
    fun getModelMatrix(): Matrix4f {
        return Matrix4f(modelMatrix)
    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    fun getWorldModelMatrix(): Matrix4f {
        /**Lösung mit Local**/
//        val worldModelMatrix = Matrix4f(modelMatrix)
//        if (parent != null)  return worldModelMatrix.mulLocal(parent?.getWorldModelMatrix())
//        return worldModelMatrix

        val worldModelMatrix = Matrix4f(modelMatrix)
        var currentParent = parent
        while (currentParent != null) {
            worldModelMatrix.set(parent?.getWorldModelMatrix() ?: Matrix4f()).mul(modelMatrix)
            currentParent = currentParent.parent
        }
        return worldModelMatrix
    }

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotate(pitch: Float, yaw: Float, roll: Float) {
        modelMatrix.rotateX(Math.toRadians(pitch))
        modelMatrix.rotateY(Math.toRadians(yaw))
        modelMatrix.rotateZ(Math.toRadians(roll))
    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        modelMatrix = Matrix4f().translate(altMidpoint.negate()).mul(modelMatrix)
        modelMatrix = Matrix4f().rotateXYZ(pitch, yaw, roll).mul(modelMatrix)
        modelMatrix = Matrix4f().translate(altMidpoint.negate()).mul(modelMatrix)
    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    fun translate(deltaPos: Vector3f) {
        modelMatrix.translate(deltaPos)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: this operation has to be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun preTranslate(deltaPos: Vector3f) {
        modelMatrix.translateLocal(deltaPos)
    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scale(scale: Vector3f) {
        modelMatrix.scale(scale)
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f {
        val position = Vector3f()
        modelMatrix.getColumn(3, position)
        return position
    }

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f {
        val position = Vector3f()
        getWorldModelMatrix().getColumn(3, position)
        return position
    }

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f {
        val result = Vector3f()
        modelMatrix.getColumn(0, result)
        return result.normalize()
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f {
        val result = Vector3f()
        modelMatrix.getColumn(1, result)
        return result.normalize()
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f {
        val result = Vector3f()
        modelMatrix.getColumn(2, result)
        return result.normalize()
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {
        val result = Vector3f()
        getWorldModelMatrix().getColumn(0, result)
        return result.normalize()
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {
        val result = Vector3f()
        getWorldModelMatrix().getColumn(1, result)
        return result.normalize()
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {
        val result = Vector3f()
        getWorldModelMatrix().getColumn(2, result)
        return result.normalize()
    }
}