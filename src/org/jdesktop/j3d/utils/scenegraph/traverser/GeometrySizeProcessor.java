/*
 * $RCSfile: GeometrySizeProcessor.java,v $
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

import java.util.HashSet;
import java.util.Enumeration;
import javax.media.j3d.*;

/**
 * Count the number of Geometry primitives in all the Shape3D nodes supplied by
 * TreeScan
 *
 * The number of Triangles, Quads, Points and Lines are available
 */
public class GeometrySizeProcessor implements ProcessNodeInterface {
    
    private long triangleCount;
    private long quadCount;
    private long lineCount;
    private long pointCount;
    
    private long vertexInStrips;
    private long vertexInArrays;
    
    private int[] triangleStripDistribution;
    private int[] triangleFanDistribution;
    
    private long stripCount;
    
    private int shape3Dcount;
    
    private HashSet appearanceSet = new HashSet();
    
    /** Creates new GeometrySizeProcessor */
    public GeometrySizeProcessor() {
        triangleCount = 0;
        pointCount = 0;
        quadCount = 0;
        lineCount = 0;
        vertexInStrips = 0;
        vertexInArrays = 0;
        stripCount = 0;
        shape3Dcount = 0;
        triangleStripDistribution = new int[15];
        triangleFanDistribution = new int[15];
        
        for(int i=0; i<triangleStripDistribution.length; i++) {
            triangleStripDistribution[i] = 0;
            triangleFanDistribution[i] = 0;
        }
        
        appearanceSet.clear();
    }
    
    /**
     * Process the Shape3D node, increment triangleCount by the number
     * of triangles in the Geometry of the node
     * @param node must be a Shape3D node
     */
    public boolean processNode(javax.media.j3d.Node node) {
        if (!javax.media.j3d.Shape3D.class.isAssignableFrom( node.getClass() ))
            throw new RuntimeException("Node class "+node.getClass().getName()+" is not a (subclass) of Shape3D as expected");
        
        shape3Dcount++;
        appearanceSet.add( ((Shape3D)node).getAppearance() );
        
        Enumeration e = ((Shape3D)node).getAllGeometries();
        
        while(e.hasMoreElements()) {
            Geometry geom = (Geometry)e.nextElement();
            
            if (geom instanceof GeometryStripArray) {
                int numStrips = ((GeometryStripArray)geom).getNumStrips();
                int vertexCounts[] = new int[numStrips];
                ((GeometryStripArray)geom).getStripVertexCounts( vertexCounts );
                for( int i=0; i<numStrips; i++)
                    vertexInStrips += vertexCounts[i];
                
                stripCount += numStrips;
                
                if (geom instanceof TriangleFanArray) {
                    for(int i=0; i<numStrips; i++) {
                        int c = vertexCounts[i] - 2;
                        storeTriangleFanSize( c );
                        triangleCount += c;
                    }
                } else if (geom instanceof TriangleStripArray) {
                    for(int i=0; i<numStrips; i++) {
                        int c = vertexCounts[i] - 2;
                        storeTriangleStripSize( c );
                        triangleCount += c;
                    }
                } else if (geom instanceof LineStripArray) {
                    for(int i=0; i<numStrips; i++)
                        lineCount += vertexCounts[i] - 1;
                } else
                    unsupportedGeom( geom );
            } else if (geom instanceof IndexedGeometryStripArray) {
                int numStrips = ((IndexedGeometryStripArray)geom).getNumStrips();
                int vertexCounts[] = new int[numStrips];
                ((IndexedGeometryStripArray)geom).getStripIndexCounts( vertexCounts );
                for( int i=0; i<numStrips; i++)
                    vertexInStrips += vertexCounts[i];
                
                stripCount += numStrips;
                
                if (geom instanceof IndexedTriangleFanArray) {
                    for(int i=0; i<numStrips; i++) {
                        int c = vertexCounts[i] - 2;
                        triangleCount += c;
                        storeTriangleFanSize( c );
                    }
                } else if (geom instanceof IndexedTriangleStripArray) {
                    for(int i=0; i<numStrips; i++) {
                        int c = vertexCounts[i] - 2;
                        triangleCount += c;
                        storeTriangleStripSize( c );
                    }
                } else if (geom instanceof IndexedLineStripArray) {
                    for(int i=0; i<numStrips; i++)
                        lineCount += vertexCounts[i] - 1;
                } else
                    unsupportedGeom( geom );
            } else if (geom instanceof IndexedGeometryArray) {
                if (geom instanceof IndexedTriangleArray) {
                    triangleCount += ((IndexedGeometryArray)geom).getIndexCount()/3;
                    vertexInArrays += ((IndexedGeometryArray)geom).getIndexCount();
                } else if (geom instanceof IndexedQuadArray) {
                    quadCount += ((IndexedGeometryArray)geom).getIndexCount()/4;
                    vertexInArrays += ((IndexedGeometryArray)geom).getIndexCount();
                } else if (geom instanceof IndexedLineArray) {
                    lineCount += ((IndexedGeometryArray)geom).getIndexCount()/2;
                    vertexInArrays += ((IndexedGeometryArray)geom).getIndexCount();
                } else if (geom instanceof IndexedPointArray) {
                    pointCount += ((IndexedGeometryArray)geom).getIndexCount();
                    vertexInArrays += ((IndexedGeometryArray)geom).getIndexCount();
                } else
                    unsupportedGeom(geom);
            } else if (geom instanceof LineArray) {
                lineCount += ((LineArray)geom).getVertexCount()/2;
                vertexInArrays += ((LineArray)geom).getVertexCount();
            } else if (geom instanceof TriangleArray) {
                triangleCount += ((TriangleArray)geom).getVertexCount()/3;
                vertexInArrays += ((TriangleArray)geom).getVertexCount();
            } else if (geom instanceof QuadArray) {
                quadCount += ((QuadArray)geom).getVertexCount()/4;
                vertexInArrays += ((QuadArray)geom).getVertexCount();
            } else if (geom instanceof PointArray) {
                pointCount += ((PointArray)geom).getVertexCount();
                vertexInArrays += ((PointArray)geom).getVertexCount();
            } else
                unsupportedGeom( geom );
        }
        return true;
    }
    
