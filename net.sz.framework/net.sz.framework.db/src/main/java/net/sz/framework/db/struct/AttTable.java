package net.sz.framework.db.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttTable {

    /**
     * 映射名字
     *
     * @return
     */
    public String name() default "";

    /**
     * 忽略表
     *
     * @return
     */
    public boolean alligator() default false;

    /**
     * 是否生成复合主键，如果启用复合主键，插入速度会变的很慢
     *
     * @return
     */
    public boolean compositePrimaryKey() default false;
}
