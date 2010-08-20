/*
 * $RCSfile: ImageComponent2DURLState.java,v $
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
 * $Date: 2008/05/21 18:58:30 $
 * $State: Exp $
 */

package org.jdesktop.j3d.utils.scenegraph.io;

import com.sun.j3d.utils.scenegraph.io.retained.Controller;
import com.sun.j3d.utils.scenegraph.io.retained.SymbolTableData;
import com.sun.j3d.utils.scenegraph.io.state.javax.media.j3d.ImageComponent2DState;
import com.sun.j3d.utils.scenegraph.io.state.javax.media.j3d.ImageComponentState;
import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URL;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.SceneGraphObject;
import org.jdesktop.j3d.utils.scenegraph.*;

/**
 * SceneGraphIO state class for ImageComponent2DURL. Saves the ImageComponent
 * but does not include the actual image. When the ImageComponent is loaded 
 * the user is responsible for loading the image from the baseURL and imageName.
 *
 * @author paulby
 */
public class ImageComponent2DURLState extends ImageComponentState {
    // Subclass of ImageComponentState instead of ImageComponent2DState so that
    // the image is not saved
    
    private URL baseURL;
    private String imageName;
    
    /** Creates a new instance of ImageComponent2DURLState */
    public ImageComponent2DURLState( SymbolTableData symbol, Controller control ) {
	super( symbol, control );
    }
    
    public void writeConstructorParams( DataOutput out ) throws 
							IOException {
        super.writeConstructorParams( out );    
	ImageComponent2DURL ic = ((ImageComponent2DURL)node);
        if (ic.getBaseURL()==null)
            writeString(null, out);
        else
            writeString(ic.getBaseURL().toExternalForm(), out);
        writeString(ic.getImageName(), out);
    }

    public void readConstructorParams( DataInput in ) throws
							IOException {

       super.readConstructorParams( in );
       String urlStr = readString(in);
       if (urlStr!=null)
            baseURL = new URL(urlStr);
            imageName = readString(in);
    }

    protected SceneGraphObject createNode( Class j3dClass ) {
        return super.createNode( j3dClass, new Class[] { Integer.TYPE,
                                                          Integer.TYPE,
                                                          Integer.TYPE,
                                                          Boolean.TYPE,
                                                          Boolean.TYPE,
                                                          URL.class,
                                                          String.class},
                                            new Object[] { new Integer(format),
                                                           width,
                                                           height,
                                                           new Boolean( byReference ),
                                                           new Boolean( yUp ),
                                                           baseURL,
                                                           imageName } );
    }
    
    protected javax.media.j3d.SceneGraphObject createNode() {
        // Never called, this is not a core state class
        throw new RuntimeException("Unsupported code path - we should never get here");
    }    
    
}
