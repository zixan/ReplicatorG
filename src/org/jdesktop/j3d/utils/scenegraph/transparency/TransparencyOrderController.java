/*
 * $RCSfile: TransparencyOrderController.java,v $
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
 * $Date: 2007/02/09 17:17:01 $
 * $State: Exp $
 */

package org.jdesktop.j3d.utils.scenegraph.transparency;

import com.sun.j3d.utils.scenegraph.transparency.TransparencySortController;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GraphStructureChangeListener;
import javax.media.j3d.Group;
import javax.media.j3d.Locale;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.SharedGroup;
import javax.media.j3d.View;
import javax.media.j3d.VirtualUniverse;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;
import org.jdesktop.j3d.utils.scenegraph.traverser.ProcessNodeInterface;
import org.jdesktop.j3d.utils.scenegraph.traverser.TreeScan;

/**
 * Provides the mechanisms required to support the TransparencyOrderedGroup
 * node. This class must be instantiated and attached to the scene graph
 * for the TransparencyOrderedGroup to function. This node can be attached 
 * anywhere in the graph as long it remains live. No children need to be added
 * to this node.
 * <br><br>
 * The node could be attached thus
 * <br>
 *      locale.addBranchGraph(new TransparencyOrderController(view));
 *<br><br>
 * Note : this feature is only supported for a single view at the moment.
 *
 * @param view The view on which Transparency sorting will take place.
 *
 * @author paulby
 */
public class TransparencyOrderController extends BranchGroup {
    
    private static TransparencyOrderController controller=null;
    private HashMap<Shape3D, Shape3DRecord> shapeMap = new HashMap();
    
    /**
     * Create the TransparencyOrderedController
     *
     * The TransparencySortingPolicy of view will be set to TRANSPARENCY_SORT_GEOMETRY
     */
    public TransparencyOrderController(View view) {
        if (controller!=null)
            throw new RuntimeException("TransparencyOrderering is only supported for a single view at this time");
        controller = this;
        view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
        TransparencySortController.setComparator(view, new OrderingComparator());
        this.addChild(new TogBehavior());
    }
    
    static TransparencyOrderController getController() {
        return controller;
    }
    
    /**
     * The tog child index for the graph that contains shape has changed. Update
     * the internal structure to reflect this.
     */
    void adjustChildIndex(Shape3D shape3D, int indexAdjustment, TransparencyOrderedGroup tog) {
        Shape3DRecord record = shapeMap.get(shape3D);
        boolean done = false;
        
        for(int i=0; i<record.togs.length && !done; i++) {
            //System.out.println("record.togs "+record.togs[i]);
            if (record.togs[i]==tog) {
                record.childIndicies[i] += indexAdjustment;
                //System.out.println("Adjust child index of "+shape3D+" to "+record.childIndicies[i]);
                done = true;
            }
        }
    }
    
    /**
     * The comparitor used to determine the rendering order of two shapes.
     *
     * Determine the order in which s1 and s2 should be rendered
     *
     * @ returns -1 if s2 should be rendered first, 1 if s1 should be rendered first and
     * 0 if the shapes share the same TransparencyOrderedGroup or don't have a 
     * common TransparencyOrderedGroup parent
     */
    int compare( Shape3D s1, Shape3D s2 ) {
        Shape3DRecord r1 = shapeMap.get(s1);
        Shape3DRecord r2 = shapeMap.get(s2);
        
        TransparencyOrderedGroup tog = null;
        int i;
        
        if (r1==null || r2==null) {
            System.err.println("SEVERE : Unknown Shape in TransparencyOrderController.compare "+s1+"   "+s2);
            return 0;
        }
        
        // Find common TOG (if any)
        for(i = Math.min(r1.togs.length, r2.togs.length)-1; i>=0 && tog==null; i--) {
            //System.out.println(i+" "+r1.togs[i]+" "+r2.togs[i]);
            if (r1.togs[i] == r2.togs[i]) {
                tog = r1.togs[i];
            }
        }
        
        //System.out.println("Here "+s1.getName()+" "+s2.getName()+"  "+r1.togs.length+" "+r2.togs.length+" tog "+tog);

        if (tog==null)
            return 0;
        
        return tog.getRenderingOrderForChild(r2.childIndicies[i+1]) - tog.getRenderingOrderForChild(r1.childIndicies[i+1]);
    }
    
    class Shape3DRecord {
        Shape3D shape;
        TransparencyOrderedGroup[] togs;  // Togs between root and this shape
        int[] childIndicies;     // Index in Tog of the graph containing this shape 
        
