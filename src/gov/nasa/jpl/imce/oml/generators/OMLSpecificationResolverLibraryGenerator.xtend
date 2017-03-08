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
		factoryFile.write(generateFactoryFile(ePackages, "gov.nasa.jpl.imce.oml.resolver.impl").bytes)
		for(eClass : ePackages.map[FunctionalAPIClasses].flatten)  {
			val classFile = new FileOutputStream(new File(targetFolder + File::separator + eClass.name + ".scala"))
			classFile.write(generateClassFile(eClass).bytes)
		}
	}
	
	def String generateFactoryFile(List<EPackage> ePackages, String packageQName) '''
		«copyright»
		package «packageQName»
		
		import gov.nasa.jpl.imce.oml._
		
		case class OMLResolvedFactoryImpl() extends resolver.api.OMLResolvedFactory {
			
		  «FOR eClass: ePackages.map[FunctionalAPIClasses].flatten.filter[!isAbstract].sortBy[name]»
		  // «eClass.name»
		  
		  def create«eClass.name»
		  «FOR attr : eClass.getSortedAttributeSignatureExceptDerived BEFORE "(" SEPARATOR ",\n " AFTER ")"» «attr.name»: «attr.queryResolverType('resolver.api.')»«ENDFOR»
		  : resolver.api.«eClass.name»
		  = resolver.impl.«eClass.name»«FOR attr : eClass.getSortedAttributeSignatureExceptDerived BEFORE "(\n" SEPARATOR ",\n" AFTER " )"»  «attr.name»«ENDFOR»
		  
		  «FOR attr: eClass.lookupCopyConstructorArguments»
		  def copy«eClass.name»_«attr.name»
		  ( that: resolver.api.«eClass.name»,
		    «attr.name»: «attr.queryResolverType('resolver.api.')» )
		  : resolver.api.«eClass.name»
		  = that match {
		  	case x: resolver.impl.«eClass.name» =>
		  	  x.copy(«attr.name» = «attr.name»)
		  }
		  
		  «ENDFOR»
		  «ENDFOR»
		}
	'''
	
	def String generateClassFile(EClass eClass) '''
		«copyright»
		package gov.nasa.jpl.imce.oml.resolver.impl
		
		import gov.nasa.jpl.imce.oml._
		
		«IF (eClass.abstract)»trait «ELSE»case class «ENDIF»«eClass.classDeclaration»
		{
		«FOR op : eClass.ScalaOperations»  «op.doc("  ")»«op.queryResolverName('resolver.api.')»
		  : «op.queryResolverType('resolver.api.')»
		  = «op.queryBody»
		  
		«ENDFOR»		  
		
		«FOR sf : eClass.getSortedDerivedAttributeSignature»  «sf.doc("  ")»override val «sf.name»
		  : «sf.queryResolverType('resolver.api.')»
		  = «sf.queryBody»
		  
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
		 «FOR attr : eClass.getSortedAttributeSignatureExceptDerived SEPARATOR ","»
		 override val «attr.name»: «attr.queryResolverType('resolver.api.')»
		 «ENDFOR»
		)«ENDIF»
		extends resolver.api.«eClass.name»
		«FOR parent : eClass.ESuperTypes BEFORE "  with " SEPARATOR "\n  with "»«parent.name»«ENDFOR»
	'''
	
	
}