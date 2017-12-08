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
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class OMLSpecificationFramelessGenerator extends OMLUtilities {
  public static void main(final String[] args) {
    int _length = args.length;
    boolean _notEquals = (1 != _length);
    if (_notEquals) {
      System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.frameless project");
      System.exit(1);
    }
    final OMLSpecificationFramelessGenerator gen = new OMLSpecificationFramelessGenerator();
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
    final List<EPackage> ePackages = Collections.<EPackage>unmodifiableList(CollectionLiterals.<EPackage>newArrayList(this.c, this.t, this.g, this.b, this.d));
    final String packageQName = "gov.nasa.jpl.imce.oml.frameless";
    final Path bundlePath = Paths.get(targetDir);
    final Path oml_Folder = bundlePath.resolve("src/main/scala/gov/nasa/jpl/imce/oml/frameless");
    oml_Folder.toFile().mkdirs();
    final Path api_Folder = bundlePath.resolve("src/main/scala/gov/nasa/jpl/imce/oml/frameless/api");
    api_Folder.toFile().mkdirs();
    this.generate(ePackages, 
      oml_Folder.toAbsolutePath().toString(), packageQName, 
      "OMLSpecificationTypedDatasets");
  }
  
  public void generate(final List<EPackage> ePackages, final String targetFolder, final String packageQName, final String tableName) {
    try {
      final File pfile = new File((((targetFolder + File.separator) + tableName) + ".scala"));
      boolean _exists = pfile.exists();
      if (_exists) {
        pfile.delete();
      }
      final FileOutputStream packageFile = new FileOutputStream(pfile);
      try {
        packageFile.write(this.generatePackageFile(ePackages, packageQName, tableName).getBytes());
      } finally {
        packageFile.close();
      }
      final File cfile = new File(((targetFolder + File.separator) + "OMLCatalystCasts.scala"));
      boolean _exists_1 = cfile.exists();
      if (_exists_1) {
        cfile.delete();
      }
      final FileOutputStream castFile = new FileOutputStream(cfile);
      try {
        castFile.write(this.generateCatalystCastsFile(ePackages, packageQName).getBytes());
      } finally {
        castFile.close();
      }
      final File sfile = new File(((targetFolder + File.separator) + "OMLProjections.scala"));
      boolean _exists_2 = sfile.exists();
      if (_exists_2) {
        sfile.delete();
      }
      final FileOutputStream smartProjectFile = new FileOutputStream(sfile);
      try {
        smartProjectFile.write(this.generateSmartProjectsFile(ePackages, packageQName).getBytes());
      } finally {
        smartProjectFile.close();
      }
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      Iterable<EClass> _filter = Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class);
      for (final EClass eClass : _filter) {
        if (((!eClass.getName().startsWith("Literal")) && (!Objects.equal(eClass.getName(), "Extent")))) {
          String _name = eClass.getName();
          String _plus = ((((targetFolder + File.separator) + "api") + File.separator) + _name);
          String _plus_1 = (_plus + ".scala");
          File _file = new File(_plus_1);
          final FileOutputStream projectFile = new FileOutputStream(_file);
          try {
            projectFile.write(this.generateProjectionFile(eClass, packageQName).getBytes());
          } finally {
            projectFile.close();
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public String generateProjectionFile(final EClass eClass, final String packageQName) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.append(" ");
    _builder.newLine();
    _builder.append("package ");
    _builder.append(packageQName);
    _builder.append(".api");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.tables._");
    _builder.newLine();
    _builder.newLine();
    _builder.append("case class ");
    String _name = eClass.getName();
    _builder.append(_name);
    _builder.append("(");
    _builder.newLineIfNotEmpty();
    {
      Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
      boolean _hasElements = false;
      for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",\n  ", "  ");
        }
        _builder.append("  ");
        String _columnName = OMLUtilities.columnName(attr);
        _builder.append(_columnName, "  ");
        _builder.append(": ");
        String _constructorTypeRef = OMLUtilities.constructorTypeRef(eClass, attr);
        _builder.append(_constructorTypeRef, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append(")");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateCatalystCastsFile(final List<EPackage> ePackages, final String packageQName) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.append(" ");
    _builder.newLine();
    _builder.append("package ");
    _builder.append(packageQName);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import frameless.CatalystCast");
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.tables.taggedTypes");
    _builder.newLine();
    _builder.append("import scala.Any");
    _builder.newLine();
    _builder.newLine();
    _builder.append("object OMLCatalystCasts {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("val theInstance = new CatalystCast[Any, Any] {}");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return Boolean.valueOf((((!IterableExtensions.isNullOrEmpty(OMLUtilities.ESuperClasses(it))) && (!it.getName().startsWith("Literal"))) && (!Objects.equal(it.getName(), "Extent"))));
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1), _function_2);
      for(final EClass eClass : _sortBy) {
        _builder.append("\t");
        _builder.append("// ");
        int _size = IterableExtensions.size(OMLUtilities.ESuperClasses(eClass));
        _builder.append(_size, "\t");
        _builder.append(" casts for ");
        String _name = eClass.getName();
        _builder.append(_name, "\t");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.newLine();
        {
          final Function1<EClass, String> _function_3 = (EClass it) -> {
            return it.getName();
          };
          List<EClass> _sortBy_1 = IterableExtensions.<EClass, String>sortBy(OMLUtilities.ESuperClasses(eClass), _function_3);
          for(final EClass eSup : _sortBy_1) {
            _builder.append("\t");
            _builder.append("implicit val ");
            String _name_1 = eClass.getName();
            _builder.append(_name_1, "\t");
            _builder.append("2");
            String _name_2 = eSup.getName();
            _builder.append(_name_2, "\t");
            _builder.append("UUIDCast");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append(": CatalystCast");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("  ");
            _builder.append("[ taggedTypes.");
            String _name_3 = eClass.getName();
            _builder.append(_name_3, "\t  ");
            _builder.append("UUID,");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("    ");
            _builder.append("taggedTypes.");
            String _name_4 = eSup.getName();
            _builder.append(_name_4, "\t    ");
            _builder.append("UUID]");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("= theInstance.asInstanceOf[CatalystCast");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("  ");
            _builder.append("[ taggedTypes.");
            String _name_5 = eClass.getName();
            _builder.append(_name_5, "\t  ");
            _builder.append("UUID,");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("    ");
            _builder.append("taggedTypes.");
            String _name_6 = eSup.getName();
            _builder.append(_name_6, "\t    ");
            _builder.append("UUID]]");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.newLine();
          }
        }
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateSmartProjectsFile(final List<EPackage> ePackages, final String packageQName) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.append(" ");
    _builder.newLine();
    _builder.append("package ");
    _builder.append(packageQName);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import frameless.{TypedColumn,TypedDataset}");
    _builder.newLine();
    _builder.append("import frameless.ops.SmartProject");
    _builder.newLine();
    _builder.append("import shapeless.HNil");
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.tables.TerminologyKind");
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.tables.taggedTypes");
    _builder.newLine();
    _builder.newLine();
    _builder.append("object OMLProjections {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("import OMLSpecificationTypedDatasets._");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("import OMLCatalystCasts._");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return Boolean.valueOf((((!IterableExtensions.isNullOrEmpty(OMLUtilities.ESuperClasses(it))) && (!it.getName().startsWith("Literal"))) && (!Objects.equal(it.getName(), "Extent"))));
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1), _function_2);
      for(final EClass eClass : _sortBy) {
        _builder.append("\t");
        _builder.append("// ");
        int _size = IterableExtensions.size(OMLUtilities.ESuperClasses(eClass));
        _builder.append(_size, "\t");
        _builder.append(" smart projects for api.");
        String _name = eClass.getName();
        _builder.append(_name, "\t");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.newLine();
        {
          final Function1<EClass, Boolean> _function_3 = (EClass it) -> {
            int _size_1 = IterableExtensions.size(OMLUtilities.schemaAPIOrOrderingKeyAttributes(it));
            return Boolean.valueOf((_size_1 > 1));
          };
          final Function1<EClass, String> _function_4 = (EClass it) -> {
            return it.getName();
          };
          List<EClass> _sortBy_1 = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(OMLUtilities.ESuperClasses(eClass), _function_3), _function_4);
          for(final EClass eSup : _sortBy_1) {
            _builder.append("\t");
            _builder.append("implicit val ");
            String _name_1 = eClass.getName();
            _builder.append(_name_1, "\t");
            _builder.append("2");
            String _name_2 = eSup.getName();
            _builder.append(_name_2, "\t");
            _builder.append("Projection");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append(": SmartProject");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("  ");
            _builder.append("[ api.");
            String _name_3 = eClass.getName();
            _builder.append(_name_3, "\t  ");
            _builder.append(",");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("    ");
            _builder.append("api.");
            String _name_4 = eSup.getName();
            _builder.append(_name_4, "\t    ");
            _builder.append("]");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("= SmartProject");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("  ");
            _builder.append("[ api.");
            String _name_5 = eClass.getName();
            _builder.append(_name_5, "\t  ");
            _builder.append(",");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("    ");
            _builder.append("api.");
            String _name_6 = eSup.getName();
            _builder.append(_name_6, "\t    ");
            _builder.append("](");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("    ");
            {
              Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eSup);
              boolean _hasElements = false;
              for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
                if (!_hasElements) {
                  _hasElements = true;
                  String _name_7 = eClass.getName();
                  String _plus = ("  (x: TypedDataset[api." + _name_7);
                  String _plus_1 = (_plus + "]) => {\n    ");
                  _builder.append(_plus_1, "\t    ");
                } else {
                  _builder.appendImmediate("\n\n    ", "\t    ");
                }
                {
                  String _name_8 = attr.getName();
                  boolean _equals = Objects.equal(_name_8, "uuid");
                  if (_equals) {
                    _builder.append("val x_uuid: TypedColumn[api.");
                    String _name_9 = eClass.getName();
                    _builder.append(_name_9, "\t    ");
                    _builder.append(", taggedTypes.");
                    String _name_10 = eSup.getName();
                    _builder.append(_name_10, "\t    ");
                    _builder.append("UUID]");
                    _builder.newLineIfNotEmpty();
                    _builder.append("\t");
                    _builder.append("    ");
                    _builder.append("    ");
                    _builder.append("= x.col[taggedTypes.");
                    String _name_11 = eClass.getName();
                    _builder.append(_name_11, "\t        ");
                    _builder.append("UUID](\'uuid).cast[taggedTypes.");
                    String _name_12 = eSup.getName();
                    _builder.append(_name_12, "\t        ");
                    _builder.append("UUID]");
                  } else {
                    _builder.append("val x_");
                    String _columnName = OMLUtilities.columnName(attr);
                    _builder.append(_columnName, "\t    ");
                    _builder.append(": TypedColumn[api.");
                    String _name_13 = eClass.getName();
                    _builder.append(_name_13, "\t    ");
                    _builder.append(", ");
                    String _constructorTypeRef = OMLUtilities.constructorTypeRef(eClass, attr);
                    _builder.append(_constructorTypeRef, "\t    ");
                    _builder.append("]");
                    _builder.newLineIfNotEmpty();
                    _builder.append("\t");
                    _builder.append("    ");
                    _builder.append("    ");
                    _builder.append("= x.col[");
                    String _constructorTypeRef_1 = OMLUtilities.constructorTypeRef(eClass, attr);
                    _builder.append(_constructorTypeRef_1, "\t        ");
                    _builder.append("](\'");
                    String _columnName_1 = OMLUtilities.columnName(attr);
                    _builder.append(_columnName_1, "\t        ");
                    _builder.append(")");
                  }
                }
              }
              if (_hasElements) {
                _builder.append("\n\n", "\t    ");
              }
            }
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("        ");
            _builder.append("val result");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("        ");
            _builder.append(": TypedDataset[api.");
            String _name_14 = eSup.getName();
            _builder.append(_name_14, "\t        ");
            _builder.append("]");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("        ");
            _builder.append("= x");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("          ");
            _builder.append(".selectMany");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("          ");
            _builder.append(".applyProduct(");
            {
              Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_1 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eSup);
              boolean _hasElements_1 = false;
              for(final ETypedElement attr_1 : _schemaAPIOrOrderingKeyAttributes_1) {
                if (!_hasElements_1) {
                  _hasElements_1 = true;
                  _builder.append("\n  ", "\t          ");
                } else {
                  _builder.appendImmediate(" :: \n  ", "\t          ");
                }
                _builder.append("x_");
                String _columnName_2 = OMLUtilities.columnName(attr_1);
                _builder.append(_columnName_2, "\t          ");
              }
              if (_hasElements_1) {
                _builder.append(" ::\n  HNil)", "\t          ");
              }
            }
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("          ");
            _builder.append(".as[api.");
            String _name_15 = eSup.getName();
            _builder.append(_name_15, "\t          ");
            _builder.append("]");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("        ");
            _builder.append("result");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("      ");
            _builder.append("})");
            _builder.newLine();
            _builder.newLine();
          }
        }
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generatePackageFile(final List<EPackage> ePackages, final String packageQName, final String tableName) {
    String _xblockexpression = null;
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return Boolean.valueOf((((OMLUtilities.isFunctionalAPI(it)).booleanValue() && (!it.isInterface())) && (!(OMLUtilities.isValueTable(it)).booleanValue())));
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      final List<EClass> eClasses = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1), _function_2);
      StringConcatenation _builder = new StringConcatenation();
      String _copyright = OMLUtilities.copyright();
      _builder.append(_copyright);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("package ");
      _builder.append(packageQName);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("import ammonite.ops.Path");
      _builder.newLine();
      _builder.newLine();
      _builder.append("import frameless.{Injection, TypedDataset}");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.covariantTag");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.covariantTag.@@");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.tables");
      _builder.newLine();
      _builder.append("import org.apache.spark.sql.SQLContext");
      _builder.newLine();
      _builder.append("import org.apache.spark.sql.SparkSession");
      _builder.newLine();
      _builder.newLine();
      _builder.append("import scala.collection.immutable.Seq");
      _builder.newLine();
      _builder.append("import scala.util.control.Exception._");
      _builder.newLine();
      _builder.append("import scala.util.{Failure,Success,Try}");
      _builder.newLine();
      _builder.append("import scala.{Int,Unit}");
      _builder.newLine();
      _builder.append("import scala.Predef.String");
      _builder.newLine();
      _builder.newLine();
      _builder.append("object ");
      _builder.append(tableName);
      _builder.append(" {");
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("implicit def tagInjection[Tag] = new Injection[String @@ Tag, String] {");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("def apply(t: String @@ Tag): String = t");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("def invert(s: String): String @@ Tag = covariantTag[Tag][String](s)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("implicit val literalTypeInjection = new Injection[tables.LiteralType, String] {");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("def apply(t: tables.LiteralType): String = tables.LiteralType.toString(t)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("def invert(s: String): tables.LiteralType = tables.LiteralType.fromString(s)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("implicit val literalNumberTypeInjection = new Injection[tables.LiteralNumberType, String] {");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("def apply(t: tables.LiteralNumberType): String = tables.LiteralNumberType.toString(t)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("def invert(s: String): tables.LiteralNumberType = tables.LiteralNumberType.fromString(s)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def createEmpty");
      _builder.append(tableName, "  ");
      _builder.append("()(implicit sqlContext: SQLContext) // frameless 0.5.0: use SparkSession instead.");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append(": ");
      _builder.append(tableName, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= ");
      _builder.append(tableName, "  ");
      {
        boolean _hasElements = false;
        for(final EClass eClass : eClasses) {
          if (!_hasElements) {
            _hasElements = true;
            _builder.append("(\n  ", "  ");
          } else {
            _builder.appendImmediate(",\n\n  ", "  ");
          }
          String _tableVariableName = OMLUtilities.tableVariableName(eClass);
          _builder.append(_tableVariableName, "  ");
          _builder.append(" = ");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("TypedDataset.create[api.");
          String _name = eClass.getName();
          _builder.append(_name, "    ");
          _builder.append("](");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("    ");
          _builder.append("Seq.empty[api.");
          String _name_1 = eClass.getName();
          _builder.append(_name_1, "      ");
          _builder.append("])");
        }
        if (_hasElements) {
          _builder.append("\n)", "  ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def convertTo");
      _builder.append(tableName, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("(t: tables.OMLSpecificationTables)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(implicit sqlContext: SQLContext) // frameless 0.5.0: use SparkSession instead.");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": ");
      _builder.append(tableName, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= ");
      _builder.append(tableName, "  ");
      {
        boolean _hasElements_1 = false;
        for(final EClass eClass_1 : eClasses) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            _builder.append("(\n  ", "  ");
          } else {
            _builder.appendImmediate(",\n\n  ", "  ");
          }
          String _tableVariableName_1 = OMLUtilities.tableVariableName(eClass_1);
          _builder.append(_tableVariableName_1, "  ");
          _builder.append(" = ");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("TypedDataset.create[api.");
          String _name_2 = eClass_1.getName();
          _builder.append(_name_2, "    ");
          _builder.append("](");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("    ");
          _builder.append("t.");
          String _tableVariableName_2 = OMLUtilities.tableVariableName(eClass_1);
          _builder.append(_tableVariableName_2, "      ");
          _builder.append(".map(i =>");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("     ");
          {
            Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass_1);
            boolean _hasElements_2 = false;
            for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
                String _name_3 = eClass_1.getName();
                String _plus = ("api." + _name_3);
                String _plus_1 = (_plus + "(\n  ");
                _builder.append(_plus_1, "       ");
              } else {
                _builder.appendImmediate(",\n  ", "       ");
              }
              String _columnName = OMLUtilities.columnName(attr);
              _builder.append(_columnName, "       ");
              _builder.append(" = i.");
              String _columnName_1 = OMLUtilities.columnName(attr);
              _builder.append(_columnName_1, "       ");
            }
            if (_hasElements_2) {
              _builder.append(")))", "       ");
            }
          }
        }
        if (_hasElements_1) {
          _builder.append("\n)", "  ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def extractFrom");
      _builder.append(tableName, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("(t: ");
      _builder.append(tableName, "  ");
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("(implicit sqlContext: SQLContext) // frameless 0.5.0: use SparkSession instead.");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": tables.OMLSpecificationTables");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("import frameless.syntax.DefaultSparkDelay");
      _builder.newLine();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("tables.OMLSpecificationTables");
      {
        boolean _hasElements_3 = false;
        for(final EClass eClass_2 : eClasses) {
          if (!_hasElements_3) {
            _hasElements_3 = true;
            _builder.append("(\n  ", "  \t");
          } else {
            _builder.appendImmediate(",\n\n  ", "  \t");
          }
          String _tableVariableName_3 = OMLUtilities.tableVariableName(eClass_2);
          _builder.append(_tableVariableName_3, "  \t");
          _builder.append(" = ");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("t.");
          String _tableVariableName_4 = OMLUtilities.tableVariableName(eClass_2);
          _builder.append(_tableVariableName_4, "  \t");
          _builder.append(".collect().run().to[Seq].map(i =>");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("  ");
          {
            Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_1 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass_2);
            boolean _hasElements_4 = false;
            for(final ETypedElement attr_1 : _schemaAPIOrOrderingKeyAttributes_1) {
              if (!_hasElements_4) {
                _hasElements_4 = true;
                String _name_4 = eClass_2.getName();
                String _plus_2 = ("tables." + _name_4);
                String _plus_3 = (_plus_2 + "(\n  ");
                _builder.append(_plus_3, "  \t  ");
              } else {
                _builder.appendImmediate(",\n  ", "  \t  ");
              }
              String _columnName_2 = OMLUtilities.columnName(attr_1);
              _builder.append(_columnName_2, "  \t  ");
              _builder.append(" = i.");
              String _columnName_3 = OMLUtilities.columnName(attr_1);
              _builder.append(_columnName_3, "  \t  ");
            }
            if (_hasElements_4) {
              _builder.append("))", "  \t  ");
            }
          }
        }
        if (_hasElements_3) {
          _builder.append("\n)", "  \t");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def parquetReadOMLSpecificationTables");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(dir: Path)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(implicit spark: SparkSession, sqlContext: SQLContext)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": Try[tables.OMLSpecificationTables]");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= nonFatalCatch[Try[tables.OMLSpecificationTables]]");
      _builder.newLine();
      _builder.append("    ");
      _builder.append(".withApply {");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("(cause: java.lang.Throwable) =>");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("cause.fillInStackTrace()");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("Failure(cause)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append(".apply {");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("dir.toIO.mkdirs()");
      _builder.newLine();
      _builder.newLine();
      _builder.append("      ");
      _builder.append("import spark.implicits._");
      _builder.newLine();
      _builder.append("\t  ");
      _builder.append("import scala.Predef.refArrayOps");
      _builder.newLine();
      _builder.append("\t  ");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("Success(");
      _builder.newLine();
      _builder.append("  \t    ");
      _builder.append("tables.OMLSpecificationTables");
      {
        boolean _hasElements_5 = false;
        for(final EClass eClass_3 : eClasses) {
          if (!_hasElements_5) {
            _hasElements_5 = true;
            _builder.append("(\n  ", "  \t    ");
          } else {
            _builder.appendImmediate(",\n\n  ", "  \t    ");
          }
          String _tableVariableName_5 = OMLUtilities.tableVariableName(eClass_3);
          _builder.append(_tableVariableName_5, "  \t    ");
          _builder.append(" = ");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t    ");
          _builder.append("  ");
          _builder.append("spark");
          _builder.newLine();
          _builder.append("  \t    ");
          _builder.append("  ");
          _builder.append(".read");
          _builder.newLine();
          _builder.append("  \t    ");
          _builder.append("  ");
          _builder.append(".parquet((dir / \"");
          String _name_5 = eClass_3.getName();
          _builder.append(_name_5, "  \t      ");
          _builder.append(".parquet\").toIO.getAbsolutePath)");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t    ");
          _builder.append("  ");
          _builder.append(".as[tables.");
          String _name_6 = eClass_3.getName();
          _builder.append(_name_6, "  \t      ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t    ");
          _builder.append("  ");
          _builder.append(".collect()");
          _builder.newLine();
          _builder.append("  \t    ");
          _builder.append("  ");
          _builder.append(".to[Seq]");
        }
        if (_hasElements_5) {
          _builder.append("\n))", "  \t    ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def parquetWriteOMLSpecificationTables");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(t: tables.OMLSpecificationTables,");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("dir: Path)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(implicit spark: SparkSession, sqlContext: SQLContext)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": Try[Unit]");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= nonFatalCatch[Try[Unit]]");
      _builder.newLine();
      _builder.append("    ");
      _builder.append(".withApply {");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("(cause: java.lang.Throwable) =>");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("cause.fillInStackTrace()");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("Failure(cause)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append(".apply {");
      _builder.newLine();
      _builder.append("    \t  ");
      _builder.append("import spark.implicits._");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("dir.toIO.mkdirs()");
      _builder.newLine();
      _builder.newLine();
      {
        for(final EClass eClass_4 : eClasses) {
          _builder.append("      ");
          _builder.append("t");
          _builder.newLine();
          _builder.append("      ");
          _builder.append(".");
          String _tableVariableName_6 = OMLUtilities.tableVariableName(eClass_4);
          _builder.append(_tableVariableName_6, "      ");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append(".toDF()");
          _builder.newLine();
          _builder.append("      ");
          _builder.append(".write");
          _builder.newLine();
          _builder.append("      ");
          _builder.append(".parquet((dir / \"");
          String _name_7 = eClass_4.getName();
          _builder.append(_name_7, "      ");
          _builder.append(".parquet\").toIO.getAbsolutePath())");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.newLine();
        }
      }
      _builder.append("  \t  ");
      _builder.append("Success(())");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        final Function1<EPackage, EList<EClassifier>> _function_3 = (EPackage it) -> {
          return it.getEClassifiers();
        };
        final Function1<EEnum, String> _function_4 = (EEnum it) -> {
          return it.getName();
        };
        List<EEnum> _sortBy = IterableExtensions.<EEnum, String>sortBy(Iterables.<EEnum>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function_3)), EEnum.class), _function_4);
        for(final EEnum eClass_5 : _sortBy) {
          _builder.append("  ");
          _builder.append("implicit val ");
          String _firstLower = StringExtensions.toFirstLower(eClass_5.getName());
          _builder.append(_firstLower, "  ");
          _builder.append("I");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": Injection[tables.");
          String _name_8 = eClass_5.getName();
          _builder.append(_name_8, "  ");
          _builder.append(", Int]");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= Injection(");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("{");
          _builder.newLine();
          {
            EList<EEnumLiteral> _eLiterals = eClass_5.getELiterals();
            for(final EEnumLiteral elit : _eLiterals) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("case tables.");
              String _literal = elit.getLiteral();
              _builder.append(_literal, "  \t");
              _builder.append(" => ");
              int _indexOf = eClass_5.getELiterals().indexOf(elit);
              _builder.append(_indexOf, "  \t");
              _builder.newLineIfNotEmpty();
            }
          }
          _builder.append("  ");
          _builder.append("},");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("{");
          _builder.newLine();
          {
            EList<EEnumLiteral> _eLiterals_1 = eClass_5.getELiterals();
            for(final EEnumLiteral elit_1 : _eLiterals_1) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("case ");
              int _indexOf_1 = eClass_5.getELiterals().indexOf(elit_1);
              _builder.append(_indexOf_1, "  \t");
              _builder.append(" => tables.");
              String _literal_1 = elit_1.getLiteral();
              _builder.append(_literal_1, "  \t");
              _builder.newLineIfNotEmpty();
            }
          }
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("  ");
          _builder.append(")");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
        }
      }
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.newLine();
      _builder.append("case class ");
      _builder.append(tableName);
      _builder.newLineIfNotEmpty();
      {
        boolean _hasElements_6 = false;
        for(final EClass eClass_6 : eClasses) {
          if (!_hasElements_6) {
            _hasElements_6 = true;
            _builder.append("(\n  ");
          } else {
            _builder.appendImmediate(",\n\n  ", "");
          }
          String _tableVariableName_7 = OMLUtilities.tableVariableName(eClass_6);
          _builder.append(_tableVariableName_7);
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": TypedDataset[api.");
          String _name_9 = eClass_6.getName();
          _builder.append(_name_9, "  ");
          _builder.append("]");
        }
        if (_hasElements_6) {
          _builder.append("\n)");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
}
