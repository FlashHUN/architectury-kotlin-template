package flash.testmod.api.animation

open class FixedFramerateAnimation(protected val frameTimeMs: Long, private val numFrames: Int) : Animation {
    private var lastTimeMs: Long?
    private var isRunning: Boolean
    private var step: Int
    private var currentFrameIndex: Int

    init {
        lastTimeMs = null
        isRunning = false
        step = 1
        currentFrameIndex = 0
    }

    override fun getFrame() = currentFrameIndex

    override fun isReverse() = step < 0

    override fun reverse() {
        step = -step
    }

    override fun start(force: Boolean) {
        if (!isRunning || force) {
            lastTimeMs = System.currentTimeMillis()
            isRunning = true
        }
    }

    override fun restart() {
        isRunning = true
        lastTimeMs = System.currentTimeMillis()
        reset()
    }

    override fun stop() {
        isRunning = false
    }

    override fun reset() {
        currentFrameIndex = if (isReverse()) numFrames - 1 else 0
    }

    override fun isRunning() = isRunning

    override fun tick() {
        if (!isRunning) return
        if (frameTimeMs <= 0) {
            stop()
            return
        }

        val timePassed = System.currentTimeMillis() - lastTimeMs!! // this should never be null, since we set the last time with every isRunning = true
        if (timePassed >= frameTimeMs) {
            val numFramesPassed = (timePassed / frameTimeMs).toInt()
            currentFrameIndex += step * numFramesPassed
            lastTimeMs = lastTimeMs!! + frameTimeMs * numFramesPassed

            if (currentFrameIndex >= numFrames || currentFrameIndex < 0) {
                currentFrameIndex = if (isReverse()) 0 else numFrames - 1
                stop()
            }
        }
    }

    override fun loop() {
        if (!isRunning) {
            reset()
            isRunning = true
        }
        tick()
    }

    override fun loopWithReverse() {
        if (!isRunning) {
            reverse()
            isRunning = true
        }
        tick()
    }

    override fun copy(): Animation {
        return FixedFramerateAnimation(frameTimeMs, numFrames)
    }
}