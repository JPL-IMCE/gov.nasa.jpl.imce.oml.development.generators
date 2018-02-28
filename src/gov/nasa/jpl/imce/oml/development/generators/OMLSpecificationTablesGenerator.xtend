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
		val ePackages = #[c, t, g, b, d]
      	val packageQName = "gov.nasa.jpl.imce.oml.tables"
      	
      	val bundlePath = Paths.get(targetDir)
		
		val oml_Folder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/tables")
		oml_Folder.toFile.mkdirs	
		
		val jvm_Folder = bundlePath.resolve("jvm/src/main/scala/gov/nasa/jpl/imce/oml/tables")
		jvm_Folder.toFile.mkdirs	
		
		generate(
      		ePackages, 
      		oml_Folder.toAbsolutePath.toString, 
      		packageQName,
      		"OMLSpecificationTables")
      	
      	val tablesFile = new FileOutputStream(jvm_Folder.resolve("OMLSpecificationTables.scala").toFile)
		try {
			tablesFile.write(generateTablesFile(ePackages, packageQName, "OMLSpecificationTables").bytes)
		} finally {
			tablesFile.close
		}
      	val oml_testFolder = bundlePath.resolve("shared/src/test/scala/test/oml/tables")
      	oml_testFolder.toFile.mkdirs	
		
		val uuidGeneratorFile = new FileOutputStream(new File(oml_testFolder + File::separator + "UUIDGenerators.scala"))
		try {
			uuidGeneratorFile.write(generateUUIDGeneratorFile(ePackages, packageQName).bytes)
		} finally {
			uuidGeneratorFile.close
		}
		
		val oml_apiFolder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/resolver/api")
		oml_apiFolder.toFile.mkdirs	
		
		val apiTaggedTypesFile = new FileOutputStream(new File(oml_apiFolder + File::separator + "taggedTypes.scala"))
		try {
			apiTaggedTypesFile.write(generateAPITaggedTypesFile(ePackages, "gov.nasa.jpl.imce.oml.resolver.api").bytes)
		} finally {
			apiTaggedTypesFile.close
		}
		
