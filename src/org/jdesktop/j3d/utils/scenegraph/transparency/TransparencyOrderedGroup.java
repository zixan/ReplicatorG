/*
 * $RCSfile: TransparencyOrderedGroup.java,v $
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
 * $Revision: 1.2 $
 * $Date: 2007/02/09 17:17:02 $
 * $State: Exp $
 */

package org.jdesktop.j3d.utils.scenegraph.transparency;

import java.util.Arrays;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import org.jdesktop.j3d.utils.scenegraph.traverser.ProcessNodeInterface;
import org.jdesktop.j3d.utils.scenegraph.traverser.TreeScan;

/**
 * Provides the user control over the rendering order of transparent objects
 * in the subgraphs of this Group. 
 * <br>
 * Transparent shapes in the 1st child of this group will be rendered before
 * (ie behind) transparent shapes in the 2nd child and so on.
 *<br>
 * Transparent shapes in the same child will be ordered using the standard test
 * of distance to view.
 *<br>
 * TransparencyOrderedGroups can not be included under a SharedGroup. If they
 * are a RuntimeException will be thrown when the graph is made live.
 *<br>
 * Even though this class is a subclass of TransformGroup it should be treated
 * as a Group. The subclassing TransformGroup is an implementation detail,
 * all of the TransformGroup specific methods will throw RuntimeExceptions.
 *
 * @author paulby
 */
public class TransparencyOrderedGroup extends javax.media.j3d.TransformGroup {
    
    private int[] childIndexOrder = null;
    private boolean checkArr[] = null;
    private final static Transform3D t3d = new Transform3D();
    
    /** Creates a new instance of TransparencyOrderedGroup */
    public TransparencyOrderedGroup() {
        super(new Transform3D());
        setCapability(Group.ALLOW_CHILDREN_READ);
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    }
    
    /**
     * Sets the childIndexOrder array.  If the specified array is
     * null, this node's childIndexOrder array is set to null.  Its
     * children will then be rendered in increasing index order.  If
     * the specified array is not null, the entire array is copied to
     * this node's childIndexOrder array.  In this case, the length of
     * the array must be equal to the number of children in this
     * group, and every entry in the array must have a unique value
     * between 0 and <code>numChildren-1</code> (i.e., there must be
     * no duplicate values and no missing indices).
     *
     * @param childIndexOrder the array that is copied into this
     * node's child index order array; this can be null
     *
     * @exception IllegalArgumentException if the specified array is
     * non-null and any of the following are true:
     * <ul>
     * <li><code>childIndexOrder.length != numChildren</code>;</li>
     * <li><code>childIndexOrder[</code><i>i</i><code>] < 0</code>,
     * for <i>i</i> in <code>[0, numChildren-1]</code>;</li>
     * <li><code>childIndexOrder[</code><i>i</i><code>] >= numChildren</code>,
     * for <i>i</i> in <code>[0, numChildren-1]</code>;</li>
     * <li><code>childIndexOrder[</code><i>i</i><code>] ==
     * childIndexOrder[</code><i>j</i><code>]</code>,
     * for <i>i</i>,<i>j</i> in <code>[0, numChildren-1]</code>,
     * <i>i</i> <code>!=</code> <i>j</i>;</li>
     * </ul>
     *
     */
    public void setChildIndexOrder(int[] childIndexOrder) {
	verifyChildIndexOrderArray(childIndexOrder, 0);
        if (childIndexOrder==null) {
            this.childIndexOrder = null;
            return;
        }

	if (this.childIndexOrder==null ||
               childIndexOrder.length != this.childIndexOrder.length)
            this.childIndexOrder = new int[childIndexOrder.length];
            
        System.arraycopy(childIndexOrder, 0, this.childIndexOrder, 0, childIndexOrder.length);       
        super.setTransform(t3d);
    }


    /**
     * Retrieves the current childIndexOrder array.
     *
     * @return a copy of this node's childIndexOrder array; this
     * can be null.
     *
     */
    public int[] getChildIndexOrder() {
        if (childIndexOrder==null)
            return null;
        
        int[] ret = new int[childIndexOrder.length];
        
        System.arraycopy(childIndexOrder, 0, ret, 0, ret.length);
        
        return ret;
	
    }
    
    /**
     * Given the index of the child return the rendering order for that child
     */
    int getRenderingOrderForChild(int childIndex) {
        if (childIndexOrder==null)
            return childIndex;
        
        int ret = -1;
        for(int i=0; i<childIndexOrder.length && ret==-1; i++) {
            if (childIndexOrder[i]==childIndex)
                ret = i;
        }
        
        if (ret==-1)
            throw new RuntimeException("Child "+childIndex+" not found in childIndexOrder array");
        
        return ret;
    }
    
