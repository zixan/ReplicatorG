/*
 * $RCSfile: SourceProcessor.java,v $
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
 * $Revision: 1.3 $
 * $Date: 2010/05/04 20:17:23 $
 * $State: Exp $
 */

package org.jdesktop.j3d.loaders.collada.xml_walker;

import java.util.List;
import java.util.logging.Logger;

import javax.vecmath.Tuple3f;

import org.collada.colladaschema.Accessor;
import org.collada.colladaschema.Asset;
import org.collada.colladaschema.BoolArray;
import org.collada.colladaschema.FloatArray;
import org.collada.colladaschema.IDREFArray;
import org.collada.colladaschema.IntArray;
import org.collada.colladaschema.Mesh;
import org.collada.colladaschema.NameArray;
import org.collada.colladaschema.Source;
import org.collada.colladaschema.Source.TechniqueCommon;
import org.jdesktop.j3d.loaders.collada.Collada14Loader;

/**
 * 
 * @author paulby
 */
public class SourceProcessor extends Processor {

	private Logger logger = Logger.getLogger(Collada14Loader.COLLADA_LOADER);

	private Source source = null;

	private enum SourceTypeEnum {
		BOOL_ARRAY, FLOAT_ARRAY, INT_ARRAY, NAME_ARRAY, ID_REF_ARRAY
	};

	private SourceTypeEnum sourceType = null;

	private float[] processedFloatArray = null;

	/** Creates a new instance of MeshProcessor */
	public SourceProcessor(Mesh mesh, Processor parent) {
		super(mesh, parent);
		logger.info("Processing Mesh ");
		List<Source> sources = mesh.getSources();
		for (Source s : sources) {
			ProcessorFactory.createProcessor(s, this);
		}

		ProcessorFactory.createProcessor(mesh.getVertices(), this);
	}

	public SourceProcessor(Source source, Processor parent) {
		super(source, parent);
		logger.info("Processing Source id=" + source.getId() + " name="
				+ source.getName());

		this.source = source;
		ElementCache.cache().putSource(source.getId(), this);

		BoolArray boolArray = source.getBoolArray();
		FloatArray floatArray = source.getFloatArray();
		IntArray intArray = source.getIntArray();
		NameArray nameArray = source.getNameArray();
		IDREFArray idRefArray = source.getIDREFArray();

		
		if (boolArray != null)
			logger.info("BoolArray");
		else if (floatArray != null) {
			List<Double> data = floatArray.getValues();
			logger.info("FloatArray " + data.size());
			float[] floatData = new float[data.size()];
			int i = 0;
			for (Double d : data)
				floatData[i++] = d.floatValue();
			ElementCache.cache().putFloatArray(floatArray.getId(), floatData);
			sourceType = SourceTypeEnum.FLOAT_ARRAY;
		} else if (intArray != null)
			logger.info("IntArray ");
		else if (nameArray != null)
			logger.info("NameArray ");
		else if (idRefArray != null)
			logger.info("IdRefArray ");

		TechniqueCommon techCommon = source.getTechniqueCommon();
		Accessor accessor = techCommon.getAccessor();

		switch (sourceType) {
		case FLOAT_ARRAY:
			// TODO Check ordering of x,y,z in accessor
			break;
		default:
			logger.warning("Unimplemented SourceType " + sourceType);
		}
	}

	// private void processFloatArray(Accessor accessor) {
	// int size = accessor.getCount().intValue();
	// int stride = accessor.getStride().intValue();
	// float[] data = ElementCache.cache().getFloatArray(accessor.getSource());
	// }

	Tuple3f getTuple3f(Tuple3f result, int index) {
		TechniqueCommon techCommon = source.getTechniqueCommon();
		Accessor accessor = techCommon.getAccessor();
		int size = accessor.getCount().intValue();
		int stride = accessor.getStride().intValue();
		int offset = accessor.getOffset().intValue();
		float[] data = ElementCache.cache().getFloatArray(accessor.getSource());

		index += offset;
		result.x = data[index];
		result.y = data[index + 1];
		result.z = data[index + 2];

		return result;
	}

	float[] getFloatArray() {
		TechniqueCommon techCommon = source.getTechniqueCommon();
		Accessor accessor = techCommon.getAccessor();

		// TODO, does offset apply here ?

		return ElementCache.cache().getFloatArray(accessor.getSource());
	}
}
