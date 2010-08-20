/*
 * $RCSfile: ShowBoundsBehavior.java,v $
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
 * $Revision: 1.3 $
 * $Date: 2007/02/09 17:17:03 $
 * $State: Exp $
 */

package org.jdesktop.j3d.utils.scenegraph.visualtools;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Box;


/**
  * This behavior will display the bounds (using getBounds) on each
  * Java3D Node registered with it.
  */
public class ShowBoundsBehavior extends Behavior {

  public final static Integer NODE_BOUNDS = new Integer(1);
  public final static Integer SCHEDULING_BOUNDS = new Integer(2);
  public final static Integer INFLUENCING_BOUNDS = new Integer(3);
  
    WakeupOnElapsedFrames frameCondition;
    WakeupOnBehaviorPost waitCondition;
    WakeupCondition currentCondition;
    ArrayList nodes = new ArrayList();
    ArrayList bounds = new ArrayList();
    ArrayList transforms = new ArrayList();
    ArrayList boundsType = new ArrayList();
    Switch root = new Switch();
    boolean newNode = false;

    TransformGroup testtg;
    Sphere tests;
    Shape3D shp;
    
    private Transform3D tmpTransform = new Transform3D();

    public ShowBoundsBehavior(BranchGroup parent) {
        super();
	root.setCapability( Switch.ALLOW_CHILDREN_READ );
	root.setCapability( Switch.ALLOW_CHILDREN_WRITE );
	root.setCapability( Switch.ALLOW_CHILDREN_EXTEND );
	root.setWhichChild( Switch.CHILD_ALL );
	parent.addChild( root );
        parent.addChild( this );
        
        this.setSchedulingBounds( new BoundingSphere( new Point3d(), 
                                                      Double.POSITIVE_INFINITY ));

	frameCondition = new WakeupOnElapsedFrames( 0, true );
        waitCondition = new WakeupOnBehaviorPost( this, 1 );
        currentCondition = waitCondition;
    }

    /**
      * Add node to the list of nodes which will have their bounds displayed
      * This method sets the ALLOW_BOUNDS_READ capability bit on node
      */
    public void showBounds( Node node ) {
	node.setCapability( Node.ALLOW_BOUNDS_READ );
	node.setCapability( Node.ALLOW_LOCAL_TO_VWORLD_READ );
        if (node instanceof TransformGroup)
          node.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
	nodes.add(node);
	bounds.add( node.getBounds() );
        boundsType.add( NODE_BOUNDS );
	root.addChild( drawBounds( node, node.getBounds(), -1 ));
	newNode = true;
        
        currentCondition = frameCondition;
        postId( 1 );
    }
    
    public void showSchedulingBounds( Node node ) {
      Bounds schedBounds=null;
      
      if (node instanceof Behavior ||
          node instanceof Sound ) {
            
        if (node instanceof Sound ) {
          node.setCapability( Sound.ALLOW_SCHEDULING_BOUNDS_READ );
          schedBounds = ((Sound)node).getSchedulingBounds();
        } else if (node instanceof Behavior ) {
          //node.setCapability( Behavior.ALLOW_SCHEDULING_BOUNDS_READ );
          schedBounds = ((Behavior)node).getSchedulingBounds();
        }
        
        node.setCapability( Node.ALLOW_LOCAL_TO_VWORLD_READ );
        nodes.add( node );
        bounds.add( schedBounds );
        boundsType.add( SCHEDULING_BOUNDS );
        root.addChild( drawBounds( node, schedBounds, -1 ));
        newNode = true;
        
        currentCondition = frameCondition;
        postId( 1 );
        
      } else
        throw new RuntimeException( node+" not supported in showSchedulingBounds");
       
          
    }
    
    public void showInfluencingBounds( Node node ) {
      if (node instanceof Light ||
          node instanceof Fog ) {
            
            throw new RuntimeException("Not Implemented");
      } else
        throw new RuntimeException( node+" not supported in showInfluencingBounds" );
    }
    
