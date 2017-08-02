package net.sz.framework.db.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttColumn {

    /**
     * 数据库映射名字
     *
     * @return
     */
    public String name() default "";

    /**
     * 主键列
     *
     * @return
     */
    public boolean key() default false;

    /**
     * 字段长度
     *
     * @return
     */
    public int length() default 255;

    /**
     * 自增列
     *
     * @return
     */
    public boolean auto() default false;

    /**
     * 普通索引
     *
     * @return
     */
    public boolean index() default false;

    /**
     * 唯一键索引
     *
     * @return
     */
    public boolean unique() default false;

    /**
     * true 表示可以为空
     *
     * @return
     */
    public boolean nullable() default true;

    /**
     * 字段描述
     *
     * @return
     */
    public String definition() default "";

    /**
     * 忽略字段
     *
     * @return
     */
    public boolean alligator() default false;

    /**
     * 数据类型
     *
     * @return
     */
    public boolean lob() default false;

    /**
     * 数据类型
     *
     * @return
     */
    public boolean bLob() default false;

}
