/*
 * $RCSfile: Math3D.java,v $
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
 * $Revision: 1.6 $
 * $Date: 2007/08/28 16:42:24 $
 * $State: Exp $
 */

package org.jdesktop.j3d.utils.math;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;

/**
 * 3D Math utilities
 *
 * @author Paul
 */;
public class Math3D {
    
    /**
     * Calculates the distance of a point from a line.
     * <p><code>
     *    x1----------------------------x2 <br>
     *                  |               <br>
     *                  | distance      <br>
     *                  |               <br>
     *                 point            <br>
     * </code>
     * <p>
     * The formula is <br>
     * <code>
     *      d = |(x2-x1) x (x1-p)| <br>
     *          ------------------ <br>
     *              |x2-x1|        <br>
     * </code>
     *
     * Where p=point, lineStart=x1, lineEnd=x2
     *
     */
    public static float pointLineDistance( final Point3f lineStart, 
                                           final Point3f lineEnd, 
                                           final Point3f point ) {
        Vector3f a = new Vector3f(lineEnd);
        a.sub(lineStart);
        
        Vector3f b = new Vector3f(lineStart);
        b.sub(point);
        
        Vector3f cross = new Vector3f();
        cross.cross(a,b);
        
        return cross.length()/a.length();
    }

    /**
     * Converts the Matrix into Euler angles (roll, pitch, yaw )
     */
    public static void toEuler( Matrix3d matrix, Vector3d euler ) {
        Vector3d v3d = new Vector3d();
        
        Vector3d zAxis = new Vector3d( 0, 0, -1 );
        Vector3d yAxis = new Vector3d( 0, 1, 0 );
        Vector3d xAxis = new Vector3d( 1, 0, 0 );

        v3d.set( xAxis );
        matrix.transform( v3d );
        v3d.x = Math.abs( v3d.x );
        v3d.z = 0;
        v3d.normalize();

        euler.x = xAxis.angle( v3d );

        v3d.set( yAxis );
        matrix.transform( v3d );
        v3d.z = Math.abs( v3d.z );
        v3d.x = 0;
        v3d.normalize();

        euler.y = yAxis.angle( v3d );

        v3d.set( zAxis );
        matrix.transform( v3d );
        v3d.y = 0;
        v3d.normalize();

        euler.z = zAxis.angle( v3d );
        if (v3d.x<0)
            euler.z = 2*Math.PI-euler.z;
     }


    public static boolean epsilonEquals(float f1, float f2, float epsilon) {
        float diff;

        diff = f1 - f2;
        if ((diff < 0 ? -diff : diff) > epsilon) {
            return false;
        }
        
        return true;
    }

    public static boolean epsilonEquals(Tuple3f t1, Tuple3f t2, float epsilon) {
        if (epsilonEquals(t1.x, t2.x, epsilon) &&
            epsilonEquals(t1.y, t2.y, epsilon) &&
            epsilonEquals(t1.z, t2.z, epsilon))
            return true;
        
        return false;
    }

    public static boolean encloses(Bounds parent, Bounds child) {
        if (parent instanceof BoundingBox)
            if (child instanceof BoundingBox)
                return encloses((BoundingBox)parent, (BoundingBox)child);
            else if (child instanceof BoundingSphere)
                return encloses((BoundingBox)parent, (BoundingSphere)child);
        else if (parent instanceof BoundingSphere)
            if (child instanceof BoundingBox)
                return encloses((BoundingSphere)parent, (BoundingBox)child);
            else if (child instanceof BoundingSphere)
                return encloses((BoundingSphere)parent, (BoundingSphere)child);

        throw new UnsupportedOperationException("Unsupported bounds combination");
    }

