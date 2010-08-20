/*
 * $RCSfile: GeometryInfoGenerator.java,v $
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

package org.jdesktop.j3d.utils.geometry;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryStripArray;
import javax.media.j3d.TriangleFanArray;
import javax.media.j3d.TriangleStripArray;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedGeometryStripArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.IndexedTriangleFanArray;
import javax.media.j3d.IndexedTriangleStripArray;

import com.sun.j3d.utils.geometry.GeometryInfo;

/** 
 * Populate a GeometryInfo object from the Geometry provided
 *
 * @author Paul Byrne
 * @version	1.5, 01/18/02
 */
public class GeometryInfoGenerator extends Object {
  
  public static void create( GeometryInfo geomInfo, GeometryArray geomArray ) {
    if (geomArray instanceof GeometryStripArray) 
      create( geomInfo, (GeometryStripArray)geomArray);
    else if (geomArray instanceof TriangleArray) {
      geomInfo.reset( GeometryInfo.TRIANGLE_ARRAY );
      processGeometryArray( geomInfo, geomArray );
    } else if (geomArray instanceof QuadArray ) {
      geomInfo.reset( GeometryInfo.QUAD_ARRAY );
      processGeometryArray( geomInfo, geomArray );
    } else if (geomArray instanceof IndexedGeometryArray)
      create( geomInfo, (IndexedGeometryArray)geomArray );
    else
      throw new RuntimeException("Unsupported Geometry type "+
                                        geomArray.getClass().getName() );
        
  }
  
  private static void create( GeometryInfo geomInfo, GeometryStripArray geomArray ) {
    if (geomArray instanceof TriangleFanArray) {
      geomInfo.reset( GeometryInfo.TRIANGLE_FAN_ARRAY );
    } else if (geomArray instanceof TriangleStripArray) {
      geomInfo.reset( GeometryInfo.TRIANGLE_STRIP_ARRAY );
    } else
      throw new RuntimeException("Unsupported Geometry type "+
                                        geomArray.getClass().getName() );
    
    processGeometryArray( geomInfo, geomArray ); 
    processStripArray( geomInfo, geomArray );
  }
  
  private static void create( GeometryInfo geomInfo, IndexedGeometryArray geomArray ) {
    if (geomArray instanceof IndexedQuadArray) {
      geomInfo.reset( GeometryInfo.QUAD_ARRAY );
    } else if (geomArray instanceof IndexedTriangleArray) {
      geomInfo.reset( GeometryInfo.TRIANGLE_ARRAY );
    } else if (geomArray instanceof IndexedTriangleFanArray) {
      geomInfo.reset( GeometryInfo.TRIANGLE_FAN_ARRAY );
      processIndexStripArray( geomInfo, (IndexedGeometryStripArray)geomArray );
    } else if (geomArray instanceof IndexedTriangleStripArray) {
      geomInfo.reset( GeometryInfo.TRIANGLE_STRIP_ARRAY );
      processIndexStripArray( geomInfo, (IndexedGeometryStripArray)geomArray );
    }
    
    processGeometryArray( geomInfo, geomArray );
    processIndexedArray( geomInfo, geomArray );
  }
  
  private static void processGeometryArray( GeometryInfo geomInfo, GeometryArray geomArray ) {
    int vertexFormat = geomArray.getVertexFormat();
    int vertexCount = geomArray.getVertexCount();
    
    double[] coords = new double[ vertexCount*3 ];
    geomArray.getCoordinates( 0, coords );
    geomInfo.setCoordinates( coords );
    
    if ((vertexFormat & GeometryArray.NORMALS)==GeometryArray.NORMALS) {
      float[] normals = new float[ vertexCount*3 ];
      geomArray.getNormals( 0, normals );
      geomInfo.setNormals( normals );
    }
    
    if ((vertexFormat & GeometryArray.COLOR_4)==GeometryArray.COLOR_4) {
      float[] colors = new float[ vertexCount*4 ];
      geomArray.getColors( 0, colors );
      geomInfo.setColors4( colors );
    } else if ((vertexFormat & GeometryArray.COLOR_3)!=0) {
      float[] colors = new float[ vertexCount*3 ];
      geomArray.getColors( 0, colors );
      geomInfo.setColors3( colors );
    }
    
    
    if ((vertexFormat & GeometryArray.TEXTURE_COORDINATE_3)==GeometryArray.TEXTURE_COORDINATE_3) {
      float[] tex = new float[ vertexCount * 3 ];
      geomArray.getTextureCoordinates( 0, 0, tex );
      geomInfo.setTextureCoordinates( 0, tex );
    } else if ((vertexFormat & GeometryArray.TEXTURE_COORDINATE_2)!=0) {
      float[] tex = new float[ vertexCount * 2 ];
      geomArray.getTextureCoordinates( 0, 0, tex );
      geomInfo.setTextureCoordinates( 0, tex );
    }
    

  }
  
  private static void processIndexedArray( GeometryInfo geomInfo, IndexedGeometryArray geomArray ) {
    int indexCount = geomArray.getIndexCount();
    int vertexFormat = geomArray.getVertexFormat();
    
    int[] coordI = new int[ indexCount ];
    
    geomArray.getCoordinateIndices( 0, coordI );
    geomInfo.setCoordinateIndices( coordI );
    
    if ((vertexFormat & GeometryArray.NORMALS)!=0) {
      int[] normalI = new int[indexCount];
      geomArray.getNormalIndices( 0, normalI );
      geomInfo.setNormalIndices( normalI );
    }
    
    if ((vertexFormat & GeometryArray.COLOR_3)!=0 ||
        (vertexFormat & GeometryArray.COLOR_4)!=0 ) {
      int[] colorI = new int[ indexCount ];
      geomArray.getColorIndices( 0, colorI );
      geomInfo.setColorIndices( colorI );
    }
    
    if ((vertexFormat & GeometryArray.TEXTURE_COORDINATE_2)!=0 ||
        (vertexFormat & GeometryArray.TEXTURE_COORDINATE_3)!=0) {
      int[] texI = new int[indexCount];
      geomArray.getTextureCoordinateIndices( 0, 0, texI );
      geomInfo.setTextureCoordinateIndices( 0, texI );
    }
    
  
  }
  
  private static void processStripArray( GeometryInfo geomInfo, 
                                  GeometryStripArray geomArray ) {
    int[] strips = new int[ geomArray.getNumStrips() ];
    geomArray.getStripVertexCounts( strips );
    
    geomInfo.setStripCounts( strips );
  }
  
  private static void processIndexStripArray( GeometryInfo geomInfo, 
                                  IndexedGeometryStripArray geomArray ) {
    int[] strips = new int[ geomArray.getNumStrips() ];
    geomArray.getStripIndexCounts( strips );
    
    geomInfo.setStripCounts( strips );
  }
}

