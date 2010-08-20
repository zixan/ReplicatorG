/*
 * $RCSfile: VerticesProcessor.java,v $
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
 * $Date: 2010/05/04 20:17:22 $
 * $State: Exp $
 */

package org.jdesktop.j3d.loaders.collada.xml_walker;

import java.util.List;
import java.util.logging.Logger;

import javax.vecmath.Tuple3f;

import org.collada.colladaschema.InputLocal;
import org.collada.colladaschema.Vertices;
import org.jdesktop.j3d.loaders.collada.Collada14Loader;

/**
 * 
 * @author paulby
 */
public class VerticesProcessor extends Processor {

	private Logger logger = Logger.getLogger(Collada14Loader.COLLADA_LOADER);
	
	private SourceProcessor positionSource = null;

	/** Creates a new instance of VerticesProcessor */
	public VerticesProcessor(Vertices vert, Processor parent) {
		super(vert, parent);
		logger.info("Processing Vertices");
		ElementCache.cache().putVertices(vert.getId(), this);
		List<InputLocal> inputs = vert.getInputs();
		for (InputLocal input : inputs) {
			logger.info("Semantic " + input.getSemantic() + "  source "
					+ input.getSource());
			if (input.getSemantic().equals("TRANSLATION")
					|| input.getSemantic().equals("POSITION"))
				positionSource =
						ElementCache.cache().getSource(input.getSource());
			else
				logger.warning("Ignoring vertices semantic "
						+ input.getSemantic());
		}
	}

	Tuple3f getTuple3f(Tuple3f result, int index) {
		return positionSource.getTuple3f(result, index);
	}

	float[] getFloatArray() {
		return positionSource.getFloatArray();
	}

}
