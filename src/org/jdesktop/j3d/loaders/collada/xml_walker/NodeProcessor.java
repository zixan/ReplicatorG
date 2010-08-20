/*
 * $RCSfile: NodeProcessor.java,v $
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
 * $Revision: 1.1 $
 * $Date: 2010/05/01 16:42:50 $
 * $State: Exp $
 */

package org.jdesktop.j3d.loaders.collada.xml_walker;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix4d;

import org.collada.colladaschema.InstanceController;
import org.collada.colladaschema.InstanceGeometry;
import org.collada.colladaschema.InstanceWithExtra;
import org.collada.colladaschema.Matrix;
import org.collada.colladaschema.Node;

/**
 * 
 * @author paulby
 */
public class NodeProcessor extends Processor {

	private ArrayList<Processor> children = new ArrayList<Processor>();

	private TransformGroup tg = null;

	public NodeProcessor(Node node, Processor parent) {
		super(node, parent);

		String id = node.getId();
		if (id != null) {
			ElementCache.cache().put(id, this);
		}

		List<Object> list = node.getTranslatesAndMatrixesAndLookats();
		
		for (Object object : list) {

			if (object instanceof Matrix) {

				Matrix mt = (Matrix) object;
				List<Double> m = mt.getValues();
				double[] da = new double[m.size()];
				int i = 0;
				for (Double d : m) {
					da[i++] = d;
				}
				Matrix4d m4d = new Matrix4d(da);
				Transform3D t3d = new Transform3D(m4d);
				tg = new TransformGroup(t3d);
			}

		}

		List<Node> nodes = node.getNodes();
		for (Node nodeType : nodes) {
			children.add((Processor) ProcessorFactory.createProcessor(nodeType,
					this));
		}

		List<InstanceWithExtra> instanceNodes = node.getInstanceNodes();
		for (InstanceWithExtra nodeType : instanceNodes) {
			children.add((Processor) ProcessorFactory.createProcessor(nodeType,
					this));
		}

		List<InstanceGeometry> geometries = node.getInstanceGeometries();
		for (InstanceGeometry nodeType : geometries) {
			children.add((Processor) ProcessorFactory.createProcessor(nodeType,
					this));
		}

		List<InstanceController> controllers = node.getInstanceControllers();
		for (InstanceController nodeType : controllers) {
			children.add((Processor) ProcessorFactory.createProcessor(nodeType,
					this));
		}

		List<InstanceWithExtra> lights = node.getInstanceLights();
		for (InstanceWithExtra nodeType : lights) {
			children.add((Processor) ProcessorFactory.createProcessor(nodeType,
					this));
		}

		List<InstanceWithExtra> cameras = node.getInstanceCameras();
		for (InstanceWithExtra nodeType : cameras) {
			children.add((Processor) ProcessorFactory.createProcessor(nodeType,
					this));
		}

	}

	@Override
	public void create(javax.media.j3d.Node parent) {
		if (tg != null) {
			if (tg.getParent() == null) {
				((Group) parent).addChild(tg);
			}
			parent = tg;
		}
		for (Processor p : children) {
			p.create(parent);
		}
		if (parent instanceof Group) {
			Group group = (Group) parent;

			// check for dead node
			if (group.numChildren() == 0) {
				
				if ((Group)group.getParent() != null)
				((Group)group.getParent()).removeChild(group);
			}
		}
		
	}

}
