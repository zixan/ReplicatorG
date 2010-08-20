/*
 * $RCSfile: GeometryArrayStripifier.java,v $
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
 * $Date: 2007/02/09 17:17:02 $
 * $State: Exp $
 */

package org.jdesktop.j3d.utils.scenegraph.traverser;

import java.util.HashMap;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.GeometryArray;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Stripifier;
import org.jdesktop.j3d.utils.geometry.GeometryInfoGenerator;
import org.jdesktop.j3d.utils.capability.Capabilities;

/**
 * Takes each TriangleArray geometry in the
 * scene graph and stripifies the data
 *
 * @author  paulby
 * @version 1.6, 01/18/02
 */
public class GeometryArrayStripifier extends Object {


    public static void stripify( javax.media.j3d.Node[] treeRoots ) {
        StripifyProcessor processor = new StripifyProcessor() {
            private GeometryInfo geomInfo = new GeometryInfo( GeometryInfo.TRIANGLE_ARRAY );
            private Stripifier stripifier = new Stripifier();
            private HashMap map = new HashMap();    // Mapping of original
                                                    // TriangleArrays to new
                                                    // StripArrays
            
            public boolean stripifyGeometry( javax.media.j3d.Shape3D shape ) {
                for( int i=0; i<shape.numGeometries(); i++) {
                    if (shape.getGeometry(i)!=null &&
                        shape.getGeometry(i) instanceof GeometryArray) {

                        GeometryArray geom = (GeometryArray)shape.getGeometry( i );

                        GeometryArray previousConvert = (GeometryArray)map.get( geom );

                        if (previousConvert!=null) {
                            shape.setGeometry( previousConvert, i );
                        } else if (geom instanceof TriangleArray || 
                                   geom instanceof IndexedTriangleArray ||
                                   geom instanceof javax.media.j3d.IndexedTriangleStripArray ||
                                   geom instanceof javax.media.j3d.TriangleStripArray ) {
                            GeometryInfoGenerator.create( geomInfo, geom );
                            stripifier.stripify( geomInfo );
                            GeometryArray newGeom = geomInfo.getGeometryArray();
                            shape.setGeometry( newGeom, i );
                            map.put( geom, newGeom );
                            
                            int[] capabilities = Capabilities.getCapabilities( geom );
                            for(int c=0; c<capabilities.length; c++)
                                newGeom.setCapability( capabilities[c] );
                        }
                    }
                }
                return true;
            }
        };
        
        for(int i=0; i<treeRoots.length; i++)
            scanTree( treeRoots[i], processor );
    }
    
  /**
   * Stripify all TriangleArray geometry in the scene graph
   *
   * @param treeRoot, root of Scene Graph
   */
    public static void stripify( javax.media.j3d.Node treeRoot ) {        
        stripify( new javax.media.j3d.Node[] { treeRoot } );
    }
    
    private static void scanTree( javax.media.j3d.Node treeRoot, StripifyProcessor processor ) {
        try {
            TreeScan.findNode( treeRoot, javax.media.j3d.Shape3D.class, processor, false, true );
        } catch(Exception e ) {
            e.printStackTrace();
            System.out.println( "ERROR GeometryArrayStripifier, SceneGraph contains Live or compiled nodes");
        }
    }
    
}
