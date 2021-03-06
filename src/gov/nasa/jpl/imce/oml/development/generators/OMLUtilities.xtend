/*
 * Copyright 2016 California Institute of Technology ("Caltech").
 * U.S. Government sponsorship acknowledged.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * License Terms
 */
package gov.nasa.jpl.imce.oml.development.generators

import gov.nasa.jpl.imce.oml.model.common.CommonPackage
import java.util.ArrayList
import java.util.Comparator
import java.util.HashSet
import java.util.Map
import java.util.Set
import java.util.regex.Pattern
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage
import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.ETypedElement
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.plugin.EcorePlugin
import org.eclipse.emf.ecore.xcore.XcorePackage
import org.eclipse.emf.ecore.xcore.XcoreStandaloneSetup
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage
import org.eclipse.emf.ecore.xml.type.XMLTypePackage
import org.eclipse.xtext.XtextPackage
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.XFeatureCall
import org.eclipse.xtext.xbase.XMemberFeatureCall
import org.eclipse.emf.ecore.EEnumLiteral

class OMLUtilities {

	public val XtextResourceSet set
	public val EPackage c
	public val EPackage t
	public val EPackage g
	public val EPackage b
	public val EPackage d

	new() {
		XcoreStandaloneSetup.doSetup()

		val omlc = "/OMLCommon.xcore"
		val omlc_uri = URI.createPlatformResourceURI("/gov.nasa.jpl.imce.oml.model/model" + omlc, false)
		val omlt = "/OMLTerminologies.xcore"
		val omlt_uri = URI.createPlatformResourceURI("/gov.nasa.jpl.imce.oml.model/model" + omlt, false)
		val omlg = "/OMLGraphs.xcore"
		val omlg_uri = URI.createPlatformResourceURI("/gov.nasa.jpl.imce.oml.model/model" + omlg, false)
		val omlb = "/OMLBundles.xcore"
		val omlb_uri = URI.createPlatformResourceURI("/gov.nasa.jpl.imce.oml.model/model" + omlb, false)
		val omld = "/OMLDescriptions.xcore"
		val omld_uri = URI.createPlatformResourceURI("/gov.nasa.jpl.imce.oml.model/model" + omld, false)

		this.set = new XtextResourceSet()
		this.set.setPackageRegistry(EPackage.Registry.INSTANCE)
		val Map<URI, URI> uriMap = set.getURIConverter().getURIMap()
		uriMap.putAll(EcorePlugin.computePlatformURIMap(false))
		uriMap.put(URI.createURI("platform:/resource/org.eclipse.emf.ecore/model/Ecore.ecore"),
			URI.createURI(EcorePackage.getResource("/model/Ecore.ecore").toURI.toString))
		uriMap.put(URI.createURI("platform:/resource/org.eclipse.emf.ecore/model/Ecore.genmodel"),
			URI.createURI(EcorePackage.getResource("/model/Ecore.genmodel").toURI.toString))
		uriMap.put(URI.createURI("platform:/resource/org.eclipse.emf.ecore/model/XMLNamespace.ecore"),
			URI.createURI(XMLNamespacePackage.getResource("/model/XMLNamespace.ecore").toURI.toString))
		uriMap.put(URI.createURI("platform:/resource/org.eclipse.emf.ecore/model/XMLNamespace.genmodel"),
			URI.createURI(XMLNamespacePackage.getResource("/model/XMLNamespace.genmodel").toURI.toString))
		uriMap.put(URI.createURI("platform:/resource/org.eclipse.emf.ecore/model/XMLType.ecore"),
			URI.createURI(XMLTypePackage.getResource("/model/XMLType.ecore").toURI.toString))
		uriMap.put(URI.createURI("platform:/resource/org.eclipse.emf.ecore/model/XMLType.genmodel"),
			URI.createURI(XMLTypePackage.getResource("/model/XMLType.genmodel").toURI.toString))

		val ecore = set.getResource(URI.createURI(EcorePackage.eNS_URI), true)
		if (!ecore.errors.empty) {
			throw new IllegalArgumentException("Failed to load the Ecore package")
		}
		val genModel = set.getResource(URI.createURI(GenModelPackage.eNS_URI), true)
		if (!genModel.errors.empty) {
			throw new IllegalArgumentException("Failed to load the GenModel package")
		}
		val xtext = set.getResource(URI.createURI(XtextPackage.eNS_URI), true)
		if (!xtext.errors.empty) {
			throw new IllegalArgumentException("Failed to load the Xtext package")
		}
		val xcore = set.getResource(URI.createURI(XcorePackage.eNS_URI), true)
		if (!xcore.errors.empty) {
			throw new IllegalArgumentException("Failed to load the Xcore package")
		}
		uriMap.put(omlc_uri, URI.createURI(locateXcore(omlc)))
		uriMap.put(omlt_uri, URI.createURI(locateXcore(omlt)))
		uriMap.put(omlg_uri, URI.createURI(locateXcore(omlg)))
		uriMap.put(omlb_uri, URI.createURI(locateXcore(omlb)))
		uriMap.put(omld_uri, URI.createURI(locateXcore(omld)))

		val omlc_r = set.getResource(omlc_uri, true)
		val omlt_r = set.getResource(omlt_uri, true)
		val omlg_r = set.getResource(omlg_uri, true)
		val omlb_r = set.getResource(omlb_uri, true)
		val omld_r = set.getResource(omld_uri, true)
		
		this.c = omlc_r.getContents().filter(EPackage).get(0)
		this.t = omlt_r.getContents().filter(EPackage).get(0)
		this.g = omlg_r.getContents().filter(EPackage).get(0)
		this.b = omlb_r.getContents().filter(EPackage).get(0)
		this.d = omld_r.getContents().filter(EPackage).get(0)
	}
	
	static def String locateXcore(String path) {
		val url = CommonPackage.getResource(path)
		if (null === url)
			throw new IllegalArgumentException("locateXcore: failed to locate path: "+path)
	
		url.toURI.toString
	}
	
