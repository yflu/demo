package com.example.demo.web.core.shiro.multRealm;

import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.core.util.SessionUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 登录验证器：只取一个Realm验证就行了，不支持多个
 */
public class MyModularRealmAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {
        Realm realm = getFirstMatchedRealm(realms, token);
        if (realm == null) {
            throw new AuthenticationException("no candidate realm");
        }
        return realm.getAuthenticationInfo(token);
    }

    private Realm getFirstMatchedRealm(Collection<Realm> realms, AuthenticationToken token) {
        for (Realm realm : realms) {
            if (realm.supports(token)) {
                return realm;
            }
        }
        return null;
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        LoginType loginType = SessionUtil.getLoginType();
        if (loginType == null) {
            super.onLogout(principals);
        } else {
            notifyLogout(principals);
            Collection<Realm> realms = getRealms();
            if (!CollectionUtils.isEmpty(realms)) {
                for (Realm realm : realms) {
                    if (realm instanceof LogoutAware && realm instanceof AbstractAuthorizingRealm && ((AbstractAuthorizingRealm) realm).getLoginType() == loginType) {
                        ((LogoutAware) realm).onLogout(principals);
                    }
                }
            }
        }
    }

    @Override
    protected Collection<Realm> getRealms() {
        LoginType loginType = SessionUtil.getLoginType();
        if (loginType == null) {
            return super.getRealms();
        }
        List<Realm> resultList = new ArrayList<>();
        Collection<Realm> realms = super.getRealms();
        for (Realm realm : realms) {
            if (realm instanceof AbstractAuthorizingRealm && ((AbstractAuthorizingRealm) realm).getLoginType() == loginType) {
                resultList.add(realm);
            }
        }
        return resultList;
    }
}
