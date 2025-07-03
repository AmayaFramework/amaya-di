package io.github.amayaframework.di.asm;

public class App {
    final Service1 s1;
    @Inject
    public Service2<Integer> s2Int;
    Service2<String> s2String;
    Service3 s3;

    public App(Service1 s1) {
        this.s1 = s1;
    }

    @Inject
    public static void setS3(App app, Service3 s3) {
        app.s3 = s3;
    }

    @Inject
    public void setS2String(Service2<String> s2String) {
        this.s2String = s2String;
    }
}
