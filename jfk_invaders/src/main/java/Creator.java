import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import objects.CreatorInterface;
import objects.JarClass;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.*;

public class Creator implements CreatorInterface {
    private static Manifest manifest;
    private static JarFile jarFile;
    public static ClassPool classPool;
    public String pathToJar;
    public String pathToJarOut;
    public static JarInputStream jarInputStream;
    private static ArrayList<String> classNames = new ArrayList<String>();
    public  ArrayList<JarClass> classes = new ArrayList<JarClass>();
    private static ArrayList<String> packages = new ArrayList<String >();
    private static ArrayList<JarEntry> jarEntries = new ArrayList<JarEntry>();

    public Creator(String pathToJar, String pathToJarOut) {
        this.pathToJar = pathToJar;
        this.pathToJarOut = pathToJarOut;
    }

    public void listClasses(){
        fillClassNamesAndEntries();
        for (JarClass jc:classes) {
            System.out.println(jc.getJarClass().getName());
        }
    }

    public void listPackages(){
        fillClassNamesAndEntries();
        for (String pack:packages) {
            System.out.println(pack);
        }
    }

    @Override
    public void listMethods(String className) {
        fillClassNamesAndEntries();

        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                System.out.println(jc.listMethods());
            }
        }
    }

    @Override
    public void listFields(String className) {
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                System.out.println(jc.listFields());
            }
        }
    }

    @Override
    public void listConstructors(String className) {
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                System.out.println(jc.listConstructors());
            }
        }
    }


    public void initializeStaticVariables(){
        try {
            jarFile = new JarFile(pathToJar);
            jarInputStream = new JarInputStream(new FileInputStream(pathToJar));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("KOMUNIKAT");
        }

        manifest = jarInputStream.getManifest();
        fillClassNamesAndEntries();
        getJarClassesLoader();
    }

    public void fillClassNamesAndEntries(){
        try {

            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while (jarEntry != null) {
                if(jarEntry.getName().endsWith(".class")){
                    String name = jarEntry.getName();
                    classNames.add(name);
                }
                else if(!jarEntry.getName().endsWith("/")){
                    jarEntries.add(jarEntry);
                }
                else if(jarEntry.getName().endsWith("/")){
                    packages.add(jarEntry.getName());
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
        } catch (IOException e) {
            System.out.println("File error.");
        }
    }

    public void getJarClassesLoader(){
        URLClassLoader classLoader = null;
        try {
            classLoader = new URLClassLoader(new URL[]{new File(pathToJar).toURL()});
        } catch (MalformedURLException e) {
            System.out.println("File error");
        }

        for (String name: classNames) {
            Class clazz;
            name = name.replaceAll("/",".");
            name = name.replace(".class", "");
            try {
                clazz = classLoader.loadClass(name);
                classes.add(new JarClass(clazz, false));

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Can't load class " + name);
            }
        }
        getJarClassesPool();
    }

    public void getJarClassesPool() {
        classPool = ClassPool.getDefault();
        try {
            classPool.insertClassPath(pathToJar);
            ClassPool.doPruning = false;
            for (JarClass jc : classes) {
                CtClass ctClass = classPool.get(jc.getJarClass().getName());
                jc.setCtClass(ctClass);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveFile() {
        JarOutputStream jarOutputStream = null;
        try {
            jarOutputStream = new JarOutputStream(new FileOutputStream(pathToJarOut), manifest);
        } catch (IOException e) {
            System.out.println("Can't create a jar file.");
        }
        int i = 0;
        byte[] buffer = new byte[1024];
        for (String className : classNames) {
            try {
                JarEntry jarEntry = new JarEntry(className);
                jarOutputStream.putNextEntry(jarEntry);
                InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(classes.get(i).getCtClass().toBytecode()));
                while (true){
                    int count = inputStream.read(buffer);
                    if (count == -1)
                        break;
                    jarOutputStream.write(buffer, 0, count);
                }
                i++;
                inputStream.close();
                jarOutputStream.closeEntry();
            } catch (IOException e) {
                System.out.println("Can't save classes");
            } catch (CannotCompileException e) {
                System.out.println("Compilation exception");
            }
        }
        for (JarEntry jar : jarEntries) {
            try {
                InputStream is = jarFile.getInputStream(jar);
                jarOutputStream.putNextEntry(jar);
                while (true){
                    int count = is.read(buffer);
                    if (count == -1)
                        break;
                    jarOutputStream.write(buffer, 0, count);
                }
                jarOutputStream.closeEntry();
                is.close();
            } catch (IOException ex) {
                System.out.println("Can't save files");
            }
        }
        try {
            jarOutputStream.close();
        } catch (IOException e) {
            System.out.println("Can't close output stream");
        }
        System.out.println("Jar created correctly.");
    }

    public void createClass(String classPath){
        CtClass cc = classPool.makeClass(classPath);
        classPath = classPath.replaceAll("\\.", "/");
        classPath +=".class";

        classNames.add(classPath);
        Class clazz = null;
        try {
            clazz = cc.toClass();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        JarClass jc = new JarClass(clazz, cc, true);
        classes.add(jc);
    }

    public void createInterface(String classPath){
        CtClass cc = classPool.makeInterface(classPath);
        classPath = classPath.replaceAll("\\.", "/");
        classPath +=".class";

        classNames.add(classPath);
        Class clazz = null;
        try {
            clazz = cc.toClass();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        JarClass jc = new JarClass(clazz, cc, true);
        classes.add(jc);
    }

    @Override
    public void removeInterface(String classPath) {
        removeClass(classPath);
    }

    public void removeClass(String classPath){
        for(int i=0; i<classNames.size(); i++){

            if(classes.get(i).getJarClass().getName().equals(classPath) && classes.get(i).isAdded()){
                classes.remove(classes.get(i));
                System.out.println("LALA");
                classNames.remove(classNames.get(i));
                System.out.println("LALA2");

            }
        }
    }

    public void addPackage(String packageName){

    }

    @Override
    public void removePackage(String packageName) {

    }

    public void addMethod(String className, String  methodName){
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                jc.addMethod(methodName);
            }
        }
    }

    @Override
    public void setMethodBody(String className, String methodName, StringBuilder methodBody) {
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                jc.setMethodBody(methodName, methodBody);
            }
        }
    }

    @Override
    public void addBeforeMethod(String className, String methodName, StringBuilder methodBody) {
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                jc.addBeforeMethod(methodName, methodBody);
            }
        }
    }

    @Override
    public void addAfterMethod(String className, String methodName, StringBuilder methodBody) {
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                jc.addAfterMethod(methodName, methodBody);
            }
        }
    }

    public void removeMethod(String className, String methodName){
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                jc.removeMethod(methodName);
            }
        }
    }

    public void addField(String className, String fieldName){
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                System.out.println("ZNALAZLEM KLASE " + jc.getJarClass().getName() + " dla " +fieldName) ;
                jc.addField(fieldName);
            }
        }
    }

    public void removeField(String className, String fieldName){
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                jc.removeField(fieldName);
            }
        }
    }

    @Override
    public void addConstructor(String className, StringBuilder constructorBody) {
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                jc.addConstructor(constructorBody);
            }
        }
    }

    @Override
    public void removeConstructor(String className) {
        for (JarClass jc: classes) {
            if(jc.getJarClass().getName().equals(className)){
                jc.removeConstructor(className);
            }
        }
    }

    @Override
    public void setCtorBody(String className, String ctorName, StringBuilder ctorBody) {
        System.out.println(className);
        for (JarClass jc: classes) {
            System.out.println(jc.getJarClass().getName());
            if(jc.getJarClass().getName().equals(className)){
                jc.setCtorBody(ctorName, ctorBody);
            }
        }
    }


}