    /**
      * Remove node from the bounds being displayed
      */
    public void hideBounds( Node node, Integer type ) {
      int i=-1;
      
      for(int j=0; j<nodes.size() && i==-1; j++)
        if (nodes.get(j)==node && boundsType.get(j)==type)
          i=j;
      
      if (i==-1) {
        System.out.println("WARNING - error removing bounds");
        return;
      }
      
      nodes.remove(i);
      bounds.remove(i);
      boundsType.remove(i);
      root.removeChild(i);
    }
    
    /**
      * Stop displaying and remove all references to existing bounds
      */
    public void removeAllBounds() {
      nodes.clear();
      bounds.clear();
      while(root.numChildren()>0)
        root.removeChild(0);
    }

    public void initialize() {

	wakeupOn( currentCondition );
    }

    public void processStimulus( Enumeration criteria ) {
	Bounds bnds;
	Node node;
        Integer type;
	Transform3D nodeTrans = new Transform3D();
	Transform3D local2vw = new Transform3D();
	TransformGroup transform;
        Bounds newBounds=null;
        
	for(int i=0; i<nodes.size(); i++) {
	    node = (Node)nodes.get(i);
	    bnds = (Bounds)bounds.get(i);
            type = (Integer)boundsType.get(i);
            
            if (type==NODE_BOUNDS)
              newBounds = node.getBounds();
            else if (type==SCHEDULING_BOUNDS) {
              if (node instanceof Sound)
                newBounds = ((Sound)node).getSchedulingBounds();
              else if (node instanceof Behavior)
                newBounds = ((Behavior)node).getSchedulingBounds();
            }

	    if (bnds instanceof BoundingSphere && node.isLive() ) {
		if ( boundsNotEqual( bnds, newBounds ) || newNode ) {
		    root.setChild( drawBounds( node, newBounds,i) , i );
		    bounds.set(i, newBounds );
		} else {
		    node.getLocalToVworld( local2vw );
		    if (node instanceof TransformGroup) {
			// Apply the transform of the current node to the
			// local2vw so we get the bounds of this transform
			// not the node above.
			((TransformGroup)node).getTransform(nodeTrans);
			local2vw.mul( nodeTrans );
		    }
		    setTransform( i, local2vw);
		}
	    } else if (bnds instanceof BoundingBox && node.isLive() ) {
		if ( boundsNotEqual( bnds, newBounds ) ) {
		    root.setChild( drawBounds( node, newBounds,i) , i );
		    bounds.set(i, newBounds );
		} else {
		    node.getLocalToVworld( local2vw );
		    if (node instanceof TransformGroup) {
			// Apply the transform of the current node to the
			// local2vw so we get the bounds of this transform
			// not the node above.
			((TransformGroup)node).getTransform(nodeTrans);
			local2vw.mul( nodeTrans );
		    }
		    setTransform(i, local2vw);
		}
	    } else if ( node.isLive() ) {
		throw new RuntimeException("showBounds does not handle this type of bounds"+bnds.getClass().getName() );
	    }
	    // TODO: Handle polytopes
	}
	newNode = false;
        
        if (nodes.size()>0)
	  currentCondition = frameCondition;
        else
          currentCondition = waitCondition;
        
        wakeupOn( currentCondition );
    }
    
    /**
      * Set the transformGroup and <code>index</code> to newTransform.
      * Checks if the transform has changed so we don't make unecesary
      * updates.
      */
    private void setTransform( int index, Transform3D newTransform ) {
        ((TransformGroup)transforms.get(index)).getTransform( tmpTransform );
        
        if ( !tmpTransform.equals(newTransform) )
          ((TransformGroup)transforms.get(index)).setTransform( newTransform );
    }

