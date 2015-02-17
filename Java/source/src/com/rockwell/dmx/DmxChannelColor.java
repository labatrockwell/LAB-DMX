package com.rockwell.dmx;

/**
 * Class to represent a 3-channel DMX color
 */
class DmxChannelColor
{
    private float[] values = new float[3];
    
    public DmxChannelColor(float r, float g, float b) {
        values[0] = r;
        values[1] = g;
        values[2] = b;
    }
    
    /**
     * Get the current color ints for manipulation
     * @return the current rgb values
     */
    public float[] getColors() {
        return values;
    }
    
    /** 
     * @return byte values for this color
     */
    public byte[] getValues() {
        byte[] byteConversion = new byte[values.length];
        byteConversion[0] = (byte)values[0];
        byteConversion[1] = (byte)values[1];
        byteConversion[2] = (byte)values[2];
        return byteConversion;
    }
  
    public boolean equals(DmxChannelColor c) {
        if (c.values[0] == values[0] && c.values[1] == values[1] &&c.values[2] == values[2]) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public String toString() {
        return ("r:" + values[0] + " g:" + values[1] + " b:" + values[2]);
    }
}