	static def String queryResolverName(EOperation op, String typePrefix) {
		if (null !== op.getEAnnotation("http://imce.jpl.nasa.gov/oml/OverrideVal")) {
			if (!op.EParameters.empty)
				throw new IllegalArgumentException("@OverrideVal is not applicable to an operation with formal parameters")
			if (op.isImplicitExtent)
				throw new IllegalArgumentException("@ImplicitExtent is not applicable to an @OverrideVal operation")
			"override val "+op.name
		} else {
			val kind = "def"
			val decl = if (null !== op.getEAnnotation("http://imce.jpl.nasa.gov/oml/Override")) "override "+kind else kind
			val args = '''«FOR p : op.EParameters SEPARATOR ",\n  "»«p.name»: «p.queryResolverType(typePrefix)»«ENDFOR»'''
			val impl = '''«IF (op.isImplicitExtent)»(implicit extent: «typePrefix»Extent)«ENDIF»'''
			decl+" "+op.name+"\n  ("+args+")"+impl
		}
	}
	
	static def String orderingClassName(EClass eClass) {
		if (eClass.orderingKeys.exists[f | f.container || f.lowerBound == 0] || eClass.name == "AnnotationEntry" || eClass.name == "Annotation")
			'''«eClass.name.toFirstLower»Ordering(implicit e: Extent)'''
		else
			'''«eClass.name.toFirstLower»Ordering'''
	}
	
	static def String orderingTableType(ETypedElement feature) {
		if (feature.lowerBound == 0) {
			if (feature.EType.name == "UUID")
				'''scala.Ordering.Option[UUID](scala.Ordering.String).compare(x.«feature.columnName»,y.«feature.columnName»)'''
			else
				throw new IllegalArgumentException("Implemented support for orderingAttributeType for: "+feature)
		} else
			'''x.«feature.columnName».compareTo(y.«feature.columnName»)'''
	}
	
	static def String orderingAttributeType(ETypedElement feature) {
		if (feature.lowerBound == 0) {
			if (feature.EType.name == "UUID")
				'''scala.Ordering.Option[java.util.UUID](UUIDOrdering).compare(x.«feature.columnName»,y.«feature.columnName»)'''
			else
				throw new IllegalArgumentException("Implemented support for orderingAttributeType for: "+feature)
		} else {
			'''x.«feature.columnName».compareTo(y.«feature.columnName»)'''	
		}
	}
	
	static def String orderingClassType(ETypedElement feature) {
		if (feature.container) {
			"scala.Ordering.Option[java.util.UUID](UUIDOrdering)"
		} else
			feature.EType.name.toFirstLower+"Ordering"
	}
	
	static def String queryResolverType(ETypedElement feature, String typePrefix) {
		val type = feature.EType
		//val isContainer = feature.container
		val scalaType = feature.scalaResolverTypeName
		switch type {
			case type instanceof EDataType: 
				if (feature.lowerBound == 0)
		  			"scala.Option["+scalaType+"]"
	   			else
	      			scalaType
			case type instanceof EClass:
				if (feature.lowerBound == 0) {
					if (feature.upperBound == -1) {
						val ann = feature.getEAnnotation("http://imce.jpl.nasa.gov/oml/Collection")?.details
						switch ann?.get("kind") ?: "" {
						case "Map(Seq)": {
							val key=ann.get("key")
							"scala.collection.immutable.Map["+key+", scala.collection.immutable.Seq["+typePrefix+type.name+"]]"				
						}
						case "Map": {
							val key=ann.get("key")
							"scala.collection.immutable.Map["+key+", "+typePrefix+type.name+"]"				
						}
						case "Set": 
							"scala.collection.immutable.Set[_ <: "+typePrefix+type.name+"]"		
					
						case "SortedSet": 
							"scala.collection.immutable.SortedSet["+typePrefix+type.name+"]"	
						default:
							throw new IllegalArgumentException("Multi-valued operation: "+feature.EClassContainer.name+"."+feature.name+" needs a @Collection(...) annotation!")		
						}	
					}
					else {
						if (feature.isLiteralFeature)
							"scala.Option["+scalaType+"]"
						else
							"scala.Option["+typePrefix+type.name+"]"
						}
				}
//				else if (isContainer)
//					"scala.Option[java.util.UUID] /* reference to a "+typePrefix+type.name+" */"
				else if (feature.isIRIReference)
					"gov.nasa.jpl.imce.oml.tables.taggedTypes.IRI"
				else if (feature.isLiteralStringFeature)
					"gov.nasa.jpl.imce.oml.tables.taggedTypes.StringDataType"
				else if (feature.isLiteralFeature)
					scalaType
				else
					typePrefix+type.name
			default:
				typePrefix+type.name+"//Default"
		}
	}
	
	static def String scalaResolverTypeName(ETypedElement feature) {
		val type = feature.EType
		switch (type.name) {
			case "EInt": "scala.Int"
			case "EBoolean": "scala.Boolean"
			case "EString": "scala.Predef.String"
			
			case "CardinalityRestrictionKind": "gov.nasa.jpl.imce.oml.tables.CardinalityRestrictionKind"
			case "DescriptionKind": "gov.nasa.jpl.imce.oml.tables.DescriptionKind"
			case "TerminologyKind": "gov.nasa.jpl.imce.oml.tables.TerminologyKind"
			
			case "UUID": "java.util.UUID"
			
			case "AbbrevIRI": "gov.nasa.jpl.imce.oml.tables.taggedTypes.AbbrevIRI"
			case "IRI": "gov.nasa.jpl.imce.oml.tables.taggedTypes.IRI"
			case "LanguageTagDataType": "gov.nasa.jpl.imce.oml.tables.taggedTypes.LanguageTagDataType"
			case "LiteralPattern": "gov.nasa.jpl.imce.oml.tables.taggedTypes.LiteralPattern"
			case "LocalName": "gov.nasa.jpl.imce.oml.tables.taggedTypes.LocalName"
			case "NamespacePrefix": "gov.nasa.jpl.imce.oml.tables.taggedTypes.NamespacePrefix"
			case "PositiveIntegerLiteral": "gov.nasa.jpl.imce.oml.tables.taggedTypes.PositiveIntegerLiteral"
			case "StringDataType": "gov.nasa.jpl.imce.oml.tables.taggedTypes.StringDataType"
			
			case "LiteralDateTime": "gov.nasa.jpl.imce.oml.tables.LiteralDateTime"
			case "LiteralNumber": "gov.nasa.jpl.imce.oml.tables.LiteralNumber"
			case "LiteralValue": "gov.nasa.jpl.imce.oml.tables.LiteralValue"
			
			default: type.name
		}
	}
	
	
	static def String schemaColumnTypeName(ETypedElement feature) {
		val type = feature.EType
		switch (type.name) {
			case "EInt": "Int"
			case "EBoolean": "Boolean"
			case "EString": "String"
			case type instanceof EClass: 
				if (feature.isIRIReference)
					"IRI (Foreign Key for: OML "+type.name+")"
				else
					"UUID (Foreign Key for: OML "+type.name+")"
			case "UUID": "UUID (Primary Key)"
			default: type.name
		}
	}
	
	
	static def String schemaColumnTypeDescription(ETypedElement feature) {
		val columnTypeName = feature.schemaColumnTypeName
		if (feature.lowerBound == 0)
		  "Option["+columnTypeName+"]"
	    else
	      columnTypeName
	}
	
