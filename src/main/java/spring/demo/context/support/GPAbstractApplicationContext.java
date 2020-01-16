package spring.demo.context.support;

/**
 * ioc容器实现的顶层设计   暂时是设计了一个refresh()方法。
 * @author Pinkboy
 *
 */
public abstract class GPAbstractApplicationContext {

    // 受保护，只提供给子类重写
    public void refresh() throws Exception{}
}
