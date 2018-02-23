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
		val eClassesExceptModules = eClasses.filter[!EAllSuperTypes.exists[name == "Module"]]
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
		import java.util.HashSet
		import java.util.LinkedList
		import java.util.Map
		import java.util.Queue
		import java.util.Set
		import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
		import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
		import org.apache.commons.compress.archivers.zip.ZipFile
		import org.eclipse.emf.common.util.URI
		import org.eclipse.emf.ecore.resource.Resource
		import org.eclipse.emf.ecore.resource.ResourceSet
		import org.eclipse.xtext.resource.XtextResource
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
		import gov.nasa.jpl.imce.oml.model.extensions.OMLExtensions
		import gov.nasa.jpl.imce.oml.model.graphs.ConceptDesignationTerminologyAxiom
		import gov.nasa.jpl.imce.oml.model.graphs.TerminologyGraph
		import gov.nasa.jpl.imce.oml.model.graphs.TerminologyNestingAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.Aspect
		import gov.nasa.jpl.imce.oml.model.terminologies.AspectSpecializationAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.BinaryScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.ChainRule
		import gov.nasa.jpl.imce.oml.model.terminologies.Concept
		import gov.nasa.jpl.imce.oml.model.terminologies.ConceptSpecializationAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.ConceptualRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.DataRange
		import gov.nasa.jpl.imce.oml.model.terminologies.DataRelationshipToScalar
		import gov.nasa.jpl.imce.oml.model.terminologies.DataRelationshipToStructure
		import gov.nasa.jpl.imce.oml.model.terminologies.Entity
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityExistentialRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityUniversalRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyExistentialRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyParticularRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyUniversalRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityStructuredDataProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.EntityStructuredDataPropertyParticularRestrictionAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.ForwardProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.InverseProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.IRIScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.NumericScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.PlainLiteralScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.Predicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.RestrictableRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionScalarDataPropertyValue
		import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionStructuredDataPropertyContext
		import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionStructuredDataPropertyTuple
		import gov.nasa.jpl.imce.oml.model.terminologies.RuleBodySegment
		import gov.nasa.jpl.imce.oml.model.terminologies.Scalar
		import gov.nasa.jpl.imce.oml.model.terminologies.ScalarDataProperty
		import gov.nasa.jpl.imce.oml.model.terminologies.ScalarOneOfLiteralAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.ScalarOneOfRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.SegmentPredicate
		import gov.nasa.jpl.imce.oml.model.terminologies.SpecializedReifiedRelationship
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
		  protected val Map<String, Pair<ConceptualRelationship, Map<String,String>>> conceptualRelationships
		  protected val Map<String, Pair<DataRange, Map<String,String>>> dataRanges 
		  protected val Map<String, Pair<DataRelationshipToScalar, Map<String,String>>> dataRelationshipToScalars
		  protected val Map<String, Pair<DataRelationshipToStructure, Map<String,String>>> dataRelationshipToStructures
		  protected val Map<String, Pair<Predicate, Map<String,String>>> predicates
		  protected val Map<String, Pair<RestrictableRelationship, Map<String,String>>> restrictableRelationships
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
		  
		  protected val Queue<String> iriLoadQueue
		  protected val Set<String> visitedIRIs
		  protected val Queue<Module> moduleQueue
		  protected val Set<Module> visitedModules
		  
		  new() {
			iriLoadQueue = new LinkedList<String>()
			visitedIRIs = new HashSet<String>()
			moduleQueue = new LinkedList<Module>()
			visitedModules = new HashSet<Module>()

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
		    conceptualRelationships = new HashMap<String, Pair<ConceptualRelationship, Map<String,String>>>()
		    dataRanges = new HashMap<String, Pair<DataRange, Map<String,String>>>()
		    dataRelationshipToScalars = new HashMap<String, Pair<DataRelationshipToScalar, Map<String,String>>>()
		    dataRelationshipToStructures = new HashMap<String, Pair<DataRelationshipToStructure, Map<String,String>>>()
		    predicates = new HashMap<String, Pair<Predicate, Map<String,String>>>()
		    restrictableRelationships = new HashMap<String, Pair<RestrictableRelationship, Map<String,String>>>()
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
		  protected static def byte[] «eClass.tableVariableName»ByteArray(Extent e) {
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
		      «ELSEIF attr.isBoolean»
		      pw.print("\"")
		      pw.print(it.«attr.featureQuery»)
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
		  		    	    
		  /**
		   * Uses an OMLSpecificationTables for resolving cross-references in the *.oml and *.omlzip representations.
		   * When there are no more OML resources to load, it is necessary to call explicitly: 
		   * 
		   *     OMLZipResource.clearOMLSpecificationTables(rs)
		   */
		  static def void load(ResourceSet rs, OMLZipResource r, File omlZipFile) {
		
		    val tables = OMLZipResource.getOrInitializeOMLSpecificationTables(rs)
		    val ext = tables.omlCommonFactory.createExtent()
		    r.contents.add(ext)
		    val zip = new ZipFile(omlZipFile)
		  	Collections.list(zip.entries).forEach[ze | 
		      val is = zip.getInputStream(ze)
		      val buffer = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
		      val lines = new ArrayList<String>()
		      lines.addAll(buffer.lines().iterator.toIterable)
		      buffer.close()
		      switch ze.name {
		  	    «FOR eClass : eClasses»
		  	    case "«pluralize(eClass.name)».json":
		  	      tables.read«eClass.tableVariableName.upperCaseInitialOrWord»(ext, lines)
    		        «ENDFOR»
		        default:
		          throw new IllegalArgumentException("«tableName».load(): unrecognized table name: "+ze.name)
		      }
		    ]
		    zip.close()   
		    
		    var Boolean more = false
		    do {
		        more = false
		        	if (!tables.iriLoadQueue.empty) {
		        		val iri = tables.iriLoadQueue.remove
		        		if (tables.visitedIRIs.add(iri)) {
		        			more = true
		     	 	    	tables.loadOMLZipResource(rs, URI.createURI(iri))	
		     	 	}
		        }
		        	
		        	if (!tables.moduleQueue.empty) {
		        		val m = tables.moduleQueue.remove
		        		if (tables.visitedModules.add(m)) {
		        			more = true
		        			tables.includeModule(m)
		        		}
		        	}
		    } while (more)

		    tables.resolve(rs, r)
		  }

		  «FOR eClass : eClasses»
		  protected def void read«eClass.tableVariableName.upperCaseInitialOrWord»(Extent ext, ArrayList<String> lines) {
		  	val kvs = OMLZipResource.lines2tuples(lines)
		  	while (!kvs.empty) {
		  	  val kv = kvs.remove(kvs.size - 1)
		  	  val oml = create«eClass.name»()
		  	  «IF (eClass.EAllSuperTypes.exists[name == "Module"])»
		  	  	ext.getModules.add(oml)
		  	  «ENDIF»
		  	  val uuid = kv.remove("uuid")
		  	  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes»
		  	  «IF attr.isLiteralFeature»«IF attr.required»
		  	  oml.«attr.name» = OMLTables.to«attr.EType.name»(kv.remove("«attr.columnName»"))
		  	  «ELSE»
		  	  val «attr.name»_value = kv.remove("«attr.columnName»")
		  	  if (null !== «attr.name»_value && «attr.name»_value.length > 0)
		  	  	oml.«attr.name» = OMLTables.to«attr.EType.name»(«attr.name»_value)
		  	  «ENDIF»
		  	  «ELSEIF !attr.isClassFeature && attr.name != "uuid"»«IF attr.required»
		  	  oml.«attr.name» = OMLTables.to«attr.EType.name»(kv.remove("«attr.columnName»"))
		  	  «ELSE»
		  	  val «attr.name»_value = kv.remove("«attr.columnName»")
		  	  if (null !== «attr.name»_value && «attr.name»_value.length > 0)
		  	  	oml.«attr.name» = OMLTables.to«attr.EType.name»(«attr.name»_value)
		  	  «ENDIF»
		  	  «ELSEIF (attr.isIRIReference)»
		  	  val String «attr.name»IRI = kv.get("«attr.columnName»")
		  	  if (null === «attr.name»IRI)
		  	  	throw new IllegalArgumentException("read«eClass.tableVariableName.upperCaseInitialOrWord»: missing '«attr.columnName»' in: "+kv.toString)
		  	  iriLoadQueue.add(«attr.name»IRI)
			  «ENDIF»
		  	  «ENDFOR»
		  	  val pair = new Pair<«eClass.name», Map<String,String>>(oml, kv)
		  	  «eClass.tableVariableName».put(uuid, pair)
		  	  include«eClass.tableVariableName.upperCaseInitialOrWord»(uuid, oml)
		  	}
		  }
		  
		  «ENDFOR»
		  protected def <U,V extends U> void includeMap(Map<String, Pair<U, Map<String, String>>> uMap, Map<String, Pair<V, Map<String, String>>> vMap) {
		    vMap.forEach[uuid,kv|uMap.put(uuid, new Pair<U, Map<String, String>>(kv.key, Collections.emptyMap))]
		  }

		  «FOR eClass : eClasses»
		  protected def void include«eClass.tableVariableName.upperCaseInitialOrWord»(String uuid, «eClass.name» oml) {
		  	«IF eClass.EAllSuperTypes.exists[name == "Module"]»
		  		modules.put(uuid, new Pair<Module, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "LogicalElement"]»
		  		logicalElements.put(uuid, new Pair<LogicalElement, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "Entity"]»
		  		entities.put(uuid, new Pair<Entity, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "EntityRelationship"]»
		  		entityRelationships.put(uuid, new Pair<EntityRelationship, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "ConceptualRelationship"]»
		  		conceptualRelationships.put(uuid, new Pair<ConceptualRelationship, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "DataRange"]»
		  		dataRanges.put(uuid, new Pair<DataRange, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "DataRelationshipToScalar"]»
		  		dataRelationshipToScalars.put(uuid, new Pair<DataRelationshipToScalar, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "DataRelationshipToStructure"]»
		  		dataRelationshipToStructures.put(uuid, new Pair<DataRelationshipToStructure, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "Predicate"]»
		  		predicates.put(uuid, new Pair<Predicate, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "RestrictableRelationship"]»
		  		restrictableRelationships.put(uuid, new Pair<RestrictableRelationship, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "RestrictionStructuredDataPropertyContext"]»
		  		restrictionStructuredDataPropertyContexts.put(uuid, new Pair<RestrictionStructuredDataPropertyContext, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "TerminologyBox"]»
		  		terminologyBoxes.put(uuid, new Pair<TerminologyBox, Map<String, String>>(oml, Collections.emptyMap))
		  		terminologyBoxes.put(oml.iri(), new Pair<TerminologyBox, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "ConceptTreeDisjunction"]»
		  		conceptTreeDisjunctions.put(uuid, new Pair<ConceptTreeDisjunction, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "ConceptualEntitySingletonInstance"]»
		  		conceptualEntitySingletonInstances.put(uuid, new Pair<ConceptualEntitySingletonInstance, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "SingletonInstanceStructuredDataPropertyContext"]»
		  		singletonInstanceStructuredDataPropertyContexts.put(uuid, new Pair<SingletonInstanceStructuredDataPropertyContext, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.name == "DescriptionBox"»
		  		descriptionBoxes.put(oml.iri(), new Pair<DescriptionBox, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	
		  }
		  «ENDFOR»
		  
		  protected def void resolve(ResourceSet rs, OMLZipResource r) {
			// Lookup table for LogicalElement cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "LogicalElement"]] SEPARATOR "\n"»includeMap(logicalElements, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for Entity cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "Entity"]] SEPARATOR "\n"»includeMap(entities, «eClass.tableVariableName»)«ENDFOR»
		    
			// Lookup table for EntityRelationship cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "EntityRelationship"]] SEPARATOR "\n"»includeMap(entityRelationships, «eClass.tableVariableName»)«ENDFOR»
		    
			// Lookup table for ConceptualRelationship cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "ConceptualRelationship"]] SEPARATOR "\n"»includeMap(conceptualRelationships, «eClass.tableVariableName»)«ENDFOR»
		    
			// Lookup table for DataRange cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRange"]] SEPARATOR "\n"»includeMap(dataRanges, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for DataRelationshipToScalar cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRelationshipToScalar"]] SEPARATOR "\n"»includeMap(dataRelationshipToScalars, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for DataRelationshipToStructure cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRelationshipToStructure"]] SEPARATOR "\n"»includeMap(dataRelationshipToStructures, «eClass.tableVariableName»)«ENDFOR»
		  	
			// Lookup table for Predicate cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "Predicate"]] SEPARATOR "\n"»includeMap(predicates, «eClass.tableVariableName»)«ENDFOR»

			// Lookup table for RestrictableRelationship cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "RestrictableRelationship"]] SEPARATOR "\n"»includeMap(restrictableRelationships, «eClass.tableVariableName»)«ENDFOR»

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
		  }

		  «FOR eClass : eClasses.filter[schemaAPIOrOrderingKeyReferences.size > 0]»
		  protected def void resolve«eClass.tableVariableName.upperCaseInitialOrWord»(ResourceSet rs) {
		  	«IF eClass.EAllSuperTypes.exists[name == "ModuleEdge"]»
		  	var more = false
		  	do {
		  		val queue = new HashMap<String, Pair<«eClass.name», Map<String, String>>>()
		  		«eClass.tableVariableName».filter[uuid, oml_kv|!oml_kv.value.empty].forEach[uuid, oml_kv|queue.put(uuid, oml_kv)]
		  		more = !queue.empty
		  		if (more) {
		  			queue.forEach[uuid, oml_kv |
		  	  			val «eClass.name» oml = oml_kv.key
		  	  			val Map<String, String> kv = oml_kv.value
		  	  			if (!kv.empty) {
		  	    				«FOR attr : eClass.schemaAPIOrOrderingKeyReferences»
		  	    				«IF (attr.isIRIReference)»
		  	    				val String «attr.name»IRI = kv.remove("«attr.columnName»")
		  	    				val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»IRI)
		  	    				if (null === «attr.name»Pair)
		  	    					throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»: "+«attr.name»IRI)
		  	    				oml.«attr.name» = «attr.name»Pair.key		  	  
		  	    				«ELSEIF (attr.lowerBound == 0)»
		  	    				val String «attr.name»XRef = kv.remove("«attr.columnName»")
		  	    				if ("null" != «attr.name»XRef) {
		  	    					val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	    					if (null === «attr.name»Pair)
		  	    						throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»: "+«attr.name»XRef)
		  	    					oml.«attr.name» = «attr.name»Pair.key
		  	    				}
		  	    				«ELSE»
		  	    				val String «attr.name»XRef = kv.remove("«attr.columnName»")
		  	    				val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	    				if (null === «attr.name»Pair)
		  	    					throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»: "+«attr.name»XRef)
		  	    				oml.«attr.name» = «attr.name»Pair.key
		  	    				«ENDIF»
		  	    				«ENDFOR»
		  	  			}
		  			]
		  		}
		  	} while (more)
		  	«ELSE»
		  	
		  	«eClass.tableVariableName».forEach[uuid, oml_kv |
		  	  val «eClass.name» oml = oml_kv.key
		  	  val Map<String, String> kv = oml_kv.value
		  	  if (!kv.empty) {
		  	    «FOR attr : eClass.schemaAPIOrOrderingKeyReferences»
		  	    «IF (attr.isIRIReference)»
		  	    val String «attr.name»IRI = kv.remove("«attr.columnName»")
		  	    val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»IRI)
		  	    if (null === «attr.name»Pair)
		  	      throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»: "+«attr.name»IRI)
		  	    oml.«attr.name» = «attr.name»Pair.key		  	  
		  	    «ELSEIF (attr.lowerBound == 0)»
		  	    val String «attr.name»XRef = kv.remove("«attr.columnName»")
		  	    if ("null" != «attr.name»XRef) {
		  	      val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	      if (null === «attr.name»Pair)
		  	        throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»: "+«attr.name»XRef)
		  	      oml.«attr.name» = «attr.name»Pair.key
		  	    }
		  	    «ELSE»
		  	    val String «attr.name»XRef = kv.remove("«attr.columnName»")
		  	    val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	    if (null === «attr.name»Pair)
		  	      throw new IllegalArgumentException("Null cross-reference lookup for «attr.name» in «eClass.tableVariableName»: "+«attr.name»XRef)
		  	    oml.«attr.name» = «attr.name»Pair.key
		  	    «ENDIF»
		  	    «ENDFOR»
		  	  }
		  	]
		  	«ENDIF»
		  }
		  
		  «ENDFOR»
		  protected def Resource loadOMLZipResource(ResourceSet rs, URI uri) {
		  	val omlCatalog = OMLExtensions.getCatalog(rs)
		  	if (null === omlCatalog)
		  		throw new IllegalArgumentException("loadOMLZipResource: ResourceSet must have an OMLCatalog!")
		
			var scan = false
			val uriString = uri.toString
		  	val Resource r = if (uriString.startsWith("file:")) {
		  		scan = true
		  		rs.getResource(uri, true)
		  	} else if (uriString.startsWith("http:")) {
				val r0a = rs.getResource(uri, false)
				val r0b = rs.resources.findFirst[r| r.contents.exists[e|
					switch e {
						Extent:
							e.modules.exists[m|m.iri() == uriString]
						default:
							false
					}
				]]
				val r0 = r0a ?: r0b
				if (null !== r0) {
					switch r0 {
						OMLZipResource: {
						}
						XtextResource: {
							scan = true
						}
						default: {
						}
					}
					r0
				} else {
					val r1 = omlCatalog.resolveURI(uriString + ".oml")
			  		val r2 = omlCatalog.resolveURI(uriString + ".omlzip")
			  		val r3 = omlCatalog.resolveURI(uriString)
			  				  		
			  		val f1 = if (null !== r1 && r1.startsWith("file:")) new File(r1.substring(5)) else null
			  		val f2 = if (null !== r2 && r2.startsWith("file:")) new File(r2.substring(5)) else null
			  		val f3 = if (null !== r3 && r3.startsWith("file:")) new File(r3.substring(5)) else null
			  	
			  		scan = true
			  		
			  		if (null !== f1 && f1.exists && f1.canRead)
			  			rs.getResource(URI.createURI(r1), true)
			  		else if (null !== f2 && f2.exists && f2.canRead)
			  			rs.getResource(URI.createURI(r2), true)
			  		else if (null !== f3 && f3.exists && f3.canRead)
			  			rs.getResource(URI.createURI(r3), true)
			  		else
			  			throw new IllegalArgumentException("loadOMLZipResource: "+uri+" not resolved!")
		  		}
		  	}
		  	
		  	if (scan)
		  		r.contents.forEach[e|
		  			switch e {
		  				Extent: {
		  					e.modules.forEach[includeModule]
		  				}
		  			}
		  		]
		  	
		  	r
		  }

		  def void includeModule(Module m) {
		  	if (null !== m) {
		    	  switch m {
		    	    TerminologyGraph: {
		    	  	  logicalElements.put(m.uuid(), new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyGraphs.put(m.uuid(), new Pair<TerminologyGraph, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyBoxes.put(m.uuid(), new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyBoxes.put(m.iri(), new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))
		    	    }
		    	    Bundle: {
		    	  	  logicalElements.put(m.uuid(), new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))
		    	      bundles.put(m.uuid(), new Pair<Bundle, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyBoxes.put(m.uuid(), new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyBoxes.put(m.iri(), new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))
		    	    }
		    	    DescriptionBox: {
		    	  	  logicalElements.put(m.uuid(), new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))
		    	      descriptionBoxes.put(m.uuid(), new Pair<DescriptionBox, Map<String,String>>(m, Collections.emptyMap))
		    	      descriptionBoxes.put(m.iri(), new Pair<DescriptionBox, Map<String,String>>(m, Collections.emptyMap))
		    	    }
		    	  }
		  	
		  	  modules.put(m.uuid(), new Pair<Module, Map<String,String>>(m, Collections.emptyMap))
		  	  m.eAllContents.forEach[e|
		  	    switch e {
		  	      «FOR eClass : eClassesExceptModules»
		  	      «eClass.name»: {
		  	        val pair = new Pair<«eClass.name», Map<String,String>>(e, Collections.emptyMap)
		  	        «eClass.tableVariableName».put(e.uuid(), pair)
		  	        «IF (eClass.EAllSuperTypes.exists[name == "LogicalElement"])»
		  	        logicalElements.put(e.uuid(), new Pair<LogicalElement, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "Entity"])»
		  	        entities.put(e.uuid(), new Pair<Entity, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "EntityRelationship"])»
		  	        entityRelationships.put(e.uuid(), new Pair<EntityRelationship, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "ConceptualRelationship"])»
		  	        conceptualRelationships.put(e.uuid(), new Pair<ConceptualRelationship, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "DataRange"])»
		  	        dataRanges.put(e.uuid(), new Pair<DataRange, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "DataRelationshipToScalar"])»
		  	        dataRelationshipToScalars.put(e.uuid(), new Pair<DataRelationshipToScalar, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "DataRelationshipToStructure"])»
		  	        dataRelationshipToStructures.put(e.uuid(), new PairDataRelationshipToStructure, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "Predicate"])»
		  	        predicates.put(e.uuid(), new Pair<Predicate, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "RestrictableRelationship"])»
		  	        restrictableRelationships.put(e.uuid(), new Pair<RestrictableRelationship, Map<String,String>>(e, Collections.emptyMap))
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
		  	      }
		  		  «ENDFOR»
		  	    }
		  	  ]
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