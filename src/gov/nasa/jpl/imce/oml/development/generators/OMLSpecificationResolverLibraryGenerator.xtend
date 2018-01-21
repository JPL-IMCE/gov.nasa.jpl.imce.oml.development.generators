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
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature

class OMLSpecificationResolverLibraryGenerator extends OMLUtilities {

	static def void main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.resolver project")
			System.exit(1)
		}

		val gen = new OMLSpecificationResolverLibraryGenerator()
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
		val targetFolder = "src/main/scala/gov/nasa/jpl/imce/oml/resolver/impl"
		val targetPath = bundlePath.resolve(targetFolder)
		targetPath.toFile.mkdirs

		generate(#[c, t, g, b, d], targetPath.toAbsolutePath.toString)
	}

	def generate(List<EPackage> ePackages, String targetFolder) {
		val factoryFile = new FileOutputStream(
			new File(targetFolder + File::separator + "OMLResolvedFactoryImpl.scala"))
		try {
			factoryFile.write(generateFactoryFile(ePackages, "gov.nasa.jpl.imce.oml.resolver.impl").bytes)
			for (eClass : ePackages.map[FunctionalAPIClasses].flatten.filter[!isExtentContainer]) {
				val classFile = new FileOutputStream(new File(targetFolder + File::separator + eClass.name + ".scala"))
				try {
					classFile.write(generateClassFile(eClass).bytes)
				} finally {
					classFile.close
				}
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
			 
			 «FOR eClass : ePackages.map[FunctionalAPIClasses].flatten.filter[!isAbstract && !isExtentContainer].sortBy[name]»
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
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val contained = container?.EOpposite
		val newVal = eClass.name.toFirstLower
		if (null === container) '''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( " else "( extent: resolver.api.Extent,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= scala.Tuple2(
				extent, 
					«eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			)
		''' else '''
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

	def String factoryMethodWithUUIDGenerator(EClass eClass, EStructuralFeature uuidNS,
		Iterable<EStructuralFeature> uuidFactors) {
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val contained = container?.EOpposite
		val newVal = eClass.name.toFirstLower
		if (null === container) {
			if (eClass.isExtentManaged) '''
				override def create«eClass.name»
				«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
				: (resolver.api.Extent, resolver.api.«eClass.name»)
				= {
				  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
				  scala.Tuple2(
					extent.copy(«eClass.tableVariableName» = extent.«eClass.tableVariableName» + (uuid -> «newVal»)), 
						«newVal»)
				}
			''' else '''
				override def create«eClass.name»
				«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
				: (resolver.api.Extent, resolver.api.«eClass.name»)
				= scala.Tuple2(
					extent,
					«eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
				)
			'''
		} else '''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
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

	def String factoryMethodWithUUIDGenerator(EClass eClass, EStructuralFeature uuidNS, String uuidScala) {
		// There may be more than 1 container!
		val containers = eClass.getSortedAttributeFactorySignature.filter(EReference).filter[isContainer].toList
		if (1 == containers.size)
			factoryMethodWithUUIDGenerator1(eClass, uuidNS, uuidScala, containers.get(0))
		else if (2 == containers.size)
			factoryMethodWithUUIDGenerator2(eClass, uuidNS, uuidScala, containers.get(0), containers.get(1))
		else
			throw new IllegalArgumentException('''factoryMethodWithUUIDGenerator: eClass=«eClass.name»''')
	}
	
	def String factoryMethodWithUUIDGenerator1(EClass eClass, EStructuralFeature uuidNS, String uuidScala, EReference container) {
		val contained = container?.EOpposite
		val newVal = eClass.name.toFirstLower
		'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= {
			  // factoryMethodWithUUIDGenerator (scala...)
			  
			  // container: «container.name» «container.EType.name»
			  // contained: «contained.name» «contained.EType.name»
			  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			  scala.Tuple2(
			    «IF (container.lowerBound == 0)»
			    «container.name».fold {
			    extent.copy(
			     «contained.EType.name.toFirstLower»ByUUID = extent.«contained.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»))
			    }{ _«container.name»_ =>
			    extent.copy(
			     «contained.name» = extent.with«contained.EType.name»(_«container.name»_, «newVal»),
			     «container.EType.name.toFirstLower»Of«contained.EType.name» = extent.«container.EType.name.toFirstLower»Of«contained.EType.name» + («newVal» -> _«container.name»_),
			     «contained.EType.name.toFirstLower»ByUUID = extent.«contained.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»))
			    },
			    «ELSE»
			    extent.copy(
			     «contained.name» = extent.with«contained.EType.name»(«container.name», «newVal»),
			     «container.EType.name.toFirstLower»Of«contained.EType.name» = extent.«container.EType.name.toFirstLower»Of«contained.EType.name» + («newVal» -> «container.name»),
			     «contained.EType.name.toFirstLower»ByUUID = extent.«contained.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)),
			    «ENDIF»
			  	«newVal»)
			}
		'''
	}

	def String factoryMethodWithUUIDGenerator2(EClass eClass, EStructuralFeature uuidNS, String uuidScala, EReference container1, EReference container2) {
		val contained1 = container1?.EOpposite
		val contained2 = container2?.EOpposite
		val newVal = eClass.name.toFirstLower
		'''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= {
			  // factoryMethodWithUUIDGenerator (scala...)
			  
			  // container1: «container1.name» «container1.EType.name»
			  // contained1: «contained1.name» «contained1.EType.name»
			  // container2: «container2.name» «container2.EType.name»
			  // contained2: «contained2.name» «contained2.EType.name»
			  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			  «IF (container1.lowerBound == 0)»
			  val extent1 = «container1.name».fold {
			  	extent.copy(
			  	  «contained1.EType.name.toFirstLower»ByUUID = extent.«contained1.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)
			  	)
			  }{ _«container1.name»_ =>
			  	extent.copy(
			  	  «contained1.name» = extent.with«contained1.EType.name»(_«container1.name»_, «newVal»),
			  	  «container1.EType.name.toFirstLower»Of«contained1.EType.name» = extent.«container1.EType.name.toFirstLower»Of«contained1.EType.name» + («newVal» -> _«container1.name»_),
			  	  «contained1.EType.name.toFirstLower»ByUUID = extent.«contained1.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)
			  	)
			  }
			  «ELSE»
			  val extent1 = extent.copy(
			  	«contained1.name» = extent.with«contained1.EType.name»(«container1.name», «newVal»),
			  	«container1.EType.name.toFirstLower»Of«contained1.EType.name» = extent.«container1.EType.name.toFirstLower»Of«contained1.EType.name» + («newVal» -> «container1.name»),
			  	«contained1.EType.name.toFirstLower»ByUUID = extent.«contained1.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)),
			  «ENDIF»
			  «IF (container2.lowerBound == 0)»
			  val extent2 = «container2.name».fold {
			  	extent1.copy(
			  	  «contained2.EType.name.toFirstLower»ByUUID = extent.«contained2.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)
			  	)
			  }{ _«container2.name»_ =>
			  	extent1.copy(
			  	  «contained2.name» = extent.with«contained2.EType.name»(_«container2.name»_, «newVal»),
			  	  «container2.EType.name.toFirstLower»Of«contained2.EType.name» = extent.«container2.EType.name.toFirstLower»Of«contained2.EType.name» + («newVal» -> _«container2.name»_),
			  	  «contained2.EType.name.toFirstLower»ByUUID = extent.«contained2.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)
			  	)
			  }
			  «ELSE»
			  val extent2 = extent1.copy(
			  	«contained2.name» = extent.with«contained2.EType.name»(«container1.name», «newVal»),
			  	«container2.EType.name.toFirstLower»Of«contained2.EType.name» = extent.«container2.EType.name.toFirstLower»Of«contained2.EType.name» + («newVal» -> «container2.name»),
			  	«contained2.EType.name.toFirstLower»ByUUID = extent.«contained2.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)),
			  «ENDIF»
			  scala.Tuple2(extent2,«newVal»)
			}
		'''
	}
	def String factoryMethodWithDerivedUUID(EClass eClass) {
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val contained = container?.EOpposite
		val newVal = eClass.name.toFirstLower
		if (null === container) {
			if (eClass.isExtentManaged) '''
				override def create«eClass.name»
				«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
				: (resolver.api.Extent, resolver.api.«eClass.name»)
				= {
				  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
				  scala.Tuple2(
					extent.copy(«eClass.tableVariableName» = extent.«eClass.tableVariableName» + (uuid -> «newVal»)), 
						«newVal»)
				}
			''' else '''
				override def create«eClass.name»
				«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
				: (resolver.api.Extent, resolver.api.«eClass.name»)
				= scala.Tuple2(
				    extent, 
				    «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isContainer] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
				)
			'''
		} else '''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= {
			  // factoryMethodWithDerivedUUID
			  // container: «container.name» «container.EType.name»
			  // contained: «contained.name» «contained.EType.name»
			  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isFactory] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
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
			if (eClass.isExtentManaged) '''
				override def create«eClass.name»
				«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
				: (resolver.api.Extent, resolver.api.«eClass.name»)
				= {
				  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
				  scala.Tuple2(
					extent.copy(«eClass.tableVariableName» = extent.«eClass.tableVariableName» + (uuid -> «newVal»)), 
						«newVal»)
				}
			''' else '''
				override def create«eClass.name»
				«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
				: (resolver.api.Extent, resolver.api.«eClass.name»)
				= scala.Tuple2(
				    extent, 
					«eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
				)
			'''
		} else '''
			override def create«eClass.name»
			«FOR attr : eClass.getSortedAttributeFactorySignature BEFORE if (eClass.isExtentContainer) "( uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " else "( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes."+eClass.name+"UUID,\n " SEPARATOR ",\n " AFTER " )"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
			: (resolver.api.Extent, resolver.api.«eClass.name»)
			= {
			  // factoryMethodWithImplicitlyDerivedUUID
			  // container: «container.name» «container.EType.name»
			  // contained: «contained.name» «contained.EType.name»
			  val «newVal» = «eClass.name»«FOR attr : eClass.getSortedAttributeFactorySignature.filter[!isFactory] BEFORE "( uuid, " SEPARATOR ", " AFTER " )"»«attr.name»«ENDFOR»
			  scala.Tuple2(
			  	extent.copy(
			  	  «contained.name» = extent.with«contained.EType.name»(«container.name», «newVal»),
			  	  «container.EType.name.toFirstLower»Of«contained.EType.name» = extent.«container.EType.name.toFirstLower»Of«contained.EType.name» + («newVal» -> «container.name»),
			  	  «contained.EType.name.toFirstLower»ByUUID = extent.«contained.EType.name.toFirstLower»ByUUID + (uuid -> «newVal»)),
			  	«newVal»)
			}
		'''
	}

	def String generateClassFile(EClass eClass) {
		
	'''
		«copyright»
		package gov.nasa.jpl.imce.oml.resolver.impl
		
		import gov.nasa.jpl.imce.oml._
		
		«IF (eClass.abstract)»trait «ELSE»case class «ENDIF»«eClass.classDeclaration»«IF (eClass.abstract)»
		{
		  override val uuid: resolver.api.taggedTypes.«eClass.name»UUID«FOR f : eClass.APIStructuralFeatures.filter[name != "uuid"] BEFORE "\n" SEPARATOR "\n" AFTER "\n  "»«f.doc("  ")»override val «f.name»: «f.queryResolverType('resolver.api.')»«ENDFOR»«ELSE»
		{«ENDIF»
		
		«FOR op : eClass.ScalaOperations.filter[null === getEAnnotation("http://imce.jpl.nasa.gov/oml/OverrideVal")]»  «op.doc("  ")»«op.queryResolverName('resolver.api.')»
			  : «op.queryResolverType('resolver.api.')»
			  = «op.queryBody»
		
		«ENDFOR»«IF (eClass.isSpecializationOfRootClass)»  override def canEqual(that: scala.Any): scala.Boolean = that match {
			  case _: «eClass.name» => true
		 	  case _ => false
		  }
		
		«ENDIF»«IF (!eClass.abstract)»
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
	}
	
	static def String classDeclaration(EClass eClass) '''
		«eClass.name»«IF (!eClass.abstract)» private[impl] 
			(
			 «FOR attr : eClass.getSortedAttributeSignature SEPARATOR ","»
			 	override val «attr.name»: «IF (attr.name == "uuid")»resolver.api.taggedTypes.«eClass.name + "UUID"»«ELSE»«attr.queryResolverType('resolver.api.')»«ENDIF»
			 «ENDFOR»
		)«ENDIF»
		extends resolver.api.«eClass.name»
		«FOR parent : eClass.ESuperTypes BEFORE "  with " SEPARATOR "\n  with "»«parent.name»«ENDFOR»
	'''

}
