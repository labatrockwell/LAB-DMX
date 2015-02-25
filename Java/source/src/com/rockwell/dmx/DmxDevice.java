package com.rockwell.dmx;


/**
 * Shared functionality amongst all DMX Device implementations
 */
class DmxDevice
{
    // Number of channels we need to set on this device
    int channelClusters;
    // Current color of the device
    public DmxChannelColor currentColor;
    // Bytes of all channels, based on the current color
    byte[] channelBytes;
    // Step counters for transition progress
    int stepsCompleted = 0;
    int targetSteps = 0;
    int colorCount;
    float[] colorStepValues;
    // Tell whether we need to refresh universe
    private boolean needRefresh = false;

    /**
     * Create a device
     * @param an device's network
     * @param universeId device's universe
     */
    public DmxDevice (int channelClusters, int colorCount) {
        this.channelClusters = channelClusters;
        this.colorCount = colorCount;
        channelBytes = new byte[this.channelClusters * colorCount];
        colorStepValues = new float[colorCount];
    }

    /**
     * Set the color of this device
     */
    public synchronized void setColor(DmxChannelColor color) throws DmxColorCountException {
        //System.out.println("Set color to " + color);
        if (color.getColors().length == colorCount) {
            currentColor = color;
            stepsCompleted = targetSteps;
            needRefresh = true;
        }
        else {
            throw new DmxColorCountException();
        }
    }

    /**
     * Start a color transition on this device
     */
    public synchronized void fadeColor(DmxChannelColor color, int steps) throws DmxColorCountException {

        if (color.getColors().length == colorCount) {
            stepsCompleted = 0;
            targetSteps = steps;
            float[] targetValues = color.getColors();
            float[] currentValues = currentColor.getColors();
            // Calculate increments for each rgb channel based on number of steps
            for (int i = 0; i < currentValues.length; i++) {
                colorStepValues[i] = (targetValues[i] - currentValues[i])/steps;
            }
            needRefresh = true;
        }
        else {
            throw new DmxColorCountException();
        }
    }

    public synchronized void stepFade() {
        if (stepsCompleted == targetSteps) {
            // If we only needed a single color refresh, or if the
            // fade is complete, then stop asking for refresh
            needRefresh = false;
        }
        else {
            // If we are in the midst of fading, continue
            float[] currentState = currentColor.getColors();
            
            float[] newVals = new float[colorCount];
            
            for (int i = 0; i < currentState.length; i++) {
                newVals[i] = constrainColor(currentState[i] + colorStepValues[i], 1, 254);
            }
            
            try {
                DmxChannelColor newColor = new DmxChannelColor(newVals);
                setColor(newColor);
                stepsCompleted++;
                needRefresh = true;
            }
            catch (DmxColorCountException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return the full byte array representing all channels on this device
     */
    public byte[] getChannelBytes() {
        byte[] values = currentColor.getValues();
        for (int i = 0; i < channelClusters * colorCount; i += colorCount) {
            for (int j = 0; j < colorCount; j++) {
                channelBytes[i + j] = values[j];
            }
        }
        return channelBytes;
    }

    /**
     * Indicate whether color has changed such that we need a refresh
     */
    public boolean needsRefresh() {
        return needRefresh;
    }

    /**
     * Block color values to ensure 0-255 range
     * @param value color value to constrain
     * @param min minimum value
     * @param max max value
     * @return
     */
    private float constrainColor(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
}
