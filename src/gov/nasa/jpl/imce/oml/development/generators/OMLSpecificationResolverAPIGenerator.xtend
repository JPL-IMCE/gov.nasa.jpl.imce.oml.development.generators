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
import org.eclipse.emf.ecore.EStructuralFeature

class OMLSpecificationResolverAPIGenerator extends OMLUtilities {
	
	static def void main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.tables project")
			System.exit(1)
		}
		
		val gen = new OMLSpecificationResolverAPIGenerator()
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
		val bundlePath = Paths.get(targetDir)
		val targetFolder = "shared/src/main/scala/gov/nasa/jpl/imce/oml/resolver/api"
		val targetPath = bundlePath.resolve(targetFolder)
		targetPath.toFile.mkdirs	
		
      	generate(
      		#[c, t, g, b, d], 
      		"gov.nasa.jpl.imce.oml.resolver.api",
      		targetPath.toAbsolutePath.toString
      	)      	
	}
	
	def generate(List<EPackage> ePackages, String packageQName, String targetFolder) {
		val packageFile = new FileOutputStream(new File(targetFolder + File::separator + "package.scala"))
		try {
			packageFile.write(generatePackageFile(ePackages, packageQName).bytes)
		} finally {
			packageFile.close
		}
		val factoryFile = new FileOutputStream(new File(targetFolder + File::separator + "OMLResolvedFactory.scala"))
		try {
			factoryFile.write(generateFactoryFile(ePackages, packageQName).bytes)
		} finally {
			factoryFile.close
		}
		val eClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isAPI]
		for(eClass : eClasses)  {
			val classFile = new FileOutputStream(new File(targetFolder + File::separator + eClass.name + ".scala"))
			try {
				if (eClass.isExtentContainer)		
					classFile.write(generateExtentContainerClassFile(eClass, eClasses).bytes)	
				else
					classFile.write(generateClassFile(eClass).bytes)	
			} finally {
				classFile.close
			}
		}
	}
	
	@SuppressWarnings("unused")
	def String generatePackageFile(List<EPackage> ePackages, String packageQName) {
	'''
		«copyright»

		package «packageQName.substring(0, packageQName.lastIndexOf('.'))»
		
		package object «packageQName.substring(packageQName.lastIndexOf('.')+1)» {
			
		
		  implicit def UUIDOrdering
		  : scala.Ordering[java.util.UUID]
		  = new scala.Ordering[java.util.UUID] {
		    def compare(x: java.util.UUID, y:java.util.UUID)
		    : scala.Int
		    = x.compareTo(y)
		  }

		  «FOR eClass: ePackages.map[FunctionalAPIClasses].flatten.filter[!orderingKeys.isEmpty].sortBy[name]»
		  implicit def «eClass.orderingClassName»
		  : scala.Ordering[«eClass.name»]
		  = new scala.Ordering[«eClass.name»] {
		  	def compare(x: «eClass.name», y: «eClass.name»)
		  	: scala.Int
		  	= «FOR keyFeature: eClass.orderingKeys»«IF (keyFeature.isClassFeature)»«keyFeature.orderingClassType».compare(x.«keyFeature.name»,y.«keyFeature.name»)«ELSE»«keyFeature.orderingAttributeType»«ENDIF» match {
		  	 	case c_«keyFeature.name» if 0 != c_«keyFeature.name» => c_«keyFeature.name»
		  	 	case 0 => «ENDFOR»«FOR keyFeature: eClass.orderingKeys BEFORE "0 }" SEPARATOR " }"»«ENDFOR»
		  }
		  
		  «ENDFOR»
		}
	'''
	}
    
	def String factoryPreamble(EClass eClass) {
		val p1 = if (eClass.isExtentContainer) "( " else "( extent: Extent,\n "
		val p2 = if (null === eClass.lookupUUIDFeature) p1 else p1 + " uuid: java.util.UUID,\n "
		p2
	}
	
	def String factoryMethod(EClass eClass) {
		val uuid = eClass.lookupUUIDFeature
		if (null === uuid)
			factoryMethodWithoutUUID(eClass)
		else {
			val uuidNS = eClass.lookupUUIDNamespaceFeature
			val uuidFactors = eClass.lookupUUIDNamespaceFactors
			val uuidOp = eClass.lookupUUIDOperation
			val uuidScala = if (null !== uuidOp)
				uuidOp.scalaAnnotation
			else
				null
			if (eClass.isUUIDDerived)
				factoryMethodWithDerivedUUID(eClass)
			else if (null !== uuidNS || !uuidFactors.empty) 
				factoryMethodWithUUIDGenerator(eClass, uuidNS, uuidFactors)
			else if (null !== uuidScala) 
				factoryMethodWithUUIDGenerator(eClass, uuidNS, uuidScala)
			else
				factoryMethodWithImplicitlyDerivedUUID(eClass)
		}
	}
	
	def String factoryMethodWithoutUUID(EClass eClass) {
		'''
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( " else "( extent: Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		'''
	}
	
	def String factoryMethodWithUUIDGenerator(EClass eClass, EStructuralFeature uuidNS, Iterable<EStructuralFeature> uuidFactors) {
		val uuidConv = if (null !== uuidNS && null !== uuidNS.EClassType?.lookupUUIDFeature) ".uuid" else ""
		'''
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( " else "( extent: Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		= «IF (uuidFactors.empty)»{«ELSE»{
		  // namespace uuid...
		  import scala.Predef.ArrowAssoc«ENDIF»
		  «IF (null !== uuidNS)»
		  val uuid = taggedTypes.«eClass.name.lowerCaseInitialOrWord»UUID(namespaceUUID(«uuidNS.name»«uuidConv».toString«FOR f : uuidFactors BEFORE ", " SEPARATOR ", "» "«f.name»" -> «f.name»«ENDFOR»))
		  create«eClass.name»( «FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "uuid, " else "extent, uuid, " SEPARATOR ", "»«attr.name»«ENDFOR» )
		  «ELSE»
		  val uuid = taggedTypes.«eClass.name.lowerCaseInitialOrWord»UUID(namespaceUUID("«eClass.name»"«FOR f : uuidFactors BEFORE ", " SEPARATOR ", "» "«f.name»" -> «f.name»«ENDFOR»))
		  create«eClass.name»( «FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "uuid, " else "extent, uuid, " SEPARATOR ", "»«attr.name»«ENDFOR» )
		  «ENDIF»
		}
		
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: taggedTypes."+eClass.name+"UUID,\n " else "( extent: Extent,\n  uuid: taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		'''
	}
	
	def String factoryMethodWithUUIDGenerator(EClass eClass, EStructuralFeature uuidNS, String uuidScala) {
		'''
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( " else "( extent: Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		= {
		  // custom uuid...
		  import scala.Predef.ArrowAssoc
		  val id = «uuidScala»
		  val uuid = taggedTypes.«eClass.name.lowerCaseInitialOrWord»UUID(id)
		  create«eClass.name»( «FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "uuid, " else "extent, uuid, " SEPARATOR ", "»«attr.name»«ENDFOR» )
		}
		
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: taggedTypes."+eClass.name+"UUID,\n " else "( extent: Extent,\n  uuid: taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		'''
	}
	
	def String factoryMethodWithDerivedUUID(EClass eClass) {
		val pairs = '''«FOR attr : eClass.getSortedAttributeFactorySignature.filter[isEssential]»«IF (attr.isIRIReference)» ++
  Seq("«attr.name»" -> namespaceUUID(«attr.name»).toString)«ELSEIF (0 == attr.lowerBound)» ++
  «attr.name».map { vt => "«attr.name»" -> vt.uuid.toString }«ELSEIF attr.isUUIDFeature» ++
    Seq("«attr.name»" -> «attr.name».uuid.toString)«ELSE» ++
    Seq("«attr.name»" -> «attr.name».value)«ENDIF»«ENDFOR»'''
		
		'''
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( " else "( extent: Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		= {
			// derived uuid...
		  import scala.Predef.ArrowAssoc
		  val uuid = taggedTypes.«eClass.name.lowerCaseInitialOrWord»UUID(namespaceUUID(
		    "«eClass.name»",
		    Seq.empty[(String, String)]«pairs» : _*))
		  create«eClass.name»( «FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "uuid, " else "extent, uuid, " SEPARATOR ", "»«attr.name»«ENDFOR» )
		}
		
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: taggedTypes."+eClass.name+"UUID,\n " else "( extent: Extent,\n  uuid: taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		'''
	}
	
	def String factoryMethodWithImplicitlyDerivedUUID(EClass eClass) {
		val pairs = '''«FOR attr : eClass.getSortedAttributeFactorySignature»«IF (attr.isIRIReference)» ++
  Seq("«attr.name»" -> namespaceUUID(«attr.name»).toString«ELSEIF (0 == attr.lowerBound)» ++
  «attr.name».map { vt => "«attr.name»" -> vt.uuid.toString }«ELSEIF attr.isUUIDFeature» ++
    Seq("«attr.name»" -> «attr.name».uuid.toString)«ELSE» ++
    Seq("«attr.name»" -> «attr.name».toString)«ENDIF»«ENDFOR»'''
		
		'''
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( " else "( extent: Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		= {
			// implicitly derived uuid...
		  import scala.Predef.ArrowAssoc
		  val implicitUUID = taggedTypes.«eClass.name.lowerCaseInitialOrWord»UUID(namespaceUUID(
		    "«eClass.name»",
		    Seq.empty[(String, String)]«pairs» : _*))
		  create«eClass.name»( «FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "implicitUUID, " else "extent, implicitUUID, " SEPARATOR ", "»«attr.name»«ENDFOR» )
		}
		
		def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: taggedTypes."+eClass.name+"UUID,\n " else "( extent: Extent,\n  uuid: taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		«IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		'''
	}
	
	def String generateFactoryFile(List<EPackage> ePackages, String packageQName) '''
		«copyright»
		package «packageQName»
		
		import gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator
		import scala.collection.immutable.Seq
		import scala.Predef.String
		
		trait OMLResolvedFactory {
		  
		  val oug: OMLUUIDGenerator
		  import oug._
		  
		  «FOR eClass: ePackages.map[FunctionalAPIClasses].flatten.filter[!isAbstract].sortBy[name]»
		  // «eClass.name»
		  «eClass.factoryMethod»
		  
		  «ENDFOR»
		}
	'''
	
	def String generateExtentContainerClassFile(EClass eClass, Iterable<EClass> allEClasses) {
		val extManaged = allEClasses.filter[isExtentManaged]
		val containers = allEClasses.map[EStructuralFeatures].flatten.filter[isContainment && !isLiteralFeature]
		
		val containerTypes = containers.map[EClassContainer].toSet.toList.sortBy[name]
		val containedTypes = containers.map[EType].toSet.toList.sortBy[name]
		
		
	'''
		«copyright»
		package gov.nasa.jpl.imce.oml.resolver.api
		
		import scala.collection.immutable.{Map, HashMap, Set}
		import scala.Option
		 
		«FOR ct : containerTypes BEFORE "// Container types:\n// - " SEPARATOR "\n// - " AFTER "\n"»«ct.name» («ct.EPackage.name»)«ENDFOR»
		«FOR ct : containedTypes BEFORE "// Contained types:\n// - " SEPARATOR "\n// - " AFTER "\n"»«ct.name» («ct.EPackage.name»)«ENDFOR»
		«eClass.doc("")»case class «eClass.name»
		(«FOR em : extManaged.filter[!isAbstract] BEFORE " " SEPARATOR ",\n  " AFTER ",\n"»«em.tableVariableName»
		  : Map[taggedTypes.«em.name»UUID, «em.name»] 
		  = HashMap.empty[taggedTypes.«em.name»UUID, «em.name»]«ENDFOR»
		«FOR c : containers BEFORE "\n  " SEPARATOR ",\n  " AFTER ",\n"»«c.name»
		  : Map[«c.EClassContainer.name», «IF (c.upperBound == 1)»«c.EType.name»«ELSE»Set[«c.EType.name»]«ENDIF»]
		  = HashMap.empty[«c.EClassContainer.name», «IF (c.upperBound == 1)»«c.EType.name»«ELSE»Set[«c.EType.name»]«ENDIF»]«ENDFOR»
		«FOR c : containers BEFORE "\n  " SEPARATOR ",\n  " AFTER ",\n"»«c.EClassContainer.name.toFirstLower»Of«c.EType.name»
		  : Map[«c.EType.name», «c.EClassContainer.name»]
		  = HashMap.empty[«c.EType.name», «c.EClassContainer.name»]«ENDFOR»
		«FOR ct : containedTypes BEFORE "\n  " SEPARATOR ",\n  " AFTER "\n"»«ct.name.toFirstLower»ByUUID
		  : Map[taggedTypes.«ct.name»UUID, «ct.name»]
		  = HashMap.empty[taggedTypes.«ct.name»UUID, «ct.name»]«ENDFOR»
		) {
		  «FOR c : containers»«IF (c.upperBound == 1)»
		  def with«c.EType.name»
		  (key: «c.EClassContainer.name», value: «c.EType.name»)
		  : Map[«c.EClassContainer.name», «c.EType.name»] 
		  = «c.name».updated(key, value)
		  
		  «ELSE»
		  def with«c.EType.name»
		  (key: «c.EClassContainer.name», value: «c.EType.name»)
		  : Map[«c.EClassContainer.name», Set[«c.EType.name»]] 
		  = «c.name»
		    .updated(key, «c.name».getOrElse(key, Set.empty[«c.EType.name»]) + value)
		  
		  «ENDIF»
		  «ENDFOR»
		  def lookupModule
		  (uuid: Option[taggedTypes.ModuleUUID])
		  : Option[Module]
		  = uuid.flatMap {
		    lookupModule
		  }
		  
		  def lookupModule
		  (uuid: taggedTypes.ModuleUUID)
		  : Option[Module]
		  = lookupTerminologyBox(uuid.asInstanceOf[taggedTypes.TerminologyBoxUUID]) orElse
		  lookupDescriptionBox(uuid.asInstanceOf[taggedTypes.DescriptionBoxUUID])
		  
		  def lookupTerminologyBox
		  (uuid: Option[taggedTypes.TerminologyBoxUUID])
		  : Option[TerminologyBox]
		  = uuid.flatMap { 
		  	lookupTerminologyBox
		  }
		  
		  def lookupTerminologyBox
		  (uuid: taggedTypes.TerminologyBoxUUID)
		  : Option[TerminologyBox]
		  = lookupTerminologyGraph(uuid.asInstanceOf[taggedTypes.TerminologyGraphUUID]) orElse 
		  lookupBundle(uuid.asInstanceOf[taggedTypes.BundleUUID])
		  «FOR em : extManaged.filter[!isAbstract] SEPARATOR "\n  " AFTER "\n"»
		  
		  def lookup«em.name»
		  (uuid: Option[taggedTypes.«em.name»UUID])
		  : Option[«em.name»]
		  = uuid.flatMap {
		    lookup«em.name»
		  } 
		  
		  def lookup«em.name»
		  (uuid: taggedTypes.«em.name»UUID)
		  : Option[«em.name»]
		  = «em.tableVariableName».get(uuid)
		  «ENDFOR»
		  «FOR c : containers SEPARATOR "\n  " AFTER "\n"»«IF (c.upperBound == 1)»
		  def lookup«c.name.toFirstUpper»
		  (key: Option[«c.EClassContainer.name»])
		  : Option[«c.EType.name»]
		  = key.flatMap { lookup«c.name.toFirstUpper» }
		  
		  def lookup«c.name.toFirstUpper»
		  (key: «c.EClassContainer.name»)
		  : Option[«c.EType.name»]
		  = «c.name».get(key)
		  «IF (c.EType.name != "Annotation" && c.EType != c.EClassContainer)»
		  
		  def lookup«c.EType.name»
		  (uuid: Option[taggedTypes.«c.EType.name»UUID])
		  : Option[«c.EType.name»]
		  = uuid.flatMap {
		    lookup«c.EType.name»
		  }
		  
		  def lookup«c.EType.name»
		  (uuid: taggedTypes.«c.EType.name»UUID)
		  : Option[«c.EType.name»]
		  = «c.EType.name.toFirstLower»ByUUID.get(uuid)
	  	  «ENDIF»
	  	  «ELSE»
		  def lookup«c.name.toFirstUpper»
		  (key: Option[«c.EClassContainer.name»])
		  : Set[«c.EType.name»]
		  = key
		  .fold[Set[«c.EType.name»]] { 
		  	Set.empty[«c.EType.name»] 
		  }{ lookup«c.name.toFirstUpper» }
		  
		  def lookup«c.name.toFirstUpper»
		  (key: «c.EClassContainer.name»)
		  : Set[«c.EType.name»]
		  = «c.name».getOrElse(key, Set.empty[«c.EType.name»])
		  «IF (c.EType.name != "Annotation")»
		  
		  def lookup«c.EType.name»
		  (uuid: Option[taggedTypes.«c.EType.name»UUID])
		  : Option[«c.EType.name»]
		  = uuid.flatMap {
		    lookup«c.EType.name»
		  }
		  
		  def lookup«c.EType.name»
		  (uuid: taggedTypes.«c.EType.name»UUID)
		  : Option[«c.EType.name»]
		  = «c.EType.name.toFirstLower»ByUUID.get(uuid)
	  	  «ENDIF»
	  	  «ENDIF»
		  «ENDFOR»
		
		  def lookupLogicalElement(uuid: taggedTypes.LogicalElementUUID)
		  : Option[LogicalElement]
		  = lookupModule(uuid.asInstanceOf[taggedTypes.ModuleUUID])«FOR c : containers.filter[name != "annotations"] BEFORE " orElse\n  " SEPARATOR " orElse\n  " AFTER "\n"»lookup«c.EType.name»(uuid.asInstanceOf[taggedTypes.«c.EType.name»UUID])«ENDFOR»
		
		}
	'''
	}
	
	def String generateClassFile(EClass eClass) {
		val apiStructuralFeatures = eClass.APIStructuralFeatures
		val apiOperations = eClass.APIOperations
		val apiExtentOperations = apiOperations.filter[isImplicitExtent]
		val hasUUID = apiStructuralFeatures.exists[f|f.name == "uuid"] || apiOperations.exists[op|op.name == "uuid"]
	'''
		«copyright»
		package gov.nasa.jpl.imce.oml.resolver.api
		
		«eClass.doc("")»«eClass.traitDeclaration»
		«IF (hasUUID || eClass.ESuperTypes.empty)»{«ELSE»{
		  override val uuid: taggedTypes.«eClass.name + "UUID"»
		«ENDIF»
		«FOR f : apiStructuralFeatures BEFORE "\n  " SEPARATOR "\n  " AFTER "\n"»«f.doc("  ")»«IF (f.isOverride)»override «ENDIF»val «f.name»: «IF (f.name == "uuid")»taggedTypes.«eClass.name + "UUID"»«ELSE»«f.queryResolverType('')»«ENDIF»«ENDFOR»
		«FOR op : apiOperations BEFORE "\n  " SEPARATOR "\n  " AFTER "\n"»«op.doc("  ")»«op.queryResolverName('')»: «IF (op.name == "uuid")»taggedTypes.«eClass.name + "UUID"»«ELSE»«op.queryResolverType('')»«ENDIF»«ENDFOR»
		  «IF (eClass.isRootHierarchyClass)»
		  
		  val vertexId: scala.Long = uuid.toString.hashCode.toLong
		  
		  def canEqual(that: scala.Any): scala.Boolean
		«ENDIF»
		}
		«IF (!apiExtentOperations.empty)»
		
		object «eClass.name» {
		
		«FOR op : apiExtentOperations»  def «op.name»
		  («eClass.name.toLowerCase.charAt(0)»: «eClass.name», ext: Extent)
		  : «op.queryResolverType('')»
		  = «eClass.name.toLowerCase.charAt(0)».«op.name»()(ext)
		
		«ENDFOR»
		}
		«ENDIF»
	'''
	}
	
	static def String traitDeclaration(EClass eClass) '''
		trait «eClass.name»
		«FOR parent : eClass.ESuperTypes BEFORE "  extends " SEPARATOR "\n  with "»«parent.name»«ENDFOR»
	'''
	
	
}