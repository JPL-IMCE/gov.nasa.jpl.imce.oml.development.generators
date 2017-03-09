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
package gov.nasa.jpl.imce.oml.generators

import java.util.Comparator
import java.util.HashSet
import java.util.Set
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.ETypedElement
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.XFeatureCall
import org.eclipse.xtext.xbase.XMemberFeatureCall
import gov.nasa.jpl.imce.oml.model.extensions.OMLXcorePackages

class OMLUtilities extends OMLXcorePackages {

	static def String queryResolverName(EOperation op, String typePrefix) {
		val kind = "def"
		val decl = if (null !== op.getEAnnotation("http://imce.jpl.nasa.gov/oml/Override")) "override "+kind else kind
		val args = '''«FOR p : op.EParameters SEPARATOR ",\n  "»«p.name»: «p.queryResolverType(typePrefix)»«ENDFOR»'''
		decl+" "+op.name+"\n  ("+args+")"
	}
	
	static def String queryResolverType(ETypedElement feature, String typePrefix) {
		val type = feature.EType
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
						}
					}
					else
						"scala.Option["+typePrefix+type.name+"]"
				}
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
			case "AbbrevIRI": "gov.nasa.jpl.imce.oml.tables.AbbrevIRI"
			case "DescriptionKind": "gov.nasa.jpl.imce.oml.tables.DescriptionKind"
			case "IRI": "gov.nasa.jpl.imce.oml.tables.IRI"
			case "LangRange": "gov.nasa.jpl.imce.oml.tables.LangRange"
			case "LexicalNumber": "gov.nasa.jpl.imce.oml.tables.LexicalNumber"
			case "LexicalTime": "gov.nasa.jpl.imce.oml.tables.LexicalTime"
			case "LexicalValue": "gov.nasa.jpl.imce.oml.tables.LexicalValue"
			case "LocalName": "gov.nasa.jpl.imce.oml.tables.LocalName"
			case "NamespacePrefix": "gov.nasa.jpl.imce.oml.tables.NamespacePrefix"
			case "Pattern": "gov.nasa.jpl.imce.oml.tables.Pattern"
			case "UUID": "java.util.UUID"
			case "TerminologyKind": "gov.nasa.jpl.imce.oml.tables.TerminologyKind"
			default: "resolver.api."+type.name
		}
	}
	
	
	static def String schemaColumnTypeName(ETypedElement feature) {
		val type = feature.EType
		switch (type.name) {
			case "EInt": "Int"
			case "EBoolean": "Boolean"
			case "EString": "String"
			case type instanceof EClass: "UUID (Foreign Key for: OML "+type.name+")"
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
	
	static def String scalaTableTypeName(ETypedElement feature) {
		val type = feature.EType
		switch (type.name) {
			case "EInt": "scala.Int"
			case "EBoolean": "scala.Boolean"
			case "EString": "scala.Predef.String"
			case type instanceof EClass: "UUID"
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
	
	static def Boolean hasOptionalAttributes(EClass eClass) {
		eClass.functionalAPIOrOrderingKeyAttributes.exists(a | a.lowerBound == 0)
	}
	
	static def Iterable<ETypedElement> functionalAPIOrOrderingKeyAttributes(EClass eClass) {
		eClass
		.functionalAPIOrOrderingKeyFeatures
		.filter[!isInterface && (isAttributeOrReferenceOrContainer || isOrderingKey)]
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
		eClass.EStructuralFeatures.filter[isAPI]
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
    
	static def Boolean isAttributeOrReferenceOrContainer(EStructuralFeature f) {
		switch f {
			EReference: 
				! f.containment
			default: true
		}
	}
	
	static def Iterable<EStructuralFeature> getSortedDerivedAttributeSignature(EClass eClass) {
		eClass
		.getSortedAttributeSignature
		.filter[derived]
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
					throw new IllegalArgumentException(".toScala can only handle an XMemberFeatureCall for calling an operation with 0 arguments.")
				
				val tF = exp.feature
				if (tF.eIsProxy)
					throw new IllegalArgumentException("Cannot resolve an XMemberFeatureCall because the feature is a proxy; expression="+exp.toString()+" in: "+exp.eResource.URI)
				
				val tS = tF.simpleName
			    val s = rS+"."+tS+"()"
			    s
			}

			default:
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
     
	static def Boolean isAttributeOrReferenceOrContainer(ETypedElement f) {
		switch f {
			EReference: 
				f.isSchema && ! f.containment
			default: 
				f.isSchema
		}
	}
	
	static def EClass EClassContainer(ETypedElement f) {
		val c = f.eContainer
		switch c {
			EClass:
				c
		}
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
    		null === e.getEAnnotation("http://imce.jpl.nasa.gov/oml/NotFunctionalAPI")
    }
    
    static def Boolean isGlossary(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/Glossary")
    }
    
    static def Boolean isScala(ENamedElement e) {
    	null !== e.getEAnnotation("http://imce.jpl.nasa.gov/oml/Scala")
    }
    
    static def String scalaAnnotation(ETypedElement f) {
    	f.getEAnnotation("http://imce.jpl.nasa.gov/oml/Scala")?.details?.get("code")
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
	  if (s.endsWith("y")) { 
	  	s.substring(0, s.length-1)+"ies"
	  } else {
	  	s+"s"
	  }	 
	}
	
	static class OMLTableCompare implements Comparator<EClass> {
		
		val knownTables = #[
		"Annotation",
		"AnnotationEntry",
		"AnnotationProperty",
		"AnnotationPropertyTable",
		"AnnotationSubjectPropertyValue",
		"AnnotationSubjectTable",
		"Extent", 
		"TerminologyGraph", 
		"Bundle", 
		"ConceptDesignationTerminologyAxiom",
		"TerminologyExtensionAxiom", 
		"TerminologyNestingAxiom",
		"Aspect",
		"Concept",
		"ReifiedRelationship", 
		"UnreifiedRelationship",
		"Scalar",
		"Structure", 
		"BinaryScalarRestriction", 
		"IRIScalarRestriction", 
		"NumericScalarRestriction",
		"PlainLiteralScalarRestriction",
		"ScalarOneOfRestriction",
		"StringScalarRestriction",
		"SynonymScalarRestriction",
		"TimeScalarRestriction",
		"EntityScalarDataProperty",
		"EntityStructuredDataProperty",
		"ScalarDataProperty",
		"StructuredDataProperty",
		"AspectSpecializationAxiom",
		"ConceptSpecializationAxiom",
		"ReifiedRelationshipSpecializationAxiom",
		"EntityExistentialRestrictionAxiom",
		"EntityUniversalRestrictionAxiom",
		"EntityScalarDataPropertyExistentialRestrictionAxiom",
		"EntityScalarDataPropertyParticularRestrictionAxiom",
		"EntityScalarDataPropertyUniversalRestrictionAxiom",
		"ScalarOneOfLiteralAxiom",
		"BundledTerminologyAxiom",
		"AnonymousConceptTaxonomyAxiom",
		"RootConceptTaxonomyAxiom",
		"SpecificDisjointConceptAxiom"
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
		"singletonReifiedRelationshipClassifierUUID",
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
		"structuredDataPropertyUUID",
		"scalarDataPropertyUUID",
		"structuredPropertyTupleUUID",
		"singletonConceptClassifierUUID",
		"singletonReifiedRelationshipClassifierUUID",
		"reifiedRelationshipInstanceUUID",
		"unreifiedRelationshipUUID",
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
	
	static def String columnName(ETypedElement feature) {
		if (feature instanceof EReference) feature.name+"UUID" else feature.name
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

}