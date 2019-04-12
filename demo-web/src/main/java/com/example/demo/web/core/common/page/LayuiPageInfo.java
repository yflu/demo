package com.example.demo.web.core.common.page;

import lombok.Data;

import java.util.List;

/**
 * 分页结果的封装(for Layui Table)
 *
 * @author eric
 * @date 2019年4月9日
 */
@Data
public class LayuiPageInfo {

    private Integer code = 0;

    private String msg = "请求成功";

    private List data;

    private long count;

}
