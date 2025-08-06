module amayaframework.di.asm {
    // Imports
    // Base dependencies
    requires org.objectweb.asm;
    requires com.github.romanqed.jfunc;
    requires com.github.romanqed.jeflect.loader;
    // Amaya DI modules
    requires amayaframework.di.core;
    requires amayaframework.di.schema;
    requires amayaframework.di.stub;
    // Exports
    exports io.github.amayaframework.di.asm;
}
