module io.github.amayaframework.di.reflect {
    // Imports
    // Base dependencies
    requires com.github.romanqed.jeflect.cloner;
    // Amaya DI modules
    requires io.github.amayaframework.di.core;
    requires io.github.amayaframework.di.schema;
    requires io.github.amayaframework.di.stub;
    // Exports
    exports io.github.amayaframework.di.reflect;
}
