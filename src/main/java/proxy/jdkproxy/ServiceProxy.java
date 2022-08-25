package proxy.jdkproxy;

/**
 * @author: ywx
 * @description 定义代理服务的接口
 * @Date: 2022/08/26
 */

public interface ServiceProxy {
    /**
     * 被调用的方法
     * @param msg 内容
     * @return
     */
    String send(String msg);
}
