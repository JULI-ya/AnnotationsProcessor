package com.example.AnnotationsProcessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;


@SupportedAnnotationTypes("com.example.AnnotationsProcessor.RequestObj")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RequestProcessor extends AbstractProcessor {

    public RequestProcessor() {
    }

    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(RequestObj.class)) {
            RequestObj ca = e.getAnnotation(RequestObj.class);
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

                    pw.println("\nimport android.os.AsyncTask;");
                    pw.println("import org.apache.http.HttpEntity;");
                    pw.println("import org.apache.http.HttpResponse;");
                    pw.println("import org.apache.http.client.HttpClient;");
                    pw.println("import org.apache.http.client.methods.HttpGet;");
                    pw.println("import org.apache.http.impl.client.DefaultHttpClient;");
                    pw.println("import org.apache.http.util.EntityUtils;");
                    pw.println("import android.util.Log;");
                    pw.println("import java.io.IOException;");

                    pw.println("\npublic class "
                            + e.getSimpleName() + "Projection {");

                    pw.println("\n  public void getJson(String url) {");
                    pw.println("        new GetJsonTask(url).execute(\"\");");
                    pw.println("    }");

                    pw.println("\n    class GetJsonTask extends AsyncTask<String, Void, String> {");

                    pw.println("\n    String mUrl;");

                    pw.println("\n      GetJsonTask(String url) {");
                    pw.println("        mUrl = url;");
                    pw.println("    }");

                    pw.println("\n        protected String doInBackground(String... urls) {");
                    pw.println("            String URL = \"http://api.openweathermap.org/data/2.5/weather?q=\" + mUrl;");
                    pw.println("            HttpClient client = new DefaultHttpClient();");
                    pw.println("            HttpGet get = new HttpGet(URL);");
                    pw.println("            try {");
                    pw.println("                HttpResponse response = client.execute(get);");
                    pw.println("                int status = response.getStatusLine().getStatusCode();");
                    pw.println("                if (status == 200) {");
                    pw.println("                    HttpEntity e = response.getEntity();");
                    pw.println("                    String data = EntityUtils.toString(e);");
                    pw.println("                    return data;");
                    pw.println("                } else {");
                    pw.println("                    return null;");
                    pw.println("                }");
                    pw.println("            } catch (IOException e) {");
                    pw.println("            e.printStackTrace();");
                    pw.println("            return null;");
                    pw.println("        }");
                    pw.println("    }");

                    pw.println("\n        protected void onPostExecute(String result) {");
                    pw.println("            Log.d(\"Json result is:\", \" \" + result);    ");
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

}
