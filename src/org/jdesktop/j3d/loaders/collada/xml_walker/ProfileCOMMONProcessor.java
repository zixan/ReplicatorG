/*
 * $RCSfile: ProfileCOMMONProcessor.java,v $
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

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;

import org.collada.colladaschema.CommonNewparamType;
import org.collada.colladaschema.Extra;
import org.collada.colladaschema.ProfileCOMMON;
import org.collada.colladaschema.ProfileCOMMON.Technique;
import org.w3c.dom.Element;

/**
 * 
 * @author paulby
 */
public class ProfileCOMMONProcessor extends Processor {

	// private PhongProcessor phongProcessor = null;

	private ArrayList<Processor> children = new ArrayList<Processor>();

	private boolean doubleSided = false;

	public ProfileCOMMONProcessor(ProfileCOMMON profileCommon, Processor parent) {
		super(profileCommon, parent);

		String id = profileCommon.getId();
		if (id != null) {
			ElementCache.cache().put(id, this);
		}

		List<Object> fcnts = profileCommon.getImagesAndNewparams();
		for (Object o : fcnts) {
			CommonNewparamType cnt = (CommonNewparamType) o;
			ElementCache.cache().putNewParam(cnt.getSid(), cnt);
		}

		for (Extra ex : profileCommon.getExtras()) {
			for (org.collada.colladaschema.Technique teq : ex.getTechniques()) {
				if (teq.getProfile().equals("GOOGLEEARTH")) {
					for (Element e : teq.getAnies()) {
						if (e.getTagName().equals("double_sided")) {
							if (e.getTextContent().trim().equals("1")) {
								doubleSided = true;
							}
						}
					}
				}
				if (teq.getProfile().equals("MAX3D")) {
					for (Element e : teq.getAnies()) {
						if (e.getTagName().equals("double_sided")) {
							if (e.getTextContent().trim().equals("1")) {
								doubleSided = true;
							}
						}
					}
				}
			}
		}

		Technique tech = profileCommon.getTechnique();
		if (tech.getPhong() != null) {
			Processor p =
					(Processor) ProcessorFactory.createProcessor(tech
							.getPhong(), this);
			if (p != null) {
				children.add(p);
			}
		}
		// ---------------------------------------------------------------
		// THM 23/02/2010 Added to be able to sumport Lambert type surfaces
		if (tech.getLambert() != null) {
			Processor p =
					(Processor) ProcessorFactory.createProcessor(tech
							.getLambert(), this);
			if (p != null) {
				children.add(p);
			}
		}
		if (tech.getBlinn() != null) {
			Processor p =
					(Processor) ProcessorFactory.createProcessor(tech
							.getBlinn(), this);
			if (p != null) {
				children.add(p);
			}
		}

	}

	@Override
	public void create(Node parent) {

		if (doubleSided && (parent instanceof Shape3D)) {
			Shape3D s3d = (Shape3D) parent;

			PolygonAttributes pa = s3d.getAppearance().getPolygonAttributes();
			if (pa == null) {
				pa = new PolygonAttributes();
				s3d.getAppearance().setPolygonAttributes(pa);
			}
			pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
			pa.setCullFace(PolygonAttributes.CULL_NONE);
			pa.setBackFaceNormalFlip(true);
			pa.setPolygonOffset(1.0f);
			pa.setPolygonOffsetFactor(1.0f);
		}

		for (Processor p : children) {
			p.create(parent);
		}
	}

}
