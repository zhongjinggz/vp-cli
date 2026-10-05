package plugins.vpcli.domain.vpstruct;

import com.vp.plugin.model.IProject;

public class VPStructFactory {
    public VPStruct create(IProject project) {
        return new VPStruct(project, new VPStructElementFactory());
    }
}
