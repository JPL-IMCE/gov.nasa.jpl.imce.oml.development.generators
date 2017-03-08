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
import java.util.regex.Pattern
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage

class OMLSpecificationTablesGenerator extends OMLUtilities {
	
	static def main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.tables project")
			System.exit(1)
		}	
		new OMLSpecificationTablesGenerator().generate(args.get(0))	
	}
	
	def generate(String targetDir) {
		val bundlePath = Paths.get(targetDir)
	
      	val oml_Folder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/tables")
		oml_Folder.toFile.mkdirs	
		
      	generate(
      		#[c, t, g, b, d], 
      		oml_Folder.toAbsolutePath.toString, 
      		"gov.nasa.jpl.imce.oml.tables",
      		"OMLSpecificationTables")
      		
      	val oml2oti_Folder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/provenance/oti")
		oml2oti_Folder.toFile.mkdirs
		
      	generate(
      		#[oml2oti], 
      		oml2oti_Folder.toAbsolutePath.toString, 
      		"gov.nasa.jpl.imce.oml.provenance.oti", 
      		"OML2OTIProvenanceTables"
      	)
	}
	
	def generate(List<EPackage> ePackages, String targetFolder, String packageQName, String tableName) {
		val packageFile = new FileOutputStream(new File(targetFolder + File::separator + "package.scala"))
		packageFile.write(generatePackageFile(ePackages, packageQName).bytes)
		val tablesFile = new FileOutputStream(new File(targetFolder + File::separator + tableName + ".scala"))
		tablesFile.write(generateTablesFile(ePackages, packageQName, tableName).bytes)
		for(eClass : ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI])  {
			val classFile = new FileOutputStream(new File(targetFolder + File::separator + eClass.name + ".scala"))
			classFile.write(generateClassFile(eClass, packageQName).bytes)
		}
	}
	
	def String generateTablesFile(List<EPackage> ePackages, String packageQName, String tableName) {
		val eClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortWith(new OMLTableCompare())
	'''
		«copyright»

		package «packageQName»
		
		import java.io.{File,InputStream}
		import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipFile}
		
		«IF 'OMLSpecificationTables' == tableName»
		import scala.collection.immutable.{Map,Seq}
		«ELSE»
		import scala.collection.immutable.Seq
		«ENDIF»
		import scala.collection.JavaConversions._
		import scala.util.control.Exception._
		import scala.util.{Failure,Success,Try}
		import scala.{Boolean,Unit}
		«IF 'OMLSpecificationTables' == tableName»
		import scala.Predef.ArrowAssoc
		«ENDIF»
		
		case class «tableName»
		«IF 'OMLSpecificationTables' == tableName»
		«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n  " AFTER ","»«eClass.tableVariable»«ENDFOR»
		  annotations: Map[AnnotationProperty, Seq[AnnotationEntry]] = Map.empty)
		«ELSE»
		«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n  " AFTER "\n)"»«eClass.tableVariable»«ENDFOR» 
		«ENDIF»
		{
		  «FOR eClass : eClasses»
		  «eClass.tableReader(tableName)»
		  «ENDFOR»
		  
		  def isEmpty: Boolean
		  «IF 'OMLSpecificationTables' == tableName»
		  «FOR eClass : eClasses BEFORE "= " SEPARATOR " &&\n  " AFTER " &&\n  annotations.isEmpty"»«eClass.tableVariableName».isEmpty«ENDFOR»
		  «ELSE»
		  «FOR eClass : eClasses BEFORE "= " SEPARATOR " &&\n  "»«eClass.tableVariableName».isEmpty«ENDFOR»
		  «ENDIF»
		}
		
		object «tableName» {
			
		  def createEmpty«tableName»()
		  : «tableName»
		  = new «tableName»()
		  
		  def load«tableName»(omlSchemaJsonZipFile: File)
		  : Try[«tableName»]
		  = nonFatalCatch[Try[«tableName»]]
		    .withApply {
		      (cause: java.lang.Throwable) =>
		        cause.fillInStackTrace()
		        Failure(cause)
		    }
		    .apply {
		      val zipFile = new ZipFile(omlSchemaJsonZipFile)
		      val omlTables =
		        zipFile
		        .getEntries
		        .toIterable
		        .par
		         .aggregate(«tableName»())(seqop = readZipArchive(zipFile), combop = mergeTables)
		      zipFile.close()
		      Success(omlTables)
		    }
		
		  def mergeTables
		  (t1: «tableName», t2: «tableName»)
		  : «tableName»
		  «IF 'OMLSpecificationTables' == tableName»
		  = «FOR eClass : eClasses BEFORE tableName + "(\n    " SEPARATOR ",\n    " AFTER ",\n    annotations = t1.annotations ++ t2.annotations)"»«eClass.tableVariableName» = t1.«eClass.tableVariableName» ++ t2.«eClass.tableVariableName»«ENDFOR»
		  «ELSE» 
		  = «FOR eClass : eClasses BEFORE tableName + "(\n    " SEPARATOR ",\n    " AFTER ")"»«eClass.tableVariableName» = t1.«eClass.tableVariableName» ++ t2.«eClass.tableVariableName»«ENDFOR» 
		  «ENDIF»
		  
		  def readZipArchive
		  (zipFile: ZipFile)
		  (tables: «tableName», ze: ZipArchiveEntry)
		  : «tableName»
		  = {
		  	val is = zipFile.getInputStream(ze)
		  	ze.getName match {
		  	  «FOR eClass : eClasses»
		  	  case «eClass.name»Helper.TABLE_JSON_FILENAME =>
		  	    tables.«eClass.tableReaderName»(is)
		      «ENDFOR»
		      «IF 'OMLSpecificationTables' == tableName»
		      case annotationPropertyIRI =>
		        tables
		          .annotationProperties
		          .find(_.iri == annotationPropertyIRI)
		          .fold[OMLSpecificationTables](tables) { ap =>
		          val annotationPropertyTable = ap -> readJSonTable[AnnotationEntry](is, AnnotationEntryHelper.fromJSON)
		          tables.copy(annotations = tables.annotations + annotationPropertyTable)
		        }
		      «ENDIF»
		    }
		  }
		  
		  def save«tableName»
		  (tables: «tableName»,
		   omlSchemaJsonZipFile: File)
		  : Try[Unit]
		  = nonFatalCatch[Try[Unit]]
		    .withApply {
		      (cause: java.lang.Throwable) =>
		        cause.fillInStackTrace()
		        Failure(cause)
		    }
		    .apply {
		  	  // @see http://www.oracle.com/technetwork/articles/java/compress-1565076.html
		  	  val fos = new java.io.FileOutputStream(omlSchemaJsonZipFile)
		  	  val bos = new java.io.BufferedOutputStream(fos, 100000)
		  	  val cos = new java.util.zip.CheckedOutputStream(bos, new java.util.zip.Adler32())
		  	  val zos = new java.util.zip.ZipOutputStream(new java.io.BufferedOutputStream(cos))
		  
		  	  zos.setMethod(java.util.zip.ZipOutputStream.DEFLATED)
		  
		      «FOR eClass : eClasses»
		      zos.putNextEntry(new java.util.zip.ZipEntry(«eClass.name»Helper.TABLE_JSON_FILENAME))
		      tables.«eClass.tableVariableName».foreach { t =>
		         val line = «eClass.name»Helper.toJSON(t)+"\n"
		         zos.write(line.getBytes(java.nio.charset.Charset.forName("UTF-8")))
		      }
		      zos.closeEntry()
		      «ENDFOR»
		      
		      «IF 'OMLSpecificationTables' == tableName»
		      tables
		        .annotationProperties
		        .foreach { ap =>
		          tables
		            .annotations
		            .get(ap)
		            .foreach { as =>
		              zos.putNextEntry(new java.util.zip.ZipEntry(ap.iri))
		              as.foreach { a =>
		                val line = AnnotationEntryHelper.toJSON(a)+"\n"
		                zos.write(line.getBytes(java.nio.charset.Charset.forName("UTF-8")))
		              }
		              zos.closeEntry()
		            }
		        }
		      «ENDIF»
		  
		      zos.close()
		  	  Success(())
		  	}
		
		}
	'''
	}
	
	static def String tableReaderName(EClass eClass)
	  '''read«pluralize(eClass.name)»'''
	
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
	
	static def String tableVariable(EClass eClass)
	'''«eClass.tableVariableName» : Seq[«eClass.name»] = Seq.empty'''
	
	static def String tableReader(EClass eClass, String tableName)	
	'''
	def «eClass.tableReaderName»(is: InputStream)
	: «tableName»
	= copy(«eClass.tableVariableName» = readJSonTable(is, «eClass.name»Helper.fromJSON))
	'''
	
	def generateJS(EPackage ePackage, String targetJSFolder) {
		for(eClass : ePackage.EClassifiers.filter(EClass).filter[isFunctionalAPI && hasOptionalAttributes])  {
			val classFile = new FileOutputStream(new File(targetJSFolder + File::separator + eClass.name + "JS.scala"))
			classFile.write(generateJSClassFile(eClass).bytes)
		}
	}
	
	def generateJVM(EPackage ePackage, String targetJVMFolder) {
		for(eClass : ePackage.EClassifiers.filter(EClass).filter[isFunctionalAPI && hasOptionalAttributes])  {
			val classFile = new FileOutputStream(new File(targetJVMFolder + File::separator + eClass.name + "Java.scala"))
			classFile.write(generateJVMClassFile(eClass).bytes)
		}
	}
	
	def String generatePackageFile(List<EPackage> ePackages, String packageQName) '''
		«copyright»

		package «packageQName.substring(0, packageQName.lastIndexOf('.'))»
		
		import java.io.InputStream
		import scala.collection.immutable.Seq
		import scala.io
		import scala.Predef.String
		
		package object «packageQName.substring(packageQName.lastIndexOf('.')+1)» {
			«FOR type : ePackages.map[EClassifiers].flatten.filter(EDataType).filter[t|!(t instanceof EEnum)].sortBy[name]»
				type «type.name» = String
		  	«ENDFOR»
		  	
		  def readJSonTable[T](is: InputStream, fromJSon: String => T)
		  : Seq[T]
		  = io.Source.fromInputStream(is).getLines.map(fromJSon).to[Seq]
		  
		  «FOR eClass: ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPIWithOrderingKeys].sortBy[name]»
		  implicit def «eClass.name.toFirstLower»Ordering
		  : scala.Ordering[«eClass.name»]
		  = new scala.Ordering[«eClass.name»] {
		  	def compare(x: «eClass.name», y: «eClass.name»)
		  	: scala.Int
		  	= «FOR keyFeature: eClass.orderingKeys»x.«keyFeature.columnName».compareTo(y.«keyFeature.columnName») match {
		  	 	case c_«keyFeature.columnName» if 0 != c_«keyFeature.columnName» => c_«keyFeature.columnName»
		  	 	case 0 => «ENDFOR»«FOR keyFeature: eClass.orderingKeys BEFORE "0 }" SEPARATOR " }"»«ENDFOR»
		  }
		  
		  «ENDFOR»
		}
	'''
	
	def String generateClassFile(EClass eClass, String packageQName) '''
		«copyright»
		 
		package «packageQName»
		
		import scala.annotation.meta.field
		import scala.scalajs.js.annotation.JSExport
		import scala._
		import scala.Predef._
		
		/**
		  «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes»
		  * @param «attr.columnName»[«attr.lowerBound»,«attr.upperBound»]
		  «ENDFOR» 
		  */
		«IF ! eClass.hasOptionalAttributes»
		@JSExport
		«ENDIF»
		case class «eClass.name»
		(
		  «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes SEPARATOR ","»
		  @(JSExport @field) «attr.columnName»: «attr.constructorTypeName»
		  «ENDFOR»
		) {
		«IF eClass.hasOptionalAttributes»
		  @JSExport
		  def this(
		  «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes.filter(a | a.lowerBound > 0) SEPARATOR ",\n" AFTER ")"»  «attr.columnName»: «attr.constructorTypeName»«ENDFOR»
		  = this(
		  «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes SEPARATOR ",\n" AFTER ")\n"»«IF attr.lowerBound > 0»    «attr.columnName»«ELSE»    None /* «attr.columnName» */«ENDIF»«ENDFOR»
		
		  «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes.filter(a | a.lowerBound == 0) SEPARATOR ""»
		  def with«attr.columnName.toFirstUpper»(l: «attr.scalaTableTypeName»)	 
		  : «eClass.name»
		  = copy(«attr.columnName»=Some(l))
		  
		  «ENDFOR»
		«ENDIF»
		  override val hashCode
		  : scala.Int 
		  = «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes BEFORE "(" SEPARATOR ", " AFTER ").##"»«attr.columnName»«ENDFOR»
		  
		  override def equals(other: scala.Any): scala.Boolean = other match {
		  	case that: «eClass.name» =>
		  	  «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes SEPARATOR " &&"»
		  	  (this.«attr.columnName» == that.«attr.columnName»)
		      «ENDFOR»
		    case _ =>
		      false
		  }
		  
		}
		
		@JSExport
		object «eClass.name»Helper {
		
		  val TABLE_JSON_FILENAME 
		  : scala.Predef.String 
		  = "«eClass.name»s.json"
		  
		  implicit val w
		  : upickle.default.Writer[«eClass.name»]
		  = upickle.default.macroW[«eClass.name»]
		
		  @JSExport
		  def toJSON(c: «eClass.name»)
		  : String
		  = upickle.default.write(expr=c, indent=0)
		
		  implicit val r
		  : upickle.default.Reader[«eClass.name»]
		  = upickle.default.macroR[«eClass.name»]
		
		  @JSExport
		  def fromJSON(c: String)
		  : «eClass.name»
		  = upickle.default.read[«eClass.name»](c)
		
		}	
	'''
	
	def String generateJSClassFile(EClass eClass) '''
		«copyright»
		 
		package gov.nasa.jpl.imce.oml.tables
		
		import scala.scalajs.js.annotation.JSExport
		
		@JSExport
		object «eClass.name»JS {
		  «IF eClass.hasOptionalAttributes»
		  
		  @JSExport
		  def js«eClass.name»(
		    «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes SEPARATOR ","»
		    «attr.columnName»: «attr.jsTypeName»
		    «ENDFOR»
		  )
		  : «eClass.name»
		  = «eClass.name»(
		    «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes SEPARATOR ","»
		    «attr.jsArgName»
		    «ENDFOR»
		  )
		  «ENDIF»
		  
		}	
	'''
	
	def String generateJVMClassFile(EClass eClass) '''
		«copyright»
		 
		package gov.nasa.jpl.imce.oml.tables
		
		import java.util.Optional
		import scala.compat.java8.OptionConverters._
		
		object «eClass.name»Java {
		  «IF eClass.hasOptionalAttributes»
		  
		  def java«eClass.name»(
		    «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes SEPARATOR ","»
		    «attr.columnName»: «attr.javaTypeName»
		    «ENDFOR»
		  )
		  : «eClass.name»
		  = «eClass.name»(
		    «FOR attr : eClass.functionalAPIOrOrderingKeyAttributes SEPARATOR ","»
		    «attr.javaArgName»
		    «ENDFOR»
		  )
		  «ENDIF»
		  
		}	
	'''
}