    /**
     * Returns true if the parent bounds fully encloses the child 
     */
    public static boolean encloses(BoundingBox parent, BoundingSphere child) {
        Point3d upper = new Point3d();
        Point3d lower = new Point3d();
        Point3d center = new Point3d();
        double radius;

        parent.getUpper(upper);
        parent.getLower(lower);
        child.getCenter(center);
        radius = child.getRadius();

        if (center.x+radius > upper.x ||
            center.y+radius > upper.y ||
            center.z+radius > upper.z)
            return false;

        if (center.x-radius < lower.x ||
            center.y-radius < lower.y ||
            center.z-radius < lower.z)
            return false;

        return true;
    }

     /**
     * Returns true if the parent bounds fully encloses the child 
     */
    public static boolean encloses(BoundingBox parent, BoundingBox child) {
        Point3d pUpper = new Point3d();
        Point3d pLower = new Point3d();
        Point3d cUpper = new Point3d();
        Point3d cLower = new Point3d();

        parent.getUpper(pUpper);
        parent.getLower(pLower);
        child.getUpper(cUpper);
        child.getLower(cLower);

        if (cUpper.x > pUpper.x ||
            cUpper.y > pUpper.y ||
            cUpper.z > pUpper.z)
                return false;

        if (cLower.x < pLower.x ||
            cLower.y < pLower.y ||
            cLower.z < pLower.z)
                return false;
        
        return true;
    }
    /**
     * Returns true if the parent bounds fully encloses the child 
     */
    public static boolean encloses(BoundingSphere parent, BoundingBox child) {
        // if the distance from the center of the sphere to any corner of
        // the box is greater than the sphere radius return false

        Point3d lower = new Point3d();
        Point3d upper = new Point3d();

        Point3d parentCenter = new Point3d();

        child.getLower(lower);
        child.getUpper(upper);

        parent.getCenter(parentCenter);

        double xDim = upper.x - lower.x;
        double yDim = upper.y - lower.y;

        double radiusSquared = Math.pow(parent.getRadius(), 2);

        Vector3d tmp = new Vector3d();

        tmp.set(lower);
        tmp.sub(parentCenter);
        if (tmp.lengthSquared()>radiusSquared)
            return false;
        tmp.set(lower.x+xDim, lower.y, lower.z);
        tmp.sub(parentCenter);
        if (tmp.lengthSquared()>radiusSquared)
            return false;
        tmp.set(lower.x, lower.y+yDim, lower.z);
        tmp.sub(parentCenter);
        if (tmp.lengthSquared()>radiusSquared)
            return false;
        tmp.set(lower.x+xDim, lower.y+yDim, lower.z);
        tmp.sub(parentCenter);
        if (tmp.lengthSquared()>radiusSquared)
            return false;

        tmp.set(upper);
        tmp.sub(parentCenter);
        if (tmp.lengthSquared()>radiusSquared)
            return false;
        tmp.set(upper.x-xDim, upper.y, upper.z);
        tmp.sub(parentCenter);
        if (tmp.lengthSquared()>radiusSquared)
            return false;
        tmp.set(upper.x, upper.y-yDim, upper.z);
        tmp.sub(parentCenter);
        if (tmp.lengthSquared()>radiusSquared)
            return false;
        tmp.set(upper.x-xDim, upper.y-yDim, upper.z);
        tmp.sub(parentCenter);
        if (tmp.lengthSquared()>radiusSquared)
            return false;

        return true;
    }

    /**
     * Returns true if the parent bounds fully encloses the child 
     */
    public static boolean encloses(BoundingSphere parent, BoundingSphere child) {
        Point3d childCenter = new Point3d();
        Point3d parentCenter = new Point3d();
        child.getCenter(childCenter);
        parent.getCenter(parentCenter);
        double childR = child.getRadius();
        double parentR = parent.getRadius();

        if (childCenter.x+childR > parentCenter.x+parentR ||
            childCenter.y+childR > parentCenter.y+parentR ||
            childCenter.z+childR > parentCenter.z+parentR)
            return false;
        
        if (childCenter.x-childR < parentCenter.x-parentR ||
            childCenter.y-childR < parentCenter.y-parentR ||
            childCenter.z-childR < parentCenter.z-parentR)
            return false;
            
        return true;
    }

}
