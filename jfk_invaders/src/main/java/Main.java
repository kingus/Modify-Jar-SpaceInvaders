import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Creator creator;
    private static int iterator;
    public static List<List<String>> argsList = new ArrayList<List<String>>();
    public static String pathToJar = "";
    public static String pathToJarOut = "";
    public static String command = "";
    public static String commandArgs1 = "";
    public static StringBuilder commandArgs2;

    public static Boolean isInput = false;
    public static Boolean isOutput = false;
    public static Boolean isCommand = false;
    public static Boolean isScript = false;

    public static void main(String[] args){
        commandArgs2 = new StringBuilder();
        parseArgsToList(args);

        if(isScript && isInput && isOutput){
            System.out.println("PATH" + commandArgs1);
            String path = commandArgs1;
            System.out.println(path);
            creator = new Creator(pathToJar, pathToJarOut);
            creator.initializeStaticVariables();
            readScript(path);
            creator.saveFile();
        }
//
//        if(isInput && isOutput && isCommand) {
//        }

    }
    public static StringBuilder setBodyFromFile(String fileName){
        StringBuilder methodBody = new StringBuilder();
        File file = new File(fileName);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNextLine())
            methodBody.append(sc.nextLine());

        return methodBody;
    }

    public static void parseArgsToList(String[] args){
        argsList = new ArrayList<>();
        int i=-1;
        for (String st:args) {
            if(st.length()>1) {
                if (st.substring(0, 2).equals("--")) {
                    i++;
                    List list = new ArrayList();
                    list.add(st);
                    argsList.add(list);
                } else {
                    (argsList.get(i)).add(st);
                }
            }
            else {
                (argsList.get(i)).add(st);
            }
        }
        System.out.println("BEBE" + argsList);
        setVariables();
    }

    public static void setVariables(){
        commandArgs2.setLength(0);
        for (List<String> list: argsList) {
            for (int i=0; i<list.size(); i++){
                if(i==0) {
                    switch (list.get(i)) {
                        case "--o":
                            if (list.size()!=2)
                                System.out.println("ERROR1");
                            else {
                                isOutput = true;
                                pathToJarOut = list.get(1);
                            }
                            break;
                        case "--i":
                            if (list.size()!=2) {
                                System.out.println("ERROR2");
                            } else {
                                isInput = true;
                                pathToJar = list.get(1);
                            }
                            break;
                        case "--add-method":
                        case "--remove-method":
                        case "--add-before-method":
                        case "--add-after-method":
                        case "--set-method-body":

                        case "--add-package":
                        case "--remove-package":

                        case "--add-class":
                        case "--remove-class":

                        case "--add-interface":
                        case "--remove-interface":

                        case "--add-field":
                        case "--remove-field":

                        case "--add-ctor":
                        case "--remove-ctor":
                        case "--set-ctor-body":

                        case "--list-fields":
                        case "--list-ctors":
                        case "--list-methods":
                            if (list.size() < 2)
                                System.out.println("ERROR3");
                            else {
                                isCommand = true;
                                command = list.get(0);
                                commandArgs1 = (list.get(1));
                            }
                            break;
                        case "--list-packages":
                        case "--list-classes":
                            if (list.size()<2) {
                                command = list.get(0);
                                isCommand = true;
                            }else
                                System.out.println("ZA DUZO PARAM1!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            break;
                        case "--script":
                            if(list.size()>2)
                                System.out.println("ERROR4");
                            else {
                                isScript = true;
                                commandArgs1 = list.get(1);
                            }
                    }
                }
                if(i>1){
                    commandArgs2.append(list.get(i)).append(" ");
                }
            }
        }
        System.out.println("C1 " +commandArgs1);
        System.out.println("C2 " +commandArgs2);
        System.out.println("C " + command);
    }

    public static void parseScriptToList(String scr){
        String[] args = scr.split(" ");
        for (int i=0; i<args.length; i++)
            System.out.println(args[i]);
        parseArgsToList(args);
    }


    public static void readScript(String scriptPath){
        System.out.println("SCR" + scriptPath);
        File file = new File(scriptPath);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int o=0;
        while (sc.hasNextLine()){
            System.out.println(o++);
            parseScriptToList(sc.nextLine());
            switchOptions();
        }
    }

    public static void switchOptions(){

        String names[];
        String className;
        String fileName;
        switch (command){
            case "--add-method":
                creator.addMethod(commandArgs1, String.valueOf(commandArgs2));
                break;
            case "--remove-method":
                creator.removeMethod(commandArgs1, String.valueOf(commandArgs2));
                break;
            case "--list-packages":
                creator.listPackages();
                break;
            case "--list-methods":
                creator.listMethods(commandArgs1);
                break;
            case "--list-classes":
                creator.listClasses();
                break;
            case "--list-fields":
                creator.listFields(commandArgs1);
                break;
            case "--list-ctors":
                creator.listConstructors(commandArgs1);
                break;
            case "--add-after-method":
                names =  commandArgs1.split("\\(");
                className = names[0].substring(0,names[0].lastIndexOf("."));
                fileName = commandArgs2.toString().replace(" ", "");
                creator.addAfterMethod(className, commandArgs1, setBodyFromFile(fileName));
                break;
            case "--add-before-method":
                names =  commandArgs1.split("\\(");
                className = names[0].substring(0,names[0].lastIndexOf("."));
                fileName = commandArgs2.toString().replace(" ", "");
                creator.addBeforeMethod(className, commandArgs1, setBodyFromFile(fileName));
                break;
            case "--set-method-body":
                names =  commandArgs1.split("\\(");
                className = names[0].substring(0,names[0].lastIndexOf("."));
                fileName = commandArgs2.toString().replace(" ", "");
                creator.setMethodBody(className, commandArgs1, setBodyFromFile(fileName));
                break;
            case "--add-package":
                creator.addPackage(commandArgs1);
                break;
            case "--remove-package":
                creator.removePackage(commandArgs1);
                break;

            case "--add-class":
                creator.createClass(commandArgs1);
                break;

            case "--remove-class":
                creator.removeClass(commandArgs1);
                break;

            case "--add-interface":
                creator.createInterface(commandArgs1);
                break;

            case "--remove-interface":
                creator.removeInterface(commandArgs1);
                break;


            case "--add-field":
                System.out.println("NANANANANA");
                creator.addField(commandArgs1, String.valueOf(commandArgs2));
                break;

            case "--remove-field":
                creator.removeField(commandArgs1, String.valueOf(commandArgs2));
                break;


            case "--add-ctor":
                creator.addConstructor(commandArgs1, commandArgs2);
                break;

            case "--remove-ctor":
                creator.removeConstructor(commandArgs1);
                break;

            case "--set-ctor-body":
                names =  commandArgs1.split("\\(");
                className = names[0];
                fileName = commandArgs2.toString().replace(" ", "");
                creator.setCtorBody(className, commandArgs1, setBodyFromFile(fileName));
                break;
            default:
                break;
        }
    }


}