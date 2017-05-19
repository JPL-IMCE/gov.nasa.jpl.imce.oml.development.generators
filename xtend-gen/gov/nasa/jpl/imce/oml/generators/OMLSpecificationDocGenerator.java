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

import com.google.common.collect.Iterables;
import gov.nasa.jpl.imce.oml.generators.OMLUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

public class OMLSpecificationDocGenerator extends OMLUtilities {
  public static void main(final String[] args) {
    int _length = args.length;
    boolean _notEquals = (1 != _length);
    if (_notEquals) {
      System.err.println(
        "usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.doc project");
      System.exit(1);
    }
    new OMLSpecificationDocGenerator().generate(args[0]);
  }
  
  public void generate(final String targetDir) {
    try {
      final Path targetPath = Paths.get(targetDir);
      targetPath.toFile().mkdirs();
      final List<EPackage> ePackages = Collections.<EPackage>unmodifiableList(CollectionLiterals.<EPackage>newArrayList(this.c, this.t, this.g, this.b, this.d));
      final Function1<EPackage, EList<EClassifier>> _function = new Function1<EPackage, EList<EClassifier>>() {
        public EList<EClassifier> apply(final EPackage it) {
          return it.getEClassifiers();
        }
      };
      final Function1<EClass, Boolean> _function_1 = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return OMLUtilities.isGlossary(it);
        }
      };
      final Function1<EClass, String> _function_2 = new Function1<EClass, String>() {
        public String apply(final EClass it) {
          return it.getName();
        }
      };
      final List<EClass> glossaryEntries = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1), _function_2);
      final Function1<EClass, Boolean> _function_3 = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return Boolean.valueOf(it.isAbstract());
        }
      };
      final Map<Boolean, List<EClass>> entriesByAbstraction = IterableExtensions.<Boolean, EClass>groupBy(glossaryEntries, _function_3);
      final Function1<EClass, Boolean> _function_4 = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return OMLUtilities.isSchema(it);
        }
      };
      final Iterable<EClass> schemaEntries = IterableExtensions.<EClass>filter(entriesByAbstraction.get(Boolean.valueOf(false)), _function_4);
      final Function1<EClass, Boolean> _function_5 = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return Boolean.valueOf(((OMLUtilities.isAPI(it)).booleanValue() && (!(OMLUtilities.isSchema(it)).booleanValue())));
        }
      };
      final Iterable<EClass> apiEntries = IterableExtensions.<EClass>filter(entriesByAbstraction.get(Boolean.valueOf(false)), _function_5);
      final Function1<EClass, Boolean> _function_6 = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return OMLUtilities.isOO(it);
        }
      };
      final Iterable<EClass> ooEntries = IterableExtensions.<EClass>filter(entriesByAbstraction.get(Boolean.valueOf(false)), _function_6);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("{% include \"./external-links.md\" %}");
      _builder.newLine();
      _builder.append("# OML Glossary Summary");
      _builder.newLine();
      _builder.newLine();
      _builder.append("The vocabulary of the Ontological Modeling Language, OML, consists of ");
      int _size = glossaryEntries.size();
      _builder.append(_size);
      _builder.append(" definitions");
      _builder.newLineIfNotEmpty();
      _builder.append("(");
      int _size_1 = entriesByAbstraction.get(Boolean.valueOf(true)).size();
      _builder.append(_size_1);
      _builder.append(" abstract and ");
      int _size_2 = entriesByAbstraction.get(Boolean.valueOf(false)).size();
      _builder.append(_size_2);
      _builder.append(" concrete).");
      _builder.newLineIfNotEmpty();
      _builder.append("This OML vocabulary is the basis of the Ontological Modeling Framework (OMF), which is ");
      _builder.newLine();
      _builder.append("a collection of multiple technology-based Application Programming Interfaces (APIs) & libraries.");
      _builder.newLine();
      _builder.newLine();
      _builder.append("- **EMF/CDO** OMF APIs and libraries based on the [Eclipse Modeling Framework] and [Connected Data Objects]");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("All ");
      int _size_3 = glossaryEntries.size();
      _builder.append(_size_3, "  ");
      _builder.append(" definitions induce corresponding EMF-based APIs and libraries.");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("For the ");
      int _size_4 = entriesByAbstraction.get(Boolean.valueOf(false)).size();
      _builder.append(_size_4, "  ");
      _builder.append(" concrete definitions, the *EMF/CDO* APIs");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("include all the ");
      int _size_5 = IterableExtensions.size(schemaEntries);
      _builder.append(_size_5, "  ");
      _builder.append(" *Normalized* APIs, all the ");
      int _size_6 = IterableExtensions.size(apiEntries);
      _builder.append(_size_6, "  ");
      _builder.append(" *Functional* APIs,");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("and ");
      int _size_7 = IterableExtensions.size(ooEntries);
      _builder.append(_size_7, "  ");
      _builder.append(" definitions uniquely intended for *EMF/CDO*.");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("- **Normalized** OMF APIs and libraries based on polyglot functional programming in Java, JavaScript and Scala");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("A subset of ");
      int _size_8 = IterableExtensions.size(schemaEntries);
      _builder.append(_size_8, "  ");
      _builder.append(" definitions from the ");
      int _size_9 = entriesByAbstraction.get(Boolean.valueOf(false)).size();
      _builder.append(_size_9, "  ");
      _builder.append(" concrete definitions");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("constitute the set of normalized relational database schema tables for the technology-agnostic OML tabular interchange representation.");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("These definitions generate language-friendly functional programming APIs for Java, JavaScript and Scala.");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("Note that Scala is the only language that can provide strong compile-time guarantees of the referential transparency of the OML functional APIs.");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("For Java and JavaScript, the OML functional APIs are intended to be referentially transparent; ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("however, these languages do not provide any guarantees at compile time or runtime for preserving these properties.");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("- **Functional** OMF APIs and libraries in Scala for in-memory processing of OML tabular interchange representations");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("A subset of ");
      int _size_10 = IterableExtensions.size(apiEntries);
      _builder.append(_size_10, "  ");
      _builder.append(" definitions from the ");
      int _size_11 = entriesByAbstraction.get(Boolean.valueOf(false)).size();
      _builder.append(_size_11, "  ");
      _builder.append(" concrete definitions");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("augment the normalized OMF APIs for the in-memory processing of OMF information");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("extracted from parsing the OML tabular interchange representation.");
      _builder.newLine();
      _builder.newLine();
      final StringBuffer buffer = new StringBuffer(_builder);
      String _string = targetPath.toAbsolutePath().toString();
      String _plus = (_string + File.separator);
      String _plus_1 = (_plus + "GLOSSARY.md");
      File _file = new File(_plus_1);
      final FileOutputStream glossaryFile = new FileOutputStream(_file);
      try {
        final Function1<EClass, Boolean> _function_7 = new Function1<EClass, Boolean>() {
          public Boolean apply(final EClass it) {
            return OMLUtilities.isGlossary(it);
          }
        };
        final Function1<EClass, String> _function_8 = new Function1<EClass, String>() {
          public String apply(final EClass it) {
            return it.getName();
          }
        };
        this.generateGlossaryFile("1", "Common", IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(this.c.getEClassifiers(), EClass.class), _function_7), _function_8), buffer);
        final Function1<EClass, Boolean> _function_9 = new Function1<EClass, Boolean>() {
          public Boolean apply(final EClass it) {
            return OMLUtilities.isGlossary(it);
          }
        };
        final Function1<EClass, String> _function_10 = new Function1<EClass, String>() {
          public String apply(final EClass it) {
            return it.getName();
          }
        };
        this.generateGlossaryFile("2", "Terminologies", IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(this.t.getEClassifiers(), EClass.class), _function_9), _function_10), buffer);
        final Function1<EClass, Boolean> _function_11 = new Function1<EClass, Boolean>() {
          public Boolean apply(final EClass it) {
            return OMLUtilities.isGlossary(it);
          }
        };
        final Function1<EClass, String> _function_12 = new Function1<EClass, String>() {
          public String apply(final EClass it) {
            return it.getName();
          }
        };
        this.generateGlossaryFile("3", "Graphs", IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(this.g.getEClassifiers(), EClass.class), _function_11), _function_12), buffer);
        final Function1<EClass, Boolean> _function_13 = new Function1<EClass, Boolean>() {
          public Boolean apply(final EClass it) {
            return OMLUtilities.isGlossary(it);
          }
        };
        final Function1<EClass, String> _function_14 = new Function1<EClass, String>() {
          public String apply(final EClass it) {
            return it.getName();
          }
        };
        this.generateGlossaryFile("4", "Bundles", IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(this.b.getEClassifiers(), EClass.class), _function_13), _function_14), buffer);
        final Function1<EClass, Boolean> _function_15 = new Function1<EClass, Boolean>() {
          public Boolean apply(final EClass it) {
            return OMLUtilities.isGlossary(it);
          }
        };
        final Function1<EClass, String> _function_16 = new Function1<EClass, String>() {
          public String apply(final EClass it) {
            return it.getName();
          }
        };
        this.generateGlossaryFile("5", "Descriptions", IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(this.d.getEClassifiers(), EClass.class), _function_15), _function_16), buffer);
        glossaryFile.write(buffer.toString().getBytes());
      } finally {
        glossaryFile.close();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public int generateGlossaryFile(final String n, final String group, final List<EClass> entries, final StringBuffer buffer) {
    int _xblockexpression = (int) 0;
    {
      final Function1<EClass, Boolean> _function = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return Boolean.valueOf(it.isAbstract());
        }
      };
      final Map<Boolean, List<EClass>> entriesByAbstraction = IterableExtensions.<Boolean, EClass>groupBy(entries, _function);
      final List<EClass> abstractEntries = entriesByAbstraction.get(Boolean.valueOf(true));
      final List<EClass> concreteEntries = entriesByAbstraction.get(Boolean.valueOf(false));
      final Function1<EClass, Boolean> _function_1 = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return OMLUtilities.isSchema(it);
        }
      };
      final Iterable<EClass> schemaEntries = IterableExtensions.<EClass>filter(concreteEntries, _function_1);
      final Function1<EClass, Boolean> _function_2 = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return Boolean.valueOf(((OMLUtilities.isAPI(it)).booleanValue() && (!(OMLUtilities.isSchema(it)).booleanValue())));
        }
      };
      final Iterable<EClass> apiEntries = IterableExtensions.<EClass>filter(concreteEntries, _function_2);
      final Function1<EClass, Boolean> _function_3 = new Function1<EClass, Boolean>() {
        public Boolean apply(final EClass it) {
          return OMLUtilities.isOO(it);
        }
      };
      final Iterable<EClass> ooEntries = IterableExtensions.<EClass>filter(concreteEntries, _function_3);
      int counter = 1;
      int subcounter = 1;
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("# ");
      _builder.append(n);
      _builder.append(" OML ");
      _builder.append(group);
      _builder.append(" Glossary {#oml-");
      String _lowerCase = group.toLowerCase();
      _builder.append(_lowerCase);
      _builder.append("-glossary}");
      String _plus = ("\n" + _builder);
      buffer.append(_plus);
      if (((null != abstractEntries) && (!abstractEntries.isEmpty()))) {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("# ");
        _builder_1.append(n);
        _builder_1.append(".");
        _builder_1.append(counter);
        _builder_1.append(" OML ");
        _builder_1.append(group);
        _builder_1.append(" Glossary of ");
        int _size = abstractEntries.size();
        _builder_1.append(_size);
        _builder_1.append(" Abstract Definitions {#oml-");
        String _lowerCase_1 = group.toLowerCase();
        _builder_1.append(_lowerCase_1);
        _builder_1.append("-abstract-glossary}");
        String _plus_1 = ("\n" + _builder_1);
        buffer.append(_plus_1);
        final Consumer<EClass> _function_4 = new Consumer<EClass>() {
          public void accept(final EClass eClass) {
            OMLSpecificationDocGenerator.this.generateClassGlossaryContents(buffer, eClass);
          }
        };
        abstractEntries.forEach(_function_4);
        int _counter = counter;
        counter = (_counter + 1);
      }
      StringConcatenation _builder_2 = new StringConcatenation();
      _builder_2.append("# ");
      _builder_2.append(n);
      _builder_2.append(".");
      _builder_2.append(counter);
      _builder_2.append(" OML ");
      _builder_2.append(group);
      _builder_2.append(" Glossary of ");
      int _size_1 = concreteEntries.size();
      _builder_2.append(_size_1);
      _builder_2.append(" Concrete Definitions {#oml-");
      String _lowerCase_2 = group.toLowerCase();
      _builder_2.append(_lowerCase_2);
      _builder_2.append("-concrete-glossary}");
      String _plus_2 = ("\n" + _builder_2);
      String _plus_3 = (_plus_2 + "\n");
      buffer.append(_plus_3);
      if (((null != schemaEntries) && (!IterableExtensions.isEmpty(schemaEntries)))) {
        StringConcatenation _builder_3 = new StringConcatenation();
        _builder_3.append("# ");
        _builder_3.append(n);
        _builder_3.append(".");
        _builder_3.append(counter);
        _builder_3.append(".");
        _builder_3.append(subcounter);
        _builder_3.append(" OML ");
        _builder_3.append(group);
        _builder_3.append(" Glossary of ");
        int _size_2 = IterableExtensions.size(schemaEntries);
        _builder_3.append(_size_2);
        _builder_3.append(" Schema Concrete Definitions {#oml-");
        String _lowerCase_3 = group.toLowerCase();
        _builder_3.append(_lowerCase_3);
        _builder_3.append("-schema-concrete-glossary}");
        String _plus_4 = ("\n" + _builder_3);
        String _plus_5 = (_plus_4 + "\n");
        buffer.append(_plus_5);
        OMLUtilities.OMLTableCompare _oMLTableCompare = new OMLUtilities.OMLTableCompare();
        final Consumer<EClass> _function_5 = new Consumer<EClass>() {
          public void accept(final EClass eClass) {
            OMLSpecificationDocGenerator.this.generateClassGlossaryContents(buffer, eClass);
          }
        };
        IterableExtensions.<EClass>sortWith(schemaEntries, _oMLTableCompare).forEach(_function_5);
        int _subcounter = subcounter;
        subcounter = (_subcounter + 1);
      }
      if (((null != apiEntries) && (!IterableExtensions.isEmpty(apiEntries)))) {
        StringConcatenation _builder_4 = new StringConcatenation();
        _builder_4.append("# ");
        _builder_4.append(n);
        _builder_4.append(".");
        _builder_4.append(counter);
        _builder_4.append(".");
        _builder_4.append(subcounter);
        _builder_4.append(" OML ");
        _builder_4.append(group);
        _builder_4.append(" Glossary of ");
        int _size_3 = IterableExtensions.size(apiEntries);
        _builder_4.append(_size_3);
        _builder_4.append(" Functional API Concrete Definitions {#oml-");
        String _lowerCase_4 = group.toLowerCase();
        _builder_4.append(_lowerCase_4);
        _builder_4.append("-functional-concrete-glossary}");
        String _plus_6 = ("\n" + _builder_4);
        String _plus_7 = (_plus_6 + "\n");
        buffer.append(_plus_7);
        final Consumer<EClass> _function_6 = new Consumer<EClass>() {
          public void accept(final EClass eClass) {
            OMLSpecificationDocGenerator.this.generateClassGlossaryContents(buffer, eClass);
          }
        };
        apiEntries.forEach(_function_6);
        int _subcounter_1 = subcounter;
        subcounter = (_subcounter_1 + 1);
      }
      int _xifexpression = (int) 0;
      if (((null != ooEntries) && (!IterableExtensions.isEmpty(ooEntries)))) {
        int _xblockexpression_1 = (int) 0;
        {
          StringConcatenation _builder_5 = new StringConcatenation();
          _builder_5.append("# ");
          _builder_5.append(n);
          _builder_5.append(".");
          _builder_5.append(counter);
          _builder_5.append(".");
          _builder_5.append(subcounter);
          _builder_5.append(" OML ");
          _builder_5.append(group);
          _builder_5.append(" Glossary of ");
          int _size_4 = IterableExtensions.size(ooEntries);
          _builder_5.append(_size_4);
          _builder_5.append(" EMF/CDO API Concrete Definitions {#oml-");
          String _lowerCase_5 = group.toLowerCase();
          _builder_5.append(_lowerCase_5);
          _builder_5.append("-emf-cdo-concrete-glossary}");
          String _plus_8 = ("\n" + _builder_5);
          String _plus_9 = (_plus_8 + "\n");
          buffer.append(_plus_9);
          final Consumer<EClass> _function_7 = new Consumer<EClass>() {
            public void accept(final EClass eClass) {
              OMLSpecificationDocGenerator.this.generateClassGlossaryContents(buffer, eClass);
            }
          };
          ooEntries.forEach(_function_7);
          int _subcounter_2 = subcounter;
          _xblockexpression_1 = subcounter = (_subcounter_2 + 1);
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public StringBuffer generateClassGlossaryContents(final StringBuffer buffer, final EClass eClass) {
    StringBuffer _xblockexpression = null;
    {
      StringConcatenation _builder = new StringConcatenation();
      _builder.newLine();
      _builder.append("## OML ");
      String _name = eClass.getName();
      _builder.append(_name);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      String _markDown = OMLUtilities.markDown(eClass);
      _builder.append(_markDown);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      buffer.append(_builder);
      String prefix = "{APIs: ";
      Boolean _isSchema = OMLUtilities.isSchema(eClass);
      if ((_isSchema).booleanValue()) {
        buffer.append((prefix + "**Normalized**"));
        prefix = ", ";
      }
      Boolean _isAPI = OMLUtilities.isAPI(eClass);
      if ((_isAPI).booleanValue()) {
        buffer.append((prefix + "**Functional**"));
        prefix = ", ";
      }
      Boolean _isOO = OMLUtilities.isOO(eClass);
      if ((_isOO).booleanValue()) {
        buffer.append((prefix + "**EMF/CDO**"));
        prefix = ", ";
      }
      buffer.append("}\n");
      String _xifexpression = null;
      boolean _isAbstract = eClass.isAbstract();
      if (_isAbstract) {
        _xifexpression = "Abstract";
      } else {
        _xifexpression = "Concrete";
      }
      final String gprefix = _xifexpression;
      final Iterable<EClass> general = OMLUtilities.ESuperClasses(eClass);
      String _xifexpression_1 = null;
      boolean _isEmpty = IterableExtensions.isEmpty(general);
      if (_isEmpty) {
        _xifexpression_1 = gprefix;
      } else {
        _xifexpression_1 = "and";
      }
      final String sprefix = _xifexpression_1;
      final Iterable<EClass> specific = OMLUtilities.ESpecificClasses(eClass);
      boolean _isEmpty_1 = IterableExtensions.isEmpty(general);
      boolean _not = (!_isEmpty_1);
      if (_not) {
        int _size = IterableExtensions.size(general);
        String _plus = ((gprefix + " definition with ") + Integer.valueOf(_size));
        String _plus_1 = (_plus + " ");
        String _pluralizeIfMany = OMLUtilities.pluralizeIfMany("generalization", IterableExtensions.size(general));
        String _plus_2 = (_plus_1 + _pluralizeIfMany);
        final String gbefore = (_plus_2 + ":\n");
        StringConcatenation _builder_1 = new StringConcatenation();
        {
          boolean _hasElements = false;
          for(final EClass g : general) {
            if (!_hasElements) {
              _hasElements = true;
              _builder_1.append(gbefore);
            } else {
              _builder_1.appendImmediate("\n", "");
            }
            _builder_1.append(" - OML ");
            String _name_1 = g.getName();
            _builder_1.append(_name_1);
          }
          if (_hasElements) {
            _builder_1.append("\n");
          }
        }
        String _plus_3 = ("\n" + _builder_1);
        buffer.append(_plus_3);
      }
      boolean _isAbstract_1 = eClass.isAbstract();
      if (_isAbstract_1) {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append(sprefix);
        _builder_2.append(" with ");
        int _size_1 = IterableExtensions.size(specific);
        _builder_2.append(_size_1);
        _builder_2.append(" ");
        String _pluralizeIfMany_1 = OMLUtilities.pluralizeIfMany("specialization", IterableExtensions.size(specific));
        String _plus_4 = (_builder_2.toString() + _pluralizeIfMany_1);
        final String sbefore = (_plus_4 + ":\n");
        StringConcatenation _builder_3 = new StringConcatenation();
        {
          boolean _hasElements_1 = false;
          for(final EClass s : specific) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
              _builder_3.append(sbefore);
            } else {
              _builder_3.appendImmediate("\n", "");
            }
            _builder_3.append(" - OML ");
            String _name_2 = s.getName();
            _builder_3.append(_name_2);
          }
          if (_hasElements_1) {
            _builder_3.append("\n");
          }
        }
        String _plus_5 = ("\n" + _builder_3);
        buffer.append(_plus_5);
      } else {
        Boolean _isSchema_1 = OMLUtilities.isSchema(eClass);
        if ((_isSchema_1).booleanValue()) {
          StringConcatenation _builder_4 = new StringConcatenation();
          {
            Iterable<ETypedElement> _functionalAPIOrOrderingKeyAttributes = OMLUtilities.functionalAPIOrOrderingKeyAttributes(eClass);
            for(final ETypedElement attr : _functionalAPIOrOrderingKeyAttributes) {
              _builder_4.append("- ");
              String _columnName = OMLUtilities.columnName(attr);
              _builder_4.append(_columnName);
              _builder_4.append(": ");
              String _schemaColumnTypeDescription = OMLUtilities.schemaColumnTypeDescription(attr);
              _builder_4.append(_schemaColumnTypeDescription);
              _builder_4.newLineIfNotEmpty();
            }
          }
          String _plus_6 = ("\nNormalized Relational Schema Table:\n" + _builder_4);
          buffer.append(_plus_6);
        }
      }
      _xblockexpression = buffer;
    }
    return _xblockexpression;
  }
}
