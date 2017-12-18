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

import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.List
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage

class OMLSpecificationOMLZipGenerator extends OMLUtilities {
	
	static def void main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.zip project")
			System.exit(1)
		}	
		val gen = new OMLSpecificationOMLZipGenerator()
		val dir = args.get(0)
		var ok = false
		try {
			gen.generate(dir)	
			ok = true
		} catch (Throwable t) {
			System.err.println(t.getMessage)
			t.printStackTrace(System.err)
		} finally {
			if (ok)
				System.out.println("Done")
			else
				System.err.println("Abnormal exit!")
		}
	}
	
	
	def generate(String targetDir) {
		val ePackages = #[c, t, g, b, d]
      	val packageQName = "gov.nasa.jpl.imce.oml.zip"
      	
      	val bundlePath = Paths.get(targetDir)
		
		val oml_Folder = bundlePath.resolve("src/gov/nasa/jpl/imce/oml/zip")
		oml_Folder.toFile.mkdirs	
		
		generate(
      		ePackages, 
      		oml_Folder.toAbsolutePath.toString, 
      		packageQName,
      		"OMLSpecificationTables")
      		
		
	}
	
	def generate(List<EPackage> ePackages, String targetFolder, String packageQName, String tableName) {
		val tablesFile = new FileOutputStream(new File(targetFolder + File::separator + tableName + ".xtend"))
		try {
			tablesFile.write(generateTablesFile(ePackages, packageQName, tableName).bytes)
		} finally {
			tablesFile.close
		}

	}
	
	def String generateTablesFile(List<EPackage> ePackages, String packageQName, String tableName) {
		val eClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortWith(new OMLTableCompare())
	'''
		«copyright»

		package «packageQName»
		
		import java.io.BufferedReader
		import java.io.ByteArrayOutputStream
		import java.io.File
		import java.io.InputStreamReader
		import java.io.PrintWriter
		import java.lang.IllegalArgumentException
		import java.nio.charset.StandardCharsets
		import java.util.ArrayList
		import java.util.Collections
		import java.util.HashMap
		import java.util.Map
		import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
		import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
		import org.apache.commons.compress.archivers.zip.ZipFile
		import org.eclipse.emf.common.util.URI
		import org.eclipse.xtext.xbase.lib.Pair
		
		import gov.nasa.jpl.imce.oml.model.extensions.OMLTables
		import gov.nasa.jpl.imce.oml.model.bundles.AnonymousConceptUnionAxiom
		import gov.nasa.jpl.imce.oml.model.bundles.Bundle
		import gov.nasa.jpl.imce.oml.model.bundles.BundledTerminologyAxiom
		import gov.nasa.jpl.imce.oml.model.bundles.ConceptTreeDisjunction
		import gov.nasa.jpl.imce.oml.model.bundles.RootConceptTaxonomyAxiom
		import gov.nasa.jpl.imce.oml.model.bundles.SpecificDisjointConceptAxiom
		import gov.nasa.jpl.imce.oml.model.common.AnnotationProperty
		import gov.nasa.jpl.imce.oml.model.common.AnnotationPropertyValue
		import gov.nasa.jpl.imce.oml.model.common.Extent
		import gov.nasa.jpl.imce.oml.model.common.LogicalElement
		import gov.nasa.jpl.imce.oml.model.common.Module
		import gov.nasa.jpl.imce.oml.model.descriptions.ConceptInstance
		import gov.nasa.jpl.imce.oml.model.descriptions.ConceptualEntitySingletonInstance
		import gov.nasa.jpl.imce.oml.model.descriptions.DescriptionBox
		import gov.nasa.jpl.imce.oml.model.descriptions.DescriptionBoxExtendsClosedWorldDefinitions
		import gov.nasa.jpl.imce.oml.model.descriptions.DescriptionBoxRefinement
		import gov.nasa.jpl.imce.oml.model.descriptions.ReifiedRelationshipInstance
		import gov.nasa.jpl.imce.oml.model.descriptions.ReifiedRelationshipInstanceDomain
		import gov.nasa.jpl.imce.oml.model.descriptions.ReifiedRelationshipInstanceRange
		import gov.nasa.jpl.imce.oml.model.descriptions.ScalarDataPropertyValue
		import gov.nasa.jpl.imce.oml.model.descriptions.SingletonInstanceScalarDataPropertyValue
		import gov.nasa.jpl.imce.oml.model.descriptions.SingletonInstanceStructuredDataPropertyContext
		import gov.nasa.jpl.imce.oml.model.descriptions.SingletonInstanceStructuredDataPropertyValue
		import gov.nasa.jpl.imce.oml.model.descriptions.StructuredDataPropertyTuple
		import gov.nasa.jpl.imce.oml.model.descriptions.UnreifiedRelationshipInstanceTuple
		import gov.nasa.jpl.imce.oml.model.graphs.ConceptDesignationTerminologyAxiom
		import gov.nasa.jpl.imce.oml.model.graphs.TerminologyGraph
		import gov.nasa.jpl.imce.oml.model.graphs.TerminologyNestingAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.Aspect
		import gov.nasa.jpl.imce.oml.model.terminologies.AspectPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.AspectSpecializationAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.BinaryScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.ChainRule
		import gov.nasa.jpl.imce.oml.model.terminologies.Concept
		import gov.nasa.jpl.imce.oml.model.terminologies.ConceptPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ConceptSpecializationAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.DataRange
		import gov.nasa.jpl.imce.oml.model.terminologies.DataRelationshipToScalar
		import gov.nasa.jpl.imce.oml.model.terminologies.DataRelationshipToStructure
		import gov.nasa.jpl.imce.oml.model.terminologies.Entity
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityExistentialRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyExistentialRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyParticularRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyUniversalRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityStructuredDataProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityStructuredDataPropertyParticularRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityUniversalRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.IRIScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.NumericScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.PlainLiteralScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipInversePropertyPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipPropertyPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipSourceInversePropertyPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipSourcePropertyPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipSpecializationAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipTargetInversePropertyPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipTargetPropertyPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionScalarDataPropertyValue
		import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionStructuredDataPropertyContext
		import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionStructuredDataPropertyTuple
		import gov.nasa.jpl.imce.oml.model.terminologies.RuleBodySegment
		import gov.nasa.jpl.imce.oml.model.terminologies.Scalar
		import gov.nasa.jpl.imce.oml.model.terminologies.ScalarDataProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.ScalarOneOfLiteralAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.ScalarOneOfRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.StringScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.SubDataPropertyOfAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.SubObjectPropertyOfAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.Structure
		import gov.nasa.jpl.imce.oml.model.terminologies.StructuredDataProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.SynonymScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.TerminologyBox
		import gov.nasa.jpl.imce.oml.model.terminologies.TerminologyExtensionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.TimeScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.UnreifiedRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.UnreifiedRelationshipInversePropertyPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.UnreifiedRelationshipPropertyPredicate
		
		import gov.nasa.jpl.imce.oml.model.common.CommonFactory
		import gov.nasa.jpl.imce.oml.model.terminologies.TerminologiesFactory
		import gov.nasa.jpl.imce.oml.model.graphs.GraphsFactory
		import gov.nasa.jpl.imce.oml.model.bundles.BundlesFactory
		import gov.nasa.jpl.imce.oml.model.descriptions.DescriptionsFactory
		
		/**
		 * @generated
		 */
		class «tableName» {
		 
		  «FOR eClass : eClasses»
		  protected val Map<String, Pair<«eClass.name», Map<String,String>>> «eClass.tableVariableName»
		  «ENDFOR»
		
		  protected val Map<String, Pair<Module, Map<String,String>>> modules
		  protected val Map<String, Pair<LogicalElement, Map<String,String>>> logicalElements
		  protected val Map<String, Pair<Entity, Map<String,String>>> entities
		  protected val Map<String, Pair<EntityRelationship, Map<String,String>>> entityRelationships
		  protected val Map<String, Pair<DataRange, Map<String,String>>> dataRanges 
		  protected val Map<String, Pair<DataRelationshipToScalar, Map<String,String>>> dataRelationshipToScalars
		  protected val Map<String, Pair<DataRelationshipToStructure, Map<String,String>>> dataRelationshipToStructures 
		  protected val Map<String, Pair<RestrictionStructuredDataPropertyContext, Map<String,String>>> restrictionStructuredDataPropertyContexts 
		  protected val Map<String, Pair<TerminologyBox, Map<String,String>>> terminologyBoxes
		  protected val Map<String, Pair<ConceptTreeDisjunction, Map<String,String>>> conceptTreeDisjunctions
		  protected val Map<String, Pair<ConceptualEntitySingletonInstance, Map<String,String>>> conceptualEntitySingletonInstances
		  protected val Map<String, Pair<SingletonInstanceStructuredDataPropertyContext, Map<String,String>>> singletonInstanceStructuredDataPropertyContexts
		
		  extension CommonFactory omlCommonFactory
		  extension TerminologiesFactory omlTerminologiesFactory
		  extension GraphsFactory omlGraphsFactory
		  extension BundlesFactory omlBundlesFactory
		  extension DescriptionsFactory omlDescriptionsFactory
		  
		  new() {
		  	omlCommonFactory = CommonFactory.eINSTANCE
		  	omlTerminologiesFactory = TerminologiesFactory.eINSTANCE
		  	omlGraphsFactory = GraphsFactory.eINSTANCE
		  	omlBundlesFactory = BundlesFactory.eINSTANCE
		  	omlDescriptionsFactory = DescriptionsFactory.eINSTANCE
		  	
		  	«FOR eClass : eClasses SEPARATOR "\n"»«eClass.tableVariableName» = new HashMap<String, Pair<«eClass.name», Map<String,String>>>()«ENDFOR»
		  
		    modules = new HashMap<String, Pair<Module, Map<String,String>>>()
		    	logicalElements = new HashMap<String, Pair<LogicalElement, Map<String,String>>>()
		    entities = new HashMap<String, Pair<Entity, Map<String,String>>>()
		    entityRelationships = new HashMap<String, Pair<EntityRelationship, Map<String,String>>>()
		    dataRanges = new HashMap<String, Pair<DataRange, Map<String,String>>>()
		    dataRelationshipToScalars = new HashMap<String, Pair<DataRelationshipToScalar, Map<String,String>>>()
		    dataRelationshipToStructures = new HashMap<String, Pair<DataRelationshipToStructure, Map<String,String>>>()
		    restrictionStructuredDataPropertyContexts = new HashMap<String, Pair<RestrictionStructuredDataPropertyContext, Map<String,String>>>()
		    terminologyBoxes = new HashMap<String, Pair<TerminologyBox, Map<String,String>>>()
		    conceptTreeDisjunctions = new HashMap<String, Pair<ConceptTreeDisjunction, Map<String,String>>>()
		    conceptualEntitySingletonInstances = new HashMap<String, Pair<ConceptualEntitySingletonInstance, Map<String,String>>>()
		    singletonInstanceStructuredDataPropertyContexts = new HashMap<String, Pair<SingletonInstanceStructuredDataPropertyContext, Map<String,String>>>()
		  }
		  
		  static def void save(Extent e, ZipArchiveOutputStream zos) {
		    var ZipArchiveEntry entry = null
		    «FOR eClass : eClasses»
		    // «eClass.name»
		    entry = new ZipArchiveEntry("«pluralize(eClass.name)».json")
		    zos.putArchiveEntry(entry)
		    try {
		      zos.write(«eClass.tableVariableName»ByteArray(e))
		    } finally {
		      zos.closeArchiveEntry()
		    }
		    «ENDFOR»
		  }
		  
		  «FOR eClass : eClasses»
		  static def byte[] «eClass.tableVariableName»ByteArray(Extent e) {
		  	val ByteArrayOutputStream bos = new ByteArrayOutputStream()
		  	val PrintWriter pw = new PrintWriter(bos)
		  	OMLTables.«eClass.tableVariableName»(e).forEach[it |
		  	  pw.print("{")
		      «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR "\npw.print(\",\")"»
		      pw.print("\"«attr.columnName»\":")
		      «IF attr.isIRIReference»
		      pw.print("\"")
		      pw.print(it.«attr.featureQuery».iri())
		      pw.print("\"")
		      «ELSEIF attr.isLiteralFeature»
		      pw.print(OMLTables.toString(it.«attr.featureQuery»))
		      «ELSEIF attr.isClassFeature && attr.lowerBound == 0»
		      if (null !== «attr.featureQuery») {
		        pw.print("\"")
		        pw.print(it.«attr.featureQuery»?.uuid())
		        pw.print("\"")
		      } else
		        pw.print("null")
		      «ELSEIF attr.isClassFeature && attr.lowerBound > 0»
		      pw.print("\"")
		      pw.print(it.«attr.featureQuery».uuid())
		      pw.print("\"")
		      «ELSEIF attr.isUUID»
		      pw.print("\"")
		      pw.print(it.«attr.featureQuery»)
		      pw.print("\"")
		      «ELSE»
		      pw.print(OMLTables.toString(it.«attr.featureQuery»))
		      «ENDIF»
		      «ENDFOR»
		      pw.println("}")
		    ]
		    pw.close()
		    return bos.toByteArray()
		  }
		  
		  «ENDFOR»
		  		    	    
		  static def void load(OMLZipResourceSet rs, OMLZipResource r, File omlZipFile) {
		
		    val tables = new «tableName»()
		    
		    val zip = new ZipFile(omlZipFile)
		  	Collections.list(zip.entries).forEach[ze | 
		      val is = zip.getInputStream(ze)
		      val buffer = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
		      val lines = new ArrayList<String>()
		      lines.addAll(buffer.lines().iterator.toIterable)
		      switch ze.name {
		  	    «FOR eClass : eClasses»
		  	    case "«pluralize(eClass.name)».json":
		  	      tables.read«eClass.tableVariableName.upperCaseInitialOrWord»(lines)
    		        «ENDFOR»
		        default:
		          throw new IllegalArgumentException("«tableName».load(): unrecognized table name: "+ze.name)
		      }
		    ]
		    tables.resolve(rs, r)
		  }

		  «FOR eClass : eClasses»
		  protected def void read«eClass.tableVariableName.upperCaseInitialOrWord»(ArrayList<String> lines) {
		  	val kvs = OMLZipResource.lines2tuples(lines)
		  	kvs.forEach[kv|
		  	  val oml = create«eClass.name»()
		  	  val uuid = kv.remove("uuid")
		  	  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes»«IF attr.isLiteralFeature»
		  	  oml.«attr.name» = OMLTables.to«attr.EType.name»(kv.remove("«attr.columnName»"))«ELSEIF !attr.isClassFeature && attr.name != "uuid"»
		  	  oml.«attr.name» = OMLTables.to«attr.EType.name»(kv.remove("«attr.columnName»"))«ENDIF»
		  	  «ENDFOR»
		  	  val pair = new Pair<«eClass.name», Map<String,String>>(oml, kv)
		  	  «eClass.tableVariableName».put(uuid, pair)
		  	]
		  }
		  
		  «ENDFOR»
		
		  protected def <U,V extends U> void includeMap(Map<String, Pair<U, Map<String, String>>> uMap, Map<String, Pair<V, Map<String, String>>> vMap) {
		    vMap.forEach[uuid,kv|uMap.put(uuid, new Pair<U, Map<String, String>>(kv.key, Collections.emptyMap))]
		  }
		  
		  protected def void resolve(OMLZipResourceSet rs, OMLZipResource r) {
			// Lookup table for LogicalElement cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "LogicalElement"]] SEPARATOR "\n"»includeMap(logicalElements, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for Entity cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "Entity"]] SEPARATOR "\n"»includeMap(entities, «eClass.tableVariableName»)«ENDFOR»
		    
			// Lookup table for EntityRelationship cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "EntityRelationship"]] SEPARATOR "\n"»includeMap(entityRelationships, «eClass.tableVariableName»)«ENDFOR»
		    
			// Lookup table for DataRange cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRange"]] SEPARATOR "\n"»includeMap(dataRanges, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for DataRelationshipToScalar cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRelationshipToScalar"]] SEPARATOR "\n"»includeMap(dataRelationshipToScalars, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for DataRelationshipToStructure cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRelationshipToStructure"]] SEPARATOR "\n"»includeMap(dataRelationshipToStructures, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for RestrictionStructuredDataPropertyContext cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "RestrictionStructuredDataPropertyContext"]] SEPARATOR "\n"»includeMap(restrictionStructuredDataPropertyContexts, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for TerminologyBox cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "TerminologyBox"]] SEPARATOR "\n"»includeMap(terminologyBoxes, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for ConceptTreeDisjunction cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "ConceptTreeDisjunction"]] SEPARATOR "\n"»includeMap(conceptTreeDisjunctions, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for ConceptualEntitySingletonInstance cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "ConceptualEntitySingletonInstance"]] SEPARATOR "\n"»includeMap(conceptualEntitySingletonInstances, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for SingletonInstanceStructuredDataPropertyContext cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "SingletonInstanceStructuredDataPropertyContext"]] SEPARATOR "\n"»includeMap(singletonInstanceStructuredDataPropertyContexts, «eClass.tableVariableName»)«ENDFOR»
		  	
		    «FOR eClass : eClasses.filter[schemaAPIOrOrderingKeyReferences.size > 0]»
		    resolve«eClass.tableVariableName.upperCaseInitialOrWord»(rs)
		    «ENDFOR»
		    
		    	val ext = createExtent()
		    	ext.getModules.addAll(terminologyGraphs.values.map[key])
		    	ext.getModules.addAll(bundles.values.map[key])
		    	ext.getModules.addAll(descriptionBoxes.values.map[key])
		    	r.contents.add(ext)
		  }

		  «FOR eClass : eClasses.filter[schemaAPIOrOrderingKeyReferences.size > 0]»
		  protected def void resolve«eClass.tableVariableName.upperCaseInitialOrWord»(OMLZipResourceSet rs) {
		  	«eClass.tableVariableName».forEach[uuid, oml_kv |
		  	  val «eClass.name» oml = oml_kv.key
		  	  val Map<String, String> kv = oml_kv.value
		  	  if (!kv.empty) {
		  	    «FOR attr : eClass.schemaAPIOrOrderingKeyReferences»
		  	    «IF (attr.isIRIReference)»
		  	    val String «attr.name»IRI = kv.remove("«attr.columnName»")
		  	    loadOMLZipResource(rs, URI.createURI(«attr.name»IRI))
		  	    val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»IRI)
		  	    if (null === «attr.name»Pair)
		  	      throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»")
		  	    oml.«attr.name» = «attr.name»Pair.key		  	  
		  	    «ELSEIF (attr.lowerBound == 0)»
		  	    val String «attr.name»XRef = kv.remove("«attr.columnName»")
		  	    if ("null" != «attr.name»XRef) {
		  	      val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	      if (null === «attr.name»Pair)
		  	        throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»")
		  	      oml.«attr.name» = «attr.name»Pair.key
		  	    }
		  	    «ELSE»
		  	    val String «attr.name»XRef = kv.remove("«attr.columnName»")
		  	    val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	    if (null === «attr.name»Pair)
		  	      throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»")
		  	    oml.«attr.name» = «attr.name»Pair.key
		  	    «ENDIF»
		  	    «ENDFOR»
		  	  }
		  	]
		  }
		  
		  «ENDFOR»		  

		  protected def OMLZipResource loadOMLZipResource(OMLZipResourceSet rs, URI uri) {
		  	val r = rs.getResource(uri, true)
		  	switch r {
		  		OMLZipResource: {
		  		  r.contents.get(0).eAllContents.forEach[e|
		  		    switch e {
		  	          «FOR eClass : eClasses»
		  	          «eClass.name»: {
		  	          	val pair = new Pair<«eClass.name», Map<String,String>>(e, Collections.emptyMap)
		  	            «eClass.tableVariableName».put(e.uuid(), pair)
		  	            «IF (eClass.EAllSuperTypes.exists[name == "LogicalElement"])»
		  	            logicalElements.put(e.uuid(), new Pair<LogicalElement, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "Entity"])»
		  	            entities.put(e.uuid(), new Pair<Entity, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "EntityRelationship"])»
		  	            entityRelationships.put(e.uuid(), new Pair<EntityRelationship, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "DataRange"])»
		  	            dataRanges.put(e.uuid(), new Pair<DataRange, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "DataRelationshipToScalar"])»
		  	            dataRelationshipToScalars.put(e.uuid(), new Pair<DataRelationshipToScalar, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "DataRelationshipToStructure"])»
		  	            dataRelationshipToStructures.put(e.uuid(), new PairDataRelationshipToStructure, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "RestrictionStructuredDataPropertyContext"])»
		  	            restrictionStructuredDataPropertyContexts.put(e.uuid(), new Pair<RestrictionStructuredDataPropertyContext, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "TerminologyBox"])»
		  	            terminologyBoxes.put(e.uuid(), new Pair<TerminologyBox, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "ConceptTreeDisjunction"])»
		  	            conceptTreeDisjunctions.put(e.uuid(), new Pair<ConceptTreeDisjunction, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "ConceptualEntitySingletonInstance"])»
		  	            conceptualEntitySingletonInstances.put(e.uuid(), new Pair<ConceptualEntitySingletonInstance, Map<String,String>>(e, Collections.emptyMap))
		  	            «ELSEIF (eClass.EAllSuperTypes.exists[name == "SingletonInstanceStructuredDataPropertyContext"])»
		  	            singletonInstanceStructuredDataPropertyContexts.put(e.uuid(), new Pair<SingletonInstanceStructuredDataPropertyContext, Map<String,String>>(e, Collections.emptyMap))
		  	            «ENDIF»
		  	            «IF (eClass.EAllSuperTypes.exists[name == "Module"])»
		  	            «eClass.tableVariableName».put(e.iri(), pair)
		  	            «ENDIF»
		  	            «IF (eClass.EAllSuperTypes.exists[name == "TerminologyBox"])»
		  	            terminologyBoxes.put(e.uuid(), new Pair<TerminologyBox, Map<String,String>>(e, Collections.emptyMap))
		  	            terminologyBoxes.put(e.iri(), new Pair<TerminologyBox, Map<String,String>>(e, Collections.emptyMap))
		  	            «ENDIF»
		  	          }
		  		    	  «ENDFOR»
		  		    	}
		  		  ]
		  		  return r
		  		}
		  		default:
		  		  throw new IllegalArgumentException("OMLTables.loadOMLZipResource("+uri+") should have produce an OMLZipResource!")
		    }
		  }
		  
		}
	'''
	}
	
	static def String tableReaderName(EClass eClass)
	  '''read«pluralize(eClass.name)»'''
	
	static def String tableVariable(EClass eClass)
	'''«eClass.tableVariableName» : Seq[«eClass.name»] = Seq.empty'''
	

}