	static def String constructorTypeName(ETypedElement feature) {
		val scalaType = feature.scalaTableTypeName
		if (feature.lowerBound == 0)
		  "scala.Option["+scalaType+"]"
	    else
	      scalaType
	}
	
	static def String constructorTypeRef(EClass eClass, ETypedElement feature) {
		val scalaType = scalaTableTypeRef(eClass, feature)
		if (feature.lowerBound == 0)
		  "scala.Option["+scalaType+"]"
	    else
	      scalaType
	}
	
	static def String circeDecoder(EClass eClass, ETypedElement feature) {
		val ftype = feature.EType.name
		if (ftype == "LiteralString") {
			'''c.downField("«feature.columnName»").as[taggedTypes.StringDataType](gov.nasa.jpl.imce.oml.taggedTypes.decodeArrayTag[taggedTypes.StringDataTypeTag])'''
		} else if (ftype == "LiteralPattern") {
			'''Decoder.decodeOption(gov.nasa.jpl.imce.oml.taggedTypes.decodeArrayTag[taggedTypes.LiteralPatternTag])(c.downField("«feature.columnName»").success.get)'''
		} else if (ftype == "LiteralValue") {
			'''c.downField("«feature.columnName»").as[LiteralValue](LiteralValue.decodeLiteralValueArray)'''
		} else {
			val scalaType = circeDecoderType(eClass, feature)
			if (feature.lowerBound == 0) {
				if (scalaType == "LiteralNumber" || scalaType == "LiteralValue" || scalaType == "LiteralDateTime")
					"Decoder.decodeOption(" + scalaType + ".decode" + scalaType + ")(c.downField(\"" +
						feature.columnName + "\").success.get)"
				else
					"Decoder.decodeOption(taggedTypes.decode" + scalaType + ")(c.downField(\"" + feature.columnName +
						"\").success.get)"
			} else if (scalaType == "LiteralValue" || scalaType.endsWith("Kind") || scalaType.startsWith("scala."))
				"c.downField(\"" + feature.columnName + "\").as[" + scalaType + "]"
			else
				"c.downField(\"" + feature.columnName + "\").as[taggedTypes." + scalaType + "]"
		}
	}
	
	static def String circeEncoder(EClass eClass, ETypedElement feature) {
		val ftype = feature.EType.name
		if (ftype == "LiteralString") {
			'''gov.nasa.jpl.imce.oml.taggedTypes.encodeArrayTag[taggedTypes.StringDataTypeTag](x.«feature.columnName»)'''
		} else if (ftype == "LiteralPattern") {
			'''Encoder.encodeOption(gov.nasa.jpl.imce.oml.taggedTypes.encodeArrayTag[taggedTypes.LiteralPatternTag]).apply(x.«feature.columnName»)'''
		} else if (ftype == "LiteralValue") {
			'''LiteralValue.encodeLiteralValueArray(x.«feature.columnName»)'''
		} else {
			val scalaType = circeEncoderFunction(eClass, feature)
			if (feature.lowerBound == 0)
				"Encoder.encodeOption("+scalaType+").apply(x."+feature.columnName+")"
	    		else
	    			scalaType+"(x."+feature.columnName+")"
	    }
	}
	
	static def String javaArgName(ETypedElement feature) {
		if (feature.lowerBound == 0)
		  feature.columnName+".asScala"
		else
		  feature.columnName
	}
	
	static def String javaTypeName(ETypedElement feature) {
		val scalaType = feature.scalaTableTypeName
		if (feature.lowerBound == 0)
		  "Optional["+scalaType+"]"
		else
		  scalaType
	}
	
	static def String jsArgName(ETypedElement feature) {
		if (feature.lowerBound == 0)
		  feature.columnName+".toOption"
		else
		  feature.columnName
	}
	
	static def String jsTypeName(ETypedElement feature) {
		val scalaType = feature.scalaTableTypeName
		if (feature.lowerBound == 0)
		  "scala.scalajs.js.UndefOr["+scalaType+"]"
		else
		  scalaType
	}
	
	static def String circeDecoderType(EClass eClass, ETypedElement feature) {
		val type = feature.EType
		switch (type.name) {
			case "EInt": "scala.Int"
			case "EBoolean": "scala.Boolean"
			case "EString": "scala.Predef.String"
			case "CardinalityRestrictionKind": "CardinalityRestrictionKind"
			case "DescriptionKind": "DescriptionKind"
			case "TerminologyKind": "TerminologyKind"
			case "UUID": eClass.name+"UUID"
			case type instanceof EDataType:
				type.name
			case type instanceof EClass: 
				if (feature.isIRIReference)
					"IRI"
				else if (feature.isLiteralDateTimeFeature)
					"LiteralDateTime"
				else if (feature.isLiteralNumberFeature)
					"LiteralNumber"
				else if (feature.isLiteralStringFeature)
					"StringDataType"
				else if (feature.isLiteralFeature)
					"LiteralValue"
				else
					type.name+"UUID"
			default: 
			    type.name
		}
	}
	
