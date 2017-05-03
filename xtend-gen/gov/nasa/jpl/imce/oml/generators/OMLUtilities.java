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
import gov.nasa.jpl.imce.oml.model.extensions.OMLXcorePackages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.xcore.XOperation;
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XMemberFeatureCall;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

public class OMLUtilities extends OMLXcorePackages {
  public static class OMLTableCompare implements Comparator<EClass> {
    private final List<String> knownTables = Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList("Annotation", "AnnotationEntry", "AnnotationProperty", "AnnotationPropertyTable", "AnnotationSubjectPropertyValue", "AnnotationSubjectTable", "Extent", "TerminologyGraph", "Bundle", "ConceptDesignationTerminologyAxiom", "TerminologyExtensionAxiom", "TerminologyNestingAxiom", "Aspect", "Concept", "ReifiedRelationship", "UnreifiedRelationship", "Scalar", "Structure", "BinaryScalarRestriction", "IRIScalarRestriction", "NumericScalarRestriction", "PlainLiteralScalarRestriction", "ScalarOneOfRestriction", "StringScalarRestriction", "SynonymScalarRestriction", "TimeScalarRestriction", "EntityScalarDataProperty", "EntityStructuredDataProperty", "ScalarDataProperty", "StructuredDataProperty", "AspectSpecializationAxiom", "ConceptSpecializationAxiom", "ReifiedRelationshipSpecializationAxiom", "EntityExistentialRestrictionAxiom", "EntityUniversalRestrictionAxiom", "EntityScalarDataPropertyExistentialRestrictionAxiom", "EntityScalarDataPropertyParticularRestrictionAxiom", "EntityScalarDataPropertyUniversalRestrictionAxiom", "ScalarOneOfLiteralAxiom", "BundledTerminologyAxiom", "AnonymousConceptTaxonomyAxiom", "RootConceptTaxonomyAxiom", "SpecificDisjointConceptAxiom"));
    
