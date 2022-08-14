package rpc;

/**
 * @author: ywx
 * @description
 * @Date: 2022/08/14
 */

public class TinterfaceImpl implements Tinterface{
    @Override
    public String send(String msg) {
        return "send message " + msg;
    }

    @Override
    public String sends(String msg) {
        return "xxxx";
    }
}
