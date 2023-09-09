package flash.testmod.api.animation

interface Animation {
    fun getFrame() : Int
    fun isReverse() : Boolean
    fun reverse()
    fun start(force: Boolean = false)
    fun restart()
    fun stop()
    fun reset()
    fun isRunning() : Boolean
    fun tick()
    fun loop()
    fun loopWithReverse()
    fun copy() : Animation
}