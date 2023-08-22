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
    private val wall1 : Renderable = loadModel("assets/blocks/walls.obj", 0f, 0f, (90.0f* PI/180).toFloat()) ?: throw IllegalArgumentException("Could not load the model")
    //private val tronBike : Renderable = loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", (-90.0f* PI/180).toFloat(), (90.0f* PI/180).toFloat(), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    private val fourBlock : Renderable = loadModel("assets/blocks/4erBlock.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val lBlock : Renderable = loadModel("assets/blocks/L-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val lBlockReverse : Renderable = loadModel("assets/blocks/L-Block.Reverse.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val longBlock : Renderable = loadModel("assets/blocks/Lang.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val tBlock : Renderable = loadModel("assets/blocks/T-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val zBlock : Renderable = loadModel("assets/blocks/Z-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
    private val zBlockReverse : Renderable = loadModel("assets/blocks/Z-Block.Reverse.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
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

        ground = Mesh(vertexData1, indexData1, objAttributes1, groundMaterial)

        groundList.add(ground)
        groundRenderable = Renderable(groundList)


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
        camera.translate(Vector3f(0.0f, 5.0f, 10.0f))


        // lights

        pointLight = PointLight(Vector3f(-20f, 30f, 0f), Vector3f(100f, 100f, 10f))
        pointLight1 = PointLight(Vector3f(20f, 30f, 0f), Vector3f(100f, 100f, 10f))
        //pointLight1 = PointLight(Vector3f(-20f, 1f, -20f), Vector3f(0.5f, 1f, 0.5f))
        pointLight2 = PointLight(Vector3f(20f, 30f, 20f), Vector3f(100f, 100f, 10f))
        pointLight3 = PointLight(Vector3f(-20f, 30f, -200f), Vector3f(100f, 100f, 10f))
        pointLight4 = PointLight(Vector3f(-20f, 1f, 20f), Vector3f(1f, 1f, 0.5f))

        listPointLight.add(pointLight)
        listPointLight.add(pointLight1)
        listPointLight.add(pointLight2)
        listPointLight.add(pointLight3)
        listPointLight.add(pointLight4)

        pointLights = PointLights(listPointLight)

        spotLight = SpotLight(20f, 30f, Vector3f(0f, 1f, 0f), Vector3f(1f, 1f, 1f))
        spotLight.rotate(Math.toRadians(-10f), 0f, 0f)

        //pointLight.parent = tronBike
        //spotLight.parent = tronBike


        //initial opengl state
        enableDepthTest(GL_LESS)
        //enableFaceCulling(GL_CCW, GL_BACK)
        glClearColor(0.52f, 0.52f, 0.52f, .0f); GLError.checkThrow()


        block = spawner()
        block.translate(Vector3f(0f, 20f, 0f))


    }
    var gate = true
    fun render(dt: Float, t: Float) {


        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()

        staticShader.setUniform("staticColor", Vector3f(1f, 1f, 1f))

        camera.bind(staticShader)
        groundRenderable.render(staticShader)

        //wall1.render(staticShader)

        update(dt, t)
        onMouseMove(window.mousePos.xpos, window.mousePos.ypos)
        //tronBike.render(staticShader)

        block.render(staticShader)


        //testCube.render(staticShader)

        pointLights.render(staticShader)
        //spotLight.bind(staticShader, camera.getCalculateViewMatrix())

    }
    var last = 7
    fun spawner (): Renderable {

        var block = testCube

        var x =  (0..6).random()
        if (x== last) x =  (0..6-last).random()
        when (x){

            0 ->block = loadModel("assets/blocks/Z-Block.Reverse.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
            1 ->block = loadModel("assets/blocks/Z-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
            2 ->block  = loadModel("assets/blocks/T-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
            3 ->block  = loadModel("assets/blocks/Lang.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
            4 ->block = loadModel("assets/blocks/L-Block.Reverse.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
            5 ->block  = loadModel("assets/blocks/L-Block.obj", 0f, 0f, 0f) ?: throw IllegalArgumentException("Could not load the model")
            6 -> block  = loadModel("assets/blocks/4erBlock.obj", 0f, 0f,  0f) ?: throw IllegalArgumentException("Could not load the model")

        }
         last = x
        block.translate(Vector3f(0f,20f,0f))
        gate = false
        return block
    }
    fun test ( block: Renderable?):Renderable {

        if (block == null) {
            val blockNew = spawner()
            return blockNew
        }
        if (gate == true) {
            val blockNew = spawner()
            return blockNew
        }
        else return block

    }



    fun update(dt: Float, t: Float) {

        // camera movement

        if (window.getKeyState(GLFW_KEY_W)) {
            camera.translate(Vector3f(0f, 0f, -dt*5f))
        } else if (window.getKeyState(GLFW_KEY_S)) {
            camera.translate(Vector3f(0f, 0f , dt*5f))
        }

        if (window.getKeyState(GLFW_KEY_SPACE)) {
            camera.translate(Vector3f(0f, dt*5f, 0f))
        } else if (window.getKeyState(GLFW_KEY_LEFT_CONTROL)) {
            camera.translate(Vector3f(0f, -dt*5f, 0f))
        }

        if (window.getKeyState(GLFW_KEY_A)) {
            camera.translate(Vector3f(-dt*5f, 0f, 0f))
        } else if (window.getKeyState(GLFW_KEY_D)) {
            camera.translate(Vector3f(dt *5f, 0f, 0f))
        }


        fun fall(block:Renderable){
            block.translate(Vector3f(0f, -.03f, 0f))
        }

        if (block.getPosition().y > 0f ) fall (block)
        else {
            placedBlocks.add(block)
            block = spawner()
        }

        for(each in placedBlocks) {
            each.render(staticShader)
        }


    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

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
        //lastY = ypos

//        camera.rotate(0f, offsetX, 0f)
//        camera.rotate(offsetY, 0f, 0f)
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