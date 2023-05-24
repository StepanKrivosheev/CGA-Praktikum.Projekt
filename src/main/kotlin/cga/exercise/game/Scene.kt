package cga.exercise.game

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import cga.framework.GameWindow
import org.lwjgl.opengl.GL30.*
import cga.framework.OBJLoader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector3f
import org.joml.Vector4f
import java.lang.Math.sqrt
import kotlin.math.pow

/**
 * Created 29.03.2023.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
    private val mesh: Mesh
    private val meshGround: Mesh
    private val ground : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj",true,true)
    private val meshSphere: Mesh
    private val sphere : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/sphere.obj",true,true)
    private val matrixGround : Matrix4f = Matrix4f()
    private val matrixSphere : Matrix4f = Matrix4f()
    //private val simpleMesh: Mesh

    //scene setup
    init {
        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()

        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f
        )

        val indices = intArrayOf(
            0, 1, 2,
            0, 2, 4,
            4, 2, 1
        )

        val position = VertexAttribute(3, GL_FLOAT,24,0)
        val color = VertexAttribute(3, GL_FLOAT,24,12)
        val vertexAttributes = arrayOf<VertexAttribute>(position,color)
        mesh = Mesh(vertices,indices,vertexAttributes)

        //sphere
        val sphereMesh: OBJLoader.OBJMesh = sphere.objects[0].meshes[0]

        matrixSphere.scale(0.5f)

        val vertexDataSphere = sphereMesh.vertexData
        val indexDataSphere = sphereMesh.indexData

        val spherePos = VertexAttribute(3, GL_FLOAT,32,0)
        val sphereColor = VertexAttribute(2, GL_FLOAT,32,12)
        val sphereNorm = VertexAttribute(3, GL_FLOAT,32,20)
        val VertexAttributesSphere = arrayOf<VertexAttribute>(spherePos,sphereColor,sphereNorm)

        meshSphere = Mesh( vertexDataSphere , indexDataSphere , VertexAttributesSphere)

        //Ground

        /**/
        val groundMesh: OBJLoader.OBJMesh = ground.objects[0].meshes[0]

        matrixGround.rotateX(270f*Math.PI.toFloat()/180f)
        matrixGround.scale(.7f)

        val vertexDataGround = groundMesh.vertexData
        val indexDataGround = groundMesh.indexData

        val groundPos = VertexAttribute(3, GL_FLOAT,32,0)
        val groundColor = VertexAttribute(2, GL_FLOAT,32,12)
        val groundNorm = VertexAttribute(3, GL_FLOAT,32,20)
        val VertexAttributesGround = arrayOf<VertexAttribute>(groundPos,groundColor,groundNorm)

        meshGround = Mesh( vertexDataGround , indexDataGround , VertexAttributesGround)

    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        enableFaceCulling(GL_CCW, GL_BACK)
        enableDepthTest(GL_ALWAYS)
        staticShader.use()

        staticShader.setUniform("model_matrix", matrixGround,true)
        meshGround.render()




        staticShader.setUniform("model_matrix", matrixSphere,true)
        meshSphere.render()



    }

    fun update(dt: Float, t: Float) {}

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {
        mesh.cleanup()
    }

    /**
     * enables culling of specified faces
     * orientation: ordering of the vertices to define the front face
     * faceToCull: specifies the face, that will be culled (back, front)
     * Please read the docs for accepted parameters
     */
    fun enableFaceCulling(orientation: Int, faceToCull: Int){
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(orientation); GLError.checkThrow()
        glCullFace(faceToCull); GLError.checkThrow()
    }

    /**
     * enables depth test
     * comparisonSpecs: specifies the comparison that takes place during the depth buffer test
     * Please read the docs for accepted parameters
     */
    fun enableDepthTest(comparisonSpecs: Int){
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(comparisonSpecs); GLError.checkThrow()
    }
}

