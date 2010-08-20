/*
 *  $Header: /cvs/j3d-contrib-utils/src/classes/org/jdesktop/j3d/utils/behaviors/PerfBehaviorListener.java,v 1.1 2007/04/14 16:29:51 paulby Exp $
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

/**
 * The Listener interface for PerfBehavior
 *
 * @author Paul Byrne
 * @version	1.4, 01/18/02
 */
public interface PerfBehaviorListener {
    
    /**
      * Called by PerfBehavior when new performance figures are available
      */
    public void updatePerformanceFigures( float framesPerSecond );

}
