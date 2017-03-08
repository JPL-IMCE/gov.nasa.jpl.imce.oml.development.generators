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

- Maged Elaasar (lead)
- Nicolas Rouquette

# Running the Xtend generators

To run the Xtend generators, the following projects must be in the workspace of an Eclipse Neon Modeling package installation:
- [gov.nasa.jpl.imce.omf.schema.generators](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.omf.schema.generators)
- [gov.nasa.jpl.imce.omf.schema.specification](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.omf.schema.specification)
- [jpl.omf.schema.tables](https://github.com/JPL-IMCE/jpl.omf.schema.tables)

You can import these projects via the following [Eclipse Team Project Set](https://github.com/JPL-IMCE/gov.nasa.jpl.imce.omf.schema.generators/OMFSchemaTablesGeneration.psf).

You can run the Xtend generator with the following Eclipse launch configuration:
[launchers/OMFSchemaGeneratorTest.launch](launchers/OMFSchemaGeneratorTest.launch).

The expected Console output has several EMF-related misleading errors.
Instead of the console output, use the JUnit success/failure to determine whether the code generation worked properly or not.

