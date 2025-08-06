module amayaframework.di.reflect {
    // Imports
    // Base dependencies
    requires com.github.romanqed.jeflect.cloner;
    // Amaya DI modules
    requires amayaframework.di.core;
    requires amayaframework.di.schema;
    requires amayaframework.di.stub;
    // Exports
    exports io.github.amayaframework.di.reflect;
}
