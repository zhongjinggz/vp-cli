package plugins.vpcli.domain.myuml.myproject;

// Singleton
public class MyProjectFactory {
    private MyProject myProject;
    public MyProject get() {
        if (myProject == null) {
            myProject = new MyProject();
        }
        return myProject;
    }
}
