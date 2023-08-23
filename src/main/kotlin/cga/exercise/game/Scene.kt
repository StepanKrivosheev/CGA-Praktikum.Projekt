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


    //private val ground: Mesh
    private val groundObj = OBJLoader.loadOBJ("assets/models/ground.obj")
    private val groundList = mutableListOf<Mesh>()
    //private val groundRenderable : Renderable
    private val camera = TronCamera()
    private val pointLight : PointLight
    private val pointLight1 : PointLight
    private val pointLight2 : PointLight
    private val pointLight3 : PointLight
    //private val pointLight4 : PointLight
    private val pointLights : PointLights
    private val listPointLight = mutableListOf<PointLight>()
    private val spotLight : SpotLight
    private val wall1 : Renderable = loadModel("assets/blocks/walls.obj", 0f, 0f, (90.0f* PI/180).toFloat()) ?: throw IllegalArgumentException("Could not load the model")
    //private val tronBike : Renderable = loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", (-90.0f* PI/180).toFloat(), (90.0f* PI/180).toFloat(), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    private val fourBlock : Renderable = loadModel("assets/blocks/4erBlock.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val lBlock : Renderable = loadModel("assets/blocks/L-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val lBlockReverse : Renderable = loadModel("assets/blocks/L-Block.Reverse.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val longBlock : Renderable = loadModel("assets/blocks/Lang.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val tBlock : Renderable = loadModel("assets/blocks/T-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val zBlock : Renderable = loadModel("assets/blocks/Z-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val zBlockReverse : Renderable = loadModel("assets/blocks/Z-Block.Reverse.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val ground : Renderable = loadModel("assets/blocks/ground.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private var firstMouse : Boolean = true
    private var lastX: Double = window.windowWidth / 2.0
    private var lastY: Double = window.windowHeight / 2.0

    private val testCube : Renderable = loadModel("assets/testCube.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private var block: Renderable
    val placedBlocks = mutableListOf<Renderable>()


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

        //ground = Mesh(vertexData1, indexData1, objAttributes1, groundMaterial)

        //groundList.add(ground)
        //groundRenderable = Renderable(groundList)


        // blocks

        fourBlock.translate(Vector3f(0f, 20f, 0f))
        lBlock.translate(Vector3f(3f, 3f, 0f))
        lBlockReverse.translate(Vector3f(6f, 3f, 0f))
        longBlock.translate(Vector3f(9f, 20f, 0f))
        tBlock.translate(Vector3f(-3f, 3f, 0f))
        zBlock.translate(Vector3f(-6f, 3f, 0f))
        zBlockReverse.translate(Vector3f(-9f, 3f, 0f))

        testCube.translate(Vector3f(0f, 2f, 0f))



        // bike

        wall1.scale(Vector3f(0.3f))
        //tronBike.scale(Vector3f(0.8f))


        // camera

        //camera.parent = tronBike

        camera.rotate(Math.toRadians(-35f), 0f, 0f)
        camera.translate(Vector3f(0.0f, 5.0f, 30.0f))


        // lights

        pointLight = PointLight(Vector3f(4f, 30f, 4f), Vector3f(100f, 100f, 100f))
        pointLight1 = PointLight(Vector3f(-4f, 30f, -4f), Vector3f(100f, 100f, 100f))
        pointLight2 = PointLight(Vector3f(4f, 30f, -4f), Vector3f(100f, 100f, 100f))
        pointLight3 = PointLight(Vector3f(-4f, 30f, 4f), Vector3f(100f, 100f, 100f))

        listPointLight.add(pointLight)

        pointLights = PointLights(listPointLight)

        spotLight = SpotLight(20f, 30f, Vector3f(0f, 1f, 0f), Vector3f(1f, 1f, 1f))
        spotLight.rotate(Math.toRadians(-10f), 0f, 0f)

        //pointLight.parent = tronBike
        //spotLight.parent = tronBike

        block = spawner()

        for (each in block.meshes){

        }
        //initial opengl state
        enableDepthTest(GL_LESS)
        //enableFaceCulling(GL_CCW, GL_BACK)
        glClearColor(0.9f, 0.9f, 0.9f, .9f); GLError.checkThrow()
    }
    var gate = true
    fun render(dt: Float, t: Float) {


        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()

        staticShader.setUniform("staticColor", Vector3f(1f, 1f, 1f))

        camera.bind(staticShader)
        ground.render(staticShader)

        //wall1.render(staticShader)

        update(dt, t)
        onMouseMove(window.mousePos.xpos, window.mousePos.ypos)
        //tronBike.render(staticShader)

        block.render(staticShader)


        //testCube.render(staticShader)

        pointLights.render(staticShader)
        //spotLight.bind(staticShader, camera.getCalculateViewMatrix())

        //println(block.meshes[0].getVertexes(true,true,true))
    }
    var last = 7
    fun spawner (): Renderable {

        var block = testCube

        var pitch =  Math.toRadians(listOf(0f,90f,180f,270f).random())
        var yaw   =  Math.toRadians(listOf(0f,90f,180f,270f).random())
        var roll  =  Math.toRadians(listOf(0f,90f,180f,270f).random())

        var x =  (0..6).random()
        if (x== last) x =  (0..6-last).random()
        when (x){

            0 ->block = loadModel("assets/blocks/Z-Block.Reverse.obj", pitch,yaw,roll) ?: throw IllegalArgumentException("Could not load the model")
            1 ->block = loadModel("assets/blocks/Z-Block.obj", pitch,yaw,roll) ?: throw IllegalArgumentException("Could not load the model")
            2 ->block  = loadModel("assets/blocks/T-Block.obj", pitch,yaw,roll) ?: throw IllegalArgumentException("Could not load the model")
            3 ->block  = loadModel("assets/blocks/Lang.obj", pitch,yaw,roll) ?: throw IllegalArgumentException("Could not load the model")
            4 ->block = loadModel("assets/blocks/L-Block.Reverse.obj", pitch,yaw,roll) ?: throw IllegalArgumentException("Could not load the model")
            5 ->block  = loadModel("assets/blocks/L-Block.obj", pitch,yaw,roll) ?: throw IllegalArgumentException("Could not load the model")
            6 ->block  = loadModel("assets/blocks/4erBlock.obj", pitch,yaw,roll) ?: throw IllegalArgumentException("Could not load the model")

        }

        last = x
        block.translate(Vector3f(0f,20f,0f))
        return block
    }




    fun fall(block:Renderable){
        block.preTranslate(Vector3f(0f, -0.001f, 0f))
    }


    fun update(dt: Float, t: Float) {



        //camera.rotateAroundPoint(0f,.0001f,0f,Vector3f(0f,0f,0f)

        if (block.getPosition().y > 2f ) {
            fall(block)
        }
        else {
            placedBlocks.add(block)
            block = spawner()
        }

        for(each in placedBlocks) {
            each.render(staticShader)
        }

        if(window.getKeyState(GLFW_KEY_SPACE))block.preTranslate(Vector3f(0f, -.003f, 0f))
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int)
    {
        val movementSpeed = 1.0f
        val rotation = Math.toRadians(90.0f)

        if (action == GLFW_PRESS || action == GLFW_REPEAT) {
            when (key) {
                GLFW_KEY_W -> block.preTranslate(Vector3f(0f, 0f, -movementSpeed))
                GLFW_KEY_S -> block.preTranslate(Vector3f(0f, 0f, movementSpeed))
                GLFW_KEY_A -> block.preTranslate(Vector3f(-movementSpeed, 0f, 0f))
                GLFW_KEY_D -> block.preTranslate(Vector3f(movementSpeed, 0f, 0f))

                GLFW_KEY_E -> block.rotate(rotation, 0f, 0f)
                GLFW_KEY_R -> block.rotate(0f, rotation, 0f)
                GLFW_KEY_T -> block.rotate(0f, 0f, rotation)
            }
        }
    }

    fun onMouseMove(xpos: Double, ypos: Double) {

        // camera rotation

        if (firstMouse) {
            lastY = ypos
            lastX = xpos
            firstMouse = false
        }

        val sensitivity = 0.002f

        val offsetY : Float = (lastY - ypos).toFloat() * sensitivity
        val offsetX: Float = (lastX - xpos).toFloat() * sensitivity

        lastX = xpos
        lastY = ypos

        camera.rotate(0f, offsetX, 0f)
        camera.rotate(offsetY, 0f, 0f)
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
