//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.03.17 at 08:38:02 AM PDT 
//


package org.collada.colladaschema;

import javax.xml.bind.annotation.XmlEnum;


/**
 * <p>Java class for cg_pipeline_stage.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="cg_pipeline_stage">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VERTEX"/>
 *     &lt;enumeration value="FRAGMENT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum CgPipelineStage {

    VERTEX,
    FRAGMENT;

    public String value() {
        return name();
    }

    public static CgPipelineStage fromValue(String v) {
        return valueOf(v);
    }

}
