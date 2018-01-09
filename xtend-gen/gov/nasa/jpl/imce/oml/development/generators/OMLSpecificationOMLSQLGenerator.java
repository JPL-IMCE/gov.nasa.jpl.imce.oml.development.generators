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
import java.util.HashMap;
import java.util.List;
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
      final List<EClass> eConcrete = IterableExtensions.<EClass>sortWith(_filter, _oMLTableCompare);
      final Function1<EPackage, EList<EClassifier>> _function_2 = (EPackage it) -> {
        return it.getEClassifiers();
      };
      final Function1<EClass, Boolean> _function_3 = (EClass it) -> {
        return Boolean.valueOf(((it.isAbstract() && (OMLUtilities.isSchema(it)).booleanValue()) && (OMLUtilities.isAPI(it)).booleanValue()));
      };
      final Function1<EClass, String> _function_4 = (EClass it) -> {
        return it.getName();
      };
      final List<EClass> eAbstract = IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(Iterables.<EClassifier>concat(ListExtensions.<EPackage, EList<EClassifier>>map(ePackages, _function_2)), EClass.class), _function_3), _function_4);
      final HashMap<String, EClass> abbrevMap = new HashMap<String, EClass>();
      final Consumer<EClass> _function_5 = (EClass c) -> {
        final EClass prev = abbrevMap.put(OMLSpecificationOMLSQLGenerator.abbreviatedTableName(c), c);
        if ((null != prev)) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Conflict for ");
          String _abbreviatedTableName = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(c);
          _builder.append(_abbreviatedTableName);
          _builder.append(": ");
          String _name = c.getName();
          _builder.append(_name);
          _builder.append(" vs. ");
          String _name_1 = prev.getName();
          _builder.append(_name_1);
          System.out.println(_builder);
        }
      };
      eConcrete.forEach(_function_5);
      final Consumer<EClass> _function_6 = (EClass c) -> {
        final EClass prev = abbrevMap.put(OMLSpecificationOMLSQLGenerator.abbreviatedTableName(c), c);
        if ((null != prev)) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Conflict for ");
          String _abbreviatedTableName = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(c);
          _builder.append(_abbreviatedTableName);
          _builder.append(": ");
          String _name = c.getName();
          _builder.append(_name);
          _builder.append(" vs. ");
          String _name_1 = prev.getName();
          _builder.append(_name_1);
          System.out.println(_builder);
        }
      };
      eAbstract.forEach(_function_6);
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
      _builder.append("-- Summary of abstract table names");
      _builder.newLine();
      _builder.append("-- ");
      {
        final Function1<EClass, String> _function_7 = (EClass it) -> {
          return OMLSpecificationOMLSQLGenerator.abbreviatedTableName(it);
        };
        List<EClass> _sortBy = IterableExtensions.<EClass, String>sortBy(eAbstract, _function_7);
        for(final EClass eClass : _sortBy) {
          _builder.newLineIfNotEmpty();
          _builder.append("-- ");
          String _abbreviatedTableName = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass);
          _builder.append(_abbreviatedTableName);
          String _pad = OMLSpecificationOMLSQLGenerator.pad(eClass);
          _builder.append(_pad);
          _builder.append(" ");
          String _upperCaseInitialOrWord = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass));
          _builder.append(_upperCaseInitialOrWord);
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("\t\t");
      _builder.newLine();
      _builder.append("-- Summary of concrete table names");
      _builder.newLine();
      _builder.append("-- ");
      {
        final Function1<EClass, String> _function_8 = (EClass it) -> {
          return OMLSpecificationOMLSQLGenerator.abbreviatedTableName(it);
        };
        List<EClass> _sortBy_1 = IterableExtensions.<EClass, String>sortBy(eConcrete, _function_8);
        for(final EClass eClass_1 : _sortBy_1) {
          _builder.newLineIfNotEmpty();
          _builder.append("-- ");
          String _abbreviatedTableName_1 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_1);
          _builder.append(_abbreviatedTableName_1);
          String _pad_1 = OMLSpecificationOMLSQLGenerator.pad(eClass_1);
          _builder.append(_pad_1);
          _builder.append(" ");
          String _upperCaseInitialOrWord_1 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_1));
          _builder.append(_upperCaseInitialOrWord_1);
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      {
        for(final EClass eClass_2 : eAbstract) {
          _builder.append("-- -----------------------------------------------------");
          _builder.newLine();
          _builder.append("-- Table `OML`.`");
          String _abbreviatedTableName_2 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_2);
          _builder.append(_abbreviatedTableName_2);
          _builder.append("`");
          _builder.newLineIfNotEmpty();
          _builder.append("-- -----------------------------------------------------");
          _builder.newLine();
          _builder.append("CREATE TABLE IF NOT EXISTS `OML`.`");
          String _abbreviatedTableName_3 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_2);
          _builder.append(_abbreviatedTableName_3);
          _builder.append("` (");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("`uuid` CHAR(36) NOT NULL PRIMARY KEY,\t\t  ");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC)\t");
          _builder.newLine();
          _builder.append(")");
          _builder.newLine();
          _builder.append("COMMENT = \'Abstract Classification Table ");
          String _upperCaseInitialOrWord_2 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_2));
          _builder.append(_upperCaseInitialOrWord_2);
          _builder.append("\';");
          _builder.newLineIfNotEmpty();
          _builder.newLine();
        }
      }
      _builder.newLine();
      {
        for(final EClass eClass_3 : eConcrete) {
          _builder.append("-- -----------------------------------------------------");
          _builder.newLine();
          _builder.append("-- Table `OML`.`");
          String _abbreviatedTableName_4 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_3);
          _builder.append(_abbreviatedTableName_4);
          _builder.append("`");
          _builder.newLineIfNotEmpty();
          _builder.append("-- -----------------------------------------------------");
          _builder.newLine();
          _builder.append("CREATE TABLE IF NOT EXISTS `OML`.`");
          String _abbreviatedTableName_5 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_3);
          _builder.append(_abbreviatedTableName_5);
          _builder.append("` (");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          {
            Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass_3);
            boolean _hasElements = false;
            for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
              if (!_hasElements) {
                _hasElements = true;
              } else {
                _builder.appendImmediate(",\n", "  ");
              }
              _builder.append("`");
              String _columnName = OMLUtilities.columnName(attr);
              _builder.append(_columnName, "  ");
              _builder.append("` ");
              {
                Boolean _isUUID = OMLUtilities.isUUID(attr);
                if ((_isUUID).booleanValue()) {
                  _builder.append("CHAR(36) NOT NULL PRIMARY KEY");
                } else {
                  String _columnName_1 = OMLUtilities.columnName(attr);
                  boolean _equals = Objects.equal(_columnName_1, "kind");
                  if (_equals) {
                    _builder.append("INT NOT NULL COMMENT \'");
                    String _name = attr.getEType().getName();
                    _builder.append(_name, "  ");
                    _builder.append("\'");
                  } else {
                    Boolean _isIRIReference = OMLUtilities.isIRIReference(attr);
                    if ((_isIRIReference).booleanValue()) {
                      _builder.append("TEXT NOT NULL COMMENT \'");
                      String _abbreviatedTableName_6 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(OMLUtilities.EClassType(attr));
                      _builder.append(_abbreviatedTableName_6, "  ");
                      _builder.append(" (");
                      String _name_1 = attr.getEType().getName();
                      _builder.append(_name_1, "  ");
                      _builder.append(")\'");
                    } else {
                      Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(attr);
                      if ((_isLiteralFeature).booleanValue()) {
                        _builder.append("TEXT COMMENT \'(");
                        String _name_2 = attr.getEType().getName();
                        _builder.append(_name_2, "  ");
                        _builder.append(" value)\',");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("`");
                        String _columnName_2 = OMLUtilities.columnName(attr);
                        _builder.append(_columnName_2, "  ");
                        _builder.append("LiteralType` VARCHAR(30) COMMENT \'(");
                        String _name_3 = attr.getEType().getName();
                        _builder.append(_name_3, "  ");
                        _builder.append(" kind)\'");
                      } else {
                        if (((OMLUtilities.isClassFeature(attr)).booleanValue() && (attr.getLowerBound() == 0))) {
                          _builder.append("CHAR(36) NULL COMMENT \'");
                          String _abbreviatedTableName_7 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(OMLUtilities.EClassType(attr));
                          _builder.append(_abbreviatedTableName_7, "  ");
                          _builder.append(" (");
                          String _name_4 = attr.getEType().getName();
                          _builder.append(_name_4, "  ");
                          _builder.append(")\'");
                        } else {
                          if (((OMLUtilities.isClassFeature(attr)).booleanValue() && (attr.getLowerBound() > 0))) {
                            _builder.append("CHAR(36) NOT NULL COMMENT \'");
                            String _abbreviatedTableName_8 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(OMLUtilities.EClassType(attr));
                            _builder.append(_abbreviatedTableName_8, "  ");
                            _builder.append(" (");
                            String _name_5 = attr.getEType().getName();
                            _builder.append(_name_5, "  ");
                            _builder.append(")\'");
                          } else {
                            if ((Objects.equal(attr.getEType().getName(), "EBoolean") && (attr.getLowerBound() > 0))) {
                              _builder.append("BOOLEAN NOT NULL");
                            } else {
                              int _lowerBound = attr.getLowerBound();
                              boolean _greaterThan = (_lowerBound > 0);
                              if (_greaterThan) {
                                _builder.append("TEXT NOT NULL COMMENT \'");
                                String _name_6 = attr.getEType().getName();
                                _builder.append(_name_6, "  ");
                                _builder.append("\'");
                              } else {
                                _builder.append("TEXT COMMENT \'");
                                String _name_7 = attr.getEType().getName();
                                _builder.append(_name_7, "  ");
                                _builder.append("\'");
                              }
                            }
                          }
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
          _builder.newLine();
          _builder.append("  ");
          {
            final Function1<ETypedElement, Boolean> _function_9 = (ETypedElement it) -> {
              return Boolean.valueOf((((OMLUtilities.isClassFeature(it)).booleanValue() && (!(OMLUtilities.isIRIReference(it)).booleanValue())) && (eAbstract.contains(OMLUtilities.EClassType(it)) || eConcrete.contains(OMLUtilities.EClassType(it)))));
            };
            Iterable<ETypedElement> _filter_1 = IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass_3), _function_9);
            boolean _hasElements_1 = false;
            for(final ETypedElement attr_1 : _filter_1) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
              } else {
                _builder.appendImmediate(",\n\n", "  ");
              }
              _builder.append("CONSTRAINT `fk_");
              String _abbreviatedTableName_9 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_3);
              _builder.append(_abbreviatedTableName_9, "  ");
              _builder.append("_");
              String _columnName_3 = OMLUtilities.columnName(attr_1);
              _builder.append(_columnName_3, "  ");
              _builder.append("`");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("  ");
              _builder.append("FOREIGN KEY (`");
              String _columnName_4 = OMLUtilities.columnName(attr_1);
              _builder.append(_columnName_4, "    ");
              _builder.append("`)");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("  ");
              _builder.append("REFERENCES `OML`.`");
              String _abbreviatedTableName_10 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(OMLUtilities.EClassType(attr_1));
              _builder.append(_abbreviatedTableName_10, "    ");
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
          _builder.append("COMMENT = \'Concrete Information Table ");
          String _upperCaseInitialOrWord_3 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_3));
          _builder.append(_upperCaseInitialOrWord_3);
          _builder.append("\';");
          _builder.newLineIfNotEmpty();
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("USE `OML`;");
      _builder.newLine();
      _builder.append("DELIMITER $$");
      _builder.newLine();
      _builder.newLine();
      {
        for(final EClass eClass_4 : eConcrete) {
          {
            boolean _isEmpty = IterableExtensions.isEmpty(OMLUtilities.ESuperClasses(eClass_4));
            boolean _not = (!_isEmpty);
            if (_not) {
              _builder.append("-- -----------------------------------------------------");
              _builder.newLine();
              _builder.append("-- Concrete Information Table `OML`.`");
              String _abbreviatedTableName_11 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_4);
              _builder.append(_abbreviatedTableName_11);
              _builder.append("` (");
              String _upperCaseInitialOrWord_4 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_4));
              _builder.append(_upperCaseInitialOrWord_4);
              _builder.append(")");
              _builder.newLineIfNotEmpty();
              _builder.append("-- -----------------------------------------------------");
              _builder.newLine();
              _builder.newLine();
              _builder.append("DELIMITER $$");
              _builder.newLine();
              _builder.append("USE `OML`$$");
              _builder.newLine();
              _builder.append("CREATE DEFINER = CURRENT_USER TRIGGER `OML`.`");
              String _abbreviatedTableName_12 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_4);
              _builder.append(_abbreviatedTableName_12);
              _builder.append("_AFTER_INSERT` AFTER INSERT ON `");
              String _abbreviatedTableName_13 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_4);
              _builder.append(_abbreviatedTableName_13);
              _builder.append("` FOR EACH ROW");
              _builder.newLineIfNotEmpty();
              _builder.append("BEGIN");
              _builder.newLine();
              {
                final Function1<EClass, String> _function_10 = (EClass it) -> {
                  return OMLSpecificationOMLSQLGenerator.abbreviatedTableName(it);
                };
                List<EClass> _sortBy_2 = IterableExtensions.<EClass, String>sortBy(eClass_4.getEAllSuperTypes(), _function_10);
                for(final EClass eSup : _sortBy_2) {
                  _builder.append("-- ");
                  String _upperCaseInitialOrWord_5 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eSup));
                  _builder.append(_upperCaseInitialOrWord_5);
                  _builder.append("(x) if ");
                  String _upperCaseInitialOrWord_6 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_4));
                  _builder.append(_upperCaseInitialOrWord_6);
                  _builder.append("(x)");
                  _builder.newLineIfNotEmpty();
                  _builder.append("insert into `OML`.`");
                  String _abbreviatedTableName_14 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eSup);
                  _builder.append(_abbreviatedTableName_14);
                  _builder.append("`(`uuid`) values(new.`uuid`);");
                  _builder.newLineIfNotEmpty();
                }
              }
              _builder.append("END$$");
              _builder.newLine();
              _builder.newLine();
              _builder.append("DELIMITER $$");
              _builder.newLine();
              _builder.append("USE `OML`$$");
              _builder.newLine();
              _builder.append("CREATE DEFINER = CURRENT_USER TRIGGER `OML`.`");
              String _abbreviatedTableName_15 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_4);
              _builder.append(_abbreviatedTableName_15);
              _builder.append("_AFTER_DELETE` AFTER DELETE ON `");
              String _abbreviatedTableName_16 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass_4);
              _builder.append(_abbreviatedTableName_16);
              _builder.append("` FOR EACH ROW");
              _builder.newLineIfNotEmpty();
              _builder.append("BEGIN");
              _builder.newLine();
              {
                final Function1<EClass, String> _function_11 = (EClass it) -> {
                  return OMLSpecificationOMLSQLGenerator.abbreviatedTableName(it);
                };
                List<EClass> _sortBy_3 = IterableExtensions.<EClass, String>sortBy(eClass_4.getEAllSuperTypes(), _function_11);
                for(final EClass eSup_1 : _sortBy_3) {
                  _builder.append("-- ");
                  String _upperCaseInitialOrWord_7 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eSup_1));
                  _builder.append(_upperCaseInitialOrWord_7);
                  _builder.append("(x) if ");
                  String _upperCaseInitialOrWord_8 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_4));
                  _builder.append(_upperCaseInitialOrWord_8);
                  _builder.append("(x)");
                  _builder.newLineIfNotEmpty();
                  _builder.append("delete from `OML`.`");
                  String _abbreviatedTableName_17 = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eSup_1);
                  _builder.append(_abbreviatedTableName_17);
                  _builder.append("`;");
                  _builder.newLineIfNotEmpty();
                }
              }
              _builder.append("END$$");
              _builder.newLine();
              _builder.newLine();
            }
          }
        }
      }
      _builder.newLine();
      _builder.append("DELIMITER ;");
      _builder.newLine();
      _builder.newLine();
      _builder.append("SET SQL_MODE=@OLD_SQL_MODE;");
      _builder.newLine();
      _builder.append("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;");
      _builder.newLine();
      _builder.append("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  private final static String SPACES = "                     ";
  
  public static String pad(final EClass eClass) {
    String _xblockexpression = null;
    {
      final String abbrev = OMLSpecificationOMLSQLGenerator.abbreviatedTableName(eClass);
      int _length = OMLSpecificationOMLSQLGenerator.SPACES.length();
      int _length_1 = abbrev.length();
      int _minus = (_length - _length_1);
      _xblockexpression = OMLSpecificationOMLSQLGenerator.SPACES.substring(1, _minus);
    }
    return _xblockexpression;
  }
  
  public static String abbreviatedTableName(final EClass eClass) {
    String _xblockexpression = null;
    {
      final String abbrev = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass)).replace("CrossReferencabilityKinds", "CRTK").replace("CrossReferencableKinds", "CRBK").replace("CrossReference", "CRef").replace("DataRelationship", "DRel").replace("DescriptionBox", "DBox").replace("DescriptionBoxes", "DBox").replace("ExtendsClosedWorldDefinitions", "ExtCWDef").replace("ExtrinsicIdentityKinds", "EIdK").replace("IntrinsicIdentityKinds", "IIdK").replace("IdentityKinds", "Ik").replace("LogicalElements", "LogEs").replace("ReifiedRelationship", "RR").replace("ScalarDataProperties", "ScPs").replace("ScalarDataProperty", "ScP").replace("SingletonInstances", "SI").replace("StructuredDataProperties", "StPs").replace("StructuredDataProperty", "StP").replace("TerminologyBoxes", "TBox").replace("TerminologyBox", "TBox").replace("UnreifiedRelationship", "UR").replace("Annotation", "Annot").replace("Axioms", "Ax").replace("Assertions", "Asts").replace("Binary", "Bin").replace("Bundle", "Bdl").replace("Concept", "C").replace("Conceptual", "C").replace("Context", "Ctxt").replace("Datatypes", "Dt").replace("DataRange", "Dr").replace("Directed", "Dir").replace("DisjointUnion", "DsjU").replace("Disjoint", "Dsjt").replace("Disjunctions", "Dsju").replace("Designation", "Des").replace("Entity", "E").replace("Entities", "Es").replace("Element", "Elt").replace("Existential", "Ex").replace("Forward", "Fwd").replace("Inverse", "Inv").replace("Instance", "I").replace("Literal", "Lit").replace("Logical", "Log").replace("Module", "Mod").replace("Predicates", "P").replace("Particular", "Ptr").replace("Properties", "Props").replace("Property", "Prop").replace("Refinement", "Rfn").replace("Relationship", "Rel").replace("Resource", "Res").replace("Restricted", "Rest").replace("Restriction", "R").replace("Reverse", "Rev").replace("Scalar", "Sc").replace("Segment", "Seg").replace("Singleton", "S1").replace("Source", "Src").replace("Specialization", "Spe").replace("Specific", "Spe").replace("Statements", "St").replace("Structure", "St").replace("Target", "Tgt").replace("Terminology", "Tlgy").replace("Tuples", "Ts").replace("Unary", "Ury").replace("Universal", "Ux").replace("Value", "Val");
      _xblockexpression = abbrev;
    }
    return _xblockexpression;
  }
}
