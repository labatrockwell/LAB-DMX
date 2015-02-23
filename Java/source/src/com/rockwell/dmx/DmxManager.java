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

    /**
     * Create the manager
     * @param panelsPerUniverse number of panels in each panel universe
     * @param pendantsPerUniverse number of pendants in each pendant universe
     */
    public DmxManager() {
        an = new ArtNet();

        an.init();

        //Set Broadcast address as allowed by Art-Net revision Q
        an.setBroadCastAddress("10.255.255.255");

        try {
            //Start the server
            //Binds to port 6454
            an.start();
        } catch (Exception e) {
            System.out.println("Error starting ArtNet Server!");
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