        public Shape3DRecord(Shape3D shape, Object parent) {
            this.shape = shape;
            //System.out.println("Shape "+shape);
            findTogs(shape, 0, parent);
        }
        
        /**
         * Traverse up the graph from node recording all Togs that are
         * a parent
         */
        private void findTogs(Node node, int depth, Object graphParent) {
            int nextIndex=0;
            Node parent = node.getParent();
            int childIndexInParent=-1;
            
            if (parent==null && graphParent!=null) {
                if (graphParent instanceof Node) {
                    parent = (Node)graphParent;
                    // children are always added as the last child
                    // (because insertChild and setChild are not supported).
                    if (parent instanceof TransparencyOrderedGroup)
                        childIndexInParent = ((Group)parent).numChildren();
                }
                graphParent = null;
            } else if (parent instanceof TransparencyOrderedGroup)
                childIndexInParent = ((Group)parent).indexOfChild(node);
            
            //System.out.println("Node "+node+"   parent "+parent+"  depth "+depth);
            if (parent!=null) {
                if (parent instanceof SharedGroup) {
                    throw new RuntimeException("TransparencySort utility does not support SharedGroups");
                } else if (parent instanceof TransparencyOrderedGroup) {
                     findTogs(parent, depth+1, graphParent);
                } else
                    findTogs(parent, depth, graphParent);
            } else {
                //System.out.println("Reached Locale "+depth);
                togs = new TransparencyOrderedGroup[depth];
                childIndicies = new int[depth];
            }
            
            if (parent instanceof TransparencyOrderedGroup) {
                //System.out.println("togs["+(togs.length-depth-1)+"] = "+parent+"  childIndex "+childIndexInParent);
                togs[togs.length - depth-1] = (TransparencyOrderedGroup)parent;                
                childIndicies[togs.length-depth-1] = childIndexInParent;
           }

        }
    }
    
    /**
     * Behavior for initialisation and to add the required
     * frame delay between removeChild of the graph and the
     * actual removal of Shapes from the RenderBin
     */
    class TogBehavior extends Behavior implements GraphStructureChangeListener {
        private WakeupCondition wakeup = new WakeupOnElapsedFrames(0,false);
        private ArrayList<Shape3D> currentFrameRemoveList=new ArrayList();
        private ArrayList<Shape3D> lastFrameRemoveList=new ArrayList(1);
        
        public void initialize() {
            shapeMap.clear();
            
            // Find the universe and attach the graph change listener
            getLocale().getVirtualUniverse().addGraphStructureChangeListener(this);
            
            // Traverse the entire universe and note all Shape3Ds that are
            // currently attached
            Enumeration en = getLocale().getAllBranchGraphs();
            while(en.hasMoreElements())
                graphAdded(getLocale(), (BranchGroup)en.nextElement());
            
            setSchedulingBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
            wakeupOn(wakeup);
        }
        
        public synchronized void processStimulus(Enumeration e) {
            if (lastFrameRemoveList!=null)
                for(Shape3D s : lastFrameRemoveList)
                    shapeMap.remove(s);
            
            lastFrameRemoveList = currentFrameRemoveList;
            currentFrameRemoveList = new ArrayList();
            wakeupOn(wakeup);
        }
        
        private synchronized void graphAdded(final Object parent, BranchGroup child) {
            TreeScan.findNode( child, Shape3D.class, new ProcessNodeInterface() {
                public boolean processNode(Node node) {
                    //System.out.println("Adding Shape "+node);
                    shapeMap.put((Shape3D)node, new Shape3DRecord((Shape3D)node, parent));
                    
                    // Make sure added item is not on remove list
                    currentFrameRemoveList.remove((Shape3D)node);
                    lastFrameRemoveList.remove((Shape3D)node);

                    return true;
                }

            }, false, true);        
        }

        private synchronized void graphRemoved(BranchGroup child) {
            TreeScan.findNode( child, Shape3D.class, new ProcessNodeInterface() {
                public boolean processNode(Node node) {
                    Object ret = currentFrameRemoveList.add((Shape3D)node);

                    //System.out.println("Removed "+node);
                    if (ret==null)
                        throw new RuntimeException("Unknown Shape3D found during graphRemoval");

                    return true;
                }

            }, false, true); 
        }

        public void branchGroupAdded(java.lang.Object parent, BranchGroup child) {
            graphAdded(parent, child);
        }
        
        public void branchGroupMoved(java.lang.Object oldParent, java.lang.Object newParent, BranchGroup child) {
            // Add will replace existing entry
            graphAdded(newParent, child);
        }
        
        public void branchGroupRemoved(java.lang.Object parent, BranchGroup child) {
            graphRemoved(child);
        }
    }
}
