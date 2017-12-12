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

@SuppressWarnings("all")
public class OMLUtilities extends OMLXcorePackages {
  public static class OMLTableCompare implements Comparator<EClass> {
    private final List<String> knownTables = Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList("Annotation", "AnnotationEntry", "AnnotationProperty", "AnnotationPropertyTable", "AnnotationSubjectPropertyValue", "AnnotationSubjectTable", "Extent", "TerminologyGraph", "Bundle", "ConceptDesignationTerminologyAxiom", "TerminologyExtensionAxiom", "TerminologyNestingAxiom", "Aspect", "Concept", "ReifiedRelationship", "UnreifiedRelationship", "Scalar", "Structure", "BinaryScalarRestriction", "IRIScalarRestriction", "NumericScalarRestriction", "PlainLiteralScalarRestriction", "ScalarOneOfRestriction", "StringScalarRestriction", "SynonymScalarRestriction", "TimeScalarRestriction", "EntityScalarDataProperty", "EntityStructuredDataProperty", "ScalarDataProperty", "StructuredDataProperty", "AspectSpecializationAxiom", "ConceptSpecializationAxiom", "ReifiedRelationshipSpecializationAxiom", "EntityExistentialRestrictionAxiom", "EntityUniversalRestrictionAxiom", "EntityScalarDataPropertyExistentialRestrictionAxiom", "EntityScalarDataPropertyParticularRestrictionAxiom", "EntityScalarDataPropertyUniversalRestrictionAxiom", "ScalarOneOfLiteralAxiom", "BundledTerminologyAxiom", "AnonymousConceptTaxonomyAxiom", "RootConceptTaxonomyAxiom", "SpecificDisjointConceptAxiom"));
    
    @Override
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
    
