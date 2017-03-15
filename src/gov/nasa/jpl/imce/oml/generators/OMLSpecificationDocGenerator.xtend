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

class OMLSpecificationDocGenerator extends OMLUtilities {

	static def main(String[] args) {
		if (1 != args.length) {
			System.err.println(
				"usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.doc project")
			System.exit(1)
		}

		new OMLSpecificationDocGenerator().generate(args.get(0))
	}

	def generate(String targetDir) {
		val targetPath = Paths.get(targetDir)
		targetPath.toFile.mkdirs

		val ePackages = #[c, t, g, b, d]
		val glossaryEntries = ePackages.map[EClassifiers].flatten.filter(EClass).filter[isGlossary].sortBy[name]

		val entriesByAbstraction = glossaryEntries.groupBy[isAbstract]
		val schemaEntries = entriesByAbstraction.get(false).filter[isSchema]
		val apiEntries = entriesByAbstraction.get(false).filter[isAPI && !isSchema]
		val ooEntries = entriesByAbstraction.get(false).filter[isOO]

		val buffer = new StringBuffer('''
				{% include "./external-links.md" %}
				# OML Glossary Summary
				
				The vocabulary of the Ontological Modeling Language, OML, consists of «glossaryEntries.size» definitions
				(«entriesByAbstraction.get(true).size» abstract and «entriesByAbstraction.get(false).size» concrete).
				This OML vocabulary is the basis of the Ontological Modeling Framework (OMF), which is 
				a collection of multiple technology-based Application Programming Interfaces (APIs) & libraries.
				
				- **EMF/CDO** OMF APIs and libraries based on the [Eclipse Modeling Framework] and [Connected Data Objects]
				
				  All «glossaryEntries.size» definitions induce corresponding EMF-based APIs and libraries.
				  For the «entriesByAbstraction.get(false).size» concrete definitions, the *EMF/CDO* APIs
				  include all the «schemaEntries.size» *Normalized* APIs, all the «apiEntries.size» *Functional* APIs,
				  and «ooEntries.size» definitions uniquely intended for *EMF/CDO*.
				  
				- **Normalized** OMF APIs and libraries based on polyglot functional programming in Java, JavaScript and Scala
				
				  A subset of «schemaEntries.size» definitions from the «entriesByAbstraction.get(false).size» concrete definitions
				  constitute the set of normalized relational database schema tables for the technology-agnostic OML tabular interchange representation.
				  These definitions generate language-friendly functional programming APIs for Java, JavaScript and Scala.
				  Note that Scala is the only language that can provide strong compile-time guarantees of the referential transparency of the OML functional APIs.
				  For Java and JavaScript, the OML functional APIs are intended to be referentially transparent; 
				  however, these languages do not provide any guarantees at compile time or runtime for preserving these properties.
				  
				- **Functional** OMF APIs and libraries in Scala for in-memory processing of OML tabular interchange representations
				
				  A subset of «apiEntries.size» definitions from the «entriesByAbstraction.get(false).size» concrete definitions
				  augment the normalized OMF APIs for the in-memory processing of OMF information
				  extracted from parsing the OML tabular interchange representation.
				
				''')
			
			
		val glossaryFile = new FileOutputStream(new File(targetPath.toAbsolutePath.toString + File::separator + "GLOSSARY.md"))
		
		try {
			
			generateGlossaryFile("Common", c.EClassifiers.filter(EClass).filter[isGlossary].sortBy[name], buffer)
			generateGlossaryFile("Terminologies", t.EClassifiers.filter(EClass).filter[isGlossary].sortBy[name], buffer)
			generateGlossaryFile("Graphs", g.EClassifiers.filter(EClass).filter[isGlossary].sortBy[name], buffer)
			generateGlossaryFile("Bundles", b.EClassifiers.filter(EClass).filter[isGlossary].sortBy[name], buffer)
			generateGlossaryFile("Descriptions", d.EClassifiers.filter(EClass).filter[isGlossary].sortBy[name], buffer)
			
			glossaryFile.write(buffer.toString.bytes)
			
		} finally {
			glossaryFile.close()
		}
	}

