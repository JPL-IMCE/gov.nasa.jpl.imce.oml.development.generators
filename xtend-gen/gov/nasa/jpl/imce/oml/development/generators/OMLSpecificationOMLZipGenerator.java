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
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipSpecializationAxiom");
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
                  Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(attr);
                  if ((_isLiteralFeature).booleanValue()) {
                    _builder.append("  ");
                    _builder.append("    ");
                    _builder.append("pw.print(OMLTables.toString(it.");
                    String _featureQuery_1 = OMLUtilities.getFeatureQuery(attr);
                    _builder.append(_featureQuery_1, "      ");
                    _builder.append("))");
                    _builder.newLineIfNotEmpty();
                  } else {
                    if (((OMLUtilities.isClassFeature(attr)).booleanValue() && (attr.getLowerBound() == 0))) {
                      _builder.append("  ");
                      _builder.append("    ");
                      _builder.append("if (null !== ");
                      String _featureQuery_2 = OMLUtilities.getFeatureQuery(attr);
                      _builder.append(_featureQuery_2, "      ");
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
                      String _featureQuery_3 = OMLUtilities.getFeatureQuery(attr);
                      _builder.append(_featureQuery_3, "        ");
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
                        String _featureQuery_4 = OMLUtilities.getFeatureQuery(attr);
                        _builder.append(_featureQuery_4, "      ");
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
                          String _featureQuery_5 = OMLUtilities.getFeatureQuery(attr);
                          _builder.append(_featureQuery_5, "      ");
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
                          String _featureQuery_6 = OMLUtilities.getFeatureQuery(attr);
                          _builder.append(_featureQuery_6, "      ");
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
      _builder.append("static def void load(ResourceSet rs, OMLZipResource r, File omlZipFile) {");
      _builder.newLine();
      _builder.newLine();
      _builder.append("    ");
      _builder.append("val tables = new ");
      _builder.append(tableName, "    ");
      _builder.append("()");
      _builder.newLineIfNotEmpty();
      _builder.append("    ");
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
          _builder.append("(lines)");
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
          _builder.append("(ArrayList<String> lines) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("val kvs = OMLZipResource.lines2tuples(lines)");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("kvs.forEach[kv|");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("val oml = create");
          String _name_3 = eClass_5.getName();
          _builder.append(_name_3, "  \t  ");
          _builder.append("()");
          _builder.newLineIfNotEmpty();
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
          _builder.append("\t");
          _builder.append("]");
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
        final Function1<EClass, Boolean> _function_2 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_3 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "LogicalElement"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_3));
        };
        Iterable<EClass> _filter_1 = IterableExtensions.<EClass>filter(eClasses, _function_2);
        boolean _hasElements_2 = false;
        for(final EClass eClass_6 : _filter_1) {
          if (!_hasElements_2) {
            _hasElements_2 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(logicalElements, ");
          String _tableVariableName_6 = OMLUtilities.tableVariableName(eClass_6);
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
        final Function1<EClass, Boolean> _function_3 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_4 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "Entity"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_4));
        };
        Iterable<EClass> _filter_2 = IterableExtensions.<EClass>filter(eClasses, _function_3);
        boolean _hasElements_3 = false;
        for(final EClass eClass_7 : _filter_2) {
          if (!_hasElements_3) {
            _hasElements_3 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(entities, ");
          String _tableVariableName_7 = OMLUtilities.tableVariableName(eClass_7);
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
        final Function1<EClass, Boolean> _function_4 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_5 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "EntityRelationship"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_5));
        };
        Iterable<EClass> _filter_3 = IterableExtensions.<EClass>filter(eClasses, _function_4);
        boolean _hasElements_4 = false;
        for(final EClass eClass_8 : _filter_3) {
          if (!_hasElements_4) {
            _hasElements_4 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(entityRelationships, ");
          String _tableVariableName_8 = OMLUtilities.tableVariableName(eClass_8);
          _builder.append(_tableVariableName_8, "    ");
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
        final Function1<EClass, Boolean> _function_5 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_6 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "DataRange"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_6));
        };
        Iterable<EClass> _filter_4 = IterableExtensions.<EClass>filter(eClasses, _function_5);
        boolean _hasElements_5 = false;
        for(final EClass eClass_9 : _filter_4) {
          if (!_hasElements_5) {
            _hasElements_5 = true;
          } else {
            _builder.appendImmediate("\n", "    ");
          }
          _builder.append("includeMap(dataRanges, ");
          String _tableVariableName_9 = OMLUtilities.tableVariableName(eClass_9);
          _builder.append(_tableVariableName_9, "    ");
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
        final Function1<EClass, Boolean> _function_6 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_7 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "DataRelationshipToScalar"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_7));
        };
        Iterable<EClass> _filter_5 = IterableExtensions.<EClass>filter(eClasses, _function_6);
        boolean _hasElements_6 = false;
        for(final EClass eClass_10 : _filter_5) {
          if (!_hasElements_6) {
            _hasElements_6 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(dataRelationshipToScalars, ");
          String _tableVariableName_10 = OMLUtilities.tableVariableName(eClass_10);
          _builder.append(_tableVariableName_10, "  \t");
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
        final Function1<EClass, Boolean> _function_7 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_8 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "DataRelationshipToStructure"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_8));
        };
        Iterable<EClass> _filter_6 = IterableExtensions.<EClass>filter(eClasses, _function_7);
        boolean _hasElements_7 = false;
        for(final EClass eClass_11 : _filter_6) {
          if (!_hasElements_7) {
            _hasElements_7 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(dataRelationshipToStructures, ");
          String _tableVariableName_11 = OMLUtilities.tableVariableName(eClass_11);
          _builder.append(_tableVariableName_11, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for RestrictionStructuredDataPropertyContext cross references");
      _builder.newLine();
      _builder.append("  \t");
      {
        final Function1<EClass, Boolean> _function_8 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_9 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "RestrictionStructuredDataPropertyContext"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_9));
        };
        Iterable<EClass> _filter_7 = IterableExtensions.<EClass>filter(eClasses, _function_8);
        boolean _hasElements_8 = false;
        for(final EClass eClass_12 : _filter_7) {
          if (!_hasElements_8) {
            _hasElements_8 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(restrictionStructuredDataPropertyContexts, ");
          String _tableVariableName_12 = OMLUtilities.tableVariableName(eClass_12);
          _builder.append(_tableVariableName_12, "  \t");
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
        final Function1<EClass, Boolean> _function_9 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_10 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "TerminologyBox"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_10));
        };
        Iterable<EClass> _filter_8 = IterableExtensions.<EClass>filter(eClasses, _function_9);
        boolean _hasElements_9 = false;
        for(final EClass eClass_13 : _filter_8) {
          if (!_hasElements_9) {
            _hasElements_9 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(terminologyBoxes, ");
          String _tableVariableName_13 = OMLUtilities.tableVariableName(eClass_13);
          _builder.append(_tableVariableName_13, "  \t");
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
        final Function1<EClass, Boolean> _function_10 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_11 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "ConceptTreeDisjunction"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_11));
        };
        Iterable<EClass> _filter_9 = IterableExtensions.<EClass>filter(eClasses, _function_10);
        boolean _hasElements_10 = false;
        for(final EClass eClass_14 : _filter_9) {
          if (!_hasElements_10) {
            _hasElements_10 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(conceptTreeDisjunctions, ");
          String _tableVariableName_14 = OMLUtilities.tableVariableName(eClass_14);
          _builder.append(_tableVariableName_14, "  \t");
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
        final Function1<EClass, Boolean> _function_11 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_12 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "ConceptualEntitySingletonInstance"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_12));
        };
        Iterable<EClass> _filter_10 = IterableExtensions.<EClass>filter(eClasses, _function_11);
        boolean _hasElements_11 = false;
        for(final EClass eClass_15 : _filter_10) {
          if (!_hasElements_11) {
            _hasElements_11 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(conceptualEntitySingletonInstances, ");
          String _tableVariableName_15 = OMLUtilities.tableVariableName(eClass_15);
          _builder.append(_tableVariableName_15, "  \t");
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
        final Function1<EClass, Boolean> _function_12 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_13 = (EClass it_1) -> {
            String _name_9 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_9, "SingletonInstanceStructuredDataPropertyContext"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_13));
        };
        Iterable<EClass> _filter_11 = IterableExtensions.<EClass>filter(eClasses, _function_12);
        boolean _hasElements_12 = false;
        for(final EClass eClass_16 : _filter_11) {
          if (!_hasElements_12) {
            _hasElements_12 = true;
          } else {
            _builder.appendImmediate("\n", "  \t");
          }
          _builder.append("includeMap(singletonInstanceStructuredDataPropertyContexts, ");
          String _tableVariableName_16 = OMLUtilities.tableVariableName(eClass_16);
          _builder.append(_tableVariableName_16, "  \t");
          _builder.append(")");
        }
      }
      _builder.newLineIfNotEmpty();
      _builder.append("  \t");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_13 = (EClass it) -> {
          int _size = IterableExtensions.size(OMLUtilities.schemaAPIOrOrderingKeyReferences(it));
          return Boolean.valueOf((_size > 0));
        };
        Iterable<EClass> _filter_12 = IterableExtensions.<EClass>filter(eClasses, _function_13);
        for(final EClass eClass_17 : _filter_12) {
          _builder.append("    ");
          _builder.append("resolve");
          String _upperCaseInitialOrWord_2 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_17));
          _builder.append(_upperCaseInitialOrWord_2, "    ");
          _builder.append("(rs)");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("    ");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("val ext = createExtent()");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("ext.getModules.addAll(terminologyGraphs.values.map[key])");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("ext.getModules.addAll(bundles.values.map[key])");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("ext.getModules.addAll(descriptionBoxes.values.map[key])");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("r.contents.add(ext)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_14 = (EClass it) -> {
          int _size = IterableExtensions.size(OMLUtilities.schemaAPIOrOrderingKeyReferences(it));
          return Boolean.valueOf((_size > 0));
        };
        Iterable<EClass> _filter_13 = IterableExtensions.<EClass>filter(eClasses, _function_14);
        for(final EClass eClass_18 : _filter_13) {
          _builder.append("  ");
          _builder.append("protected def void resolve");
          String _upperCaseInitialOrWord_3 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_18));
          _builder.append(_upperCaseInitialOrWord_3, "  ");
          _builder.append("(ResourceSet rs) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t");
          String _tableVariableName_17 = OMLUtilities.tableVariableName(eClass_18);
          _builder.append(_tableVariableName_17, "  \t");
          _builder.append(".forEach[uuid, oml_kv |");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("val ");
          String _name_9 = eClass_18.getName();
          _builder.append(_name_9, "  \t  ");
          _builder.append(" oml = oml_kv.key");
          _builder.newLineIfNotEmpty();
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("val Map<String, String> kv = oml_kv.value");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("if (!kv.empty) {");
          _builder.newLine();
          {
            Iterable<ETypedElement> _schemaAPIOrOrderingKeyReferences = OMLUtilities.schemaAPIOrOrderingKeyReferences(eClass_18);
            for(final ETypedElement attr_2 : _schemaAPIOrOrderingKeyReferences) {
              {
                Boolean _isIRIReference_1 = OMLUtilities.isIRIReference(attr_2);
                if ((_isIRIReference_1).booleanValue()) {
                  _builder.append("  ");
                  _builder.append("\t    ");
                  _builder.append("val String ");
                  String _name_10 = attr_2.getName();
                  _builder.append(_name_10, "  \t    ");
                  _builder.append("IRI = kv.remove(\"");
                  String _columnName_3 = OMLUtilities.columnName(attr_2);
                  _builder.append(_columnName_3, "  \t    ");
                  _builder.append("\")");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("\t    ");
                  _builder.append("loadOMLZipResource(rs, URI.createURI(");
                  String _name_11 = attr_2.getName();
                  _builder.append(_name_11, "  \t    ");
                  _builder.append("IRI))");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("\t    ");
                  _builder.append("val Pair<");
                  String _name_12 = OMLUtilities.EClassType(attr_2).getName();
                  _builder.append(_name_12, "  \t    ");
                  _builder.append(", Map<String, String>> ");
                  String _name_13 = attr_2.getName();
                  _builder.append(_name_13, "  \t    ");
                  _builder.append("Pair = ");
                  String _tableVariableName_18 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                  _builder.append(_tableVariableName_18, "  \t    ");
                  _builder.append(".get(");
                  String _name_14 = attr_2.getName();
                  _builder.append(_name_14, "  \t    ");
                  _builder.append("IRI)");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("\t    ");
                  _builder.append("if (null === ");
                  String _name_15 = attr_2.getName();
                  _builder.append(_name_15, "  \t    ");
                  _builder.append("Pair)");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("\t    ");
                  _builder.append("  ");
                  _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                  String _name_16 = attr_2.getName();
                  _builder.append(_name_16, "  \t      ");
                  _builder.append(" in ");
                  String _tableVariableName_19 = OMLUtilities.tableVariableName(eClass_18);
                  _builder.append(_tableVariableName_19, "  \t      ");
                  _builder.append("\")");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                  _builder.append("\t    ");
                  _builder.append("oml.");
                  String _name_17 = attr_2.getName();
                  _builder.append(_name_17, "  \t    ");
                  _builder.append(" = ");
                  String _name_18 = attr_2.getName();
                  _builder.append(_name_18, "  \t    ");
                  _builder.append("Pair.key\t\t  \t  ");
                  _builder.newLineIfNotEmpty();
                } else {
                  int _lowerBound = attr_2.getLowerBound();
                  boolean _equals = (_lowerBound == 0);
                  if (_equals) {
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("val String ");
                    String _name_19 = attr_2.getName();
                    _builder.append(_name_19, "  \t    ");
                    _builder.append("XRef = kv.remove(\"");
                    String _columnName_4 = OMLUtilities.columnName(attr_2);
                    _builder.append(_columnName_4, "  \t    ");
                    _builder.append("\")");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("if (\"null\" != ");
                    String _name_20 = attr_2.getName();
                    _builder.append(_name_20, "  \t    ");
                    _builder.append("XRef) {");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("  ");
                    _builder.append("val Pair<");
                    String _name_21 = OMLUtilities.EClassType(attr_2).getName();
                    _builder.append(_name_21, "  \t      ");
                    _builder.append(", Map<String, String>> ");
                    String _name_22 = attr_2.getName();
                    _builder.append(_name_22, "  \t      ");
                    _builder.append("Pair = ");
                    String _tableVariableName_20 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                    _builder.append(_tableVariableName_20, "  \t      ");
                    _builder.append(".get(");
                    String _name_23 = attr_2.getName();
                    _builder.append(_name_23, "  \t      ");
                    _builder.append("XRef)");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("  ");
                    _builder.append("if (null === ");
                    String _name_24 = attr_2.getName();
                    _builder.append(_name_24, "  \t      ");
                    _builder.append("Pair)");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("    ");
                    _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                    String _name_25 = attr_2.getName();
                    _builder.append(_name_25, "  \t        ");
                    _builder.append(" in ");
                    String _tableVariableName_21 = OMLUtilities.tableVariableName(eClass_18);
                    _builder.append(_tableVariableName_21, "  \t        ");
                    _builder.append("\")");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("  ");
                    _builder.append("oml.");
                    String _name_26 = attr_2.getName();
                    _builder.append(_name_26, "  \t      ");
                    _builder.append(" = ");
                    String _name_27 = attr_2.getName();
                    _builder.append(_name_27, "  \t      ");
                    _builder.append("Pair.key");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("}");
                    _builder.newLine();
                  } else {
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("val String ");
                    String _name_28 = attr_2.getName();
                    _builder.append(_name_28, "  \t    ");
                    _builder.append("XRef = kv.remove(\"");
                    String _columnName_5 = OMLUtilities.columnName(attr_2);
                    _builder.append(_columnName_5, "  \t    ");
                    _builder.append("\")");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("val Pair<");
                    String _name_29 = OMLUtilities.EClassType(attr_2).getName();
                    _builder.append(_name_29, "  \t    ");
                    _builder.append(", Map<String, String>> ");
                    String _name_30 = attr_2.getName();
                    _builder.append(_name_30, "  \t    ");
                    _builder.append("Pair = ");
                    String _tableVariableName_22 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                    _builder.append(_tableVariableName_22, "  \t    ");
                    _builder.append(".get(");
                    String _name_31 = attr_2.getName();
                    _builder.append(_name_31, "  \t    ");
                    _builder.append("XRef)");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("if (null === ");
                    String _name_32 = attr_2.getName();
                    _builder.append(_name_32, "  \t    ");
                    _builder.append("Pair)");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("  ");
                    _builder.append("throw new IllegalArgumentException(\"Null cross-reference lookup for ");
                    String _name_33 = attr_2.getName();
                    _builder.append(_name_33, "  \t      ");
                    _builder.append(" in ");
                    String _tableVariableName_23 = OMLUtilities.tableVariableName(eClass_18);
                    _builder.append(_tableVariableName_23, "  \t      ");
                    _builder.append("\")");
                    _builder.newLineIfNotEmpty();
                    _builder.append("  ");
                    _builder.append("\t    ");
                    _builder.append("oml.");
                    String _name_34 = attr_2.getName();
                    _builder.append(_name_34, "  \t    ");
                    _builder.append(" = ");
                    String _name_35 = attr_2.getName();
                    _builder.append(_name_35, "  \t    ");
                    _builder.append("Pair.key");
                    _builder.newLineIfNotEmpty();
                  }
                }
              }
            }
          }
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("}");
          _builder.newLine();
          _builder.append("  ");
          _builder.append("\t");
          _builder.append("]");
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
      _builder.append("protected def OMLZipResource loadOMLZipResource(ResourceSet rs, URI uri) {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("val r = rs.getResource(uri, true)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("switch r {");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("OMLZipResource: {");
      _builder.newLine();
      _builder.append("  \t\t  ");
      _builder.append("r.contents.get(0).eAllContents.forEach[e|");
      _builder.newLine();
      _builder.append("  \t\t    ");
      _builder.append("switch e {");
      _builder.newLine();
      {
        for(final EClass eClass_19 : eClasses) {
          _builder.append("  \t          ");
          String _name_36 = eClass_19.getName();
          _builder.append(_name_36, "  \t          ");
          _builder.append(": {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t          ");
          _builder.append("\t");
          _builder.append("val pair = new Pair<");
          String _name_37 = eClass_19.getName();
          _builder.append(_name_37, "  \t          \t");
          _builder.append(", Map<String,String>>(e, Collections.emptyMap)");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t          ");
          _builder.append("  ");
          String _tableVariableName_24 = OMLUtilities.tableVariableName(eClass_19);
          _builder.append(_tableVariableName_24, "  \t            ");
          _builder.append(".put(e.uuid(), pair)");
          _builder.newLineIfNotEmpty();
          {
            final Function1<EClass, Boolean> _function_15 = (EClass it) -> {
              String _name_38 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_38, "LogicalElement"));
            };
            boolean _exists = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_15);
            if (_exists) {
              _builder.append("  \t          ");
              _builder.append("  ");
              _builder.append("logicalElements.put(e.uuid(), new Pair<LogicalElement, Map<String,String>>(e, Collections.emptyMap))");
              _builder.newLine();
            } else {
              final Function1<EClass, Boolean> _function_16 = (EClass it) -> {
                String _name_38 = it.getName();
                return Boolean.valueOf(Objects.equal(_name_38, "Entity"));
              };
              boolean _exists_1 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_16);
              if (_exists_1) {
                _builder.append("  \t          ");
                _builder.append("  ");
                _builder.append("entities.put(e.uuid(), new Pair<Entity, Map<String,String>>(e, Collections.emptyMap))");
                _builder.newLine();
              } else {
                final Function1<EClass, Boolean> _function_17 = (EClass it) -> {
                  String _name_38 = it.getName();
                  return Boolean.valueOf(Objects.equal(_name_38, "EntityRelationship"));
                };
                boolean _exists_2 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_17);
                if (_exists_2) {
                  _builder.append("  \t          ");
                  _builder.append("  ");
                  _builder.append("entityRelationships.put(e.uuid(), new Pair<EntityRelationship, Map<String,String>>(e, Collections.emptyMap))");
                  _builder.newLine();
                } else {
                  final Function1<EClass, Boolean> _function_18 = (EClass it) -> {
                    String _name_38 = it.getName();
                    return Boolean.valueOf(Objects.equal(_name_38, "DataRange"));
                  };
                  boolean _exists_3 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_18);
                  if (_exists_3) {
                    _builder.append("  \t          ");
                    _builder.append("  ");
                    _builder.append("dataRanges.put(e.uuid(), new Pair<DataRange, Map<String,String>>(e, Collections.emptyMap))");
                    _builder.newLine();
                  } else {
                    final Function1<EClass, Boolean> _function_19 = (EClass it) -> {
                      String _name_38 = it.getName();
                      return Boolean.valueOf(Objects.equal(_name_38, "DataRelationshipToScalar"));
                    };
                    boolean _exists_4 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_19);
                    if (_exists_4) {
                      _builder.append("  \t          ");
                      _builder.append("  ");
                      _builder.append("dataRelationshipToScalars.put(e.uuid(), new Pair<DataRelationshipToScalar, Map<String,String>>(e, Collections.emptyMap))");
                      _builder.newLine();
                    } else {
                      final Function1<EClass, Boolean> _function_20 = (EClass it) -> {
                        String _name_38 = it.getName();
                        return Boolean.valueOf(Objects.equal(_name_38, "DataRelationshipToStructure"));
                      };
                      boolean _exists_5 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_20);
                      if (_exists_5) {
                        _builder.append("  \t          ");
                        _builder.append("  ");
                        _builder.append("dataRelationshipToStructures.put(e.uuid(), new PairDataRelationshipToStructure, Map<String,String>>(e, Collections.emptyMap))");
                        _builder.newLine();
                      } else {
                        final Function1<EClass, Boolean> _function_21 = (EClass it) -> {
                          String _name_38 = it.getName();
                          return Boolean.valueOf(Objects.equal(_name_38, "RestrictionStructuredDataPropertyContext"));
                        };
                        boolean _exists_6 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_21);
                        if (_exists_6) {
                          _builder.append("  \t          ");
                          _builder.append("  ");
                          _builder.append("restrictionStructuredDataPropertyContexts.put(e.uuid(), new Pair<RestrictionStructuredDataPropertyContext, Map<String,String>>(e, Collections.emptyMap))");
                          _builder.newLine();
                        } else {
                          final Function1<EClass, Boolean> _function_22 = (EClass it) -> {
                            String _name_38 = it.getName();
                            return Boolean.valueOf(Objects.equal(_name_38, "TerminologyBox"));
                          };
                          boolean _exists_7 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_22);
                          if (_exists_7) {
                            _builder.append("  \t          ");
                            _builder.append("  ");
                            _builder.append("terminologyBoxes.put(e.uuid(), new Pair<TerminologyBox, Map<String,String>>(e, Collections.emptyMap))");
                            _builder.newLine();
                          } else {
                            final Function1<EClass, Boolean> _function_23 = (EClass it) -> {
                              String _name_38 = it.getName();
                              return Boolean.valueOf(Objects.equal(_name_38, "ConceptTreeDisjunction"));
                            };
                            boolean _exists_8 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_23);
                            if (_exists_8) {
                              _builder.append("  \t          ");
                              _builder.append("  ");
                              _builder.append("conceptTreeDisjunctions.put(e.uuid(), new Pair<ConceptTreeDisjunction, Map<String,String>>(e, Collections.emptyMap))");
                              _builder.newLine();
                            } else {
                              final Function1<EClass, Boolean> _function_24 = (EClass it) -> {
                                String _name_38 = it.getName();
                                return Boolean.valueOf(Objects.equal(_name_38, "ConceptualEntitySingletonInstance"));
                              };
                              boolean _exists_9 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_24);
                              if (_exists_9) {
                                _builder.append("  \t          ");
                                _builder.append("  ");
                                _builder.append("conceptualEntitySingletonInstances.put(e.uuid(), new Pair<ConceptualEntitySingletonInstance, Map<String,String>>(e, Collections.emptyMap))");
                                _builder.newLine();
                              } else {
                                final Function1<EClass, Boolean> _function_25 = (EClass it) -> {
                                  String _name_38 = it.getName();
                                  return Boolean.valueOf(Objects.equal(_name_38, "SingletonInstanceStructuredDataPropertyContext"));
                                };
                                boolean _exists_10 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_25);
                                if (_exists_10) {
                                  _builder.append("  \t          ");
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
          {
            final Function1<EClass, Boolean> _function_26 = (EClass it) -> {
              String _name_38 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_38, "Module"));
            };
            boolean _exists_11 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_26);
            if (_exists_11) {
              _builder.append("  \t          ");
              _builder.append("  ");
              String _tableVariableName_25 = OMLUtilities.tableVariableName(eClass_19);
              _builder.append(_tableVariableName_25, "  \t            ");
              _builder.append(".put(e.iri(), pair)");
              _builder.newLineIfNotEmpty();
            }
          }
          {
            final Function1<EClass, Boolean> _function_27 = (EClass it) -> {
              String _name_38 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_38, "TerminologyBox"));
            };
            boolean _exists_12 = IterableExtensions.<EClass>exists(eClass_19.getEAllSuperTypes(), _function_27);
            if (_exists_12) {
              _builder.append("  \t          ");
              _builder.append("  ");
              _builder.append("terminologyBoxes.put(e.uuid(), new Pair<TerminologyBox, Map<String,String>>(e, Collections.emptyMap))");
              _builder.newLine();
              _builder.append("  \t          ");
              _builder.append("  ");
              _builder.append("terminologyBoxes.put(e.iri(), new Pair<TerminologyBox, Map<String,String>>(e, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          _builder.append("  \t          ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.append("  \t\t    \t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t\t  ");
      _builder.append("]");
      _builder.newLine();
      _builder.append("  \t\t  ");
      _builder.append("return r");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("default:");
      _builder.newLine();
      _builder.append("  \t\t  ");
      _builder.append("throw new IllegalArgumentException(\"OMLTables.loadOMLZipResource(\"+uri+\") should have produce an OMLZipResource!\")");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
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
