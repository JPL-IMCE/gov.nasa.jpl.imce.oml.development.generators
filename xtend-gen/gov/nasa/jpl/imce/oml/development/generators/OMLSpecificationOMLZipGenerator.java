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
      _builder.append("import java.util.HashSet");
      _builder.newLine();
      _builder.append("import java.util.LinkedList");
      _builder.newLine();
      _builder.append("import java.util.Map");
      _builder.newLine();
      _builder.append("import java.util.Queue");
      _builder.newLine();
      _builder.append("import java.util.Set");
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
      _builder.append("import org.eclipse.xtext.resource.XtextResource");
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
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipEnumerationRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipOneOfRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipValueRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipExistentialRangeRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.descriptions.InstanceRelationshipUniversalRangeRestriction");
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
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.AspectKind");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.AspectSpecializationAxiom");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.BinaryScalarRestriction");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.CardinalityRestrictedAspect");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.CardinalityRestrictedConcept");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.CardinalityRestrictedReifiedRelationship");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ChainRule");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.Concept");
      _builder.newLine();
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ConceptKind");
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
      _builder.append("import gov.nasa.jpl.imce.oml.model.terminologies.ReifiedRelationshipRestriction");
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
      _builder.append("protected val Map<String, Pair<AspectKind, Map<String,String>>> aspectKinds");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Pair<ConceptKind, Map<String,String>>> conceptKinds");
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
      _builder.append("protected val Queue<String> iriLoadQueue");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Set<String> visitedIRIs");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Queue<Module> moduleQueue");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Set<Module> visitedModules");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected val Map<String, Module> iri2module");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("new() {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("iriLoadQueue = new LinkedList<String>()");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("visitedIRIs = new HashSet<String>()");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("moduleQueue = new LinkedList<Module>()");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("visitedModules = new HashSet<Module>()");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("iri2module = new HashMap<String, Module>()");
      _builder.newLine();
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
      _builder.append("    ");
      _builder.append("logicalElements = new HashMap<String, Pair<LogicalElement, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("entities = new HashMap<String, Pair<Entity, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("aspectKinds = new HashMap<String, Pair<AspectKind, Map<String,String>>>()");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("conceptKinds = new HashMap<String, Pair<ConceptKind, Map<String,String>>>()");
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
          _builder.append("entry.time = 0L");
          _builder.newLine();
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
      _builder.append("\t");
      _builder.append("val fileURI = URI.createFileURI(omlZipFile.absolutePath)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("val c = OMLExtensions.findCatalogIfExists(rs, fileURI)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("if (null === c)");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("throw new IllegalArgumentException(\"");
      _builder.append(tableName, "\t\t");
      _builder.append(".load(): failed to find an OML catalog from: \"+fileURI)");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.append("if (c.parsedCatalogs.empty)");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("throw new IllegalArgumentException(\"");
      _builder.append(tableName, "\t\t");
      _builder.append(".load(): No OML catalog found from: \"+fileURI)");
      _builder.newLineIfNotEmpty();
      _builder.append("\t");
      _builder.append("if (c.entries.empty)");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("throw new IllegalArgumentException(\"");
      _builder.append(tableName, "\t\t");
      _builder.append(".load(): Empty OML catalog from: \"+c.parsedCatalogs.join(\"\\n\"))");
      _builder.newLineIfNotEmpty();
      _builder.append("\t\t\t\t\t\t      ");
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
      _builder.append("    ");
      _builder.append("try {");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("Collections.list(zip.entries).forEach[ze | ");
      _builder.newLine();
      _builder.append("      \t\t");
      _builder.append("val is = zip.getInputStream(ze)");
      _builder.newLine();
      _builder.append("      \t\t");
      _builder.append("val buffer = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))");
      _builder.newLine();
      _builder.append("      \t\t");
      _builder.append("val lines = new ArrayList<String>()");
      _builder.newLine();
      _builder.append("      \t\t");
      _builder.append("lines.addAll(buffer.lines().iterator.toIterable)");
      _builder.newLine();
      _builder.append("      \t\t");
      _builder.append("is.close()");
      _builder.newLine();
      _builder.append("      \t\t");
      _builder.append("switch ze.name {");
      _builder.newLine();
      {
        for(final EClass eClass_4 : eClasses) {
          _builder.append("  \t    \t\t\t");
          _builder.append("case \"");
          String _pluralize_1 = OMLUtilities.pluralize(eClass_4.getName());
          _builder.append(_pluralize_1, "  \t    \t\t\t");
          _builder.append(".json\":");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t    \t\t\t");
          _builder.append("\t");
          _builder.append("tables.read");
          String _upperCaseInitialOrWord = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_4));
          _builder.append(_upperCaseInitialOrWord, "  \t    \t\t\t\t");
          _builder.append("(ext, lines)");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("        \t\t\t");
      _builder.append("default:");
      _builder.newLine();
      _builder.append("          \t\t\t");
      _builder.append("throw new IllegalArgumentException(\"");
      _builder.append(tableName, "          \t\t\t");
      _builder.append(".load(): unrecognized table name: \"+ze.name)");
      _builder.newLineIfNotEmpty();
      _builder.append("      \t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    \t\t");
      _builder.append("]");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("} finally {");
      _builder.newLine();
      _builder.append("\t    ");
      _builder.append("zip.close()");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("    ");
      _builder.append("tables.processQueue(rs)");
      _builder.newLine();
      _builder.append("    ");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("tables.resolve(rs, r)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def void queueModule(Module m) {");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("moduleQueue.add(m)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("def void processQueue(ResourceSet rs) {");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("var Boolean more = false");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("do {");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("more = false");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("if (!iriLoadQueue.empty) {");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("more = true");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("val iri = iriLoadQueue.remove");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("if (visitedIRIs.add(iri)) {");
      _builder.newLine();
      _builder.append("\t\t\t  ");
      _builder.append("loadOMLZipResource(rs, URI.createURI(iri))\t");
      _builder.newLine();
      _builder.append("     \t \t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("        ");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("if (!moduleQueue.empty) {");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("more = true");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("val m = moduleQueue.remove");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("if (visitedModules.add(m)) {");
      _builder.newLine();
      _builder.append("\t\t\t  ");
      _builder.append("includeModule(m)");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("        ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("} while (more)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  ");
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
                  {
                    boolean _isRequired = attr_1.isRequired();
                    if (_isRequired) {
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
                      _builder.newLineIfNotEmpty();
                    } else {
                      _builder.append("  ");
                      _builder.append("\t  ");
                      _builder.append("val ");
                      String _name_6 = attr_1.getName();
                      _builder.append(_name_6, "  \t  ");
                      _builder.append("_value = kv.remove(\"");
                      String _columnName_2 = OMLUtilities.columnName(attr_1);
                      _builder.append(_columnName_2, "  \t  ");
                      _builder.append("\")");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t  ");
                      _builder.append("if (null !== ");
                      String _name_7 = attr_1.getName();
                      _builder.append(_name_7, "  \t  ");
                      _builder.append("_value && \"null\" != ");
                      String _name_8 = attr_1.getName();
                      _builder.append(_name_8, "  \t  ");
                      _builder.append("_value && ");
                      String _name_9 = attr_1.getName();
                      _builder.append(_name_9, "  \t  ");
                      _builder.append("_value.length > 0)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t  ");
                      _builder.append("\t");
                      _builder.append("oml.");
                      String _name_10 = attr_1.getName();
                      _builder.append(_name_10, "  \t  \t");
                      _builder.append(" = OMLTables.to");
                      String _name_11 = attr_1.getEType().getName();
                      _builder.append(_name_11, "  \t  \t");
                      _builder.append("(");
                      String _name_12 = attr_1.getName();
                      _builder.append(_name_12, "  \t  \t");
                      _builder.append("_value)");
                      _builder.newLineIfNotEmpty();
                    }
                  }
                } else {
                  if (((!(OMLUtilities.isClassFeature(attr_1)).booleanValue()) && (!Objects.equal(attr_1.getName(), "uuid")))) {
                    {
                      boolean _isRequired_1 = attr_1.isRequired();
                      if (_isRequired_1) {
                        _builder.append("  ");
                        _builder.append("\t  ");
                        _builder.append("oml.");
                        String _name_13 = attr_1.getName();
                        _builder.append(_name_13, "  \t  ");
                        _builder.append(" = OMLTables.to");
                        String _name_14 = attr_1.getEType().getName();
                        _builder.append(_name_14, "  \t  ");
                        _builder.append("(kv.remove(\"");
                        String _columnName_3 = OMLUtilities.columnName(attr_1);
                        _builder.append(_columnName_3, "  \t  ");
                        _builder.append("\"))");
                        _builder.newLineIfNotEmpty();
                      } else {
                        _builder.append("  ");
                        _builder.append("\t  ");
                        _builder.append("val ");
                        String _name_15 = attr_1.getName();
                        _builder.append(_name_15, "  \t  ");
                        _builder.append("_value = kv.remove(\"");
                        String _columnName_4 = OMLUtilities.columnName(attr_1);
                        _builder.append(_columnName_4, "  \t  ");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t  ");
                        _builder.append("if (null !== ");
                        String _name_16 = attr_1.getName();
                        _builder.append(_name_16, "  \t  ");
                        _builder.append("_value && ");
                        String _name_17 = attr_1.getName();
                        _builder.append(_name_17, "  \t  ");
                        _builder.append("_value.length > 0)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t  ");
                        _builder.append("\t");
                        _builder.append("oml.");
                        String _name_18 = attr_1.getName();
                        _builder.append(_name_18, "  \t  \t");
                        _builder.append(" = OMLTables.to");
                        String _name_19 = attr_1.getEType().getName();
                        _builder.append(_name_19, "  \t  \t");
                        _builder.append("(");
                        String _name_20 = attr_1.getName();
                        _builder.append(_name_20, "  \t  \t");
                        _builder.append("_value)");
                        _builder.newLineIfNotEmpty();
                      }
                    }
                  } else {
                    Boolean _isIRIReference_1 = OMLUtilities.isIRIReference(attr_1);
                    if ((_isIRIReference_1).booleanValue()) {
                      _builder.append("  ");
                      _builder.append("\t  ");
                      _builder.append("val String ");
                      String _name_21 = attr_1.getName();
                      _builder.append(_name_21, "  \t  ");
                      _builder.append("IRI = kv.get(\"");
                      String _columnName_5 = OMLUtilities.columnName(attr_1);
                      _builder.append(_columnName_5, "  \t  ");
                      _builder.append("\")");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t  ");
                      _builder.append("if (null === ");
                      String _name_22 = attr_1.getName();
                      _builder.append(_name_22, "  \t  ");
                      _builder.append("IRI)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t  ");
                      _builder.append("\t");
                      _builder.append("throw new IllegalArgumentException(\"read");
                      String _upperCaseInitialOrWord_2 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_5));
                      _builder.append(_upperCaseInitialOrWord_2, "  \t  \t");
                      _builder.append(": missing \'");
                      String _columnName_6 = OMLUtilities.columnName(attr_1);
                      _builder.append(_columnName_6, "  \t  \t");
                      _builder.append("\' in: \"+kv.toString)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t  ");
                      _builder.append("iriLoadQueue.add(");
                      String _name_23 = attr_1.getName();
                      _builder.append(_name_23, "  \t  ");
                      _builder.append("IRI)");
                      _builder.newLineIfNotEmpty();
                    }
                  }
                }
              }
            }
          }
          _builder.append("  ");
          _builder.append("\t  ");
          _builder.append("val pair = new Pair<");
          String _name_24 = eClass_5.getName();
          _builder.append(_name_24, "  \t  ");
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
          String _upperCaseInitialOrWord_3 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_5));
          _builder.append(_upperCaseInitialOrWord_3, "  \t  ");
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
      _builder.append("  ");
      _builder.append("protected def <U,V extends U> Boolean includeMap(Map<String, Pair<U, Map<String, String>>> uMap, Map<String, Pair<V, Map<String, String>>> vMap) {");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("val Boolean[] updated = #{ false }");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("vMap.forEach[uuid,kv|");
      _builder.newLine();
      _builder.append("    \t\t");
      _builder.append("val prev = uMap.put(uuid, new Pair<U, Map<String, String>>(kv.key, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("if (null === prev) {");
      _builder.newLine();
      _builder.append("        \t\t");
      _builder.append("updated.set(0, true)");
      _builder.newLine();
      _builder.append("        \t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("]");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("updated.get(0)");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        for(final EClass eClass_6 : eClasses) {
          _builder.append("  ");
          _builder.append("protected def void include");
          String _upperCaseInitialOrWord_4 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_6));
          _builder.append(_upperCaseInitialOrWord_4, "  ");
          _builder.append("(String uuid, ");
          String _name_25 = eClass_6.getName();
          _builder.append(_name_25, "  ");
          _builder.append(" oml) {");
          _builder.newLineIfNotEmpty();
          {
            final Function1<EClass, Boolean> _function_4 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "Module"));
            };
            boolean _exists_1 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_4);
            if (_exists_1) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("queueModule(oml)");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("modules.put(uuid, new Pair<Module, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_5 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "LogicalElement"));
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
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "Entity"));
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
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "AspectKind"));
            };
            boolean _exists_4 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_7);
            if (_exists_4) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("aspectKinds.put(uuid, new Pair<AspectKind, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_8 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "ConceptKind"));
            };
            boolean _exists_5 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_8);
            if (_exists_5) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("conceptKinds.put(uuid, new Pair<ConceptKind, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_9 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "EntityRelationship"));
            };
            boolean _exists_6 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_9);
            if (_exists_6) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("entityRelationships.put(uuid, new Pair<EntityRelationship, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_10 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "ConceptualRelationship"));
            };
            boolean _exists_7 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_10);
            if (_exists_7) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("conceptualRelationships.put(uuid, new Pair<ConceptualRelationship, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_11 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "DataRange"));
            };
            boolean _exists_8 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_11);
            if (_exists_8) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("dataRanges.put(uuid, new Pair<DataRange, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_12 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "DataRelationshipToScalar"));
            };
            boolean _exists_9 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_12);
            if (_exists_9) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("dataRelationshipToScalars.put(uuid, new Pair<DataRelationshipToScalar, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_13 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "DataRelationshipToStructure"));
            };
            boolean _exists_10 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_13);
            if (_exists_10) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("dataRelationshipToStructures.put(uuid, new Pair<DataRelationshipToStructure, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_14 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "Predicate"));
            };
            boolean _exists_11 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_14);
            if (_exists_11) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("predicates.put(uuid, new Pair<Predicate, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_15 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "RestrictableRelationship"));
            };
            boolean _exists_12 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_15);
            if (_exists_12) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("restrictableRelationships.put(uuid, new Pair<RestrictableRelationship, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_16 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "RestrictionStructuredDataPropertyContext"));
            };
            boolean _exists_13 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_16);
            if (_exists_13) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("restrictionStructuredDataPropertyContexts.put(uuid, new Pair<RestrictionStructuredDataPropertyContext, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_17 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "TerminologyBox"));
            };
            boolean _exists_14 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_17);
            if (_exists_14) {
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
            final Function1<EClass, Boolean> _function_18 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "ConceptTreeDisjunction"));
            };
            boolean _exists_15 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_18);
            if (_exists_15) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("conceptTreeDisjunctions.put(uuid, new Pair<ConceptTreeDisjunction, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_19 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "ConceptualEntitySingletonInstance"));
            };
            boolean _exists_16 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_19);
            if (_exists_16) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("conceptualEntitySingletonInstances.put(uuid, new Pair<ConceptualEntitySingletonInstance, Map<String, String>>(oml, Collections.emptyMap))");
              _builder.newLine();
            }
          }
          {
            final Function1<EClass, Boolean> _function_20 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "SingletonInstanceStructuredDataPropertyContext"));
            };
            boolean _exists_17 = IterableExtensions.<EClass>exists(eClass_6.getEAllSuperTypes(), _function_20);
            if (_exists_17) {
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("singletonInstanceStructuredDataPropertyContexts.put(uuid, new Pair<SingletonInstanceStructuredDataPropertyContext, Map<String, String>>(oml, Collections.emptyMap))");
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
      _builder.newLine();
      _builder.append("  ");
      _builder.append("protected def void resolve(ResourceSet rs, OMLZipResource r) {");
      _builder.newLine();
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("System.out.println(\"Resolve: \"+r.URI)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("val t0 = System.currentTimeMillis");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("// Lookup table for LogicalElement cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_21 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_22 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "LogicalElement"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_22));
        };
        Iterable<EClass> _filter_1 = IterableExtensions.<EClass>filter(eClasses, _function_21);
        for(final EClass eClass_7 : _filter_1) {
          _builder.append("    ");
          _builder.append("if (includeMap(logicalElements, ");
          String _tableVariableName_6 = OMLUtilities.tableVariableName(eClass_7);
          _builder.append(_tableVariableName_6, "    ");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ logicalElements, ");
          String _tableVariableName_7 = OMLUtilities.tableVariableName(eClass_7);
          _builder.append(_tableVariableName_7, "    \t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for Entity cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_22 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_23 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "Entity"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_23));
        };
        Iterable<EClass> _filter_2 = IterableExtensions.<EClass>filter(eClasses, _function_22);
        for(final EClass eClass_8 : _filter_2) {
          _builder.append("  \t");
          _builder.append("if (includeMap(entities, ");
          String _tableVariableName_8 = OMLUtilities.tableVariableName(eClass_8);
          _builder.append(_tableVariableName_8, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_9 = OMLUtilities.tableVariableName(eClass_8);
          _builder.append(_tableVariableName_9, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for AspectKind cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_23 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_24 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "AspectKind"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_24));
        };
        Iterable<EClass> _filter_3 = IterableExtensions.<EClass>filter(eClasses, _function_23);
        for(final EClass eClass_9 : _filter_3) {
          _builder.append("  \t");
          _builder.append("if (includeMap(aspectKinds, ");
          String _tableVariableName_10 = OMLUtilities.tableVariableName(eClass_9);
          _builder.append(_tableVariableName_10, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ aspectKinds, ");
          String _tableVariableName_11 = OMLUtilities.tableVariableName(eClass_9);
          _builder.append(_tableVariableName_11, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for ConceptKind cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_24 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_25 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "ConceptKind"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_25));
        };
        Iterable<EClass> _filter_4 = IterableExtensions.<EClass>filter(eClasses, _function_24);
        for(final EClass eClass_10 : _filter_4) {
          _builder.append("  \t");
          _builder.append("if (includeMap(conceptKinds, ");
          String _tableVariableName_12 = OMLUtilities.tableVariableName(eClass_10);
          _builder.append(_tableVariableName_12, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ conceptKinds, ");
          String _tableVariableName_13 = OMLUtilities.tableVariableName(eClass_10);
          _builder.append(_tableVariableName_13, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for EntityRelationship cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_25 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_26 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "EntityRelationship"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_26));
        };
        Iterable<EClass> _filter_5 = IterableExtensions.<EClass>filter(eClasses, _function_25);
        for(final EClass eClass_11 : _filter_5) {
          _builder.append("    ");
          _builder.append("if (includeMap(entityRelationships, ");
          String _tableVariableName_14 = OMLUtilities.tableVariableName(eClass_11);
          _builder.append(_tableVariableName_14, "    ");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_15 = OMLUtilities.tableVariableName(eClass_11);
          _builder.append(_tableVariableName_15, "    \t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for ConceptualRelationship cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_26 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_27 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "ConceptualRelationship"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_27));
        };
        Iterable<EClass> _filter_6 = IterableExtensions.<EClass>filter(eClasses, _function_26);
        for(final EClass eClass_12 : _filter_6) {
          _builder.append("    ");
          _builder.append("if (includeMap(conceptualRelationships, ");
          String _tableVariableName_16 = OMLUtilities.tableVariableName(eClass_12);
          _builder.append(_tableVariableName_16, "    ");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_17 = OMLUtilities.tableVariableName(eClass_12);
          _builder.append(_tableVariableName_17, "    \t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for DataRange cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_27 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_28 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "DataRange"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_28));
        };
        Iterable<EClass> _filter_7 = IterableExtensions.<EClass>filter(eClasses, _function_27);
        for(final EClass eClass_13 : _filter_7) {
          _builder.append("    ");
          _builder.append("if (includeMap(dataRanges, ");
          String _tableVariableName_18 = OMLUtilities.tableVariableName(eClass_13);
          _builder.append(_tableVariableName_18, "    ");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_19 = OMLUtilities.tableVariableName(eClass_13);
          _builder.append(_tableVariableName_19, "    \t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for DataRelationshipToScalar cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_28 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_29 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "DataRelationshipToScalar"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_29));
        };
        Iterable<EClass> _filter_8 = IterableExtensions.<EClass>filter(eClasses, _function_28);
        for(final EClass eClass_14 : _filter_8) {
          _builder.append("  \t");
          _builder.append("if (includeMap(dataRelationshipToScalars, ");
          String _tableVariableName_20 = OMLUtilities.tableVariableName(eClass_14);
          _builder.append(_tableVariableName_20, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_21 = OMLUtilities.tableVariableName(eClass_14);
          _builder.append(_tableVariableName_21, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for DataRelationshipToStructure cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_29 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_30 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "DataRelationshipToStructure"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_30));
        };
        Iterable<EClass> _filter_9 = IterableExtensions.<EClass>filter(eClasses, _function_29);
        for(final EClass eClass_15 : _filter_9) {
          _builder.append("  \t");
          _builder.append("if (includeMap(dataRelationshipToStructures, ");
          String _tableVariableName_22 = OMLUtilities.tableVariableName(eClass_15);
          _builder.append(_tableVariableName_22, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_23 = OMLUtilities.tableVariableName(eClass_15);
          _builder.append(_tableVariableName_23, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for Predicate cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_30 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_31 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "Predicate"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_31));
        };
        Iterable<EClass> _filter_10 = IterableExtensions.<EClass>filter(eClasses, _function_30);
        for(final EClass eClass_16 : _filter_10) {
          _builder.append("    ");
          _builder.append("if (includeMap(predicates, ");
          String _tableVariableName_24 = OMLUtilities.tableVariableName(eClass_16);
          _builder.append(_tableVariableName_24, "    ");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_25 = OMLUtilities.tableVariableName(eClass_16);
          _builder.append(_tableVariableName_25, "    \t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for RestrictableRelationship cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_31 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_32 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "RestrictableRelationship"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_32));
        };
        Iterable<EClass> _filter_11 = IterableExtensions.<EClass>filter(eClasses, _function_31);
        for(final EClass eClass_17 : _filter_11) {
          _builder.append("    ");
          _builder.append("if (includeMap(restrictableRelationships, ");
          String _tableVariableName_26 = OMLUtilities.tableVariableName(eClass_17);
          _builder.append(_tableVariableName_26, "    ");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_27 = OMLUtilities.tableVariableName(eClass_17);
          _builder.append(_tableVariableName_27, "    \t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("    ");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for RestrictionStructuredDataPropertyContext cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_32 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_33 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "RestrictionStructuredDataPropertyContext"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_33));
        };
        Iterable<EClass> _filter_12 = IterableExtensions.<EClass>filter(eClasses, _function_32);
        for(final EClass eClass_18 : _filter_12) {
          _builder.append("  \t");
          _builder.append("if (includeMap(restrictionStructuredDataPropertyContexts, ");
          String _tableVariableName_28 = OMLUtilities.tableVariableName(eClass_18);
          _builder.append(_tableVariableName_28, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_29 = OMLUtilities.tableVariableName(eClass_18);
          _builder.append(_tableVariableName_29, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for TerminologyBox cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_33 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_34 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "TerminologyBox"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_34));
        };
        Iterable<EClass> _filter_13 = IterableExtensions.<EClass>filter(eClasses, _function_33);
        for(final EClass eClass_19 : _filter_13) {
          _builder.append("  \t");
          _builder.append("if (includeMap(terminologyBoxes, ");
          String _tableVariableName_30 = OMLUtilities.tableVariableName(eClass_19);
          _builder.append(_tableVariableName_30, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_31 = OMLUtilities.tableVariableName(eClass_19);
          _builder.append(_tableVariableName_31, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for ConceptTreeDisjunction cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_34 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_35 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "ConceptTreeDisjunction"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_35));
        };
        Iterable<EClass> _filter_14 = IterableExtensions.<EClass>filter(eClasses, _function_34);
        for(final EClass eClass_20 : _filter_14) {
          _builder.append("  \t");
          _builder.append("if (includeMap(conceptTreeDisjunctions, ");
          String _tableVariableName_32 = OMLUtilities.tableVariableName(eClass_20);
          _builder.append(_tableVariableName_32, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_33 = OMLUtilities.tableVariableName(eClass_20);
          _builder.append(_tableVariableName_33, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for ConceptualEntitySingletonInstance cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_35 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_36 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "ConceptualEntitySingletonInstance"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_36));
        };
        Iterable<EClass> _filter_15 = IterableExtensions.<EClass>filter(eClasses, _function_35);
        for(final EClass eClass_21 : _filter_15) {
          _builder.append("  \t");
          _builder.append("if (includeMap(conceptualEntitySingletonInstances, ");
          String _tableVariableName_34 = OMLUtilities.tableVariableName(eClass_21);
          _builder.append(_tableVariableName_34, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_35 = OMLUtilities.tableVariableName(eClass_21);
          _builder.append(_tableVariableName_35, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("\t");
      _builder.append("// Lookup table for SingletonInstanceStructuredDataPropertyContext cross references");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_36 = (EClass it) -> {
          final Function1<EClass, Boolean> _function_37 = (EClass it_1) -> {
            String _name_26 = it_1.getName();
            return Boolean.valueOf(Objects.equal(_name_26, "SingletonInstanceStructuredDataPropertyContext"));
          };
          return Boolean.valueOf(IterableExtensions.<EClass>exists(it.getEAllSuperTypes(), _function_37));
        };
        Iterable<EClass> _filter_16 = IterableExtensions.<EClass>filter(eClasses, _function_36);
        for(final EClass eClass_22 : _filter_16) {
          _builder.append("  \t");
          _builder.append("if (includeMap(singletonInstanceStructuredDataPropertyContexts, ");
          String _tableVariableName_36 = OMLUtilities.tableVariableName(eClass_22);
          _builder.append(_tableVariableName_36, "  \t");
          _builder.append(")) {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("\t");
          _builder.append("System.out.println(\"+ entities, ");
          String _tableVariableName_37 = OMLUtilities.tableVariableName(eClass_22);
          _builder.append(_tableVariableName_37, "  \t\t");
          _builder.append("\")");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("var iterations = 0");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("val progress = new ArrayList<Boolean>(1)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("val allDone = new ArrayList<Boolean>(1)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("progress.add(false)");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("allDone.add(false)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("do {");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("progress.set(0, false)");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("allDone.set(0, true)");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("iterations += 1");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("System.out.println(\"Resolve iterations: \"+iterations)");
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_37 = (EClass it) -> {
          int _size = IterableExtensions.size(OMLUtilities.schemaAPIOrOrderingKeyReferences(it));
          return Boolean.valueOf((_size > 0));
        };
        Iterable<EClass> _filter_17 = IterableExtensions.<EClass>filter(eClasses, _function_37);
        for(final EClass eClass_23 : _filter_17) {
          _builder.append("    \t");
          _builder.append("resolve");
          String _upperCaseInitialOrWord_5 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_23));
          _builder.append(_upperCaseInitialOrWord_5, "    \t");
          _builder.append("(rs, progress, allDone)");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("    ");
      _builder.append("} while (!allDone.get(0) && !progress.get(0) && iterations < 10)");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("if (!allDone.get(0) && !progress.get(0)) {");
      _builder.newLine();
      _builder.append("    \t");
      _builder.append("throw new IllegalArgumentException(\"Failed to resolve cross references within \"+iterations+\" iterations.\")");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("val dt = t0 - System.currentTimeMillis");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("val ms = dt % 1000");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("val s = dt / 1000");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("System.out.println(\"Resolve: \"+r.URI+\" in \"+s+\"s, \"+ms+\"ms\")");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      {
        final Function1<EClass, Boolean> _function_38 = (EClass it) -> {
          int _size = IterableExtensions.size(OMLUtilities.schemaAPIOrOrderingKeyReferences(it));
          return Boolean.valueOf((_size > 0));
        };
        Iterable<EClass> _filter_18 = IterableExtensions.<EClass>filter(eClasses, _function_38);
        for(final EClass eClass_24 : _filter_18) {
          _builder.append("  ");
          _builder.append("protected def void resolve");
          String _upperCaseInitialOrWord_6 = OMLUtilities.upperCaseInitialOrWord(OMLUtilities.tableVariableName(eClass_24));
          _builder.append(_upperCaseInitialOrWord_6, "  ");
          _builder.append("(ResourceSet rs, ArrayList<Boolean> progress, ArrayList<Boolean> allDone) {");
          _builder.newLineIfNotEmpty();
          {
            final Function1<EClass, Boolean> _function_39 = (EClass it) -> {
              String _name_26 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_26, "ModuleEdge"));
            };
            boolean _exists_18 = IterableExtensions.<EClass>exists(eClass_24.getEAllSuperTypes(), _function_39);
            if (_exists_18) {
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
              String _name_26 = eClass_24.getName();
              _builder.append(_name_26, "  \t\t");
              _builder.append(", Map<String, String>>>()");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("\t");
              String _tableVariableName_38 = OMLUtilities.tableVariableName(eClass_24);
              _builder.append(_tableVariableName_38, "  \t\t");
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
              String _name_27 = eClass_24.getName();
              _builder.append(_name_27, "  \t  \t\t\t");
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
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  \t\t\t\t");
              _builder.append("allDone.set(0, false)");
              _builder.newLine();
              {
                Iterable<ETypedElement> _schemaAPIOrOrderingKeyReferences = OMLUtilities.schemaAPIOrOrderingKeyReferences(eClass_24);
                for(final ETypedElement attr_2 : _schemaAPIOrOrderingKeyReferences) {
                  {
                    Boolean _isIRIReference_2 = OMLUtilities.isIRIReference(attr_2);
                    if ((_isIRIReference_2).booleanValue()) {
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t");
                      _builder.append("val String ");
                      String _name_28 = attr_2.getName();
                      _builder.append(_name_28, "  \t    \t\t\t");
                      _builder.append("IRI = kv.get(\"");
                      String _columnName_7 = OMLUtilities.columnName(attr_2);
                      _builder.append(_columnName_7, "  \t    \t\t\t");
                      _builder.append("\")");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t");
                      _builder.append("val Pair<");
                      String _name_29 = OMLUtilities.EClassType(attr_2).getName();
                      _builder.append(_name_29, "  \t    \t\t\t");
                      _builder.append(", Map<String, String>> ");
                      String _name_30 = attr_2.getName();
                      _builder.append(_name_30, "  \t    \t\t\t");
                      _builder.append("Pair = ");
                      String _tableVariableName_39 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                      _builder.append(_tableVariableName_39, "  \t    \t\t\t");
                      _builder.append(".get(");
                      String _name_31 = attr_2.getName();
                      _builder.append(_name_31, "  \t    \t\t\t");
                      _builder.append("IRI)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t");
                      _builder.append("if (null !== ");
                      String _name_32 = attr_2.getName();
                      _builder.append(_name_32, "  \t    \t\t\t");
                      _builder.append("Pair) {");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t");
                      _builder.append("\t");
                      _builder.append("oml.");
                      String _name_33 = attr_2.getName();
                      _builder.append(_name_33, "  \t    \t\t\t\t");
                      _builder.append(" = ");
                      String _name_34 = attr_2.getName();
                      _builder.append(_name_34, "  \t    \t\t\t\t");
                      _builder.append("Pair.key");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t");
                      _builder.append("\t");
                      _builder.append("kv.remove(\"");
                      String _columnName_8 = OMLUtilities.columnName(attr_2);
                      _builder.append(_columnName_8, "  \t    \t\t\t\t");
                      _builder.append("\")");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t");
                      _builder.append("\t");
                      _builder.append("progress.set(0, true)");
                      _builder.newLine();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t");
                      _builder.append("} else");
                      _builder.newLine();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    \t\t\t");
                      _builder.append("\t");
                      _builder.append("progress.set(0, false)");
                      _builder.newLine();
                    } else {
                      int _lowerBound = attr_2.getLowerBound();
                      boolean _equals = (_lowerBound == 0);
                      if (_equals) {
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("val String ");
                        String _name_35 = attr_2.getName();
                        _builder.append(_name_35, "  \t    \t\t\t");
                        _builder.append("XRef = kv.get(\"");
                        String _columnName_9 = OMLUtilities.columnName(attr_2);
                        _builder.append(_columnName_9, "  \t    \t\t\t");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("if (null !== ");
                        String _name_36 = attr_2.getName();
                        _builder.append(_name_36, "  \t    \t\t\t");
                        _builder.append("XRef && \"null\" != ");
                        String _name_37 = attr_2.getName();
                        _builder.append(_name_37, "  \t    \t\t\t");
                        _builder.append("XRef) {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t");
                        _builder.append("val Pair<");
                        String _name_38 = OMLUtilities.EClassType(attr_2).getName();
                        _builder.append(_name_38, "  \t    \t\t\t\t");
                        _builder.append(", Map<String, String>> ");
                        String _name_39 = attr_2.getName();
                        _builder.append(_name_39, "  \t    \t\t\t\t");
                        _builder.append("Pair = ");
                        String _tableVariableName_40 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                        _builder.append(_tableVariableName_40, "  \t    \t\t\t\t");
                        _builder.append(".get(");
                        String _name_40 = attr_2.getName();
                        _builder.append(_name_40, "  \t    \t\t\t\t");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t");
                        _builder.append("if (null !== ");
                        String _name_41 = attr_2.getName();
                        _builder.append(_name_41, "  \t    \t\t\t\t");
                        _builder.append("Pair) {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t\t");
                        _builder.append("oml.");
                        String _name_42 = attr_2.getName();
                        _builder.append(_name_42, "  \t    \t\t\t\t\t");
                        _builder.append(" = ");
                        String _name_43 = attr_2.getName();
                        _builder.append(_name_43, "  \t    \t\t\t\t\t");
                        _builder.append("Pair.key");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t\t");
                        _builder.append("kv.remove(\"");
                        String _columnName_10 = OMLUtilities.columnName(attr_2);
                        _builder.append(_columnName_10, "  \t    \t\t\t\t\t");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t\t");
                        _builder.append("progress.set(0, true)");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t");
                        _builder.append("} else");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t\t");
                        _builder.append("progress.set(0, false)");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("}");
                        _builder.newLine();
                      } else {
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("val String ");
                        String _name_44 = attr_2.getName();
                        _builder.append(_name_44, "  \t    \t\t\t");
                        _builder.append("XRef = kv.get(\"");
                        String _columnName_11 = OMLUtilities.columnName(attr_2);
                        _builder.append(_columnName_11, "  \t    \t\t\t");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("val Pair<");
                        String _name_45 = OMLUtilities.EClassType(attr_2).getName();
                        _builder.append(_name_45, "  \t    \t\t\t");
                        _builder.append(", Map<String, String>> ");
                        String _name_46 = attr_2.getName();
                        _builder.append(_name_46, "  \t    \t\t\t");
                        _builder.append("Pair = ");
                        String _tableVariableName_41 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_2));
                        _builder.append(_tableVariableName_41, "  \t    \t\t\t");
                        _builder.append(".get(");
                        String _name_47 = attr_2.getName();
                        _builder.append(_name_47, "  \t    \t\t\t");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("if (null !== ");
                        String _name_48 = attr_2.getName();
                        _builder.append(_name_48, "  \t    \t\t\t");
                        _builder.append("Pair) {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t");
                        _builder.append("oml.");
                        String _name_49 = attr_2.getName();
                        _builder.append(_name_49, "  \t    \t\t\t\t");
                        _builder.append(" = ");
                        String _name_50 = attr_2.getName();
                        _builder.append(_name_50, "  \t    \t\t\t\t");
                        _builder.append("Pair.key");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t");
                        _builder.append("kv.remove(\"");
                        String _columnName_12 = OMLUtilities.columnName(attr_2);
                        _builder.append(_columnName_12, "  \t    \t\t\t\t");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t");
                        _builder.append("progress.set(0, true)");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("} else");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    \t\t\t");
                        _builder.append("\t");
                        _builder.append("progress.set(0, false)");
                        _builder.newLine();
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
              _builder.append("} while (more && progress.get(0))");
              _builder.newLine();
            } else {
              _builder.append("  ");
              _builder.append("\t");
              _builder.newLine();
              _builder.append("  ");
              _builder.append("\t");
              String _tableVariableName_42 = OMLUtilities.tableVariableName(eClass_24);
              _builder.append(_tableVariableName_42, "  \t");
              _builder.append(".forEach[uuid, oml_kv |");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
              _builder.append("\t");
              _builder.append("  ");
              _builder.append("val ");
              String _name_51 = eClass_24.getName();
              _builder.append(_name_51, "  \t  ");
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
                Iterable<ETypedElement> _schemaAPIOrOrderingKeyReferences_1 = OMLUtilities.schemaAPIOrOrderingKeyReferences(eClass_24);
                for(final ETypedElement attr_3 : _schemaAPIOrOrderingKeyReferences_1) {
                  {
                    Boolean _isIRIReference_3 = OMLUtilities.isIRIReference(attr_3);
                    if ((_isIRIReference_3).booleanValue()) {
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("val String ");
                      String _name_52 = attr_3.getName();
                      _builder.append(_name_52, "  \t    ");
                      _builder.append("IRI = kv.get(\"");
                      String _columnName_13 = OMLUtilities.columnName(attr_3);
                      _builder.append(_columnName_13, "  \t    ");
                      _builder.append("\")");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("val Pair<");
                      String _name_53 = OMLUtilities.EClassType(attr_3).getName();
                      _builder.append(_name_53, "  \t    ");
                      _builder.append(", Map<String, String>> ");
                      String _name_54 = attr_3.getName();
                      _builder.append(_name_54, "  \t    ");
                      _builder.append("Pair = ");
                      String _tableVariableName_43 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_3));
                      _builder.append(_tableVariableName_43, "  \t    ");
                      _builder.append(".get(");
                      String _name_55 = attr_3.getName();
                      _builder.append(_name_55, "  \t    ");
                      _builder.append("IRI)");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("if (null !== ");
                      String _name_56 = attr_3.getName();
                      _builder.append(_name_56, "  \t    ");
                      _builder.append("Pair) {");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("\t");
                      _builder.append("oml.");
                      String _name_57 = attr_3.getName();
                      _builder.append(_name_57, "  \t    \t");
                      _builder.append(" = ");
                      String _name_58 = attr_3.getName();
                      _builder.append(_name_58, "  \t    \t");
                      _builder.append("Pair.key");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("\t");
                      _builder.append("kv.remove(\"");
                      String _columnName_14 = OMLUtilities.columnName(attr_3);
                      _builder.append(_columnName_14, "  \t    \t");
                      _builder.append("\")");
                      _builder.newLineIfNotEmpty();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("\t");
                      _builder.append("progress.set(0, true)");
                      _builder.newLine();
                      _builder.append("  ");
                      _builder.append("\t");
                      _builder.append("    ");
                      _builder.append("}");
                      _builder.newLine();
                    } else {
                      int _lowerBound_1 = attr_3.getLowerBound();
                      boolean _equals_1 = (_lowerBound_1 == 0);
                      if (_equals_1) {
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("val String ");
                        String _name_59 = attr_3.getName();
                        _builder.append(_name_59, "  \t    ");
                        _builder.append("XRef = kv.get(\"");
                        String _columnName_15 = OMLUtilities.columnName(attr_3);
                        _builder.append(_columnName_15, "  \t    ");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("if (null !== ");
                        String _name_60 = attr_3.getName();
                        _builder.append(_name_60, "  \t    ");
                        _builder.append("XRef && \"null\" != ");
                        String _name_61 = attr_3.getName();
                        _builder.append(_name_61, "  \t    ");
                        _builder.append("XRef) {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("val Pair<");
                        String _name_62 = OMLUtilities.EClassType(attr_3).getName();
                        _builder.append(_name_62, "  \t      ");
                        _builder.append(", Map<String, String>> ");
                        String _name_63 = attr_3.getName();
                        _builder.append(_name_63, "  \t      ");
                        _builder.append("Pair = ");
                        String _tableVariableName_44 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_3));
                        _builder.append(_tableVariableName_44, "  \t      ");
                        _builder.append(".get(");
                        String _name_64 = attr_3.getName();
                        _builder.append(_name_64, "  \t      ");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("if (null !== ");
                        String _name_65 = attr_3.getName();
                        _builder.append(_name_65, "  \t      ");
                        _builder.append("Pair) {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  \t");
                        _builder.append("oml.");
                        String _name_66 = attr_3.getName();
                        _builder.append(_name_66, "  \t      \t");
                        _builder.append(" = ");
                        String _name_67 = attr_3.getName();
                        _builder.append(_name_67, "  \t      \t");
                        _builder.append("Pair.key");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  \t");
                        _builder.append("kv.remove(\"");
                        String _columnName_16 = OMLUtilities.columnName(attr_3);
                        _builder.append(_columnName_16, "  \t      \t");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  \t");
                        _builder.append("progress.set(0, true)");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("  ");
                        _builder.append("}");
                        _builder.newLine();
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
                        String _name_68 = attr_3.getName();
                        _builder.append(_name_68, "  \t    ");
                        _builder.append("XRef = kv.get(\"");
                        String _columnName_17 = OMLUtilities.columnName(attr_3);
                        _builder.append(_columnName_17, "  \t    ");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("val Pair<");
                        String _name_69 = OMLUtilities.EClassType(attr_3).getName();
                        _builder.append(_name_69, "  \t    ");
                        _builder.append(", Map<String, String>> ");
                        String _name_70 = attr_3.getName();
                        _builder.append(_name_70, "  \t    ");
                        _builder.append("Pair = ");
                        String _tableVariableName_45 = OMLUtilities.tableVariableName(OMLUtilities.EClassType(attr_3));
                        _builder.append(_tableVariableName_45, "  \t    ");
                        _builder.append(".get(");
                        String _name_71 = attr_3.getName();
                        _builder.append(_name_71, "  \t    ");
                        _builder.append("XRef)");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("if (null !== ");
                        String _name_72 = attr_3.getName();
                        _builder.append(_name_72, "  \t    ");
                        _builder.append("Pair) {");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("\t");
                        _builder.append("oml.");
                        String _name_73 = attr_3.getName();
                        _builder.append(_name_73, "  \t    \t");
                        _builder.append(" = ");
                        String _name_74 = attr_3.getName();
                        _builder.append(_name_74, "  \t    \t");
                        _builder.append("Pair.key");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("\t");
                        _builder.append("kv.remove(\"");
                        String _columnName_18 = OMLUtilities.columnName(attr_3);
                        _builder.append(_columnName_18, "  \t    \t");
                        _builder.append("\")");
                        _builder.newLineIfNotEmpty();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("\t");
                        _builder.append("progress.set(0, true)");
                        _builder.newLine();
                        _builder.append("  ");
                        _builder.append("\t");
                        _builder.append("    ");
                        _builder.append("}");
                        _builder.newLine();
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
      _builder.append("val uriString = uri.toString");
      _builder.newLine();
      _builder.append("  \t");
      _builder.append("val Resource r = if (uriString.startsWith(\"file:\")) {");
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
      _builder.append("val r0 = r0a ?: r0b");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("if (null !== r0) {");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("r0");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("} else {");
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
      _builder.append("switch r {");
      _builder.newLine();
      _builder.append("  \t\t");
      _builder.append("XtextResource:");
      _builder.newLine();
      _builder.append("  \t\t\t");
      _builder.append("r.contents.forEach[e|");
      _builder.newLine();
      _builder.append("  \t\t\t\t");
      _builder.append("switch e {");
      _builder.newLine();
      _builder.append("  \t\t\t\t\t");
      _builder.append("Extent: {");
      _builder.newLine();
      _builder.append("  \t\t\t\t\t\t");
      _builder.append("e.modules.forEach[queueModule]");
      _builder.newLine();
      _builder.append("\t\t\t\t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("]");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
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
      _builder.append("  \t  ");
      _builder.append("val iri = m.iri()");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("val uuid = m.uuid()");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("iri2module.put(iri, m)");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("switch m {");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("TerminologyGraph: {");
      _builder.newLine();
      _builder.append("    \t  \t  ");
      _builder.append("logicalElements.put(uuid, new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyGraphs.put(uuid, new Pair<TerminologyGraph, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyBoxes.put(uuid, new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyBoxes.put(iri, new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("Bundle: {");
      _builder.newLine();
      _builder.append("    \t  \t  ");
      _builder.append("logicalElements.put(uuid, new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("bundles.put(uuid, new Pair<Bundle, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyBoxes.put(uuid, new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("terminologyBoxes.put(iri, new Pair<TerminologyBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("DescriptionBox: {");
      _builder.newLine();
      _builder.append("    \t  \t  ");
      _builder.append("logicalElements.put(uuid, new Pair<LogicalElement, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("descriptionBoxes.put(uuid, new Pair<DescriptionBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t      ");
      _builder.append("descriptionBoxes.put(iri, new Pair<DescriptionBox, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("    \t    ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("}");
      _builder.newLine();
      _builder.append("  \t");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("modules.put(uuid, new Pair<Module, Map<String,String>>(m, Collections.emptyMap))");
      _builder.newLine();
      _builder.append("  \t  ");
      _builder.append("m.eAllContents.forEach[e|");
      _builder.newLine();
      _builder.append("  \t    ");
      _builder.append("switch e {");
      _builder.newLine();
      {
        for(final EClass eClass_25 : eClassesExceptModules) {
          _builder.append("  \t      ");
          String _name_75 = eClass_25.getName();
          _builder.append(_name_75, "  \t      ");
          _builder.append(": {");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t      ");
          _builder.append("  ");
          _builder.append("val pair = new Pair<");
          String _name_76 = eClass_25.getName();
          _builder.append(_name_76, "  \t        ");
          _builder.append(", Map<String,String>>(e, Collections.emptyMap)");
          _builder.newLineIfNotEmpty();
          _builder.append("  \t      ");
          _builder.append("  ");
          String _tableVariableName_46 = OMLUtilities.tableVariableName(eClass_25);
          _builder.append(_tableVariableName_46, "  \t        ");
          _builder.append(".put(e.uuid(), pair)");
          _builder.newLineIfNotEmpty();
          {
            final Function1<EClass, Boolean> _function_40 = (EClass it) -> {
              String _name_77 = it.getName();
              return Boolean.valueOf(Objects.equal(_name_77, "LogicalElement"));
            };
            boolean _exists_19 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_40);
            if (_exists_19) {
              _builder.append("  \t      ");
              _builder.append("  ");
              _builder.append("logicalElements.put(e.uuid(), new Pair<LogicalElement, Map<String,String>>(e, Collections.emptyMap))");
              _builder.newLine();
            } else {
              final Function1<EClass, Boolean> _function_41 = (EClass it) -> {
                String _name_77 = it.getName();
                return Boolean.valueOf(Objects.equal(_name_77, "Entity"));
              };
              boolean _exists_20 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_41);
              if (_exists_20) {
                _builder.append("  \t      ");
                _builder.append("  ");
                _builder.append("entities.put(e.uuid(), new Pair<Entity, Map<String,String>>(e, Collections.emptyMap))");
                _builder.newLine();
              } else {
                final Function1<EClass, Boolean> _function_42 = (EClass it) -> {
                  String _name_77 = it.getName();
                  return Boolean.valueOf(Objects.equal(_name_77, "AspectKind"));
                };
                boolean _exists_21 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_42);
                if (_exists_21) {
                  _builder.append("  \t      ");
                  _builder.append("  ");
                  _builder.append("aspectKinds.put(e.uuid(), new Pair<AspectKind, Map<String,String>>(e, Collections.emptyMap))");
                  _builder.newLine();
                } else {
                  final Function1<EClass, Boolean> _function_43 = (EClass it) -> {
                    String _name_77 = it.getName();
                    return Boolean.valueOf(Objects.equal(_name_77, "ConceptKind"));
                  };
                  boolean _exists_22 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_43);
                  if (_exists_22) {
                    _builder.append("  \t      ");
                    _builder.append("  ");
                    _builder.append("conceptKinds.put(e.uuid(), new Pair<ConceptKind, Map<String,String>>(e, Collections.emptyMap))");
                    _builder.newLine();
                  } else {
                    final Function1<EClass, Boolean> _function_44 = (EClass it) -> {
                      String _name_77 = it.getName();
                      return Boolean.valueOf(Objects.equal(_name_77, "EntityRelationship"));
                    };
                    boolean _exists_23 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_44);
                    if (_exists_23) {
                      _builder.append("  \t      ");
                      _builder.append("  ");
                      _builder.append("entityRelationships.put(e.uuid(), new Pair<EntityRelationship, Map<String,String>>(e, Collections.emptyMap))");
                      _builder.newLine();
                    } else {
                      final Function1<EClass, Boolean> _function_45 = (EClass it) -> {
                        String _name_77 = it.getName();
                        return Boolean.valueOf(Objects.equal(_name_77, "ConceptualRelationship"));
                      };
                      boolean _exists_24 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_45);
                      if (_exists_24) {
                        _builder.append("  \t      ");
                        _builder.append("  ");
                        _builder.append("conceptualRelationships.put(e.uuid(), new Pair<ConceptualRelationship, Map<String,String>>(e, Collections.emptyMap))");
                        _builder.newLine();
                      } else {
                        final Function1<EClass, Boolean> _function_46 = (EClass it) -> {
                          String _name_77 = it.getName();
                          return Boolean.valueOf(Objects.equal(_name_77, "DataRange"));
                        };
                        boolean _exists_25 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_46);
                        if (_exists_25) {
                          _builder.append("  \t      ");
                          _builder.append("  ");
                          _builder.append("dataRanges.put(e.uuid(), new Pair<DataRange, Map<String,String>>(e, Collections.emptyMap))");
                          _builder.newLine();
                        } else {
                          final Function1<EClass, Boolean> _function_47 = (EClass it) -> {
                            String _name_77 = it.getName();
                            return Boolean.valueOf(Objects.equal(_name_77, "DataRelationshipToScalar"));
                          };
                          boolean _exists_26 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_47);
                          if (_exists_26) {
                            _builder.append("  \t      ");
                            _builder.append("  ");
                            _builder.append("dataRelationshipToScalars.put(e.uuid(), new Pair<DataRelationshipToScalar, Map<String,String>>(e, Collections.emptyMap))");
                            _builder.newLine();
                          } else {
                            final Function1<EClass, Boolean> _function_48 = (EClass it) -> {
                              String _name_77 = it.getName();
                              return Boolean.valueOf(Objects.equal(_name_77, "DataRelationshipToStructure"));
                            };
                            boolean _exists_27 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_48);
                            if (_exists_27) {
                              _builder.append("  \t      ");
                              _builder.append("  ");
                              _builder.append("dataRelationshipToStructures.put(e.uuid(), new PairDataRelationshipToStructure, Map<String,String>>(e, Collections.emptyMap))");
                              _builder.newLine();
                            } else {
                              final Function1<EClass, Boolean> _function_49 = (EClass it) -> {
                                String _name_77 = it.getName();
                                return Boolean.valueOf(Objects.equal(_name_77, "Predicate"));
                              };
                              boolean _exists_28 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_49);
                              if (_exists_28) {
                                _builder.append("  \t      ");
                                _builder.append("  ");
                                _builder.append("predicates.put(e.uuid(), new Pair<Predicate, Map<String,String>>(e, Collections.emptyMap))");
                                _builder.newLine();
                              } else {
                                final Function1<EClass, Boolean> _function_50 = (EClass it) -> {
                                  String _name_77 = it.getName();
                                  return Boolean.valueOf(Objects.equal(_name_77, "RestrictableRelationship"));
                                };
                                boolean _exists_29 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_50);
                                if (_exists_29) {
                                  _builder.append("  \t      ");
                                  _builder.append("  ");
                                  _builder.append("restrictableRelationships.put(e.uuid(), new Pair<RestrictableRelationship, Map<String,String>>(e, Collections.emptyMap))");
                                  _builder.newLine();
                                } else {
                                  final Function1<EClass, Boolean> _function_51 = (EClass it) -> {
                                    String _name_77 = it.getName();
                                    return Boolean.valueOf(Objects.equal(_name_77, "RestrictionStructuredDataPropertyContext"));
                                  };
                                  boolean _exists_30 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_51);
                                  if (_exists_30) {
                                    _builder.append("  \t      ");
                                    _builder.append("  ");
                                    _builder.append("restrictionStructuredDataPropertyContexts.put(e.uuid(), new Pair<RestrictionStructuredDataPropertyContext, Map<String,String>>(e, Collections.emptyMap))");
                                    _builder.newLine();
                                  } else {
                                    final Function1<EClass, Boolean> _function_52 = (EClass it) -> {
                                      String _name_77 = it.getName();
                                      return Boolean.valueOf(Objects.equal(_name_77, "TerminologyBox"));
                                    };
                                    boolean _exists_31 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_52);
                                    if (_exists_31) {
                                      _builder.append("  \t      ");
                                      _builder.append("  ");
                                      _builder.append("terminologyBoxes.put(e.uuid(), new Pair<TerminologyBox, Map<String,String>>(e, Collections.emptyMap))");
                                      _builder.newLine();
                                    } else {
                                      final Function1<EClass, Boolean> _function_53 = (EClass it) -> {
                                        String _name_77 = it.getName();
                                        return Boolean.valueOf(Objects.equal(_name_77, "ConceptTreeDisjunction"));
                                      };
                                      boolean _exists_32 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_53);
                                      if (_exists_32) {
                                        _builder.append("  \t      ");
                                        _builder.append("  ");
                                        _builder.append("conceptTreeDisjunctions.put(e.uuid(), new Pair<ConceptTreeDisjunction, Map<String,String>>(e, Collections.emptyMap))");
                                        _builder.newLine();
                                      } else {
                                        final Function1<EClass, Boolean> _function_54 = (EClass it) -> {
                                          String _name_77 = it.getName();
                                          return Boolean.valueOf(Objects.equal(_name_77, "ConceptualEntitySingletonInstance"));
                                        };
                                        boolean _exists_33 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_54);
                                        if (_exists_33) {
                                          _builder.append("  \t      ");
                                          _builder.append("  ");
                                          _builder.append("conceptualEntitySingletonInstances.put(e.uuid(), new Pair<ConceptualEntitySingletonInstance, Map<String,String>>(e, Collections.emptyMap))");
                                          _builder.newLine();
                                        } else {
                                          final Function1<EClass, Boolean> _function_55 = (EClass it) -> {
                                            String _name_77 = it.getName();
                                            return Boolean.valueOf(Objects.equal(_name_77, "SingletonInstanceStructuredDataPropertyContext"));
                                          };
                                          boolean _exists_34 = IterableExtensions.<EClass>exists(eClass_25.getEAllSuperTypes(), _function_55);
                                          if (_exists_34) {
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
