/*
 * $RCSfile: LightProcessor.java,v $
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
 * $Date: 2010/05/01 16:42:50 $
 * $State: Exp $
 */
package org.jdesktop.j3d.loaders.collada.xml_walker;

import javax.media.j3d.Group;
import javax.media.j3d.Node;

import org.collada.colladaschema.Light;
import org.collada.colladaschema.Light.TechniqueCommon.Ambient;
import org.collada.colladaschema.Light.TechniqueCommon.Directional;
import org.collada.colladaschema.Light.TechniqueCommon.Point;
import org.collada.colladaschema.Light.TechniqueCommon.Spot;

/**
 * 
 * @author paulby
 */
public class LightProcessor extends Processor {

	private Light l;

	public LightProcessor(Light light, Processor parent) {
		super(light, parent);

		// String id = light.getId();
		// if (id != null) {
		// ElementCache.cache().putLight(id, this);
		// }
		this.l = light;

	}

	@Override
	public void create(Node parent) {
		if (parent instanceof Group) {
			Light.TechniqueCommon tc = l.getTechniqueCommon();
			Ambient al = tc.getAmbient();
			Directional dl = tc.getDirectional();
			Point pl = tc.getPoint();
			Spot sl = tc.getSpot();

			javax.media.j3d.Light light = null;
			if (al != null) {
//				List<Double> c = al.getColor().getValues();
//				light = new AmbientLight(new Color3f(c.get(0).floatValue(), c
//						.get(1).floatValue(), c.get(2).floatValue()));
//				light.setInfluencingBounds(new BoundingSphere(new Point3d(),
//						Double.POSITIVE_INFINITY));
			} else if (dl != null) {
				// List<Double> c = dl.getColor().getValues();
				// light = new DirectionalLight(new
				// Color3f(c.get(0).floatValue(),
				// c.get(1).floatValue(), c.get(2).floatValue()),
				// new Vector3f(0.0f, 0.0f, -1.0f));
				// light.setInfluencingBounds(new BoundingSphere(new Point3d(),
				// Double.POSITIVE_INFINITY));
			} else if (pl != null) {
				// List<Double> c = dl.getColor().getValues();
				// light = new PointLight(new Color3f(c.get(0).floatValue(),
				// c.get(1).floatValue(), c.get(2).floatValue()),
				// new Vector3f(1.0f, 1.0f, 1.0f));
				// light.setInfluencingBounds(new BoundingSphere(new Point3d(),
				// Double.POSITIVE_INFINITY));
			} else if (sl != null) {
				// List<Double> c = dl.getColor().getValues();
				// light = new SpotLight(new Color3f(c.get(0).floatValue(),
				// c.get(1).floatValue(), c.get(2).floatValue()),
				// new Vector3f(1.0f, 1.0f, 1.0f));
				// light.setInfluencingBounds(new BoundingSphere(new Point3d(),
				// Double.POSITIVE_INFINITY));
			}

			if (light != null) {
				((Group) parent).addChild(light);
			}
		}
	}
}
