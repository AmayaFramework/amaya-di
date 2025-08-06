module amayaframework.di {
    // Imports
    // DI Modules
    requires transitive amayaframework.di.core;
    requires amayaframework.di.schema;
    requires amayaframework.di.stub;
    // External imports
    requires com.github.romanqed.jtype;
    requires com.github.romanqed.jfunc;
    // Exports
    exports io.github.amayaframework.di;
}
