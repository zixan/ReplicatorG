/*
 * $RCSfile: PolylistProcessor.java,v $
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

import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

import javax.media.j3d.Shape3D;

import org.collada.colladaschema.InputLocalOffset;
import org.collada.colladaschema.Polylist;
import org.jdesktop.j3d.loaders.collada.Collada14Loader;

import com.sun.j3d.utils.geometry.GeometryInfo;

/**
 * 
 * @author paulby
 */
public class PolylistProcessor extends PrimitiveProcessor implements Primitive {

	private Logger logger = Logger.getLogger(Collada14Loader.COLLADA_LOADER);

	/** Creates a new instance of PolylistProcessor */
	public PolylistProcessor(Polylist polylist, Processor parent) {
		super(polylist, parent);
		logger.info("Polylist " + polylist.getName());

		List<InputLocalOffset> inputs = polylist.getInputs();
		List<BigInteger> pList = polylist.getP();
		initiate(inputs, pList, polylist.getMaterial());
	}

	public Shape3D getShape3D() {

		return super.getShape3D(GeometryInfo.QUAD_ARRAY);
	}
}
