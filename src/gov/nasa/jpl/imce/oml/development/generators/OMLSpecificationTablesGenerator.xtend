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

import gov.nasa.jpl.imce.oml.oti.provenance.ProvenancePackage
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.List
import java.util.Map
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference

class OMLSpecificationTablesGenerator extends OMLUtilities {
	
	static def void main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.tables project")
			System.exit(1)
		}	
		val gen = new OMLSpecificationTablesGenerator()
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
	
	static def String locateOML2OTI(String path) {
		val url = ProvenancePackage.getResource(path)
		if (null !== url)
			url.toURI.toString
		else {
			val binURL = ProvenancePackage.getResource("/gov/nasa/jpl/imce/oml/oti/provenance/ProvenancePackage.class")
			if (null === binURL)
				throw new IllegalArgumentException("locateXcore: failed to locate path: "+path)
			val Path binPath = Paths.get(binURL.toURI)
			val xcorePath = binPath.parent.parent.parent.parent.parent.parent.parent.parent.parent.resolve(path.substring(1))
			val located = xcorePath.toAbsolutePath.toString
			located
		}
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
		
		val oml2oti_path = "/model/OMLProvenanceOTI.xcore"
		val oml2oti_uri = URI.createPlatformResourceURI("/gov.nasa.jpl.imce.oml.model"+oml2oti_path, false)
		val Map<URI, URI> uriMap = set.getURIConverter().getURIMap()
		uriMap.put(oml2oti_uri, URI.createURI(locateOML2OTI(oml2oti_path)))
		val oml2oti_r = set.getResource(oml2oti_uri, true)
		val oml2oti = oml2oti_r.getContents().filter(EPackage).get(0)
		
      	generate(
      		#[oml2oti], 
      		oml2oti_Folder.toAbsolutePath.toString, 
      		"gov.nasa.jpl.imce.oml.provenance.oti", 
      		"OML2OTIProvenanceTables"
      	)
	}
	
	def generate(List<EPackage> ePackages, String targetFolder, String packageQName, String tableName) {
		val packageFile = new FileOutputStream(new File(targetFolder + File::separator + "package.scala"))
		try {
			packageFile.write(generatePackageFile(ePackages, packageQName).bytes)
		} finally {
			packageFile.close
		}
		val tablesFile = new FileOutputStream(new File(targetFolder + File::separator + tableName + ".scala"))
		try {
			tablesFile.write(generateTablesFile(ePackages, packageQName, tableName).bytes)
		} finally {
			tablesFile.close
		}
		for(eClass : ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI])  {
			val classFile = new FileOutputStream(new File(targetFolder + File::separator + eClass.name + ".scala"))
			try {
				classFile.write(generateClassFile(eClass, packageQName).bytes)
			} finally {
				classFile.close
			}
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
		import scala.collection.immutable.Seq
		«ELSE»
		import scala.collection.immutable.Seq
		«ENDIF»
		import scala.collection.JavaConversions._
		import scala.util.control.Exception._
		import scala.util.{Failure,Success,Try}
		«IF 'OMLSpecificationTables' == tableName»
		import scala.{Boolean,StringContext,Unit}
		import scala.Predef.String
		«ELSE»
		import scala.{Boolean,Unit}
		«ENDIF»
		
		case class «tableName»
		«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n  " AFTER "\n)"»«eClass.tableVariable»«ENDFOR»
		{
		  «FOR eClass : eClasses»
		  «eClass.tableReader(tableName)»
		  «ENDFOR»
		  
		  def isEmpty: Boolean
		  «FOR eClass : eClasses BEFORE "= " SEPARATOR " &&\n  "»«eClass.tableVariableName».isEmpty«ENDFOR»
		  
		  «IF 'OMLSpecificationTables' == tableName»
		  def show: String = {
		  
		    def showSeq[T](title: String, s: Seq[T]): String = {
		      if (s.isEmpty)
		         "\n" + title + ": empty"
		      else
		         "\n" + title + s": ${s.size} entries" +
		         s.map(_.toString).mkString("\n ", "\n ", "\n")
		    }
		  
		    val buff = new scala.collection.mutable.StringBuilder()
		  
		  «FOR eClass : eClasses SEPARATOR "\n"»  buff ++= showSeq("«eClass.tableVariableName»", «eClass.tableVariableName»)«ENDFOR»
		  
		    buff.toString
		  }
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
		  = «FOR eClass : eClasses BEFORE tableName + "(\n    " SEPARATOR ",\n    " AFTER ")"»«eClass.tableVariableName» = t1.«eClass.tableVariableName» ++ t2.«eClass.tableVariableName»«ENDFOR»
		  
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
		  	  omlSchemaJsonZipFile.getParentFile.mkdirs()
		  	  
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
		      
		      zos.close()
		  	  Success(())
		  	}
		
		}
	'''
	}
	
	static def String tableReaderName(EClass eClass)
	  '''read«pluralize(eClass.name)»'''
	
	static def String tableVariable(EClass eClass)
	'''«eClass.tableVariableName» : Seq[«eClass.name»] = Seq.empty'''
	
	static def String tableReader(EClass eClass, String tableName)	
	'''
	def «eClass.tableReaderName»(is: InputStream)
	: «tableName»
	= copy(«eClass.tableVariableName» = readJSonTable(is, «eClass.name»Helper.fromJSON))
	'''
	
	def generateJS(EPackage ePackage, String targetJSFolder) {
		for(eClass : ePackage.EClassifiers.filter(EClass).filter[isFunctionalAPI && hasSchemaOptionalAttributes])  {
			val classFile = new FileOutputStream(new File(targetJSFolder + File::separator + eClass.name + "JS.scala"))
			try {
				classFile.write(generateJSClassFile(eClass).bytes)
			} finally {
				classFile.close
			}
		}
	}
	
	def generateJVM(EPackage ePackage, String targetJVMFolder) {
		for(eClass : ePackage.EClassifiers.filter(EClass).filter[isFunctionalAPI && hasSchemaOptionalAttributes])  {
			val classFile = new FileOutputStream(new File(targetJVMFolder + File::separator + eClass.name + "Java.scala"))
			try {
				classFile.write(generateJVMClassFile(eClass).bytes)
			} finally {
				classFile.close
			}
		}
	}
	
	@SuppressWarnings("unused")
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
		  	= «FOR keyFeature: eClass.orderingKeys»«keyFeature.orderingTableType» match {
		  	 	case c_«keyFeature.columnName» if 0 != c_«keyFeature.columnName» => c_«keyFeature.columnName»
		  	 	case 0 => «ENDFOR»«FOR keyFeature: eClass.orderingKeys BEFORE "0 }" SEPARATOR " }"»«ENDFOR»
		  }
		  
		  «ENDFOR»
		}
	'''
	
	def String generateClassFile(EClass eClass, String packageQName) {
		val uuid = eClass.lookupUUIDFeature
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val uuidNS = eClass.lookupUUIDNamespaceFeature
		val uuidFactors = eClass.lookupUUIDNamespaceFactors
		val pairs = eClass.getSortedAttributeFactorySignature.filter[isUUIDFeature && lowerBound>0]
		val uuidWithGenerator = (null !== uuidNS) && (null !== uuidFactors)
		val uuidWithoutContainer = (null !== uuid) && (null === container) && (null !== uuidNS)
		val uuidWithContainer = (null !== uuid) && (null !== container)
	'''
		«copyright»
		 
		package «packageQName»
		
		import scala.annotation.meta.field
		import scala.scalajs.js.annotation.{JSExport,JSExportTopLevel}
		import scala._
		import scala.Predef._
		
		/**
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes»
		  * @param «attr.columnName»[«attr.lowerBound»,«attr.upperBound»]
		  «ENDFOR» 
		  */
		«IF ! eClass.hasSchemaOptionalAttributes»
		@JSExportTopLevel("«eClass.name»")
		«ENDIF»
		case class «eClass.name»
		(
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ","»
		  @(JSExport @field) «attr.columnName»: «attr.constructorTypeName»
		  «ENDFOR»
		) {
		«IF eClass.hasSchemaOptionalAttributes»
		  def this(
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | a.lowerBound > 0) SEPARATOR ",\n" AFTER ")"»  «attr.columnName»: «attr.constructorTypeName»«ENDFOR»
		  = this(
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ",\n" AFTER ")\n"»«IF attr.lowerBound > 0»    «attr.columnName»«ELSE»    None /* «attr.columnName» */«ENDIF»«ENDFOR»
		
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | a.lowerBound == 0) SEPARATOR ""»
		  def with«attr.columnName.toFirstUpper»(l: «attr.scalaTableTypeName»)	 
		  : «eClass.name»
		  = copy(«attr.columnName»=Some(l))
		  
		  «ENDFOR»
		«ENDIF»
		«IF uuidWithoutContainer»
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE "  // Ctor(uuidWithoutContainer)\n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n" SEPARATOR ",\n" AFTER ")\n  = this(\n      oug.namespaceUUID(\n        "+uuidNS.name+".toString"»    «attr.columnName»: «attr.constructorTypeName»«ENDFOR»«FOR f : uuidFactors SEPARATOR ","»,
		          "«f.name»" -> «f.name»«ENDFOR»«FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE ").toString,\n" SEPARATOR ",\n" AFTER ")\n"»      «attr.columnName»«ENDFOR»
		«ELSEIF uuidWithGenerator»
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE "  // Ctor(uuidWithGenerator)   \n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n" SEPARATOR ",\n" AFTER ")\n  = this(\n      oug.namespaceUUID(\n        "+uuidNS.name+"UUID"»    «attr.columnName»: «attr.constructorTypeName»«ENDFOR»«FOR f : uuidFactors SEPARATOR ","»,
		          "«f.name»" -> «f.name»«ENDFOR»«FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE ").toString,\n" SEPARATOR ",\n" AFTER ")\n"»      «attr.columnName»«ENDFOR»
		«ELSEIF uuidWithContainer»
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE "  // Ctor(uuidWithContainer)   \n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n" SEPARATOR ",\n" AFTER ")\n  = this(\n      oug.namespaceUUID(\n        \""+eClass.name+"\""»    «attr.columnName»: «attr.constructorTypeName»«ENDFOR»«FOR f : pairs»,
		          "«f.name»" -> «f.columnUUID»«ENDFOR»«FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE ").toString,\n" SEPARATOR ",\n" AFTER ")\n"»      «attr.columnName»«ENDFOR»
		«ENDIF»
		
		  override val hashCode
		  : scala.Int 
		  = «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes BEFORE "(" SEPARATOR ", " AFTER ").##"»«attr.columnName»«ENDFOR»
		  
		  override def equals(other: scala.Any): scala.Boolean = other match {
		  	case that: «eClass.name» =>
		  	  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR " &&"»
		  	  (this.«attr.columnName» == that.«attr.columnName»)
		      «ENDFOR»
		    case _ =>
		      false
		  }
		  
		}
		
		@JSExportTopLevel("«eClass.name»Helper")
		object «eClass.name»Helper {
		
		  val TABLE_JSON_FILENAME 
		  : scala.Predef.String 
		  = "«pluralize(eClass.name)».json"
		  
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
	}
	
	def String generateJSClassFile(EClass eClass) '''
		«copyright»
		 
		package gov.nasa.jpl.imce.oml.tables
		
		import scala.scalajs.js.annotation.JSExportTopLevel
		
		@JSExportTopLevel("«eClass.name»JS")
		object «eClass.name»JS {
		  «IF eClass.hasSchemaOptionalAttributes»
		  
		  def js«eClass.name»(
		    «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ","»
		    «attr.columnName»: «attr.jsTypeName»
		    «ENDFOR»
		  )
		  : «eClass.name»
		  = «eClass.name»(
		    «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ","»
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
		  «IF eClass.hasSchemaOptionalAttributes»
		  
		  def java«eClass.name»(
		    «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ","»
		    «attr.columnName»: «attr.javaTypeName»
		    «ENDFOR»
		  )
		  : «eClass.name»
		  = «eClass.name»(
		    «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ","»
		    «attr.javaArgName»
		    «ENDFOR»
		  )
		  «ENDIF»
		  
		}	
	'''
}