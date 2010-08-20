/*
 * $RCSfile: ImageComponent2DURL.java,v $
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
 * $Date: 2007/03/29 21:13:42 $
 * $State: Exp $
 */
package org.jdesktop.j3d.utils.scenegraph;

import com.sun.j3d.utils.scenegraph.io.SceneGraphStateProvider;
import com.sun.j3d.utils.scenegraph.io.state.javax.media.j3d.SceneGraphObjectState;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.media.j3d.ImageComponent2D;
import org.jdesktop.j3d.utils.scenegraph.io.ImageComponent2DURLState;

/**
 *
 * A subclass of ImageComponent2D that includes a base URL and image name. There
 * is also an associated state class for SceneGraphIO so when this node is saved
 * only the baseURL and imageName are written, not the image itself. A simple 
 * AssetManager is provided which will load the images from the combined URL and
 * imageName. Users can replace the asset manager with to provide more advanced
 * handling if required.
 *
 * <p>
 * The imageName may contain both the filename and a directory hierarchy
 * ie
 * <p>
 * baseURL http://server.com/textures
 * imageName material/stone/flagstone.jpg
 *
 * @author  paulby
 */
public class ImageComponent2DURL extends javax.media.j3d.ImageComponent2D implements SceneGraphStateProvider {

    private java.net.URL baseURL = null;
    private String imageName = null;
    
    private static AssetManager assetManager = new SimpleAssetManager();
    
    /**
     * Special constructor that creates a new ImageComponent2DURL from the provided
     * ImageComponent2D. This constructor will not call the AssetManager to load the
     * image, instead taking the image from the provide ImageComponent2D.
     */
    public ImageComponent2DURL( ImageComponent2D ic, java.net.URL baseURL, String imageName ) {
        super( ic.getFormat(), ic.getImage(), ic.isByReference(), ic.isYUp() );
        this.baseURL = baseURL;
        this.imageName = imageName;
        set(ic.getImage());
    }
    
    /**
     * Constructs a 2D image component object using the specified
     * format, width, height, byReference flag, and yUp flag.
     * Default values are used for all other parameters.
     *
     * @param format the image component format, one of: FORMAT_RGB,
     * FORMAT_RGBA, etc.
     * @param width the number of columns of pixels in this image component
     * object
     * @param height the number of rows of pixels in this image component
     * object
     * @param byReference a flag that indicates whether the data is copied
     * into this image component object or is accessed by reference.
     * @param yUp a flag that indicates the y-orientation of this image
     * component.  If yUp is set to true, the origin of the image is
     * the lower left; otherwise, the origin of the image is the upper
     * left.
     * @param baseURL The base URL for the image location
     * @param imageName The path and name of the image
     * @exception IllegalArgumentException if format is invalid, or if
     * width or height are not positive.
     *
     */
    public ImageComponent2DURL( int format, int width, int height,
                                boolean byReference, boolean yUp, 
                                java.net.URL baseURL,
                                String imageName ) {
        this( format, width, height, byReference, yUp, baseURL, imageName, false );
    }
    
    /**
     * Constructs a 2D image component object using the specified
     * format, width, height, byReference flag, and yUp flag.
     * Default values are used for all other parameters.
     * If dontUseAssetManager is true then the constructor will not use
     * the asset manager to load the image.
     *
     * @param format the image component format, one of: FORMAT_RGB,
     * FORMAT_RGBA, etc.
     * @param width the number of columns of pixels in this image component
     * object
     * @param height the number of rows of pixels in this image component
     * object
     * @param byReference a flag that indicates whether the data is copied
     * into this image component object or is accessed by reference.
     * @param yUp a flag that indicates the y-orientation of this image
     * component.  If yUp is set to true, the origin of the image is
     * the lower left; otherwise, the origin of the image is the upper
     * left.
     * @param baseURL The base URL for the image location
     * @param imageName The path and name of the image
     * @exception IllegalArgumentException if format is invalid, or if
     * width or height are not positive.
     *
     */
    public ImageComponent2DURL( int format, int width, int height,
                                boolean byReference, boolean yUp, 
                                java.net.URL baseURL,
                                String imageName,
                                boolean dontUseAssetManager) {
        super( format, width, height, byReference, yUp );
        this.baseURL = baseURL;
        this.imageName = imageName;
        if (!dontUseAssetManager)
            notifyAssetManager();
    }
    
    /**
     * Call the asset manager to load the image
     */
    private void notifyAssetManager() {
        assetManager.loadImage(this);
    }
    
    /**
     * Set the asset manager to handle all ImageComponent2DURL objects.
     */
    public static void setAssetManager(AssetManager manager) {
        if (manager==null)
            throw new IllegalArgumentException("AssetManager can not be null");
        assetManager = manager;
    }
    
    /**
     * Get the current asset manager.
     */
    public static AssetManager getAssetManager() {
        return assetManager;
    }
     
    /**
     * Set the Base URL for this image component
     *
     * @param url The URL for the image component
     */
    public void setBaseURL( java.net.URL url ) {
        this.baseURL = url;
    }
    
    /**
     * Get the Base URL for this image component
     *
     * @return TheURL for this image component
     */
    public java.net.URL getBaseURL() {
        return baseURL;
    }
    
    /** 
     * Set the name of the image
     *
     * The baseURL prepended to the name will give the full
     * URL of the image
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    /** 
     * Get the name of the image
     *
     * The baseURL prepended to the name will give the full
     * URL of the image
     */
    public String getImageName() {
        return new String(imageName);
    }
    
    /**
     * Return the State Class used by SceneGraphIO
     */
    public Class<? extends SceneGraphObjectState> getStateClass() {
        return ImageComponent2DURLState.class;
    }
    
 
    /** 
     * AssetManager interface. The AssetManager is responsible for loading
     * each image.
     */
    public interface AssetManager {
        public void loadImage(ImageComponent2DURL image);
        
        /**
         * Create an image component of the correct size/type for the specified
         * image
         */
        public ImageComponent2DURL createImageComponent2DURL(java.net.URL baseURL, String filename);
    }
    
    /**
     * A simple implementation of AssetManager that loads the image
     * from the combined baseURL and imageName.
     */
    static class SimpleAssetManager implements AssetManager {
        public void loadImage(ImageComponent2DURL ic) {
            String url=ic.getBaseURL().toExternalForm();
            
            try {
                URL fullURL = new URL(url+"/"+ic.getImageName());
                BufferedImage bufImg = ImageIO.read(fullURL);
                ic.set(bufImg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }    
        
        public ImageComponent2DURL createImageComponent2DURL(URL baseURL, String filename) {
            throw new RuntimeException("Not Implemented");
        }
    }
    
}
