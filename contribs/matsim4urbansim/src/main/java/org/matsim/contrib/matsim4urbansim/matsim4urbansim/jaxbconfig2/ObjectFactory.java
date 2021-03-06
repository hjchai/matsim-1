//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.19 at 12:45:10 PM CEST 
//


package org.matsim.contrib.matsim4urbansim.matsim4urbansim.jaxbconfig2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the playground.tnicolai.matsim4opus.matsim4urbansim.jaxbconfig2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MatsimConfig_QNAME = new QName("", "matsim_config");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: playground.tnicolai.matsim4opus.matsim4urbansim.jaxbconfig2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ControlerType }
     * 
     */
    public ControlerType createControlerType() {
        return new ControlerType();
    }

    /**
     * Create an instance of {@link ConfigType }
     * 
     */
    public ConfigType createConfigType() {
        return new ConfigType();
    }

    /**
     * Create an instance of {@link AccessibilityParameterType }
     * 
     */
    public AccessibilityParameterType createAccessibilityParameterType() {
        return new AccessibilityParameterType();
    }

    /**
     * Create an instance of {@link PlanCalcScoreType }
     * 
     */
    public PlanCalcScoreType createPlanCalcScoreType() {
        return new PlanCalcScoreType();
    }

    /**
     * Create an instance of {@link Matsim4UrbansimType }
     * 
     */
    public Matsim4UrbansimType createMatsim4UrbansimType() {
        return new Matsim4UrbansimType();
    }

    /**
     * Create an instance of {@link UrbansimParameterType }
     * 
     */
    public UrbansimParameterType createUrbansimParameterType() {
        return new UrbansimParameterType();
    }

    /**
     * Create an instance of {@link FileType }
     * 
     */
    public FileType createFileType() {
        return new FileType();
    }

    /**
     * Create an instance of {@link InputPlansFileType }
     * 
     */
    public InputPlansFileType createInputPlansFileType() {
        return new InputPlansFileType();
    }

    /**
     * Create an instance of {@link MatsimConfigType }
     * 
     */
    public MatsimConfigType createMatsimConfigType() {
        return new MatsimConfigType();
    }

    /**
     * Create an instance of {@link Matsim4UrbansimContolerType }
     * 
     */
    public Matsim4UrbansimContolerType createMatsim4UrbansimContolerType() {
        return new Matsim4UrbansimContolerType();
    }

    /**
     * Create an instance of {@link StrategyType }
     * 
     */
    public StrategyType createStrategyType() {
        return new StrategyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MatsimConfigType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "matsim_config")
    public JAXBElement<MatsimConfigType> createMatsimConfig(MatsimConfigType value) {
        return new JAXBElement<MatsimConfigType>(_MatsimConfig_QNAME, MatsimConfigType.class, null, value);
    }

}
