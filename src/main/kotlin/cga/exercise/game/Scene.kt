package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.light.PointLights
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.exercise.components.texture.Texture2D.Companion.invoke
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader.loadModel
import cga.framework.OBJLoader
import org.joml.Math
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import java.awt.Point
import kotlin.math.PI
import kotlin.math.sin

/**
 * Created 29.03.2023.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")


    private val ground: Mesh
    private val groundObj = OBJLoader.loadOBJ("assets/models/ground.obj")
    private val groundList = mutableListOf<Mesh>()
    private val groundRenderable : Renderable
    private val camera = TronCamera()
    private val pointLight : PointLight
    private val pointLight1 : PointLight
    private val pointLight2 : PointLight
    private val pointLight3 : PointLight
    private val pointLight4 : PointLight
    private val pointLights : PointLights
    private val listPointLight = mutableListOf<PointLight>()
    private val spotLight : SpotLight
    private val tronBike : Renderable? = loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", (-90.0f* PI/180).toFloat(), (90.0f* PI/180).toFloat(), 0.0f)
    private val fourBlock : Renderable? = loadModel("assets/blocks/4erBlock.obj", 0f, 0f, 0f)
    private val lBlock : Renderable? = loadModel("assets/blocks/L-Block.obj", 0f, 0f, 0f)
    private var firstMouse : Boolean = true
    private var lastX: Double = window.windowWidth / 2.0
    private var lastY: Double = window.windowHeight / 2.0


    //scene setup
    init {

        // ground

        val groundMesh: OBJLoader.OBJMesh = groundObj.objects[0].meshes[0]

        val vertexData1 = groundMesh.vertexData
        val indexData1 = groundMesh.indexData

        val groundPos = VertexAttribute(3, GL_FLOAT, 32, 0)
        val groundTex = VertexAttribute(2, GL_FLOAT, 32,12)
        val groundNorm = VertexAttribute(3, GL_FLOAT, 32,20)
        val objAttributes1 = arrayOf<VertexAttribute>(groundPos, groundTex, groundNorm)

        val groundDiff = Texture2D("assets/textures/ground_diff.png", true)
        val groundEmit = Texture2D("assets/textures/ground_emit.png", true)
        val groundSpec = Texture2D("assets/textures/ground_spec.png", true)

        groundDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        groundEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        groundSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundMaterial = Material(groundDiff, groundEmit, groundSpec, 60.0f, Vector2f(64.0f, 64.0f))

        ground = Mesh(vertexData1, indexData1, objAttributes1, groundMaterial)

        groundList.add(ground)
        groundRenderable = Renderable(groundList)


        // blocks

        fourBlock?.translate(Vector3f(0f, 2f, 0f))
        lBlock?.translate(Vector3f(4f, 2f, 0f))


        // bike

        tronBike?.scale(Vector3f(0.8f))


        // camera

        camera.parent = tronBike

        camera.rotate(Math.toRadians(-35f), 0f, 0f)
        camera.translate(Vector3f(0.0f, 0.0f, 4.0f))


        // lights

        pointLight = PointLight(Vector3f(0f, 0.5f, 0f), Vector3f(0.1f, 0.1f, 0.1f))
        pointLight1 = PointLight(Vector3f(-20f, 1f, -20f), Vector3f(0.5f, 1f, 0.5f))
        pointLight2 = PointLight(Vector3f(20f, 1f, -20f), Vector3f(1f, 0.5f, 0.5f))
        pointLight3 = PointLight(Vector3f(20f, 1f, 20f), Vector3f(0.5f, 0.5f, 1f))
        pointLight4 = PointLight(Vector3f(-20f, 1f, 20f), Vector3f(1f, 1f, 0.5f))

        listPointLight.add(pointLight)
        listPointLight.add(pointLight1)
        listPointLight.add(pointLight2)
        listPointLight.add(pointLight3)
        listPointLight.add(pointLight4)

        pointLights = PointLights(listPointLight)

        spotLight = SpotLight(20f, 30f, Vector3f(0f, 1f, 0f), Vector3f(1f, 1f, 1f))
        spotLight.rotate(Math.toRadians(-10f), 0f, 0f)

        pointLight.parent = tronBike
        spotLight.parent = tronBike


        // misc

        enableDepthTest(GL_LESS)
        enableFaceCulling(GL_CCW, GL_BACK)

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()

        staticShader.setUniform("staticColor", Vector3f(0f, 1f, 0f))

        camera.bind(staticShader)
        groundRenderable.render(staticShader)

        update(dt, t)
        onMouseMove(window.mousePos.xpos, window.mousePos.ypos)
        tronBike?.render(staticShader)

        fourBlock?.render(staticShader)
        lBlock?.render(staticShader)

        pointLights.render(staticShader)
        spotLight.bind(staticShader, camera.getCalculateViewMatrix())

    }

    fun update(dt: Float, t: Float) {

        // movement bike

        if (window.getKeyState(GLFW_KEY_W)) {
            tronBike?.translate(Vector3f(0f, 0f, -dt*5f))
            if (window.getKeyState(GLFW_KEY_A)) {
                tronBike?.rotate(0f, Math.toRadians(0.1f), 0f)
            } else if (window.getKeyState(GLFW_KEY_D)) {
                tronBike?.rotate(0f, Math.toRadians(-0.1f), 0f)
            }
        } else if (window.getKeyState(GLFW_KEY_S)) {
            tronBike?.translate(Vector3f(0f, 0f , dt*5f))
        }

        // light changing

        val lightColor = Vector3f(sin(t*0.1f), sin(t*0.2f), sin(t*0.3f))

        pointLight.lightColor = lightColor
        staticShader.setUniform("staticColor", lightColor)

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {

        if (firstMouse)
        {
            lastX = xpos
            firstMouse = false
        }

        val offsetX : Float = ((lastX - xpos) * 0.002).toFloat()
        lastX = xpos

        camera.rotateAroundPoint(0f, offsetX, 0f, Vector3f(0f, 0f, 0f))

    }

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