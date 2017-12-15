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
		val eClasses = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isFunctionalAPI && !isInterface && !isValueTable].sortWith(new OMLTableCompare())
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
		
		«FOR eClass : eClasses»
		-- -----------------------------------------------------
		-- Table `OML`.`«eClass.tableVariableName.upperCaseInitialOrWord»`
		-- -----------------------------------------------------
		CREATE TABLE IF NOT EXISTS `OML`.`«eClass.tableVariableName.upperCaseInitialOrWord»` (
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes SEPARATOR ",\n" AFTER ",\n"»
		  `«attr.columnName»`  «IF attr.isUUID»BINARY(16) NOT NULL PRIMARY KEY
		  «ELSEIF attr.isIRIReference»VARCHAR(256) NOT NULL
		  «ELSEIF attr.isLiteralFeature»-- TODO: LiteralFeature
		  TEXT, `LiteralType` VARCHAR(20) NOT NULL,
		  «ELSEIF attr.isClassFeature && attr.lowerBound == 0»BINARY(16) NULL
		  «ELSEIF attr.isClassFeature && attr.lowerBound > 0»BINARY(16) NOT NULL
		  «ELSE»TEXT«ENDIF»«ENDFOR»
		  «FOR attr : eClass.schemaAPIOrOrderingKeyAttributes.filter[isClassFeature] SEPARATOR ",\n\n" AFTER ",\n"»CONSTRAINT `fk_«eClass.tableVariableName.upperCaseInitialOrWord»_«attr.columnName»_«attr.EClassType.tableVariableName.upperCaseInitialOrWord»`
		    FOREIGN KEY (`«attr.columnName»`)
		    REFERENCES `OML`.`«attr.EClassType.tableVariableName.upperCaseInitialOrWord»`(`uuid`)
		    ON DELETE CASCADE
		    ON UPDATE CASCADE«ENDFOR»
		  
		  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC)	
		)
		  
		«ENDFOR»
		  		  

	'''
	}
	
}