	static def String circeEncoderFunction(EClass eClass, ETypedElement feature) {
		val type = feature.EType
		switch (type.name) {
			case "EInt": "scala.Int"
			case "EBoolean": "Encoder.encodeBoolean"
			case "EString": "scala.Predef.String"
			case "CardinalityRestrictionKind": "CardinalityRestrictionKind.encodeCardinalityRestrictionKind"
			case "DescriptionKind": "DescriptionKind.encodeDescriptionKind"
			case "TerminologyKind": "TerminologyKind.encodeTerminologyKind"
			case "UUID": "taggedTypes.encode"+eClass.name+"UUID"
			case type instanceof EDataType:
				"taggedTypes.encode"+type.name
			case type instanceof EClass: 
				if (feature.isIRIReference)
					"taggedTypes.encodeIRI"
				else if (feature.isLiteralDateTimeFeature)
					"LiteralDateTime.encodeLiteralDateTime"
				else if (feature.isLiteralNumberFeature)
					"LiteralNumber.encodeLiteralNumber"
				else if (feature.isLiteralStringFeature)
					"taggedTypes.encodeStringDataType"
				else if (feature.isLiteralFeature)
					"LiteralValue.encodeLiteralValue"
				else
					"taggedTypes.encode"+type.name+"UUID"
			default: 
			    "taggedTypes.encode"+type.name
		}
	}
	
	static def String scalaTableTypeRef(EClass eClass, ETypedElement feature) {
		val type = feature.EType
		switch (type.name) {
			case "EInt": "scala.Int"
			case "EBoolean": "scala.Boolean"
			case "EString": "scala.Predef.String"
			case "CardinalityRestrictionKind": "CardinalityRestrictionKind"
			case "DescriptionKind": "DescriptionKind"
			case "TerminologyKind": "TerminologyKind"
			case "UUID": "taggedTypes."+eClass.name+"UUID"
			case type instanceof EDataType:
				"taggedTypes."+type.name
			case type instanceof EClass: 
				if (feature.isIRIReference)
					"taggedTypes.IRI"
				else if (feature.isLiteralDateTimeFeature)
					"LiteralDateTime"
				else if (feature.isLiteralNumberFeature)
					"LiteralNumber"
				else if (feature.isLiteralStringFeature)
					"taggedTypes.StringDataType"
				else if (feature.isLiteralFeature)
					"LiteralValue"
				else
					"taggedTypes."+type.name+"UUID"
			default: type.name
		}
	}
	
	static def String scalaTableTypeName(ETypedElement feature) {
		val type = feature.EType
		switch (type.name) {
			case "EInt": "scala.Int"
			case "EBoolean": "scala.Boolean"
			case "EString": "scala.Predef.String"
			case type instanceof EClass: 
				if (feature.isIRIReference)
					"IRI"
				else if (feature.isLiteralDateTimeFeature)
					"LiteralDateTime"
				else if (feature.isLiteralNumberFeature)
					"LiteralNumber"
				else if (feature.isLiteralStringFeature)
					"StringDataType"
				else if (feature.isLiteralFeature)
					"LiteralValue"
				else
					type.name+"UUID"
			default: type.name
		}
	}
	
	static def Iterable<EClass> FunctionalAPIClasses(EPackage ePkg) {
		ePkg.EClassifiers.filter(EClass).filter[isAPI]
	}
	
	static def Boolean isFunctionalAPIWithOrderingKeys(EClass eClass) {
		if (!eClass.isFunctionalAPI)
			return false
		else {
			val keys = eClass.orderingKeys
			if (keys.empty)
				return false
			else
				return true
		}
	}
	
	static def Iterable<ETypedElement> orderingKeys(EClass eClass) {
		eClass
		.functionalAPIOrOrderingKeyAttributes
		.filter[isOrderingKey]
	} 
	
	static def Iterable<ETypedElement> functionalAPIOrOrderingKeyAttributes(EClass eClass) {
		eClass
		.functionalAPIOrOrderingKeyFeatures
		.filter[!isInterface && (isFunctionalAttributeOrReferenceExceptContainer || isOrderingKey)]
	}
	
	static def Iterable<ETypedElement> functionalAPIOrOrderingKeyFeatures(EClass eClass) {
		val Set<ETypedElement> features = eClass
		.selfAndAllSupertypes
		.map[ETypedElements]
		.flatten
		.toSet
		
		val sorted = features
		.sortWith(new OMLFeatureCompare())
		
		sorted
	}
	
	static def Boolean hasSchemaOptionalAttributes(EClass eClass) {
		eClass.schemaAPIOrOrderingKeyAttributes.exists(a | a.lowerBound == 0)
	}
	
	static def Iterable<ETypedElement> schemaAPIOrOrderingKeyReferences(EClass eClass) {
		eClass.schemaAPIOrOrderingKeyAttributes.filter[isClassFeature && !isLiteralFeature]
	}
	
	static def Iterable<ETypedElement> schemaAPIOrOrderingKeyAttributes(EClass eClass) {
		eClass
		.schemaAPIOrOrderingKeyFeatures
		.filter[!isInterface && (isSchemaAttributeOrReferenceOrContainer || isOrderingKey)]
	}
	
	static def Iterable<ETypedElement> schemaAPIOrOrderingKeyFeatures(EClass eClass) {
		val Set<ETypedElement> features = eClass
		.selfAndAllSupertypes
		.map[ETypedElements]
		.flatten
		.toSet
		
		val sorted = features
		.sortWith(new OMLFeatureCompare())
		
		sorted
	}
	static def Iterable<ETypedElement> ETypedElements(EClass eClass) {
		val features = new HashSet<ETypedElement>()
		features.addAll(eClass.EStructuralFeatures.filter[isFunctionalAPIOrOrderingKey])
		features.addAll(eClass.EOperations.filter[isFunctionalAPIOrOrderingKey])
		features
	}
	
	static def Iterable<EClass> selfAndAllSupertypes(EClass eClass) {
		val parents = new HashSet(eClass.EAllSuperTypes)
		parents.add(eClass)
		parents
	}
	
	static def Iterable<EClass> ESuperClasses(EClass eClass) {
		eClass.ESuperTypes.sortBy[name]
	}
	
	static def Iterable<EClass> ESpecificClasses(EClass eClass) {
		eClass.EPackage.EClassifiers.filter(EClass).filter[ESuperTypes.contains(eClass)].sortBy[name]
	}
	
	static def Iterable<EStructuralFeature> APIStructuralFeatures(EClass eClass) {
		eClass.EStructuralFeatures.filter[isAPI && !isFactory  && (!isContainment || isLiteralFeature)]
	}
    
	static def Boolean isRootHierarchyClass(EClass eClass) {
		eClass.isAbstract && eClass.ESuperTypes.isEmpty && !eClass.orderingKeys.isEmpty
	}
	
