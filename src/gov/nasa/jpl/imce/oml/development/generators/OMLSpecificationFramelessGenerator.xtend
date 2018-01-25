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
import java.util.ArrayList
import java.util.List
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.ETypedElement

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
		val rfile = new File(targetFolder + File::separator  + "OMLReaders.scala")
		if (rfile.exists)
			rfile.delete
		val readersFile = new FileOutputStream(rfile)
		try {
			readersFile.write(generateReadersFile(ePackages, packageQName).bytes)
		} finally {
			readersFile.close
		}
		val wfile = new File(targetFolder + File::separator  + "OMLParquetWriters.scala")
		if (wfile.exists)
			wfile.delete
		val writersFile = new FileOutputStream(wfile)
		try {
			writersFile.write(generateParquetWritersFile(ePackages, packageQName).bytes)
		} finally {
			writersFile.close
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
	
	
	def String generateParquetWritersFile(List<EPackage> ePackages, String packageQName) {
		val eClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortBy[name]
	'''
		«copyright»

		package «packageQName»
		
		import gov.nasa.jpl.imce.oml.tables
		import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
		import org.apache.spark.sql.SQLContext
		import scala.collection.immutable.Seq
		import scala.Unit
		import scala.Predef.String

		object OMLParquetWriters {
			
			«FOR eClass: eClasses»
			
			def write«eClass.tableVariableName.upperCaseInitialOrWord»
			(table: Seq[tables.«eClass.name»], path: String)
			(implicit sqlContext: SQLContext, encoder: ExpressionEncoder[tables.«eClass.name»])
			: Unit
			= sqlContext
			  .createDataset(table)
			  .write
			  .parquet(path)
			«ENDFOR»
		}
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
	
	static def String rowColumnType(ETypedElement col) {
		val tname = col.EType.name
		if (tname == "LiteralNumber")
			"String, String"
		else if (tname == "LiteralValue")
			"String, String"
		else if (tname == "EBoolean")
			"Boolean"
		else if (tname.endsWith("Kind"))
			"Int"
		else
			"String"
	}
	
	static def String rowColumnQuery(ETypedElement col) {
		val tname = col.EType.name
		if (tname == "LiteralNumber")
			'''row.getAs[GenericRowWithSchema]("«col.columnName»").getAs[String]("value"),row.getAs[GenericRowWithSchema]("«col.columnName»").getAs[String]("literalType")'''
		else if (tname == "LiteralValue")
			'''row.getAs[GenericRowWithSchema]("«col.columnName»").getAs[String]("value"),row.getAs[GenericRowWithSchema]("«col.columnName»").getAs[String]("literalType")'''
		else if (tname == "EBoolean")
			'''row.getAs[Boolean]("«col.columnName»")'''
		else if (tname.endsWith("Kind"))
			'''row.getAs[Int]("«col.columnName»")'''
		else
			'''row.getAs[String]("«col.columnName»")'''
	}
	
	static def String sqlColumnQuery(ETypedElement col) {
		val tname = col.EType.name
		if (tname == "LiteralNumber")
			'''row.getAs[String]("«col.columnName»"),row.getAs[String]("«col.columnName»LiteralType")'''
		else if (tname == "LiteralValue")
			'''row.getAs[String]("«col.columnName»"),row.getAs[String]("«col.columnName»LiteralType")'''
		else if (tname == "EBoolean")
			'''row.getAs[Boolean]("«col.columnName»")'''
		else if (tname.endsWith("Kind"))
			'''row.getAs[Int]("«col.columnName»")'''
		else
			'''row.getAs[String]("«col.columnName»")'''
	}
	
	static def String rowColumnDecl(ETypedElement col) {
		val tname = col.EType.name
		if (tname == "LiteralNumber")
			'''«col.columnName»: String, «col.columnName»LiteralType: String'''
		else if (tname == "LiteralValue")
			'''«col.columnName»: String, «col.columnName»LiteralType: String'''
		else if (tname == "EBoolean")
			'''«col.columnName»: Boolean'''
		else if (tname.endsWith("Kind"))
			'''«col.columnName»: Int'''
		else
			'''«col.columnName»: String'''
	}
	
	static def String rowColumnVars(ETypedElement col) {
		val tname = col.EType.name
		if (tname == "LiteralNumber")
			'''«col.columnName», «col.columnName»LiteralType'''
		else if (tname == "LiteralValue")
			'''«col.columnName», «col.columnName»LiteralType'''
		else if (tname == "EBoolean")
			'''«col.columnName»'''
		else if (tname.endsWith("Kind"))
			'''«col.columnName»'''
		else
			'''«col.columnName»'''
	}
	
	
	def String generateReadersFile(List<EPackage> ePackages, String packageQName) {
		val eClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortBy[name]
	
	'''
		«copyright»
		 
		package «packageQName»
		
		import org.apache.spark.sql.Row
		import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
		import gov.nasa.jpl.imce.oml.tables
		import scala.{Boolean,Int,None,Some,StringContext}
		import scala.Predef.{identity,String}
		
		object OMLReaders {
			
			def terminologyKind(kind: Int)
			: tables.TerminologyKind
			= kind match {
				case 0 =>
				  tables.OpenWorldDefinitions
				case 1 =>
				  tables.ClosedWorldDesignations
		    }

			def terminologyKind(kind: tables.TerminologyKind)
			: Int
			= kind match {
				case tables.OpenWorldDefinitions =>
				  0
				case tables.ClosedWorldDesignations =>
				  1
		    }

			def descriptionKind(kind: Int)
			: tables.DescriptionKind
			= kind match {
				case 0 =>
				  tables.Final
				case 1 =>
				  tables.Partial
		    }

			def descriptionKind(kind: tables.DescriptionKind)
			: Int
			= kind match {
				case tables.Final =>
				  0
				case tables.Partial =>
				  1
		    }
		
			«FOR eClass: eClasses»
			«val cols = eClass.schemaAPIOrOrderingKeyAttributes»
			case class «eClass.name»Tuple
			«FOR col : cols BEFORE "(" SEPARATOR ",\n " AFTER ")"»«rowColumnDecl(col)»«ENDFOR»

			def «eClass.name»Row2Tuple
			(row: Row)
			: «eClass.name»Tuple
			= «FOR col : cols BEFORE eClass.name+"Tuple(\n  " SEPARATOR ",\n  " AFTER "\n)"»«rowColumnQuery(col)»«ENDFOR»

			def «eClass.name»SQL2Tuple
			(row: Row)
			: «eClass.name»Tuple
			= «FOR col : cols BEFORE eClass.name+"Tuple(\n  " SEPARATOR ",\n  " AFTER "\n)"»«sqlColumnQuery(col)»«ENDFOR»
						
			def «eClass.name»Tuple2Type
			(tuple: «eClass.name»Tuple)
			: tables.«eClass.name»
			= «FOR col : cols BEFORE "tables."+eClass.name+"(\n  " SEPARATOR ",\n  " AFTER "\n)"»«val tname = 
			  if (col.columnName == "uuid") "tables.taggedTypes."+eClass.name.lowerCaseInitialOrWord+"UUID(tuple."+col.columnName+")" 
			  else if (col.isIRIReference || col.name == "iri") "tables.taggedTypes.iri(tuple."+col.columnName+")"
			  else if (col.EType.name == "EBoolean") "tuple."+col.columnName
			  else if (col.EType.name == "LiteralValue") '''tables.LiteralValue.fromJSON(s"""{"literalType":"${tuple.«col.columnName»LiteralType}","value":"${tuple.«col.columnName»}"}""")'''
			  else if (col.EType.name == "LiteralNumber") '''tables.LiteralNumber.fromJSON(s"""{"literalType":"${tuple.«col.columnName»LiteralType}","value":"${tuple.«col.columnName»}"}""")'''
			  else if (col.EType.name == "LiteralString") '''tables.taggedTypes.stringDataType(tuple.«col.columnName»)'''
			  else if (col.EType.name == "LiteralDateTime") '''if (tuple.«col.columnName».isEmpty) None else tables.LiteralDateTime.parseDateTime(tuple.«col.columnName»)'''
			  else if (col.isClassFeature) "tables.taggedTypes."+col.EType.name.lowerCaseInitialOrWord+"UUID(tuple."+col.columnName+")"
			  else if (col.name == "kind") col.EType.name.lowerCaseInitialOrWord+"(tuple."+col.columnName+")"
			  else "tables.taggedTypes."+col.EType.name.lowerCaseInitialOrWord+"(tuple."+col.columnName+")"»«IF (col.lowerBound == 0 && col.EType.name != "LiteralDateTime")»if («IF (col.EType.name == "LiteralNumber")»(null == tuple.«col.columnName»LiteralType || tuple.«col.columnName»LiteralType.isEmpty) && (null == tuple.«col.columnName» || tuple.«col.columnName».isEmpty)«ELSE»null == tuple.«col.columnName» || tuple.«col.columnName».isEmpty«ENDIF») None else Some(«tname»)«ELSE»«tname»«ENDIF»«ENDFOR»

			def «eClass.name»Type2Tuple
			(e: tables.«eClass.name»)
			: «eClass.name»Tuple
			= «FOR col : cols BEFORE eClass.name+"Tuple(\n  " SEPARATOR ",\n  " AFTER "\n)"»«val tname = 
			  if (col.columnName == "uuid") "e."+col.columnName
			  else if (col.isIRIReference || col.name == "iri") "e."+col.columnName
			  else if (col.EType.name == "EBoolean") "e."+col.columnName
			  else if (col.EType.name == "LiteralValue" && col.lowerBound == 0) '''e.«col.columnName».fold[String](null) { n => n.value }, e.«col.columnName».fold[String](null) { n => n.literalType.toString }'''
			  else if (col.EType.name == "LiteralValue" && col.lowerBound == 1) '''e.«col.columnName».value, e.«col.columnName».literalType.toString'''
			  else if (col.EType.name == "LiteralNumber") '''e.«col.columnName».fold[String](null) { n => n.value }, e.«col.columnName».fold[String](null) { n => n.literalType.toString }'''
			  else if (col.EType.name == "LiteralString") '''e.«col.columnName»'''
			  else if (col.EType.name == "LiteralDateTime") '''e.«col.columnName».fold[String](null)(_.value)'''
			  else if (col.name == "kind") col.EType.name.lowerCaseInitialOrWord+"(e."+col.columnName+")"
			  else if (col.lowerBound == 0) '''e.«col.columnName».fold[String](null)(identity)'''
			  else '''e.«col.columnName»'''»«tname»«ENDFOR»
			«ENDFOR»	
		}
	'''
	}
	
	def String generatePackageFile(List<EPackage> ePackages, String packageQName, String tableName) {
		val eClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortBy[name]
		
		val cClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortWith(new OMLTableCompare())
		val cClasses1 = cClasses.takeWhile[name != 'BinaryScalarRestriction']
		val cClasses2 = new ArrayList<EClass>()
		cClasses2.addAll(cClasses)
		cClasses2.removeAll(cClasses1)
		val cClasses3 = cClasses2.filter[!name.endsWith('Restriction')]
	
		val restrictions = cClasses.filter[name.endsWith('Restriction')]
		
	'''
		«copyright»

		package «packageQName»
		
		import java.util.Properties
		
		import ammonite.ops.Path
		
		import frameless.{Injection, TypedDataset, TypedExpressionEncoder}
		import gov.nasa.jpl.imce.oml.covariantTag
		import gov.nasa.jpl.imce.oml.covariantTag.@@
		import gov.nasa.jpl.imce.oml.tables
		import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
		import org.apache.spark.sql.{SQLContext, SaveMode, SparkSession}
		
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
		        Failure(cause)
		    }
		    .apply {
		  	  dir.toIO.mkdirs()

		      import spark.implicits._
		      import scala.Predef.refArrayOps
			  
		      «FOR eClass : eClasses SEPARATOR "\n\n"»val «eClass.tableVariableName»
		      : Seq[tables.«eClass.name»]
		      = spark
		        .read
		        .parquet((dir / "«eClass.name».parquet").toIO.getAbsolutePath)
		        .map(OMLReaders.«eClass.name»Row2Tuple)
		        .collect()
		        .map(OMLReaders.«eClass.name»Tuple2Type)
		        .to[Seq]«ENDFOR»

		  	  Success(
		  	    tables.OMLSpecificationTables«FOR eClass : cClasses BEFORE "(\n  " SEPARATOR ",\n  " AFTER "\n))"»«eClass.tableVariableName» = «eClass.tableVariableName»«ENDFOR»
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
		  
		  «ENDFOR»«FOR eClass: eClasses»
		  implicit val «eClass.tableVariableName»Encoder
		  : ExpressionEncoder[tables.«eClass.name»]
		  = TypedExpressionEncoder[tables.«eClass.name»]
		  
		«ENDFOR»
		
		  def parquetWriteOMLSpecificationTables
		  (t: tables.OMLSpecificationTables,
		   dir: Path)
		  (implicit spark: SparkSession, sqlContext: SQLContext)
		  : Unit
		  = {
		  	  dir.toIO.mkdirs()

		      «FOR eClass : eClasses»
		      OMLParquetWriters.write«eClass.tableVariableName.upperCaseInitialOrWord»(
		        t.«eClass.tableVariableName»,
		        (dir / "«eClass.name».parquet").toIO.getAbsolutePath)

		      «ENDFOR»
		  	}

		  def sqlReadOMLSpecificationTables
		  (url: String,
		   props: Properties)
		  (implicit spark: SparkSession, sqlContext: SQLContext)
		  : Try[tables.OMLSpecificationTables]
		  = nonFatalCatch[Try[tables.OMLSpecificationTables]]
		    .withApply {
		      (cause: java.lang.Throwable) =>
		        Failure(cause)
		    }
		    .apply {
		    	
		      import spark.implicits._
		      import scala.Predef.refArrayOps
			  
		      «FOR eClass : eClasses SEPARATOR "\n\n"»val «eClass.tableVariableName»
		      : Seq[tables.«eClass.name»]
		      = spark
		        .read
		        .jdbc(url, "OML.«OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass)»", props)
		        .map(OMLReaders.«eClass.name»SQL2Tuple)
		        .collect()
		        .map(OMLReaders.«eClass.name»Tuple2Type)
		        .to[Seq]«ENDFOR»

		  	  Success(
		  	    tables.OMLSpecificationTables«FOR eClass : cClasses BEFORE "(\n  " SEPARATOR ",\n  " AFTER "\n))"»«eClass.tableVariableName» = «eClass.tableVariableName»«ENDFOR»
		  	}

		  def sqlWriteOMLSpecificationTables
		  (t: tables.OMLSpecificationTables,
		   url: String,
		   props: Properties)
		  (implicit spark: SparkSession, sqlContext: SQLContext)
		  : Try[Unit]
		  = nonFatalCatch[Try[Unit]]
		    .withApply {
		      (cause: java.lang.Throwable) =>
		        Failure(cause)
		    }
		    .apply {
		      import spark.implicits._
		      
		      «FOR eClass : cClasses1»
		      TypedDataset
		        .create(t.«eClass.tableVariableName»)
		        .dataset
		        .map(OMLReaders.«eClass.name»Type2Tuple)
		        .write
		        .mode(SaveMode.Append)
		        .jdbc(url, "OML.«OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass)»", props)
		      
		      «ENDFOR»
			    OMLWriters
			      .writeRestrictions(
			        url, 
			        props,
			        t.scalars.map(_.uuid),«FOR eClass : restrictions SEPARATOR ',\n    ' AFTER ')'»
			        t.«eClass.tableVariableName», "OML.«OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass)»", OMLReaders.«eClass.name»Type2Tuple«ENDFOR»

		      «FOR eClass : cClasses3»
		      «IF eClass.name == 'RuleBodySegment'»
		      OMLWriters
		        .serializeAndWriteRuleBodySegments(
		          url,
		          props,
		          t.ruleBodySegments,
		          "OML.«OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass)»",
		          OMLReaders.«eClass.name»Type2Tuple,
		          Seq.empty[tables.taggedTypes.«eClass.name»UUID],
		          OMLWriters.«eClass.name.toFirstLower»Partitioner)

		      «ELSEIF eClass.name == 'RestrictionStructuredDataPropertyTuple'»
		      OMLWriters
		        .serializeAndWriteRestrictionStructuredDataPropertyTuples(
		          url,
		          props,
		          t.restrictionStructuredDataPropertyTuples,
		          "OML.«OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass)»",
		          OMLReaders.«eClass.name»Type2Tuple,
		          t.entityStructuredDataPropertyParticularRestrictionAxioms.map(_.uuid),
		          OMLWriters.«eClass.name.toFirstLower»Partitioner)

		      «ELSEIF eClass.name == 'AnonymousConceptUnionAxiom'»
		      OMLWriters
		        .serializeAndWriteAnonymousConceptUnionAxioms(
		          url,
		          props,
		          t.anonymousConceptUnionAxioms,
		          "OML.«OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass)»",
		          OMLReaders.«eClass.name»Type2Tuple,
		          t.rootConceptTaxonomyAxioms.map(_.uuid),
		          OMLWriters.«eClass.name.toFirstLower»Partitioner)

		      «ELSEIF eClass.name == 'StructuredDataPropertyTuple'»
		      OMLWriters
		        .serializeAndWriteStructuredDataPropertyTuples(
		          url,
		          props,
		          t.structuredDataPropertyTuples,
		          "OML.«OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass)»",
		          OMLReaders.«eClass.name»Type2Tuple,
		          t.singletonInstanceStructuredDataPropertyValues.map(_.uuid),
		          OMLWriters.«eClass.name.toFirstLower»Partitioner)

		      «ELSE»
		      TypedDataset
		        .create(t.«eClass.tableVariableName»)
		        .dataset
		        .map(OMLReaders.«eClass.name»Type2Tuple)
		        .write
		        .mode(SaveMode.Append)
		        .jdbc(url, "OML.«OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass)»", props)

		      «ENDIF»«ENDFOR»		      
		  	  Success(())
		  	}
		}
				
		case class «tableName»
		«FOR eClass : eClasses BEFORE "(\n  " SEPARATOR ",\n\n  " AFTER "\n)"»«eClass.tableVariableName»
		  : TypedDataset[api.«eClass.name»]«ENDFOR»

	'''
	}
}