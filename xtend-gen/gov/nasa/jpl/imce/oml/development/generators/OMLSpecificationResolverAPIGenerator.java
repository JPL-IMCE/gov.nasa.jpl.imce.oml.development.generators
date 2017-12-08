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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class OMLSpecificationResolverAPIGenerator extends OMLUtilities {
  public static void main(final String[] args) {
    int _length = args.length;
    boolean _notEquals = (1 != _length);
    if (_notEquals) {
      System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.tables project");
      System.exit(1);
    }
    final OMLSpecificationResolverAPIGenerator gen = new OMLSpecificationResolverAPIGenerator();
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
    final String targetFolder = "shared/src/main/scala/gov/nasa/jpl/imce/oml/resolver/api";
    final Path targetPath = bundlePath.resolve(targetFolder);
    targetPath.toFile().mkdirs();
    this.generate(
      Collections.<EPackage>unmodifiableList(CollectionLiterals.<EPackage>newArrayList(this.c, this.t, this.g, this.b, this.d)), 
      "gov.nasa.jpl.imce.oml.resolver.api", 
      targetPath.toAbsolutePath().toString());
  }
  
  public void generate(final List<EPackage> ePackages, final String packageQName, final String targetFolder) {
    try {
      File _file = new File(((targetFolder + File.separator) + "package.scala"));
      final FileOutputStream packageFile = new FileOutputStream(_file);
      try {
        packageFile.write(this.generatePackageFile(ePackages, packageQName).getBytes());
      } finally {
        packageFile.close();
      }
      File _file_1 = new File(((targetFolder + File.separator) + "OMLResolvedFactory.scala"));
      final FileOutputStream factoryFile = new FileOutputStream(_file_1);
      try {
        factoryFile.write(this.generateFactoryFile(ePackages, packageQName).getBytes());
      } finally {
        factoryFile.close();
      }
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return OMLUtilities.isAPI(it);
      };
      final Iterable<EClass> eClasses = IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1);
      for (final EClass eClass : eClasses) {
        {
          String _name = eClass.getName();
          String _plus = ((targetFolder + File.separator) + _name);
          String _plus_1 = (_plus + ".scala");
          File _file_2 = new File(_plus_1);
          final FileOutputStream classFile = new FileOutputStream(_file_2);
          try {
            Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer).booleanValue()) {
              classFile.write(this.generateExtentContainerClassFile(eClass, eClasses).getBytes());
            } else {
              classFile.write(this.generateClassFile(eClass).getBytes());
            }
          } finally {
            classFile.close();
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @SuppressWarnings("unused")
  public String generatePackageFile(final List<EPackage> ePackages, final String packageQName) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("package ");
    String _substring = packageQName.substring(0, packageQName.lastIndexOf("."));
    _builder.append(_substring);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("package object ");
    int _lastIndexOf = packageQName.lastIndexOf(".");
    int _plus = (_lastIndexOf + 1);
    String _substring_1 = packageQName.substring(_plus);
    _builder.append(_substring_1);
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("implicit def UUIDOrdering");
    _builder.newLine();
    _builder.append("  ");
    _builder.append(": scala.Ordering[java.util.UUID]");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("= new scala.Ordering[java.util.UUID] {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("def compare(x: java.util.UUID, y:java.util.UUID)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append(": scala.Int");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("= x.compareTo(y)");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    {
      final Function1<EPackage, Iterable<EClass>> _function = (EPackage it) -> {
        return OMLUtilities.FunctionalAPIClasses(it);
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        boolean _isEmpty = IterableExtensions.isEmpty(OMLUtilities.orderingKeys(it));
        return Boolean.valueOf((!_isEmpty));
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>concat(ListExtensions.<EPackage, Iterable<EClass>>map(ePackages, _function)), _function_1), _function_2);
      for(final EClass eClass : _sortBy) {
        _builder.append("  ");
        _builder.append("implicit def ");
        String _orderingClassName = OMLUtilities.orderingClassName(eClass);
        _builder.append(_orderingClassName, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(": scala.Ordering[");
        String _name = eClass.getName();
        _builder.append(_name, "  ");
        _builder.append("]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= new scala.Ordering[");
        String _name_1 = eClass.getName();
        _builder.append(_name_1, "  ");
        _builder.append("] {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append("def compare(x: ");
        String _name_2 = eClass.getName();
        _builder.append(_name_2, "  \t");
        _builder.append(", y: ");
        String _name_3 = eClass.getName();
        _builder.append(_name_3, "  \t");
        _builder.append(")");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append(": scala.Int");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append("= ");
        {
          Iterable<ETypedElement> _orderingKeys = OMLUtilities.orderingKeys(eClass);
          for(final ETypedElement keyFeature : _orderingKeys) {
            {
              Boolean _isClassFeature = OMLUtilities.isClassFeature(keyFeature);
              if ((_isClassFeature).booleanValue()) {
                String _orderingClassType = OMLUtilities.orderingClassType(keyFeature);
                _builder.append(_orderingClassType, "  \t");
                _builder.append(".compare(x.");
                String _name_4 = keyFeature.getName();
                _builder.append(_name_4, "  \t");
                _builder.append(",y.");
                String _name_5 = keyFeature.getName();
                _builder.append(_name_5, "  \t");
                _builder.append(")");
              } else {
                String _orderingAttributeType = OMLUtilities.orderingAttributeType(keyFeature);
                _builder.append(_orderingAttributeType, "  \t");
              }
            }
            _builder.append(" match {");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            _builder.append("\t");
            _builder.append(" \t");
            _builder.append("case c_");
            String _name_6 = keyFeature.getName();
            _builder.append(_name_6, "  \t \t");
            _builder.append(" if 0 != c_");
            String _name_7 = keyFeature.getName();
            _builder.append(_name_7, "  \t \t");
            _builder.append(" => c_");
            String _name_8 = keyFeature.getName();
            _builder.append(_name_8, "  \t \t");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            _builder.append("\t");
            _builder.append(" \t");
            _builder.append("case 0 => ");
          }
        }
        {
          Iterable<ETypedElement> _orderingKeys_1 = OMLUtilities.orderingKeys(eClass);
          boolean _hasElements = false;
          for(final ETypedElement keyFeature_1 : _orderingKeys_1) {
            if (!_hasElements) {
              _hasElements = true;
              _builder.append("0 }", "  \t");
            } else {
              _builder.appendImmediate(" }", "  \t");
            }
          }
        }
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
        _builder.append("  ");
        _builder.newLine();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String factoryPreamble(final EClass eClass) {
    String _xblockexpression = null;
    {
      String _xifexpression = null;
      Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
      if ((_isExtentContainer).booleanValue()) {
        _xifexpression = "( ";
      } else {
        _xifexpression = "( extent: Extent,\n ";
      }
      final String p1 = _xifexpression;
      String _xifexpression_1 = null;
      EStructuralFeature _lookupUUIDFeature = OMLUtilities.lookupUUIDFeature(eClass);
      boolean _tripleEquals = (null == _lookupUUIDFeature);
      if (_tripleEquals) {
        _xifexpression_1 = p1;
      } else {
        _xifexpression_1 = (p1 + " uuid: java.util.UUID,\n ");
      }
      final String p2 = _xifexpression_1;
      _xblockexpression = p2;
    }
    return _xblockexpression;
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
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("def create");
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
            _xifexpression = "( ";
          } else {
            _xifexpression = "( extent: Extent,\n ";
          }
          _builder.append(_xifexpression);
        } else {
          _builder.appendImmediate(",\n ", "");
        }
        _builder.append(" ");
        String _name_1 = attr.getName();
        _builder.append(_name_1);
        _builder.append(": ");
        String _queryResolverType = OMLUtilities.queryResolverType(attr, "");
        _builder.append(_queryResolverType);
      }
      if (_hasElements) {
        _builder.append(" )");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
      if ((_isExtentContainer_1).booleanValue()) {
        _builder.append(": ");
        String _name_2 = eClass.getName();
        _builder.append(_name_2);
      } else {
        _builder.append(": (Extent, ");
        String _name_3 = eClass.getName();
        _builder.append(_name_3);
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public String factoryMethodWithUUIDGenerator(final EClass eClass, final EStructuralFeature uuidNS, final Iterable<EStructuralFeature> uuidFactors) {
    String _xblockexpression = null;
    {
      String _xifexpression = null;
      boolean _and = false;
      if (!(null != uuidNS)) {
        _and = false;
      } else {
        EClass _EClassType = OMLUtilities.EClassType(uuidNS);
        EStructuralFeature _lookupUUIDFeature = null;
        if (_EClassType!=null) {
          _lookupUUIDFeature=OMLUtilities.lookupUUIDFeature(_EClassType);
        }
        boolean _tripleNotEquals = (null != _lookupUUIDFeature);
        _and = _tripleNotEquals;
      }
      if (_and) {
        _xifexpression = ".uuid";
      } else {
        _xifexpression = "";
      }
      final String uuidConv = _xifexpression;
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("def create");
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
              _xifexpression_1 = "( extent: Extent,\n ";
            }
            _builder.append(_xifexpression_1);
          } else {
            _builder.appendImmediate(",\n ", "");
          }
          _builder.append(" ");
          String _name_1 = attr.getName();
          _builder.append(_name_1);
          _builder.append(": ");
          String _queryResolverType = OMLUtilities.queryResolverType(attr, "");
          _builder.append(_queryResolverType);
        }
        if (_hasElements) {
          _builder.append(" )");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
        if ((_isExtentContainer_1).booleanValue()) {
          _builder.append(": ");
          String _name_2 = eClass.getName();
          _builder.append(_name_2);
        } else {
          _builder.append(": (Extent, ");
          String _name_3 = eClass.getName();
          _builder.append(_name_3);
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("= ");
      {
        boolean _isEmpty = IterableExtensions.isEmpty(uuidFactors);
        if (_isEmpty) {
          _builder.append("{");
        } else {
          _builder.append("{");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("// namespace uuid...");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("import scala.Predef.ArrowAssoc");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        if ((null != uuidNS)) {
          _builder.append("  ");
          _builder.append("val uuid = taggedTypes.");
          String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
          _builder.append(_lowerCaseInitialOrWord, "  ");
          _builder.append("UUID(namespaceUUID(");
          String _name_4 = uuidNS.getName();
          _builder.append(_name_4, "  ");
          _builder.append(uuidConv, "  ");
          _builder.append(".toString");
          {
            boolean _hasElements_1 = false;
            for(final EStructuralFeature f : uuidFactors) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
                _builder.append(", ", "  ");
              } else {
                _builder.appendImmediate(", ", "  ");
              }
              _builder.append(" \"");
              String _name_5 = f.getName();
              _builder.append(_name_5, "  ");
              _builder.append("\" -> ");
              String _name_6 = f.getName();
              _builder.append(_name_6, "  ");
            }
          }
          _builder.append("))");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("create");
          String _name_7 = eClass.getName();
          _builder.append(_name_7, "  ");
          _builder.append("( ");
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements_2 = false;
            for(final EStructuralFeature attr_1 : _sortedAttributeFactorySignature_1) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
                String _xifexpression_2 = null;
                Boolean _isExtentContainer_2 = OMLUtilities.isExtentContainer(eClass);
                if ((_isExtentContainer_2).booleanValue()) {
                  _xifexpression_2 = "uuid, ";
                } else {
                  _xifexpression_2 = "extent, uuid, ";
                }
                _builder.append(_xifexpression_2, "  ");
              } else {
                _builder.appendImmediate(", ", "  ");
              }
              String _name_8 = attr_1.getName();
              _builder.append(_name_8, "  ");
            }
          }
          _builder.append(" )");
          _builder.newLineIfNotEmpty();
        } else {
          _builder.append("  ");
          _builder.append("val uuid = taggedTypes.");
          String _lowerCaseInitialOrWord_1 = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
          _builder.append(_lowerCaseInitialOrWord_1, "  ");
          _builder.append("UUID(namespaceUUID(\"");
          String _name_9 = eClass.getName();
          _builder.append(_name_9, "  ");
          _builder.append("\"");
          {
            boolean _hasElements_3 = false;
            for(final EStructuralFeature f_1 : uuidFactors) {
              if (!_hasElements_3) {
                _hasElements_3 = true;
                _builder.append(", ", "  ");
              } else {
                _builder.appendImmediate(", ", "  ");
              }
              _builder.append(" \"");
              String _name_10 = f_1.getName();
              _builder.append(_name_10, "  ");
              _builder.append("\" -> ");
              String _name_11 = f_1.getName();
              _builder.append(_name_11, "  ");
            }
          }
          _builder.append("))");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("create");
          String _name_12 = eClass.getName();
          _builder.append(_name_12, "  ");
          _builder.append("( ");
          {
            Iterable<EStructuralFeature> _sortedAttributeFactorySignature_2 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
            boolean _hasElements_4 = false;
            for(final EStructuralFeature attr_2 : _sortedAttributeFactorySignature_2) {
              if (!_hasElements_4) {
                _hasElements_4 = true;
                String _xifexpression_3 = null;
                Boolean _isExtentContainer_3 = OMLUtilities.isExtentContainer(eClass);
                if ((_isExtentContainer_3).booleanValue()) {
                  _xifexpression_3 = "uuid, ";
                } else {
                  _xifexpression_3 = "extent, uuid, ";
                }
                _builder.append(_xifexpression_3, "  ");
              } else {
                _builder.appendImmediate(", ", "  ");
              }
              String _name_13 = attr_2.getName();
              _builder.append(_name_13, "  ");
            }
          }
          _builder.append(" )");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("def create");
      String _name_14 = eClass.getName();
      _builder.append(_name_14);
      _builder.newLineIfNotEmpty();
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature_3 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements_5 = false;
        for(final EStructuralFeature attr_3 : _sortedAttributeFactorySignature_3) {
          if (!_hasElements_5) {
            _hasElements_5 = true;
            String _xifexpression_4 = null;
            Boolean _isExtentContainer_4 = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer_4).booleanValue()) {
              String _name_15 = eClass.getName();
              String _plus = ("( uuid: taggedTypes." + _name_15);
              _xifexpression_4 = (_plus + "UUID,\n ");
            } else {
              String _name_16 = eClass.getName();
              String _plus_1 = ("( extent: Extent,\n  uuid: taggedTypes." + _name_16);
              _xifexpression_4 = (_plus_1 + "UUID,\n ");
            }
            _builder.append(_xifexpression_4);
          } else {
            _builder.appendImmediate(",\n ", "");
          }
          _builder.append(" ");
          String _name_17 = attr_3.getName();
          _builder.append(_name_17);
          _builder.append(": ");
          String _queryResolverType_1 = OMLUtilities.queryResolverType(attr_3, "");
          _builder.append(_queryResolverType_1);
        }
        if (_hasElements_5) {
          _builder.append(" )");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        Boolean _isExtentContainer_5 = OMLUtilities.isExtentContainer(eClass);
        if ((_isExtentContainer_5).booleanValue()) {
          _builder.append(": ");
          String _name_18 = eClass.getName();
          _builder.append(_name_18);
        } else {
          _builder.append(": (Extent, ");
          String _name_19 = eClass.getName();
          _builder.append(_name_19);
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithUUIDGenerator(final EClass eClass, final EStructuralFeature uuidNS, final String uuidScala) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("def create");
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
            _xifexpression = "( ";
          } else {
            _xifexpression = "( extent: Extent,\n ";
          }
          _builder.append(_xifexpression);
        } else {
          _builder.appendImmediate(",\n ", "");
        }
        _builder.append(" ");
        String _name_1 = attr.getName();
        _builder.append(_name_1);
        _builder.append(": ");
        String _queryResolverType = OMLUtilities.queryResolverType(attr, "");
        _builder.append(_queryResolverType);
      }
      if (_hasElements) {
        _builder.append(" )");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
      if ((_isExtentContainer_1).booleanValue()) {
        _builder.append(": ");
        String _name_2 = eClass.getName();
        _builder.append(_name_2);
      } else {
        _builder.append(": (Extent, ");
        String _name_3 = eClass.getName();
        _builder.append(_name_3);
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("= {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// custom uuid...");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("import scala.Predef.ArrowAssoc");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("val id = ");
    _builder.append(uuidScala, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("val uuid = taggedTypes.");
    String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
    _builder.append(_lowerCaseInitialOrWord, "  ");
    _builder.append("UUID(id)");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("create");
    String _name_4 = eClass.getName();
    _builder.append(_name_4, "  ");
    _builder.append("( ");
    {
      Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
      boolean _hasElements_1 = false;
      for(final EStructuralFeature attr_1 : _sortedAttributeFactorySignature_1) {
        if (!_hasElements_1) {
          _hasElements_1 = true;
          String _xifexpression_1 = null;
          Boolean _isExtentContainer_2 = OMLUtilities.isExtentContainer(eClass);
          if ((_isExtentContainer_2).booleanValue()) {
            _xifexpression_1 = "uuid, ";
          } else {
            _xifexpression_1 = "extent, uuid, ";
          }
          _builder.append(_xifexpression_1, "  ");
        } else {
          _builder.appendImmediate(", ", "  ");
        }
        String _name_5 = attr_1.getName();
        _builder.append(_name_5, "  ");
      }
    }
    _builder.append(" )");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("def create");
    String _name_6 = eClass.getName();
    _builder.append(_name_6);
    _builder.newLineIfNotEmpty();
    {
      Iterable<EStructuralFeature> _sortedAttributeFactorySignature_2 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
      boolean _hasElements_2 = false;
      for(final EStructuralFeature attr_2 : _sortedAttributeFactorySignature_2) {
        if (!_hasElements_2) {
          _hasElements_2 = true;
          String _xifexpression_2 = null;
          Boolean _isExtentContainer_3 = OMLUtilities.isExtentContainer(eClass);
          if ((_isExtentContainer_3).booleanValue()) {
            String _name_7 = eClass.getName();
            String _plus = ("( uuid: taggedTypes." + _name_7);
            _xifexpression_2 = (_plus + "UUID,\n ");
          } else {
            String _name_8 = eClass.getName();
            String _plus_1 = ("( extent: Extent,\n  uuid: taggedTypes." + _name_8);
            _xifexpression_2 = (_plus_1 + "UUID,\n ");
          }
          _builder.append(_xifexpression_2);
        } else {
          _builder.appendImmediate(",\n ", "");
        }
        _builder.append(" ");
        String _name_9 = attr_2.getName();
        _builder.append(_name_9);
        _builder.append(": ");
        String _queryResolverType_1 = OMLUtilities.queryResolverType(attr_2, "");
        _builder.append(_queryResolverType_1);
      }
      if (_hasElements_2) {
        _builder.append(" )");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      Boolean _isExtentContainer_4 = OMLUtilities.isExtentContainer(eClass);
      if ((_isExtentContainer_4).booleanValue()) {
        _builder.append(": ");
        String _name_10 = eClass.getName();
        _builder.append(_name_10);
      } else {
        _builder.append(": (Extent, ");
        String _name_11 = eClass.getName();
        _builder.append(_name_11);
        _builder.append(")");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public String factoryMethodWithDerivedUUID(final EClass eClass) {
    String _xblockexpression = null;
    {
      StringConcatenation _builder = new StringConcatenation();
      {
        final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
          return OMLUtilities.isEssential(it);
        };
        Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function);
        for(final EStructuralFeature attr : _filter) {
          {
            Boolean _isIRIReference = OMLUtilities.isIRIReference(attr);
            if ((_isIRIReference).booleanValue()) {
              _builder.append(" ++");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("Seq(\"");
              String _name = attr.getName();
              _builder.append(_name, "  ");
              _builder.append("\" -> namespaceUUID(");
              String _name_1 = attr.getName();
              _builder.append(_name_1, "  ");
              _builder.append(").toString)");
            } else {
              int _lowerBound = attr.getLowerBound();
              boolean _equals = (0 == _lowerBound);
              if (_equals) {
                _builder.append(" ++");
                _builder.newLineIfNotEmpty();
                _builder.append("  ");
                String _name_2 = attr.getName();
                _builder.append(_name_2, "  ");
                _builder.append(".map { vt => \"");
                String _name_3 = attr.getName();
                _builder.append(_name_3, "  ");
                _builder.append("\" -> vt.uuid.toString }");
              } else {
                Boolean _isUUIDFeature = OMLUtilities.isUUIDFeature(attr);
                if ((_isUUIDFeature).booleanValue()) {
                  _builder.append(" ++");
                  _builder.newLineIfNotEmpty();
                  _builder.append("    ");
                  _builder.append("Seq(\"");
                  String _name_4 = attr.getName();
                  _builder.append(_name_4, "    ");
                  _builder.append("\" -> ");
                  String _name_5 = attr.getName();
                  _builder.append(_name_5, "    ");
                  _builder.append(".uuid.toString)");
                } else {
                  _builder.append(" ++");
                  _builder.newLineIfNotEmpty();
                  _builder.append("    ");
                  _builder.append("Seq(\"");
                  String _name_6 = attr.getName();
                  _builder.append(_name_6, "    ");
                  _builder.append("\" -> ");
                  String _name_7 = attr.getName();
                  _builder.append(_name_7, "    ");
                  _builder.append(".value)");
                }
              }
            }
          }
        }
      }
      final String pairs = _builder.toString();
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("def create");
      String _name_8 = eClass.getName();
      _builder_1.append(_name_8);
      _builder_1.newLineIfNotEmpty();
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements = false;
        for(final EStructuralFeature attr_1 : _sortedAttributeFactorySignature) {
          if (!_hasElements) {
            _hasElements = true;
            String _xifexpression = null;
            Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer).booleanValue()) {
              _xifexpression = "( ";
            } else {
              _xifexpression = "( extent: Extent,\n ";
            }
            _builder_1.append(_xifexpression);
          } else {
            _builder_1.appendImmediate(",\n ", "");
          }
          _builder_1.append(" ");
          String _name_9 = attr_1.getName();
          _builder_1.append(_name_9);
          _builder_1.append(": ");
          String _queryResolverType = OMLUtilities.queryResolverType(attr_1, "");
          _builder_1.append(_queryResolverType);
        }
        if (_hasElements) {
          _builder_1.append(" )");
        }
      }
      _builder_1.newLineIfNotEmpty();
      {
        Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
        if ((_isExtentContainer_1).booleanValue()) {
          _builder_1.append(": ");
          String _name_10 = eClass.getName();
          _builder_1.append(_name_10);
        } else {
          _builder_1.append(": (Extent, ");
          String _name_11 = eClass.getName();
          _builder_1.append(_name_11);
          _builder_1.append(")");
        }
      }
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("= {");
      _builder_1.newLine();
      _builder_1.append("\t");
      _builder_1.append("// derived uuid...");
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("import scala.Predef.ArrowAssoc");
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("val uuid = taggedTypes.");
      String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
      _builder_1.append(_lowerCaseInitialOrWord, "  ");
      _builder_1.append("UUID(namespaceUUID(");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("    ");
      _builder_1.append("\"");
      String _name_12 = eClass.getName();
      _builder_1.append(_name_12, "    ");
      _builder_1.append("\",");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("    ");
      _builder_1.append("Seq.empty[(String, String)]");
      _builder_1.append(pairs, "    ");
      _builder_1.append(" : _*))");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("  ");
      _builder_1.append("create");
      String _name_13 = eClass.getName();
      _builder_1.append(_name_13, "  ");
      _builder_1.append("( ");
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements_1 = false;
        for(final EStructuralFeature attr_2 : _sortedAttributeFactorySignature_1) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            String _xifexpression_1 = null;
            Boolean _isExtentContainer_2 = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer_2).booleanValue()) {
              _xifexpression_1 = "uuid, ";
            } else {
              _xifexpression_1 = "extent, uuid, ";
            }
            _builder_1.append(_xifexpression_1, "  ");
          } else {
            _builder_1.appendImmediate(", ", "  ");
          }
          String _name_14 = attr_2.getName();
          _builder_1.append(_name_14, "  ");
        }
      }
      _builder_1.append(" )");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("}");
      _builder_1.newLine();
      _builder_1.newLine();
      _builder_1.append("def create");
      String _name_15 = eClass.getName();
      _builder_1.append(_name_15);
      _builder_1.newLineIfNotEmpty();
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature_2 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements_2 = false;
        for(final EStructuralFeature attr_3 : _sortedAttributeFactorySignature_2) {
          if (!_hasElements_2) {
            _hasElements_2 = true;
            String _xifexpression_2 = null;
            Boolean _isExtentContainer_3 = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer_3).booleanValue()) {
              String _name_16 = eClass.getName();
              String _plus = ("( uuid: taggedTypes." + _name_16);
              _xifexpression_2 = (_plus + "UUID,\n ");
            } else {
              String _name_17 = eClass.getName();
              String _plus_1 = ("( extent: Extent,\n  uuid: taggedTypes." + _name_17);
              _xifexpression_2 = (_plus_1 + "UUID,\n ");
            }
            _builder_1.append(_xifexpression_2);
          } else {
            _builder_1.appendImmediate(",\n ", "");
          }
          _builder_1.append(" ");
          String _name_18 = attr_3.getName();
          _builder_1.append(_name_18);
          _builder_1.append(": ");
          String _queryResolverType_1 = OMLUtilities.queryResolverType(attr_3, "");
          _builder_1.append(_queryResolverType_1);
        }
        if (_hasElements_2) {
          _builder_1.append(" )");
        }
      }
      _builder_1.newLineIfNotEmpty();
      {
        Boolean _isExtentContainer_4 = OMLUtilities.isExtentContainer(eClass);
        if ((_isExtentContainer_4).booleanValue()) {
          _builder_1.append(": ");
          String _name_19 = eClass.getName();
          _builder_1.append(_name_19);
        } else {
          _builder_1.append(": (Extent, ");
          String _name_20 = eClass.getName();
          _builder_1.append(_name_20);
          _builder_1.append(")");
        }
      }
      _builder_1.newLineIfNotEmpty();
      _xblockexpression = _builder_1.toString();
    }
    return _xblockexpression;
  }
  
  public String factoryMethodWithImplicitlyDerivedUUID(final EClass eClass) {
    String _xblockexpression = null;
    {
      StringConcatenation _builder = new StringConcatenation();
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        for(final EStructuralFeature attr : _sortedAttributeFactorySignature) {
          {
            Boolean _isIRIReference = OMLUtilities.isIRIReference(attr);
            if ((_isIRIReference).booleanValue()) {
              _builder.append(" ++");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("Seq(\"");
              String _name = attr.getName();
              _builder.append(_name, "  ");
              _builder.append("\" -> namespaceUUID(");
              String _name_1 = attr.getName();
              _builder.append(_name_1, "  ");
              _builder.append(").toString");
            } else {
              int _lowerBound = attr.getLowerBound();
              boolean _equals = (0 == _lowerBound);
              if (_equals) {
                _builder.append(" ++");
                _builder.newLineIfNotEmpty();
                _builder.append("  ");
                String _name_2 = attr.getName();
                _builder.append(_name_2, "  ");
                _builder.append(".map { vt => \"");
                String _name_3 = attr.getName();
                _builder.append(_name_3, "  ");
                _builder.append("\" -> vt.uuid.toString }");
              } else {
                Boolean _isUUIDFeature = OMLUtilities.isUUIDFeature(attr);
                if ((_isUUIDFeature).booleanValue()) {
                  _builder.append(" ++");
                  _builder.newLineIfNotEmpty();
                  _builder.append("    ");
                  _builder.append("Seq(\"");
                  String _name_4 = attr.getName();
                  _builder.append(_name_4, "    ");
                  _builder.append("\" -> ");
                  String _name_5 = attr.getName();
                  _builder.append(_name_5, "    ");
                  _builder.append(".uuid.toString)");
                } else {
                  _builder.append(" ++");
                  _builder.newLineIfNotEmpty();
                  _builder.append("    ");
                  _builder.append("Seq(\"");
                  String _name_6 = attr.getName();
                  _builder.append(_name_6, "    ");
                  _builder.append("\" -> ");
                  String _name_7 = attr.getName();
                  _builder.append(_name_7, "    ");
                  _builder.append(".toString)");
                }
              }
            }
          }
        }
      }
      final String pairs = _builder.toString();
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("def create");
      String _name_8 = eClass.getName();
      _builder_1.append(_name_8);
      _builder_1.newLineIfNotEmpty();
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature_1 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements = false;
        for(final EStructuralFeature attr_1 : _sortedAttributeFactorySignature_1) {
          if (!_hasElements) {
            _hasElements = true;
            String _xifexpression = null;
            Boolean _isExtentContainer = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer).booleanValue()) {
              _xifexpression = "( ";
            } else {
              _xifexpression = "( extent: Extent,\n ";
            }
            _builder_1.append(_xifexpression);
          } else {
            _builder_1.appendImmediate(",\n ", "");
          }
          _builder_1.append(" ");
          String _name_9 = attr_1.getName();
          _builder_1.append(_name_9);
          _builder_1.append(": ");
          String _queryResolverType = OMLUtilities.queryResolverType(attr_1, "");
          _builder_1.append(_queryResolverType);
        }
        if (_hasElements) {
          _builder_1.append(" )");
        }
      }
      _builder_1.newLineIfNotEmpty();
      {
        Boolean _isExtentContainer_1 = OMLUtilities.isExtentContainer(eClass);
        if ((_isExtentContainer_1).booleanValue()) {
          _builder_1.append(": ");
          String _name_10 = eClass.getName();
          _builder_1.append(_name_10);
        } else {
          _builder_1.append(": (Extent, ");
          String _name_11 = eClass.getName();
          _builder_1.append(_name_11);
          _builder_1.append(")");
        }
      }
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("= {");
      _builder_1.newLine();
      _builder_1.append("\t");
      _builder_1.append("// implicitly derived uuid...");
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("import scala.Predef.ArrowAssoc");
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("val implicitUUID = taggedTypes.");
      String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
      _builder_1.append(_lowerCaseInitialOrWord, "  ");
      _builder_1.append("UUID(namespaceUUID(");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("    ");
      _builder_1.append("\"");
      String _name_12 = eClass.getName();
      _builder_1.append(_name_12, "    ");
      _builder_1.append("\",");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("    ");
      _builder_1.append("Seq.empty[(String, String)]");
      _builder_1.append(pairs, "    ");
      _builder_1.append(" : _*))");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("  ");
      _builder_1.append("create");
      String _name_13 = eClass.getName();
      _builder_1.append(_name_13, "  ");
      _builder_1.append("( ");
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature_2 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements_1 = false;
        for(final EStructuralFeature attr_2 : _sortedAttributeFactorySignature_2) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            String _xifexpression_1 = null;
            Boolean _isExtentContainer_2 = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer_2).booleanValue()) {
              _xifexpression_1 = "implicitUUID, ";
            } else {
              _xifexpression_1 = "extent, implicitUUID, ";
            }
            _builder_1.append(_xifexpression_1, "  ");
          } else {
            _builder_1.appendImmediate(", ", "  ");
          }
          String _name_14 = attr_2.getName();
          _builder_1.append(_name_14, "  ");
        }
      }
      _builder_1.append(" )");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("}");
      _builder_1.newLine();
      _builder_1.newLine();
      _builder_1.append("def create");
      String _name_15 = eClass.getName();
      _builder_1.append(_name_15);
      _builder_1.newLineIfNotEmpty();
      {
        Iterable<EStructuralFeature> _sortedAttributeFactorySignature_3 = OMLUtilities.getSortedAttributeFactorySignature(eClass);
        boolean _hasElements_2 = false;
        for(final EStructuralFeature attr_3 : _sortedAttributeFactorySignature_3) {
          if (!_hasElements_2) {
            _hasElements_2 = true;
            String _xifexpression_2 = null;
            Boolean _isExtentContainer_3 = OMLUtilities.isExtentContainer(eClass);
            if ((_isExtentContainer_3).booleanValue()) {
              String _name_16 = eClass.getName();
              String _plus = ("( uuid: taggedTypes." + _name_16);
              _xifexpression_2 = (_plus + "UUID,\n ");
            } else {
              String _name_17 = eClass.getName();
              String _plus_1 = ("( extent: Extent,\n  uuid: taggedTypes." + _name_17);
              _xifexpression_2 = (_plus_1 + "UUID,\n ");
            }
            _builder_1.append(_xifexpression_2);
          } else {
            _builder_1.appendImmediate(",\n ", "");
          }
          _builder_1.append(" ");
          String _name_18 = attr_3.getName();
          _builder_1.append(_name_18);
          _builder_1.append(": ");
          String _queryResolverType_1 = OMLUtilities.queryResolverType(attr_3, "");
          _builder_1.append(_queryResolverType_1);
        }
        if (_hasElements_2) {
          _builder_1.append(" )");
        }
      }
      _builder_1.newLineIfNotEmpty();
      {
        Boolean _isExtentContainer_4 = OMLUtilities.isExtentContainer(eClass);
        if ((_isExtentContainer_4).booleanValue()) {
          _builder_1.append(": ");
          String _name_19 = eClass.getName();
          _builder_1.append(_name_19);
        } else {
          _builder_1.append(": (Extent, ");
          String _name_20 = eClass.getName();
          _builder_1.append(_name_20);
          _builder_1.append(")");
        }
      }
      _builder_1.newLineIfNotEmpty();
      _xblockexpression = _builder_1.toString();
    }
    return _xblockexpression;
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
    _builder.append("import gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator");
    _builder.newLine();
    _builder.append("import scala.collection.immutable.Seq");
    _builder.newLine();
    _builder.append("import scala.Predef.String");
    _builder.newLine();
    _builder.newLine();
    _builder.append("trait OMLResolvedFactory {");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("val oug: OMLUUIDGenerator");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("import oug._");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    {
      final Function1<EPackage, Iterable<EClass>> _function = (EPackage it) -> {
        return OMLUtilities.FunctionalAPIClasses(it);
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        boolean _isAbstract = it.isAbstract();
        return Boolean.valueOf((!_isAbstract));
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>concat(ListExtensions.<EPackage, Iterable<EClass>>map(ePackages, _function)), _function_1), _function_2);
      for(final EClass eClass : _sortBy) {
        _builder.append("  ");
        _builder.append("// ");
        String _name = eClass.getName();
        _builder.append(_name, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        String _factoryMethod = this.factoryMethod(eClass);
        _builder.append(_factoryMethod, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateExtentContainerClassFile(final EClass eClass, final Iterable<EClass> allEClasses) {
    String _xblockexpression = null;
    {
      final Function1<EClass, Boolean> _function = (EClass it) -> {
        return OMLUtilities.isExtentManaged(it);
      };
      final Iterable<EClass> extManaged = IterableExtensions.<EClass>filter(allEClasses, _function);
      final Function1<EClass, EList<EStructuralFeature>> _function_1 = (EClass it) -> {
        return it.getEStructuralFeatures();
      };
      final Function1<EStructuralFeature, Boolean> _function_2 = (EStructuralFeature it) -> {
        return Boolean.valueOf(((OMLUtilities.isContainment(it)).booleanValue() && (!(OMLUtilities.isLiteralFeature(it)).booleanValue())));
      };
      final Iterable<EStructuralFeature> containers = IterableExtensions.<EStructuralFeature>filter(Iterables.<EStructuralFeature>concat(IterableExtensions.<EClass, EList<EStructuralFeature>>map(allEClasses, _function_1)), _function_2);
      final Function1<EStructuralFeature, EClass> _function_3 = (EStructuralFeature it) -> {
        return OMLUtilities.EClassContainer(it);
      };
      final Function1<EClass, String> _function_4 = (EClass it) -> {
        return it.getName();
      };
      final List<EClass> containerTypes = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>toList(IterableExtensions.<EClass>toSet(IterableExtensions.<EStructuralFeature, EClass>map(containers, _function_3))), _function_4);
      final Function1<EStructuralFeature, EClassifier> _function_5 = (EStructuralFeature it) -> {
        return it.getEType();
      };
      final Function1<EClassifier, String> _function_6 = (EClassifier it) -> {
        return it.getName();
      };
      final List<EClassifier> containedTypes = IterableExtensions.<EClassifier, String>sortBy(IterableExtensions.<EClassifier>toList(IterableExtensions.<EClassifier>toSet(IterableExtensions.<EStructuralFeature, EClassifier>map(containers, _function_5))), _function_6);
      StringConcatenation _builder = new StringConcatenation();
      String _copyright = OMLUtilities.copyright();
      _builder.append(_copyright);
      _builder.newLineIfNotEmpty();
      _builder.append("package gov.nasa.jpl.imce.oml.resolver.api");
      _builder.newLine();
      _builder.newLine();
      _builder.append("import scala.collection.immutable.{Map, HashMap, Set}");
      _builder.newLine();
      _builder.append("import scala.Option");
      _builder.newLine();
      _builder.append(" ");
      _builder.newLine();
      {
        boolean _hasElements = false;
        for(final EClass ct : containerTypes) {
          if (!_hasElements) {
            _hasElements = true;
            _builder.append("// Container types:\n// - ");
          } else {
            _builder.appendImmediate("\n// - ", "");
          }
          String _name = ct.getName();
          _builder.append(_name);
          _builder.append(" (");
          String _name_1 = ct.getEPackage().getName();
          _builder.append(_name_1);
          _builder.append(")");
        }
        if (_hasElements) {
          _builder.append("\n");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        boolean _hasElements_1 = false;
        for(final EClassifier ct_1 : containedTypes) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            _builder.append("// Contained types:\n// - ");
          } else {
            _builder.appendImmediate("\n// - ", "");
          }
          String _name_2 = ct_1.getName();
          _builder.append(_name_2);
          _builder.append(" (");
          String _name_3 = ct_1.getEPackage().getName();
          _builder.append(_name_3);
          _builder.append(")");
        }
        if (_hasElements_1) {
          _builder.append("\n");
        }
      }
      _builder.newLineIfNotEmpty();
      String _doc = OMLUtilities.doc(eClass, "");
      _builder.append(_doc);
      _builder.append("case class ");
      String _name_4 = eClass.getName();
      _builder.append(_name_4);
      _builder.newLineIfNotEmpty();
      _builder.append("(");
      {
        final Function1<EClass, Boolean> _function_7 = (EClass it) -> {
          boolean _isAbstract = it.isAbstract();
          return Boolean.valueOf((!_isAbstract));
        };
        Iterable<EClass> _filter = IterableExtensions.<EClass>filter(extManaged, _function_7);
        boolean _hasElements_2 = false;
        for(final EClass em : _filter) {
          if (!_hasElements_2) {
            _hasElements_2 = true;
            _builder.append(" ");
          } else {
            _builder.appendImmediate(",\n  ", "");
          }
          String _tableVariableName = OMLUtilities.tableVariableName(em);
          _builder.append(_tableVariableName);
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": Map[taggedTypes.");
          String _name_5 = em.getName();
          _builder.append(_name_5, "  ");
          _builder.append("UUID, ");
          String _name_6 = em.getName();
          _builder.append(_name_6, "  ");
          _builder.append("] ");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= HashMap.empty[taggedTypes.");
          String _name_7 = em.getName();
          _builder.append(_name_7, "  ");
          _builder.append("UUID, ");
          String _name_8 = em.getName();
          _builder.append(_name_8, "  ");
          _builder.append("]");
        }
        if (_hasElements_2) {
          _builder.append(",\n");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        boolean _hasElements_3 = false;
        for(final EStructuralFeature c : containers) {
          if (!_hasElements_3) {
            _hasElements_3 = true;
            _builder.append("\n  ");
          } else {
            _builder.appendImmediate(",\n  ", "");
          }
          String _name_9 = c.getName();
          _builder.append(_name_9);
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": Map[");
          String _name_10 = OMLUtilities.EClassContainer(c).getName();
          _builder.append(_name_10, "  ");
          _builder.append(", ");
          {
            int _upperBound = c.getUpperBound();
            boolean _equals = (_upperBound == 1);
            if (_equals) {
              String _name_11 = c.getEType().getName();
              _builder.append(_name_11, "  ");
            } else {
              _builder.append("Set[");
              String _name_12 = c.getEType().getName();
              _builder.append(_name_12, "  ");
              _builder.append("]");
            }
          }
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= HashMap.empty[");
          String _name_13 = OMLUtilities.EClassContainer(c).getName();
          _builder.append(_name_13, "  ");
          _builder.append(", ");
          {
            int _upperBound_1 = c.getUpperBound();
            boolean _equals_1 = (_upperBound_1 == 1);
            if (_equals_1) {
              String _name_14 = c.getEType().getName();
              _builder.append(_name_14, "  ");
            } else {
              _builder.append("Set[");
              String _name_15 = c.getEType().getName();
              _builder.append(_name_15, "  ");
              _builder.append("]");
            }
          }
          _builder.append("]");
        }
        if (_hasElements_3) {
          _builder.append(",\n");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        boolean _hasElements_4 = false;
        for(final EStructuralFeature c_1 : containers) {
          if (!_hasElements_4) {
            _hasElements_4 = true;
            _builder.append("\n  ");
          } else {
            _builder.appendImmediate(",\n  ", "");
          }
          String _firstLower = StringExtensions.toFirstLower(OMLUtilities.EClassContainer(c_1).getName());
          _builder.append(_firstLower);
          _builder.append("Of");
          String _name_16 = c_1.getEType().getName();
          _builder.append(_name_16);
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": Map[");
          String _name_17 = c_1.getEType().getName();
          _builder.append(_name_17, "  ");
          _builder.append(", ");
          String _name_18 = OMLUtilities.EClassContainer(c_1).getName();
          _builder.append(_name_18, "  ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= HashMap.empty[");
          String _name_19 = c_1.getEType().getName();
          _builder.append(_name_19, "  ");
          _builder.append(", ");
          String _name_20 = OMLUtilities.EClassContainer(c_1).getName();
          _builder.append(_name_20, "  ");
          _builder.append("]");
        }
        if (_hasElements_4) {
          _builder.append(",\n");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        boolean _hasElements_5 = false;
        for(final EClassifier ct_2 : containedTypes) {
          if (!_hasElements_5) {
            _hasElements_5 = true;
            _builder.append("\n  ");
          } else {
            _builder.appendImmediate(",\n  ", "");
          }
          String _firstLower_1 = StringExtensions.toFirstLower(ct_2.getName());
          _builder.append(_firstLower_1);
          _builder.append("ByUUID");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": Map[taggedTypes.");
          String _name_21 = ct_2.getName();
          _builder.append(_name_21, "  ");
          _builder.append("UUID, ");
          String _name_22 = ct_2.getName();
          _builder.append(_name_22, "  ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= HashMap.empty[taggedTypes.");
          String _name_23 = ct_2.getName();
          _builder.append(_name_23, "  ");
          _builder.append("UUID, ");
          String _name_24 = ct_2.getName();
          _builder.append(_name_24, "  ");
          _builder.append("]");
        }
        if (_hasElements_5) {
          _builder.append("\n");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append(") {");
      _builder.newLine();
      {
        for(final EStructuralFeature c_2 : containers) {
          {
            int _upperBound_2 = c_2.getUpperBound();
            boolean _equals_2 = (_upperBound_2 == 1);
            if (_equals_2) {
              _builder.append("  ");
              _builder.append("def with");
              String _name_25 = c_2.getEType().getName();
              _builder.append(_name_25, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("(key: ");
              String _name_26 = OMLUtilities.EClassContainer(c_2).getName();
              _builder.append(_name_26, "  ");
              _builder.append(", value: ");
              String _name_27 = c_2.getEType().getName();
              _builder.append(_name_27, "  ");
              _builder.append(")");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": Map[");
              String _name_28 = OMLUtilities.EClassContainer(c_2).getName();
              _builder.append(_name_28, "  ");
              _builder.append(", ");
              String _name_29 = c_2.getEType().getName();
              _builder.append(_name_29, "  ");
              _builder.append("] ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= ");
              String _name_30 = c_2.getName();
              _builder.append(_name_30, "  ");
              _builder.append(".updated(key, value)");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.newLine();
            } else {
              _builder.append("  ");
              _builder.append("def with");
              String _name_31 = c_2.getEType().getName();
              _builder.append(_name_31, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("(key: ");
              String _name_32 = OMLUtilities.EClassContainer(c_2).getName();
              _builder.append(_name_32, "  ");
              _builder.append(", value: ");
              String _name_33 = c_2.getEType().getName();
              _builder.append(_name_33, "  ");
              _builder.append(")");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": Map[");
              String _name_34 = OMLUtilities.EClassContainer(c_2).getName();
              _builder.append(_name_34, "  ");
              _builder.append(", Set[");
              String _name_35 = c_2.getEType().getName();
              _builder.append(_name_35, "  ");
              _builder.append("]] ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= ");
              String _name_36 = c_2.getName();
              _builder.append(_name_36, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("  ");
              _builder.append(".updated(key, ");
              String _name_37 = c_2.getName();
              _builder.append(_name_37, "    ");
              _builder.append(".getOrElse(key, Set.empty[");
              String _name_38 = c_2.getEType().getName();
              _builder.append(_name_38, "    ");
              _builder.append("]) + value)");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.newLine();
            }
          }
        }
      }
      _builder.append("  ");
      _builder.append("def lookupModule");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(uuid: Option[taggedTypes.ModuleUUID])");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": Option[Module]");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= uuid.flatMap {");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("lookupModule");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def lookupModule");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(uuid: taggedTypes.ModuleUUID)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": Option[Module]");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= lookupTerminologyBox(uuid.asInstanceOf[taggedTypes.TerminologyBoxUUID]) orElse");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("lookupDescriptionBox(uuid.asInstanceOf[taggedTypes.DescriptionBoxUUID])");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def lookupTerminologyBox");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(uuid: Option[taggedTypes.TerminologyBoxUUID])");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": Option[TerminologyBox]");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= uuid.flatMap { ");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("lookupTerminologyBox");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def lookupTerminologyBox");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(uuid: taggedTypes.TerminologyBoxUUID)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": Option[TerminologyBox]");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= lookupTerminologyGraph(uuid.asInstanceOf[taggedTypes.TerminologyGraphUUID]) orElse ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("lookupBundle(uuid.asInstanceOf[taggedTypes.BundleUUID])");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_8 = (EClass it) -> {
          boolean _isAbstract = it.isAbstract();
          return Boolean.valueOf((!_isAbstract));
        };
        Iterable<EClass> _filter_1 = IterableExtensions.<EClass>filter(extManaged, _function_8);
        boolean _hasElements_6 = false;
        for(final EClass em_1 : _filter_1) {
          if (!_hasElements_6) {
            _hasElements_6 = true;
          } else {
            _builder.appendImmediate("\n  ", "  ");
          }
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("def lookup");
          String _name_39 = em_1.getName();
          _builder.append(_name_39, "  ");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("(uuid: Option[taggedTypes.");
          String _name_40 = em_1.getName();
          _builder.append(_name_40, "  ");
          _builder.append("UUID])");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": Option[");
          String _name_41 = em_1.getName();
          _builder.append(_name_41, "  ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= uuid.flatMap {");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("lookup");
          String _name_42 = em_1.getName();
          _builder.append(_name_42, "    ");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("} ");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("def lookup");
          String _name_43 = em_1.getName();
          _builder.append(_name_43, "  ");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("(uuid: taggedTypes.");
          String _name_44 = em_1.getName();
          _builder.append(_name_44, "  ");
          _builder.append("UUID)");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": Option[");
          String _name_45 = em_1.getName();
          _builder.append(_name_45, "  ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= ");
          String _tableVariableName_1 = OMLUtilities.tableVariableName(em_1);
          _builder.append(_tableVariableName_1, "  ");
          _builder.append(".get(uuid)");
          _builder.newLineIfNotEmpty();
        }
        if (_hasElements_6) {
          _builder.append("\n", "  ");
        }
      }
      {
        boolean _hasElements_7 = false;
        for(final EStructuralFeature c_3 : containers) {
          if (!_hasElements_7) {
            _hasElements_7 = true;
          } else {
            _builder.appendImmediate("\n  ", "  ");
          }
          {
            int _upperBound_3 = c_3.getUpperBound();
            boolean _equals_3 = (_upperBound_3 == 1);
            if (_equals_3) {
              _builder.append("  ");
              _builder.append("def lookup");
              String _firstUpper = StringExtensions.toFirstUpper(c_3.getName());
              _builder.append(_firstUpper, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("(key: Option[");
              String _name_46 = OMLUtilities.EClassContainer(c_3).getName();
              _builder.append(_name_46, "  ");
              _builder.append("])");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": Option[");
              String _name_47 = c_3.getEType().getName();
              _builder.append(_name_47, "  ");
              _builder.append("]");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= key.flatMap { lookup");
              String _firstUpper_1 = StringExtensions.toFirstUpper(c_3.getName());
              _builder.append(_firstUpper_1, "  ");
              _builder.append(" }");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("def lookup");
              String _firstUpper_2 = StringExtensions.toFirstUpper(c_3.getName());
              _builder.append(_firstUpper_2, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("(key: ");
              String _name_48 = OMLUtilities.EClassContainer(c_3).getName();
              _builder.append(_name_48, "  ");
              _builder.append(")");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": Option[");
              String _name_49 = c_3.getEType().getName();
              _builder.append(_name_49, "  ");
              _builder.append("]");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= ");
              String _name_50 = c_3.getName();
              _builder.append(_name_50, "  ");
              _builder.append(".get(key)");
              _builder.newLineIfNotEmpty();
              {
                if (((!Objects.equal(c_3.getEType().getName(), "Annotation")) && (!Objects.equal(c_3.getEType(), OMLUtilities.EClassContainer(c_3))))) {
                  _builder.append("  ");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.append("def lookup");
                  String _name_51 = c_3.getEType().getName();
                  _builder.append(_name_51, "  ");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("(uuid: Option[taggedTypes.");
                  String _name_52 = c_3.getEType().getName();
                  _builder.append(_name_52, "  ");
                  _builder.append("UUID])");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append(": Option[");
                  String _name_53 = c_3.getEType().getName();
                  _builder.append(_name_53, "  ");
                  _builder.append("]");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("= uuid.flatMap {");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.append("  ");
                  _builder.append("lookup");
                  String _name_54 = c_3.getEType().getName();
                  _builder.append(_name_54, "    ");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("}");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.append("def lookup");
                  String _name_55 = c_3.getEType().getName();
                  _builder.append(_name_55, "  ");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("(uuid: taggedTypes.");
                  String _name_56 = c_3.getEType().getName();
                  _builder.append(_name_56, "  ");
                  _builder.append("UUID)");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append(": Option[");
                  String _name_57 = c_3.getEType().getName();
                  _builder.append(_name_57, "  ");
                  _builder.append("]");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("= ");
                  String _firstLower_2 = StringExtensions.toFirstLower(c_3.getEType().getName());
                  _builder.append(_firstLower_2, "  ");
                  _builder.append("ByUUID.get(uuid)");
                  _builder.newLineIfNotEmpty();
                }
              }
            } else {
              _builder.append("  ");
              _builder.append("def lookup");
              String _firstUpper_3 = StringExtensions.toFirstUpper(c_3.getName());
              _builder.append(_firstUpper_3, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("(key: Option[");
              String _name_58 = OMLUtilities.EClassContainer(c_3).getName();
              _builder.append(_name_58, "  ");
              _builder.append("])");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": Set[");
              String _name_59 = c_3.getEType().getName();
              _builder.append(_name_59, "  ");
              _builder.append("]");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= key");
              _builder.newLine();
              _builder.append("  ");
              _builder.append(".fold[Set[");
              String _name_60 = c_3.getEType().getName();
              _builder.append(_name_60, "  ");
              _builder.append("]] { ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("Set.empty[");
              String _name_61 = c_3.getEType().getName();
              _builder.append(_name_61, "  \t");
              _builder.append("] ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("}{ lookup");
              String _firstUpper_4 = StringExtensions.toFirstUpper(c_3.getName());
              _builder.append(_firstUpper_4, "  ");
              _builder.append(" }");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("def lookup");
              String _firstUpper_5 = StringExtensions.toFirstUpper(c_3.getName());
              _builder.append(_firstUpper_5, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("(key: ");
              String _name_62 = OMLUtilities.EClassContainer(c_3).getName();
              _builder.append(_name_62, "  ");
              _builder.append(")");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": Set[");
              String _name_63 = c_3.getEType().getName();
              _builder.append(_name_63, "  ");
              _builder.append("]");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= ");
              String _name_64 = c_3.getName();
              _builder.append(_name_64, "  ");
              _builder.append(".getOrElse(key, Set.empty[");
              String _name_65 = c_3.getEType().getName();
              _builder.append(_name_65, "  ");
              _builder.append("])");
              _builder.newLineIfNotEmpty();
              {
                String _name_66 = c_3.getEType().getName();
                boolean _notEquals = (!Objects.equal(_name_66, "Annotation"));
                if (_notEquals) {
                  _builder.append("  ");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.append("def lookup");
                  String _name_67 = c_3.getEType().getName();
                  _builder.append(_name_67, "  ");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("(uuid: Option[taggedTypes.");
                  String _name_68 = c_3.getEType().getName();
                  _builder.append(_name_68, "  ");
                  _builder.append("UUID])");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append(": Option[");
                  String _name_69 = c_3.getEType().getName();
                  _builder.append(_name_69, "  ");
                  _builder.append("]");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("= uuid.flatMap {");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.append("  ");
                  _builder.append("lookup");
                  String _name_70 = c_3.getEType().getName();
                  _builder.append(_name_70, "    ");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("}");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.append("def lookup");
                  String _name_71 = c_3.getEType().getName();
                  _builder.append(_name_71, "  ");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("(uuid: taggedTypes.");
                  String _name_72 = c_3.getEType().getName();
                  _builder.append(_name_72, "  ");
                  _builder.append("UUID)");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append(": Option[");
                  String _name_73 = c_3.getEType().getName();
                  _builder.append(_name_73, "  ");
                  _builder.append("]");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("= ");
                  String _firstLower_3 = StringExtensions.toFirstLower(c_3.getEType().getName());
                  _builder.append(_firstLower_3, "  ");
                  _builder.append("ByUUID.get(uuid)");
                  _builder.newLineIfNotEmpty();
                }
              }
            }
          }
        }
        if (_hasElements_7) {
          _builder.append("\n", "  ");
        }
      }
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def lookupLogicalElement(uuid: taggedTypes.LogicalElementUUID)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": Option[LogicalElement]");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= lookupModule(uuid.asInstanceOf[taggedTypes.ModuleUUID])");
      {
        final Function1<EStructuralFeature, Boolean> _function_9 = (EStructuralFeature it) -> {
          String _name_74 = it.getName();
          return Boolean.valueOf((!Objects.equal(_name_74, "annotations")));
        };
        Iterable<EStructuralFeature> _filter_2 = IterableExtensions.<EStructuralFeature>filter(containers, _function_9);
        boolean _hasElements_8 = false;
        for(final EStructuralFeature c_4 : _filter_2) {
          if (!_hasElements_8) {
            _hasElements_8 = true;
            _builder.append(" orElse\n  ", "  ");
          } else {
            _builder.appendImmediate(" orElse\n  ", "  ");
          }
          _builder.append("lookup");
          String _name_74 = c_4.getEType().getName();
          _builder.append(_name_74, "  ");
          _builder.append("(uuid.asInstanceOf[taggedTypes.");
          String _name_75 = c_4.getEType().getName();
          _builder.append(_name_75, "  ");
          _builder.append("UUID])");
        }
        if (_hasElements_8) {
          _builder.append("\n", "  ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public String generateClassFile(final EClass eClass) {
    String _xblockexpression = null;
    {
      final Iterable<EStructuralFeature> apiStructuralFeatures = OMLUtilities.APIStructuralFeatures(eClass);
      final Iterable<EOperation> apiOperations = OMLUtilities.APIOperations(eClass);
      final Function1<EOperation, Boolean> _function = (EOperation it) -> {
        return OMLUtilities.isImplicitExtent(it);
      };
      final Iterable<EOperation> apiExtentOperations = IterableExtensions.<EOperation>filter(apiOperations, _function);
      final boolean hasUUID = (IterableExtensions.<EStructuralFeature>exists(apiStructuralFeatures, ((Function1<EStructuralFeature, Boolean>) (EStructuralFeature f) -> {
        String _name = f.getName();
        return Boolean.valueOf(Objects.equal(_name, "uuid"));
      })) || IterableExtensions.<EOperation>exists(apiOperations, ((Function1<EOperation, Boolean>) (EOperation op) -> {
        String _name = op.getName();
        return Boolean.valueOf(Objects.equal(_name, "uuid"));
      })));
      StringConcatenation _builder = new StringConcatenation();
      String _copyright = OMLUtilities.copyright();
      _builder.append(_copyright);
      _builder.newLineIfNotEmpty();
      _builder.append("package gov.nasa.jpl.imce.oml.resolver.api");
      _builder.newLine();
      _builder.newLine();
      String _doc = OMLUtilities.doc(eClass, "");
      _builder.append(_doc);
      String _traitDeclaration = OMLSpecificationResolverAPIGenerator.traitDeclaration(eClass);
      _builder.append(_traitDeclaration);
      _builder.newLineIfNotEmpty();
      {
        if ((hasUUID || eClass.getESuperTypes().isEmpty())) {
          _builder.append("{");
        } else {
          _builder.append("{");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("override val uuid: taggedTypes.");
          String _name = eClass.getName();
          String _plus = (_name + "UUID");
          _builder.append(_plus, "  ");
          _builder.newLineIfNotEmpty();
        }
      }
      {
        boolean _hasElements = false;
        for(final EStructuralFeature f : apiStructuralFeatures) {
          if (!_hasElements) {
            _hasElements = true;
            _builder.append("\n  ");
          } else {
            _builder.appendImmediate("\n  ", "");
          }
          String _doc_1 = OMLUtilities.doc(f, "  ");
          _builder.append(_doc_1);
          {
            Boolean _isOverride = OMLUtilities.isOverride(f);
            if ((_isOverride).booleanValue()) {
              _builder.append("override ");
            }
          }
          _builder.append("val ");
          String _name_1 = f.getName();
          _builder.append(_name_1);
          _builder.append(": ");
          {
            String _name_2 = f.getName();
            boolean _equals = Objects.equal(_name_2, "uuid");
            if (_equals) {
              _builder.append("taggedTypes.");
              String _name_3 = eClass.getName();
              String _plus_1 = (_name_3 + "UUID");
              _builder.append(_plus_1);
            } else {
              String _queryResolverType = OMLUtilities.queryResolverType(f, "");
              _builder.append(_queryResolverType);
            }
          }
        }
        if (_hasElements) {
          _builder.append("\n");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        boolean _hasElements_1 = false;
        for(final EOperation op : apiOperations) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            _builder.append("\n  ");
          } else {
            _builder.appendImmediate("\n  ", "");
          }
          String _doc_2 = OMLUtilities.doc(op, "  ");
          _builder.append(_doc_2);
          String _queryResolverName = OMLUtilities.queryResolverName(op, "");
          _builder.append(_queryResolverName);
          _builder.append(": ");
          {
            String _name_4 = op.getName();
            boolean _equals_1 = Objects.equal(_name_4, "uuid");
            if (_equals_1) {
              _builder.append("taggedTypes.");
              String _name_5 = eClass.getName();
              String _plus_2 = (_name_5 + "UUID");
              _builder.append(_plus_2);
            } else {
              String _queryResolverType_1 = OMLUtilities.queryResolverType(op, "");
              _builder.append(_queryResolverType_1);
            }
          }
        }
        if (_hasElements_1) {
          _builder.append("\n");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        Boolean _isRootHierarchyClass = OMLUtilities.isRootHierarchyClass(eClass);
        if ((_isRootHierarchyClass).booleanValue()) {
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("val vertexId: scala.Long = uuid.toString.hashCode.toLong");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("def canEqual(that: scala.Any): scala.Boolean");
          _builder.newLine();
        }
      }
      _builder.append("}");
      _builder.newLine();
      {
        boolean _isEmpty = IterableExtensions.isEmpty(apiExtentOperations);
        boolean _not = (!_isEmpty);
        if (_not) {
          _builder.newLine();
          _builder.append("object ");
          String _name_6 = eClass.getName();
          _builder.append(_name_6);
          _builder.append(" {");
          _builder.newLineIfNotEmpty();
          _builder.newLine();
          {
            for(final EOperation op_1 : apiExtentOperations) {
              _builder.append("  def ");
              String _name_7 = op_1.getName();
              _builder.append(_name_7);
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("(");
              char _charAt = eClass.getName().toLowerCase().charAt(0);
              _builder.append(_charAt, "  ");
              _builder.append(": ");
              String _name_8 = eClass.getName();
              _builder.append(_name_8, "  ");
              _builder.append(", ext: Extent)");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": ");
              String _queryResolverType_2 = OMLUtilities.queryResolverType(op_1, "");
              _builder.append(_queryResolverType_2, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= ");
              char _charAt_1 = eClass.getName().toLowerCase().charAt(0);
              _builder.append(_charAt_1, "  ");
              _builder.append(".");
              String _name_9 = op_1.getName();
              _builder.append(_name_9, "  ");
              _builder.append("()(ext)");
              _builder.newLineIfNotEmpty();
              _builder.newLine();
            }
          }
          _builder.append("}");
          _builder.newLine();
        }
      }
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public static String traitDeclaration(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("trait ");
    String _name = eClass.getName();
    _builder.append(_name);
    _builder.newLineIfNotEmpty();
    {
      EList<EClass> _eSuperTypes = eClass.getESuperTypes();
      boolean _hasElements = false;
      for(final EClass parent : _eSuperTypes) {
        if (!_hasElements) {
          _hasElements = true;
          _builder.append("  extends ");
        } else {
          _builder.appendImmediate("\n  with ", "");
        }
        String _name_1 = parent.getName();
        _builder.append(_name_1);
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
}
