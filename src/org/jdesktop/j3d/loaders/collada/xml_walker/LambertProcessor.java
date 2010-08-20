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
import org.collada.colladaschema.ProfileCOMMON.Technique.Lambert;
import org.jdesktop.j3d.loaders.collada.utils.Collada14LoaderUtils;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * 
 * @author Thierry (copied-and modified from classe PhongProcessor of Paulby)
 */
public class LambertProcessor extends Processor {

	private Appearance app = null;

	// private Phong phong;
	private Lambert lambert;

	private enum Type {
		AMBIENT, EMISSION, DIFFUSE, SPECULAR
	}

	public LambertProcessor(Lambert lambert, Processor parent) {
		super(lambert, parent);
		// System.out.println("LambertProcessor>>constructor(Lambert, Processor) Begin ");
		this.lambert = lambert;
		// System.out.println("LambertProcessor>>constructor(Lambert, Processor) End ");
	}

	@Override
	public void create(Node parent) {
		// System.out.println("LambertProcessor>>create(Node) begin ");
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

		if (lambert != null) {
			app.setMaterial(new Material());

			CommonColorOrTextureType fccott = lambert.getAmbient();
			if (fccott != null) {
				processColorOrTexture(fccott, app, Type.AMBIENT);
			}

			fccott = lambert.getDiffuse();
			if (fccott != null) {
				processColorOrTexture(fccott, app, Type.DIFFUSE);
			}

			fccott = lambert.getEmission();
			if (fccott != null) {
				processColorOrTexture(fccott, app, Type.EMISSION);
			}

			// THM 23/02/2010 No 'Specular' in Lambert
			/*
			 * fccott = lambert.getSpecular(); if (fccott != null) {
			 * processColorOrTexture(fccott, app, Type.SPECULAR); }
			 */

			// CommonTransparentType transType = phong.getTransparent();
			CommonFloatOrParamType transparency = lambert.getTransparency();
			if (transparency != null) {
				CommonFloatOrParamType.Float f = transparency.getFloat();
				if (f != null) {
					float fl = (float) f.getValue();
					if (fl == 1.0f) {
						fl = 0.0f;
					}
					if (fl > 0.0f) {
						TransparencyAttributes ta =
								app.getTransparencyAttributes();
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

			// THM 23/02/2010 No 'Shininess' in Lambert
			/*
			 * CommonFloatOrParamType shininess = lambert.getShininess(); if
			 * (shininess != null) { CommonFloatOrParamType.Float f = shininess
			 * .getFloat(); if (f != null && f.getValue() > 0.0f) {
			 * app.getMaterial().setShininess((float) f.getValue()); } }
			 */

		}

		((Shape3D) parent).setAppearance(app);
		// System.out.println("LambertProcessor>>create(Node) End ");

	}

	/**
	 * Create a color3f from the supplied color
	 */
	private void processColorOrTexture(CommonColorOrTextureType colorOrTexture,
			Appearance app, Type t) {
		// System.out.println("LambertProcessor>>processColorOrTexture(CommonColorOrTextureType, Appearance, Type) begin ");

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
			Image im =
					(Image) cnt.getSurface().getInitFroms().get(0).getValue();
			tex = im.getId();

			String path = ElementCache.cache().getImage(tex);
			URL url = ElementCache.cache().getLoadingURL();
			BufferedImage image = Collada14LoaderUtils.readImage(url, path);
			if (image != null) {
				Texture2D tex2d =
						(Texture2D) new TextureLoader(image).getTexture();
				app.setTexture(tex2d);
			}

		}
		// System.out.println("LambertProcessor>>processColorOrTexture(CommonColorOrTextureType, Appearance, Type) end ");

	}
}