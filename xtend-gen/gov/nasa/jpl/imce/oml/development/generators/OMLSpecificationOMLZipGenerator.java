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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class OMLSpecificationOMLZipGenerator extends OMLUtilities {
  public static void main(final String[] args) {
    int _length = args.length;
    boolean _notEquals = (1 != _length);
    if (_notEquals) {
      System.err.println("usage: <dir> where <dir> is the directory of the /gov.nasa.jpl.imce.oml.zip project");
      System.exit(1);
    }
    final OMLSpecificationOMLZipGenerator gen = new OMLSpecificationOMLZipGenerator();
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
    final String packageQName = "gov.nasa.jpl.imce.oml.zip";
    final Path bundlePath = Paths.get(targetDir);
    final Path oml_Folder = bundlePath.resolve("src/gov/nasa/jpl/imce/oml/zip");
    oml_Folder.toFile().mkdirs();
    this.generate(ePackages, 
      oml_Folder.toAbsolutePath().toString(), packageQName, 
      "OMLSpecificationTables");
  }
  
  public void generate(final List<EPackage> ePackages, final String targetFolder, final String packageQName, final String tableName) {
    try {
      File _file = new File((((targetFolder + File.separator) + tableName) + ".xtend"));
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
      final Function1<EClass, Boolean> _function_2 = (EClass it) -> {
        final Function1<EClass, Boolean> _function_3 = (EClass it_1) -> {
          String _name = it_1.getName();
          return Boolean.valueOf(Objects.equal(_name, "Module"));
        };
        boolean _exists = IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_3);
        return Boolean.valueOf((!_exists));
      };
      final Iterable<EClass> eClassesExceptModules = IterableExtensions.<EClass>filter(eClasses, _function_2);
      StringConcatenation _builder = new StringConcatenation();
      String _copyright = OMLUtilities.copyright();
      _builder.append(_copyright);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("package ");
      _builder.append(packageQName);
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("import java.io.BufferedReader");
      _builder.newLine();
      _builder.append("import java.io.ByteArrayOutputStream");
      _builder.newLine();
      _builder.append("import java.io.File");
      _builder.newLine();
      _builder.append("import java.io.InputStreamReader");
      _builder.newLine();
      _builder.append("import java.io.PrintWriter");
      _builder.newLine();
      _builder.append("import java.lang.IllegalArgumentException");
      _builder.newLine();
      _builder.append("import java.nio.charset.StandardCharsets");
      _builder.newLine();
      _builder.append("import java.util.ArrayList");
      _builder.newLine();
      _builder.append("import java.util.Collections");
      _builder.newLine();
      _builder.append("import java.util.HashMap");
      _builder.newLine();
      _builder.append("import java.util.Map");
      _builder.newLine();
      _builder.append("import org.apache.commons.compress.archivers.zip.ZipArchiveEntry");
      _builder.newLine();
      _builder.append("import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream");
      _builder.newLine();
      _builder.append("import org.apache.commons.compress.archivers.zip.ZipFile");
      _builder.newLine();
      _builder.append("import org.eclipse.emf.common.util.URI");
      _builder.newLine();
      _builder.append("import org.eclipse.emf.ecore.resource.Resource");
      _builder.newLine();
      _builder.append("import org.eclipse.emf.ecore.resource.ResourceSet");
      _builder.newLine();
      _builder.append("import org.eclipse.xtext.xbase.lib.Pair");
      _builder.newLine();
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.extensions.OMLTables");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.bundles.AnonymousConceptUnionAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.bundles.Bundle");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.bundles.BundledTerminologyAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.bundles.ConceptTreeDisjunction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.bundles.RootConceptTaxonomyAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.bundles.SpecificDisjointConceptAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.common.AnnotationProperty");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.common.AnnotationPropertyValue");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.common.Extent");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.common.LogicalElement");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.common.Module");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.ConceptInstance");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.ConceptualEntitySingletonInstance");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.DescriptionBox");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.DescriptionBoxExtendsClosedWorldDefinitions");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.DescriptionBoxRefinement");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.ReifiedRelationshipInstance");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.ReifiedRelationshipInstanceDomain");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.ReifiedRelationshipInstanceRange");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.ScalarDataPropertyValue");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.SingletonInstanceScalarDataPropertyValue");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.SingletonInstanceStructuredDataPropertyContext");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.SingletonInstanceStructuredDataPropertyValue");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.StructuredDataPropertyTuple");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.UnreifiedRelationshipInstanceTuple");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.extensions.OMLExtensions");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.graphs.ConceptDesignationTerminologyAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.graphs.TerminologyGraph");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.graphs.TerminologyNestingAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.Aspect");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.AspectSpecializationAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.BinaryScalarRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ChainRule");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.Concept");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ConceptSpecializationAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ConceptualRelationship");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.DataRange");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.DataRelationshipToScalar");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.DataRelationshipToStructure");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.Entity");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityRelationship");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityExistentialRestrictionAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityUniversalRestrictionAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataProperty");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyExistentialRestrictionAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyParticularRestrictionAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityScalarDataPropertyUniversalRestrictionAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityStructuredDataProperty");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.EntityStructuredDataPropertyParticularRestrictionAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ForwardProperty");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.InverseProperty");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.IRIScalarRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.NumericScalarRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.PlainLiteralScalarRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.Predicate");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationship");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.RestrictableRelationship");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionScalarDataPropertyValue");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionStructuredDataPropertyContext");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.RestrictionStructuredDataPropertyTuple");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.RuleBodySegment");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.Scalar");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ScalarDataProperty");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ScalarOneOfLiteralAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ScalarOneOfRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.SegmentPredicate");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.SpecializedReifiedRelationship");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.StringScalarRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.SubDataPropertyOfAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.SubObjectPropertyOfAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.Structure");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.StructuredDataProperty");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.SynonymScalarRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.TerminologyBox");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.TerminologyExtensionAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.TimeScalarRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.UnreifiedRelationship");
      _builder.newLine();
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.common.CommonFactory");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.TerminologiesFactory");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.graphs.GraphsFactory");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.bundles.BundlesFactory");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.DescriptionsFactory");
      _builder.newLine();
      _builder.newLine();
      _builder.append("/**");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("* @generated");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("*/");
      _builder.newLine();
      _builder.append("class ");
      _builder.append(tableName);
      _builder.append(" {");
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      {
        for(final EClass eClass : eClasses) {
          _builder.append("  ");
          _builder.append("protected val Map<String, Pair<");
          String _name = eClass.getName();
          _builder.append(_name, "  ");
          _builder.append(", Map<String,String>>> ");
          String _tableVariableName = OMLUtilities.tableVariableName(eClass);
          _builder.append(_tableVariableName, "  ");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<Module, Map<String,String>>> modules");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<LogicalElement, Map<String,String>>> logicalElements");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<Entity, Map<String,String>>> entities");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<EntityRelationship, Map<String,String>>> entityRelationships");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<ConceptualRelationship, Map<String,String>>> conceptualRelationships");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<DataRange, Map<String,String>>> dataRanges ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<DataRelationshipToScalar, Map<String,String>>> dataRelationshipToScalars");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<DataRelationshipToStructure, Map<String,String>>> dataRelationshipToStructures");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<Predicate, Map<String,String>>> predicates");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<RestrictableRelationship, Map<String,String>>> restrictableRelationships");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<RestrictionStructuredDataPropertyContext, Map<String,String>>> restrictionStructuredDataPropertyContexts");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<TerminologyBox, Map<String,String>>> terminologyBoxes");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<ConceptTreeDisjunction, Map<String,String>>> conceptTreeDisjunctions");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<ConceptualEntitySingletonInstance, Map<String,String>>> conceptualEntitySingletonInstances");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<SingletonInstanceStructuredDataPropertyContext, Map<String,String>>> singletonInstanceStructuredDataPropertyContexts");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("extension CommonFactory omlCommonFactory");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("extension TerminologiesFactory omlTerminologiesFactory");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("extension GraphsFactory omlGraphsFactory");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("extension BundlesFactory omlBundlesFactory");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("extension DescriptionsFactory omlDescriptionsFactory");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("new() {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("omlCommonFactory = CommonFactory.eINSTANCE");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("omlTerminologiesFactory = TerminologiesFactory.eINSTANCE");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("omlGraphsFactory = GraphsFactory.eINSTANCE");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("omlBundlesFactory = BundlesFactory.eINSTANCE");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("omlDescriptionsFactory = DescriptionsFactory.eINSTANCE");
      _builder.newLine();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("  \t");
      {
        boolean _hasElements = false;
        for(final EClass eClass_1 : eClasses) {
          if (!_hasElements) {
            _hasElements = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          String _tableVariableName_1 = OMLUtilities.tableVariableName(eClass_1);
          _builder.append(_tableVariableName_1, "  \t");
          _builder.append(" = new HashMap<String, Pair<");
          String _name_1 = eClass_1.getName();
          _builder.append(_name_1, "  \t");
          _builder.append(", Map<String,String>>>()");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("modules = new HashMap<String, Pair<Module, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("logicalElements = new HashMap<String, Pair<LogicalElement, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("entities = new HashMap<String, Pair<Entity, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("entityRelationships = new HashMap<String, Pair<EntityRelationship, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("conceptualRelationships = new HashMap<String, Pair<ConceptualRelationship, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("dataRanges = new HashMap<String, Pair<DataRange, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("dataRelationshipToScalars = new HashMap<String, Pair<DataRelationshipToScalar, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("dataRelationshipToStructures = new HashMap<String, Pair<DataRelationshipToStructure, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("predicates = new HashMap<String, Pair<Predicate, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("restrictableRelationships = new HashMap<String, Pair<RestrictableRelationship, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("restrictionStructuredDataPropertyContexts = new HashMap<String, Pair<RestrictionStructuredDataPropertyContext, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("terminologyBoxes = new HashMap<String, Pair<TerminologyBox, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("conceptTreeDisjunctions = new HashMap<String, Pair<ConceptTreeDisjunction, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("conceptualEntitySingletonInstances = new HashMap<String, Pair<ConceptualEntitySingletonInstance, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("singletonInstanceStructuredDataPropertyContexts = new HashMap<String, Pair<SingletonInstanceStructuredDataPropertyContext, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("static def void save(Extent e, ZipArchiveOutputStream zos) {");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("var ZipArchiveEntry entry = null");
      _builder.newLine();
      {
        for(final EClass eClass_2 : eClasses) {
          _builder.append("    ");
          _builder.append("// ");
          String _name_2 = eClass_2.getName();
          _builder.append(_name_2, "    ");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("entry = new ZipArchiveEntry(\"");
          String _pluralize = OMLUtilities.pluralize(eClass_2.getName());
          _builder.append(_pluralize, "    ");
          _builder.append(".json\")");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("zos.putArchiveEntry(entry)");
          _builder.newLine();
          _builder.append("    ");
          _builder.append("try {");
          _builder.newLine();
          _builder.append("    ");
          _builder.append("  ");
          _builder.append("zos.write(");
          String _tableVariableName_2 = OMLUtilities.tableVariableName(eClass_2);
          _builder.append(_tableVariableName_2, "      ");
          _builder.append("ByteArray(e))");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("} finally {");
          _builder.newLine();
          _builder.append("    ");
          _builder.append("  ");
          _builder.append("zos.closeArchiveEntry()");
          _builder.newLine();
          _builder.append("    ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      {
        for(final EClass eClass_3 : eClasses) {
          _builder.append("  ");
          _builder.append("protected static def byte[] ");
          String _tableVariableName_3 = OMLUtilities.tableVariableName(eClass_3);
          _builder.append(_tableVariableName_3, "  ");
          _builder.append("ByteArray(Extent e) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("val ByteArrayOutputStream bos = new ByteArrayOutputStream()");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("val PrintWriter pw = new PrintWriter(bos)");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("OMLTables.");
          String _tableVariableName_4 = OMLUtilities.tableVariableName(eClass_3);
          _builder.append(_tableVariableName_4, "  \t");
          _builder.append("(e).forEach[it |");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("pw.print(\"{\")");
          _builder.newLine();
          {
            Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass_3);
            boolean _hasElements_1 = false;
            for(final ETypedElement attr : _schemaAPIOrOrderingKeyAttributes) {
              if (!_hasElements_1) {
                _hasElements_1 = true;
              } else {
                _builder.appendImmediate("\npw.print(\",\")", "      ");
              }
              _builder.append("  ");
              _builder.append("    ");
              _builder.append("pw.print(\"\\\"");
              String _columnName = OMLUtilities.columnName(attr);
              _builder.append(_columnName, "      ");
              _builder.append("\\\":\")");
              _builder.newLineIfNotEmpty();
              {
                Boolean _isIRIReference = OMLUtilities.isIRIReference(attr);
                if ((_isIRIReference).booleanValue()) {
                  _builder.append("  ");
                  _builder.append("    ");
                  _builder.append("pw.print(\"\\\"\")");
                  _builder.newLine();
                  _builder.append("  ");
                  _builder.append("    ");
                  _builder.append("pw.print(it.");
                  String _featureQuery = OMLUtilities.getFeatureQuery(attr);
                  _builder.append(_featureQuery, "      ");
                  _builder.append(".iri())");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("    ");
                  _builder.append("pw.print(\"\\\"\")");
                  _builder.newLine();
                } else {
                  Boolean _isBoolean = OMLUtilities.isBoolean(attr);
                  if ((_isBoolean).booleanValue()) {
                    _builder.append("  ");
                    _builder.append("    ");
                    _builder.append("pw.print(\"\\\"\")");
                    _builder.newLine();
                    _builder.append("  ");
                    _builder.append("    ");
                    _builder.append("pw.print(it.");
                    String _featureQuery_1 = OMLUtilities.getFeatureQuery(attr);
                    _builder.append(_featureQuery_1, "      ");
                    _builder.append(")");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("    ");
                    _builder.append("pw.print(\"\\\"\")");
                    _builder.newLine();
                  } else {
                    Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(attr);
                    if ((_isLiteralFeature).booleanValue()) {
                      _builder.append("  ");
                      _builder.append("    ");
                      _builder.append("pw.print(OMLTables.toString(it.");
                      String _featureQuery_2 = OMLUtilities.getFeatureQuery(attr);
                      _builder.append(_featureQuery_2, "      ");
                      _builder.append("))");
                      _builder.newLineIfNotEmpty();
                    } else {
                      if (((OMLUtilities.isClassFeature(attr)).booleanValue() && (attr.getLowerBound() == 0))) {
                        _builder.append("  ");
                        _builder.append("    ");
                        _builder.append("if (null !== ");
                        String _featureQuery_3 = OMLUtilities.getFeatureQuery(attr);
                        _builder.append(_featureQuery_3, "      ");
                        _builder.append(") {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("pw.print(\"\\\"\")");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("pw.print(it.");
                        String _featureQuery_4 = OMLUtilities.getFeatureQuery(attr);
                        _builder.append(_featureQuery_4, "        ");
                        _builder.append("?.uuid())");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("pw.print(\"\\\"\")");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("    ");
                        _builder.append("} else");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("pw.print(\"null\")");
                        _builder.newLine();
                      } else {
                        if (((OMLUtilities.isClassFeature(attr)).booleanValue() && (attr.getLowerBound() > 0))) {
                          _builder.append("  ");
                          _builder.append("    ");
                          _builder.append("pw.print(\"\\\"\")");
                          _builder.newLine();
                          _builder.append("  ");
                          _builder.append("    ");
                          _builder.append("pw.print(it.");
                          String _featureQuery_5 = OMLUtilities.getFeatureQuery(attr);
                          _builder.append(_featureQuery_5, "      ");
                          _builder.append(".uuid())");
                          _builder.newLineIfNotEmpty();
                          _builder.append("  ");
                          _builder.append("    ");
                          _builder.append("pw.print(\"\\\"\")");
                          _builder.newLine();
                        } else {
                          Boolean _isUUID = OMLUtilities.isUUID(attr);
                          if ((_isUUID).booleanValue()) {
                            _builder.append("  ");
                            _builder.append("    ");
                            _builder.append("pw.print(\"\\\"\")");
                            _builder.newLine();
                            _builder.append("  ");
                            _builder.append("    ");
                            _builder.append("pw.print(it.");
                            String _featureQuery_6 = OMLUtilities.getFeatureQuery(attr);
                            _builder.append(_featureQuery_6, "      ");
                            _builder.append(")");
                            _builder.newLineIfNotEmpty();
                            _builder.append("  ");
                            _builder.append("    ");
                            _builder.append("pw.print(\"\\\"\")");
                            _builder.newLine();
                          } else {
                            _builder.append("  ");
                            _builder.append("    ");
                            _builder.append("pw.print(OMLTables.toString(it.");
                            String _featureQuery_7 = OMLUtilities.getFeatureQuery(attr);
                            _builder.append(_featureQuery_7, "      ");
                            _builder.append("))");
                            _builder.newLineIfNotEmpty();
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          _builder.append("  ");
          _builder.append("    ");
          _builder.append("pw.println(\"}\")");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("]");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("pw.close()");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("  ");
          _builder.append("return bos.toByteArray()");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
        }
      }
      _builder.append("  \t\t    \t    ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("/**");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("* Uses an OMLSpecificationTables for resolving cross-references in the *.oml and *.omlzip representations.");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("* When there are no more OML resources to load, it is necessary to call explicitly: ");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("* ");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("*     OMLZipResource.clearOMLSpecificationTables(rs)");
      _builder.newLine();
      _builder.append("   ");
      _builder.append("*/");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("static def void load(ResourceSet rs, OMLZipResource r, File omlZipFile) {");
      _builder.newLine();
      _builder.newLine();
      _builder.append("    ");
      _builder.append("val tables = OMLZipResource.getOrInitializeOMLSpecificationTables(rs)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("val ext = tables.omlCommonFactory.createExtent()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("r.contents.add(ext)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("val zip = new ZipFile(omlZipFile)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("Collections.list(zip.entries).forEach[ze | ");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("val is = zip.getInputStream(ze)");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("val buffer = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("val lines = new ArrayList<String>()");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("lines.addAll(buffer.lines().iterator.toIterable)");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("buffer.close()");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("switch ze.name {");
      _builder.newLine();
      {
        for(final EClass eClass_4 : eClasses) {
          _builder.append("  \t    ");
          _builder.append("case \"");
          String _pluralize_1 = OMLUtilities.pluralize(eClass_4.getName());
          _builder.append(_pluralize_1, "  \t    ");
          _builder.append(".json\":");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t    ");
          _builder.append("  ");
          _builder.append("tables.read");
          String _upperCaseInitialOrWord = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_4));
          _builder.append(_upperCaseInitialOrWord, "  \t      ");
          _builder.append("(ext, lines)");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("        ");
      _builder.append("default:");
      _builder.newLine();
      _builder.append("          ");
      _builder.append("throw new IllegalArgumentException(\"");
      _builder.append(tableName, "          ");
      _builder.append(".load(): unrecognized table name: \"+ze.name)");
      _builder.newLineIfNotEmpty();
      _builder.append("      ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("]");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("zip.close()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("tables.resolve(rs, r)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        for(final EClass eClass_5 : eClasses) {
          _builder.append("  ");
          _builder.append("protected def void read");
          String _upperCaseInitialOrWord_1 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_5));
          _builder.append(_upperCaseInitialOrWord_1, "  ");
          _builder.append("(Extent ext, ArrayList<String> lines) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("val kvs = OMLZipResource.lines2tuples(lines)");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("while (!kvs.empty) {");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("val kv = kvs.remove(kvs.size - 1)");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("val oml = create");
          String _name_3 = eClass_5.getName();
          _builder.append(_name_3, "  \t  ");
          _builder.append("()");
          _builder.newLineIfNotEmpty();
          {
            final Function1<EClass, Boolean> _function_3 = (EClass it) -> {
              String _name_4 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_4, "Module"));
            };
            boolean _exists = IterableExtensions.<EClass>exists(eClass_5.getEAllSuperTypes(), _function_3);
            if (_exists) {
              _builder.append("  ");
              _builder.append("\t  ");
              _builder.append("ext.getModules.add(oml)");
              _builder.newLine();
            }
          }
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("val uuid = kv.remove(\"uuid\")");
          _builder.newLine();
          {
            Iterable<ETypedElement> _schemaAPIOrOrderingKeyAttributes_1 = OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass_5);
            for(final ETypedElement attr_1 : _schemaAPIOrOrderingKeyAttributes_1) {
              {
                Boolean _isLiteralFeature_1 = OMLUtilities.isLiteralFeature(attr_1);
                if ((_isLiteralFeature_1).booleanValue()) {
                  _builder.append("  ");
                  _builder.append("\t  ");
                  _builder.append("oml.");
                  String _name_4 = attr_1.getName();
                  _builder.append(_name_4, "  \t  ");
                  _builder.append(" = OMLTables.to");
                  String _name_5 = attr_1.getEType().getName();
                  _builder.append(_name_5, "  \t  ");
                  _builder.append("(kv.remove(\"");
                  String _columnName_1 = OMLUtilities.columnName(attr_1);
                  _builder.append(_columnName_1, "  \t  ");
                  _builder.append("\"))");
                } else {
                  if (((!(OMLUtilities.isClassFeature(attr_1)).booleanValue()) && (!Objects.equal(attr_1.getName(), "uuid")))) {
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t  ");
                    _builder.append("oml.");
                    String _name_6 = attr_1.getName();
                    _builder.append(_name_6, "  \t  ");
                    _builder.append(" = OMLTables.to");
                    String _name_7 = attr_1.getEType().getName();
                    _builder.append(_name_7, "  \t  ");
                    _builder.append("(kv.remove(\"");
                    String _columnName_2 = OMLUtilities.columnName(attr_1);
                    _builder.append(_columnName_2, "  \t  ");
                    _builder.append("\"))");
                  }
                }
              }
              _builder.newLineIfNotEmpty();
            }
          }
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("val pair = new Pair<");
          String _name_8 = eClass_5.getName();
          _builder.append(_name_8, "  \t  ");
          _builder.append(", Map<String,String>>(oml, kv)");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          String _tableVariableName_5 = OMLUtilities.tableVariableName(eClass_5);
          _builder.append(_tableVariableName_5, "  \t  ");
          _builder.append(".put(uuid, pair)");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("include");
          String _upperCaseInitialOrWord_2 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_5));
          _builder.append(_upperCaseInitialOrWord_2, "  \t  ");
          _builder.append("(uuid, oml)");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("}");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected def <U,V extends U> void includeMap(Map<String, Pair<U, Map<String, String>>> uMap, Map<String, Pair<V, Map<String, String>>> vMap) {");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("vMap.forEach[uuid,kv|uMap.put(uuid, new Pair<U, Map<String, String>>(kv.key, Collections.emptyMap))]");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        for(final EClass eClass_6 : eClasses) {
          _builder.append("  ");
          _builder.append("protected def void include");
          String _upperCaseInitialOrWord_3 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_6));
          _builder.append(_upperCaseInitialOrWord_3, "  ");
          _builder.append("(String uuid, ");
          String _name_9 = eClass_6.getName();
          _builder.append(_name_9, "  ");
          _builder.append(" oml) {");
          _builder.newLineIfNotEmpty();
          {
            final Function1<EClass, Boolean> _function_4 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "Module"));
            };
            boolean _exists_1 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_4);
            if (_exists_1) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("modules.put(uuid, new Pair<Module, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_5 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "LogicalElement"));
            };
            boolean _exists_2 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_5);
            if (_exists_2) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("logicalElements.put(uuid, new Pair<LogicalElement, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_6 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "Entity"));
            };
            boolean _exists_3 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_6);
            if (_exists_3) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("entities.put(uuid, new Pair<Entity, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_7 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "EntityRelationship"));
            };
            boolean _exists_4 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_7);
            if (_exists_4) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("entityRelationships.put(uuid, new Pair<EntityRelationship, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_8 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "ConceptualRelationship"));
            };
            boolean _exists_5 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_8);
            if (_exists_5) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("conceptualRelationships.put(uuid, new Pair<ConceptualRelationship, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_9 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "DataRange"));
            };
            boolean _exists_6 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_9);
            if (_exists_6) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("dataRanges.put(uuid, new Pair<DataRange, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_10 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "DataRelationshipToScalar"));
            };
            boolean _exists_7 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_10);
            if (_exists_7) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("dataRelationshipToScalars.put(uuid, new Pair<DataRelationshipToScalar, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_11 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "DataRelationshipToStructure"));
            };
            boolean _exists_8 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_11);
            if (_exists_8) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("dataRelationshipToStructures.put(uuid, new Pair<DataRelationshipToStructure, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_12 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "Predicate"));
            };
            boolean _exists_9 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_12);
            if (_exists_9) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("predicates.put(uuid, new Pair<Predicate, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_13 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "RestrictableRelationship"));
            };
            boolean _exists_10 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_13);
            if (_exists_10) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("restrictableRelationships.put(uuid, new Pair<RestrictableRelationship, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_14 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "RestrictionStructuredDataPropertyContext"));
            };
            boolean _exists_11 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_14);
            if (_exists_11) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("restrictionStructuredDataPropertyContexts.put(uuid, new Pair<RestrictionStructuredDataPropertyContext, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_15 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "TerminologyBox"));
            };
            boolean _exists_12 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_15);
            if (_exists_12) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("terminologyBoxes.put(uuid, new Pair<TerminologyBox, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("terminologyBoxes.put(oml.iri(), new Pair<TerminologyBox, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_16 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "ConceptTreeDisjunction"));
            };
            boolean _exists_13 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_16);
            if (_exists_13) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("conceptTreeDisjunctions.put(uuid, new Pair<ConceptTreeDisjunction, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_17 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "ConceptualEntitySingletonInstance"));
            };
            boolean _exists_14 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_17);
            if (_exists_14) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("conceptualEntitySingletonInstances.put(uuid, new Pair<ConceptualEntitySingletonInstance, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_18 = (EClass it) -> {
              String _name_10 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_10, "SingletonInstanceStructuredDataPropertyContext"));
            };
            boolean _exists_15 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_18);
            if (_exists_15) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("singletonInstanceStructuredDataPropertyContexts.put(uuid, new Pair<SingletonInstanceStructuredDataPropertyContext, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            String _name_10 = eClass_6.getName();
            boolean _equals = Objects.equal(_name_10, "DescriptionBox");
            if (_equals) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("descriptionBoxes.put(oml.iri(), new Pair<DescriptionBox, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          _builder.append("  ");
          _builder.append("\t");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected def void resolve(ResourceSet rs, OMLZipResource r) {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for LogicalElement cross references");
      _builder.newLine();
      _builder.append("    ");
      {
        final Function1<EClass, Boolean> _function_19 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_20 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "LogicalElement"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_20));
        };
        Iterable<EClass> _filter_1 = IterableExtensions.<EClass>filter(eClasses, _function_19);
        boolean _hasElements_2 = false;
        for(final EClass eClass_7 : _filter_1) {
          if (!_hasElements_2) {
            _hasElements_2 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(logicalElements, ");
          String _tableVariableName_6 = OMLUtilities.tableVariableName(eClass_7);
          _builder.append(_tableVariableName_6, "    ");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for Entity cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_20 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_21 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "Entity"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_21));
        };
        Iterable<EClass> _filter_2 = IterableExtensions.<EClass>filter(eClasses, _function_20);
        boolean _hasElements_3 = false;
        for(final EClass eClass_8 : _filter_2) {
          if (!_hasElements_3) {
            _hasElements_3 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(entities, ");
          String _tableVariableName_7 = OMLUtilities.tableVariableName(eClass_8);
          _builder.append(_tableVariableName_7, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for EntityRelationship cross references");
      _builder.newLine();
      _builder.append("    ");
      {
        final Function1<EClass, Boolean> _function_21 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_22 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "EntityRelationship"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_22));
        };
        Iterable<EClass> _filter_3 = IterableExtensions.<EClass>filter(eClasses, _function_21);
        boolean _hasElements_4 = false;
        for(final EClass eClass_9 : _filter_3) {
          if (!_hasElements_4) {
            _hasElements_4 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(entityRelationships, ");
          String _tableVariableName_8 = OMLUtilities.tableVariableName(eClass_9);
          _builder.append(_tableVariableName_8, "    ");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for ConceptualRelationship cross references");
      _builder.newLine();
      _builder.append("    ");
      {
        final Function1<EClass, Boolean> _function_22 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_23 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "ConceptualRelationship"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_23));
        };
        Iterable<EClass> _filter_4 = IterableExtensions.<EClass>filter(eClasses, _function_22);
        boolean _hasElements_5 = false;
        for(final EClass eClass_10 : _filter_4) {
          if (!_hasElements_5) {
            _hasElements_5 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(conceptualRelationships, ");
          String _tableVariableName_9 = OMLUtilities.tableVariableName(eClass_10);
          _builder.append(_tableVariableName_9, "    ");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for DataRange cross references");
      _builder.newLine();
      _builder.append("    ");
      {
        final Function1<EClass, Boolean> _function_23 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_24 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "DataRange"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_24));
        };
        Iterable<EClass> _filter_5 = IterableExtensions.<EClass>filter(eClasses, _function_23);
        boolean _hasElements_6 = false;
        for(final EClass eClass_11 : _filter_5) {
          if (!_hasElements_6) {
            _hasElements_6 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(dataRanges, ");
          String _tableVariableName_10 = OMLUtilities.tableVariableName(eClass_11);
          _builder.append(_tableVariableName_10, "    ");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for DataRelationshipToScalar cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_24 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_25 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "DataRelationshipToScalar"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_25));
        };
        Iterable<EClass> _filter_6 = IterableExtensions.<EClass>filter(eClasses, _function_24);
        boolean _hasElements_7 = false;
        for(final EClass eClass_12 : _filter_6) {
          if (!_hasElements_7) {
            _hasElements_7 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(dataRelationshipToScalars, ");
          String _tableVariableName_11 = OMLUtilities.tableVariableName(eClass_12);
          _builder.append(_tableVariableName_11, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for DataRelationshipToStructure cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_25 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_26 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "DataRelationshipToStructure"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_26));
        };
        Iterable<EClass> _filter_7 = IterableExtensions.<EClass>filter(eClasses, _function_25);
        boolean _hasElements_8 = false;
        for(final EClass eClass_13 : _filter_7) {
          if (!_hasElements_8) {
            _hasElements_8 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(dataRelationshipToStructures, ");
          String _tableVariableName_12 = OMLUtilities.tableVariableName(eClass_13);
          _builder.append(_tableVariableName_12, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for Predicate cross references");
      _builder.newLine();
      _builder.append("    ");
      {
        final Function1<EClass, Boolean> _function_26 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_27 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "Predicate"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_27));
        };
        Iterable<EClass> _filter_8 = IterableExtensions.<EClass>filter(eClasses, _function_26);
        boolean _hasElements_9 = false;
        for(final EClass eClass_14 : _filter_8) {
          if (!_hasElements_9) {
            _hasElements_9 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(predicates, ");
          String _tableVariableName_13 = OMLUtilities.tableVariableName(eClass_14);
          _builder.append(_tableVariableName_13, "    ");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for RestrictableRelationship cross references");
      _builder.newLine();
      _builder.append("    ");
      {
        final Function1<EClass, Boolean> _function_27 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_28 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "RestrictableRelationship"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_28));
        };
        Iterable<EClass> _filter_9 = IterableExtensions.<EClass>filter(eClasses, _function_27);
        boolean _hasElements_10 = false;
        for(final EClass eClass_15 : _filter_9) {
          if (!_hasElements_10) {
            _hasElements_10 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(restrictableRelationships, ");
          String _tableVariableName_14 = OMLUtilities.tableVariableName(eClass_15);
          _builder.append(_tableVariableName_14, "    ");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for RestrictionStructuredDataPropertyContext cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_28 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_29 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "RestrictionStructuredDataPropertyContext"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_29));
        };
        Iterable<EClass> _filter_10 = IterableExtensions.<EClass>filter(eClasses, _function_28);
        boolean _hasElements_11 = false;
        for(final EClass eClass_16 : _filter_10) {
          if (!_hasElements_11) {
            _hasElements_11 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(restrictionStructuredDataPropertyContexts, ");
          String _tableVariableName_15 = OMLUtilities.tableVariableName(eClass_16);
          _builder.append(_tableVariableName_15, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for TerminologyBox cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_29 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_30 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "TerminologyBox"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_30));
        };
        Iterable<EClass> _filter_11 = IterableExtensions.<EClass>filter(eClasses, _function_29);
        boolean _hasElements_12 = false;
        for(final EClass eClass_17 : _filter_11) {
          if (!_hasElements_12) {
            _hasElements_12 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(terminologyBoxes, ");
          String _tableVariableName_16 = OMLUtilities.tableVariableName(eClass_17);
          _builder.append(_tableVariableName_16, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for ConceptTreeDisjunction cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_30 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_31 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "ConceptTreeDisjunction"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_31));
        };
        Iterable<EClass> _filter_12 = IterableExtensions.<EClass>filter(eClasses, _function_30);
        boolean _hasElements_13 = false;
        for(final EClass eClass_18 : _filter_12) {
          if (!_hasElements_13) {
            _hasElements_13 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(conceptTreeDisjunctions, ");
          String _tableVariableName_17 = OMLUtilities.tableVariableName(eClass_18);
          _builder.append(_tableVariableName_17, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for ConceptualEntitySingletonInstance cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_31 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_32 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "ConceptualEntitySingletonInstance"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_32));
        };
        Iterable<EClass> _filter_13 = IterableExtensions.<EClass>filter(eClasses, _function_31);
        boolean _hasElements_14 = false;
        for(final EClass eClass_19 : _filter_13) {
          if (!_hasElements_14) {
            _hasElements_14 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(conceptualEntitySingletonInstances, ");
          String _tableVariableName_18 = OMLUtilities.tableVariableName(eClass_19);
          _builder.append(_tableVariableName_18, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for SingletonInstanceStructuredDataPropertyContext cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_32 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_33 = (EClass it_1) -> {
            String _name_11 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_11, "SingletonInstanceStructuredDataPropertyContext"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_33));
        };
        Iterable<EClass> _filter_14 = IterableExtensions.<EClass>filter(eClasses, _function_32);
        boolean _hasElements_15 = false;
        for(final EClass eClass_20 : _filter_14) {
          if (!_hasElements_15) {
            _hasElements_15 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(singletonInstanceStructuredDataPropertyContexts, ");
          String _tableVariableName_19 = OMLUtilities.tableVariableName(eClass_20);
          _builder.append(_tableVariableName_19, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_33 = (EClass it) -> {
          int _size = IterableExtensions.size(OMLUtilities.schemaAPIOrOrderingKeyReferences(it));
          return Boolean.valueOf((_size > 0));
        };
        Iterable<EClass> _filter_15 = IterableExtensions.<EClass>filter(eClasses, _function_33);
        for(final EClass eClass_21 : _filter_15) {
          _builder.append("    ");
          _builder.append("resolve");
          String _upperCaseInitialOrWord_4 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_21));
          _builder.append(_upperCaseInitialOrWord_4, "    ");
          _builder.append("(rs)");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_34 = (EClass it) -> {
          int _size = IterableExtensions.size(OMLUtilities.schemaAPIOrOrderingKeyReferences(it));
          return Boolean.valueOf((_size > 0));
        };
        Iterable<EClass> _filter_16 = IterableExtensions.<EClass>filter(eClasses, _function_34);
        for(final EClass eClass_22 : _filter_16) {
          _builder.append("  ");
          _builder.append("protected def void resolve");
          String _upperCaseInitialOrWord_5 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_22));
          _builder.append(_upperCaseInitialOrWord_5, "  ");
          _builder.append("(ResourceSet rs) {");
          _builder.newLineIfNotEmpty();
          {
            final Function1<EClass, Boolean> _function_35 = (EClass it) -> {
              String _name_11 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_11, "ModuleEdge"));
            };
            boolean _exists_16 = IterableExtensions.<EClass>exists(eClass_22.getEAllSuperTypes(), _function_35);
            if (_exists_16) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("var more = false");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("do {");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("\t");
              _builder.append("val queue = new HashMap<String, Pair<");
              String _name_11 = eClass_22.getName();
              _builder.append(_name_11, "  \t\t");
              _builder.append(", Map<String, String>>>()");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("\t");
              String _tableVariableName_20 = OMLUtilities.tableVariableName(eClass_22);
              _builder.append(_tableVariableName_20, "  \t\t");
              _builder.append(".filter[uuid, oml_kv|!oml_kv.value.empty].forEach[uuid, oml_kv|queue.put(uuid, oml_kv)]");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("\t");
              _builder.append("more = !queue.empty");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("\t");
              _builder.append("if (more) {");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("\t\t");
              _builder.append("queue.forEach[uuid, oml_kv |");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  \t\t\t");
              _builder.append("val ");
              String _name_12 = eClass_22.getName();
              _builder.append(_name_12, "  \t  \t\t\t");
              _builder.append(" oml = oml_kv.key");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  \t\t\t");
              _builder.append("val Map<String, String> kv = oml_kv.value");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  \t\t\t");
              _builder.append("if (!kv.empty) {");
              _builder.newLine();
              {
                Iterable<ETypedElement> _schemaAPIOrOrderingKeyReferences = OMLUtilities.schemaAPIOrOrderingKeyReferences(eClass_22);
                for(final ETypedElement attr_2 : _schemaAPIOrOrderingKeyReferences) {
                  {
                    Boolean _isIRIReference_1 = OMLUtilities.isIRIReference(attr_2);
                    if ((_isIRIReference_1).booleanValue()) {
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t\t");
                      _builder.append("val String ");
                      String _name_13 = attr_2.getName();
                      _builder.append(_name_13, "  \t    \t\t\t\t");
                      _builder.append("IRI = kv.remove(\"");
                      String _columnName_3 = OMLUtilities.columnName(attr_2);
                      _builder.append(_columnName_3, "  \t    \t\t\t\t");
                      _builder.append("\")");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t\t");
                      _builder.append("loadOMLZipResource(rs, URI.createURI(");
                      String _name_14 = attr_2.getName();
                      _builder.append(_name_14, "  \t    \t\t\t\t");
                      _builder.append("IRI))");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t\t");
                      _builder.append("val Pair<");
                      String _name_15 = OMLUtilities.EClassType(attr_2).getName();
                      _builder.append(_name_15, "  \t    \t\t\t\t");
                      _builder.append(", Map<String, String>> ");
                      String _name_16 = attr_2.getName();
                      _builder.append(_name_16, "  \t    \t\t\t\t");
                      _builder.append("Pair = ");
                      String _tableVariableName_21 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                      _builder.append(_tableVariableName_21, "  \t    \t\t\t\t");
                      _builder.append(".get(");
                      String _name_17 = attr_2.getName();
                      _builder.append(_name_17, "  \t    \t\t\t\t");
                      _builder.append("IRI)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t\t");
                      _builder.append("if (null === ");
                      String _name_18 = attr_2.getName();
                      _builder.append(_name_18, "  \t    \t\t\t\t");
                      _builder.append("Pair)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t\t");
                      _builder.append("\t");
                      _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                      String _name_19 = attr_2.getName();
                      _builder.append(_name_19, "  \t    \t\t\t\t\t");
                      _builder.append(" in ");
                      String _tableVariableName_22 = OMLUtilities.tableVariableName(eClass_22);
                      _builder.append(_tableVariableName_22, "  \t    \t\t\t\t\t");
                      _builder.append(": \"+");
                      String _name_20 = attr_2.getName();
                      _builder.append(_name_20, "  \t    \t\t\t\t\t");
                      _builder.append("IRI)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t\t");
                      _builder.append("oml.");
                      String _name_21 = attr_2.getName();
                      _builder.append(_name_21, "  \t    \t\t\t\t");
                      _builder.append(" = ");
                      String _name_22 = attr_2.getName();
                      _builder.append(_name_22, "  \t    \t\t\t\t");
                      _builder.append("Pair.key\t\t  \t  ");
                      _builder.newLineIfNotEmpty();
                    } else {
                      int _lowerBound = attr_2.getLowerBound();
                      boolean _equals_1 = (_lowerBound == 0);
                      if (_equals_1) {
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("val String ");
                        String _name_23 = attr_2.getName();
                        _builder.append(_name_23, "  \t    \t\t\t\t");
                        _builder.append("XRef = kv.remove(\"");
                        String _columnName_4 = OMLUtilities.columnName(attr_2);
                        _builder.append(_columnName_4, "  \t    \t\t\t\t");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("if (\"null\" != ");
                        String _name_24 = attr_2.getName();
                        _builder.append(_name_24, "  \t    \t\t\t\t");
                        _builder.append("XRef) {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("\t");
                        _builder.append("val Pair<");
                        String _name_25 = OMLUtilities.EClassType(attr_2).getName();
                        _builder.append(_name_25, "  \t    \t\t\t\t\t");
                        _builder.append(", Map<String, String>> ");
                        String _name_26 = attr_2.getName();
                        _builder.append(_name_26, "  \t    \t\t\t\t\t");
                        _builder.append("Pair = ");
                        String _tableVariableName_23 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                        _builder.append(_tableVariableName_23, "  \t    \t\t\t\t\t");
                        _builder.append(".get(");
                        String _name_27 = attr_2.getName();
                        _builder.append(_name_27, "  \t    \t\t\t\t\t");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("\t");
                        _builder.append("if (null === ");
                        String _name_28 = attr_2.getName();
                        _builder.append(_name_28, "  \t    \t\t\t\t\t");
                        _builder.append("Pair)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("\t\t");
                        _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                        String _name_29 = attr_2.getName();
                        _builder.append(_name_29, "  \t    \t\t\t\t\t\t");
                        _builder.append(" in ");
                        String _tableVariableName_24 = OMLUtilities.tableVariableName(eClass_22);
                        _builder.append(_tableVariableName_24, "  \t    \t\t\t\t\t\t");
                        _builder.append(": \"+");
                        String _name_30 = attr_2.getName();
                        _builder.append(_name_30, "  \t    \t\t\t\t\t\t");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("\t");
                        _builder.append("oml.");
                        String _name_31 = attr_2.getName();
                        _builder.append(_name_31, "  \t    \t\t\t\t\t");
                        _builder.append(" = ");
                        String _name_32 = attr_2.getName();
                        _builder.append(_name_32, "  \t    \t\t\t\t\t");
                        _builder.append("Pair.key");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("}");
                        _builder.newLine();
                      } else {
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("val String ");
                        String _name_33 = attr_2.getName();
                        _builder.append(_name_33, "  \t    \t\t\t\t");
                        _builder.append("XRef = kv.remove(\"");
                        String _columnName_5 = OMLUtilities.columnName(attr_2);
                        _builder.append(_columnName_5, "  \t    \t\t\t\t");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("val Pair<");
                        String _name_34 = OMLUtilities.EClassType(attr_2).getName();
                        _builder.append(_name_34, "  \t    \t\t\t\t");
                        _builder.append(", Map<String, String>> ");
                        String _name_35 = attr_2.getName();
                        _builder.append(_name_35, "  \t    \t\t\t\t");
                        _builder.append("Pair = ");
                        String _tableVariableName_25 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                        _builder.append(_tableVariableName_25, "  \t    \t\t\t\t");
                        _builder.append(".get(");
                        String _name_36 = attr_2.getName();
                        _builder.append(_name_36, "  \t    \t\t\t\t");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("if (null === ");
                        String _name_37 = attr_2.getName();
                        _builder.append(_name_37, "  \t    \t\t\t\t");
                        _builder.append("Pair)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("\t");
                        _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                        String _name_38 = attr_2.getName();
                        _builder.append(_name_38, "  \t    \t\t\t\t\t");
                        _builder.append(" in ");
                        String _tableVariableName_26 = OMLUtilities.tableVariableName(eClass_22);
                        _builder.append(_tableVariableName_26, "  \t    \t\t\t\t\t");
                        _builder.append(": \"+");
                        String _name_39 = attr_2.getName();
                        _builder.append(_name_39, "  \t    \t\t\t\t\t");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t\t");
                        _builder.append("oml.");
                        String _name_40 = attr_2.getName();
                        _builder.append(_name_40, "  \t    \t\t\t\t");
                        _builder.append(" = ");
                        String _name_41 = attr_2.getName();
                        _builder.append(_name_41, "  \t    \t\t\t\t");
                        _builder.append("Pair.key");
                        _builder.newLineIfNotEmpty();
                      }
                    }
                  }
                }
              }
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  \t\t\t");
              _builder.append("}");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("\t\t");
              _builder.append("]");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("\t");
              _builder.append("}");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("} while (more)");
              _builder.newLine();
            } else {
              _builder.append("  ");
              _builder.append("\t");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              String _tableVariableName_27 = OMLUtilities.tableVariableName(eClass_22);
              _builder.append(_tableVariableName_27, "  \t");
              _builder.append(".forEach[uuid, oml_kv |");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  ");
              _builder.append("val ");
              String _name_42 = eClass_22.getName();
              _builder.append(_name_42, "  \t  ");
              _builder.append(" oml = oml_kv.key");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  ");
              _builder.append("val Map<String, String> kv = oml_kv.value");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  ");
              _builder.append("if (!kv.empty) {");
              _builder.newLine();
              {
                Iterable<ETypedElement> _schemaAPIOrOrderingKeyReferences_1 = OMLUtilities.schemaAPIOrOrderingKeyReferences(eClass_22);
                for(final ETypedElement attr_3 : _schemaAPIOrOrderingKeyReferences_1) {
                  {
                    Boolean _isIRIReference_2 = OMLUtilities.isIRIReference(attr_3);
                    if ((_isIRIReference_2).booleanValue()) {
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("val String ");
                      String _name_43 = attr_3.getName();
                      _builder.append(_name_43, "  \t    ");
                      _builder.append("IRI = kv.remove(\"");
                      String _columnName_6 = OMLUtilities.columnName(attr_3);
                      _builder.append(_columnName_6, "  \t    ");
                      _builder.append("\")");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("loadOMLZipResource(rs, URI.createURI(");
                      String _name_44 = attr_3.getName();
                      _builder.append(_name_44, "  \t    ");
                      _builder.append("IRI))");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("val Pair<");
                      String _name_45 = OMLUtilities.EClassType(attr_3).getName();
                      _builder.append(_name_45, "  \t    ");
                      _builder.append(", Map<String, String>> ");
                      String _name_46 = attr_3.getName();
                      _builder.append(_name_46, "  \t    ");
                      _builder.append("Pair = ");
                      String _tableVariableName_28 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_3));
                      _builder.append(_tableVariableName_28, "  \t    ");
                      _builder.append(".get(");
                      String _name_47 = attr_3.getName();
                      _builder.append(_name_47, "  \t    ");
                      _builder.append("IRI)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("if (null === ");
                      String _name_48 = attr_3.getName();
                      _builder.append(_name_48, "  \t    ");
                      _builder.append("Pair)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("  ");
                      _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                      String _name_49 = attr_3.getName();
                      _builder.append(_name_49, "  \t      ");
                      _builder.append(" in ");
                      String _tableVariableName_29 = OMLUtilities.tableVariableName(eClass_22);
                      _builder.append(_tableVariableName_29, "  \t      ");
                      _builder.append(": \"+");
                      String _name_50 = attr_3.getName();
                      _builder.append(_name_50, "  \t      ");
                      _builder.append("IRI)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("oml.");
                      String _name_51 = attr_3.getName();
                      _builder.append(_name_51, "  \t    ");
                      _builder.append(" = ");
                      String _name_52 = attr_3.getName();
                      _builder.append(_name_52, "  \t    ");
                      _builder.append("Pair.key\t\t  \t  ");
                      _builder.newLineIfNotEmpty();
                    } else {
                      int _lowerBound_1 = attr_3.getLowerBound();
                      boolean _equals_2 = (_lowerBound_1 == 0);
                      if (_equals_2) {
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("val String ");
                        String _name_53 = attr_3.getName();
                        _builder.append(_name_53, "  \t    ");
                        _builder.append("XRef = kv.remove(\"");
                        String _columnName_7 = OMLUtilities.columnName(attr_3);
                        _builder.append(_columnName_7, "  \t    ");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("if (\"null\" != ");
                        String _name_54 = attr_3.getName();
                        _builder.append(_name_54, "  \t    ");
                        _builder.append("XRef) {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("val Pair<");
                        String _name_55 = OMLUtilities.EClassType(attr_3).getName();
                        _builder.append(_name_55, "  \t      ");
                        _builder.append(", Map<String, String>> ");
                        String _name_56 = attr_3.getName();
                        _builder.append(_name_56, "  \t      ");
                        _builder.append("Pair = ");
                        String _tableVariableName_30 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_3));
                        _builder.append(_tableVariableName_30, "  \t      ");
                        _builder.append(".get(");
                        String _name_57 = attr_3.getName();
                        _builder.append(_name_57, "  \t      ");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("if (null === ");
                        String _name_58 = attr_3.getName();
                        _builder.append(_name_58, "  \t      ");
                        _builder.append("Pair)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("    ");
                        _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                        String _name_59 = attr_3.getName();
                        _builder.append(_name_59, "  \t        ");
                        _builder.append(" in ");
                        String _tableVariableName_31 = OMLUtilities.tableVariableName(eClass_22);
                        _builder.append(_tableVariableName_31, "  \t        ");
                        _builder.append(": \"+");
                        String _name_60 = attr_3.getName();
                        _builder.append(_name_60, "  \t        ");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("oml.");
                        String _name_61 = attr_3.getName();
                        _builder.append(_name_61, "  \t      ");
                        _builder.append(" = ");
                        String _name_62 = attr_3.getName();
                        _builder.append(_name_62, "  \t      ");
                        _builder.append("Pair.key");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("}");
                        _builder.newLine();
                      } else {
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("val String ");
                        String _name_63 = attr_3.getName();
                        _builder.append(_name_63, "  \t    ");
                        _builder.append("XRef = kv.remove(\"");
                        String _columnName_8 = OMLUtilities.columnName(attr_3);
                        _builder.append(_columnName_8, "  \t    ");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("val Pair<");
                        String _name_64 = OMLUtilities.EClassType(attr_3).getName();
                        _builder.append(_name_64, "  \t    ");
                        _builder.append(", Map<String, String>> ");
                        String _name_65 = attr_3.getName();
                        _builder.append(_name_65, "  \t    ");
                        _builder.append("Pair = ");
                        String _tableVariableName_32 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_3));
                        _builder.append(_tableVariableName_32, "  \t    ");
                        _builder.append(".get(");
                        String _name_66 = attr_3.getName();
                        _builder.append(_name_66, "  \t    ");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("if (null === ");
                        String _name_67 = attr_3.getName();
                        _builder.append(_name_67, "  \t    ");
                        _builder.append("Pair)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                        String _name_68 = attr_3.getName();
                        _builder.append(_name_68, "  \t      ");
                        _builder.append(" in ");
                        String _tableVariableName_33 = OMLUtilities.tableVariableName(eClass_22);
                        _builder.append(_tableVariableName_33, "  \t      ");
                        _builder.append(": \"+");
                        String _name_69 = attr_3.getName();
                        _builder.append(_name_69, "  \t      ");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("oml.");
                        String _name_70 = attr_3.getName();
                        _builder.append(_name_70, "  \t    ");
                        _builder.append(" = ");
                        String _name_71 = attr_3.getName();
                        _builder.append(_name_71, "  \t    ");
                        _builder.append("Pair.key");
                        _builder.newLineIfNotEmpty();
                      }
                    }
                  }
                }
              }
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  ");
              _builder.append("}");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("]");
              _builder.newLine();
            }
          }
          _builder.append("  ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("  ");
          _builder.newLine();
        }
      }
      _builder.append("  ");
      _builder.append("protected def Resource loadOMLZipResource(ResourceSet rs, URI uri) {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("val omlCatalog = OMLExtensions.getCatalog(rs)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("if (null === omlCatalog)");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("throw new IllegalArgumentException(\"loadOMLZipResource: ResourceSet must have an OMLCatalog!\")");
      _builder.newLine();
      _builder.newLine();
      _builder.append("\t");
      _builder.append("var scan = false");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("val uriString = uri.toString");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("val Resource r = if (uriString.startsWith(\"file:\")) {");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("scan = true");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("rs.getResource(uri, true)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("} else if (uriString.startsWith(\"http:\")) {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("val r0a = rs.getResource(uri, false)");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("val r0b = rs.resources.findFirst[r| r.contents.exists[e|");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("switch e {");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("Extent:");
      _builder.newLine();
      _builder.append("\t\t\t\t\t");
      _builder.append("e.modules.exists[m|m.iri() == uriString]");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("default:");
      _builder.newLine();
      _builder.append("\t\t\t\t\t");
      _builder.append("false");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("]]");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("if (null !== r0a)");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("r0a");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("else if (null !== r0b)");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("r0b");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("else {");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("val r1 = omlCatalog.resolveURI(uriString + \".oml\")");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("val r2 = omlCatalog.resolveURI(uriString + \".omlzip\")");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("val r3 = omlCatalog.resolveURI(uriString)");
      _builder.newLine();
      _builder.append("\t  \t\t\t\t  \t\t");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("val f1 = if (null !== r1 && r1.startsWith(\"file:\")) new File(r1.substring(5)) else null");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("val f2 = if (null !== r2 && r2.startsWith(\"file:\")) new File(r2.substring(5)) else null");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("val f3 = if (null !== r3 && r3.startsWith(\"file:\")) new File(r3.substring(5)) else null");
      _builder.newLine();
      _builder.append("\t  \t");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("scan = true");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("if (null !== f1 && f1.exists && f1.canRead)");
      _builder.newLine();
      _builder.append("\t  \t\t\t");
      _builder.append("rs.getResource(URI.createURI(r1), true)");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("else if (null !== f2 && f2.exists && f2.canRead)");
      _builder.newLine();
      _builder.append("\t  \t\t\t");
      _builder.append("rs.getResource(URI.createURI(r2), true)");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("else if (null !== f3 && f3.exists && f3.canRead)");
      _builder.newLine();
      _builder.append("\t  \t\t\t");
      _builder.append("rs.getResource(URI.createURI(r3), true)");
      _builder.newLine();
      _builder.append("\t  \t\t");
      _builder.append("else");
      _builder.newLine();
      _builder.append("\t  \t\t\t");
      _builder.append("throw new IllegalArgumentException(\"loadOMLZipResource: \"+uri+\" not resolved!\")");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("if (scan)");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("r.contents.forEach[e|");
      _builder.newLine();
      _builder.append("  \t\t\t");
      _builder.append("switch e {");
      _builder.newLine();
      _builder.append("  \t\t\t\t");
      _builder.append("Extent: {");
      _builder.newLine();
      _builder.append("  \t\t\t\t\t");
      _builder.append("e.modules.forEach[includeModule]");
      _builder.newLine();
      _builder.append("  \t\t\t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t\t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("]");
      _builder.newLine();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("r");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def void includeModule(Module m) {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("if (null !== m) {");
      _builder.newLine();
      _builder.append("    \t  ");
      _builder.append("switch m {");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("TerminologyGraph: {");
      _builder.newLine();
      _builder.append("    \t  \t  ");
      _builder.append("logicalElements.put(m.uuid(), new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyGraphs.put(m.uuid(), new Pair<TerminologyGraph, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyBoxes.put(m.uuid(), new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyBoxes.put(m.iri(), new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("Bundle: {");
      _builder.newLine();
      _builder.append("    \t  \t  ");
      _builder.append("logicalElements.put(m.uuid(), new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("bundles.put(m.uuid(), new Pair<Bundle, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyBoxes.put(m.uuid(), new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyBoxes.put(m.iri(), new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("DescriptionBox: {");
      _builder.newLine();
      _builder.append("    \t  \t  ");
      _builder.append("logicalElements.put(m.uuid(), new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("descriptionBoxes.put(m.uuid(), new Pair<DescriptionBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("descriptionBoxes.put(m.iri(), new Pair<DescriptionBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    \t  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("modules.put(m.uuid(), new Pair<Module, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("m.eAllContents.forEach[e|");
      _builder.newLine();
      _builder.append("  \t    ");
      _builder.append("switch e {");
      _builder.newLine();
      {
        for(final EClass eClass_23 : eClassesExceptModules) {
          _builder.append("  \t      ");
          String _name_72 = eClass_23.getName();
          _builder.append(_name_72, "  \t      ");
          _builder.append(": {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t      ");
          _builder.append("  ");
          _builder.append("val pair = new Pair<");
          String _name_73 = eClass_23.getName();
          _builder.append(_name_73, "  \t        ");
          _builder.append(", Map<String,String>>(e, Collections.emptyMap)");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t      ");
          _builder.append("  ");
          String _tableVariableName_34 = OMLUtilities.tableVariableName(eClass_23);
          _builder.append(_tableVariableName_34, "  \t        ");
          _builder.append(".put(e.uuid(), pair)");
          _builder.newLineIfNotEmpty();
          {
            final Function1<EClass, Boolean> _function_36 = (EClass it) -> {
              String _name_74 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_74, "LogicalElement"));
            };
            boolean _exists_17 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_36);
            if (_exists_17) {
              _builder.append("  \t      ");
              _builder.append("  ");
              _builder.append("logicalElements.put(e.uuid(), new Pair<LogicalElement, Map<String,String>>(e, Collections.emptyMap))");
              _builder.newLine();
            } else {
              final Function1<EClass, Boolean> _function_37 = (EClass it) -> {
                String _name_74 = it.getName();
                return Boolean.valueOf(Objects.equal(_name_74, "Entity"));
              };
              boolean _exists_18 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_37);
              if (_exists_18) {
                _builder.append("  \t      ");
                _builder.append("  ");
                _builder.append("entities.put(e.uuid(), new Pair<Entity, Map<String,String>>(e, Collections.emptyMap))");
                _builder.newLine();
              } else {
                final Function1<EClass, Boolean> _function_38 = (EClass it) -> {
                  String _name_74 = it.getName();
                  return Boolean.valueOf(Objects.equal(_name_74, "EntityRelationship"));
                };
                boolean _exists_19 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_38);
                if (_exists_19) {
                  _builder.append("  \t      ");
                  _builder.append("  ");
                  _builder.append("entityRelationships.put(e.uuid(), new Pair<EntityRelationship, Map<String,String>>(e, Collections.emptyMap))");
                  _builder.newLine();
                } else {
                  final Function1<EClass, Boolean> _function_39 = (EClass it) -> {
                    String _name_74 = it.getName();
                    return Boolean.valueOf(Objects.equal(_name_74, "ConceptualRelationship"));
                  };
                  boolean _exists_20 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_39);
                  if (_exists_20) {
                    _builder.append("  \t      ");
                    _builder.append("  ");
                    _builder.append("conceptualRelationships.put(e.uuid(), new Pair<ConceptualRelationship, Map<String,String>>(e, Collections.emptyMap))");
                    _builder.newLine();
                  } else {
                    final Function1<EClass, Boolean> _function_40 = (EClass it) -> {
                      String _name_74 = it.getName();
                      return Boolean.valueOf(Objects.equal(_name_74, "DataRange"));
                    };
                    boolean _exists_21 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_40);
                    if (_exists_21) {
                      _builder.append("  \t      ");
                      _builder.append("  ");
                      _builder.append("dataRanges.put(e.uuid(), new Pair<DataRange, Map<String,String>>(e, Collections.emptyMap))");
                      _builder.newLine();
                    } else {
                      final Function1<EClass, Boolean> _function_41 = (EClass it) -> {
                        String _name_74 = it.getName();
                        return Boolean.valueOf(Objects.equal(_name_74, "DataRelationshipToScalar"));
                      };
                      boolean _exists_22 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_41);
                      if (_exists_22) {
                        _builder.append("  \t      ");
                        _builder.append("  ");
                        _builder.append("dataRelationshipToScalars.put(e.uuid(), new Pair<DataRelationshipToScalar, Map<String,String>>(e, Collections.emptyMap))");
                        _builder.newLine();
                      } else {
                        final Function1<EClass, Boolean> _function_42 = (EClass it) -> {
                          String _name_74 = it.getName();
                          return Boolean.valueOf(Objects.equal(_name_74, "DataRelationshipToStructure"));
                        };
                        boolean _exists_23 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_42);
                        if (_exists_23) {
                          _builder.append("  \t      ");
                          _builder.append("  ");
                          _builder.append("dataRelationshipToStructures.put(e.uuid(), new PairDataRelationshipToStructure, Map<String,String>>(e, Collections.emptyMap))");
                          _builder.newLine();
                        } else {
                          final Function1<EClass, Boolean> _function_43 = (EClass it) -> {
                            String _name_74 = it.getName();
                            return Boolean.valueOf(Objects.equal(_name_74, "Predicate"));
                          };
                          boolean _exists_24 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_43);
                          if (_exists_24) {
                            _builder.append("  \t      ");
                            _builder.append("  ");
                            _builder.append("predicates.put(e.uuid(), new Pair<Predicate, Map<String,String>>(e, Collections.emptyMap))");
                            _builder.newLine();
                          } else {
                            final Function1<EClass, Boolean> _function_44 = (EClass it) -> {
                              String _name_74 = it.getName();
                              return Boolean.valueOf(Objects.equal(_name_74, "RestrictableRelationship"));
                            };
                            boolean _exists_25 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_44);
                            if (_exists_25) {
                              _builder.append("  \t      ");
                              _builder.append("  ");
                              _builder.append("restrictableRelationships.put(e.uuid(), new Pair<RestrictableRelationship, Map<String,String>>(e, Collections.emptyMap))");
                              _builder.newLine();
                            } else {
                              final Function1<EClass, Boolean> _function_45 = (EClass it) -> {
                                String _name_74 = it.getName();
                                return Boolean.valueOf(Objects.equal(_name_74, "RestrictionStructuredDataPropertyContext"));
                              };
                              boolean _exists_26 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_45);
                              if (_exists_26) {
                                _builder.append("  \t      ");
                                _builder.append("  ");
                                _builder.append("restrictionStructuredDataPropertyContexts.put(e.uuid(), new Pair<RestrictionStructuredDataPropertyContext, Map<String,String>>(e, Collections.emptyMap))");
                                _builder.newLine();
                              } else {
                                final Function1<EClass, Boolean> _function_46 = (EClass it) -> {
                                  String _name_74 = it.getName();
                                  return Boolean.valueOf(Objects.equal(_name_74, "TerminologyBox"));
                                };
                                boolean _exists_27 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_46);
                                if (_exists_27) {
                                  _builder.append("  \t      ");
                                  _builder.append("  ");
                                  _builder.append("terminologyBoxes.put(e.uuid(), new Pair<TerminologyBox, Map<String,String>>(e, Collections.emptyMap))");
                                  _builder.newLine();
                                } else {
                                  final Function1<EClass, Boolean> _function_47 = (EClass it) -> {
                                    String _name_74 = it.getName();
                                    return Boolean.valueOf(Objects.equal(_name_74, "ConceptTreeDisjunction"));
                                  };
                                  boolean _exists_28 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_47);
                                  if (_exists_28) {
                                    _builder.append("  \t      ");
                                    _builder.append("  ");
                                    _builder.append("conceptTreeDisjunctions.put(e.uuid(), new Pair<ConceptTreeDisjunction, Map<String,String>>(e, Collections.emptyMap))");
                                    _builder.newLine();
                                  } else {
                                    final Function1<EClass, Boolean> _function_48 = (EClass it) -> {
                                      String _name_74 = it.getName();
                                      return Boolean.valueOf(Objects.equal(_name_74, "ConceptualEntitySingletonInstance"));
                                    };
                                    boolean _exists_29 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_48);
                                    if (_exists_29) {
                                      _builder.append("  \t      ");
                                      _builder.append("  ");
                                      _builder.append("conceptualEntitySingletonInstances.put(e.uuid(), new Pair<ConceptualEntitySingletonInstance, Map<String,String>>(e, Collections.emptyMap))");
                                      _builder.newLine();
                                    } else {
                                      final Function1<EClass, Boolean> _function_49 = (EClass it) -> {
                                        String _name_74 = it.getName();
                                        return Boolean.valueOf(Objects.equal(_name_74, "SingletonInstanceStructuredDataPropertyContext"));
                                      };
                                      boolean _exists_30 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_49);
                                      if (_exists_30) {
                                        _builder.append("  \t      ");
                                        _builder.append("  ");
                                        _builder.append("singletonInstanceStructuredDataPropertyContexts.put(e.uuid(), new Pair<SingletonInstanceStructuredDataPropertyContext, Map<String,String>>(e, Collections.emptyMap))");
                                        _builder.newLine();
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
                  }
                }
              }
            }
          }
          {
            final Function1<EClass, Boolean> _function_50 = (EClass it) -> {
              String _name_74 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_74, "ModuleEdge"));
            };
            boolean _exists_31 = IterableExtensions.<EClass>exists(eClass_23.getEAllSuperTypes(), _function_50);
            if (_exists_31) {
              _builder.append("  \t      ");
              _builder.append("  ");
              _builder.append("//includeModule(e.targetModule)");
              _builder.newLine();
            }
          }
          _builder.append("  \t      ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.append("  \t    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("]");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
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
}
