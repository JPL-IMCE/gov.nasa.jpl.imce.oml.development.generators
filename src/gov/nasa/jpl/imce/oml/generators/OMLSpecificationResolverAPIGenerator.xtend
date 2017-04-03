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

import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.List
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage


class OMLSpecificationResolverAPIGenerator extends OMLUtilities {
	
	static def main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.tables project")
			System.exit(1)
		}
		
		new OMLSpecificationResolverAPIGenerator().generate(args.get(0))	
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
	
	def String generatePackageFile(List<EPackage> ePackages, String packageQName) '''
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
	
	def String generateFactoryFile(List<EPackage> ePackages, String packageQName) '''
		«copyright»
		package «packageQName»
		
		trait OMLResolvedFactory {
			
		  «FOR eClass: ePackages.map[FunctionalAPIClasses].flatten.filter[!isAbstract].sortBy[name]»
		  // «eClass.name»
		  
		  def create«eClass.name»
		  «FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer)"( " else "( extent: Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('')»«ENDFOR»
		  «IF (eClass.isExtentContainer)»: «eClass.name»«ELSE»: (Extent, «eClass.name»)«ENDIF»
		  
		  «ENDFOR»
		}
	'''
	
	def String generateExtentContainerClassFile(EClass eClass, Iterable<EClass> allEClasses) {
		val extManaged = allEClasses.filter[isExtentManaged]
		val containers = allEClasses.map[EStructuralFeatures].flatten.filter[isContainment]
		
		val containerTypes = containers.map[EClassContainer].toSet.toList.sortBy[name]
		val containedTypes = containers.map[EType].toSet.toList.sortBy[name]
		
		
	'''
		«copyright»
		package gov.nasa.jpl.imce.oml.resolver.api
		
		import scala.collection.immutable.{Map, HashMap, Set}
		import scala.{Option,None,Some}
		 
		«FOR ct : containerTypes BEFORE "// Container types:\n// - " SEPARATOR "\n// - " AFTER "\n"»«ct.EPackage.name».«ct.name»«ENDFOR»
		«FOR ct : containedTypes BEFORE "// Contained types:\n// - " SEPARATOR "\n// - " AFTER "\n"»«ct.EPackage.name».«ct.name»«ENDFOR»
		«eClass.doc("")»case class «eClass.name»
		(«FOR em : extManaged BEFORE " " SEPARATOR ",\n  " AFTER ",\n"»«em.tableVariableName»: Map[java.util.UUID, «em.name»] = HashMap.empty[java.util.UUID, «em.name»]«ENDFOR»
		«FOR c : containers BEFORE "\n  " SEPARATOR ",\n  " AFTER ",\n"»«c.name»: Map[«c.EClassContainer.name», Set[«c.EType.name»]] = HashMap.empty[«c.EClassContainer.name», Set[«c.EType.name»]]«ENDFOR»
		«FOR c : containers.filter[name != "annotations"] BEFORE "\n  " SEPARATOR ",\n  " AFTER "\n"»«c.EType.name.toFirstLower»ByUUID: Map[java.util.UUID, «c.EType.name»] = HashMap.empty[java.util.UUID, «c.EType.name»]«ENDFOR»
		) {
			
		  def lookupTerminologyBox(uuid: Option[java.util.UUID])
		  : Option[TerminologyBox]
		  = uuid.fold[Option[TerminologyBox]](None) { lookupTerminologyBox }
		  
		  def lookupTerminologyBox(uuid: java.util.UUID)
		  : Option[TerminologyBox]
		  = for {
		  	m <- modules.get(uuid)
		  	result <- m match {
		      case tbox: TerminologyBox => Some(tbox)
		  	  case _ => None
		  	}
		  } yield result
		  
		  def lookupTerminologyGraph(uuid: Option[java.util.UUID])
		  : Option[TerminologyGraph]
		  = uuid.fold[Option[TerminologyGraph]](None) { lookupTerminologyGraph }
		
		  def lookupTerminologyGraph(uuid: java.util.UUID)
		  : Option[TerminologyGraph]
		  = for {
		    m <- modules.get(uuid)
		    result <- m match {
		      case tg: TerminologyGraph => Some(tg)
		      case _ => None
		    }
		  } yield result
		
		  def lookupBundle(uuid: Option[java.util.UUID])
		  : Option[Bundle]
		  = uuid.fold[Option[Bundle]](None) { lookupBundle }
		  
		  def lookupBundle(uuid: java.util.UUID)
		  : Option[Bundle]
		  = for {
		    m <- modules.get(uuid)
		    result <- m match {
		      case b: Bundle => Some(b)
		  	  case _ => None
		    }
		  } yield result
		
		  def lookupDescriptionBox(uuid: Option[java.util.UUID])
		  : Option[DescriptionBox]
		  = uuid.fold[Option[DescriptionBox]](None) { lookupDescriptionBox }
		
		  def lookupDescriptionBox(uuid: java.util.UUID)
		  : Option[DescriptionBox]
		  = for {
		    m <- modules.get(uuid)
		    result <- m match {
		      case dbox: DescriptionBox => Some(dbox)
		  	  case _ => None
		    }
		  } yield result
		
		  «FOR em : extManaged SEPARATOR "\n  " AFTER "\n"»
		  def lookup«em.name»(uuid: Option[java.util.UUID])
		  : Option[«em.name»]
		  = uuid.fold[Option[«em.name»]](None) { lookup«em.name» } 
		  
		  def lookup«em.name»(uuid: java.util.UUID)
		  : Option[«em.name»]
		  = «em.tableVariableName».get(uuid)
		  «ENDFOR»

		  «FOR c : containers SEPARATOR "\n  " AFTER "\n"»
		  def lookup«c.name.toFirstUpper»(key: Option[«c.EClassContainer.name»])
		  : Set[«c.EType.name»]
		  = key.fold[Set[«c.EType.name»]](Set.empty[«c.EType.name»]) { lookup«c.name.toFirstUpper» }
		  
		  def lookup«c.name.toFirstUpper»(key: «c.EClassContainer.name»)
		  : Set[«c.EType.name»]
		  = «c.name».getOrElse(key, Set.empty[«c.EType.name»])
		  «IF (c.EType.name != "Annotation")»
		  
		  def lookup«c.EType.name»(uuid: Option[java.util.UUID])
		  : Option[«c.EType.name»]
		  = uuid.fold[Option[«c.EType.name»]](None) { lookup«c.EType.name» } 
		  
		  def lookup«c.EType.name»(uuid: java.util.UUID)
		  : Option[«c.EType.name»]
		  = «c.EType.name.toFirstLower»ByUUID.get(uuid)
	  	  «ENDIF»
		  «ENDFOR»
		
		  def lookupElement(uuid: java.util.UUID)
		  : Option[Element]
		  = lookupModule(uuid)«FOR c : containers.filter[name != "annotations"] BEFORE " orElse\n  " SEPARATOR " orElse\n  " AFTER "\n"»lookup«c.EType.name»(uuid)«ENDFOR»
		
		}
	'''
	}
	
	def String generateClassFile(EClass eClass) '''
		«copyright»
		package gov.nasa.jpl.imce.oml.resolver.api
		
		«eClass.doc("")»«eClass.traitDeclaration»
		{
		«FOR f : eClass.APIStructuralFeatures BEFORE "\n  " SEPARATOR "\n  " AFTER "\n"»«f.doc("  ")»«IF (f.isOverride)»override «ENDIF»val «f.name»: «f.queryResolverType('')»«ENDFOR»
		«FOR op : eClass.APIOperations BEFORE "\n  " SEPARATOR "\n  " AFTER "\n"»«op.doc("  ")»«op.queryResolverName('')»: «op.queryResolverType('')»«ENDFOR»
		«IF (eClass.isRootHierarchyClass)»
		  
		  def canEqual(that: scala.Any): scala.Boolean
		«ENDIF»
		}
	'''
	
	static def String traitDeclaration(EClass eClass) '''
		trait «eClass.name»
		«FOR parent : eClass.ESuperTypes BEFORE "  extends " SEPARATOR "\n  with "»«parent.name»«ENDFOR»
	'''
	
	
}