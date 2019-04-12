package com.example.demo.common.entity.sys;

import com.example.demo.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author Eric
 * @since 2019-04-10
 */
@ApiModel(value="SysMenu对象", description="菜单表")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单ID")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    @ApiModelProperty(value = "父节点ID")
    @TableField("pid")
    private Long pid;

    @ApiModelProperty(value = "菜单名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "菜单编号")
    @TableField("code")
    private String code;

    @ApiModelProperty(value = "菜单标记:Y是 N否")
    @TableField("menu_flag")
    private String menuFlag;

    @ApiModelProperty(value = "链接")
    @TableField("url")
    private String url;

    @ApiModelProperty(value = "层级")
    @TableField("level")
    private Integer level;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }
    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getMenuFlag() {
        return menuFlag;
    }

    public void setMenuFlag(String menuFlag) {
        this.menuFlag = menuFlag;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "SysMenu{" +
        "menuId=" + menuId +
        ", pid=" + pid +
        ", name=" + name +
        ", code=" + code +
        ", menuFlag=" + menuFlag +
        ", url=" + url +
        ", level=" + level +
        ", sort=" + sort +
        "}";
    }
}