	static def Boolean isSpecializationOfRootClass(EClass eClass) {
		!eClass.ESuperTypes.isEmpty && eClass.selfAndAllSupertypes.exists[isRootHierarchyClass]
	}
	
	static def Iterable<EOperation> APIOperations(EClass eClass) {
		eClass.EOperations.filter[isAPI]
	}
    
	static def Iterable<EStructuralFeature> getSortedDerivedAttributeSignature(EClass eClass) {
		eClass
		.getSortedAttributeSignature
		.filter[derived]
	}
	
	static def Iterable<EStructuralFeature> getSortedAttributeFactorySignature(EClass eClass) {
		eClass
		.selfAndAllSupertypes
		.map[EStructuralFeatures.filter[(isAPI || isFactory) && (!isContainment || isLiteralFeature) && !derived && !isUUID]]
		.flatten
		.sortWith(new OMLFeatureCompare())
	}
	
	static def EStructuralFeature lookupUUIDFeature(EClass eClass) {
		eClass
		.getSortedAttributeSignature
		.findFirst[isUUID]
	}
	
	static def EOperation lookupUUIDOperation(EClass eClass) {
		eClass.EAllOperations.findFirst[isUUID && isScala]
	}
	
	static def ETypedElement lookupUUIDTypedElement(EClass eClass) {
		val typedElements = new BasicEList<ETypedElement>()
		typedElements.addAll(eClass.getSortedAttributeSignature)
		typedElements.addAll(eClass.selfAndAllSupertypes.filter(EOperation).filter[!isOverride])
		typedElements.findFirst[isUUID]
	}
	
	static def Iterable<EStructuralFeature> getSortedAttributeSignatureExceptDerived(EClass eClass) {
		eClass
		.getSortedAttributeSignature
		.filter[!derived]
	}
	
	static def Iterable<EStructuralFeature> getSortedAttributeSignature(EClass eClass) {
		eClass
		.selfAndAllSupertypes
		.map[APIStructuralFeatures]
		.flatten
		.sortWith(new OMLFeatureCompare())
	}
	
	static def Iterable<EStructuralFeature> lookupCopyConstructorArguments(EClass eClass) {
		eClass.getSortedAttributeSignature.filter[isCopyConstructorArgument]
	}
	
	static def Boolean isUUIDFeature(EStructuralFeature sf) {
		null !== sf.EClassType?.lookupUUIDTypedElement
	}
	
	static def Boolean isEssential(EStructuralFeature sf) {
		sf.lowerBound == 1 && sf.upperBound == 1	
	}
	
    static def Boolean isUUIDDerived(EClass e) {
    		null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/DerivedUUID")
    }
    
    static def EStructuralFeature lookupUUIDNamespaceFeature(EClass e) {
    		val ns = e.selfAndAllSupertypes.map[getEAnnotation("http://imce.jpl.nasa.gov/oml/NamespaceUUID")?.details?.get("namespace")].filterNull
    		e.getSortedAttributeFactorySignature.findFirst[name == ns?.head]
    }
    
    static def Iterable<EStructuralFeature> lookupUUIDNamespaceFactors(EClass e) {
		e.selfAndAllSupertypes.map [ eClass |
			val factors = eClass.getEAnnotation("http://imce.jpl.nasa.gov/oml/NamespaceUUID")?.details?.get("factors")
			if (null === factors)
				new BasicEList<EStructuralFeature>()
			else {
				val factoredFeatures = new ArrayList<String>()
				factoredFeatures.addAll(factors.split(","))
				eClass.getSortedAttributeFactorySignature.filter[s|factoredFeatures.exists[f|f == s.name]]
			}
		].flatten.sortBy[name]
	}
	
	static def Boolean isCopyConstructorArgument(EStructuralFeature attribute) {
		null !== attribute.getEAnnotation("http://imce.jpl.nasa.gov/oml/CopyConstructor")
	}
	
	static def Iterable<EOperation> ScalaOperations(EClass eClass) {
		eClass.APIOperations.filter(op | op.isScala || null !== op.xExpressions) 
	}
	
    static def Iterable<XExpression> xExpressions(EOperation op) {
    	(new XcoreMapper()).getXOperation(op)?.body?.expressions
    }
    
	static def String queryBody(EOperation op) {
		val scalaCode = op.scalaAnnotation
		val xExpressions = op.xExpressions
		if (null !== scalaCode)
'''{
  «scalaCode»
}'''
		else if (null !== xExpressions)
			'''«FOR exp: xExpressions BEFORE "{\n  " SEPARATOR "\n  " AFTER "\n}"»«exp.toScala»«ENDFOR»'''
	}
    
	/*
	 * Transform an XText base XExpression to an equivalent Scala expression in concrete syntax (String).
	 * 
	 */
	static def String toScala(XExpression exp) {
		val result = switch exp {
			XFeatureCall: {
			    val n = NodeModelUtils.findActualNodeFor(exp)
			    val s = NodeModelUtils.getTokenText(n)
				s
			}
				
			XMemberFeatureCall: {
				val rF = exp.actualReceiver
				val rS = rF.toScala
				
				if (!exp.actualArguments.empty)
					throw new IllegalArgumentException(
					".toScala can only handle an XMemberFeatureCall for calling an operation with 0 arguments: "+
					exp.toString()+" in: "+exp.eResource.URI)
				
				val tF = exp.feature
				if (tF.eIsProxy)
					throw new IllegalArgumentException(
					"Cannot resolve an XMemberFeatureCall because the feature is a proxy; expression="+
					exp.toString()+" in: "+exp.eResource.URI)
				
				val tS = tF.simpleName
			    val s = rS+"."+tS+"()"
			    s
			}

			default:
				if (null === exp)
					"null /* ERROR!!! */"
				else
					exp.toString + "/* default(debug) */"
		}
		result
	}
	
	static def String queryBody(EStructuralFeature f) {
		val scalaCode = f.scalaAnnotation
		if (null !== scalaCode)
'''{
  «scalaCode»
}'''
		else '''// N/A'''
	}
    
    static def Boolean isUUID(ETypedElement e) {
    		e.name == "uuid"
    }
    
	static def Boolean isFunctionalAPIOrOrderingKey(ENamedElement e) {
	 	e.isFunctionalAPI || e.isOrderingKey
	}
	
