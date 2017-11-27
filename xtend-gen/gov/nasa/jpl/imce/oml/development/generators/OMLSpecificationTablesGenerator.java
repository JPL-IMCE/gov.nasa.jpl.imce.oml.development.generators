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
import gov.nasa.jpl.imce.oml.oti.provenance.ProvenancePackage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
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

@SuppressWarnings("all")
public class OMLSpecificationTablesGenerator extends OMLUtilities {
  public static void main(final String[] args) {
    int _length = args.length;
    boolean _notEquals = (1 != _length);
    if (_notEquals) {
      System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.tables project");
      System.exit(1);
    }
    final OMLSpecificationTablesGenerator gen = new OMLSpecificationTablesGenerator();
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
  
  public static String locateOML2OTI(final String path) {
    try {
      String _xblockexpression = null;
      {
        final URL url = ProvenancePackage.class.getResource(path);
        String _xifexpression = null;
        if ((null != url)) {
          _xifexpression = url.toURI().toString();
        } else {
          String _xblockexpression_1 = null;
          {
            final URL binURL = ProvenancePackage.class.getResource("/gov/nasa/jpl/imce/oml/oti/provenance/ProvenancePackage.class");
            if ((null == binURL)) {
              throw new IllegalArgumentException(("locateXcore: failed to locate path: " + path));
            }
            final Path binPath = Paths.get(binURL.toURI());
            final Path xcorePath = binPath.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().resolve(path.substring(1));
            final String located = xcorePath.toAbsolutePath().toString();
            _xblockexpression_1 = located;
          }
          _xifexpression = _xblockexpression_1;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void generate(final String targetDir) {
    try {
      final List<EPackage> ePackages = Collections.<EPackage>unmodifiableList(CollectionLiterals.<EPackage>newArrayList(this.c, this.t, this.g, this.b, this.d));
      final String packageQName = "gov.nasa.jpl.imce.oml.tables";
      final Path bundlePath = Paths.get(targetDir);
      final Path oml_Folder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/tables");
      oml_Folder.toFile().mkdirs();
      this.generate(ePackages, 
        oml_Folder.toAbsolutePath().toString(), packageQName, 
        "OMLSpecificationTables");
      final Path oml_testFolder = bundlePath.resolve("shared/src/test/scala/test/oml/tables");
      oml_testFolder.toFile().mkdirs();
      String _plus = (oml_testFolder + File.separator);
      String _plus_1 = (_plus + "UUIDGenerators.scala");
      File _file = new File(_plus_1);
      final FileOutputStream uuidGeneratorFile = new FileOutputStream(_file);
      try {
        uuidGeneratorFile.write(this.generateUUIDGeneratorFile(ePackages, packageQName).getBytes());
      } finally {
        uuidGeneratorFile.close();
      }
      final Path oml_apiFolder = bundlePath.resolve("shared/src/main/scala/gov/nasa/jpl/imce/oml/resolver/api");
      oml_apiFolder.toFile().mkdirs();
      String _plus_2 = (oml_apiFolder + File.separator);
      String _plus_3 = (_plus_2 + "taggedTypes.scala");
      File _file_1 = new File(_plus_3);
      final FileOutputStream apiTaggedTypesFile = new FileOutputStream(_file_1);
      try {
        apiTaggedTypesFile.write(this.generateAPITaggedTypesFile(ePackages, "gov.nasa.jpl.imce.oml.resolver.api").getBytes());
      } finally {
        apiTaggedTypesFile.close();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
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
      File _file_1 = new File(((targetFolder + File.separator) + "taggedTypes.scala"));
      final FileOutputStream taggedTypesFile = new FileOutputStream(_file_1);
      try {
        taggedTypesFile.write(this.generateTaggedTypesFile(ePackages, packageQName).getBytes());
      } finally {
        taggedTypesFile.close();
      }
      File _file_2 = new File((((targetFolder + File.separator) + tableName) + ".scala"));
      final FileOutputStream tablesFile = new FileOutputStream(_file_2);
      try {
        tablesFile.write(this.generateTablesFile(ePackages, packageQName, tableName).getBytes());
      } finally {
        tablesFile.close();
      }
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      Iterable<EClass> _filter = Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class);
      for (final EClass eClass : _filter) {
        Boolean _isFunctionalAPI = OMLUtilities.isFunctionalAPI(eClass);
        if ((_isFunctionalAPI).booleanValue()) {
          String _name = eClass.getName();
          String _plus = ((targetFolder + File.separator) + _name);
          String _plus_1 = (_plus + ".scala");
          File _file_3 = new File(_plus_1);
          final FileOutputStream classFile = new FileOutputStream(_file_3);
          try {
            classFile.write(this.generateClassFile(eClass, packageQName).getBytes());
          } finally {
            classFile.close();
          }
        } else {
          if (((!eClass.getName().startsWith("Literal")) && (!Objects.equal(eClass.getName(), "Extent")))) {
            String _name_1 = eClass.getName();
            String _plus_2 = ((targetFolder + File.separator) + _name_1);
            String _plus_3 = (_plus_2 + ".scala");
            File _file_4 = new File(_plus_3);
            final FileOutputStream classFile_1 = new FileOutputStream(_file_4);
            try {
              classFile_1.write(this.generateTraitFile(eClass, packageQName).getBytes());
            } finally {
              classFile_1.close();
            }
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
          _builder.append("import scala.collection.immutable.{Seq,Set}");
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
      {
        boolean _equals_1 = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals_1) {
          _builder.append("import scala.{Boolean,StringContext,Unit}");
          _builder.newLine();
          _builder.append("import scala.Predef.String");
          _builder.newLine();
        } else {
          _builder.append("import scala.{Boolean,Unit}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("case class ");
      _builder.append(tableName);
      _builder.newLineIfNotEmpty();
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
          _builder.append("\n)");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("{");
      _builder.newLine();
      {
        for(final EClass eClass_1 : eClasses) {
          _builder.append("  ");
          String _tableReader = OMLSpecificationTablesGenerator.tableReader(eClass_1, tableName);
          _builder.append(_tableReader, "  ");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def isEmpty: Boolean");
      _builder.newLine();
      _builder.append("  ");
      {
        boolean _hasElements_1 = false;
        for(final EClass eClass_2 : eClasses) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            _builder.append("= ", "  ");
          } else {
            _builder.appendImmediate(" &&\n  ", "  ");
          }
          String _tableVariableName = OMLUtilities.tableVariableName(eClass_2);
          _builder.append(_tableVariableName, "  ");
          _builder.append(".isEmpty");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.newLine();
      {
        boolean _equals_2 = Objects.equal("OMLSpecificationTables", tableName);
        if (_equals_2) {
          _builder.append("  ");
          _builder.append("def show: String = {");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("def showSeq[T](title: String, s: Seq[T]): String = {");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("    ");
          _builder.append("if (s.isEmpty)");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("       ");
          _builder.append("\"\\n\" + title + \": empty\"");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("    ");
          _builder.append("else");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("       ");
          _builder.append("\"\\n\" + title + s\": ${s.size} entries\" +");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("       ");
          _builder.append("s.map(_.toString).mkString(\"\\n \", \"\\n \", \"\\n\")");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("val buff = new scala.collection.mutable.StringBuilder()");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          {
            boolean _hasElements_2 = false;
            for(final EClass eClass_3 : eClasses) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
              } else {
                _builder.appendImmediate("\n", "  ");
              }
              _builder.append("  buff ++= showSeq(\"");
              String _tableVariableName_1 = OMLUtilities.tableVariableName(eClass_3);
              _builder.append(_tableVariableName_1, "  ");
              _builder.append("\", ");
              String _tableVariableName_2 = OMLUtilities.tableVariableName(eClass_3);
              _builder.append(_tableVariableName_2, "  ");
              _builder.append(")");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("buff.toString");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
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
      _builder.append("  ");
      _builder.append("= ");
      {
        boolean _hasElements_3 = false;
        for(final EClass eClass_4 : eClasses) {
          if (!_hasElements_3) {
            _hasElements_3 = true;
            _builder.append((tableName + "(\n    "), "  ");
          } else {
            _builder.appendImmediate(",\n    ", "  ");
          }
          String _tableVariableName_3 = OMLUtilities.tableVariableName(eClass_4);
          _builder.append(_tableVariableName_3, "  ");
          _builder.append(" = ");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("(t1.");
          String _tableVariableName_4 = OMLUtilities.tableVariableName(eClass_4);
          _builder.append(_tableVariableName_4, "  ");
          _builder.append(".to[Set] ++ t2.");
          String _tableVariableName_5 = OMLUtilities.tableVariableName(eClass_4);
          _builder.append(_tableVariableName_5, "  ");
          _builder.append(".to[Set]).to[Seq].sortBy(_.uuid)");
        }
        if (_hasElements_3) {
          _builder.append(")", "  ");
        }
      }
      _builder.newLineIfNotEmpty();
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
        for(final EClass eClass_5 : eClasses) {
          _builder.append("  \t  ");
          _builder.append("case ");
          String _name = eClass_5.getName();
          _builder.append(_name, "  \t  ");
          _builder.append("Helper.TABLE_JSON_FILENAME =>");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t  ");
          _builder.append("  ");
          _builder.append("tables.");
          String _tableReaderName = OMLSpecificationTablesGenerator.tableReaderName(eClass_5);
          _builder.append(_tableReaderName, "  \t    ");
          _builder.append("(is)");
          _builder.newLineIfNotEmpty();
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
      _builder.append("omlSchemaJsonZipFile.getParentFile.mkdirs()");
      _builder.newLine();
      _builder.append("  \t  ");
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
        for(final EClass eClass_6 : eClasses) {
          _builder.append("      ");
          _builder.append("zos.putNextEntry(new java.util.zip.ZipEntry(");
          String _name_1 = eClass_6.getName();
          _builder.append(_name_1, "      ");
          _builder.append("Helper.TABLE_JSON_FILENAME))");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("tables.");
          String _tableVariableName_6 = OMLUtilities.tableVariableName(eClass_6);
          _builder.append(_tableVariableName_6, "      ");
          _builder.append(".foreach { t =>");
          _builder.newLineIfNotEmpty();
          _builder.append("      ");
          _builder.append("   ");
          _builder.append("val line = ");
          String _name_2 = eClass_6.getName();
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
    _builder.append(" = ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("(");
    String _tableVariableName_1 = OMLUtilities.tableVariableName(eClass);
    _builder.append(_tableVariableName_1, "  ");
    _builder.append(".to[Set] ++ ");
    _builder.newLineIfNotEmpty();
    _builder.append("   ");
    _builder.append("readJSonTable(is, ");
    String _name = eClass.getName();
    _builder.append(_name, "   ");
    _builder.append("Helper.fromJSON).to[Set]");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append(").to[Seq].sortBy(_.uuid))");
    _builder.newLine();
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
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return OMLUtilities.isFunctionalAPIWithOrderingKeys(it);
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1), _function_2);
      for(final EClass eClass : _sortBy) {
        _builder.append("  ");
        _builder.append("implicit def ");
        String _firstLower = StringExtensions.toFirstLower(eClass.getName());
        _builder.append(_firstLower, "  ");
        _builder.append("Ordering");
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
  
  @SuppressWarnings("unused")
  public String generateTaggedTypesFile(final List<EPackage> ePackages, final String packageQName) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("package ");
    _builder.append(packageQName);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.taggedTypes.{decodeTag,encodeTag}");
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.covariantTag");
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.covariantTag.@@");
    _builder.newLine();
    _builder.append("import io.circe.{Decoder,Encoder}");
    _builder.newLine();
    _builder.append("import scala.{Int,Ordering}");
    _builder.newLine();
    _builder.append("import scala.Predef.String");
    _builder.newLine();
    _builder.newLine();
    _builder.append("object taggedTypes {");
    _builder.newLine();
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EDataType, Boolean> _function_1 = (EDataType t) -> {
        return Boolean.valueOf(((!(t instanceof EEnum)) && (!Objects.equal(t.getName(), "UUID"))));
      };
      final Function1<EDataType, String> _function_2 = (EDataType it) -> {
        return it.getName();
      };
      List<EDataType> _sortBy = IterableExtensions.<EDataType, String>sortBy(IterableExtensions.<EDataType>filter(Iterables.<EDataType>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EDataType.class), _function_1), _function_2);
      for(final EDataType type : _sortBy) {
        _builder.append("  ");
        _builder.append("trait ");
        String _name = type.getName();
        _builder.append(_name, "  ");
        _builder.append("Tag");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("type ");
        String _name_1 = type.getName();
        _builder.append(_name_1, "  ");
        _builder.append(" = String @@ ");
        String _name_2 = type.getName();
        _builder.append(_name_2, "  ");
        _builder.append("Tag");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("def ");
        String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(type.getName());
        _builder.append(_lowerCaseInitialOrWord, "  ");
        _builder.append("(s: String) = covariantTag[");
        String _name_3 = type.getName();
        _builder.append(_name_3, "  ");
        _builder.append("Tag](s)");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("implicit val decode");
        String _name_4 = type.getName();
        _builder.append(_name_4, "  ");
        _builder.append(": Decoder[");
        String _name_5 = type.getName();
        _builder.append(_name_5, "  ");
        _builder.append("] = decodeTag[");
        String _name_6 = type.getName();
        _builder.append(_name_6, "  ");
        _builder.append("Tag]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("implicit val encode");
        String _name_7 = type.getName();
        _builder.append(_name_7, "  ");
        _builder.append(": Encoder[");
        String _name_8 = type.getName();
        _builder.append(_name_8, "  ");
        _builder.append("] = encodeTag[");
        String _name_9 = type.getName();
        _builder.append(_name_9, "  ");
        _builder.append("Tag]");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function_3 = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, String> _function_4 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy_1 = IterableExtensions.<EClass, String>sortBy(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function_3)), EClass.class), _function_4);
      for(final EClass eClass : _sortBy_1) {
        _builder.append("  ");
        _builder.append("trait ");
        String _name_10 = eClass.getName();
        _builder.append(_name_10, "  ");
        _builder.append("Tag");
        {
          final Function1<EClass, String> _function_5 = (EClass it) -> {
            return it.getName();
          };
          List<EClass> _sortBy_2 = IterableExtensions.<EClass, String>sortBy(OMLUtilities.ESuperClasses(eClass), _function_5);
          boolean _hasElements = false;
          for(final EClass eSup : _sortBy_2) {
            if (!_hasElements) {
              _hasElements = true;
              _builder.append(" <: ", "  ");
            } else {
              _builder.appendImmediate(" with ", "  ");
            }
            String _name_11 = eSup.getName();
            _builder.append(_name_11, "  ");
            _builder.append("Tag");
          }
        }
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function_6 = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_7 = (EClass it) -> {
        return Boolean.valueOf(((!it.getName().startsWith("Literal")) && (!Objects.equal(it.getName(), "Extent"))));
      };
      final Function1<EClass, String> _function_8 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy_3 = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function_6)), EClass.class), _function_7), _function_8);
      for(final EClass eClass_1 : _sortBy_3) {
        _builder.append("  ");
        _builder.append("type ");
        String _name_12 = eClass_1.getName();
        _builder.append(_name_12, "  ");
        _builder.append("UUID ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= String @@ ");
        String _name_13 = eClass_1.getName();
        _builder.append(_name_13, "  ");
        _builder.append("Tag");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("def ");
        String _lowerCaseInitialOrWord_1 = OMLUtilities.lowerCaseInitialOrWord(eClass_1.getName());
        _builder.append(_lowerCaseInitialOrWord_1, "  ");
        _builder.append("UUID(uuid: String)");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(": ");
        String _name_14 = eClass_1.getName();
        _builder.append(_name_14, "  ");
        _builder.append("UUID");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= covariantTag[");
        String _name_15 = eClass_1.getName();
        _builder.append(_name_15, "  ");
        _builder.append("Tag][String](uuid)");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("implicit val decode");
        String _name_16 = eClass_1.getName();
        _builder.append(_name_16, "  ");
        _builder.append("UUID");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(": Decoder[");
        String _name_17 = eClass_1.getName();
        _builder.append(_name_17, "  ");
        _builder.append("UUID]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= decodeTag[");
        String _name_18 = eClass_1.getName();
        _builder.append(_name_18, "  ");
        _builder.append("Tag]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("implicit val encode");
        String _name_19 = eClass_1.getName();
        _builder.append(_name_19, "  ");
        _builder.append("UUID");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(": Encoder[");
        String _name_20 = eClass_1.getName();
        _builder.append(_name_20, "  ");
        _builder.append("UUID]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= encodeTag[");
        String _name_21 = eClass_1.getName();
        _builder.append(_name_21, "  ");
        _builder.append("Tag]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("implicit val ordering");
        String _name_22 = eClass_1.getName();
        _builder.append(_name_22, "  ");
        _builder.append("UUID");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(": Ordering[");
        String _name_23 = eClass_1.getName();
        _builder.append(_name_23, "  ");
        _builder.append("UUID] ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= new Ordering[");
        String _name_24 = eClass_1.getName();
        _builder.append(_name_24, "  ");
        _builder.append("UUID] {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append("override def compare");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append("(x: ");
        String _name_25 = eClass_1.getName();
        _builder.append(_name_25, "  \t");
        _builder.append("UUID, ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t ");
        _builder.append("y: ");
        String _name_26 = eClass_1.getName();
        _builder.append(_name_26, "  \t ");
        _builder.append("UUID)");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append(": Int = x.compareTo(y)");
        _builder.newLine();
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
  
  @SuppressWarnings("unused")
  public String generateAPITaggedTypesFile(final List<EPackage> ePackages, final String packageQName) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("package ");
    _builder.append(packageQName);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import java.util.UUID");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.covariantTag");
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.covariantTag.@@");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import io.circe.{HCursor,Json}");
    _builder.newLine();
    _builder.append("import io.circe.{Decoder,Encoder}");
    _builder.newLine();
    _builder.append("import scala.{Int,Left,Ordering,Right}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("object taggedTypes {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("implicit def decodeTag[Tag]: Decoder[UUID @@ Tag] = new Decoder[UUID @@ Tag] {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("final def apply(c: HCursor): Decoder.Result[UUID @@ Tag] = c.value.as[UUID] match {");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("case Right(uuid) => Right(covariantTag[Tag][UUID](uuid))");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("case Left(failure) => Left(failure)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("implicit def encodeTag[Tag]: Encoder[UUID @@ Tag] = new Encoder[UUID @@ Tag] {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("final def apply(s: UUID @@ Tag): Json = Json.fromString(s.toString)");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("def fromUUIDString[Tag](uuid: scala.Predef.String @@ Tag)");
    _builder.newLine();
    _builder.append("  ");
    _builder.append(": UUID @@ Tag ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("= covariantTag[Tag][UUID](UUID.fromString(uuid))");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return Boolean.valueOf(((!it.getName().startsWith("Literal")) && (!Objects.equal(it.getName(), "Extent"))));
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1), _function_2);
      for(final EClass eClass : _sortBy) {
        _builder.append("  ");
        _builder.append("type ");
        String _name = eClass.getName();
        _builder.append(_name, "  ");
        _builder.append("UUID");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= UUID @@ gov.nasa.jpl.imce.oml.tables.taggedTypes.");
        String _name_1 = eClass.getName();
        _builder.append(_name_1, "  ");
        _builder.append("Tag");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("def ");
        String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
        _builder.append(_lowerCaseInitialOrWord, "  ");
        _builder.append("UUID(uuid: UUID): ");
        String _name_2 = eClass.getName();
        _builder.append(_name_2, "  ");
        _builder.append("UUID");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= covariantTag[gov.nasa.jpl.imce.oml.tables.taggedTypes.");
        String _name_3 = eClass.getName();
        _builder.append(_name_3, "  ");
        _builder.append("Tag][UUID](uuid)");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("implicit val decode");
        String _name_4 = eClass.getName();
        _builder.append(_name_4, "  ");
        _builder.append("UUID: Decoder[");
        String _name_5 = eClass.getName();
        _builder.append(_name_5, "  ");
        _builder.append("UUID]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= decodeTag[gov.nasa.jpl.imce.oml.tables.taggedTypes.");
        String _name_6 = eClass.getName();
        _builder.append(_name_6, "  ");
        _builder.append("Tag]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("implicit val encode");
        String _name_7 = eClass.getName();
        _builder.append(_name_7, "  ");
        _builder.append("UUID: Encoder[");
        String _name_8 = eClass.getName();
        _builder.append(_name_8, "  ");
        _builder.append("UUID]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= encodeTag[gov.nasa.jpl.imce.oml.tables.taggedTypes.");
        String _name_9 = eClass.getName();
        _builder.append(_name_9, "  ");
        _builder.append("Tag]");
        _builder.newLineIfNotEmpty();
        _builder.newLine();
        _builder.append("  ");
        _builder.append("implicit val ordering");
        String _name_10 = eClass.getName();
        _builder.append(_name_10, "  ");
        _builder.append("UUID");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(": Ordering[");
        String _name_11 = eClass.getName();
        _builder.append(_name_11, "  ");
        _builder.append("UUID]");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("= new Ordering[");
        String _name_12 = eClass.getName();
        _builder.append(_name_12, "  ");
        _builder.append("UUID] {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append("override def compare");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append("(x: ");
        String _name_13 = eClass.getName();
        _builder.append(_name_13, "  \t");
        _builder.append("UUID, ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t ");
        _builder.append("y: ");
        String _name_14 = eClass.getName();
        _builder.append(_name_14, "  \t ");
        _builder.append("UUID)");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("\t");
        _builder.append(": Int = x.compareTo(y)");
        _builder.newLine();
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
  
  @SuppressWarnings("unused")
  public String generateUUIDGeneratorFile(final List<EPackage> ePackages, final String packageQName) {
    StringConcatenation _builder = new StringConcatenation();
    String _copyright = OMLUtilities.copyright();
    _builder.append(_copyright);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("package test.oml.tables");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import gov.nasa.jpl.imce.oml.tables.taggedTypes");
    _builder.newLine();
    _builder.append("import org.scalacheck.Gen");
    _builder.newLine();
    _builder.newLine();
    _builder.append("object UUIDGenerators {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("val uuid = Gen.uuid");
    _builder.newLine();
    _builder.newLine();
    {
      final Function1<EPackage, EList<EClassifier>> _function = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_1 = (EClass it) -> {
        return Boolean.valueOf(((!it.getName().startsWith("Literal")) && (!Objects.equal(it.getName(), "Extent"))));
      };
      final Function1<EClass, String> _function_2 = (EClass it) -> {
        return it.getName();
      };
      List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function)), EClass.class), _function_1), _function_2);
      for(final EClass eClass : _sortBy) {
        _builder.append("  ");
        _builder.append("val ");
        String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
        _builder.append(_lowerCaseInitialOrWord, "  ");
        _builder.append("UUID = uuid.map(id => taggedTypes.");
        String _lowerCaseInitialOrWord_1 = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
        _builder.append(_lowerCaseInitialOrWord_1, "  ");
        _builder.append("UUID(id.toString))");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
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
        return Boolean.valueOf(((OMLUtilities.isUUIDFeature(it)).booleanValue() && (it.getLowerBound() > 0)));
      };
      final Iterable<EStructuralFeature> pairs = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_1);
      final Function1<ETypedElement, Boolean> _function_2 = (ETypedElement a) -> {
        return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
      };
      final Iterable<ETypedElement> keyAttributes = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_2);
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
      {
        boolean _isEmpty = IterableExtensions.isEmpty(uuidFactors);
        boolean _not = (!_isEmpty);
        if (_not) {
          _builder.append("import scala.Predef.ArrowAssoc");
          _builder.newLine();
        } else {
          boolean _isEmpty_1 = IterableExtensions.isEmpty(pairs);
          boolean _not_1 = (!_isEmpty_1);
          if (_not_1) {
            _builder.append("import scala.Predef.ArrowAssoc");
            _builder.newLine();
          }
        }
      }
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
        boolean _not_2 = (!(_hasSchemaOptionalAttributes).booleanValue());
        if (_not_2) {
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
          {
            if ((((null != OMLUtilities.lookupUUIDFeature(eClass)) && Objects.equal(OMLUtilities.columnName(attr_1), "uuid")) && (!IterableExtensions.isEmpty(OMLUtilities.ESuperClasses(eClass))))) {
              _builder.append("  ");
              _builder.append("@(JSExport @field) override val ");
              String _columnName_1 = OMLUtilities.columnName(attr_1);
              _builder.append(_columnName_1, "  ");
              _builder.append(": ");
              String _constructorTypeRef = OMLUtilities.constructorTypeRef(eClass, attr_1);
              _builder.append(_constructorTypeRef, "  ");
              _builder.newLineIfNotEmpty();
            } else {
              EClass _EClassContainer = OMLUtilities.EClassContainer(attr_1);
              boolean _notEquals = (!Objects.equal(_EClassContainer, eClass));
              if (_notEquals) {
                _builder.append("  ");
                _builder.append("@(JSExport @field) override val ");
                String _columnName_2 = OMLUtilities.columnName(attr_1);
                _builder.append(_columnName_2, "  ");
                _builder.append(": ");
                String _constructorTypeRef_1 = OMLUtilities.constructorTypeRef(eClass, attr_1);
                _builder.append(_constructorTypeRef_1, "  ");
                _builder.newLineIfNotEmpty();
              } else {
                _builder.append("  ");
                _builder.append("@(JSExport @field) val ");
                String _columnName_3 = OMLUtilities.columnName(attr_1);
                _builder.append(_columnName_3, "  ");
                _builder.append(": ");
                String _constructorTypeRef_2 = OMLUtilities.constructorTypeRef(eClass, attr_1);
                _builder.append(_constructorTypeRef_2, "  ");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
      _builder.append(")");
      {
        Iterable<EClass> _ESuperClasses = OMLUtilities.ESuperClasses(eClass);
        boolean _hasElements_1 = false;
        for(final EClass sup : _ESuperClasses) {
          if (!_hasElements_1) {
            _hasElements_1 = true;
            _builder.append(" extends");
          } else {
            _builder.appendImmediate(" with", "");
          }
          _builder.append(" ");
          String _name_2 = sup.getName();
          _builder.append(_name_2);
        }
      }
      _builder.append(" {");
      _builder.newLineIfNotEmpty();
      {
        Boolean _hasSchemaOptionalAttributes_1 = OMLUtilities.hasSchemaOptionalAttributes(eClass);
        if ((_hasSchemaOptionalAttributes_1).booleanValue()) {
          _builder.append("  ");
          _builder.append("def this(");
          _builder.newLine();
          _builder.append("  ");
          {
            final Function1<ETypedElement, Boolean> _function_3 = (ETypedElement a) -> {
              int _lowerBound_1 = a.getLowerBound();
              return Boolean.valueOf((_lowerBound_1 > 0));
            };
            Iterable<ETypedElement> _filter = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_3);
            boolean _hasElements_2 = false;
            for(final ETypedElement attr_2 : _filter) {
              if (!_hasElements_2) {
                _hasElements_2 = true;
              } else {
                _builder.appendImmediate(",\n", "  ");
              }
              _builder.append("  ");
              String _columnName_4 = OMLUtilities.columnName(attr_2);
              _builder.append(_columnName_4, "  ");
              _builder.append(": ");
              String _constructorTypeRef_3 = OMLUtilities.constructorTypeRef(eClass, attr_2);
              _builder.append(_constructorTypeRef_3, "  ");
            }
            if (_hasElements_2) {
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
            boolean _hasElements_3 = false;
            for(final ETypedElement attr_3 : _schemaAPIOrOrderingKeyAttributes_2) {
              if (!_hasElements_3) {
                _hasElements_3 = true;
              } else {
                _builder.appendImmediate(",\n", "  ");
              }
              {
                int _lowerBound_1 = attr_3.getLowerBound();
                boolean _greaterThan = (_lowerBound_1 > 0);
                if (_greaterThan) {
                  _builder.append("    ");
                  String _columnName_5 = OMLUtilities.columnName(attr_3);
                  _builder.append(_columnName_5, "  ");
                } else {
                  _builder.append("    scala.None /* ");
                  String _columnName_6 = OMLUtilities.columnName(attr_3);
                  _builder.append(_columnName_6, "  ");
                  _builder.append(" */");
                }
              }
            }
            if (_hasElements_3) {
              _builder.append(")\n", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.newLine();
          {
            final Function1<ETypedElement, Boolean> _function_4 = (ETypedElement a) -> {
              int _lowerBound_2 = a.getLowerBound();
              return Boolean.valueOf((_lowerBound_2 == 0));
            };
            Iterable<ETypedElement> _filter_1 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_4);
            boolean _hasElements_4 = false;
            for(final ETypedElement attr_4 : _filter_1) {
              if (!_hasElements_4) {
                _hasElements_4 = true;
              } else {
                _builder.appendImmediate("", "  ");
              }
              _builder.append("  ");
              _builder.append("def with");
              String _firstUpper = StringExtensions.toFirstUpper(OMLUtilities.columnName(attr_4));
              _builder.append(_firstUpper, "  ");
              _builder.append("(l: ");
              String _scalaTableTypeRef = OMLUtilities.scalaTableTypeRef(eClass, attr_4);
              _builder.append(_scalaTableTypeRef, "  ");
              _builder.append(")\t ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append(": ");
              String _name_3 = eClass.getName();
              _builder.append(_name_3, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("= copy(");
              String _columnName_7 = OMLUtilities.columnName(attr_4);
              _builder.append(_columnName_7, "  ");
              _builder.append("=scala.Some(l))");
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
            boolean _hasElements_5 = false;
            for(final ETypedElement attr_5 : keyAttributes) {
              if (!_hasElements_5) {
                _hasElements_5 = true;
                _builder.append("  // Ctor(uuidWithoutContainer)\n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n");
              } else {
                _builder.appendImmediate(",\n", "");
              }
              _builder.append("    ");
              String _columnName_8 = OMLUtilities.columnName(attr_5);
              _builder.append(_columnName_8);
              _builder.append(": ");
              String _constructorTypeRef_4 = OMLUtilities.constructorTypeRef(eClass, attr_5);
              _builder.append(_constructorTypeRef_4);
            }
            if (_hasElements_5) {
              String _lowerCaseInitialOrWord = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
              String _plus = (")\n  = this(\n      taggedTypes." + _lowerCaseInitialOrWord);
              String _plus_1 = (_plus + "UUID(oug.namespaceUUID(\n        ");
              String _name_4 = uuidNS.getName();
              String _plus_2 = (_plus_1 + _name_4);
              String _plus_3 = (_plus_2 + ".toString");
              _builder.append(_plus_3);
            }
          }
          {
            boolean _hasElements_6 = false;
            for(final EStructuralFeature f : uuidFactors) {
              if (!_hasElements_6) {
                _hasElements_6 = true;
              } else {
                _builder.appendImmediate(",", "");
              }
              _builder.append(",");
              _builder.newLineIfNotEmpty();
              _builder.append("        ");
              _builder.append("\"");
              String _name_5 = f.getName();
              _builder.append(_name_5, "        ");
              _builder.append("\" -> ");
              String _name_6 = f.getName();
              _builder.append(_name_6, "        ");
            }
          }
          {
            final Function1<ETypedElement, Boolean> _function_5 = (ETypedElement a) -> {
              return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
            };
            Iterable<ETypedElement> _filter_2 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_5);
            boolean _hasElements_7 = false;
            for(final ETypedElement attr_6 : _filter_2) {
              if (!_hasElements_7) {
                _hasElements_7 = true;
                _builder.append(").toString),\n");
              } else {
                _builder.appendImmediate(",\n", "");
              }
              _builder.append("      ");
              String _columnName_9 = OMLUtilities.columnName(attr_6);
              _builder.append(_columnName_9);
            }
            if (_hasElements_7) {
              _builder.append(")\n");
            }
          }
          _builder.newLineIfNotEmpty();
        } else {
          if (uuidWithGenerator) {
            {
              boolean _hasElements_8 = false;
              for(final ETypedElement attr_7 : keyAttributes) {
                if (!_hasElements_8) {
                  _hasElements_8 = true;
                  _builder.append("  // Ctor(uuidWithGenerator)   \n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n");
                } else {
                  _builder.appendImmediate(",\n", "");
                }
                _builder.append("    ");
                String _columnName_10 = OMLUtilities.columnName(attr_7);
                _builder.append(_columnName_10);
                _builder.append(": ");
                String _constructorTypeRef_5 = OMLUtilities.constructorTypeRef(eClass, attr_7);
                _builder.append(_constructorTypeRef_5);
              }
              if (_hasElements_8) {
                String _lowerCaseInitialOrWord_1 = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
                String _plus_4 = (")\n  = this(\n      taggedTypes." + _lowerCaseInitialOrWord_1);
                String _plus_5 = (_plus_4 + "UUID(oug.namespaceUUID(\n        ");
                String _name_7 = uuidNS.getName();
                String _plus_6 = (_plus_5 + _name_7);
                String _plus_7 = (_plus_6 + "UUID");
                _builder.append(_plus_7);
              }
            }
            {
              boolean _hasElements_9 = false;
              for(final EStructuralFeature f_1 : uuidFactors) {
                if (!_hasElements_9) {
                  _hasElements_9 = true;
                } else {
                  _builder.appendImmediate(",", "");
                }
                _builder.append(",");
                _builder.newLineIfNotEmpty();
                _builder.append("        ");
                _builder.append("\"");
                String _name_8 = f_1.getName();
                _builder.append(_name_8, "        ");
                _builder.append("\" -> ");
                String _name_9 = f_1.getName();
                _builder.append(_name_9, "        ");
              }
            }
            {
              final Function1<ETypedElement, Boolean> _function_6 = (ETypedElement a) -> {
                return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
              };
              Iterable<ETypedElement> _filter_3 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_6);
              boolean _hasElements_10 = false;
              for(final ETypedElement attr_8 : _filter_3) {
                if (!_hasElements_10) {
                  _hasElements_10 = true;
                  _builder.append(").toString),\n");
                } else {
                  _builder.appendImmediate(",\n", "");
                }
                _builder.append("      ");
                String _columnName_11 = OMLUtilities.columnName(attr_8);
                _builder.append(_columnName_11);
              }
              if (_hasElements_10) {
                _builder.append(")\n");
              }
            }
            _builder.newLineIfNotEmpty();
          } else {
            if (uuidWithContainer) {
              {
                boolean _hasElements_11 = false;
                for(final ETypedElement attr_9 : keyAttributes) {
                  if (!_hasElements_11) {
                    _hasElements_11 = true;
                    _builder.append("  // Ctor(uuidWithContainer)   \n  def this(\n    oug: gov.nasa.jpl.imce.oml.uuid.OMLUUIDGenerator,\n");
                  } else {
                    _builder.appendImmediate(",\n", "");
                  }
                  _builder.append("    ");
                  String _columnName_12 = OMLUtilities.columnName(attr_9);
                  _builder.append(_columnName_12);
                  _builder.append(": ");
                  String _constructorTypeRef_6 = OMLUtilities.constructorTypeRef(eClass, attr_9);
                  _builder.append(_constructorTypeRef_6);
                }
                if (_hasElements_11) {
                  String _lowerCaseInitialOrWord_2 = OMLUtilities.lowerCaseInitialOrWord(eClass.getName());
                  String _plus_8 = (")\n  = this(\n      taggedTypes." + _lowerCaseInitialOrWord_2);
                  String _plus_9 = (_plus_8 + "UUID(oug.namespaceUUID(\n        \"");
                  String _name_10 = eClass.getName();
                  String _plus_10 = (_plus_9 + _name_10);
                  String _plus_11 = (_plus_10 + "\"");
                  _builder.append(_plus_11);
                }
              }
              {
                for(final EStructuralFeature f_2 : pairs) {
                  _builder.append(",");
                  _builder.newLineIfNotEmpty();
                  _builder.append("        ");
                  _builder.append("\"");
                  String _name_11 = f_2.getName();
                  _builder.append(_name_11, "        ");
                  _builder.append("\" -> ");
                  String _columnUUID = OMLUtilities.columnUUID(f_2);
                  _builder.append(_columnUUID, "        ");
                }
              }
              {
                final Function1<ETypedElement, Boolean> _function_7 = (ETypedElement a) -> {
                  return Boolean.valueOf(((!Objects.equal(uuid, a)) && (a.getLowerBound() > 0)));
                };
                Iterable<ETypedElement> _filter_4 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_7);
                boolean _hasElements_12 = false;
                for(final ETypedElement attr_10 : _filter_4) {
                  if (!_hasElements_12) {
                    _hasElements_12 = true;
                    _builder.append(").toString),\n");
                  } else {
                    _builder.appendImmediate(",\n", "");
                  }
                  _builder.append("      ");
                  String _columnName_13 = OMLUtilities.columnName(attr_10);
                  _builder.append(_columnName_13);
                }
                if (_hasElements_12) {
                  _builder.append(")\n");
                }
              }
              _builder.newLineIfNotEmpty();
            }
          }
        }
      }
      _builder.newLine();
      {
        EStructuralFeature _lookupUUIDFeature = OMLUtilities.lookupUUIDFeature(eClass);
        boolean _tripleNotEquals = (null != _lookupUUIDFeature);
        if (_tripleNotEquals) {
          _builder.append("val vertexId: scala.Long = uuid.hashCode.toLong");
          _builder.newLine();
          _builder.newLine();
        }
      }
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
        boolean _hasElements_13 = false;
        for(final ETypedElement attr_11 : _schemaAPIOrOrderingKeyAttributes_3) {
          if (!_hasElements_13) {
            _hasElements_13 = true;
            _builder.append("(", "  ");
          } else {
            _builder.appendImmediate(", ", "  ");
          }
          String _columnName_14 = OMLUtilities.columnName(attr_11);
          _builder.append(_columnName_14, "  ");
        }
        if (_hasElements_13) {
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
        boolean _hasElements_14 = false;
        for(final ETypedElement attr_12 : _schemaAPIOrOrderingKeyAttributes_4) {
          if (!_hasElements_14) {
            _hasElements_14 = true;
          } else {
            _builder.appendImmediate(" &&", "  \t  ");
          }
          _builder.append("  \t  ");
          {
            Boolean _isXRefColumn = OMLUtilities.isXRefColumn(attr_12);
            if ((_isXRefColumn).booleanValue()) {
              {
                int _lowerBound_2 = attr_12.getLowerBound();
                boolean _equals = (_lowerBound_2 == 0);
                if (_equals) {
                  _builder.append("((this.");
                  String _columnName_15 = OMLUtilities.columnName(attr_12);
                  _builder.append(_columnName_15, "  \t  ");
                  _builder.append(", that.");
                  String _columnName_16 = OMLUtilities.columnName(attr_12);
                  _builder.append(_columnName_16, "  \t  ");
                  _builder.append(") match {");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  \t  ");
                  _builder.append("    ");
                  _builder.append("case (scala.Some(t1), scala.Some(t2)) =>");
                  _builder.newLine();
                  _builder.append("  \t  ");
                  _builder.append("      ");
                  _builder.append("t1 == t2");
                  _builder.newLine();
                  _builder.append("  \t  ");
                  _builder.append("    ");
                  _builder.append("case (scala.None, scala.None) =>");
                  _builder.newLine();
                  _builder.append("  \t  ");
                  _builder.append("      ");
                  _builder.append("true");
                  _builder.newLine();
                  _builder.append("  \t  ");
                  _builder.append("    ");
                  _builder.append("case _ =>");
                  _builder.newLine();
                  _builder.append("  \t  ");
                  _builder.append("      ");
                  _builder.append("false");
                  _builder.newLine();
                  _builder.append("  \t  ");
                  _builder.append("})");
                } else {
                  _builder.append("(this.");
                  String _columnName_17 = OMLUtilities.columnName(attr_12);
                  _builder.append(_columnName_17, "  \t  ");
                  _builder.append(" == that.");
                  String _columnName_18 = OMLUtilities.columnName(attr_12);
                  _builder.append(_columnName_18, "  \t  ");
                  _builder.append(") ");
                }
              }
            } else {
              _builder.append("(this.");
              String _columnName_19 = OMLUtilities.columnName(attr_12);
              _builder.append(_columnName_19, "  \t  ");
              _builder.append(" == that.");
              String _columnName_20 = OMLUtilities.columnName(attr_12);
              _builder.append(_columnName_20, "  \t  ");
              _builder.append(")");
            }
          }
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
      _builder.append("import io.circe.{Decoder, Encoder, HCursor, Json}");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("import io.circe.parser.parse");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("import scala.Predef.String");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("val TABLE_JSON_FILENAME ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append(": String ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= \"");
      String _pluralize = OMLUtilities.pluralize(eClass.getName());
      _builder.append(_pluralize, "  ");
      _builder.append(".json\"");
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("implicit val decode");
      String _name_15 = eClass.getName();
      _builder.append(_name_15, "  ");
      _builder.append(": Decoder[");
      String _name_16 = eClass.getName();
      _builder.append(_name_16, "  ");
      _builder.append("]");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= Decoder.instance[");
      String _name_17 = eClass.getName();
      _builder.append(_name_17, "  ");
      _builder.append("] { c: HCursor =>");
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("import cats.syntax.either._");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("for {");
      _builder.newLine();
      _builder.append("    \t  ");
      {
        Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_5 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
        boolean _hasElements_15 = false;
        for(final ETypedElement attr_13 : _schemaAPIOrOrderingKeyAttributes_5) {
          if (!_hasElements_15) {
            _hasElements_15 = true;
          } else {
            _builder.appendImmediate("\n", "    \t  ");
          }
          String _columnName_21 = OMLUtilities.columnName(attr_13);
          _builder.append(_columnName_21, "    \t  ");
          _builder.append(" <- ");
          String _circeDecoder = OMLUtilities.circeDecoder(eClass, attr_13);
          _builder.append(_circeDecoder, "    \t  ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("    \t");
      _builder.append("} yield ");
      String _name_18 = eClass.getName();
      _builder.append(_name_18, "    \t");
      _builder.append("(");
      _builder.newLineIfNotEmpty();
      _builder.append("    \t  ");
      {
        Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_6 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
        boolean _hasElements_16 = false;
        for(final ETypedElement attr_14 : _schemaAPIOrOrderingKeyAttributes_6) {
          if (!_hasElements_16) {
            _hasElements_16 = true;
          } else {
            _builder.appendImmediate(",\n", "    \t  ");
          }
          String _columnName_22 = OMLUtilities.columnName(attr_14);
          _builder.append(_columnName_22, "    \t  ");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("    \t");
      _builder.append(")");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("implicit val encode");
      String _name_19 = eClass.getName();
      _builder.append(_name_19, "  ");
      _builder.append(": Encoder[");
      String _name_20 = eClass.getName();
      _builder.append(_name_20, "  ");
      _builder.append("]");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= new Encoder[");
      String _name_21 = eClass.getName();
      _builder.append(_name_21, "  ");
      _builder.append("] {");
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
      _builder.append("override final def apply(x: ");
      String _name_22 = eClass.getName();
      _builder.append(_name_22, "    ");
      _builder.append("): Json ");
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
      _builder.append("= Json.obj(");
      _builder.newLine();
      _builder.append("    \t  ");
      {
        Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_7 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
        boolean _hasElements_17 = false;
        for(final ETypedElement attr_15 : _schemaAPIOrOrderingKeyAttributes_7) {
          if (!_hasElements_17) {
            _hasElements_17 = true;
          } else {
            _builder.appendImmediate(",\n", "    \t  ");
          }
          _builder.append("(\"");
          String _columnName_23 = OMLUtilities.columnName(attr_15);
          _builder.append(_columnName_23, "    \t  ");
          _builder.append("\", ");
          String _circeEncoder = OMLUtilities.circeEncoder(eClass, attr_15);
          _builder.append(_circeEncoder, "    \t  ");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
      _builder.append(")");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("@JSExport");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def toJSON(c: ");
      String _name_23 = eClass.getName();
      _builder.append(_name_23, "  ");
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append(": String");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("= encode");
      String _name_24 = eClass.getName();
      _builder.append(_name_24, "  ");
      _builder.append("(c).noSpaces");
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
      String _name_25 = eClass.getName();
      _builder.append(_name_25, "  ");
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.append("= parse(c) match {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("case scala.Right(json) =>");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("decode");
      String _name_26 = eClass.getName();
      _builder.append(_name_26, "  \t  ");
      _builder.append("(json.hcursor) match {");
      _builder.newLineIfNotEmpty();
      _builder.append("  \t    \t");
      _builder.append("case scala.Right(result) =>");
      _builder.newLine();
      _builder.append("  \t    \t  ");
      _builder.append("result");
      _builder.newLine();
      _builder.append("  \t    \t");
      _builder.append("case scala.Left(failure) =>");
      _builder.newLine();
      _builder.append("  \t    \t  ");
      _builder.append("throw failure");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("case scala.Left(failure) =>");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("throw failure");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public String generateTraitFile(final EClass eClass, final String packageQName) {
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
    _builder.append("trait ");
    String _name = eClass.getName();
    _builder.append(_name);
    {
      Iterable<EClass> _ESuperClasses = OMLUtilities.ESuperClasses(eClass);
      boolean _hasElements = false;
      for(final EClass sup : _ESuperClasses) {
        if (!_hasElements) {
          _hasElements = true;
          _builder.append(" extends");
        } else {
          _builder.appendImmediate(" with", "");
        }
        _builder.append(" ");
        String _name_1 = sup.getName();
        _builder.append(_name_1);
      }
    }
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    {
      Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
      for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
        {
          if ((((null != OMLUtilities.lookupUUIDFeature(eClass)) && Objects.equal(OMLUtilities.columnName(attr), "uuid")) && (!IterableExtensions.isEmpty(OMLUtilities.ESuperClasses(eClass))))) {
            _builder.append("  ");
            _builder.append("override val ");
            String _columnName = OMLUtilities.columnName(attr);
            _builder.append(_columnName, "  ");
            _builder.append(": ");
            String _constructorTypeRef = OMLUtilities.constructorTypeRef(eClass, attr);
            _builder.append(_constructorTypeRef, "  ");
            _builder.newLineIfNotEmpty();
          } else {
            EClass _EClassContainer = OMLUtilities.EClassContainer(attr);
            boolean _notEquals = (!Objects.equal(_EClassContainer, eClass));
            if (_notEquals) {
              _builder.append("  ");
              _builder.append("override val ");
              String _columnName_1 = OMLUtilities.columnName(attr);
              _builder.append(_columnName_1, "  ");
              _builder.append(": ");
              String _constructorTypeRef_1 = OMLUtilities.constructorTypeRef(eClass, attr);
              _builder.append(_constructorTypeRef_1, "  ");
              _builder.newLineIfNotEmpty();
            } else {
              _builder.append("  ");
              _builder.append("val ");
              String _columnName_2 = OMLUtilities.columnName(attr);
              _builder.append(_columnName_2, "  ");
              _builder.append(": ");
              String _constructorTypeRef_2 = OMLUtilities.constructorTypeRef(eClass, attr);
              _builder.append(_constructorTypeRef_2, "  ");
              _builder.newLineIfNotEmpty();
            }
          }
        }
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
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
