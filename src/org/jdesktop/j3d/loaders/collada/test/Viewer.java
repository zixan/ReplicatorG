/*
 * $RCSfile: Viewer.java,v $
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
 * $Date: 2010/05/01 16:42:52 $
 * $State: Exp $
 */
package org.jdesktop.j3d.loaders.collada.test;


import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.jdesktop.j3d.loaders.collada.Collada14Loader;
import org.jdesktop.j3d.utils.scenegraph.traverser.ChangePolygonAttributes;
import org.jdesktop.j3d.utils.view.ViewUtils;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * Simple Java 3D example program to display an .obj object.
 */
public class Viewer extends javax.swing.JFrame {

    private String filename = null;

    private SimpleUniverse univ = null;
    private BranchGroup scene = null;
    private BranchGroup root = null;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.7);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	objScale.addChild(objTrans);

        Collada14Loader collada = new Collada14Loader();
	Scene s = null;
	try {
	  s = collada.load(filename);
	}
	catch (FileNotFoundException e) {
	  System.err.println(e);
	  System.exit(1);
	}
	  
	objTrans.addChild(s.getSceneGroup());

	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 500.0);

        // Set up the background
        Color3f bgColor = new Color3f(0.05f, 0.05f, 0.5f);
        Background bgNode = new Background(bgColor);
        bgNode.setApplicationBounds(bounds);
        objRoot.addChild(bgNode);

	return objRoot;
    }
    
    private Canvas3D createUniverse() {
	// Get the preferred graphics configuration for the default screen
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

	// Create a Canvas3D using the preferred configuration
	Canvas3D canvas3d = new Canvas3D(config);

	// Create simple universe with view branch
	univ = new SimpleUniverse(canvas3d);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 1000.0);

	// add mouse behaviors to the ViewingPlatform
	ViewingPlatform viewingPlatform = univ.getViewingPlatform();

	PlatformGeometry pg = new PlatformGeometry();

	// Set up the ambient light
	Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
	AmbientLight ambientLightNode = new AmbientLight(ambientColor);
	ambientLightNode.setInfluencingBounds(bounds);
	pg.addChild(ambientLightNode);

	// Set up the directional lights
	Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
	Color3f light2Color = new Color3f(1.0f, 1.0f, 1.0f);
	Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);

	DirectionalLight light1
	    = new DirectionalLight(light1Color, light1Direction);
	light1.setInfluencingBounds(bounds);
	pg.addChild(light1);

	DirectionalLight light2
	    = new DirectionalLight(light2Color, light2Direction);
	light2.setInfluencingBounds(bounds);
	pg.addChild(light2);

	viewingPlatform.setPlatformGeometry( pg );
      
        // Ensure the entire model is in view
        double fov = viewingPlatform.getViewers()[0].getView().getFieldOfView();
        TransformGroup viewTG = viewingPlatform.getViewPlatformTransform();
        double distance = ViewUtils.setViewpoint( viewTG, scene.getBounds(), fov, ViewUtils.Axis.POSITIVE_Z_AXIS);

        viewingPlatform.getViewers()[0].getView().setBackClipDistance(distance*1.25);

        Transform3D t3d = new Transform3D();
        viewTG.getTransform(t3d);
        System.out.println("Set tg "+t3d);

        OrbitBehavior orbit = new OrbitBehavior(canvas3d,
                                                OrbitBehavior.REVERSE_ALL);
        orbit.setSchedulingBounds(bounds);
        viewingPlatform.setViewPlatformBehavior(orbit);	    

	return canvas3d;
    }

    private void usage() {
//        System.out.println(
//                "Usage: java ObjLoad [-s] [-n] [-t] [-c degrees] <.obj file>");
//        System.out.println("  -s Spin (no user interaction)");
//        System.out.println("  -n No triangulation");
//        System.out.println("  -t No stripification");
//        System.out.println(
//                "  -c Set crease angle for normal generation (default is 60 without");
//        System.out.println(
//                "     smoothing group info, otherwise 180 within smoothing groups)");
//        System.exit(0);
    } // End of usage

    /**
     * Creates new form ObjLoad
     */
    public Viewer(String args[]) {
        if (args.length != 0) {
            for (int i = 0 ; i < args.length ; i++) {
                if (args[i].startsWith("-")) {
//                    if (args[i].equals("-s")) {
//                        spin = true;
//                    } else if (args[i].equals("-n")) {
//                        noTriangulate = true;
//                    } else if (args[i].equals("-t")) {
//                        noStripify = true;
//                    } else if (args[i].equals("-c")) {
//                        if (i < args.length - 1) {
//                            creaseAngle = (new Double(args[++i])).doubleValue();
//                        } else usage();
//                    } else {
//                        usage();
//                    }
                } else {
//                    try {
//                        if ((args[i].indexOf("file:") == 0) ||
//                                (args[i].indexOf("http") == 0)) {
//                            filename = new URL(args[i]);
//                        } else if (args[i].charAt(0) != '/') {
//                            filename = new URL("file:./" + args[i]);
//                        } else {
//                            filename = new URL("file:" + args[i]);
//                        }
//                    } catch (MalformedURLException e) {
//                        System.err.println(e);
//                        System.exit(1);
//                    }
                }
            }       
        }
            
        if (filename == null) {
            filename = "collada/samples/basic_samples/Cube/cube_triangulate.dae";
            filename = "collada/samples/basic_samples/Duck/duck_triangulate.dae";

            if (filename == null) {
                System.err.println("Must provide a file to load");
                System.exit(1);
            }
        }     
        
	// Initialize the GUI components
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
	initComponents();

	// Create the content branch and add it to the universe
	scene = createSceneGraph();

	// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
	Canvas3D c = createUniverse();
	drawingPanel.add(c, java.awt.BorderLayout.CENTER);

        root = new BranchGroup();
        root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        root.addChild(scene);
        scene.setCapability(BranchGroup.ALLOW_DETACH);


	univ.addBranchGraph(root);
    }

    // ----------------------------------------------------------------
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        drawingPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        loadMI = new javax.swing.JMenuItem();
        exitMI = new javax.swing.JMenuItem();
        View = new javax.swing.JMenu();
        polygonFillMI = new javax.swing.JRadioButtonMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ObjLoad");

        drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
        drawingPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");

        loadMI.setText("Load...");
        loadMI.setEnabled(false);
        loadMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMIActionPerformed(evt);
            }
        });
        fileMenu.add(loadMI);

        exitMI.setText("Exit");
        exitMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMIActionPerformed(evt);
            }
        });
        fileMenu.add(exitMI);

        jMenuBar1.add(fileMenu);

        View.setText("View");

        polygonFillMI.setSelected(true);
        polygonFillMI.setText("Polygon Fill");
        polygonFillMI.setToolTipText("Select Filled or edge only rendering");
        polygonFillMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polygonFillMIActionPerformed(evt);
            }
        });
        View.add(polygonFillMI);

        jMenuBar1.add(View);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void loadMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMIActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_loadMIActionPerformed

private void polygonFillMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polygonFillMIActionPerformed
// TODO add your handling code here:
    int mode;

    if (polygonFillMI.isSelected()) {
        mode = PolygonAttributes.POLYGON_FILL;
    } else {
        mode = PolygonAttributes.POLYGON_LINE;
    }

    root.removeChild(scene);
    ChangePolygonAttributes.setPolygonMode(scene, mode, true);
    root.addChild(scene);
}//GEN-LAST:event_polygonFillMIActionPerformed

private void exitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMIActionPerformed
    System.exit(1);
}//GEN-LAST:event_exitMIActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Viewer viewer = new Viewer(args);
                viewer.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu View;
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JMenuItem exitMI;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem loadMI;
    private javax.swing.JRadioButtonMenuItem polygonFillMI;
    // End of variables declaration//GEN-END:variables
    
}
