package com.example.demo.web.core.shiro.multRealm;

import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.core.util.SessionUtil;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Collection;

/**
 * 鉴权器：只取一个Realm。shiro默认会在所有realms里取authorizationInfo并缓存
 */
public class MyModularRealmAuthorizer extends ModularRealmAuthorizer {

    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        assertRealmsConfigured();
        Realm realm = getRealm();
        if (realm != null) {
            if (((Authorizer) realm).isPermitted(principals, permission)) {
                return true;
            }
        }
        return false;
    }

    public Realm getRealm() {
        LoginType loginType = SessionUtil.getLoginType();
        Collection<Realm> realms = super.getRealms();
        for (Realm realm : realms) {
            if (realm instanceof AbstractAuthorizingRealm && ((AbstractAuthorizingRealm) realm).getLoginType() == loginType) {
                return realm;
            }
        }
        return null;
    }

    /*@Override
    public Collection<Realm> getRealms() {
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
    }*/
}
