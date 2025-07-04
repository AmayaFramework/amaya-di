module io.github.amayaframework.di {
    // TODO remove
    requires io.github.amayaframework.di.asm;
    requires io.github.amayaframework.di.reflect;
    // Imports
    // DI Modules
    requires io.github.amayaframework.di.core;
    requires io.github.amayaframework.di.schema;
    requires io.github.amayaframework.di.stub;
    // External imports
    requires com.github.romanqed.jtype;
    requires com.github.romanqed.jfunc;
    requires com.github.romanqed.jgraph;
    // Exports
    exports io.github.amayaframework.di;
}
