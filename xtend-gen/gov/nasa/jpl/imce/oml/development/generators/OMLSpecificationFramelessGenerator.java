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
import gov.nasa.jpl.imce.oml.development.generators.OMLSpecificationOMLSQLGenerator;
import gov.nasa.jpl.imce.oml.development.generators.OMLUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
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
      final File rfile = new File(((targetFolder + File.separator) + "OMLReaders.scala"));
      boolean _exists_3 = rfile.exists();
      if (_exists_3) {
        rfile.delete();
      }
      final FileOutputStream readersFile = new FileOutputStream(rfile);
      try {
        readersFile.write(this.generateReadersFile(ePackages, packageQName).getBytes());
      } finally {
        readersFile.close();
      }
      final File wfile = new File(((targetFolder + File.separator) + "OMLParquetWriters.scala"));
      boolean _exists_4 = wfile.exists();
      if (_exists_4) {
        wfile.delete();
      }
      final FileOutputStream writersFile = new FileOutputStream(wfile);
      try {
        writersFile.write(this.generateParquetWritersFile(ePackages, packageQName).getBytes());
      } finally {
        writersFile.close();
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
  
  public String generateParquetWritersFile(final List<EPackage> ePackages, final String packageQName) {
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
      _builder.append("import gov.nasa.jpl.imce.oml.tables");
      _builder.newLine();
      _builder.append("import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder");
      _builder.newLine();
      _builder.append("import org.apache.spark.sql.SQLContext");
      _builder.newLine();
      _builder.append("import scala.collection.immutable.Seq");
      _builder.newLine();
      _builder.append("import scala.Unit");
      _builder.newLine();
      _builder.append("import scala.Predef.String");
      _builder.newLine();
      _builder.newLine();
      _builder.append("object OMLParquetWriters {");
      _builder.newLine();
      _builder.append("\t");
      _builder.newLine();
      {
        for(final EClass eClass : eClasses) {
          _builder.append("\t");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("def write");
          String _upperCaseInitialOrWord = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass));
          _builder.append(_upperCaseInitialOrWord, "\t");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("(table: Seq[tables.");
          String _name = eClass.getName();
          _builder.append(_name, "\t");
          _builder.append("], path: String)");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("(implicit sqlContext: SQLContext, encoder: ExpressionEncoder[tables.");
          String _name_1 = eClass.getName();
          _builder.append(_name_1, "\t");
          _builder.append("])");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append(": Unit");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("= sqlContext");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("  ");
          _builder.append(".createDataset(table)");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("  ");
          _builder.append(".write");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("  ");
          _builder.append(".parquet(path)");
          _builder.newLine();
        }
      }
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
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
  
  public static String rowColumnType(final ETypedElement col) {
    String _xblockexpression = null;
    {
      final String tname = col.getEType().getName();
      final String cname = OMLUtilities.columnName(col);
      String _xifexpression = null;
      boolean _equals = Objects.equal(tname, "LiteralNumber");
      if (_equals) {
        _xifexpression = "String, String";
      } else {
        String _xifexpression_1 = null;
        boolean _equals_1 = Objects.equal(tname, "LiteralValue");
        if (_equals_1) {
          _xifexpression_1 = "String, String";
        } else {
          String _xifexpression_2 = null;
          boolean _equals_2 = Objects.equal(tname, "EBoolean");
          if (_equals_2) {
            _xifexpression_2 = "Boolean";
          } else {
            String _xifexpression_3 = null;
            if ((tname.endsWith("Kind") && (!cname.endsWith("UUID")))) {
              _xifexpression_3 = "Int";
            } else {
              _xifexpression_3 = "String";
            }
            _xifexpression_2 = _xifexpression_3;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String rowColumnQuery(final ETypedElement col) {
    String _xblockexpression = null;
    {
      final String tname = col.getEType().getName();
      final String cname = OMLUtilities.columnName(col);
      String _xifexpression = null;
      boolean _equals = Objects.equal(tname, "LiteralNumber");
      if (_equals) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("row.getAs[GenericRowWithSchema](\"");
        _builder.append(cname);
        _builder.append("\").getAs[String](\"value\"),row.getAs[GenericRowWithSchema](\"");
        String _columnName = OMLUtilities.columnName(col);
        _builder.append(_columnName);
        _builder.append("\").getAs[String](\"literalType\")");
        _xifexpression = _builder.toString();
      } else {
        String _xifexpression_1 = null;
        boolean _equals_1 = Objects.equal(tname, "LiteralValue");
        if (_equals_1) {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("row.getAs[GenericRowWithSchema](\"");
          _builder_1.append(cname);
          _builder_1.append("\").getAs[String](\"value\"),row.getAs[GenericRowWithSchema](\"");
          String _columnName_1 = OMLUtilities.columnName(col);
          _builder_1.append(_columnName_1);
          _builder_1.append("\").getAs[String](\"literalType\")");
          _xifexpression_1 = _builder_1.toString();
        } else {
          String _xifexpression_2 = null;
          boolean _equals_2 = Objects.equal(tname, "EBoolean");
          if (_equals_2) {
            StringConcatenation _builder_2 = new StringConcatenation();
            _builder_2.append("row.getAs[Boolean](\"");
            _builder_2.append(cname);
            _builder_2.append("\")");
            _xifexpression_2 = _builder_2.toString();
          } else {
            String _xifexpression_3 = null;
            if ((tname.endsWith("Kind") && (!cname.endsWith("UUID")))) {
              StringConcatenation _builder_3 = new StringConcatenation();
              _builder_3.append("row.getAs[Int](\"");
              _builder_3.append(cname);
              _builder_3.append("\")");
              _xifexpression_3 = _builder_3.toString();
            } else {
              StringConcatenation _builder_4 = new StringConcatenation();
              _builder_4.append("row.getAs[String](\"");
              _builder_4.append(cname);
              _builder_4.append("\")");
              _xifexpression_3 = _builder_4.toString();
            }
            _xifexpression_2 = _xifexpression_3;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String sqlColumnQuery(final ETypedElement col) {
    String _xblockexpression = null;
    {
      final String tname = col.getEType().getName();
      final String cname = OMLUtilities.columnName(col);
      String _xifexpression = null;
      boolean _equals = Objects.equal(tname, "LiteralNumber");
      if (_equals) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("row.getAs[String](\"");
        _builder.append(cname);
        _builder.append("\"),row.getAs[String](\"");
        _builder.append(cname);
        _builder.append("LiteralType\")");
        _xifexpression = _builder.toString();
      } else {
        String _xifexpression_1 = null;
        boolean _equals_1 = Objects.equal(tname, "LiteralValue");
        if (_equals_1) {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("row.getAs[String](\"");
          _builder_1.append(cname);
          _builder_1.append("\"),row.getAs[String](\"");
          _builder_1.append(cname);
          _builder_1.append("LiteralType\")");
          _xifexpression_1 = _builder_1.toString();
        } else {
          String _xifexpression_2 = null;
          boolean _equals_2 = Objects.equal(tname, "EBoolean");
          if (_equals_2) {
            StringConcatenation _builder_2 = new StringConcatenation();
            _builder_2.append("row.getAs[Boolean](\"");
            _builder_2.append(cname);
            _builder_2.append("\")");
            _xifexpression_2 = _builder_2.toString();
          } else {
            String _xifexpression_3 = null;
            if ((tname.endsWith("Kind") && (!cname.endsWith("UUID")))) {
              StringConcatenation _builder_3 = new StringConcatenation();
              _builder_3.append("row.getAs[Int](\"");
              _builder_3.append(cname);
              _builder_3.append("\")");
              _xifexpression_3 = _builder_3.toString();
            } else {
              StringConcatenation _builder_4 = new StringConcatenation();
              _builder_4.append("row.getAs[String](\"");
              _builder_4.append(cname);
              _builder_4.append("\")");
              _xifexpression_3 = _builder_4.toString();
            }
            _xifexpression_2 = _xifexpression_3;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String rowColumnDecl(final ETypedElement col) {
    String _xblockexpression = null;
    {
      final String tname = col.getEType().getName();
      final String cname = OMLUtilities.columnName(col);
      String _xifexpression = null;
      boolean _equals = Objects.equal(tname, "LiteralNumber");
      if (_equals) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append(cname);
        _builder.append(": String, ");
        _builder.append(cname);
        _builder.append("LiteralType: String");
        _xifexpression = _builder.toString();
      } else {
        String _xifexpression_1 = null;
        boolean _equals_1 = Objects.equal(tname, "LiteralValue");
        if (_equals_1) {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append(cname);
          _builder_1.append(": String, ");
          _builder_1.append(cname);
          _builder_1.append("LiteralType: String");
          _xifexpression_1 = _builder_1.toString();
        } else {
          String _xifexpression_2 = null;
          boolean _equals_2 = Objects.equal(tname, "EBoolean");
          if (_equals_2) {
            StringConcatenation _builder_2 = new StringConcatenation();
            _builder_2.append(cname);
            _builder_2.append(": Boolean");
            _xifexpression_2 = _builder_2.toString();
          } else {
            String _xifexpression_3 = null;
            if ((tname.endsWith("Kind") && (!cname.endsWith("UUID")))) {
              StringConcatenation _builder_3 = new StringConcatenation();
              _builder_3.append(cname);
              _builder_3.append(": Int");
              _xifexpression_3 = _builder_3.toString();
            } else {
              StringConcatenation _builder_4 = new StringConcatenation();
              _builder_4.append(cname);
              _builder_4.append(": String");
              _xifexpression_3 = _builder_4.toString();
            }
            _xifexpression_2 = _xifexpression_3;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String rowColumnVars(final ETypedElement col) {
    String _xblockexpression = null;
    {
      final String tname = col.getEType().getName();
      final String cname = OMLUtilities.columnName(col);
      String _xifexpression = null;
      boolean _equals = Objects.equal(tname, "LiteralNumber");
      if (_equals) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append(cname);
        _builder.append(", ");
        _builder.append(cname);
        _builder.append("LiteralType");
        _xifexpression = _builder.toString();
      } else {
        String _xifexpression_1 = null;
        boolean _equals_1 = Objects.equal(tname, "LiteralValue");
        if (_equals_1) {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append(cname);
          _builder_1.append(", ");
          _builder_1.append(cname);
          _builder_1.append("LiteralType");
          _xifexpression_1 = _builder_1.toString();
        } else {
          String _xifexpression_2 = null;
          boolean _equals_2 = Objects.equal(tname, "EBoolean");
          if (_equals_2) {
            StringConcatenation _builder_2 = new StringConcatenation();
            _builder_2.append(cname);
            _xifexpression_2 = _builder_2.toString();
          } else {
            String _xifexpression_3 = null;
            if ((tname.endsWith("Kind") && (!cname.endsWith("UUID")))) {
              StringConcatenation _builder_3 = new StringConcatenation();
              _builder_3.append(cname);
              _xifexpression_3 = _builder_3.toString();
            } else {
              StringConcatenation _builder_4 = new StringConcatenation();
              _builder_4.append(cname);
              _xifexpression_3 = _builder_4.toString();
            }
            _xifexpression_2 = _xifexpression_3;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public String generateReadersFile(final List<EPackage> ePackages, final String packageQName) {
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
      _builder.append(" ");
      _builder.newLine();
      _builder.append("package ");
      _builder.append(packageQName);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("import org.apache.spark.sql.Row");
      _builder.newLine();
      _builder.append("import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.tables");
      _builder.newLine();
      _builder.append("import scala.{Boolean,Int,None,Some,StringContext}");
      _builder.newLine();
      _builder.append("import scala.Predef.{identity,String}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("object OMLReaders {");
      _builder.newLine();
      _builder.append("\t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def cardinalityRestrictionKind(kind: Int)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append(": tables.CardinalityRestrictionKind");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("= kind match {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case 0 =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("tables.MinCardinalityRestriction");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case 1 =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("tables.MaxCardinalityRestriction");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case 2 =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("tables.ExactCardinalityRestriction");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def cardinalityRestrictionKind(kind: tables.CardinalityRestrictionKind)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append(": Int");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("= kind match {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case tables.MinCardinalityRestriction =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("0");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case tables.MaxCardinalityRestriction =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("1");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case tables.ExactCardinalityRestriction =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("2");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("def terminologyKind(kind: Int)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append(": tables.TerminologyKind");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("= kind match {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case 0 =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("tables.OpenWorldDefinitions");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case 1 =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("tables.ClosedWorldDesignations");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def terminologyKind(kind: tables.TerminologyKind)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append(": Int");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("= kind match {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case tables.OpenWorldDefinitions =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("0");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case tables.ClosedWorldDesignations =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("1");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def descriptionKind(kind: Int)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append(": tables.DescriptionKind");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("= kind match {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case 0 =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("tables.Final");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case 1 =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("tables.Partial");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def descriptionKind(kind: tables.DescriptionKind)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append(": Int");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("= kind match {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case tables.Final =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("0");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("case tables.Partial =>");
      _builder.newLine();
      _builder.append("\t\t  ");
      _builder.append("1");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        for(final EClass eClass : eClasses) {
          _builder.append("\t");
          final Iterable<ETypedElement> cols = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("case class ");
          String _name = eClass.getName();
          _builder.append(_name, "\t");
          _builder.append("Tuple");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          {
            boolean _hasElements = false;
            for(final ETypedElement col : cols) {
              if (!_hasElements) {
                _hasElements = true;
                _builder.append("(", "\t");
              } else {
                _builder.appendImmediate(",\n ", "\t");
              }
              String _rowColumnDecl = OMLSpecificationFramelessGenerator.rowColumnDecl(col);
              _builder.append(_rowColumnDecl, "\t");
            }
            if (_hasElements) {
              _builder.append(")", "\t");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.newLine();
          _builder.append("\t");
          _builder.append("def ");
          String _name_1 = eClass.getName();
          _builder.append(_name_1, "\t");
          _builder.append("Row2Tuple");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("(row: Row)");
          _builder.newLine();
          _builder.append("\t");
          _builder.append(": ");
          String _name_2 = eClass.getName();
          _builder.append(_name_2, "\t");
          _builder.append("Tuple");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("= ");
          {
            boolean _hasElements_1 = false;
            for(final ETypedElement col_1 : cols) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
                String _name_3 = eClass.getName();
                String _plus = (_name_3 + "Tuple(\n  ");
                _builder.append(_plus, "\t");
              } else {
                _builder.appendImmediate(",\n  ", "\t");
              }
              String _rowColumnQuery = OMLSpecificationFramelessGenerator.rowColumnQuery(col_1);
              _builder.append(_rowColumnQuery, "\t");
            }
            if (_hasElements_1) {
              _builder.append("\n)", "\t");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.newLine();
          _builder.append("\t");
          _builder.append("def ");
          String _name_4 = eClass.getName();
          _builder.append(_name_4, "\t");
          _builder.append("SQL2Tuple");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("(row: Row)");
          _builder.newLine();
          _builder.append("\t");
          _builder.append(": ");
          String _name_5 = eClass.getName();
          _builder.append(_name_5, "\t");
          _builder.append("Tuple");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("= ");
          {
            boolean _hasElements_2 = false;
            for(final ETypedElement col_2 : cols) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
                String _name_6 = eClass.getName();
                String _plus_1 = (_name_6 + "Tuple(\n  ");
                _builder.append(_plus_1, "\t");
              } else {
                _builder.appendImmediate(",\n  ", "\t");
              }
              String _sqlColumnQuery = OMLSpecificationFramelessGenerator.sqlColumnQuery(col_2);
              _builder.append(_sqlColumnQuery, "\t");
            }
            if (_hasElements_2) {
              _builder.append("\n)", "\t");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("\t\t\t");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("def ");
          String _name_7 = eClass.getName();
          _builder.append(_name_7, "\t");
          _builder.append("Tuple2Type");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("(tuple: ");
          String _name_8 = eClass.getName();
          _builder.append(_name_8, "\t");
          _builder.append("Tuple)");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append(": tables.");
          String _name_9 = eClass.getName();
          _builder.append(_name_9, "\t");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("= ");
          {
            boolean _hasElements_3 = false;
            for(final ETypedElement col_3 : cols) {
              if (!_hasElements_3) {
                _hasElements_3 = true;
                String _name_10 = eClass.getName();
                String _plus_2 = ("tables." + _name_10);
                String _plus_3 = (_plus_2 + "(\n  ");
                _builder.append(_plus_3, "\t");
              } else {
                _builder.appendImmediate(",\n  ", "\t");
              }
              String _xifexpression = null;
              String _columnName = OMLUtilities.columnName(col_3);
              boolean _equals = Objects.equal(_columnName, "uuid");
              if (_equals) {
                String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
                String _plus_4 = ("tables.taggedTypes." + _lowerCaseInitialOrWord);
                String _plus_5 = (_plus_4 + "UUID(tuple.");
                String _columnName_1 = OMLUtilities.columnName(col_3);
                String _plus_6 = (_plus_5 + _columnName_1);
                _xifexpression = (_plus_6 + ")");
              } else {
                String _xifexpression_1 = null;
                if (((OMLUtilities.isIRIReference(col_3)).booleanValue() || Objects.equal(col_3.getName(), "iri"))) {
                  String _columnName_2 = OMLUtilities.columnName(col_3);
                  String _plus_7 = ("tables.taggedTypes.iri(tuple." + _columnName_2);
                  _xifexpression_1 = (_plus_7 + ")");
                } else {
                  String _xifexpression_2 = null;
                  String _name_11 = col_3.getEType().getName();
                  boolean _equals_1 = Objects.equal(_name_11, "EBoolean");
                  if (_equals_1) {
                    String _columnName_3 = OMLUtilities.columnName(col_3);
                    _xifexpression_2 = ("tuple." + _columnName_3);
                  } else {
                    String _xifexpression_3 = null;
                    String _name_12 = col_3.getEType().getName();
                    boolean _equals_2 = Objects.equal(_name_12, "LiteralValue");
                    if (_equals_2) {
                      StringConcatenation _builder_1 = new StringConcatenation();
                      _builder_1.append("tables.LiteralValue.fromJSON(s\"\"\"{\"literalType\":\"${tuple.");
                      String _columnName_4 = OMLUtilities.columnName(col_3);
                      _builder_1.append(_columnName_4);
                      _builder_1.append("LiteralType}\",\"value\":\"${tuple.");
                      String _columnName_5 = OMLUtilities.columnName(col_3);
                      _builder_1.append(_columnName_5);
                      _builder_1.append("}\"}\"\"\")");
                      _xifexpression_3 = _builder_1.toString();
                    } else {
                      String _xifexpression_4 = null;
                      String _name_13 = col_3.getEType().getName();
                      boolean _equals_3 = Objects.equal(_name_13, "LiteralNumber");
                      if (_equals_3) {
                        StringConcatenation _builder_2 = new StringConcatenation();
                        _builder_2.append("tables.LiteralNumber.fromJSON(s\"\"\"{\"literalType\":\"${tuple.");
                        String _columnName_6 = OMLUtilities.columnName(col_3);
                        _builder_2.append(_columnName_6);
                        _builder_2.append("LiteralType}\",\"value\":\"${tuple.");
                        String _columnName_7 = OMLUtilities.columnName(col_3);
                        _builder_2.append(_columnName_7);
                        _builder_2.append("}\"}\"\"\")");
                        _xifexpression_4 = _builder_2.toString();
                      } else {
                        String _xifexpression_5 = null;
                        String _name_14 = col_3.getEType().getName();
                        boolean _equals_4 = Objects.equal(_name_14, "LiteralString");
                        if (_equals_4) {
                          StringConcatenation _builder_3 = new StringConcatenation();
                          _builder_3.append("tables.taggedTypes.stringDataType(tuple.");
                          String _columnName_8 = OMLUtilities.columnName(col_3);
                          _builder_3.append(_columnName_8);
                          _builder_3.append(")");
                          _xifexpression_5 = _builder_3.toString();
                        } else {
                          String _xifexpression_6 = null;
                          String _name_15 = col_3.getEType().getName();
                          boolean _equals_5 = Objects.equal(_name_15, "LiteralDateTime");
                          if (_equals_5) {
                            StringConcatenation _builder_4 = new StringConcatenation();
                            _builder_4.append("if (tuple.");
                            String _columnName_9 = OMLUtilities.columnName(col_3);
                            _builder_4.append(_columnName_9);
                            _builder_4.append(".isEmpty) None else tables.LiteralDateTime.parseDateTime(tuple.");
                            String _columnName_10 = OMLUtilities.columnName(col_3);
                            _builder_4.append(_columnName_10);
                            _builder_4.append(")");
                            _xifexpression_6 = _builder_4.toString();
                          } else {
                            String _xifexpression_7 = null;
                            Boolean _isClassFeature = OMLUtilities.isClassFeature(col_3);
                            if ((_isClassFeature).booleanValue()) {
                              String _lowerCaseInitialOrWord_1 = OMLUtilities.lowerCaseInitialOrWord(col_3.getEType().getName());
                              String _plus_8 = ("tables.taggedTypes." + _lowerCaseInitialOrWord_1);
                              String _plus_9 = (_plus_8 + "UUID(tuple.");
                              String _columnName_11 = OMLUtilities.columnName(col_3);
                              String _plus_10 = (_plus_9 + _columnName_11);
                              _xifexpression_7 = (_plus_10 + ")");
                            } else {
                              String _xifexpression_8 = null;
                              if ((Objects.equal(col_3.getName(), "kind") || Objects.equal(col_3.getName(), "restrictionKind"))) {
                                String _lowerCaseInitialOrWord_2 = OMLUtilities.lowerCaseInitialOrWord(col_3.getEType().getName());
                                String _plus_11 = (_lowerCaseInitialOrWord_2 + "(tuple.");
                                String _columnName_12 = OMLUtilities.columnName(col_3);
                                String _plus_12 = (_plus_11 + _columnName_12);
                                _xifexpression_8 = (_plus_12 + ")");
                              } else {
                                String _lowerCaseInitialOrWord_3 = OMLUtilities.lowerCaseInitialOrWord(col_3.getEType().getName());
                                String _plus_13 = ("tables.taggedTypes." + _lowerCaseInitialOrWord_3);
                                String _plus_14 = (_plus_13 + "(tuple.");
                                String _columnName_13 = OMLUtilities.columnName(col_3);
                                String _plus_15 = (_plus_14 + _columnName_13);
                                _xifexpression_8 = (_plus_15 + ")");
                              }
                              _xifexpression_7 = _xifexpression_8;
                            }
                            _xifexpression_6 = _xifexpression_7;
                          }
                          _xifexpression_5 = _xifexpression_6;
                        }
                        _xifexpression_4 = _xifexpression_5;
                      }
                      _xifexpression_3 = _xifexpression_4;
                    }
                    _xifexpression_2 = _xifexpression_3;
                  }
                  _xifexpression_1 = _xifexpression_2;
                }
                _xifexpression = _xifexpression_1;
              }
              final String tname = _xifexpression;
              {
                if (((col_3.getLowerBound() == 0) && (!Objects.equal(col_3.getEType().getName(), "LiteralDateTime")))) {
                  _builder.append("if (");
                  {
                    String _name_16 = col_3.getEType().getName();
                    boolean _equals_6 = Objects.equal(_name_16, "LiteralNumber");
                    if (_equals_6) {
                      _builder.append("(null == tuple.");
                      String _columnName_14 = OMLUtilities.columnName(col_3);
                      _builder.append(_columnName_14, "\t");
                      _builder.append("LiteralType || tuple.");
                      String _columnName_15 = OMLUtilities.columnName(col_3);
                      _builder.append(_columnName_15, "\t");
                      _builder.append("LiteralType.isEmpty) && (null == tuple.");
                      String _columnName_16 = OMLUtilities.columnName(col_3);
                      _builder.append(_columnName_16, "\t");
                      _builder.append(" || tuple.");
                      String _columnName_17 = OMLUtilities.columnName(col_3);
                      _builder.append(_columnName_17, "\t");
                      _builder.append(".isEmpty)");
                    } else {
                      _builder.append("null == tuple.");
                      String _columnName_18 = OMLUtilities.columnName(col_3);
                      _builder.append(_columnName_18, "\t");
                      _builder.append(" || tuple.");
                      String _columnName_19 = OMLUtilities.columnName(col_3);
                      _builder.append(_columnName_19, "\t");
                      _builder.append(".isEmpty");
                    }
                  }
                  _builder.append(") None else Some(");
                  _builder.append(tname, "\t");
                  _builder.append(")");
                } else {
                  _builder.append(tname, "\t");
                }
              }
            }
            if (_hasElements_3) {
              _builder.append("\n)", "\t");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.newLine();
          _builder.append("\t");
          _builder.append("def ");
          String _name_17 = eClass.getName();
          _builder.append(_name_17, "\t");
          _builder.append("Type2Tuple");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("(e: tables.");
          String _name_18 = eClass.getName();
          _builder.append(_name_18, "\t");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append(": ");
          String _name_19 = eClass.getName();
          _builder.append(_name_19, "\t");
          _builder.append("Tuple");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("= ");
          {
            boolean _hasElements_4 = false;
            for(final ETypedElement col_4 : cols) {
              if (!_hasElements_4) {
                _hasElements_4 = true;
                String _name_20 = eClass.getName();
                String _plus_16 = (_name_20 + "Tuple(\n  ");
                _builder.append(_plus_16, "\t");
              } else {
                _builder.appendImmediate(",\n  ", "\t");
              }
              String _xifexpression_9 = null;
              String _columnName_20 = OMLUtilities.columnName(col_4);
              boolean _equals_7 = Objects.equal(_columnName_20, "uuid");
              if (_equals_7) {
                String _columnName_21 = OMLUtilities.columnName(col_4);
                _xifexpression_9 = ("e." + _columnName_21);
              } else {
                String _xifexpression_10 = null;
                if (((OMLUtilities.isIRIReference(col_4)).booleanValue() || Objects.equal(col_4.getName(), "iri"))) {
                  String _columnName_22 = OMLUtilities.columnName(col_4);
                  _xifexpression_10 = ("e." + _columnName_22);
                } else {
                  String _xifexpression_11 = null;
                  String _name_21 = col_4.getEType().getName();
                  boolean _equals_8 = Objects.equal(_name_21, "EBoolean");
                  if (_equals_8) {
                    String _columnName_23 = OMLUtilities.columnName(col_4);
                    _xifexpression_11 = ("e." + _columnName_23);
                  } else {
                    String _xifexpression_12 = null;
                    if ((Objects.equal(col_4.getEType().getName(), "LiteralValue") && (col_4.getLowerBound() == 0))) {
                      StringConcatenation _builder_5 = new StringConcatenation();
                      _builder_5.append("e.");
                      String _columnName_24 = OMLUtilities.columnName(col_4);
                      _builder_5.append(_columnName_24);
                      _builder_5.append(".fold[String](null) { n => n.value }, e.");
                      String _columnName_25 = OMLUtilities.columnName(col_4);
                      _builder_5.append(_columnName_25);
                      _builder_5.append(".fold[String](null) { n => n.literalType.toString }");
                      _xifexpression_12 = _builder_5.toString();
                    } else {
                      String _xifexpression_13 = null;
                      if ((Objects.equal(col_4.getEType().getName(), "LiteralValue") && (col_4.getLowerBound() == 1))) {
                        StringConcatenation _builder_6 = new StringConcatenation();
                        _builder_6.append("e.");
                        String _columnName_26 = OMLUtilities.columnName(col_4);
                        _builder_6.append(_columnName_26);
                        _builder_6.append(".value, e.");
                        String _columnName_27 = OMLUtilities.columnName(col_4);
                        _builder_6.append(_columnName_27);
                        _builder_6.append(".literalType.toString");
                        _xifexpression_13 = _builder_6.toString();
                      } else {
                        String _xifexpression_14 = null;
                        String _name_22 = col_4.getEType().getName();
                        boolean _equals_9 = Objects.equal(_name_22, "LiteralNumber");
                        if (_equals_9) {
                          StringConcatenation _builder_7 = new StringConcatenation();
                          _builder_7.append("e.");
                          String _columnName_28 = OMLUtilities.columnName(col_4);
                          _builder_7.append(_columnName_28);
                          _builder_7.append(".fold[String](null) { n => n.value }, e.");
                          String _columnName_29 = OMLUtilities.columnName(col_4);
                          _builder_7.append(_columnName_29);
                          _builder_7.append(".fold[String](null) { n => n.literalType.toString }");
                          _xifexpression_14 = _builder_7.toString();
                        } else {
                          String _xifexpression_15 = null;
                          String _name_23 = col_4.getEType().getName();
                          boolean _equals_10 = Objects.equal(_name_23, "LiteralString");
                          if (_equals_10) {
                            StringConcatenation _builder_8 = new StringConcatenation();
                            _builder_8.append("e.");
                            String _columnName_30 = OMLUtilities.columnName(col_4);
                            _builder_8.append(_columnName_30);
                            _xifexpression_15 = _builder_8.toString();
                          } else {
                            String _xifexpression_16 = null;
                            String _name_24 = col_4.getEType().getName();
                            boolean _equals_11 = Objects.equal(_name_24, "LiteralDateTime");
                            if (_equals_11) {
                              StringConcatenation _builder_9 = new StringConcatenation();
                              _builder_9.append("e.");
                              String _columnName_31 = OMLUtilities.columnName(col_4);
                              _builder_9.append(_columnName_31);
                              _builder_9.append(".fold[String](null)(_.value)");
                              _xifexpression_16 = _builder_9.toString();
                            } else {
                              String _xifexpression_17 = null;
                              if ((Objects.equal(col_4.getName(), "kind") || Objects.equal(col_4.getName(), "restrictionKind"))) {
                                String _lowerCaseInitialOrWord_4 = OMLUtilities.lowerCaseInitialOrWord(col_4.getEType().getName());
                                String _plus_17 = (_lowerCaseInitialOrWord_4 + "(e.");
                                String _columnName_32 = OMLUtilities.columnName(col_4);
                                String _plus_18 = (_plus_17 + _columnName_32);
                                _xifexpression_17 = (_plus_18 + ")");
                              } else {
                                String _xifexpression_18 = null;
                                int _lowerBound = col_4.getLowerBound();
                                boolean _equals_12 = (_lowerBound == 0);
                                if (_equals_12) {
                                  StringConcatenation _builder_10 = new StringConcatenation();
                                  _builder_10.append("e.");
                                  String _columnName_33 = OMLUtilities.columnName(col_4);
                                  _builder_10.append(_columnName_33);
                                  _builder_10.append(".fold[String](null)(identity)");
                                  _xifexpression_18 = _builder_10.toString();
                                } else {
                                  StringConcatenation _builder_11 = new StringConcatenation();
                                  _builder_11.append("e.");
                                  String _columnName_34 = OMLUtilities.columnName(col_4);
                                  _builder_11.append(_columnName_34);
                                  _xifexpression_18 = _builder_11.toString();
                                }
                                _xifexpression_17 = _xifexpression_18;
                              }
                              _xifexpression_16 = _xifexpression_17;
                            }
                            _xifexpression_15 = _xifexpression_16;
                          }
                          _xifexpression_14 = _xifexpression_15;
                        }
                        _xifexpression_13 = _xifexpression_14;
                      }
                      _xifexpression_12 = _xifexpression_13;
                    }
                    _xifexpression_11 = _xifexpression_12;
                  }
                  _xifexpression_10 = _xifexpression_11;
                }
                _xifexpression_9 = _xifexpression_10;
              }
              final String tname_1 = _xifexpression_9;
              _builder.append(tname_1, "\t");
            }
            if (_hasElements_4) {
              _builder.append("\n)", "\t");
            }
          }
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
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
      final Function1<EPackage, EList<EClassifier>> _function_3 = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_4 = (EClass it) -> {
        return Boolean.valueOf((((OMLUtilities.isFunctionalAPI(it)).booleanValue() && (!it.isInterface())) && (!(OMLUtilities.isValueTable(it)).booleanValue())));
      };
      Iterable<EClass> _filter = IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function_3)), EClass.class), _function_4);
      OMLUtilities.OMLTableCompare _oMLTableCompare = new OMLUtilities.OMLTableCompare();
      final List<EClass> cClasses = IterableExtensions.<EClass>sortWith(_filter, _oMLTableCompare);
      final Function1<EClass, Boolean> _function_5 = (EClass it) -> {
        String _name = it.getName();
        return Boolean.valueOf((!Objects.equal(_name, "BinaryScalarRestriction")));
      };
      final Iterable<EClass> cClasses1 = IterableExtensions.<EClass>takeWhile(cClasses, _function_5);
      final ArrayList<EClass> cClasses2 = new ArrayList<EClass>();
      cClasses2.addAll(cClasses);
      CollectionExtensions.<EClass>removeAll(cClasses2, cClasses1);
      final Function1<EClass, Boolean> _function_6 = (EClass it) -> {
        return Boolean.valueOf(((!it.getName().endsWith("Restriction")) && (!it.getName().endsWith("ReifiedRelationshipRestriction"))));
      };
      final Iterable<EClass> cClasses3 = IterableExtensions.<EClass>filter(cClasses2, _function_6);
      final Function1<EClass, Boolean> _function_7 = (EClass it) -> {
        return Boolean.valueOf((it.getName().endsWith("Restriction") && (!it.getName().endsWith("ReifiedRelationshipRestriction"))));
      };
      final Iterable<EClass> restrictions = IterableExtensions.<EClass>filter(cClasses, _function_7);
      StringConcatenation _builder = new StringConcatenation();
      String _copyright = OMLUtilities.copyright();
      _builder.append(_copyright);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("package ");
      _builder.append(packageQName);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("import java.util.Properties");
      _builder.newLine();
      _builder.newLine();
      _builder.append("import ammonite.ops.Path");
      _builder.newLine();
      _builder.newLine();
      _builder.append("import frameless.{Injection, TypedDataset, TypedExpressionEncoder}");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.covariantTag");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.covariantTag.@@");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.tables");
      _builder.newLine();
      _builder.append("import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder");
      _builder.newLine();
      _builder.append("import org.apache.spark.sql.{SQLContext, SaveMode, SparkSession}");
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
      _builder.append("      ");
      _builder.append("import scala.Predef.refArrayOps");
      _builder.newLine();
      _builder.append("\t  ");
      _builder.newLine();
      _builder.append("      ");
      {
        boolean _hasElements_5 = false;
        for(final EClass eClass_3 : eClasses) {
          if (!_hasElements_5) {
            _hasElements_5 = true;
          } else {
            _builder.appendImmediate("\n\n", "      ");
          }
          _builder.append("val ");
          String _tableVariableName_5 = OMLUtilities.tableVariableName(eClass_3);
          _builder.append(_tableVariableName_5, "      ");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append(": Seq[tables.");
          String _name_5 = eClass_3.getName();
          _builder.append(_name_5, "      ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("= spark");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".read");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".parquet((dir / \"");
          String _name_6 = eClass_3.getName();
          _builder.append(_name_6, "        ");
          _builder.append(".parquet\").toIO.getAbsolutePath)");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".map(OMLReaders.");
          String _name_7 = eClass_3.getName();
          _builder.append(_name_7, "        ");
          _builder.append("Row2Tuple)");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".collect()");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".map(OMLReaders.");
          String _name_8 = eClass_3.getName();
          _builder.append(_name_8, "        ");
          _builder.append("Tuple2Type)");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".to[Seq]");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("Success(");
      _builder.newLine();
      _builder.append("  \t    ");
      _builder.append("tables.OMLSpecificationTables");
      {
        boolean _hasElements_6 = false;
        for(final EClass eClass_4 : cClasses) {
          if (!_hasElements_6) {
            _hasElements_6 = true;
            _builder.append("(\n  ", "  \t    ");
          } else {
            _builder.appendImmediate(",\n  ", "  \t    ");
          }
          String _tableVariableName_6 = OMLUtilities.tableVariableName(eClass_4);
          _builder.append(_tableVariableName_6, "  \t    ");
          _builder.append(" = ");
          String _tableVariableName_7 = OMLUtilities.tableVariableName(eClass_4);
          _builder.append(_tableVariableName_7, "  \t    ");
        }
        if (_hasElements_6) {
          _builder.append("\n))", "  \t    ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        final Function1<EPackage, EList<EClassifier>> _function_8 = (EPackage it) -> {
          return it.getEClassifiers();
        };
        final Function1<EEnum, String> _function_9 = (EEnum it) -> {
          return it.getName();
        };
        List<EEnum> _sortBy = IterableExtensions.<EEnum, String>sortBy(Iterables.<EEnum>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function_8)), EEnum.class), _function_9);
        for(final EEnum eClass_5 : _sortBy) {
          _builder.append("  ");
          _builder.append("implicit val ");
          String _firstLower = StringExtensions.toFirstLower(eClass_5.getName());
          _builder.append(_firstLower, "  ");
          _builder.append("I");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": Injection[tables.");
          String _name_9 = eClass_5.getName();
          _builder.append(_name_9, "  ");
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
              String _enumLiteralName = OMLUtilities.enumLiteralName(elit);
              _builder.append(_enumLiteralName, "  \t");
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
              String _enumLiteralName_1 = OMLUtilities.enumLiteralName(elit_1);
              _builder.append(_enumLiteralName_1, "  \t");
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
      {
        for(final EClass eClass_6 : eClasses) {
          _builder.append("  ");
          _builder.append("implicit val ");
          String _tableVariableName_8 = OMLUtilities.tableVariableName(eClass_6);
          _builder.append(_tableVariableName_8, "  ");
          _builder.append("Encoder");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": ExpressionEncoder[tables.");
          String _name_10 = eClass_6.getName();
          _builder.append(_name_10, "  ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= TypedExpressionEncoder[tables.");
          String _name_11 = eClass_6.getName();
          _builder.append(_name_11, "  ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.newLine();
        }
      }
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
      _builder.append(": Unit");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= {");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("dir.toIO.mkdirs()");
      _builder.newLine();
      _builder.newLine();
      {
        for(final EClass eClass_7 : eClasses) {
          _builder.append("      ");
          _builder.append("OMLParquetWriters.write");
          String _upperCaseInitialOrWord = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_7));
          _builder.append(_upperCaseInitialOrWord, "      ");
          _builder.append("(");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append("t.");
          String _tableVariableName_9 = OMLUtilities.tableVariableName(eClass_7);
          _builder.append(_tableVariableName_9, "        ");
          _builder.append(",");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append("(dir / \"");
          String _name_12 = eClass_7.getName();
          _builder.append(_name_12, "        ");
          _builder.append(".parquet\").toIO.getAbsolutePath)");
          _builder.newLineIfNotEmpty();
          _builder.newLine();
        }
      }
      _builder.append("  \t");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def sqlReadOMLSpecificationTables");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(url: String,");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("props: Properties)");
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
      _builder.append("Failure(cause)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append(".apply {");
      _builder.newLine();
      _builder.append("    \t");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("import spark.implicits._");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("import scala.Predef.refArrayOps");
      _builder.newLine();
      _builder.append("\t  ");
      _builder.newLine();
      _builder.append("      ");
      {
        boolean _hasElements_7 = false;
        for(final EClass eClass_8 : eClasses) {
          if (!_hasElements_7) {
            _hasElements_7 = true;
          } else {
            _builder.appendImmediate("\n\n", "      ");
          }
          _builder.append("val ");
          String _tableVariableName_10 = OMLUtilities.tableVariableName(eClass_8);
          _builder.append(_tableVariableName_10, "      ");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append(": Seq[tables.");
          String _name_13 = eClass_8.getName();
          _builder.append(_name_13, "      ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("= spark");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".read");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".jdbc(url, \"OML.");
          String _abbreviatedTableName = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_8);
          _builder.append(_abbreviatedTableName, "        ");
          _builder.append("\", props)");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".map(OMLReaders.");
          String _name_14 = eClass_8.getName();
          _builder.append(_name_14, "        ");
          _builder.append("SQL2Tuple)");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".collect()");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".map(OMLReaders.");
          String _name_15 = eClass_8.getName();
          _builder.append(_name_15, "        ");
          _builder.append("Tuple2Type)");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".to[Seq]");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("Success(");
      _builder.newLine();
      _builder.append("  \t    ");
      _builder.append("tables.OMLSpecificationTables");
      {
        boolean _hasElements_8 = false;
        for(final EClass eClass_9 : cClasses) {
          if (!_hasElements_8) {
            _hasElements_8 = true;
            _builder.append("(\n  ", "  \t    ");
          } else {
            _builder.appendImmediate(",\n  ", "  \t    ");
          }
          String _tableVariableName_11 = OMLUtilities.tableVariableName(eClass_9);
          _builder.append(_tableVariableName_11, "  \t    ");
          _builder.append(" = ");
          String _tableVariableName_12 = OMLUtilities.tableVariableName(eClass_9);
          _builder.append(_tableVariableName_12, "  \t    ");
        }
        if (_hasElements_8) {
          _builder.append("\n))", "  \t    ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def sqlWriteOMLSpecificationTables");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(t: tables.OMLSpecificationTables,");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("url: String,");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("props: Properties)");
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
      _builder.append("Failure(cause)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append(".apply {");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("import spark.implicits._");
      _builder.newLine();
      _builder.append("      ");
      _builder.newLine();
      {
        for(final EClass eClass_10 : cClasses1) {
          _builder.append("      ");
          _builder.append("TypedDataset");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".create(t.");
          String _tableVariableName_13 = OMLUtilities.tableVariableName(eClass_10);
          _builder.append(_tableVariableName_13, "        ");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".dataset");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".map(OMLReaders.");
          String _name_16 = eClass_10.getName();
          _builder.append(_name_16, "        ");
          _builder.append("Type2Tuple)");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".write");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".mode(SaveMode.Append)");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".jdbc(url, \"OML.");
          String _abbreviatedTableName_1 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_10);
          _builder.append(_abbreviatedTableName_1, "        ");
          _builder.append("\", props)");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.newLine();
        }
      }
      _builder.append("\t    ");
      _builder.append("OMLWriters");
      _builder.newLine();
      _builder.append("\t      ");
      _builder.append(".writeRestrictions(");
      _builder.newLine();
      _builder.append("\t        ");
      _builder.append("url, ");
      _builder.newLine();
      _builder.append("\t        ");
      _builder.append("props,");
      _builder.newLine();
      _builder.append("\t        ");
      _builder.append("t.scalars.map(_.uuid),");
      {
        boolean _hasElements_9 = false;
        for(final EClass eClass_11 : restrictions) {
          if (!_hasElements_9) {
            _hasElements_9 = true;
          } else {
            _builder.appendImmediate(",\n    ", "\t        ");
          }
          _builder.newLineIfNotEmpty();
          _builder.append("\t        ");
          _builder.append("t.");
          String _tableVariableName_14 = OMLUtilities.tableVariableName(eClass_11);
          _builder.append(_tableVariableName_14, "\t        ");
          _builder.append(", \"OML.");
          String _abbreviatedTableName_2 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_11);
          _builder.append(_abbreviatedTableName_2, "\t        ");
          _builder.append("\", OMLReaders.");
          String _name_17 = eClass_11.getName();
          _builder.append(_name_17, "\t        ");
          _builder.append("Type2Tuple");
        }
        if (_hasElements_9) {
          _builder.append(")", "\t        ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      {
        for(final EClass eClass_12 : cClasses3) {
          {
            String _name_18 = eClass_12.getName();
            boolean _equals = Objects.equal(_name_18, "RuleBodySegment");
            if (_equals) {
              _builder.append("      ");
              _builder.append("OMLWriters");
              _builder.newLine();
              _builder.append("      ");
              _builder.append("  ");
              _builder.append(".serializeAndWriteRuleBodySegments(");
              _builder.newLine();
              _builder.append("      ");
              _builder.append("    ");
              _builder.append("url,");
              _builder.newLine();
              _builder.append("      ");
              _builder.append("    ");
              _builder.append("props,");
              _builder.newLine();
              _builder.append("      ");
              _builder.append("    ");
              _builder.append("t.ruleBodySegments,");
              _builder.newLine();
              _builder.append("      ");
              _builder.append("    ");
              _builder.append("\"OML.");
              String _abbreviatedTableName_3 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_12);
              _builder.append(_abbreviatedTableName_3, "          ");
              _builder.append("\",");
              _builder.newLineIfNotEmpty();
              _builder.append("      ");
              _builder.append("    ");
              _builder.append("OMLReaders.");
              String _name_19 = eClass_12.getName();
              _builder.append(_name_19, "          ");
              _builder.append("Type2Tuple,");
              _builder.newLineIfNotEmpty();
              _builder.append("      ");
              _builder.append("    ");
              _builder.append("Seq.empty[tables.taggedTypes.");
              String _name_20 = eClass_12.getName();
              _builder.append(_name_20, "          ");
              _builder.append("UUID],");
              _builder.newLineIfNotEmpty();
              _builder.append("      ");
              _builder.append("    ");
              _builder.append("OMLWriters.");
              String _firstLower_1 = StringExtensions.toFirstLower(eClass_12.getName());
              _builder.append(_firstLower_1, "          ");
              _builder.append("Partitioner)");
              _builder.newLineIfNotEmpty();
              _builder.newLine();
            } else {
              String _name_21 = eClass_12.getName();
              boolean _equals_1 = Objects.equal(_name_21, "RestrictionStructuredDataPropertyTuple");
              if (_equals_1) {
                _builder.append("      ");
                _builder.append("OMLWriters");
                _builder.newLine();
                _builder.append("      ");
                _builder.append("  ");
                _builder.append(".serializeAndWriteRestrictionStructuredDataPropertyTuples(");
                _builder.newLine();
                _builder.append("      ");
                _builder.append("    ");
                _builder.append("url,");
                _builder.newLine();
                _builder.append("      ");
                _builder.append("    ");
                _builder.append("props,");
                _builder.newLine();
                _builder.append("      ");
                _builder.append("    ");
                _builder.append("t.restrictionStructuredDataPropertyTuples,");
                _builder.newLine();
                _builder.append("      ");
                _builder.append("    ");
                _builder.append("\"OML.");
                String _abbreviatedTableName_4 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_12);
                _builder.append(_abbreviatedTableName_4, "          ");
                _builder.append("\",");
                _builder.newLineIfNotEmpty();
                _builder.append("      ");
                _builder.append("    ");
                _builder.append("OMLReaders.");
                String _name_22 = eClass_12.getName();
                _builder.append(_name_22, "          ");
                _builder.append("Type2Tuple,");
                _builder.newLineIfNotEmpty();
                _builder.append("      ");
                _builder.append("    ");
                _builder.append("t.entityStructuredDataPropertyParticularRestrictionAxioms.map(_.uuid),");
                _builder.newLine();
                _builder.append("      ");
                _builder.append("    ");
                _builder.append("OMLWriters.");
                String _firstLower_2 = StringExtensions.toFirstLower(eClass_12.getName());
                _builder.append(_firstLower_2, "          ");
                _builder.append("Partitioner)");
                _builder.newLineIfNotEmpty();
                _builder.newLine();
              } else {
                String _name_23 = eClass_12.getName();
                boolean _equals_2 = Objects.equal(_name_23, "AnonymousConceptUnionAxiom");
                if (_equals_2) {
                  _builder.append("      ");
                  _builder.append("OMLWriters");
                  _builder.newLine();
                  _builder.append("      ");
                  _builder.append("  ");
                  _builder.append(".serializeAndWriteAnonymousConceptUnionAxioms(");
                  _builder.newLine();
                  _builder.append("      ");
                  _builder.append("    ");
                  _builder.append("url,");
                  _builder.newLine();
                  _builder.append("      ");
                  _builder.append("    ");
                  _builder.append("props,");
                  _builder.newLine();
                  _builder.append("      ");
                  _builder.append("    ");
                  _builder.append("t.anonymousConceptUnionAxioms,");
                  _builder.newLine();
                  _builder.append("      ");
                  _builder.append("    ");
                  _builder.append("\"OML.");
                  String _abbreviatedTableName_5 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_12);
                  _builder.append(_abbreviatedTableName_5, "          ");
                  _builder.append("\",");
                  _builder.newLineIfNotEmpty();
                  _builder.append("      ");
                  _builder.append("    ");
                  _builder.append("OMLReaders.");
                  String _name_24 = eClass_12.getName();
                  _builder.append(_name_24, "          ");
                  _builder.append("Type2Tuple,");
                  _builder.newLineIfNotEmpty();
                  _builder.append("      ");
                  _builder.append("    ");
                  _builder.append("t.rootConceptTaxonomyAxioms.map(_.uuid),");
                  _builder.newLine();
                  _builder.append("      ");
                  _builder.append("    ");
                  _builder.append("OMLWriters.");
                  String _firstLower_3 = StringExtensions.toFirstLower(eClass_12.getName());
                  _builder.append(_firstLower_3, "          ");
                  _builder.append("Partitioner)");
                  _builder.newLineIfNotEmpty();
                  _builder.newLine();
                } else {
                  String _name_25 = eClass_12.getName();
                  boolean _equals_3 = Objects.equal(_name_25, "StructuredDataPropertyTuple");
                  if (_equals_3) {
                    _builder.append("      ");
                    _builder.append("OMLWriters");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("  ");
                    _builder.append(".serializeAndWriteStructuredDataPropertyTuples(");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("    ");
                    _builder.append("url,");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("    ");
                    _builder.append("props,");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("    ");
                    _builder.append("t.structuredDataPropertyTuples,");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("    ");
                    _builder.append("\"OML.");
                    String _abbreviatedTableName_6 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_12);
                    _builder.append(_abbreviatedTableName_6, "          ");
                    _builder.append("\",");
                    _builder.newLineIfNotEmpty();
                    _builder.append("      ");
                    _builder.append("    ");
                    _builder.append("OMLReaders.");
                    String _name_26 = eClass_12.getName();
                    _builder.append(_name_26, "          ");
                    _builder.append("Type2Tuple,");
                    _builder.newLineIfNotEmpty();
                    _builder.append("      ");
                    _builder.append("    ");
                    _builder.append("t.singletonInstanceStructuredDataPropertyValues.map(_.uuid),");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("    ");
                    _builder.append("OMLWriters.");
                    String _firstLower_4 = StringExtensions.toFirstLower(eClass_12.getName());
                    _builder.append(_firstLower_4, "          ");
                    _builder.append("Partitioner)");
                    _builder.newLineIfNotEmpty();
                    _builder.newLine();
                  } else {
                    _builder.append("      ");
                    _builder.append("TypedDataset");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("  ");
                    _builder.append(".create(t.");
                    String _tableVariableName_15 = OMLUtilities.tableVariableName(eClass_12);
                    _builder.append(_tableVariableName_15, "        ");
                    _builder.append(")");
                    _builder.newLineIfNotEmpty();
                    _builder.append("      ");
                    _builder.append("  ");
                    _builder.append(".dataset");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("  ");
                    _builder.append(".map(OMLReaders.");
                    String _name_27 = eClass_12.getName();
                    _builder.append(_name_27, "        ");
                    _builder.append("Type2Tuple)");
                    _builder.newLineIfNotEmpty();
                    _builder.append("      ");
                    _builder.append("  ");
                    _builder.append(".write");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("  ");
                    _builder.append(".mode(SaveMode.Append)");
                    _builder.newLine();
                    _builder.append("      ");
                    _builder.append("  ");
                    _builder.append(".jdbc(url, \"OML.");
                    String _abbreviatedTableName_7 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_12);
                    _builder.append(_abbreviatedTableName_7, "        ");
                    _builder.append("\", props)");
                    _builder.newLineIfNotEmpty();
                    _builder.newLine();
                  }
                }
              }
            }
          }
        }
      }
      _builder.append("  \t  ");
      _builder.append("Success(())");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.newLine();
      _builder.append("case class ");
      _builder.append(tableName);
      _builder.newLineIfNotEmpty();
      {
        boolean _hasElements_10 = false;
        for(final EClass eClass_13 : eClasses) {
          if (!_hasElements_10) {
            _hasElements_10 = true;
            _builder.append("(\n  ");
          } else {
            _builder.appendImmediate(",\n\n  ", "");
          }
          String _tableVariableName_16 = OMLUtilities.tableVariableName(eClass_13);
          _builder.append(_tableVariableName_16);
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append(": TypedDataset[api.");
          String _name_28 = eClass_13.getName();
          _builder.append(_name_28, "  ");
          _builder.append("]");
        }
        if (_hasElements_10) {
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
