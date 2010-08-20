/*
 * $RCSfile: LibraryGeometriesProcessor.java,v $
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

import org.collada.colladaschema.Asset;
import org.collada.colladaschema.Geometry;
import org.collada.colladaschema.LibraryGeometries;

/**
 * 
 * @author paulby
 */
public class LibraryGeometriesProcessor extends Processor {

	private ArrayList<Processor> children = new ArrayList<Processor>();

	/**
	 * Creates a new instance of LibraryGeometriesProcessor
	 * 
	 * @param jaxbNode
	 */
	public LibraryGeometriesProcessor(LibraryGeometries jaxbNode,
			Processor parent) {
		super(jaxbNode, parent);
		Asset asset = jaxbNode.getAsset(); // 0 or 1

		// String id = jaxbNode.getId();
		// if (id != null) {
		// ElementCache.cache().put(id, this);
		// }

		List<Geometry> geoms = jaxbNode.getGeometries();
		for (Geometry nodeType : geoms) {
			children.add((Processor) ProcessorFactory.createProcessor(nodeType,
					this));
		}
	}

	@Override
	public void create(Node parent) {
		for (Processor p : children) {
			p.create(parent);
		}
	}

}
