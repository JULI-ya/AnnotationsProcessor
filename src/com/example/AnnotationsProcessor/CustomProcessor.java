package com.example.AnnotationsProcessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;


@SupportedAnnotationTypes({"com.example.AnnotationsProcessor.CustomAnnotation"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class CustomProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element e : roundEnv.getElementsAnnotatedWith(CustomAnnotation.class)) {
            if (e.getKind() == ElementKind.CLASS) {
                processClass(e);
            } else if (e.getKind() == ElementKind.METHOD) {
                processMethod(e);
            }

        }
        return true;
    }

    private void processClass(Element e) {
        CustomAnnotation ca = e.getAnnotation(CustomAnnotation.class);
        String name = e.getSimpleName().toString();
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        name = new String(name);
        Element clazz = e.getEnclosingElement();
        try {
            JavaFileObject f = processingEnv.getFiler().
                    createSourceFile(e + "_c");
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Creating " + f.toUri());
            Writer w = f.openWriter();
            try {
                PrintWriter pw = new PrintWriter(w);
                pw.println("package "
                        + clazz + ";");
                pw.println("\npublic class "
                        + e.getSimpleName() + "_c {");

                TypeMirror type = e.asType();

                pw.println("\n    public " + ca.className() + " result = \"" + ca.value() + "\";");

                pw.println("    public int type = " + ca.type() + ";");


                pw.println("\n    protected " + e.getSimpleName()
                        + "_c() {}");
                pw.println("\n    /** Handle something. */");
                pw.println("    protected final void handle" + name
                        + "(" + ca.className() + " value" + ") {");
                pw.println("\n//" + e);
                pw.println("//" + ca);
                pw.println("//" + clazz);
                pw.println("\n        System.out.println(value);");
                pw.println("    }");
                pw.println("}");
                pw.flush();
            } finally {
                w.close();
            }
        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    x.toString());
        }
    }

    private void processMethod(Element e) {
        CustomAnnotation ca = e.getAnnotation(CustomAnnotation.class);
        String name = e.getSimpleName().toString();
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        name = new String(name);
        TypeElement clazz = (TypeElement) e.getEnclosingElement();
        try {
            JavaFileObject f = processingEnv.getFiler().
                    createSourceFile(clazz.getQualifiedName() + "_m");
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Creating " + f.toUri());
            Writer w = f.openWriter();
            try {
                String pack = clazz.getQualifiedName().toString();
                PrintWriter pw = new PrintWriter(w);
                pw.println("package "
                        + pack.substring(0, pack.lastIndexOf('.')) + ";");
                pw.println("\npublic class "
                        + clazz.getSimpleName() + "_m {");

                TypeMirror type = e.asType();

                pw.println("\n    public " + ca.className() + " result = \"" + ca.value() + "\";");

                pw.println("    public int type = " + ca.type() + ";");


                pw.println("\n    protected " + clazz.getSimpleName()
                        + "_m() {}");
                pw.println("\n    /** Handle something. */");
                pw.println("    protected final void handle" + name
                        + "(" + ca.className() + " value" + ") {");
                pw.println("\n//" + e);
                pw.println("//" + ca);
                pw.println("\n        System.out.println(value);");
                pw.println("    }");
                pw.println("}");
                pw.flush();
            } finally {
                w.close();
            }
        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    x.toString());
        }
    }


}
