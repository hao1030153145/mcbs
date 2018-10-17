/**
 * @project: dpmbs
 * @Title: UserController.java
 * @Package: com.transing.dpmbs.web.controller
 * <p/>
 * Copyright (c) 2014-2017 Jeeframework Limited, Inc.
 * All rights reserved.
 */
package com.transing.dpmbs.web.controller;

import com.jeeframework.util.cookie.CookieHelper;
import com.jeeframework.util.encrypt.BASE64Util;
import com.jeeframework.util.encrypt.MD5Util;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.RoleMenuService;
import com.transing.dpmbs.biz.service.UserService;
import com.transing.dpmbs.constant.Constants;
import com.transing.dpmbs.integration.bo.Menu;
import com.transing.dpmbs.integration.bo.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("userController")
@Api(value = "系统用户管理", description = "系统用户管理相关的访问接口", position = 2)
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RoleMenuService roleMenuService;

    @RequestMapping(value = "/login.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "后台用户登录界面", position = 0)
    public ModelAndView loginHtml(HttpServletRequest req, HttpServletResponse res) {
        Cookie cookie=CookieHelper.getCookie(req,Constants.LOGIN_COOKIE_SIGN);
        if(cookie!=null) {

            User user = (User) req.getSession().getAttribute(Constants.WITH_SESSION_USER);
            if(null != user){
                Long userId = user.getId();
                List<Menu> menuList = roleMenuService.getMenuListByUserId(userId.intValue());
                String url = "";
                if(!Validate.isEmpty(menuList)){
                    for (Menu menu :menuList) {
                        int pid = menu.getPid();
                        if(pid != 0){
                            url = menu.getUrl();
                            break;
                        }
                    }
                }

                return new ModelAndView("redirect:"+url);
            }
        }

        return new ModelAndView("login/login");
    }

    @RequestMapping(value = "/logout.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "后台用户退出登录", position = 0)
    public ModelAndView logoutHtml(HttpServletRequest req, HttpServletResponse res) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        //清除cookie
        Cookie cookies[] = req.getCookies();
        if (cookies == null || Constants.LOGIN_COOKIE_SIGN.length() == 0) {
            req.setAttribute("result", retMap);
            return new ModelAndView("login/login");
        }
        for (Cookie cookie : cookies) {
            if (Constants.LOGIN_COOKIE_SIGN.equalsIgnoreCase(cookie.getName())) {
                cookie.setMaxAge(0);
                break;
            }
        }
        req.getSession().removeAttribute(Constants.WITH_SESSION_USER);
        req.setAttribute("result", retMap);
        return new ModelAndView("login/login");
    }

    @RequestMapping(value = "/login.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "后台用户登录接口", notes = "remember: 1记住, 0不记住", position = 0)
    public Map login(@RequestParam("userName") @ApiParam(value = "用户名") String userName, @RequestParam("passwd") @ApiParam(value = "密码") String passwd, @RequestParam("remember") @ApiParam(value = "记住我") String remember, HttpServletRequest req, HttpServletResponse res) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (Validate.isEmpty(userName)) {
            throw new WebException(SystemCode.BIZ_LOGIN_NAME_EXCEPTION);
        }
        if (Validate.isEmpty(passwd)) {
            throw new WebException(SystemCode.BIZ_LOGIN_PASSWORD_EXCEPTION);
        }

        userName = userName.trim();

        User userFilter = new User();
        userFilter.setAccount(userName);
        userFilter.setPasswd(passwd);

        User user = userService.getUser(userFilter);

        if (user == null) {
            throw new WebException(SystemCode.BIZ_LOGIN_PASSNOTRIGHT_EXCEPTION);
        }

        if (!Validate.isEmpty(remember) && remember.equals("1")) {
            long validTime = System.currentTimeMillis() + (Constants.COOKIE_MAX_AGE * 1000);
            // MD5加密用户详细信息
            String cookieValueWithMd5 = MD5Util.encrypt(user.getId() + ":" + user.getPasswd() + ":" + validTime + ":" + Constants.LOGIN_KEY);
            // 将要被保存的完整的Cookie值
            String cookieValue = user.getId() + ":" + validTime + ":" + cookieValueWithMd5;
            // 再一次对Cookie的值进行BASE64编码
            String cookieValueBase64 = new String(BASE64Util.encode(cookieValue.getBytes()));
            // 是自动登录则设置cookie
            CookieHelper.setCookie(res, Constants.LOGIN_COOKIE_SIGN, cookieValueBase64, null, "/", Constants.COOKIE_TWO_MONTH_AGE); // 设置了自动登录，cookie在客户端保存4周
        }
        req.getSession().setMaxInactiveInterval(2 * 3600); // Session保存两小时
        req.getSession().setAttribute(Constants.WITH_SESSION_USER, user);

        Long userId = user.getId();
        List<Menu> menuList = roleMenuService.getMenuListByUserId(userId.intValue());
        String url = "";
        if(!Validate.isEmpty(menuList)){
            for (Menu menu :menuList) {
                int pid = menu.getPid();
                if(pid != 0){
                    url = menu.getUrl();
                    break;
                }
            }
        }

        retMap.put("url", url);
        retMap.put("userId", userId);
        return retMap;
    }
    @RequestMapping(value = "/getMenuList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询用户权限菜单")
    public List<Menu> getMenuList(@RequestParam (value = "userId") @ApiParam(value = "用户id") String userId){
        List<Menu> menuList = roleMenuService.getMenuListByUserId(Integer.parseInt(userId));
        return menuList;
    }

}
