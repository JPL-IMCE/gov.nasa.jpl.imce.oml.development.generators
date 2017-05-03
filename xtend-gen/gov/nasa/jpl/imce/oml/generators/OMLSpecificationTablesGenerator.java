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
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

public class OMLSpecificationTablesGenerator extends OMLUtilities {
  public static void main(final String[] args) {
    int _length = args.length;
    boolean _notEquals = (1 != _length);
    if (_notEquals) {
      System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.tables project");
      System.exit(1);
    }
    new OMLSpecificationTablesGenerator().generate(args[0]);
  }
  
  public void generate(final String targetDir) {
    final Path bundlePath = Paths.get(targetDir);
    final Path oml_Folder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/tables");
    oml_Folder.toFile().mkdirs();
    this.generate(
      Collections.<EPackage>unmodifiableList(CollectionLiterals.<EPackage>newArrayList(this.c, this.t, this.g, this.b, this.d)), 
      oml_Folder.toAbsolutePath().toString(), 
      "gov.nasa.jpl.imce.oml.tables", 
      "OMLSpecificationTables");
    final Path oml2oti_Folder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/provenance/oti");
    oml2oti_Folder.toFile().mkdirs();
    this.generate(
      Collections.<EPackage>unmodifiableList(CollectionLiterals.<EPackage>newArrayList(this.oml2oti)), 
      oml2oti_Folder.toAbsolutePath().toString(), 
      "gov.nasa.jpl.imce.oml.provenance.oti", 
      "OML2OTIProvenanceTables");
  }
  
