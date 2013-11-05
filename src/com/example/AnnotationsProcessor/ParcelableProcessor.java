package com.example.AnnotationsProcessor;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Set;

@SupportedAnnotationTypes("com.example.AnnotationsProcessor.ParcelableObj")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ParcelableProcessor extends AbstractProcessor {

    /**
     * public for ServiceLoader
     */
    public ParcelableProcessor() {
    }

    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(ParcelableObj.class)) {
            ParcelableObj ca = e.getAnnotation(ParcelableObj.class);
            Element clazz = e.getEnclosingElement();
            try {
                JavaFileObject f = processingEnv.getFiler().
                        createSourceFile(e.toString() + "Projection");
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "Creating " + f.toUri());
                Writer w = f.openWriter();
                PrintWriter pw = new PrintWriter(w);
                try {
                    pw.println("package "
                            + clazz + ";");
                    pw.println("\npublic class "
                            + e.getSimpleName() + "Projection {");

                    TypeMirror type = e.asType();

                    pw.println("\n//" + e);
                    pw.println("//" + ca);
                    pw.println("//" + clazz);

                    pw.println("    public final static void parse(org.json.JSONObject  object, " + e.getSimpleName() + " entity) {");
                    pw.println("        try {");
                    pw.println("            for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {");
                    pw.println("                field.setAccessible(true);");
                    pw.println("                String fieldName = field.getName();");
                    pw.println("                if(!object.isNull(field.getName())) {");
                    pw.println("                    field.set(entity, object.get(fieldName));");
                    pw.println("                }");
                    pw.println("            }");
                    pw.println("        } catch (IllegalAccessException e) {");
                    pw.println("            e.printStackTrace();");
                    pw.println("        } catch (org.json.JSONException e) {");
                    pw.println("            e.printStackTrace();");
                    pw.println("        }");
                    pw.println("    }");
                    pw.println("}");
                    pw.flush();
//                } catch (ClassNotFoundException e1) {
//                    pw.println(e1);
//                    e1.printStackTrace();
                } finally {
                    w.close();
                }
            } catch (IOException x) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        x.toString());
            }
        }
        return true;
    }

    private static String capitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }
}
