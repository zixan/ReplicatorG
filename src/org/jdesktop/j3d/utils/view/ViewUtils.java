/*
 * $RCSfile: ViewUtils.java,v $
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision: 1.1 $
 * $Date: 2007/08/15 16:25:56 $
 * $State: Exp $
 */
package org.jdesktop.j3d.utils.view;

import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.Bounds;
import javax.media.j3d.BoundingSphere;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;

/**
 * Utility functions for manipulating the View
 *
 * @author  Paul Byrne
 * @version $Id: ViewUtils.java,v 1.1 2007/08/15 16:25:56 paulby Exp $
 */
public class ViewUtils extends Object {

    public enum Axis { POSITIVE_X_AXIS,
                       POSITIVE_Y_AXIS,
                       POSITIVE_Z_AXIS,
                       NEGATIVE_X_AXIS,
                       NEGATIVE_Y_AXIS,
                       NEGATIVE_Z_AXIS };
    
        
    /**
     * Set the transform so that everything inside the Bounds is
     * visible within the view frustum. Return the views distance from
     * center of the scene after transformation
     *
     * @param tg The transform to change
     * @param sceneBounds The bounds of the scene to fit inside the view frustum
     * @param fieldOfView The fieldOfView
     * @param axis The axis along which to view the scene, this must be one of
     * POSITIVE_X_AXIS, POSITIVE_Y_AXIS, POSITIVE_Z_AXIS, NEGATIVE_X_AXIS, NEGATIVE_Y_AXIS or NEGATIVE_Z_AXIS 
     * @return The distance the eye is from the center of the scene bounds.
     */
    public static double setViewpoint( TransformGroup tg, Bounds sceneBounds,
                                double fieldOfView, Axis axis ) {
                                    
        if (sceneBounds instanceof BoundingSphere)
            return setViewpoint( tg, (BoundingSphere)sceneBounds, fieldOfView, axis );
        
        return setViewpoint( tg, new BoundingSphere(sceneBounds), fieldOfView, axis );
    }
    
    
    private static double setViewpoint( TransformGroup tg, BoundingSphere sceneBounds,
                               double fieldOfView, Axis axis ) {
        Transform3D viewTrans = new Transform3D();
        Transform3D eyeTrans = new Transform3D();
        
        // point the view at the center of the object
        Point3d center = new Point3d();
        sceneBounds.getCenter(center);
        double radius = sceneBounds.getRadius();
        Point3d eyePos = new Point3d(center);
        Vector3d up = new Vector3d();
        
        // pull the eye back far enough to see the whole object
        double eyeDist = radius / Math.tan(fieldOfView / 2.0);
        switch( axis ) {
            case POSITIVE_X_AXIS :
                eyePos.x += eyeDist;
                up.y = 1;
                break;
            case POSITIVE_Y_AXIS :
                eyePos.y += eyeDist;
                up.z = -1;
                break;
            case POSITIVE_Z_AXIS :
                eyePos.z += eyeDist;
                up.y = 1;
                break;
            case NEGATIVE_X_AXIS :
                eyePos.x -= eyeDist;
                up.y = 1;
                break;
            case NEGATIVE_Y_AXIS :
                eyePos.y -= eyeDist;
                up.z = -1;
                break;
            case NEGATIVE_Z_AXIS :
                eyePos.z -= eyeDist;
                up.y = 1;
                break;
        }
        
        viewTrans.setIdentity();
        viewTrans.lookAt( eyePos, center, up );
        viewTrans.invert();
        // set the view transform
        tg.setTransform(viewTrans);
        
        return eyeDist;
    }
    
}