    @Override
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
    if (((IterableExtensions.<ETypedElement>exists(OMLUtilities.orderingKeys(eClass), ((Function1<ETypedElement, Boolean>) (ETypedElement f) -> {
      return Boolean.valueOf(((OMLUtilities.isContainer(f)).booleanValue() || (f.getLowerBound() == 0)));
    })) || Objects.equal(eClass.getName(), "AnnotationEntry")) || Objects.equal(eClass.getName(), "Annotation"))) {
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
                if (_elvis != null) {
                  switch (_elvis) {
                    case "Map(Seq)":
                      String _xblockexpression_2 = null;
                      {
                        final String key = ann.get("key");
                        String _name = type.getName();
                        String _plus = (((("scala.collection.immutable.Map[" + key) + ", scala.collection.immutable.Seq[") + typePrefix) + _name);
                        _xblockexpression_2 = (_plus + "]]");
                      }
                      _switchResult_1 = _xblockexpression_2;
                      break;
                    case "Map":
                      String _xblockexpression_3 = null;
                      {
                        final String key = ann.get("key");
                        String _name = type.getName();
                        String _plus = (((("scala.collection.immutable.Map[" + key) + ", ") + typePrefix) + _name);
                        _xblockexpression_3 = (_plus + "]");
                      }
                      _switchResult_1 = _xblockexpression_3;
                      break;
                    case "Set":
                      String _name = type.getName();
                      String _plus = (("scala.collection.immutable.Set[_ <: " + typePrefix) + _name);
                      _switchResult_1 = (_plus + "]");
                      break;
                    case "SortedSet":
                      String _name_1 = type.getName();
                      String _plus_1 = (("scala.collection.immutable.SortedSet[" + typePrefix) + _name_1);
                      _switchResult_1 = (_plus_1 + "]");
                      break;
                    default:
                      String _name_2 = OMLUtilities.EClassContainer(feature).getName();
                      String _plus_2 = ("Multi-valued operation: " + _name_2);
                      String _plus_3 = (_plus_2 + ".");
                      String _name_3 = feature.getName();
                      String _plus_4 = (_plus_3 + _name_3);
                      String _plus_5 = (_plus_4 + " needs a @Collection(...) annotation!");
                      throw new IllegalArgumentException(_plus_5);
                  }
                } else {
                  String _name_2 = OMLUtilities.EClassContainer(feature).getName();
                  String _plus_2 = ("Multi-valued operation: " + _name_2);
                  String _plus_3 = (_plus_2 + ".");
                  String _name_3 = feature.getName();
                  String _plus_4 = (_plus_3 + _name_3);
                  String _plus_5 = (_plus_4 + " needs a @Collection(...) annotation!");
                  throw new IllegalArgumentException(_plus_5);
                }
                _xblockexpression_1 = _switchResult_1;
              }
              _xifexpression_2 = _xblockexpression_1;
            } else {
              String _xifexpression_3 = null;
              Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(feature);
              if ((_isLiteralFeature).booleanValue()) {
                _xifexpression_3 = (("scala.Option[" + scalaType) + "]");
              } else {
                String _name = type.getName();
                String _plus = (("scala.Option[" + typePrefix) + _name);
                _xifexpression_3 = (_plus + "]");
              }
              _xifexpression_2 = _xifexpression_3;
            }
            _xifexpression_1 = _xifexpression_2;
          } else {
            String _xifexpression_4 = null;
            Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
            if ((_isIRIReference).booleanValue()) {
              _xifexpression_4 = "gov.nasa.jpl.imce.oml.tables.taggedTypes.IRI";
            } else {
              String _xifexpression_5 = null;
              Boolean _isLiteralStringFeature = OMLUtilities.isLiteralStringFeature(feature);
              if ((_isLiteralStringFeature).booleanValue()) {
                _xifexpression_5 = "gov.nasa.jpl.imce.oml.tables.taggedTypes.StringDataType";
              } else {
                String _xifexpression_6 = null;
                Boolean _isLiteralFeature_1 = OMLUtilities.isLiteralFeature(feature);
                if ((_isLiteralFeature_1).booleanValue()) {
                  _xifexpression_6 = scalaType;
                } else {
                  String _name_1 = type.getName();
                  _xifexpression_6 = (typePrefix + _name_1);
                }
                _xifexpression_5 = _xifexpression_6;
              }
              _xifexpression_4 = _xifexpression_5;
            }
            _xifexpression_1 = _xifexpression_4;
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
      if (_name != null) {
        switch (_name) {
          case "EInt":
            _switchResult = "scala.Int";
            break;
          case "EBoolean":
            _switchResult = "scala.Boolean";
            break;
          case "EString":
            _switchResult = "scala.Predef.String";
            break;
          case "DescriptionKind":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.DescriptionKind";
            break;
          case "TerminologyKind":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.TerminologyKind";
            break;
          case "UUID":
            _switchResult = "java.util.UUID";
            break;
          case "AbbrevIRI":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.taggedTypes.AbbrevIRI";
            break;
          case "IRI":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.taggedTypes.IRI";
            break;
          case "LanguageTagDataType":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.taggedTypes.LanguageTagDataType";
            break;
          case "LiteralPattern":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.taggedTypes.LiteralPattern";
            break;
          case "LocalName":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.taggedTypes.LocalName";
            break;
          case "NamespacePrefix":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.taggedTypes.NamespacePrefix";
            break;
          case "PositiveIntegerLiteral":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.taggedTypes.PositiveIntegerLiteral";
            break;
          case "StringDataType":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.taggedTypes.StringDataType";
            break;
          case "LiteralDateTime":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.LiteralDateTime";
            break;
          case "LiteralNumber":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.LiteralNumber";
            break;
          case "LiteralValue":
            _switchResult = "gov.nasa.jpl.imce.oml.tables.LiteralValue";
            break;
          default:
            _switchResult = type.getName();
            break;
        }
      } else {
        _switchResult = type.getName();
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
          String _xifexpression = null;
          Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
          if ((_isIRIReference).booleanValue()) {
            String _name_1 = type.getName();
            String _plus = ("IRI (Foreign Key for: OML " + _name_1);
            _xifexpression = (_plus + ")");
          } else {
            String _name_2 = type.getName();
            String _plus_1 = ("UUID (Foreign Key for: OML " + _name_2);
            _xifexpression = (_plus_1 + ")");
          }
          _switchResult = _xifexpression;
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
  
  public static String constructorTypeRef(final EClass eClass, final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final String scalaType = OMLUtilities.scalaTableTypeRef(eClass, feature);
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
  
  public static String circeDecoder(final EClass eClass, final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final String scalaType = OMLUtilities.circeDecoderType(eClass, feature);
      String _xifexpression = null;
      int _lowerBound = feature.getLowerBound();
      boolean _equals = (_lowerBound == 0);
      if (_equals) {
        String _xifexpression_1 = null;
        if (((Objects.equal(scalaType, "LiteralNumber") || Objects.equal(scalaType, "LiteralValue")) || Objects.equal(scalaType, "LiteralDateTime"))) {
          String _columnName = OMLUtilities.columnName(feature);
          String _plus = ((((("Decoder.decodeOption(" + scalaType) + ".decode") + scalaType) + ")(c.downField(\"") + _columnName);
          _xifexpression_1 = (_plus + "\").success.get)");
        } else {
          String _columnName_1 = OMLUtilities.columnName(feature);
          String _plus_1 = ((("Decoder.decodeOption(taggedTypes.decode" + scalaType) + ")(c.downField(\"") + _columnName_1);
          _xifexpression_1 = (_plus_1 + "\").success.get)");
        }
        _xifexpression = _xifexpression_1;
      } else {
        String _xifexpression_2 = null;
        if (((Objects.equal(scalaType, "LiteralValue") || scalaType.endsWith("Kind")) || scalaType.startsWith("scala."))) {
          String _columnName_2 = OMLUtilities.columnName(feature);
          String _plus_2 = ("c.downField(\"" + _columnName_2);
          String _plus_3 = (_plus_2 + "\").as[");
          String _plus_4 = (_plus_3 + scalaType);
          _xifexpression_2 = (_plus_4 + "]");
        } else {
          String _columnName_3 = OMLUtilities.columnName(feature);
          String _plus_5 = ("c.downField(\"" + _columnName_3);
          String _plus_6 = (_plus_5 + "\").as[taggedTypes.");
          String _plus_7 = (_plus_6 + scalaType);
          _xifexpression_2 = (_plus_7 + "]");
        }
        _xifexpression = _xifexpression_2;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static String circeEncoder(final EClass eClass, final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final String scalaType = OMLUtilities.circeEncoderFunction(eClass, feature);
      String _xifexpression = null;
      int _lowerBound = feature.getLowerBound();
      boolean _equals = (_lowerBound == 0);
      if (_equals) {
        String _columnName = OMLUtilities.columnName(feature);
        String _plus = ((("Encoder.encodeOption(" + scalaType) + ").apply(x.") + _columnName);
        _xifexpression = (_plus + ")");
      } else {
        String _columnName_1 = OMLUtilities.columnName(feature);
        String _plus_1 = ((scalaType + "(x.") + _columnName_1);
        _xifexpression = (_plus_1 + ")");
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
  
  public static String circeDecoderType(final EClass eClass, final ETypedElement feature) {
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
        if (Objects.equal(_name, "DescriptionKind")) {
          _matched=true;
          _switchResult = "DescriptionKind";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "TerminologyKind")) {
          _matched=true;
          _switchResult = "TerminologyKind";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "UUID")) {
          _matched=true;
          String _name_1 = eClass.getName();
          _switchResult = (_name_1 + "UUID");
        }
      }
      if (!_matched) {
        if ((type instanceof EDataType)) {
          _matched=true;
          _switchResult = type.getName();
        }
      }
      if (!_matched) {
        if ((type instanceof EClass)) {
          _matched=true;
          String _xifexpression = null;
          Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
          if ((_isIRIReference).booleanValue()) {
            _xifexpression = "IRI";
          } else {
            String _xifexpression_1 = null;
            Boolean _isLiteralDateTimeFeature = OMLUtilities.isLiteralDateTimeFeature(feature);
            if ((_isLiteralDateTimeFeature).booleanValue()) {
              _xifexpression_1 = "LiteralDateTime";
            } else {
              String _xifexpression_2 = null;
              Boolean _isLiteralNumberFeature = OMLUtilities.isLiteralNumberFeature(feature);
              if ((_isLiteralNumberFeature).booleanValue()) {
                _xifexpression_2 = "LiteralNumber";
              } else {
                String _xifexpression_3 = null;
                Boolean _isLiteralStringFeature = OMLUtilities.isLiteralStringFeature(feature);
                if ((_isLiteralStringFeature).booleanValue()) {
                  _xifexpression_3 = "StringDataType";
                } else {
                  String _xifexpression_4 = null;
                  Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(feature);
                  if ((_isLiteralFeature).booleanValue()) {
                    _xifexpression_4 = "LiteralValue";
                  } else {
                    String _name_2 = type.getName();
                    _xifexpression_4 = (_name_2 + "UUID");
                  }
                  _xifexpression_3 = _xifexpression_4;
                }
                _xifexpression_2 = _xifexpression_3;
              }
              _xifexpression_1 = _xifexpression_2;
            }
            _xifexpression = _xifexpression_1;
          }
          _switchResult = _xifexpression;
        }
      }
      if (!_matched) {
        _switchResult = type.getName();
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public static String circeEncoderFunction(final EClass eClass, final ETypedElement feature) {
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
          _switchResult = "Encoder.encodeBoolean";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "EString")) {
          _matched=true;
          _switchResult = "scala.Predef.String";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "DescriptionKind")) {
          _matched=true;
          _switchResult = "DescriptionKind.encodeDescriptionKind";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "TerminologyKind")) {
          _matched=true;
          _switchResult = "TerminologyKind.encodeTerminologyKind";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "UUID")) {
          _matched=true;
          String _name_1 = eClass.getName();
          String _plus = ("taggedTypes.encode" + _name_1);
          _switchResult = (_plus + "UUID");
        }
      }
      if (!_matched) {
        if ((type instanceof EDataType)) {
          _matched=true;
          String _name_2 = type.getName();
          _switchResult = ("taggedTypes.encode" + _name_2);
        }
      }
      if (!_matched) {
        if ((type instanceof EClass)) {
          _matched=true;
          String _xifexpression = null;
          Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
          if ((_isIRIReference).booleanValue()) {
            _xifexpression = "taggedTypes.encodeIRI";
          } else {
            String _xifexpression_1 = null;
            Boolean _isLiteralDateTimeFeature = OMLUtilities.isLiteralDateTimeFeature(feature);
            if ((_isLiteralDateTimeFeature).booleanValue()) {
              _xifexpression_1 = "LiteralDateTime.encodeLiteralDateTime";
            } else {
              String _xifexpression_2 = null;
              Boolean _isLiteralNumberFeature = OMLUtilities.isLiteralNumberFeature(feature);
              if ((_isLiteralNumberFeature).booleanValue()) {
                _xifexpression_2 = "LiteralNumber.encodeLiteralNumber";
              } else {
                String _xifexpression_3 = null;
                Boolean _isLiteralStringFeature = OMLUtilities.isLiteralStringFeature(feature);
                if ((_isLiteralStringFeature).booleanValue()) {
                  _xifexpression_3 = "taggedTypes.encodeStringDataType";
                } else {
                  String _xifexpression_4 = null;
                  Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(feature);
                  if ((_isLiteralFeature).booleanValue()) {
                    _xifexpression_4 = "LiteralValue.encodeLiteralValue";
                  } else {
                    String _name_3 = type.getName();
                    String _plus_1 = ("taggedTypes.encode" + _name_3);
                    _xifexpression_4 = (_plus_1 + "UUID");
                  }
                  _xifexpression_3 = _xifexpression_4;
                }
                _xifexpression_2 = _xifexpression_3;
              }
              _xifexpression_1 = _xifexpression_2;
            }
            _xifexpression = _xifexpression_1;
          }
          _switchResult = _xifexpression;
        }
      }
      if (!_matched) {
        String _name_4 = type.getName();
        _switchResult = ("taggedTypes.encode" + _name_4);
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public static String scalaTableTypeRef(final EClass eClass, final ETypedElement feature) {
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
        if (Objects.equal(_name, "DescriptionKind")) {
          _matched=true;
          _switchResult = "DescriptionKind";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "TerminologyKind")) {
          _matched=true;
          _switchResult = "TerminologyKind";
        }
      }
      if (!_matched) {
        if (Objects.equal(_name, "UUID")) {
          _matched=true;
          String _name_1 = eClass.getName();
          String _plus = ("taggedTypes." + _name_1);
          _switchResult = (_plus + "UUID");
        }
      }
      if (!_matched) {
        if ((type instanceof EDataType)) {
          _matched=true;
          String _name_2 = type.getName();
          _switchResult = ("taggedTypes." + _name_2);
        }
      }
      if (!_matched) {
        if ((type instanceof EClass)) {
          _matched=true;
          String _xifexpression = null;
          Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
          if ((_isIRIReference).booleanValue()) {
            _xifexpression = "taggedTypes.IRI";
          } else {
            String _xifexpression_1 = null;
            Boolean _isLiteralDateTimeFeature = OMLUtilities.isLiteralDateTimeFeature(feature);
            if ((_isLiteralDateTimeFeature).booleanValue()) {
              _xifexpression_1 = "LiteralDateTime";
            } else {
              String _xifexpression_2 = null;
              Boolean _isLiteralNumberFeature = OMLUtilities.isLiteralNumberFeature(feature);
              if ((_isLiteralNumberFeature).booleanValue()) {
                _xifexpression_2 = "LiteralNumber";
              } else {
                String _xifexpression_3 = null;
                Boolean _isLiteralStringFeature = OMLUtilities.isLiteralStringFeature(feature);
                if ((_isLiteralStringFeature).booleanValue()) {
                  _xifexpression_3 = "taggedTypes.StringDataType";
                } else {
                  String _xifexpression_4 = null;
                  Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(feature);
                  if ((_isLiteralFeature).booleanValue()) {
                    _xifexpression_4 = "LiteralValue";
                  } else {
                    String _name_3 = type.getName();
                    String _plus_1 = ("taggedTypes." + _name_3);
                    _xifexpression_4 = (_plus_1 + "UUID");
                  }
                  _xifexpression_3 = _xifexpression_4;
                }
                _xifexpression_2 = _xifexpression_3;
              }
              _xifexpression_1 = _xifexpression_2;
            }
            _xifexpression = _xifexpression_1;
          }
          _switchResult = _xifexpression;
        }
      }
      if (!_matched) {
        _switchResult = type.getName();
      }
      _xblockexpression = _switchResult;
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
          String _xifexpression = null;
          Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
          if ((_isIRIReference).booleanValue()) {
            _xifexpression = "IRI";
          } else {
            String _xifexpression_1 = null;
            Boolean _isLiteralDateTimeFeature = OMLUtilities.isLiteralDateTimeFeature(feature);
            if ((_isLiteralDateTimeFeature).booleanValue()) {
              _xifexpression_1 = "LiteralDateTime";
            } else {
              String _xifexpression_2 = null;
              Boolean _isLiteralNumberFeature = OMLUtilities.isLiteralNumberFeature(feature);
              if ((_isLiteralNumberFeature).booleanValue()) {
                _xifexpression_2 = "LiteralNumber";
              } else {
                String _xifexpression_3 = null;
                Boolean _isLiteralStringFeature = OMLUtilities.isLiteralStringFeature(feature);
                if ((_isLiteralStringFeature).booleanValue()) {
                  _xifexpression_3 = "StringDataType";
                } else {
                  String _xifexpression_4 = null;
                  Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(feature);
                  if ((_isLiteralFeature).booleanValue()) {
                    _xifexpression_4 = "LiteralValue";
                  } else {
                    String _name_1 = type.getName();
                    _xifexpression_4 = (_name_1 + "UUID");
                  }
                  _xifexpression_3 = _xifexpression_4;
                }
                _xifexpression_2 = _xifexpression_3;
              }
              _xifexpression_1 = _xifexpression_2;
            }
            _xifexpression = _xifexpression_1;
          }
          _switchResult = _xifexpression;
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
    final Function1<EClass, Boolean> _function = (EClass it) -> {
      return OMLUtilities.isAPI(it);
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
    final Function1<ETypedElement, Boolean> _function = (ETypedElement it) -> {
      return OMLUtilities.isOrderingKey(it);
    };
    return IterableExtensions.<ETypedElement>filter(OMLUtilities.functionalAPIOrOrderingKeyAttributes(eClass), _function);
  }
  
  public static Iterable<ETypedElement> functionalAPIOrOrderingKeyAttributes(final EClass eClass) {
    final Function1<ETypedElement, Boolean> _function = (ETypedElement it) -> {
      return Boolean.valueOf(((!(OMLUtilities.isInterface(it)).booleanValue()) && ((OMLUtilities.isFunctionalAttributeOrReferenceExceptContainer(it)).booleanValue() || (OMLUtilities.isOrderingKey(it)).booleanValue())));
    };
    return IterableExtensions.<ETypedElement>filter(OMLUtilities.functionalAPIOrOrderingKeyFeatures(eClass), _function);
  }
  
  public static Iterable<ETypedElement> functionalAPIOrOrderingKeyFeatures(final EClass eClass) {
    List<ETypedElement> _xblockexpression = null;
    {
      final Function1<EClass, Iterable<ETypedElement>> _function = (EClass it) -> {
        return OMLUtilities.ETypedElements(it);
      };
      final Set<ETypedElement> features = IterableExtensions.<ETypedElement>toSet(Iterables.<ETypedElement>concat(IterableExtensions.<EClass, Iterable<ETypedElement>>map(OMLUtilities.selfAndAllSupertypes(eClass), _function)));
      OMLUtilities.OMLFeatureCompare _oMLFeatureCompare = new OMLUtilities.OMLFeatureCompare();
      final List<ETypedElement> sorted = IterableExtensions.<ETypedElement>sortWith(features, _oMLFeatureCompare);
      _xblockexpression = sorted;
    }
    return _xblockexpression;
  }
  
  public static Boolean hasSchemaOptionalAttributes(final EClass eClass) {
    final Function1<ETypedElement, Boolean> _function = (ETypedElement a) -> {
      int _lowerBound = a.getLowerBound();
      return Boolean.valueOf((_lowerBound == 0));
    };
    return Boolean.valueOf(IterableExtensions.<ETypedElement>exists(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function));
  }
  
  public static Iterable<ETypedElement> schemaAPIOrOrderingKeyReferences(final EClass eClass) {
    final Function1<ETypedElement, Boolean> _function = (ETypedElement it) -> {
      return Boolean.valueOf(((OMLUtilities.isClassFeature(it)).booleanValue() && (!(OMLUtilities.isLiteralFeature(it)).booleanValue())));
    };
    return IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyAttributes(eClass), _function);
  }
  
  public static Iterable<ETypedElement> schemaAPIOrOrderingKeyAttributes(final EClass eClass) {
    final Function1<ETypedElement, Boolean> _function = (ETypedElement it) -> {
      return Boolean.valueOf(((!(OMLUtilities.isInterface(it)).booleanValue()) && ((OMLUtilities.isSchemaAttributeOrReferenceOrContainer(it)).booleanValue() || (OMLUtilities.isOrderingKey(it)).booleanValue())));
    };
    return IterableExtensions.<ETypedElement>filter(OMLUtilities.schemaAPIOrOrderingKeyFeatures(eClass), _function);
  }
  
  public static Iterable<ETypedElement> schemaAPIOrOrderingKeyFeatures(final EClass eClass) {
    List<ETypedElement> _xblockexpression = null;
    {
      final Function1<EClass, Iterable<ETypedElement>> _function = (EClass it) -> {
        return OMLUtilities.ETypedElements(it);
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
      final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
        return OMLUtilities.isFunctionalAPIOrOrderingKey(it);
      };
      Iterables.<ETypedElement>addAll(features, IterableExtensions.<EStructuralFeature>filter(eClass.getEStructuralFeatures(), _function));
      final Function1<EOperation, Boolean> _function_1 = (EOperation it) -> {
        return OMLUtilities.isFunctionalAPIOrOrderingKey(it);
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
    final Function1<EClass, String> _function = (EClass it) -> {
      return it.getName();
    };
    return IterableExtensions.<EClass, String>sortBy(eClass.getESuperTypes(), _function);
  }
  
  public static Iterable<EClass> ESpecificClasses(final EClass eClass) {
    final Function1<EClass, Boolean> _function = (EClass it) -> {
      return Boolean.valueOf(it.getESuperTypes().contains(eClass));
    };
    final Function1<EClass, String> _function_1 = (EClass it) -> {
      return it.getName();
    };
    return IterableExtensions.<EClass, String>sortBy(IterableExtensions.<EClass>filter(Iterables.<EClass>filter(eClass.getEPackage().getEClassifiers(), EClass.class), _function), _function_1);
  }
  
  public static Iterable<EStructuralFeature> APIStructuralFeatures(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
      return Boolean.valueOf((((OMLUtilities.isAPI(it)).booleanValue() && (!(OMLUtilities.isFactory(it)).booleanValue())) && ((!(OMLUtilities.isContainment(it)).booleanValue()) || (OMLUtilities.isLiteralFeature(it)).booleanValue())));
    };
    return IterableExtensions.<EStructuralFeature>filter(eClass.getEStructuralFeatures(), _function);
  }
  
  public static Boolean isRootHierarchyClass(final EClass eClass) {
    return Boolean.valueOf(((eClass.isAbstract() && eClass.getESuperTypes().isEmpty()) && (!IterableExtensions.isEmpty(OMLUtilities.orderingKeys(eClass)))));
  }
  
  public static Boolean isSpecializationOfRootClass(final EClass eClass) {
    return Boolean.valueOf(((!eClass.getESuperTypes().isEmpty()) && IterableExtensions.<EClass>exists(OMLUtilities.selfAndAllSupertypes(eClass), ((Function1<EClass, Boolean>) (EClass it) -> {
      return OMLUtilities.isRootHierarchyClass(it);
    }))));
  }
  
  public static Iterable<EOperation> APIOperations(final EClass eClass) {
    final Function1<EOperation, Boolean> _function = (EOperation it) -> {
      return OMLUtilities.isAPI(it);
    };
    return IterableExtensions.<EOperation>filter(eClass.getEOperations(), _function);
  }
  
  public static Iterable<EStructuralFeature> getSortedDerivedAttributeSignature(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
      return Boolean.valueOf(it.isDerived());
    };
    return IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeSignature(eClass), _function);
  }
  
  public static Iterable<EStructuralFeature> getSortedAttributeFactorySignature(final EClass eClass) {
    final Function1<EClass, Iterable<EStructuralFeature>> _function = (EClass it) -> {
      final Function1<EStructuralFeature, Boolean> _function_1 = (EStructuralFeature it_1) -> {
        return Boolean.valueOf((((((OMLUtilities.isAPI(it_1)).booleanValue() || (OMLUtilities.isFactory(it_1)).booleanValue()) && ((!(OMLUtilities.isContainment(it_1)).booleanValue()) || (OMLUtilities.isLiteralFeature(it_1)).booleanValue())) && (!it_1.isDerived())) && (!(OMLUtilities.isUUID(it_1)).booleanValue())));
      };
      return IterableExtensions.<EStructuralFeature>filter(it.getEStructuralFeatures(), _function_1);
    };
    Iterable<EStructuralFeature> _flatten = Iterables.<EStructuralFeature>concat(IterableExtensions.<EClass, Iterable<EStructuralFeature>>map(OMLUtilities.selfAndAllSupertypes(eClass), _function));
    OMLUtilities.OMLFeatureCompare _oMLFeatureCompare = new OMLUtilities.OMLFeatureCompare();
    return IterableExtensions.<EStructuralFeature>sortWith(_flatten, _oMLFeatureCompare);
  }
  
  public static EStructuralFeature lookupUUIDFeature(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
      return OMLUtilities.isUUID(it);
    };
    return IterableExtensions.<EStructuralFeature>findFirst(OMLUtilities.getSortedAttributeSignature(eClass), _function);
  }
  
  public static EOperation lookupUUIDOperation(final EClass eClass) {
    final Function1<EOperation, Boolean> _function = (EOperation it) -> {
      return Boolean.valueOf(((OMLUtilities.isUUID(it)).booleanValue() && (OMLUtilities.isScala(it)).booleanValue()));
    };
    return IterableExtensions.<EOperation>findFirst(eClass.getEAllOperations(), _function);
  }
  
  public static ETypedElement lookupUUIDTypedElement(final EClass eClass) {
    ETypedElement _xblockexpression = null;
    {
      final BasicEList<ETypedElement> typedElements = new BasicEList<ETypedElement>();
      Iterables.<ETypedElement>addAll(typedElements, OMLUtilities.getSortedAttributeSignature(eClass));
      final Function1<EOperation, Boolean> _function = (EOperation it) -> {
        Boolean _isOverride = OMLUtilities.isOverride(it);
        return Boolean.valueOf((!(_isOverride).booleanValue()));
      };
      Iterables.<ETypedElement>addAll(typedElements, IterableExtensions.<EOperation>filter(Iterables.<EOperation>filter(OMLUtilities.selfAndAllSupertypes(eClass), EOperation.class), _function));
      final Function1<ETypedElement, Boolean> _function_1 = (ETypedElement it) -> {
        return OMLUtilities.isUUID(it);
      };
      _xblockexpression = IterableExtensions.<ETypedElement>findFirst(typedElements, _function_1);
    }
    return _xblockexpression;
  }
  
  public static Iterable<EStructuralFeature> getSortedAttributeSignatureExceptDerived(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
      boolean _isDerived = it.isDerived();
      return Boolean.valueOf((!_isDerived));
    };
    return IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeSignature(eClass), _function);
  }
  
  public static Iterable<EStructuralFeature> getSortedAttributeSignature(final EClass eClass) {
    final Function1<EClass, Iterable<EStructuralFeature>> _function = (EClass it) -> {
      return OMLUtilities.APIStructuralFeatures(it);
    };
    Iterable<EStructuralFeature> _flatten = Iterables.<EStructuralFeature>concat(IterableExtensions.<EClass, Iterable<EStructuralFeature>>map(OMLUtilities.selfAndAllSupertypes(eClass), _function));
    OMLUtilities.OMLFeatureCompare _oMLFeatureCompare = new OMLUtilities.OMLFeatureCompare();
    return IterableExtensions.<EStructuralFeature>sortWith(_flatten, _oMLFeatureCompare);
  }
  
  public static Iterable<EStructuralFeature> lookupCopyConstructorArguments(final EClass eClass) {
    final Function1<EStructuralFeature, Boolean> _function = (EStructuralFeature it) -> {
      return OMLUtilities.isCopyConstructorArgument(it);
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
  
  public static Boolean isEssential(final EStructuralFeature sf) {
    return Boolean.valueOf(((sf.getLowerBound() == 1) && (sf.getUpperBound() == 1)));
  }
  
  public static Boolean isUUIDDerived(final EClass e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/DerivedUUID");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static EStructuralFeature lookupUUIDNamespaceFeature(final EClass e) {
    EStructuralFeature _xblockexpression = null;
    {
      final Function1<EClass, String> _function = (EClass it) -> {
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
      };
      final Iterable<String> ns = IterableExtensions.<String>filterNull(IterableExtensions.<EClass, String>map(OMLUtilities.selfAndAllSupertypes(e), _function));
      final Function1<EStructuralFeature, Boolean> _function_1 = (EStructuralFeature it) -> {
        String _name = it.getName();
        String _head = null;
        if (ns!=null) {
          _head=IterableExtensions.<String>head(ns);
        }
        return Boolean.valueOf(Objects.equal(_name, _head));
      };
      _xblockexpression = IterableExtensions.<EStructuralFeature>findFirst(OMLUtilities.getSortedAttributeFactorySignature(e), _function_1);
    }
    return _xblockexpression;
  }
  
  public static Iterable<EStructuralFeature> lookupUUIDNamespaceFactors(final EClass e) {
    final Function1<EClass, Iterable<EStructuralFeature>> _function = (EClass eClass) -> {
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
            final Function1<EStructuralFeature, Boolean> _function_1 = (EStructuralFeature s) -> {
              final Function1<String, Boolean> _function_2 = (String f) -> {
                String _name = s.getName();
                return Boolean.valueOf(Objects.equal(f, _name));
              };
              return Boolean.valueOf(IterableExtensions.<String>exists(factoredFeatures, _function_2));
            };
            _xblockexpression_1 = IterableExtensions.<EStructuralFeature>filter(OMLUtilities.getSortedAttributeFactorySignature(eClass), _function_1);
          }
          _xifexpression = _xblockexpression_1;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    };
    final Function1<EStructuralFeature, String> _function_1 = (EStructuralFeature it) -> {
      return it.getName();
    };
    return IterableExtensions.<EStructuralFeature, String>sortBy(Iterables.<EStructuralFeature>concat(IterableExtensions.<EClass, Iterable<EStructuralFeature>>map(OMLUtilities.selfAndAllSupertypes(e), _function)), _function_1);
  }
  
  public static Boolean isCopyConstructorArgument(final EStructuralFeature attribute) {
    EAnnotation _eAnnotation = attribute.getEAnnotation("http://imce.jpl.nasa.gov/oml/CopyConstructor");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Iterable<EOperation> ScalaOperations(final EClass eClass) {
    final Function1<EOperation, Boolean> _function = (EOperation op) -> {
      return Boolean.valueOf(((OMLUtilities.isScala(op)).booleanValue() || (null != OMLUtilities.xExpressions(op))));
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
  
  public static Boolean isLiteralDateTime(final EClassifier type) {
    String _name = type.getName();
    return Boolean.valueOf(Objects.equal(_name, "LiteralDateTime"));
  }
  
  public static Boolean isLiteralDateTimeFeature(final ETypedElement f) {
    return OMLUtilities.isLiteralDateTime(f.getEType());
  }
  
  public static Boolean isLiteralNumber(final EClassifier type) {
    String _name = type.getName();
    return Boolean.valueOf(Objects.equal(_name, "LiteralNumber"));
  }
  
  public static Boolean isLiteralNumberFeature(final ETypedElement f) {
    return OMLUtilities.isLiteralNumber(f.getEType());
  }
  
  public static Boolean isLiteralString(final EClassifier type) {
    String _name = type.getName();
    return Boolean.valueOf(Objects.equal(_name, "LiteralString"));
  }
  
  public static Boolean isLiteralStringFeature(final ETypedElement f) {
    return OMLUtilities.isLiteralString(f.getEType());
  }
  
  public static Boolean isLiteralValue(final EClassifier type) {
    return Boolean.valueOf((((Objects.equal(type.getName(), "LiteralValue") || Objects.equal(type.getName(), "LiteralNumber")) || Objects.equal(type.getName(), "LiteralDateTime")) || Objects.equal(type.getName(), "LiteralString")));
  }
  
  public static Boolean isLiteralFeature(final ETypedElement f) {
    return OMLUtilities.isLiteralValue(f.getEType());
  }
  
  public static Boolean isSchemaAttributeOrReferenceOrContainer(final ETypedElement f) {
    Boolean _switchResult = null;
    boolean _matched = false;
    if (f instanceof EReference) {
      _matched=true;
      _switchResult = Boolean.valueOf(((OMLUtilities.isSchema(f)).booleanValue() && ((!((EReference)f).isContainment()) || (OMLUtilities.isLiteralFeature(f)).booleanValue())));
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
  
  public static String getFeatureQuery(final ETypedElement feature) {
    String _xblockexpression = null;
    {
      final EClass c = OMLUtilities.EClassContainer(feature);
      final Function1<EOperation, Boolean> _function = (EOperation it) -> {
        String _name = it.getName();
        String _name_1 = feature.getName();
        return Boolean.valueOf(Objects.equal(_name, _name_1));
      };
      final EOperation op = IterableExtensions.<EOperation>findFirst(c.getEAllOperations(), _function);
      final String name = feature.getName();
      String _xifexpression = null;
      if ((null == op)) {
        _xifexpression = name;
      } else {
        _xifexpression = (name + "()");
      }
      _xblockexpression = _xifexpression;
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
    boolean _and = false;
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/NotFunctionalAPI");
    boolean _tripleEquals = (null == _eAnnotation);
    if (!_tripleEquals) {
      _and = false;
    } else {
      boolean _switchResult = false;
      boolean _matched = false;
      if (e instanceof ETypedElement) {
        _matched=true;
        boolean _xblockexpression = false;
        {
          final EClassifier c = ((ETypedElement)e).getEType();
          boolean _switchResult_1 = false;
          boolean _matched_1 = false;
          if (c instanceof EClass) {
            _matched_1=true;
            _switchResult_1 = ((!Objects.equal(((EClass)c).getName(), "LiteralValue")) || (!IterableExtensions.<EClass>exists(((EClass)c).getEAllSuperTypes(), ((Function1<EClass, Boolean>) (EClass it) -> {
              String _name = it.getName();
              return Boolean.valueOf(Objects.equal(_name, "LiteralValue"));
            }))));
          }
          if (!_matched_1) {
            _switchResult_1 = true;
          }
          _xblockexpression = _switchResult_1;
        }
        _switchResult = _xblockexpression;
      }
      if (!_matched) {
        if (e instanceof EClass) {
          _matched=true;
          _switchResult = ((!Objects.equal(((EClass)e).getName(), "LiteralValue")) && (!IterableExtensions.<EClass>exists(((EClass)e).getEAllSuperTypes(), ((Function1<EClass, Boolean>) (EClass it) -> {
            String _name = it.getName();
            return Boolean.valueOf(Objects.equal(_name, "LiteralValue"));
          }))));
        }
      }
      if (!_matched) {
        _switchResult = true;
      }
      _and = _switchResult;
    }
    return Boolean.valueOf(_and);
  }
  
  public static Boolean isIRIReference(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/IRIReference");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isExtentContainer(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/ExtentContainer");
    return Boolean.valueOf((null != _eAnnotation));
  }
  
  public static Boolean isExtentManaged(final EClass e) {
    final Function1<EClass, Boolean> _function = (EClass eClass) -> {
      EAnnotation _eAnnotation = eClass.getEAnnotation("http://imce.jpl.nasa.gov/oml/ExtentManaged");
      return Boolean.valueOf((null != _eAnnotation));
    };
    return Boolean.valueOf(IterableExtensions.<EClass>exists(OMLUtilities.selfAndAllSupertypes(e), _function));
  }
  
  public static Boolean isFactory(final ENamedElement e) {
    EAnnotation _eAnnotation = e.getEAnnotation("http://imce.jpl.nasa.gov/oml/Factory");
    return Boolean.valueOf((null != _eAnnotation));
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
  
  public static String lowerCaseInitialOrWord(final String s) {
    String _xifexpression = null;
    boolean _startsWith = s.startsWith("IRI");
    if (_startsWith) {
      String _substring = s.substring(3);
      _xifexpression = ("iri" + _substring);
    } else {
      _xifexpression = StringExtensions.toFirstLower(s);
    }
    return _xifexpression;
  }
  
  public static String upperCaseInitialOrWord(final String s) {
    String _xifexpression = null;
    boolean _startsWith = s.startsWith("iri");
    if (_startsWith) {
      String _substring = s.substring(3);
      _xifexpression = ("IRI" + _substring);
    } else {
      _xifexpression = StringExtensions.toFirstUpper(s);
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
  
  public static String columnUUID(final ETypedElement feature) {
    String _xifexpression = null;
    if ((feature instanceof EReference)) {
      String _xifexpression_1 = null;
      Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
      if ((_isIRIReference).booleanValue()) {
        String _name = ((EReference)feature).getName();
        String _plus = ("oug.namespaceUUID(" + _name);
        _xifexpression_1 = (_plus + "IRI).toString");
      } else {
        String _name_1 = ((EReference)feature).getName();
        _xifexpression_1 = (_name_1 + "UUID");
      }
      _xifexpression = _xifexpression_1;
    } else {
      _xifexpression = feature.getName();
    }
    return _xifexpression;
  }
  
  public static Boolean isXRefColumn(final ETypedElement feature) {
    boolean _xifexpression = false;
    if ((feature instanceof EReference)) {
      boolean _xifexpression_1 = false;
      Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
      if ((_isIRIReference).booleanValue()) {
        _xifexpression_1 = false;
      } else {
        boolean _xifexpression_2 = false;
        Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(feature);
        if ((_isLiteralFeature).booleanValue()) {
          _xifexpression_2 = false;
        } else {
          _xifexpression_2 = true;
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    } else {
      _xifexpression = false;
    }
    return Boolean.valueOf(_xifexpression);
  }
  
  public static String columnName(final ETypedElement feature) {
    String _xifexpression = null;
    if ((feature instanceof EReference)) {
      String _xifexpression_1 = null;
      Boolean _isIRIReference = OMLUtilities.isIRIReference(feature);
      if ((_isIRIReference).booleanValue()) {
        String _name = ((EReference)feature).getName();
        _xifexpression_1 = (_name + "IRI");
      } else {
        String _xifexpression_2 = null;
        Boolean _isLiteralFeature = OMLUtilities.isLiteralFeature(feature);
        if ((_isLiteralFeature).booleanValue()) {
          _xifexpression_2 = ((EReference)feature).getName();
        } else {
          String _name_1 = ((EReference)feature).getName();
          _xifexpression_2 = (_name_1 + "UUID");
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
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
