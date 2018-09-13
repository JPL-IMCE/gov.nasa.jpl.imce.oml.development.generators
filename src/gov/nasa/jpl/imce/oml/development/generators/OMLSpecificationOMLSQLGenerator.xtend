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
import java.util.HashMap
import java.util.List
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage

class OMLSpecificationOMLSQLGenerator extends OMLUtilities {
	
	static def void main(String[] args) {
		if (1 != args.length) {
			System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.sql project")
			System.exit(1)
		}	
		val gen = new OMLSpecificationOMLSQLGenerator()
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
      	val packageQName = "gov.nasa.jpl.imce.oml.sql"
      	
      	val bundlePath = Paths.get(targetDir)
		
		val oml_Folder = bundlePath.resolve("schema")
		oml_Folder.toFile.mkdirs	
		
		generate(
      		ePackages, 
      		oml_Folder.toAbsolutePath.toString, 
      		packageQName,
      		"OML")
      		
		
	}
	
	def generate(List<EPackage> ePackages, String targetFolder, String packageQName, String tableName) {
		val tablesFile = new FileOutputStream(new File(targetFolder + File::separator + tableName + ".sql"))
		try {
			tablesFile.write(generateTablesFile(ePackages, packageQName, tableName).bytes)
		} finally {
			tablesFile.close
		}

	}
	
	def String generateTablesFile(List<EPackage> ePackages, String packageQName, String tableName) {
		val eConcrete = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortWith(new OMLTableCompare())
		val eAbstract =  ePackages.map[EClassifiers].flatten.filter(EClass).filter[abstract && isSchema && isAPI].sortBy[name]
		val abbrevMap = new HashMap<String, EClass>()
		eConcrete.forEach[c|
			val prev = abbrevMap.put(c.abbreviatedTableName, c)
			if (null !== prev) {
				System.out.println('''Conflict for «c.abbreviatedTableName»: «c.name» vs. «prev.name»''')
			}
		]
		eAbstract.forEach[c|
			val prev = abbrevMap.put(c.abbreviatedTableName, c)
			if (null !== prev) {
				System.out.println('''Conflict for «c.abbreviatedTableName»: «c.name» vs. «prev.name»''')
			}
		]
	'''
		«copyrightSQL»

		SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
		SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
		SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

		-- -----------------------------------------------------
		-- Schema OML
		-- -----------------------------------------------------
		CREATE SCHEMA IF NOT EXISTS `OML` DEFAULT CHARACTER SET utf8 ;
		USE `OML` ;
		
		-- Summary of abstract table names
		-- «FOR eClass : eAbstract.sortBy[abbreviatedTableName]»
		-- «eClass.abbreviatedTableName»«eClass.pad» «eClass.tableVariableName.upperCaseInitialOrWord»«ENDFOR»
				
		-- Summary of concrete table names
		-- «FOR eClass : eConcrete.sortBy[abbreviatedTableName]»
		-- «eClass.abbreviatedTableName»«eClass.pad» «eClass.tableVariableName.upperCaseInitialOrWord»«ENDFOR»

		«FOR eClass : eAbstract»
		-- -----------------------------------------------------
		-- Table `OML`.`«eClass.abbreviatedTableName»`
		-- -----------------------------------------------------
		CREATE TABLE IF NOT EXISTS `OML`.`«eClass.abbreviatedTableName»` (
		  `uuid` CHAR(36) NOT NULL PRIMARY KEY,		  
		  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC)	
		)
		COMMENT = 'Abstract Classification Table «eClass.tableVariableName.upperCaseInitialOrWord»';
		
		«ENDFOR»
		
		«FOR eClass : eConcrete»
		-- -----------------------------------------------------
		-- Table `OML`.`«eClass.abbreviatedTableName»`
		-- -----------------------------------------------------
		CREATE TABLE IF NOT EXISTS `OML`.`«eClass.abbreviatedTableName»` (
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ",\n" AFTER ",\n"»`«attr.columnName»` «IF attr.isUUID»CHAR(36) NOT NULL PRIMARY KEY«ELSEIF attr.columnName == 'kind'»INT NOT NULL COMMENT '«attr.EType.name»'«ELSEIF attr.isIRIReference»TEXT NOT NULL COMMENT '«attr.EClassType.abbreviatedTableName» («attr.EType.name»)'«ELSEIF attr.isLiteralFeature»TEXT COMMENT '(«attr.EType.name» value)',
		  `«attr.columnName»LiteralType` VARCHAR(30) COMMENT '(«attr.EType.name» kind)'«ELSEIF attr.isClassFeature && attr.lowerBound == 0»CHAR(36) NULL COMMENT '«attr.EClassType.abbreviatedTableName» («attr.EType.name»)'«ELSEIF attr.isClassFeature && attr.lowerBound > 0»CHAR(36) NOT NULL COMMENT '«attr.EClassType.abbreviatedTableName» («attr.EType.name»)'«ELSEIF attr.EType.name == "EBoolean" && attr.lowerBound > 0»BOOLEAN NOT NULL«ELSEIF attr.lowerBound > 0»TEXT NOT NULL COMMENT '«attr.EType.name»'«ELSE»TEXT COMMENT '«attr.EType.name»'«ENDIF»«ENDFOR»
		  
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter[isClassFeature && !isIRIReference && (eAbstract.contains(EClassType) || eConcrete.contains(EClassType))] SEPARATOR ",\n\n" AFTER ",\n"»CONSTRAINT `fk_«eClass.abbreviatedTableName»_«attr.columnName»`
		    FOREIGN KEY (`«attr.columnName»`)
		    REFERENCES `OML`.`«attr.EClassType.abbreviatedTableName»`(`uuid`)
		    ON DELETE CASCADE
		    ON UPDATE CASCADE«ENDFOR»
		  
		  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC)	
		)
		COMMENT = 'Concrete Information Table «eClass.tableVariableName.upperCaseInitialOrWord»';
		
		«ENDFOR»

		USE `OML`;
		DELIMITER $$
		
		«FOR eClass : eConcrete»«IF !eClass.ESuperClasses.empty»
		-- -----------------------------------------------------
		-- Concrete Information Table `OML`.`«eClass.abbreviatedTableName»` («eClass.tableVariableName.upperCaseInitialOrWord»)
		-- -----------------------------------------------------
		
		DELIMITER $$
		USE `OML`$$
		CREATE DEFINER = CURRENT_USER TRIGGER `OML`.`«eClass.abbreviatedTableName»_AFTER_INSERT` AFTER INSERT ON `«eClass.abbreviatedTableName»` FOR EACH ROW
		BEGIN
		«FOR eSup : eClass.EAllSuperTypes.sortBy[abbreviatedTableName]»
		-- «eSup.tableVariableName.upperCaseInitialOrWord»(x) if «eClass.tableVariableName.upperCaseInitialOrWord»(x)
		insert into `OML`.`«eSup.abbreviatedTableName»`(`uuid`) values(new.`uuid`);
		«ENDFOR»	
		END$$

		DELIMITER $$
		USE `OML`$$
		CREATE DEFINER = CURRENT_USER TRIGGER `OML`.`«eClass.abbreviatedTableName»_AFTER_DELETE` AFTER DELETE ON `«eClass.abbreviatedTableName»` FOR EACH ROW
		BEGIN
		«FOR eSup : eClass.EAllSuperTypes.sortBy[abbreviatedTableName]»
		-- «eSup.tableVariableName.upperCaseInitialOrWord»(x) if «eClass.tableVariableName.upperCaseInitialOrWord»(x)
		delete from `OML`.`«eSup.abbreviatedTableName»`;
		«ENDFOR»
		END$$
		
		«ENDIF»«ENDFOR»
		
		DELIMITER ;
		
		SET SQL_MODE=@OLD_SQL_MODE;
		SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
		SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
	'''
	}
	
