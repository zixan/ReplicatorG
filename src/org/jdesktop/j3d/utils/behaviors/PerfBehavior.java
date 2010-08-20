/*
 *  $Header: /cvs/j3d-contrib-utils/src/classes/org/jdesktop/j3d/utils/behaviors/PerfBehavior.java,v 1.3 2007/08/10 00:18:36 paulby Exp $
 *
 *                         Sun Public License Notice
 *
 *  The contents of this file are subject to the Sun Public License Version
 *  1.0 (the "License"). You may not use this file except in compliance with
 *  the License. A copy of the License is available at http://www.sun.com/
 *  
 *  The Original Code is Java 3D(tm) Fly Through.
 *  The Initial Developer of the Original Code is Paul Byrne.
 *  Portions created by Paul Byrne are Copyright (C) 2002.
 *  All Rights Reserved.
 *  
 *  Contributor(s): Paul Byrne.
 *  
 **/
package org.jdesktop.j3d.utils.behaviors;

import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * A performance measurment behavior. The behavior automatically calibrates
 * itself so that the work in measuring performance does not itself impact 
 * performance. 
 *
 * Performance information will be passed to a registered PerfBehaviorListener,
 * if there is no listener then the frames per second will be printed to 
 * System.out
 *
 * The org.jdesktop.j3dfly.utils.gui.StatsDialog class  provides a 
 * GUI Dialog which uses this behavior to display performance stats.
 *
 * @author Chien Yang, Paul Byrne
 * @version	1.9, 01/18/02
 */
public class PerfBehavior extends Behavior {
    WakeupOnElapsedFrames FPSwakeup = new WakeupOnElapsedFrames(0);
    private int numFramesToDiscard = 8;

    private boolean doCalibration = true;
    private int discardedFrames = 0;
    private int numframes = 0;
    private int maxframes = 1;
    private long currtime = 0;
    private long lasttime = 0;
    private long deltatime;
    private boolean finiteLoop = false;
    private int loop = 0;
    private int loopCount = 6;
    private int testDuration = 1000;
    
    private ArrayList<PerfBehaviorListener> listeners;
    
    /**
     * Construct the Behavior
     * The Scheduling bounds are set to inifinity
     */
    public PerfBehavior() {
 	setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),Double.POSITIVE_INFINITY));
	setEnable(true);
    }
    
    /**
     *  Print the fps results to System.out
     */
    public void printToSystemOut() {
        addPerfBehaviorListener( new PerfBehaviorListener() {

            public void updatePerformanceFigures(float framesPerSecond) {
                System.out.println("fps "+framesPerSecond);
            }
            
        });
    }
    
    public void calibrate() {
        calibrate(8, 1000);
    }
    
    /**
     * Calibrate the behavior
     */
    public void calibrate(int framesToDiscard, int testDuration) {
        doCalibration = true;
        discardedFrames = 0;
        numframes=0;
        maxframes=1;
        currtime = 0;
        lasttime = 0;
        loop = 0;
        loopCount = 6;
        this.testDuration = testDuration;
        this.numFramesToDiscard = framesToDiscard;
    }
    
    /**
     * Called by Java3D to initialize the behavior
     */
    public void initialize() {
	// Set the trigger for the interpolator
	wakeupOn(FPSwakeup);
    }
    
    /**
     * Add the listener which will be notified whenever the performance
     * stats are updated.
     *
     * Current implementation only supports a single listener
     */
    public void addPerfBehaviorListener( PerfBehaviorListener listener ) {
        if (listeners==null)
            listeners = new ArrayList<PerfBehaviorListener>();
        listeners.add(listener);
    }
    
    /**
     * Remove the PerfBehaviorListener
     */
    public void removePerfBehaviorListener( PerfBehaviorListener listener ) {
        if (listeners!=null)
            listeners.remove(listener);
    }
    
    private void notifyListeners(float fps) {
        for(PerfBehaviorListener l : listeners)
            l.updatePerformanceFigures(fps);
    }

    // Called every time the behavior is activated
    public void processStimulus(java.util.Enumeration critera) {
	// Apply Calibration Algorithm :
	// To determine maxframes to run before sampling the time
	// to determine frames per second.
	// testduration = 10000 ,To run test pass for 10 seconds
        
	if (doCalibration) { // do the calibration
	    // Always throw away the next "N" frames *after* the first, dummy
	    // call to postSwap
	    if (discardedFrames <= numFramesToDiscard) {
		if (false) {
		    if (discardedFrames == 0)
			System.out.println("postSwap: initial call discarded");
		    else
			System.out.println("postSwap: frame #" +
					   discardedFrames + " discarded");
		}

		discardedFrames += 1;
		lasttime = currentTimeMillis();
	    }
	    else {
		numframes += 1;
		//System.out.println(maxframes+" "+numframes+" "+lasttime+" ");
		if (numframes >= maxframes) {
		    currtime = currentTimeMillis();
		    deltatime = currtime - lasttime;
//		    System.out.println("deltatime = " + deltatime +
//		    		          ", numframes = " + numframes);

                    if (deltatime > testDuration && maxframes >= 8 ) {
			maxframes =
			    (int)Math.ceil((double)maxframes * 1000 /
					    (double)deltatime);
//			System.out.println("maxframes = " + maxframes);

			// reset the value for the measurement
			doCalibration = false;
			numframes = 0;
			lasttime = currentTimeMillis();
		    }
		    else {
			maxframes *= 2;
		    }
		}
	    }
	}
	else { // do the measurement
	    numframes += 1;
	    if (numframes >= maxframes) {
		currtime = currentTimeMillis();
		deltatime = currtime - lasttime;
		double fps = (double)numframes / ((double)deltatime / 1000.0d );
                //System.out.println( numframes+"  "+(deltatime/1000.0)+"  "+fps);
                if (listeners!=null) {
                    notifyListeners( (float)fps );
                }
		if (finiteLoop) {
		    loop ++;
		    if (loop >= loopCount) System.exit(0);
		}
		lasttime = currentTimeMillis();
                numframes = 0;
	    }
	}

	// Set the trigger for the interpolator
	wakeupOn(FPSwakeup);
	
    }
    
    private long currentTimeMillis() {
        return System.nanoTime()/1000000;
    }
}


