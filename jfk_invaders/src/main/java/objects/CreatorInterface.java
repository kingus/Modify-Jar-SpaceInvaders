package objects;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.*;

public interface CreatorInterface {

        void initializeStaticVariables();
        void fillClassNamesAndEntries();
        void getJarClassesLoader();
        void getJarClassesPool();

        void listClasses();
        void listPackages();
        void listMethods(String className);
        void listFields(String className);
        void listConstructors(String className);

        void saveFile();

        void createClass(String classPath);
        void removeClass(String classPath);

        void createInterface(String classPath);
        void removeInterface(String classPath);

        void addPackage(String packageName);
        void removePackage(String packageName);

        void addMethod(String className, String methodName);
        void setMethodBody(String className, String methodName, StringBuilder methodBody);
        void addBeforeMethod(String className, String methodName, StringBuilder methodBody);
        void addAfterMethod(String className, String methodName, StringBuilder methodBody);
        void removeMethod(String className, String methodName);

        void addField(String className, String fieldName);
        void removeField(String className, String fieldName);

        void addConstructor(String className, StringBuilder constructorBody);
        void removeConstructor(String className);
        void setCtorBody(String className, String ctorName, StringBuilder ctorBody);

}