//      	val oml2oti_Folder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/provenance/oti")
//		oml2oti_Folder.toFile.mkdirs
//		
//		val oml2oti_path = "/model/OMLProvenanceOTI.xcore"
//		val oml2oti_uri = URI.createPlatformResourceURI("/gov.nasa.jpl.imce.oml.model"+oml2oti_path, false)
//		val Map<URI, URI> uriMap = set.getURIConverter().getURIMap()
//		uriMap.put(oml2oti_uri, URI.createURI(locateOML2OTI(oml2oti_path)))
//		val oml2oti_r = set.getResource(oml2oti_uri, true)
//		val oml2oti = oml2oti_r.getContents().filter(EPackage).get(0)
//		
//      	generate(
//      		#[oml2oti], 
//      		oml2oti_Folder.toAbsolutePath.toString, 
//      		"gov.nasa.jpl.imce.oml.provenance.oti", 
//      		"OML2OTIProvenanceTables"
//      	)
	}
	
	def generate(List<EPackage> ePackages, String targetFolder, String packageQName, String tableName) {
		val packageFile = new FileOutputStream(new File(targetFolder + File::separator + "package.scala"))
		try {
			packageFile.write(generatePackageFile(ePackages, packageQName).bytes)
		} finally {
			packageFile.close
		}
		val taggedTypesFile = new FileOutputStream(new File(targetFolder + File::separator + "taggedTypes.scala"))
		try {
			taggedTypesFile.write(generateTaggedTypesFile(ePackages, packageQName).bytes)
		} finally {
			taggedTypesFile.close
		}
		
		for(eClass : ePackages.map[EClassifiers].flatten.filter(EClass))  {
			if (eClass.isFunctionalAPI) {
				val classFile = new FileOutputStream(new File(targetFolder + File::separator + eClass.name + ".scala"))			
				try {
					classFile.write(generateClassFile(eClass, packageQName).bytes)			
				} finally {
					classFile.close
				}
			} else if (!eClass.name.startsWith("Literal") && eClass.name != "Extent") {
				val classFile = new FileOutputStream(new File(targetFolder + File::separator + eClass.name + ".scala"))			
				try {
					classFile.write(generateTraitFile(eClass, packageQName).bytes)		
				} finally {
					classFile.close
				}
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
		import gov.nasa.jpl.imce.oml.parallelSort
		
		«IF 'OMLSpecificationTables' == tableName»
		import scala.collection.immutable.{Seq,Set}
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
		  = «FOR eClass : eClasses BEFORE tableName + "(\n    " SEPARATOR ",\n    " AFTER ")"»«eClass.tableVariableName» = parallelSort.parSortBy((
		        t1.«eClass.tableVariableName».to[Set] ++ 
		        t2.«eClass.tableVariableName».to[Set]
		      ).to[Seq], (a: «eClass.name») => a.uuid)«ENDFOR»
		  
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
	= copy(«eClass.tableVariableName» = 
	  parallelSort.parSortBy((«eClass.tableVariableName».to[Set] ++ 
	   readJSonTable(is, «eClass.name»Helper.fromJSON).to[Set]
	  ).to[Seq], (a: «eClass.name») => a.uuid))
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
	
	@SuppressWarnings("unused")
	def String generateTaggedTypesFile(List<EPackage> ePackages, String packageQName) '''
		«copyright»

		package «packageQName»
		
		import gov.nasa.jpl.imce.oml.taggedTypes.{decodeTag,encodeTag}
		import gov.nasa.jpl.imce.oml.covariantTag
		import gov.nasa.jpl.imce.oml.covariantTag.@@
		import io.circe.{Decoder,Encoder}
		import scala.{Int,Ordering}
		import scala.Predef.String
		
		object taggedTypes {
		
		  «FOR type : ePackages.map[EClassifiers].flatten.filter(EDataType).filter[t|!(t instanceof EEnum) && t.name != "UUID"].sortBy[name]»
		  trait «type.name»Tag
		  type «type.name» = String @@ «type.name»Tag
		  def «type.name.lowerCaseInitialOrWord»(s: String) = covariantTag[«type.name»Tag](s)
		  
		  implicit val decode«type.name»: Decoder[«type.name»] = decodeTag[«type.name»Tag]
		  implicit val encode«type.name»: Encoder[«type.name»] = encodeTag[«type.name»Tag]
		  «ENDFOR»
		  
		  «FOR eClass: ePackages.map[EClassifiers].flatten.filter(EClass).sortBy[name]»
		  trait «eClass.name»Tag«FOR eSup: eClass.ESuperClasses.sortBy[name] BEFORE " <: " SEPARATOR " with "»«eSup.name»Tag«ENDFOR»
		  «ENDFOR»
		  
		  «FOR eClass: ePackages.map[EClassifiers].flatten.filter(EClass).filter[!name.startsWith("Literal") && name != "Extent"].sortBy[name]»
		  type «eClass.name»UUID 
		  = String @@ «eClass.name»Tag
		  
		  def «eClass.name.lowerCaseInitialOrWord»UUID(uuid: String)
		  : «eClass.name»UUID
		  = covariantTag[«eClass.name»Tag][String](uuid)
		  
		  implicit val decode«eClass.name»UUID
		  : Decoder[«eClass.name»UUID]
		  = decodeTag[«eClass.name»Tag]
		  
		  implicit val encode«eClass.name»UUID
		  : Encoder[«eClass.name»UUID]
		  = encodeTag[«eClass.name»Tag]
		  
		  implicit val ordering«eClass.name»UUID
		  : Ordering[«eClass.name»UUID] 
		  = new Ordering[«eClass.name»UUID] {
		  	override def compare
		  	(x: «eClass.name»UUID, 
		  	 y: «eClass.name»UUID)
		  	: Int = x.compareTo(y)
		  }
		  
		  «ENDFOR»
		}
	'''
	
	@SuppressWarnings("unused")
	def String generateAPITaggedTypesFile(List<EPackage> ePackages, String packageQName) '''
		«copyright»

		package «packageQName»
		
		import java.util.UUID
		
		import gov.nasa.jpl.imce.oml.covariantTag
		import gov.nasa.jpl.imce.oml.covariantTag.@@
		
		import io.circe.{HCursor,Json}
		import io.circe.{Decoder,Encoder}
		import scala.{Int,Left,Ordering,Right}
		
		object taggedTypes {
		
		  implicit def decodeTag[Tag]: Decoder[UUID @@ Tag] = new Decoder[UUID @@ Tag] {
		    final def apply(c: HCursor): Decoder.Result[UUID @@ Tag] = c.value.as[UUID] match {
		      case Right(uuid) => Right(covariantTag[Tag][UUID](uuid))
		      case Left(failure) => Left(failure)
		    }
		  }
		
		  implicit def encodeTag[Tag]: Encoder[UUID @@ Tag] = new Encoder[UUID @@ Tag] {
		    final def apply(s: UUID @@ Tag): Json = Json.fromString(s.toString)
		  }
		  
		  def fromUUIDString[Tag](uuid: scala.Predef.String @@ Tag)
		  : UUID @@ Tag 
		  = covariantTag[Tag][UUID](UUID.fromString(uuid))
		  
		  «FOR eClass: ePackages.map[EClassifiers].flatten.filter(EClass).filter[!name.startsWith("Literal") && name != "Extent"].sortBy[name]»
		  type «eClass.name»UUID
		  = UUID @@ gov.nasa.jpl.imce.oml.tables.taggedTypes.«eClass.name»Tag
		  
		  def «eClass.name.lowerCaseInitialOrWord»UUID(uuid: UUID): «eClass.name»UUID
		  = covariantTag[gov.nasa.jpl.imce.oml.tables.taggedTypes.«eClass.name»Tag][UUID](uuid)
		  
		  implicit val decode«eClass.name»UUID: Decoder[«eClass.name»UUID]
		  = decodeTag[gov.nasa.jpl.imce.oml.tables.taggedTypes.«eClass.name»Tag]
		  
		  implicit val encode«eClass.name»UUID: Encoder[«eClass.name»UUID]
		  = encodeTag[gov.nasa.jpl.imce.oml.tables.taggedTypes.«eClass.name»Tag]

		  implicit val ordering«eClass.name»UUID
		  : Ordering[«eClass.name»UUID]
		  = new Ordering[«eClass.name»UUID] {
		  	override def compare
		  	(x: «eClass.name»UUID, 
		  	 y: «eClass.name»UUID)
		  	: Int = x.compareTo(y)
		  }
		  
		  «ENDFOR»
		}
	'''
	
	@SuppressWarnings("unused")
	def String generateUUIDGeneratorFile(List<EPackage> ePackages, String packageQName) '''
		«copyright»

		package test.oml.tables
		
		import gov.nasa.jpl.imce.oml.tables.taggedTypes
		import org.scalacheck.Gen
		
		object UUIDGenerators {
		
		  val uuid = Gen.uuid
		
		  «FOR eClass: ePackages.map[EClassifiers].flatten.filter(EClass).filter[!name.startsWith("Literal") && name != "Extent"].sortBy[name]»
		  val «eClass.name.lowerCaseInitialOrWord»UUID = uuid.map(id => taggedTypes.«eClass.name.lowerCaseInitialOrWord»UUID(id.toString))
		  «ENDFOR»
		
		}
	'''
	
	def String generateClassFile(EClass eClass, String packageQName) {
		val uuid = eClass.lookupUUIDFeature
		val uuidOp = eClass.lookupUUIDOperation?.scalaTablesAnnotation
		val container = eClass.getSortedAttributeFactorySignature.filter(EReference).findFirst[isContainer]
		val uuidNS = eClass.lookupUUIDNamespaceFeature
		val uuidName = if (uuidNS?.name == "iri") "iri" else uuidNS?.name+"UUID"
		val uuidFactors = eClass.lookupUUIDNamespaceFactors
		val pairs = eClass.getSortedAttributeFactorySignature.filter[lowerBound>0] // [isUUIDFeature && lowerBound>0]
		val keyAttributes = eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0)
		val uuidWithGenerator = (null !== uuidNS) && (null !== uuidFactors)
		val uuidWithoutContainer = (null !== uuid) && (null === container) && (null !== uuidNS)
		val uuidWithOperation = (null !== uuidOp)
		val uuidWithContainer = (null !== uuid) && (null !== container)
	'''
		«copyright»
		 
		package «packageQName»
		
		import scala.annotation.meta.field
		import scala.scalajs.js.annotation.{JSExport,JSExportTopLevel}
		«IF (uuidWithGenerator || uuidWithoutContainer)»«IF !uuidFactors.empty»
		import scala.Predef.ArrowAssoc
		«ENDIF»«ELSEIF uuidWithContainer»«IF !pairs.empty»
		import scala.Predef.ArrowAssoc
		«ENDIF»«ENDIF»
		
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
		  «IF null !== eClass.lookupUUIDFeature && attr.columnName == "uuid" && !eClass.ESuperClasses.empty»
		  @(JSExport @field) override val «attr.columnName»: «constructorTypeRef(eClass, attr)»
		  «ELSEIF (attr.EClassContainer != eClass)»
		  @(JSExport @field) override val «attr.columnName»: «constructorTypeRef(eClass, attr)»
		  «ELSE»
		  @(JSExport @field) val «attr.columnName»: «constructorTypeRef(eClass, attr)»
		  «ENDIF»
		  «ENDFOR»
		)«FOR sup : eClass.ESuperClasses BEFORE " extends" SEPARATOR " with"» «sup.name»«ENDFOR» {
		«IF eClass.hasSchemaOptionalAttributes»
		  def this(
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | a.lowerBound > 0) SEPARATOR ",\n" AFTER ")"»  «attr.columnName»: «constructorTypeRef(eClass, attr)»«ENDFOR»
		  = this(
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ",\n" AFTER ")\n"»«IF attr.lowerBound > 0»    «attr.columnName»«ELSE»    scala.None /* «attr.columnName» */«ENDIF»«ENDFOR»
		
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | a.lowerBound == 0) SEPARATOR ""»
		  def with«attr.columnName.toFirstUpper»(«IF uuidWithOperation»oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator, «ENDIF»l: «scalaTableTypeRef(eClass, attr)»)	 
		  : «eClass.name»
		  = copy(«attr.columnName»=scala.Some(l))«IF uuidWithOperation».copy(uuid = calculateUUID(oug))«ENDIF»
		  
		  «ENDFOR»
		«ENDIF»
		«IF uuidWithoutContainer»
		  «FOR attr : keyAttributes BEFORE "  // Ctor(uuidWithoutContainer)\n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n" SEPARATOR ",\n" AFTER ")\n  = this(\n      taggedTypes."+eClass.name.lowerCaseInitialOrWord+"UUID(oug.namespaceUUID(\n        "+uuidNS.name+".toString"»    «attr.columnName»: «constructorTypeRef(eClass, attr)»«ENDFOR»«FOR f : uuidFactors SEPARATOR ","»,
		          "«f.name»" -> «f.name»«ENDFOR»«FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE ").toString),\n" SEPARATOR ",\n" AFTER ")\n"»      «attr.columnName»«ENDFOR»
		«ELSEIF uuidWithGenerator»
		  «FOR attr : keyAttributes BEFORE "  // Ctor(uuidWithGenerator)   \n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n" SEPARATOR ",\n" AFTER ")\n  = this(\n      taggedTypes."+eClass.name.lowerCaseInitialOrWord+"UUID(oug.namespaceUUID(\n        "+uuidName»    «attr.columnName»: «constructorTypeRef(eClass, attr)»«ENDFOR»«FOR f : uuidFactors SEPARATOR ","»,
		          "«f.name»" -> «f.name»«ENDFOR»«FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE ").toString),\n" SEPARATOR ",\n" AFTER ")\n"»      «attr.columnName»«ENDFOR»
		«ELSEIF uuidWithContainer»
		  «FOR attr : keyAttributes BEFORE "  // Ctor(uuidWithContainer)   \n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n" SEPARATOR ",\n" AFTER ")\n  = this(\n      taggedTypes."+eClass.name.lowerCaseInitialOrWord+"UUID(oug.namespaceUUID(\n        \""+eClass.name+"\""»    «attr.columnName»: «constructorTypeRef(eClass, attr)»«ENDFOR»«FOR f : pairs»,
		          "«f.name»" -> «IF f.isUUIDFeature»«f.columnUUID»«ELSEIF f.EType.name == "LiteralString" || f.EType.name == "LocalName"»«f.name»«ELSE»«f.name».value«ENDIF»«ENDFOR»«FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter(a | uuid != a && a.lowerBound > 0) BEFORE ").toString),\n" SEPARATOR ",\n" AFTER ")\n"»      «attr.columnName»«ENDFOR»
		«ENDIF»«IF uuidWithOperation»
		
		def calculateUUID(oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator): taggedTypes.«eClass.name»UUID = «uuidOp»
		
		«ENDIF»
		
		«IF null !== eClass.lookupUUIDFeature»
		  val vertexId: scala.Long = uuid.hashCode.toLong

		«ENDIF»
		  override val hashCode
		  : scala.Int 
		  = «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes BEFORE "(" SEPARATOR ", " AFTER ").##"»«attr.columnName»«ENDFOR»
		  
		  override def equals(other: scala.Any): scala.Boolean = other match {
		  	case that: «eClass.name» =>
		  	  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR " &&"»
		  	  «IF attr.isXRefColumn»«IF attr.lowerBound == 0»((this.«attr.columnName», that.«attr.columnName») match {
		  	      case (scala.Some(t1), scala.Some(t2)) =>
		  	        t1 == t2
		  	      case (scala.None, scala.None) =>
		  	        true
		  	      case _ =>
		  	        false
		  	  })«ELSE»(this.«attr.columnName» == that.«attr.columnName») «ENDIF»«ELSE»(this.«attr.columnName» == that.«attr.columnName»)«ENDIF»
		      «ENDFOR»
		    case _ =>
		      false
		  }
		  
		}
		
		@JSExportTopLevel("«eClass.name»Helper")
		object «eClass.name»Helper {
		
		  import io.circe.{Decoder, Encoder, HCursor, Json}
		  import io.circe.parser.parse
		  import scala.Predef.String
		
		  val TABLE_JSON_FILENAME 
		  : String 
		  = "«pluralize(eClass.name)».json"
		
		  implicit val decode«eClass.name»: Decoder[«eClass.name»]
		  = Decoder.instance[«eClass.name»] { c: HCursor =>
		    
		    import cats.syntax.either._
		  
		    for {
		    	  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR "\n"»«attr.columnName» <- «circeDecoder(eClass, attr)»«ENDFOR»
		    	} yield «eClass.name»(
		    	  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ",\n"»«attr.columnName»«ENDFOR»
		    	)
		  }
		  
		  implicit val encode«eClass.name»: Encoder[«eClass.name»]
		  = new Encoder[«eClass.name»] {
		    override final def apply(x: «eClass.name»): Json 
		    = Json.obj(
		    	  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ",\n"»("«attr.columnName»", «circeEncoder(eClass, attr)»)«ENDFOR»
		    )
		  }
		
		  @JSExport
		  def toJSON(c: «eClass.name»)
		  : String
		  = encode«eClass.name»(c).noSpaces
		
		  @JSExport
		  def fromJSON(c: String)
		  : «eClass.name»
		  = parse(c) match {
		  	case scala.Right(json) =>
		  	  decode«eClass.name»(json.hcursor) match {
		  	    	case scala.Right(result) =>
		  	    	  result
		  	    	case scala.Left(failure) =>
		  	    	  throw failure
		  	  }
		    case scala.Left(failure) =>
		  	  throw failure
		  }
		
		}
	'''
	}
	
	def String generateTraitFile(EClass eClass, String packageQName) {
	'''
		«copyright»
		 
		package «packageQName»
		
		trait «eClass.name»«FOR sup : eClass.ESuperClasses BEFORE " extends" SEPARATOR " with"» «sup.name»«ENDFOR» {
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes»
		  «IF null !== eClass.lookupUUIDFeature && attr.columnName == "uuid" && !eClass.ESuperClasses.empty»
		  override val «attr.columnName»: «constructorTypeRef(eClass, attr)»
		  «ELSEIF (attr.EClassContainer != eClass)»
		  override val «attr.columnName»: «constructorTypeRef(eClass, attr)»
		  «ELSE»
		  val «attr.columnName»: «constructorTypeRef(eClass, attr)»
		  «ENDIF»
		  «ENDFOR»
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