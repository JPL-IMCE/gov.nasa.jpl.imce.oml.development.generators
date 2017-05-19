/**
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
package gov.nasa.jpl.imce.oml.generators;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import gov.nasa.jpl.imce.oml.generators.OMLUtilities;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

public class OMLLoadTest extends OMLUtilities {
  public static void main(final String[] args) {
    final OMLLoadTest o = new OMLLoadTest();
    o.test();
  }
  
  public void test() {
    final Function1<EClass, Boolean> _function = new Function1<EClass, Boolean>() {
      public Boolean apply(final EClass it) {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, "Extent"));
      }
    };
    final EClass extent = IterableExtensions.<EClass>findFirst(Iterables.<EClass>filter(this.c.getEClassifiers(), EClass.class), _function);
    final Function1<EClass, Boolean> _function_1 = new Function1<EClass, Boolean>() {
      public Boolean apply(final EClass it) {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, "Module"));
      }
    };
    final Function1<EOperation, Boolean> _function_2 = new Function1<EOperation, Boolean>() {
      public Boolean apply(final EOperation it) {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, "extent"));
      }
    };
    final EOperation module_extent = IterableExtensions.<EOperation>findFirst(IterableExtensions.<EClass>findFirst(Iterables.<EClass>filter(this.c.getEClassifiers(), EClass.class), _function_1).getEOperations(), _function_2);
    EClassifier _eType = module_extent.getEType();
    boolean _notEquals = (!Objects.equal(extent, _eType));
    if (_notEquals) {
      System.err.println("Module.extent() should be typed by Extent");
      EClassifier _eType_1 = module_extent.getEType();
      String _plus = ("Module.extent().EType =" + _eType_1);
      System.err.println(_plus);
      System.err.println(("TerminologyExtent =" + extent));
    } else {
      System.out.println("OK");
    }
  }
}
