package plugins.vpcli.domain.outputmodel.pumelementexporter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IDependency;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRealization;
import com.vp.plugin.model.IStereotype;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;
import plugins.vpcli.domain.outputmodel.PumlDiagram;

/**
 * 步 #16：关系的泛型 Stereotypes of Relationships。
 * 覆盖关联/依赖/泛化连线 `: 标签` 处的泛型渲染，以及无名泛型。
 */
class PumlRelationshipStereotypeTest {

    // ---- 关联 Association ----

    @Test
    void associationWithNameStereotypeAndDirection() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("Stereotype1");
        IAssociation assoc = mock(IAssociation.class);
        when(assoc.getName()).thenReturn("name");
        when(assoc.getDirection()).thenReturn(IAssociation.DIRECTION_FROM_TO);
        when(assoc.stereotypeModelIterator()).thenReturn(iterOf(s));

        IAssociationEnd from = end(model("class1"));
        IAssociationEnd to = end(model("class2"));

        new PumlAssociationExporter().export(diagram, edgeOf(assoc, from, to));

        assertTrue(diagram.toString().contains("class1 -- class2: name <<Stereotype1>> >"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void associationWithOnlyStereotypeNoName() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("Stereotype2");
        IAssociation assoc = mock(IAssociation.class);
        when(assoc.getName()).thenReturn("");
        when(assoc.getDirection()).thenReturn(IAssociation.DIRECTION_FROM_TO);
        when(assoc.stereotypeModelIterator()).thenReturn(iterOf(s));

        IAssociationEnd from = end(model("class3"));
        IAssociationEnd to = end(model("class4"));

        new PumlAssociationExporter().export(diagram, edgeOf(assoc, from, to));

        assertTrue(diagram.toString().contains("class3 -- class4: <<Stereotype2>>"),
            () -> "实际输出：\n" + diagram);
    }

    // ---- 依赖 Dependency ----

    @Test
    void dependencyWithOnlyStereotypeNoName() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("Stereotype3");
        IDependency dep = mock(IDependency.class);
        when(dep.getName()).thenReturn("");
        when(dep.stereotypeModelIterator()).thenReturn(iterOf(s));

        new PumlDependencyExporter().export(diagram, edgeOf(dep, model("class5"), model("class6")));

        assertTrue(diagram.toString().contains("class5 ..> class6: <<Stereotype3>>"),
            () -> "实际输出：\n" + diagram);
    }

    @Test
    void dependencyWithNameAndStereotype() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("Stereotype1");
        IDependency dep = mock(IDependency.class);
        when(dep.getName()).thenReturn("uses");
        when(dep.stereotypeModelIterator()).thenReturn(iterOf(s));

        new PumlDependencyExporter().export(diagram, edgeOf(dep, model("class5"), model("class6")));

        assertTrue(diagram.toString().contains("class5 ..> class6: uses <<Stereotype1>>"),
            () -> "实际输出：\n" + diagram);
    }

    // ---- 泛化 Generalization ----

    @Test
    void generalizationWithOnlyStereotypeNoName() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("Stereotype4");
        IGeneralization gen = mock(IGeneralization.class);
        when(gen.getName()).thenReturn("");
        when(gen.stereotypeModelIterator()).thenReturn(iterOf(s));

        new PumlGeneralizationExporter().export(diagram, edgeOf(gen, model("class7"), model("class8")));

        assertTrue(diagram.toString().contains("class7 <|-- class8: <<Stereotype4>>"),
            () -> "实际输出：\n" + diagram);
    }

    // ---- 实现 Realization ----

    @Test
    void realizationWithOnlyStereotypeNoName() {
        PumlDiagram diagram = new PumlDiagram("d");
        IStereotype s = stereo("Stereotype4");
        IRealization real = mock(IRealization.class);
        when(real.getName()).thenReturn("");
        when(real.stereotypeModelIterator()).thenReturn(iterOf(s));

        new PumlRealizationExporter().export(diagram, edgeOf(real, model("class7"), model("class8")));

        assertTrue(diagram.toString().contains("class7 <|.. class8: <<Stereotype4>>"),
            () -> "实际输出：\n" + diagram);
    }

    // ---- 无名称无泛型：行为不变（无冒号标签） ----

    @Test
    void dependencyWithNoNameNoStereotypeOmitsLabel() {
        PumlDiagram diagram = new PumlDiagram("d");
        IDependency dep = mock(IDependency.class);
        when(dep.getName()).thenReturn("");
        when(dep.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        new PumlDependencyExporter().export(diagram, edgeOf(dep, model("class9"), model("class10")));

        String out = diagram.toString();
        assertTrue(out.contains("class9 ..> class10"),
            () -> "实际输出：\n" + out);
        assertTrue(!out.contains(": "),
            () -> "期望无冒号标签。实际输出：\n" + out);
    }

    @Test
    void generalizationWithNoNameNoStereotypeOmitsLabel() {
        PumlDiagram diagram = new PumlDiagram("d");
        IGeneralization gen = mock(IGeneralization.class);
        when(gen.getName()).thenReturn("");
        when(gen.stereotypeModelIterator()).thenReturn(Collections.emptyIterator());

        new PumlGeneralizationExporter().export(diagram, edgeOf(gen, model("class11"), model("class12")));

        String out = diagram.toString();
        assertTrue(out.contains("class11 <|-- class12"),
            () -> "实际输出：\n" + out);
        assertTrue(!out.contains(": "),
            () -> "期望无冒号标签。实际输出：\n" + out);
    }

    // ---- helpers ----

    private static IStereotype stereo(String name) {
        IStereotype s = mock(IStereotype.class);
        when(s.getName()).thenReturn(name);
        return s;
    }

    private static Iterator<?> iterOf(IStereotype... stereotypes) {
        List<IStereotype> list = Arrays.asList(stereotypes);
        return list.iterator();
    }

    private static IModelElement model(String name) {
        IModelElement m = mock(IModelElement.class);
        when(m.getName()).thenReturn(name);
        return m;
    }

    private static IAssociationEnd end(IModelElement classifier) {
        IAssociationEnd e = mock(IAssociationEnd.class);
        when(e.getModelElement()).thenReturn(classifier);
        when(e.getMultiplicity()).thenReturn("");
        when(e.getName()).thenReturn("");
        // NAVIGABLE_UNSPECIFIED=1：非 NAVIGABLE(0)、NON_NAVIGABLE(2)，走 shapeFor default → ShapeKind.NONE（--）
        when(e.getNavigable()).thenReturn(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
        return e;
    }

    private static IDiagramElement edgeOf(IModelElement model, IModelElement from,
                                          IModelElement to) {
        if (model instanceof IDependency) {
            when(((IDependency) model).getFrom()).thenReturn(from);
            when(((IDependency) model).getTo()).thenReturn(to);
        } else if (model instanceof IGeneralization) {
            when(((IGeneralization) model).getFrom()).thenReturn(from);
            when(((IGeneralization) model).getTo()).thenReturn(to);
        } else if (model instanceof IRealization) {
            when(((IRealization) model).getFrom()).thenReturn(from);
            when(((IRealization) model).getTo()).thenReturn(to);
        }
        IDiagramElement e = mock(IDiagramElement.class);
        when(e.getModelElement()).thenReturn(model);
        return e;
    }

    private static IDiagramElement edgeOf(IAssociation model, IAssociationEnd from,
                                          IAssociationEnd to) {
        when(model.getFromEnd()).thenReturn(from);
        when(model.getToEnd()).thenReturn(to);
        IDiagramElement e = mock(IDiagramElement.class);
        when(e.getModelElement()).thenReturn(model);
        return e;
    }
}
