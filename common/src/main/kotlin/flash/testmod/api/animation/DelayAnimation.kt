package flash.testmod.api.animation

import net.minecraft.util.Mth

open class DelayAnimation(private val delays: List<Int>) : Animation {
    private var lastTimeMs: Long?
    private var isRunning: Boolean
    private var step: Int
    private var currentFrameIndex: Int
    private val numFrames: Int

    init {
        lastTimeMs = null
        isRunning = false
        step = 1
        currentFrameIndex = 0
        numFrames = delays.size
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
        if (!isRunning || lastTimeMs == null) return
        if (numFrames == 0) {
            stop()
            return
        }

        var timePassed = System.currentTimeMillis() - lastTimeMs!! // this should never be null, since we set the last time with every isRunning = true
        var numFramesPassed = 0
        var totalFrameTimePassed = 0
        var frameTimeMs = delays[currentFrameIndex]
        while (timePassed - frameTimeMs >= 0) {
            numFramesPassed++
            totalFrameTimePassed += frameTimeMs
            timePassed -= frameTimeMs
            val newIndex = currentFrameIndex + step * numFramesPassed
            if (newIndex < numFrames - 1 && newIndex >= 0) {
                frameTimeMs = delays[newIndex]
            } else {
                stop()
                break
            }
        }
        if (numFramesPassed > 0) {
            currentFrameIndex = Mth.clamp(currentFrameIndex + step * numFramesPassed, 0, numFrames - 1)
            lastTimeMs = lastTimeMs!! + totalFrameTimePassed

            if (((currentFrameIndex == 0 && isReverse()) || (currentFrameIndex == numFrames - 1 && !isReverse())) && timePassed > frameTimeMs) {
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
        return DelayAnimation(delays)
    }
}