  public void generate(final List<EPackage> ePackages, final String targetFolder, final String packageQName, final String tableName) {
    try {
      File _file = new File(((targetFolder + File.separator) + "package.scala"));
      final FileOutputStream packageFile = new FileOutputStream(_file);
      try {
        packageFile.write(this.generatePackageFile(ePackages, packageQName).getBytes());
      } finally {
        packageFile.close();
      }
      File _file_1 = new File((((targetFolder + File.separator) + tableName) + ".scala"));
      final FileOutputStream tablesFile = new FileOutputStream(_file_1);
      try {
        tablesFile.write(this.generateTablesFile(ePackages, packageQName, tableName).getBytes());
      } finally {
        tablesFile.close();
      }
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return OMLUtilities.isFunctionalAPI(it);
      };
      Iterable<EClass> _filter = IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1);
      for (final EClass eClass : _filter) {
        {
          String _name = eClass.getName();
          String _plus = ((targetFolder + File.separator) + _name);
          String _plus_1 = (_plus + ".scala");
          File _file_2 = new File(_plus_1);
          final FileOutputStream classFile = new FileOutputStream(_file_2);
          try {
            classFile.write(this.generateClassFile(eClass, packageQName).getBytes());
          } finally {
            classFile.close();
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public String generateTablesFile(final List<EPackage> ePackages, final String packageQName, final String tableName) {
    String _xblockexpression = null;
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return Boolean.valueOf((((OMLUtilities.isFunctionalAPI(it)).booleanValue() && (!it.isInterface())) && (!(OMLUtilities.isValueTable(it)).booleanValue())));
      };
      Iterable<EClass> _filter = IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1);
      OMLUtilities.OMLTableCompare _oMLTableCompare = new OMLUtilities.OMLTableCompare();
      final List<EClass> eClasses = IterableExtensions.<EClass>sortWith(_filter, _oMLTableCompare);
      StringConcatenation _builder = new StringConcatenation();
      String _copyright = OMLUtilities.copyright();
      _builder.append(_copyright);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("package ");
      _builder.append(packageQName);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("import java.io.{File,InputStream}");
      _builder.newLine();
      _builder.append("import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipFile}");
      _builder.newLine();
      _builder.newLine();
      {
        boolean _equals = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals) {
          _builder.append("import scala.collection.immutable.{Map,Seq}");
          _builder.newLine();
        } else {
          _builder.append("import scala.collection.immutable.Seq");
          _builder.newLine();
        }
      }
      _builder.append("import scala.collection.JavaConversions._");
      _builder.newLine();
      _builder.append("import scala.util.control.Exception._");
      _builder.newLine();
      _builder.append("import scala.util.{Failure,Success,Try}");
      _builder.newLine();
      _builder.append("import scala.{Boolean,Unit}");
      _builder.newLine();
      {
        boolean _equals_1 = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals_1) {
          _builder.append("import scala.Predef.ArrowAssoc");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("case class ");
      _builder.append(tableName);
      _builder.newLineIfNotEmpty();
      {
        boolean _equals_2 = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals_2) {
          {
            boolean _hasElements = false;
            for(final EClass eClass : eClasses) {
              if (!_hasElements) {
                _hasElements = true;
                _builder.append("(\n  ");
              } else {
                _builder.appendImmediate(",\n  ", "");
              }
              String _tableVariable = OMLSpecificationTablesGenerator.tableVariable(eClass);
              _builder.append(_tableVariable);
            }
            if (_hasElements) {
              _builder.append(",");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("annotations: Map[AnnotationProperty, Seq[AnnotationEntry]] = Map.empty)");
          _builder.newLine();
        } else {
          {
            boolean _hasElements_1 = false;
            for(final EClass eClass_1 : eClasses) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
                _builder.append("(\n  ");
              } else {
                _builder.appendImmediate(",\n  ", "");
              }
              String _tableVariable_1 = OMLSpecificationTablesGenerator.tableVariable(eClass_1);
              _builder.append(_tableVariable_1);
            }
            if (_hasElements_1) {
              _builder.append("\n)");
            }
          }
          _builder.append(" ");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("{");
      _builder.newLine();
      {
        for(final EClass eClass_2 : eClasses) {
          _builder.append("  ");
          String _tableReader = OMLSpecificationTablesGenerator.tableReader(eClass_2, tableName);
          _builder.append(_tableReader, "  ");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def isEmpty: Boolean");
      _builder.newLine();
      {
        boolean _equals_3 = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals_3) {
          _builder.append("  ");
          {
            boolean _hasElements_2 = false;
            for(final EClass eClass_3 : eClasses) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
                _builder.append("= ", "  ");
              } else {
                _builder.appendImmediate(" &&\n  ", "  ");
              }
              String _tableVariableName = OMLUtilities.tableVariableName(eClass_3);
              _builder.append(_tableVariableName, "  ");
              _builder.append(".isEmpty");
            }
            if (_hasElements_2) {
              _builder.append(" &&\n  annotations.isEmpty", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
        } else {
          _builder.append("  ");
          {
            boolean _hasElements_3 = false;
            for(final EClass eClass_4 : eClasses) {
              if (!_hasElements_3) {
                _hasElements_3 = true;
                _builder.append("= ", "  ");
              } else {
                _builder.appendImmediate(" &&\n  ", "  ");
              }
              String _tableVariableName_1 = OMLUtilities.tableVariableName(eClass_4);
              _builder.append(_tableVariableName_1, "  ");
              _builder.append(".isEmpty");
            }
          }
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("object ");
      _builder.append(tableName);
      _builder.append(" {");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def createEmpty");
      _builder.append(tableName, "  ");
      _builder.append("()");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append(": ");
      _builder.append(tableName, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= new ");
      _builder.append(tableName, "  ");
      _builder.append("()");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def load");
      _builder.append(tableName, "  ");
      _builder.append("(omlSchemaJsonZipFile: File)");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append(": Try[");
      _builder.append(tableName, "  ");
      _builder.append("]");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= nonFatalCatch[Try[");
      _builder.append(tableName, "  ");
      _builder.append("]]");
      _builder.newLineIfNotEmpty();
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
      _builder.append("      ");
      _builder.append("val zipFile = new ZipFile(omlSchemaJsonZipFile)");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("val omlTables =");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("zipFile");
      _builder.newLine();
      _builder.append("        ");
      _builder.append(".getEntries");
      _builder.newLine();
      _builder.append("        ");
      _builder.append(".toIterable");
      _builder.newLine();
      _builder.append("        ");
      _builder.append(".par");
      _builder.newLine();
      _builder.append("         ");
      _builder.append(".aggregate(");
      _builder.append(tableName, "         ");
      _builder.append("())(seqop = readZipArchive(zipFile), combop = mergeTables)");
      _builder.newLineIfNotEmpty();
      _builder.append("      ");
      _builder.append("zipFile.close()");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("Success(omlTables)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def mergeTables");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(t1: ");
      _builder.append(tableName, "  ");
      _builder.append(", t2: ");
      _builder.append(tableName, "  ");
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append(": ");
      _builder.append(tableName, "  ");
      _builder.newLineIfNotEmpty();
      {
        boolean _equals_4 = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals_4) {
          _builder.append("  ");
          _builder.append("= ");
          {
            boolean _hasElements_4 = false;
            for(final EClass eClass_5 : eClasses) {
              if (!_hasElements_4) {
                _hasElements_4 = true;
                _builder.append((tableName + "(\n    "), "  ");
              } else {
                _builder.appendImmediate(",\n    ", "  ");
              }
              String _tableVariableName_2 = OMLUtilities.tableVariableName(eClass_5);
              _builder.append(_tableVariableName_2, "  ");
              _builder.append(" = t1.");
              String _tableVariableName_3 = OMLUtilities.tableVariableName(eClass_5);
              _builder.append(_tableVariableName_3, "  ");
              _builder.append(" ++ t2.");
              String _tableVariableName_4 = OMLUtilities.tableVariableName(eClass_5);
              _builder.append(_tableVariableName_4, "  ");
            }
            if (_hasElements_4) {
              _builder.append(",\n    annotations = t1.annotations ++ t2.annotations)", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
        } else {
          _builder.append("  ");
          _builder.append("= ");
          {
            boolean _hasElements_5 = false;
            for(final EClass eClass_6 : eClasses) {
              if (!_hasElements_5) {
                _hasElements_5 = true;
                _builder.append((tableName + "(\n    "), "  ");
              } else {
                _builder.appendImmediate(",\n    ", "  ");
              }
              String _tableVariableName_5 = OMLUtilities.tableVariableName(eClass_6);
              _builder.append(_tableVariableName_5, "  ");
              _builder.append(" = t1.");
              String _tableVariableName_6 = OMLUtilities.tableVariableName(eClass_6);
              _builder.append(_tableVariableName_6, "  ");
              _builder.append(" ++ t2.");
              String _tableVariableName_7 = OMLUtilities.tableVariableName(eClass_6);
              _builder.append(_tableVariableName_7, "  ");
            }
            if (_hasElements_5) {
              _builder.append(")", "  ");
            }
          }
          _builder.append(" ");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def readZipArchive");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(zipFile: ZipFile)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("(tables: ");
      _builder.append(tableName, "  ");
      _builder.append(", ze: ZipArchiveEntry)");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append(": ");
      _builder.append(tableName, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("val is = zipFile.getInputStream(ze)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("ze.getName match {");
      _builder.newLine();
      {
        for(final EClass eClass_7 : eClasses) {
          _builder.append("  \t  ");
          _builder.append("case ");
          String _name = eClass_7.getName();
          _builder.append(_name, "  \t  ");
          _builder.append("Helper.TABLE_JSON_FILENAME =>");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t  ");
          _builder.append("  ");
          _builder.append("tables.");
          String _tableReaderName = OMLSpecificationTablesGenerator.tableReaderName(eClass_7);
          _builder.append(_tableReaderName, "  \t    ");
          _builder.append("(is)");
          _builder.newLineIfNotEmpty();
        }
      }
      {
        boolean _equals_5 = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals_5) {
          _builder.append("      ");
          _builder.append("case annotationPropertyIRI =>");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append("tables");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("    ");
          _builder.append(".annotationProperties");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("    ");
          _builder.append(".find(_.iri == annotationPropertyIRI)");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("    ");
          _builder.append(".fold[OMLSpecificationTables](tables) { ap =>");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("    ");
          _builder.append("val annotationPropertyTable = ap -> readJSonTable[AnnotationEntry](is, AnnotationEntryHelper.fromJSON)");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("    ");
          _builder.append("tables.copy(annotations = tables.annotations + annotationPropertyTable)");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def save");
      _builder.append(tableName, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("(tables: ");
      _builder.append(tableName, "  ");
      _builder.append(",");
      _builder.newLineIfNotEmpty();
      _builder.append("   ");
      _builder.append("omlSchemaJsonZipFile: File)");
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
      _builder.append("  \t  ");
      _builder.append("// @see http://www.oracle.com/technetwork/articles/java/compress-1565076.html");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("val fos = new java.io.FileOutputStream(omlSchemaJsonZipFile)");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("val bos = new java.io.BufferedOutputStream(fos, 100000)");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("val cos = new java.util.zip.CheckedOutputStream(bos, new java.util.zip.Adler32())");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("val zos = new java.util.zip.ZipOutputStream(new java.io.BufferedOutputStream(cos))");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("zos.setMethod(java.util.zip.ZipOutputStream.DEFLATED)");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      {
        for(final EClass eClass_8 : eClasses) {
          _builder.append("      ");
          _builder.append("zos.putNextEntry(new java.util.zip.ZipEntry(");
          String _name_1 = eClass_8.getName();
          _builder.append(_name_1, "      ");
          _builder.append("Helper.TABLE_JSON_FILENAME))");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("tables.");
          String _tableVariableName_8 = OMLUtilities.tableVariableName(eClass_8);
          _builder.append(_tableVariableName_8, "      ");
          _builder.append(".foreach { t =>");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("   ");
          _builder.append("val line = ");
          String _name_2 = eClass_8.getName();
          _builder.append(_name_2, "         ");
          _builder.append("Helper.toJSON(t)+\"\\n\"");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("   ");
          _builder.append("zos.write(line.getBytes(java.nio.charset.Charset.forName(\"UTF-8\")))");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("zos.closeEntry()");
          _builder.newLine();
        }
      }
      _builder.append("      ");
      _builder.newLine();
      {
        boolean _equals_6 = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals_6) {
          _builder.append("      ");
          _builder.append("tables");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".annotationProperties");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append(".foreach { ap =>");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("    ");
          _builder.append("tables");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("      ");
          _builder.append(".annotations");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("      ");
          _builder.append(".get(ap)");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("      ");
          _builder.append(".foreach { as =>");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("        ");
          _builder.append("zos.putNextEntry(new java.util.zip.ZipEntry(ap.iri))");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("        ");
          _builder.append("as.foreach { a =>");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("          ");
          _builder.append("val line = AnnotationEntryHelper.toJSON(a)+\"\\n\"");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("          ");
          _builder.append("zos.write(line.getBytes(java.nio.charset.Charset.forName(\"UTF-8\")))");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("        ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("        ");
          _builder.append("zos.closeEntry()");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("      ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("      ");
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.append("  ");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("zos.close()");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("Success(())");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public static String tableReaderName(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("read");
    String _pluralize = OMLUtilities.pluralize(eClass.getName());
    _builder.append(_pluralize);
    return _builder.toString();
  }
  
  public static String tableVariable(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    String _tableVariableName = OMLUtilities.tableVariableName(eClass);
    _builder.append(_tableVariableName);
    _builder.append(" : Seq[");
    String _name = eClass.getName();
    _builder.append(_name);
    _builder.append("] = Seq.empty");
    return _builder.toString();
  }
  
  public static String tableReader(final EClass eClass, final String tableName) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("def ");
    String _tableReaderName = OMLSpecificationTablesGenerator.tableReaderName(eClass);
    _builder.append(_tableReaderName);
    _builder.append("(is: InputStream)");
    _builder.newLineIfNotEmpty();
    _builder.append(": ");
    _builder.append(tableName);
    _builder.newLineIfNotEmpty();
    _builder.append("= copy(");
    String _tableVariableName = OMLUtilities.tableVariableName(eClass);
    _builder.append(_tableVariableName);
    _builder.append(" = readJSonTable(is, ");
    String _name = eClass.getName();
    _builder.append(_name);
    _builder.append("Helper.fromJSON))");
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public void generateJS(final EPackage ePackage, final String targetJSFolder) {
    try {
      final Function1<EClass, Boolean> _function = (EClass it) -> {
        return Boolean.valueOf(((OMLUtilities.isFunctionalAPI(it)).booleanValue() && (OMLUtilities.hasSchemaOptionalAttributes(it)).booleanValue()));
      };
      Iterable<EClass> _filter = IterableExtensions.<EClass>filter(Iterables.<EClass>filter(ePackage.getEClassifiers(), EClass.class), _function);
      for (final EClass eClass : _filter) {
        {
          String _name = eClass.getName();
          String _plus = ((targetJSFolder + File.separator) + _name);
          String _plus_1 = (_plus + "JS.scala");
          File _file = new File(_plus_1);
          final FileOutputStream classFile = new FileOutputStream(_file);
          try {
            classFile.write(this.generateJSClassFile(eClass).getBytes());
          } finally {
            classFile.close();
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void generateJVM(final EPackage ePackage, final String targetJVMFolder) {
    try {
      final Function1<EClass, Boolean> _function = (EClass it) -> {
        return Boolean.valueOf(((OMLUtilities.isFunctionalAPI(it)).booleanValue() && (OMLUtilities.hasSchemaOptionalAttributes(it)).booleanValue()));
      };
      Iterable<EClass> _filter = IterableExtensions.<EClass>filter(Iterables.<EClass>filter(ePackage.getEClassifiers(), EClass.class), _function);
      for (final EClass eClass : _filter) {
        {
          String _name = eClass.getName();
          String _plus = ((targetJVMFolder + File.separator) + _name);
          String _plus_1 = (_plus + "Java.scala");
          File _file = new File(_plus_1);
          final FileOutputStream classFile = new FileOutputStream(_file);
          try {
            classFile.write(this.generateJVMClassFile(eClass).getBytes());
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
    _builder.append("import java.io.InputStream");
    _builder.newLine();
    _builder.append("import scala.collection.immutable.Seq");
    _builder.newLine();
    _builder.append("import scala.io");
    _builder.newLine();
    _builder.append("import scala.Predef.String");
    _builder.newLine();
    _builder.newLine();
    _builder.append("package object ");
    int _lastIndexOf = packageQName.lastIndexOf(".");
    int _plus = (_lastIndexOf + 1);
    String _substring_1 = packageQName.substring(_plus);
    _builder.append(_substring_1);
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EDataType, Boolean> _function_1 = (EDataType t) -> {
        return Boolean.valueOf((!(t instanceof EEnum)));
      };
      final Function1<EDataType, String> _function_2 = (EDataType it) -> {
        return it.getName();
      };
      List<EDataType> _sortBy = IterableExtensions.<EDataType, String>sortBy(IterableExtensions.<EDataType>filter(Iterables.<EDataType>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EDataType.class), _function_1), _function_2);
      for(final EDataType type : _sortBy) {
        _builder.append("\t");
        _builder.append("type ");
        String _name = type.getName();
        _builder.append(_name, "\t");
        _builder.append(" = String");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  \t");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("def readJSonTable[T](is: InputStream, fromJSon: String => T)");
    _builder.newLine();
    _builder.append("  ");
    _builder.append(": Seq[T]");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("= io.Source.fromInputStream(is).getLines.map(fromJSon).to[Seq]");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function_3 = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_4 = (EClass it) -> {
        return OMLUtilities.isFunctionalAPIWithOrderingKeys(it);
      };
      final Function1<EClass, String> _function_5 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy_1 = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function_3)), EClass.class), _function_4), _function_5);
      for(final EClass eClass : _sortBy_1) {
        _builder.append("  ");
        _builder.append("implicit def ");
        String _firstLower = StringExtensions.toFirstLower(eClass.getName());
        _builder.append(_firstLower, "  ");
        _builder.append("Ordering");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(": scala.Ordering[");
        String _name_1 = eClass.getName();
        _builder.append(_name_1, "  ");
        _builder.append("]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= new scala.Ordering[");
        String _name_2 = eClass.getName();
        _builder.append(_name_2, "  ");
        _builder.append("] {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append("def compare(x: ");
        String _name_3 = eClass.getName();
        _builder.append(_name_3, "  \t");
        _builder.append(", y: ");
        String _name_4 = eClass.getName();
        _builder.append(_name_4, "  \t");
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
            String _orderingTableType = OMLUtilities.orderingTableType(keyFeature);
            _builder.append(_orderingTableType, "  \t");
            _builder.append(" match {");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            _builder.append("\t");
            _builder.append(" \t");
            _builder.append("case c_");
            String _columnName = OMLUtilities.columnName(keyFeature);
            _builder.append(_columnName, "  \t \t");
            _builder.append(" if 0 != c_");
            String _columnName_1 = OMLUtilities.columnName(keyFeature);
            _builder.append(_columnName_1, "  \t \t");
            _builder.append(" => c_");
            String _columnName_2 = OMLUtilities.columnName(keyFeature);
            _builder.append(_columnName_2, "  \t \t");
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
  
  public String generateClassFile(final EClass eClass, final String packageQName) {
    String _xblockexpression = null;
    {
      final EStructuralFeature uuid = OMLUtilities.lookupUUIDFeature(eClass);
      final Function1<EReference, Boolean> _function = (EReference it) -> {
        return Boolean.valueOf(it.isContainer());
      };
      final EReference container = IterableExtensions.<EReference>findFirst(Iterables.<EReference>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), EReference.class), _function);
      final EStructuralFeature uuidNS = OMLUtilities.lookupUUIDNamespaceFeature(eClass);
      final Iterable<EStructuralFeature> uuidFactors = OMLUtilities.lookupUUIDNamespaceFactors(eClass);
      final Function1<EStructuralFeature, Boolean> _function_1 = (EStructuralFeature it) -> {
        return OMLUtilities.isUUIDFeature(it);
      };
      final Iterable<EStructuralFeature> pairs = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_1);
      final boolean uuidWithGenerator = ((null != uuidNS) && (null != uuidFactors));
      final boolean uuidWithoutContainer = (((null != uuid) && (null == container)) && (null != uuidNS));
      final boolean uuidWithContainer = ((null != uuid) && (null != container));
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
      _builder.append("import scala.annotation.meta.field");
      _builder.newLine();
      _builder.append("import scala.scalajs.js.annotation.{JSExport,JSExportTopLevel}");
      _builder.newLine();
      _builder.append("import scala._");
      _builder.newLine();
      _builder.append("import scala.Predef._");
      _builder.newLine();
      _builder.newLine();
      _builder.append("/**");
      _builder.newLine();
      {
        Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
        for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
          _builder.append("  ");
          _builder.append("* @param ");
          String _columnName = OMLUtilities.columnName(attr);
          _builder.append(_columnName, "  ");
          _builder.append("[");
          int _lowerBound = attr.getLowerBound();
          _builder.append(_lowerBound, "  ");
          _builder.append(",");
          int _upperBound = attr.getUpperBound();
          _builder.append(_upperBound, "  ");
          _builder.append("]");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("  ");
      _builder.append("*/");
      _builder.newLine();
      {
        Boolean _hasSchemaOptionalAttributes = OMLUtilities.hasSchemaOptionalAttributes(eClass);
        boolean _not = (!(_hasSchemaOptionalAttributes).booleanValue());
        if (_not) {
          _builder.append("@JSExportTopLevel(\"");
          String _name = eClass.getName();
          _builder.append(_name);
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("case class ");
      String _name_1 = eClass.getName();
      _builder.append(_name_1);
      _builder.newLineIfNotEmpty();
      _builder.append("(");
      _builder.newLine();
      {
        Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_1 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
        boolean _hasElements = false;
        for(final ETypedElement attr_1 : _schemaAPIOrOrderingKeyAttributes_1) {
          if (!_hasElements) {
            _hasElements = true;
          } else {
            _builder.appendImmediate(",", "  ");
          }
          _builder.append("  ");
          _builder.append("@(JSExport @field) ");
          String _columnName_1 = OMLUtilities.columnName(attr_1);
          _builder.append(_columnName_1, "  ");
          _builder.append(": ");
          String _constructorTypeName = OMLUtilities.constructorTypeName(attr_1);
          _builder.append(_constructorTypeName, "  ");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append(") {");
      _builder.newLine();
      {
        Boolean _hasSchemaOptionalAttributes_1 = OMLUtilities.hasSchemaOptionalAttributes(eClass);
        if ((_hasSchemaOptionalAttributes_1).booleanValue()) {
          _builder.append("  ");
          _builder.append("def this(");
          _builder.newLine();
          _builder.append("  ");
          {
            final Function1<ETypedElement, Boolean> _function_2 = (ETypedElement a) -> {
              int _lowerBound_1 = a.getLowerBound();
              return Boolean.valueOf((_lowerBound_1 > 0));
            };
            Iterable<ETypedElement> _filter = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_2);
            boolean _hasElements_1 = false;
            for(final ETypedElement attr_2 : _filter) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
              } else {
                _builder.appendImmediate(",\n", "  ");
              }
              _builder.append("  ");
              String _columnName_2 = OMLUtilities.columnName(attr_2);
              _builder.append(_columnName_2, "  ");
              _builder.append(": ");
              String _constructorTypeName_1 = OMLUtilities.constructorTypeName(attr_2);
              _builder.append(_constructorTypeName_1, "  ");
            }
            if (_hasElements_1) {
              _builder.append(")", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("= this(");
          _builder.newLine();
          _builder.append("  ");
          {
            Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_2 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
            boolean _hasElements_2 = false;
            for(final ETypedElement attr_3 : _schemaAPIOrOrderingKeyAttributes_2) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
              } else {
                _builder.appendImmediate(",\n", "  ");
              }
              {
                int _lowerBound_1 = attr_3.getLowerBound();
                boolean _greaterThan = (_lowerBound_1 > 0);
                if (_greaterThan) {
                  _builder.append("    ");
                  String _columnName_3 = OMLUtilities.columnName(attr_3);
                  _builder.append(_columnName_3, "  ");
                } else {
                  _builder.append("    None /* ");
                  String _columnName_4 = OMLUtilities.columnName(attr_3);
                  _builder.append(_columnName_4, "  ");
                  _builder.append(" */");
                }
              }
            }
            if (_hasElements_2) {
              _builder.append(")\n", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.newLine();
          {
            final Function1<ETypedElement, Boolean> _function_3 = (ETypedElement a) -> {
              int _lowerBound_2 = a.getLowerBound();
              return Boolean.valueOf((_lowerBound_2 == 0));
            };
            Iterable<ETypedElement> _filter_1 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_3);
            boolean _hasElements_3 = false;
            for(final ETypedElement attr_4 : _filter_1) {
              if (!_hasElements_3) {
                _hasElements_3 = true;
              } else {
                _builder.appendImmediate("", "  ");
              }
              _builder.append("  ");
              _builder.append("def with");
              String _firstUpper = StringExtensions.toFirstUpper(OMLUtilities.columnName(attr_4));
              _builder.append(_firstUpper, "  ");
              _builder.append("(l: ");
              String _scalaTableTypeName = OMLUtilities.scalaTableTypeName(attr_4);
              _builder.append(_scalaTableTypeName, "  ");
              _builder.append(")\t ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": ");
              String _name_2 = eClass.getName();
              _builder.append(_name_2, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= copy(");
              String _columnName_5 = OMLUtilities.columnName(attr_4);
              _builder.append(_columnName_5, "  ");
              _builder.append("=Some(l))");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.newLine();
            }
          }
        }
      }
      {
        if (uuidWithoutContainer) {
          {
            final Function1<ETypedElement, Boolean> _function_4 = (ETypedElement a) -> {
              return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
            };
            Iterable<ETypedElement> _filter_2 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_4);
            boolean _hasElements_4 = false;
            for(final ETypedElement attr_5 : _filter_2) {
              if (!_hasElements_4) {
                _hasElements_4 = true;
                _builder.append("  // Ctor(uuidWithoutContainer)\n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n");
              } else {
                _builder.appendImmediate(",\n", "");
              }
              _builder.append("    ");
              String _columnName_6 = OMLUtilities.columnName(attr_5);
              _builder.append(_columnName_6);
              _builder.append(": ");
              String _constructorTypeName_2 = OMLUtilities.constructorTypeName(attr_5);
              _builder.append(_constructorTypeName_2);
            }
            if (_hasElements_4) {
              String _name_3 = uuidNS.getName();
              String _plus = (")\n  = this(\n      oug.namespaceUUID(\n        " + _name_3);
              String _plus_1 = (_plus + ".toString");
              _builder.append(_plus_1);
            }
          }
          {
            boolean _hasElements_5 = false;
            for(final EStructuralFeature f : uuidFactors) {
              if (!_hasElements_5) {
                _hasElements_5 = true;
              } else {
                _builder.appendImmediate(",", "");
              }
              _builder.append(",");
              _builder.newLineIfNotEmpty();
              _builder.append("        ");
              _builder.append("\"");
              String _name_4 = f.getName();
              _builder.append(_name_4, "        ");
              _builder.append("\" -> ");
              String _name_5 = f.getName();
              _builder.append(_name_5, "        ");
            }
          }
          {
            final Function1<ETypedElement, Boolean> _function_5 = (ETypedElement a) -> {
              return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
            };
            Iterable<ETypedElement> _filter_3 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_5);
            boolean _hasElements_6 = false;
            for(final ETypedElement attr_6 : _filter_3) {
              if (!_hasElements_6) {
                _hasElements_6 = true;
                _builder.append(").toString,\n");
              } else {
                _builder.appendImmediate(",\n", "");
              }
              _builder.append("      ");
              String _columnName_7 = OMLUtilities.columnName(attr_6);
              _builder.append(_columnName_7);
            }
            if (_hasElements_6) {
              _builder.append(")\n");
            }
          }
          _builder.newLineIfNotEmpty();
        } else {
          if (uuidWithGenerator) {
            {
              final Function1<ETypedElement, Boolean> _function_6 = (ETypedElement a) -> {
                return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
              };
              Iterable<ETypedElement> _filter_4 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_6);
              boolean _hasElements_7 = false;
              for(final ETypedElement attr_7 : _filter_4) {
                if (!_hasElements_7) {
                  _hasElements_7 = true;
                  _builder.append("  // Ctor(uuidWithGenerator)   \n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n");
                } else {
                  _builder.appendImmediate(",\n", "");
                }
                _builder.append("    ");
                String _columnName_8 = OMLUtilities.columnName(attr_7);
                _builder.append(_columnName_8);
                _builder.append(": ");
                String _constructorTypeName_3 = OMLUtilities.constructorTypeName(attr_7);
                _builder.append(_constructorTypeName_3);
              }
              if (_hasElements_7) {
                String _name_6 = uuidNS.getName();
                String _plus_2 = (")\n  = this(\n      oug.namespaceUUID(\n        " + _name_6);
                String _plus_3 = (_plus_2 + "UUID");
                _builder.append(_plus_3);
              }
            }
            {
              boolean _hasElements_8 = false;
              for(final EStructuralFeature f_1 : uuidFactors) {
                if (!_hasElements_8) {
                  _hasElements_8 = true;
                } else {
                  _builder.appendImmediate(",", "");
                }
                _builder.append(",");
                _builder.newLineIfNotEmpty();
                _builder.append("        ");
                _builder.append("\"");
                String _name_7 = f_1.getName();
                _builder.append(_name_7, "        ");
                _builder.append("\" -> ");
                String _name_8 = f_1.getName();
                _builder.append(_name_8, "        ");
              }
            }
            {
              final Function1<ETypedElement, Boolean> _function_7 = (ETypedElement a) -> {
                return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
              };
              Iterable<ETypedElement> _filter_5 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_7);
              boolean _hasElements_9 = false;
              for(final ETypedElement attr_8 : _filter_5) {
                if (!_hasElements_9) {
                  _hasElements_9 = true;
                  _builder.append(").toString,\n");
                } else {
                  _builder.appendImmediate(",\n", "");
                }
                _builder.append("      ");
                String _columnName_9 = OMLUtilities.columnName(attr_8);
                _builder.append(_columnName_9);
              }
              if (_hasElements_9) {
                _builder.append(")\n");
              }
            }
            _builder.newLineIfNotEmpty();
          } else {
            if (uuidWithContainer) {
              {
                final Function1<ETypedElement, Boolean> _function_8 = (ETypedElement a) -> {
                  return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
                };
                Iterable<ETypedElement> _filter_6 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_8);
                boolean _hasElements_10 = false;
                for(final ETypedElement attr_9 : _filter_6) {
                  if (!_hasElements_10) {
                    _hasElements_10 = true;
                    _builder.append("  // Ctor(uuidWithContainer)   \n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n");
                  } else {
                    _builder.appendImmediate(",\n", "");
                  }
                  _builder.append("    ");
                  String _columnName_10 = OMLUtilities.columnName(attr_9);
                  _builder.append(_columnName_10);
                  _builder.append(": ");
                  String _constructorTypeName_4 = OMLUtilities.constructorTypeName(attr_9);
                  _builder.append(_constructorTypeName_4);
                }
                if (_hasElements_10) {
                  String _name_9 = eClass.getName();
                  String _plus_4 = (")\n  = this(\n      oug.namespaceUUID(\n        \"" + _name_9);
                  String _plus_5 = (_plus_4 + "\"");
                  _builder.append(_plus_5);
                }
              }
              {
                for(final EStructuralFeature f_2 : pairs) {
                  _builder.append(",");
                  _builder.newLineIfNotEmpty();
                  _builder.append("        ");
                  _builder.append("\"");
                  String _name_10 = f_2.getName();
                  _builder.append(_name_10, "        ");
                  _builder.append("\" -> ");
                  String _name_11 = f_2.getName();
                  _builder.append(_name_11, "        ");
                  _builder.append("UUID");
                }
              }
              {
                final Function1<ETypedElement, Boolean> _function_9 = (ETypedElement a) -> {
                  return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
                };
                Iterable<ETypedElement> _filter_7 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_9);
                boolean _hasElements_11 = false;
                for(final ETypedElement attr_10 : _filter_7) {
                  if (!_hasElements_11) {
                    _hasElements_11 = true;
                    _builder.append(").toString,\n");
                  } else {
                    _builder.appendImmediate(",\n", "");
                  }
                  _builder.append("      ");
                  String _columnName_11 = OMLUtilities.columnName(attr_10);
                  _builder.append(_columnName_11);
                }
                if (_hasElements_11) {
                  _builder.append(")\n");
                }
              }
              _builder.newLineIfNotEmpty();
            }
          }
        }
      }
      _builder.newLine();
      _builder.append("  ");
      _builder.append("override val hashCode");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": scala.Int ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= ");
      {
        Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_3 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
        boolean _hasElements_12 = false;
        for(final ETypedElement attr_11 : _schemaAPIOrOrderingKeyAttributes_3) {
          if (!_hasElements_12) {
            _hasElements_12 = true;
            _builder.append("(", "  ");
          } else {
            _builder.appendImmediate(", ", "  ");
          }
          String _columnName_12 = OMLUtilities.columnName(attr_11);
          _builder.append(_columnName_12, "  ");
        }
        if (_hasElements_12) {
          _builder.append(").##", "  ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("override def equals(other: scala.Any): scala.Boolean = other match {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("case that: ");
      String _name_12 = eClass.getName();
      _builder.append(_name_12, "  \t");
      _builder.append(" =>");
      _builder.newLineIfNotEmpty();
      {
        Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_4 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
        boolean _hasElements_13 = false;
        for(final ETypedElement attr_12 : _schemaAPIOrOrderingKeyAttributes_4) {
          if (!_hasElements_13) {
            _hasElements_13 = true;
          } else {
            _builder.appendImmediate(" &&", "  \t  ");
          }
          _builder.append("  \t  ");
          _builder.append("(this.");
          String _columnName_13 = OMLUtilities.columnName(attr_12);
          _builder.append(_columnName_13, "  \t  ");
          _builder.append(" == that.");
          String _columnName_14 = OMLUtilities.columnName(attr_12);
          _builder.append(_columnName_14, "  \t  ");
          _builder.append(")");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("    ");
      _builder.append("case _ =>");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("false");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("@JSExportTopLevel(\"");
      String _name_13 = eClass.getName();
      _builder.append(_name_13);
      _builder.append("Helper\")");
      _builder.newLineIfNotEmpty();
      _builder.append("object ");
      String _name_14 = eClass.getName();
      _builder.append(_name_14);
      _builder.append("Helper {");
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("val TABLE_JSON_FILENAME ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": scala.Predef.String ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= \"");
      String _name_15 = eClass.getName();
      _builder.append(_name_15, "  ");
      _builder.append("s.json\"");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("implicit val w");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": upickle.default.Writer[");
      String _name_16 = eClass.getName();
      _builder.append(_name_16, "  ");
      _builder.append("]");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= upickle.default.macroW[");
      String _name_17 = eClass.getName();
      _builder.append(_name_17, "  ");
      _builder.append("]");
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("@JSExport");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def toJSON(c: ");
      String _name_18 = eClass.getName();
      _builder.append(_name_18, "  ");
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append(": String");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= upickle.default.write(expr=c, indent=0)");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("implicit val r");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": upickle.default.Reader[");
      String _name_19 = eClass.getName();
      _builder.append(_name_19, "  ");
      _builder.append("]");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= upickle.default.macroR[");
      String _name_20 = eClass.getName();
      _builder.append(_name_20, "  ");
      _builder.append("]");
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("@JSExport");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def fromJSON(c: String)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": ");
      String _name_21 = eClass.getName();
      _builder.append(_name_21, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= upickle.default.read[");
      String _name_22 = eClass.getName();
      _builder.append(_name_22, "  ");
      _builder.append("](c)");
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("}\t");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public String generateJSClassFile(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.append(" ");
    _builder.newLine();
    _builder.append("package gov.nasa.jpl.imce.oml.tables");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import scala.scalajs.js.annotation.JSExportTopLevel");
    _builder.newLine();
    _builder.newLine();
    _builder.append("@JSExportTopLevel(\"");
    String _name = eClass.getName();
    _builder.append(_name);
    _builder.append("JS\")");
    _builder.newLineIfNotEmpty();
    _builder.append("object ");
    String _name_1 = eClass.getName();
    _builder.append(_name_1);
    _builder.append("JS {");
    _builder.newLineIfNotEmpty();
    {
      Boolean _hasSchemaOptionalAttributes = OMLUtilities.hasSchemaOptionalAttributes(eClass);
      if ((_hasSchemaOptionalAttributes).booleanValue()) {
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("def js");
        String _name_2 = eClass.getName();
        _builder.append(_name_2, "  ");
        _builder.append("(");
        _builder.newLineIfNotEmpty();
        {
          Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
          boolean _hasElements = false;
          for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "    ");
            }
            _builder.append("  ");
            _builder.append("  ");
            String _columnName = OMLUtilities.columnName(attr);
            _builder.append(_columnName, "    ");
            _builder.append(": ");
            String _jsTypeName = OMLUtilities.jsTypeName(attr);
            _builder.append(_jsTypeName, "    ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        _builder.append(")");
        _builder.newLine();
        _builder.append("  ");
        _builder.append(": ");
        String _name_3 = eClass.getName();
        _builder.append(_name_3, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= ");
        String _name_4 = eClass.getName();
        _builder.append(_name_4, "  ");
        _builder.append("(");
        _builder.newLineIfNotEmpty();
        {
          Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_1 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
          boolean _hasElements_1 = false;
          for(final ETypedElement attr_1 : _schemaAPIOrOrderingKeyAttributes_1) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
            } else {
              _builder.appendImmediate(",", "    ");
            }
            _builder.append("  ");
            _builder.append("  ");
            String _jsArgName = OMLUtilities.jsArgName(attr_1);
            _builder.append(_jsArgName, "    ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        _builder.append(")");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("}\t");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateJVMClassFile(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.append(" ");
    _builder.newLine();
    _builder.append("package gov.nasa.jpl.imce.oml.tables");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import java.util.Optional");
    _builder.newLine();
    _builder.append("import scala.compat.java8.OptionConverters._");
    _builder.newLine();
    _builder.newLine();
    _builder.append("object ");
    String _name = eClass.getName();
    _builder.append(_name);
    _builder.append("Java {");
    _builder.newLineIfNotEmpty();
    {
      Boolean _hasSchemaOptionalAttributes = OMLUtilities.hasSchemaOptionalAttributes(eClass);
      if ((_hasSchemaOptionalAttributes).booleanValue()) {
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("def java");
        String _name_1 = eClass.getName();
        _builder.append(_name_1, "  ");
        _builder.append("(");
        _builder.newLineIfNotEmpty();
        {
          Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
          boolean _hasElements = false;
          for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "    ");
            }
            _builder.append("  ");
            _builder.append("  ");
            String _columnName = OMLUtilities.columnName(attr);
            _builder.append(_columnName, "    ");
            _builder.append(": ");
            String _javaTypeName = OMLUtilities.javaTypeName(attr);
            _builder.append(_javaTypeName, "    ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        _builder.append(")");
        _builder.newLine();
        _builder.append("  ");
        _builder.append(": ");
        String _name_2 = eClass.getName();
        _builder.append(_name_2, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= ");
        String _name_3 = eClass.getName();
        _builder.append(_name_3, "  ");
        _builder.append("(");
        _builder.newLineIfNotEmpty();
        {
          Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_1 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
          boolean _hasElements_1 = false;
          for(final ETypedElement attr_1 : _schemaAPIOrOrderingKeyAttributes_1) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
            } else {
              _builder.appendImmediate(",", "    ");
            }
            _builder.append("  ");
            _builder.append("  ");
            String _javaArgName = OMLUtilities.javaArgName(attr_1);
            _builder.append(_javaArgName, "    ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        _builder.append(")");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("}\t");
    _builder.newLine();
    return _builder.toString();
  }
}
