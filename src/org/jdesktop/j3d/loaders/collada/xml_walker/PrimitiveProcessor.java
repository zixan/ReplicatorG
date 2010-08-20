/*
 * $RCSfile: PrimitiveProcessor.java,v $
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
 * $Date: 2010/05/01 16:42:51 $
 * $State: Exp $
 */
package org.jdesktop.j3d.loaders.collada.xml_walker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

import org.collada.colladaschema.InputLocalOffset;
import org.jdesktop.j3d.loaders.collada.Collada14LoaderOptions;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.geometry.StripifierStats;

public class PrimitiveProcessor extends Processor {

	public PrimitiveProcessor(Object colladaSchema, Processor parent) {
		super(colladaSchema, parent);
	}

	// private int[] vertexCount = null;
	private int[] prim = null;

	// private ArrayList<Point3f> vertices=null;
	// private ArrayList<Vector3f> normals=null;

	private int[] coordinateIndices = null;
	private float[] coordinates = null;
	private int[] normalIndices = null;
	private float[] normals = null;
	private int[] texCoordinateIndices = null;
	private float[] texCoordinates = null;

	private String materialId = null;

	protected void initiate(List<InputLocalOffset> inputs,
			List<BigInteger> pList, String matId) {
		if (pList != null) {
			prim = new int[pList.size()];
			int i = 0;
			for (BigInteger p : pList)
				prim[i++] = p.intValue();
		}

		int maxOffset = 0;
		for (InputLocalOffset in : inputs) {
			maxOffset = Math.max(in.getOffset().intValue(), maxOffset);
		}

		for (InputLocalOffset in : inputs) {
			logger.info("Inputs " + in.getSemantic() + " source "
					+ in.getSource() + "  offset " + in.getOffset());

			if (in.getSemantic().equalsIgnoreCase("VERTEX")) {
				processVertices(in, maxOffset);
			} else if (in.getSemantic().equalsIgnoreCase("NORMAL")) {
				processNormals(in, maxOffset);
			} else if (in.getSemantic().equalsIgnoreCase("TEXCOORD")) {
				processTexCoordinates(in, maxOffset);
			} else {
				logger.warning("UNIMPLEMENTED SEMANTIC " + in.getSemantic());
			}
		}
		materialId = matId;
	}

	private void processVertices(InputLocalOffset in, int maxOffset) {
		VerticesProcessor source = ElementCache.cache().getVertices(
				in.getSource());
		int offset = in.getOffset().intValue();

		if (source == null) {
			logger.warning("Failed to get Vertices " + in.getSource());
		} else {
			coordinates = source.getFloatArray();

			ArrayList<Integer> coordInd = new ArrayList<Integer>();
			for (int i = offset; i < prim.length; i += maxOffset + 1) {
				coordInd.add(prim[i]);
			}

			coordinateIndices = new int[coordInd.size()];
			int i = 0;
			for (int index : coordInd)
				coordinateIndices[i++] = index;
		}
	}

	private void processTexCoordinates(InputLocalOffset in, int maxOffset) {
		SourceProcessor source = ElementCache.cache().getSource(in.getSource());
		int offset = in.getOffset().intValue();

		if (source == null) {
			logger.warning("Failed to get texcoords " + in.getSource());
		} else {
			texCoordinates = source.getFloatArray();

			ArrayList<Integer> coordInd = new ArrayList<Integer>();
			for (int i = offset; i < prim.length; i += maxOffset + 1) {
				coordInd.add(prim[i]);
			}

			texCoordinateIndices = new int[coordInd.size()];
			int i = 0;
			for (int index : coordInd) {
				texCoordinateIndices[i++] = index;
			}
		}
	}

	private void processNormals(InputLocalOffset in, int maxOffset) {
		SourceProcessor source = ElementCache.cache().getSource(in.getSource());

		int offset = in.getOffset().intValue();

		if (source == null) {
			logger.warning("Failed to get Normals " + in.getSource());
		} else {
			normals = source.getFloatArray();
			ArrayList<Integer> normInd = new ArrayList<Integer>();
			for (int i = offset; i < prim.length; i += maxOffset + 1) {
				normInd.add(prim[i]);
			}

			normalIndices = new int[normInd.size()];
			int i = 0;
			for (int index : normInd)
				normalIndices[i++] = index;
		}
	}

	protected Shape3D getShape3D(int type) {
		logger.info("TrianglesTypeProcessor#getShape3D");

		Shape3D s3d = new Shape3D();
		s3d.setAppearance(new Appearance());

		GeometryInfo gi = new GeometryInfo(type);
		gi.setCoordinates(coordinates);
		gi.setCoordinateIndices(coordinateIndices);

		if (texCoordinates != null) {
			gi.setTextureCoordinateParams(1, 2);
			gi.setTextureCoordinates(0, texCoordinates);
			gi.setTextureCoordinateIndices(0, texCoordinateIndices);
		}

		if (normals != null) {
			gi.setNormals(normals);
			gi.setNormalIndices(normalIndices);
		}

		Stripifier strip = new Stripifier();
		strip.stripify(gi);
		// StripifierStats stats = strip.getStripifierStats();

		s3d.addGeometry(gi.getGeometryArray());

		String s = ElementCache.cache().getMaterial(materialId);
		MaterialProcessor matProc = (MaterialProcessor) ElementCache.cache()
				.get(s);
		if (matProc != null) {
			matProc.create(s3d);
		}

		if (Collada14LoaderOptions.getInstance().isColored()) {
			Appearance ap = new Appearance();
			ap.setColoringAttributes(new ColoringAttributes(new Color3f(
					(float) Math.random(), (float) Math.random(), (float) Math
							.random()), ColoringAttributes.NICEST));
			s3d.setAppearance(ap);
		}

		return s3d;
	}

}
