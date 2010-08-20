/*
 * $RCSfile: Collada14Loader.java,v $
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
 * $Date: 2010/06/17 20:35:01 $
 * $State: Exp $
 */

package org.jdesktop.j3d.loaders.collada;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.BranchGroup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.collada.colladaschema.Asset;
import org.collada.colladaschema.COLLADA;
import org.collada.colladaschema.UpAxisType;
import org.jdesktop.j3d.loaders.collada.xml_walker.ElementCache;
import org.jdesktop.j3d.loaders.collada.xml_walker.Processor;
import org.jdesktop.j3d.loaders.collada.xml_walker.ProcessorFactory;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.LoaderBase;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;

/**
 * A Java 3D Loader for the Collada interchange format, schema version 1.4.x.<br>
 * This code was 'adopted' by user mcneillk, and is maintained with the
 * participation of the Java 3D community.
 * 
 * @author paulby
 * @author mcneillk
 */
public class Collada14Loader extends LoaderBase {

	public static final String COLLADA_LOADER = "collada.loader";

	private float scale = 1f;
	private boolean zUp = false;

	private JAXBContext jaxbContext;

	/**
	 * Set this to a different level using the logging API, if desired, before
	 * constructing this class.
	 */

	public Collada14Loader() {
	}

	/**
	 * Whether the local 'Z' axis is UP.<br>
	 * If true, callers may want to do a Transform.rotX(-Math.PI / 2d) on the
	 * scene's top BranchGroup after loading.
	 * 
	 * @return true if Z is up, false otherwise
	 */
	public boolean isZUp() {
		return zUp;
	}

	/**
	 * If not equal to one, callers may wish to apply the scale the scene's
	 * BranchGroup to scale geometry to Java3D 'normalized' meters.
	 * 
	 * @return a float between 0 - 1, indicating the scale to apply.
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Loads the file with the given path. NOTE: the JAXBContext for each loader
	 * will be re-used for subsequent calls to this method. The method must be
	 * synchronized externally if it is to be shared by multiple threads.
	 */
	public Scene load(String filename) throws FileNotFoundException,
			IncorrectFormatException, ParsingErrorException {
		try {
			javax.xml.bind.Unmarshaller unmarshaller =
					getJaxbContext().createUnmarshaller();
			File file = new File(filename);

			org.collada.colladaschema.COLLADA collada =
					(org.collada.colladaschema.COLLADA) unmarshaller
							.unmarshal(file);

			ElementCache.cache().setLoadingURL(file.toURI().toURL());
			return doLoad(collada);
		} catch (JAXBException ex) {
			Logger.getLogger(COLLADA_LOADER).log(Level.SEVERE, null, ex);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	private JAXBContext getJaxbContext() throws JAXBException {

		if (jaxbContext != null) {
			return jaxbContext;
		}
		jaxbContext =
				javax.xml.bind.JAXBContext
						.newInstance("org.collada.colladaschema");

		return jaxbContext;
	}

	/**
	 * @see #load(String)
	 */
	public Scene load(URL url) throws FileNotFoundException,
			IncorrectFormatException, ParsingErrorException {
		try {
			ElementCache.cache().setLoadingURL(url);
			Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
			COLLADA collada =
					(org.collada.colladaschema.COLLADA) unmarshaller
							.unmarshal(url);

			return doLoad(collada);
		} catch (JAXBException ex) {
			Logger.getLogger(COLLADA_LOADER).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public Scene load(Reader arg0) throws FileNotFoundException,
			IncorrectFormatException, ParsingErrorException {
		throw new UnsupportedOperationException("Not supported.");
	}

	private Scene doLoad(COLLADA collada) {

		if (Logger.getLogger(COLLADA_LOADER).getLevel() == null) {
			Logger.getLogger(COLLADA_LOADER).setLevel(Level.WARNING);
		}
		
		Logger.getLogger(COLLADA_LOADER).log(Level.WARNING,
				"Collada Schema Version " + collada.getVersion());

		Asset asset = collada.getAsset();
		Asset.Unit unit = asset.getUnit();
		UpAxisType upAxis = asset.getUpAxis();

		if (unit != null) {
			scale = (float) unit.getMeter();
			Logger.getLogger(COLLADA_LOADER).log(
					Level.WARNING,
					"Units: " + unit.getName() + "; Java3D scale factor is "
							+ unit.getMeter());

		}
		if (upAxis != null) {
			if (upAxis == UpAxisType.Z_UP) {
				zUp = true;
			}
			Logger.getLogger(COLLADA_LOADER).log(Level.WARNING,
					"Up Axis is " + upAxis);

		}
		// TODO:load by processor
		ElementCache.cache().setAsset(asset);

		List<Object> lib =
				collada
						.getLibraryLightsAndLibraryGeometriesAndLibraryAnimationClips();

		BranchGroup root = new BranchGroup();

		Processor lgtp = null;

		for (Object o : lib) {
			Processor p = ProcessorFactory.createProcessor(o, null);
		}
		lgtp = ProcessorFactory.createProcessor(collada.getScene(), null);

		lgtp.create(root);

		if (asset.getUnit() != null) {
			scale = (float) asset.getUnit().getMeter();

		}

		SceneBase ret = new SceneBase();
		ret.setSceneGroup(root);

		return ret;
	}

}
