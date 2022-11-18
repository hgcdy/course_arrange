package cn.netinnet.coursearrange.exception;

import cn.netinnet.coursearrange.enums.ResultEnum;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

@Slf4j
public class Assert {

    /**
     * 断言对象不为空
     * obj 为空则抛异常
     */
    public static void notNull(Object obj, ResultEnum resultEnum){
        if(obj == null){
            log.debug("obj is null.....................");
            throw new ServiceException(resultEnum);
        }
    }


    /**
     * 断言对象为空
     * 如果对象obj不为空，则抛出异常
     */
    public static void isNull(Object object, ResultEnum resultEnum) {
        if (object != null) {
            log.debug("obj is not null......");
            throw new ServiceException(resultEnum);
        }
    }

    /**
     * 断言表达式为真
     * 如果不为真，则抛出异常
     */
    public static void isTrue(boolean expression, ResultEnum resultEnum) {
        if (!expression) {
            log.debug("fail...............");
            throw new ServiceException(resultEnum);
        }
    }

    /**
     * 断言表达式为假
     * 如果不为假，则抛出异常
     */
    public static void isFalse(boolean expression, ResultEnum resultEnum) {
        if (expression) {
            log.debug("fail...............");
            throw new ServiceException(resultEnum);
        }
    }



    /**
     * 断言两个对象不相等
     * 如果相等，则抛出异常
     */
    public static void notEquals(Object m1, Object m2,  ResultEnum resultEnum) {
        if (m1.equals(m2)) {
            log.debug("equals...............");
            throw new ServiceException(resultEnum);
        }
    }

    /**
     * 断言两个对象相等
     * 如果不相等，则抛出异常
     */
    public static void equals(Object m1, Object m2,  ResultEnum resultEnum) {
        if (!m1.equals(m2)) {
            log.debug("not equals...............");
            throw new ServiceException(resultEnum);
        }
    }

    /**
     * 断言参数不为空
     * 如果为空，则抛出异常
     */
    public static void notEmpty(String s, ResultEnum resultEnum) {
        if (StringUtils.isBlank(s)) {
            log.debug("is empty...............");
            throw new ServiceException(resultEnum);
        }
    }

    /**
     * 断言参数不为空
     * 如果为空，则抛出异常
     */
    public static void notEmptyList(List list, ResultEnum resultEnum) {
        if (CollectionUtils.isEmpty(list)) {
            log.debug("is empty...............");
            throw new ServiceException(resultEnum);
        }
    }
}
