/*
 * $RCSfile: ChangeTextureAttributes.java,v $
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

package org.jdesktop.j3d.utils.scenegraph.traverser;

import javax.media.j3d.Texture;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.Appearance;

/**
 *
 * @author  Paul Byrne
 * @version $Id: ChangeTextureAttributes.java,v 1.2 2007/02/09 17:17:02 kcr Exp $
 */
public class ChangeTextureAttributes extends Object {
    
    /**
     * Traverse the graph setting the Texture Enable flag in all Texture
     * objects that exist, including those in TextureUnitState's
     */
    public static void setTextureEnable( javax.media.j3d.Node treeRoot,
                                         final boolean enabled ) {
        
        AppearanceChangeProcessor processor = new AppearanceChangeProcessor() {
            public void changeAppearance( javax.media.j3d.Shape3D shape,
            javax.media.j3d.Appearance app ) {
                if (app==null) return;
                Texture texture = app.getTexture();
                if (texture!=null)
                    texture.setEnable( enabled );
                if (app.getTextureUnitState()!=null) {
                    TextureUnitState[] states = app.getTextureUnitState();
                    for(int i=0; i<states.length; i++)
                        if (states[i].getTexture()!=null)
                            states[i].getTexture().setEnable( enabled );
                }
            }
            
        };
        
        scanTree( treeRoot, processor );
    }
    
    private static void scanTree( javax.media.j3d.Node treeRoot,
    AppearanceChangeProcessor processor ) {
        try {
            Class shapeClass = Class.forName("javax.media.j3d.Shape3D");
            
            TreeScan.findNode( treeRoot, shapeClass, processor, false, true );
        } catch(Exception e ) {
            e.printStackTrace();
            System.out.println( "ERROR ChangeTextureAttributes, SceneGraph contains"+
            " Live or compiled nodes, without correct capabilities");
        }
    }
    
}
