/*
 * $RCSfile: J3fLoader.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2007/02/09 17:17:00 $
 * $State: Exp $
 */

package org.jdesktop.j3d.loaders.wrappers;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;

import javax.media.j3d.BranchGroup;

import com.sun.j3d.utils.scenegraph.io.SceneGraphFileReader;

/**
  * Provides a Loader which can load Java3D scene graphs saved using the
 * SceneGraphFileWriter.
 *
 * The Java3D LoaderInterface implemented by this class does not provide access
 * to all the features that can be stored in a .j3f file.
 * Most importantly the file may contain a universe which will be ignored
 * by this loader.
 *
 * @author Paul Byrne
 * @version	1.8, 01/18/02
  */
public class J3fLoader extends com.sun.j3d.loaders.LoaderBase {

    private SceneGraphFileReader reader=null;
//    private com.sun.j3d.demos.utils.scenegraph.io.SceneGraphFileReader oldReader=null;
//    private static J3fLoaderListener loaderListener = null;
    private BranchGroup[] graphs = null;
    private static ClassLoader classLoader = null;
    
    /**
     * Setup a listener to get notified when this loader is used
     */
//    public static void setJ3fLoaderListener( J3fLoaderListener listener ) {
//        loaderListener = listener;
//    }
    
    /**
     * Set the ClassLoader which will be used by the SceneGraphIO system
     * when loading the scene graph.
     *
     * The default null in which case the system will use system class loader
     */
    public static void setClassLoader( ClassLoader cLoader ) {
        classLoader = cLoader;
    }
    
    /**
     * Get the ClassLoader set by setClassLoader
     *
     * If setClassLoader has not been called the default null will be returned.
     * This indicates that systemClassLoader will be used.
     */
    public static ClassLoader getClassLoader() {
        return classLoader;
    }
    
    /**
     * Load all the BranchGraphs from the file called filename and return
     * a Scene containing all the BranchGraphs
     */
    public Scene load( String filename ) throws java.io.FileNotFoundException,
                                                com.sun.j3d.loaders.IncorrectFormatException,
                                                com.sun.j3d.loaders.ParsingErrorException {

        SceneBase scenebase = null;
        Scene scene=null;
        try {      
            reader = new SceneGraphFileReader( new java.io.File( filename ));
            if (classLoader!=null)
                reader.setClassLoader( classLoader );
            int size = reader.getBranchGraphCount();
            scenebase = new SceneBase();

            BranchGroup bg = new BranchGroup();
            graphs = new BranchGroup[ size ];
            
            if (size>1) {            
                for(int i=0; i<size; i++) {
                    graphs[i] = reader.readBranchGraph( i )[0];
                    bg.addChild( graphs[i] );
                }
            } else {
                bg = reader.readBranchGraph(0)[0];
                graphs[0] = bg;
            }
            
            scenebase.setSceneGroup( bg );
            
            String[] objectNames = reader.getNames();
            for(int i=0; i<objectNames.length; i++)
                try {
                    scenebase.addNamedObject( objectNames[i], reader.getNamedObject( objectNames[i] ) );
                } catch( com.sun.j3d.utils.scenegraph.io.ObjectNotLoadedException e ) {}
                  catch( com.sun.j3d.utils.scenegraph.io.NamedObjectException e ) {}

//            if (loaderListener!=null)
//                loaderListener.loadingJ3f( this );
            reader.close();
            
        } catch( java.io.InvalidClassException ex ) {
            throw new com.sun.j3d.loaders.IncorrectFormatException( ex.getMessage() );
        } catch( java.io.FileNotFoundException e ) {
            throw new java.io.FileNotFoundException( filename );
        } catch( java.io.IOException exc ) {
            throw new java.io.FileNotFoundException( exc.getMessage() );
        }
        
        
        if (scenebase==null)
        return scene;
        else return scenebase;
    }
    
    
    /**
     * Scene only supports a single sceneBase.
     *
     * For any J3f files containing multiple BranchGraphs all the graphs
     * will be added as children of SceneBase.
     *
     * This call will return the actual set of BranchGraphs (this will
     * still be children of the sceneBase ). If their is only a single
     * branchgraph getBranchGrapgs()[0] == sceneBase
     */
    public BranchGroup[] getBranchGraphs() {
        return graphs;
    }
    
    /**
     * Get the user object for the file, should be called from within a J3fLoaderListener only
     */
    public Object getFileUserData() throws java.io.IOException {
        return reader.readUserData();
    }

    /** Not Implemented
     */
    public Scene load( java.net.URL url ) throws java.io.FileNotFoundException,
                                                com.sun.j3d.loaders.IncorrectFormatException,
                                                com.sun.j3d.loaders.ParsingErrorException {
        throw new RuntimeException("NOT IMPLEMENTED");                                            
    }
    
    /** Not Implemented
     */
    public Scene load( java.io.Reader reader ) throws java.io.FileNotFoundException,
                                                com.sun.j3d.loaders.IncorrectFormatException,
                                                com.sun.j3d.loaders.ParsingErrorException {

        throw new RuntimeException("NOT IMPLEMENTED");                                            
    }
    
    
}

