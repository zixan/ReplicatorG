/*
 * $RCSfile: ChangePolygonAttributes.java,v $
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

import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Appearance;

/** 
 *
 * @author  paulby
 * @version 
 */
public class ChangePolygonAttributes extends Object {

  /**
    * Traverse the graph setting the polygon mode in all Polygon Attributes
    * object that exist
    */
  public static void setPolygonMode( javax.media.j3d.Node treeRoot,
                                     final int polygonMode ) {
    setPolygonMode( treeRoot, polygonMode, false );
  }                     
                 
  /**
    * Traverse the graph setting the polygon mode in all PolygonAttributes.
    * If createAttribute is false on PolygonAttribute objects that already
    * exist are modified.
    * If craeteAttribute is true, PolygonAttributes and Appearance objects will
    * be created as necessary
    */
  public static void setPolygonMode( javax.media.j3d.Node treeRoot,
                                     final int polygonMode, 
                                     final boolean createAttribute ) {
  
    AppearanceChangeProcessor processor = new AppearanceChangeProcessor() {
      public void changeAppearance( javax.media.j3d.Shape3D shape,
                                    javax.media.j3d.Appearance app ) {
        if (app==null)
          if (createAttribute) {
            app = new Appearance();
            shape.setAppearance( app );
          } else 
            return;
        PolygonAttributes poly = app.getPolygonAttributes();
        if (poly!=null)
          poly.setPolygonMode( polygonMode );
        else if (createAttribute) {
          poly = new PolygonAttributes();
          poly.setPolygonMode( polygonMode );
          app.setPolygonAttributes( poly );
        }
      }
    };
    
    scanTree( treeRoot, processor );
  }
  
  /**
    * Traverse the graph setting the cullFace in all Polygon Attributes
    * object that exist
    */
  public static void setCullFace( javax.media.j3d.Node treeRoot,
                                     final int cullFace ) {

    setCullFace( treeRoot, cullFace, false );                                  
  }
                                    
  /**
    * Traverse the graph setting the cullFace in all PolygonAttributes.
    * If createAttribute is false on PolygonAttribute objects that already
    * exist are modified.
    * If craeteAttribute is true, PolygonAttributes and Appearance objects will
    * be created as necessary
    */
  public static void setCullFace( javax.media.j3d.Node treeRoot,
                                     final int cullFace,
                                     final boolean createAttribute ) {
  
    AppearanceChangeProcessor processor = new AppearanceChangeProcessor() {
      public void changeAppearance( javax.media.j3d.Shape3D shape,
                                    javax.media.j3d.Appearance app ) {
        if (app==null)
          if (createAttribute) {
            app = new Appearance();
            shape.setAppearance( app );
          } else 
            return;
        PolygonAttributes poly = app.getPolygonAttributes();
        if (poly!=null)
          poly.setCullFace( cullFace );
        else if (createAttribute) {
          poly = new PolygonAttributes();
          poly.setCullFace( cullFace );
          app.setPolygonAttributes( poly );
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
      System.out.println( "ERROR ChangePolygonAttributes, SceneGraph contains"+
                          " Live or compiled nodes, without correct capabilities");
    }
  }
  
}
