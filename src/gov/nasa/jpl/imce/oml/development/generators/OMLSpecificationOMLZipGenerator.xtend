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
		import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipEnumerationRestriction
		import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipOneOfRestriction
		import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipValueRestriction
		import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipExistentialRangeRestriction
		import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipUniversalRangeRestriction
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
		import gov.nasa.jpl.imce.oml.model.terminologies.AspectKind
		import gov.nasa.jpl.imce.oml.model.terminologies.AspectSpecializationAxiom
		import gov.nasa.jpl.imce.oml.model.terminologies.BinaryScalarRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.CardinalityRestrictedAspect
		import gov.nasa.jpl.imce.oml.model.terminologies.CardinalityRestrictedConcept
		import gov.nasa.jpl.imce.oml.model.terminologies.CardinalityRestrictedReifiedRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.ChainRule
		import gov.nasa.jpl.imce.oml.model.terminologies.Concept
		import gov.nasa.jpl.imce.oml.model.terminologies.ConceptKind
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
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipRestriction
		import gov.nasa.jpl.imce.oml.model.terminologies.Predicate
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationship
		import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipSpecializationAxiom
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
		  protected val Map<String, Pair<AspectKind, Map<String,String>>> aspectKinds
		  protected val Map<String, Pair<ConceptKind, Map<String,String>>> conceptKinds
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
		  protected val Map<String, Module> iri2module
		  
		  new() {
			iriLoadQueue = new LinkedList<String>()
			visitedIRIs = new HashSet<String>()
			moduleQueue = new LinkedList<Module>()
			visitedModules = new HashSet<Module>()
			iri2module = new HashMap<String, Module>()

		  	omlCommonFactory = CommonFactory.eINSTANCE
		  	omlTerminologiesFactory = TerminologiesFactory.eINSTANCE
		  	omlGraphsFactory = GraphsFactory.eINSTANCE
		  	omlBundlesFactory = BundlesFactory.eINSTANCE
		  	omlDescriptionsFactory = DescriptionsFactory.eINSTANCE
		  	
		  	«FOR eClass : eClasses SEPARATOR "\n"»«eClass.tableVariableName» = new HashMap<String, Pair<«eClass.name», Map<String,String>>>()«ENDFOR»
		  
		    modules = new HashMap<String, Pair<Module, Map<String,String>>>()
		    logicalElements = new HashMap<String, Pair<LogicalElement, Map<String,String>>>()
		    entities = new HashMap<String, Pair<Entity, Map<String,String>>>()
		    aspectKinds = new HashMap<String, Pair<AspectKind, Map<String,String>>>()
		    conceptKinds = new HashMap<String, Pair<ConceptKind, Map<String,String>>>()
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
		    entry.time = 0L
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
		
			val fileURI = URI.createFileURI(omlZipFile.absolutePath)
			val c = OMLExtensions.findCatalogIfExists(rs, fileURI)
			if (null === c)
				throw new IllegalArgumentException("«tableName».load(): failed to find an OML catalog from: "+fileURI)
			if (c.parsedCatalogs.empty)
				throw new IllegalArgumentException("«tableName».load(): No OML catalog found from: "+fileURI)
			if (c.entries.empty)
				throw new IllegalArgumentException("«tableName».load(): Empty OML catalog from: "+c.parsedCatalogs.join("\n"))
								      
		    val tables = OMLZipResource.getOrInitializeOMLSpecificationTables(rs)
		    val ext = tables.omlCommonFactory.createExtent()
		    r.contents.add(ext)
		    val zip = new ZipFile(omlZipFile)
		    try {
		  		Collections.list(zip.entries).forEach[ze | 
		      		val is = zip.getInputStream(ze)
		      		val buffer = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
		      		val lines = new ArrayList<String>()
		      		lines.addAll(buffer.lines().iterator.toIterable)
		      		is.close()
		      		switch ze.name {
		  	    			«FOR eClass : eClasses»
		  	    			case "«pluralize(eClass.name)».json":
		  	    				tables.read«eClass.tableVariableName.upperCaseInitialOrWord»(ext, lines)
    		        			«ENDFOR»
		        			default:
		          			throw new IllegalArgumentException("«tableName».load(): unrecognized table name: "+ze.name)
		      		}
		    		]
		    } finally {
			    zip.close()
			}
		
		    tables.processQueue(rs)
		    
		    tables.resolve(rs, r)
		  }
		  
		  def void queueModule(Module m) {
		  	moduleQueue.add(m)
		  }
		  
		  def void processQueue(ResourceSet rs) {
		    var Boolean more = false
		    do {
		        more = false
		        if (!iriLoadQueue.empty) {
		        	more = true
		        	val iri = iriLoadQueue.remove
		        	if (visitedIRIs.add(iri)) {
					  loadOMLZipResource(rs, URI.createURI(iri))	
		     	 	}
		        }
		        
		        if (!moduleQueue.empty) {
		        	more = true
		        	val m = moduleQueue.remove
		        	if (visitedModules.add(m)) {
					  includeModule(m)
		        	}
		        }
		    } while (more)
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
		  	  if (null !== «attr.name»_value && "null" != «attr.name»_value && «attr.name»_value.length > 0)
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
		  protected def <U,V extends U> Boolean includeMap(Map<String, Pair<U, Map<String, String>>> uMap, Map<String, Pair<V, Map<String, String>>> vMap) {
		    val Boolean[] updated = #{ false }
		    vMap.forEach[uuid,kv|
		    		val prev = uMap.put(uuid, new Pair<U, Map<String, String>>(kv.key, Collections.emptyMap))
		        	if (null === prev) {
		        		updated.set(0, true)
		        	}
		    ]
		    updated.get(0)
		  }

		  «FOR eClass : eClasses»
		  protected def void include«eClass.tableVariableName.upperCaseInitialOrWord»(String uuid, «eClass.name» oml) {
		  	«IF eClass.EAllSuperTypes.exists[name == "Module"]»
		  		queueModule(oml)
		  		modules.put(uuid, new Pair<Module, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "LogicalElement"]»
		  		logicalElements.put(uuid, new Pair<LogicalElement, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "Entity"]»
		  		entities.put(uuid, new Pair<Entity, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "AspectKind"]»
		  		aspectKinds.put(uuid, new Pair<AspectKind, Map<String, String>>(oml, Collections.emptyMap))
		  	«ENDIF»
		  	«IF eClass.EAllSuperTypes.exists[name == "ConceptKind"]»
		  		conceptKinds.put(uuid, new Pair<ConceptKind, Map<String, String>>(oml, Collections.emptyMap))
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
		  	
		  }
		  «ENDFOR»
		
		  protected def void resolve(ResourceSet rs, OMLZipResource r) {
		
		  	System.out.println("Resolve: "+r.URI)
		  	val t0 = System.currentTimeMillis
		  	// Lookup table for LogicalElement cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "LogicalElement"]]»
		    if (includeMap(logicalElements, «eClass.tableVariableName»)) {
		    	System.out.println("+ logicalElements, «eClass.tableVariableName»")
		    }
		    «ENDFOR»
		
			// Lookup table for Entity cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "Entity"]]»
		  	if (includeMap(entities, «eClass.tableVariableName»)) {
		  		System.out.println("+ entities, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for AspectKind cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "AspectKind"]]»
		  	if (includeMap(aspectKinds, «eClass.tableVariableName»)) {
		  		System.out.println("+ aspectKinds, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for ConceptKind cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "ConceptKind"]]»
		  	if (includeMap(conceptKinds, «eClass.tableVariableName»)) {
		  		System.out.println("+ conceptKinds, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for EntityRelationship cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "EntityRelationship"]]»
		    if (includeMap(entityRelationships, «eClass.tableVariableName»)) {
		    	System.out.println("+ entities, «eClass.tableVariableName»")
		    }
		    «ENDFOR»
		
			// Lookup table for ConceptualRelationship cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "ConceptualRelationship"]]»
		    if (includeMap(conceptualRelationships, «eClass.tableVariableName»)) {
		    	System.out.println("+ entities, «eClass.tableVariableName»")
		    }
		    «ENDFOR»
		
			// Lookup table for DataRange cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRange"]]»
		    if (includeMap(dataRanges, «eClass.tableVariableName»)) {
		    	System.out.println("+ entities, «eClass.tableVariableName»")
		    }
		    «ENDFOR»
		
			// Lookup table for DataRelationshipToScalar cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRelationshipToScalar"]]»
		  	if (includeMap(dataRelationshipToScalars, «eClass.tableVariableName»)) {
		  		System.out.println("+ entities, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for DataRelationshipToStructure cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "DataRelationshipToStructure"]]»
		  	if (includeMap(dataRelationshipToStructures, «eClass.tableVariableName»)) {
		  		System.out.println("+ entities, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for Predicate cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "Predicate"]]»
		    if (includeMap(predicates, «eClass.tableVariableName»)) {
		    	System.out.println("+ entities, «eClass.tableVariableName»")
		    }
		    «ENDFOR»

			// Lookup table for RestrictableRelationship cross references
		    «FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "RestrictableRelationship"]]»
		    if (includeMap(restrictableRelationships, «eClass.tableVariableName»)) {
		    	System.out.println("+ entities, «eClass.tableVariableName»")
		    }
		    «ENDFOR»

			// Lookup table for RestrictionStructuredDataPropertyContext cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "RestrictionStructuredDataPropertyContext"]]»
		  	if (includeMap(restrictionStructuredDataPropertyContexts, «eClass.tableVariableName»)) {
		  		System.out.println("+ entities, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for TerminologyBox cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "TerminologyBox"]]»
		  	if (includeMap(terminologyBoxes, «eClass.tableVariableName»)) {
		  		System.out.println("+ entities, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for ConceptTreeDisjunction cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "ConceptTreeDisjunction"]]»
		  	if (includeMap(conceptTreeDisjunctions, «eClass.tableVariableName»)) {
		  		System.out.println("+ entities, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for ConceptualEntitySingletonInstance cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "ConceptualEntitySingletonInstance"]]»
		  	if (includeMap(conceptualEntitySingletonInstances, «eClass.tableVariableName»)) {
		  		System.out.println("+ entities, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
			// Lookup table for SingletonInstanceStructuredDataPropertyContext cross references
		  	«FOR eClass : eClasses.filter[EAllSuperTypes.exists[name == "SingletonInstanceStructuredDataPropertyContext"]]»
		  	if (includeMap(singletonInstanceStructuredDataPropertyContexts, «eClass.tableVariableName»)) {
		  		System.out.println("+ entities, «eClass.tableVariableName»")
		  	}
		  	«ENDFOR»
		
		  	var iterations = 0
		  	val progress = new ArrayList<Boolean>(1)
		  	val allDone = new ArrayList<Boolean>(1)
		  	progress.add(false)
		  	allDone.add(false)
		    do {
		    	progress.set(0, false)
		    	allDone.set(0, true)
		    	iterations += 1
		    	System.out.println("Resolve iterations: "+iterations)
		    	«FOR eClass : eClasses.filter[schemaAPIOrOrderingKeyReferences.size > 0]»
		    	resolve«eClass.tableVariableName.upperCaseInitialOrWord»(rs, progress, allDone)
		    	«ENDFOR»
		    } while (!allDone.get(0) && !progress.get(0) && iterations < 10)
		    if (!allDone.get(0) && !progress.get(0)) {
		    	throw new IllegalArgumentException("Failed to resolve cross references within "+iterations+" iterations.")
		    }
		    val dt = t0 - System.currentTimeMillis
		    val ms = dt % 1000
		    val s = dt / 1000
		    System.out.println("Resolve: "+r.URI+" in "+s+"s, "+ms+"ms")
		  }

		  «FOR eClass : eClasses.filter[schemaAPIOrOrderingKeyReferences.size > 0]»
		  protected def void resolve«eClass.tableVariableName.upperCaseInitialOrWord»(ResourceSet rs, ArrayList<Boolean> progress, ArrayList<Boolean> allDone) {
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
		  	  				allDone.set(0, false)
		  	    			«FOR attr : eClass.schemaAPIOrOrderingKeyReferences»
		  	    			«IF (attr.isIRIReference)»
		  	    			val String «attr.name»IRI = kv.get("«attr.columnName»")
		  	    			val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»IRI)
		  	    			if (null !== «attr.name»Pair) {
		  	    				oml.«attr.name» = «attr.name»Pair.key
		  	    				kv.remove("«attr.columnName»")
		  	    				progress.set(0, true)
		  	    			} else
		  	    				progress.set(0, false)
		  	    			«ELSEIF (attr.lowerBound == 0)»
		  	    			val String «attr.name»XRef = kv.get("«attr.columnName»")
		  	    			if (null !== «attr.name»XRef && "null" != «attr.name»XRef) {
		  	    				val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	    				if (null !== «attr.name»Pair) {
		  	    					oml.«attr.name» = «attr.name»Pair.key
		  	    					kv.remove("«attr.columnName»")
		  	    					progress.set(0, true)
		  	    				} else
		  	    					progress.set(0, false)
		  	    			}
		  	    			«ELSE»
		  	    			val String «attr.name»XRef = kv.get("«attr.columnName»")
		  	    			val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	    			if (null !== «attr.name»Pair) {
		  	    				oml.«attr.name» = «attr.name»Pair.key
		  	    				kv.remove("«attr.columnName»")
		  	    				progress.set(0, true)
		  	    			} else
		  	    				progress.set(0, false)
		  	    			«ENDIF»
		  	    			«ENDFOR»
		  	  			}
		  			]
		  		}
		  	} while (more && progress.get(0))
		  	«ELSE»
		  	
		  	«eClass.tableVariableName».forEach[uuid, oml_kv |
		  	  val «eClass.name» oml = oml_kv.key
		  	  val Map<String, String> kv = oml_kv.value
		  	  if (!kv.empty) {
		  	    «FOR attr : eClass.schemaAPIOrOrderingKeyReferences»
		  	    «IF (attr.isIRIReference)»
		  	    val String «attr.name»IRI = kv.get("«attr.columnName»")
		  	    val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»IRI)
		  	    if (null !== «attr.name»Pair) {
		  	    	oml.«attr.name» = «attr.name»Pair.key
		  	    	kv.remove("«attr.columnName»")
		  	    	progress.set(0, true)
		  	    }
		  	    «ELSEIF (attr.lowerBound == 0)»
		  	    val String «attr.name»XRef = kv.get("«attr.columnName»")
		  	    if (null !== «attr.name»XRef && "null" != «attr.name»XRef) {
		  	      val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	      if (null !== «attr.name»Pair) {
		  	      	oml.«attr.name» = «attr.name»Pair.key
		  	      	kv.remove("«attr.columnName»")
		  	      	progress.set(0, true)
		  	      }
		  	    }
		  	    «ELSE»
		  	    val String «attr.name»XRef = kv.get("«attr.columnName»")
		  	    val Pair<«attr.EClassType.name», Map<String, String>> «attr.name»Pair = «attr.EClassType.tableVariableName».get(«attr.name»XRef)
		  	    if (null !== «attr.name»Pair) {
		  	    	oml.«attr.name» = «attr.name»Pair.key
		  	    	kv.remove("«attr.columnName»")
		  	    	progress.set(0, true)
		  	    }
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
		
			val uriString = uri.toString
		  	val Resource r = if (uriString.startsWith("file:")) {
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
					r0
				} else {
					val r1 = omlCatalog.resolveURI(uriString + ".oml")
			  		val r2 = omlCatalog.resolveURI(uriString + ".omlzip")
			  		val r3 = omlCatalog.resolveURI(uriString)
			  				  		
			  		val f1 = if (null !== r1 && r1.startsWith("file:")) new File(r1.substring(5)) else null
			  		val f2 = if (null !== r2 && r2.startsWith("file:")) new File(r2.substring(5)) else null
			  		val f3 = if (null !== r3 && r3.startsWith("file:")) new File(r3.substring(5)) else null
			  		
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
		  	
		  	switch r {
		  		XtextResource:
		  			r.contents.forEach[e|
		  				switch e {
		  					Extent: {
		  						e.modules.forEach[queueModule]
							}
						}
					]
			}
		
		  	r
		  }

		  def void includeModule(Module m) {
		  	if (null !== m) {
		  	  val iri = m.iri()
		  	  val uuid = m.uuid()
		  	  iri2module.put(iri, m)
		      switch m {
		    	    TerminologyGraph: {
		    	  	  logicalElements.put(uuid, new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyGraphs.put(uuid, new Pair<TerminologyGraph, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyBoxes.put(uuid, new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyBoxes.put(iri, new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))
		    	    }
		    	    Bundle: {
		    	  	  logicalElements.put(uuid, new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))
		    	      bundles.put(uuid, new Pair<Bundle, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyBoxes.put(uuid, new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))
		    	      terminologyBoxes.put(iri, new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))
		    	    }
		    	    DescriptionBox: {
		    	  	  logicalElements.put(uuid, new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))
		    	      descriptionBoxes.put(uuid, new Pair<DescriptionBox, Map<String,String>>(m, Collections.emptyMap))
		    	      descriptionBoxes.put(iri, new Pair<DescriptionBox, Map<String,String>>(m, Collections.emptyMap))
		    	    }
		      }
		  	
		  	  modules.put(uuid, new Pair<Module, Map<String,String>>(m, Collections.emptyMap))
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
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "AspectKind"])»
		  	        aspectKinds.put(e.uuid(), new Pair<AspectKind, Map<String,String>>(e, Collections.emptyMap))
		  	        «ELSEIF (eClass.EAllSuperTypes.exists[name == "ConceptKind"])»
		  	        conceptKinds.put(e.uuid(), new Pair<ConceptKind, Map<String,String>>(e, Collections.emptyMap))
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