	static val String SPACES = "                                       "
	
	static def String pad(EClass eClass) {
		val abbrev = eClass.abbreviatedTableName
		SPACES.substring(1, SPACES.length - abbrev.length)
	}
	static def String abbreviatedTableName(EClass eClass) {
		val n = eClass
		.tableVariableName.upperCaseInitialOrWord
		
		val abbrev = 
		n
		.replace("CrossReferencabilityKinds","CRTK")
		.replace("CrossReferencableKinds","CRBK")
		.replace("CrossReference","CRef")
		.replace("DataRelationship","DRel")
		.replace("DescriptionBox","DBox")
		.replace("DescriptionBoxes","DBox")
		.replace("ExtendsClosedWorldDefinitions","ExtCWDef")
		.replace("ExtrinsicIdentityKinds","EIdK")
		.replace("IntrinsicIdentityKinds","IIdK")
		.replace("IdentityKinds","Ik")
		.replace("LogicalElements","LogEs")
		.replace("ReifiedRelationship","RR")
		.replace("ScalarDataProperties","ScPs")
		.replace("ScalarDataProperty","ScP")
		.replace("SingletonInstances","SI")
		.replace("StructuredDataProperties","StPs")
		.replace("StructuredDataProperty","StP")
		.replace("TerminologyBoxes","TBox")
		.replace("TerminologyBox","TBox")
		.replace("UnreifiedRelationship","UR")
		
		
		.replace("Annotation","Annot")
		.replace("AspectKind", "Ak")
		.replace("Aspects", "As")
		.replace("Axioms","Ax")
		.replace("Assertions","Asts")
		.replace("Binary","Bin")
		.replace("Bundle","Bdl")
		.replace("ChainRules", "CR")
		.replace("Concept","C")
		.replace("Conceptual","C")
		.replace("ConceptTreeDisjunction","CTD")
		.replace("Context","Ctxt")
		.replace("Datatypes","Dt")
		.replace("DataRange","Dr")
		.replace("Directed","Dir")
		.replace("DisjointUnion","DsjU")
		.replace("Disjoint","Dsjt")
		.replace("Disjunctions","Dsju")
		.replace("Designation","Des")
		.replace("Entity","E")
		.replace("Entities","Es")
		.replace("Element","Elt")
		.replace("Existential","Ex")
		.replace("Forward","Fwd")
		.replace("Inverse","Inv")
		.replace("Instance","I")
		.replace("Literal","Lit")
		.replace("Logical","Log")
		.replace("Module","Mod")
		.replace("Predicates","P")
		.replace("Particular","Ptr")
		.replace("Properties","Props")
		.replace("Property","Prop")
		.replace("Refinement","Rfn")
		.replace("Relationship","Rel")
		.replace("Resource","Res")
		.replace("Restricted","Rest")
		.replace("Restriction","R")
		.replace("Reverse","Rev")
		.replace("Scalar","Sc")
		.replace("Segment","Seg")
		.replace("Singleton","S1")
		.replace("Source","Src")
		.replace("Specialization","Spe")
		.replace("Specific","Spe")
		.replace("Statements","St")
		.replace("Structure","St")
		.replace("Target","Tgt")
		.replace("Terminology","Tlgy")
		.replace("Tuples","Ts")
		.replace("Unary","Ury")
		.replace("Universal","Ux")
		.replace("Value","Val")
		
		if (n == abbrev && n.length > 8)
			throw new IllegalArgumentException('''No abbreviation defined for «n»''')
		abbrev
	}
	
}