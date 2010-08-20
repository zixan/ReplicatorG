/*
 * $RCSfile: BlinnProcessor.java,v $
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

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import org.collada.colladaschema.CommonColorOrTextureType;
import org.collada.colladaschema.CommonFloatOrParamType;
import org.collada.colladaschema.CommonNewparamType;
import org.collada.colladaschema.Image;
import org.collada.colladaschema.CommonColorOrTextureType.Color;
import org.collada.colladaschema.CommonColorOrTextureType.Texture;
import org.collada.colladaschema.ProfileCOMMON.Technique.Blinn;
import org.jdesktop.j3d.loaders.collada.utils.Collada14LoaderUtils;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * 
 * @author paulby
 */
public class BlinnProcessor extends Processor {

	private Appearance app = null;

	private Blinn blinn;

	private enum Type {
		AMBIENT, EMISSION, DIFFUSE, SPECULAR
	}

	public BlinnProcessor(Blinn phong, Processor parent) {
		super(phong, parent);

		this.blinn = phong;

	}

	@Override
	public void create(Node parent) {

		if (app != null) {
			((Shape3D) parent).setAppearance(app);
			return;
		}

		if (parent instanceof Shape3D) {
			Shape3D s3d = (Shape3D) parent;
			app = s3d.getAppearance();
		}

		if (app == null) {
			app = new Appearance();
		}

		if (blinn != null) {
			app.setMaterial(new Material());

			CommonColorOrTextureType fccott = blinn.getAmbient();
			if (fccott != null) {
				processColorOrTexture(fccott, app, Type.AMBIENT);
			}

			fccott = blinn.getDiffuse();
			if (fccott != null) {
				processColorOrTexture(fccott, app, Type.DIFFUSE);
			}

			fccott = blinn.getEmission();
			if (fccott != null) {
				processColorOrTexture(fccott, app, Type.EMISSION);
			}

			fccott = blinn.getSpecular();
			if (fccott != null) {
				processColorOrTexture(fccott, app, Type.SPECULAR);
			}

			// CommonTransparentType transType = phong.getTransparent();
			CommonFloatOrParamType transparency = blinn.getTransparency();
			if (transparency != null) {
				org.collada.colladaschema.CommonFloatOrParamType.Float f = transparency
						.getFloat();
				if (f != null) {
					float fl = (float) f.getValue();
					if (fl == 1.0f) {
						fl = 0.0f;
					}
					if (fl > 0.0f) {
						TransparencyAttributes ta = app
								.getTransparencyAttributes();
						if (ta == null) {
							ta = new TransparencyAttributes();
							app.setTransparencyAttributes(ta);
						}
						ta.setTransparency(fl);
						ta.setTransparencyMode(TransparencyAttributes.NICEST);
						ta.setTransparencyMode(TransparencyAttributes.BLENDED);
					}
				}
			}

			CommonFloatOrParamType shininess = blinn.getShininess();
			if (shininess != null) {
				org.collada.colladaschema.CommonFloatOrParamType.Float f = shininess
						.getFloat();
				if (f != null && f.getValue() > 0.0f) {
					app.getMaterial().setShininess((float) f.getValue());
				}
			}

			// CommonFloatOrParamType reflectivity = phong.getReflectivity();
			// if (reflectivity != null) {
			// org.collada.colladaschema.CommonFloatOrParamType.Float f =
			// reflectivity
			// .getFloat();
			// if (f != null) {
			//					
			// }
			// }

		}

		((Shape3D) parent).setAppearance(app);
	}

	/**
	 * Create a color3f from the supplied color
	 */
	private void processColorOrTexture(CommonColorOrTextureType colorOrTexture,
			Appearance app, Type t) {

		Color color = colorOrTexture.getColor();
		if (color != null) {
			Material mat = app.getMaterial();

			List<Double> c = color.getValues();

			float[] result = new float[c.size()];
			int i = 0;
			for (Double d : c) {
				result[i++] = d.floatValue();
			}
			switch (t) {
			case AMBIENT:
				mat.setAmbientColor(new Color3f(result));
				break;
			case DIFFUSE:
				if (result.length == 3) {
					mat.setDiffuseColor(new Color3f(result));
				} else {
					mat.setDiffuseColor(result[0], result[1], result[2],
							result[3]);
				}
				break;
			case EMISSION:
				mat.setEmissiveColor(new Color3f(result));
				break;
			case SPECULAR:
				mat.setSpecularColor(new Color3f(result));
				break;
			}
		} else {
			Texture te = colorOrTexture.getTexture();
			String tex = te.getTexture();
			ElementCache ec = ElementCache.cache();
			CommonNewparamType cnt = ec.getNewParam(tex);
			tex = cnt.getSampler2D().getSource();
			cnt = ec.getNewParam(tex);
			Image im = (Image) cnt.getSurface().getInitFroms().get(0)
					.getValue();
			tex = im.getId();

			String path = ElementCache.cache().getImage(tex);
			URL url = ElementCache.cache().getLoadingURL();
			BufferedImage image = Collada14LoaderUtils.readImage(url, path);
			if (image != null) {
				Texture2D tex2d = (Texture2D) new TextureLoader(image)
						.getTexture();
				app.setTexture(tex2d);
			}

		}
	}
}
