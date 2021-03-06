//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.03.17 at 08:38:02 AM PDT 
//


package org.collada.colladaschema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}asset" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}convex_mesh"/>
 *           &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}mesh"/>
 *           &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}spline"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "asset",
    "mesh",
    "spline",
    "convexMesh",
    "extras"
})
@XmlRootElement(name = "geometry")
public class Geometry {

    protected Asset asset;
    protected Mesh mesh;
    protected Spline spline;
    @XmlElement(name = "convex_mesh")
    protected ConvexMesh convexMesh;
    @XmlElement(name = "extra")
    protected List<Extra> extras;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String name;

    /**
     * 
     * 						The geometry element may contain an asset element.
     * 						
     * 
     * @return
     *     possible object is
     *     {@link Asset }
     *     
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * 
     * 						The geometry element may contain an asset element.
     * 						
     * 
     * @param value
     *     allowed object is
     *     {@link Asset }
     *     
     */
    public void setAsset(Asset value) {
        this.asset = value;
    }

    /**
     * 
     * 							The geometry element may contain only one mesh or convex_mesh.
     * 							
     * 
     * @return
     *     possible object is
     *     {@link Mesh }
     *     
     */
    public Mesh getMesh() {
        return mesh;
    }

    /**
     * 
     * 							The geometry element may contain only one mesh or convex_mesh.
     * 							
     * 
     * @param value
     *     allowed object is
     *     {@link Mesh }
     *     
     */
    public void setMesh(Mesh value) {
        this.mesh = value;
    }

    /**
     * Gets the value of the spline property.
     * 
     * @return
     *     possible object is
     *     {@link Spline }
     *     
     */
    public Spline getSpline() {
        return spline;
    }

    /**
     * Sets the value of the spline property.
     * 
     * @param value
     *     allowed object is
     *     {@link Spline }
     *     
     */
    public void setSpline(Spline value) {
        this.spline = value;
    }

    /**
     * 
     * 							The geometry element may contain only one mesh or convex_mesh.
     * 							
     * 
     * @return
     *     possible object is
     *     {@link ConvexMesh }
     *     
     */
    public ConvexMesh getConvexMesh() {
        return convexMesh;
    }

    /**
     * 
     * 							The geometry element may contain only one mesh or convex_mesh.
     * 							
     * 
     * @param value
     *     allowed object is
     *     {@link ConvexMesh }
     *     
     */
    public void setConvexMesh(ConvexMesh value) {
        this.convexMesh = value;
    }

    /**
     * 
     * 						The extra element may appear any number of times.
     * 						Gets the value of the extras property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extras property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtras().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Extra }
     * 
     * 
     */
    public List<Extra> getExtras() {
        if (extras == null) {
            extras = new ArrayList<Extra>();
        }
        return this.extras;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
