package mod.elfilibustero.sketch.beans;

import java.util.ArrayList;
import java.util.List;

public class ExternalLibraryBean {

    private List<DependencyBean> dependencies;
    private List<LibrariesBean> libraries;

    public ExternalLibraryBean() {
        dependencies = new ArrayList<>();
        libraries = new ArrayList<>();
    }

    public void setDependencies(List<DependencyBean> beans) {
        dependencies = beans;
    }

    public void setLibraries(List<LibrariesBean> beans) {
        libraries = beans;
    }

    public List<DependencyBean> getDependencies() {
        return dependencies;
    }

    public List<LibrariesBean> getLibraries() {
        return libraries;
    }
}
