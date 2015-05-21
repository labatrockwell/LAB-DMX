package com.rockwell.dmx;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import artnet4j.ArtNet;

/**
 * Manager for all DMX Universes
 *
 */
public class DmxManager
{
    // The artnet to manage the install
    ArrayList<DmxUniverse> universes = new ArrayList<DmxUniverse>();
    ArtNet an;
    int fadeSpeed;

    /**
     * Create the manager
     * @param universeCount number of universes
     * @param deviceCount devices per universe
     */
    public DmxManager(String broadcastAddress, int universeCount, int[] deviceCount, int deviceChannels, int fadeSpeed) {
        an = new ArtNet();

        an.init();

        //Set Broadcast address as allowed by Art-Net revision Q
        an.setBroadCastAddress(broadcastAddress);

        try {
            //Start the server
            //Binds to port 6454
            an.start();
        } catch (Exception e) {
            System.out.println("Error starting ArtNet Server!");
            e.printStackTrace();
        }
        
        for (int i = 0; i < universeCount; i++) {
            addUniverse();
        }
        
        for (int i = 0; i < universeCount; i++) {
            for (int j = 0; j < deviceCount[i]; j++) {
                universes.get(i).addDevice(deviceChannels, 3);
            }
            universes.get(i).begin();
        }
        this.fadeSpeed = fadeSpeed;
        
        float[] dark = {0f,0f,0f};
        try
        {
            setAllColors(new DmxChannelColor(dark));
        }
        catch (DmxColorCountException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add a universe to the array
     */
    private void addUniverse() {
        universes.add(new DmxUniverse(an, universes.size()));
    }

    /**
     * Uniformly set all devices to a color
     * @param color the color to set
     */
    public void setAllColors(DmxChannelColor color) {
        for (DmxUniverse dmxu : universes) {
            dmxu.setAll(color);
        }
    }
    
    /**
     * Uniformly fade all devices to a color
     * @param color the color to set
     */
    public void fadeAllColors(DmxChannelColor color) {
        for (DmxUniverse dmxu : universes) {
            dmxu.fadeAll(color, fadeSpeed);
        }
    }

    /**
     * Set a specific universe color
     * @param universeId universe to set
     * @param color color to set
     */
    public void setUniverseColor(int universeId, DmxChannelColor color) {
        universes.get(universeId).setAll(color);
    }

    /**
     * Set a specific device color
     * @param universeId universe to set
     * @param deviceId device in that universe to set
     * @param color color to set
     */
    public void setDeviceColor(int universeId, int deviceId, DmxChannelColor color) {
        universes.get(universeId).setOne(deviceId, color);
    }

    /**
     * Stop the artnet
     */
    public void shutDown() {
        an.stop();
    }
}
