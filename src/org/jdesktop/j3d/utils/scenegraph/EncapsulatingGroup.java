/*
 * $RCSfile: EncapsulatingGroup.java,v $
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
 * $Date: 2007/02/09 17:17:01 $
 * $State: Exp $
 */

package org.jdesktop.j3d.utils.scenegraph;

import javax.media.j3d.Node;

/**
 * Interface for groups that encapsulate other scene graph nodes and
 * override getChild, addChild etc to hide that fact. For example lg3d
 * J3dComponent3D. Unless this interface is used to provide full access
 * to the graph applications/tools that use the SceneGraphChangeListener
 * can get very confused because they are passed parent nodes of
 * which they have no knowledge.
 *
 * @author paulby
 */
public interface EncapsulatingGroup {
    
    /**
     * Return the encapsulated child at the specified index
     *
     * @param index the index of the child to be returned
     * @return the children at location index. The index  must be a value greater than or equal to 0 and less than numChildren().
     * @throws CapabilityNotSetException - if the appropriate capability is not set and this group node is part of live or compiled scene graph 
    java.lang.IndexOutOfBoundsException - if index is invalid.
     */
    public Node getEncapsulatedChild(int index);
    
    /**
     * Return the number of encapsulated children
     *
     * @return the number of encapsulated children
     */
    public int numEncapsulatedChildren();
    
}