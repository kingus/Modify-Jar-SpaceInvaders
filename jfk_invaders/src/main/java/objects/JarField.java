package objects;

public class JarField {
    private String name;
    private String modifiers;

    public String getReturnedType() {
        return returnedType;
    }

    public void setReturnedType(String returnedType) {
        this.returnedType = returnedType;
    }

    public JarField(String name, String modifiers) {
        this.name = name;
        this.modifiers = modifiers;
    }

    private String returnedType;

    public JarField(String returnedType) {
        this.returnedType = returnedType;
    }

    public JarField(String name, String modifiers, String returnedType) {
        this.name = name;
        this.modifiers = modifiers;
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
            return returnedType;
    }
}
