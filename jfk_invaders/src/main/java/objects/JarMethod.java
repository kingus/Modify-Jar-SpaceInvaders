package objects;

public class JarMethod {
    private String name;
    private String modifiers;
    private String returnedType;

    private String parameters;

    public JarMethod(String name, String modifiers, String parameters) {
        this.name = name;
        this.modifiers = modifiers;
        this.parameters = parameters;
    }


    public String getParameters() {
        return parameters;
    }

    public JarMethod(String name, String modifiers, String parameters, String returnedType) {

        this.name = name;
        this.modifiers = modifiers;
        this.parameters = parameters;
        this.returnedType = returnedType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifiers() {
        return modifiers;
    }

    public void setModifiers(String modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public String toString() {
        return getModifiers() + " " + returnedType +" " +getName() + " " +getParameters();

    }
}