    public int compare(final EClass c1, final EClass c2) {
      int _xblockexpression = (int) 0;
      {
        final String name1 = c1.getName();
        final String name2 = c2.getName();
        final int i1 = this.knownTables.indexOf(name1);
        final int i2 = this.knownTables.indexOf(name2);
        int _xifexpression = (int) 0;
        if (((i1 > (-1)) && (i2 > (-1)))) {
          _xifexpression = Integer.valueOf(i1).compareTo(Integer.valueOf(i2));
        } else {
          int _xifexpression_1 = (int) 0;
          if (((i1 > (-1)) && (i2 == (-1)))) {
            _xifexpression_1 = (-1);
          } else {
            int _xifexpression_2 = (int) 0;
            if (((i1 == (-1)) && (i2 > (-1)))) {
              _xifexpression_2 = 1;
            } else {
              _xifexpression_2 = name1.compareTo(name2);
            }
            _xifexpression_1 = _xifexpression_2;
          }
          _xifexpression = _xifexpression_1;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }
  }
  
  public static class OMLFeatureCompare implements Comparator<ETypedElement> {
    private final List<String> knownAttributes = Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList("uuid", "tboxUUID", "extentUUID", "terminologyBundleUUID", "bundledTerminologyUUID", "extendedTerminologyUUID", "nestingTerminologyUUID", "nestingContextUUID", "bundleUUID", "moduleUUID", "descriptionBoxUUID", "refiningDescriptionBoxUUID", "singletonConceptClassifierUUID", "singletonReifiedRelationshipClassifierUUID", "dataStructureTypeUUID", "superAspectUUID", "subEntityUUID", "superConceptUUID", "subConceptUUID", "axiomUUID", "keyUUID", "subjectUUID", "propertyUUID", "closedWorldDefinitionsUUID", "refinedDescriptionBoxUUID", "refiningDescriptionBoxUUID", "dataStructureTypeUUID", "structuredDataPropertyValueUUID", "singletonInstanceUUID", "structuredDataPropertyUUID", "scalarDataPropertyUUID", "structuredPropertyTupleUUID", "singletonConceptClassifierUUID", "singletonReifiedRelationshipClassifierUUID", "reifiedRelationshipInstanceUUID", "unreifiedRelationshipUUID", "restrictedRelationUUID", "restrictedDomainUUID", "restrictedRangeUUID", "restrictedEntityUUID", "scalarPropertyUUID", "scalarRestrictionUUID", "domainUUID", "rangeUUID", "sourceUUID", "targetUUID", "superRelationshipUUID", "subRelationshipUUID", "rootUUID", "disjointTaxonomyParentUUID", "disjointLeafUUID", "conceptInstancesUUID", "reifiedRelationshipInstancesUUID", "reifiedRelationshipInstancesDomainsUUID", "reifiedRelationshipInstancesRangesUUID", "unreifiedRelationshipInstancesUUID", "singletonInstanceScalarDataPropertyValuesUUID", "singletonInstanceStructuredDataPropertyValuesUUID", "scalarDataPropertyValuesUUID", "structuredDataPropertyTuplesUUID", "kind", "isAbstract", "isAsymmetric", "isEssential", "isFunctional", "isInverseEssential", "isInverseFunctional", "isIrreflexive", "isReflexive", "isSymmetric", "isTransitive", "isIdentityCriteria", "minExclusive", "minInclusive", "maxExclusive", "maxInclusive", "length", "minLength", "maxLength", "nsPrefix", "name", "langRange", "pattern", "unreifiedPropertyName", "unreifiedInversePropertyName", "iri", "value", "scalarPropertyValue", "literalValue", "annotationsUUID", "boxStatementsUUID", "boxAxiomsUUID", "bundleStatementsUUID", "bundleAxiomsUUID"));
    
    public int compare(final ETypedElement o1, final ETypedElement o2) {
      int _xblockexpression = (int) 0;
      {
        final String name1 = OMLUtilities.columnName(o1);
        final String name2 = OMLUtilities.columnName(o2);
        final int i1 = this.knownAttributes.indexOf(name1);
        final int i2 = this.knownAttributes.indexOf(name2);
        int _xifexpression = (int) 0;
        if (((i1 > (-1)) && (i2 > (-1)))) {
          _xifexpression = Integer.valueOf(i1).compareTo(Integer.valueOf(i2));
        } else {
          int _xifexpression_1 = (int) 0;
          if (((i1 > (-1)) && (i2 == (-1)))) {
            _xifexpression_1 = (-1);
          } else {
            int _xifexpression_2 = (int) 0;
            if (((i1 == (-1)) && (i2 > (-1)))) {
              _xifexpression_2 = 1;
            } else {
              _xifexpression_2 = name1.compareTo(name2);
            }
            _xifexpression_1 = _xifexpression_2;
          }
          _xifexpression = _xifexpression_1;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }
  }
  
  public static String queryResolverName(final EOperation op, final String typePrefix) {
    String _xifexpression = null;
    EAnnotation _eAnnotation = op.getEAnnotation("http://imce.jpl.nasa.gov/oml/OverrideVal");
    boolean _tripleNotEquals = (null != _eAnnotation);
    if (_tripleNotEquals) {
      String _xblockexpression = null;
      {
        boolean _isEmpty = op.getEParameters().isEmpty();
        boolean _not = (!_isEmpty);
        if (_not) {
          throw new IllegalArgumentException("@OverrideVal is not applicable to an operation with formal parameters");
        }
        Boolean _isImplicitExtent = OMLUtilities.isImplicitExtent(op);
        if ((_isImplicitExtent).booleanValue()) {
          throw new IllegalArgumentException("@ImplicitExtent is not applicable to an @OverrideVal operation");
        }
        String _name = op.getName();
        _xblockexpression = ("override val " + _name);
      }
      _xifexpression = _xblockexpression;
    } else {
      String _xblockexpression_1 = null;
      {
        final String kind = "def";
        String _xifexpression_1 = null;
        EAnnotation _eAnnotation_1 = op.getEAnnotation("http://imce.jpl.nasa.gov/oml/Override");
        boolean _tripleNotEquals_1 = (null != _eAnnotation_1);
        if (_tripleNotEquals_1) {
          _xifexpression_1 = ("override " + kind);
        } else {
          _xifexpression_1 = kind;
        }
        final String decl = _xifexpression_1;
        StringConcatenation _builder = new StringConcatenation();
        {
          EList<EParameter> _eParameters = op.getEParameters();
          boolean _hasElements = false;
          for(final EParameter p : _eParameters) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",\n  ", "");
            }
            String _name = p.getName();
            _builder.append(_name);
            _builder.append(": ");
            String _queryResolverType = OMLUtilities.queryResolverType(p, typePrefix);
            _builder.append(_queryResolverType);
          }
        }
        final String args = _builder.toString();
        StringConcatenation _builder_1 = new StringConcatenation();
        {
          Boolean _isImplicitExtent = OMLUtilities.isImplicitExtent(op);
          if ((_isImplicitExtent).booleanValue()) {
            _builder_1.append("(implicit extent: ");
            _builder_1.append(typePrefix);
            _builder_1.append("Extent)");
          }
        }
        final String impl = _builder_1.toString();
        String _name_1 = op.getName();
        String _plus = ((decl + " ") + _name_1);
        String _plus_1 = (_plus + "\n  (");
        String _plus_2 = (_plus_1 + args);
        String _plus_3 = (_plus_2 + ")");
        _xblockexpression_1 = (_plus_3 + impl);
      }
      _xifexpression = _xblockexpression_1;
    }
    return _xifexpression;
  }
  
  public static String orderingClassName(final EClass eClass) {
    String _xifexpression = null;
    if (((IterableExtensions.<ETypedElement>exists(OMLUtilities.orderingKeys(eClass), new Function1<ETypedElement, Boolean>() {
      public Boolean apply(final ETypedElement f) {
        return Boolean.valueOf(((OMLUtilities.isContainer(f)).booleanValue() || (f.getLowerBound() == 0)));
      }
    }) || Objects.equal(eClass.getName(), "AnnotationEntry")) || Objects.equal(eClass.getName(), "Annotation"))) {
      StringConcatenation _builder = new StringConcatenation();
      String _firstLower = StringExtensions.toFirstLower(eClass.getName());
      _builder.append(_firstLower);
      _builder.append("Ordering(implicit e: Extent)");
      _xifexpression = _builder.toString();
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      String _firstLower_1 = StringExtensions.toFirstLower(eClass.getName());
      _builder_1.append(_firstLower_1);
      _builder_1.append("Ordering");
      _xifexpression = _builder_1.toString();
    }
    return _xifexpression;
  }
  
  public static String orderingTableType(final ETypedElement feature) {
    String _xifexpression = null;
    int _lowerBound = feature.getLowerBound();
    boolean _equals = (_lowerBound == 0);
    if (_equals) {
      String _xifexpression_1 = null;
      String _name = feature.getEType().getName();
      boolean _equals_1 = Objects.equal(_name, "UUID");
      if (_equals_1) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("scala.Ordering.Option[UUID](scala.Ordering.String).compare(x.");
        String _columnName = OMLUtilities.columnName(feature);
        _builder.append(_columnName);
        _builder.append(",y.");
        String _columnName_1 = OMLUtilities.columnName(feature);
        _builder.append(_columnName_1);
        _builder.append(")");
        _xifexpression_1 = _builder.toString();
      } else {
        throw new IllegalArgumentException(("Implemented support for orderingAttributeType for: " + feature));
      }
      _xifexpression = _xifexpression_1;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("x.");
      String _columnName_2 = OMLUtilities.columnName(feature);
      _builder_1.append(_columnName_2);
      _builder_1.append(".compareTo(y.");
      String _columnName_3 = OMLUtilities.columnName(feature);
      _builder_1.append(_columnName_3);
      _builder_1.append(")");
      _xifexpression = _builder_1.toString();
    }
    return _xifexpression;
  }
  
  public static String orderingAttributeType(final ETypedElement feature) {
    String _xifexpression = null;
    int _lowerBound = feature.getLowerBound();
    boolean _equals = (_lowerBound == 0);
    if (_equals) {
      String _xifexpression_1 = null;
      String _name = feature.getEType().getName();
      boolean _equals_1 = Objects.equal(_name, "UUID");
      if (_equals_1) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("scala.Ordering.Option[java.util.UUID](UUIDOrdering).compare(x.");
        String _columnName = OMLUtilities.columnName(feature);
        _builder.append(_columnName);
        _builder.append(",y.");
        String _columnName_1 = OMLUtilities.columnName(feature);
        _builder.append(_columnName_1);
        _builder.append(")");
        _xifexpression_1 = _builder.toString();
      } else {
        throw new IllegalArgumentException(("Implemented support for orderingAttributeType for: " + feature));
      }
      _xifexpression = _xifexpression_1;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("x.");
      String _columnName_2 = OMLUtilities.columnName(feature);
      _builder_1.append(_columnName_2);
      _builder_1.append(".compareTo(y.");
      String _columnName_3 = OMLUtilities.columnName(feature);
      _builder_1.append(_columnName_3);
      _builder_1.append(")");
      _xifexpression = _builder_1.toString();
    }
    return _xifexpression;
  }
  
  public static String orderingClassType(final ETypedElement feature) {
    String _xifexpression = null;
    Boolean _isContainer = OMLUtilities.isContainer(feature);
    if ((_isContainer).booleanValue()) {
      _xifexpression = "scala.Ordering.Option[java.util.UUID](UUIDOrdering)";
    } else {
      String _firstLower = StringExtensions.toFirstLower(feature.getEType().getName());
      _xifexpression = (_firstLower + "Ordering");
    }
    return _xifexpression;
  }
  
  public static String queryResolverType(final ETypedElement feature, final String typePrefix) {
    String _xblockexpression = null;
    {
      final EClassifier type = feature.getEType();
      final String scalaType = OMLUtilities.scalaResolverTypeName(feature);
      String _switchResult = null;
      boolean _matched = false;
      if ((type instanceof EDataType)) {
        _matched=true;
        String _xifexpression = null;
        int _lowerBound = feature.getLowerBound();
        boolean _equals = (_lowerBound == 0);
        if (_equals) {
          _xifexpression = (("scala.Option[" + scalaType) + "]");
        } else {
          _xifexpression = scalaType;
        }
        _switchResult = _xifexpression;
      }
      if (!_matched) {
        if ((type instanceof EClass)) {
          _matched=true;
          String _xifexpression_1 = null;
          int _lowerBound_1 = feature.getLowerBound();
          boolean _equals_1 = (_lowerBound_1 == 0);
          if (_equals_1) {
            String _xifexpression_2 = null;
            int _upperBound = feature.getUpperBound();
            boolean _equals_2 = (_upperBound == (-1));
            if (_equals_2) {
              String _xblockexpression_1 = null;
              {
                EAnnotation _eAnnotation = feature.getEAnnotation("http://imce.jpl.nasa.gov/oml/Collection");
                EMap<String, String> _details = null;
                if (_eAnnotation!=null) {
                  _details=_eAnnotation.getDetails();
                }
                final EMap<String, String> ann = _details;
                String _switchResult_1 = null;
                String _elvis = null;
                String _get = null;
                if (ann!=null) {
                  _get=ann.get("kind");
                }
                if (_get != null) {
                  _elvis = _get;
                } else {
                  _elvis = "";
                }
                boolean _matched_1 = false;
                if (Objects.equal(_elvis, "Map(Seq)")) {
                  _matched_1=true;
                  String _xblockexpression_2 = null;
                  {
                    final String key = ann.get("key");
                    String _name = type.getName();
                    String _plus = (((("scala.collection.immutable.Map[" + key) + ", scala.collection.immutable.Seq[") + typePrefix) + _name);
                    _xblockexpression_2 = (_plus + "]]");
                  }
                  _switchResult_1 = _xblockexpression_2;
                }
                if (!_matched_1) {
                  if (Objects.equal(_elvis, "Map")) {
                    _matched_1=true;
                    String _xblockexpression_3 = null;
                    {
                      final String key = ann.get("key");
                      String _name = type.getName();
                      String _plus = (((("scala.collection.immutable.Map[" + key) + ", ") + typePrefix) + _name);
                      _xblockexpression_3 = (_plus + "]");
                    }
                    _switchResult_1 = _xblockexpression_3;
                  }
                }
                if (!_matched_1) {
                  if (Objects.equal(_elvis, "Set")) {
                    _matched_1=true;
                    String _name = type.getName();
                    String _plus = (("scala.collection.immutable.Set[_ <: " + typePrefix) + _name);
                    _switchResult_1 = (_plus + "]");
                  }
                }
                if (!_matched_1) {
                  if (Objects.equal(_elvis, "SortedSet")) {
                    _matched_1=true;
                    String _name_1 = type.getName();
                    String _plus_1 = (("scala.collection.immutable.SortedSet[" + typePrefix) + _name_1);
                    _switchResult_1 = (_plus_1 + "]");
                  }
                }
                _xblockexpression_1 = _switchResult_1;
              }
              _xifexpression_2 = _xblockexpression_1;
            } else {
              String _name = type.getName();
              String _plus = (("scala.Option[" + typePrefix) + _name);
              _xifexpression_2 = (_plus + "]");
            }
            _xifexpression_1 = _xifexpression_2;
          } else {
            String _name_1 = type.getName();
            _xifexpression_1 = (typePrefix + _name_1);
          }
          _switchResult = _xifexpression_1;
        }
      }
      if (!_matched) {
        String _name_2 = type.getName();
        String _plus_1 = (typePrefix + _name_2);
        _switchResult = (_plus_1 + "//Default");
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public static String scalaResolverTypeName(final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final EClassifier type = feature.getEType();
      String _switchResult = null;
      String _name = type.getName();
      boolean _matched = false;
      if (Objects.equal(_name, "EInt")) {
        _matched=true;
        _switchResult = "scala.Int";
      }
      if (!_matched) {
        if (Objects.equal(_name, "EBoolean")) {
          _matched=true;
          _switchResult = "scala.Boolean";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "EString")) {
          _matched=true;
          _switchResult = "scala.Predef.String";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "AbbrevIRI")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.AbbrevIRI";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "DescriptionKind")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.DescriptionKind";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "IRI")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.IRI";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "LangRange")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.LangRange";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "LexicalNumber")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.LexicalNumber";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "LexicalTime")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.LexicalTime";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "LexicalValue")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.LexicalValue";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "LocalName")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.LocalName";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "NamespacePrefix")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.NamespacePrefix";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "Pattern")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.Pattern";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "UUID")) {
          _matched=true;
          _switchResult = "java.util.UUID";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "TerminologyKind")) {
          _matched=true;
          _switchResult = "gov.nasa.jpl.imce.oml.tables.TerminologyKind";
        }
      }
      if (!_matched) {
        String _name_1 = type.getName();
        _switchResult = ("resolver.api." + _name_1);
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public static String schemaColumnTypeName(final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final EClassifier type = feature.getEType();
      String _switchResult = null;
      String _name = type.getName();
      boolean _matched = false;
      if (Objects.equal(_name, "EInt")) {
        _matched=true;
        _switchResult = "Int";
      }
      if (!_matched) {
        if (Objects.equal(_name, "EBoolean")) {
          _matched=true;
          _switchResult = "Boolean";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "EString")) {
          _matched=true;
          _switchResult = "String";
        }
      }
      if (!_matched) {
        if ((type instanceof EClass)) {
          _matched=true;
          String _name_1 = type.getName();
          String _plus = ("UUID (Foreign Key for: OML " + _name_1);
          _switchResult = (_plus + ")");
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "UUID")) {
          _matched=true;
          _switchResult = "UUID (Primary Key)";
        }
      }
      if (!_matched) {
        _switchResult = type.getName();
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public static String schemaColumnTypeDescription(final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final String columnTypeName = OMLUtilities.schemaColumnTypeName(feature);
      String _xifexpression = null;
      int _lowerBound = feature.getLowerBound();
      boolean _equals = (_lowerBound == 0);
      if (_equals) {
        _xifexpression = (("Option[" + columnTypeName) + "]");
      } else {
        _xifexpression = columnTypeName;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String constructorTypeName(final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final String scalaType = OMLUtilities.scalaTableTypeName(feature);
      String _xifexpression = null;
      int _lowerBound = feature.getLowerBound();
      boolean _equals = (_lowerBound == 0);
      if (_equals) {
        _xifexpression = (("scala.Option[" + scalaType) + "]");
      } else {
        _xifexpression = scalaType;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String javaArgName(final ETypedElement feature) {
    String _xifexpression = null;
    int _lowerBound = feature.getLowerBound();
    boolean _equals = (_lowerBound == 0);
    if (_equals) {
      String _columnName = OMLUtilities.columnName(feature);
      _xifexpression = (_columnName + ".asScala");
    } else {
      _xifexpression = OMLUtilities.columnName(feature);
    }
    return _xifexpression;
  }
  
  public static String javaTypeName(final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final String scalaType = OMLUtilities.scalaTableTypeName(feature);
      String _xifexpression = null;
      int _lowerBound = feature.getLowerBound();
      boolean _equals = (_lowerBound == 0);
      if (_equals) {
        _xifexpression = (("Optional[" + scalaType) + "]");
      } else {
        _xifexpression = scalaType;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String jsArgName(final ETypedElement feature) {
    String _xifexpression = null;
    int _lowerBound = feature.getLowerBound();
    boolean _equals = (_lowerBound == 0);
    if (_equals) {
      String _columnName = OMLUtilities.columnName(feature);
      _xifexpression = (_columnName + ".toOption");
    } else {
      _xifexpression = OMLUtilities.columnName(feature);
    }
    return _xifexpression;
  }
  
  public static String jsTypeName(final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final String scalaType = OMLUtilities.scalaTableTypeName(feature);
      String _xifexpression = null;
      int _lowerBound = feature.getLowerBound();
      boolean _equals = (_lowerBound == 0);
      if (_equals) {
        _xifexpression = (("scala.scalajs.js.UndefOr[" + scalaType) + "]");
      } else {
        _xifexpression = scalaType;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String scalaTableTypeName(final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final EClassifier type = feature.getEType();
      String _switchResult = null;
      String _name = type.getName();
      boolean _matched = false;
      if (Objects.equal(_name, "EInt")) {
        _matched=true;
        _switchResult = "scala.Int";
      }
      if (!_matched) {
        if (Objects.equal(_name, "EBoolean")) {
          _matched=true;
          _switchResult = "scala.Boolean";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "EString")) {
          _matched=true;
          _switchResult = "scala.Predef.String";
        }
      }
      if (!_matched) {
        if ((type instanceof EClass)) {
          _matched=true;
          _switchResult = "UUID";
        }
      }
      if (!_matched) {
        _switchResult = type.getName();
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public static Iterable<EClass> FunctionalAPIClasses(final EPackage ePkg) {
    final Function1<EClass, Boolean> _function = new Function1<EClass, Boolean>() {
      public Boolean apply(final EClass it) {
        return OMLUtilities.isAPI(it);
      }
    };
    return IterableExtensions.<EClass>filter(Iterables.<EClass>filter(ePkg.getEClassifiers(), EClass.class), _function);
  }
  
  public static Boolean isFunctionalAPIWithOrderingKeys(final EClass eClass) {
    Boolean _isFunctionalAPI = OMLUtilities.isFunctionalAPI(eClass);
    boolean _not = (!(_isFunctionalAPI).booleanValue());
    if (_not) {
      return Boolean.valueOf(false);
    } else {
      final Iterable<ETypedElement> keys = OMLUtilities.orderingKeys(eClass);
      boolean _isEmpty = IterableExtensions.isEmpty(keys);
      if (_isEmpty) {
        return Boolean.valueOf(false);
      } else {
        return Boolean.valueOf(true);
      }
    }
  }
  
  public static Iterable<ETypedElement> orderingKeys(final EClass eClass) {
    final Function1<ETypedElement, Boolean> _function = new Function1<ETypedElement, Boolean>() {
      public Boolean apply(final ETypedElement it) {
        return OMLUtilities.isOrderingKey(it);
      }
    };
    return IterableExtensions.<ETypedElement>filter(OMLUtilities.functionalAPIOrOrderingKeyAttributes(eClass), _function);
  }
  
  public static Iterable<ETypedElement> functionalAPIOrOrderingKeyAttributes(final EClass eClass) {
    final Function1<ETypedElement, Boolean> _function = new Function1<ETypedElement, Boolean>() {
      public Boolean apply(final ETypedElement it) {
        return Boolean.valueOf(((!(OMLUtilities.isInterface(it)).booleanValue()) && ((OMLUtilities.isFunctionalAttributeOrReferenceExceptContainer(it)).booleanValue() || (OMLUtilities.isOrderingKey(it)).booleanValue())));
      }
    };
    return IterableExtensions.<ETypedElement>filter(OMLUtilities.functionalAPIOrOrderingKeyFeatures(eClass), _function);
  }
  
  public static Iterable<ETypedElement> functionalAPIOrOrderingKeyFeatures(final EClass eClass) {
    List<ETypedElement> _xblockexpression = null;
    {
      final Function1<EClass, Iterable<ETypedElement>> _function = new Function1<EClass, Iterable<ETypedElement>>() {
        public Iterable<ETypedElement> apply(final EClass it) {
          return OMLUtilities.ETypedElements(it);
        }
      };
      final Set<ETypedElement> features = IterableExtensions.<ETypedElement>toSet(Iterables.<ETypedElement>concat(IterableExtensions.<EClass, Iterable<ETypedElement>>map(OMLUtilities.selfAndAllSupertypes(eClass), _function)));
      OMLUtilities.OMLFeatureCompare _oMLFeatureCompare = new OMLUtilities.OMLFeatureCompare();
      final List<ETypedElement> sorted = IterableExtensions.<ETypedElement>sortWith(features, _oMLFeatureCompare);
      _xblockexpression = sorted;
    }
    return _xblockexpression;
  }
  
  public static Boolean hasSchemaOptionalAttributes(final EClass eClass) {
    final Function1<ETypedElement, Boolean> _function = new Function1<ETypedElement, Boolean>() {
      public Boolean apply(final ETypedElement a) {
        int _lowerBound = a.getLowerBound();
        return Boolean.valueOf((_lowerBound == 0));
      }
    };
    return Boolean.valueOf(IterableExtensions.<ETypedElement>exists(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function));
  }
  
  public static Iterable<ETypedElement> schemaAPIOrOrderingKeyAttributes(final EClass eClass) {
    final Function1<ETypedElement, Boolean> _function = new Function1<ETypedElement, Boolean>() {
      public Boolean apply(final ETypedElement it) {
        return Boolean.valueOf(((!(OMLUtilities.isInterface(it)).booleanValue()) && ((OMLUtilities.isSchemaAttributeOrReferenceOrContainer(it)).booleanValue() || (OMLUtilities.isOrderingKey(it)).booleanValue())));
      }
    };
    return IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyFeatures(eClass), _function);
  }
  
  public static Iterable<ETypedElement> schemaAPIOrOrderingKeyFeatures(final EClass eClass) {
    List<ETypedElement> _xblockexpression = null;
    {
      final Function1<EClass, Iterable<ETypedElement>> _function = new Function1<EClass, Iterable<ETypedElement>>() {
        public Iterable<ETypedElement> apply(final EClass it) {
          return OMLUtilities.ETypedElements(it);
        }
      };
      final Set<ETypedElement> features = IterableExtensions.<ETypedElement>toSet(Iterables.<ETypedElement>concat(IterableExtensions.<EClass, Iterable<ETypedElement>>map(OMLUtilities.selfAndAllSupertypes(eClass), _function)));
      OMLUtilities.OMLFeatureCompare _oMLFeatureCompare = new OMLUtilities.OMLFeatureCompare();
      final List<ETypedElement> sorted = IterableExtensions.<ETypedElement>sortWith(features, _oMLFeatureCompare);
      _xblockexpression = sorted;
    }
    return _xblockexpression;
  }
  
  public static Iterable<ETypedElement> ETypedElements(final EClass eClass) {
    HashSet<ETypedElement> _xblockexpression = null;
    {
      final HashSet<ETypedElement> features = new HashSet<ETypedElement>();
      final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
        public Boolean apply(final EStructuralFeature it) {
          return OMLUtilities.isFunctionalAPIOrOrderingKey(it);
        }
      };
      Iterables.<ETypedElement>addAll(features, IterableExtensions.<EStructuralFeature>filter(eClass.getEStructuralFeatures(), _function));
      final Function1<EOperation, Boolean> _function_1 = new Function1<EOperation, Boolean>() {
        public Boolean apply(final EOperation it) {
          return OMLUtilities.isFunctionalAPIOrOrderingKey(it);
        }
      };
      Iterables.<ETypedElement>addAll(features, IterableExtensions.<EOperation>filter(eClass.getEOperations(), _function_1));
      _xblockexpression = features;
    }
    return _xblockexpression;
  }
  
  public static Iterable<EClass> selfAndAllSupertypes(final EClass eClass) {
    HashSet<EClass> _xblockexpression = null;
    {
      EList<EClass> _eAllSuperTypes = eClass.getEAllSuperTypes();
      final HashSet<EClass> parents = new HashSet<EClass>(_eAllSuperTypes);
      parents.add(eClass);
      _xblockexpression = parents;
    }
    return _xblockexpression;
  }
  
  public static Iterable<EClass> ESuperClasses(final EClass eClass) {
    final Function1<EClass, String> _function = new Function1<EClass, String>() {
      public String apply(final EClass it) {
        return it.getName();
      }
    };
    return IterableExtensions.<EClass, String>sortBy(eClass.getESuperTypes(), _function);
  }
  
  public static Iterable<EClass> ESpecificClasses(final EClass eClass) {
    final Function1<EClass, Boolean> _function = new Function1<EClass, Boolean>() {
      public Boolean apply(final EClass it) {
        return Boolean.valueOf(it.getESuperTypes().contains(eClass));
      }
    };
    final Function1<EClass, String> _function_1 = new Function1<EClass, String>() {
      public String apply(final EClass it) {
        return it.getName();
      }
    };
    return IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(eClass.getEPackage().getEClassifiers(), EClass.class), _function), _function_1);
  }
  
  public static Iterable<EStructuralFeature> APIStructuralFeatures(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
      public Boolean apply(final EStructuralFeature it) {
        return Boolean.valueOf(((OMLUtilities.isAPI(it)).booleanValue() && (OMLUtilities.isFunctionalAttributeOrReferenceExceptContainer(it)).booleanValue()));
      }
    };
    return IterableExtensions.<EStructuralFeature>filter(eClass.getEStructuralFeatures(), _function);
  }
  
  public static Boolean isRootHierarchyClass(final EClass eClass) {
    return Boolean.valueOf(((eClass.isAbstract() && eClass.getESuperTypes().isEmpty()) && (!IterableExtensions.isEmpty(OMLUtilities.orderingKeys(eClass)))));
  }
  
  public static Boolean isSpecializationOfRootClass(final EClass eClass) {
    return Boolean.valueOf(((!eClass.getESuperTypes().isEmpty()) && IterableExtensions.<EClass>exists(OMLUtilities.selfAndAllSupertypes(eClass), new Function1<EClass, Boolean>() {
      public Boolean apply(final EClass it) {
        return OMLUtilities.isRootHierarchyClass(it);
      }
    })));
  }
  
  public static Iterable<EOperation> APIOperations(final EClass eClass) {
    final Function1<EOperation, Boolean> _function = new Function1<EOperation, Boolean>() {
      public Boolean apply(final EOperation it) {
        return OMLUtilities.isAPI(it);
      }
    };
    return IterableExtensions.<EOperation>filter(eClass.getEOperations(), _function);
  }
  
  public static Iterable<EStructuralFeature> getSortedDerivedAttributeSignature(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
      public Boolean apply(final EStructuralFeature it) {
        return Boolean.valueOf(it.isDerived());
      }
    };
    return IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeSignature(eClass), _function);
  }
  
  public static Iterable<EStructuralFeature> getSortedAttributeFactorySignature(final EClass eClass) {
    final Function1<EClass, Iterable<EStructuralFeature>> _function = new Function1<EClass, Iterable<EStructuralFeature>>() {
      public Iterable<EStructuralFeature> apply(final EClass it) {
        final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
          public Boolean apply(final EStructuralFeature it) {
            return Boolean.valueOf(((((OMLUtilities.isAPI(it)).booleanValue() && (!(OMLUtilities.isContainment(it)).booleanValue())) && (!it.isDerived())) && (!(OMLUtilities.isUUID(it)).booleanValue())));
          }
        };
        return IterableExtensions.<EStructuralFeature>filter(it.getEStructuralFeatures(), _function);
      }
    };
    Iterable<EStructuralFeature> _flatten = Iterables.<EStructuralFeature>concat(IterableExtensions.<EClass, Iterable<EStructuralFeature>>map(OMLUtilities.selfAndAllSupertypes(eClass), _function));
    OMLUtilities.OMLFeatureCompare _oMLFeatureCompare = new OMLUtilities.OMLFeatureCompare();
    return IterableExtensions.<EStructuralFeature>sortWith(_flatten, _oMLFeatureCompare);
  }
  
  public static EStructuralFeature lookupUUIDFeature(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
      public Boolean apply(final EStructuralFeature it) {
        return OMLUtilities.isUUID(it);
      }
    };
    return IterableExtensions.<EStructuralFeature>findFirst(OMLUtilities.getSortedAttributeSignature(eClass), _function);
  }
  
  public static ETypedElement lookupUUIDTypedElement(final EClass eClass) {
    ETypedElement _xblockexpression = null;
    {
      final BasicEList<ETypedElement> typedElements = new BasicEList<ETypedElement>();
      Iterables.<ETypedElement>addAll(typedElements, OMLUtilities.getSortedAttributeSignature(eClass));
      final Function1<EOperation, Boolean> _function = new Function1<EOperation, Boolean>() {
        public Boolean apply(final EOperation it) {
          Boolean _isOverride = OMLUtilities.isOverride(it);
          return Boolean.valueOf((!(_isOverride).booleanValue()));
        }
      };
      Iterables.<ETypedElement>addAll(typedElements, IterableExtensions.<EOperation>filter(Iterables.<EOperation>filter(OMLUtilities.selfAndAllSupertypes(eClass), EOperation.class), _function));
      final Function1<ETypedElement, Boolean> _function_1 = new Function1<ETypedElement, Boolean>() {
        public Boolean apply(final ETypedElement it) {
          return OMLUtilities.isUUID(it);
        }
      };
      _xblockexpression = IterableExtensions.<ETypedElement>findFirst(typedElements, _function_1);
    }
    return _xblockexpression;
  }
  
  public static Iterable<EStructuralFeature> getSortedAttributeSignatureExceptDerived(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
      public Boolean apply(final EStructuralFeature it) {
        boolean _isDerived = it.isDerived();
        return Boolean.valueOf((!_isDerived));
      }
    };
    return IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeSignature(eClass), _function);
  }
  
  public static Iterable<EStructuralFeature> getSortedAttributeSignature(final EClass eClass) {
    final Function1<EClass, Iterable<EStructuralFeature>> _function = new Function1<EClass, Iterable<EStructuralFeature>>() {
      public Iterable<EStructuralFeature> apply(final EClass it) {
        return OMLUtilities.APIStructuralFeatures(it);
      }
    };
    Iterable<EStructuralFeature> _flatten = Iterables.<EStructuralFeature>concat(IterableExtensions.<EClass, Iterable<EStructuralFeature>>map(OMLUtilities.selfAndAllSupertypes(eClass), _function));
    OMLUtilities.OMLFeatureCompare _oMLFeatureCompare = new OMLUtilities.OMLFeatureCompare();
    return IterableExtensions.<EStructuralFeature>sortWith(_flatten, _oMLFeatureCompare);
  }
  
  public static Iterable<EStructuralFeature> lookupCopyConstructorArguments(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
      public Boolean apply(final EStructuralFeature it) {
        return OMLUtilities.isCopyConstructorArgument(it);
      }
    };
    return IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeSignature(eClass), _function);
  }
  
  public static Boolean isUUIDFeature(final EStructuralFeature sf) {
    EClass _EClassType = OMLUtilities.EClassType(sf);
    ETypedElement _lookupUUIDTypedElement = null;
    if (_EClassType!=null) {
      _lookupUUIDTypedElement=OMLUtilities.lookupUUIDTypedElement(_EClassType);
    }
    return Boolean.valueOf((null != _lookupUUIDTypedElement));
  }
  
  public static Boolean isUUIDDerived(final EClass e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/DerivedUUID");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static EStructuralFeature lookupUUIDNamespaceFeature(final EClass e) {
    EStructuralFeature _xblockexpression = null;
    {
      final Function1<EClass, String> _function = new Function1<EClass, String>() {
        public String apply(final EClass it) {
          EAnnotation _eAnnotation = it.getEAnnotation("http://imce.jpl.nasa.gov/oml/NamespaceUUID");
          EMap<String, String> _details = null;
          if (_eAnnotation!=null) {
            _details=_eAnnotation.getDetails();
          }
          String _get = null;
          if (_details!=null) {
            _get=_details.get("namespace");
          }
          return _get;
        }
      };
      final Iterable<String> ns = IterableExtensions.<String>filterNull(IterableExtensions.<EClass, String>map(OMLUtilities.selfAndAllSupertypes(e), _function));
      final Function1<EStructuralFeature, Boolean> _function_1 = new Function1<EStructuralFeature, Boolean>() {
        public Boolean apply(final EStructuralFeature it) {
          String _name = it.getName();
          String _head = null;
          if (ns!=null) {
            _head=IterableExtensions.<String>head(ns);
          }
          return Boolean.valueOf(Objects.equal(_name, _head));
        }
      };
      _xblockexpression = IterableExtensions.<EStructuralFeature>findFirst(OMLUtilities.getSortedAttributeFactorySignature(e), _function_1);
    }
    return _xblockexpression;
  }
  
  public static Iterable<EStructuralFeature> lookupUUIDNamespaceFactors(final EClass e) {
    final Function1<EClass, Iterable<EStructuralFeature>> _function = new Function1<EClass, Iterable<EStructuralFeature>>() {
      public Iterable<EStructuralFeature> apply(final EClass eClass) {
        Iterable<EStructuralFeature> _xblockexpression = null;
        {
          EAnnotation _eAnnotation = eClass.getEAnnotation("http://imce.jpl.nasa.gov/oml/NamespaceUUID");
          EMap<String, String> _details = null;
          if (_eAnnotation!=null) {
            _details=_eAnnotation.getDetails();
          }
          String _get = null;
          if (_details!=null) {
            _get=_details.get("factors");
          }
          final String factors = _get;
          Iterable<EStructuralFeature> _xifexpression = null;
          if ((null == factors)) {
            _xifexpression = new BasicEList<EStructuralFeature>();
          } else {
            Iterable<EStructuralFeature> _xblockexpression_1 = null;
            {
              final ArrayList<String> factoredFeatures = new ArrayList<String>();
              CollectionExtensions.<String>addAll(factoredFeatures, factors.split(","));
              final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
                public Boolean apply(final EStructuralFeature s) {
                  final Function1<String, Boolean> _function = new Function1<String, Boolean>() {
                    public Boolean apply(final String f) {
                      String _name = s.getName();
                      return Boolean.valueOf(Objects.equal(f, _name));
                    }
                  };
                  return Boolean.valueOf(IterableExtensions.<String>exists(factoredFeatures, _function));
                }
              };
              _xblockexpression_1 = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function);
            }
            _xifexpression = _xblockexpression_1;
          }
          _xblockexpression = _xifexpression;
        }
        return _xblockexpression;
      }
    };
    return Iterables.<EStructuralFeature>concat(IterableExtensions.<EClass, Iterable<EStructuralFeature>>map(OMLUtilities.selfAndAllSupertypes(e), _function));
  }
  
  public static Boolean isCopyConstructorArgument(final EStructuralFeature attribute) {
    EAnnotation _eAnnotation = attribute.getEAnnotation("http://imce.jpl.nasa.gov/oml/CopyConstructor");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Iterable<EOperation> ScalaOperations(final EClass eClass) {
    final Function1<EOperation, Boolean> _function = new Function1<EOperation, Boolean>() {
      public Boolean apply(final EOperation op) {
        return Boolean.valueOf(((OMLUtilities.isScala(op)).booleanValue() || (null != OMLUtilities.xExpressions(op))));
      }
    };
    return IterableExtensions.<EOperation>filter(OMLUtilities.APIOperations(eClass), _function);
  }
  
  public static Iterable<XExpression> xExpressions(final EOperation op) {
    XOperation _xOperation = new XcoreMapper().getXOperation(op);
    XBlockExpression _body = null;
    if (_xOperation!=null) {
      _body=_xOperation.getBody();
    }
    EList<XExpression> _expressions = null;
    if (_body!=null) {
      _expressions=_body.getExpressions();
    }
    return _expressions;
  }
  
  public static String queryBody(final EOperation op) {
    String _xblockexpression = null;
    {
      final String scalaCode = OMLUtilities.scalaAnnotation(op);
      final Iterable<XExpression> xExpressions = OMLUtilities.xExpressions(op);
      String _xifexpression = null;
      if ((null != scalaCode)) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("{");
        _builder.newLine();
        _builder.append("  ");
        _builder.append(scalaCode, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("}");
        _xifexpression = _builder.toString();
      } else {
        String _xifexpression_1 = null;
        if ((null != xExpressions)) {
          StringConcatenation _builder_1 = new StringConcatenation();
          {
            boolean _hasElements = false;
            for(final XExpression exp : xExpressions) {
              if (!_hasElements) {
                _hasElements = true;
                _builder_1.append("{\n  ");
              } else {
                _builder_1.appendImmediate("\n  ", "");
              }
              String _scala = OMLUtilities.toScala(exp);
              _builder_1.append(_scala);
            }
            if (_hasElements) {
              _builder_1.append("\n}");
            }
          }
          _xifexpression_1 = _builder_1.toString();
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  /**
   * Transform an XText base XExpression to an equivalent Scala expression in concrete syntax (String).
   */
  public static String toScala(final XExpression exp) {
    String _xblockexpression = null;
    {
      String _switchResult = null;
      boolean _matched = false;
      if (exp instanceof XFeatureCall) {
        _matched=true;
        String _xblockexpression_1 = null;
        {
          final ICompositeNode n = NodeModelUtils.findActualNodeFor(exp);
          final String s = NodeModelUtils.getTokenText(n);
          _xblockexpression_1 = s;
        }
        _switchResult = _xblockexpression_1;
      }
      if (!_matched) {
        if (exp instanceof XMemberFeatureCall) {
          _matched=true;
          String _xblockexpression_1 = null;
          {
            final XExpression rF = ((XMemberFeatureCall)exp).getActualReceiver();
            final String rS = OMLUtilities.toScala(rF);
            boolean _isEmpty = ((XMemberFeatureCall)exp).getActualArguments().isEmpty();
            boolean _not = (!_isEmpty);
            if (_not) {
              String _string = ((XMemberFeatureCall)exp).toString();
              String _plus = (".toScala can only handle an XMemberFeatureCall for calling an operation with 0 arguments: " + _string);
              String _plus_1 = (_plus + " in: ");
              URI _uRI = ((XMemberFeatureCall)exp).eResource().getURI();
              String _plus_2 = (_plus_1 + _uRI);
              throw new IllegalArgumentException(_plus_2);
            }
            final JvmIdentifiableElement tF = ((XMemberFeatureCall)exp).getFeature();
            boolean _eIsProxy = tF.eIsProxy();
            if (_eIsProxy) {
              String _string_1 = ((XMemberFeatureCall)exp).toString();
              String _plus_3 = ("Cannot resolve an XMemberFeatureCall because the feature is a proxy; expression=" + _string_1);
              String _plus_4 = (_plus_3 + " in: ");
              URI _uRI_1 = ((XMemberFeatureCall)exp).eResource().getURI();
              String _plus_5 = (_plus_4 + _uRI_1);
              throw new IllegalArgumentException(_plus_5);
            }
            final String tS = tF.getSimpleName();
            final String s = (((rS + ".") + tS) + "()");
            _xblockexpression_1 = s;
          }
          _switchResult = _xblockexpression_1;
        }
      }
      if (!_matched) {
        String _xifexpression = null;
        if ((null == exp)) {
          _xifexpression = "null /* ERROR!!! */";
        } else {
          String _string = exp.toString();
          _xifexpression = (_string + "/* default(debug) */");
        }
        _switchResult = _xifexpression;
      }
      final String result = _switchResult;
      _xblockexpression = result;
    }
    return _xblockexpression;
  }
  
  public static String queryBody(final EStructuralFeature f) {
    String _xblockexpression = null;
    {
      final String scalaCode = OMLUtilities.scalaAnnotation(f);
      String _xifexpression = null;
      if ((null != scalaCode)) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("{");
        _builder.newLine();
        _builder.append("  ");
        _builder.append(scalaCode, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("}");
        _xifexpression = _builder.toString();
      } else {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("// N/A");
        _xifexpression = _builder_1.toString();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static Boolean isUUID(final ETypedElement e) {
    String _name = e.getName();
    return Boolean.valueOf(Objects.equal(_name, "uuid"));
  }
  
  public static Boolean isFunctionalAPIOrOrderingKey(final ENamedElement e) {
    return Boolean.valueOf(((OMLUtilities.isFunctionalAPI(e)).booleanValue() || (OMLUtilities.isOrderingKey(e)).booleanValue()));
  }
  
  public static Boolean isFunctionalAPI(final ENamedElement e) {
    boolean _xifexpression = false;
    if (((OMLUtilities.isSchema(e)).booleanValue() && (OMLUtilities.isAPI(e)).booleanValue())) {
      boolean _switchResult = false;
      boolean _matched = false;
      if (e instanceof EClass) {
        _matched=true;
        boolean _isAbstract = ((EClass)e).isAbstract();
        _switchResult = (!_isAbstract);
      }
      if (!_matched) {
        _switchResult = true;
      }
      _xifexpression = _switchResult;
    } else {
      _xifexpression = false;
    }
    return Boolean.valueOf(_xifexpression);
  }
  
  public static Boolean isContainment(final ETypedElement f) {
    boolean _switchResult = false;
    boolean _matched = false;
    if (f instanceof EReference) {
      _matched=true;
      _switchResult = (((EReference)f).isContainment() && (OMLUtilities.isAPI(f)).booleanValue());
    }
    if (!_matched) {
      _switchResult = false;
    }
    return Boolean.valueOf(_switchResult);
  }
  
  public static Boolean isContainer(final ETypedElement f) {
    boolean _switchResult = false;
    boolean _matched = false;
    if (f instanceof EReference) {
      _matched=true;
      _switchResult = (((EReference)f).isContainer() && (OMLUtilities.isAPI(f)).booleanValue());
    }
    if (!_matched) {
      _switchResult = false;
    }
    return Boolean.valueOf(_switchResult);
  }
  
  public static Boolean isFunctionalAttributeOrReferenceExceptContainer(final ETypedElement f) {
    return Boolean.valueOf(((!(OMLUtilities.isContainer(f)).booleanValue()) && (!(OMLUtilities.isContainment(f)).booleanValue())));
  }
  
  public static Boolean isSchemaAttributeOrReferenceOrContainer(final ETypedElement f) {
    Boolean _switchResult = null;
    boolean _matched = false;
    if (f instanceof EReference) {
      _matched=true;
      _switchResult = Boolean.valueOf(((OMLUtilities.isSchema(f)).booleanValue() && (!((EReference)f).isContainment())));
    }
    if (!_matched) {
      _switchResult = OMLUtilities.isSchema(f);
    }
    return _switchResult;
  }
  
  public static EClass EClassType(final ETypedElement f) {
    EClass _xblockexpression = null;
    {
      final EClassifier c = f.getEType();
      EClass _switchResult = null;
      boolean _matched = false;
      if (c instanceof EClass) {
        _matched=true;
        _switchResult = ((EClass)c);
      }
      if (!_matched) {
        _switchResult = null;
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public static EClass EClassContainer(final ETypedElement f) {
    EClass _xblockexpression = null;
    {
      final EObject c = f.eContainer();
      EClass _switchResult = null;
      boolean _matched = false;
      if (c instanceof EClass) {
        _matched=true;
        _switchResult = ((EClass)c);
      }
      if (!_matched) {
        _switchResult = null;
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public static Boolean isClassFeature(final ETypedElement feature) {
    boolean _xblockexpression = false;
    {
      final EClassifier type = feature.getEType();
      _xblockexpression = (type instanceof EClass);
    }
    return Boolean.valueOf(_xblockexpression);
  }
  
  public static Boolean isOrderingKey(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/IsOrderingKey");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isOverride(final ETypedElement feature) {
    EAnnotation _eAnnotation = feature.getEAnnotation("http://imce.jpl.nasa.gov/oml/Override");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isOO(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/NotFunctionalAPI");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isInterface(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/FunctionalInterface");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isAPI(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/NotFunctionalAPI");
    return Boolean.valueOf((null == _eAnnotation));
  }
  
  public static Boolean isExtentContainer(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ExtentContainer");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isExtentManaged(final EClass e) {
    final Function1<EClass, Boolean> _function = new Function1<EClass, Boolean>() {
      public Boolean apply(final EClass eClass) {
        EAnnotation _eAnnotation = eClass.getEAnnotation("http://imce.jpl.nasa.gov/oml/ExtentManaged");
        return Boolean.valueOf((null != _eAnnotation));
      }
    };
    return Boolean.valueOf(IterableExtensions.<EClass>exists(OMLUtilities.selfAndAllSupertypes(e), _function));
  }
  
  public static Boolean isGlossary(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/Glossary");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isImplicitExtent(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ImplicitExtent");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isScala(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/Scala");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static String scalaAnnotation(final ETypedElement f) {
    EAnnotation _eAnnotation = f.getEAnnotation("http://imce.jpl.nasa.gov/oml/Scala");
    EMap<String, String> _details = null;
    if (_eAnnotation!=null) {
      _details=_eAnnotation.getDetails();
    }
    String _get = null;
    if (_details!=null) {
      _get=_details.get("code");
    }
    return _get;
  }
  
  public static Boolean isSchema(final ENamedElement e) {
    return Boolean.valueOf(((null == e.getEAnnotation("http://imce.jpl.nasa.gov/oml/NotSchema")) && (!(OMLUtilities.isResolverAPI(e)).booleanValue())));
  }
  
  public static Boolean isResolverAPI(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ResolverAPI");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isValueTable(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ValueTable");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static String pluralizeIfMany(final String s, final int cardinality) {
    String _xifexpression = null;
    if ((cardinality > 1)) {
      _xifexpression = OMLUtilities.pluralize(s);
    } else {
      _xifexpression = s;
    }
    return _xifexpression;
  }
  
  public static String pluralize(final String s) {
    String _xifexpression = null;
    boolean _endsWith = s.endsWith("y");
    if (_endsWith) {
      int _length = s.length();
      int _minus = (_length - 1);
      String _substring = s.substring(0, _minus);
      _xifexpression = (_substring + "ies");
    } else {
      String _xifexpression_1 = null;
      boolean _endsWith_1 = s.endsWith("x");
      if (_endsWith_1) {
        _xifexpression_1 = (s + "es");
      } else {
        String _xifexpression_2 = null;
        boolean _endsWith_2 = s.endsWith("s");
        if (_endsWith_2) {
          _xifexpression_2 = s;
        } else {
          _xifexpression_2 = (s + "s");
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
  
  public static String tableVariableName(final EClass eClass) {
    String _xblockexpression = null;
    {
      final String n = eClass.getName();
      String _xifexpression = null;
      boolean _startsWith = n.startsWith("IRI");
      if (_startsWith) {
        String _pluralize = OMLUtilities.pluralize(n.substring(3));
        _xifexpression = ("iri" + _pluralize);
      } else {
        String _xblockexpression_1 = null;
        {
          final Matcher m = Pattern.compile("^(\\p{Upper}+)(\\w+)$").matcher(n);
          boolean _matches = m.matches();
          boolean _not = (!_matches);
          if (_not) {
            String _name = eClass.getName();
            String _plus = ("tableVariableName needs a class whose name begins with uppercase characters: " + _name);
            throw new IllegalArgumentException(_plus);
          }
          String _lowerCase = m.group(1).toLowerCase();
          String _pluralize_1 = OMLUtilities.pluralize(m.group(2));
          _xblockexpression_1 = (_lowerCase + _pluralize_1);
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String columnName(final ETypedElement feature) {
    String _xifexpression = null;
    if ((feature instanceof EReference)) {
      String _name = ((EReference)feature).getName();
      _xifexpression = (_name + "UUID");
    } else {
      _xifexpression = feature.getName();
    }
    return _xifexpression;
  }
  
  public static String markDown(final ENamedElement e) {
    String _xblockexpression = null;
    {
      String _elvis = null;
      EAnnotation _eAnnotation = e.getEAnnotation("http://www.eclipse.org/emf/2002/GenModel");
      EMap<String, String> _details = null;
      if (_eAnnotation!=null) {
        _details=_eAnnotation.getDetails();
      }
      String _get = null;
      if (_details!=null) {
        _get=_details.get("documentation");
      }
      if (_get != null) {
        _elvis = _get;
      } else {
        _elvis = "";
      }
      final String doc = _elvis;
      _xblockexpression = doc;
    }
    return _xblockexpression;
  }
  
  public static String doc(final ENamedElement e, final String indent) {
    String _xblockexpression = null;
    {
      String _elvis = null;
      EAnnotation _eAnnotation = e.getEAnnotation("http://www.eclipse.org/emf/2002/GenModel");
      EMap<String, String> _details = null;
      if (_eAnnotation!=null) {
        _details=_eAnnotation.getDetails();
      }
      String _get = null;
      if (_details!=null) {
        _get=_details.get("documentation");
      }
      if (_get != null) {
        _elvis = _get;
      } else {
        _elvis = "";
      }
      final String doc = _elvis;
      String _xifexpression = null;
      boolean _isEmpty = doc.isEmpty();
      if (_isEmpty) {
        _xifexpression = doc;
      } else {
        String _replaceAll = doc.replaceAll("\n", (("\n" + indent) + " * "));
        String _plus = ((("/*\n" + indent) + " * ") + _replaceAll);
        String _plus_1 = (_plus + "\n");
        String _plus_2 = (_plus_1 + indent);
        String _plus_3 = (_plus_2 + " */\n");
        _xifexpression = (_plus_3 + indent);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String copyright() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("/*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* Copyright 2016 California Institute of Technology (\"Caltech\").");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* U.S. Government sponsorship acknowledged.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* Licensed under the Apache License, Version 2.0 (the \"License\");");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* you may not use this file except in compliance with the License.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* You may obtain a copy of the License at");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*     http://www.apache.org/licenses/LICENSE-2.0");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* Unless required by applicable law or agreed to in writing, software");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* distributed under the License is distributed on an \"AS IS\" BASIS,");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* See the License for the specific language governing permissions and");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* limitations under the License.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* License Terms");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*/");
    _builder.newLine();
    _builder.newLine();
    return _builder.toString();
  }
}
