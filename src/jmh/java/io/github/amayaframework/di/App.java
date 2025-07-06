package io.github.amayaframework.di;

public final class App {
    final Service1 s1;
    @Inject
    public Service2 s2;
    Service3 s3;

    public App(Service1 s1) {
        this.s1 = s1;
    }

    @Inject
    public void setS3(Service3 s3) {
        this.s3 = s3;
    }
}
