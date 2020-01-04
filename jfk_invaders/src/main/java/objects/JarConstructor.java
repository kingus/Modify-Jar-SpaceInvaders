package objects;

public class JarConstructor extends JarMethod{

    public JarConstructor(String name, String modifiers, String parameters, String returnedType) {
        super(name, modifiers, parameters, returnedType);
    }
    public JarConstructor(String name, String modifiers, String parameters) {
        super(name, modifiers, parameters);
    }
    @Override
    public String toString() {
        return getModifiers() +" " +getName() + " " +getParameters();
    }
}
