/*
 * $RCSfile: Capabilities.java,v $
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
 * $Date: 2007/02/09 17:17:00 $
 * $State: Exp $
 */

package org.jdesktop.j3d.utils.capability;

import java.util.ArrayList;

/** 
 * Convenience class for extracting capability information from a SceneGraph Object
 *
 * @author Paul Byrne
 * @version	1.6, 01/18/02
 */
public class Capabilities extends Object {
 
  /** Print a list of SET capabilities to stdout, one capability per line
 * @param obj The Object for which to print the capabilities
 */
  public static void printCapabilities( javax.media.j3d.SceneGraphObject obj ) {
    ArrayList list = new ArrayList();
    getCapabilities( obj, list );
    java.util.Iterator it = list.iterator();
    while(it.hasNext())
      System.out.println( (String)it.next() );
  }
  
  /** Extract the names of all the SET capabilities in the object.
 * The names (Strings) are appended to the arrayList
 * @param obj The object for which to extract the capability strings
 * @param capabilityStrings The ArrayList to which the capability names will be appended
 */
  public static void getCapabilities( javax.media.j3d.SceneGraphObject obj,
                                         java.util.ArrayList capabilityStrings ) {

    int value;
    String str;
    Class cl = obj.getClass();

    java.lang.reflect.Field[] fields = cl.getFields();

    try {
      for(int i=0; i<fields.length; i++) {
        str = fields[i].getName();
        value = fields[i].getInt( fields[i] );
        if (str.indexOf("ALLOW") != -1 || str.indexOf("ENABLE_")!=-1) {
          if (obj.getCapability(value))
          capabilityStrings.add( str );
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Internal Error");
    }
  }
  
  /**
   * Return an array of capability bits for the object
   *
   * If no capabilities are set then an array of length 0 is returned
   *
   * @param obj The object for which to extract the capability bits
   */
  public static int[] getCapabilities( javax.media.j3d.SceneGraphObject obj ) {
      ArrayList bits = new ArrayList();
      
    int value;
    String str;
    Class cl = obj.getClass();

    java.lang.reflect.Field[] fields = cl.getFields();

    try {
      for(int i=0; i<fields.length; i++) {
        str = fields[i].getName();
        value = fields[i].getInt( fields[i] );
        if (str.indexOf("ALLOW_") != -1 || str.indexOf("ENABLE_")!=-1 ) {
          if (obj.getCapability(value))
            bits.add( new Integer(value) );
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Internal Error");
    }
    
    int[] ret = new int[ bits.size() ];
    for(int i=0; i<ret.length; i++)
        ret[i] = ((Integer)bits.get(i)).intValue();
    
    return ret;
  }
}

