/*
 * $RCSfile: TransparencyOrderedGroupTest.java,v $
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
 * $Date: 2007/02/09 17:16:59 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.transparency;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.scenegraph.transparency.TransparencySortController;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.ColorCube;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.media.j3d.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.*;
import java.awt.GraphicsConfiguration;
import org.jdesktop.j3d.utils.scenegraph.transparency.TransparencyOrderController;
import org.jdesktop.j3d.utils.scenegraph.transparency.TransparencyOrderedGroup;

/**
 */
public class TransparencyOrderedGroupTest extends javax.swing.JFrame {

    private SimpleUniverse univ = null;
    private BranchGroup scene = null;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        objRoot.addChild(createTest2());
        
        return objRoot;
    }
    
    // TOG addChild, insertChild and removeChild tests (non visual)
    private Group createTest3() {
        BranchGroup bg = new BranchGroup();
        
        TransparencyOrderedGroup tog = new TransparencyOrderedGroup();
        tog.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        tog.setCapability(Group.ALLOW_CHILDREN_WRITE);
        bg.addChild(tog);
        
        tog.addChild(createShape(0f,0f,0f,1f,null,"c1"));
        tog.addChild(createShape(0f,0f,0f,1f,null,"c2"));
        tog.addChild(createShape(0f,0f,0f,1f,null,"c3"));
        tog.addChild(createShape(0f,0f,0f,1f,null,"c4"));
        
        tog.setChildIndexOrder(new int[] {3,2,1,0});
        univ.addBranchGraph(bg);
        
        tog.addChild(createShape(0f,0f,0f,1f,null,"i1"));
               
        {
            int[] io = tog.getChildIndexOrder();        
            for(int i=0; i<io.length; i++)
                System.out.println(io[i]+" "+tog.getChild(i).getName());
        }
        
//        tog.removeChild(0);        
//        {
//            int[] io = tog.getChildIndexOrder();        
//            for(int i=0; i<io.length; i++)
//                System.out.println(io[i]+" "+tog.getChild(i).getName());
//        }
        
//        tog.removeChild(3);        
//        {
//            int[] io = tog.getChildIndexOrder();        
//            for(int i=0; i<io.length; i++)
//                System.out.println(io[i]+" "+tog.getChild(i).getName());
//        }
//        
//        tog.removeChild(0);        
//        {
//            int[] io = tog.getChildIndexOrder();        
//            for(int i=0; i<io.length; i++)
//                System.out.println(io[i]+" "+tog.getChild(i).getName());
//        }
        
       
        return null;
    }
    
    // Rendering test
    private Group createTest2() {
        BranchGroup bg = new BranchGroup();
        BranchGroup bg2 = new BranchGroup();
        
        Appearance app = new Appearance();
        final TransparencyAttributes transparencyAttr = new TransparencyAttributes();
        transparencyAttr.setTransparency(jSlider1.getValue()/100.0f);
        transparencyAttr.setTransparencyMode(TransparencyAttributes.FASTEST);
        transparencyAttr.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        app.setTransparencyAttributes(transparencyAttr);
        
        jSlider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                transparencyAttr.setTransparency(jSlider1.getValue()/100.0f);
            }
            
        });
        
        Appearance app2 = new Appearance();
        final TransparencyAttributes transparencyAttr2 = new TransparencyAttributes();
        transparencyAttr2.setTransparency(0.5f);
        transparencyAttr2.setTransparencyMode(TransparencyAttributes.FASTEST);
        transparencyAttr2.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        app2.setTransparencyAttributes(transparencyAttr2);
        ColoringAttributes colorAttr = new ColoringAttributes();
        colorAttr.setColor(1f, 0f,0f);
        app2.setColoringAttributes(colorAttr);
        
        jSlider2.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                transparencyAttr2.setTransparency(jSlider2.getValue()/100.0f);
            }
            
        });
        
        Appearance app3 = new Appearance();
        ColoringAttributes colorAttr2 = new ColoringAttributes();
        colorAttr2.setColor(0f, 1f,0f);
        app3.setColoringAttributes(colorAttr2);
        
        Appearance app4 = new Appearance();
        final TransparencyAttributes transparencyAttr3 = new TransparencyAttributes();
        transparencyAttr3.setTransparency(0.5f);
        transparencyAttr3.setTransparencyMode(TransparencyAttributes.FASTEST);
        transparencyAttr3.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        app4.setTransparencyAttributes(transparencyAttr3);
        ColoringAttributes colorAttr3 = new ColoringAttributes();
        colorAttr3.setColor(1f, 1f,0f);
        app4.setColoringAttributes(colorAttr3);

        bg2.addChild(createShape(0.3f, 0.3f, 0.5f, 0.1f, app2, "right"));
        bg2.addChild(createShape(-0.3f, 0.3f, 0.5f, 0.1f, app2, "left"));
        
        bg2.addChild(createShape(0.3f, -0.3f, 0.5f, 0.1f, app3, "solid"));
        
        TransparencyOrderedGroup tog2 = new TransparencyOrderedGroup();
        tog2.addChild(createShape(0.39f, 0.3f, 0.55f, 0.05f, app4, null));
        tog2.addChild(bg2);
        
        final TransparencyOrderedGroup tog = new TransparencyOrderedGroup();
        tog.addChild(tog2);  // Rendered First (behind)
        tog.addChild(createShape(0f,0f,0f, 0.6f, app, "BigBox")); // Rendered Second (in front)
        
        tog.setChildIndexOrder(new int[] {0,1});
        jCheckBox1.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               int[] order = tog.getChildIndexOrder();
               int tmp = order[0];
               order[0] = order[1];
               order[1] = tmp;
               tog.setChildIndexOrder(order);
           } 
        });
        
        bg.addChild(tog);
        
        // Background box
        Appearance bApp = new Appearance();
        ColoringAttributes bClr = new ColoringAttributes();
        bClr.setColor(0f,0f,1f);
        bApp.setColoringAttributes(bClr);
        bg.addChild(createShape( 0f, 0f, -1f, 0.6f, bApp, "background"));
        
        return bg;
    }
    
    private Node createShape( float x, float y, float z, float size, Appearance app, String name) {
        BranchGroup bg = new BranchGroup();
        bg.setName("bg-"+name);
        bg.setCapability(BranchGroup.ALLOW_DETACH);
        Transform3D t3d = new Transform3D();
        t3d.setTranslation(new Vector3f(x,y,z));
        TransformGroup tg = new TransformGroup(t3d);
        tg.setName("tg-"+name);
        //tg.addChild(new Box(size,size,size, app));
        
        TriangleStripArray geom = new TriangleStripArray(4, TriangleStripArray.COORDINATES, new int[] {4});
        float s = size;
        geom.setCoordinates(0, new float[] {
            -s, -s, 0f,
            s, -s, 0f,
            -s, s, 0f,
            s, s, 0f
        });
        
        Shape3D shape = new Shape3D(geom, app);
        shape.setName(name);
        
        tg.addChild(shape);
        bg.addChild(tg);
        
        return bg;
    }
    
    private Group createTest1() {
        TransparencyOrderedGroup tog1 = new TransparencyOrderedGroup();
        tog1.setName("tog1");
        
        TransparencyOrderedGroup tog2 = new TransparencyOrderedGroup();
        tog2.setName("tog2");
        BranchGroup bg = new BranchGroup();
        
        Shape3D s1 = new Shape3D();
        s1.setName("s1");
        Shape3D s2 = new Shape3D();
        s2.setName("s2");
        Shape3D s3 = new Shape3D();
        s3.setName("s3");
        Shape3D s4 = new Shape3D();
        s4.setName("s4");
        
        BranchGroup bg2 = new BranchGroup();
        
        bg2.addChild(s1);
        tog2.addChild(bg2);
        tog2.addChild(s2);
        tog1.addChild(s3);
        tog1.addChild(bg);
        tog1.addChild(tog2);
        bg.addChild(s4);
        
	return tog1;
    }

    private Canvas3D createUniverse() {
	// Get the preferred graphics configuration for the default screen
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

	// Create a Canvas3D using the preferred configuration
	Canvas3D c = new Canvas3D(config);

	// Create simple universe with view branch
	univ = new SimpleUniverse(c);

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	univ.getViewingPlatform().setNominalViewingTransform();

	// Ensure at least 5 msec per frame (i.e., < 200Hz)
	univ.getViewer().getView().setMinimumFrameCycleTime(5);

        univ.addBranchGraph(new TransparencyOrderController(univ.getViewer().getView()));
        
	return c;
    }

    /**
     * Creates new form HelloUniverse
     */
    public TransparencyOrderedGroupTest() {
	// Initialize the GUI components
	initComponents();

	// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
	Canvas3D c = createUniverse();
	drawingPanel.add(c, java.awt.BorderLayout.CENTER);

	// Create the content branch and add it to the universe
	scene = createSceneGraph();
	univ.addBranchGraph(scene);
    }

    // ----------------------------------------------------------------
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        drawingPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        jSlider2 = new javax.swing.JSlider();
        jCheckBox1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HelloUniverse");
        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("White Transparency");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel1.add(jLabel1, gridBagConstraints);

        jSlider1.setValue(20);
        jPanel1.add(jSlider1, new java.awt.GridBagConstraints());

        jLabel2.setText("Red Transparency");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel1.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jSlider2, gridBagConstraints);

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("White Quad In Front");
        jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jCheckBox1, gridBagConstraints);

        drawingPanel.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransparencyOrderedGroupTest().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    // End of variables declaration//GEN-END:variables
    
}