    static def Boolean isFunctionalAPI(ENamedElement e) {
    	if (e.isSchema && e.isAPI) {
    		switch e {
    			EClass:
    			  !e.isAbstract
    	 		default:
    			  true
    	  }
 		} else
    	  false
    }
     
     
	static def Boolean isContainment(ETypedElement f) {
		switch f {
			EReference: 
				f.containment && f.isAPI
			default: 
				false
		}
	}
	
	static def Boolean isContainer(ETypedElement f) {
		switch f {
			EReference: 
				f.container && f.isAPI
			default: 
				false
		}
	}
	
	static def Boolean isBoolean(ETypedElement f) {
		val b1 = EcorePackage.Literals.EBOOLEAN.eResource.getURIFragment(EcorePackage.Literals.EBOOLEAN)
		val b2 = f.EType.eResource.getURIFragment(f.EType)
		val ok = b1 === b2
		ok
	}
	
	static def Boolean isFunctionalAttributeOrReferenceExceptContainer(ETypedElement f) {
		!f.isContainer && !f.isContainment
	}
	
	static def Boolean isLiteralDateTime(EClassifier type) {
		type.name == "LiteralDateTime"
	}
	
	static def Boolean isLiteralDateTimeFeature(ETypedElement f) {
		isLiteralDateTime(f.EType)
	}
	
	static def Boolean isLiteralNumber(EClassifier type) {
		type.name == "LiteralNumber"
	}
	
	static def Boolean isLiteralNumberFeature(ETypedElement f) {
		isLiteralNumber(f.EType)
	}
	
	static def Boolean isLiteralString(EClassifier type) {
		type.name == "LiteralString"
	}
	
	static def Boolean isLiteralStringFeature(ETypedElement f) {
		isLiteralString(f.EType)
	}
	
	static def Boolean isLiteralValue(EClassifier type) {
		type.name == "LiteralValue" || type.name == "LiteralNumber" || type.name == "LiteralDateTime" || type.name == "LiteralString"
	}
	
	static def Boolean isLiteralFeature(ETypedElement f) {
		isLiteralValue(f.EType)
	}
	
	static def Boolean isSchemaAttributeOrReferenceOrContainer(ETypedElement f) {
		switch f {
			EReference: 
				f.isSchema && (! f.containment || f.isLiteralFeature)
			default: 
				f.isSchema
		}
	}
	
	static def EClass EClassType(ETypedElement f) {
		val c = f.EType
		switch c {
			EClass:
				c
			default:
				null
		}
	}
	
	static def EClass EClassContainer(ETypedElement f) {
		val c = f.eContainer
		switch c {
			EClass:
				c
			default:
				null
		}
	}
	
	static def String getFeatureQuery(ETypedElement feature) {
		val c = feature.EClassContainer
		val op = c.EAllOperations.findFirst[name == feature.name]
		val name = feature.name
		if (null === op)
		  name
		else
		  name+"()"
	}
	
	static def Boolean isClassFeature(ETypedElement feature) {
		val type = feature.EType
		type instanceof EClass
	}
	
    static def Boolean isOrderingKey(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/IsOrderingKey")
    }
    
	static def Boolean isOverride(ETypedElement feature) {
		null !== feature.getEAnnotation("http://imce.jpl.nasa.gov/oml/Override")
	}
	
    static def Boolean isOO(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/NotFunctionalAPI")
    }
    
    static def Boolean isInterface(ENamedElement e) {
    		null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/FunctionalInterface")
    }
    
    static def Boolean isAPI(ENamedElement e) {
    	(null === e.getEAnnotation("http://imce.jpl.nasa.gov/oml/NotFunctionalAPI")) && 
    	switch (e) {
    		ETypedElement: {
    			val c = e.EType
    			switch (c) {
    				EClass:
    					(c.name != "LiteralValue") || !c.EAllSuperTypes.exists [ it.name == "LiteralValue" ]
    				default:
    					true
    			}
    		}
    		EClass:
   				 (e.name != "LiteralValue") && !e.EAllSuperTypes.exists [ it.name == "LiteralValue" ]
   			default:
   				true
   		}
    }
    
    static def Boolean isIRIReference(ENamedElement e) {
    		null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/IRIReference")
    }
    
    static def Boolean isExtentContainer(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ExtentContainer")
    }
    
    static def Boolean isExtentManaged(EClass e) {
    		e.selfAndAllSupertypes.exists[eClass |
    			null !== eClass.getEAnnotation("http://imce.jpl.nasa.gov/oml/ExtentManaged")
    		]
    }
    
    static def Boolean isFactory(ENamedElement e) {
    		null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/Factory")
    }
    
    static def Boolean isGlossary(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/Glossary")
    }
    
    static def Boolean isImplicitExtent(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ImplicitExtent")
    }
    
    static def Boolean isScala(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/Scala")
    }
    
    static def String scalaAnnotation(ETypedElement f) {
    	f.getEAnnotation("http://imce.jpl.nasa.gov/oml/Scala")?.details?.get("code")
    }
		
    static def String scalaTablesAnnotation(ETypedElement f) {
    	f.getEAnnotation("http://imce.jpl.nasa.gov/oml/Scala")?.details?.get("tables")
    }
    
    static def Boolean isSchema(ENamedElement e) {
    	null === e.getEAnnotation("http://imce.jpl.nasa.gov/oml/NotSchema") && !e.isResolverAPI
    }
		
    static def Boolean isResolverAPI(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ResolverAPI")
    }
     
    static def Boolean isValueTable(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ValueTable")
    }
   
	static def String pluralizeIfMany(String s, int cardinality) {
		if (cardinality > 1)
			pluralize(s)
		else
			s
	}

	static def String pluralize(String s) {
	  if (s.endsWith("y"))
	  	s.substring(0, s.length-1)+"ies"
	  else if (s.endsWith("x"))
	  	s+"es"
	  else if (s.endsWith("s"))
	  	s
	  else
	  	s+"s"
	}
	
	static def String lowerCaseInitialOrWord(String s) {
		if (s.startsWith("IRI"))
			"iri" + s.substring(3)
		else
			s.toFirstLower
	}
	
	static def String upperCaseInitialOrWord(String s) {
		if (s.startsWith("iri"))
			"IRI" + s.substring(3)
		else
			s.toFirstUpper
	}
	
