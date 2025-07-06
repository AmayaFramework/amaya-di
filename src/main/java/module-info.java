module io.github.amayaframework.di {
    // Imports
    // DI Modules
    requires io.github.amayaframework.di.core;
    requires io.github.amayaframework.di.schema;
    requires io.github.amayaframework.di.stub;
    // External imports
    requires com.github.romanqed.jtype;
    requires com.github.romanqed.jfunc;
    // Exports
    exports io.github.amayaframework.di;
}
