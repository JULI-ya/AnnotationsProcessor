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
import java.util.Set;

@SupportedAnnotationTypes("*")
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

                    pw.println("    public final static void parse(String  s, " + e.getSimpleName() + " entity) {");
                    pw.println("        try {");
                    pw.println("            org.json.JSONObject object = new org.json.JSONObject(s);");
                    pw.println("            for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {");
                    pw.println("                field.setAccessible(true);");
                    pw.println("                String fieldName = field.getName();");
                    pw.println("                if (field.isAnnotationPresent(com.example.AnnotationsProcessor.ChildObj.class)) {");
                    pw.println("                    Class<?> cl = field.getType();");
                    pw.println("                    Object child = field.getType().getConstructor().newInstance();");
                    pw.println("                    if (!object.isNull(fieldName)){");
                    pw.println("                        java.lang.reflect.Method method = field.getType().getMethod(\"parse\", String.class, field.getType());");
                    pw.println("                        method.invoke(cl, object.get(fieldName).toString(), child);");
                    pw.println("                        cl.cast(child);");
                    pw.println("                        field.set(entity, child);");
                    pw.println("                     }");
                    pw.println("                } else if (!object.isNull(fieldName)) {");
                    pw.println("                    if (field.getType() == boolean.class) {");
                    pw.println("                        field.set(entity, object.getBoolean(fieldName));");
                    pw.println("                    } else if (field.getType() == int.class) {");
                    pw.println("                        field.set(entity, object.getInt(fieldName));");
                    pw.println("                    } else");
                    pw.println("                        field.set(entity, object.get(fieldName));");
                    pw.println("                }");
                    pw.println("            }");
                    pw.println("        } catch (NoSuchMethodException e) {");
                    pw.println("            e.printStackTrace();");
                    pw.println("        } catch (InstantiationException e) {");
                    pw.println("            e.printStackTrace();");
                    pw.println("        } catch (java.lang.reflect.InvocationTargetException e) {");
                    pw.println("            e.printStackTrace();");
                    pw.println("        } catch (org.json.JSONException e) {");
                    pw.println("            e.printStackTrace();");
                    pw.println("        } catch (IllegalAccessException e) {");
                    pw.println("            e.printStackTrace();");
                    pw.println("        }");
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
        return true;
    }

    private static String capitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }
}
