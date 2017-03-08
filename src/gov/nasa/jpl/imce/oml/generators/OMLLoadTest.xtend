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

import org.eclipse.emf.ecore.EClass

class OMLLoadTest extends OMLUtilities {
	
	static def main(String[] args) {
		val o = new OMLLoadTest()
		o.test()
	}
	
	def test() {
		val extent =
		c
		.EClassifiers
		.filter(EClass)
		.findFirst[name == 'Extent']
		
      	val module_extent = 
      	c
      	.EClassifiers
      	.filter(EClass)
      	.findFirst[name == 'Module']
      	.EOperations
      	.findFirst[name == 'extent']
      	
      	if (extent != module_extent.EType) {
      		System.err.println("Module.extent() should be typed by Extent")
      		System.err.println("Module.extent().EType ="+module_extent.EType)
      		System.err.println("TerminologyExtent ="+extent)
      	} else { 		
     		System.out.println("OK")
      	}
      	
	}
	
}