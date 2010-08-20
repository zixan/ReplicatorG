/*
 * $RCSfile: ElementCache.java,v $
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

import java.net.URL;
import java.util.HashMap;

import org.collada.colladaschema.Asset;
import org.collada.colladaschema.CommonNewparamType;
import org.collada.colladaschema.FxNewparamCommon;

/**
 * 
 * @author paulby
 */
public class ElementCache {

	private HashMap<String, SourceProcessor> sourceMap = new HashMap<String, SourceProcessor>();
	private HashMap<String, VerticesProcessor> verticesMap = new HashMap<String, VerticesProcessor>();
	private HashMap<String, float[]> floatArrayMap = new HashMap<String, float[]>();
	private HashMap<String, LightProcessor> lightMap = new HashMap<String, LightProcessor>();
	private HashMap<String, String> materialMap = new HashMap<String, String>();
	private HashMap<String, String> imageMap = new HashMap<String, String>();

	private HashMap<String, CommonNewparamType> newparamMap = new HashMap<String, CommonNewparamType>();

	private HashMap<String, Processor> map = new HashMap<String, Processor>();

	private static ElementCache elementCache = new ElementCache();

	private URL loadingURL = null;

	private Asset asset;

	private ElementCache() {
	}

	public static ElementCache cache() {
		return elementCache;
	}

	public void putSource(String id, SourceProcessor element) {
		// System.out.println("---> adding source " + id);
		sourceMap.put(id, element);
	}

	public SourceProcessor getSource(String id) {
		return sourceMap.get(trim(id));
	}

	public void putVertices(String id, VerticesProcessor element) {
		verticesMap.put(id, element);
	}

	public VerticesProcessor getVertices(String id) {
		return verticesMap.get(trim(id));
	}

	public void putFloatArray(String id, float[] floatArray) {
		assert (id != null);
		floatArrayMap.put(id, floatArray);
	}

	public float[] getFloatArray(String id) {
		return floatArrayMap.get(trim(id));
	}

	public void putLight(String id, LightProcessor light) {
		lightMap.put(id, light);
	}

	public LightProcessor getLight(String id) {
		return lightMap.get(trim(id));
	}

	public void putMaterial(String symbol, String target) {
		materialMap.put(symbol, target);
	}

	public String getMaterial(String symbol) {
		return materialMap.get(symbol);
	}

	public void putImage(String symbol, String target) {
		imageMap.put(symbol, target);
	}

	public String getImage(String symbol) {
		return imageMap.get(symbol);
	}

	public void putNewParam(String symbol, CommonNewparamType target) {
		newparamMap.put(symbol, target);
	}

	public CommonNewparamType getNewParam(String symbol) {
		return newparamMap.get(symbol);
	}

	public void put(String id, Processor proc) {
		map.put(id, proc);
	}

	public Processor get(String id) {
		return map.get(trim(id));
	}

	/**
	 * Trim leading #
	 */
	private String trim(String id) {
		if (id == null) {
			return "";
		}
		return id.substring(1, id.length());
	}

	public URL getLoadingURL() {
		return loadingURL;
	}

	public void setLoadingURL(URL loadingURL) {
		this.loadingURL = loadingURL;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

}