    /**
     * Appends the specified child node to this group node's list of children.
     *
     * <p>
     * If the current child index order array is non-null, the array
     * is increased in size by one element, and a new element
     * containing the index of the new child is added to the end of
     * the array.  Thus, this new child will be rendered last.
     *
     * @param child the child to add to this node's list of children
     * @exception CapabilityNotSetException if the appropriate capability is
     * not set and this group node is part of live or compiled scene graph
     * @exception RestrictedAccessException if this group node is part
     * of live
     * or compiled scene graph and the child node being added is not
     * a BranchGroup node
     * @exception MultipleParentException if <code>child</code> has already
     * been added as a child of another group node.
     */
    public void addChild(Node child) {
        super.addChild(child);
        
        if (childIndexOrder!=null) {
            int[] tmp = new int[childIndexOrder.length+1];
            System.arraycopy(childIndexOrder, 0, tmp, 0, childIndexOrder.length);
            tmp[tmp.length-1] = tmp.length-1;
            childIndexOrder = tmp;
        }
    }
    
    /**
     * Appends the specified child node to this group node's list of
     * children, and sets the child index order array to the specified
     * array.  If the specified array is null, this node's
     * childIndexOrder array is set to null.  Its children will then
     * be rendered in increasing index order.  If the specified array
     * is not null, the entire array is copied to this node's
     * childIndexOrder array.  In this case, the length of the array
     * must be equal to the number of children in this group after the
     * new child has been added, and every entry in the array must
     * have a unique value between 0 and <code>numChildren-1</code>
     * (i.e., there must be no duplicate values and no missing
     * indices).
     *
     * @param child the child to add to this node's list of children
     *
     * @param childIndexOrder the array that is copied into this
     * node's child index order array; this can be null
     *
     * @exception CapabilityNotSetException if the appropriate capability is
     * not set and this group node is part of live or compiled scene graph
     *
     * @exception RestrictedAccessException if this group node is part
     * of live
     * or compiled scene graph and the child node being added is not
     * a BranchGroup node
     *
     * @exception MultipleParentException if <code>child</code> has already
     * been added as a child of another group node.
     *
     * @exception IllegalArgumentException if the specified array is
     * non-null and any of the following are true:
     * <ul>
     * <li><code>childIndexOrder.length != numChildren</code>;</li>
     * <li><code>childIndexOrder[</code><i>i</i><code>] < 0</code>,
     * for <i>i</i> in <code>[0, numChildren-1]</code>;</li>
     * <li><code>childIndexOrder[</code><i>i</i><code>] >= numChildren</code>,
     * for <i>i</i> in <code>[0, numChildren-1]</code>;</li>
     * <li><code>childIndexOrder[</code><i>i</i><code>] ==
     * childIndexOrder[</code><i>j</i><code>]</code>,
     * for <i>i</i>,<i>j</i> in <code>[0, numChildren-1]</code>,
     * <i>i</i> <code>!=</code> <i>j</i>;</li>
     * </ul>
     */
    public void addChild(Node child, int[] childIndexOrder) {
	
	verifyChildIndexOrderArray(childIndexOrder, 1);
        
        this.childIndexOrder = new int[childIndexOrder.length];
        System.arraycopy(childIndexOrder, 0, this.childIndexOrder, 0, childIndexOrder.length);

	super.addChild(child);
    }
    
    /**
     * Inserts the specified child node in this group node's list of
     * children at the specified index.
     * This method is only supported when the child index order array
     * is null.
     *
     * @param child the new child
     * @param index at which location to insert. The <code>index</code>
     * must be a value
     * greater than or equal to 0 and less than or equal to
     * <code>numChildren()</code>.
     * @exception CapabilityNotSetException if the appropriate capability is
     * not set and this group node is part of live or compiled scene graph
     * @exception RestrictedAccessException if this group node is part of
     * live
     * or compiled scene graph and the child node being inserted is not
     * a BranchGroup node
     * @exception MultipleParentException if <code>child</code> has already
     * been added as a child of another group node.
     * @exception IndexOutOfBoundsException if <code>index</code> is invalid.
     * @exception IllegalStateException if the childIndexOrder array is
     * not null.
     *
     */
    public void insertChild(Node child, int index) {
	if (childIndexOrder != null) {
	    throw new IllegalStateException("insertChild illegal when childIndexOrder != null");
	}
        super.insertChild(child, index);
    }
    
    /**
     * Replaces the node at the specified index with the child node provided.
     * This method is only supported when the child index order array
     * is null.
     *
     * @param child the new child
     * @param index of node to replace. The <code>index</code>
     * must be a value
     * greater than or equal to 0 and less than or equal to
     * <code>numChildren()</code>.
     * @exception CapabilityNotSetException if the appropriate capability is
     * not set and this group node is part of live or compiled scene graph
     * @exception RestrictedAccessException if this group node is part of
     * live
     * or compiled scene graph and the child node being inserted is not
     * a BranchGroup node
     * @exception MultipleParentException if <code>child</code> has already
     * been added as a child of another group node.
     * @exception IndexOutOfBoundsException if <code>index</code> is invalid.
     * @exception IllegalStateException if the childIndexOrder array is
     * not null.
     *
     */    
    public void setChild(Node child, int index) {
	if (childIndexOrder != null) {
	    throw new IllegalStateException("setChild illegal when childIndexOrder != null");
	}
        super.setChild(child, index);        
    }

