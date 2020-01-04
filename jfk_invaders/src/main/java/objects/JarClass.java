package objects;

import javassist.*;

import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class JarClass {
    private Class jarClass;
    private CtClass ctClass;
    private boolean isAdded;

    private ArrayList<JarMethod> jarMethods;
    private ArrayList<JarConstructor> jarConstructors;
    private ArrayList<JarField> jarFields;

    public JarClass(Class jarClass, boolean isAdded) {
        this.jarClass = jarClass;
        this.isAdded=isAdded;
        setClassJarFields();
        setClassMethods();
        setClassConstructors();
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public JarClass(Class jarClass, CtClass ctClass, boolean isAdded) {
        this.jarClass = jarClass;
        this.ctClass = ctClass;
        this.isAdded=isAdded;

    }

    public Class getJarClass() {
        return jarClass;
    }

    public ArrayList<JarMethod> getJarMethods() {
        return jarMethods;
    }

    public CtClass getCtClass() {
        return ctClass;
    }

    public void setJarMethods(ArrayList<JarMethod> jarMethods) {
        this.jarMethods = jarMethods;
    }

    public void setJarClass(Class jarClass) {
        this.jarClass = jarClass;
    }

    public void setCtClass(CtClass ctClass) {
        this.ctClass = ctClass;
    }

    public void setClassMethods(){
        jarMethods = new ArrayList<JarMethod>();

        for (Method m:jarClass.getDeclaredMethods()) {
            int argsNumber = 0;
            String params = "(";
            for (Parameter param: m.getParameters()){
                int paramsNumber = m.getParameterCount();
                params += param;
                if(argsNumber<paramsNumber-1)
                    params += ", ";
                argsNumber++;
            }
            params += ")";
            jarMethods.add(new JarMethod(m.getName(), Modifier.toString(m.getModifiers()), params, m.getGenericReturnType().toString()));
        }
    }

    public void setClassConstructors(){
        jarConstructors = new ArrayList<JarConstructor>();

        for (Constructor c:jarClass.getDeclaredConstructors()) {
            int argsNumber = 0;
            String params = "(";
            for (Parameter param: c.getParameters()){
                int paramsNumber = c.getParameterCount();
                params += param;
                if(argsNumber<paramsNumber-1)
                    params += ", ";
                argsNumber++;
            }
            params += ")";
            jarConstructors.add(new JarConstructor(c.getName(), Modifier.toString(c.getModifiers()), params));
        }
    }

    public void setClassJarFields() {
        jarFields = new ArrayList<JarField>();

        for (Field f : jarClass.getDeclaredFields()) {
            f.setAccessible(true);
            jarFields.add(new JarField(f.toGenericString()));
        }
    }
    public StringBuilder listFields() {
        StringBuilder ret = new StringBuilder();
        for (JarField jf: jarFields) {
            ret.append("\t").append(jf).append("\n");
        }
        return ret;
    }
    public StringBuilder listMethods() {
        StringBuilder ret = new StringBuilder();
        for (JarMethod jm: jarMethods) {
            ret.append("\t").append(jm).append("\n");
        }
        return ret;
    }
    public StringBuilder listConstructors() {
        StringBuilder ret = new StringBuilder();
        for (JarConstructor jc: jarConstructors) {
            ret.append("\t").append(jc).append("\n");
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(jarClass.getName()).append("\n");
        ret.append("FIELDS\n");
        ret.append(listFields());
        ret.append("METHODS\n");
        ret.append(listMethods());
        ret.append("CONSTRUCTORS\n");
      ret.append(listConstructors());
         return ret.toString();
    }

    public void addMethod(String methodName){
        StringBuilder methodBody = new StringBuilder(methodName);
        if(methodBody.toString().contains("void"))
            methodBody.append("{}");
        else
            methodBody.append("{return null;}");
        CtMethod ctMethod = null;
        try {
            ctClass.defrost();
            ctMethod = CtNewMethod.make(methodBody.toString(),ctClass);
            this.ctClass.addMethod(ctMethod);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }
    public void addField(String fieldName){

        CtField ctField = null;
        fieldName+=";";
        try {
            ctField = ctField.make(fieldName, this.ctClass);
            this.ctClass.addField(ctField);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public void addConstructor(StringBuilder constructorBody){

        CtConstructor ctConstructor = null;
        try {
            ctConstructor = CtNewConstructor.make(String.valueOf(constructorBody), this.ctClass);
            this.ctClass.addConstructor(ctConstructor);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public void setCtorBody(String constrName, StringBuilder constructorBody) {
        CtConstructor[] constructors = ctClass.getDeclaredConstructors();
        CtConstructor ctConstructor = null;
        for (CtConstructor constructor: constructors) {
            if(constructor.getLongName().equals(constrName)) {
                try {
                    constructor.setBody(String.valueOf(constructorBody));
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }

    }

        public void removeMethod(String methodName){
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for(int i=0; i < methods.length;i++)
        {
            if(methods[i].getLongName().equals(methodName)) {
                try {
                    this.ctClass.removeMethod(methods[i]);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeField(String fieldName){
        CtField[] fields = ctClass.getDeclaredFields();
        for(int i=0; i < fields.length;i++)
        {
            if(fields[i].getName().equals(fieldName)) {
                try {
                    this.ctClass.removeField(fields[i]);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeConstructor(String fieldName){
        CtConstructor[] constructors = ctClass.getConstructors();
        for(int i=0; i < constructors.length;i++)
        {
            if(constructors[i].getLongName().equals(fieldName)) {
                try {
                    this.ctClass.removeConstructor(constructors[i]);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void addBeforeMethod(String methodName, StringBuilder methodBody){
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for(int i=0; i < methods.length;i++)
        {

            if(methods[i].getLongName().equals(methodName)) {
                try {
                    methods[i].insertBefore(String.valueOf(methodBody));
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void addAfterMethod(String methodName, StringBuilder methodBody){
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for(int i=0; i < methods.length;i++){

            if(methods[i].getLongName().equals(methodName)) {
                try {
                    methods[i].insertAfter(String.valueOf(methodBody));
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setMethodBody(String methodName, StringBuilder methodBody){
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for(int i=0; i < methods.length;i++)
        {
            if(methods[i].getLongName().equals(methodName)) {
                try {
                   methods[i].setBody(String.valueOf(methodBody));
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
