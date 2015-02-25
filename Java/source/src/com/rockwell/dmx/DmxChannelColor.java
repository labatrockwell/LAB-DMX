package com.rockwell.dmx;

/**
 * Class to represent a 1 to 4 channel DMX color
 */
class DmxChannelColor
{
    private float[] values;
    
    public DmxChannelColor(float[] values) throws DmxColorCountException{
        if (values.length >= 1 && values.length <= 4) {
            this.values = values;
        }
        else {
            throw new DmxColorCountException();
        }
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
        for (int i = 0; i < values.length; i++) {
            byteConversion[i] = (byte)values[i];
        }
        return byteConversion;
    }
  
    public boolean equals(DmxChannelColor c) {
        if (c.values.length != values.length) {
            return false;
        }
        else {
            for (int i = 0; i < values.length; i++) {
                if (values[i] != c.values[i]) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public String toString() {
        String output = "DMX Color: ";
        for (int i = 0; i < values.length; i++) {
            output+= values[i] + " ";
        }
        return output;
    }
}
