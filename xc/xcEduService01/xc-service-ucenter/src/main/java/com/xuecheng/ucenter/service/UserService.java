package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author newHeart
 * @Create 2020/2/21 8:48
 */
@Service
public class UserService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    public XcUserExt getUserExt(String username) {

        XcUser user = xcUserRepository.findXcUserByUsername(username);
        if (null==user){
            return null;
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(user.getId());
        if (null!=xcCompanyUser) {
            xcUserExt.setCompanyId(xcCompanyUser.getCompanyId());
        }
        return xcUserExt;
    }
}
