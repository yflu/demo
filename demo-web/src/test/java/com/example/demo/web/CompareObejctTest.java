package com.example.demo.web;

import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.core.util.CompareObejct;
import org.junit.Test;

public class CompareObejctTest {

    @Test
    public void comparer() {
        CompareObejct<SysUser> comparer = new CompareObejct<SysUser>();
        SysUser user1 = new SysUser();
        user1.setUserId(1231L);
        user1.setAccount("123123");
        user1.setPassword("11111");
        SysUser user2 = new SysUser();
        user2.setUserId(112331L);
        user2.setSalt("222");
        user2.setPassword("1113311");
        user2.setNickname("44");
        comparer.setCurrent(user2);
        comparer.setOriginal(user1);
        System.out.println(comparer.contrastObj(SysUser.class));
    }
}
