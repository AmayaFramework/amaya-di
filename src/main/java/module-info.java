open module io.github.amayaframework.di {
    // Imports
    requires com.github.romanqed.jtype;
    requires com.github.romanqed.jfunc;
    requires com.github.romanqed.jeflect;
    requires org.objectweb.asm;
    // Exports
    exports io.github.amayaframework.di;
    exports io.github.amayaframework.di.scheme;
    exports io.github.amayaframework.di.graph;
    exports io.github.amayaframework.di.stub;
}
