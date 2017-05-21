# Xtend generators for JPL's Ontological Modeling Framework (OMF)

The [Xcore](http://wiki.eclipse.org/Xcore) OMF metamodel/schema is intended to be a single source
for several derived artifacts whose contents is a projection of the OMF metamodel/schema. Among such
artifacts, the [cross-platform normalized database schema tables](https://github.com/JPL-IMCE/jpl.omf.schema.tables)
provide a single source specification for normalized database schema tables cross-compiled for several
environments, including JVM (pure Java, pure Scala, mixed Java+Scala), JavaScript (Node.js), mixed (ScalaJS).

## Copyrights

[JPL/Caltech](copyrights/Caltech.md)

## License

[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)

## Status

The Xtend code generator produces the cross-platform single source specification in Scala of the OMF Schema.

## Contributors

- Nicolas Rouquette
- Maged Elaasar

# Running the Xtend generators

To run the Xtend generators, the following projects must be in the workspace of an Eclipse Neon Modeling package installation:

- [gov.nasa.jpl.imce.oml.model](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.tycho/tree/master/plugins/core/gov.nasa.jpl.imce.oml.model)
- [gov.nasa.jpl.imce.oml.oti.provenance](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.tycho/tree/master/plugins/other/gov.nasa.jpl.imce.oml.oti.provenance)
	These projects provides the OML `*.xcore` metamodels used as inputs to the code generators.
	
- [gov.nasa.jpl.imce.oml.development.generators](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.development.generators)
	The generators, currently specified as model-to-text transformations in the [Eclipse Xtend](https://www.eclipse.org/xtend/) language.

- [gov.nasa.jpl.imce.oml.tables](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.tables)
- [gov.nasa.jpl.imce.oml.resolver](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.resolver)
- [gov.nasa.jpl.imce.oml.doc](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.doc)
	The generators affect the content of these projects.
	After generation, these projects need to be separately committed & pushed.
	
## OML normalized relational schema code generation

- Execute the launch configuration [OML Development Generator for OML Schema Tables](../launchers/OML Development Generator for OML Schema Tables.launch)

	Affects [gov.nasa.jpl.imce.oml.tables](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.tables)
	
## OML resolver between normalized relational table schema and functional API in-memory representation

- Execute the launch configuration [OML Development Generator for OML Resolver API](../launchers/OML Development Generator for OML Resolver API.launch)

	Affects [gov.nasa.jpl.imce.oml.tables](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.tables)

## OML resolver implementation

- Execute the launch configuration [OML Development Generator for OML Resolver Implementation](../launchers/OML Development Generator for OML Resolver Implementation.launch)

	Affects [gov.nasa.jpl.imce.oml.resolver](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.resolver)
	
## OML specification document glossary

- Execute the launch configuration [OML Development Generator for OML Specification Document](../launchers/OML Development Generator for OML Specification Document.launch)

	Affects [gov.nasa.jpl.imce.oml.doc](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.oml.doc)
	