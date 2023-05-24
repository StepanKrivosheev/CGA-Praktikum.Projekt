package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.lwjgl.opengl.GL30.*
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * Created 29.03.2023.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

    private val simpleMesh: Mesh
    private val sphere: Mesh
    private val ground: Mesh
    private val groundMatrix: Matrix4f = Matrix4f()
    private val sphereMatrix: Matrix4f = Matrix4f()
    private val objRes1 = OBJLoader.loadOBJ("assets/models/ground.obj")
    private val objRes = OBJLoader.loadOBJ("assets/models/sphere.obj")
    private val sphereList = mutableListOf<Mesh>()
    private val groundList = mutableListOf<Mesh>()
    private val groundRenderable : Renderable
    private val sphereRenderable : Renderable
    private val camera = TronCamera()


    //scene setup
    init {
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
            4, 2, 3
        )

        val pos = VertexAttribute(3, GL_FLOAT, 24, 0)
        val color = VertexAttribute(3, GL_FLOAT, 24,12)
        val attributes = arrayOf<VertexAttribute>(pos, color)

        simpleMesh = Mesh(vertices, indices, attributes)



        val objMesh: OBJLoader.OBJMesh = objRes.objects[0].meshes[0]

        val vertexData = objMesh.vertexData
        val indexData = objMesh.indexData

        val objPos = VertexAttribute(3, GL_FLOAT, 32, 0)
        val objColor = VertexAttribute(2, GL_FLOAT, 32,12)
        val objNorm = VertexAttribute(3, GL_FLOAT, 32,20)
        val objAttributes = arrayOf<VertexAttribute>(objPos, objColor, objNorm)

        sphere = Mesh(vertexData, indexData, objAttributes)

//        sphereMatrix.scale(0.5f)


        val objMesh1: OBJLoader.OBJMesh = objRes1.objects[0].meshes[0]

        val vertexData1 = objMesh1.vertexData
        val indexData1 = objMesh1.indexData

        val objPos1 = VertexAttribute(3, GL_FLOAT, 32, 0)
        val objColor1 = VertexAttribute(2, GL_FLOAT, 32,12)
        val objNorm1 = VertexAttribute(3, GL_FLOAT, 32,20)
        val objAttributes1 = arrayOf<VertexAttribute>(objPos1, objColor1, objNorm1)

        ground = Mesh(vertexData1, indexData1, objAttributes1)


        groundList.add(ground)
        sphereList.add(sphere)

        sphereRenderable = Renderable(sphereList)
        sphereRenderable.scale(Vector3f(0.5f))

        groundRenderable = Renderable(groundList)
        //groundRenderable.rotate(90f, 0f, 0f)
        //groundRenderable.scale(Vector3f(0.7f))

        camera.parent = sphereRenderable

        camera.rotate(Math.toRadians(-20f), 0f, 0f)
        camera.translate(Vector3f(0.0f, 0.0f, 4.0f))

        enableDepthTest(GL_LESS)
        enableFaceCulling(GL_CCW, GL_BACK)

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()

        camera.bind(staticShader)
        groundRenderable.render(staticShader)
        sphereRenderable.render(staticShader)

    }

    fun update(dt: Float, t: Float) {}

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {}

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