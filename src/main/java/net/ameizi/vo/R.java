package net.ameizi.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
public class R implements Serializable {

    private int code = 0;
    private String msg;
    private Object data;

    public static R ok() {
        R r = new R();
        r.setMsg("ok");
        return r;
    }

    public static R ok(Object data) {
        R r = new R();
        r.setMsg("ok");
        r.setData(data);
        return r;
    }

    public static R fail() {
        return fail(1, "fail");
    }

    public static R fail(String msg) {
        return fail(1, msg);
    }

    public static R fail(int code, String msg) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static R fail(int code, String msg, Object data) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}
