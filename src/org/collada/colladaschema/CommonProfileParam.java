//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.03.17 at 08:38:02 AM PDT 
//


package org.collada.colladaschema;

import javax.xml.bind.annotation.XmlEnum;


/**
 * <p>Java class for Common_profile_param.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Common_profile_param">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="A"/>
 *     &lt;enumeration value="ANGLE"/>
 *     &lt;enumeration value="B"/>
 *     &lt;enumeration value="DOUBLE_SIDED"/>
 *     &lt;enumeration value="G"/>
 *     &lt;enumeration value="P"/>
 *     &lt;enumeration value="Q"/>
 *     &lt;enumeration value="R"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="T"/>
 *     &lt;enumeration value="TIME"/>
 *     &lt;enumeration value="U"/>
 *     &lt;enumeration value="V"/>
 *     &lt;enumeration value="W"/>
 *     &lt;enumeration value="X"/>
 *     &lt;enumeration value="Y"/>
 *     &lt;enumeration value="Z"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum CommonProfileParam {

    A,
    ANGLE,
    B,
    DOUBLE_SIDED,
    G,
    P,
    Q,
    R,
    S,
    T,
    TIME,
    U,
    V,
    W,
    X,
    Y,
    Z;

    public String value() {
        return name();
    }

    public static CommonProfileParam fromValue(String v) {
        return valueOf(v);
    }

}