	def generateGlossaryFile(String group, List<EClass> entries, StringBuffer buffer) {
		val entriesByAbstraction = entries.groupBy[isAbstract]
		val abstractEntries = entriesByAbstraction.get(true)
		val concreteEntries = entriesByAbstraction.get(false)
		val schemaEntries = concreteEntries.filter[isSchema]
		val apiEntries = concreteEntries.filter[isAPI && !isSchema]
		val ooEntries = concreteEntries.filter[isOO]
		
		buffer.append("\n"+'''# OML «group» Glossary{#oml-«group.toLowerCase»-glossary}''')
			
		if (null !== abstractEntries && !abstractEntries.empty) {
			buffer.append("\n"+'''* OML «group» Glossary of «abstractEntries.size» Abstract Definitions{#oml-«group.toLowerCase»-abstract-glossary}''')
			abstractEntries.forEach [ eClass | generateClassGlossaryContents(buffer, eClass) ]
		}
		
		buffer.append("\n"+'''* OML «group» Glossary of «concreteEntries.size» Concrete Definitions{#oml-«group.toLowerCase»-concrete-glossary}''' +"\n")
		
		if (null !== schemaEntries && !schemaEntries.empty) {
			buffer.append("\n"+'''* OML «group» Glossary of «schemaEntries.size» Schema Concrete Definitions{#oml-«group.toLowerCase»-schema-concrete-glossary}''' +"\n")
			schemaEntries.sortWith(new OMLTableCompare()).forEach [ eClass | generateClassGlossaryContents(buffer, eClass) ]
		}
		
		if (null !== apiEntries && !apiEntries.empty) {
			buffer.append("\n"+'''* OML «group» Glossary of «apiEntries.size» Functional API Concrete Definitions{#oml-«group.toLowerCase»-functional-concrete-glossary}''' +"\n")
			apiEntries.forEach [ eClass | generateClassGlossaryContents(buffer, eClass) ]
		}
		
		if (null !== ooEntries && !ooEntries.empty) {
 			buffer.append("\n"+'''* OML «group» Glossary of «ooEntries.size» EMF/CDO API Concrete Definitions{#oml-«group.toLowerCase»-emf-cdo-concrete-glossary}''' +"\n")
			ooEntries.forEach [ eClass | generateClassGlossaryContents(buffer, eClass)] 
		}
			
	}

	def StringBuffer generateClassGlossaryContents(StringBuffer buffer, EClass eClass) {

		buffer.append('''
			
			## OML «eClass.name»
			
			«eClass.markDown»
			
		''')

		var prefix = "{APIs: "
		if (eClass.isSchema) {
			buffer.append(prefix + "**Normalized**");
			prefix = ", "
		}
		if (eClass.isAPI) {
			buffer.append(prefix + "**Functional**");
			prefix = ", "
		}
		if (eClass.isOO) {
			buffer.append(prefix + "**EMF/CDO**");
			prefix = ", "
		}
		buffer.append("}\n")

		val gprefix = if(eClass.isAbstract) "Abstract" else "Concrete"
		val general = eClass.ESuperClasses
		val sprefix = if(general.empty) gprefix else "and"
		val specific = eClass.ESpecificClasses

		if (!general.empty) {
			//val gbefore = '''«gprefix» definition with «general.size» «pluralizeIfMany("generalization", general.size)»:'''+"\n"
			val gbefore = gprefix+" definition with "+general.size+" "+pluralizeIfMany("generalization", general.size)+":\n"
			//buffer.append("\n" +'''«FOR g : general BEFORE gbefore SEPARATOR "\n" AFTER "\n"» - OML «g.name»«ENDFOR»''')
			buffer.append("\n" +'''«FOR g : general BEFORE gbefore SEPARATOR "\n" AFTER "\n"» - OML «g.name»«ENDFOR»''')
		}
		
		if (eClass.isAbstract) {
			//val sbefore = '''«sprefix» with «specific.size» «pluralizeIfMany("specialization", specific.size)»:'''+"\n"
			val sbefore = '''«sprefix» with «specific.size» '''+pluralizeIfMany("specialization", specific.size)+":\n"
			buffer.append(
				"\n" +
					'''«FOR s : specific BEFORE sbefore SEPARATOR "\n" AFTER "\n"» - OML «s.name»«ENDFOR»''')
		} else if (eClass.isSchema)
			buffer.append(
			"\nNormalized Relational Schema Table:\n"+'''
			«FOR attr : eClass.functionalAPIOrOrderingKeyAttributes»
				- «attr.columnName»: «attr.schemaColumnTypeDescription»
			«ENDFOR»	
			'''
			)
		
		buffer
	}

}
