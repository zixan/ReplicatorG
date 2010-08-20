/*
 * $RCSfile: GeometryStatistics.java,v $
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

/** 
 *
 * @author  paulby
 * @version 
 *
 * Provide statistics on the Geometry in a given scene graph
 */
public class GeometryStatistics extends Object {
  
  /**
    * Print the geometry statistics for the all SceneGraph in treeRoots to
    * stdout. Information on all geometry nodes in the Scene Graph will
    * be used, even nodes under disabled Switch nodes.
    *
    * treeRoot and all of it's children must be non-live and non-compiled
    * if they are not an error message will be displayed
    */
  public static void printStatistics( javax.media.j3d.Node[] treeRoots ) {
    GeometrySizeProcessor processor = new GeometrySizeProcessor();
    TreeScan scan = new TreeScan();
    int triangleCount=0;
    int quadCount=0;
    int lineCount=0;
    int pointCount=0;
    
    try {
      Class geometryClass = Class.forName("javax.media.j3d.Shape3D");
    
      for(int i=0; i<treeRoots.length; i++) {
        scan.findNode( treeRoots[i], geometryClass, processor, false, false );
      }
            
      System.out.println("Triangle Count : "+ processor.getTriangleCount());
      System.out.println("Quad Count     : "+ processor.getQuadCount());
      System.out.println("Line Count     : "+ processor.getLineCount());
      System.out.println("Point Count    : "+processor.getPointCount() );
      
      System.out.println("Percentage of Vertex in Strips "+
                                processor.getStripPercentage() +"%");
      System.out.println("Average Strip Length "+
                                processor.getAverageStripLength() );
      
      int [] dist = processor.getTriangleStripDistribution();
      
      System.out.println("Triangle Strip size distribution");
        System.out.println( "1       "+dist[1] );
        System.out.println( "2       "+dist[2] );
        System.out.println( "3       "+dist[3] );
        System.out.println( "4       "+dist[4] );
        System.out.println( "5       "+dist[5] );
        System.out.println( "6       "+dist[6] );
        System.out.println( "7       "+dist[7] );
        System.out.println( "8       "+dist[8] );
        System.out.println( "9       "+dist[9] );
        System.out.println( "10-19   "+dist[10] );
        System.out.println( "20-49   "+dist[11] );
        System.out.println( "50-99   "+dist[12] );
        System.out.println( "100-999 "+dist[13] );
        System.out.println( ">1000   "+dist[14] );
    } catch( javax.media.j3d.CapabilityNotSetException e ) {
      System.out.println("Error, SceneGraph is either live or compiled");
    } catch( ClassNotFoundException ex ) {
      ex.printStackTrace();
      System.exit(1);
    }
  }

  /**
    * Print the geometry statistics for the SceneGraph treeRoot to
    * stdout. Information on all geometry nodes in the Scene Graph will
    * be used, even nodes under disabled Switch nodes.
    *
    * treeRoot and all of it's children must be non-live and non-compiled
    * if they are not an error message will be displayed
    */
  public static void printStatistics( javax.media.j3d.Node treeRoot ) {
      printStatistics( new javax.media.j3d.Node[] { treeRoot } );
  }
  
}
