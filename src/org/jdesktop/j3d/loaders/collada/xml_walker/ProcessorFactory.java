/*
 * $RCSfile: ProcessorFactory.java,v $
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.jdesktop.j3d.loaders.collada.Collada14Loader;

/**
 * Creates walker objects from collada schema objects
 * 
 * @author paulby
 */
public class ProcessorFactory {

	protected static Logger logger =
			Logger.getLogger(Collada14Loader.COLLADA_LOADER);

	private static Exception lastException;

	private static final String walkerPackage =
			"org.jdesktop.j3d.loaders.collada.xml_walker.";

	/**
	 * Create a procesor to handle this schemaObject
	 * 
	 * @param schemaObj
	 * @param parentProcessor
	 * @return
	 */
	public static Processor createProcessor(Object schemaObj,
			Processor parentProcessor) {
		if (schemaObj == null)
			return null;

		Class schemaClass = schemaObj.getClass();
		String schemaClassName = schemaClass.getName();
		String schemaObjName =
				schemaClassName.substring(schemaClassName.lastIndexOf('.') + 1);
		if (schemaObjName.indexOf('$') != 0)
			schemaObjName =
					schemaObjName.substring(schemaObjName.lastIndexOf('$') + 1);
		try {
			Class walkerClass =
					Class.forName(walkerPackage + schemaObjName + "Processor");
			Constructor con =
					walkerClass.getConstructor(new Class[] { schemaClass,
							Processor.class });
			return (Processor) con.newInstance(schemaObj, parentProcessor);
		} catch (ClassNotFoundException ex) {

			// ken: don't repeat output
			if (lastException == null
					|| !lastException.getMessage().equals(ex.getMessage())) {
				logger.warning("No Handler for " + schemaClass);
			}
			lastException = ex;

		} catch (NoSuchMethodException ex) {
			logger.warning("No constructor " + schemaObjName + "("
					+ schemaClassName + ")");
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
