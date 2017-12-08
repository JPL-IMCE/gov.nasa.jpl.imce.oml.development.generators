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
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage

class OMLSpecificationFramelessGenerator extends OMLUtilities {
	
	static def void main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.frameless project")
			System.exit(1)
		}	
		val gen = new OMLSpecificationFramelessGenerator()
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
      	val packageQName = "gov.nasa.jpl.imce.oml.frameless"
      	
      	val bundlePath = Paths.get(targetDir)
		
		val oml_Folder = bundlePath.resolve("src/main/scala/gov/nasa/jpl/imce/oml/frameless")
		oml_Folder.toFile.mkdirs	
		
		val api_Folder = bundlePath.resolve("src/main/scala/gov/nasa/jpl/imce/oml/frameless/api")
		api_Folder.toFile.mkdirs	
		
		generate(
      		ePackages, 
      		oml_Folder.toAbsolutePath.toString, 
      		packageQName,
      		"OMLSpecificationTypedDatasets")
      		
	}
	
	def generate(List<EPackage> ePackages, String targetFolder, String packageQName, String tableName) {
		val pfile = new File(targetFolder + File::separator + tableName + ".scala")
		if (pfile.exists)
			pfile.delete
		val packageFile = new FileOutputStream(pfile)
		try {
			packageFile.write(generatePackageFile(ePackages, packageQName, tableName).bytes)
		} finally {
			packageFile.close
		}
		val cfile = new File(targetFolder + File::separator  + "OMLCatalystCasts.scala")
		if (cfile.exists)
			cfile.delete
		val castFile = new FileOutputStream(cfile)
		try {
			castFile.write(generateCatalystCastsFile(ePackages, packageQName).bytes)
		} finally {
			castFile.close
		}
		val sfile = new File(targetFolder + File::separator  + "OMLProjections.scala")
		if (sfile.exists)
			sfile.delete
		val smartProjectFile = new FileOutputStream(sfile)
		try {
			smartProjectFile.write(generateSmartProjectsFile(ePackages, packageQName).bytes)
		} finally {
			smartProjectFile.close
		}
		for(eClass : ePackages.map[EClassifiers].flatten.filter(EClass))  {
			if (!eClass.name.startsWith("Literal") && eClass.name != "Extent") {
				val projectFile = new FileOutputStream(new File(targetFolder + File::separator + "api" + File::separator + eClass.name + ".scala"))			
				try {
					projectFile.write(generateProjectionFile(eClass, packageQName).bytes)		
				} finally {
					projectFile.close
				}
			}	
		}
	}
	
	def String generateProjectionFile(EClass eClass, String packageQName) {
	'''
		«copyright»
		 
		package «packageQName».api
		
		import gov.nasa.jpl.imce.oml.tables._

		case class «eClass.name»(
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ",\n  "»
		  «attr.columnName»: «constructorTypeRef(eClass, attr)»
		  «ENDFOR»
		)
	'''
	}
	
	
	def String generateCatalystCastsFile(List<EPackage> ePackages, String packageQName) {
	'''
		«copyright»
		 
		package «packageQName»
		
		import frameless.CatalystCast
		import gov.nasa.jpl.imce.oml.tables.taggedTypes
		import scala.Any

		object OMLCatalystCasts {
			val theInstance = new CatalystCast[Any, Any] {}
			
			«FOR eClass: ePackages.map[EClassifiers].flatten.filter(EClass).filter[!ESuperClasses.nullOrEmpty && !name.startsWith("Literal") && name != "Extent"].sortBy[name]»
			// «eClass.ESuperClasses.size» casts for «eClass.name»
			
			«FOR eSup: eClass.ESuperClasses.sortBy[name]»
			implicit val «eClass.name»2«eSup.name»UUIDCast
			: CatalystCast
			  [ taggedTypes.«eClass.name»UUID,
			    taggedTypes.«eSup.name»UUID]
			= theInstance.asInstanceOf[CatalystCast
			  [ taggedTypes.«eClass.name»UUID,
			    taggedTypes.«eSup.name»UUID]]
			
			«ENDFOR»
			«ENDFOR»
		}
	'''
	}
	
	def String generateSmartProjectsFile(List<EPackage> ePackages, String packageQName) {
	'''
		«copyright»
		 
		package «packageQName»
		
		import frameless.{TypedColumn,TypedDataset}
		import frameless.ops.SmartProject
		import shapeless.HNil
		import gov.nasa.jpl.imce.oml.tables.TerminologyKind
		import gov.nasa.jpl.imce.oml.tables.taggedTypes

		object OMLProjections {
			
			import OMLSpecificationTypedDatasets._
			import OMLCatalystCasts._
			
			«FOR eClass: ePackages.map[EClassifiers].flatten.filter(EClass).filter[!ESuperClasses.nullOrEmpty && !name.startsWith("Literal") && name != "Extent"].sortBy[name]»
			// «eClass.ESuperClasses.size» smart projects for api.«eClass.name»
			
			«FOR eSup: eClass.ESuperClasses.filter[schemaAPIOrOrderingKeyAttributes.size > 1].sortBy[name]»
			implicit val «eClass.name»2«eSup.name»Projection
			: SmartProject
			  [ api.«eClass.name»,
			    api.«eSup.name»]
			= SmartProject
			  [ api.«eClass.name»,
			    api.«eSup.name»](
			    «FOR attr : eSup.schemaAPIOrOrderingKeyAttributes BEFORE "  (x: TypedDataset[api."+eClass.name+"]) => {\n    " SEPARATOR "\n\n    " AFTER "\n\n"»«IF (attr.name == "uuid")»val x_uuid: TypedColumn[api.«eClass.name», taggedTypes.«eSup.name»UUID]
			        = x.col[taggedTypes.«eClass.name»UUID]('uuid).cast[taggedTypes.«eSup.name»UUID]«ELSE»val x_«attr.columnName»: TypedColumn[api.«eClass.name», «constructorTypeRef(eClass, attr)»]
			        = x.col[«constructorTypeRef(eClass, attr)»]('«attr.columnName»)«ENDIF»«ENDFOR»
			        val result
			        : TypedDataset[api.«eSup.name»]
			        = x
			          .selectMany
			          .applyProduct(«FOR attr : eSup.schemaAPIOrOrderingKeyAttributes BEFORE "\n  " SEPARATOR " :: \n  " AFTER " ::\n  HNil)"»x_«attr.columnName»«ENDFOR»
			          .as[api.«eSup.name»]
			        result
			      })

			«ENDFOR»
			«ENDFOR»
		}
	'''
	}
	
	def String generatePackageFile(List<EPackage> ePackages, String packageQName, String tableName) {
		val eClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortBy[name]
	
	'''
		«copyright»

		package «packageQName»
		
		import ammonite.ops.Path
		
		import frameless.{Injection, TypedDataset}
		import gov.nasa.jpl.imce.oml.covariantTag
		import gov.nasa.jpl.imce.oml.covariantTag.@@
		import gov.nasa.jpl.imce.oml.tables
		import org.apache.spark.sql.SQLContext
		import org.apache.spark.sql.SparkSession
		
		import scala.collection.immutable.Seq
		import scala.util.control.Exception._
		import scala.util.{Failure,Success,Try}
		import scala.{Int,Unit}
		import scala.Predef.String

		object «tableName» {
		
		  implicit def tagInjection[Tag] = new Injection[String @@ Tag, String] {
		    def apply(t: String @@ Tag): String = t
		    def invert(s: String): String @@ Tag = covariantTag[Tag][String](s)
		  }
		
		  implicit val literalTypeInjection = new Injection[tables.LiteralType, String] {
		    def apply(t: tables.LiteralType): String = tables.LiteralType.toString(t)
		    def invert(s: String): tables.LiteralType = tables.LiteralType.fromString(s)
		  }
		
		  implicit val literalNumberTypeInjection = new Injection[tables.LiteralNumberType, String] {
		    def apply(t: tables.LiteralNumberType): String = tables.LiteralNumberType.toString(t)
		  	def invert(s: String): tables.LiteralNumberType = tables.LiteralNumberType.fromString(s)
		  }

		  def createEmpty«tableName»()(implicit sqlContext: SQLContext) // frameless 0.5.0: use SparkSession instead.
		  : «tableName»
		  = «tableName»«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n\n  " AFTER "\n)"»«eClass.tableVariableName» = 
		    TypedDataset.create[api.«eClass.name»](
		      Seq.empty[api.«eClass.name»])«ENDFOR»
		    
		  def convertTo«tableName»
		  (t: tables.OMLSpecificationTables)
		  (implicit sqlContext: SQLContext) // frameless 0.5.0: use SparkSession instead.
		  : «tableName»
		  = «tableName»«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n\n  " AFTER "\n)"»«eClass.tableVariableName» = 
		    TypedDataset.create[api.«eClass.name»](
		      t.«eClass.tableVariableName».map(i =>
		       «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes BEFORE "api."+eClass.name+"(\n  " SEPARATOR ",\n  " AFTER ")))"»«attr.columnName» = i.«attr.columnName»«ENDFOR»«ENDFOR»
		
		  def extractFrom«tableName»
		  (t: «tableName»)
		  (implicit sqlContext: SQLContext) // frameless 0.5.0: use SparkSession instead.
		  : tables.OMLSpecificationTables
		  = {
		  	import frameless.syntax.DefaultSparkDelay
		  	
		  	tables.OMLSpecificationTables«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n\n  " AFTER "\n)"»«eClass.tableVariableName» = 
		  	t.«eClass.tableVariableName».collect().run().to[Seq].map(i =>
		  	  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes BEFORE "tables."+eClass.name+"(\n  " SEPARATOR ",\n  " AFTER "))"»«attr.columnName» = i.«attr.columnName»«ENDFOR»«ENDFOR»
		  }
		
		  def parquetReadOMLSpecificationTables
		  (dir: Path)
		  (implicit spark: SparkSession, sqlContext: SQLContext)
		  : Try[tables.OMLSpecificationTables]
		  = nonFatalCatch[Try[tables.OMLSpecificationTables]]
		    .withApply {
		      (cause: java.lang.Throwable) =>
		        cause.fillInStackTrace()
		        Failure(cause)
		    }
		    .apply {
		  	  dir.toIO.mkdirs()

		      import spark.implicits._
			  import scala.Predef.refArrayOps
			  
		  	  Success(
		  	    tables.OMLSpecificationTables«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n\n  " AFTER "\n))"»«eClass.tableVariableName» = 
		  	      spark
		  	      .read
		  	      .parquet((dir / "«eClass.name».parquet").toIO.getAbsolutePath)
		  	      .as[tables.«eClass.name»]
		  	      .collect()
		  	      .to[Seq]«ENDFOR»
		  	}

		  def parquetWriteOMLSpecificationTables
		  (t: tables.OMLSpecificationTables,
		   dir: Path)
		  (implicit spark: SparkSession, sqlContext: SQLContext)
		  : Try[Unit]
		  = nonFatalCatch[Try[Unit]]
		    .withApply {
		      (cause: java.lang.Throwable) =>
		        cause.fillInStackTrace()
		        Failure(cause)
		    }
		    .apply {
		    	  import spark.implicits._

		  	  dir.toIO.mkdirs()

		      «FOR eClass : eClasses»
		      t
		      .«eClass.tableVariableName»
		      .toDF()
		      .write
		      .parquet((dir / "«eClass.name».parquet").toIO.getAbsolutePath())
		      
		      «ENDFOR»
		  	  Success(())
		  	}

		  «FOR eClass: ePackages.map[EClassifiers].flatten.filter(EEnum).sortBy[name]»
		  implicit val «eClass.name.toFirstLower»I
		  : Injection[tables.«eClass.name», Int]
		  = Injection(
		  {
		  	«FOR elit: eClass.ELiterals»
		  	case tables.«elit.literal» => «eClass.ELiterals.indexOf(elit)»
		  	«ENDFOR»
		  },
		  {
		  	«FOR elit: eClass.ELiterals»
		  	case «eClass.ELiterals.indexOf(elit)» => tables.«elit.literal»
		    «ENDFOR»
		  }
		  )
		  
		  «ENDFOR»
		}
				
		case class «tableName»
		«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n\n  " AFTER "\n)"»«eClass.tableVariableName»
		  : TypedDataset[api.«eClass.name»]«ENDFOR»

	'''
	}
}