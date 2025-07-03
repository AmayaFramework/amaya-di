module io.github.amayaframework.di.asm {
    // Imports
    // Base dependencies
    requires org.objectweb.asm;
    requires com.github.romanqed.jfunc;
    requires com.github.romanqed.jeflect.loader;
    // Amaya DI modules
    requires io.github.amayaframework.di.core;
    requires io.github.amayaframework.di.schema;
    requires io.github.amayaframework.di.stub;
    requires com.github.romanqed.jtype;
    // Exports
    exports io.github.amayaframework.di.asm;
}
