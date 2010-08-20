/*
 * $RCSfile: Collada14LoaderUtils.java,v $
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
 * $Date: 2010/05/04 20:17:23 $
 * $State: Exp $
 */
package org.jdesktop.j3d.loaders.collada.utils;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.jdesktop.j3d.loaders.collada.Collada14Loader;

public class Collada14LoaderUtils {

	public static BufferedImage readImage(URL url, String path) {

		/*
		 * String str = url.toString(); str = str.substring(0,
		 * str.lastIndexOf("/")); String[] strs = path.split("/"); for (String
		 * string : strs) { if (string.equals("..")) { str = str.substring(0,
		 * str.lastIndexOf("/")); } else if (string.equals(".")) {
		 * 
		 * } else { str += "/" + string; } }
		 */
		Logger.getLogger(Collada14Loader.COLLADA_LOADER).log(Level.INFO,
				"Trying to load image with ImageIO : " + url + path);

		BufferedImage image = null;
		try {
			// image = ImageIO.read(new URL(str));
			image = ImageIO.read(new URL(url, path));
		} catch (Exception e) {
			Logger.getLogger(Collada14Loader.COLLADA_LOADER).log(Level.WARNING,
					"Error loading image", e);

		}
		/*
		 * if (image == null) { try {
		 * Logger.getLogger(Collada14Loader.COLLADA_LOADER).log(Level.INFO,
		 * "Trying to load image with jimi : " + str); JimiReader reader =
		 * Jimi.createJimiReader(new URL(str)); Dimension d = reader.getSize();
		 * BufferedImage bi = new BufferedImage(d.width, d.height,
		 * BufferedImage.TYPE_INT_ARGB);
		 * bi.getGraphics().drawImage(reader.getImage(), 0, 0, null); image =
		 * bi; } catch (Exception e) { e.printStackTrace(); } }
		 */
		return image;
	}
}