    /**
     * Removes the specified child node from this group node's
     * list of children.
     * If the specified object is not in the list, the list is not modified.
     *
     * <p>
     * If the current child index order array is non-null, the element
     * containing the removed child's index will be removed from the
     * child index order array, and the array will be reduced in size
     * by one element.  If the child removed was not the last child in
     * the Group, the values of the child index order array will be
     * updated to reflect the indices that were renumbered.  More
     * formally, each child whose index in the Group node was greater
     * than the removed element (before removal) will have its index
     * decremented by one.
     *
     * @param child the child node to be removed.
     * @exception CapabilityNotSetException if appropriate capability is
     * not set and this object is part of live or compiled scene graph
     * @exception RestrictedAccessException if this group node is part of
     * live or compiled scene graph and the child node being removed is not
     * a BranchGroup node
     */   
    public void removeChild(Node child) {
        int index = indexOfChild(child);
        super.removeChild(child);
        removeChildFromIndexOrder(index);
    }
    
    /**
     * Removes the child node at the specified index from this group node's
     * list of children.
     *
     * <p>
     * If the current child index order array is non-null, the element
     * containing the removed child's index will be removed from the
     * child index order array, and the array will be reduced in size
     * by one element.  If the child removed was not the last child in
     * the Group, the values of the child index order array will be
     * updated to reflect the indices that were renumbered.  More
     * formally, each child whose index in the Group node was greater
     * than the removed element (before removal) will have its index
     * decremented by one.
     *
     * @param index which child to remove.  The <code>index</code>
     * must be a value
     * greater than or equal to 0 and less than <code>numChildren()</code>.
     * @exception CapabilityNotSetException if the appropriate capability is
     * not set and this group node is part of live or compiled scene graph
     * @exception RestrictedAccessException if this group node is part of
     * live or compiled scene graph and the child node being removed is not
     * a BranchGroup node
     * @exception IndexOutOfBoundsException if <code>index</code> is invalid.
     */
     public void removeChild(int index) {
        super.removeChild(index);
        removeChildFromIndexOrder(index);
    }
     
    /**
     * TransparencyOrderedGroup should not be used as a TransformGroup, this
     * method will always throw a RuntimeException
     */
    public void setTransform(Transform3D t) {
        throw new RuntimeException("TransparencyOrderedGroup.setTransform should not be used.");
    }
    
    /**
     * TransparencyOrderedGroup should not be used as a TransformGroup, this
     * method will always throw a RuntimeException
     */
    public void getTransform(Transform3D t) {
        throw new RuntimeException("TransparencyOrderedGroup.getTransform should not be used.");
    }
    
    private void removeChildFromIndexOrder(int index) {
        if (childIndexOrder!=null) {
            int[] tmp = new int[childIndexOrder.length-1];
            
            int newi = 0;
            for(int i=0; i<childIndexOrder.length; i++) {
                int cio = childIndexOrder[i];

                if (cio>index) {
                    tmp[newi++] = cio-1;
                    //System.out.println("----"+(cio-1)+" "+getChild(cio-1).getName());
                    if (isLive())
                        updateShapeChildIndicies(getChild(cio-1), -1);
                } else if (cio==index) {
                    // Remove this index
                } else
                    tmp[newi++] = cio;
            }
        
            childIndexOrder = tmp;
        }
    }
    
    /**
     * Scan the graph from root applying the adjustment to the child index of
     * each Shape3D for this tog
     */
    private void updateShapeChildIndicies(Node root, final int indexAdjustment) {
        final TransparencyOrderController controller = TransparencyOrderController.getController();
        TreeScan.findNode(root, Shape3D.class, new ProcessNodeInterface() {
            public boolean processNode(Node node) {
                //System.out.println("Scan found "+node);
                controller.adjustChildIndex((Shape3D)node, indexAdjustment, TransparencyOrderedGroup.this);
                return true;
            }            
        }, false, true);
    }
    
    void verifyChildIndexOrderArray(int[] cIOArr, int plus) {

	if(cIOArr != null) {	    
	    
	    if(cIOArr.length != numChildren() + plus) {
		throw new 
		    IllegalArgumentException("childIndexOrder.length != number of children");
	    }
	    
	    if((checkArr == null) || (checkArr.length != cIOArr.length)) {
		checkArr = new boolean[cIOArr.length];
	    }
	    
	    Arrays.fill(checkArr, false);
	    
	    for(int i=0; i<cIOArr.length; i++) {
		if(cIOArr[i] < 0) {
		    throw new
			IllegalArgumentException("childIndexOrder[i] must be >= 0, for i in [0, numChildren-1]");
		}
		else if(cIOArr[i] >= cIOArr.length) {
		    throw new
			IllegalArgumentException("childIndexOrder[i] must be < numChildren, for i in [0, numChildren-1]");
		}
		else if(checkArr[cIOArr[i]]) {
		    throw new
			IllegalArgumentException("childIndexOrder[i] must not be equal to childIndexOrder[j], for i,j in [0,numChildren-1] and i != j");
		}
		else {
		    checkArr[cIOArr[i]] = true;
		}
	    }
	}
    }}
