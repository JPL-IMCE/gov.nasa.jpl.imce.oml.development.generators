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
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature

class OMLSpecificationResolverLibraryGenerator extends OMLUtilities {
	
	static def main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.tables project")
			System.exit(1)
		}
		
		new OMLSpecificationResolverLibraryGenerator().generate(args.get(0))	
	}
	
	def generate(String targetDir) {
		
		val bundlePath = Paths.get(targetDir)
		val targetFolder = "src/main/scala/gov/nasa/jpl/imce/oml/resolver/impl"
		val targetPath = bundlePath.resolve(targetFolder)
		targetPath.toFile.mkdirs	
		
      	generate(#[c, t, g, b, d], targetPath.toAbsolutePath.toString)	      	
	}
	
	def generate(List<EPackage> ePackages, String targetFolder) {
		val factoryFile = new FileOutputStream(new File(targetFolder + File::separator + "OMLResolvedFactoryImpl.scala"))
		try {
			factoryFile.write(generateFactoryFile(ePackages, "gov.nasa.jpl.imce.oml.resolver.impl").bytes)
			for(eClass : ePackages.map[FunctionalAPIClasses].flatten.filter[!isExtentContainer])  {
				val classFile = new FileOutputStream(new File(targetFolder + File::separator + eClass.name + ".scala"))
				classFile.write(generateClassFile(eClass).bytes)
			}
		} finally {
			factoryFile.close
		}
	}
	
	def String generateFactoryFile(List<EPackage> ePackages, String packageQName) '''
		«copyright»
		package «packageQName»
		
		import gov.nasa.jpl.imce.oml._
		
		import scala.Predef.ArrowAssoc
		
		case class OMLResolvedFactoryImpl
		( override val oug: uuid.OMLUUIDGenerator ) 
		extends resolver.api.OMLResolvedFactory {
			
		  override def createExtent
		  : resolver.api.Extent 
		  = resolver.api.Extent()
		  
		  «FOR eClass: ePackages.map[FunctionalAPIClasses].flatten.filter[!isAbstract && !isExtentContainer].sortBy[name]»
		  // «eClass.name»
		  «eClass.factoryMethod»
		  		  
		  «ENDFOR»
		}
	'''
	
	def String factoryMethod(EClass eClass) {
		val uuid = eClass.lookupUUIDFeature
		if (null === uuid)
			factoryMethodWithoutUUID(eClass)
		else {
			val uuidNS = eClass.lookupUUIDNamespaceFeature
			val uuidFactors = eClass.lookupUUIDNamespaceFactors
			if (null !== uuidNS && null !== uuidFactors) 
				factoryMethodWithUUIDGenerator(eClass, uuidNS, uuidFactors)
			else if (eClass.isUUIDDerived)
				factoryMethodWithDerivedUUID(eClass)
			else
				factoryMethodWithImplicitlyDerivedUUID(eClass)
		}
	}
	
	def String factoryMethodWithoutUUID(EClass eClass) {
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val contained = container?.EOpposite
		val newVal = eClass.name.toFirstLower
		if (null === container)
			'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( " else "( extent: resolver.api.Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= scala.Tuple2(
				extent, 
			 	«eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			)
			'''
		else
			'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( " else "( extent: resolver.api.Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= {
			  // factoryMethodWithoutUUID
			  // container: «container.name» «container.EType.name»
			  // contained: «contained.name» «contained.EType.name»
			  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			  scala.Tuple2(
			    extent.copy(
			      «contained.name» = extent.with«contained.EType.name»(«container.name», «newVal»),
			      «container.EType.name.toFirstLower»Of«contained.EType.name» = extent.«container.EType.name.toFirstLower»Of«contained.EType.name» + («newVal» -> «container.name»)),
			    «newVal»)
			}
			'''
	}
	
	def String factoryMethodWithUUIDGenerator(EClass eClass, EStructuralFeature uuidNS, Iterable<EStructuralFeature> uuidFactors) {
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val contained = container?.EOpposite
		val newVal = eClass.name.toFirstLower
		if (null === container) {
			if (eClass.isExtentManaged)
			'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= {
			  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			  scala.Tuple2(
				extent.copy(«eClass.tableVariableName» = extent.«eClass.tableVariableName» + (uuid -> «newVal»)), 
			 	«newVal»)
			}
			'''
			else
			'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= scala.Tuple2(
				extent,
				«eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			)
			'''	
		} else
		'''
		override def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
		: (resolver.api.Extent, resolver.api.«eClass.name»)
		= {
		  // factoryMethodWithUUIDGenerator
		  // container: «container.name» «container.EType.name»
		  // contained: «contained.name» «contained.EType.name»
		  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
		  scala.Tuple2(
		    extent.copy(
		  	  «contained.name» = extent.with«contained.EType.name»(«container.name», «newVal»),
		  	  «container.EType.name.toFirstLower»Of«contained.EType.name» = extent.«container.EType.name.toFirstLower»Of«contained.EType.name» + («newVal» -> «container.name»),
		  	  «contained.EType.name.toFirstLower»ByUUID = extent.«contained.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)),
		  	«newVal»)
		}
		'''
	}
	
	def String factoryMethodWithDerivedUUID(EClass eClass) {
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val contained = container?.EOpposite
		val newVal = eClass.name.toFirstLower
		if (null === container) {
			if (eClass.isExtentManaged)
			'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= {
			  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			  scala.Tuple2(
				extent.copy(«eClass.tableVariableName» = extent.«eClass.tableVariableName» + (uuid -> «newVal»)), 
			 	«newVal»)
			}
			'''
			else
			'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= scala.Tuple2(
			    extent, 
			    «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			)
			'''
		} else
		'''
		override def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
		: (resolver.api.Extent, resolver.api.«eClass.name»)
		= {
		  // factoryMethodWithDerivedUUID
		  // container: «container.name» «container.EType.name»
		  // contained: «contained.name» «contained.EType.name»
		  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
		  scala.Tuple2(
		  	extent.copy(
		  	  «contained.name» = extent.with«contained.EType.name»(«container.name», «newVal»),
		  	  «container.EType.name.toFirstLower»Of«contained.EType.name» = extent.«container.EType.name.toFirstLower»Of«contained.EType.name» + («newVal» -> «container.name»),
		  	  «contained.EType.name.toFirstLower»ByUUID = extent.«contained.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)),
		  	«newVal»)
		}
		'''
	}
	
	def String factoryMethodWithImplicitlyDerivedUUID(EClass eClass) {
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val contained = container?.EOpposite
		val newVal = eClass.name.toFirstLower
		if (null === container) {
			if (eClass.isExtentManaged)
			'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= {
			  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			  scala.Tuple2(
				extent.copy(«eClass.tableVariableName» = extent.«eClass.tableVariableName» + (uuid -> «newVal»)), 
			 	«newVal»)
			}
			'''
			else
			'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= scala.Tuple2(
			    extent, 
				«eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			)
			'''
		} else
		'''
		override def create«eClass.name»
		«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: java.util.UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: java.util.UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
		: (resolver.api.Extent, resolver.api.«eClass.name»)
		= {
		  // factoryMethodWithImplicitlyDerivedUUID
		  // container: «container.name» «container.EType.name»
		  // contained: «contained.name» «contained.EType.name»
		  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
		  scala.Tuple2(
		  	extent.copy(
		  	  «contained.name» = extent.with«contained.EType.name»(«container.name», «newVal»),
		  	  «container.EType.name.toFirstLower»Of«contained.EType.name» = extent.«container.EType.name.toFirstLower»Of«contained.EType.name» + («newVal» -> «container.name»),
		  	  «contained.EType.name.toFirstLower»ByUUID = extent.«contained.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)),
		  	«newVal»)
		}
		'''
	}
	
	def String generateClassFile(EClass eClass) '''
		«copyright»
		package gov.nasa.jpl.imce.oml.resolver.impl
		
		import gov.nasa.jpl.imce.oml._
		
		«IF (eClass.abstract)»trait «ELSE»case class «ENDIF»«eClass.classDeclaration»
		{
		«IF (eClass.abstract)»«FOR f : eClass.APIStructuralFeatures SEPARATOR "\n  " AFTER "\n  "»«f.doc("  ")»override val «f.name»: «f.queryResolverType('resolver.api.')»«ENDFOR»«ENDIF»
				
		«FOR op : eClass.ScalaOperations.filter[null === getEAnnotation("http://imce.jpl.nasa.gov/oml/OverrideVal")]»  «op.doc("  ")»«op.queryResolverName('resolver.api.')»
		  : «op.queryResolverType('resolver.api.')»
		  = «op.queryBody»
		  
		«ENDFOR»		  
		
		
		«IF (eClass.isSpecializationOfRootClass)»
		
		  override def canEqual(that: scala.Any): scala.Boolean = that match {
		  	case _: «eClass.name» => true
		  	case _ => false
		  }
		«ENDIF»
		«IF (!eClass.abstract)»
		
		  override val hashCode
		  : scala.Int
		  = «FOR keyFeature: eClass.getSortedAttributeSignature BEFORE "(" SEPARATOR ", " AFTER ").##"»«keyFeature.name»«ENDFOR»
		
		  override def equals(other: scala.Any): scala.Boolean = other match {
			  case that: «eClass.name» =>
			    (that canEqual this) &&
			    «FOR keyFeature: eClass.getSortedAttributeSignature SEPARATOR " &&\n"»(this.«keyFeature.name» == that.«keyFeature.name»)«ENDFOR»
		
			  case _ =>
			    false
		  }
		«ENDIF»
		}
	'''
	
	static def String classDeclaration(EClass eClass) '''
		«eClass.name»«IF (!eClass.abstract)» private[impl] 
		(
		 «FOR attr : eClass.getSortedAttributeSignature SEPARATOR ","»
		 override val «attr.name»: «attr.queryResolverType('resolver.api.')»
		 «ENDFOR»
		)«ENDIF»
		extends resolver.api.«eClass.name»
		«FOR parent : eClass.ESuperTypes BEFORE "  with " SEPARATOR "\n  with "»«parent.name»«ENDFOR»
	'''
	
	
}