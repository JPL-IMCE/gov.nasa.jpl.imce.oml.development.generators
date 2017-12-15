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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class OMLSpecificationOMLSQLGenerator extends OMLUtilities {
  public static void main(final String[] args) {
    int _length = args.length;
    boolean _notEquals = (1 != _length);
    if (_notEquals) {
      System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.sql project");
      System.exit(1);
    }
    final OMLSpecificationOMLSQLGenerator gen = new OMLSpecificationOMLSQLGenerator();
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
    final String packageQName = "gov.nasa.jpl.imce.oml.sql";
    final Path bundlePath = Paths.get(targetDir);
    final Path oml_Folder = bundlePath.resolve("schema");
    oml_Folder.toFile().mkdirs();
    this.generate(ePackages, 
      oml_Folder.toAbsolutePath().toString(), packageQName, 
      "OML");
  }
  
  public void generate(final List<EPackage> ePackages, final String targetFolder, final String packageQName, final String tableName) {
    try {
      File _file = new File((((targetFolder + File.separator) + tableName) + ".sql"));
      final FileOutputStream tablesFile = new FileOutputStream(_file);
      try {
        tablesFile.write(this.generateTablesFile(ePackages, packageQName, tableName).getBytes());
      } finally {
        tablesFile.close();
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
      String _copyrightSQL = OMLUtilities.copyrightSQL();
      _builder.append(_copyrightSQL);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;");
      _builder.newLine();
      _builder.append("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;");
      _builder.newLine();
      _builder.append("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE=\'TRADITIONAL,ALLOW_INVALID_DATES\';");
      _builder.newLine();
      _builder.newLine();
      _builder.newLine();
      _builder.append("-- -----------------------------------------------------");
      _builder.newLine();
      _builder.append("-- Schema OML");
      _builder.newLine();
      _builder.append("-- -----------------------------------------------------");
      _builder.newLine();
      _builder.append("CREATE SCHEMA IF NOT EXISTS `OML` DEFAULT CHARACTER SET utf8 ;");
      _builder.newLine();
      _builder.append("USE `OML` ;");
      _builder.newLine();
      _builder.newLine();
      {
        for(final EClass eClass : eClasses) {
          _builder.append("-- -----------------------------------------------------");
          _builder.newLine();
          _builder.append("-- Table `OML`.`");
          String _upperCaseInitialOrWord = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass));
          _builder.append(_upperCaseInitialOrWord);
          _builder.append("`");
          _builder.newLineIfNotEmpty();
          _builder.append("-- -----------------------------------------------------");
          _builder.newLine();
          _builder.append("CREATE TABLE IF NOT EXISTS `OML`.`");
          String _upperCaseInitialOrWord_1 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass));
          _builder.append(_upperCaseInitialOrWord_1);
          _builder.append("` (");
          _builder.newLineIfNotEmpty();
          {
            Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass);
            boolean _hasElements = false;
            for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
              if (!_hasElements) {
                _hasElements = true;
              } else {
                _builder.appendImmediate(",\n", "  ");
              }
              _builder.append("  ");
              _builder.append("`");
              String _columnName = OMLUtilities.columnName(attr);
              _builder.append(_columnName, "  ");
              _builder.append("`  ");
              {
                Boolean _isUUID = OMLUtilities.isUUID(attr);
                if ((_isUUID).booleanValue()) {
                  _builder.append("BINARY(16) NOT NULL PRIMARY KEY");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                } else {
                  Boolean _isIRIReference = OMLUtilities.isIRIReference(attr);
                  if ((_isIRIReference).booleanValue()) {
                    _builder.append("VARCHAR(256) NOT NULL");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                  } else {
                    Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(attr);
                    if ((_isLiteralFeature).booleanValue()) {
                      _builder.append("-- TODO: LiteralFeature");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("TEXT, `LiteralType` VARCHAR(20) NOT NULL,");
                      _builder.newLine();
                      _builder.append("  ");
                    } else {
                      if (((OMLUtilities.isClassFeature(attr)).booleanValue() && (attr.getLowerBound() == 0))) {
                        _builder.append("BINARY(16) NULL");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                      } else {
                        if (((OMLUtilities.isClassFeature(attr)).booleanValue() && (attr.getLowerBound() > 0))) {
                          _builder.append("BINARY(16) NOT NULL");
                          _builder.newLineIfNotEmpty();
                          _builder.append("  ");
                        } else {
                          _builder.append("TEXT");
                        }
                      }
                    }
                  }
                }
              }
            }
            if (_hasElements) {
              _builder.append(",\n", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          {
            final Function1<ETypedElement, Boolean> _function_2 = (ETypedElement it) -> {
              return OMLUtilities.isClassFeature(it);
            };
            Iterable<ETypedElement> _filter_1 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function_2);
            boolean _hasElements_1 = false;
            for(final ETypedElement attr_1 : _filter_1) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
              } else {
                _builder.appendImmediate(",\n\n", "  ");
              }
              _builder.append("CONSTRAINT `fk_");
              String _upperCaseInitialOrWord_2 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass));
              _builder.append(_upperCaseInitialOrWord_2, "  ");
              _builder.append("_");
              String _columnName_1 = OMLUtilities.columnName(attr_1);
              _builder.append(_columnName_1, "  ");
              _builder.append("_");
              String _upperCaseInitialOrWord_3 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_1)));
              _builder.append(_upperCaseInitialOrWord_3, "  ");
              _builder.append("`");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("  ");
              _builder.append("FOREIGN KEY (`");
              String _columnName_2 = OMLUtilities.columnName(attr_1);
              _builder.append(_columnName_2, "    ");
              _builder.append("`)");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("  ");
              _builder.append("REFERENCES `OML`.`");
              String _upperCaseInitialOrWord_4 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_1)));
              _builder.append(_upperCaseInitialOrWord_4, "    ");
              _builder.append("`(`uuid`)");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("  ");
              _builder.append("ON DELETE CASCADE");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("  ");
              _builder.append("ON UPDATE CASCADE");
            }
            if (_hasElements_1) {
              _builder.append(",\n", "  ");
            }
          }
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC)\t");
          _builder.newLine();
          _builder.append(")");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
        }
      }
      _builder.append("  \t\t  ");
      _builder.newLine();
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
}
