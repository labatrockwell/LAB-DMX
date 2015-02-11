package com.rockwell.dmx;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.ArrayUtils;

import artnet4j.ArtNet;
import artnet4j.packets.ArtDmxPacket;

/**
 * A universe of DMX Devices
 */
public class DmxUniverse
{
    // The parent network
    ArtNet an;
    int id;
    // Devices in this universe
    ArrayList<DmxDevice> dmxDevices = new ArrayList<DmxDevice>();
    // Fixed timer to check if universe refresh is needed
    Timer refreshTimer = new Timer();
    // refresh universe at 60fps
    long refreshRate = 16;
    int maxChannels = 512;

    /** 
     * Initialize universe
     * @param an DMX network
     * @param universeId ID on that network
     */
    public DmxUniverse(ArtNet an, int universeId) {
        this.an = an;
        id = universeId;
    }
    
    public void begin() {
        refreshTimer.schedule(new UniverseRefreshTask(), 0, refreshRate);
    }

    /**
     * Set one device to a color
     * @param deviceId the device to set
     * @param color the color to set
     */
    public void setOne(int deviceId, DmxChannelColor color) {
        dmxDevices.get(deviceId).setColor(color);
    }

    /**
     * Set the whole universe to a color
     * @param color the color to set
     */
    public void setAll(DmxChannelColor color) {
        for (DmxDevice device : dmxDevices) {
            device.setColor(color);
        }
    }
    
    /**
     * Start a transition based color on one device
     * @param deviceId the device to change
     * @param color the color to change to
     * @param steps the number of steps to take
     */
    public void fadeOne(int deviceId, DmxChannelColor color, int steps) {
        dmxDevices.get(deviceId).fadeColor(color, steps);
    }

    /**
     * Start a transition of the whole universe to a new color
     * @param color the color to target
     * @param steps the number of steps to take
     */
    public void fadeAll(DmxChannelColor color, int steps) {
        for (DmxDevice device : dmxDevices) {
            device.fadeColor(color, steps);
        }
    }
    
    /**
     * Add a device to this universe
     */
    public void addDevice(int channelCount, int colorCount) {
        dmxDevices.add(new DmxDevice(an, id, channelCount, colorCount));
    }
    
    public void startIndependentDeviceAmbients(int fadeSpeed) {
        for (DmxDevice d : dmxDevices) {
            d.startAmbientMode(fadeSpeed);
        }
    }
    
    public void stopIndependentDeviceAmbients() {
        for (DmxDevice d : dmxDevices) {
            d.stopAmbientMode();
        }
    }

    /**
     * Get all bytes and generate refresh packet
     */
    public int refreshUniverse() {
        //Create a DMX control packet
        ArtDmxPacket adp = new ArtDmxPacket();
        
        byte[] universeBytes = new byte[0];
        
        // Add all byte arrays from the devices
        for (DmxDevice device : dmxDevices) {
            universeBytes = ArrayUtils.addAll(universeBytes, device.getChannelBytes());
        }
        
        //pad with zeros to force everything else off
        if (universeBytes.length < maxChannels){
	        byte[] temp = new byte[maxChannels - universeBytes.length];
	        for(int i = 0; i < temp.length; i++){
	        	temp[i] = (byte)0;
	        }
	        universeBytes = ArrayUtils.addAll(universeBytes, temp);
        }
        
        // Set the Universe
        adp.setDMX(universeBytes, universeBytes.length);
        
        adp.setUniverse(0, id);
        an.broadcastPacket(adp);
        return universeBytes.length;
    }
    
    /**
     * Task to check for universe refresh
     */
    public class UniverseRefreshTask extends TimerTask {
        public void run() {
            for (DmxDevice d : dmxDevices) {
                d.stepFade();
            }
            
            refreshUniverse();
        }
    }
}