	static def String tableVariableName(EClass eClass) {
	  val n = eClass.name
	  if (n.startsWith("IRI")) {
	  	"iri" + pluralize(n.substring(3))
	  } else {
	  	val m = Pattern.compile("^(\\p{Upper}+)(\\w+)$").matcher(n)
	  	if (!m.matches())
	  		throw new IllegalArgumentException("tableVariableName needs a class whose name begins with uppercase characters: " + eClass.name)
	 	m.group(1).toLowerCase + pluralize(m.group(2))
	  }
	}
	
	static class OMLTableCompare implements Comparator<EClass> {
		
		val knownTables = #[
		"Extent",
		
		// Modules
		
		"TerminologyBox",
		"TerminologyGraph", 
		"Bundle", 
		"DescriptionBox",
		
		"AnnotationProperty",
		
		// LogicalElement
		
		// ModuleElement (LogicalElement)
		
		// TerminologyBoxStatement (ModuleElement)
		
		// Term (TerminologyBoxStatement)
		
		// UnaryTermKind (Term
		
		"Aspect",
		"Concept",
		
		"Scalar",
		"Structure", 
		
		// ModuleEdges
		
		// ModuleEdges (TerminologyBox)
		
		"ConceptDesignationTerminologyAxiom",
		"TerminologyExtensionAxiom", 
		"TerminologyNestingAxiom",
		
		// ModuleEdges (Bundle)
		
		"BundledTerminologyAxiom",
		
		// ModuleEdges (DescriptionBox)
		
		"DescriptionBoxExtendsClosedWorldDefinitions",
		"DescriptionBoxRefinement",
		
		// DataRange (Term)
		
		"BinaryScalarRestriction", 
		"IRIScalarRestriction", 
		"NumericScalarRestriction",
		"PlainLiteralScalarRestriction",
		"ScalarOneOfRestriction",
		"ScalarOneOfLiteralAxiom",
		"StringScalarRestriction",
		"SynonymScalarRestriction",
		"TimeScalarRestriction",
		
		// DirectedBinaryRelationshipKind (Term)
		
		// DataRelationship (DirectedBinaryRelationshipKind)
		
		"EntityScalarDataProperty",
		"EntityStructuredDataProperty",
		
		"ScalarDataProperty",
		"StructuredDataProperty",
		
		// EntityRelationship (DirectedBinaryRelationshipKind)
		
		"ReifiedRelationship", 
		"ReifiedRelationshipRestriction",
		"ForwardProperty",
		"InverseProperty",
		
		"UnreifiedRelationship",
		
		// Rule (Term)
		
		"ChainRule",
		
		// ElementCrossReferenceTuple
		
		"RuleBodySegment",
		
		// SegmentPredicate (ElementCrossReferenceTuple)
		
		"SegmentPredicate",
		
		// TermAxiom (TerminologyBoxStatement)
		
		// EntityRestrictionAxiom (TermAxiom)
		
		"EntityExistentialRestrictionAxiom",
		"EntityUniversalRestrictionAxiom",
		
		// EntityScalarDataPropertyRestrictionAxiom (TermAxiom)
		
		"EntityScalarDataPropertyExistentialRestrictionAxiom",
		"EntityScalarDataPropertyParticularRestrictionAxiom",
		"EntityScalarDataPropertyUniversalRestrictionAxiom",
		
		// EntityStructuredDataPropertyRestrictionAxiom (TermAxiom)
		
		"EntityStructuredDataPropertyParticularRestrictionAxiom",
		
		// EntityStructuredDataPropertyRestrictionAxiom (nested)
		
		"RestrictionStructuredDataPropertyTuple",
		"RestrictionScalarDataPropertyValue",
		
		// SpecializationAxiom (TermAxiom)
		
		"AspectSpecializationAxiom",
		"ConceptSpecializationAxiom",
		"ReifiedRelationshipSpecializationAxiom",
		
		// Other TermAxioms
		
		"SubDataPropertyOfAxiom",
		"SubObjectPropertyOfAxiom",
		
		// TerminologyBundleStatement (ModuleElement)
		
		"RootConceptTaxonomyAxiom",
		
		// RootConceptTaxonomyAxiom (nested)
		
		"AnonymousConceptUnionAxiom",
		"SpecificDisjointConceptAxiom",
		
		// TerminologyInstanceAssertion (ModuleElement)
		
		// ConceptualEntitySingletonInstance (TerminologyInstanceAssertion)
		
		"ConceptInstance",
		"ReifiedRelationshipInstance",
		
		// TerminologyInstanceAssertion (ElementCrossReferenceTuple)
		
		"ReifiedRelationshipInstanceDomain",
		"ReifiedRelationshipInstanceRange",
		"UnreifiedRelationshipInstanceTuple",
		
		// TerminologyInstanceAssertion (ElementCrossReferenceTuple or ValueCrossReferenceTuple) 
		
		"SingletonInstanceStructuredDataPropertyValue",
		"SingletonInstanceScalarDataPropertyValue",
		"StructuredDataPropertyTuple",
		"ScalarDataPropertyValue",
		
		// NonLogicalElement
		
		"AnnotationPropertyValue"
		]
		
