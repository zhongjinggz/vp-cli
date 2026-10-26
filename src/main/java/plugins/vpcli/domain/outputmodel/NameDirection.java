package plugins.vpcli.domain.outputmodel;

/**
 * 关联名称的阅读方向（对应 UML 关联名称旁的小实心三角）。
 * 步 A5 起取代 myuml 的 {@code MyAssociation.NameDirection}，
 * 使 outputmodel 前端不再耦合 myuml。
 */
public enum NameDirection {
    FROM_TO,
    TO_FROM,
    /** 仅用作"无名称"时的占位，不输出方向三角。 */
    NONE
}
