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
package gov.nasa.jpl.imce.oml.development.generators;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import gov.nasa.jpl.imce.oml.development.generators.OMLUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class OMLSpecificationResolverLibraryGenerator extends OMLUtilities {
  public static void main(final String[] args) {
    int _length = args.length;
    boolean _notEquals = (1 != _length);
    if (_notEquals) {
      System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.resolver project");
      System.exit(1);
    }
    final OMLSpecificationResolverLibraryGenerator gen = new OMLSpecificationResolverLibraryGenerator();
    final String dir = args[0];
    boolean ok = false;
    try {
      gen.generate(dir);
      ok = true;
    } catch (final Throwable _t) {
      if (_t instanceof Throwable) {
        final Throwable t = (Throwable)_t;
        System.err.println(t.getMessage());
        t.printStackTrace(System.err);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    } finally {
      if (ok) {
        System.out.println("Done");
      } else {
        System.err.println("Abnormal exit!");
      }
    }
  }
  
  public void generate(final String targetDir) {
    final Path bundlePath = Paths.get(targetDir);
    final String targetFolder = "src/main/scala/gov/nasa/jpl/imce/oml/resolver/impl";
    final Path targetPath = bundlePath.resolve(targetFolder);
    targetPath.toFile().mkdirs();
    this.generate(Collections.<EPackage>unmodifiableList(CollectionLiterals.<EPackage>newArrayList(this.c, this.t, this.g, this.b, this.d)), targetPath.toAbsolutePath().toString());
  }
  
  public void generate(final List<EPackage> ePackages, final String targetFolder) {
    try {
      File _file = new File(((targetFolder + File.separator) + "OMLResolvedFactoryImpl.scala"));
      final FileOutputStream factoryFile = new FileOutputStream(_file);
      try {
        factoryFile.write(this.generateFactoryFile(ePackages, "gov.nasa.jpl.imce.oml.resolver.impl").getBytes());
        final Function1<EPackage, Iterable<EClass>> _function = (EPackage it) -> {
          return OMLUtilities.FunctionalAPIClasses(it);
        };
        final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
          Boolean _isExtentContainer = OMLUtilities.isExtentContainer(it);
          return Boolean.valueOf((!(_isExtentContainer).booleanValue()));
        };
        Iterable<EClass> _filter = IterableExtensions.<EClass>filter(Iterables.<EClass>concat(ListExtensions.<EPackage, Iterable<EClass>>map(ePackages, _function)), _function_1);
        for (final EClass eClass : _filter) {
          {
            String _name = eClass.getName();
            String _plus = ((targetFolder + File.separator) + _name);
            String _plus_1 = (_plus + ".scala");
            File _file_1 = new File(_plus_1);
            final FileOutputStream classFile = new FileOutputStream(_file_1);
            try {
              classFile.write(this.generateClassFile(eClass).getBytes());
            } finally {
              classFile.close();
            }
          }
        }
      } finally {
        factoryFile.close();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public String generateFactoryFile(final List<EPackage> ePackages, final String packageQName) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.append("package ");
    _builder.append(packageQName);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml._");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import scala.Predef.ArrowAssoc");
    _builder.newLine();
    _builder.newLine();
    _builder.append("case class OMLResolvedFactoryImpl");
    _builder.newLine();
    _builder.append("( override val oug: uuid.OMLUUIDGenerator ) ");
    _builder.newLine();
    _builder.append("extends resolver.api.OMLResolvedFactory {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t ");
    _builder.append("override def createExtent");
    _builder.newLine();
    _builder.append("\t ");
    _builder.append(": resolver.api.Extent ");
    _builder.newLine();
    _builder.append("\t ");
    _builder.append("= resolver.api.Extent()");
    _builder.newLine();
    _builder.append("\t ");
    _builder.newLine();
    {
      final Function1<EPackage, Iterable<EClass>> _function = (EPackage it) -> {
        return OMLUtilities.FunctionalAPIClasses(it);
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return Boolean.valueOf(((!it.isAbstract()) && (!(OMLUtilities.isExtentContainer(it)).booleanValue())));
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>concat(ListExtensions.<EPackage, Iterable<EClass>>map(ePackages, _function)), _function_1), _function_2);
      for(final EClass eClass : _sortBy) {
        _builder.append("\t ");
        _builder.append("// ");
        String _name = eClass.getName();
        _builder.append(_name, "\t ");
        _builder.newLineIfNotEmpty();
        _builder.append("\t ");
        String _factoryMethod = this.factoryMethod(eClass);
        _builder.append(_factoryMethod, "\t ");
        _builder.newLineIfNotEmpty();
        _builder.append("\t ");
        _builder.append("\t\t  ");
        _builder.newLine();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String factoryMethod(final EClass eClass) {
    String _xblockexpression = null;
    {
      final EStructuralFeature uuid = OMLUtilities.lookupUUIDFeature(eClass);
      String _xifexpression = null;
      if ((null == uuid)) {
        _xifexpression = this.factoryMethodWithoutUUID(eClass);
      } else {
        String _xblockexpression_1 = null;
        {
          final EStructuralFeature uuidNS = OMLUtilities.lookupUUIDNamespaceFeature(eClass);
          final Iterable<EStructuralFeature> uuidFactors = OMLUtilities.lookupUUIDNamespaceFactors(eClass);
          final EOperation uuidOp = OMLUtilities.lookupUUIDOperation(eClass);
          String _xifexpression_1 = null;
          if ((null != uuidOp)) {
            _xifexpression_1 = OMLUtilities.scalaAnnotation(uuidOp);
          } else {
            _xifexpression_1 = null;
          }
          final String uuidScala = _xifexpression_1;
          String _xifexpression_2 = null;
          Boolean _isUUIDDerived = OMLUtilities.isUUIDDerived(eClass);
          if ((_isUUIDDerived).booleanValue()) {
            _xifexpression_2 = this.factoryMethodWithDerivedUUID(eClass);
          } else {
            String _xifexpression_3 = null;
            if (((null != uuidNS) || (!IterableExtensions.isEmpty(uuidFactors)))) {
              _xifexpression_3 = this.factoryMethodWithUUIDGenerator(eClass, uuidNS, uuidFactors);
            } else {
              String _xifexpression_4 = null;
              if ((null != uuidScala)) {
                _xifexpression_4 = this.factoryMethodWithUUIDGenerator(eClass, uuidNS, uuidScala);
              } else {
                _xifexpression_4 = this.factoryMethodWithImplicitlyDerivedUUID(eClass);
              }
              _xifexpression_3 = _xifexpression_4;
            }
            _xifexpression_2 = _xifexpression_3;
          }
          _xblockexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithoutUUID(final EClass eClass) {
    String _xblockexpression = null;
    {
      final Function1<EReference, Boolean> _function = (EReference it) -> {
        return Boolean.valueOf(it.isContainer());
      };
      final EReference container = IterableExtensions.<EReference>findFirst(Iterables.<EReference>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), EReference.class), _function);
      EReference _eOpposite = null;
      if (container!=null) {
        _eOpposite=container.getEOpposite();
      }
      final EReference contained = _eOpposite;
      final String newVal = StringExtensions.toFirstLower(eClass.getName());
      String _xifexpression = null;
      if ((null == container)) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("override def create");
        String _name = eClass.getName();
        _builder.append(_name);
        _builder.newLineIfNotEmpty();
        {
          Iterable<EStructuralFeature> _sortedAttributeFactorySignature = OMLUtilities.getSortedAttributeFactorySignature(eClass);
          boolean _hasElements = false;
          for(final EStructuralFeature attr : _sortedAttributeFactorySignature) {
            if (!_hasElements) {
              _hasElements = true;
              String _xifexpression_1 = null;
              Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
              if ((_isExtentContainer).booleanValue()) {
                _xifexpression_1 = "( ";
              } else {
                _xifexpression_1 = "( extent: resolver.api.Extent,\n ";
              }
              _builder.append(_xifexpression_1);
            } else {
              _builder.appendImmediate(",\n ", "");
            }
            _builder.append(" ");
            String _name_1 = attr.getName();
            _builder.append(_name_1);
            _builder.append(": ");
            String _queryResolverType = OMLUtilities.queryResolverType(attr, "resolver.api.");
            _builder.append(_queryResolverType);
          }
          if (_hasElements) {
            _builder.append(" )");
          }
        }
        _builder.newLineIfNotEmpty();
        _builder.append(": (resolver.api.Extent, resolver.api.");
        String _name_2 = eClass.getName();
        _builder.append(_name_2);
        _builder.append(")");
        _builder.newLineIfNotEmpty();
        _builder.append("= scala.Tuple2(");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("extent, ");
        _builder.newLine();
        _builder.append("\t\t");
        String _name_3 = eClass.getName();
        _builder.append(_name_3, "\t\t");
        {
          final Function1<EStructuralFeature, Boolean> _function_1 = (EStructuralFeature it) -> {
            Boolean _isContainer = OMLUtilities.isContainer(it);
            return Boolean.valueOf((!(_isContainer).booleanValue()));
          };
          Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_1);
          boolean _hasElements_1 = false;
          for(final EStructuralFeature attr_1 : _filter) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
              _builder.append("( ", "\t\t");
            } else {
              _builder.appendImmediate(", ", "\t\t");
            }
            String _name_4 = attr_1.getName();
            _builder.append(_name_4, "\t\t");
          }
          if (_hasElements_1) {
            _builder.append(" )", "\t\t");
          }
        }
        _builder.newLineIfNotEmpty();
        _builder.append(")");
        _builder.newLine();
        _xifexpression = _builder.toString();
      } else {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("override def create");
        String _name_5 = eClass.getName();
        _builder_1.append(_name_5);
        _builder_1.newLineIfNotEmpty();
        {
          Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
          boolean _hasElements_2 = false;
          for(final EStructuralFeature attr_2 : _sortedAttributeFactorySignature_1) {
            if (!_hasElements_2) {
              _hasElements_2 = true;
              String _xifexpression_2 = null;
              Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
              if ((_isExtentContainer_1).booleanValue()) {
                _xifexpression_2 = "( ";
              } else {
                _xifexpression_2 = "( extent: resolver.api.Extent,\n ";
              }
              _builder_1.append(_xifexpression_2);
            } else {
              _builder_1.appendImmediate(",\n ", "");
            }
            _builder_1.append(" ");
            String _name_6 = attr_2.getName();
            _builder_1.append(_name_6);
            _builder_1.append(": ");
            String _queryResolverType_1 = OMLUtilities.queryResolverType(attr_2, "resolver.api.");
            _builder_1.append(_queryResolverType_1);
          }
          if (_hasElements_2) {
            _builder_1.append(" )");
          }
        }
        _builder_1.newLineIfNotEmpty();
        _builder_1.append(": (resolver.api.Extent, resolver.api.");
        String _name_7 = eClass.getName();
        _builder_1.append(_name_7);
        _builder_1.append(")");
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("= {");
        _builder_1.newLine();
        _builder_1.append("  ");
        _builder_1.append("// factoryMethodWithoutUUID");
        _builder_1.newLine();
        _builder_1.append("  ");
        _builder_1.append("// container: ");
        String _name_8 = container.getName();
        _builder_1.append(_name_8, "  ");
        _builder_1.append(" ");
        String _name_9 = container.getEType().getName();
        _builder_1.append(_name_9, "  ");
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("  ");
        _builder_1.append("// contained: ");
        String _name_10 = contained.getName();
        _builder_1.append(_name_10, "  ");
        _builder_1.append(" ");
        String _name_11 = contained.getEType().getName();
        _builder_1.append(_name_11, "  ");
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("  ");
        _builder_1.append("val ");
        _builder_1.append(newVal, "  ");
        _builder_1.append(" = ");
        String _name_12 = eClass.getName();
        _builder_1.append(_name_12, "  ");
        {
          final Function1<EStructuralFeature, Boolean> _function_2 = (EStructuralFeature it) -> {
            Boolean _isContainer = OMLUtilities.isContainer(it);
            return Boolean.valueOf((!(_isContainer).booleanValue()));
          };
          Iterable<EStructuralFeature> _filter_1 = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_2);
          boolean _hasElements_3 = false;
          for(final EStructuralFeature attr_3 : _filter_1) {
            if (!_hasElements_3) {
              _hasElements_3 = true;
              _builder_1.append("( ", "  ");
            } else {
              _builder_1.appendImmediate(", ", "  ");
            }
            String _name_13 = attr_3.getName();
            _builder_1.append(_name_13, "  ");
          }
          if (_hasElements_3) {
            _builder_1.append(" )", "  ");
          }
        }
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("  ");
        _builder_1.append("scala.Tuple2(");
        _builder_1.newLine();
        _builder_1.append("    ");
        _builder_1.append("extent.copy(");
        _builder_1.newLine();
        _builder_1.append("      ");
        String _name_14 = contained.getName();
        _builder_1.append(_name_14, "      ");
        _builder_1.append(" = extent.with");
        String _name_15 = contained.getEType().getName();
        _builder_1.append(_name_15, "      ");
        _builder_1.append("(");
        String _name_16 = container.getName();
        _builder_1.append(_name_16, "      ");
        _builder_1.append(", ");
        _builder_1.append(newVal, "      ");
        _builder_1.append("),");
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("      ");
        String _firstLower = StringExtensions.toFirstLower(container.getEType().getName());
        _builder_1.append(_firstLower, "      ");
        _builder_1.append("Of");
        String _name_17 = contained.getEType().getName();
        _builder_1.append(_name_17, "      ");
        _builder_1.append(" = extent.");
        String _firstLower_1 = StringExtensions.toFirstLower(container.getEType().getName());
        _builder_1.append(_firstLower_1, "      ");
        _builder_1.append("Of");
        String _name_18 = contained.getEType().getName();
        _builder_1.append(_name_18, "      ");
        _builder_1.append(" + (");
        _builder_1.append(newVal, "      ");
        _builder_1.append(" -> ");
        String _name_19 = container.getName();
        _builder_1.append(_name_19, "      ");
        _builder_1.append(")),");
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("    ");
        _builder_1.append(newVal, "    ");
        _builder_1.append(")");
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("}");
        _builder_1.newLine();
        _xifexpression = _builder_1.toString();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithUUIDGenerator(final EClass eClass, final EStructuralFeature uuidNS, final Iterable<EStructuralFeature> uuidFactors) {
    String _xblockexpression = null;
    {
      final Function1<EReference, Boolean> _function = (EReference it) -> {
        return Boolean.valueOf(it.isContainer());
      };
      final EReference container = IterableExtensions.<EReference>findFirst(Iterables.<EReference>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), EReference.class), _function);
      EReference _eOpposite = null;
      if (container!=null) {
        _eOpposite=container.getEOpposite();
      }
      final EReference contained = _eOpposite;
      final String newVal = StringExtensions.toFirstLower(eClass.getName());
      String _xifexpression = null;
      if ((null == container)) {
        String _xifexpression_1 = null;
        Boolean _isExtentManaged = OMLUtilities.isExtentManaged(eClass);
        if ((_isExtentManaged).booleanValue()) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("override def create");
          String _name = eClass.getName();
          _builder.append(_name);
          _builder.newLineIfNotEmpty();
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements = false;
            for(final EStructuralFeature attr : _sortedAttributeFactorySignature) {
              if (!_hasElements) {
                _hasElements = true;
                String _xifexpression_2 = null;
                Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
                if ((_isExtentContainer).booleanValue()) {
                  String _name_1 = eClass.getName();
                  String _plus = ("( uuid: resolver.api.taggedTypes." + _name_1);
                  _xifexpression_2 = (_plus + "UUID,\n ");
                } else {
                  String _name_2 = eClass.getName();
                  String _plus_1 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_2);
                  _xifexpression_2 = (_plus_1 + "UUID,\n ");
                }
                _builder.append(_xifexpression_2);
              } else {
                _builder.appendImmediate(",\n ", "");
              }
              _builder.append(" ");
              String _name_3 = attr.getName();
              _builder.append(_name_3);
              _builder.append(": ");
              String _queryResolverType = OMLUtilities.queryResolverType(attr, "resolver.api.");
              _builder.append(_queryResolverType);
            }
            if (_hasElements) {
              _builder.append(" )");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append(": (resolver.api.Extent, resolver.api.");
          String _name_4 = eClass.getName();
          _builder.append(_name_4);
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("= {");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("val ");
          _builder.append(newVal, "  ");
          _builder.append(" = ");
          String _name_5 = eClass.getName();
          _builder.append(_name_5, "  ");
          {
            final Function1<EStructuralFeature, Boolean> _function_1 = (EStructuralFeature it) -> {
              Boolean _isContainer = OMLUtilities.isContainer(it);
              return Boolean.valueOf((!(_isContainer).booleanValue()));
            };
            Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_1);
            boolean _hasElements_1 = false;
            for(final EStructuralFeature attr_1 : _filter) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
                _builder.append("( uuid, ", "  ");
              } else {
                _builder.appendImmediate(", ", "  ");
              }
              String _name_6 = attr_1.getName();
              _builder.append(_name_6, "  ");
            }
            if (_hasElements_1) {
              _builder.append(" )", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("scala.Tuple2(");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("extent.copy(");
          String _tableVariableName = OMLUtilities.tableVariableName(eClass);
          _builder.append(_tableVariableName, "\t");
          _builder.append(" = extent.");
          String _tableVariableName_1 = OMLUtilities.tableVariableName(eClass);
          _builder.append(_tableVariableName_1, "\t");
          _builder.append(" + (uuid -> ");
          _builder.append(newVal, "\t");
          _builder.append(")), ");
          _builder.newLineIfNotEmpty();
          _builder.append("\t\t");
          _builder.append(newVal, "\t\t");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("}");
          _builder.newLine();
          _xifexpression_1 = _builder.toString();
        } else {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("override def create");
          String _name_7 = eClass.getName();
          _builder_1.append(_name_7);
          _builder_1.newLineIfNotEmpty();
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements_2 = false;
            for(final EStructuralFeature attr_2 : _sortedAttributeFactorySignature_1) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
                String _xifexpression_3 = null;
                Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
                if ((_isExtentContainer_1).booleanValue()) {
                  String _name_8 = eClass.getName();
                  String _plus_2 = ("( uuid: resolver.api.taggedTypes." + _name_8);
                  _xifexpression_3 = (_plus_2 + "UUID,\n ");
                } else {
                  String _name_9 = eClass.getName();
                  String _plus_3 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_9);
                  _xifexpression_3 = (_plus_3 + "UUID,\n ");
                }
                _builder_1.append(_xifexpression_3);
              } else {
                _builder_1.appendImmediate(",\n ", "");
              }
              _builder_1.append(" ");
              String _name_10 = attr_2.getName();
              _builder_1.append(_name_10);
              _builder_1.append(": ");
              String _queryResolverType_1 = OMLUtilities.queryResolverType(attr_2, "resolver.api.");
              _builder_1.append(_queryResolverType_1);
            }
            if (_hasElements_2) {
              _builder_1.append(" )");
            }
          }
          _builder_1.newLineIfNotEmpty();
          _builder_1.append(": (resolver.api.Extent, resolver.api.");
          String _name_11 = eClass.getName();
          _builder_1.append(_name_11);
          _builder_1.append(")");
          _builder_1.newLineIfNotEmpty();
          _builder_1.append("= scala.Tuple2(");
          _builder_1.newLine();
          _builder_1.append("\t");
          _builder_1.append("extent,");
          _builder_1.newLine();
          _builder_1.append("\t");
          String _name_12 = eClass.getName();
          _builder_1.append(_name_12, "\t");
          {
            final Function1<EStructuralFeature, Boolean> _function_2 = (EStructuralFeature it) -> {
              Boolean _isContainer = OMLUtilities.isContainer(it);
              return Boolean.valueOf((!(_isContainer).booleanValue()));
            };
            Iterable<EStructuralFeature> _filter_1 = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_2);
            boolean _hasElements_3 = false;
            for(final EStructuralFeature attr_3 : _filter_1) {
              if (!_hasElements_3) {
                _hasElements_3 = true;
                _builder_1.append("( uuid, ", "\t");
              } else {
                _builder_1.appendImmediate(", ", "\t");
              }
              String _name_13 = attr_3.getName();
              _builder_1.append(_name_13, "\t");
            }
            if (_hasElements_3) {
              _builder_1.append(" )", "\t");
            }
          }
          _builder_1.newLineIfNotEmpty();
          _builder_1.append(")");
          _builder_1.newLine();
          _xifexpression_1 = _builder_1.toString();
        }
        _xifexpression = _xifexpression_1;
      } else {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("override def create");
        String _name_14 = eClass.getName();
        _builder_2.append(_name_14);
        _builder_2.newLineIfNotEmpty();
        {
          Iterable<EStructuralFeature> _sortedAttributeFactorySignature_2 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
          boolean _hasElements_4 = false;
          for(final EStructuralFeature attr_4 : _sortedAttributeFactorySignature_2) {
            if (!_hasElements_4) {
              _hasElements_4 = true;
              String _xifexpression_4 = null;
              Boolean _isExtentContainer_2 = OMLUtilities.isExtentContainer(eClass);
              if ((_isExtentContainer_2).booleanValue()) {
                String _name_15 = eClass.getName();
                String _plus_4 = ("( uuid: resolver.api.taggedTypes." + _name_15);
                _xifexpression_4 = (_plus_4 + "UUID,\n ");
              } else {
                String _name_16 = eClass.getName();
                String _plus_5 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_16);
                _xifexpression_4 = (_plus_5 + "UUID,\n ");
              }
              _builder_2.append(_xifexpression_4);
            } else {
              _builder_2.appendImmediate(",\n ", "");
            }
            _builder_2.append(" ");
            String _name_17 = attr_4.getName();
            _builder_2.append(_name_17);
            _builder_2.append(": ");
            String _queryResolverType_2 = OMLUtilities.queryResolverType(attr_4, "resolver.api.");
            _builder_2.append(_queryResolverType_2);
          }
          if (_hasElements_4) {
            _builder_2.append(" )");
          }
        }
        _builder_2.newLineIfNotEmpty();
        _builder_2.append(": (resolver.api.Extent, resolver.api.");
        String _name_18 = eClass.getName();
        _builder_2.append(_name_18);
        _builder_2.append(")");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("= {");
        _builder_2.newLine();
        _builder_2.append("  ");
        _builder_2.append("// factoryMethodWithUUIDGenerator");
        _builder_2.newLine();
        _builder_2.append("  ");
        _builder_2.append("// container: ");
        String _name_19 = container.getName();
        _builder_2.append(_name_19, "  ");
        _builder_2.append(" ");
        String _name_20 = container.getEType().getName();
        _builder_2.append(_name_20, "  ");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("// contained: ");
        String _name_21 = contained.getName();
        _builder_2.append(_name_21, "  ");
        _builder_2.append(" ");
        String _name_22 = contained.getEType().getName();
        _builder_2.append(_name_22, "  ");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("val ");
        _builder_2.append(newVal, "  ");
        _builder_2.append(" = ");
        String _name_23 = eClass.getName();
        _builder_2.append(_name_23, "  ");
        {
          final Function1<EStructuralFeature, Boolean> _function_3 = (EStructuralFeature it) -> {
            Boolean _isContainer = OMLUtilities.isContainer(it);
            return Boolean.valueOf((!(_isContainer).booleanValue()));
          };
          Iterable<EStructuralFeature> _filter_2 = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_3);
          boolean _hasElements_5 = false;
          for(final EStructuralFeature attr_5 : _filter_2) {
            if (!_hasElements_5) {
              _hasElements_5 = true;
              _builder_2.append("( uuid, ", "  ");
            } else {
              _builder_2.appendImmediate(", ", "  ");
            }
            String _name_24 = attr_5.getName();
            _builder_2.append(_name_24, "  ");
          }
          if (_hasElements_5) {
            _builder_2.append(" )", "  ");
          }
        }
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("scala.Tuple2(");
        _builder_2.newLine();
        _builder_2.append("    ");
        _builder_2.append("extent.copy(");
        _builder_2.newLine();
        _builder_2.append("     ");
        String _name_25 = contained.getName();
        _builder_2.append(_name_25, "     ");
        _builder_2.append(" = extent.with");
        String _name_26 = contained.getEType().getName();
        _builder_2.append(_name_26, "     ");
        _builder_2.append("(");
        String _name_27 = container.getName();
        _builder_2.append(_name_27, "     ");
        _builder_2.append(", ");
        _builder_2.append(newVal, "     ");
        _builder_2.append("),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("     ");
        String _firstLower = StringExtensions.toFirstLower(container.getEType().getName());
        _builder_2.append(_firstLower, "     ");
        _builder_2.append("Of");
        String _name_28 = contained.getEType().getName();
        _builder_2.append(_name_28, "     ");
        _builder_2.append(" = extent.");
        String _firstLower_1 = StringExtensions.toFirstLower(container.getEType().getName());
        _builder_2.append(_firstLower_1, "     ");
        _builder_2.append("Of");
        String _name_29 = contained.getEType().getName();
        _builder_2.append(_name_29, "     ");
        _builder_2.append(" + (");
        _builder_2.append(newVal, "     ");
        _builder_2.append(" -> ");
        String _name_30 = container.getName();
        _builder_2.append(_name_30, "     ");
        _builder_2.append("),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("     ");
        String _firstLower_2 = StringExtensions.toFirstLower(contained.getEType().getName());
        _builder_2.append(_firstLower_2, "     ");
        _builder_2.append("ByUUID = extent.");
        String _firstLower_3 = StringExtensions.toFirstLower(contained.getEType().getName());
        _builder_2.append(_firstLower_3, "     ");
        _builder_2.append("ByUUID + (uuid -> ");
        _builder_2.append(newVal, "     ");
        _builder_2.append(")),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  \t");
        _builder_2.append(newVal, "  \t");
        _builder_2.append(")");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("}");
        _builder_2.newLine();
        _xifexpression = _builder_2.toString();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithUUIDGenerator(final EClass eClass, final EStructuralFeature uuidNS, final String uuidScala) {
    String _xblockexpression = null;
    {
      final Function1<EReference, Boolean> _function = (EReference it) -> {
        return Boolean.valueOf(it.isContainer());
      };
      final List<EReference> containers = IterableExtensions.<EReference>toList(IterableExtensions.<EReference>filter(Iterables.<EReference>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), EReference.class), _function));
      String _xifexpression = null;
      int _size = containers.size();
      boolean _equals = (1 == _size);
      if (_equals) {
        _xifexpression = this.factoryMethodWithUUIDGenerator1(eClass, uuidNS, uuidScala, containers.get(0));
      } else {
        String _xifexpression_1 = null;
        int _size_1 = containers.size();
        boolean _equals_1 = (2 == _size_1);
        if (_equals_1) {
          _xifexpression_1 = this.factoryMethodWithUUIDGenerator2(eClass, uuidNS, uuidScala, containers.get(0), containers.get(1));
        } else {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("factoryMethodWithUUIDGenerator: eClass=");
          String _name = eClass.getName();
          _builder.append(_name);
          throw new IllegalArgumentException(_builder.toString());
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithUUIDGenerator1(final EClass eClass, final EStructuralFeature uuidNS, final String uuidScala, final EReference container) {
    String _xblockexpression = null;
    {
      EReference _eOpposite = null;
      if (container!=null) {
        _eOpposite=container.getEOpposite();
      }
      final EReference contained = _eOpposite;
      final String newVal = StringExtensions.toFirstLower(eClass.getName());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("override def create");
      String _name = eClass.getName();
      _builder.append(_name);
      _builder.newLineIfNotEmpty();
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements = false;
        for(final EStructuralFeature attr : _sortedAttributeFactorySignature) {
          if (!_hasElements) {
            _hasElements = true;
            String _xifexpression = null;
            Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer).booleanValue()) {
              String _name_1 = eClass.getName();
              String _plus = ("( uuid: resolver.api.taggedTypes." + _name_1);
              _xifexpression = (_plus + "UUID,\n ");
            } else {
              String _name_2 = eClass.getName();
              String _plus_1 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_2);
              _xifexpression = (_plus_1 + "UUID,\n ");
            }
            _builder.append(_xifexpression);
          } else {
            _builder.appendImmediate(",\n ", "");
          }
          _builder.append(" ");
          String _name_3 = attr.getName();
          _builder.append(_name_3);
          _builder.append(": ");
          String _queryResolverType = OMLUtilities.queryResolverType(attr, "resolver.api.");
          _builder.append(_queryResolverType);
        }
        if (_hasElements) {
          _builder.append(" )");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append(": (resolver.api.Extent, resolver.api.");
      String _name_4 = eClass.getName();
      _builder.append(_name_4);
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("= {");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("// factoryMethodWithUUIDGenerator (scala...)");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("// container: ");
      String _name_5 = container.getName();
      _builder.append(_name_5, "  ");
      _builder.append(" ");
      String _name_6 = container.getEType().getName();
      _builder.append(_name_6, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("// contained: ");
      String _name_7 = contained.getName();
      _builder.append(_name_7, "  ");
      _builder.append(" ");
      String _name_8 = contained.getEType().getName();
      _builder.append(_name_8, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("val ");
      _builder.append(newVal, "  ");
      _builder.append(" = ");
      String _name_9 = eClass.getName();
      _builder.append(_name_9, "  ");
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements_1 = false;
        for(final EStructuralFeature attr_1 : _sortedAttributeFactorySignature_1) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            _builder.append("( uuid, ", "  ");
          } else {
            _builder.appendImmediate(", ", "  ");
          }
          String _name_10 = attr_1.getName();
          _builder.append(_name_10, "  ");
        }
        if (_hasElements_1) {
          _builder.append(" )", "  ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("scala.Tuple2(");
      _builder.newLine();
      {
        int _lowerBound = container.getLowerBound();
        boolean _equals = (_lowerBound == 0);
        if (_equals) {
          _builder.append("    ");
          String _name_11 = container.getName();
          _builder.append(_name_11, "    ");
          _builder.append(".fold {");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("extent.copy(");
          _builder.newLine();
          _builder.append("    ");
          _builder.append(" ");
          String _firstLower = StringExtensions.toFirstLower(contained.getEType().getName());
          _builder.append(_firstLower, "     ");
          _builder.append("ByUUID = extent.");
          String _firstLower_1 = StringExtensions.toFirstLower(contained.getEType().getName());
          _builder.append(_firstLower_1, "     ");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "     ");
          _builder.append("))");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("}{ _");
          String _name_12 = container.getName();
          _builder.append(_name_12, "    ");
          _builder.append("_ =>");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("extent.copy(");
          _builder.newLine();
          _builder.append("    ");
          _builder.append(" ");
          String _name_13 = contained.getName();
          _builder.append(_name_13, "     ");
          _builder.append(" = extent.with");
          String _name_14 = contained.getEType().getName();
          _builder.append(_name_14, "     ");
          _builder.append("(_");
          String _name_15 = container.getName();
          _builder.append(_name_15, "     ");
          _builder.append("_, ");
          _builder.append(newVal, "     ");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append(" ");
          String _firstLower_2 = StringExtensions.toFirstLower(container.getEType().getName());
          _builder.append(_firstLower_2, "     ");
          _builder.append("Of");
          String _name_16 = contained.getEType().getName();
          _builder.append(_name_16, "     ");
          _builder.append(" = extent.");
          String _firstLower_3 = StringExtensions.toFirstLower(container.getEType().getName());
          _builder.append(_firstLower_3, "     ");
          _builder.append("Of");
          String _name_17 = contained.getEType().getName();
          _builder.append(_name_17, "     ");
          _builder.append(" + (");
          _builder.append(newVal, "     ");
          _builder.append(" -> _");
          String _name_18 = container.getName();
          _builder.append(_name_18, "     ");
          _builder.append("_),");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append(" ");
          String _firstLower_4 = StringExtensions.toFirstLower(contained.getEType().getName());
          _builder.append(_firstLower_4, "     ");
          _builder.append("ByUUID = extent.");
          String _firstLower_5 = StringExtensions.toFirstLower(contained.getEType().getName());
          _builder.append(_firstLower_5, "     ");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "     ");
          _builder.append("))");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("},");
          _builder.newLine();
        } else {
          _builder.append("    ");
          _builder.append("extent.copy(");
          _builder.newLine();
          _builder.append("    ");
          _builder.append(" ");
          String _name_19 = contained.getName();
          _builder.append(_name_19, "     ");
          _builder.append(" = extent.with");
          String _name_20 = contained.getEType().getName();
          _builder.append(_name_20, "     ");
          _builder.append("(");
          String _name_21 = container.getName();
          _builder.append(_name_21, "     ");
          _builder.append(", ");
          _builder.append(newVal, "     ");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append(" ");
          String _firstLower_6 = StringExtensions.toFirstLower(container.getEType().getName());
          _builder.append(_firstLower_6, "     ");
          _builder.append("Of");
          String _name_22 = contained.getEType().getName();
          _builder.append(_name_22, "     ");
          _builder.append(" = extent.");
          String _firstLower_7 = StringExtensions.toFirstLower(container.getEType().getName());
          _builder.append(_firstLower_7, "     ");
          _builder.append("Of");
          String _name_23 = contained.getEType().getName();
          _builder.append(_name_23, "     ");
          _builder.append(" + (");
          _builder.append(newVal, "     ");
          _builder.append(" -> ");
          String _name_24 = container.getName();
          _builder.append(_name_24, "     ");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append(" ");
          String _firstLower_8 = StringExtensions.toFirstLower(contained.getEType().getName());
          _builder.append(_firstLower_8, "     ");
          _builder.append("ByUUID = extent.");
          String _firstLower_9 = StringExtensions.toFirstLower(contained.getEType().getName());
          _builder.append(_firstLower_9, "     ");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "     ");
          _builder.append(")),");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("  \t");
      _builder.append(newVal, "  \t");
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithUUIDGenerator2(final EClass eClass, final EStructuralFeature uuidNS, final String uuidScala, final EReference container1, final EReference container2) {
    String _xblockexpression = null;
    {
      EReference _eOpposite = null;
      if (container1!=null) {
        _eOpposite=container1.getEOpposite();
      }
      final EReference contained1 = _eOpposite;
      EReference _eOpposite_1 = null;
      if (container2!=null) {
        _eOpposite_1=container2.getEOpposite();
      }
      final EReference contained2 = _eOpposite_1;
      final String newVal = StringExtensions.toFirstLower(eClass.getName());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("override def create");
      String _name = eClass.getName();
      _builder.append(_name);
      _builder.newLineIfNotEmpty();
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements = false;
        for(final EStructuralFeature attr : _sortedAttributeFactorySignature) {
          if (!_hasElements) {
            _hasElements = true;
            String _xifexpression = null;
            Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer).booleanValue()) {
              String _name_1 = eClass.getName();
              String _plus = ("( uuid: resolver.api.taggedTypes." + _name_1);
              _xifexpression = (_plus + "UUID,\n ");
            } else {
              String _name_2 = eClass.getName();
              String _plus_1 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_2);
              _xifexpression = (_plus_1 + "UUID,\n ");
            }
            _builder.append(_xifexpression);
          } else {
            _builder.appendImmediate(",\n ", "");
          }
          _builder.append(" ");
          String _name_3 = attr.getName();
          _builder.append(_name_3);
          _builder.append(": ");
          String _queryResolverType = OMLUtilities.queryResolverType(attr, "resolver.api.");
          _builder.append(_queryResolverType);
        }
        if (_hasElements) {
          _builder.append(" )");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append(": (resolver.api.Extent, resolver.api.");
      String _name_4 = eClass.getName();
      _builder.append(_name_4);
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("= {");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("// factoryMethodWithUUIDGenerator (scala...)");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("// container1: ");
      String _name_5 = container1.getName();
      _builder.append(_name_5, "  ");
      _builder.append(" ");
      String _name_6 = container1.getEType().getName();
      _builder.append(_name_6, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("// contained1: ");
      String _name_7 = contained1.getName();
      _builder.append(_name_7, "  ");
      _builder.append(" ");
      String _name_8 = contained1.getEType().getName();
      _builder.append(_name_8, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("// container2: ");
      String _name_9 = container2.getName();
      _builder.append(_name_9, "  ");
      _builder.append(" ");
      String _name_10 = container2.getEType().getName();
      _builder.append(_name_10, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("// contained2: ");
      String _name_11 = contained2.getName();
      _builder.append(_name_11, "  ");
      _builder.append(" ");
      String _name_12 = contained2.getEType().getName();
      _builder.append(_name_12, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("val ");
      _builder.append(newVal, "  ");
      _builder.append(" = ");
      String _name_13 = eClass.getName();
      _builder.append(_name_13, "  ");
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements_1 = false;
        for(final EStructuralFeature attr_1 : _sortedAttributeFactorySignature_1) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            _builder.append("( uuid, ", "  ");
          } else {
            _builder.appendImmediate(", ", "  ");
          }
          String _name_14 = attr_1.getName();
          _builder.append(_name_14, "  ");
        }
        if (_hasElements_1) {
          _builder.append(" )", "  ");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        int _lowerBound = container1.getLowerBound();
        boolean _equals = (_lowerBound == 0);
        if (_equals) {
          _builder.append("  ");
          _builder.append("val extent1 = ");
          String _name_15 = container1.getName();
          _builder.append(_name_15, "  ");
          _builder.append(".fold {");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("extent.copy(");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t  ");
          String _firstLower = StringExtensions.toFirstLower(contained1.getEType().getName());
          _builder.append(_firstLower, "  \t  ");
          _builder.append("ByUUID = extent.");
          String _firstLower_1 = StringExtensions.toFirstLower(contained1.getEType().getName());
          _builder.append(_firstLower_1, "  \t  ");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "  \t  ");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append(")");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("}{ _");
          String _name_16 = container1.getName();
          _builder.append(_name_16, "  ");
          _builder.append("_ =>");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("extent.copy(");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t  ");
          String _name_17 = contained1.getName();
          _builder.append(_name_17, "  \t  ");
          _builder.append(" = extent.with");
          String _name_18 = contained1.getEType().getName();
          _builder.append(_name_18, "  \t  ");
          _builder.append("(_");
          String _name_19 = container1.getName();
          _builder.append(_name_19, "  \t  ");
          _builder.append("_, ");
          _builder.append(newVal, "  \t  ");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          String _firstLower_2 = StringExtensions.toFirstLower(container1.getEType().getName());
          _builder.append(_firstLower_2, "  \t  ");
          _builder.append("Of");
          String _name_20 = contained1.getEType().getName();
          _builder.append(_name_20, "  \t  ");
          _builder.append(" = extent.");
          String _firstLower_3 = StringExtensions.toFirstLower(container1.getEType().getName());
          _builder.append(_firstLower_3, "  \t  ");
          _builder.append("Of");
          String _name_21 = contained1.getEType().getName();
          _builder.append(_name_21, "  \t  ");
          _builder.append(" + (");
          _builder.append(newVal, "  \t  ");
          _builder.append(" -> _");
          String _name_22 = container1.getName();
          _builder.append(_name_22, "  \t  ");
          _builder.append("_),");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          String _firstLower_4 = StringExtensions.toFirstLower(contained1.getEType().getName());
          _builder.append(_firstLower_4, "  \t  ");
          _builder.append("ByUUID = extent.");
          String _firstLower_5 = StringExtensions.toFirstLower(contained1.getEType().getName());
          _builder.append(_firstLower_5, "  \t  ");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "  \t  ");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append(")");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
        } else {
          _builder.append("  ");
          _builder.append("val extent1 = extent.copy(");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t");
          String _name_23 = contained1.getName();
          _builder.append(_name_23, "  \t");
          _builder.append(" = extent.with");
          String _name_24 = contained1.getEType().getName();
          _builder.append(_name_24, "  \t");
          _builder.append("(");
          String _name_25 = container1.getName();
          _builder.append(_name_25, "  \t");
          _builder.append(", ");
          _builder.append(newVal, "  \t");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          String _firstLower_6 = StringExtensions.toFirstLower(container1.getEType().getName());
          _builder.append(_firstLower_6, "  \t");
          _builder.append("Of");
          String _name_26 = contained1.getEType().getName();
          _builder.append(_name_26, "  \t");
          _builder.append(" = extent.");
          String _firstLower_7 = StringExtensions.toFirstLower(container1.getEType().getName());
          _builder.append(_firstLower_7, "  \t");
          _builder.append("Of");
          String _name_27 = contained1.getEType().getName();
          _builder.append(_name_27, "  \t");
          _builder.append(" + (");
          _builder.append(newVal, "  \t");
          _builder.append(" -> ");
          String _name_28 = container1.getName();
          _builder.append(_name_28, "  \t");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          String _firstLower_8 = StringExtensions.toFirstLower(contained1.getEType().getName());
          _builder.append(_firstLower_8, "  \t");
          _builder.append("ByUUID = extent.");
          String _firstLower_9 = StringExtensions.toFirstLower(contained1.getEType().getName());
          _builder.append(_firstLower_9, "  \t");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "  \t");
          _builder.append(")),");
          _builder.newLineIfNotEmpty();
        }
      }
      {
        int _lowerBound_1 = container2.getLowerBound();
        boolean _equals_1 = (_lowerBound_1 == 0);
        if (_equals_1) {
          _builder.append("  ");
          _builder.append("val extent2 = ");
          String _name_29 = container2.getName();
          _builder.append(_name_29, "  ");
          _builder.append(".fold {");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("extent1.copy(");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t  ");
          String _firstLower_10 = StringExtensions.toFirstLower(contained2.getEType().getName());
          _builder.append(_firstLower_10, "  \t  ");
          _builder.append("ByUUID = extent.");
          String _firstLower_11 = StringExtensions.toFirstLower(contained2.getEType().getName());
          _builder.append(_firstLower_11, "  \t  ");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "  \t  ");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append(")");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("}{ _");
          String _name_30 = container2.getName();
          _builder.append(_name_30, "  ");
          _builder.append("_ =>");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("extent1.copy(");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t  ");
          String _name_31 = contained2.getName();
          _builder.append(_name_31, "  \t  ");
          _builder.append(" = extent.with");
          String _name_32 = contained2.getEType().getName();
          _builder.append(_name_32, "  \t  ");
          _builder.append("(_");
          String _name_33 = container2.getName();
          _builder.append(_name_33, "  \t  ");
          _builder.append("_, ");
          _builder.append(newVal, "  \t  ");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          String _firstLower_12 = StringExtensions.toFirstLower(container2.getEType().getName());
          _builder.append(_firstLower_12, "  \t  ");
          _builder.append("Of");
          String _name_34 = contained2.getEType().getName();
          _builder.append(_name_34, "  \t  ");
          _builder.append(" = extent.");
          String _firstLower_13 = StringExtensions.toFirstLower(container2.getEType().getName());
          _builder.append(_firstLower_13, "  \t  ");
          _builder.append("Of");
          String _name_35 = contained2.getEType().getName();
          _builder.append(_name_35, "  \t  ");
          _builder.append(" + (");
          _builder.append(newVal, "  \t  ");
          _builder.append(" -> _");
          String _name_36 = container2.getName();
          _builder.append(_name_36, "  \t  ");
          _builder.append("_),");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          String _firstLower_14 = StringExtensions.toFirstLower(contained2.getEType().getName());
          _builder.append(_firstLower_14, "  \t  ");
          _builder.append("ByUUID = extent.");
          String _firstLower_15 = StringExtensions.toFirstLower(contained2.getEType().getName());
          _builder.append(_firstLower_15, "  \t  ");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "  \t  ");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append(")");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
        } else {
          _builder.append("  ");
          _builder.append("val extent2 = extent1.copy(");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t");
          String _name_37 = contained2.getName();
          _builder.append(_name_37, "  \t");
          _builder.append(" = extent.with");
          String _name_38 = contained2.getEType().getName();
          _builder.append(_name_38, "  \t");
          _builder.append("(");
          String _name_39 = container1.getName();
          _builder.append(_name_39, "  \t");
          _builder.append(", ");
          _builder.append(newVal, "  \t");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          String _firstLower_16 = StringExtensions.toFirstLower(container2.getEType().getName());
          _builder.append(_firstLower_16, "  \t");
          _builder.append("Of");
          String _name_40 = contained2.getEType().getName();
          _builder.append(_name_40, "  \t");
          _builder.append(" = extent.");
          String _firstLower_17 = StringExtensions.toFirstLower(container2.getEType().getName());
          _builder.append(_firstLower_17, "  \t");
          _builder.append("Of");
          String _name_41 = contained2.getEType().getName();
          _builder.append(_name_41, "  \t");
          _builder.append(" + (");
          _builder.append(newVal, "  \t");
          _builder.append(" -> ");
          String _name_42 = container2.getName();
          _builder.append(_name_42, "  \t");
          _builder.append("),");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          String _firstLower_18 = StringExtensions.toFirstLower(contained2.getEType().getName());
          _builder.append(_firstLower_18, "  \t");
          _builder.append("ByUUID = extent.");
          String _firstLower_19 = StringExtensions.toFirstLower(contained2.getEType().getName());
          _builder.append(_firstLower_19, "  \t");
          _builder.append("ByUUID + (uuid -> ");
          _builder.append(newVal, "  \t");
          _builder.append(")),");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("  ");
      _builder.append("scala.Tuple2(extent2,");
      _builder.append(newVal, "  ");
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithDerivedUUID(final EClass eClass) {
    String _xblockexpression = null;
    {
      final Function1<EReference, Boolean> _function = (EReference it) -> {
        return Boolean.valueOf(it.isContainer());
      };
      final EReference container = IterableExtensions.<EReference>findFirst(Iterables.<EReference>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), EReference.class), _function);
      EReference _eOpposite = null;
      if (container!=null) {
        _eOpposite=container.getEOpposite();
      }
      final EReference contained = _eOpposite;
      final String newVal = StringExtensions.toFirstLower(eClass.getName());
      String _xifexpression = null;
      if ((null == container)) {
        String _xifexpression_1 = null;
        Boolean _isExtentManaged = OMLUtilities.isExtentManaged(eClass);
        if ((_isExtentManaged).booleanValue()) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("override def create");
          String _name = eClass.getName();
          _builder.append(_name);
          _builder.newLineIfNotEmpty();
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements = false;
            for(final EStructuralFeature attr : _sortedAttributeFactorySignature) {
              if (!_hasElements) {
                _hasElements = true;
                String _xifexpression_2 = null;
                Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
                if ((_isExtentContainer).booleanValue()) {
                  String _name_1 = eClass.getName();
                  String _plus = ("( uuid: resolver.api.taggedTypes." + _name_1);
                  _xifexpression_2 = (_plus + "UUID,\n ");
                } else {
                  String _name_2 = eClass.getName();
                  String _plus_1 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_2);
                  _xifexpression_2 = (_plus_1 + "UUID,\n ");
                }
                _builder.append(_xifexpression_2);
              } else {
                _builder.appendImmediate(",\n ", "");
              }
              _builder.append(" ");
              String _name_3 = attr.getName();
              _builder.append(_name_3);
              _builder.append(": ");
              String _queryResolverType = OMLUtilities.queryResolverType(attr, "resolver.api.");
              _builder.append(_queryResolverType);
            }
            if (_hasElements) {
              _builder.append(" )");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append(": (resolver.api.Extent, resolver.api.");
          String _name_4 = eClass.getName();
          _builder.append(_name_4);
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("= {");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("val ");
          _builder.append(newVal, "  ");
          _builder.append(" = ");
          String _name_5 = eClass.getName();
          _builder.append(_name_5, "  ");
          {
            final Function1<EStructuralFeature, Boolean> _function_1 = (EStructuralFeature it) -> {
              Boolean _isContainer = OMLUtilities.isContainer(it);
              return Boolean.valueOf((!(_isContainer).booleanValue()));
            };
            Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_1);
            boolean _hasElements_1 = false;
            for(final EStructuralFeature attr_1 : _filter) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
                _builder.append("( uuid, ", "  ");
              } else {
                _builder.appendImmediate(", ", "  ");
              }
              String _name_6 = attr_1.getName();
              _builder.append(_name_6, "  ");
            }
            if (_hasElements_1) {
              _builder.append(" )", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("scala.Tuple2(");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("extent.copy(");
          String _tableVariableName = OMLUtilities.tableVariableName(eClass);
          _builder.append(_tableVariableName, "\t");
          _builder.append(" = extent.");
          String _tableVariableName_1 = OMLUtilities.tableVariableName(eClass);
          _builder.append(_tableVariableName_1, "\t");
          _builder.append(" + (uuid -> ");
          _builder.append(newVal, "\t");
          _builder.append(")), ");
          _builder.newLineIfNotEmpty();
          _builder.append("\t\t");
          _builder.append(newVal, "\t\t");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("}");
          _builder.newLine();
          _xifexpression_1 = _builder.toString();
        } else {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("override def create");
          String _name_7 = eClass.getName();
          _builder_1.append(_name_7);
          _builder_1.newLineIfNotEmpty();
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements_2 = false;
            for(final EStructuralFeature attr_2 : _sortedAttributeFactorySignature_1) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
                String _xifexpression_3 = null;
                Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
                if ((_isExtentContainer_1).booleanValue()) {
                  String _name_8 = eClass.getName();
                  String _plus_2 = ("( uuid: resolver.api.taggedTypes." + _name_8);
                  _xifexpression_3 = (_plus_2 + "UUID,\n ");
                } else {
                  String _name_9 = eClass.getName();
                  String _plus_3 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_9);
                  _xifexpression_3 = (_plus_3 + "UUID,\n ");
                }
                _builder_1.append(_xifexpression_3);
              } else {
                _builder_1.appendImmediate(",\n ", "");
              }
              _builder_1.append(" ");
              String _name_10 = attr_2.getName();
              _builder_1.append(_name_10);
              _builder_1.append(": ");
              String _queryResolverType_1 = OMLUtilities.queryResolverType(attr_2, "resolver.api.");
              _builder_1.append(_queryResolverType_1);
            }
            if (_hasElements_2) {
              _builder_1.append(" )");
            }
          }
          _builder_1.newLineIfNotEmpty();
          _builder_1.append(": (resolver.api.Extent, resolver.api.");
          String _name_11 = eClass.getName();
          _builder_1.append(_name_11);
          _builder_1.append(")");
          _builder_1.newLineIfNotEmpty();
          _builder_1.append("= scala.Tuple2(");
          _builder_1.newLine();
          _builder_1.append("    ");
          _builder_1.append("extent, ");
          _builder_1.newLine();
          _builder_1.append("    ");
          String _name_12 = eClass.getName();
          _builder_1.append(_name_12, "    ");
          {
            final Function1<EStructuralFeature, Boolean> _function_2 = (EStructuralFeature it) -> {
              Boolean _isContainer = OMLUtilities.isContainer(it);
              return Boolean.valueOf((!(_isContainer).booleanValue()));
            };
            Iterable<EStructuralFeature> _filter_1 = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_2);
            boolean _hasElements_3 = false;
            for(final EStructuralFeature attr_3 : _filter_1) {
              if (!_hasElements_3) {
                _hasElements_3 = true;
                _builder_1.append("( uuid, ", "    ");
              } else {
                _builder_1.appendImmediate(", ", "    ");
              }
              String _name_13 = attr_3.getName();
              _builder_1.append(_name_13, "    ");
            }
            if (_hasElements_3) {
              _builder_1.append(" )", "    ");
            }
          }
          _builder_1.newLineIfNotEmpty();
          _builder_1.append(")");
          _builder_1.newLine();
          _xifexpression_1 = _builder_1.toString();
        }
        _xifexpression = _xifexpression_1;
      } else {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("override def create");
        String _name_14 = eClass.getName();
        _builder_2.append(_name_14);
        _builder_2.newLineIfNotEmpty();
        {
          Iterable<EStructuralFeature> _sortedAttributeFactorySignature_2 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
          boolean _hasElements_4 = false;
          for(final EStructuralFeature attr_4 : _sortedAttributeFactorySignature_2) {
            if (!_hasElements_4) {
              _hasElements_4 = true;
              String _xifexpression_4 = null;
              Boolean _isExtentContainer_2 = OMLUtilities.isExtentContainer(eClass);
              if ((_isExtentContainer_2).booleanValue()) {
                String _name_15 = eClass.getName();
                String _plus_4 = ("( uuid: resolver.api.taggedTypes." + _name_15);
                _xifexpression_4 = (_plus_4 + "UUID,\n ");
              } else {
                String _name_16 = eClass.getName();
                String _plus_5 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_16);
                _xifexpression_4 = (_plus_5 + "UUID,\n ");
              }
              _builder_2.append(_xifexpression_4);
            } else {
              _builder_2.appendImmediate(",\n ", "");
            }
            _builder_2.append(" ");
            String _name_17 = attr_4.getName();
            _builder_2.append(_name_17);
            _builder_2.append(": ");
            String _queryResolverType_2 = OMLUtilities.queryResolverType(attr_4, "resolver.api.");
            _builder_2.append(_queryResolverType_2);
          }
          if (_hasElements_4) {
            _builder_2.append(" )");
          }
        }
        _builder_2.newLineIfNotEmpty();
        _builder_2.append(": (resolver.api.Extent, resolver.api.");
        String _name_18 = eClass.getName();
        _builder_2.append(_name_18);
        _builder_2.append(")");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("= {");
        _builder_2.newLine();
        _builder_2.append("  ");
        _builder_2.append("// factoryMethodWithDerivedUUID");
        _builder_2.newLine();
        _builder_2.append("  ");
        _builder_2.append("// container: ");
        String _name_19 = container.getName();
        _builder_2.append(_name_19, "  ");
        _builder_2.append(" ");
        String _name_20 = container.getEType().getName();
        _builder_2.append(_name_20, "  ");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("// contained: ");
        String _name_21 = contained.getName();
        _builder_2.append(_name_21, "  ");
        _builder_2.append(" ");
        String _name_22 = contained.getEType().getName();
        _builder_2.append(_name_22, "  ");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("val ");
        _builder_2.append(newVal, "  ");
        _builder_2.append(" = ");
        String _name_23 = eClass.getName();
        _builder_2.append(_name_23, "  ");
        {
          final Function1<EStructuralFeature, Boolean> _function_3 = (EStructuralFeature it) -> {
            Boolean _isFactory = OMLUtilities.isFactory(it);
            return Boolean.valueOf((!(_isFactory).booleanValue()));
          };
          Iterable<EStructuralFeature> _filter_2 = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_3);
          boolean _hasElements_5 = false;
          for(final EStructuralFeature attr_5 : _filter_2) {
            if (!_hasElements_5) {
              _hasElements_5 = true;
              _builder_2.append("( uuid, ", "  ");
            } else {
              _builder_2.appendImmediate(", ", "  ");
            }
            String _name_24 = attr_5.getName();
            _builder_2.append(_name_24, "  ");
          }
          if (_hasElements_5) {
            _builder_2.append(" )", "  ");
          }
        }
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("scala.Tuple2(");
        _builder_2.newLine();
        _builder_2.append("  \t");
        _builder_2.append("extent.copy(");
        _builder_2.newLine();
        _builder_2.append("  \t  ");
        String _name_25 = contained.getName();
        _builder_2.append(_name_25, "  \t  ");
        _builder_2.append(" = extent.with");
        String _name_26 = contained.getEType().getName();
        _builder_2.append(_name_26, "  \t  ");
        _builder_2.append("(");
        String _name_27 = container.getName();
        _builder_2.append(_name_27, "  \t  ");
        _builder_2.append(", ");
        _builder_2.append(newVal, "  \t  ");
        _builder_2.append("),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  \t  ");
        String _firstLower = StringExtensions.toFirstLower(container.getEType().getName());
        _builder_2.append(_firstLower, "  \t  ");
        _builder_2.append("Of");
        String _name_28 = contained.getEType().getName();
        _builder_2.append(_name_28, "  \t  ");
        _builder_2.append(" = extent.");
        String _firstLower_1 = StringExtensions.toFirstLower(container.getEType().getName());
        _builder_2.append(_firstLower_1, "  \t  ");
        _builder_2.append("Of");
        String _name_29 = contained.getEType().getName();
        _builder_2.append(_name_29, "  \t  ");
        _builder_2.append(" + (");
        _builder_2.append(newVal, "  \t  ");
        _builder_2.append(" -> ");
        String _name_30 = container.getName();
        _builder_2.append(_name_30, "  \t  ");
        _builder_2.append("),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  \t  ");
        String _firstLower_2 = StringExtensions.toFirstLower(contained.getEType().getName());
        _builder_2.append(_firstLower_2, "  \t  ");
        _builder_2.append("ByUUID = extent.");
        String _firstLower_3 = StringExtensions.toFirstLower(contained.getEType().getName());
        _builder_2.append(_firstLower_3, "  \t  ");
        _builder_2.append("ByUUID + (uuid -> ");
        _builder_2.append(newVal, "  \t  ");
        _builder_2.append(")),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  \t");
        _builder_2.append(newVal, "  \t");
        _builder_2.append(")");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("}");
        _builder_2.newLine();
        _xifexpression = _builder_2.toString();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithImplicitlyDerivedUUID(final EClass eClass) {
    String _xblockexpression = null;
    {
      final Function1<EReference, Boolean> _function = (EReference it) -> {
        return Boolean.valueOf(it.isContainer());
      };
      final EReference container = IterableExtensions.<EReference>findFirst(Iterables.<EReference>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), EReference.class), _function);
      EReference _eOpposite = null;
      if (container!=null) {
        _eOpposite=container.getEOpposite();
      }
      final EReference contained = _eOpposite;
      final String newVal = StringExtensions.toFirstLower(eClass.getName());
      String _xifexpression = null;
      if ((null == container)) {
        String _xifexpression_1 = null;
        Boolean _isExtentManaged = OMLUtilities.isExtentManaged(eClass);
        if ((_isExtentManaged).booleanValue()) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("override def create");
          String _name = eClass.getName();
          _builder.append(_name);
          _builder.newLineIfNotEmpty();
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements = false;
            for(final EStructuralFeature attr : _sortedAttributeFactorySignature) {
              if (!_hasElements) {
                _hasElements = true;
                String _xifexpression_2 = null;
                Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
                if ((_isExtentContainer).booleanValue()) {
                  String _name_1 = eClass.getName();
                  String _plus = ("( uuid: resolver.api.taggedTypes." + _name_1);
                  _xifexpression_2 = (_plus + "UUID,\n ");
                } else {
                  String _name_2 = eClass.getName();
                  String _plus_1 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_2);
                  _xifexpression_2 = (_plus_1 + "UUID,\n ");
                }
                _builder.append(_xifexpression_2);
              } else {
                _builder.appendImmediate(",\n ", "");
              }
              _builder.append(" ");
              String _name_3 = attr.getName();
              _builder.append(_name_3);
              _builder.append(": ");
              String _queryResolverType = OMLUtilities.queryResolverType(attr, "resolver.api.");
              _builder.append(_queryResolverType);
            }
            if (_hasElements) {
              _builder.append(" )");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append(": (resolver.api.Extent, resolver.api.");
          String _name_4 = eClass.getName();
          _builder.append(_name_4);
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("= {");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("val ");
          _builder.append(newVal, "  ");
          _builder.append(" = ");
          String _name_5 = eClass.getName();
          _builder.append(_name_5, "  ");
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements_1 = false;
            for(final EStructuralFeature attr_1 : _sortedAttributeFactorySignature_1) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
                _builder.append("( uuid, ", "  ");
              } else {
                _builder.appendImmediate(", ", "  ");
              }
              String _name_6 = attr_1.getName();
              _builder.append(_name_6, "  ");
            }
            if (_hasElements_1) {
              _builder.append(" )", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("scala.Tuple2(");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("extent.copy(");
          String _tableVariableName = OMLUtilities.tableVariableName(eClass);
          _builder.append(_tableVariableName, "\t");
          _builder.append(" = extent.");
          String _tableVariableName_1 = OMLUtilities.tableVariableName(eClass);
          _builder.append(_tableVariableName_1, "\t");
          _builder.append(" + (uuid -> ");
          _builder.append(newVal, "\t");
          _builder.append(")), ");
          _builder.newLineIfNotEmpty();
          _builder.append("\t\t");
          _builder.append(newVal, "\t\t");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("}");
          _builder.newLine();
          _xifexpression_1 = _builder.toString();
        } else {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("override def create");
          String _name_7 = eClass.getName();
          _builder_1.append(_name_7);
          _builder_1.newLineIfNotEmpty();
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature_2 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements_2 = false;
            for(final EStructuralFeature attr_2 : _sortedAttributeFactorySignature_2) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
                String _xifexpression_3 = null;
                Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
                if ((_isExtentContainer_1).booleanValue()) {
                  String _name_8 = eClass.getName();
                  String _plus_2 = ("( uuid: resolver.api.taggedTypes." + _name_8);
                  _xifexpression_3 = (_plus_2 + "UUID,\n ");
                } else {
                  String _name_9 = eClass.getName();
                  String _plus_3 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_9);
                  _xifexpression_3 = (_plus_3 + "UUID,\n ");
                }
                _builder_1.append(_xifexpression_3);
              } else {
                _builder_1.appendImmediate(",\n ", "");
              }
              _builder_1.append(" ");
              String _name_10 = attr_2.getName();
              _builder_1.append(_name_10);
              _builder_1.append(": ");
              String _queryResolverType_1 = OMLUtilities.queryResolverType(attr_2, "resolver.api.");
              _builder_1.append(_queryResolverType_1);
            }
            if (_hasElements_2) {
              _builder_1.append(" )");
            }
          }
          _builder_1.newLineIfNotEmpty();
          _builder_1.append(": (resolver.api.Extent, resolver.api.");
          String _name_11 = eClass.getName();
          _builder_1.append(_name_11);
          _builder_1.append(")");
          _builder_1.newLineIfNotEmpty();
          _builder_1.append("= scala.Tuple2(");
          _builder_1.newLine();
          _builder_1.append("    ");
          _builder_1.append("extent, ");
          _builder_1.newLine();
          _builder_1.append("\t");
          String _name_12 = eClass.getName();
          _builder_1.append(_name_12, "\t");
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature_3 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements_3 = false;
            for(final EStructuralFeature attr_3 : _sortedAttributeFactorySignature_3) {
              if (!_hasElements_3) {
                _hasElements_3 = true;
                _builder_1.append("( uuid, ", "\t");
              } else {
                _builder_1.appendImmediate(", ", "\t");
              }
              String _name_13 = attr_3.getName();
              _builder_1.append(_name_13, "\t");
            }
            if (_hasElements_3) {
              _builder_1.append(" )", "\t");
            }
          }
          _builder_1.newLineIfNotEmpty();
          _builder_1.append(")");
          _builder_1.newLine();
          _xifexpression_1 = _builder_1.toString();
        }
        _xifexpression = _xifexpression_1;
      } else {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("override def create");
        String _name_14 = eClass.getName();
        _builder_2.append(_name_14);
        _builder_2.newLineIfNotEmpty();
        {
          Iterable<EStructuralFeature> _sortedAttributeFactorySignature_4 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
          boolean _hasElements_4 = false;
          for(final EStructuralFeature attr_4 : _sortedAttributeFactorySignature_4) {
            if (!_hasElements_4) {
              _hasElements_4 = true;
              String _xifexpression_4 = null;
              Boolean _isExtentContainer_2 = OMLUtilities.isExtentContainer(eClass);
              if ((_isExtentContainer_2).booleanValue()) {
                String _name_15 = eClass.getName();
                String _plus_4 = ("( uuid: resolver.api.taggedTypes." + _name_15);
                _xifexpression_4 = (_plus_4 + "UUID,\n ");
              } else {
                String _name_16 = eClass.getName();
                String _plus_5 = ("( extent: resolver.api.Extent,\n  uuid: resolver.api.taggedTypes." + _name_16);
                _xifexpression_4 = (_plus_5 + "UUID,\n ");
              }
              _builder_2.append(_xifexpression_4);
            } else {
              _builder_2.appendImmediate(",\n ", "");
            }
            _builder_2.append(" ");
            String _name_17 = attr_4.getName();
            _builder_2.append(_name_17);
            _builder_2.append(": ");
            String _queryResolverType_2 = OMLUtilities.queryResolverType(attr_4, "resolver.api.");
            _builder_2.append(_queryResolverType_2);
          }
          if (_hasElements_4) {
            _builder_2.append(" )");
          }
        }
        _builder_2.newLineIfNotEmpty();
        _builder_2.append(": (resolver.api.Extent, resolver.api.");
        String _name_18 = eClass.getName();
        _builder_2.append(_name_18);
        _builder_2.append(")");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("= {");
        _builder_2.newLine();
        _builder_2.append("  ");
        _builder_2.append("// factoryMethodWithImplicitlyDerivedUUID");
        _builder_2.newLine();
        _builder_2.append("  ");
        _builder_2.append("// container: ");
        String _name_19 = container.getName();
        _builder_2.append(_name_19, "  ");
        _builder_2.append(" ");
        String _name_20 = container.getEType().getName();
        _builder_2.append(_name_20, "  ");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("// contained: ");
        String _name_21 = contained.getName();
        _builder_2.append(_name_21, "  ");
        _builder_2.append(" ");
        String _name_22 = contained.getEType().getName();
        _builder_2.append(_name_22, "  ");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("val ");
        _builder_2.append(newVal, "  ");
        _builder_2.append(" = ");
        String _name_23 = eClass.getName();
        _builder_2.append(_name_23, "  ");
        {
          final Function1<EStructuralFeature, Boolean> _function_1 = (EStructuralFeature it) -> {
            Boolean _isFactory = OMLUtilities.isFactory(it);
            return Boolean.valueOf((!(_isFactory).booleanValue()));
          };
          Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_1);
          boolean _hasElements_5 = false;
          for(final EStructuralFeature attr_5 : _filter) {
            if (!_hasElements_5) {
              _hasElements_5 = true;
              _builder_2.append("( uuid, ", "  ");
            } else {
              _builder_2.appendImmediate(", ", "  ");
            }
            String _name_24 = attr_5.getName();
            _builder_2.append(_name_24, "  ");
          }
          if (_hasElements_5) {
            _builder_2.append(" )", "  ");
          }
        }
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  ");
        _builder_2.append("scala.Tuple2(");
        _builder_2.newLine();
        _builder_2.append("  \t");
        _builder_2.append("extent.copy(");
        _builder_2.newLine();
        _builder_2.append("  \t  ");
        String _name_25 = contained.getName();
        _builder_2.append(_name_25, "  \t  ");
        _builder_2.append(" = extent.with");
        String _name_26 = contained.getEType().getName();
        _builder_2.append(_name_26, "  \t  ");
        _builder_2.append("(");
        String _name_27 = container.getName();
        _builder_2.append(_name_27, "  \t  ");
        _builder_2.append(", ");
        _builder_2.append(newVal, "  \t  ");
        _builder_2.append("),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  \t  ");
        String _firstLower = StringExtensions.toFirstLower(container.getEType().getName());
        _builder_2.append(_firstLower, "  \t  ");
        _builder_2.append("Of");
        String _name_28 = contained.getEType().getName();
        _builder_2.append(_name_28, "  \t  ");
        _builder_2.append(" = extent.");
        String _firstLower_1 = StringExtensions.toFirstLower(container.getEType().getName());
        _builder_2.append(_firstLower_1, "  \t  ");
        _builder_2.append("Of");
        String _name_29 = contained.getEType().getName();
        _builder_2.append(_name_29, "  \t  ");
        _builder_2.append(" + (");
        _builder_2.append(newVal, "  \t  ");
        _builder_2.append(" -> ");
        String _name_30 = container.getName();
        _builder_2.append(_name_30, "  \t  ");
        _builder_2.append("),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  \t  ");
        String _firstLower_2 = StringExtensions.toFirstLower(contained.getEType().getName());
        _builder_2.append(_firstLower_2, "  \t  ");
        _builder_2.append("ByUUID = extent.");
        String _firstLower_3 = StringExtensions.toFirstLower(contained.getEType().getName());
        _builder_2.append(_firstLower_3, "  \t  ");
        _builder_2.append("ByUUID + (uuid -> ");
        _builder_2.append(newVal, "  \t  ");
        _builder_2.append(")),");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("  \t");
        _builder_2.append(newVal, "  \t");
        _builder_2.append(")");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("}");
        _builder_2.newLine();
        _xifexpression = _builder_2.toString();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public String generateClassFile(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.append("package gov.nasa.jpl.imce.oml.resolver.impl");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml._");
    _builder.newLine();
    _builder.newLine();
    {
      boolean _isAbstract = eClass.isAbstract();
      if (_isAbstract) {
        _builder.append("trait ");
      } else {
        _builder.append("case class ");
      }
    }
    String _classDeclaration = OMLSpecificationResolverLibraryGenerator.classDeclaration(eClass);
    _builder.append(_classDeclaration);
    {
      boolean _isAbstract_1 = eClass.isAbstract();
      if (_isAbstract_1) {
        _builder.newLineIfNotEmpty();
        _builder.append("{");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("override val uuid: resolver.api.taggedTypes.");
        String _name = eClass.getName();
        _builder.append(_name, "  ");
        _builder.append("UUID");
        {
          final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
            String _name_1 = it.getName();
            return Boolean.valueOf((!Objects.equal(_name_1, "uuid")));
          };
          Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.APIStructuralFeatures(eClass), _function);
          boolean _hasElements = false;
          for(final EStructuralFeature f : _filter) {
            if (!_hasElements) {
              _hasElements = true;
              _builder.append("\n", "  ");
            } else {
              _builder.appendImmediate("\n", "  ");
            }
            String _doc = OMLUtilities.doc(f, "  ");
            _builder.append(_doc, "  ");
            _builder.append("override val ");
            String _name_1 = f.getName();
            _builder.append(_name_1, "  ");
            _builder.append(": ");
            String _queryResolverType = OMLUtilities.queryResolverType(f, "resolver.api.");
            _builder.append(_queryResolverType, "  ");
          }
          if (_hasElements) {
            _builder.append("\n  ", "  ");
          }
        }
      } else {
        _builder.newLineIfNotEmpty();
        _builder.append("{");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    {
      final Function1<EOperation, Boolean> _function_1 = (EOperation it) -> {
        EAnnotation _eAnnotation = it.getEAnnotation("http://imce.jpl.nasa.gov/oml/OverrideVal");
        return Boolean.valueOf((null == _eAnnotation));
      };
      Iterable<EOperation> _filter_1 = IterableExtensions.<EOperation>filter(OMLUtilities.ScalaOperations(eClass), _function_1);
      for(final EOperation op : _filter_1) {
        _builder.append("  ");
        String _doc_1 = OMLUtilities.doc(op, "  ");
        _builder.append(_doc_1);
        String _queryResolverName = OMLUtilities.queryResolverName(op, "resolver.api.");
        _builder.append(_queryResolverName);
        _builder.newLineIfNotEmpty();
        _builder.append("\t  ");
        _builder.append(": ");
        String _queryResolverType_1 = OMLUtilities.queryResolverType(op, "resolver.api.");
        _builder.append(_queryResolverType_1, "\t  ");
        _builder.newLineIfNotEmpty();
        _builder.append("\t  ");
        _builder.append("= ");
        String _queryBody = OMLUtilities.queryBody(op);
        _builder.append(_queryBody, "\t  ");
        _builder.newLineIfNotEmpty();
        _builder.newLine();
      }
    }
    {
      Boolean _isSpecializationOfRootClass = OMLUtilities.isSpecializationOfRootClass(eClass);
      if ((_isSpecializationOfRootClass).booleanValue()) {
        _builder.append("  override def canEqual(that: scala.Any): scala.Boolean = that match {");
        _builder.newLineIfNotEmpty();
        _builder.append("\t  ");
        _builder.append("case _: ");
        String _name_2 = eClass.getName();
        _builder.append(_name_2, "\t  ");
        _builder.append(" => true");
        _builder.newLineIfNotEmpty();
        _builder.append(" \t  ");
        _builder.append("case _ => false");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
        _builder.newLine();
      }
    }
    {
      boolean _isAbstract_2 = eClass.isAbstract();
      boolean _not = (!_isAbstract_2);
      if (_not) {
        _builder.append("  ");
        _builder.append("override val hashCode");
        _builder.newLine();
        _builder.append("  ");
        _builder.append(": scala.Int");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("= ");
        {
          Iterable<EStructuralFeature> _sortedAttributeSignature = OMLUtilities.getSortedAttributeSignature(eClass);
          boolean _hasElements_1 = false;
          for(final EStructuralFeature keyFeature : _sortedAttributeSignature) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
              _builder.append("(", "  ");
            } else {
              _builder.appendImmediate(", ", "  ");
            }
            String _name_3 = keyFeature.getName();
            _builder.append(_name_3, "  ");
          }
          if (_hasElements_1) {
            _builder.append(").##", "  ");
          }
        }
        _builder.newLineIfNotEmpty();
        _builder.newLine();
        _builder.append("  ");
        _builder.append("override def equals(other: scala.Any): scala.Boolean = other match {");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("case that: ");
        String _name_4 = eClass.getName();
        _builder.append(_name_4, "    ");
        _builder.append(" =>");
        _builder.newLineIfNotEmpty();
        _builder.append("      ");
        _builder.append("(that canEqual this) &&");
        _builder.newLine();
        _builder.append("      ");
        {
          Iterable<EStructuralFeature> _sortedAttributeSignature_1 = OMLUtilities.getSortedAttributeSignature(eClass);
          boolean _hasElements_2 = false;
          for(final EStructuralFeature keyFeature_1 : _sortedAttributeSignature_1) {
            if (!_hasElements_2) {
              _hasElements_2 = true;
            } else {
              _builder.appendImmediate(" &&\n", "      ");
            }
            _builder.append("(this.");
            String _name_5 = keyFeature_1.getName();
            _builder.append(_name_5, "      ");
            _builder.append(" == that.");
            String _name_6 = keyFeature_1.getName();
            _builder.append(_name_6, "      ");
            _builder.append(")");
          }
        }
        _builder.newLineIfNotEmpty();
        _builder.newLine();
        _builder.append("    ");
        _builder.append("case _ =>");
        _builder.newLine();
        _builder.append("      ");
        _builder.append("false");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public static String classDeclaration(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = eClass.getName();
    _builder.append(_name);
    {
      boolean _isAbstract = eClass.isAbstract();
      boolean _not = (!_isAbstract);
      if (_not) {
        _builder.append(" private[impl] ");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("(");
        _builder.newLine();
        {
          Iterable<EStructuralFeature> _sortedAttributeSignature = OMLUtilities.getSortedAttributeSignature(eClass);
          boolean _hasElements = false;
          for(final EStructuralFeature attr : _sortedAttributeSignature) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "\t ");
            }
            _builder.append("\t ");
            _builder.append("override val ");
            String _name_1 = attr.getName();
            _builder.append(_name_1, "\t ");
            _builder.append(": ");
            {
              String _name_2 = attr.getName();
              boolean _equals = Objects.equal(_name_2, "uuid");
              if (_equals) {
                _builder.append("resolver.api.taggedTypes.");
                String _name_3 = eClass.getName();
                String _plus = (_name_3 + "UUID");
                _builder.append(_plus, "\t ");
              } else {
                String _queryResolverType = OMLUtilities.queryResolverType(attr, "resolver.api.");
                _builder.append(_queryResolverType, "\t ");
              }
            }
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("extends resolver.api.");
    String _name_4 = eClass.getName();
    _builder.append(_name_4);
    _builder.newLineIfNotEmpty();
    {
      EList<EClass> _eSuperTypes = eClass.getESuperTypes();
      boolean _hasElements_1 = false;
      for(final EClass parent : _eSuperTypes) {
        if (!_hasElements_1) {
          _hasElements_1 = true;
          _builder.append("  with ");
        } else {
          _builder.appendImmediate("\n  with ", "");
        }
        String _name_5 = parent.getName();
        _builder.append(_name_5);
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
}