    private BranchGroup drawBounds( Node node, Bounds bounds, int i ) {
	BranchGroup ret = new BranchGroup();
	Appearance appearance = new Appearance();
	PolygonAttributes poly = new PolygonAttributes();
	poly.setPolygonMode( PolygonAttributes.POLYGON_LINE );
	appearance.setPolygonAttributes( poly );
	ret.setCapability( BranchGroup.ALLOW_DETACH );
	Transform3D nodeTrans = new Transform3D();

	Primitive primitive = null;
	Point3d c = new Point3d();

	if (bounds instanceof BoundingSphere) {
	    ((BoundingSphere)bounds).getCenter(c);
	    double radius = ((BoundingSphere)bounds).getRadius();

	    if (radius > 0.0) {	// Radius = -1 before renderer is running
	        primitive = new Sphere( (float)radius, 
						Sphere.GEOMETRY_NOT_SHARED,
						10,
						appearance );
	    } else
		return ret;

	} else if (bounds instanceof BoundingBox) {
	    Point3d upper = new Point3d();
	    Point3d lower = new Point3d();
	    ((BoundingBox)bounds).getUpper( upper );
	    ((BoundingBox)bounds).getLower( lower );
	    Vector3f size = new Vector3f();
	    size.x = (float)(upper.x - lower.x);
	    size.y = (float)(upper.y - lower.y);
	    size.z = (float)(upper.z - lower.z);

	    c.x = lower.x + size.x/2f;
	    c.y = lower.y + size.y/2f;
	    c.z = lower.z + size.z/2f;

	    // A box of size 2 has vertex length of 4 so we have
	    // to /2
	    primitive = new Box( size.x/2f, size.y/2f, size.z/2f, appearance );
	} else
	    throw new RuntimeException("Not implemented yet");

	Transform3D trans = new Transform3D();
	trans.set( new Vector3d( c.x, c.y, c.z ) );

	Transform3D local2vw = new Transform3D();
	if (node.isLive())
	    node.getLocalToVworld( local2vw );

	if (node instanceof TransformGroup) {
	    // Apply the transform of the current node to the
	    // local2vw so we get the bounds of this transform
	    // not the node above.
	    ((TransformGroup)node).getTransform(nodeTrans);
	    local2vw.mul( nodeTrans );
	}

	TransformGroup tg = new TransformGroup( trans );
	TransformGroup tgvw = new TransformGroup( local2vw );
	tgvw.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
	tgvw.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );

	if (i>transforms.size()-1)
	    transforms.add( tgvw );
        else
	    transforms.set(i, tgvw );

        tg.addChild( primitive );
	tgvw.addChild( tg );
        ret.addChild( tgvw );

        return ret;
    }

    private boolean boundsNotEqual( Bounds b1, Bounds b2 ) {
	if ( b1 instanceof BoundingBox && b2 instanceof BoundingBox ) {
	    Point3d p1u = new Point3d();
	    Point3d p1l = new Point3d();
	    Point3d p2u = new Point3d();
	    Point3d p2l = new Point3d();

	    ((BoundingBox)b1).getUpper( p1u );
	    ((BoundingBox)b1).getLower( p1l );
	    ((BoundingBox)b2).getUpper( p2u );
	    ((BoundingBox)b2).getLower( p2l );

	    if (p1u.equals(p2u) && p1l.equals(p2l))
		return false;		// We are NotEqual
	    else
		return true;
	} else if (b1 instanceof BoundingPolytope && 
				      b2 instanceof BoundingPolytope ) {
	     throw new RuntimeException("Not Implemented yet");
	} else if (b1 instanceof BoundingSphere && 
				      b2 instanceof BoundingSphere ) {
	    Point3d b1Center = new Point3d();
	    Point3d b2Center = new Point3d();
	    double b1radius;
	    double b2radius;

	    ((BoundingSphere)b1).getCenter(b1Center);
	    ((BoundingSphere)b2).getCenter(b2Center);
	    b1radius = ((BoundingSphere)b1).getRadius();
	    b2radius = ((BoundingSphere)b2).getRadius();

	    if (b1Center.equals(b2Center) && b1radius==b2radius)
		return false;		// NotEqual is false ie we are equal
	    else
		return true;
	} else
	    return true;
    }
}