    /**
     * Keep a record of the distribution of Triangle strip lengths
     *
     * @param size number of Triangles in the strip
     */
    private void storeTriangleStripSize( int size ) {
        if (size<10)
            triangleStripDistribution[size] ++;
        else if (size<20)
            triangleStripDistribution[10] ++;
        else if (size<50)
            triangleStripDistribution[11] ++;
        else if (size<100)
            triangleStripDistribution[12] ++;
        else if (size<1000) {
            triangleStripDistribution[13] ++;
        }else
            triangleStripDistribution[14] ++;
    }
    
    /**
     * Keep a record of the distribution of Triangle strip lengths
     *
     * @param size number of Triangles in the strip
     */
    private void storeTriangleFanSize( int size ) {
        if (size<10)
            triangleFanDistribution[size] ++;
        else if (size<20)
            triangleFanDistribution[10] ++;
        else if (size<50)
            triangleFanDistribution[11] ++;
        else if (size<100)
            triangleFanDistribution[12] ++;
        else if (size<1000) {
            triangleFanDistribution[13] ++;
        }else
            triangleFanDistribution[14] ++;
    }
    
    /**
     * Display the Unsupported Geometry error message to stderr
     */
    private void unsupportedGeom( Geometry geom ) {
        if (geom==null)
            return;
        else
            System.err.println("GeometrySizeProcessor: Unsupported Geometry Type "+
                    geom.getClass().getName() );
    }
    
    /** Getter for property lineCount.
     * @return Value of property lineCount.
     */
    public long getLineCount() {
        return lineCount;
    }
    /** Setter for property lineCount.
     * @param lineCount New value of property lineCount.
     */
    public void setLineCount(long lineCount) {
        this.lineCount = lineCount;
    }
    /** Getter for property quadCount.
     * @return Value of property quadCount.
     */
    public long getQuadCount() {
        return quadCount;
    }
    /** Setter for property quadCount.
     * @param quadCount New value of property quadCount.
     */
    public void setQuadCount(long quadCount) {
        this.quadCount = quadCount;
    }
    /** Getter for property pointCount.
     * @return Value of property pointCount.
     */
    public long getPointCount() {
        return pointCount;
    }
    /** Setter for property pointCount.
     * @param pointCount New value of property pointCount.
     */
    public void setPointCount(long pointCount) {
        this.pointCount = pointCount;
    }
    /** Getter for property triangleCount.
     * @return Value of property triangleCount.
     */
    public long getTriangleCount() {
        return triangleCount;
    }
    /** Setter for property triangleCount.
     * @param triangleCount New value of property triangleCount.
     */
    public void setTriangleCount(long triangleCount) {
        this.triangleCount = triangleCount;
    }
    
    /**
     * Return the triangle strip size distributions
     *
     * Each element of the array represents the following Strip size
     *
     *    element     strip size
     *        0
     *        1       1
     *        2       2
     *        3       3
     *        4       4
     *        5       5
     *        6       6
     *        7       7
     *        8       8
     *        9       9
     *        10      10-19
     *        11      20-49
     *        12      50-99
     *        13      100-999
     *        14      > 1000
     */
    public int[] getTriangleStripDistribution() {
        return triangleStripDistribution;
    }
    
    /**
     * Return the triangle fan size distributions
     *
     * Each element of the array represents the following Strip size
     *
     *    element     strip size
     *        0
     *        1       1
     *        2       2
     *        3       3
     *        4       4
     *        5       5
     *        6       6
     *        7       7
     *        8       8
     *        9       9
     *        10      10-19
     *        11      20-49
     *        12      50-99
     *        13      100-999
     *        14      > 1000
     */
    public int[] getTriangleFanDistribution() {
        return triangleFanDistribution;
    }
    
    public int getShape3DCount() {
        return shape3Dcount;
    }
    
    public void setShape3DCount( int count ) {
        shape3Dcount = count;
    }
    
    public int getAppearanceCount() {
        return appearanceSet.size();
    }
    
    /**
     * Return the percentage of Vertex which are in strips
     */
    public float getStripPercentage() {
        if ((vertexInStrips + vertexInArrays)==0)
            return 0;
        else
            return ((float)vertexInStrips)/(vertexInStrips + vertexInArrays) * 100f;
    }
    
    /**
     * Return the average length of the Strips
     */
    public float getAverageStripLength() {
        if (stripCount==0)
            return 0.0f;
        else
            return (float)vertexInStrips/(float)stripCount;
    }
}