		override compare(EClass c1, EClass c2) {
			val name1 = c1.name
			val name2 = c2.name
			val i1 = knownTables.indexOf(name1)
			val i2 = knownTables.indexOf(name2)
			if (i1 > -1 && i2 > -1)
			   i1.compareTo(i2)
			else if (i1 > -1 && i2 == -1)
			   -1
			else if (i1 == -1 && i2 > -1)
			   1
			else
			   name1.compareTo(name2)   
		}
		
	}
	
	static class OMLFeatureCompare implements Comparator<ETypedElement> {
		
		val knownAttributes = #[
		"uuid", 
		"tboxUUID",
		"extentUUID",
		"terminologyBundleUUID",
		"bundledTerminologyUUID",
		"extendedTerminologyUUID",
		"nestingTerminologyUUID",
		"nestingContextUUID",
		"bundleUUID",
		"moduleUUID",
		"descriptionBoxUUID",
		"refiningDescriptionBoxUUID",
		"singletonConceptClassifierUUID",
		"singletonConceptualRelationshipClassifierUUID",
		"dataStructureTypeUUID",
		"superAspectUUID",
		"subEntityUUID",
		"superConceptUUID",
		"subConceptUUID",
		"axiomUUID",
		"keyUUID",
		"subjectUUID",
		"propertyUUID",
		"closedWorldDefinitionsUUID",
		"refinedDescriptionBoxUUID",
		"refiningDescriptionBoxUUID",
		"dataStructureTypeUUID",
		"structuredDataPropertyValueUUID",
		"singletonInstanceUUID",
		"structuredDataPropertyContextUUID",
		"structuredDataPropertyUUID",
		"scalarDataPropertyUUID",
		"structuredPropertyTupleUUID",
		"singletonConceptClassifierUUID",
		"singletonReifiedRelationshipClassifierUUID",
		"reifiedRelationshipInstanceUUID",
		"unreifiedRelationshipUUID",
		"forwardPropertyUUID",
		"inversePropertyUUID",
		"restrictedRelationUUID",
		"restrictedDomainUUID",
		"restrictedRangeUUID",
		"restrictedEntityUUID",
		"scalarPropertyUUID",
		"scalarRestrictionUUID",
		"domainUUID",
		"rangeUUID",
		"sourceUUID",
		"targetUUID",
		"superRelationshipUUID",
		"subRelationshipUUID",
		"rootUUID",
		"disjointTaxonomyParentUUID",
		"disjointLeafUUID",
		"conceptInstancesUUID",
		"reifiedRelationshipInstancesUUID",
		"reifiedRelationshipInstancesDomainsUUID",
		"reifiedRelationshipInstancesRangesUUID",
		"unreifiedRelationshipInstancesUUID",
		"singletonInstanceScalarDataPropertyValuesUUID",
		"singletonInstanceStructuredDataPropertyValuesUUID",
		"scalarDataPropertyValuesUUID",
		"structuredDataPropertyTuplesUUID",
		"kind",
		"isAbstract", 
		"isAsymmetric", 
		"isEssential", 
		"isFunctional",
		"isInverseEssential",
		"isInverseFunctional", 
		"isIrreflexive", 
		"isReflexive",
		"isSymmetric", 
		"isTransitive",
		"isIdentityCriteria",
		"minExclusive",
		"minInclusive",
		"maxExclusive",
		"maxInclusive",
		"length",
		"minLength",
		"maxLength",
		"nsPrefix",
		"name",
		"bodySegmentUUID",
		"predicateUUID",
		"reifiedRelationshipSourceUUID",
		"reifiedRelationshipInverseSourceUUID",
		"reifiedRelationshipTargetUUID",
		"reifiedRelationshipInverseTargetUUID",
		"unreifiedRelationshipInverseUUID",
		"langRange",
		"pattern",
		"unreifiedPropertyName",
		"unreifiedInversePropertyName",
		"iri",
		"value",
		"scalarPropertyValue",
		"literalValue",
		"annotationsUUID",
		"boxStatementsUUID",
		"boxAxiomsUUID",
		"bundleStatementsUUID",
		"bundleAxiomsUUID"
		]
		override compare(ETypedElement o1, ETypedElement o2) {
			val name1 = o1.columnName
			val name2 = o2.columnName
			val i1 = knownAttributes.indexOf(name1)
			val i2 = knownAttributes.indexOf(name2)
			if (i1 > -1 && i2 > -1)
			   i1.compareTo(i2)
			else if (i1 > -1 && i2 == -1)
			   -1
			else if (i1 == -1 && i2 > -1)
			   1
			else
			   name1.compareTo(name2)   
		}
		
	}
	
	static def String columnUUID(ETypedElement feature) {
		if (feature instanceof EReference) { 
			if (feature.isIRIReference)
				"oug.namespaceUUID("+feature.name+"IRI).toString" 
			else
				feature.name+"UUID" 
		} else 
			feature.name
	}
	
	static def Boolean isXRefColumn(ETypedElement feature) {
		if (feature instanceof EReference) { 
			if (feature.isIRIReference)
				false
			else if (feature.isLiteralFeature)
				false
			else
				true
		} else 
			false
	}
	
	static def String columnName(ETypedElement feature) {
		if (feature instanceof EReference) { 
			if (feature.isIRIReference)
				feature.name+"IRI" 
			else if (feature.isLiteralFeature)
				feature.name
			else if (feature.name == "iri")
				"iri"
			else
				feature.name+"UUID" 
		} else 
			feature.name
	}
	
	static def String enumLiteralName(EEnumLiteral lit) {
		switch lit.name {
			case "Min":
				"MinCardinalityRestriction"
			case "Max":
				"MaxCardinalityRestriction"
			case "Exact":
				"ExactCardinalityRestriction"
			default:
				lit.name
		}
	}
	
	static def String markDown(ENamedElement e) {
		val doc = e.getEAnnotation("http://www.eclipse.org/emf/2002/GenModel")?.details?.get("documentation") ?: ""
		doc
	}
	
	static def String doc(ENamedElement e, String indent) {
		val doc = e.getEAnnotation("http://www.eclipse.org/emf/2002/GenModel")?.details?.get("documentation") ?: ""
		if (doc.empty) 
		doc
		else 
		"/*\n"+indent+" * "+doc.replaceAll("\n","\n"+indent+" * ")+"\n"+indent+" */\n"+indent	
	}
	
	static def String copyright() '''
		/*
		 * Copyright 2016 California Institute of Technology ("Caltech").
		 * U.S. Government sponsorship acknowledged.
		 *
		 * Licensed under the Apache License, Version 2.0 (the "License");
		 * you may not use this file except in compliance with the License.
		 * You may obtain a copy of the License at
		 *
		 *     http://www.apache.org/licenses/LICENSE-2.0
		 *
		 * Unless required by applicable law or agreed to in writing, software
		 * distributed under the License is distributed on an "AS IS" BASIS,
		 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
		 * See the License for the specific language governing permissions and
		 * limitations under the License.
		 * License Terms
		 */
		
	'''

	static def String copyrightSQL() '''
		--
		-- Copyright 2016 California Institute of Technology ("Caltech").
		-- U.S. Government sponsorship acknowledged.
		--
		-- Licensed under the Apache License, Version 2.0 (the "License");
		-- you may not use this file except in compliance with the License.
		-- You may obtain a copy of the License at
		--
		--     http://www.apache.org/licenses/LICENSE-2.0
		--
		-- Unless required by applicable law or agreed to in writing, software
		-- distributed under the License is distributed on an "AS IS" BASIS,
		-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
		-- See the License for the specific language governing permissions and
		-- limitations under the License.
		-- License Terms
		--
	'''
}